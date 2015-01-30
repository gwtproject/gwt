/*
 * Copyright 2015 Google Inc.
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

import com.google.gwt.dev.jjs.ast.JMethod;
import com.google.gwt.dev.jjs.ast.JProgram;

/**
 * Test for {@link RedundantCastRemover}.
 */
public class RedundantCastRemoverTest extends OptimizerTestBase {
  @Override
  protected void setUp() throws Exception {
    runTypeTightener = true;
    addSnippetClassDecl("static class A { public int fun() {return 0;} }");
    addSnippetClassDecl("static class B extends A { }");
    addSnippetClassDecl("static class C extends B { }");
    addSnippetClassDecl("static class D extends B { }");
    addSnippetClassDecl("static class E extends B { }");
  }

  public void testNoChange() throws Exception {
    optimize("void",
        "A a = new A();",
        "if (a != null) {",
        "  ((B)a).fun();",
        "}",
        "if (a == null) {",
        "  ((C)a).fun();",
        "}")
        .intoString("EntryPoint$A a = new EntryPoint$A();\n" +
            "if (a != null) {\n" +
            "  ((EntryPoint$B) a).fun();\n" +
            "}\n" +
            "if (a == null) {\n" +
            "  ((EntryPoint$C) a).fun();\n" +
            "}");
  }

  public void testConditions() throws Exception {
    optimize("void",
        "A a = new A();",
        "boolean flag = false;",
        "if (a != null && a instanceof B) {",
        "  ((B)a).fun();",
        "}",
        "if (a != null || a instanceof B) {",
        "  ((B)a).fun();",
        "}",
        "if ((a != null && a instanceof B) || a instanceof B) {",
        "  ((B)a).fun();",
        "}",
        "if (flag = a instanceof B) {",
        "  ((B)a).fun();",
        "  ((C)a).fun();",
        "}"
        )
        .intoString("EntryPoint$A a = new EntryPoint$A();\n" +
            "boolean flag = false;\n" +
            "if (a != null && a instanceof EntryPoint$B) {\n" +
            "  a.fun();\n" +
            "}\n" +
            "if (a != null || a instanceof EntryPoint$B) {\n" +
            "  ((EntryPoint$B) a).fun();\n" +
            "}\n" +
            "if (a != null && a instanceof EntryPoint$B || a instanceof EntryPoint$B) {\n" +
            "  a.fun();\n" +
            "}\n" +
            "if (flag = a instanceof EntryPoint$B) {\n" +
            "  a.fun();\n" +
            "  ((EntryPoint$C) a).fun();\n" +
            "}");
  }

  public void testMaybeNullType() throws Exception {
    optimize("void",
        "A a = new A();",
        "a = null;",
        "if (a instanceof B) {",
        "  ((B)a).fun();",
        "  ((C)a).fun();",
        "}",
        "((B)a).fun();",
        "((C)a).fun();")
        .intoString("EntryPoint$A a = new EntryPoint$A();\n" +
            "a = null;\n" +
            "if (a instanceof EntryPoint$B) {\n" +
            "  ((EntryPoint$B) a).fun();\n" +
            "  ((EntryPoint$C) a).fun();\n" +
            "}\n" +
            "((EntryPoint$B) a).fun();\n" +
            "((EntryPoint$C) a).fun();");
  }

  public void testSingleConditional() throws Exception {
    optimize("void",
        "A a = new A();",
        "int result = 0;",
        "result = (a instanceof B) ? ((B)a).fun() : ((C)a).fun();",
        "result = (a instanceof B) ? ((C)a).fun() : ((B)a).fun();",
        "((B)a).fun();",
        "((C)a).fun();")
        .intoString("EntryPoint$A a = new EntryPoint$A();\n" +
            "int result = 0;\n" +
            "result = a instanceof EntryPoint$B ? a.fun() : ((EntryPoint$C) a).fun();\n" +
            "result = a instanceof EntryPoint$B ? ((EntryPoint$C) a).fun() : ((EntryPoint$B) a).fun();\n" +
            "((EntryPoint$B) a).fun();\n" +
            "((EntryPoint$C) a).fun();");
  }

  public void testSingleIf() throws Exception {
    optimize("void",
        "A a = new A();",
        "if (a instanceof B) {",
        "  ((B)a).fun();",
        "  ((C)a).fun();",
        "}",
        "((B)a).fun();",
        "((C)a).fun();")
        .intoString("EntryPoint$A a = new EntryPoint$A();\n" +
            "if (a instanceof EntryPoint$B) {\n" +
            "  a.fun();\n" +
            "  ((EntryPoint$C) a).fun();\n" +
            "}\n" +
            "((EntryPoint$B) a).fun();\n" +
            "((EntryPoint$C) a).fun();");
  }

  public void testSingleIfElse() throws Exception {
    optimize("void",
        "A a = new A();",
        "if (a instanceof B) {",
        "  ((B)a).fun();",
        "  ((C)a).fun();",
        "}",
        "else {",
        "  ((B)a).fun();",
        "  ((C)a).fun();",
        "}",
        "((B)a).fun();",
        "((C)a).fun();")
        .intoString("EntryPoint$A a = new EntryPoint$A();\n" +
            "if (a instanceof EntryPoint$B) {\n" +
            "  a.fun();\n" +
            "  ((EntryPoint$C) a).fun();\n" +
            "} else {\n" +
            "  ((EntryPoint$B) a).fun();\n" +
            "  ((EntryPoint$C) a).fun();\n" +
            "}\n" +
            "((EntryPoint$B) a).fun();\n" +
            "((EntryPoint$C) a).fun();");
  }

