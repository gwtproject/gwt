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
 * Validate that clinits are removed when they are guaranteed to, have been run, both by checking
 * flow control and by knowing the clinit hierarchy.
 */
public class JsDuplicateClinitRemoverTest extends OptimizerTestBase {
  private static final String CLINIT_DECL = """
      function emptyFunc(){}
      function clinit_A(){clinit_A = emptyFunc}
      function clinit_B(){clinit_B = emptyFunc}
      function clinit_C(){clinit_C = emptyFunc; clinit_B();}
      """;

  public void testRemoveDupClinitsInBlock() throws Exception {
    optimize(CLINIT_DECL,
        """
            clinit_A();
            clinit_A();
            """)
        .into(CLINIT_DECL,
        "clinit_A()");

    // Ensure each declared clinit is recognized as a clinit, not just clinit_A
    optimize(CLINIT_DECL,
        """
            clinit_B();
            clinit_B();
            """)
        .into(CLINIT_DECL,
        "clinit_B()");
  }

  public void testRemoveDupClinitsInExpr() throws Exception {
    optimize(CLINIT_DECL,
        "value = (clinit_A(),clinit_A(), 1);")
        .into(CLINIT_DECL,
        "value = (clinit_A(), 1);");
  }

  public void testDupClinitsBlockAndExpr() throws Exception {
    optimize(CLINIT_DECL,
        """
            clinit_A();
            value = (clinit_A(), clinit_A(), 1);
            """)
        .into(CLINIT_DECL,
            """
                clinit_A();
                value = 1;
                """);
    optimize(CLINIT_DECL,
        """
            value = (clinit_A(), 1);
            clinit_A();
            """)
        .into(CLINIT_DECL,
            "value = (clinit_A(), 1);"
        );
  }

  public void testRemoveDupClinitsInIf() throws Exception {
    optimize(CLINIT_DECL,
        """
            clinit_A();
            if (cond1) {
              while (cond2) {
                b++;
                clinit_A();
              }
            } else {
              clinit_A();
              c();
            }
            """)
        .into(CLINIT_DECL,
            """
                clinit_A();
                if (cond1) {
                  while (cond2) {
                    b++;
                  }
                } else {
                  c();
                }
                """);

    verifyNoChange(CLINIT_DECL,
        """
            if (cond1) {
              while (cond2) {
                b++;
                clinit_A();
              }
            } else {
              clinit_B();
              c();
            }
            clinit_A();
            clinit_B();
            """
    );

    verifyNoChange(CLINIT_DECL,
        """
            if (cond1) {
              while (cond2) {
                b++;
                clinit_A();
              }
            } else {
              clinit_A();
              c();
            }
            clinit_A();
            """);
  }

  public void testRemoveDupClinitsInWhile() throws Exception {
    optimize(CLINIT_DECL,
        """
            clinit_A();
            while (cond) {
              b++;
              clinit_A();
            }
            """)
        .into(CLINIT_DECL,
            """
                clinit_A();
                while (cond) {
                  b++;
                }
                """);

    verifyNoChange(CLINIT_DECL,
        """
            while (cond) {
              b++;
              clinit_A();
            }
            clinit_A();
            """);

    optimize(CLINIT_DECL,
        """
            clinit_A();
            while (x() + (clinit_A(), y) > 0) {
              b++;
              clinit_A();
            }
            """)
        .into(CLINIT_DECL,
            """
                clinit_A();
                while (x() + y > 0) {
                  b++;
                }
                """);

    optimize(CLINIT_DECL,
        """
            while (x() + (clinit_A(), y) > 0) {
              b++;
            }
            clinit_A();
            """)
        .into(CLINIT_DECL,
            """
                while (x() + (clinit_A(), y) > 0) {
                  b++;
                }
                """);
  }

