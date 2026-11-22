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

import com.google.gwt.dev.js.ast.JsProgram;

/**
 * Tests the JsStaticEval optimizer.
 */
public class JsStaticEvalTest extends OptimizerTestBase {

  public void testAddLiterals() throws Exception {
    optimize("alert(21+21);").into("alert(42);");
    optimize("alert('Hello '+'World');").into("alert('Hello World');");
    optimize("alert('Hello ' + 42);").into("alert('Hello 42');");
    optimize("alert(42 + ' Hello');").into("alert('42 Hello');");
    optimize("alert(42.0 + ' Hello');").into("alert('42 Hello');");
    optimize("alert(42.2 + ' Hello');").into("alert('42.2 Hello');");
    optimize("alert('Hello ' + 42.2);").into("alert('Hello 42.2');");
    optimize("alert(2004318071 + '');").into("alert('2004318071');");
  }

  public void testAssociativity() throws Exception {
    // This test method uses optimizeToSource, as the precedence of the "expected" source doesn't
    // even match itself after printing without a pass through JsStaticEval. That is, this test
    // would fail:
    // optimizeJs("alert(a||b||c)").into("alert(a||b||c);");

    // Simple test
    assertEquals("alert(a||b||c||d);", optimizeToSource("alert((a||b)||(c||d));"));
    assertEquals("alert(a||b||c||d||e||f);", optimizeToSource("alert((a||b)||(c||(d||(e||f))));"));
    assertEquals("alert(a&&b&&c&&d);", optimizeToSource("alert((a&&b)&&(c&&d));"));

    // Preserve precedence
    assertEquals("alert((a||b)&&(c||d));",
        optimizeToSource("alert((a || b) && (c || d));"));
    assertEquals("alert(a&&b||c&&d);",
        optimizeToSource("alert((a && b) || ( c && d));"));
    assertEquals("a(),b&&c();", optimizeToSource("a(), b && c()"));
    assertEquals("a(),c();", optimizeToSource("a() && b, c()"));

    // Don't damage math expressions
    assertEquals("alert(seconds/3600);",
        optimizeToSource("alert(seconds / (60 * 60))"));
    assertEquals("alert(seconds/60*60);",
        optimizeToSource("alert(seconds / 60 * 60)"));
    optimize("alert(1 - (1 - foo))").into("alert(1-(1-foo));");

    // Don't damage assignments
    assertEquals("alert((a=7,b=foo));",
        optimizeToSource("alert((a = 7, b = (bar, foo)))"));
    assertEquals("alert(1+(a='2')+3+4);",
        optimizeToSource("alert(1 + (a = '2') + 3 + 4);"));
    assertEquals("alert(1+(a='2')+7);",
        optimizeToSource("alert(1 + (a = '2') + (3 + 4));"));

    // Break comma expressions up
    assertEquals("alert((a(),b(),c(),d));",
        optimizeToSource("alert(((a(),b()),(c(),d)));"));
    assertEquals("alert((a(),b(),c(),d));",
        optimizeToSource("alert(((a(),b()),(c(),d)));"));
    // and remove expressions without side effects
    assertEquals("alert(d);", optimizeToSource("alert(((a,b),(c,d)));"));

    // Pattern of coercing a numeric add operation to a string
    optimize("alert('' + (a + b))").into("alert(''+(a+b));");

    // Tests involving numeric and string literals and identifiers
    assertEquals("alert(21+(1+$foo));",
        optimizeToSource("alert((20 + 1) + (1 + $foo));"));

    // These are also tricky, because $foo could be non-numeric
    assertEquals("alert($foo+1+21);", optimizeToSource("alert(($foo + 1) + (20 + 1));"));
    assertEquals("alert($bar+13+7+(2+$foo));",
        optimizeToSource("alert((($bar + (10 + 3)) + (2 + 5)) + (2 + $foo));"));

    // Without type info, there's nothing that can be done for this expr
    assertEquals("alert($foo+($bar+($baz+$quux)));",
        optimizeToSource("alert($foo + ($bar + ($baz + $quux)));"));
  }

  /**
   * Test for issue 7088. JsStatic eval infinite loop in
   * {@link JsStaticEval.StaticEvalVisitor#endVisit(com.google.gwt.dev.js.ast.JsBlock,
   * com.google.gwt.dev.js.ast.JsContext)}
   */
  public void testDeclareAfterReturn() throws Exception {
    // TODO(rluble):  Note that the source output has the wrong precedence for function definition
    // and application.
    optimize("(function(){return 0;{var a;var b}})();")
        .into("(function(){return 0;var a;var b}());");
  }

  public void testIfWithEmptyThen() throws Exception {
    optimize("if (a()) { }").into("a();");
  }

