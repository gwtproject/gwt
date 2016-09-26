/*
 * Copyright 2008 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.google.gwt.dev.jjs.impl;

import com.google.gwt.dev.jjs.SourceInfo;
import com.google.gwt.dev.jjs.ast.AccessModifier;
import com.google.gwt.dev.jjs.ast.Context;
import com.google.gwt.dev.jjs.ast.JClassType;
import com.google.gwt.dev.jjs.ast.JConditional;
import com.google.gwt.dev.jjs.ast.JDeclaredType;
import com.google.gwt.dev.jjs.ast.JExpression;
import com.google.gwt.dev.jjs.ast.JInterfaceType;
import com.google.gwt.dev.jjs.ast.JMethod;
import com.google.gwt.dev.jjs.ast.JMethodBody;
import com.google.gwt.dev.jjs.ast.JMethodCall;
import com.google.gwt.dev.jjs.ast.JModVisitor;
import com.google.gwt.dev.jjs.ast.JParameter;
import com.google.gwt.dev.jjs.ast.JParameterRef;
import com.google.gwt.dev.jjs.ast.JProgram;
import com.google.gwt.dev.jjs.ast.JProgram.DispatchType;
import com.google.gwt.dev.jjs.ast.JReferenceType;
import com.google.gwt.dev.jjs.ast.JReturnStatement;
import com.google.gwt.dev.jjs.ast.JTypeOracle;
import com.google.gwt.dev.jjs.ast.JVariableRef;
import com.google.gwt.dev.jjs.ast.RuntimeConstants;
import com.google.gwt.dev.jjs.ast.js.JsniMethodBody;
import com.google.gwt.dev.jjs.ast.js.JsniMethodRef;
import com.google.gwt.dev.jjs.impl.MakeCallsStatic.CreateStaticImplsVisitor;
import com.google.gwt.dev.jjs.impl.MakeCallsStatic.StaticCallConverter;
import com.google.gwt.dev.js.ast.JsContext;
import com.google.gwt.dev.js.ast.JsInvocation;
import com.google.gwt.dev.js.ast.JsModVisitor;
import com.google.gwt.dev.js.ast.JsNameRef;
import com.google.gwt.thirdparty.guava.common.collect.Iterables;
import com.google.gwt.thirdparty.guava.common.collect.Lists;
import com.google.gwt.thirdparty.guava.common.collect.Maps;
import com.google.gwt.thirdparty.guava.common.collect.Sets;

import java.util.Collections;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Devirtualization is the process of converting virtual method calls on instances that might be
 * a JSO, a string or and array (like "obj.doFoo();") to static calls (like
 * "SomeClass.doFoo__devirtual$(obj)).
 *
 * This transformation is done on arrays, strings and JSOs virtual method calls; as this objects
 * do not have the virtual methods in their prototypes. The static version is a trampoline that
 * decides how to dispatch the method.
 *
 * See https://code.google.com/p/google-web-toolkit/wiki/OverlayTypes for why this is done for JSOs.
 * <br />
 *
 * To complete the transformation:
 * <ul>
 *  <li>
 *  1. methods that need to be devirtualized must be turned into static functions.
 *  </li>
 *  <li>
 *  2. all method calls to the original functions must be rerouted either to the new static
 *      version or to a static dispatcher trampoline function that is created by this pass.
 *  </li>
 * </ul>
 * These trampolines are created whether a call to the function exists for separate compiled
 * modules to work. In a globally optimized build unused ones are pruned away. <br />
 *
 * This transform may NOT be run multiple times; it will create ever-expanding replacement
 * expressions.
 */
public class Devirtualizer {

