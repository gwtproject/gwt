/*
 * Copyright 2026 GWT Project Authors
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

import com.google.gwt.dev.js.ast.JsContext;
import com.google.gwt.dev.js.ast.JsFunction;
import com.google.gwt.dev.js.ast.JsModVisitor;
import com.google.gwt.dev.js.ast.JsName;
import com.google.gwt.dev.js.ast.JsProgram;

/**
 * Need tests for
 * super clinits
 * if/for/while/do/conditional/short-circuit/switch
 */
public class JsDuplicateClinitRemoverTest extends OptimizerTestBase {
  private static final String CLINIT_DECL = "function emptyFunc(){}" +
      "function clinit_A(){clinit_A = emptyFunc}";
  public void testRemoveDupClinitsInBlock() throws Exception {
    optimize(CLINIT_DECL,
        "clinit_A();",
        "clinit_A();")
        .into(CLINIT_DECL,
        "clinit_A()");
  }

  public void testRemoveDupClinitsInExpr() throws Exception {
    optimize(CLINIT_DECL,
        "value = (clinit_A(),clinit_A(), 1);")
        .into(CLINIT_DECL,
        "value=(clinit_A(),1);");
  }

  @Override
  protected void doOptimize(JsProgram program) {
    JsSymbolResolver.exec(program);
    DuplicateClinitRemover.exec(program);
    // Duplicate clinits are replaced by nulls, so we need to run static eval to remove them
    JsStaticEval.exec(program);
  }

  @Override
  protected void setupJsProgram(JsProgram program) {
    new JsModVisitor() {
      @Override
      public void endVisit(JsFunction x, JsContext ctx) {
        // Ensure the optimizer knows which methods are clinits
        if (x.getName().toString().startsWith("clinit_")) {
          x.markAsClinit();
        }

        // Indicate that all methods were compiled from Java source
        x.setFromJava(true);

        // Provide a static ref for each function, as if it was from Java source
        JsName name = x.getName();
        if (name != null) {
          name.setStaticRef(x);
        }
      }
    }.accept(program);
  }
}
