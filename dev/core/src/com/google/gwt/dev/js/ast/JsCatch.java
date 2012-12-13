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
 * Represents a JavaScript catch clause.
 */
public class JsCatch extends JsNode implements HasCondition {

  protected JsCatchScope scope;

  private JsBlock body;

  private JsExpression condition;

  private JsParameter param;

  public JsCatch(SourceInfo sourceInfo, JsScope parent, String ident) {
    super(sourceInfo);
    assert (parent != null);
    scope = new JsCatchScope(parent, ident);
    param = new JsParameter(sourceInfo, scope.findExistingName(ident));
  }

  public JsBlock getBody() {
    return body;
  }

  @Override
  public JsExpression getCondition() {
    return condition;
  }

  @Override
  public NodeKind getKind() {
    return NodeKind.BREAK;
  }

  public JsParameter getParameter() {
    return param;
  }

  public JsScope getScope() {
    return scope;
  }

  public void setBody(JsBlock body) {
    this.body = body;
  }

  @Override
  public void setCondition(JsExpression condition) {
    this.condition = condition;
  }

  @Override
  public void traverse(JsVisitor v, JsContext ctx) {
    if (v.visit(this, ctx)) {
      param = v.accept(param);
      if (condition != null) {
        condition = v.accept(condition);
      }
      body = v.accept(body);
    }
    v.endVisit(this, ctx);
  }

  /*
  * Used for externalization only.
  */
  public JsCatch() {
  }

  @Override
  public void writeExternal(ObjectOutput out) throws IOException {
    super.writeExternal(out);
    out.writeObject(scope);
    out.writeObject(body);
    out.writeObject(condition);
    out.writeObject(param);
  }

  @Override
  public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
    super.readExternal(in);
    scope = (JsCatchScope) in.readObject();
    body = (JsBlock) in.readObject();
    condition = (JsExpression) in.readObject();
    param = (JsParameter) in.readObject();
  }
}
