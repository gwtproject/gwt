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
import com.google.gwt.dev.jjs.ast.Context;
import com.google.gwt.dev.jjs.ast.JConstructor;
import com.google.gwt.dev.jjs.ast.JDeclaredType;
import com.google.gwt.dev.jjs.ast.JExpressionStatement;
import com.google.gwt.dev.jjs.ast.JField;
import com.google.gwt.dev.jjs.ast.JInterfaceType;
import com.google.gwt.dev.jjs.ast.JMember;
import com.google.gwt.dev.jjs.ast.JMethod;
import com.google.gwt.dev.jjs.ast.JMethod.JsPropertyAccessorType;
import com.google.gwt.dev.jjs.ast.JMethodCall;
import com.google.gwt.dev.jjs.ast.JPrimitiveType;
import com.google.gwt.dev.jjs.ast.JProgram;
import com.google.gwt.dev.jjs.ast.JStatement;
import com.google.gwt.dev.jjs.ast.JType;
import com.google.gwt.dev.jjs.ast.JVisitor;
import com.google.gwt.thirdparty.guava.common.base.Predicate;
import com.google.gwt.thirdparty.guava.common.collect.FluentIterable;
import com.google.gwt.thirdparty.guava.common.collect.Iterables;
import com.google.gwt.thirdparty.guava.common.collect.Maps;
import com.google.gwt.thirdparty.guava.common.collect.Sets;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Checks and throws errors for invalid JsInterop constructs.
 */
// TODO: handle custom JsType field/method names when that feature exists.
// TODO: move JsInterop checks from JSORestrictionsChecker to here.
// TODO: provide more information in global name collisions as it could be difficult to pinpoint in
// big projects.
public class JsInteropRestrictionChecker extends JVisitor {

  public static void exec(TreeLogger logger, JProgram jprogram,
      MinimalRebuildCache minimalRebuildCache) throws UnableToCompleteException {
    JsInteropRestrictionChecker jsInteropRestrictionChecker =
        new JsInteropRestrictionChecker(logger, jprogram, minimalRebuildCache);
    jsInteropRestrictionChecker.accept(jprogram);
    if (jsInteropRestrictionChecker.hasErrors) {
      throw new UnableToCompleteException();
    }
  }

  private Map<String, String> currentJsMethodNameByGetterNames;
  private Map<String, String> currentJsMethodNameBySetterNames;
  private Map<String, JType> currentJsPropertyTypeByName;
  private Map<String, String> currentLocalNameByMemberNames;
  private Set<JMethod> currentProcessedMethods;
  private JDeclaredType currentType;
  private boolean hasErrors;
  private final JProgram jprogram;
  private final TreeLogger logger;
  private final MinimalRebuildCache minimalRebuildCache;

  public JsInteropRestrictionChecker(TreeLogger logger, JProgram jprogram,
      MinimalRebuildCache minimalRebuildCache) {
    this.logger = logger;
    this.jprogram = jprogram;
    this.minimalRebuildCache = minimalRebuildCache;
  }

  @Override
  public void endVisit(JDeclaredType x, Context ctx) {
    assert currentType == x;
    currentType = null;
  }

  @Override
  public boolean visit(JDeclaredType x, Context ctx) {
    assert currentType == null;
    currentProcessedMethods = Sets.newHashSet();
    currentJsPropertyTypeByName = Maps.newHashMap();
    currentJsMethodNameByGetterNames = Maps.newHashMap();
    currentJsMethodNameBySetterNames = Maps.newHashMap();
    currentLocalNameByMemberNames = Maps.newHashMap();
    minimalRebuildCache.removeJsInteropNames(x.getName());
    currentType = x;

    if (x.isJsFunction()) {
      checkJsFunction(x);
    } else if (x.isJsFunctionImplementation()) {
      checkJsFunctionImplementation(x);
    } else if (x.isJsType() && x instanceof JInterfaceType) {
      checkJsInterface(x);
    } else {
      checkJsConstructors(x);
    }

    // Perform custom class traversal to examine fields and methods of this class and all
    // superclasses so that name collisions between local and inherited members can be found.
    do {
      acceptWithInsertRemoveImmutable(x.getFields());
      acceptWithInsertRemoveImmutable(x.getMethods());
      x = x.getSuperClass();
    } while (x != null);

    // Skip the default class traversal.
    return false;
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
    if (!jsConstructor.getJsName().isEmpty()) {
      logError("Constructor '%s' cannot have an export name.", jsConstructor.getQualifiedName());
    }

    boolean anyNonDelegatingConstructor = Iterables.any(x.getMethods(), new Predicate<JMethod>() {
      @Override
      public boolean apply(JMethod method) {
        return method != jsConstructor && method instanceof JConstructor
            && !isDelegatingToConstructor((JConstructor) method, jsConstructor);
      }
    });

    if (anyNonDelegatingConstructor) {
      logError("Constructor '%s' can be a JsConstructor only if all constructors in the class are "
          + "delegating to it.", jsConstructor.getQualifiedName());
    }
  }

