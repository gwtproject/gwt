/*
 * Copyright 2026 GWT Project Authors
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
package com.google.gwt.dev.js;

import com.google.gwt.dev.jjs.impl.OptimizerStats;
import com.google.gwt.dev.js.ast.JsBinaryOperation;
import com.google.gwt.dev.js.ast.JsBinaryOperator;
import com.google.gwt.dev.js.ast.JsContext;
import com.google.gwt.dev.js.ast.JsFunction;
import com.google.gwt.dev.js.ast.JsModVisitor;
import com.google.gwt.dev.js.ast.JsName;
import com.google.gwt.dev.js.ast.JsNameRef;
import com.google.gwt.dev.js.ast.JsProgram;
import com.google.gwt.dev.js.ast.JsVars;
import com.google.gwt.dev.js.ast.JsVars.JsVar;
import com.google.gwt.dev.js.ast.JsVisitor;
import com.google.gwt.dev.util.collect.IdentityHashSet;

import java.util.Set;
import java.util.Stack;

/**
 * Removes variable declarations and write-only assignments for local variables that are never read
 * within their enclosing function (including nested functions).
 * <p>
 * Future work here could regroup all var decls at the top of the function, and collect any
 * assignment statements into init expressions (reordering as needed to preserve side effects).
 */
public class JsUnusedVarPruner {
  public static final String NAME = JsUnusedVarPruner.class.getSimpleName();

  public static int exec(JsProgram program) {
    return new JsUnusedVarPruner(program).execImpl();
  }

  private final JsProgram program;

  private JsUnusedVarPruner(JsProgram program) {
    this.program = program;
  }

  private int execImpl() {
    try (OptimizerStats stats = OptimizerStats.optimization(NAME)) {
      FunctionVisitor functionVisitor = new FunctionVisitor();
      functionVisitor.accept(program);

      stats.recordModified(functionVisitor.totalMods);

      return stats.getNumMods();
    }
  }

  /**
   * Finds functions and collects their vars, revisiting to find reads and prune unread vars.
   * Due to var hoisting and closures, we scan each function twice, once in this visitor to collect
   * vars in the function itself, then another visitor to find reads of those vars.
   * <p>
   * A third pass then removes any writes from all vars that are not read, and attempts to remove
   * the var declaration itself.
   */
  private static class FunctionVisitor extends JsVisitor {
    private int totalMods = 0;
    private final Stack<Set<JsName>> namesStack = new Stack<>();

    @Override
    public boolean visit(JsFunction x, JsContext ctx) {
      namesStack.push(new IdentityHashSet<>());
      return super.visit(x, ctx);
    }

    @Override
    public void endVisit(JsVar x, JsContext ctx) {
      if (namesStack.isEmpty()) {
        return;
      }
      namesStack.peek().add(x.getName());
    }

    @Override
    public void endVisit(JsFunction x, JsContext ctx) {
      Set<JsName> declaredNames = this.namesStack.pop();

      ReadNameCollector collector = new ReadNameCollector();
      collector.accept(x.getBody());

      declaredNames.removeAll(collector.getReadNames());
      if (declaredNames.isEmpty()) {
        return;
      }

      PruneVisitor pruner = new PruneVisitor(declaredNames);
      pruner.accept(x.getBody());
      totalMods += pruner.getNumMods();
    }
  }

  /**
   * Visits a function (including nested functions) and discovers all read vars.
   * <p>
   * At this time, operations like ++ and += that both write and read are considered here to be
   * reads, even if the value is never used.
   */
  private static class ReadNameCollector extends JsVisitor {
    private final Set<JsName> readNames = new IdentityHashSet<>();

    @Override
    public boolean visit(JsBinaryOperation x, JsContext ctx) {
      if (x.getOperator() == JsBinaryOperator.ASG && x.getArg1() instanceof JsNameRef lhs) {
        if (lhs.getQualifier() == null) {
          // Only visit the RHS, the LHS is a write to a local (or param)
          accept(x.getArg2());
          return false;
        }
      }
      return true;
    }

    @Override
    public void endVisit(JsNameRef x, JsContext ctx) {
      assert x.getName() != null && x.isResolved();
      if (x.getQualifier() == null) {
        readNames.add(x.getName());
      }
    }

    Set<JsName> getReadNames() {
      return readNames;
    }
  }

  /**
   * Removes var declarations for unread local variables, and replaces assignment with just the RHS
   * expression. Vars that have init expressions with side effects must be retained - in the future
   * we could fold those into the following assignment with a comma expression, or a subsequent
   * statement.
   */
  private static class PruneVisitor extends JsModVisitor {
    private final Set<JsName> unreadNames;

    PruneVisitor(Set<JsName> unreadNames) {
      this.unreadNames = unreadNames;
    }

    @Override
    public void endVisit(JsVar x, JsContext ctx) {
      if (unreadNames.contains(x.getName())) {
        if (x.getInitExpr() != null && x.getInitExpr().hasSideEffects()) {
          // This var must be kept, its assignment has a side effect
          return;
        }
        ctx.removeMe();
      }
    }

    @Override
    public void endVisit(JsVars x, JsContext ctx) {
      if (x.isEmpty()) {
        ctx.removeMe();
      }
    }

    @Override
    public void endVisit(JsBinaryOperation x, JsContext ctx) {
      if (x.getOperator() == JsBinaryOperator.ASG
          && x.getArg1() instanceof JsNameRef lhs
          && unreadNames.contains(lhs.getName())) {
        assert lhs.isResolved();
        ctx.replaceMe(x.getArg2());
      }
    }
  }
}
