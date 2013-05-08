/*
 * Copyright 2013 Google Inc.
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
package com.google.gwt.dev.jjs.impl;

import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.dev.javac.testing.impl.MockJavaResource;
import com.google.gwt.dev.jjs.ast.JBlock;
import com.google.gwt.dev.jjs.ast.JExpression;
import com.google.gwt.dev.jjs.ast.JMethod;
import com.google.gwt.dev.jjs.ast.JMethodBody;
import com.google.gwt.dev.jjs.ast.JProgram;
import com.google.gwt.dev.jjs.ast.JReturnStatement;
import com.google.gwt.dev.resource.Resource;
import com.google.gwt.dev.util.arg.OptionSource;

/**
 * Tests that {@link GwtAstBuilder} correctly builds the AST for features introduced in Java 7.
 */
public class Java7AstTest extends JJSTestBase {

  @Override
  public void setUp() {
    sourceLevel = OptionSource.SourceLevel._7;
    addAll(LIST_T, ARRAYLIST_T, JAVA_LANG_AUTOCLOSEABLE, TEST_RESOURCE, EXCEPTION1, EXCEPTION2);
  }

  public void testCompileNewStyleLiterals() throws Exception {
    assertEqualExpression("int", "10000000", "1_000_0000");
    assertEqualExpression("int", "5", "0b101");
    assertEqualExpression("int", "6", "0B110");
  }

  public void testCompileStringSwitch() throws Exception {
    assertEqualBlock(
        "String input = \"\";" +
        "switch (input) {" +
        "  case \"AA\": break;" +
        "  case \"BB\": break;" +
        "}",
        "String input = \"\";" +
        "switch (input) {" +
        "  case \"AA\": break;" +
        "  case \"BB\": break;" +
        "}");
  }


  public void testCompileDiamondOperator() throws Exception {
    addSnippetImport("com.google.gwt.List");
    addSnippetImport("com.google.gwt.ArrayList");
    assertEqualBlock(
        "List l = new ArrayList();",
        "List<String> l = new ArrayList<>();");
  }

  public void testCompileTryWithResources() throws Exception {
    addSnippetImport("java.lang.AutoCloseable");
    addSnippetImport("com.google.gwt.TestResource");
    // TODO(rluble): Temp variable numbering when building try-with-resource statements in
    // GwtAstBuilder might make this test brittle.
    assertEqualBlock(""
        + "try { "
        + "  final TestResource r1 = new TestResource(); "
        + "  Throwable $primary_ex_2 = null; "
        + "  try { "
        + "  } catch (Throwable $caught_ex_3) { "
        + "    $primary_ex_2 = $caught_ex_3;"
        + "    throw $primary_ex_2;"
        + "  } finally {"
        + "    $primary_ex_2 = Exceptions.safeClose(r1, $primary_ex_2);"
        + "    if ($primary_ex_2 != null)"
        + "          throw $primary_ex_2;"
        + "  }"
        + "}",
        "try (TestResource r1 = new TestResource(); ) { }");
    assertEqualBlock(""
        + "try { "
        + "  final TestResource r1 = new TestResource(); "
        + "  final TestResource r2 = new TestResource(); "
        + "  Throwable $primary_ex_3 = null; "
        + "  try { "
        + "  } catch (Throwable $caught_ex_4) { "
        + "    $primary_ex_3 = $caught_ex_4;"
        + "    throw $primary_ex_3;"
        + "  } finally {"
        + "    $primary_ex_3 = Exceptions.safeClose(r2, $primary_ex_3);"
        + "    $primary_ex_3 = Exceptions.safeClose(r1, $primary_ex_3);"
        + "    if ($primary_ex_3 != null)"
        + "          throw $primary_ex_3;"
        + "  }"
        + "}",
        "try (TestResource r1 = new TestResource(); TestResource r2 = new TestResource();) { }");
  }


  public void testCompileMultiExceptions() throws Exception {
    addSnippetImport("com.google.gwt.Exception1");
    addSnippetImport("com.google.gwt.Exception2");
    assertEqualBlock(""
        + "int i = 0;"
        + "try {"
        + "  if (i == 0) {"
        + "    throw new Exception1(); "
        + "  } else {"
        + "    throw new Exception2();"
        + "  }"
        + "} catch(Exception1 | Exception2 e) {"
        + "}", ""
        + "int i = 0;"
        + "try {"
        + "  if (i == 0) {"
        + "    throw new Exception1(); "
        + "  } else {"
        + "    throw new Exception2();"
        + "  }"
        + "} catch(Exception1 | Exception2 e) {"
        + "}");
  }

  public static final MockJavaResource INTEGERLITERALS = new MockJavaResource(
      "com.google.gwt.IntegerLiterals") {
    @Override
    public CharSequence getContent() {
      StringBuilder code = new StringBuilder();
      code.append("package com.google.gwt;\n");
      code.append("public class IntegerLiterals {\n");
      code.append("  int million = 1_000_000;\n");
      code.append("}\n");
      return code;
    }
  };

