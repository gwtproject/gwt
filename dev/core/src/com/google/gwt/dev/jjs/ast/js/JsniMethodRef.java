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
package com.google.gwt.dev.jjs.ast.js;

import com.google.gwt.dev.jjs.SourceInfo;
import com.google.gwt.dev.jjs.ast.Context;
import com.google.gwt.dev.jjs.ast.JClassType;
import com.google.gwt.dev.jjs.ast.JMethod;
import com.google.gwt.dev.jjs.ast.JMethodCall;
import com.google.gwt.dev.jjs.ast.JNullLiteral;
import com.google.gwt.dev.jjs.ast.JProgram;
import com.google.gwt.dev.jjs.ast.JVisitor;
import com.google.gwt.dev.util.Util;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

/**
 * A call to a JSNI method.
 */
public class JsniMethodRef extends JMethodCall {

  private String ident;
  private JClassType jsoType;

  public JsniMethodRef(SourceInfo info, String ident, JMethod method, JClassType jsoType) {
    // Just use a null literal as the qualifier on a non-static method
    super(info, method.isStatic() ? null : JNullLiteral.INSTANCE, method);
    assert ident != null;
    this.ident = ident;
    this.jsoType = jsoType;
  }

  public String getIdent() {
    return ident;
  }

  @Override
  public JClassType getType() {
    return jsoType;
  }

  @Override
  public boolean hasSideEffects() {
    return false;
  }

  /**
   * Resolve an external references during AST stitching.
   */
  public void resolve(JMethod newMethod, JClassType jsoType) {
    super.resolve(newMethod);
    assert !jsoType.isExternal();
    assert jsoType.getName().equals(JProgram.JAVASCRIPTOBJECT);
    this.jsoType = jsoType;
  }

  @Override
  public void traverse(JVisitor visitor, Context ctx) {
    if (visitor.visit(this, ctx)) {
    }
    visitor.endVisit(this, ctx);
  }

  public JsniMethodRef() {
  }

  @Override
  public void writeExternal(ObjectOutput out) throws IOException {
    super.writeExternal(out);
    out.writeObject(jsoType);
    Util.serializeString(ident, out);
  }

  @Override
  public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
    super.readExternal(in);
    jsoType = (JClassType) in.readObject();
    ident = Util.deserializeString(in);
  }
}
