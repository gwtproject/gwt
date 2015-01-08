/*
 * Copyright 2014 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package com.google.gwt.dev.jjs.impl;

import com.google.gwt.dev.javac.testing.impl.JavaResourceBase;
import com.google.gwt.dev.javac.testing.impl.MockJavaResource;
import com.google.gwt.dev.jjs.ast.JMethod;
import com.google.gwt.dev.jjs.ast.JProgram;

/**
 * Test for {@link UnifyAst}.
 */
public class UnifyAstTest extends OptimizerTestBase {

  @Override
  protected void setUp() throws Exception {
    super.setUp();
    addAll(A_A, A_I, A_J, A_B, B_C, B_D, B_E);
  }

  public void testOverrides_base() throws Exception {
    Result result = optimize("void", "");

    String fullMethodSignature = "a.B.m()V";
    String element = "a.I.m()V";
    assertOverrides(result, fullMethodSignature, element);
    assertOverrides(result, "a.A.m()V", "a.I.m()V");
    assertOverrides(result, "b.C.m()V", "a.I.m()V");
    assertOverrides(result, "b.D.m()V", "a.I.m()V");
  }

  public void testOverrides_differentReturnTypes() throws Exception {
    Result result = optimize("void", "");

    assertOverrides(result, "a.B.m1()La/A;", "a.I.m1()La/A;");
    assertOverrides(result, "a.A.m1()La/A;", "a.I.m1()La/A;");
    assertOverrides(result, "b.C.m1()La/A;", "a.A.m1()La/A;");
    assertOverrides(result, "b.D.m1()La/A;", "a.I.m1()La/A;", "a.A.m1()La/A;");
    assertOverrides(result, "b.C.m1()Lb/C;");
    assertOverrides(result, "b.D.m1()Lb/D;");
    assertOverrides(result, "b.E.m1()Lb/C;", "b.C.m1()Lb/C;");
    assertOverrides(result, "b.E.m1()Lb/E;");
  }

  public void testOverrides_packagePrivate() throws Exception {
    Result result = optimize("void", "");

    assertOverrides(result, "a.B.pp()V", "a.A.pp()V", "a.I.pp()V");
    assertOverrides(result, "a.A.pp()V");
    assertOverrides(result, "a.J.pp()V", "a.I.pp()V");
    assertOverrides(result, "a.I.pp()V");
    assertOverrides(result, "b.D.m1()La/A;", "a.I.m1()La/A;", "a.A.m1()La/A;");
    assertOverrides(result, "b.C.m1()Lb/C;");
    assertOverrides(result, "b.D.m1()Lb/D;");
    assertOverrides(result, "b.E.m1()Lb/C;", "b.C.m1()Lb/C;");
    assertOverrides(result, "b.E.m1()Lb/E;");
  }

  @Override
  protected boolean optimizeMethod(JProgram program, JMethod method) {
    program.addEntryMethod(findMainMethod(program));
    return false;
  }

  public static final MockJavaResource A_A =
      JavaResourceBase.createMockJavaResource("a.A",
          "package a;",
          "public class A {",
          "  public void m() { }",
          "  public A m1() { return null; }",
          "  void pp() {}",
          "}");

  public static final MockJavaResource A_I =
      JavaResourceBase.createMockJavaResource("a.I",
          "package a;",
          "public interface I {",
          "  void m();",
          "  A m1();",
          "  void pp();",
          "}");

  public static final MockJavaResource A_J =
      JavaResourceBase.createMockJavaResource("a.J",
          "package a;",
          "public interface J extends I {",
          "  void pp();",
          "}");

  public static final MockJavaResource A_B =
      JavaResourceBase.createMockJavaResource("a.B",
          "package a;",
          "public class B extends A implements a.I {",
          "  public void pp() {}",
          "}");

  public static final MockJavaResource B_C =
      JavaResourceBase.createMockJavaResource("b.C",
          "package b;",
          "public class C extends a.A {",
          "  public C m1() { return null; }",
          "  void pp() {}",
          "}");

  public static final MockJavaResource B_D =
      JavaResourceBase.createMockJavaResource("b.D",
          "package b;",
          "public class D extends a.B {",
          "  public D m1() { return null; }",
          "}");

  public static final MockJavaResource B_E =
      JavaResourceBase.createMockJavaResource("b.E",
          "package b;",
          "public class E extends b.C {",
          "  public E m1() { return null; }",
          "}");
}
