package com.google.gwt.dev.jjs.impl;

import com.google.gwt.dev.jjs.ast.*;
import com.google.gwt.thirdparty.guava.common.collect.Lists;

/**
 * Breaks up Java 14 case statements so that each has exactly one value, allowing them to be
 * rewritten as js case statements, which are only permitted one value each.
 */
public class SplitCaseStatementValues {
  private static class CaseSplitter extends JModVisitor {
    @Override
    public void endVisit(JCaseStatement x, Context ctx) {
      if (x.getExprs().size() > 1) {
        // If more than one value is present in a case, append each in its own case by inserting
        // them in reverse order.
        for (JExpression expr : Lists.reverse(x.getExprs())) {
          ctx.insertBefore(new JCaseStatement(x.getSourceInfo(), expr));
        }
        ctx.removeMe();
      }
      super.endVisit(x, ctx);
    }
  }

  public static void exec(JProgram program) {
    new CaseSplitter().accept(program);
  }
}
