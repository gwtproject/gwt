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

import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.dev.MinimalRebuildCache;
import com.google.gwt.dev.javac.testing.impl.MockJavaResource;
import com.google.gwt.dev.jjs.ast.JMethod;
import com.google.gwt.dev.jjs.ast.JProgram;

/**
 * Tests for the JsInteropRestrictionChecker.
 */
public class JsInteropRestrictionCheckerTest extends OptimizerTestBase {

  public void testCollidingFieldExportsFails() throws Exception {
    addSnippetImport("com.google.gwt.core.client.js.JsExport");
    addSnippetClassDecl(
        "public static class Buggy {",
        "  @JsExport(\"show\")",
        "  public static final int show = 0;",
        "  @JsExport(\"show\")",
        "  public static final int display = 0;",
        "}");

    assertCompileFails();
  }

  public void testCollidingJsTypeJsPropertiesSucceeds() throws Exception {
    addSnippetImport("com.google.gwt.core.client.js.JsType");
    addSnippetImport("com.google.gwt.core.client.js.JsProperty");
    addSnippetClassDecl(
        "@JsType",
        "public static interface IBuggy {",
        "  @JsProperty",
        "  int x();",
        "  @JsProperty",
        "  void x(int x);",
        "}",
        "public static class Buggy implements IBuggy {",
        "  public int x() {return 0;}",
        "  public void x(int x) {}",
        "}");

    assertCompileSucceeds();
  }

  public void testCollidingMethodExportsFails() throws Exception {
    addSnippetImport("com.google.gwt.core.client.js.JsExport");
    addSnippetClassDecl(
        "public static class Buggy {",
        "  @JsExport(\"show\")",
        "  public static void show() {}",
        "  @JsExport(\"show\")",
        "  public static void display() {}",
        "}");

    assertCompileFails();
  }

  public void testCollidingMethodToFieldExportsFails() throws Exception {
    addSnippetImport("com.google.gwt.core.client.js.JsExport");
    addSnippetClassDecl(
        "public static class Buggy {",
        "  @JsExport(\"show\")",
        "  public static void show() {}",
        "  @JsExport(\"show\")",
        "  public static final int display = 0;",
        "}");

    assertCompileFails();
  }

  public void testCollidingMethodToFieldJsTypeFails() throws Exception {
    addSnippetImport("com.google.gwt.core.client.js.JsType");
    addSnippetClassDecl(
        "@JsType",
        "public static class Buggy {",
        "  public void show() {}",
        "  public final int show = 0;",
        "}");

    assertCompileFails();
  }

  public void testCollidingSyntheticBridgeMethodSucceeds() throws Exception {
    addSnippetImport("com.google.gwt.core.client.js.JsType");
    addSnippetImport("com.google.gwt.core.client.js.JsProperty");
    addSnippetClassDecl(
        "public static interface Comparable<T> {",
        "  int compareTo(T other);",
        "}",
        "@JsType",
        "public static class Enum<E extends Enum<E>> implements Comparable<E> {",
        "  public int compareTo(E other) {return 0;}",
        "}",
        "public static class Buggy {}");

    assertCompileSucceeds();
  }

  public void testMultiplePrivateConstructorsExportSucceeds() throws Exception {
    addSnippetImport("com.google.gwt.core.client.js.JsExport");
    addSnippetClassDecl(
        "@JsExport",
        "public static class Buggy {",
        "  private Buggy() {}",
        "  private Buggy(int a) {}",
        "}");

    assertCompileSucceeds();
  }

  public void testMultiplePublicConstructorsExportFails() throws Exception {
    addSnippetImport("com.google.gwt.core.client.js.JsExport");
    addSnippetClassDecl(
        "@JsExport",
        "public static class Buggy {",
        "  public Buggy() {}",
        "  public Buggy(int a) {}",
        "}");

    assertCompileFails();
  }

  public void testSingleExportSucceeds() throws Exception {
    addSnippetImport("com.google.gwt.core.client.js.JsExport");
    addSnippetClassDecl(
        "public static class Buggy {",
        "  @JsExport(\"show\")",
        "  public static void show() {}",
        "}");

    assertCompileSucceeds();
  }

  public void testSingleJsTypeSucceeds() throws Exception {
    addSnippetImport("com.google.gwt.core.client.js.JsType");
    addSnippetClassDecl(
        "@JsType",
        "public static class Buggy {",
        "  public void show() {}",
        "}");

    assertCompileSucceeds();
  }

  public void testSingleInterfaceSucceeds() throws Exception {
    addMockResources(jsFunctionInterface1);
    addSnippetClassDecl(
        "public static class Buggy implements MyJsFunctionInterface1 {\n",
        "public int foo(int x) { return 0; }\n",
        "}\n");
    assertCompileSucceeds();
  }

  public void testOneJsFunctionAndOneNonJsFunctionSucceeds() throws Exception {
    addMockResources(jsFunctionInterface1, plainInterface);
    addSnippetClassDecl(
        "public static class Buggy implements MyJsFunctionInterface1, MyPlainInterface {\n",
        "public int foo(int x) { return 0; }\n",
        "}\n");
    assertCompileSucceeds();
  }

  public void testSameJsFunctionInBothSuperClassAndSuperInterfaceSucceeds() throws Exception {
    addMockResources(jsFunctionInterface1, plainInterface, jsFunctionInterfaceImpl);
    addSnippetClassDecl(
        "public static class Buggy extends MyJsFunctionInterfaceImpl "
        + "implements MyJsFunctionInterface1, MyPlainInterface {\n",
        "public int foo(int x) { return 0; }\n",
        "}\n");
    assertCompileSucceeds();
  }

