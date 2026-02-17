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
 * switch/forin
 * super clinits:
 * if/for/while/do/conditional/short-circuit/switch/forin
 *
 * Several tests use an expression like "x + (clinit_A(), y) > 0" to avoid short-circuiting but
 * still allow for side effects so the clinit won't be moved out before the for condition.
 */
public class JsDuplicateClinitRemoverTest extends OptimizerTestBase {
  private static final String CLINIT_DECL = "function emptyFunc(){}" +
      "function clinit_A(){clinit_A = emptyFunc}" +
      "function clinit_B(){clinit_B = emptyFunc}" +
      "function clinit_C(){clinit_C = emptyFunc; clinit_B();}";

  public void testRemoveDupClinitsInBlock() throws Exception {
    optimize(CLINIT_DECL,
        "clinit_A();",
        "clinit_A();")
        .into(CLINIT_DECL,
        "clinit_A()");
  }

  public void ignore_testRemoveDupClinitsInExpr() throws Exception {
    optimize(CLINIT_DECL,
        "value = (clinit_A(),clinit_A(), 1);")
        .into(CLINIT_DECL,
        "value=(clinit_A(),1);");
  }

  public void testDupClinitsBlockAndExpr() throws Exception {
    optimize(CLINIT_DECL,
        "clinit_A();",
        "value = (clinit_A(),clinit_A(), 1);")
        .into(CLINIT_DECL,
            "clinit_A();",
            "value=1;");
    optimize(CLINIT_DECL,
        "value = (clinit_A(), 1);",
        "clinit_A();")
        .into(CLINIT_DECL,
            "value=(clinit_A(), 1);"
        );
  }

  public void testRemoveDupClinitsInIf() throws Exception {
    optimize(CLINIT_DECL,
        "clinit_A();",
        "if (cond1) { while(cond2) { b++; clinit_A(); } } else { clinit_A(); c(); }")
        .into(CLINIT_DECL,
            "clinit_A();",
            "if (cond1) { while(cond2) { b++; } } else { c(); }");

    verifyNoChange(CLINIT_DECL,
        "if (cond1) { while(cond2) { b++; clinit_A(); } } else { clinit_B(); c(); }",
        "clinit_A();",
        "clinit_B();"
    );

    verifyNoChange(CLINIT_DECL,
        "if (cond1) { while(cond2) { b++; clinit_A(); } } else { clinit_A(); c(); }",
        "clinit_A();");
  }

  public void testRemoveDupClinitsInWhile() throws Exception {
    optimize(CLINIT_DECL,
        "clinit_A();",
        "while(cond) { b++; clinit_A(); }")
        .into(CLINIT_DECL,
            "clinit_A();",
            "while(cond) { b++; }");

    verifyNoChange(CLINIT_DECL,
        "while(cond) { b++; clinit_A(); }",
        "clinit_A();");

    optimize(CLINIT_DECL,
        "clinit_A();",
        "while(x() + (clinit_A(), y) > 0) { b++; clinit_A(); }")
        .into(CLINIT_DECL,
            "clinit_A();",
            "while(x() + y > 0) { b++; }");

    optimize(CLINIT_DECL,
        "while(x() + (clinit_A(), y) > 0) { b++; }",
        "clinit_A();")
        .into(CLINIT_DECL,
            "while(x() + (clinit_A(), y) > 0) { b++; }");
  }

