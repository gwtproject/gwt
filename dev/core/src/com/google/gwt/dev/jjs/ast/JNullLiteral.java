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

/**
 * Java null literal expression.
 */
public class JNullLiteral extends JValueLiteral {

  public static final JNullLiteral INSTANCE = new JNullLiteral(SourceOrigin.UNKNOWN);

  private JNullLiteral(SourceInfo sourceInfo) {
    super(sourceInfo);
  }

  @Override
  public JType getType() {
    return JNullType.INSTANCE;
  }

  @Override
  public Object getValueObj() {
    return null;
  }

  @Override
  public void traverse(JVisitor visitor, Context ctx) {
    if (visitor.visit(this, ctx)) {
    }
    visitor.endVisit(this, ctx);
  }

  /**
   * Note, if this ever becomes not-a-singleton, we'll need to check the
   * SourceInfo == SourceOrigin.UNKNOWN.
   */
  private Object readResolve() {
    return INSTANCE;
  }
}