  /**
   * Rewrite any virtual dispatches to Object, Strings or JavaScriptObject such that
   * dispatch occurs statically for JSOs, strings and arrays. <br />
   *
   * In the following cases JMethodCalls need to be rewritten:
   * <ol>
   * <li>a dual dispatch interface</li>
   * <li>a single dispatch trough single-jso interface</li>
   * <li>a java.lang.Object override from JavaScriptObject</li>
   * <li>methods defined at String</li>
   * <li>in draftMode, a 'static' virtual JSO call that hasn't been made
   * static yet.</li>
   * </ol>
   *
   */
  private class RewriteVirtualDispatches extends JModVisitor {

    @Override
    public void endVisit(JMethod x, Context ctx) {
      if (!mightNeedDevirtualization(x)) {
        return;
      }
      // The pruning pass will discard devirtualized methods that have not been called in
      // whole program optimizing mode.
      ensureDevirtualVersionExists(x);
    }

    @Override
    public void endVisit(JsniMethodRef x, Context ctx) {
      JMethod method = x.getTarget();
      if (method == null || !mightNeedDevirtualization(method)) {
        return;
      }
      ensureDevirtualVersionExists(method);

      // Replace the JMethod in jsni reference to a reference to the devirtualized method.
      // Note that a JsniMethodRefs is a pair containing the Jsni reference as text (i.e.
      // "@java.lang.Boolean::booleanValue") and a reference to the actual JMethod in the AST;
      // in generation time the actual JMethod that is called is looked up from the reference text.
      //
      // Here we just replace the JMethod the reference is pointing to without updating the
      // reference text.
      //
      // Keeping the "key" unchanged avoid the necessity to sync up with the modifications in the
      // JS AST when the JsniMethodBody is processed.
      JMethod devirtualMethod = devirtualMethodByMethod.get(method);
      ctx.replaceMe(new JsniMethodRef(
          x.getSourceInfo(),
          x.getIdent(),
          devirtualMethod,
          program.getJavaScriptObject()));
    }

    @Override
    public void endVisit(JMethodCall x, Context ctx) {
      JMethod method = x.getTarget();
      if (!method.needsDynamicDispatch()) {
        return;
      }

      JReferenceType instanceType = (JReferenceType) x.getInstance().getType().getUnderlyingType();
      if (!mightNeedDevirtualization(method, instanceType)) {
        return;
      }

      // it is a super.m() call and the superclass is not a JSO. (this case is NOT reached if
      // MakeCallsStatic was called).
      if (x.isStaticDispatchOnly() && !method.isJsOverlay()) {
        return;
      }

      ensureDevirtualVersionExists(method);

      // Replaces this virtual method call with a static call to a devirtual version of the method.
      JMethod devirtualMethod = devirtualMethodByMethod.get(method);
      ctx.replaceMe(converter.convertCall(x, devirtualMethod));
    }

    @Override
    public boolean visit(JMethod x, Context ctx) {
      // Don't rewrite the polymorphic call inside of the devirtualizing method!
      if (methodByDevirtualMethod.containsValue(x)) {
        return false;
      }
      return true;
    }

    @Override
    public boolean visit(JsniMethodBody x, Context ctx) {
      final Set<String> devirtualMethodJsniIdentifiers = Sets.newHashSet();
      for (JsniMethodRef jsniMethodRef : x.getJsniMethodRefs()) {
        JMethod target = jsniMethodRef.getTarget();
        if (target != null && mightNeedDevirtualization(target)) {
          devirtualMethodJsniIdentifiers.add(jsniMethodRef.getIdent());
        }
      }

      // Devirtualize jsni method calls.
      new JsModVisitor() {
        @Override
        public void endVisit(JsInvocation x, JsContext ctx) {
          if (!(x.getQualifier() instanceof JsNameRef)) {
            // If the invocation does not have a name as a qualifier then it is an expression and
            // cannot be a jsni method reference.
            return;
          }
          JsNameRef nameRef = (JsNameRef) x.getQualifier();
          if (!nameRef.isJsniReference()) {
            // The invocation is not to a JSNI method.
            return;
          }

          // Retrieve the method referred by the JsniMethodRef and check whether it needs
          // devirtualization.
          if (!devirtualMethodJsniIdentifiers.contains(nameRef.getIdent())) {
            return;
          }
          // Devirtualize method by rewriting
          // a.@java.lang.Boolean::booleanValue() ==> @java.lang.Boolean::booleanValue(a).
          //
          // Not the the reference identifier is *NOT* changed and will act a the key in the lookup
          // for the corresponding JMethod which is contained in the corresponding JsniMethodRef
          // node.
          ctx.replaceMe(
              new JsInvocation(
                  x.getSourceInfo(),
                  new JsNameRef(
                      nameRef.getSourceInfo(), nameRef.getIdent()),
                  Iterables.concat(
                      Collections.singleton(nameRef.getQualifier()), x.getArguments())));
          return;
        }
      }.accept(x.getFunc());

      // Now go ahead and fix the corresponding JSNI references.
      return true;
    }

