/*
 * Copyright 2014 Google Inc.
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
package com.google.gwt.resources.gss.ast;

import com.google.gwt.thirdparty.common.css.compiler.ast.CssConditionalRuleNode;
import com.google.gwt.thirdparty.common.css.compiler.ast.CssValueNode;
import com.google.gwt.thirdparty.guava.common.base.Preconditions;
import com.google.gwt.thirdparty.guava.common.collect.ImmutableList;

import javax.annotation.Nullable;

/**
 * Represent a conditional rule that has to be evaluated at runtime.
 */
public class CssRuntimeConditionalRuleNode extends CssConditionalRuleNode {

  public CssRuntimeConditionalRuleNode(CssConditionalRuleNode node,
      @Nullable CssJavaExpressionNode condition) {
    super(node.getType(), node.getName().deepCopy(), null,
        node.getBlock() != null ? node.getBlock().deepCopy() : null);
    setSourceCodeLocation(node.getSourceCodeLocation());

    if (condition != null) {
      setRuntimeCondition(condition);
    }
  }

  /**
   * Copy constructor.
   *
   * @param node
   */
  public CssRuntimeConditionalRuleNode(CssRuntimeConditionalRuleNode node) {
    this(node, node.getRuntimeCondition());
  }

  /**
   * Can only be called after the node is completely built (meaning it has
   * the condition set).
   */
  public CssJavaExpressionNode getRuntimeCondition() {
    if (getType() == Type.ELSE) {
      Preconditions.checkState(getParametersCount() == 0);
      return null;
    }

    Preconditions.checkState(getParametersCount() == 1);
    return (CssJavaExpressionNode) this.getParameters().get(0);
  }

  void setRuntimeCondition(CssJavaExpressionNode condition) {
    Preconditions.checkState(getType() != Type.ELSE);
    Preconditions.checkState(getParametersCount() <= 1);
    this.setParameters(ImmutableList.<CssValueNode>of(condition));
  }

  @Override
  public CssConditionalRuleNode deepCopy() {
    return new CssRuntimeConditionalRuleNode(this);
  }
}
