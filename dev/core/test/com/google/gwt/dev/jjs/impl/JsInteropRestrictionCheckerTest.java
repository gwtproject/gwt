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

  public void testCollidingJsPropertiesSetterAndGetterSucceeds() throws Exception {
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

  public void testCollidingJsPropertiesTwoGettersFails() throws Exception {
    addSnippetImport("com.google.gwt.core.client.js.JsType");
    addSnippetImport("com.google.gwt.core.client.js.JsProperty");
    addSnippetClassDecl(
        "@JsType",
        "public static interface IBuggy {",
        "  @JsProperty",
        "  int x();",
        "  @JsProperty",
        "  int getX();",
        "}",
        "public static class Buggy implements IBuggy {",
        "  public int x() {return 0;}",
        "  public int getX() {return 0;}",
        "}");

    assertCompileFails();
  }

  public void testCollidingJsPropertiesTwoSettersFails() throws Exception {
    addSnippetImport("com.google.gwt.core.client.js.JsType");
    addSnippetImport("com.google.gwt.core.client.js.JsProperty");
    addSnippetClassDecl(
        "@JsType",
        "public static interface IBuggy {",
        "  @JsProperty",
        "  void x(int x);",
        "  @JsProperty",
        "  void setX(int x);",
        "}",
        "public static class Buggy implements IBuggy {",
        "  public void x(int x) {}",
        "  public void setX(int x) {}",
        "}");

    assertCompileFails();
  }

  public void testCollidingJsTypeAndJsPropertyGetterFails() throws Exception {
    addSnippetImport("com.google.gwt.core.client.js.JsType");
    addSnippetImport("com.google.gwt.core.client.js.JsProperty");
    addSnippetClassDecl(
        "@JsType",
        "public static interface IBuggy {",
        "  Object x(Object foo, Object bar);",
        "  @JsProperty",
        "  int getX();",
        "}",
        "public static class Buggy implements IBuggy {",
        "  public Object x(Object foo, Object bar) {return null;}",
        "  public int getX() {return 0;}",
        "}");

    assertCompileFails();
  }

  public void testCollidingJsTypeAndJsPropertySetterFails() throws Exception {
    addSnippetImport("com.google.gwt.core.client.js.JsType");
    addSnippetImport("com.google.gwt.core.client.js.JsProperty");
    addSnippetClassDecl(
        "@JsType",
        "public static interface IBuggy {",
        "  Object x(Object foo, Object bar);",
        "  @JsProperty",
        "  void setX(int a);",
        "}",
        "public static class Buggy implements IBuggy {",
        "  public Object x(Object foo, Object bar) {return null;}",
        "  public void setX(int a) {}",
        "}");

    assertCompileFails();
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

  // TODO: enable when supported.
  // public void testCollidingMethodToMethodJsTypeFails() throws Exception {
  // addSnippetImport("com.google.gwt.core.client.js.JsType");
  // addSnippetClassDecl(
  // "@JsType",
  // "public interface IBuggy1 {",
  // "  void show();",
  // "}",
  // "@JsType",
  // "public interface IBuggy2 {",
  // "  void show(boolean b);",
  // "}",
  // "public static class Buggy implements IBuggy1 {",
  // "  public void show() {}",
  // "}",
  // "public static class Buggy2 extends Buggy implements IBuggy2 {",
  // "  public void show(boolean b) {}",
  // "}");
  //
  // assertCompileFails();
  // }

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

  public void testJsPropertyInNonJsTypeFails() throws Exception {
    addSnippetImport("com.google.gwt.core.client.js.JsType");
    addSnippetImport("com.google.gwt.core.client.js.JsProperty");
    addSnippetClassDecl(
        "public static class Buggy {",
        "  @JsProperty",
        "  public int x() {return 0;}",
        "}");

    assertCompileFails();
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
