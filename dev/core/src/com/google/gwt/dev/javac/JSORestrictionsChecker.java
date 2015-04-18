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
package com.google.gwt.dev.javac;

import com.google.gwt.dev.jdt.SafeASTVisitor;
import com.google.gwt.dev.util.InstalledHelpInfo;
import com.google.gwt.dev.util.collect.Stack;
import com.google.gwt.thirdparty.guava.common.base.Strings;

import org.eclipse.jdt.core.compiler.CharOperation;
import org.eclipse.jdt.internal.compiler.ast.ASTNode;
import org.eclipse.jdt.internal.compiler.ast.AllocationExpression;
import org.eclipse.jdt.internal.compiler.ast.CompilationUnitDeclaration;
import org.eclipse.jdt.internal.compiler.ast.ConstructorDeclaration;
import org.eclipse.jdt.internal.compiler.ast.FieldDeclaration;
import org.eclipse.jdt.internal.compiler.ast.MethodDeclaration;
import org.eclipse.jdt.internal.compiler.ast.TypeDeclaration;
import org.eclipse.jdt.internal.compiler.classfmt.ClassFileConstants;
import org.eclipse.jdt.internal.compiler.lookup.AnnotationBinding;
import org.eclipse.jdt.internal.compiler.lookup.BlockScope;
import org.eclipse.jdt.internal.compiler.lookup.ClassScope;
import org.eclipse.jdt.internal.compiler.lookup.CompilationUnitScope;
import org.eclipse.jdt.internal.compiler.lookup.FieldBinding;
import org.eclipse.jdt.internal.compiler.lookup.MethodBinding;
import org.eclipse.jdt.internal.compiler.lookup.MethodScope;
import org.eclipse.jdt.internal.compiler.lookup.ReferenceBinding;
import org.eclipse.jdt.internal.compiler.lookup.SourceTypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;

import java.util.HashMap;
import java.util.Map;

/**
 * Check a compilation unit for violations of
 * {@link com.google.gwt.core.client.JavaScriptObject JavaScriptObject} (JSO)
 * restrictions. The restrictions are summarized in
 * <code>jsoRestrictions.html</code>.
 *
 *
 * Any violations found are attached as errors on the
 * CompilationUnitDeclaration.
 *
 * @see <a
 *      href="http://code.google.com/p/google-web-toolkit/wiki/OverlayTypes">Overlay
 *      types design doc</a>
 * @see jsoRestrictions.html
 */
public class JSORestrictionsChecker {

  public static final String ERR_JSEXPORT_ONLY_CTORS_STATIC_METHODS_AND_STATIC_FINAL_FIELDS =
      "@JsExport may only be applied to public constructors and static methods and public "
      + "static final fields in public classes.";
  public static final String ERR_EITHER_JSEXPORT_JSNOEXPORT =
      "@JsExport and @JsNoExport is not allowed at the same time.";
  public static final String ERR_JSEXPORT_ON_ENUMERATION =
      "@JsExport is not allowed on individual enumerations";
  public static final String ERR_MUST_EXTEND_MAGIC_PROTOTYPE_CLASS =
      "Classes implementing @JsType with a prototype must extend that interface's Prototype class";
  public static final String ERR_CLASS_EXTENDS_MAGIC_PROTOTYPE_BUT_NO_PROTOTYPE_ATTRIBUTE =
      "Classes implementing a @JsType without a prototype should not extend the Prototype class";
  public static final String ERR_CONSTRUCTOR_WITH_PARAMETERS =
      "Constructors must not have parameters in subclasses of JavaScriptObject";
  public static final String ERR_INSTANCE_FIELD = "Instance fields cannot be used in subclasses of JavaScriptObject";
  public static final String ERR_INSTANCE_METHOD_NONFINAL =
      "Instance methods must be 'final' in non-final subclasses of JavaScriptObject";
  public static final String ERR_IS_NONSTATIC_NESTED = "Nested classes must be 'static' if they extend JavaScriptObject";
  public static final String ERR_NEW_JSO =
      "'new' cannot be used to create instances of JavaScriptObject subclasses; instances must originate in JavaScript";
  public static final String ERR_NONEMPTY_CONSTRUCTOR =
      "Constructors must be totally empty in subclasses of JavaScriptObject";
  public static final String ERR_NONPROTECTED_CONSTRUCTOR =
      "Constructors must be 'protected' in subclasses of JavaScriptObject";
  public static final String ERR_OVERRIDDEN_METHOD =
      "Methods cannot be overridden in JavaScriptObject subclasses";
  public static final String JSO_CLASS = "com/google/gwt/core/client/JavaScriptObject";
  public static final String ERR_FORGOT_TO_MAKE_PROTOTYPE_IMPL_JSTYPE = "@JsType subtype extends magic _Prototype class, but _Prototype class doesn't implement JsType";
  public static final String ERR_JS_TYPE_WITH_PROTOTYPE_SET_NOT_ALLOWED_ON_CLASS_TYPES = "@JsType with prototype set not allowed on class types";
  public static final String ERR_JS_FUNCTION_ONLY_ALLOWED_ON_FUNCTIONAL_INTERFACE =
      "@JsFunction is only allowed on functional interface";
  public static final String ERR_JS_FUNCTION_INTERFACE_CANNOT_EXTEND_ANY_INTERFACE =
      "Interface annotated as @JsFunction cannot extend any other interfaces";
  public static final String ERR_JS_FUNCTION_CANNOT_HAVE_DEFAULT_METHODS =
      "JsFunction cannot have default methods";
  static boolean LINT_MODE = false;

