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
    addSnippetClassDecl("static class A { public int fun() {return 0;} }");
    addSnippetClassDecl("static class B extends A { }");
    addSnippetClassDecl("static class C extends B { }");
    addSnippetClassDecl("static class D extends B { }");
  }

  public void testNoChange() throws Exception {
    optimize("void",
        "A a = new A();",
        "if (a != null) {",
        "  ((A)a).fun();",
        "}",
        "if (a == null) {",
        "  ((B)a).fun();",
        "}")
        .intoString("EntryPoint$A a = new EntryPoint$A();\n" +
            "if (a != null) {\n" +
            "  ((EntryPoint$A) a).fun();\n" +
            "}\n" +
            "if (a == null) {\n" +
            "  ((EntryPoint$B) a).fun();\n" +
            "}");
  }

  public void testConditions() throws Exception {
    optimize("void",
        "A a = new A();",
        "boolean flag = false;",
        "if (a != null && a instanceof A) {",
        "  ((A)a).fun();",
        "}",
        "if (a != null || a instanceof A) {",
        "  ((A)a).fun();",
        "}",
        "if ((a != null && a instanceof A) || a instanceof A) {",
        "  ((A)a).fun();",
        "}",
        "if (flag = a instanceof A) {",
        "  ((A)a).fun();",
        "  ((B)a).fun();",
        "}"
        )
        .intoString("EntryPoint$A a = new EntryPoint$A();\n" +
            "boolean flag = false;\n" +
            "if (a != null && a instanceof EntryPoint$A) {\n" +
            "  a.fun();\n" +
            "}\n" +
            "if (a != null || a instanceof EntryPoint$A) {\n" +
            "  ((EntryPoint$A) a).fun();\n" +
            "}\n" +
            "if (a != null && a instanceof EntryPoint$A || a instanceof EntryPoint$A) {\n" +
            "  a.fun();\n" +
            "}\n" +
            "if (flag = a instanceof EntryPoint$A) {\n" +
            "  a.fun();\n" +
            "  ((EntryPoint$B) a).fun();\n" +
            "}");
  }

  public void testSingleConditional() throws Exception {
    optimize("void",
        "A a = new A();",
        "int result = 0;",
        "result = (a instanceof A) ? ((A)a).fun() : ((B)a).fun();",
        "result = (a instanceof A) ? ((B)a).fun() : ((A)a).fun();",
        "((A)a).fun();",
        "((B)a).fun();")
        .intoString("EntryPoint$A a = new EntryPoint$A();\n" +
            "int result = 0;\n" +
            "result = a instanceof EntryPoint$A ? a.fun() : ((EntryPoint$B) a).fun();\n" +
            "result = a instanceof EntryPoint$A ? ((EntryPoint$B) a).fun() : ((EntryPoint$A) a).fun();\n" +
            "((EntryPoint$A) a).fun();\n" +
            "((EntryPoint$B) a).fun();");
  }

  public void testSingleIf() throws Exception {
    optimize("void",
        "A a = new A();",
        "if (a instanceof A) {",
        "  ((A)a).fun();",
        "  ((B)a).fun();",
        "}",
        "((A)a).fun();",
        "((B)a).fun();")
        .intoString("EntryPoint$A a = new EntryPoint$A();\n" +
            "if (a instanceof EntryPoint$A) {\n" +
            "  a.fun();\n" +
            "  ((EntryPoint$B) a).fun();\n" +
            "}\n" +
            "((EntryPoint$A) a).fun();\n" +
            "((EntryPoint$B) a).fun();");
  }

  public void testSingleIfElse() throws Exception {
    optimize("void",
        "A a = new A();",
        "if (a instanceof A) {",
        "  ((A)a).fun();",
        "  ((B)a).fun();",
        "}",
        "else {",
        "  ((A)a).fun();",
        "  ((B)a).fun();",
        "}",
        "((A)a).fun();",
        "((B)a).fun();")
        .intoString("EntryPoint$A a = new EntryPoint$A();\n" +
            "if (a instanceof EntryPoint$A) {\n" +
            "  a.fun();\n" +
            "  ((EntryPoint$B) a).fun();\n" +
            "} else {\n" +
            "  ((EntryPoint$A) a).fun();\n" +
            "  ((EntryPoint$B) a).fun();\n" +
            "}\n" +
            "((EntryPoint$A) a).fun();\n" +
            "((EntryPoint$B) a).fun();");
  }

  public void testSingleIfWithDef() throws Exception {
    optimize("void",
        "A b = new B();",
        "if (b instanceof B) {",
        "  ((B)b).fun();",
        "  ((A)b).fun();",
        "  b = new A();",
        "  ((B)b).fun();",
        "  ((A)b).fun();",
        "}",
        "((B)b).fun();",
        "((A)b).fun();")
        .intoString("EntryPoint$A b = new EntryPoint$B();\n" +
            "if (b instanceof EntryPoint$B) {\n" +
            "  b.fun();\n" +
            "  ((EntryPoint$A) b).fun();\n" +
            "  b = new EntryPoint$A();\n" +
            "  ((EntryPoint$B) b).fun();\n" +
            "  ((EntryPoint$A) b).fun();\n" +
            "}\n" +
            "((EntryPoint$B) b).fun();\n" +
            "((EntryPoint$A) b).fun();");
  }

  public void testSingleIfElseWithDef() throws Exception {
    optimize("void",
        "A b = new B();",
        "if (b instanceof B) {",
        "  ((B)b).fun();",
        "  ((A)b).fun();",
        "  b = new A();",
        "  ((B)b).fun();",
        "  ((A)b).fun();",
        "}",
        "else {",
        "  ((B)b).fun();",
        "  ((A)b).fun();",
        "}",
        "((B)b).fun();",
        "((A)b).fun();")
        .intoString("EntryPoint$A b = new EntryPoint$B();\n" +
            "if (b instanceof EntryPoint$B) {\n" +
            "  b.fun();\n" +
            "  ((EntryPoint$A) b).fun();\n" +
            "  b = new EntryPoint$A();\n" +
            "  ((EntryPoint$B) b).fun();\n" +
            "  ((EntryPoint$A) b).fun();\n" +
            "} else {\n" +
            "  ((EntryPoint$B) b).fun();\n" +
            "  ((EntryPoint$A) b).fun();\n" +
            "}\n" +
            "((EntryPoint$B) b).fun();\n" +
            "((EntryPoint$A) b).fun();");
  }

  public void testSingleWhile() throws Exception {
    optimize("void",
        "A a = new A();",
        "while (a instanceof A) {",
        "  ((A)a).fun();",
        "  ((B)a).fun();",
        "}",
        "((A)a).fun();",
        "((B)a).fun();")
        .intoString("EntryPoint$A a = new EntryPoint$A();\n" +
            "while (a instanceof EntryPoint$A) {\n" +
            "  a.fun();\n" +
            "  ((EntryPoint$B) a).fun();\n" +
            "}\n" +
            "((EntryPoint$A) a).fun();\n" +
            "((EntryPoint$B) a).fun();");
  }

  public void testSingleWhileWithDef() throws Exception {
    optimize("void",
        "A b = new B();",
        "while (b instanceof B) {",
        "  ((B)b).fun();",
        "  ((A)b).fun();",
        "  b = new A();",
        "  ((B)b).fun();",
        "  ((A)b).fun();",
        "}",
        "((B)b).fun();",
        "((A)b).fun();")
        .intoString("EntryPoint$A b = new EntryPoint$B();\n" +
            "while (b instanceof EntryPoint$B) {\n" +
            "  b.fun();\n" +
            "  ((EntryPoint$A) b).fun();\n" +
            "  b = new EntryPoint$A();\n" +
            "  ((EntryPoint$B) b).fun();\n" +
            "  ((EntryPoint$A) b).fun();\n" +
            "}\n" +
            "((EntryPoint$B) b).fun();\n" +
            "((EntryPoint$A) b).fun();");
  }

  public void testSingleFor() throws Exception {
    optimize("void",
        "A a = new A();",
        "for (int i = 0; a instanceof A; i++) {",
        "  ((A)a).fun();",
        "  ((B)a).fun();",
        "}",
        "((A)a).fun();",
        "((B)a).fun();")
        .intoString("EntryPoint$A a = new EntryPoint$A();\n" +
            "for (int i = 0; a instanceof EntryPoint$A; i++) {\n" +
            "  a.fun();\n" +
            "  ((EntryPoint$B) a).fun();\n" +
            "}\n" +
            "((EntryPoint$A) a).fun();\n" +
            "((EntryPoint$B) a).fun();");
  }

  public void testSingleForWithDef() throws Exception {
    optimize("void",
        "A b = new B();",
        "for (int i = 0; b instanceof B; i++) {",
        "  ((B)b).fun();",
        "  ((A)b).fun();",
        "  b = new A();",
        "  ((B)b).fun();",
        "  ((A)b).fun();",
        "}",
        "  ((B)b).fun();",
        "  ((A)b).fun();")
        .intoString("EntryPoint$A b = new EntryPoint$B();\n" +
            "for (int i = 0; b instanceof EntryPoint$B; i++) {\n" +
            "  b.fun();\n" +
            "  ((EntryPoint$A) b).fun();\n" +
            "  b = new EntryPoint$A();\n" +
            "  ((EntryPoint$B) b).fun();\n" +
            "  ((EntryPoint$A) b).fun();\n" +
            "}\n" +
            "((EntryPoint$B) b).fun();\n" +
            "((EntryPoint$A) b).fun();");
  }

  public void testNestedIf() throws Exception {
    optimize("void",
        "    A a = new A();\n",
        "    if (a instanceof B) {\n",
        "      ((C)a).fun();\n",
        "      ((B)a).fun();\n",
        "      ((A)a).fun();\n",
        "      if (a instanceof C) {\n",
        "        ((C)a).fun();\n",
        "        ((B)a).fun();\n",
        "        ((A)a).fun();\n",
        "      }\n",
        "      ((C)a).fun();\n",
        "      ((B)a).fun();\n",
        "      ((A)a).fun();\n",
        "    }",
        "    ((C)a).fun();\n",
        "    ((B)a).fun();\n",
        "    ((A)a).fun();\n")
        .intoString("EntryPoint$A a = new EntryPoint$A();\n" +
            "if (a instanceof EntryPoint$B) {\n" +
            "  ((EntryPoint$C) a).fun();\n" +
            "  a.fun();\n" +
            "  ((EntryPoint$A) a).fun();\n" +
            "  if (a instanceof EntryPoint$C) {\n" +
            "    a.fun();\n" +
            "    a.fun();\n" +
            "    ((EntryPoint$A) a).fun();\n" +
            "  }\n" +
            "  ((EntryPoint$C) a).fun();\n" +
            "  a.fun();\n" +
            "  ((EntryPoint$A) a).fun();\n" +
            "}\n" +
            "((EntryPoint$C) a).fun();\n" +
            "((EntryPoint$B) a).fun();\n" +
            "((EntryPoint$A) a).fun();");
  }

  public void testNestedIfWithDef() throws Exception {
    optimize("void",
        "    A a = new A();\n",
        "    if (a instanceof B) {\n",
        "      ((C)a).fun();\n",
        "      ((B)a).fun();\n",
        "      ((A)a).fun();\n",
        "      a = new B();\n",
        "      if (a instanceof C) {\n",
        "        ((C)a).fun();\n",
        "        ((B)a).fun();\n",
        "        ((A)a).fun();\n",
        "      }\n",
        "      ((C)a).fun();\n",
        "      ((B)a).fun();\n",
        "      ((A)a).fun();\n",
        "    }",
        "    ((C)a).fun();\n",
        "    ((B)a).fun();\n",
        "    ((A)a).fun();\n")
        .intoString("EntryPoint$A a = new EntryPoint$A();\n" +
            "if (a instanceof EntryPoint$B) {\n" +
            "  ((EntryPoint$C) a).fun();\n" +
            "  a.fun();\n" +
            "  ((EntryPoint$A) a).fun();\n" +
            "  a = new EntryPoint$B();\n" +
            "  if (a instanceof EntryPoint$C) {\n" +
            "    a.fun();\n" +
            "    ((EntryPoint$B) a).fun();\n" +
            "    ((EntryPoint$A) a).fun();\n" +
            "  }\n" +
            "  ((EntryPoint$C) a).fun();\n" +
            "  ((EntryPoint$B) a).fun();\n" +
            "  ((EntryPoint$A) a).fun();\n" +
            "}\n" +
            "((EntryPoint$C) a).fun();\n" +
            "((EntryPoint$B) a).fun();\n" +
            "((EntryPoint$A) a).fun();");
  }

  public void testNestedIfElse() throws Exception {
    optimize("void",
        "    A a = new A();\n" +
        "    if (a instanceof B) {\n" +
        "      ((C)a).fun();\n" +
        "      ((B)a).fun();\n" +
        "      ((A)a).fun();\n" +
        "      if (a instanceof C) {\n" +
        "        ((C)a).fun();\n" +
        "        ((B)a).fun();\n" +
        "        ((A)a).fun();\n" +
        "      }\n" +
        "      else {\n" +
        "        ((C)a).fun();\n" +
        "        ((B)a).fun();\n" +
        "        ((A)a).fun();\n" +
        "      }\n" +
        "      ((C)a).fun();\n" +
        "      ((B)a).fun();\n" +
        "      ((A)a).fun();\n" +
        "    }\n" +
        "    else {\n" +
        "      ((C)a).fun();\n" +
        "      ((B)a).fun();\n" +
        "      ((A)a).fun();\n" +
        "    }",
        "((C)a).fun();",
        "((B)a).fun();",
        "((A)a).fun();")
        .intoString("EntryPoint$A a = new EntryPoint$A();\n" +
            "if (a instanceof EntryPoint$B) {\n" +
            "  ((EntryPoint$C) a).fun();\n" +
            "  a.fun();\n" +
            "  ((EntryPoint$A) a).fun();\n" +
            "  if (a instanceof EntryPoint$C) {\n" +
            "    a.fun();\n" +
            "    a.fun();\n" +
            "    ((EntryPoint$A) a).fun();\n" +
            "  } else {\n" +
            "    ((EntryPoint$C) a).fun();\n" +
            "    a.fun();\n" +
            "    ((EntryPoint$A) a).fun();\n" +
            "  }\n" +
            "  ((EntryPoint$C) a).fun();\n" +
            "  a.fun();\n" +
            "  ((EntryPoint$A) a).fun();\n" +
            "} else {\n" +
            "  ((EntryPoint$C) a).fun();\n" +
            "  ((EntryPoint$B) a).fun();\n" +
            "  ((EntryPoint$A) a).fun();\n" +
            "}\n" +
            "((EntryPoint$C) a).fun();\n" +
            "((EntryPoint$B) a).fun();\n" +
            "((EntryPoint$A) a).fun();");
  }

  public void testNestedIfElseWithDef() throws Exception {
    optimize("void",
        "    A a = new A();\n" +
        "    if (a instanceof B) {\n" +
        "      ((C)a).fun();\n" +
        "      ((B)a).fun();\n" +
        "      ((A)a).fun();\n" +
        "      if (a instanceof C) {\n" +
        "        ((C)a).fun();\n" +
        "        ((B)a).fun();\n" +
        "        ((A)a).fun();\n" +
        "        a = new B();" +
        "        ((C)a).fun();\n" +
        "        ((B)a).fun();\n" +
        "        ((A)a).fun();\n" +
        "      }\n" +
        "      else {\n" +
        "        ((C)a).fun();\n" +
        "        ((B)a).fun();\n" +
        "        ((A)a).fun();\n" +
        "      }\n" +
        "      ((C)a).fun();\n" +
        "      ((B)a).fun();\n" +
        "      ((A)a).fun();\n" +
        "    }\n" +
        "    else {\n" +
        "      ((C)a).fun();\n" +
        "      ((B)a).fun();\n" +
        "      ((A)a).fun();\n" +
        "    }",
        "    ((C)a).fun();",
        "    ((B)a).fun();",
        "    ((A)a).fun();")
        .intoString("EntryPoint$A a = new EntryPoint$A();\n" +
            "if (a instanceof EntryPoint$B) {\n" +
            "  ((EntryPoint$C) a).fun();\n" +
            "  a.fun();\n" +
            "  ((EntryPoint$A) a).fun();\n" +
            "  if (a instanceof EntryPoint$C) {\n" +
            "    a.fun();\n" +
            "    a.fun();\n" +
            "    ((EntryPoint$A) a).fun();\n" +
            "    a = new EntryPoint$B();\n" +
            "    ((EntryPoint$C) a).fun();\n" +
            "    ((EntryPoint$B) a).fun();\n" +
            "    ((EntryPoint$A) a).fun();\n" +
            "  } else {\n" +
            "    ((EntryPoint$C) a).fun();\n" +
            "    a.fun();\n" +
            "    ((EntryPoint$A) a).fun();\n" +
            "  }\n" +
            "  ((EntryPoint$C) a).fun();\n" +
            "  ((EntryPoint$B) a).fun();\n" +
            "  ((EntryPoint$A) a).fun();\n" +
            "} else {\n" +
            "  ((EntryPoint$C) a).fun();\n" +
            "  ((EntryPoint$B) a).fun();\n" +
            "  ((EntryPoint$A) a).fun();\n" +
            "}\n" +
            "((EntryPoint$C) a).fun();\n" +
            "((EntryPoint$B) a).fun();\n" +
            "((EntryPoint$A) a).fun();");
  }

  public void testNestedWhile() throws Exception {
    optimize("void",
        "    A a = new A();\n",
        "    while (a instanceof B) {\n",
        "      ((C)a).fun();\n",
        "      ((B)a).fun();\n",
        "      ((A)a).fun();\n",
        "      while (a instanceof C) {\n",
        "        ((C)a).fun();\n",
        "        ((B)a).fun();\n",
        "        ((A)a).fun();\n",
        "        break;",
        "      }\n",
        "      ((C)a).fun();\n",
        "      ((B)a).fun();\n",
        "      ((A)a).fun();\n",
        "    }",
        "((C)a).fun();",
        "((B)a).fun();",
        "((A)a).fun();")
        .intoString("EntryPoint$A a = new EntryPoint$A();\n" +
            "while (a instanceof EntryPoint$B) {\n" +
            "  ((EntryPoint$C) a).fun();\n" +
            "  a.fun();\n" +
            "  ((EntryPoint$A) a).fun();\n" +
            "  while (a instanceof EntryPoint$C) {\n" +
            "    a.fun();\n" +
            "    ((EntryPoint$B) a).fun();\n" +
            "    ((EntryPoint$A) a).fun();\n" +
            "    break;\n" +
            "  }\n" +
            "  ((EntryPoint$C) a).fun();\n" +
            "  a.fun();\n" +
            "  ((EntryPoint$A) a).fun();\n" +
            "}\n" +
            "((EntryPoint$C) a).fun();\n" +
            "((EntryPoint$B) a).fun();\n" +
            "((EntryPoint$A) a).fun();");
  }

  public void testNestedWhileWithDef() throws Exception {
    optimize("void",
        "    A a = new A();\n",
        "    while (a instanceof B) {\n",
        "      ((C)a).fun();\n",
        "      ((B)a).fun();\n",
        "      ((A)a).fun();\n",
        "      while (a instanceof C) {\n",
        "        ((C)a).fun();\n",
        "        ((B)a).fun();\n",
        "        ((A)a).fun();\n",
        "        a = new B();\n",
        "        ((C)a).fun();\n",
        "        ((B)a).fun();\n",
        "        ((A)a).fun();\n",
        "        break;",
        "      }\n",
        "      ((C)a).fun();\n",
        "      ((B)a).fun();\n",
        "      ((A)a).fun();\n",
        "    }",
        "((C)a).fun();",
        "((B)a).fun();",
        "((A)a).fun();")
        .intoString("EntryPoint$A a = new EntryPoint$A();\n" +
            "while (a instanceof EntryPoint$B) {\n" +
            "  ((EntryPoint$C) a).fun();\n" +
            "  a.fun();\n" +
            "  ((EntryPoint$A) a).fun();\n" +
            "  while (a instanceof EntryPoint$C) {\n" +
            "    a.fun();\n" +
            "    ((EntryPoint$B) a).fun();\n" +
            "    ((EntryPoint$A) a).fun();\n" +
            "    a = new EntryPoint$B();\n" +
            "    ((EntryPoint$C) a).fun();\n" +
            "    ((EntryPoint$B) a).fun();\n" +
            "    ((EntryPoint$A) a).fun();\n" +
            "    break;\n" +
            "  }\n" +
            "  ((EntryPoint$C) a).fun();\n" +
            "  ((EntryPoint$B) a).fun();\n" +
            "  ((EntryPoint$A) a).fun();\n" +
            "}\n" +
            "((EntryPoint$C) a).fun();\n" +
            "((EntryPoint$B) a).fun();\n" +
            "((EntryPoint$A) a).fun();");
  }

  public void testNestedFor() throws Exception {
    optimize("void",
        "    A a = new A();\n",
        "    for (int i = 0; a instanceof B; i++) {\n",
        "      ((C)a).fun();\n",
        "      ((B)a).fun();\n",
        "      ((A)a).fun();\n",
        "      for (int j = 0; a instanceof C; j++) {\n",
        "        ((C)a).fun();\n",
        "        ((B)a).fun();\n",
        "        ((A)a).fun();\n",
        "        break;",
        "      }\n",
        "      ((C)a).fun();\n",
        "      ((B)a).fun();\n",
        "      ((A)a).fun();\n",
        "    }",
        "((C)a).fun();",
        "((B)a).fun();",
        "((A)a).fun();")
        .intoString("EntryPoint$A a = new EntryPoint$A();\n" +
            "for (int i = 0; a instanceof EntryPoint$B; i++) {\n" +
            "  ((EntryPoint$C) a).fun();\n" +
            "  a.fun();\n" +
            "  ((EntryPoint$A) a).fun();\n" +
            "  for (int j = 0; a instanceof EntryPoint$C; j++) {\n" +
            "    a.fun();\n" +
            "    ((EntryPoint$B) a).fun();\n" +
            "    ((EntryPoint$A) a).fun();\n" +
            "    break;\n" +
            "  }\n" +
            "  ((EntryPoint$C) a).fun();\n" +
            "  a.fun();\n" +
            "  ((EntryPoint$A) a).fun();\n" +
            "}\n" +
            "((EntryPoint$C) a).fun();\n" +
            "((EntryPoint$B) a).fun();\n" +
            "((EntryPoint$A) a).fun();");
  }

  public void testNestedForWithDef() throws Exception {
    optimize("void",
        "    A a = new A();\n",
        "    for (int i = 0; a instanceof B; i++) {\n",
        "      ((C)a).fun();\n",
        "      ((B)a).fun();\n",
        "      ((A)a).fun();\n",
        "      for (int j = 0; a instanceof C; j++) {\n",
        "        ((C)a).fun();\n",
        "        ((B)a).fun();\n",
        "        ((A)a).fun();\n",
        "        a = new B();\n",
        "        ((C)a).fun();\n",
        "        ((B)a).fun();\n",
        "        ((A)a).fun();\n",
        "        break;",
        "      }\n",
        "      ((C)a).fun();\n",
        "      ((B)a).fun();\n",
        "      ((A)a).fun();\n",
        "    }",
        "((C)a).fun();",
        "((B)a).fun();",
        "((A)a).fun();")
        .intoString("EntryPoint$A a = new EntryPoint$A();\n" +
            "for (int i = 0; a instanceof EntryPoint$B; i++) {\n" +
            "  ((EntryPoint$C) a).fun();\n" +
            "  a.fun();\n" +
            "  ((EntryPoint$A) a).fun();\n" +
            "  for (int j = 0; a instanceof EntryPoint$C; j++) {\n" +
            "    a.fun();\n" +
            "    ((EntryPoint$B) a).fun();\n" +
            "    ((EntryPoint$A) a).fun();\n" +
            "    a = new EntryPoint$B();\n" +
            "    ((EntryPoint$C) a).fun();\n" +
            "    ((EntryPoint$B) a).fun();\n" +
            "    ((EntryPoint$A) a).fun();\n" +
            "    break;\n" +
            "  }\n" +
            "  ((EntryPoint$C) a).fun();\n" +
            "  ((EntryPoint$B) a).fun();\n" +
            "  ((EntryPoint$A) a).fun();\n" +
            "}\n" +
            "((EntryPoint$C) a).fun();\n" +
            "((EntryPoint$B) a).fun();\n" +
            "((EntryPoint$A) a).fun();");
  }

  public void testNestedAll() throws Exception {
    optimize("void",
        "    A a = new A();\n" +
        "    if (a instanceof A) {\n" +
        "      ((C)a).fun();\n" +
        "      ((B)a).fun();\n" +
        "      ((A)a).fun();\n" +
        "      while (a instanceof B) {\n" +
        "        ((C)a).fun();\n" +
        "        ((B)a).fun();\n" +
        "        ((A)a).fun();\n" +
        "        for (int i = 0; a instanceof C; i++) {\n" +
        "          ((C)a).fun();\n" +
        "          ((B)a).fun();\n" +
        "          ((A)a).fun();\n" +
        "          int result = (a instanceof D) ? ((D)a).fun() : ((A)a).fun();",
        "          result = ((D)a).fun();",
        "          break;\n" +
        "        }\n" +
        "        ((C)a).fun();\n" +
        "        ((B)a).fun();\n" +
        "        ((A)a).fun();\n" +
        "        break;\n" +
        "      }\n" +
        "      ((C)a).fun();\n" +
        "      ((B)a).fun();\n" +
        "      ((A)a).fun();\n" +
        "    }\n" +
        "    else{\n" +
        "      ((C)a).fun();\n" +
        "      ((B)a).fun();\n" +
        "      ((A)a).fun();\n" +
        "    }\n" +
        "    ((C)a).fun();\n" +
        "    ((B)a).fun();\n" +
        "    ((A)a).fun();")
        .intoString("EntryPoint$A a = new EntryPoint$A();\n" +
            "if (a instanceof EntryPoint$A) {\n" +
            "  ((EntryPoint$C) a).fun();\n" +
            "  ((EntryPoint$B) a).fun();\n" +
            "  a.fun();\n" +
            "  while (a instanceof EntryPoint$B) {\n" +
            "    ((EntryPoint$C) a).fun();\n" +
            "    a.fun();\n" +
            "    ((EntryPoint$A) a).fun();\n" +
            "    for (int i = 0; a instanceof EntryPoint$C; i++) {\n" +
            "      a.fun();\n" +
            "      ((EntryPoint$B) a).fun();\n" +
            "      ((EntryPoint$A) a).fun();\n" +
            "      int result = a instanceof EntryPoint$D ? a.fun() : ((EntryPoint$A) a).fun();\n" +
            "      result = ((EntryPoint$D) a).fun();\n" +
            "      break;\n" +
            "    }\n" +
            "    ((EntryPoint$C) a).fun();\n" +
            "    a.fun();\n" +
            "    ((EntryPoint$A) a).fun();\n" +
            "    break;\n" +
            "  }\n" +
            "  ((EntryPoint$C) a).fun();\n" +
            "  ((EntryPoint$B) a).fun();\n" +
            "  a.fun();\n" +
            "} else {\n" +
            "  ((EntryPoint$C) a).fun();\n" +
            "  ((EntryPoint$B) a).fun();\n" +
            "  ((EntryPoint$A) a).fun();\n" +
            "}\n" +
            "((EntryPoint$C) a).fun();\n" +
            "((EntryPoint$B) a).fun();\n" +
            "((EntryPoint$A) a).fun();");
  }

  public void testNestedWithDefs() throws Exception {
    optimize("void",
            "    A a = new A();\n" +
            "    if (a instanceof A) {\n" +
            "      ((C)a).fun();\n" +
            "      ((B)a).fun();\n" +
            "      ((A)a).fun();\n" +
            "      while (a instanceof B) {\n" +
            "        ((C)a).fun();\n" +
            "        ((B)a).fun();\n" +
            "        ((A)a).fun();\n" +
            "        a = new B();\n" +
            "        for (int i = 0; a instanceof C; i++) {\n" +
            "          ((C)a).fun();\n" +
            "          ((B)a).fun();\n" +
            "          ((A)a).fun();\n" +
            "          break;\n" +
            "        }\n" +
            "        ((C)a).fun();\n" +
            "        ((B)a).fun();\n" +
            "        ((A)a).fun();\n" +
            "        break;\n" +
            "      }\n" +
            "      ((C)a).fun();\n" +
            "      ((B)a).fun();\n" +
            "      ((A)a).fun();\n" +
            "    }\n" +
            "    else{\n" +
            "      ((C)a).fun();\n" +
            "      ((B)a).fun();\n" +
            "      ((A)a).fun();\n" +
            "    }\n" +
            "    ((C)a).fun();\n" +
            "    ((B)a).fun();\n" +
            "    ((A)a).fun();")
        .intoString("EntryPoint$A a = new EntryPoint$A();\n" +
            "if (a instanceof EntryPoint$A) {\n" +
            "  ((EntryPoint$C) a).fun();\n" +
            "  ((EntryPoint$B) a).fun();\n" +
            "  a.fun();\n" +
            "  while (a instanceof EntryPoint$B) {\n" +
            "    ((EntryPoint$C) a).fun();\n" +
            "    a.fun();\n" +
            "    ((EntryPoint$A) a).fun();\n" +
            "    a = new EntryPoint$B();\n" +
            "    for (int i = 0; a instanceof EntryPoint$C; i++) {\n" +
            "      a.fun();\n" +
            "      ((EntryPoint$B) a).fun();\n" +
            "      ((EntryPoint$A) a).fun();\n" +
            "      break;\n" +
            "    }\n" +
            "    ((EntryPoint$C) a).fun();\n" +
            "    ((EntryPoint$B) a).fun();\n" +
            "    ((EntryPoint$A) a).fun();\n" +
            "    break;\n" +
            "  }\n" +
            "  ((EntryPoint$C) a).fun();\n" +
            "  ((EntryPoint$B) a).fun();\n" +
            "  ((EntryPoint$A) a).fun();\n" +
            "} else {\n" +
            "  ((EntryPoint$C) a).fun();\n" +
            "  ((EntryPoint$B) a).fun();\n" +
            "  ((EntryPoint$A) a).fun();\n" +
            "}\n" +
            "((EntryPoint$C) a).fun();\n" +
            "((EntryPoint$B) a).fun();\n" +
            "((EntryPoint$A) a).fun();");
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
        "      for (; ((B)a).fun()==1; a = null) {\n" +
        "        ((B)a).fun();\n" +
        "      }\n" +
        "      ((B)a).fun();\n" +
        "    }\n" + // test definitions in for statement.
        "    if (a instanceof B && b instanceof B) {\n" +
        "      ((B)a).fun();\n" +
        "      ((B)b).fun();\n" +
        "      if (a == null) {\n" +
        "        a = null;\n" +
        "      }\n" +
        "      else {\n" +
        "        b = null;\n" +
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
            "  for (; ((EntryPoint$B) a).fun() == 1; a = null) {\n" +
            "    ((EntryPoint$B) a).fun();\n" +
            "  }\n" +
            "  ((EntryPoint$B) a).fun();\n" +
            "}\n" +
            "if (a instanceof EntryPoint$B && b instanceof EntryPoint$B) {\n" +
            "  a.fun();\n" +
            "  b.fun();\n" +
            "  if (a == null) {\n" +
            "    a = null;\n" +
            "  } else {\n" +
            "    b = null;\n" +
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
            "    if (a instanceof A && b instanceof A) {\n" +
            "      ((C)a).fun();\n" +
            "      ((B)a).fun();\n" +
            "      ((A)a).fun();\n" +
            "      ((C)b).fun();\n" +
            "      ((B)b).fun();\n" +
            "      ((A)b).fun();\n" +
            "      while (a instanceof B) {\n" +
            "        ((C)a).fun();\n" +
            "        ((B)a).fun();\n" +
            "        ((A)a).fun();\n" +
            "        ((C)b).fun();\n" +
            "        ((B)b).fun();\n" +
            "        ((A)b).fun();\n" +
            "        a = new B();\n" +
            "        ((C)a).fun();\n" +
            "        ((B)a).fun();\n" +
            "        ((A)a).fun();\n" +
            "        ((C)b).fun();\n" +
            "        ((B)b).fun();\n" +
            "        ((A)b).fun();\n" +
            "        int result = (b instanceof D) ? ((D)b).fun() : ((D)b).fun();" +
            "        result = (a instanceof C) ? ((C)a).fun() : ((B)a).fun();" +
            "        for (int i = 0; b instanceof C; i++) {\n" +
            "          ((C)a).fun();\n" +
            "          ((B)a).fun();\n" +
            "          ((A)a).fun();\n" +
            "          ((C)b).fun();\n" +
            "          ((B)b).fun();\n" +
            "          ((A)b).fun();\n" +
            "          b = new B();\n" +
            "          ((C)a).fun();\n" +
            "          ((B)a).fun();\n" +
            "          ((A)a).fun();\n" +
            "          ((C)b).fun();\n" +
            "          ((B)b).fun();\n" +
            "          ((A)b).fun();\n" +
            "          break;\n" +
            "        }\n" +
            "        ((C)a).fun();\n" +
            "        ((B)a).fun();\n" +
            "        ((A)a).fun();\n" +
            "        ((C)b).fun();\n" +
            "        ((B)b).fun();\n" +
            "        ((A)b).fun();\n" +
            "        break;\n" +
            "      }\n" +
            "      ((C)a).fun();\n" +
            "      ((B)a).fun();\n" +
            "      ((A)a).fun();\n" +
            "      ((C)b).fun();\n" +
            "      ((B)b).fun();\n" +
            "      ((A)b).fun();\n" +
            "    }\n" +
            "    else{\n" +
            "      ((C)a).fun();\n" +
            "      ((B)a).fun();\n" +
            "      ((A)a).fun();\n" +
            "      ((C)b).fun();\n" +
            "      ((B)b).fun();\n" +
            "      ((A)b).fun();\n" +
            "    }\n" +
            "    ((C)a).fun();\n" +
            "    ((B)a).fun();\n" +
            "    ((A)a).fun();" +
            "    ((C)b).fun();\n" +
            "    ((B)b).fun();\n" +
            "    ((A)b).fun();\n")
        .intoString("EntryPoint$A a = new EntryPoint$A();\n" +
            "EntryPoint$A b = new EntryPoint$A();\n" +
            "if (a instanceof EntryPoint$A && b instanceof EntryPoint$A) {\n" +
            "  ((EntryPoint$C) a).fun();\n" +
            "  ((EntryPoint$B) a).fun();\n" +
            "  a.fun();\n" +
            "  ((EntryPoint$C) b).fun();\n" +
            "  ((EntryPoint$B) b).fun();\n" +
            "  b.fun();\n" +
            "  while (a instanceof EntryPoint$B) {\n" +
            "    ((EntryPoint$C) a).fun();\n" +
            "    a.fun();\n" +
            "    ((EntryPoint$A) a).fun();\n" +
            "    ((EntryPoint$C) b).fun();\n" +
            "    ((EntryPoint$B) b).fun();\n" +
            "    ((EntryPoint$A) b).fun();\n" +
            "    a = new EntryPoint$B();\n" +
            "    ((EntryPoint$C) a).fun();\n" +
            "    ((EntryPoint$B) a).fun();\n" +
            "    ((EntryPoint$A) a).fun();\n" +
            "    ((EntryPoint$C) b).fun();\n" +
            "    ((EntryPoint$B) b).fun();\n" +
            "    ((EntryPoint$A) b).fun();\n" +
            "    int result = b instanceof EntryPoint$D ? b.fun() : ((EntryPoint$D) b).fun();\n" +
            "    result = a instanceof EntryPoint$C ? a.fun() : ((EntryPoint$B) a).fun();\n" +
            "    for (int i = 0; b instanceof EntryPoint$C; i++) {\n" +
            "      ((EntryPoint$C) a).fun();\n" +
            "      ((EntryPoint$B) a).fun();\n" +
            "      ((EntryPoint$A) a).fun();\n" +
            "      b.fun();\n" +
            "      ((EntryPoint$B) b).fun();\n" +
            "      ((EntryPoint$A) b).fun();\n" +
            "      b = new EntryPoint$B();\n" +
            "      ((EntryPoint$C) a).fun();\n" +
            "      ((EntryPoint$B) a).fun();\n" +
            "      ((EntryPoint$A) a).fun();\n" +
            "      ((EntryPoint$C) b).fun();\n" +
            "      ((EntryPoint$B) b).fun();\n" +
            "      ((EntryPoint$A) b).fun();\n" +
            "      break;\n" +
            "    }\n" +
            "    ((EntryPoint$C) a).fun();\n" +
            "    ((EntryPoint$B) a).fun();\n" +
            "    ((EntryPoint$A) a).fun();\n" +
            "    ((EntryPoint$C) b).fun();\n" +
            "    ((EntryPoint$B) b).fun();\n" +
            "    ((EntryPoint$A) b).fun();\n" +
            "    break;\n" +
            "  }\n" +
            "  ((EntryPoint$C) a).fun();\n" +
            "  ((EntryPoint$B) a).fun();\n" +
            "  ((EntryPoint$A) a).fun();\n" +
            "  ((EntryPoint$C) b).fun();\n" +
            "  ((EntryPoint$B) b).fun();\n" +
            "  ((EntryPoint$A) b).fun();\n" +
            "} else {\n" +
            "  ((EntryPoint$C) a).fun();\n" +
            "  ((EntryPoint$B) a).fun();\n" +
            "  ((EntryPoint$A) a).fun();\n" +
            "  ((EntryPoint$C) b).fun();\n" +
            "  ((EntryPoint$B) b).fun();\n" +
            "  ((EntryPoint$A) b).fun();\n" +
            "}\n" +
            "((EntryPoint$C) a).fun();\n" +
            "((EntryPoint$B) a).fun();\n" +
            "((EntryPoint$A) a).fun();\n" +
            "((EntryPoint$C) b).fun();\n" +
            "((EntryPoint$B) b).fun();\n" +
            "((EntryPoint$A) b).fun();");
  }

  @Override
  protected boolean optimizeMethod(JProgram program, JMethod method) {
    program.addEntryMethod(findMainMethod(program));
    return RedundantCastRemover.exec(program).didChange();
  }
}
