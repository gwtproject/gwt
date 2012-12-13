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
import com.google.gwt.dev.util.StringInterner;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

/**
 * Should we have a JLabelRef also?
 */
public class JLabel extends JNode implements HasName, Externalizable {

  private String name;

  public JLabel(SourceInfo info, String name) {
    super(info);
    this.name = StringInterner.get().intern(name);
  }

  public String getName() {
    return name;
  }

  public void traverse(JVisitor visitor, Context ctx) {
    if (visitor.visit(this, ctx)) {
    }
    visitor.endVisit(this, ctx);
  }

  public JLabel() {
  }

  @Override
  public void writeExternal(ObjectOutput out) throws IOException {
    super.writeExternalImpl(out);
    com.google.gwt.dev.util.Util.serializeString(name, out);
  }

  @Override
  public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
    super.readExternalImpl(in);
    name = com.google.gwt.dev.util.Util.deserializeString(in);
  }
}
