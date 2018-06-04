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

import static com.google.gwt.thirdparty.guava.common.base.Preconditions.checkArgument;

import com.google.gwt.dev.jjs.SourceInfo;

/**
 * Java method this (or super) expression.
 */
public class JThisRef extends JExpression {

  private final JDeclaredType classType;
  private final JType type;

  public JThisRef(SourceInfo info, JDeclaredType classType) {
    this(info, classType, classType);
  }

  public JThisRef(SourceInfo info, JDeclaredType classType, JType type) {
    super(info);
    this.classType = classType;
    this.type = type;
    checkArgument(type.getUnderlyingType().equals(classType));
  }

  public JDeclaredType getClassType() {
    return classType;
  }

  @Override
  public JType getType() {
    return type;
  }

  @Override
  public boolean hasSideEffects() {
    return false;
  }

  @Override
  public void traverse(JVisitor visitor, Context ctx) {
    if (visitor.visit(this, ctx)) {
    }
    visitor.endVisit(this, ctx);
  }

}