  public void testSingleIfWithDef() throws Exception {
    optimize("void",
        "A b = new A();",
        "if (b instanceof B) {",
        "  ((B)b).fun();",
        "  ((C)b).fun();",
        "  b = new B();",
        "  ((B)b).fun();",
        "  ((C)b).fun();",
        "}",
        "((B)b).fun();",
        "((C)b).fun();")
        .intoString("EntryPoint$A b = new EntryPoint$A();\n" +
            "if (b instanceof EntryPoint$B) {\n" +
            "  b.fun();\n" +
            "  ((EntryPoint$C) b).fun();\n" +
            "  b = new EntryPoint$B();\n" +
            "  ((EntryPoint$B) b).fun();\n" +
            "  ((EntryPoint$C) b).fun();\n" +
            "}\n" +
            "((EntryPoint$B) b).fun();\n" +
            "((EntryPoint$C) b).fun();");
  }

  public void testSingleIfElseWithDef() throws Exception {
    optimize("void",
        "A b = new A();",
        "if (b instanceof B) {",
        "  ((B)b).fun();",
        "  ((C)b).fun();",
        "  b = new B();",
        "  ((B)b).fun();",
        "  ((C)b).fun();",
        "}",
        "else {",
        "  ((B)b).fun();",
        "  ((C)b).fun();",
        "}",
        "((B)b).fun();",
        "((C)b).fun();")
        .intoString("EntryPoint$A b = new EntryPoint$A();\n" +
            "if (b instanceof EntryPoint$B) {\n" +
            "  b.fun();\n" +
            "  ((EntryPoint$C) b).fun();\n" +
            "  b = new EntryPoint$B();\n" +
            "  ((EntryPoint$B) b).fun();\n" +
            "  ((EntryPoint$C) b).fun();\n" +
            "} else {\n" +
            "  ((EntryPoint$B) b).fun();\n" +
            "  ((EntryPoint$C) b).fun();\n" +
            "}\n" +
            "((EntryPoint$B) b).fun();\n" +
            "((EntryPoint$C) b).fun();");
  }

  public void testSingleWhile() throws Exception {
    optimize("void",
        "A a = new A();",
        "while (a instanceof B) {",
        "  ((B)a).fun();",
        "  ((C)a).fun();",
        "}",
        "((B)a).fun();",
        "((C)a).fun();")
        .intoString("EntryPoint$A a = new EntryPoint$A();\n" +
            "while (a instanceof EntryPoint$B) {\n" +
            "  a.fun();\n" +
            "  ((EntryPoint$C) a).fun();\n" +
            "}\n" +
            "((EntryPoint$B) a).fun();\n" +
            "((EntryPoint$C) a).fun();");
  }

  public void testSingleWhileWithDef() throws Exception {
    optimize("void",
        "A b = new A();",
        "while (b instanceof B) {",
        "  ((B)b).fun();",
        "  ((C)b).fun();",
        "  b = new B();",
        "  ((B)b).fun();",
        "  ((C)b).fun();",
        "}",
        "((B)b).fun();",
        "((C)b).fun();")
        .intoString("EntryPoint$A b = new EntryPoint$A();\n" +
            "while (b instanceof EntryPoint$B) {\n" +
            "  b.fun();\n" +
            "  ((EntryPoint$C) b).fun();\n" +
            "  b = new EntryPoint$B();\n" +
            "  ((EntryPoint$B) b).fun();\n" +
            "  ((EntryPoint$C) b).fun();\n" +
            "}\n" +
            "((EntryPoint$B) b).fun();\n" +
            "((EntryPoint$C) b).fun();");
  }

  public void testSingleFor() throws Exception {
    optimize("void",
        "A a = new A();",
        "for (int i = 0; a instanceof B; i++) {",
        "  ((B)a).fun();",
        "  ((C)a).fun();",
        "}",
        "((B)a).fun();",
        "((C)a).fun();")
        .intoString("EntryPoint$A a = new EntryPoint$A();\n" +
            "for (int i = 0; a instanceof EntryPoint$B; i++) {\n" +
            "  a.fun();\n" +
            "  ((EntryPoint$C) a).fun();\n" +
            "}\n" +
            "((EntryPoint$B) a).fun();\n" +
            "((EntryPoint$C) a).fun();");
  }

  public void testSingleForWithDef() throws Exception {
    optimize("void",
        "A b = new A();",
        "for (int i = 0; b instanceof B; i++) {",
        "  ((B)b).fun();",
        "  ((C)b).fun();",
        "  b = new B();",
        "  ((B)b).fun();",
        "  ((C)b).fun();",
        "}",
        "  ((B)b).fun();",
        "  ((C)b).fun();")
        .intoString("EntryPoint$A b = new EntryPoint$A();\n" +
            "for (int i = 0; b instanceof EntryPoint$B; i++) {\n" +
            "  b.fun();\n" +
            "  ((EntryPoint$C) b).fun();\n" +
            "  b = new EntryPoint$B();\n" +
            "  ((EntryPoint$B) b).fun();\n" +
            "  ((EntryPoint$C) b).fun();\n" +
            "}\n" +
            "((EntryPoint$B) b).fun();\n" +
            "((EntryPoint$C) b).fun();");
  }

  public void testNestedIf() throws Exception {
    optimize("void",
        "    A a = new A();\n",
        "    if (a instanceof B) {\n",
        "      ((B)a).fun();\n",
        "      ((C)a).fun();\n",
        "      ((D)a).fun();\n",
        "      if (a instanceof C) {\n",
        "        ((B)a).fun();\n",
        "        ((C)a).fun();\n",
        "        ((D)a).fun();\n",
        "      }\n",
        "      ((B)a).fun();\n",
        "      ((C)a).fun();\n",
        "      ((D)a).fun();\n",
        "    }",
        "    ((B)a).fun();\n",
        "    ((C)a).fun();\n",
        "    ((D)a).fun();\n")
        .intoString("EntryPoint$A a = new EntryPoint$A();\n" +
            "if (a instanceof EntryPoint$B) {\n" +
            "  a.fun();\n" +
            "  ((EntryPoint$C) a).fun();\n" +
            "  ((EntryPoint$D) a).fun();\n" +
            "  if (a instanceof EntryPoint$C) {\n" +
            "    a.fun();\n" +
            "    a.fun();\n" +
            "    ((EntryPoint$D) a).fun();\n" +
            "  }\n" +
            "  a.fun();\n" +
            "  ((EntryPoint$C) a).fun();\n" +
            "  ((EntryPoint$D) a).fun();\n" +
            "}\n" +
            "((EntryPoint$B) a).fun();\n" +
            "((EntryPoint$C) a).fun();\n" +
            "((EntryPoint$D) a).fun();");
  }