  public static final MockJavaResource STRINGSWITCHTEST =
      new MockJavaResource("com.google.gwt.StringSwitchTest") {
    @Override
    public CharSequence getContent() {
      StringBuilder code = new StringBuilder();
      code.append("package com.google.gwt;\n");
      code.append("public class StringSwitchTest {\n");
      code.append("  int test() { \n");
      code.append("               int result = 0;");
      code.append("               String f = \"AA\";");
      code.append("               switch(f) {");
      code.append("               case \"CC\": result = - 1; break;");
      code.append("               case \"BB\": result = 1;");
      code.append("               case \"AA\": result = result + 1; break;");
      code.append("               default: result = -2; break;");
      code.append("               }  \n");
      code.append("  return result; \n");
      code.append("  }  \n");
      code.append("}\n");
      return code;
    }
  };

  public static final MockJavaResource LIST_T = new MockJavaResource(
      "com.google.gwt.List") {
    @Override
    public CharSequence getContent() {
      StringBuilder code = new StringBuilder();
      code.append("package com.google.gwt;\n");
      code.append("public interface List<T> {\n");
      code.append("  T method1(); \n");
      code.append("}\n");
      return code;
    }
  };

  public static final MockJavaResource ARRAYLIST_T = new MockJavaResource(
      "com.google.gwt.ArrayList") {
    @Override
    public CharSequence getContent() {
      StringBuilder code = new StringBuilder();
      code.append("package com.google.gwt;\n");
      code.append("import com.google.gwt.List;\n");
      code.append("public class ArrayList<T> implements List<T> {\n");
      code.append("  public T method1() { return null; } \n");
      code.append("}\n");
      return code;
    }
  };

  public static final MockJavaResource JAVA_LANG_AUTOCLOSEABLE = new MockJavaResource(
      "java.lang.AutoCloseable") {
    @Override
    public CharSequence getContent() {
      StringBuilder code = new StringBuilder();
      code.append("package java.lang;\n");
      code.append("import java.lang.Exception;\n");
      code.append("public interface AutoCloseable {\n");
      code.append("  void close() throws Exception; \n");
      code.append("}\n");
      return code;
    }
  };


  public static final MockJavaResource TEST_RESOURCE = new MockJavaResource(
      "com.google.gwt.TestResource") {
    @Override
    public CharSequence getContent() {
      StringBuilder code = new StringBuilder();
      code.append("package com.google.gwt;\n");
      code.append("public class TestResource implements AutoCloseable {\n");
      code.append("  public void close() { } \n");
      code.append("}\n");
      return code;
    }
  };

  public static final MockJavaResource EXCEPTION1 = new MockJavaResource(
      "com.google.gwt.Exception1") {
    @Override
    public CharSequence getContent() {
      StringBuilder code = new StringBuilder();
      code.append("package com.google.gwt;\n");
      code.append("public class Exception1 extends Exception {\n");
      code.append("}\n");
      return code;
    }
  };

  public static final MockJavaResource EXCEPTION2 = new MockJavaResource(
      "com.google.gwt.Exception2") {
    @Override
    public CharSequence getContent() {
      StringBuilder code = new StringBuilder();
      code.append("package com.google.gwt;\n");
      code.append("public class Exception2 extends Exception {\n");
      code.append("}\n");
      return code;
    }
  };

  private void addAll(Resource... sourceFiles) {
    for (Resource sourceFile : sourceFiles) {
      sourceOracle.addOrReplace(sourceFile);
    }
  }

  private void assertEqualExpression(String type, String expected , String expression)
      throws UnableToCompleteException {
    JExpression testExpresssion = getExpression(type, expression);
    assertEquals(expected, testExpresssion.toSource());
  }

  private JExpression getExpression(String type, String expression)
      throws UnableToCompleteException {
    JProgram program = compileSnippet(type, "return " + expression + ";");
    JMethod mainMethod = findMainMethod(program);
    JMethodBody body = (JMethodBody) mainMethod.getBody();
    JReturnStatement returnStmt = (JReturnStatement) body.getStatements().get(0);
    return  returnStmt.getExpr();
  }
  private void assertEqualBlock(String expected , String input)
      throws UnableToCompleteException {
    JBlock testExpression = getStatement(input);
    assertEquals(
        ("{ " + expected + "}").replaceAll("\\s+", " ")
        .replaceAll("\\s([\\p{Punct}&&[^$]])", "$1")
        .replaceAll("([\\p{Punct}&&[^$]])\\s", "$1"),
        testExpression.toSource().replaceAll("\\s+", " ")
        .replaceAll("\\s([\\p{Punct}&&[^$]])", "$1")
        .replaceAll("([\\p{Punct}&&[^$]])\\s", "$1"));
  }

  private JBlock getStatement(String statement)
      throws UnableToCompleteException {
    JProgram program = compileSnippet("void", statement);
    JMethod mainMethod = findMainMethod(program);
    JMethodBody body = (JMethodBody) mainMethod.getBody();
    return body.getBlock();
  }
}