  public void testIfWithEmptyThenAndElseExpression() throws Exception {
    optimize("if (a()) { } else { b(); }").into("a()||b();");
  }

  public void testIfWithEmptyThenAndElse() throws Exception {
    optimize("if (a()) { } else { throw 1; }")
        .into("if(!a()){throw 1}");
  }

  public void testIfWithEmptyThenAndEmptyElse() throws Exception {
    optimize("if (a()) { } else { }").into("a();");
  }

  public void testIfWithThenAndEmptyElse() throws Exception {
    optimize("if (a()) { throw 1; } else { }").into("if(a()){throw 1}");
  }

  public void testIfWithThenExpressionAndEmptyElse() throws Exception {
    optimize("if (a()) { b() } else { }").into("a()&&b();");
  }

  public void testIfWithThenExpressionAndElseExpression() throws Exception {
    optimize("if (a()) { b() } else { c(); }").into("a()?b():c();");
  }

  public void testIfWithThenExpressionAndElseStatement() throws Exception {
    // This can't be optimized further at present
    optimize("if (a()) { b() } else { throw 1; }")
        .into("if(a()){b()}else{throw 1}");
  }

  public void testLiteralCompares() throws Exception {
    optimize("alert(2 != 2)").into("alert(false);");
    optimize("alert(2 == 3)").into("alert(false);");
    optimize("alert(2 == 2)").into("alert(true);");
    optimize("alert(2 != 3)").into("alert(true);");
    optimize("alert(2 < 3)").into("alert(true);");
    optimize("alert(3 <= 3)").into("alert(true);");
    optimize("alert(3 > 2)").into("alert(true);");
    optimize("alert(3 >= 3)").into("alert(true);");
    optimize("alert(2 > 3)").into("alert(false);");
    optimize("alert(2 >= 3)").into("alert(false);");
    optimize("alert(3 < 2)").into("alert(false);");
    optimize("alert(3 <= 2)").into("alert(false);");
    optimize("alert(1.8E+10308 < 1.9E+10308)").into("alert(false);");
    optimize("alert(1.8E+10308 > 1.9E+10308)").into("alert(false);");


    optimize("alert(\"a\" == \"a\")").into("alert(true);");
    optimize("alert(\"a\" === \"a\")").into("alert(true);");
    optimize("alert(\"a\" != \"b\")").into("alert(true);");
    optimize("alert(\"a\" !== \"b\")").into("alert(true);");
    optimize("alert(\"a\" != null)").into("alert(true);");
    optimize("alert(\"a\" !== null)").into("alert(true);");
  }

  public void testShortCircuitAnd() throws Exception {
    optimize("alert(true && a)").into("alert(a);");
    optimize("alert(false && a)").into("alert(false);");

    // these can't be simplified to maintain type
    optimize("alert(a && true)").into("alert(a&&true);");
    optimize("alert(a && false)").into("alert(a&&false);");
    optimize("alert(!!a && !!b)").into("alert(!!a&&!!b);");
    optimize("alert(c | (a, bits && 1))").into("alert(c|(bits&&1));");

    // in boolean context we can simplify more
    optimize("alert(!!a && !!b ? c :d)").into("alert(a&&b?c:d);");
    optimize("alert(false && !!b ? c :d)").into("alert(d);");
    optimize("alert(true && !!b ? c :d)").into("alert(b?c:d);");
    optimize("alert(b && true ? c :d1)").into("alert(b?c:d1);");
    optimize("alert(b && false ? c :d1)").into("alert(d1);");
    optimize("alert(a && false && b ? c :d2)").into("alert(d2);");
  }

  public void testShortCircuitAndWithSideEffects() throws Exception {
    optimize("!!a && !!b()").into("a&&b();");
    optimize("a() && false && c();").into("a();");
    optimize("a() && false && c() ? d() : e();").into("a(),e();");
    optimize("a() && true && c() ? d() : e();").into("a()&&c()?d():e();");
  }

  public void testSimplifyCommaInVoidContext() throws Exception {
    optimize("a()&&(b(),undefined)").into("a()&&b();");
    optimize("a()?(b(),undefined):(c(),undefined)").into("a()?b():c();");
  }

  public void testSimplifyComma() throws Exception {
    optimize("alert((true, !!a()))").into("alert(!!a());");
    optimize("alert((!!a(), true))").into("alert((a(),true));");
  }

