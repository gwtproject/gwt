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
package com.google.gwt.dev.jjs.ast;

import com.google.gwt.dev.jjs.SourceInfo;

/**
 * Java switch statement/expression.
 */
public class JSwitchExpression extends JExpression {
  private JBlock body;
  private JExpression expr;
  private JType type;

  public JSwitchExpression(SourceInfo info, JExpression expr, JBlock body, JType type) {
    super(info);
    this.expr = expr;
    this.body = body;
    this.type = type;
  }

  public JBlock getBody() {
    return body;
  }

  public JExpression getExpr() {
    return expr;
  }

  @Override
  public boolean hasSideEffects() {
    return true;
  }

  public void setType(JType type) {
    this.type = type;
  }

  @Override
  public void traverse(JVisitor visitor, Context ctx) {
    if (visitor.visit(this, ctx)) {
      expr = visitor.accept(expr);
      body = (JBlock) visitor.accept(body);
    }
    visitor.endVisit(this, ctx);
  }

  @Override
  public JType getType() {
    return type;
  }
}