  public void testRemoveDupClinitsInFor() throws Exception {
    optimize(CLINIT_DECL,
        """
            clinit_A();
            for (;cond;) {
              b++;
              clinit_A();
            }
            """)
        .into(CLINIT_DECL,
            """
                clinit_A();
                for (;cond;) {
                  b++;
                }
                """);

    verifyNoChange(CLINIT_DECL,
        """
            for (;cond;) {
              b++;
              clinit_A();
            }
            clinit_A();
            """);

    optimize(CLINIT_DECL,
        """
            clinit_A();
            for (var a = x() + (clinit_A(), y); a < 10; a++) {
              b++;
            }
            """)
        .into(CLINIT_DECL,
            """
                clinit_A();
                for (var a = x() + y; a < 10; a++) {
                  b++;
                }
                """);

    optimize(CLINIT_DECL,
        """
            for (var a = x() + (clinit_A(), y); a < 10; a++) {
              b++;
            }
            clinit_A();
            """)
        .into(CLINIT_DECL,
            """
                for (var a = x() + (clinit_A(), y); a < 10; a++) {
                  b++;
                }
                """);

    optimize(CLINIT_DECL,
        """
            clinit_A();
            for (;x() + (clinit_A(), y) > 0;) {
              b++;
            }
            """)
        .into(CLINIT_DECL,
            """
                clinit_A();
                for (;x() + y > 0;) {
                  b++;
                }
                """);

    optimize(CLINIT_DECL,
        """
            for (;x() + (clinit_A(), y) > 0;) {
              b++;
            }
            clinit_A();
            """)
        .into(CLINIT_DECL,
            """
                for (;x() + (clinit_A(), y) > 0;) {
                  b++;
                }
                """);

    // Increment operation might not run, so can't remove later clinits
    verifyNoChange(CLINIT_DECL,
        """
            for (var i = 0; i < 10; i += x() + (clinit_A(), y)) {
              b++;
              clinit_A();
            }
            clinit_A();
            """);

    // but it need not run if we are sure the clinit already ran
    optimize(CLINIT_DECL,
        """
            clinit_A();
            for (var i = 0; i < 10; i += x() + (clinit_A(), y)) {
              b++;
            }
            """)
        .into(CLINIT_DECL,
            """
                clinit_A();
                for (var i = 0; i < 10; i += x() + y) {
                  b++;
                }
                """
            );
    optimize(CLINIT_DECL,
        """
            for (var i = x() + (clinit_A(), y); i < 10; i += x() + (clinit_A(), y)) {
              b++;
            }
            """)
        .into(CLINIT_DECL,
            """
                for (var i = x() + (clinit_A(), y); i < 10; i += x() + y) {
                  b++;
                }
                """
            );
    optimize(CLINIT_DECL,
        """
            for (var i = 0; i < (clinit_A(), 10); i += x() + (clinit_A(), y)) {
              b++;
            }
            """)
        .into(CLINIT_DECL,
            """
                for (var i = 0; i < (clinit_A(), 10); i += x() + y) {
                  b++;
                }
                """
            );

    // Fails, but increment always runs after the body (at least if there are no "continue"
    // statements).
    //    optimize(CLINIT_DECL,
    //        "for(var i = 0; i < 10; i += x() + (clinit_A(), y)){ b++; clinit_A(); }")
    //        .into(CLINIT_DECL,
    //            "for(var i = 0; i < 10; i += x() + y){ b++; clinit_A(); }"
    //            );
    // For now, asserting that we can't improve these cases:
    optimize(CLINIT_DECL,
        """
            for (var i = 0; i < 10; i += x() + (clinit_A(), y)) {
              b++;
              clinit_A();
            }
            """)
        .noChange();
  }

  public void testRemoveDupClinitsInDo() throws Exception {
    optimize(CLINIT_DECL,
        """
            clinit_A();
            do {
              b++;
              clinit_A();
            } while (cond);
            """
        )
        .into(CLINIT_DECL,
            """
                clinit_A();
                do {
                  b++;
                } while (cond);
                """);

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
    // For now, asserting that we can't improve these cases:
        optimize(CLINIT_DECL,
            """
                do {
                  b++;
                  clinit_B();
                } while (cond);
                clinit_B();
                """
            ).noChange();
        optimize(CLINIT_DECL,
            """
                do {
                  b++;
                  clinit_A();
                } while (x() + (clinit_A(), y) > 0);
                """
        ).noChange();

    optimize(CLINIT_DECL,
        """
            do {
              b++;
            } while (x() + y > (clinit_B(), z));
            clinit_B();
            """
    )
        .noChange();

    optimize(CLINIT_DECL,
        """
            clinit_A();
            do {
              b++;
            } while (x() + y > (clinit_B(), z));
            """
    )
        .into(CLINIT_DECL,
            """
                clinit_A();
                do {
                  b++;
                } while (x() + y > (clinit_B(), z));
                """);
  }