  public void testNestedIfWithDef() throws Exception {
    optimize("void",
        "    A a = new A();\n",
        "    if (a instanceof B) {\n",
        "      ((B)a).fun();\n",
        "      ((C)a).fun();\n",
        "      ((D)a).fun();\n",
        "      a = new B();\n",
        "      if (a instanceof C) {\n",
        "        ((B)a).fun();\n",
        "        ((C)a).fun();\n",
        "        ((D)a).fun();\n",
        "      }\n",
        "      ((B)a).fun();\n",
        "      ((C)a).fun();\n",
        "      ((D)a).fun();\n",
        "    }",
        "    ((B)a).fun();\n",
        "    ((C)a).fun();\n",
        "    ((D)a).fun();\n")
        .intoString("EntryPoint$A a = new EntryPoint$A();\n" +
            "if (a instanceof EntryPoint$B) {\n" +
            "  a.fun();\n" +
            "  ((EntryPoint$C) a).fun();\n" +
            "  ((EntryPoint$D) a).fun();\n" +
            "  a = new EntryPoint$B();\n" +
            "  if (a instanceof EntryPoint$C) {\n" +
            "    ((EntryPoint$B) a).fun();\n" +
            "    a.fun();\n" +
            "    ((EntryPoint$D) a).fun();\n" +
            "  }\n" +
            "  ((EntryPoint$B) a).fun();\n" +
            "  ((EntryPoint$C) a).fun();\n" +
            "  ((EntryPoint$D) a).fun();\n" +
            "}\n" +
            "((EntryPoint$B) a).fun();\n" +
            "((EntryPoint$C) a).fun();\n" +
            "((EntryPoint$D) a).fun();");
  }

  public void testNestedIfElse() throws Exception {
    optimize("void",
        "    A a = new A();\n" +
        "    if (a instanceof B) {\n" +
        "      ((B)a).fun();\n" +
        "      ((C)a).fun();\n" +
        "      ((D)a).fun();\n" +
        "      if (a instanceof C) {\n" +
        "        ((B)a).fun();\n" +
        "        ((C)a).fun();\n" +
        "        ((D)a).fun();\n" +
        "      }\n" +
        "      else {\n" +
        "        ((B)a).fun();\n" +
        "        ((C)a).fun();\n" +
        "        ((D)a).fun();\n" +
        "      }\n" +
        "      ((B)a).fun();\n" +
        "      ((C)a).fun();\n" +
        "      ((D)a).fun();\n" +
        "    }\n" +
        "    else {\n" +
        "      ((B)a).fun();\n" +
        "      ((C)a).fun();\n" +
        "      ((D)a).fun();\n" +
        "    }",
        "((B)a).fun();",
        "((C)a).fun();",
        "((D)a).fun();")
        .intoString("EntryPoint$A a = new EntryPoint$A();\n" +
            "if (a instanceof EntryPoint$B) {\n" +
            "  a.fun();\n" +
            "  ((EntryPoint$C) a).fun();\n" +
            "  ((EntryPoint$D) a).fun();\n" +
            "  if (a instanceof EntryPoint$C) {\n" +
            "    a.fun();\n" +
            "    a.fun();\n" +
            "    ((EntryPoint$D) a).fun();\n" +
            "  } else {\n" +
            "    a.fun();\n" +
            "    ((EntryPoint$C) a).fun();\n" +
            "    ((EntryPoint$D) a).fun();\n" +
            "  }\n" +
            "  a.fun();\n" +
            "  ((EntryPoint$C) a).fun();\n" +
            "  ((EntryPoint$D) a).fun();\n" +
            "} else {\n" +
            "  ((EntryPoint$B) a).fun();\n" +
            "  ((EntryPoint$C) a).fun();\n" +
            "  ((EntryPoint$D) a).fun();\n" +
            "}\n" +
            "((EntryPoint$B) a).fun();\n" +
            "((EntryPoint$C) a).fun();\n" +
            "((EntryPoint$D) a).fun();");
  }

  public void testNestedIfElseWithDef() throws Exception {
    optimize("void",
        "    A a = new A();\n" +
        "    if (a instanceof B) {\n" +
        "      ((B)a).fun();\n" +
        "      ((C)a).fun();\n" +
        "      ((D)a).fun();\n" +
        "      if (a instanceof C) {\n" +
        "        ((B)a).fun();\n" +
        "        ((C)a).fun();\n" +
        "        ((D)a).fun();\n" +
        "        a = new B();" +
        "        ((B)a).fun();\n" +
        "        ((C)a).fun();\n" +
        "        ((D)a).fun();\n" +
        "      }\n" +
        "      else {\n" +
        "        ((B)a).fun();\n" +
        "        ((C)a).fun();\n" +
        "        ((D)a).fun();\n" +
        "      }\n" +
        "      ((B)a).fun();\n" +
        "      ((C)a).fun();\n" +
        "      ((D)a).fun();\n" +
        "    }\n" +
        "    else {\n" +
        "      ((B)a).fun();\n" +
        "      ((C)a).fun();\n" +
        "      ((D)a).fun();\n" +
        "    }",
        "    ((B)a).fun();",
        "    ((C)a).fun();",
        "    ((D)a).fun();")
        .intoString("EntryPoint$A a = new EntryPoint$A();\n" +
            "if (a instanceof EntryPoint$B) {\n" +
            "  a.fun();\n" +
            "  ((EntryPoint$C) a).fun();\n" +
            "  ((EntryPoint$D) a).fun();\n" +
            "  if (a instanceof EntryPoint$C) {\n" +
            "    a.fun();\n" +
            "    a.fun();\n" +
            "    ((EntryPoint$D) a).fun();\n" +
            "    a = new EntryPoint$B();\n" +
            "    ((EntryPoint$B) a).fun();\n" +
            "    ((EntryPoint$C) a).fun();\n" +
            "    ((EntryPoint$D) a).fun();\n" +
            "  } else {\n" +
            "    a.fun();\n" +
            "    ((EntryPoint$C) a).fun();\n" +
            "    ((EntryPoint$D) a).fun();\n" +
            "  }\n" +
            "  ((EntryPoint$B) a).fun();\n" +
            "  ((EntryPoint$C) a).fun();\n" +
            "  ((EntryPoint$D) a).fun();\n" +
            "} else {\n" +
            "  ((EntryPoint$B) a).fun();\n" +
            "  ((EntryPoint$C) a).fun();\n" +
            "  ((EntryPoint$D) a).fun();\n" +
            "}\n" +
            "((EntryPoint$B) a).fun();\n" +
            "((EntryPoint$C) a).fun();\n" +
            "((EntryPoint$D) a).fun();");
  }