  private enum ClassState {
    NORMAL, JSO
  }

  /**
   * The order in which the checker will process types is undefined, so this
   * type accumulates the information necessary for sanity-checking the JSO
   * types.
   */
  public static class CheckerState {

    private final Map<String, String> interfacesToJsoImpls = new HashMap<String, String>();

    public void addJsoInterface(TypeDeclaration jsoType,
        CompilationUnitDeclaration cud, ReferenceBinding interf) {
      String intfName = CharOperation.toString(interf.compoundName);
      String alreadyImplementor = interfacesToJsoImpls.get(intfName);
      String myName = CharOperation.toString(jsoType.binding.compoundName);

      if (alreadyImplementor != null) {
        String msg = errAlreadyImplemented(intfName, alreadyImplementor, myName);
        errorOn(jsoType, cud, msg);
        return;
      }

      interfacesToJsoImpls.put(intfName, myName);
    }
  }

  private class JSORestrictionsVisitor extends SafeASTVisitor implements
      ClassFileConstants {

    private final Stack<ClassState> classStateStack = new Stack<ClassState>();
    private final Stack<SourceTypeBinding> typeBindingStack = new Stack<SourceTypeBinding>();

    @Override
    public void endVisit(AllocationExpression exp, BlockScope scope) {
      // In rare cases we might not be able to resolve the expression.
      if (exp.type == null) {
        return;
      }
      TypeBinding resolvedType = exp.resolvedType;
      if (resolvedType == null) {
        if (scope == null) {
          return;
        }
        resolvedType = exp.type.resolveType(scope);
      }
      // Anywhere an allocation occurs is wrong.
      if (isJsoSubclass(resolvedType)) {
        errorOn(exp, ERR_NEW_JSO);
      }
    }

    @Override
    public void endVisit(ConstructorDeclaration meth, ClassScope scope) {
      if (!isJso()) {
        return;
      }
      if ((meth.arguments != null) && (meth.arguments.length > 0)) {
        errorOn(meth, ERR_CONSTRUCTOR_WITH_PARAMETERS);
      }
      if ((meth.modifiers & AccProtected) == 0) {
        errorOn(meth, ERR_NONPROTECTED_CONSTRUCTOR);
      }
      if (meth.statements != null && meth.statements.length > 0) {
        errorOn(meth, ERR_NONEMPTY_CONSTRUCTOR);
      }
    }

    @Override
    public void endVisit(FieldDeclaration field, MethodScope scope) {
      checkJsExport(field);

      if (!isJso()) {
        return;
      }
      if (!field.isStatic()) {
        errorOn(field, ERR_INSTANCE_FIELD);
      }
    }

    @Override
    public void endVisit(MethodDeclaration meth, ClassScope scope) {
      checkJsExport(meth.binding);

      if (!isJso()) {
        return;
      }
      if ((meth.modifiers & (AccFinal | AccPrivate | AccStatic)) == 0) {
        // The method's modifiers allow it to be overridden. Make
        // one final check to see if the surrounding class is final.
        if ((meth.scope == null) || !meth.scope.enclosingSourceType().isFinal()) {
          errorOn(meth, ERR_INSTANCE_METHOD_NONFINAL);
        }
      }

      // Should not have to check isStatic() here, but isOverriding() appears
      // to be set for static methods.
      if (!meth.isStatic()
          && (meth.binding != null && meth.binding.isOverriding())) {
        errorOn(meth, ERR_OVERRIDDEN_METHOD);
      }
    }

    @Override
    public void endVisit(TypeDeclaration type, ClassScope scope) {
      popState();
    }

    @Override
    public void endVisit(TypeDeclaration type, CompilationUnitScope scope) {
      popState();
    }

    @Override
    public void endVisitValid(TypeDeclaration type, BlockScope scope) {
      popState();
    }

    @Override
    public boolean visit(TypeDeclaration type, ClassScope scope) {
      pushState(type);
      return true;
    }

    @Override
    public boolean visit(TypeDeclaration type, CompilationUnitScope scope) {
      pushState(type);
      return true;
    }

    @Override
    public boolean visitValid(TypeDeclaration type, BlockScope scope) {
      pushState(type);
      return true;
    }

    private void checkJsFunction(TypeDeclaration type, TypeBinding typeBinding) {
      ReferenceBinding binding = (ReferenceBinding) typeBinding;
      if (JdtUtil.getAnnotation(binding, JsInteropUtil.JSFUNCTION_CLASS) == null) {
        return;
      }
      if (!binding.isFunctionalInterface(type.scope)) {
        errorOn(type, ERR_JS_FUNCTION_ONLY_ALLOWED_ON_FUNCTIONAL_INTERFACE);
        return;
      }
      // If a functional interface has more than one method, it means it has default methods.
      if (binding.methods().length > 1) {
        errorOn(type, ERR_JS_FUNCTION_CANNOT_HAVE_DEFAULT_METHODS);
      }
      if (binding.superInterfaces().length > 0) {
        errorOn(type, ERR_JS_FUNCTION_INTERFACE_CANNOT_EXTEND_ANY_INTERFACE);
      }
    }

    private void checkJsType(TypeDeclaration type, TypeBinding typeBinding) {
      ReferenceBinding binding = (ReferenceBinding) typeBinding;
      if (binding.isClass()) {
        AnnotationBinding jsinterfaceAnn = JdtUtil.getAnnotation(typeBinding,
          JsInteropUtil.JSTYPE_CLASS);
        String jsPrototype = JdtUtil.getAnnotationParameterString(jsinterfaceAnn, "prototype");
        if (jsPrototype != null && !"".equals(jsPrototype)) {
          errorOn(type, ERR_JS_TYPE_WITH_PROTOTYPE_SET_NOT_ALLOWED_ON_CLASS_TYPES);
        }
      }
    }

    private void checkJsExport(MethodBinding mb) {
      if (JdtUtil.getAnnotation(mb, JsInteropUtil.JSEXPORT_CLASS) != null) {
        boolean isStatic = mb.isConstructor() || mb.isStatic();
        if (!areAllEnclosingClassesPublic() || !isStatic || !mb.isPublic()) {
          errorOn(mb, ERR_JSEXPORT_ONLY_CTORS_STATIC_METHODS_AND_STATIC_FINAL_FIELDS);
        }
        if (JdtUtil.getAnnotation(mb, JsInteropUtil.JSNOEXPORT_CLASS) != null) {
          errorOn(mb, ERR_EITHER_JSEXPORT_JSNOEXPORT);
        }
      }
    }

    private void checkJsExport(FieldDeclaration fd) {
      FieldBinding fb = fd.binding;
      if (JdtUtil.getAnnotation(fb, JsInteropUtil.JSEXPORT_CLASS) != null) {
        if (isEnumConstant(fd)) {
          errorOn(fb, ERR_JSEXPORT_ON_ENUMERATION);
        }
        if (!areAllEnclosingClassesPublic() || !fb.isStatic() || !fb.isFinal() || !fb.isPublic()) {
          errorOn(fb, ERR_JSEXPORT_ONLY_CTORS_STATIC_METHODS_AND_STATIC_FINAL_FIELDS);
        }
        if (JdtUtil.getAnnotation(fb, JsInteropUtil.JSNOEXPORT_CLASS) != null) {
          errorOn(fb, ERR_EITHER_JSEXPORT_JSNOEXPORT);
        }
      }
    }

    private boolean isEnumConstant(FieldDeclaration fd) {
      return (fd.initialization != null && fd.initialization instanceof AllocationExpression
          && ((AllocationExpression) fd.initialization).enumConstant != null);
    }

    private ClassState checkType(TypeDeclaration type) {
      SourceTypeBinding binding = type.binding;
      checkJsFunction(type, binding);
      if (isJsType(type.binding)) {
        checkJsType(type, type.binding);
        return ClassState.NORMAL;
      }

      if (checkClassImplementingJsType(type)) {
        return ClassState.NORMAL;
      }

      if (!isJsoSubclass(binding)) {
        return ClassState.NORMAL;
      }

      if (type.enclosingType != null && !binding.isStatic()) {
        errorOn(type, ERR_IS_NONSTATIC_NESTED);
      }

      ReferenceBinding[] interfaces = binding.superInterfaces();
      if (interfaces != null) {
        for (ReferenceBinding interf : interfaces) {
          if (interf.methods() == null) {
            continue;
          }

          if (interf.methods().length > 0) {
            // See if any of my superTypes implement it.
            ReferenceBinding superclass = binding.superclass();
            if (superclass == null
                || !superclass.implementsInterface(interf, true)) {
              state.addJsoInterface(type, cud, interf);
            }
          }
        }
      }

      return ClassState.JSO;
    }

    private boolean checkClassImplementingJsType(TypeDeclaration type) {
      ReferenceBinding jsInterface = findNearestJsTypeRecursive(type.binding);
      if (jsInterface == null) {
        return false;
      }

      AnnotationBinding jsinterfaceAnn = JdtUtil.getAnnotation(jsInterface,
          JsInteropUtil.JSTYPE_CLASS);
      String jsPrototype = JdtUtil.getAnnotationParameterString(jsinterfaceAnn, "prototype");
      boolean shouldExtend = !Strings.isNullOrEmpty(jsPrototype);
      checkClassExtendsMagicPrototype(type, jsInterface, shouldExtend);

      // TODO(cromwellian) add multiple-inheritance checks when ambiguity in spec is resolved
      return true;
    }

    private void checkClassExtendsMagicPrototype(TypeDeclaration type, ReferenceBinding jsInterface,
        boolean shouldExtend) {
      ReferenceBinding superClass = type.binding.superclass();
      // if type is the _Prototype stub (implements JsType) exit
      if (isMagicPrototype(type.binding, jsInterface)) {
        return;
      } else if (isMagicPrototypeStub(type)) {
        errorOn(type, ERR_FORGOT_TO_MAKE_PROTOTYPE_IMPL_JSTYPE);
      }

      if (shouldExtend) {
        // super class should be SomeInterface.Prototype, so enclosing type should match the jsInterface
        if (LINT_MODE && (superClass == null || !isMagicPrototype(superClass, jsInterface))) {
          errorOn(type, ERR_MUST_EXTEND_MAGIC_PROTOTYPE_CLASS);
        }
      } else {
        if (superClass != null && isMagicPrototype(superClass, jsInterface)) {
          errorOn(type, ERR_CLASS_EXTENDS_MAGIC_PROTOTYPE_BUT_NO_PROTOTYPE_ATTRIBUTE);
        }
      }
    }

    // Roughly parallels JProgram.isJsTypePrototype()
    private boolean isMagicPrototype(ReferenceBinding type, ReferenceBinding jsInterface) {
      if (isMagicPrototypeStub(type)) {
        for (ReferenceBinding intf : type.superInterfaces()) {
          if (intf == jsInterface) {
            return true;
          }
        }
      }
      return false;
    }

    private boolean isMagicPrototypeStub(TypeDeclaration type) {
      return isMagicPrototypeStub(type.binding);
    }

    private boolean isMagicPrototypeStub(ReferenceBinding binding) {
      return JdtUtil.getAnnotation(binding, JsInteropUtil.JSTYPEPROTOTYPE_CLASS) != null;
    }

    /**
     * Walks up chain of interfaces and superinterfaces to find the first one marked with @JsType.
     */
    private ReferenceBinding findNearestJsType(ReferenceBinding binding) {
      if (isJsType(binding)) {
        return binding;
      }

      for (ReferenceBinding intb : binding.superInterfaces()) {
        ReferenceBinding checkSuperInt = findNearestJsType(intb);
        if (checkSuperInt != null) {
          return checkSuperInt;
        }
      }
      return null;
    }

    private ReferenceBinding findNearestJsTypeRecursive(ReferenceBinding binding) {
      ReferenceBinding nearest = findNearestJsType(binding);
      if (nearest != null) {
        return nearest;
      } else if (binding.superclass() != null) {
        return findNearestJsTypeRecursive(binding.superclass());
      }
      return null;
    }

    private boolean areAllEnclosingClassesPublic() {
      for (SourceTypeBinding typeBinding : typeBindingStack) {
        if (!typeBinding.isPublic()) {
          return false;
        }
      }
      return true;
    }

    private boolean isJso() {
      return classStateStack.peek() == ClassState.JSO;
    }

    private void popState() {
      classStateStack.pop();
      typeBindingStack.pop();
    }

    private void pushState(TypeDeclaration type) {
      classStateStack.push(checkType(type));
      typeBindingStack.push(type.binding);
    }
  }

