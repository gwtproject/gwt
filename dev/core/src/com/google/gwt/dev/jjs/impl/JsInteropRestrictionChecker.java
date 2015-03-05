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
import com.google.gwt.thirdparty.guava.common.collect.Sets;

import java.util.Set;

/**
 * Checks and throws errors for invalid JsInterop constructs.
 */
// TODO: handle custom JsType field/method names when that feature exists.
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

  private final Set<String> currentJsTypeGetterNames = Sets.newHashSet();
  private final Set<String> currentJsTypeMemberNames = Sets.newHashSet();
  private final Set<String> currentJsTypeSetterNames = Sets.newHashSet();
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
    currentJsTypeMemberNames.clear();
    currentJsTypeGetterNames.clear();
    currentJsTypeSetterNames.clear();
  }

  @Override
  public boolean visit(JDeclaredType x, Context ctx) {
    assert currentType == null;
    assert currentJsTypeMemberNames.isEmpty();
    assert currentJsTypeGetterNames.isEmpty();
    assert currentJsTypeSetterNames.isEmpty();
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
      if (x.isSynthetic()) {
        // A name slot taken up by a synthetic method, such as a bridge method for a generic method,
        // is not the fault of the user and so should not be reported as an error. JS generation
        // should take responsibility for ensuring that only the correct method version (in this
        // particular set of colliding method names) is exported.
      } else {
        checkJsTypeMethod(x);
      }
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

  private void checkGetterAndRegularNameCollision(String getterName, String typeName) {
    if (currentJsTypeGetterNames.contains(getterName)
        && currentJsTypeMemberNames.contains(getterName)) {
      logError(
          "A JsType method and JsProperty property name can't both be named '%s' in type '%s'.",
          getterName, typeName);
    }
  }

  private void checkJsTypeFieldName(JField field, String memberName) {
    boolean success = currentJsTypeMemberNames.add(memberName);
    if (!success) {
      logError("'%s' can't be exported because the member name '%s' is already taken.",
          field.getQualifiedName(), memberName);
    }
  }

  private void checkJsTypeMethod(JMethod method) {
    String jsMemberName = method.getImmediateOrTransitiveJsMemberName();
    String typeName = method.getEnclosingType().getName();

    if (jsMemberName == null) {
      logError("'%s' can't be exported because the method overloads multiple methods with "
          + "different names.", method.getQualifiedName());
    } else if (method.isOrOverridesJsProperty()) {
      // If it's a JsProperty.
      JsPropertyType jsPropertyType = method.getImmediateOrTransitiveJsPropertyType();
      if (jsPropertyType == JsPropertyType.GETTER) {
        // If it's a getter.
        if (!currentJsTypeGetterNames.add(jsMemberName)) {
          // Don't allow multiple getters for the same property name.
          logError("There can't be more than one getter for JS property '%s' in type '%s'.",
              jsMemberName, typeName);
        }
        checkGetterAndRegularNameCollision(jsMemberName, typeName);
      } else if (jsPropertyType == JsPropertyType.SETTER) {
        // If it's a setter.
        if (!currentJsTypeSetterNames.add(jsMemberName)) {
          // Don't allow multiple setters for the same property name.
          logError("There can't be more than one setter for JS property '%s' in type '%s'.",
              jsMemberName, typeName);
        }
        checkSetterAndRegularNameCollision(jsMemberName, typeName);
      }
    } else {
      // If it's just an regular JsType method.
      if (!currentJsTypeMemberNames.add(jsMemberName)) {
        logError("'%s' can't be exported because the member name '%s' is already taken.",
            method.getQualifiedName(), jsMemberName);
      }
      checkGetterAndRegularNameCollision(jsMemberName, typeName);
      checkSetterAndRegularNameCollision(jsMemberName, typeName);
    }
  }

  private void checkSetterAndRegularNameCollision(String setterName, String typeName) {
    if (currentJsTypeSetterNames.contains(setterName)
        && currentJsTypeMemberNames.contains(setterName)) {
      logError(
          "A JsType method and JsProperty property name can't both be named '%s' in type '%s'.",
          setterName, typeName);
    }
  }

  private void logError(String format, Object... args) {
    logger.log(TreeLogger.ERROR, String.format(format, args));
    hasErrors = true;
  }
}