  public void testKeepClinitsAroundTry() throws Exception {
    // Ensure we're careful with exceptions as flow control to avoid a clinit

    // Can't guarantee that the clinit will run before any exception
    optimize(CLINIT_DECL, """
        try {
          a();
          clinit_A();
        } catch (e) {
          clinit_A();
        }
        """).noChange();

    // Can't guarantee that the catch will be run
    optimize(CLINIT_DECL, """
        try {
          a();
        } catch (e) {
          clinit_A();
        }
        clinit_A();
        """).noChange();

    // try might not complete and catch won't run the clinit, and catch returns control to surrounding block
    optimize(CLINIT_DECL, """
        try {
          a();
          clinit_A();
        } catch (e) {
          b();
        }
        clinit_A();
        """).noChange();

    // These cases can be improved but aren't today
    // Both try and catch run the clinit as their last operation
    optimize(CLINIT_DECL, """
        try {
          a();
          clinit_A();
        } catch (e) {
          b();
          clinit_A();
        }
        clinit_A();
        """).noChange();
    // catch unconditionally returns so the clinit must have run in try
    optimize(CLINIT_DECL, """
        try {
          a();
          clinit_A();
        } catch (e) {
          throw e;
        }
        clinit_A();
        """).noChange();
  }

  public void testRemoveDupClinitsInInvocation() throws Exception {
    optimize(CLINIT_DECL,
        """
            clinit_A();
            alert(a(), (clinit_A(), b()));
            """
    )
        .into(CLINIT_DECL,
            """
                clinit_A();
                alert(a(), b());
                """);
    optimize(CLINIT_DECL,
        """
            alert(a(), (clinit_A(), b()));
            clinit_A();
            """
        )
        .into(CLINIT_DECL,
            "alert(a(), (clinit_A(), b()));");
  }
  public void testRemoveDupClinitsInConditionals() throws Exception {
    optimize(CLINIT_DECL,
        """
            clinit_A();
            alert((clinit_A(), cond) ? a() : b());
            """
    )
        .into(CLINIT_DECL,
            """
                clinit_A();
                alert(cond ? a() : b());
                """);
    optimize(CLINIT_DECL,
        """
            alert((clinit_A(), cond) ? a() : b());
            clinit_A();
            """
        )
        .into(CLINIT_DECL,
            "alert((clinit_A(), cond ? a() : b()));");

    optimize(CLINIT_DECL,
        """
            clinit_A();
            alert(cond ? (clinit_A(), a()) : b());
            """
    )
        .into(CLINIT_DECL,
            """
                clinit_A();
                alert(cond ? a() : b());
                """);
    verifyNoChange(CLINIT_DECL,
        """
            alert(cond ? (clinit_A(), a()) : b());
            clinit_A();
            """
        );

    optimize(CLINIT_DECL,
        """
            clinit_A();
            alert(cond ? a() : (clinit_A(), b()));
            """
    )
        .into(CLINIT_DECL,
            """
                clinit_A();
                alert(cond ? a() : b());
                """);
    verifyNoChange(CLINIT_DECL,
        """
            alert(cond ? (clinit_A(), a()) : b());
            clinit_A();
            """
        );
  }

  public void testRemoveDupClinitsInBooleanOps() throws Exception {
    optimize(CLINIT_DECL,
        """
            clinit_A();
            alert((clinit_A(), cond1) && cond2);
            """
    )
        .into(CLINIT_DECL,
            """
                clinit_A();
                alert(cond1 && cond2);
                """);
    optimize(CLINIT_DECL,
        """
            clinit_A();
            alert(cond1 && (clinit_A(), cond2));
            """
    )
        .into(CLINIT_DECL,
            """
                clinit_A();
                alert(cond1 && cond2);
                """);
    optimize(CLINIT_DECL,
        "alert((clinit_A(), cond1) && (clinit_A(), cond2));"
    )
        .into(CLINIT_DECL,
            "alert((clinit_A(), cond1) && cond2);");

    optimize(CLINIT_DECL,
        """
            alert((clinit_A(), cond1) && (clinit_B(), cond2));
            clinit_A();
            clinit_B();
            """
    )
        .into(CLINIT_DECL,
            """
                alert((clinit_A(), cond1) && (clinit_B(), cond2));
                clinit_B();
                """);

    optimize(CLINIT_DECL,
        """
            clinit_A();
            alert((clinit_A(), cond1) || (clinit_A(), cond2));
            """
    )
        .into(CLINIT_DECL,
            """
                clinit_A();
                alert(cond1 || cond2);
                """);
    optimize(CLINIT_DECL,
        """
            alert((clinit_A(), cond1) || (clinit_B(), cond2));
            clinit_A();
            clinit_B();
            """
    )
        .into(CLINIT_DECL,
            """
                alert((clinit_A(), cond1) || (clinit_B(), cond2));
                clinit_B();
                """);
  }

