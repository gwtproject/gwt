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

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

/**
 * Java while statement.
 */
public class JWhileStatement extends JStatement {

  private JStatement body;
  private JExpression testExpr;

  public JWhileStatement(SourceInfo info, JExpression testExpr, JStatement body) {
    super(info);
    this.testExpr = testExpr;
    this.body = body;
  }

  public JStatement getBody() {
    return body;
  }

  public JExpression getTestExpr() {
    return testExpr;
  }

  public void traverse(JVisitor visitor, Context ctx) {
    if (visitor.visit(this, ctx)) {
      testExpr = visitor.accept(testExpr);
      if (body != null) {
        body = visitor.accept(body);
      }
    }
    visitor.endVisit(this, ctx);
  }
  public JWhileStatement() {
  }

  @Override
  public void writeExternal(ObjectOutput out) throws IOException {
    super.writeExternal(out);
    out.writeObject(testExpr);
    out.writeObject(body);
  }

  @Override
  public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
    super.readExternal(in);
    testExpr = (JExpression) in.readObject();
    body = (JStatement) in.readObject();
  }

}