  /**
   * Checks an entire
   * {@link org.eclipse.jdt.internal.compiler.ast.CompilationUnitDeclaration}.
   *
   */
  public static void check(CheckerState state, CompilationUnitDeclaration cud) {
    JSORestrictionsChecker checker = new JSORestrictionsChecker(state, cud);
    checker.check();
  }

  /**
   * Returns {@code true} if {@code typeBinding} is {@code JavaScriptObject} or
   * any subtype.
   */
  public static boolean isJso(TypeBinding typeBinding) {
    if (!(typeBinding instanceof ReferenceBinding)) {
      return false;
    }
    ReferenceBinding binding = (ReferenceBinding) typeBinding;
    while (binding != null) {
      if (JSO_CLASS.equals(String.valueOf(binding.constantPoolName()))) {
        return true;
      }
      binding = binding.superclass();
    }
    return false;
  }

  /**
   * Returns the first JsType annotation encountered traversing the type hierarchy upwards from the type.
   */
  private boolean isJsType(TypeBinding typeBinding) {

    if (!(typeBinding instanceof ReferenceBinding) || !(typeBinding instanceof SourceTypeBinding)) {
      return false;
    }

    AnnotationBinding jsInterface = JdtUtil.getAnnotation(typeBinding, JsInteropUtil.JSTYPE_CLASS);
    return jsInterface != null;
  }

