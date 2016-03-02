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

/**
 * Java method parameter definition.
 */
public class JParameter extends JVariable {

  private final boolean isThis;
  private final boolean isVarags;
  private boolean isOptional;

  public JParameter(SourceInfo info, String name, JType type, boolean isFinal) {
    this(info, name, type, isFinal, false, false, false);
  }

  JParameter(SourceInfo info, String name, JType type, boolean isFinal, boolean isVarargs,
      boolean isThis, boolean isOptional) {
    super(info, name, type, isFinal);
    this.isThis = isThis;
    this.isVarags = isVarargs;
    this.isOptional = isOptional;
    assert !isVarargs || type.isArrayType();
  }

  public JParameterRef createRef(SourceInfo info) {
    return new JParameterRef(info, this);
  }

  /**
   * Returns <code>true</code> if this parameter marked as optional.
   */
  public boolean isOptional() {
    return isOptional;
  }

  /**
   * Returns <code>true</code> if this parameter is the this parameter of a
   * static impl method.
   */
  public boolean isThis() {
    return isThis;
  }

  /**
   * Returns <code>true</code> if this parameter is a varargs parameter.
   */
  public boolean isVarargs() {
    return isVarags;
  }

  public void setOptional() {
    isOptional = true;
  }

  @Override
  public JParameterRef makeRef(SourceInfo info) {
    return new JParameterRef(info, this);
  }

  @Override
  public void setInitializer(JDeclarationStatement declStmt) {
    throw new UnsupportedOperationException("A JParameter cannot have an initializer");
  }

  @Override
  public void traverse(JVisitor visitor, Context ctx) {
    if (visitor.visit(this, ctx)) {
    }
    visitor.endVisit(this, ctx);
  }
}
