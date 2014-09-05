/*
 * Copyright 2014 Google Inc.
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
import com.google.gwt.dev.jjs.ast.JCastOperation;
import com.google.gwt.dev.jjs.ast.JExpression;
import com.google.gwt.dev.jjs.ast.JFieldRef;
import com.google.gwt.dev.jjs.ast.JModVisitor;
import com.google.gwt.dev.jjs.ast.JProgram;

/**
 * Replaces compile time constants by their values.
 * <p>
 * This pass is necessary, even in unoptimized compiles, to allow forward references to compile
 * time values in bootstrap code.
 **/
public class CompileTimeConstantsReplacer {
  private static class CompileTimeConstantsReplacingVisitor extends JModVisitor {

    @Override
    public void endVisit(JFieldRef x, Context ctx) {
      if (x.getField().isCompileTimeConstant() && !ctx.isLvalue()) {
        // TODO(rluble): Simplify the expression to a literal here after refactoring Simplifier.
        // The initializer is guaranteed to be constant but it may be a non literal expression.
        JExpression constantExpression =
            new CloneExpressionVisitor().cloneExpression(x.getField().getInitializer());
        if (x.getField().getType() != constantExpression.getType()) {
          constantExpression =
              new JCastOperation(constantExpression.getSourceInfo(), x.getField().getType(), constantExpression);
        }
        ctx.replaceMe(constantExpression);
      }
    }
  }

  public static void exec(JProgram program) {
    JModVisitor visitor;
    do {
      visitor = new CompileTimeConstantsReplacingVisitor();
      visitor.accept(program);
    } while (visitor.didChange());
  }
}