    /**
     * Constructs and caches a method that is a new static version of the given method or a
     * trampoline function that wraps a new static version of the given method. It chooses which to
     * construct based on how the given method's defining class relates to the JavascriptObject
     * class.
     */
    private void ensureDevirtualVersionExists(JMethod method) {
      if (devirtualMethodByMethod.containsKey(method)) {
        // already did this one before
        return;
      }

      JDeclaredType targetType = method.getEnclosingType();

      // Separate compilation treats all JSOs as if they are "dualImpl", as the interface might
      // be implemented by a regular Java object in a separate module.

      // TODO(rluble): (Separate compilation) Devirtualizer should be run before optimizations
      // and optimizations need to be strong enough to perform the same kind of size reductions
      // achieved by keeping track of singleImpls.

      if (method.getSignature().equals("toString()Ljava/lang/String;")) {
        // Object.toString is special because: 1) every JS object has it and 2) GWT creates
        // a bridge from toString to its implementation method.
        devirtualMethodByMethod.put(
            method, program.getIndexedMethod(RuntimeConstants.RUNTIME_TO_STRING));
      } else if (!program.typeOracle.isDualJsoInterface(targetType) &&
          program.typeOracle.isSingleJsoImpl(targetType)) {
        // Optimize the trampoline away when there is ONLY JSO dispatch.
        // TODO(rluble): verify that this case can not arise in optimized mode and if so
        // remove as is an unnecessary optimization.

        assert targetType instanceof JInterfaceType;
        assert !program.getTypeJavaLangString().getImplements().contains(targetType);

        JMethod overridingMethod =
            findOverridingMethod(method, program.typeOracle.getSingleJsoImpl(targetType));
        assert overridingMethod != null;

        JMethod jsoStaticImpl =
            staticImplCreator.getOrCreateStaticImpl(program, overridingMethod);
        devirtualMethodByMethod.put(method, jsoStaticImpl);
      } else if (isOverlayMethod(method)) {
        // A virtual dispatch on a target that is already known to be an overlay method,.
        JMethod devirtualMethod = staticImplCreator.getOrCreateStaticImpl(program, method);
        devirtualMethodByMethod.put(method, devirtualMethod);
      } else {
        JMethod devirtualMethod = getOrCreateDevirtualMethod(method);
        devirtualMethodByMethod.put(method, devirtualMethod);
      }
    }

    private boolean mightNeedDevirtualization(JMethod method) {
      return mightNeedDevirtualization(method, method.getEnclosingType());
    }

    private boolean mightNeedDevirtualization(JMethod method, JReferenceType instanceType) {
      if (instanceType == null || !method.needsDynamicDispatch()) {
        return false;
      }
      if (devirtualMethodByMethod.containsKey(method)) {
        return true;
      }
      if (isOverlayMethod(method)) {
        return true;
      }
      if (method.getEnclosingType().isJsNative()) {
        // Methods in a native JsType that are not JsOverlay should NOT be devirtualized.
        return false;
      }
      if (instanceType.isNullType()) {
        instanceType = method.getEnclosingType();
      }
      EnumSet<DispatchType> dispatchType = program.getDispatchType(instanceType);
      dispatchType.remove(DispatchType.HAS_JAVA_VIRTUAL_DISPATCH);
      return !dispatchType.isEmpty();
    }
  }

