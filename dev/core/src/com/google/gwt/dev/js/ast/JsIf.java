/*
 * Copyright 2008 Google Inc.
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
package com.google.gwt.dev.js.ast;

import com.google.gwt.dev.jjs.SourceInfo;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

/**
 * Represents a JavaScript if statement.
 */
public final class JsIf extends JsStatement {

  private JsExpression ifExpr;

  private JsStatement thenStmt;

  private JsStatement elseStmt;

  public JsIf(SourceInfo sourceInfo) {
    super(sourceInfo);
  }

  public JsIf(SourceInfo sourceInfo, JsExpression ifExpr, JsStatement thenStmt, JsStatement elseStmt) {
    super(sourceInfo);
    this.ifExpr = ifExpr;
    this.thenStmt = thenStmt;
    this.elseStmt = elseStmt;
  }

  public JsStatement getElseStmt() {
    return elseStmt;
  }

  public JsExpression getIfExpr() {
    return ifExpr;
  }

  @Override
  public NodeKind getKind() {
    return NodeKind.IF;
  }

  public JsStatement getThenStmt() {
    return thenStmt;
  }

  public void setElseStmt(JsStatement elseStmt) {
    this.elseStmt = elseStmt;
  }

  public void setIfExpr(JsExpression ifExpr) {
    this.ifExpr = ifExpr;
  }

  public void setThenStmt(JsStatement thenStmt) {
    this.thenStmt = thenStmt;
  }

  @Override
  public void traverse(JsVisitor v, JsContext ctx) {
    if (v.visit(this, ctx)) {
      ifExpr = v.accept(ifExpr);
      thenStmt = v.accept(thenStmt);
      if (elseStmt != null) {
        elseStmt = v.accept(elseStmt);
      }
    }
    v.endVisit(this, ctx);
  }

  @Override
  public boolean unconditionalControlBreak() {
    boolean thenBreaks = thenStmt != null && thenStmt.unconditionalControlBreak();
    boolean elseBreaks = elseStmt != null && elseStmt.unconditionalControlBreak();
    if (thenBreaks && elseBreaks) {
      // both branches have an unconditional break
      return true;
    }
    return false;
  }

  /*
  * Used for externalization only.
  */
  public JsIf() {
  }

  @Override
  public void writeExternal(ObjectOutput out) throws IOException {
    super.writeExternal(out);
    out.writeObject(ifExpr);
    out.writeObject(thenStmt);
    out.writeObject(elseStmt);
  }

  @Override
  public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
    super.readExternal(in);
    ifExpr = (JsExpression) in.readObject();
    thenStmt = (JsStatement) in.readObject();
    elseStmt = (JsStatement) in.readObject();
  }
}