  public void testNestedWhile() throws Exception {
    optimize("void",
        "    A a = new A();\n",
        "    while (a instanceof B) {\n",
        "      ((B)a).fun();\n",
        "      ((C)a).fun();\n",
        "      ((D)a).fun();\n",
        "      while (a instanceof C) {\n",
        "        ((B)a).fun();\n",
        "        ((C)a).fun();\n",
        "        ((D)a).fun();\n",
        "        break;",
        "      }\n",
        "      ((B)a).fun();\n",
        "      ((C)a).fun();\n",
        "      ((D)a).fun();\n",
        "    }",
        "((B)a).fun();",
        "((C)a).fun();",
        "((D)a).fun();")
        .intoString("EntryPoint$A a = new EntryPoint$A();\n" +
            "while (a instanceof EntryPoint$B) {\n" +
            "  a.fun();\n" +
            "  ((EntryPoint$C) a).fun();\n" +
            "  ((EntryPoint$D) a).fun();\n" +
            "  while (a instanceof EntryPoint$C) {\n" +
            "    a.fun();\n" +
            "    a.fun();\n" +
            "    ((EntryPoint$D) a).fun();\n" +
            "    break;\n" +
            "  }\n" +
            "  a.fun();\n" +
            "  ((EntryPoint$C) a).fun();\n" +
            "  ((EntryPoint$D) a).fun();\n" +
            "}\n" +
            "((EntryPoint$B) a).fun();\n" +
            "((EntryPoint$C) a).fun();\n" +
            "((EntryPoint$D) a).fun();");
  }

  public void testNestedWhileWithDef() throws Exception {
    optimize("void",
        "    A a = new A();\n",
        "    while (a instanceof B) {\n",
        "      ((B)a).fun();\n",
        "      ((C)a).fun();\n",
        "      ((D)a).fun();\n",
        "      while (a instanceof C) {\n",
        "        ((B)a).fun();\n",
        "        ((C)a).fun();\n",
        "        ((D)a).fun();\n",
        "        a = new B();\n",
        "        ((B)a).fun();\n",
        "        ((C)a).fun();\n",
        "        ((D)a).fun();\n",
        "        break;",
        "      }\n",
        "      ((B)a).fun();\n",
        "      ((C)a).fun();\n",
        "      ((D)a).fun();\n",
        "    }",
        "((B)a).fun();",
        "((C)a).fun();",
        "((D)a).fun();")
        .intoString("EntryPoint$A a = new EntryPoint$A();\n" +
            "while (a instanceof EntryPoint$B) {\n" +
            "  a.fun();\n" +
            "  ((EntryPoint$C) a).fun();\n" +
            "  ((EntryPoint$D) a).fun();\n" +
            "  while (a instanceof EntryPoint$C) {\n" +
            "    ((EntryPoint$B) a).fun();\n" +
            "    a.fun();\n" +
            "    ((EntryPoint$D) a).fun();\n" +
            "    a = new EntryPoint$B();\n" +
            "    ((EntryPoint$B) a).fun();\n" +
            "    ((EntryPoint$C) a).fun();\n" +
            "    ((EntryPoint$D) a).fun();\n" +
            "    break;\n" +
            "  }\n" +
            "  ((EntryPoint$B) a).fun();\n" +
            "  ((EntryPoint$C) a).fun();\n" +
            "  ((EntryPoint$D) a).fun();\n" +
            "}\n" +
            "((EntryPoint$B) a).fun();\n" +
            "((EntryPoint$C) a).fun();\n" +
            "((EntryPoint$D) a).fun();");
  }

  public void testNestedFor() throws Exception {
    optimize("void",
        "    A a = new A();\n",
        "    for (int i = 0; a instanceof B; i++) {\n",
        "      ((B)a).fun();\n",
        "      ((C)a).fun();\n",
        "      ((D)a).fun();\n",
        "      for (int j = 0; a instanceof C; j++) {\n",
        "        ((B)a).fun();\n",
        "        ((C)a).fun();\n",
        "        ((D)a).fun();\n",
        "        break;",
        "      }\n",
        "      ((B)a).fun();\n",
        "      ((C)a).fun();\n",
        "      ((D)a).fun();\n",
        "    }",
        "((B)a).fun();",
        "((C)a).fun();",
        "((D)a).fun();")
        .intoString("EntryPoint$A a = new EntryPoint$A();\n" +
            "for (int i = 0; a instanceof EntryPoint$B; i++) {\n" +
            "  a.fun();\n" +
            "  ((EntryPoint$C) a).fun();\n" +
            "  ((EntryPoint$D) a).fun();\n" +
            "  for (int j = 0; a instanceof EntryPoint$C; j++) {\n" +
            "    a.fun();\n" +
            "    a.fun();\n" +
            "    ((EntryPoint$D) a).fun();\n" +
            "    break;\n" +
            "  }\n" +
            "  a.fun();\n" +
            "  ((EntryPoint$C) a).fun();\n" +
            "  ((EntryPoint$D) a).fun();\n" +
            "}\n" +
            "((EntryPoint$B) a).fun();\n" +
            "((EntryPoint$C) a).fun();\n" +
            "((EntryPoint$D) a).fun();");
  }

