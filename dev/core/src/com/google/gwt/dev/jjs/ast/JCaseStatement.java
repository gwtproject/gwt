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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Java case statement.
 */
public class JCaseStatement extends JStatement {

  private List<JExpression> exprs;

  public JCaseStatement(SourceInfo info, JExpression expr) {
    this(info, Collections.singletonList(expr));
    assert exprs != null;
  }

  public JCaseStatement(SourceInfo info, Collection<JExpression> exprs) {
    super(info);
    this.exprs = Collections.unmodifiableList(new ArrayList<>(exprs));
  }

  @Deprecated
  public JExpression getExpr() {
    if (exprs.size() > 1) {
      throw new IllegalStateException("JCaseStatement.getExpr() called on a node with multiple expressions " + exprs);
    }
    return exprs.isEmpty() ? null : exprs.get(0);
  }

  public List<JExpression> getExprs() {
    return exprs;
  }

  @Override
  public void traverse(JVisitor visitor, Context ctx) {
    if (visitor.visit(this, ctx)) {
      exprs = visitor.acceptImmutable(exprs);
    }
    visitor.endVisit(this, ctx);
  }

}