  public void testRemoveDupClinitsInFor() throws Exception {
    optimize(CLINIT_DECL,
        "clinit_A();",
        "for(;cond;){ b++; clinit_A(); }")
        .into(CLINIT_DECL,
            "clinit_A();",
            "for(;cond;){ b++; }");

    verifyNoChange(CLINIT_DECL,
        "for(;cond;){ b++; clinit_A(); }",
        "clinit_A();");

    optimize(CLINIT_DECL,
        "clinit_A();",
        "for (var a = x() + (clinit_A(), y); a < 10; a++) { b++; }")
        .into(CLINIT_DECL,
            "clinit_A();",
            "for (var a = x() + y; a < 10; a++) { b++; }");

    optimize(CLINIT_DECL,
        "for (var a = x() + (clinit_A(), y); a < 10; a++) { b++; }",
        "clinit_A();")
        .into(CLINIT_DECL,
            "for (var a = x() + (clinit_A(), y); a < 10; a++) { b++; }");

    optimize(CLINIT_DECL,
        "clinit_A();",
        "for(;x() + (clinit_A(), y) > 0;){ b++; }")
        .into(CLINIT_DECL,
            "clinit_A();",
            "for(;x() + y > 0;){ b++; }");

    optimize(CLINIT_DECL,
        "for(;x() + (clinit_A(), y) > 0;){ b++; }",
        "clinit_A();")
        .into(CLINIT_DECL,
            "for(;x() + (clinit_A(), y) > 0;){ b++; }");

    // Increment operation might not run, so can't remove later clinits
    verifyNoChange(CLINIT_DECL,
        "for(var i = 0; i < 10; i += x() + (clinit_A(), y)){ b++; clinit_A(); }",
        "clinit_A();");

    // but it need not run if we are sure the clinit already ran
    optimize(CLINIT_DECL,
        "clinit_A();",
        "for(var i = 0; i < 10; i += x() + (clinit_A(), y)){ b++; }")
        .into(CLINIT_DECL,
            "clinit_A();",
            "for(var i = 0; i < 10; i += x() + y){ b++; }"
            );
    optimize(CLINIT_DECL,
        "for(var i = x() + (clinit_A(), y); i < 10; i += x() + (clinit_A(), y)){ b++; }")
        .into(CLINIT_DECL,
            "for(var i = x() + (clinit_A(), y); i < 10; i += x() + y){ b++; }"
            );
    optimize(CLINIT_DECL,
        "for(var i = 0; i < (clinit_A(), 10); i += x() + (clinit_A(), y)){ b++; }")
        .into(CLINIT_DECL,
            "for(var i = 0; i < (clinit_A(), 10); i += x() + y){ b++; }"
            );
    // Fails, but increment always runs after the body (at least if there are no "continue"
    // statements).
    //    optimize(CLINIT_DECL,
    //        "for(var i = 0; i < 10; i += x() + (clinit_A(), y)){ b++; clinit_A(); }")
    //        .into(CLINIT_DECL,
    //            "for(var i = 0; i < 10; i += x() + y){ b++; clinit_A(); }"
    //            );
  }

  public void testRemoveDupClinitsInDo() throws Exception {
    optimize(CLINIT_DECL,
        "clinit_A();",
        "do { b++; clinit_A(); } while(cond);"
        )
        .into(CLINIT_DECL,
            "clinit_A();",
            "do { b++; } while(cond);");

    // Fails, but the body always runs once (at least if there are no "break"/"continue"
    // statements) before the condition or code after it.
    //    optimize(CLINIT_DECL,
    //        "do { b++; clinit_B(); } while(cond);",
    //        "clinit_B();"
    //        )
    //        .into(CLINIT_DECL,
    //            "do { b++; clinit_B(); } while(cond);");
    //    optimize(CLINIT_DECL,
    //        "do { b++; clinit_A(); } while(x() + (clinit_A(), y) > 0);"
    //    )
    //        .into(CLINIT_DECL,
    //            "do { b++; clinit_A(); } while(x() + y > 0);");

    optimize(CLINIT_DECL,
        "do { b++; } while(x() + y > (clinit_B(), z));",
        "clinit_B();"
    )
        .into(CLINIT_DECL,
            "do { b++; } while(x() + y > (clinit_B(), z));");

    optimize(CLINIT_DECL,
        "clinit_A();",
        "do { b++; } while(x() + y > (clinit_B(), z));"
    )
        .into(CLINIT_DECL,
            "clinit_A();",
            "do { b++; } while(x() + y > (clinit_B(), z));");
  }

