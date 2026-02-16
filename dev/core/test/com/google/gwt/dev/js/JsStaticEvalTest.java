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
    assertEquals("a()&&b,c();", optimizeToSource("a() && b, c()"));

    // Don't damage math expressions
    assertEquals("alert(seconds/3600);",
        optimizeToSource("alert(seconds / (60 * 60))"));
    assertEquals("alert(seconds/60*60);",
        optimizeToSource("alert(seconds / 60 * 60)"));
    optimize("alert(1 - (1 - foo))").into("alert(1-(1-foo));");

    // Don't damage assignments
    assertEquals("alert((a=0,b=foo));",
        optimizeToSource("alert((a = 0, b = (bar, foo)))"));
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
   * {@link JsStaticEval.StaticEvalVisitor#endVisit(JsBlock, JsContext)}
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
  protected void doOptimize(JsProgram program) {
    JsStaticEval.exec(program);
  }
}
