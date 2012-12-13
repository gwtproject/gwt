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

/**
 * A JavaScript regular expression.
 */
public final class JsRegExp extends JsValueLiteral {

  private String flags;

  private String pattern;

  public JsRegExp(SourceInfo sourceInfo) {
    super(sourceInfo);
  }

  public String getFlags() {
    return flags;
  }

  @Override
  public NodeKind getKind() {
    return NodeKind.REGEXP;
  }

  public String getPattern() {
    return pattern;
  }

  @Override
  public boolean isBooleanFalse() {
    return false;
  }

  @Override
  public boolean isBooleanTrue() {
    return true;
  }

  @Override
  public boolean isDefinitelyNotNull() {
    return true;
  }

  @Override
  public boolean isDefinitelyNull() {
    return false;
  }

  public void setFlags(String suffix) {
    this.flags = suffix;
  }

  public void setPattern(String re) {
    this.pattern = re;
  }

  @Override
  public void traverse(JsVisitor v, JsContext ctx) {
    v.visit(this, ctx);
    v.endVisit(this, ctx);
  }

  /*
  * Used for externalization only.
  */
  public JsRegExp() {
  }

  @Override
  public void writeExternal(ObjectOutput out) throws IOException {
    super.writeExternal(out);
    Util.serializeString(flags, out);
    Util.serializeString(pattern, out);
  }

  @Override
  public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
    super.readExternal(in);
    flags = Util.deserializeString(in);
    pattern = Util.deserializeString(in);
  }
}
