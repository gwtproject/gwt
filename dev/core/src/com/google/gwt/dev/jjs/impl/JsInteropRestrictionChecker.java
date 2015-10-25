/*
 * Copyright 2015 Google Inc.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package com.google.gwt.dev.jjs.impl;

import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.dev.MinimalRebuildCache;
import com.google.gwt.dev.javac.JsInteropUtil;
import com.google.gwt.dev.jjs.ast.CanHaveSuppressedWarnings;
import com.google.gwt.dev.jjs.ast.Context;
import com.google.gwt.dev.jjs.ast.JClassType;
import com.google.gwt.dev.jjs.ast.JConstructor;
import com.google.gwt.dev.jjs.ast.JDeclarationStatement;
import com.google.gwt.dev.jjs.ast.JDeclaredType;
import com.google.gwt.dev.jjs.ast.JExpression;
import com.google.gwt.dev.jjs.ast.JExpressionStatement;
import com.google.gwt.dev.jjs.ast.JField;
import com.google.gwt.dev.jjs.ast.JInstanceOf;
import com.google.gwt.dev.jjs.ast.JInterfaceType;
import com.google.gwt.dev.jjs.ast.JMember;
import com.google.gwt.dev.jjs.ast.JMethod;
import com.google.gwt.dev.jjs.ast.JMethod.JsPropertyAccessorType;
import com.google.gwt.dev.jjs.ast.JMethodBody;
import com.google.gwt.dev.jjs.ast.JMethodCall;
import com.google.gwt.dev.jjs.ast.JParameter;
import com.google.gwt.dev.jjs.ast.JPrimitiveType;
import com.google.gwt.dev.jjs.ast.JProgram;
import com.google.gwt.dev.jjs.ast.JReferenceType;
import com.google.gwt.dev.jjs.ast.JStatement;
import com.google.gwt.dev.jjs.ast.JType;
import com.google.gwt.dev.jjs.ast.JVisitor;
import com.google.gwt.thirdparty.guava.common.base.Predicate;
import com.google.gwt.thirdparty.guava.common.collect.Collections2;
import com.google.gwt.thirdparty.guava.common.collect.FluentIterable;
import com.google.gwt.thirdparty.guava.common.collect.Iterables;
import com.google.gwt.thirdparty.guava.common.collect.LinkedHashMultimap;
import com.google.gwt.thirdparty.guava.common.collect.Lists;
import com.google.gwt.thirdparty.guava.common.collect.Multimap;

import java.util.Collection;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Checks and throws errors for invalid JsInterop constructs.
 */
public class JsInteropRestrictionChecker {

  public static void exec(TreeLogger logger, JProgram jprogram,
      MinimalRebuildCache minimalRebuildCache) throws UnableToCompleteException {
    JsInteropRestrictionChecker jsInteropRestrictionChecker =
        new JsInteropRestrictionChecker(logger, jprogram, minimalRebuildCache);

    jsInteropRestrictionChecker.checkProgram();
    if (jsInteropRestrictionChecker.hasErrors) {
      throw new UnableToCompleteException();
    }
  }

  private boolean hasErrors;
  private final JProgram jprogram;
  private final TreeLogger logger;
  private final MinimalRebuildCache minimalRebuildCache;

  // TODO review any use of word export

  private JsInteropRestrictionChecker(TreeLogger logger, JProgram jprogram,
      MinimalRebuildCache minimalRebuildCache) {
    this.logger = logger;
    this.jprogram = jprogram;
    this.minimalRebuildCache = minimalRebuildCache;
  }

