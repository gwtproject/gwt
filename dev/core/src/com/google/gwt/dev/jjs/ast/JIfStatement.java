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
 * Java if statement.
 */
public class JIfStatement extends JStatement {

  private JBlock elseStmt;
  private JExpression ifExpr;
  private JBlock thenStmt;

  public JIfStatement(SourceInfo info, JExpression ifExpr, JStatement thenStmt, JStatement elseStmt) {
    super(info);
    this.ifExpr = ifExpr;
    this.thenStmt = ensureBlock(info, thenStmt);
    this.elseStmt = ensureBlock(info, elseStmt);
  }

  public JBlock getElseStmt() {
    return elseStmt;
  }

  public JExpression getIfExpr() {
    return ifExpr;
  }

  public JBlock getThenStmt() {
    return thenStmt;
  }

  @Override
  public void traverse(JVisitor visitor, Context ctx) {
    if (visitor.visit(this, ctx)) {
      ifExpr = visitor.accept(ifExpr);
      thenStmt = ensureBlock(getSourceInfo(), visitor.accept(thenStmt, false));
      elseStmt = ensureBlock(getSourceInfo(), visitor.accept(elseStmt, false));
    }
    visitor.endVisit(this, ctx);
  }

  private static JBlock ensureBlock(SourceInfo info, JStatement statement) {
    if (statement == null) {
      return new JBlock(info);
    }
    if (statement instanceof JBlock) {
      return (JBlock) statement;
    }

    return new JBlock(statement.getSourceInfo(), statement);
  }

  @Override
  public boolean unconditionalControlBreak() {
    boolean thenBreaks = thenStmt != null && thenStmt.unconditionalControlBreak();
    if (thenBreaks && ifExpr == JBooleanLiteral.get(true)) {
      // if(true) { /* unconditional break */ }
      return true;
    }

    boolean elseBreaks = elseStmt != null && elseStmt.unconditionalControlBreak();
    if (elseBreaks && ifExpr == JBooleanLiteral.get(false)) {
      // if(false) { } else { /* unconditional break */ }
      return true;
    }

    if (thenBreaks && elseBreaks) {
      // both branches have an unconditional break
      return true;
    }
    return false;
  }
}
