/*
 * Copyright 2026 GWT Project Authors
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
import com.google.gwt.dev.jjs.ast.JBlock;
import com.google.gwt.dev.jjs.ast.JBreakStatement;
import com.google.gwt.dev.jjs.ast.JContinueStatement;
import com.google.gwt.dev.jjs.ast.JDoStatement;
import com.google.gwt.dev.jjs.ast.JForStatement;
import com.google.gwt.dev.jjs.ast.JIfStatement;
import com.google.gwt.dev.jjs.ast.JMethod;
import com.google.gwt.dev.jjs.ast.JMethodBody;
import com.google.gwt.dev.jjs.ast.JModVisitor;
import com.google.gwt.dev.jjs.ast.JPrimitiveType;
import com.google.gwt.dev.jjs.ast.JProgram;
import com.google.gwt.dev.jjs.ast.JReturnStatement;
import com.google.gwt.dev.jjs.ast.JStatement;
import com.google.gwt.dev.jjs.ast.JWhileStatement;

import java.util.List;

/**
 * Finds return statements that are effectively at the end of a void method and removes them,
 * or replaces them with something shorter in compiled output (e.g. break instead of return in a
 * loop).
 * <p>
 * Additionally, finds "continue" statements (without labels) at the end of a loop (or in a non-loop
 * block) and removes them as unnecessary.
 * <p>
 * Presently may require multiple passes to converge, but since it is unlikely to remove many such
 * statements, this will be addressed by just allowing another loop to find the changes. Additional
 * changes from other optimizations (mostly DCE) can expose other opportunities for this pass.
 */
public class RemoveUnnecessaryControlFlow {
  private static final String NAME = RemoveUnnecessaryControlFlow.class.getSimpleName();

  public static int exec(JProgram program, OptimizerContext optimizerCtx) {
    try (OptimizerStats stats = OptimizerStats.optimization(NAME)) {
      new RewriteUnnecessaryReturnsVisitor(optimizerCtx).accept(program);

      optimizerCtx.incOptimizationStep();
      return stats.getNumMods();
    }
  }

  private static class RewriteUnnecessaryReturnsVisitor extends JModVisitor {
    private final OptimizerContext optimizerCtx;
    private JMethod currentMethod;

    private RewriteUnnecessaryReturnsVisitor(OptimizerContext optimizerCtx) {
      this.optimizerCtx = optimizerCtx;
    }

    @Override
    public boolean visit(JMethod x, Context ctx) {
      currentMethod = x;
      if (x.getType() == JPrimitiveType.VOID && x.getBody() instanceof JMethodBody b) {
        BlockLevel.BLOCK.updateReturns(x, b.getBlock(), optimizerCtx);
      }
      return true;
    }

    @Override
    public void endVisit(JMethod x, Context ctx) {
      currentMethod = null;
    }

    @Override
    public void endVisit(JForStatement x, Context ctx) {
      assert currentMethod != null;
      BlockLevel.LOOP.updateContinues(currentMethod, x.getBody(), optimizerCtx);
    }

    @Override
    public void endVisit(JWhileStatement x, Context ctx) {
      BlockLevel.LOOP.updateContinues(currentMethod, x.getBody(), optimizerCtx);
    }

    @Override
    public void endVisit(JDoStatement x, Context ctx) {
      BlockLevel.LOOP.updateContinues(currentMethod, x.getBody(), optimizerCtx);
    }

    private enum BlockLevel {
      /**
       * Neither this block nor any containing block are part of a loop.
       */
      BLOCK,
      /**
       * This block (or a containing block) is part of a loop, so "return"s cannot be dropped but
       * converted to breaks, and nested loops cannot be converted to breaks.
       */
      LOOP {
        @Override
        protected void rewriteReturns(List<JStatement> stmts, int lastIndex) {
          stmts.set(lastIndex, new JBreakStatement(stmts.get(lastIndex).getSourceInfo(), null));
        }
      },
      /**
       * This block is in a loop which is contained in another loop, so "return"s cannot be omitted
       * or converted to breaks.
       */
      LOOP_IN_LOOP {
        @Override
        public void updateReturns(JMethod containingMethod, JBlock block, OptimizerContext ctx) {
          // no-op, we can't do anything
        }
      };

      public void updateReturns(JMethod containingMethod, JBlock block, OptimizerContext ctx) {
        List<JStatement> stmts = block.getStatements();
        if (stmts.isEmpty()) {
          return;
        }
        int lastIndex = stmts.size() - 1;
        JStatement lastStmt = stmts.get(lastIndex);
        if (lastStmt instanceof JReturnStatement ret && ret.getExpr() == null) {
          rewriteReturns(stmts, lastIndex);
          ctx.markModified(containingMethod);

          if (stmts.isEmpty()) {
            return;
          }
          lastStmt = stmts.get(stmts.size() - 1);
        }
        // Even if we already made a change, continue on, we could have an earlier return in a
        // branch or a loop. None of these will remove the statement in question, so we don't need
        // to iterate.
        if (lastStmt instanceof JBlock b) {
          updateReturns(containingMethod, b, ctx);
        } else if (lastStmt instanceof JIfStatement ifStmt) {
          updateReturns(containingMethod, ifStmt.getThenStmt(), ctx);
          updateReturns(containingMethod, ifStmt.getElseStmt(), ctx);
        } else if (lastStmt instanceof JWhileStatement whileStmt) {
          loop().updateReturns(containingMethod, whileStmt.getBody(), ctx);
        } else if (lastStmt instanceof JForStatement forStmt) {
          loop().updateReturns(containingMethod, forStmt.getBody(), ctx);
        } else if (lastStmt instanceof JDoStatement doStmt) {
          loop().updateReturns(containingMethod, doStmt.getBody(), ctx);
        }

        // TODO handle switches, try/catch/finally
      }

      public void updateContinues(JMethod containingMethod, JBlock block, OptimizerContext ctx) {
        if (block.isEmpty()) {
          return;
        }
        List<JStatement> stmts = block.getStatements();
        int lastIndex = stmts.size() - 1;
        JStatement lastStmt = stmts.get(lastIndex);
        if (stmts.get(lastIndex) instanceof JContinueStatement cont && cont.getLabel() == null) {
          stmts.remove(lastIndex);
          ctx.markModified(containingMethod);

          if (stmts.isEmpty()) {
            return;
          }
          lastStmt = stmts.get(stmts.size() - 1);
        }

        if (lastStmt instanceof JBlock b) {
          updateContinues(containingMethod, b, ctx);
        } else if (lastStmt instanceof JIfStatement ifStmt) {
          updateContinues(containingMethod, ifStmt.getThenStmt(), ctx);
          updateContinues(containingMethod, ifStmt.getElseStmt(), ctx);
        } // Don't handle nested loops here, will be handled when visited.

        // TODO handle switches, try/catch/finally
      }

      protected void rewriteReturns(List<JStatement> stmts, int lastIndex) {
        stmts.remove(lastIndex);
      }

      private BlockLevel loop() {
        return this == BLOCK ? LOOP : LOOP_IN_LOOP;
      }
    }
  }
}
