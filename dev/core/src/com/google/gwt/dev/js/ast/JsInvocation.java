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
import com.google.gwt.thirdparty.guava.common.collect.Iterables;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Represents a JavaScript invocation.
 */
public final class JsInvocation extends JsExpression implements HasArguments {

  private final List<JsExpression> args = new ArrayList<JsExpression>();

  private JsExpression qualifier;

  public JsInvocation(SourceInfo sourceInfo) {
    super(sourceInfo);
  }

  public JsInvocation(SourceInfo sourceInfo, JsFunction function, JsExpression... args) {
    this(sourceInfo, function.getName().makeRef(sourceInfo), args);
  }

  public JsInvocation(SourceInfo sourceInfo, JsExpression function, JsExpression... args) {
    super(sourceInfo);
    setQualifier(function);
    Collections.addAll(this.args, args);
  }

  public JsInvocation(SourceInfo sourceInfo, JsFunction function, Iterable<JsExpression> args) {
    this(sourceInfo, function.getName().makeRef(sourceInfo), args);
  }

  public JsInvocation(SourceInfo sourceInfo, JsExpression function, Iterable<JsExpression> args) {
    super(sourceInfo);
    assert function != null;
    setQualifier(function);
    Iterables.addAll(this.args, args);
  }

  @Override
  public List<JsExpression> getArguments() {
    return args;
  }

  @Override
  public NodeKind getKind() {
    return NodeKind.INVOKE;
  }

  public JsExpression getQualifier() {
    return qualifier;
  }

  @Override
  public boolean hasSideEffects() {
    return true;
  }

  @Override
  public boolean isDefinitelyNull() {
    return false;
  }

  public void setQualifier(JsExpression qualifier) {
    this.qualifier = qualifier;
  }

  @Override
  public void traverse(JsVisitor v, JsContext ctx) {
    if (v.visit(this, ctx)) {
      qualifier = v.accept(qualifier);
      v.acceptList(args);
    }
    v.endVisit(this, ctx);
  }
}