  private boolean isDelegatingToConstructor(JConstructor ctor, JConstructor targetCtor) {
    List<JStatement> statements = ctor.getBody().getBlock().getStatements();
    JExpressionStatement statement = (JExpressionStatement) statements.get(0);
    JMethodCall call = (JMethodCall) statement.getExpr();
    assert call.isStaticDispatchOnly() : "Every ctor should either have this() or super() call";
    return call.getTarget().equals(targetCtor);
  }

  @Override
  public boolean visit(JField x, Context ctx) {
    if (!x.isJsProperty()) {
      return false;
    }

    if (x.needsVtable()) {
      checkLocalName(x);
    } else if (currentType == x.getEnclosingType()) {
      checkGlobalName(x);
    }

    return false;
  }

  @Override
  public boolean visit(JMethod x, Context ctx) {
    if (!currentProcessedMethods.add(x)) {
      return false;
    }
    currentProcessedMethods.addAll(x.getOverriddenMethods());

    if (!x.isOrOverridesJsMethod()) {
      return false;
    }

    if (x.needsVtable()) {
      checkJsMethod(x);
    } else if (currentType == x.getEnclosingType()) {
      checkGlobalName(x);
    }

    return false;
  }

  private void checkGlobalName(JMember x) {
    if (!minimalRebuildCache.addGlobalName(x.getQualifiedExportName(), currentType.getName())) {
      logError("'%s' can't be exported because the global name '%s' is already taken.",
          x.getQualifiedName(), x.getQualifiedExportName());
    }
  }

  private void checkLocalName(JMember member) {
    String jsName = member.getJsName();
    if (currentLocalNameByMemberNames.put(jsName, member.getQualifiedName()) != null) {
      logError("'%s' can't be exported in type '%s' because the name '%s' is already taken.",
          member.getQualifiedName(), currentType.getName(), jsName);
    }
  }

  private void checkJsPropertyType(String propertyName, String enclosingTypeName, JType type) {
    JType recordedType = currentJsPropertyTypeByName.put(propertyName, type);
    if (recordedType != null && recordedType != type) {
      logError("The setter and getter for JsProperty '%s' in type '%s' must have consistent types.",
          propertyName, enclosingTypeName);
    }
  }

  private void checkJsInterface(JDeclaredType interfaceType) {
    for (JDeclaredType superInterface : interfaceType.getImplements()) {
      if (!superInterface.isJsType()) {
        logWarning(
            "JsType interface '%s' extends non-JsType interface '%s'. This is not recommended.",
            interfaceType.getName(), superInterface.getName());
      }
    }
  }