  /**
   * Returns true if the constructor method is locally empty (allows calls to empty init and super
   * constructor).
   */
  private static boolean isConstructorEmpty(final JConstructor constructor) {
    List<JStatement> statements = FluentIterable
        .from(constructor.getBody().getStatements())
        .filter(new Predicate<JStatement>() {
          @Override
          public boolean apply(JStatement statement) {
            JClassType type = constructor.getEnclosingType();
            if (isImplicitSuperCall(statement, type.getSuperClass())) {
              return false;
            }
            if (isEmptyInitCall(statement, type)) {
              return false;
            }
            if (statement instanceof JDeclarationStatement) {
              return ((JDeclarationStatement) statement).getInitializer() != null;
            }
            return true;
          }
        }).toList();
    return statements.isEmpty();
  }

  private static JMethodCall isMethodCall(JStatement statement) {
    if (!(statement instanceof JExpressionStatement)) {
      return null;
    }
    JExpression expression = ((JExpressionStatement) statement).getExpr();

    return expression instanceof JMethodCall ? (JMethodCall) expression : null;
  }

  private static boolean isEmptyInitCall(JStatement statement, JDeclaredType type) {
    JMethodCall methodCall = isMethodCall(statement);

    return methodCall != null
        && methodCall.getTarget() == type.getInitMethod()
        && ((JMethodBody) methodCall.getTarget().getBody()).getStatements().isEmpty();
  }

  private static boolean isImplicitSuperCall(JStatement statement, JDeclaredType superType) {
    JMethodCall methodCall = isMethodCall(statement);

    return methodCall != null
        && methodCall.isStaticDispatchOnly()
        && methodCall.getTarget().isConstructor()
        && methodCall.getTarget().getEnclosingType() == superType;
  }

  /**
   * Returns true if the clinit for a type is locally empty (except for the call to its super
   * clinit).
   */
  private static boolean isClinitEmpty(JDeclaredType type) {
    JMethod clinit = type.getClinitMethod();
    List<JStatement> statements = FluentIterable
        .from(((JMethodBody) clinit.getBody()).getStatements())
        .filter(new Predicate<JStatement>() {
          @Override
          public boolean apply(JStatement statement) {
            if (!(statement instanceof JDeclarationStatement)) {
              return true;
            }
            JDeclarationStatement declarationStatement = (JDeclarationStatement) statement;
            JField field = (JField) declarationStatement.getVariableRef().getTarget();
            return !field.isCompileTimeConstant();
          }
        }).toList();
    if (statements.isEmpty()) {
      return true;
    }
    return statements.size() == 1 && isClinitCall(statements.get(0), type.getSuperClass());
  }

  private static boolean isClinitCall(JStatement statement, JClassType superClass) {
    if (superClass == null || !(statement instanceof JExpressionStatement)) {
      return false;
    }

    JExpression expression = ((JExpressionStatement) statement).getExpr();
    if (!(expression instanceof JMethodCall)) {
      return false;
    }
    return ((JMethodCall) expression).getTarget() == superClass.getClinitMethod();
  }

  private void checkJsConstructors(JDeclaredType x) {
    List<JMethod> jsConstructors = FluentIterable
        .from(x.getMethods())
        .filter(new Predicate<JMethod>() {
          @Override
          public boolean apply(JMethod m) {
            return m.isJsConstructor();
          }
        }).toList();

    if (jsConstructors.isEmpty()) {
      return;
    }

    if (jsConstructors.size() > 1) {
      logError("More than one JsConstructor exists for %s.", x.getName());
    }

    final JConstructor jsConstructor = (JConstructor) jsConstructors.get(0);

    boolean anyNonDelegatingConstructor = Iterables.any(x.getMethods(), new Predicate<JMethod>() {
      @Override
      public boolean apply(JMethod method) {
        return method != jsConstructor && method instanceof JConstructor
            && !isDelegatingToConstructor((JConstructor) method, jsConstructor);
      }
    });

    if (anyNonDelegatingConstructor) {
      logError("Constructor %s can be a JsConstructor only if all constructors in the class are "
          + "delegating to it.", getDescription(jsConstructor));
    }
  }