  /**
   * Returns {@code true} if {@code typeBinding} is a subtype of
   * {@code JavaScriptObject}, but not {@code JavaScriptObject} itself.
   */
  public static boolean isJsoSubclass(TypeBinding typeBinding) {
    if (!(typeBinding instanceof ReferenceBinding)) {
      return false;
    }
    ReferenceBinding binding = (ReferenceBinding) typeBinding;
    return isJso(binding.superclass());
  }

  static String errAlreadyImplemented(String intfName, String impl1,
      String impl2) {
    return "Only one JavaScriptObject type may implement the methods of an "
        + "interface that declared methods. The interface (" + intfName
        + ") is implemented by both (" + impl1 + ") and (" + impl2 + ")";
  }

  private static void errorOn(ASTNode node, CompilationUnitDeclaration cud,
      String error) {
    GWTProblem.recordError(node, cud, error, new InstalledHelpInfo(
        "jsoRestrictions.html"));
  }

  private final CompilationUnitDeclaration cud;
  private final CheckerState state;

  private JSORestrictionsChecker(CheckerState state,
      CompilationUnitDeclaration cud) {
    this.cud = cud;
    this.state = state;
  }

  private void check() {
    cud.traverse(new JSORestrictionsVisitor(), cud.scope);
  }

  private void errorOn(ASTNode node, String error) {
    errorOn(node, cud, error);
  }

  private void errorOn(MethodBinding mb, String error) {
    ASTNode node = JdtUtil.safeSourceMethod(mb);
    if (node == null) {
      node = cud;
      // Workaround for bad JDT bug
      error = "Error in " + mb.toString() + ": " + error;
    }
    errorOn(node, cud, error);
  }

  private void errorOn(FieldBinding fb, String error) {
    ASTNode node = fb.sourceField();
    errorOn(node, cud, error);
  }
}
