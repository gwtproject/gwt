/*
 * Copyright 2014 Google Inc.
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

import com.google.gwt.dev.MinimalRebuildCache;
import com.google.gwt.dev.jjs.ast.Context;
import com.google.gwt.dev.jjs.ast.JClassLiteral;
import com.google.gwt.dev.jjs.ast.JDeclaredType;
import com.google.gwt.dev.jjs.ast.JField;
import com.google.gwt.dev.jjs.ast.JFieldRef;
import com.google.gwt.dev.jjs.ast.JMethod;
import com.google.gwt.dev.jjs.ast.JMethodCall;
import com.google.gwt.dev.jjs.ast.JNewInstance;
import com.google.gwt.dev.jjs.ast.JProgram;
import com.google.gwt.dev.jjs.ast.JType;
import com.google.gwt.dev.jjs.ast.JVisitor;
import com.google.gwt.dev.jjs.ast.js.JsniFieldRef;
import com.google.gwt.dev.jjs.ast.js.JsniMethodRef;

/**
 * Records control flow information.
 * <p>
 * Collects caller->callee, instantiating method->instantiated type, overridden method->overriding
 * method, exported methods and other control flow information in MinimalRebuildCache indexes to
 * support control flow based link time pruning.
 */
public class ControlFlowRecorder extends JVisitor {

  public static void exec(JProgram program, MinimalRebuildCache minimalRebuildCache,
      boolean onlyUpdate) {
    new ControlFlowRecorder(minimalRebuildCache, onlyUpdate, program).execImpl();
  }

  private static String computeName(JMethod method) {
    return method.getJsniSignature(true, false);
  }

  private String currentMethodName;
  private final MinimalRebuildCache minimalRebuildCache;
  private final boolean onlyUpdate;
  private final JProgram program;

  public ControlFlowRecorder(MinimalRebuildCache minimalRebuildCache, boolean onlyUpdate,
      JProgram program) {
    this.minimalRebuildCache = minimalRebuildCache;
    this.onlyUpdate = onlyUpdate;
    this.program = program;
  }

  @Override
  public void endVisit(JClassLiteral x, Context ctx) {
    JType type = x.getRefType();
    if (type instanceof JDeclaredType) {
      String typeName = type.getName();
      minimalRebuildCache.recordStaticReferenceInMethod(typeName, currentMethodName);
    }
    super.endVisit(x, ctx);
  }

  @Override
  public void endVisit(JFieldRef x, Context ctx) {
    processJFieldRef(x);
    super.endVisit(x, ctx);
  }

  @Override
  public void endVisit(JsniFieldRef x, Context ctx) {
    processJFieldRef(x);
    super.endVisit(x, ctx);
  }

  @Override
  public void endVisit(JsniMethodRef x, Context ctx) {
    processMethodCall(x);
    super.endVisit(x, ctx);
  }

  @Override
  public boolean visit(JDeclaredType x, Context ctx) {
    if (!onlyUpdate) {
      minimalRebuildCache.removeControlFlowIndexesFor(x.getName());
    }

    return super.visit(x, ctx);
  }

  @Override
  public boolean visit(JField x, Context ctx) {
    String typeName = x.getEnclosingType().getName();

    if (program.typeOracle.isExportedField(x) && x.isStatic()) {
      minimalRebuildCache.recordExportedStaticReferenceInType(typeName);
    }

    return super.visit(x, ctx);
  }

  @Override
  public boolean visit(JMethod x, Context ctx) {
    String typeName = x.getEnclosingType().getName();
    currentMethodName = computeName(x);

    minimalRebuildCache.recordTypeContainsMethod(typeName, currentMethodName);

    for (JMethod overriddenMethod : program.typeOracle.getAllOverriddenMethods(x)) {
      String overriddenMethodName = computeName(overriddenMethod);
      minimalRebuildCache.recordMethodOverridesMethod(currentMethodName, overriddenMethodName);
    }

    if (program.typeOracle.isExportedMethod(x) || program.typeOracle.isJsTypeMethod(x)) {
      if (x.isStatic() || x.isConstructor()) {
        minimalRebuildCache.recordExportedStaticReferenceInType(typeName);
      }
      minimalRebuildCache.recordExportedMethodInType(currentMethodName, typeName);
    }

    return super.visit(x, ctx);
  }

  @Override
  public boolean visit(JMethodCall x, Context ctx) {
    processMethodCall(x);
    return super.visit(x, ctx);
  }

  @Override
  public boolean visit(JNewInstance x, Context ctx) {
    String typeName = x.getTarget().getEnclosingType().getName();
    minimalRebuildCache.recordMethodInstantiatesType(currentMethodName, typeName);
    return super.visit(x, ctx);
  }

  private void execImpl() {
    accept(program);
  }

  private void processJFieldRef(JFieldRef x) {
    if (x.getTarget() instanceof JField) {
      JField field = (JField) x.getTarget();
      if (field.isStatic()) {
        String typeName = field.getEnclosingType().getName();
        minimalRebuildCache.recordStaticReferenceInMethod(typeName, currentMethodName);
      }
    }
  }

  private void processMethodCall(JMethodCall x) {
    JMethod targetMethod = x.getTarget();
    String calleeMethodName = computeName(targetMethod);
    minimalRebuildCache.recordMethodCallsMethod(currentMethodName, calleeMethodName);

    if (targetMethod.isStatic()) {
      String typeName = targetMethod.getEnclosingType().getName();
      minimalRebuildCache.recordStaticReferenceInMethod(typeName, currentMethodName);
    }

    // Instantiations in JSNI don't use JNewInstance and must be recognized by method calls on
    // Constructor functions.
    if (targetMethod.isConstructor()) {
      String typeName = targetMethod.getEnclosingType().getName();
      minimalRebuildCache.recordMethodInstantiatesType(currentMethodName, typeName);
    }
  }
}
