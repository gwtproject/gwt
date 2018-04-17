/*
 * Copyright 2017 Google Inc.
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

import com.google.gwt.dev.javac.testing.impl.JavaResourceBase;
import com.google.gwt.dev.jjs.ast.JInterfaceType;
import com.google.gwt.dev.jjs.ast.JMethod;
import com.google.gwt.dev.jjs.ast.JMethodBody;
import com.google.gwt.dev.jjs.ast.JProgram;

/**
 * Tests that {@link com.google.gwt.dev.jjs.impl.GwtAstBuilder} correctly builds the AST for
 * features introduced in Java 9.
 */
public class Java9AstTest extends FullCompileTestBase {

  @Override
  public void setUp() throws Exception {
    super.setUp();
    addAll(
        JavaResourceBase.createMockJavaResource("test.InterfaceWithPrivateMethods",
            "package test;",
            "public interface InterfaceWithPrivateMethods {",
            "  default int defaultMethod() { return privateMethod(); }",
            "  private int privateMethod() { return 42; }",
            "  private static int staticPrivateMethod() { return 42; }",
            "}"),
        JavaResourceBase.createMockJavaResource("test.TestResource",
            "package test;",
            "public class TestResource implements AutoCloseable {",
            "  public void close() { }",
            "}"));
  }

  public void testCompileTryWithResources() throws Exception {
    assertEqualBlock(
        "TestResource r = new TestResource();"
            + "try {"
            + "  Throwable $primary_ex_3 = null;"
            + "  try {"
            + "    $resource_2 = r;"
            + "  } catch(Throwable $caught_ex_4) {"
            + "    $primary_ex_3 = $caught_ex_4;"
            + "    throw $primary_ex_3;"
            + "  } finally {"
            + "    $primary_ex_3 = Exceptions.safeClose($resource_2,$primary_ex_3);"
            + "    if ($primary_ex_3 != null)"
            + "      throw $primary_ex_3;"
            + "  }"
            + "}",
        "TestResource r = new TestResource();"
            + "try (r) {}");
  }

  public void testInterfaceWithPrivateMethods() throws Exception {
    JProgram program =
        compileSnippet("void",
            "(new InterfaceWithPrivateMethods() {}).defaultMethod();", false);

    JInterfaceType interfaceWithPrivateMethods =
        (JInterfaceType) getType(program, "test.InterfaceWithPrivateMethods");
    // should have an actual method with body on it
    JMethod defaultMethod = findMethod(interfaceWithPrivateMethods, "defaultMethod");
    assertEquals(1, ((JMethodBody) defaultMethod.getBody()).getBlock().getStatements().size());
    JMethod privateMethod = findMethod(interfaceWithPrivateMethods, "privateMethod");
    assertEquals(1, ((JMethodBody) privateMethod.getBody()).getBlock().getStatements().size());
    JMethod staticPrivateMethod = findMethod(interfaceWithPrivateMethods, "staticPrivateMethod");
    assertEquals(1,
        ((JMethodBody) staticPrivateMethod.getBody()).getBlock().getStatements().size());
  }

  @Override
  protected void optimizeJava() {
  }
}