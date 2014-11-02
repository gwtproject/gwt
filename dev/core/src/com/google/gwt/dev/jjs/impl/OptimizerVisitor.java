/*
 * Copyright 2007 Google Inc.
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

import com.google.gwt.dev.jjs.ast.Context;
import com.google.gwt.dev.jjs.ast.JField;
import com.google.gwt.dev.jjs.ast.JMethod;
import com.google.gwt.dev.jjs.ast.JModVisitor;
import com.google.gwt.dev.jjs.ast.JNode;
import com.google.gwt.thirdparty.guava.common.collect.HashMultimap;
import com.google.gwt.thirdparty.guava.common.collect.Multimap;

/**
 *  A visitor for optimizing an AST.
 */
public abstract class OptimizerVisitor extends JModVisitor {

  protected final int idx; // used to identify the corresponding optimizer.
  protected JMethod currentMethod = null;
  protected JField currentField = null;
  protected final OptimizerDependencies optDep;

  // added {callee->callers} pairs by the visitor.
  protected Multimap<JMethod, JMethod> newCallSite = HashMultimap.create();

  public OptimizerVisitor(int idx, OptimizerDependencies optDep) {
    this.idx = idx;
    this.optDep = optDep;
  }

  @Override
  public void endVisit(JMethod x, Context ctx) {
    currentMethod = null;
  }

  @Override
  public void endVisit(JField x, Context ctx) {
    currentField = null;
  }

  @Override
  public boolean visit(JMethod x, Context ctx) {
    currentMethod = x;
    return super.visit(x, ctx);
  }

  @Override
  public boolean visit(JField x, Context ctx) {
    currentField = x;
    return super.visit(x, ctx);
  }

  @Override
  protected void recordModifications() {
    if (currentMethod != null) {
      optDep.addModifiedMethod(currentMethod, idx);
    }
    if (currentField != null) {
      optDep.addModifiedField(currentField, idx);
    }
  }

  @Override
  protected void updateCallGraph(JNode node) {
    Multimap<JMethod, JMethod> callees =
        CallerMethodsOfCalledMethod.getCallees(currentMethod, node);
    newCallSite.putAll(callees);
  }

  @Override
  protected void recordRemoves(JNode x) {
    if (x instanceof JMethod) {
      optDep.removeModifiedMethod((JMethod) x);
    } else if (x instanceof JField) {
      optDep.removeModifiedField((JField) x);
    }
  }
}