  private boolean isDelegatingToConstructor(JConstructor ctor, JConstructor targetCtor) {
    List<JStatement> statements = ctor.getBody().getBlock().getStatements();
    JExpressionStatement statement = (JExpressionStatement) statements.get(0);
    JMethodCall call = (JMethodCall) statement.getExpr();
    assert call.isStaticDispatchOnly() : "Every ctor should either have this() or super() call";
    return call.getTarget().equals(targetCtor);
  }

  private void checkField(JField x) {
    if (x.getEnclosingType().isJsNative()) {
      checkMemberOfNativeJsType(x);
    }
    checkUnusableByJs(x);

    if (!x.isJsProperty()) {
      return;
    }

    if (!x.needsDynamicDispatch() && !x.getEnclosingType().isJsNative()) {
      checkGlobalName(x);
    }
  }

  private void checkMethod(JMethod x) {
    if (x.getEnclosingType().isJsNative()) {
      checkMemberOfNativeJsType(x);
    }

    if (x.isJsOverlay()) {
      checkJsOverlay(x);
    }

    checkUnusableByJs(x);

    if (!x.isOrOverridesJsMethod()) {
      return;
    }

    if (!x.needsDynamicDispatch() && !x.getEnclosingType().isJsNative()) {
      checkGlobalName(x);
    }
  }

  private void checkGlobalName(JMember x) {
    if (!minimalRebuildCache.addExportedGlobalName(x.getQualifiedJsName(),
        x.getEnclosingType().getName())) {
      logError("%s can't be exported because the global name '%s' is already taken.",
          getDescription(x), x.getQualifiedJsName());
    }
  }

  private void checkLocalNames(JDeclaredType type) {
    Multimap<String, JMember> memberByLocalMemberNames = collectLocalNames(type);
    for (String jsName : memberByLocalMemberNames.keySet()) {

      Collection<JMember> members = memberByLocalMemberNames.get(jsName);
      Collection<JMember> setters = Collections2.filter(members, isSetterPredicate);
      Collection<JMember> getters =  Collections2.filter(members, isGetterPredicate);

      Iterator<JMember> iterator = members.iterator();
      JMember member = iterator.next();
      List<JMember> conflictingMembers = Lists.newArrayList(iterator);

      if (member.getEnclosingType() != type) {
        // Errors in the supertypes are reported when supertypes are checked.
        continue;
      }

      if (jsName.equals(JsInteropUtil.INVALID_JSNAME)) {
        // We couldn't extract the JsPropertyType.
        logInvalidMemberErros(type, members);
        continue;
      }

      if (!member.getEnclosingType().isJsNative()) {
        // Check for name conflicts, for non native jsTypes.
        int nonAccessorMembers = members.size() - setters.size() - getters.size();
        if ((nonAccessorMembers > 0 && (!setters.isEmpty() || !getters.isEmpty()))
            || nonAccessorMembers > 1) {
          logError("%s can't be exported because the name '%s' is already taken by %s.",
              getDescription(member), jsName,
              getDescription(conflictingMembers.get(0)));
        }

        checkJsPropertyGetterConsistentWithSetter(jsName, type,
            (JMethod) Iterables.getFirst(setters, null),
            (JMethod) Iterables.getFirst(getters, null));
      }

      if (setters.size() > 1) {
        // Don't allow multiple getters for the same property name.
        logError("There can't be more than one setter for JsProperty '%s' in type '%s'.",
            jsName, type.getName());
        continue;
      }
      if (getters.size() > 1) {
        // Don't allow multiple getters for the same property name.
        logError("There can't be more than one getter for JsProperty '%s' in type '%s'.",
            jsName, type.getName());
        continue;
      }

      // Check that JsPropeties are correctly defined.
      for (JMember getter : getters) {
        if (getter.getEnclosingType() != type) {
          break;
        }
        checkValidGetter((JMethod) getter);

      }
      for (JMember setter : setters) {
        if (setter.getEnclosingType() != type) {
          break;
        }
        checkValidSetter((JMethod) setter);
      }
    }
  }