  public void testNestedForWithDef() throws Exception {
    optimize("void",
        "    A a = new A();\n",
        "    for (int i = 0; a instanceof B; i++) {\n",
        "      ((B)a).fun();\n",
        "      ((C)a).fun();\n",
        "      ((D)a).fun();\n",
        "      for (int j = 0; a instanceof C; j++) {\n",
        "        ((B)a).fun();\n",
        "        ((C)a).fun();\n",
        "        ((D)a).fun();\n",
        "        a = new B();\n",
        "        ((B)a).fun();\n",
        "        ((C)a).fun();\n",
        "        ((D)a).fun();\n",
        "        break;",
        "      }\n",
        "      ((B)a).fun();\n",
        "      ((C)a).fun();\n",
        "      ((D)a).fun();\n",
        "    }",
        "((B)a).fun();",
        "((C)a).fun();",
        "((D)a).fun();")
        .intoString("EntryPoint$A a = new EntryPoint$A();\n" +
            "for (int i = 0; a instanceof EntryPoint$B; i++) {\n" +
            "  a.fun();\n" +
            "  ((EntryPoint$C) a).fun();\n" +
            "  ((EntryPoint$D) a).fun();\n" +
            "  for (int j = 0; a instanceof EntryPoint$C; j++) {\n" +
            "    ((EntryPoint$B) a).fun();\n" +
            "    a.fun();\n" +
            "    ((EntryPoint$D) a).fun();\n" +
            "    a = new EntryPoint$B();\n" +
            "    ((EntryPoint$B) a).fun();\n" +
            "    ((EntryPoint$C) a).fun();\n" +
            "    ((EntryPoint$D) a).fun();\n" +
            "    break;\n" +
            "  }\n" +
            "  ((EntryPoint$B) a).fun();\n" +
            "  ((EntryPoint$C) a).fun();\n" +
            "  ((EntryPoint$D) a).fun();\n" +
            "}\n" +
            "((EntryPoint$B) a).fun();\n" +
            "((EntryPoint$C) a).fun();\n" +
            "((EntryPoint$D) a).fun();");
  }

  public void testNestedAll() throws Exception {
    optimize("void",
        "    A a = new A();\n" +
        "    if (a instanceof B) {\n" +
        "      ((B)a).fun();\n" +
        "      ((C)a).fun();\n" +
        "      ((D)a).fun();\n" +
        "      while (a instanceof C) {\n" +
        "        ((B)a).fun();\n" +
        "        ((C)a).fun();\n" +
        "        ((D)a).fun();\n" +
        "        for (int i = 0; a instanceof D; i++) {\n" +
        "          ((B)a).fun();\n" +
        "          ((C)a).fun();\n" +
        "          ((D)a).fun();\n" +
        "          int result = (a instanceof E) ? ((E)a).fun() : ((B)a).fun();",
        "          result = ((E)a).fun();",
        "          break;\n" +
        "        }\n" +
        "        ((B)a).fun();\n" +
        "        ((C)a).fun();\n" +
        "        ((D)a).fun();\n" +
        "        break;\n" +
        "      }\n" +
        "      ((B)a).fun();\n" +
        "      ((C)a).fun();\n" +
        "      ((D)a).fun();\n" +
        "    }\n" +
        "    else{\n" +
        "      ((B)a).fun();\n" +
        "      ((C)a).fun();\n" +
        "      ((D)a).fun();\n" +
        "    }\n" +
        "    ((B)a).fun();\n" +
        "    ((C)a).fun();\n" +
        "    ((D)a).fun();")
        .intoString("EntryPoint$A a = new EntryPoint$A();\n" +
            "if (a instanceof EntryPoint$B) {\n" +
            "  a.fun();\n" +
            "  ((EntryPoint$C) a).fun();\n" +
            "  ((EntryPoint$D) a).fun();\n" +
            "  while (a instanceof EntryPoint$C) {\n" +
            "    a.fun();\n" +
            "    a.fun();\n" +
            "    ((EntryPoint$D) a).fun();\n" +
            "    for (int i = 0; a instanceof EntryPoint$D; i++) {\n" +
            "      a.fun();\n" +
            "      a.fun();\n" +
            "      a.fun();\n" +
            "      int result = a instanceof EntryPoint$E ? a.fun() : a.fun();\n" +
            "      result = ((EntryPoint$E) a).fun();\n" +
            "      break;\n" +
            "    }\n" +
            "    a.fun();\n" +
            "    a.fun();\n" +
            "    ((EntryPoint$D) a).fun();\n" +
            "    break;\n" +
            "  }\n" +
            "  a.fun();\n" +
            "  ((EntryPoint$C) a).fun();\n" +
            "  ((EntryPoint$D) a).fun();\n" +
            "} else {\n" +
            "  ((EntryPoint$B) a).fun();\n" +
            "  ((EntryPoint$C) a).fun();\n" +
            "  ((EntryPoint$D) a).fun();\n" +
            "}\n" +
            "((EntryPoint$B) a).fun();\n" +
            "((EntryPoint$C) a).fun();\n" +
            "((EntryPoint$D) a).fun();");
  }