  /**
   * Returns true if {@code method} is an overlay method. Overlay methods include the ones that
   * are marked as JsOverlay but also implicit overlays.
   */
  private boolean isOverlayMethod(JMethod method) {
    return method.isJsOverlay()
        // Synthetic private methods on interfaces are the result of lambdas that capture the
        // enclosing instance and are defined on default methods; these can appear in native
        // interfaces and thus need to be treated as overlays.
        || (method.getEnclosingType() instanceof JInterfaceType && method.isPrivate())
        // JsFunction implementation methods other than the other than the SAM implementation are
        // also considered overalys to allow for lighter weight JsFuncitons.
        // TODO(rluble): SAM implementation should also be devirtualized.
        || (method.getEnclosingType().isJsFunctionImplementation()
            && !method.isOrOverridesJsFunctionMethod());
  }

  public static void exec(JProgram program) {
    new Devirtualizer(program).execImpl();
  }

  /**
   * Maps each Object instance methods (ie, {@link Object#equals(Object)}) onto
   * its corresponding devirtualizing method.
   */
  private Map<JMethod, JMethod> devirtualMethodByMethod = Maps.newHashMap();

  /**
   * Contains the Cast.hasJavaObjectVirtualDispatch method.
   */
  private final JMethod hasJavaObjectVirtualDispatch;

  /**
   * Contains the Cast.isJavaArray method.
   */
  private final JMethod isJavaArray;

  /**
   * Contains the set of devirtualizing methods that replace polymorphic calls
   * to Object methods.
   */
  private final Map<JMethod, JMethod> methodByDevirtualMethod = Maps.newHashMap();

  private final JProgram program;

  private final CreateStaticImplsVisitor staticImplCreator;
  private final StaticCallConverter converter;

  /**
   * Creates and empty devirtualized method for devirtualizing {@code method} in class
   * {@code inclass}.
   */
  private JMethod createDevirtualMethodFor(JMethod method, JDeclaredType inClass) {
    SourceInfo sourceInfo = method.getSourceInfo().makeChild();

    String prefix = computeEscapedSignature(method.getSignature());
    JMethod devirtualMethod = new JMethod(sourceInfo, prefix + "__devirtual$",
        inClass, method.getType(), false, true, true, AccessModifier.PUBLIC);
    // TODO(rluble): DoNotInline should be carried over if 'any' of the targets is marked so.
    devirtualMethod.setInliningMode(method.getInliningMode());
    devirtualMethod.setBody(new JMethodBody(sourceInfo));
    devirtualMethod.setSynthetic();
    inClass.addMethod(devirtualMethod);
    // Setup parameters.
    devirtualMethod.createThisParameter(sourceInfo, method.getEnclosingType());
    for (JParameter oldParam : method.getParams()) {
      devirtualMethod.createFinalParameter(sourceInfo, oldParam.getName(), oldParam.getType());
    }

    devirtualMethod.freezeParamTypes();
    devirtualMethod.addThrownExceptions(method.getThrownExceptions());
    sourceInfo.addCorrelation(sourceInfo.getCorrelator().by(devirtualMethod));

    return devirtualMethod;
  }

  /**
   * A normal method signature contains characters that are not valid in a method name. If you want
   * to construct a method name based on an existing method signature then those characters need to
   * be escaped.
   */
  private static String computeEscapedSignature(String methodSignature) {
    return methodSignature.replaceAll("[\\<\\>\\(\\)\\;\\/\\[]", "_");
  }

