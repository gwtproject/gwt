package com.google.gwt.dev.jjs.impl;

import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.dev.jjs.ast.JMethod;
import com.google.gwt.dev.jjs.ast.JProgram;

public class ExpandBlocksTest extends OptimizerTestBase {

  public void testSimpleIf() throws Exception {
    addSnippetClassDecl(
        "static class A { ",
        "  static boolean a = true;",
        "  static int b = 0;",
        "  static int c = 0;",
        "}");
    optimize("void", "if (A.a) { return; } A.b++;")
        .into("if (A.a) { return; } else { A.b++; } ");
    optimize("void", "if (A.a) { return; } else { A.b++; } A.c++;")
        .into("if (A.a) { return; } else { A.b++; A.c++; }");
    //TODO incomplete:
    optimize("void", "if (A.a) { return; } else if (A.b == 0) { A.b++; } else { return; } A.c++;")
        .into("if (A.a) { return; } else { if (A.b == 0) { A.b++; } else { return; } A.c++;}");
// TODO do this instead
    //    optimize("void", "if (A.a) { return; } else if (A.b == 0) { A.b++; } else { return; } A.c++;")
//        .into("if (A.a) { return; } else if (A.b == 0) { A.b++; A.c++; } else { return; }");
  }

  public void testNestedIf() throws Exception {
    addSnippetClassDecl(
        "static class A { ",
        "  static boolean a = true;",// bool condition
        "  static int b = 0;",
        "  static int c = 0;",// statement expected to be moved
        "  static int d = 0;",
        "  static int method() { return b + c + d; }",
        "}");
    optimize("void", "while(A.a) { A.b++; if (A.a) { break; } A.c++; } A.d++;")
        .into("while(A.a) { A.b++; if (A.a) { break; } else { A.c++; } } A.d++;");
    optimize("void", "while(A.a) { A.b++; if (A.a) { continue; } else { A.b++; } A.c++; } A.d++;")
        .into("while(A.a) { A.b++; if (A.a) { continue; } else { A.b++; A.c++; } } A.d++;");

    optimize("void",
        "if (A.a) {" +
            "  return;",
        "}",
        "A.c++;",
        "if (A.a) {",
        "  while (A.a) {",
        "    A.b++;",
        "    if (A.a) {",
        "      break;",
        "    }",
        "    A.c++;",
        "  }",
        "}"
        ).into(
            "if (A.a) {",
            "  return;",
            "} else {",
            "  A.c++;",
            "  if (A.a) {",
            "    while (A.a) {",
            "      A.b++;",
            "      if (A.a) {",
            "        break;",
            "      } else {",
            "        A.c++;",
            "      }",
            "    }",
            "  }",
            "}"
    );
  }

  public void testImplEntry() throws Exception {
    addSnippetClassDecl(
        "static class A { ",
        "  static boolean a = true;",// bool condition
        "  static int b = 0;",
        "  static int c = 0;",// statement expected to be moved
        "  static int d = 0;",
        "  static int method() { return b + c + d; }",
        "}");

    // A method that looks a little like Impl.entry0(), as a real life test case
    optimize("int",
        "try {" +
            "  if (A.a) {",
        "    try {",
        "      return A.method();",
        "    } catch (Exception e) {",
        "      return -1;",
        "    }",
        "  } else {",
        "    return A.method();",
        "  }",
        "} finally {",
        "  A.c++;",
        "}"
    ).into(
        "try {" +
            "  if (A.a) {",
        "    try {",
        "      return A.method();",
        "    } catch (Exception e) {",
        "      return -1;",
        "    }",
        "  } else {",
        "    return A.method();",
        "  }",
        "} finally {",
        "  A.c++;",
        "}"
    );
  }

  @Override
  protected boolean doOptimizeMethod(TreeLogger logger, JProgram program, JMethod method) throws UnableToCompleteException {
    ExpandBlocks.exec(program);
    return true;
  }
}