  public void testNestedWithDefs() throws Exception {
    optimize("void",
            "    A a = new A();\n" +
            "    if (a instanceof B) {\n" +
            "      ((B)a).fun();\n" +
            "      ((C)a).fun();\n" +
            "      ((D)a).fun();\n" +
            "      while (a instanceof C) {\n" +
            "        ((B)a).fun();\n" +
            "        ((C)a).fun();\n" +
            "        ((D)a).fun();\n" +
            "        a = new B();\n" +
            "        for (int i = 0; a instanceof D; i++) {\n" +
            "          ((B)a).fun();\n" +
            "          ((C)a).fun();\n" +
            "          ((D)a).fun();\n" +
            "          break;\n" +
            "        }\n" +
            "        ((B)a).fun();\n" +
            "        ((C)a).fun();\n" +
            "        ((D)a).fun();\n" +
            "        break;\n" +
            "      }\n" +
            "      ((B)a).fun();\n" +
            "      ((C)a).fun();\n" +
            "      ((D)a).fun();\n" +
            "    }\n" +
            "    else{\n" +
            "      ((B)a).fun();\n" +
            "      ((C)a).fun();\n" +
            "      ((D)a).fun();\n" +
            "    }\n" +
            "    ((B)a).fun();\n" +
            "    ((C)a).fun();\n" +
            "    ((D)a).fun();")
        .intoString("EntryPoint$A a = new EntryPoint$A();\n" +
            "if (a instanceof EntryPoint$B) {\n" +
            "  a.fun();\n" +
            "  ((EntryPoint$C) a).fun();\n" +
            "  ((EntryPoint$D) a).fun();\n" +
            "  while (a instanceof EntryPoint$C) {\n" +
            "    ((EntryPoint$B) a).fun();\n" +
            "    a.fun();\n" +
            "    ((EntryPoint$D) a).fun();\n" +
            "    a = new EntryPoint$B();\n" +
            "    for (int i = 0; a instanceof EntryPoint$D; i++) {\n" +
            "      ((EntryPoint$B) a).fun();\n" +
            "      ((EntryPoint$C) a).fun();\n" +
            "      a.fun();\n" +
            "      break;\n" +
            "    }\n" +
            "    ((EntryPoint$B) a).fun();\n" +
            "    ((EntryPoint$C) a).fun();\n" +
            "    ((EntryPoint$D) a).fun();\n" +
            "    break;\n" +
            "  }\n" +
            "  ((EntryPoint$B) a).fun();\n" +
            "  ((EntryPoint$C) a).fun();\n" +
            "  ((EntryPoint$D) a).fun();\n" +
            "} else {\n" +
            "  ((EntryPoint$B) a).fun();\n" +
            "  ((EntryPoint$C) a).fun();\n" +
            "  ((EntryPoint$D) a).fun();\n" +
            "}\n" +
            "((EntryPoint$B) a).fun();\n" +
            "((EntryPoint$C) a).fun();\n" +
            "((EntryPoint$D) a).fun();");
  }

  public void testMultipleVariables() throws Exception {
    optimize("void",
        "    A a = new A();\n" +
        "    A b = new A();\n" +
        "    if (a instanceof B && b instanceof C) {\n" +
        "      ((C)a).fun();\n" +
        "      ((B)a).fun();\n" +
        "      ((C)b).fun();\n" +
        "      ((B)b).fun();\n" +
        "    }\n" +
        "    else{\n" +
        "      ((C)a).fun();\n" +
        "      ((B)a).fun();\n" +
        "      ((C)b).fun();\n" +
        "      ((B)b).fun();\n" +
        "    }")
        .intoString("EntryPoint$A a = new EntryPoint$A();\n" +
            "EntryPoint$A b = new EntryPoint$A();\n" +
            "if (a instanceof EntryPoint$B && b instanceof EntryPoint$C) {\n" +
            "  ((EntryPoint$C) a).fun();\n" +
            "  a.fun();\n" +
            "  b.fun();\n" +
            "  ((EntryPoint$B) b).fun();\n" +
            "} else {\n" +
            "  ((EntryPoint$C) a).fun();\n" +
            "  ((EntryPoint$B) a).fun();\n" +
            "  ((EntryPoint$C) b).fun();\n" +
            "  ((EntryPoint$B) b).fun();\n" +
            "}");
  }