  public void testMultipleInterfacesFails() throws Exception {
    addMockResources(jsFunctionInterface1, jsFunctionInterface2);
    addSnippetClassDecl(
        "public static class Buggy implements MyJsFunctionInterface1, MyJsFunctionInterface2 {\n",
        "public int foo(int x) { return 0; }\n",
        "public int bar(int x) { return 0; }\n",
        "}\n");
    assertCompileFails();
  }

  public void testMultipleInterfacesWithSameMethodSignatureFails() throws Exception {
    addMockResources(jsFunctionInterface1, jsFunctionInterface3);
    addSnippetClassDecl(
        "public static class Buggy implements MyJsFunctionInterface1, MyJsFunctionInterface3 {\n",
        "public int foo(int x) { return 0; }\n",
        "}\n");
    assertCompileFails();
  }

  public void testMultipleInterfacesFromSuperClassAndSuperInterfaceFails() throws Exception {
    addMockResources(jsFunctionInterface1, jsFunctionInterface3, jsFunctionInterfaceImpl);
    addSnippetClassDecl(
        "public static class Buggy extends MyJsFunctionInterfaceImpl "
        + "implements MyJsFunctionInterface3 {\n",
        "public int foo(int x) { return 0; }\n",
        "}\n");
    assertCompileFails();
  }

  public void testMultipleInterfacesFromSuperClassAndSuperSuperInterfaceFails() throws Exception {
    addMockResources(jsFunctionSubInterface, jsFunctionInterface1, jsFunctionInterfaceImpl,
        jsFunctionInterface3);
    addSnippetClassDecl(
        "public static class Buggy extends MyJsFunctionInterfaceImpl "
        + "implements MyJsFunctionSubInterface {\n",
        "public int foo(int x) { return 0; }\n",
        "}\n");
    assertCompileFails();
  }

  MockJavaResource jsFunctionInterface1 = new MockJavaResource(
      "test.MyJsFunctionInterface1") {
    @Override
    public CharSequence getContent() {
      StringBuilder code = new StringBuilder();
      code.append("package test;\n");
      code.append("import com.google.gwt.core.client.js.JsFunction;\n");
      code.append("@JsFunction public interface MyJsFunctionInterface1 {\n");
      code.append("int foo(int x);\n");
      code.append("}\n");
      return code;
    }
  };

  MockJavaResource jsFunctionInterface2 = new MockJavaResource(
      "test.MyJsFunctionInterface2") {
    @Override
    public CharSequence getContent() {
      StringBuilder code = new StringBuilder();
      code.append("package test;\n");
      code.append("import com.google.gwt.core.client.js.JsFunction;\n");
      code.append("@JsFunction public interface MyJsFunctionInterface2 {\n");
      code.append("int bar(int x);\n");
      code.append("}\n");
      return code;
    }
  };

  MockJavaResource jsFunctionInterface3 = new MockJavaResource(
      "test.MyJsFunctionInterface3") {
    @Override
    public CharSequence getContent() {
      StringBuilder code = new StringBuilder();
      code.append("package test;\n");
      code.append("import com.google.gwt.core.client.js.JsFunction;\n");
      code.append("@JsFunction public interface MyJsFunctionInterface3 {\n");
      code.append("int foo(int x);\n");
      code.append("}\n");
      return code;
    }
  };

  MockJavaResource plainInterface = new MockJavaResource(
      "test.MyPlainInterface") {
    @Override
    public CharSequence getContent() {
      StringBuilder code = new StringBuilder();
      code.append("package test;\n");
      code.append("public interface MyPlainInterface {\n");
      code.append("int foo(int x);\n");
      code.append("}\n");
      return code;
    }
  };

  MockJavaResource jsFunctionSubInterface = new MockJavaResource(
      "test.MyJsFunctionSubInterface") {
    @Override
    public CharSequence getContent() {
      StringBuilder code = new StringBuilder();
      code.append("package test;\n");
      code.append("public interface MyJsFunctionSubInterface extends MyJsFunctionInterface3 {\n");
      code.append("}\n");
      return code;
    }
  };

  MockJavaResource jsFunctionInterfaceImpl = new MockJavaResource(
      "test.MyJsFunctionInterfaceImpl") {
    @Override
    public CharSequence getContent() {
      StringBuilder code = new StringBuilder();
      code.append("package test;\n");
      code.append("public class MyJsFunctionInterfaceImpl implements MyJsFunctionInterface1 {\n");
      code.append("public int foo(int x) { return 1; }\n");
      code.append("}\n");
      return code;
    }
  };

  @Override
  protected boolean optimizeMethod(JProgram program, JMethod method) {
    try {
      JsInteropRestrictionChecker.exec(TreeLogger.NULL, program, new MinimalRebuildCache());
    } catch (UnableToCompleteException e) {
      throw new RuntimeException(e);
    }
    return false;
  }

  private void assertCompileFails() {
    try {
      optimize("void", "new Buggy();");
      fail("JsInteropRestrictionCheckerTest should have prevented the name collision.");
    } catch (Exception e) {
      assertTrue(e.getCause() instanceof UnableToCompleteException
          || e instanceof UnableToCompleteException);
    }
  }

  private void assertCompileSucceeds() throws UnableToCompleteException {
    optimize("void", "new Buggy();");
  }
}
