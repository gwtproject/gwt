package com.google.gwt.dev.jjs.impl;

import com.google.gwt.dev.jjs.ast.Context;
import com.google.gwt.dev.jjs.ast.JBlock;
import com.google.gwt.dev.jjs.ast.JBreakStatement;
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
 */
public class RemoveUnnecessaryControlFlow {
  private static String NAME = RemoveUnnecessaryControlFlow.class.getSimpleName();

  public static int exec(JProgram program, OptimizerContext optimizerCtx) {
    try (OptimizerStats stats = OptimizerStats.optimization(NAME)) {

      new RewriteUnnecessaryReturnsVisitor(optimizerCtx).accept(program);

      optimizerCtx.incOptimizationStep();
      return stats.getNumMods();
    }

  }

  private static class RewriteUnnecessaryReturnsVisitor extends JModVisitor {
    private final OptimizerContext optimizerCtx;

    public RewriteUnnecessaryReturnsVisitor(OptimizerContext optimizerCtx) {
      this.optimizerCtx = optimizerCtx;
    }

    @Override
    public boolean visit(JMethod x, Context ctx) {
      if (x.getType() == JPrimitiveType.VOID && x.getBody() instanceof JMethodBody b) {
        BlockLevel.BLOCK.update(x, b.getBlock(), optimizerCtx);
      }
      return false;
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
        protected void rewrite(List<JStatement> stmts, int lastIndex) {
          stmts.set(lastIndex, new JBreakStatement(stmts.get(lastIndex).getSourceInfo(), null));
        }
      },
      /**
       * This block is in a loop which is contained in another loop, so "return"s cannot be omitted
       * or converted to breaks.
       */
      LOOP_IN_LOOP {
        @Override
        public void update(JMethod containingMethod, JBlock block, OptimizerContext ctx) {
          // no-op, we can't do anything
        }
      };

      public void update(JMethod containingMethod, JBlock block, OptimizerContext ctx) {
        List<JStatement> stmts = block.getStatements();
        if (stmts.isEmpty()) {
          return;
        }
        int lastIndex = stmts.size() - 1;
        if (stmts.get(lastIndex) instanceof JReturnStatement ret && ret.getExpr() == null) {
          rewrite(stmts, lastIndex);
          ctx.markModified(containingMethod);

          if (stmts.isEmpty()) {
            return;
          }
          lastIndex = stmts.size() - 1;
        }
        // Even if we already made a change, continue on, we could have an earlier return in a branch
        // or a loop. None of these will remove the statement in question, so we don't need to iterate.

        if (stmts.get(lastIndex) instanceof JBlock b) {
          update(containingMethod, b, ctx);
        } else if (stmts.get(lastIndex) instanceof JIfStatement ifStmt) {
          update(containingMethod, ifStmt.getThenStmt(), ctx);
          update(containingMethod, ifStmt.getElseStmt(), ctx);
        } else if (stmts.get(lastIndex) instanceof JWhileStatement whileStmt) {
          loop().update(containingMethod, whileStmt.getBody(), ctx);
        } else if (stmts.get(lastIndex) instanceof JForStatement forStmt) {
          loop().update(containingMethod, forStmt.getBody(), ctx);
        } else if (stmts.get(lastIndex) instanceof JDoStatement doStmt) {
          loop().update(containingMethod, doStmt.getBody(), ctx);
        } // TODO handle switches, try/catch/finally

      }

      protected void rewrite(List<JStatement> stmts, int lastIndex) {
        stmts.remove(lastIndex);
      }

      private BlockLevel loop() {
        return this == BLOCK ? LOOP : LOOP_IN_LOOP;
      }
    }
  }
}
