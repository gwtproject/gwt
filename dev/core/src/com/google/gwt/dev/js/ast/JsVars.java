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
import com.google.gwt.dev.util.Util;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * A JavaScript <code>var</code> statement.
 */
public class JsVars extends JsStatement implements Iterable<JsVars.JsVar> {

  /**
   * A var declared using the JavaScript <code>var</code> statement.
   */
  public static class JsVar extends JsNode implements HasName {

    private JsExpression initExpr;

    private JsName name;

    public JsVar(SourceInfo sourceInfo, JsName name) {
      super(sourceInfo);
      this.name = name;
    }

    public JsExpression getInitExpr() {
      return initExpr;
    }

    @Override
    public NodeKind getKind() {
      return NodeKind.VAR;
    }

    @Override
    public JsName getName() {
      return name;
    }

    public void setInitExpr(JsExpression initExpr) {
      this.initExpr = initExpr;
    }

    @Override
    public void traverse(JsVisitor v, JsContext ctx) {
      if (v.visit(this, ctx)) {
        if (initExpr != null) {
          initExpr = v.accept(initExpr);
        }
      }
      v.endVisit(this, ctx);
    }

    /*
    * Used for externalization only.
    */
    public JsVar() {
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
      super.writeExternal(out);
      out.writeObject(name);
      out.writeObject(initExpr);
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
      super.readExternal(in);
      name = (JsName) in.readObject();
      initExpr = (JsExpression) in.readObject();
    }
  }

  private List<JsVar> vars;

  public JsVars(SourceInfo sourceInfo) {
    super(sourceInfo);
    vars = new ArrayList<JsVar>();
  }

  public void add(JsVar var) {
    vars.add(var);
  }

  @Override
  public NodeKind getKind() {
    return NodeKind.VARS;
  }

  public int getNumVars() {
    return vars.size();
  }

  public boolean isEmpty() {
    return vars.isEmpty();
  }

  // Iterator returns JsVar objects
  @Override
  public Iterator<JsVar> iterator() {
    return vars.iterator();
  }

  @Override
  public void traverse(JsVisitor v, JsContext ctx) {
    if (v.visit(this, ctx)) {
      v.acceptWithInsertRemove(vars);
    }
    v.endVisit(this, ctx);
  }

  /*
  * Used for externalization only.
  */
  public JsVars() {
  }

  @Override
  public void writeExternal(ObjectOutput out) throws IOException {
    super.writeExternal(out);
    Util.serializeCollection(vars, out);
  }

  @Override
  public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
    super.readExternal(in);
    vars = Util.deserializeObjectList(in);
  }

}