  public void testShortCircuitOr() throws Exception {
    optimize("alert(true || a)").into("alert(true);");
    optimize("alert(false || a)").into("alert(a);");

    // these can't be simplified to maintain type
    optimize("alert(a || true)").into("alert(a||true);");
    optimize("alert(a || false)").into("alert(a||false);");
    optimize("alert(!!a || !!b)").into("alert(!!a||!!b);");
    optimize("alert(c | (a, bits || 0))").into("alert(c|(bits||0));");

    // in boolean context we can simplify more
    optimize("alert(!!a || !!b ? c :d)").into("alert(a||b?c:d);");
    optimize("alert(true || !!b ? c :d)").into("alert(c);");
    optimize("alert(false || !!b ? c :d)").into("alert(b?c:d);");
    optimize("alert(b || false ? c :d1)").into("alert(b?c:d1);");
    optimize("alert(b || true ? c1 :d)").into("alert(c1);");
    optimize("alert(a || true || b ? c2 :d)").into("alert(c2);");
  }

  public void testShortCircuitOrWithSideEffects() throws Exception {
    optimize("!!a || !!b()").into("a||b();");
    optimize("a() || true || c();").into("a();");
    optimize("a() || true || c() ? d() : e();").into("a(),d();");
    optimize("a() || false || c() ? d() : e();").into("a()||c()?d():e();");
  }

  public void testLiteralEqNull() throws Exception {
    optimize("alert('test' == null)").into("alert(false);");
  }

  public void testLiteralNeNull() throws Exception {
    optimize("alert('test' != null)").into("alert(true);");
  }

  public void testNullEqNull() throws Exception {
    optimize("alert(null == null)").into("alert(true);");
  }

  public void testNullNeNull() throws Exception {
    optimize("alert(null != null)").into("alert(false);");
  }


  @Override
  protected boolean doOptimize(JsProgram program) {
    JsSymbolResolver.exec(program);
    int changes = JsStaticEval.exec(program);
    if (changes != 0) {
      // Try one more time, to ensure that it correctly converged in a single run
      assertEquals(0, JsStaticEval.exec(program));
    }
    return changes != 0;
  }

  /**
   * Simplify (name = expr, name) to (name = expr), since the assignment expression already
   * evaluates to the assigned value.
   */
  public void testSimplifyCommaAssignmentReturn() throws Exception {
    // Assign a local name and use it
    optimize("function f() { var a; alert((a = foo(), a)); }")
            .into("function f(){var a;alert(a=foo())}");
    // Test with a name out of scope
    optimize("alert((a = foo(), a))").into("alert(a=foo());");

    // Confirm that we only use the same local (fails if resolver is not used)
    optimize("function f() { var a, b = 1; alert((a = foo(), b)); }")
            .into("function f(){var a,b=1;alert((a=foo(),b))}");
  }

  /**
   * Simplify (name op= expr, name) to (name op= expr) for any compound assignment, since the
   * compound assignment already evaluates to the updated value.
   */
  public void testSimplifyCommaCompoundAssignmentReturn() throws Exception {
    optimize("function f() { var a; alert((a += foo(), a)); }")
            .into("function f(){var a;alert(a+=foo())}\n");
    optimize("alert((a -= foo(), a))").into("alert(a-=foo());");
    optimize("alert((a |= foo(), a))").into("alert(a|=foo());");

    // Confirm that we only simplify when the read is the same local
    optimize("function f() { var a, b = 1; alert((a += foo(), b)); }")
            .into("function f(){var a,b=1;alert((a+=foo(),b))}\n");
  }

  public void testSimplifyCommaIncDecReturn() throws Exception {
    // Prefix is returned as-is
    optimize("function f() { var a; alert((++a, a)); }").into("function f(){var a;alert(++a)}\n");
    optimize("alert((--a, a))").into("alert(--a);");

    // Postfix is rewritten to the equivalent prefix form
    optimize("function f() { var a; alert((a++, a)); }").into("function f(){var a;alert(++a)}\n");
    optimize("alert((a--, a))").into("alert(--a);");

    // Confirm that we only simplify when the read is the same local
    optimize("function f() { var a, b = 1; alert((a++, b)); }")
            .into("function f(){var a,b=1;alert((a++,b))}\n");

    // Confirm postfix exprs are left alone, even in a comma expr
    optimize("alert(a++)").into("alert(a++);");
    optimize("alert((foo(), a++))").into("alert((foo(),a++));");

    // In theory this could be optimized, but it doesn't match the known pattern. If we
    // fill this in later, it would be through some expression inliner
    optimize("alert((g(), a++, a))").into("alert((g(),a++,a));");
  }

  /**
   * Delete evaluates to a boolean, the only unary that can't be cleaned up like this.
   */
  public void testDoNotSimplifyCommaDelete() throws Exception {
    optimize("alert((delete a, a))").into("alert((delete a,a));");
  }

}
