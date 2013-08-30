/*
 * Copyright 2010 Google Inc.
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

import com.google.gwt.core.ext.BadPropertyValueException;
import com.google.gwt.core.ext.PropertyOracle;
import com.google.gwt.core.ext.SelectionProperty;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.dev.jjs.SourceInfo;
import com.google.gwt.dev.jjs.impl.CompilerContext;
import com.google.gwt.dev.js.ast.JsBinaryOperation;
import com.google.gwt.dev.js.ast.JsBinaryOperator;
import com.google.gwt.dev.js.ast.JsContext;
import com.google.gwt.dev.js.ast.JsExpression;
import com.google.gwt.dev.js.ast.JsModVisitor;
import com.google.gwt.dev.js.ast.JsPrefixOperation;
import com.google.gwt.dev.js.ast.JsUnaryOperator;

/**
 * Coerces lhs of right shift operations to int. Necessary for Safari 5 bug
 * https://bugs.webkit.org/show_bug.cgi?id=40367 fixed in
 * http://trac.webkit.org/changeset/60990 -- this should be removed once that
 * fix has been pushed.
 */
public class JsCoerceIntShift {
  // TODO(jat): remove this once Safari 5 has the update

  /**
   * Rewrite a >> b as (~~a) >> b.
   */
  private static class MyVisitor extends JsModVisitor {

    @Override
    public void endVisit(JsBinaryOperation x, JsContext ctx) {
      JsBinaryOperator op = x.getOperator();
      if (op != JsBinaryOperator.SHR && op != JsBinaryOperator.SHRU) {
        return;
      }

      SourceInfo sourceInfo = x.getSourceInfo();
      JsExpression lhs = x.getArg1();
      JsExpression rhs = x.getArg2();
      JsExpression newNode = new JsBinaryOperation(sourceInfo, op,
          new JsPrefixOperation(sourceInfo, JsUnaryOperator.BIT_NOT,
              new JsPrefixOperation(sourceInfo, JsUnaryOperator.BIT_NOT, lhs)),
          rhs);
      ctx.replaceMe(newNode);
    }
  }

  /**
   * If this permutation may be executed on WebKit, rewrite a >> b as ~~a >> b.
   *
   * @param compilerContext
   * @return true if any changes were made
   */
  public static boolean exec(CompilerContext compilerContext) {
    boolean seenWebKit = false;
    for (PropertyOracle oracle : compilerContext.getPropertyOracles()) {
      try {
        SelectionProperty prop = oracle.getSelectionProperty(compilerContext.getLogger(),
            "user.agent");
        // TODO(jat): more checks if we split up the safari permutation
        if ("safari".equals(prop.getCurrentValue())) {
          seenWebKit = true;
          break;
        }
      } catch (BadPropertyValueException e) {
        // if we couldn't get the property, assume this might be used on WebKit.
        seenWebKit = true;
        break;
      }
    }
    if (!seenWebKit) {
      return false;
    }
    MyVisitor v = new MyVisitor();
    v.accept(compilerContext.getJsProgram());
    return v.didChange();
  }
}
