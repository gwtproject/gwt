/*
 * Copyright 2007 Google Inc.
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
package com.google.gwt.dev.jjs.ast;

import com.google.gwt.dev.jjs.SourceInfo;

/**
 * Wrapper to represent a Java switch expression as a JStatement.
 */
public class JSwitchStatement extends JStatement {

  private final JSwitchExpression expr;

  public JSwitchStatement(SourceInfo info, JExpression expr, JBlock block) {
    this(new JSwitchExpression(info, expr, block, JPrimitiveType.VOID));
  }

  public JSwitchStatement(JSwitchExpression expr) {
    super(expr.getSourceInfo());
    this.expr = expr;
  }

  public JBlock getBody() {
    return expr.getBody();
  }

  public JExpression getExpr() {
    return expr.getExpr();
  }

  @Override
  public void traverse(JVisitor visitor, Context ctx) {
    if (visitor.visit(this, ctx)) {
      visitor.accept(expr);
    }
    visitor.endVisit(this, ctx);
  }
}