  public void testRemoveClinitsInMultiExprs() throws Exception {
    optimize(CLINIT_DECL,
        "var val = (clinit_A(), cond ? (clinit_A(), a()) : (clinit_A(), b()));")
        .into(CLINIT_DECL, "var val = (clinit_A(), cond ? a() : b());");
  }

  public void testRemoveDupClinitsInSwitch() throws Exception {
    optimize(CLINIT_DECL,
        """
            clinit_A();
            switch (x) {
              case 1:
                clinit_A();
                a();
                break;
              default:
                clinit_A();
                b();
            }""")
        .into(CLINIT_DECL,
            """
                clinit_A();
                switch (x) {
                  case 1:
                    a();
                    break;
                  default:
                    b();
                }
                """);

    // Expression is always run before any case
    optimize(CLINIT_DECL,
        """
            switch (x + (clinit_A(), y)) {
              case 1:
                a();
                break;
            }
            clinit_A();
            """)
        .into(CLINIT_DECL,
            """
                switch (x + (clinit_A(), y)) {
                  case 1:
                    a();
                    break;
                }
                """);

    // Cases run independently, and we don't check fallthrough
    verifyNoChange(CLINIT_DECL,
        """
            switch (x) {
              case 1:
                clinit_A();
                break;
              case 2:
                clinit_A();
              default:
                clinit_A();
            }
            clinit_A();
            """);
  }

  public void testRemoveDupClinitsInForIn() throws Exception {
    optimize(CLINIT_DECL,
        """
            clinit_A();
            for (var k in obj) {
              b++;
              clinit_A();
            }
            """)
        .into(CLINIT_DECL,
            """
                clinit_A();
                for (var k in obj) {
                  b++;
                }
                """);

    optimize(CLINIT_DECL,
        """
            clinit_A();
            for (var k in (clinit_A(), obj)) {
              b++;
            }
            """)
        .into(CLINIT_DECL,
            """
                clinit_A();
                for (var k in obj) {
                  b++;
                }
                """);

    verifyNoChange(CLINIT_DECL,
        """
            for (var k in obj) {
              b++;
              clinit_A();
            }
            clinit_A();
            """);
  }

  public void testRemoveSuperClinits() throws Exception {
    // C always runs A, so we can remove A if C is present
    optimize(CLINIT_DECL,
        """
            clinit_C();
            clinit_A();
            """)
        .into(CLINIT_DECL,
            "clinit_C();");

    // Don't reorder though, A before C won't run A again, but still runs C
    verifyNoChange(CLINIT_DECL,
        """
            clinit_A();
            clinit_C();
            """);

    // Unrelated clinits, no impact
    verifyNoChange(CLINIT_DECL,
        """
            clinit_C();
            clinit_B();
            """);

    // Simple test showing that this also works anywhere else a duplicate could be removed
    optimize(CLINIT_DECL,
        """
            clinit_C();
            if (cond) {
              clinit_A();
            }
            """)
        .into(CLINIT_DECL,
            """
                clinit_C();
                cond;
                """);
    optimize(CLINIT_DECL,
        """
            clinit_C();
            alert((clinit_A(), cond1) && cond2);
            """)
        .into(CLINIT_DECL,
            """
                clinit_C();
                alert(cond1 && cond2);
                """);

    // ...but not vice versa
    optimize(CLINIT_DECL,
        """
            clinit_A();
            if (cond) {
              clinit_C();
            }
            """)
        .noChange();
  }

  protected void verifyNoChange(String... input) throws Exception {
    optimize(input).into(input);
  }

  @Override
  protected boolean doOptimize(JsProgram program) {
    JsSymbolResolver.exec(program);
    int changes = JsDuplicateClinitRemover.exec(program);
    // Duplicate clinits are replaced by nulls, so we need to run static eval to remove them
    JsStaticEval.exec(program);
    return changes != 0;
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