  private void checkJsMethod(JMethod method) {
    if (method.isSynthetic() && !method.isForwarding()) {
      // A name slot taken up by a synthetic method, such as a bridge method for a generic method,
      // is not the fault of the user and so should not be reported as an error. JS generation
      // should take responsibility for ensuring that only the correct method version (in this
      // particular set of colliding method names) is exported. Forwarding synthetic methods
      // (such as an accidental override forwarding method that occurs when a JsType interface
      // starts exposing a method in class B that is only ever implemented in its parent class A)
      // though should be checked since they are exported and do take up an name slot.
      return;
    }

    String jsMemberName = method.getJsName();
    String qualifiedMethodName = method.getQualifiedName();
    String typeName = method.getEnclosingType().getName();
    JsPropertyAccessorType accessorType = method.getJsPropertyAccessorType();

    if (jsMemberName == null) {
      logError("'%s' can't be exported because the method overloads multiple methods with "
          + "different names.", qualifiedMethodName);
    }

    if (accessorType == JsPropertyAccessorType.GETTER) {
      if (!method.getParams().isEmpty() || method.getType() == JPrimitiveType.VOID) {
        logError("There can't be void return type or any parameters for the JsProperty getter"
            + " '%s'.", qualifiedMethodName);
        return;
      }
      if (method.getType() != JPrimitiveType.BOOLEAN && method.getName().startsWith("is")) {
        logError("There can't be non-booelean return for the JsProperty 'is' getter '%s'.",
            qualifiedMethodName);
        return;
      }
      if (currentJsMethodNameByGetterNames.put(jsMemberName, qualifiedMethodName) != null) {
        // Don't allow multiple getters for the same property name.
        logError("There can't be more than one getter for JsProperty '%s' in type '%s'.",
            jsMemberName, typeName);
        return;
      }
      checkNameCollisionForGetterAndRegular(jsMemberName, typeName);
      checkJsPropertyType(jsMemberName, typeName, method.getOriginalReturnType());
    } else if (accessorType == JsPropertyAccessorType.SETTER) {
      if (method.getParams().size() != 1 || method.getType() != JPrimitiveType.VOID) {
        logError("There needs to be single parameter and void return type for the JsProperty setter"
            + " '%s'.", qualifiedMethodName);
        return;
      }
      if (currentJsMethodNameBySetterNames.put(jsMemberName, qualifiedMethodName) != null) {
        // Don't allow multiple setters for the same property name.
        logError("There can't be more than one setter for JsProperty '%s' in type '%s'.",
            jsMemberName, typeName);
        return;
      }
      checkNameCollisionForSetterAndRegular(jsMemberName, typeName);
      checkJsPropertyType(jsMemberName, typeName,
          Iterables.getOnlyElement(method.getParams()).getType());
    } else if (accessorType == JsPropertyAccessorType.UNDEFINED) {
      // We couldn't extract the JsPropertyType.
      logError("JsProperty '%s' doesn't follow Java Bean naming conventions.", qualifiedMethodName);
    } else {
      checkLocalName(method);
      checkNameCollisionForGetterAndRegular(jsMemberName, typeName);
      checkNameCollisionForSetterAndRegular(jsMemberName, typeName);
    }
  }

  private void checkNameCollisionForGetterAndRegular(String getterName, String typeName) {
    if (currentJsMethodNameByGetterNames.containsKey(getterName)
        && currentLocalNameByMemberNames.containsKey(getterName)) {
      logError("'%s' and '%s' can't both be named '%s' in type '%s'.",
          currentLocalNameByMemberNames.get(getterName),
          currentJsMethodNameByGetterNames.get(getterName), getterName, typeName);
    }
  }

  private void checkNameCollisionForSetterAndRegular(String setterName, String typeName) {
    if (currentJsMethodNameBySetterNames.containsKey(setterName)
        && currentLocalNameByMemberNames.containsKey(setterName)) {
      logError("'%s' and '%s' can't both be named '%s' in type '%s'.",
          currentLocalNameByMemberNames.get(setterName),
          currentJsMethodNameBySetterNames.get(setterName), setterName, typeName);
    }
  }

  private void checkJsFunction(JDeclaredType type) {
    if (type.getImplements().size() > 0) {
      logError("JsFunction '%s' cannot extend other interfaces.", type);
    }

    if (type.isJsType()) {
      logError("'%s' cannot be both a JsFunction and a JsType at the same time.", type);
    }

    Set<String> subTypes = jprogram.typeOracle.getSubTypeNames(type.getName());
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

    Set<String> subTypes = jprogram.typeOracle.getSubTypeNames(type.getName());
    if (!subTypes.isEmpty()) {
      logError("Implementation of JsFunction '%s' cannot be extended by other classes:%s", type,
          subTypes);
    }
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
}
