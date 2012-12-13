/*
 * Copyright 2008 Google Inc.
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
package com.google.gwt.dev.jjs.ast.js;

import com.google.gwt.dev.jjs.SourceInfo;
import com.google.gwt.dev.jjs.ast.Context;
import com.google.gwt.dev.jjs.ast.JClassType;
import com.google.gwt.dev.jjs.ast.JExpression;
import com.google.gwt.dev.jjs.ast.JNode;
import com.google.gwt.dev.jjs.ast.JVisitor;
import com.google.gwt.dev.util.Util;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a JS construct that should be emitted as a JSON-style object.
 */
public class JsonObject extends JExpression {

  /**
   * An individual property initializer within a JSON object initializer.
   */
  public static class JsonPropInit extends JNode {

    public JExpression labelExpr;
    public JExpression valueExpr;

    public JsonPropInit(SourceInfo sourceInfo, JExpression labelExpr, JExpression valueExpr) {
      super(sourceInfo);
      this.labelExpr = labelExpr;
      this.valueExpr = valueExpr;
    }

    public void traverse(JVisitor visitor, Context ctx) {
      if (visitor.visit(this, ctx)) {
        labelExpr = visitor.accept(labelExpr);
        valueExpr = visitor.accept(valueExpr);
      }
      visitor.endVisit(this, ctx);
    }
  }

  public List<JsonPropInit> propInits;
  private JClassType jsoType;

  public JsonObject(SourceInfo sourceInfo, JClassType jsoType) {
    super(sourceInfo);
    this.propInits = new ArrayList<JsonPropInit>();
    this.jsoType = jsoType;
  }

  public JClassType getType() {
    return jsoType;
  }

  @Override
  public boolean hasSideEffects() {
    for (JsonPropInit propInit : propInits) {
      if (propInit.labelExpr.hasSideEffects() || propInit.valueExpr.hasSideEffects()) {
        return true;
      }
    }
    return false;
  }

  /**
   * Resolve an external references during AST stitching.
   */
  public void resolve(JClassType jsoType) {
    assert jsoType.replaces(this.jsoType);
    this.jsoType = jsoType;
  }

  public void traverse(JVisitor visitor, Context ctx) {
    if (visitor.visit(this, ctx)) {
      visitor.accept(propInits);
    }
    visitor.endVisit(this, ctx);
  }


  public JsonObject() {
  }

  @Override
  public void writeExternal(ObjectOutput out) throws IOException {
    super.writeExternal(out);
    Util.serializeCollection(propInits, out);
    out.writeObject(jsoType);
  }

  @Override
  public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
    super.readExternal(in);
    propInits = Util.deserializeObjectList(in);
    jsoType = (JClassType) in.readObject();
  }


}