  public void testDefinitions() throws Exception {
    optimize("void",
        "    A a = new A();\n" +
        "    A b = new A();\n" +
        "    if (a instanceof B) {\n" +
        "      ((B)a).fun();\n" +
        "      {\n" +
        "        a = new B();\n" +
        "      }\n" +
        "      ((B)a).fun();\n" +
        "    }\n" + // test definitions in block statement.
        "    if (a instanceof B && b instanceof B) {\n" +
        "      ((B)a).fun();\n" +
        "      ((B)b).fun();\n" +
        "      do{\n" +
        "        a = new B();\n" +
        "        if (a != null) {\n" +
        "          break;\n" +
        "        }\n" +
        "      } while((b=new B()) == null);\n" +
        "      ((B)a).fun();\n" +
        "      ((B)b).fun();\n" +
        "    }\n" + // test definitions in do statement.
        "    if (a instanceof B) {\n" +
        "      ((B)a).fun();\n" +
        "      switch(((B)a).fun()){\n" +
        "        case 0:\n" +
        "          ((B)a).fun();\n" +
        "          a = new B();\n" +
        "          break;\n" +
        "        case 1:\n" +
        "          ((B)a).fun();\n" +
        "          break;\n" +
        "          default:\n" +
        "            break;\n" +
        "      }\n" +
        "      ((B)a).fun();\n" +
        "    }\n" + // test definitions in switch statement and case statement.
        "    if (a instanceof B) {\n" +
        "      ((B)a).fun();\n" +
        "      for (; ((B)a).fun()==1; a = new B()) {\n" +
        "        ((B)a).fun();\n" +
        "      }\n" +
        "      ((B)a).fun();\n" +
        "    }\n" + // test definitions in for statement.
        "    if (a instanceof B && b instanceof B) {\n" +
        "      ((B)a).fun();\n" +
        "      ((B)b).fun();\n" +
        "      if (a == null) {\n" +
        "        a = new B();\n" +
        "      }\n" +
        "      else {\n" +
        "        b = new B();\n" +
        "      }\n" +
        "      ((B)a).fun();\n" +
        "      ((B)b).fun();\n" +
        "    }\n" + // test definitions in if statement.
        "    if (a instanceof B && b instanceof B) {\n" +
        "      ((B)a).fun();\n" +
        "      ((B)b).fun();\n" +
        "      try{\n" +
        "        a = new A();\n" +
        "      }\n" +
        "      catch(Exception e){\n" +
        "        ((B)a).fun();\n" +
        "        ((B)b).fun();\n" +
        "        b = new A();\n" +
        "      }\n" +
        "      ((B)a).fun();\n" +
        "      ((B)b).fun();\n" +
        "    }\n" + // test definitions in try and catch statement.
        "    if (a instanceof B) {\n" +
        "      ((B)a).fun();\n" +
        "      while ( (a=new A()) != null) {\n" +
        "        ((B)a).fun();" +
        "        continue;\n" +
        "      }\n" +
        "      ((B)a).fun();\n" + // test definitions in while statement.
        "    }")
        .intoString("EntryPoint$A a = new EntryPoint$A();\n" +
            "EntryPoint$A b = new EntryPoint$A();\n" +
            "if (a instanceof EntryPoint$B) {\n" +
            "  a.fun();\n" +
            "  {\n" +
            "    a = new EntryPoint$B();\n" +
            "  }\n" +
            "  ((EntryPoint$B) a).fun();\n" +
            "}\n" +
            "if (a instanceof EntryPoint$B && b instanceof EntryPoint$B) {\n" +
            "  a.fun();\n" +
            "  b.fun();\n" +
            "  do {\n" +
            "    a = new EntryPoint$B();\n" +
            "    if (a != null) {\n" +
            "      break;\n" +
            "    }\n" +
            "  } while ((b = new EntryPoint$B()) == null);\n" +
            "  ((EntryPoint$B) a).fun();\n" +
            "  ((EntryPoint$B) b).fun();\n" +
            "}\n" +
            "if (a instanceof EntryPoint$B) {\n" +
            "  a.fun();\n" +
            "  switch (a.fun())  {\n" +
            "    case 0: \n" +
            "    a.fun();\n" +
            "    a = new EntryPoint$B();\n" +
            "    break;\n" +
            "    case 1: \n" +
            "    ((EntryPoint$B) a).fun();\n" +
            "    break;\n" +
            "    default: \n" +
            "    break;\n" +
            "  }\n" +
            "  ((EntryPoint$B) a).fun();\n" +
            "}\n" +
            "if (a instanceof EntryPoint$B) {\n" +
            "  a.fun();\n" +
            "  for (; ((EntryPoint$B) a).fun() == 1; a = new EntryPoint$B()) {\n" +
            "    ((EntryPoint$B) a).fun();\n" +
            "  }\n" +
            "  ((EntryPoint$B) a).fun();\n" +
            "}\n" +
            "if (a instanceof EntryPoint$B && b instanceof EntryPoint$B) {\n" +
            "  a.fun();\n" +
            "  b.fun();\n" +
            "  if (a == null) {\n" +
            "    a = new EntryPoint$B();\n" +
            "  } else {\n" +
            "    b = new EntryPoint$B();\n" +
            "  }\n" +
            "  ((EntryPoint$B) a).fun();\n" +
            "  ((EntryPoint$B) b).fun();\n" +
            "}\n" +
            "if (a instanceof EntryPoint$B && b instanceof EntryPoint$B) {\n" +
            "  a.fun();\n" +
            "  b.fun();\n" +
            "  try {\n" +
            "    a = new EntryPoint$A();\n" +
            "  } catch (Exception e) {\n" +
            "    ((EntryPoint$B) a).fun();\n" +
            "    b.fun();\n" +
            "    b = new EntryPoint$A();\n" +
            "  }\n" +
            "  ((EntryPoint$B) a).fun();\n" +
            "  ((EntryPoint$B) b).fun();\n" +
            "}\n" +
            "if (a instanceof EntryPoint$B) {\n" +
            "  a.fun();\n" +
            "  while ((a = new EntryPoint$A()) != null) {\n" +
            "    ((EntryPoint$B) a).fun();\n" +
            "    continue;\n" +
            "  }\n" +
            "  ((EntryPoint$B) a).fun();\n" +
            "}");
  }