  public void testRemoveDupClinitsInInvocation() throws Exception {
    optimize(CLINIT_DECL,
        "clinit_A();",
        "alert(a(), (clinit_A(),b()));"
    )
        .into(CLINIT_DECL,
            "clinit_A();",
            "alert(a(), b());");
    optimize(CLINIT_DECL,
        "alert(a(), (clinit_A(),b()));",
        "clinit_A();"
        )
        .into(CLINIT_DECL,
            "alert(a(), (clinit_A(),b()));");
  }
  public void testRemoveDupClinitsInConditionals() throws Exception {
    optimize(CLINIT_DECL,
        "clinit_A();",
        "alert((clinit_A(), cond) ? a() : b());"
    )
        .into(CLINIT_DECL,
            "clinit_A();",
            "alert(cond ? a() : b());");
    optimize(CLINIT_DECL,
        "alert((clinit_A(), cond) ? a() : b());",
        "clinit_A();"
        )
        .into(CLINIT_DECL,
            "alert((clinit_A(), cond) ? a() : b());");

    optimize(CLINIT_DECL,
        "clinit_A();",
        "alert(cond ? (clinit_A(), a()) : b());"
    )
        .into(CLINIT_DECL,
            "clinit_A();",
            "alert(cond ? a() : b());");
    verifyNoChange(CLINIT_DECL,
        "alert(cond ? (clinit_A(), a()) : b());",
        "clinit_A();"
        );

    optimize(CLINIT_DECL,
        "clinit_A();",
        "alert(cond ? a() : (clinit_A(), b()));"
    )
        .into(CLINIT_DECL,
            "clinit_A();",
            "alert(cond ? a() : b());");
    verifyNoChange(CLINIT_DECL,
        "alert(cond ? (clinit_A(), a()) : b());",
        "clinit_A();"
        );
  }

  public void testRemoveDupClinitsInBooleanOps() throws Exception {
    optimize(CLINIT_DECL,
        "clinit_A();",
        "alert((clinit_A(), cond1) && cond2);"
    )
        .into(CLINIT_DECL,
            "clinit_A();",
            "alert(cond1 && cond2);");
    optimize(CLINIT_DECL,
        "clinit_A();",
        "alert(cond1 && (clinit_A(), cond2));"
    )
        .into(CLINIT_DECL,
            "clinit_A();",
            "alert(cond1 && cond2);");
    optimize(CLINIT_DECL,
        "alert((clinit_A(), cond1) && (clinit_A(), cond2));"
    )
        .into(CLINIT_DECL,
            "alert((clinit_A(), cond1) && cond2);");

    optimize(CLINIT_DECL,
        "alert((clinit_A(), cond1) && (clinit_B(), cond2));",
        "clinit_A();",
        "clinit_B();"
    )
        .into(CLINIT_DECL,
            "alert((clinit_A(), cond1) && (clinit_B(), cond2));",
            "clinit_B();");

    optimize(CLINIT_DECL,
        "clinit_A();",
        "alert((clinit_A(), cond1) || (clinit_A(), cond2));"
    )
        .into(CLINIT_DECL,
            "clinit_A();",
            "alert(cond1 || cond2);");
    optimize(CLINIT_DECL,
        "alert((clinit_A(), cond1) || (clinit_B(), cond2));",
        "clinit_A();",
        "clinit_B();"
    )
        .into(CLINIT_DECL,
            "alert((clinit_A(), cond1) || (clinit_B(), cond2));",
            "clinit_B();");
  }

  protected void verifyNoChange(String... input) throws Exception {
    optimize(input).into(input);
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
      JsFunction clinitA = null;
      JsFunction clinitC = null;
      @Override
      public void endVisit(JsFunction x, JsContext ctx) {
        // Ensure the optimizer knows which methods are clinits, hierarchy
        if (x.getName().toString().startsWith("clinit_")) {
          x.markAsClinit();
          if (x.getName().toString().endsWith("C")) {
            clinitC = x;
          } else if (x.getName().toString().endsWith("A")) {
            clinitA = x;
          }
        }

        // Indicate that all methods were compiled from Java source
        x.setFromJava(true);

        // Provide a static ref for each function, as if it was from Java source
        JsName name = x.getName();
        if (name != null) {
          name.setStaticRef(x);
        }
      }

      @Override
      public void endVisit(JsProgram x, JsContext ctx) {
        assert clinitA != null && clinitC != null;
        clinitC.setSuperClinit(clinitA);
      }
    }.accept(program);
  }
}
