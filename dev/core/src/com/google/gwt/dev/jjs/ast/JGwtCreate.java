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

import java.util.List;

/**
 * Represents a GWT.create() call before deferred binding decisions are
 * finalized. Replaced with the entry call for the appropriate rebind result in
 * that permutation.
 */
public class JGwtCreate extends JExpression {

  public static JExpression createInstantiationExpression(SourceInfo info, JClassType classType) {
    /*
     * Find the appropriate (noArg) constructor. In our AST, constructors are
     * instance methods that should be qualified with a new expression.
     */
    JConstructor noArgCtor = null;
    for (JMethod ctor : classType.getMethods()) {
      if (ctor instanceof JConstructor) {
        if (ctor.getOriginalParamTypes().size() == 0) {
          noArgCtor = (JConstructor) ctor;
          break;
        }
      }
    }
    if (noArgCtor == null) {
      return null;
    }
    // Call it, using a new expression as a qualifier
    return new JNewInstance(info, noArgCtor);
  }

  private final List<JExpression> instantiationExpressions;

  private final List<String> resultTypes;

  private final String sourceType;

  /*
   * Initially object; will be updated by type tightening.
   */
  private JType type;

  /**
   * Constructor used for cloning an existing node.
   */
  public JGwtCreate(SourceInfo info, String sourceType, List<String> resultTypes, JType type,
      List<JExpression> instantiationExpressions) {
    super(info);
    this.sourceType = sourceType;
    this.resultTypes = resultTypes;
    this.type = type;
    this.instantiationExpressions = instantiationExpressions;
  }

  public List<JExpression> getInstantiationExpressions() {
    return instantiationExpressions;
  }

  public List<String> getResultTypes() {
    return resultTypes;
  }

  public String getSourceType() {
    return sourceType;
  }

  @Override
  public JType getType() {
    return type;
  }

  @Override
  public boolean hasSideEffects() {
    for (JExpression expr : instantiationExpressions) {
      if (expr.hasSideEffects()) {
        return true;
      }
    }
    return false;
  }

  public void setType(JType newType) {
    type = newType;
  }

  @Override
  public void traverse(JVisitor visitor, Context ctx) {
    if (visitor.visit(this, ctx)) {
      visitor.accept(instantiationExpressions);
    }
    visitor.endVisit(this, ctx);
  }
}
