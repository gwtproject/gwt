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
import com.google.gwt.dev.jjs.ast.JSwitchExpression;
import com.google.gwt.dev.jjs.ast.JSwitchStatement;
import com.google.gwt.dev.jjs.ast.JTryStatement;
import com.google.gwt.dev.jjs.ast.JWhileStatement;

import java.util.List;

/**
 * Finds return statements that are effectively at the end of a void method and removes them,
 * or replaces them with something shorter in compiled output.
 * <p>
 * Additionally, loops and switch blocks get special treatment:
 * <ul>
 *   <li>loops: finds "continue" statements (without labels) at the end of a loop (or in a non-loop
 *   block) and removes them as unnecessary, and replaces returns at the end of a loop with a break.
 *   </li>
 *   <li>switch: finds break/return at the end of the block and removes it.</li>
 * </ul>
 * <p>
 * Loops or switches inside other loops are unable to remove return or replace break, but can only
 * remove the final continue/break respectively.
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
        BlockLevel.BLOCK.updateLastStatement(x, b.getBlock(), optimizerCtx);
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

    @Override
    public boolean visit(JSwitchExpression x, Context ctx) {
      // At this time, skip switch expressions entirely - break/continue within loops effectively
      // behave like they are in a different method, we should restart the whole "method" from
      // this node.
      // However, if we see a void type, then we're actually just a regular switch statement
      return x.getType() == JPrimitiveType.VOID;
    }

    /**
     * Enumerates meaningful structures for removing unnecessary control flow.
     */
    private enum BlockLevel {
      /**
       * Neither this block nor any containing block within a loop or a switch, returns can be
       * removed.
       */
      BLOCK,

      /**
       * This block (or a containing block) is part of a loop, so {@code return}s cannot be dropped
       * but converted to breaks, and nested loops cannot be converted to breaks. Anywhere they are
       * found, if the last statement of a loop is a continue, it can be removed.
       */
      LOOP {
        @Override
        protected boolean rewriteReturn(List<JStatement> stmts, int lastIndex) {
          stmts.set(lastIndex,
              new JBreakStatement(stmts.get(lastIndex).getSourceInfo(), null));
          return true;
        }
      },

      /**
       * This block is in a loop which is contained in another loop, so "return"s cannot be omitted
       * or converted to breaks. Continues can still be removed.
       */
      LOOP_IN_LOOP {
        @Override
        public void updateLastStatement(JMethod containingMethod, JBlock block,
              OptimizerContext ctx) {
          // No-op, we can't remove either breaks or returns. Overriding this means we don't need
          // to descend further.
        }
      },

      /**
       * This block is in a switch, so breaks and returns at the end can both be removed.
       */
      SWITCH {
        @Override
        protected boolean rewriteBreak(List<JStatement> stmts, int lastIndex) {
          stmts.remove(lastIndex);
          return true;
        }
      },

      /**
       * This block is in a switch which is contained in a loop - breaks can be omitted, but returns
       * cannot.
       */
      SWITCH_IN_LOOP {
        @Override
        protected boolean rewriteBreak(List<JStatement> stmts, int lastIndex) {
          stmts.remove(lastIndex);
          return true;
        }

        @Override
        protected boolean rewriteReturn(List<JStatement> stmts, int lastIndex) {
          return false;
        }
      };

      /**
       * Test the last statement of a method to see if it is capable of being removed or rewritten.
       * <p>
       * Check a block for unnecessary returns, descending then into the last statement as
       * appropriate.
       *
       * @param containingMethod the method that contains the current block
       * @param block the block to check
       * @param ctx the current context
       */
      public void updateLastStatement(JMethod containingMethod, JBlock block,
            OptimizerContext ctx) {
        List<JStatement> stmts = block.getStatements();
        if (stmts.isEmpty()) {
          return;
        }
        int lastIndex = stmts.size() - 1;
        JStatement lastStmt = stmts.get(lastIndex);
        if (lastStmt instanceof JReturnStatement ret && ret.getExpr() == null) {
          if (rewriteReturn(stmts, lastIndex)) {
            ctx.markModified(containingMethod);

            if (stmts.isEmpty()) {
              return;
            }
            lastStmt = stmts.get(stmts.size() - 1);
          }
        } else if (lastStmt instanceof JBreakStatement breakStmt && breakStmt.getLabel() == null) {
          if (rewriteBreak(stmts, lastIndex)) {
            ctx.markModified(containingMethod);

            if (stmts.isEmpty()) {
              return;
            }
            lastStmt = stmts.get(stmts.size() - 1);
          }
        }
        // Even if we already made a change, continue on, we could have an earlier return in a
        // branch or a loop. None of these will remove the statement in question, so we don't need
        // to iterate.
        if (lastStmt instanceof JBlock b) {
          updateLastStatement(containingMethod, b, ctx);
        } else if (lastStmt instanceof JIfStatement ifStmt) {
          updateLastStatement(containingMethod, ifStmt.getThenStmt(), ctx);
          updateLastStatement(containingMethod, ifStmt.getElseStmt(), ctx);
        } else if (lastStmt instanceof JWhileStatement whileStmt) {
          loop().updateLastStatement(containingMethod, whileStmt.getBody(), ctx);
        } else if (lastStmt instanceof JForStatement forStmt) {
          loop().updateLastStatement(containingMethod, forStmt.getBody(), ctx);
        } else if (lastStmt instanceof JDoStatement doStmt) {
          loop().updateLastStatement(containingMethod, doStmt.getBody(), ctx);
        } else if (lastStmt instanceof JTryStatement tryStmt) {
          updateLastStatement(containingMethod, tryStmt.getTryBlock(), ctx);
          if (tryStmt.getFinallyBlock() != null) {
            updateLastStatement(containingMethod, tryStmt.getFinallyBlock(), ctx);
          }
          for (JTryStatement.CatchClause catchBlock : tryStmt.getCatchClauses()) {
            updateLastStatement(containingMethod, catchBlock.getBlock(), ctx);
          }
        } else if (lastStmt instanceof JSwitchStatement switchStmt) {
          // Since we're in a switch and the switch is effectively the end of the method, we can
          // replace any return with break, and if the last statement is a break/return, outright
          // remove it. We don't look at earlier case/defaults
          switchStmt().updateLastStatement(containingMethod, switchStmt.getBody(), ctx);
        }
      }

      /**
       * Anywhere within a method, a loop that ends with {@code continue;} can have it
       * removed. This can recursively check the final statement of a block.
       *
       * @param containingMethod the method containing the {@code continue;}
       * @param block the block being checked
       * @param ctx the current context
       */
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

        // Don't handle nested loops here, will be handled when visited.
        if (lastStmt instanceof JBlock b) {
          updateContinues(containingMethod, b, ctx);
        } else if (lastStmt instanceof JIfStatement ifStmt) {
          updateContinues(containingMethod, ifStmt.getThenStmt(), ctx);
          updateContinues(containingMethod, ifStmt.getElseStmt(), ctx);
        } else if (lastStmt instanceof JTryStatement tryStmt) {
          updateContinues(containingMethod, tryStmt.getTryBlock(), ctx);
          if (tryStmt.getFinallyBlock() != null) {
            updateContinues(containingMethod, tryStmt.getFinallyBlock(), ctx);
          }
          for (JTryStatement.CatchClause catchBlock : tryStmt.getCatchClauses()) {
            updateContinues(containingMethod, catchBlock.getBlock(), ctx);
          }
        } else if (lastStmt instanceof JSwitchStatement) {
          updateContinues(containingMethod, ((JSwitchStatement) lastStmt).getBody(), ctx);
        }
      }

      /**
       * Helper to rewrite/remove/ignore returns differently in different contexts.
       *
       * @param stmts the statements of the block containing the return
       * @param lastIndex the position of the return in its parent block
       * @return true if a change was made, false otherwise
       */
      protected boolean rewriteReturn(List<JStatement> stmts, int lastIndex) {
        stmts.remove(lastIndex);
        return true;
      }

      /**
       * Helper to rewrite/remove/ignore breaks differently in different contexts.
       *
       * @param stmts the statements of the block containing the break
       * @param lastIndex the position of the return in its parent block
       * @return true if a change was made, false otherwise
       */
      protected boolean rewriteBreak(List<JStatement> stmts, int lastIndex) {
        // default implementation does nothing, breaks aren't found outside of specific parents
        assert this == LOOP || this == LOOP_IN_LOOP : "Shouldn't encounter breaks here";
        return false;
      }

      // Move into a loop from the current block
      private BlockLevel loop() {
        return this == LOOP || this == SWITCH_IN_LOOP ? LOOP_IN_LOOP : LOOP;
      }

      // Move into a switch from the current block
      private BlockLevel switchStmt() {
        return this == LOOP || this == SWITCH_IN_LOOP ? SWITCH_IN_LOOP : SWITCH;
      }
    }
  }
}
