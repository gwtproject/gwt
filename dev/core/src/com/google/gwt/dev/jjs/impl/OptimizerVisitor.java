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

/**
 * A visitor for optimizing an AST.
 */
public abstract class OptimizerVisitor extends JModVisitor {

  protected JMethod currentMethod = null;
  protected JField currentField = null;
  protected final OptimizerDependencies optDep;

  private boolean methodModified = false;
  private boolean fieldModified = false;

  public OptimizerVisitor(OptimizerDependencies optDep) {
    this.optDep = optDep;
  }

  public boolean enterMethod(JMethod x, Context ctx) {
    return super.visit(x, ctx);
  }

  public void exitMethod(JMethod x, Context ctx) {
    super.endVisit(x, ctx);
  }

  public boolean enterField(JField f, Context ctx) {
    return super.visit(f, ctx);
  }

  public void exitField(JField f, Context ctx) {
    super.endVisit(f, ctx);
  }

  @Override
  public final void endVisit(JMethod x, Context ctx) {
    exitMethod(x, ctx);
    if (methodModified) {
      optDep.addModifiedMethod(x);
      optDep.getCallGraph().updateCallGraphOfMethod(x);
    }
    currentMethod = null;
  }

  @Override
  public final void endVisit(JField x, Context ctx) {
    exitField(x, ctx);
    if (fieldModified) {
      optDep.addModifiedField(x);
    }
    currentField = null;
  }

  @Override
  public final boolean visit(JMethod x, Context ctx) {
    currentMethod = x;
    methodModified = false;
    return enterMethod(x, ctx);
  }

  @Override
  public final boolean visit(JField x, Context ctx) {
    currentField = x;
    fieldModified = false;
    return enterField(x, ctx);
  }

  @Override
  protected void recordModifications() {
    if (currentMethod != null) {
      methodModified = true;
    }
    if (currentField != null) {
      fieldModified = true;
    }
  }

  @Override
  protected void recordRemoves(JNode x) {
    if (x instanceof JMethod) {
      optDep.removeModifiedMethod((JMethod) x);
      optDep.getCallGraph().removeMethod((JMethod) x);
    } else if (x instanceof JField) {
      optDep.removeModifiedField((JField) x);
    }
  }
}