  private Devirtualizer(JProgram program) {
    this.program = program;

    this.hasJavaObjectVirtualDispatch =
        program.getIndexedMethod(RuntimeConstants.CAST_HAS_JAVA_OBJECT_VIRTUAL_DISPATCH);
    this.isJavaArray = program.getIndexedMethod(RuntimeConstants.ARRAY_IS_JAVA_ARRAY);
    // TODO: consider turning on null checks for "this"?
    // However, for JSO's there is existing code that relies on nulls being okay.
    this.converter = new StaticCallConverter(program, false);
    staticImplCreator = new CreateStaticImplsVisitor(program);
  }

  private void execImpl() {
    JClassType jsoType = program.getJavaScriptObject();
    if (jsoType == null) {
      return;
    }

    new RewriteVirtualDispatches().accept(program);
  }

  /**
   * Finds the method that overrides this method, starting with the target
   * class.
   */
  private JMethod findOverridingMethod(JMethod method, JClassType target) {
    if (target == null) {
      return null;
    }

    for (JMethod overridingMethod : target.getMethods()) {
      if (JTypeOracle.methodsDoMatch(method, overridingMethod)) {
        return overridingMethod;
      }
    }
    return findOverridingMethod(method, target.getSuperClass());
  }

  /**
   * Construct conditional expression for dispatch. Handle the cases where a dispatch or check
   * is null indicating impossibility of such operation.
   */
  private static JExpression constructMinimalCondition(JMethod checkMethod, JVariableRef target,
      JMethodCall trueDispatch, JExpression falseDispatch) {
    // TODO(rluble): Maybe we should emit slightly different code in checked mode, so that if
    // no condition is met an exception would be thrown rather than cascading.
    if (falseDispatch == null && trueDispatch == null) {
      return null;
    }
    if (falseDispatch == null) {
      // No need for condition to be evaluated.
      return trueDispatch;
    }
    if (trueDispatch == null || falseDispatch instanceof JMethodCall &&
        ((JMethodCall) falseDispatch).getTarget() == trueDispatch.getTarget()) {
      // Both branches do the same dispatch (or no trueDispatch).
      return falseDispatch;
    }
    JMethodCall condition =
        new JMethodCall(trueDispatch.getSourceInfo(), null, checkMethod, target);

    return new JConditional(condition.getSourceInfo(), trueDispatch.getType(), condition,
        trueDispatch, falseDispatch);
  }

  /**
   * Create a dispatch call taking the arguments from the devirtual method.
   */
  private static JMethodCall maybeCreateDispatch(JMethod dispatchTo, JMethod devirtualMethod) {
    if (dispatchTo == null) {
      return null;
    }
    List<JParameter> parameters = Lists.newArrayList(devirtualMethod.getParams());
    SourceInfo sourceInfo = devirtualMethod.getSourceInfo();
    JParameterRef thisParamRef = null;

    if (!dispatchTo.isStatic()) {
      // This is a virtual dispatch, take the first parameter as the receiver.
      thisParamRef = parameters.remove(0).makeRef(sourceInfo);
    }

    JMethodCall dispatchCall = new JMethodCall(sourceInfo, thisParamRef, dispatchTo);
    for (JParameter param : parameters) {
      dispatchCall.addArg(param.makeRef(sourceInfo));
    }
    return dispatchCall;
  }