  private void checkJsPropertyGetterConsistentWithSetter(
      String propertyName, JType type,  JMethod setter, JMethod getter) {
    if (getter != null && setter != null &&
        setter.getParams().size() == 1
        &&  getter.getType() != setter.getParams().get(0).getType()) {
      logError("The setter and getter for JsProperty '%s' in type '%s' must have consistent types.",
          propertyName, type.getName());
    }
  }

  private void logInvalidMemberErros(JDeclaredType type, Collection<JMember> members) {
    for (JMember invalidMember : members) {
      if (invalidMember.getEnclosingType() != type) {
        break;
      }
      if (invalidMember instanceof JMethod
          && ((JMethod) invalidMember).getJsPropertyAccessorType()
          == JsPropertyAccessorType.UNDEFINED) {
        logError("JsProperty %s doesn't follow Java Bean naming conventions.",
            getDescription(invalidMember));
      } else {
        logError("%s can't be exported because the method overrides a method with "
            + "different name.", getDescription(invalidMember));
      }
    }
  }

  private void checkValidSetter(JMethod setter) {
    JMethod method = setter;
    if (method.getParams().size() != 1 || method.getType() != JPrimitiveType.VOID) {
      logError("There needs to be single parameter and void return type for the "
          + "JsProperty setter %s.", getDescription(method));
    }
  }

  private void checkValidGetter(JMethod getter) {
    if (!getter.getParams().isEmpty() || getter.getType() == JPrimitiveType.VOID) {
      logError("There can't be void return type or any parameters for the JsProperty getter"
          + " %s.", getDescription(getter));
      return;
    }
    if (getter.getType() != JPrimitiveType.BOOLEAN && getter.getName().startsWith("is")) {
      logError("There can't be non-boolean return for the JsProperty 'is' getter %s.",
          getDescription(getter));
    }
  }

  private void checkJsOverlay(JMethod method) {
    if (method.getEnclosingType().isJsoType()) {
      return;
    }

    String qualifiedName = method.getQualifiedName();

    if (!method.getEnclosingType().isJsNative()) {
      logError("Method '%s' in non-native type cannot be @JsOverlay.", qualifiedName);
    }

    if (!method.getOverriddenMethods().isEmpty()) {
      logError("JsOverlay method '%s' cannot override a supertype method.", qualifiedName);
      return;
    }

    if (method.isJsNative() || method.isJsniMethod() || method.isStatic() || !method.isFinal()) {
      logError("JsOverlay method '%s' cannot be non-final, static, nor native.", qualifiedName);
    }
  }

  private void checkMemberOfNativeJsType(JMember member) {
    if (member.isSynthetic()) {
      return;
    }

    if (member.getJsName() == null && !member.isJsOverlay()) {
      logError("Native JsType member %s is not public or has @JsIgnore.",
          getDescription(member));
      return;
    }
  }

  private void checkStaticJsPropertyCalls() {
    new JVisitor() {
      @Override
      public boolean visit(JMethod x, Context ctx) {
        // Skip unnecessary synthetic override, as they will not be generated.
        return !JjsUtils.isUnnecessarySyntheticAccidentalOverride(x);
      }

      @Override
      public void endVisit(JMethodCall x, Context ctx) {
        JMethod target = x.getTarget();
        if (x.isStaticDispatchOnly() && target.isJsPropertyAccessor()) {
          logError("Cannot call property accessor %s via super (%s:%d).",
              getDescription(target),
              x.getSourceInfo().getFileName(),
              x.getSourceInfo().getStartLine());
        }
      }
    }.accept(jprogram);
  }

