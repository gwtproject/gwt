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
package com.google.gwt.dev.jjs.test;

import com.google.gwt.junit.client.GWTTestCase;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Tests Java 7 features. It is super sourced so that gwt can be compiles under Java 6.
 *
 * IMPORTANT: For each test here there must exist the corresponding method in the non super sourced
 * version.
 *
 * Eventually this test will graduate and not be super sourced.
 */
public class Java8Test extends GWTTestCase {
  int local = 42;

  public interface Lambda<T> {
    T run(int a, int b);
  }

  public interface Lambda2<String> {
    boolean run(String a, String b);
  }

  public class AcceptsLambda<T> {
    public T accept(Lambda<T> foo) {
      return foo.run(10, 20);
    }
    public boolean accept2(Lambda2<String> foo) {
      return foo.run("a", "b");
    }
  }

  public class Pojo {
    public Pojo(int x, int y) {
      assertEquals(10, x);
      assertEquals(20, y);
    }
  }

  public interface DefaultInterface {
    void method1();
    // CHECKSTYLE_OFF
    default int method2() { return 42; }
    // CHECKSTYLE_ON
  }

  static class Inner {
    int local = 22;
    public void run() {
      assertEquals(94, new AcceptsLambda<Integer>().accept((a,b) -> Java8Test.local +  local + a + b).intValue());
    }
  }

  interface Static {
    static int staticMethod() { return 99; }
  }

  public class DefaultInterfaceImpl implements DefaultInterface {
    public void method1() {}
  }

  @Override
  public String getModuleName() {
    return "com.google.gwt.dev.jjs.Java8Test";
  }

  public void testLambdaNoCapture() {
    assertEquals(30, new AcceptsLambda<Integer>().accept((a, b) -> a + b).intValue());
  }

  public void testLambdaCaptureLocal() {
    int x = 10;
    assertEquals(40, new AcceptsLambda<Integer>().accept((a,b) -> x + a + b).intValue());
  }

  public void testLambdaCaptureLocalAndField() {
    int x = 10;
    assertEquals(82, new AcceptsLambda<Integer>().accept((a,b) -> x + local + a + b).intValue());
  }

  public void testCompileLambdaCaptureOuterInnerField() throws Exception {
    new Inner().run();
  }

  public static Integer foo(int x, int y) { return x + y; }

  public Integer fooInstance(int x, int y) { return x + y + 1; }

  public void testStaticReferenceBinding() throws Exception {
    assertEquals(30, new AcceptsLambda<Integer>().accept(Java8Test::foo).intValue());
  }

  public void testInstanceReferenceBinding() throws Exception {
    assertEquals(31, new AcceptsLambda<Integer>().accept(this::fooInstance).intValue());
  }

  public void testImplicitQualifierReferenceBinding() throws Exception {
    assertFalse(new AcceptsLambda<String>().accept2(String::equalsIgnoreCase));
  }

  public void testConstructorReferenceBinding() {
    new AcceptsLambda<Pojo>().accept(Pojo::new);
  }

  public void testDefaultInterfaceMethod() {
    assertEquals(42, new DefaultInterfaceImpl().method2());
  }

  public void testStaticInterfaceMethod() {
    assertEquals(99, Static.staticMethod());
  }
}
