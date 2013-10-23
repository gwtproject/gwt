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
import com.google.gwt.dev.jjs.ast.JExpression;
import com.google.gwt.dev.jjs.ast.JPrimitiveType;
import com.google.gwt.dev.jjs.ast.JType;
import com.google.gwt.dev.jjs.ast.JVisitor;
import com.google.gwt.thirdparty.guava.common.collect.Lists;

import java.util.List;


/**
 * Represents multiple ordered expressions as a single compound expression.
 */
public class JMultiExpression extends JExpression {

  private List<JExpression> expressions = Lists.newArrayList();

  public JMultiExpression(SourceInfo info) {
    super(info);
  }

  public JType getType() {
    int c = getExpressions().size();
    if (c == 0) {
      return JPrimitiveType.VOID;
    } else {
      return getExpressions().get(c - 1).getType();
    }
  }

  @Override
  public boolean hasSideEffects() {
    for (int i = 0; i < getExpressions().size(); ++i) {
      JExpression expr = getExpressions().get(i);
      if (expr.hasSideEffects()) {
        return true;
      }
    }
    return false;
  }

  public void traverse(JVisitor visitor, Context ctx) {
    if (visitor.visit(this, ctx)) {
      visitor.acceptWithInsertRemove(getExpressions());
    }
    visitor.endVisit(this, ctx);
  }

  public List<JExpression> getExpressions() {
    return expressions;
  }
}