  /**
   * Create a conditional method to discriminate between static and virtual
   * dispatch.
   *
   * <pre>
   * static boolean equals__devirtual$(Object this, Object other) {
   *   return Cast.isJavaString() ? String.equals(other) :
   *       Cast.hasJavaObjectVirtualDispatch(this) ?
   *       this.equals(other) : JavaScriptObject.equals$(this, other);
   * }
   * </pre>
   */
  private JMethod getOrCreateDevirtualMethod(JMethod method) {

    if (methodByDevirtualMethod.containsKey(method)) {
      return methodByDevirtualMethod.get(method);
    }

    /////////////////////////////////////////////////////////////////
    // 1. Determine which types of object are target of this dispatch
    /////////////////////////////////////////////////////////////////
    JReferenceType enclosingType = method.getEnclosingType();
    EnumSet<DispatchType> possibleTargetTypes = program.getDispatchType(
        enclosingType.getUnderlyingType());

    /////////////////////////////////////////////////////////////////
    // 2. Compute the dispatch to method for each relevant case.
    /////////////////////////////////////////////////////////////////
    EnumMap<DispatchType, JMethod> dispatchToMethodByTargetType = new EnumMap<>(DispatchType.class);
    for (Map.Entry<JClassType, DispatchType> nativeRepresentedType :
        program.getRepresentedAsNativeTypesDispatchMap().entrySet()) {
      // skip non-instantiated boxed types, which have been pruned from the AST.
      if (program.typeOracle.isInstantiatedType(nativeRepresentedType.getKey())) {
        maybeCreateDispatchFor(method, nativeRepresentedType.getValue(), possibleTargetTypes,
            dispatchToMethodByTargetType, nativeRepresentedType.getKey());
      }
    }

    if (possibleTargetTypes.contains(DispatchType.JAVA_ARRAY)) {
      maybeCreateDispatchFor(method, DispatchType.JAVA_ARRAY, possibleTargetTypes,
          dispatchToMethodByTargetType, program.getTypeJavaLangObject());
    }

    if (possibleTargetTypes.contains(DispatchType.JSO)) {
      JMethod overridingMethod = findOverridingMethod(method,
          program.typeOracle.getSingleJsoImpl(enclosingType));
      if (overridingMethod == null && enclosingType == program.getTypeJavaLangObject()) {
        overridingMethod = findOverridingMethod(method, program.getJavaScriptObject());
      }
      assert overridingMethod != null : method.getEnclosingType().getName() + "::" +
          method.getName() + " not overridden by JavaScriptObject";
      dispatchToMethodByTargetType.put(DispatchType.JSO,
          staticImplCreator.getOrCreateStaticImpl(program, overridingMethod));
    }

    if (possibleTargetTypes.contains(DispatchType.HAS_JAVA_VIRTUAL_DISPATCH)) {
      dispatchToMethodByTargetType.put(DispatchType.HAS_JAVA_VIRTUAL_DISPATCH, method);
    }

    /////////////////////////////////////////////////////////////////
    // 3. Create a devirtualized method.
    /////////////////////////////////////////////////////////////////

    // Decide where to place the devirtual method. Ideally these methods should reside in the
    // declaring type, but some of these will be interfaces and currently GWT does not emit
    // any code for them.
    // TODO(rluble): place interface methods in the corresponding interface once Java 8 defender
    // method support is implemented.
    JClassType devirtualMethodEnclosingClass  = null;
    if (method.getEnclosingType() instanceof JClassType) {
      devirtualMethodEnclosingClass = (JClassType) method.getEnclosingType();
    }  else {
      for (Map.Entry<JClassType, DispatchType> nativeRepresentedType :
          program.getRepresentedAsNativeTypesDispatchMap().entrySet()) {
        if (dispatchToMethodByTargetType.containsKey(nativeRepresentedType.getValue())) {
          devirtualMethodEnclosingClass = nativeRepresentedType.getKey();
          break;
        }
      }
    }

    if (devirtualMethodEnclosingClass == null) {
      if (dispatchToMethodByTargetType.get(DispatchType.JSO) != null) {
        // This is an interface method implemented by a JSO, place in the JSO class.
        devirtualMethodEnclosingClass = (JClassType)
            dispatchToMethodByTargetType.get(DispatchType.JSO).getEnclosingType();
      } else {
        // It is an interface implemented by devirtualized types, place it in Object.
        devirtualMethodEnclosingClass = program.getTypeJavaLangObject();
      }
    }

    // Devirtualization of external methods stays external and devirtualization of internal methods
    // stays internal.
    assert program.isReferenceOnly(devirtualMethodEnclosingClass)
        == program.isReferenceOnly(method.getEnclosingType());
    // TODO(stalcup): devirtualization is modifying both internal and external types. Really
    // external types should never be modified. Change the point at which types are saved into
    // libraries to be after normalization has occurred, so that no further modification is
    // necessary when loading external types.
    JMethod devirtualMethod = createDevirtualMethodFor(method, devirtualMethodEnclosingClass);

    /**
     * Encoding
     */
    SourceInfo sourceInfo = method.getSourceInfo().makeChild();
    JParameter thisParam = devirtualMethod.getParams().get(0);

    // Synthesize the dispatch at a single conditional doing the checks in this order.
    //   isString(obj) ? dispatchToString : (
    //     isRegularJavaObject(obj) ? obj.method : (
    //       isJavaArray(obj) ?
    //         dispatchToArray :
    //         dispatchToJSO
    //     )
    //   )

    // Construct back to fort. Last is JSO.
    JExpression dispatchExpression =
        maybeCreateDispatch(dispatchToMethodByTargetType.get(DispatchType.JSO), devirtualMethod);

    // Dispatch to array
    dispatchExpression = constructMinimalCondition(
        isJavaArray,
        thisParam.makeRef(thisParam.getSourceInfo()),
        maybeCreateDispatch(dispatchToMethodByTargetType.get(DispatchType.JAVA_ARRAY),
            devirtualMethod),
        dispatchExpression);

    // Dispatch to regular object
    dispatchExpression = constructMinimalCondition(
        hasJavaObjectVirtualDispatch,
        thisParam.makeRef(thisParam.getSourceInfo()),
        maybeCreateDispatch(
            dispatchToMethodByTargetType.get(DispatchType.HAS_JAVA_VIRTUAL_DISPATCH),
            devirtualMethod),
        dispatchExpression);

    // Dispatch to regular string, double, boolean
    for (Map.Entry<JClassType, DispatchType> nativeRepresentedType
        : program.getRepresentedAsNativeTypesDispatchMap().entrySet()) {
      DispatchType dispatchType = nativeRepresentedType.getValue();
      String castInstanceOfQualifier = dispatchType.getTypeCategory().castInstanceOfQualifier();
      dispatchExpression = constructMinimalCondition(
          program.getIndexedMethod("Cast.instanceOf" + castInstanceOfQualifier),
          thisParam.makeRef(thisParam.getSourceInfo()),
          maybeCreateDispatch(dispatchToMethodByTargetType.get(dispatchType), devirtualMethod),
          dispatchExpression);
    }

    // return dispatchConditional;
    JReturnStatement returnStatement = new JReturnStatement(sourceInfo, dispatchExpression);

    ((JMethodBody) devirtualMethod.getBody()).getBlock().addStmt(returnStatement);
    methodByDevirtualMethod.put(method, devirtualMethod);

    return devirtualMethod;
  }

  private void maybeCreateDispatchFor(JMethod method, DispatchType target,
      EnumSet<DispatchType> possibleTargetTypes,
      EnumMap<DispatchType, JMethod> dispatchToMethodByTargetType, JClassType targetDevirtualType) {
    if (possibleTargetTypes.contains(target)) {
      JMethod overridingMethod = findOverridingMethod(method, targetDevirtualType);
      if (overridingMethod == null) {
        throw new AssertionError(method.getEnclosingType().getName() + "::" + method.getName()
            + " not overridden by " + targetDevirtualType.getSimpleName());
      }
      dispatchToMethodByTargetType.put(target,
          staticImplCreator.getOrCreateStaticImpl(program, overridingMethod));
    }
  }

  private static String getJsniReferenceIdentifier(JMethod method) {
    return "@" + method.getJsniSignature(true, false);
  }
}