  public void testMultipleVariablesWithNestedStmtsAndDefs() throws Exception {
    optimize("void",
            "    A a = new A();\n" +
            "    A b = new A();\n" +
            "    if (a instanceof B && b instanceof B) {\n" +
            "      ((B)a).fun();\n" +
            "      ((C)a).fun();\n" +
            "      ((D)a).fun();\n" +
            "      ((B)b).fun();\n" +
            "      ((C)b).fun();\n" +
            "      ((D)b).fun();\n" +
            "      while (a instanceof C) {\n" +
            "        ((B)a).fun();\n" +
            "        ((C)a).fun();\n" +
            "        ((D)a).fun();\n" +
            "        ((B)b).fun();\n" +
            "        ((C)b).fun();\n" +
            "        ((D)b).fun();\n" +
            "        a = new B();\n" +
            "        ((B)a).fun();\n" +
            "        ((C)a).fun();\n" +
            "        ((D)a).fun();\n" +
            "        ((B)b).fun();\n" +
            "        ((C)b).fun();\n" +
            "        ((D)b).fun();\n" +
            "        int result = (b instanceof D) ? ((D)b).fun() : ((D)b).fun();" +
            "        result = (a instanceof D) ? ((D)a).fun() : ((C)a).fun();" +
            "        for (int i = 0; b instanceof C; i++) {\n" +
            "          ((B)a).fun();\n" +
            "          ((C)a).fun();\n" +
            "          ((D)a).fun();\n" +
            "          ((B)b).fun();\n" +
            "          ((C)b).fun();\n" +
            "          ((D)b).fun();\n" +
            "          b = new B();\n" +
            "          ((B)a).fun();\n" +
            "          ((C)a).fun();\n" +
            "          ((D)a).fun();\n" +
            "          ((B)b).fun();\n" +
            "          ((C)b).fun();\n" +
            "          ((D)b).fun();\n" +
            "          break;\n" +
            "        }\n" +
            "        ((B)a).fun();\n" +
            "        ((C)a).fun();\n" +
            "        ((D)a).fun();\n" +
            "        ((B)b).fun();\n" +
            "        ((C)b).fun();\n" +
            "        ((D)b).fun();\n" +
            "        break;\n" +
            "      }\n" +
            "      ((B)a).fun();\n" +
            "      ((C)a).fun();\n" +
            "      ((D)a).fun();\n" +
            "      ((B)b).fun();\n" +
            "      ((C)b).fun();\n" +
            "      ((D)b).fun();\n" +
            "    }\n" +
            "    else{\n" +
            "      ((B)a).fun();\n" +
            "      ((C)a).fun();\n" +
            "      ((D)a).fun();\n" +
            "      ((B)b).fun();\n" +
            "      ((C)b).fun();\n" +
            "      ((D)b).fun();\n" +
            "    }\n" +
            "    ((B)a).fun();\n" +
            "    ((C)a).fun();\n" +
            "    ((D)a).fun();" +
            "    ((B)b).fun();\n" +
            "    ((C)b).fun();\n" +
            "    ((D)b).fun();\n")
        .intoString("EntryPoint$A a = new EntryPoint$A();\n" +
            "EntryPoint$A b = new EntryPoint$A();\n" +
            "if (a instanceof EntryPoint$B && b instanceof EntryPoint$B) {\n" +
            "  a.fun();\n" +
            "  ((EntryPoint$C) a).fun();\n" +
            "  ((EntryPoint$D) a).fun();\n" +
            "  b.fun();\n" +
            "  ((EntryPoint$C) b).fun();\n" +
            "  ((EntryPoint$D) b).fun();\n" +
            "  while (a instanceof EntryPoint$C) {\n" +
            "    ((EntryPoint$B) a).fun();\n" +
            "    a.fun();\n" +
            "    ((EntryPoint$D) a).fun();\n" +
            "    ((EntryPoint$B) b).fun();\n" +
            "    ((EntryPoint$C) b).fun();\n" +
            "    ((EntryPoint$D) b).fun();\n" +
            "    a = new EntryPoint$B();\n" +
            "    ((EntryPoint$B) a).fun();\n" +
            "    ((EntryPoint$C) a).fun();\n" +
            "    ((EntryPoint$D) a).fun();\n" +
            "    ((EntryPoint$B) b).fun();\n" +
            "    ((EntryPoint$C) b).fun();\n" +
            "    ((EntryPoint$D) b).fun();\n" +
            "    int result = b instanceof EntryPoint$D ? b.fun() : ((EntryPoint$D) b).fun();\n" +
            "    result = a instanceof EntryPoint$D ? a.fun() : ((EntryPoint$C) a).fun();\n" +
            "    for (int i = 0; b instanceof EntryPoint$C; i++) {\n" +
            "      ((EntryPoint$B) a).fun();\n" +
            "      ((EntryPoint$C) a).fun();\n" +
            "      ((EntryPoint$D) a).fun();\n" +
            "      ((EntryPoint$B) b).fun();\n" +
            "      b.fun();\n" +
            "      ((EntryPoint$D) b).fun();\n" +
            "      b = new EntryPoint$B();\n" +
            "      ((EntryPoint$B) a).fun();\n" +
            "      ((EntryPoint$C) a).fun();\n" +
            "      ((EntryPoint$D) a).fun();\n" +
            "      ((EntryPoint$B) b).fun();\n" +
            "      ((EntryPoint$C) b).fun();\n" +
            "      ((EntryPoint$D) b).fun();\n" +
            "      break;\n" +
            "    }\n" +
            "    ((EntryPoint$B) a).fun();\n" +
            "    ((EntryPoint$C) a).fun();\n" +
            "    ((EntryPoint$D) a).fun();\n" +
            "    ((EntryPoint$B) b).fun();\n" +
            "    ((EntryPoint$C) b).fun();\n" +
            "    ((EntryPoint$D) b).fun();\n" +
            "    break;\n" +
            "  }\n" +
            "  ((EntryPoint$B) a).fun();\n" +
            "  ((EntryPoint$C) a).fun();\n" +
            "  ((EntryPoint$D) a).fun();\n" +
            "  ((EntryPoint$B) b).fun();\n" +
            "  ((EntryPoint$C) b).fun();\n" +
            "  ((EntryPoint$D) b).fun();\n" +
            "} else {\n" +
            "  ((EntryPoint$B) a).fun();\n" +
            "  ((EntryPoint$C) a).fun();\n" +
            "  ((EntryPoint$D) a).fun();\n" +
            "  ((EntryPoint$B) b).fun();\n" +
            "  ((EntryPoint$C) b).fun();\n" +
            "  ((EntryPoint$D) b).fun();\n" +
            "}\n" +
            "((EntryPoint$B) a).fun();\n" +
            "((EntryPoint$C) a).fun();\n" +
            "((EntryPoint$D) a).fun();\n" +
            "((EntryPoint$B) b).fun();\n" +
            "((EntryPoint$C) b).fun();\n" +
            "((EntryPoint$D) b).fun();");
  }

  @Override
  protected boolean optimizeMethod(JProgram program, JMethod method) {
    program.addEntryMethod(findMainMethod(program));
    return RedundantCastRemover.exec(program).didChange();
  }
}