  private void checkInstanceOfNativeJsTypes() {
    new JVisitor() {
      @Override
      public boolean visit(JInstanceOf x, Context ctx) {
        JReferenceType type = x.getTestType();
        if (type.isJsNative() && type instanceof JInterfaceType) {
          logError("Cannot do instanceof against native JsType interface %s (%s:%d).",
              type.getName(),
              x.getSourceInfo().getFileName(),
              x.getSourceInfo().getStartLine());
        }
        return true;
      }
    }.accept(jprogram);
  }

  private void checkNativeJsType(JDeclaredType type) {
    // TODO(rluble): add inheritance restrictions.
    if (type.isEnumOrSubclass() != null) {
      logError("Enum '%s' cannot be a native JsType.", type);
      return;
    }
    if (!isClinitEmpty(type)) {
      logError("Native JsType '%s' cannot have static initializer.", type);
    }

    for (JConstructor constructor : type.getConstructors()) {
      if (!isConstructorEmpty(constructor)) {
        logError("Native JsType constructor %s cannot have non-empty method body.",
            getDescription(constructor));
      }
    }
  }

  private void checkJsFunction(JDeclaredType type) {
    if (!isClinitEmpty(type)) {
      logError("JsFunction '%s' cannot have static initializer.", type);
    }

    if (type.getImplements().size() > 0) {
      logError("JsFunction '%s' cannot extend other interfaces.", type);
    }

    if (type.isJsType()) {
      logError("'%s' cannot be both a JsFunction and a JsType at the same time.", type);
    }

    Set<String> subTypes = jprogram.typeOracle.getSubInterfaceNames(type.getName());
    if (!subTypes.isEmpty()) {
      logError("JsFunction '%s' cannot be extended by other interfaces:%s", type, subTypes);
    }
  }

  private void checkJsFunctionImplementation(JDeclaredType type) {
    if (type.getImplements().size() != 1) {
      logError("JsFunction implementation '%s' cannot implement more than one interface.", type);
    }

    if (type.isJsType()) {
      logError("'%s' cannot be both a JsFunction implementation and a JsType at the same time.",
          type);
    }

    if (type.getSuperClass() != jprogram.getTypeJavaLangObject()) {
      logError("JsFunction implementation '%s' cannot extend a class.", type);
    }

    Set<String> subTypes = jprogram.typeOracle.getSubClassNames(type.getName());
    if (!subTypes.isEmpty()) {
      logError("Implementation of JsFunction '%s' cannot be extended by other classes:%s", type,
          subTypes);
    }
  }

  private void checkProgram() {
    for (JDeclaredType type : jprogram.getModuleDeclaredTypes()) {
      checkType(type);
    }
    checkStaticJsPropertyCalls();
    checkInstanceOfNativeJsTypes();
  }

  private void checkType(JDeclaredType type) {
    minimalRebuildCache.removeExportedNames(type.getName());

    if (type.isJsNative()) {
      checkNativeJsType(type);
    }

    if (type.isJsFunction()) {
      checkJsFunction(type);
    } else if (type.isJsFunctionImplementation()) {
      checkJsFunctionImplementation(type);
    } else {
      checkJsConstructors(type);
    }

    checkLocalNames(type);

    for (JField field : type.getFields()) {
      checkField(field);
    }
    for (JMethod method : type.getMethods()) {
      checkMethod(method);
    }
  }

  private void checkUnusableByJs(JMethod method) {
    if (!method.canBeCalledExternally() || isUnusableByJsSuppressed(method.getEnclosingType())
        || isUnusableByJsSuppressed(method)) {
      return;
    }
    // check parameters.
    for (JParameter parameter : method.getParams()) {
      if (!parameter.getType().canBeReferencedExternally()
          && !isUnusableByJsSuppressed(parameter)) {
        logWarning(
            "[unusable-by-js] Type of parameter '%s' in method %s is not usable by but exposed to"
            + " JavaScript.",
            parameter.getName(), getDescription(method));
      }
    }
    // check return type.
    if (!method.getType().canBeReferencedExternally()) {
      logWarning(
          "[unusable-by-js] Return type of %s is not usable by but exposed to JavaScript.",
          getDescription(method));
    }
  }

