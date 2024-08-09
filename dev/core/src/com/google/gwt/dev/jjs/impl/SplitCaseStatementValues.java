/*
 * Copyright 2024 GWT Project Authors
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
package com.google.gwt.dev.jjs.impl;

import com.google.gwt.dev.jjs.ast.Context;
import com.google.gwt.dev.jjs.ast.JCaseStatement;
import com.google.gwt.dev.jjs.ast.JExpression;
import com.google.gwt.dev.jjs.ast.JModVisitor;
import com.google.gwt.dev.jjs.ast.JProgram;

/**
 * Breaks up Java 14 case statements so that each has exactly one value, allowing them to be
 * rewritten as js case statements, which are only permitted one value each.
 */
public class SplitCaseStatementValues {
  private static class CaseSplitter extends JModVisitor {
    @Override
    public void endVisit(JCaseStatement x, Context ctx) {
      if (x.getExprs().size() > 1) {
        // If more than one value is present in a case, append each in its own case
        for (JExpression expr : x.getExprs()) {
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
