/*
 * Copyright 2009 Google Inc.
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
package com.google.gwt.dev.js;

import com.google.gwt.dev.jjs.JsSourceMap;
import com.google.gwt.dev.jjs.ast.JClassType;
import com.google.gwt.dev.jjs.ast.JMethod;
import com.google.gwt.dev.jjs.impl.JavaToJavaScriptMap;
import com.google.gwt.dev.jjs.impl.codesplitter.FragmentExtractor;
import com.google.gwt.dev.js.ast.JsName;
import com.google.gwt.dev.js.ast.JsStatement;
import com.google.gwt.dev.js.ast.JsVars.JsVar;
import com.google.gwt.dev.js.ast.JsVisitable;
import com.google.gwt.dev.util.TextOutput;
import com.google.gwt.dev.util.collect.HashMap;

import java.util.Map;

/**
 * A version of {@link JsSourceGenerationVisitor} that records a
 * {@link SizeBreakdown} as it goes.
 */
public class JsSourceGenerationVisitorWithSizeBreakdown extends
    JsSourceGenerationVisitor {

  private JavaToJavaScriptMap map;
  private JsName billedAncestor; // non-null when an ancestor is also being billed
  private TextOutput out;
  private final Map<JsName, Integer> sizeMap = new HashMap<JsName, Integer>();

  public JsSourceGenerationVisitorWithSizeBreakdown(TextOutput out,
      JavaToJavaScriptMap javaToJavaScriptMap) {
    super(out);
    this.out = out;
    this.map = javaToJavaScriptMap;
  }

  public SizeBreakdown getSizeBreakdown() {
    return new SizeBreakdown(out.getPosition(), sizeMap);
  }

  public JsSourceMap getSourceInfoMap() {
    // override if your child class creates sourceinfo
    return null;
  }

  @Override
  protected final <T extends JsVisitable> T doAccept(T node) {
    JsName newName = nameToBillTo(node, billedAncestor != null);
    return generateAndBill(node, newName);
  }

  /**
   * Generate some JavaScript and bill the number of characters generated to the given name.
   */
  protected <T extends JsVisitable> T generateAndBill(T node, JsName nameToBillTo) {
    if (nameToBillTo == null) {
      return super.doAccept(node);
    } else {
      int start = out.getPosition();

      JsName savedAncestor = billedAncestor;
      billedAncestor = nameToBillTo;
      T retValue = super.doAccept(node);
      billedAncestor = savedAncestor;

      billChars(nameToBillTo, out.getPosition() - start);
      return retValue;
    }
  }

  private void billChars(JsName nameToBillTo, int chars) {
    Integer oldSize = sizeMap.get(nameToBillTo);
    if (oldSize == null) {
      oldSize = 0;
    }
    sizeMap.put(nameToBillTo, oldSize + chars);
  }

  /**
   * Returns the type, function, or variable name where this node's character count
   * should be added, or null to bill to nobody.
   */
  private JsName nameToBillTo(JsVisitable node, boolean isAncestorBilled) {
    if (node instanceof JsStatement) {
      JsStatement stat = (JsStatement) node;
      JClassType type = map.typeForStatement(stat);
      if (type != null) {
        return map.nameForType(type);
      }

      JMethod method = FragmentExtractor.methodFor(stat, map);
      if (method != null) {
        return map.nameForMethod(method);
      }

      return null;

    } else if (node instanceof JsVar) {
      return isAncestorBilled ? null : ((JsVar) node).getName(); // handle top-level vars
    }

    return null;
  }

}