  private void checkUnusableByJs(JField field) {
    if (!field.canBeReferencedExternally() || isUnusableByJsSuppressed(field.getEnclosingType())
        || isUnusableByJsSuppressed(field)) {
      return;
    }
    if (!field.getType().canBeReferencedExternally()) {
      logWarning(
          "[unusable-by-js] Type of field '%s' in type '%s' is not usable by but exposed to "
              + "JavaScript.",
          field.getName(), field.getEnclosingType().getName());
    }
  }

  private Multimap<String, JMember> collectLocalNames(JDeclaredType type) {
    Multimap<String, JMember> memberByLocalMemberNames = LinkedHashMultimap.create();
    for (;type != null; type = type.getSuperClass())  {
      for (JField field : type.getFields()) {
        if (!field.isJsProperty()) {
          continue;
        }
        if (field.needsDynamicDispatch()) {
          memberByLocalMemberNames.put(field.getJsName(), field);
        }
      }
      for (JMethod method : type.getMethods()) {
        if (!method.isOrOverridesJsMethod()
            || (method.isSynthetic() && !method.isForwarding())) {
          continue;
        }

        String jsMethodName = method.getJsName();
        if (method.needsDynamicDispatch() &&
            !overriddenByCollectedMethod(method, memberByLocalMemberNames.get(jsMethodName))) {
          memberByLocalMemberNames.put(jsMethodName, method);
        }
      }
    }
    return memberByLocalMemberNames;
  }

  private boolean overriddenByCollectedMethod(JMethod method, Iterable<JMember> collectedMembers) {
    for (JMember collectedMember : collectedMembers) {
      if (method.getOverridingMethods().contains(collectedMember)) {
        return true;
      }
    }
    return false;
  }

  private static String getDescription(JMember member) {
    if (member instanceof JField) {
      return String.format("'%s'", member.getQualifiedName());
    }
    JMethod method = (JMethod) member;
    if (method.isSyntheticAccidentalOverride()) {
      return String.format("'%s' (exposed by '%s')",
          method.getOverriddenMethods().iterator().next().getQualifiedName(),
          method.getEnclosingType().getName());
    }
    return String.format("'%s'", member.getQualifiedName());
  }

  private boolean isUnusableByJsSuppressed(CanHaveSuppressedWarnings x) {
    return x.getSuppressedWarnings() != null &&
        x.getSuppressedWarnings().contains(JsInteropUtil.UNUSABLE_BY_JS);
  }

  private void logError(String format, JType type) {
    logError(format, type.getName());
  }

  private void logError(String format, JType type, Set<String> subTypes) {
    StringBuilder subTypeNames = new StringBuilder();
    for (String typeName : subTypes) {
      subTypeNames.append("\n\t").append(typeName);
    }
    logError(format, type.getName(), subTypeNames);
  }

  private void logError(String format, Object... args) {
    logger.log(TreeLogger.ERROR, String.format(format, args));
    hasErrors = true;
  }

  private void logWarning(String format, Object... args) {
    logger.log(TreeLogger.WARN, String.format(format, args));
  }

  private static Predicate<JMember> createAccessorPredicate(final EnumSet types) {
    return new Predicate<JMember>() {
      @Override
      public boolean apply(JMember member) {
        return member instanceof JMethod
            && types.contains(((JMethod) member).getJsPropertyAccessorType());
      }
    };
  }

  private static Predicate<JMember> isSetterPredicate = createAccessorPredicate(
      EnumSet.of(JsPropertyAccessorType.SETTER));
  private static Predicate<JMember> isGetterPredicate = createAccessorPredicate(
      EnumSet.of(JsPropertyAccessorType.GETTER));
  private static Predicate<JMember> isRegularMethodPredicate = createAccessorPredicate(
      EnumSet.of(JsPropertyAccessorType.NONE));
}
