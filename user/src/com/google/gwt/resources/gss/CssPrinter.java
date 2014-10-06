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
import com.google.gwt.thirdparty.common.css.compiler.ast.CssUnknownAtRuleNode;
import com.google.gwt.thirdparty.common.css.compiler.ast.CssValueNode;
import com.google.gwt.thirdparty.common.css.compiler.passes.CompactPrinter;

import java.util.Stack;

/**
 * Visitor that convert the ast to a CSS string.
 */
public class CssPrinter extends CompactPrinter {
  /**
   * This value is used by {@link #concat} to help create a more balanced AST
   * tree by producing parenthetical expressions.
   */
  private static final int CONCAT_EXPRESSION_LIMIT = 20;
  private static final String CONTATENATION_BLOCK = ") + (";
  private static final String CONTATENATION = " + ";
  private static final String LEFT_BRACKET = "(";
  private static final String RIGHT_BRACKET = ")";
  private static final String EXTERNAL = "external";

  private final Stack<Boolean> elseNodeFound = new Stack<Boolean>();

  private StringBuilder masterStringBuilder;
  private String css;
  private int concatenationNumber;

  public CssPrinter(CssTree tree) {
    super(tree);
  }

  public CssPrinter(CssNode node) {
    super(node);
  }

  @Override
  public boolean enterTree(CssRootNode root) {
    masterStringBuilder.append("(");
    return super.enterTree(root);
  }

  @Override
  public String getCompactPrintedString() {
    return css;
  }

  @Override
  public void leaveTree(CssRootNode root) {
    masterStringBuilder.append(flushInternalStringBuilder()).append(")");
    super.leaveTree(root);
  }

  @Override
  public void runPass() {
    masterStringBuilder = new StringBuilder();
    concatenationNumber = 0;

    super.runPass();

    css = masterStringBuilder
        .toString()
        // remove empty string concatenation : '+ ("")'
        .replaceAll(" \\+ \\(\"\"\\)", "")
        // remove possible empty string concatenation '("") + ' at the  beginning
        .replaceAll("^\\(\"\"\\) \\+ ", "");
  }

  @Override
  public boolean enterConditionalBlock(CssConditionalBlockNode node) {
    masterStringBuilder.append(flushInternalStringBuilder());

    masterStringBuilder.append(CONTATENATION_BLOCK);

    elseNodeFound.push(false);

    return true;
  }

  @Override
  public void leaveConditionalBlock(CssConditionalBlockNode block) {
    if (!elseNodeFound.pop()) {
      masterStringBuilder.append("\"\"");
    }
    masterStringBuilder.append(CONTATENATION_BLOCK);

    // Reset concatenation counter
    concatenationNumber = 0;
  }

  @Override
  public boolean enterConditionalRule(CssConditionalRuleNode node) {
    if (node.getType() == Type.ELSE) {
      elseNodeFound.pop();
      elseNodeFound.push(true);

      masterStringBuilder.append(LEFT_BRACKET);
    } else {
      CssRuntimeConditionalRuleNode conditionalRuleNode = (CssRuntimeConditionalRuleNode) node;

      masterStringBuilder.append(LEFT_BRACKET);
      masterStringBuilder.append(conditionalRuleNode.getRuntimeCondition().getValue());
      masterStringBuilder.append(") ? (");

      // Reset concatenation counter
      concatenationNumber = 0;
    }

    return true;
  }

  @Override
  public void leaveConditionalRule(CssConditionalRuleNode node) {
    masterStringBuilder.append(flushInternalStringBuilder()).append(RIGHT_BRACKET);

    if (node.getType() != Type.ELSE) {
      masterStringBuilder.append(" : ");
    }
  }

  @Override
  public boolean enterUnknownAtRule(CssUnknownAtRuleNode node) {
    if (EXTERNAL.equals(node.getName().getValue())) {
      // Don't print external at-rule
      return false;
    }
    return super.enterUnknownAtRule(node);
  }

  @Override
  protected void appendValueNode(CssValueNode node) {
    if (node instanceof CssJavaExpressionNode || node instanceof CssDotPathNode) {
      concat("(" + node.getValue() + ")");
    } else {
      super.appendValueNode(node);
    }
  }

  private void concat(String stringToAppend) {
    masterStringBuilder.append(flushInternalStringBuilder());

    appendConcatOperation();

    masterStringBuilder.append(stringToAppend);

    appendConcatOperation();
  }

  private void appendConcatOperation() {
    // Avoid long string concatenation chain
    if (concatenationNumber >= CONCAT_EXPRESSION_LIMIT) {
      masterStringBuilder.append(CONTATENATION_BLOCK);
      concatenationNumber = 0;
    } else {
      masterStringBuilder.append(CONTATENATION);
      concatenationNumber++;
    }
  }

  /**
   * Read what the internal StringBuilder used by the CompactPrinter has already built. Escape it.
   * and reset the internal StringBuilder
   *
   * @return
   */
  private String flushInternalStringBuilder() {
    String content = "\"" + Generator.escape(sb.toString()) + "\"";
    sb = new StringBuilder();

    return content;
  }
}
