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
package com.google.gwt.resources.gss;

import com.google.gwt.core.ext.Generator;
import com.google.gwt.resources.gss.ast.CssDotPathNode;
import com.google.gwt.resources.gss.ast.CssJavaExpressionNode;
import com.google.gwt.resources.gss.ast.CssRuntimeConditionalRuleNode;
import com.google.gwt.thirdparty.common.css.compiler.ast.CssAtRuleNode.Type;
import com.google.gwt.thirdparty.common.css.compiler.ast.CssConditionalBlockNode;
import com.google.gwt.thirdparty.common.css.compiler.ast.CssConditionalRuleNode;
import com.google.gwt.thirdparty.common.css.compiler.ast.CssNode;
import com.google.gwt.thirdparty.common.css.compiler.ast.CssRootNode;
import com.google.gwt.thirdparty.common.css.compiler.ast.CssTree;
import com.google.gwt.thirdparty.common.css.compiler.ast.CssValueNode;
import com.google.gwt.thirdparty.common.css.compiler.passes.CompactPrinter;

import com.google.gwt.thirdparty.common.css.compiler.ast.CssSelectorListNode;
import com.google.gwt.thirdparty.common.css.compiler.ast.CssRulesetNode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Stack;

/**
 * Visitor that converts the AST to a {@code String} that can be evaluated as a Java expression.
 *
 * <p>For example, the following GSS code
 * <pre>
 *   @if(eval("com.foo.bar()")) {
 *     .foo {
 *       padding: 5px;
 *     }
 *   }
 *   {@literal @}else {
 *     .foo {
 *       padding: 15px;
 *     }
 *   }
 *   .bar {
 *     width:10px;
 *   }
 * }
 * </pre>
 * will be translated to
 * {@code "(com.foo.bar() ? (\".foo{padding:5px}\") : (\".foo{padding:15px}\")) + (\".bar{width:10px}\")"}
 */
public class CssRulesetCollector extends CompactPrinter {

  StringBuilder builder = new StringBuilder();

  public static class SelectorOut {
    private String methodName;
    private String content;
    private CssSelectorListNode selectors;

    public String getMethodName() {
      return methodName;
    }

    /**
     * @return the content
     */
    public String getContent() {
      return content;
    }

    public CssSelectorListNode getSelectors()
    {
      return selectors;
    }
  }

  private List<SelectorOut> cssOuts = new ArrayList<>();
  private int counter = 0;

  private SelectorOut current;

  public CssRulesetCollector(CssTree tree) {
    super(tree);
  }

  @Override
  public boolean enterRuleset(CssRulesetNode ruleset) {
    current = new SelectorOut();
    current.methodName = "__ruleSet" + counter;
    current.selectors = ruleset.getSelectors();
    counter++;
    resetBuffer();
    builder = new StringBuilder();
    builder.append("\"");

    return true;
  }

  @Override
  public void leaveRuleset(CssRulesetNode ruleset) {
    builder.append(Generator.escape(getOutputBuffer()));
    builder.append("\"");
    current.content = builder.toString();
    cssOuts.add(current);
    current = null;
  }

  @Override
  public void runPass() {
    cssOuts = new ArrayList<>();
    current = null;
    counter = 0;
    super.runPass();
  }

  public List<SelectorOut> getCssOuts() {
    return Collections.unmodifiableList(cssOuts);
  }

  @Override
  protected void appendValueNode(CssValueNode node) {
    if (node instanceof CssJavaExpressionNode || node instanceof CssDotPathNode) {
      builder.append(Generator.escape(getOutputBuffer()));
      resetBuffer();
      builder.append("\"");

      builder.append(" + ");
      builder.append(node.getValue());

      builder.append(" + ");
      builder.append("\"");
    } else {
      super.appendValueNode(node);
    }
  }

  @Override
  public boolean enterConditionalBlock(CssConditionalBlockNode node) {
    return true;
  }

  @Override
  public boolean enterConditionalRule(CssConditionalRuleNode node) {
    return true;
  }
}
