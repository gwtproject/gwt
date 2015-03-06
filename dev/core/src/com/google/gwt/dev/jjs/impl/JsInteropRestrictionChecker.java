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
import com.google.gwt.dev.jjs.ast.JDeclaredType;
import com.google.gwt.dev.jjs.ast.JField;
import com.google.gwt.dev.jjs.ast.JMember;
import com.google.gwt.dev.jjs.ast.JMethod;
import com.google.gwt.dev.jjs.ast.JMethod.JsPropertyType;
import com.google.gwt.dev.jjs.ast.JProgram;
import com.google.gwt.dev.jjs.ast.JVisitor;
import com.google.gwt.thirdparty.guava.common.collect.Maps;

import java.util.Map;

/**
 * Checks and throws errors for invalid JsInterop constructs.
 */
// TODO: handle custom JsType field/method names when that feature exists.
// TODO: move JsInterop checks from JSORestrictionsChecker to here.
// TODO: check collisions with members in parent JsType classes.
// TODO: check setter and getter for the same property has compatible types.
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

  private Map<String, String> currentJsTypeMethodNameByGetterNames;
  private Map<String, String> currentJsTypeMethodNameByMemberNames;
  private Map<String, String> currentJsTypeMethodNameBySetterNames;
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
    currentJsTypeMethodNameByMemberNames = Maps.newHashMap();
    currentJsTypeMethodNameByGetterNames = Maps.newHashMap();
    currentJsTypeMethodNameBySetterNames = Maps.newHashMap();
  }

  @Override
  public boolean visit(JDeclaredType x, Context ctx) {
    assert currentType == null;
    minimalRebuildCache.removeJsInteropNames(x.getName());
    currentType = x;

    return true;
  }

  @Override
  public boolean visit(JField x, Context ctx) {
    if (jprogram.typeOracle.isExportedField(x)) {
      checkExportName(x);
    } else if (jprogram.typeOracle.isJsTypeField(x)) {
      checkJsTypeFieldName(x, x.getJsMemberName());
    }

    return false;
  }

  @Override
  public boolean visit(JMethod x, Context ctx) {
    if (jprogram.typeOracle.isExportedMethod(x)) {
      checkExportName(x);
    } else if (jprogram.typeOracle.isJsTypeMethod(x)) {
      checkJsTypeMethod(x);
    }

    if (jprogram.typeOracle.isJsPropertyMethod(x)
        && !jprogram.typeOracle.isOrExtendsJsType(x.getEnclosingType(), false)) {
      logError("Method '%s' can't be a JsProperty since enclosing type '%s' is not a JsType.",
          x.getName(), x.getEnclosingType().getName());
    }

    return false;
  }

  private void checkExportName(JMember x) {
    boolean success = minimalRebuildCache.addExportedGlobalName(x.getQualifiedExportName(),
        currentType.getName());
    if (!success) {
      logError("'%s' can't be exported because the global name '%s' is already taken.",
          x.getQualifiedName(), x.getQualifiedExportName());
    }
  }

  private void checkJsTypeFieldName(JField field, String memberName) {
    boolean success =
        currentJsTypeMethodNameByMemberNames.put(memberName, field.getQualifiedName()) == null;
    if (!success) {
      logError("'%s' can't be exported because the member name '%s' is already taken.",
          field.getQualifiedName(), memberName);
    }
  }

  private void checkJsTypeMethod(JMethod method) {
    if (method.isSynthetic()) {
      // A name slot taken up by a synthetic method, such as a bridge method for a generic method,
      // is not the fault of the user and so should not be reported as an error. JS generation
      // should take responsibility for ensuring that only the correct method version (in this
      // particular set of colliding method names) is exported.
      return;
    }

    String jsMemberName = method.getImmediateOrTransitiveJsMemberName();
    String qualifiedMethodName = method.getQualifiedName();
    String typeName = method.getEnclosingType().getName();

    if (jsMemberName == null) {
      logError("'%s' can't be exported because the method overloads multiple methods with "
          + "different names.", qualifiedMethodName);
    } else if (method.isOrOverridesJsProperty()) {
      // If it's a JsProperty.
      JsPropertyType jsPropertyType = method.getImmediateOrTransitiveJsPropertyType();
      if (jsPropertyType == JsPropertyType.GET) {
        // If it's a getter.
        if (currentJsTypeMethodNameByGetterNames.put(jsMemberName, qualifiedMethodName) != null) {
          // Don't allow multiple getters for the same property name.
          logError("There can't be more than one getter for JS property '%s' in type '%s'.",
              jsMemberName, typeName);
        }
        checkNameCollisionForGetterAndRegular(jsMemberName, typeName);
      } else if (jsPropertyType == JsPropertyType.SET) {
        // If it's a setter.
        if (currentJsTypeMethodNameBySetterNames.put(jsMemberName, qualifiedMethodName) != null) {
          // Don't allow multiple setters for the same property name.
          logError("There can't be more than one setter for JS property '%s' in type '%s'.",
              jsMemberName, typeName);
        }
        checkNameCollisionForSetterAndRegular(jsMemberName, typeName);
      }
    } else {
      // If it's just an regular JsType method.
      if (currentJsTypeMethodNameByMemberNames.put(jsMemberName, qualifiedMethodName) != null) {
        logError("'%s' can't be exported because the member name '%s' is already taken.",
            qualifiedMethodName, jsMemberName);
      }
      checkNameCollisionForGetterAndRegular(jsMemberName, typeName);
      checkNameCollisionForSetterAndRegular(jsMemberName, typeName);
    }
  }

  private void checkNameCollisionForGetterAndRegular(String getterName, String typeName) {
    if (currentJsTypeMethodNameByGetterNames.containsKey(getterName)
        && currentJsTypeMethodNameByMemberNames.containsKey(getterName)) {
      logError(
          "A JsType method and JsProperty property name can't both be named '%s' in type '%s'.",
          getterName, typeName);
      logError("The JsType member '%s' and JsProperty property '%s' name can't both be named "
          + "'%s' in type '%s'.", currentJsTypeMethodNameByMemberNames.get(getterName),
          currentJsTypeMethodNameByGetterNames.get(getterName), getterName, typeName);
    }
  }

  private void checkNameCollisionForSetterAndRegular(String setterName, String typeName) {
    if (currentJsTypeMethodNameBySetterNames.containsKey(setterName)
        && currentJsTypeMethodNameByMemberNames.containsKey(setterName)) {
      logError("The JsType member '%s' and JsProperty property '%s' name can't both be named "
          + "'%s' in type '%s'.", currentJsTypeMethodNameByMemberNames.get(setterName),
          currentJsTypeMethodNameBySetterNames.get(setterName), setterName, typeName);
    }
  }

  private void logError(String format, Object... args) {
    logger.log(TreeLogger.ERROR, String.format(format, args));
    hasErrors = true;
  }
}
