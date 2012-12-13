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
package com.google.gwt.dev.jjs.ast;

import com.google.gwt.dev.jjs.SourceInfo;
import com.google.gwt.dev.jjs.SourceOrigin;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

/**
 * Java literal expression that evaluates to a Long.
 */
public class JLongLiteral extends JValueLiteral {

  public static final JLongLiteral ZERO = new JLongLiteral(SourceOrigin.UNKNOWN, 0L);

  public static JLongLiteral get(long value) {
    return (value == 0) ? ZERO : new JLongLiteral(SourceOrigin.UNKNOWN, value);
  }

  private long value;

  public JLongLiteral(SourceInfo sourceInfo, long value) {
    super(sourceInfo);
    this.value = value;
  }

  @Override
  public JValueLiteral cloneFrom(JValueLiteral value) {
    Object valueObj = value.getValueObj();
    if (valueObj instanceof Character) {
      Character character = (Character) valueObj;
      return new JLongLiteral(value.getSourceInfo(), character.charValue());
    } else if (valueObj instanceof Number) {
      Number number = (Number) valueObj;
      return new JLongLiteral(value.getSourceInfo(), number.longValue());
    }
    return null;
  }

  public JType getType() {
    return JPrimitiveType.LONG;
  }

  public long getValue() {
    return value;
  }

  @Override
  public Object getValueObj() {
    return Long.valueOf(value);
  }

  public void traverse(JVisitor visitor, Context ctx) {
    if (visitor.visit(this, ctx)) {
    }
    visitor.endVisit(this, ctx);
  }

  private Object readResolve() {
    return (value == 0L) ? ZERO : this;
  }

  public JLongLiteral() {
  }

  @Override
  public void writeExternal(ObjectOutput out) throws IOException {
    super.writeExternal(out);
    out.writeLong(value);
  }

  @Override
  public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
    super.readExternal(in);
    value = (long) in.readLong();
  }
}
