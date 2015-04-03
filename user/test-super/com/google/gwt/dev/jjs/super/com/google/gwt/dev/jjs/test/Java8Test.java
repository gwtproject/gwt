/*
 * Copyright 2014 Google Inc.
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

import com.google.gwt.core.client.GwtScriptOnly;
import com.google.gwt.junit.client.GWTTestCase;

/**
 * Tests Java 8 features. It is super sourced so that gwt can be compiles under Java 7.
 *
 * IMPORTANT: For each test here there must exist the corresponding method in the non super sourced
 * version.
 *
 * Eventually this test will graduate and not be super sourced.
 */
@GwtScriptOnly
public class Java8Test extends GWTTestCase {
  int local = 42;

  static abstract class SameClass {
    public int method1() { return 10; }
    public abstract int method2();
  }

  interface Lambda<T> {
    T run(int a, int b);
  }

  interface Lambda2<String> {
    boolean run(String a, String b);
  }

  interface Lambda3<String> {
    boolean run(String a);
  }

  class AcceptsLambda<T> {
    public T accept(Lambda<T> foo) {
      return foo.run(10, 20);
    }
    public boolean accept2(Lambda2<String> foo) {
      return foo.run("a", "b");
    }
    public boolean accept3(Lambda3<String> foo) {
      return foo.run("hello");
    }
  }

  class Pojo {
    private final int x;
    private final int y;

    public Pojo(int x, int y) {
      this.x = x;
      this.y = y;
    }

    public int fooInstance(int a, int b) {
      return a + b + x + y;
    }
  }

  interface DefaultInterface {
    void method1();
    // CHECKSTYLE_OFF
    default int method2() { return 42; }
    default int redeclaredAsAbstract() {
        return 88;
    }
    default Integer addInts(int x, int y) { return x + y; }
    default String print() { return "DefaultInterface"; }
    // CHECKSTYLE_ON
  }

  interface DefaultInterface2 {
    void method3();
    // CHECKSTYLE_OFF
    default int method4() { return 23; }
    default int redeclaredAsAbstract() {
      return 77;
    }
    // CHECKSTYLE_ON
  }

  interface DefaultInterfaceSubType extends DefaultInterface {
    // CHECKSTYLE_OFF
    default int method2() { return 43; }
    default String print() {
      return "DefaultInterfaceSubType " + DefaultInterface.super.print();
    }
    // CHECKSTYLE_ON
  }

  static abstract class DualImplementorSuper implements DefaultInterface {
    public void method1() {
    }

    public abstract int redeclaredAsAbstract();
  }

  static class DualImplementorBoth extends VirtualUpRef implements DefaultInterface,
      DefaultInterface2 {
    public void method1() {
    }
    public void method3() {
    }
  }

  static class DualImplementor extends DualImplementorSuper implements DefaultInterface2 {
    public void method3() {
    }

    public int redeclaredAsAbstract() {
      return DefaultInterface2.super.redeclaredAsAbstract();
    }
  }

  // this doesn't implement DefaultInterface, but will provide implementation in subclasses
  static class VirtualUpRef {
    public int method2() {
      return 99;
    }
    public int redeclaredAsAbstract() {
      return 44;
    }
  }

  class Inner {
    int local = 22;
    public void run() {
      assertEquals(94, new AcceptsLambda<Integer>().accept((a,b) -> Java8Test.this.local +  local + a + b).intValue());
    }
  }

  static class Static {
    static int staticField;
    static {
      staticField = 99;
    }
    static Integer staticMethod(int x, int y) { return x + y + staticField; }
  }

  static class StaticFailIfClinitRuns {
    static {
      fail("clinit() shouldn't run from just taking a reference to a method");
    }

    public static Integer staticMethod(int x, int y) {
      return null;
    }
  }

  static class DefaultInterfaceImpl implements DefaultInterface {
    public void method1() {
    }
  }

  static class DefaultInterfaceImpl2 implements DefaultInterface {
    public void method1() {
    }
    public int method2() {
      return 100;
    }
  }

  static class DefaultInterfaceImplVirtualUpRef extends VirtualUpRef implements DefaultInterface {
    public void method1() {
    }
  }

  static class DefaultInterfaceImplVirtualUpRefTwoInterfaces extends VirtualUpRef
      implements DefaultInterfaceSubType {
    public void method1() {
    }
    // CHECKSTYLE_OFF
    public String print() { return "DefaultInterfaceImplVirtualUpRefTwoInterfaces"; }
    // CHECKSTYLE_ON
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

  public void testLambdaCaptureLocalWithInnerClass() {
    int x = 10;
    Lambda<Integer> l = (a,b) -> new Lambda<Integer>() {
      @Override public Integer run(int a, int b) {
        int t = x;
        return t + a + b;
      }
    }.run(a,b);
    assertEquals(40, new AcceptsLambda<Integer>().accept(l).intValue());
  }

  public void testLambdaCaptureLocalAndField() {
    int x = 10;
    assertEquals(82, new AcceptsLambda<Integer>().accept((a,b) -> x + local + a + b).intValue());
  }

  public void testLambdaCaptureLocalAndFieldWithInnerClass() {
    int x = 10;
    Lambda<Integer> l = (a,b) -> new Lambda<Integer>() {
      @Override public Integer run(int j, int k) {
        int t = x;
        int s = local;
        return t + s + a + b;
      }
    }.run(a,b);
    assertEquals(82, new AcceptsLambda<Integer>().accept(l).intValue());
  }

  public void testCompileLambdaCaptureOuterInnerField() throws Exception {
    new Inner().run();
  }

  public void testStaticReferenceBinding() throws Exception {
    assertEquals(129, new AcceptsLambda<Integer>().accept(Static::staticMethod).intValue());
    // if this next line runs a clinit, it fails
    Lambda l = dummyMethodToMakeCheckStyleHappy(StaticFailIfClinitRuns::staticMethod);
    try {
      // but now it should fail
      l.run(1,2);
      fail("Clinit should have run for the first time");
    } catch (AssertionError ae) {
      // success, it was supposed to throw!
    }
  }

  private static Lambda<Integer> dummyMethodToMakeCheckStyleHappy(Lambda<Integer> l) {
    return l;
  }

  public void testInstanceReferenceBinding() throws Exception {
    Pojo instance1 = new Pojo(1, 2);
    Pojo instance2 = new Pojo(3, 4);
    assertEquals(33, new AcceptsLambda<Integer>().accept(instance1::fooInstance).intValue());
    assertEquals(37, new AcceptsLambda<Integer>().accept(instance2::fooInstance).intValue());
  }

  public void testImplicitQualifierReferenceBinding() throws Exception {
    assertFalse(new AcceptsLambda<String>().accept2(String::equalsIgnoreCase));
    assertTrue(new AcceptsLambda<String>().accept3("hello world"::contains));
  }

  public void testConstructorReferenceBinding() {
    assertEquals(30, new AcceptsLambda<Pojo>().accept(Pojo::new).fooInstance(0, 0));
  }

  public void testStaticInterfaceMethod() {
    assertEquals(99, (int) Static.staticMethod(0, 0));
  }

  interface ArrayCtor {
    ArrayElem [][][] copy(int i);
  }

  interface ArrayCtorBoxed {
    ArrayElem [][][] copy(Integer i);
  }

  static class ArrayElem {
  }

  public void testArrayConstructorReference() {
    ArrayCtor ctor = ArrayElem[][][]::new;
    ArrayElem[][][] array = ctor.copy(100);
    assertEquals(100, array.length);
  }

  public void testArrayConstructorReferenceBoxed() {
    ArrayCtorBoxed ctor = ArrayElem[][][]::new;
    ArrayElem[][][] array = ctor.copy(100);
    assertEquals(100, array.length);
  }

  interface ThreeArgs {
    int foo(int x, int y, int z);
  }

  interface ThreeVarArgs {
    int foo(int x, int y, int... z);
  }

  public static int addMany(int x, int y, int... nums) {
    int sum = x + y;
    for (int num : nums) {
      sum += num;
    }
    return sum;
  }

  public void testVarArgsReferenceBinding() {
    ThreeArgs t = Java8Test::addMany;
    assertEquals(6, t.foo(1,2,3));
  }

  public void testVarArgsPassthroughReferenceBinding() {
    ThreeVarArgs t = Java8Test::addMany;
    assertEquals(6, t.foo(1,2,3));
  }

  public void testVarArgsPassthroughReferenceBindingProvidedArray() {
    ThreeVarArgs t = Java8Test::addMany;
    assertEquals(6, t.foo(1,2, new int[] {3}));
  }

  interface I {
    int foo(Integer i);
  }

  public void testSuperReferenceExpression() {
    class Y {
      int foo(Integer i) {
        return 42;
      }
    }

    class X extends Y {
      int foo(Integer i) {
        return 23;
      }

      int goo() {
        I i = super::foo;
        return i.foo(0);
      }
    }

    assertEquals(42, new X().goo());
  }

  static class X2 {
    protected int field;
    void foo() {
      int local;
      class Y extends X2 {
        class Z extends X2 {
          void f() {
            Ctor c = X2::new;
            X2 x = c.makeX(123456);
            assertEquals(123456, x.field);
            c = Y::new;
            x = c.makeX(987654);
            x = new Y(987654);
            assertEquals(987655, x.field);
            c = Z::new;
            x = c.makeX(456789);
            x = new Z(456789);
            assertEquals(456791, x.field);
          }
          private Z(int z) {
            super(z + 2);
          }
          Z() {
          }
        }

        private Y(int y) {
          super(y + 1);
        }

        private Y() {
        }
      }
      new Y().new Z().f();
    }

    private X2(int x) {
      this.field = x;
    }
    X2() {
    }
  }

  public void testSuperReferenceExpressionWithVarArgs() {
    class Base {
      int foo(Object... objects) {
        return 0;
      }
    }

    class X extends Base {
      int foo(Object... objects) {
        throw new AssertionError();
      }

      void goo() {
        I i = super::foo;
        i.foo(10);
      }
    }
    new X().goo();
  }

  interface Ctor {
    X2 makeX(int x);
  }

  public void testPrivateConstructorReference() {
    new X2().foo();
  }

  public void testDefaultInterfaceMethod() {
    assertEquals(42, new DefaultInterfaceImpl().method2());
  }

  public void testDefaultInterfaceMethodVirtualUpRef() {
    assertEquals(99, new DefaultInterfaceImplVirtualUpRef().method2());
    assertEquals(99, new DefaultInterfaceImplVirtualUpRefTwoInterfaces().method2());
    assertEquals("SimpleB", new com.google.gwt.dev.jjs.test.package3.SimpleC().m());
    assertEquals("SimpleASimpleB", new com.google.gwt.dev.jjs.test.package1.SimpleD().m());
  }

  public void testDefaultInterfaceMethodMultiple() {
    assertEquals(42, new DualImplementor().method2());
    assertEquals(23, new DualImplementor().method4());
    assertEquals(77, new DualImplementor().redeclaredAsAbstract());
    assertEquals(44, new DualImplementorBoth().redeclaredAsAbstract());
    DefaultInterfaceImplVirtualUpRefTwoInterfaces instanceImplementInterfaceSubType =
        new DefaultInterfaceImplVirtualUpRefTwoInterfaces();
    DefaultInterfaceSubType interfaceSubType1 = instanceImplementInterfaceSubType;
    assertEquals("DefaultInterfaceImplVirtualUpRefTwoInterfaces",
        instanceImplementInterfaceSubType.print());
    assertEquals("DefaultInterfaceImplVirtualUpRefTwoInterfaces", interfaceSubType1.print());
    DefaultInterfaceSubType interfaceSubType2 = new DefaultInterfaceSubType() {
      @Override
      public void method1() { }
    };
    assertEquals("DefaultInterfaceSubType DefaultInterface",
        interfaceSubType2.print());
    DefaultInterfaceSubType interfaceSubType3 = () -> { };
    assertEquals("DefaultInterfaceSubType DefaultInterface",
        interfaceSubType3.print());
  }

  public void testDefenderMethodByInterfaceInstance() {
    DefaultInterfaceImpl2 interfaceImpl2 = new DefaultInterfaceImpl2();
    DefaultInterface interface1 = interfaceImpl2;
    assertEquals(100, interfaceImpl2.method2());
    assertEquals(100, interface1.method2());
  }

  public void testDefaultMethodReference() {
    DefaultInterfaceImplVirtualUpRef x = new DefaultInterfaceImplVirtualUpRef();
    assertEquals(30, (int) new AcceptsLambda<Integer>().accept(x::addInts));
  }

  interface InterfaceWithTwoDefenderMethods {
    // CHECKSTYLE_OFF
    default String foo() { return "interface.foo"; }
    default String bar() { return this.foo() + " " + foo(); }
    // CHECKSTYLE_ON
  }

  class ClassImplementOneDefenderMethod implements InterfaceWithTwoDefenderMethods {
    public String foo() {
      return "class.foo";
    }
  }

  public void testThisRefInDefenderMethod() {
    ClassImplementOneDefenderMethod c = new ClassImplementOneDefenderMethod();
    InterfaceWithTwoDefenderMethods i1 = c;
    InterfaceWithTwoDefenderMethods i2 = new InterfaceWithTwoDefenderMethods() { };
    assertEquals("class.foo class.foo", c.bar());
    assertEquals("class.foo class.foo", i1.bar());
    assertEquals("interface.foo interface.foo", i2.bar());
  }

  interface InterfaceImplementOneDefenderMethod extends InterfaceWithTwoDefenderMethods {
    // CHECKSTYLE_OFF
    default String foo() { return "interface1.foo"; }
    // CHECKSTYLE_ON
  }

  interface InterfaceImplementZeroDefenderMethod extends InterfaceWithTwoDefenderMethods {
  }

  class ClassImplementsTwoInterfaces implements InterfaceImplementOneDefenderMethod,
      InterfaceImplementZeroDefenderMethod {
  }

  public void testClassImplementsTwoInterfacesWithSameDefenderMethod() {
    ClassImplementsTwoInterfaces c = new ClassImplementsTwoInterfaces();
    assertEquals("interface1.foo", c.foo());
  }

  abstract class AbstractClass implements InterfaceWithTwoDefenderMethods {
  }

  class Child1 extends AbstractClass {
    public String foo() {
      return super.foo() + " child1.foo";
    }
  }

  class Child2 extends AbstractClass {
  }

  public void testAbstractClassImplementsInterface() {
    Child1 child1 = new Child1();
    Child2 child2 = new Child2();
    assertEquals("interface.foo child1.foo", child1.foo());
    assertEquals("interface.foo", child2.foo());
  }

  interface InterfaceI {
    // CHECKSTYLE_OFF
    default String print() { return "interface1"; }
    // CHECKSTYLE_ON
  }
  interface InterfaceII {
    // CHECKSTYLE_OFF
    default String print() { return "interface2"; }
    // CHECKSTYLE_ON
  }
  class ClassI {
    public String print() {
      return "class1";
    }
  }
  class ClassII extends ClassI implements InterfaceI, InterfaceII {
    public String print() {
      return super.print() + " " + InterfaceI.super.print() + " " + InterfaceII.super.print();
    }
  }

  public void testSuperRefInDefenderMethod() {
    ClassII c = new ClassII();
    assertEquals("class1 interface1 interface2", c.print());
  }

  interface II {
    // CHECKSTYLE_OFF
    default String fun() { return "fun() in i: " + this.foo(); };
    default String foo() { return "foo() in i.\n"; };
    // CHECKSTYLE_ON
  }
  interface JJ extends II {
    // CHECKSTYLE_OFF
    default String fun() { return "fun() in j: " + this.foo() + II.super.fun(); };
    default String foo() { return "foo() in j.\n"; }
    // CHECKSTYLE_ON
  }
  class AA {
    public String fun() {
      return "fun() in a: " + this.foo();
    }
    public String foo() {
      return "foo() in a.\n";
    }
  }
  class BB extends AA implements JJ {
    public String fun() {
      return "fun() in b: " + this.foo() + super.fun() + JJ.super.fun();
    }
    public String foo() {
      return "foo() in b.\n";
    }
  }
  class CC extends BB implements JJ {
    public String fun() {
      return "fun() in c: " + super.fun();
    }
  }

  public void testSuperThisRefsInDefenderMethod() {
    CC c = new CC();
    II i1 = c;
    JJ j1 = c;
    BB b = new BB();
    II i2 = b;
    JJ j2 = b;
    JJ j3 = new JJ() { };
    II i3 = j3;
    II i4 = new II() { };
    String c_fun = "fun() in c: fun() in b: foo() in b.\n"
        + "fun() in a: foo() in b.\n"
        + "fun() in j: foo() in b.\n"
        + "fun() in i: foo() in b.\n";
    String b_fun = "fun() in b: foo() in b.\n"
        + "fun() in a: foo() in b.\n"
        + "fun() in j: foo() in b.\n"
        + "fun() in i: foo() in b.\n";
    String j_fun = "fun() in j: foo() in j.\n"
        + "fun() in i: foo() in j.\n";
    String i_fun = "fun() in i: foo() in i.\n";
    assertEquals(c_fun, c.fun());
    assertEquals(c_fun, i1.fun());
    assertEquals(c_fun, j1.fun());
    assertEquals(b_fun, b.fun());
    assertEquals(b_fun, i2.fun());
    assertEquals(b_fun, j2.fun());
    assertEquals(j_fun, j3.fun());
    assertEquals(j_fun, i3.fun());
    assertEquals(i_fun, i4.fun());
  }

  interface OuterInterface {
    // CHECKSTYLE_OFF
    default String m() {
      return "I.m;" + new InnerClass().n();
    }
    default String n() {
      return "I.n;" + this.m();
    }
    // CHECKSTYLE_ON
    class InnerClass {
      public String n() {
        return "A.n;" + m();
      }
      public String m() {
        return "A.m;";
      }
    }
  }
  class OuterClass {
    public String m() {
      return "B.m;";
    }
    public String n1() {
      OuterInterface i = new OuterInterface() { };
      return "B.n1;" + i.n() + OuterClass.this.m();
    }
    public String n2() {
      OuterInterface i = new OuterInterface() {
        @Override
        public String n() {
          return this.m() + OuterClass.this.m();
        }
      };
      return "B.n2;" + i.n() + OuterClass.this.m();
    }
  }
  public void testNestedInterfaceClass() {
    OuterClass outerClass = new OuterClass();
    assertEquals("B.n1;I.n;I.m;A.n;A.m;B.m;", outerClass.n1());
    assertEquals("B.n2;I.m;A.n;A.m;B.m;B.m;", outerClass.n2());
  }

  class EmptyA { }
  interface EmptyI { }
  interface EmptyJ { }
  class EmptyB extends EmptyA implements EmptyI { }
  class EmptyC extends EmptyA implements EmptyI, EmptyJ { }
  public void testBaseIntersectionCast() {
    EmptyA localB = new EmptyB();
    EmptyA localC = new EmptyC();
    EmptyB b2BI = (EmptyB & EmptyI) localB;
    EmptyC c2CIJ = (EmptyC & EmptyI & EmptyJ) localC;
    EmptyI ii1 = (EmptyB & EmptyI) localB;
    EmptyI ii2 = (EmptyC & EmptyI) localC;
    EmptyI ii3 = (EmptyC & EmptyJ) localC;
    EmptyI ii4 = (EmptyC & EmptyI & EmptyJ) localC;
    EmptyJ jj1 = (EmptyC & EmptyI & EmptyJ) localC;
    EmptyJ jj2 = (EmptyC & EmptyI) localC;
    EmptyJ jj3 = (EmptyC & EmptyJ) localC;
    EmptyJ jj4 = (EmptyI & EmptyJ) localC;

    try {
      EmptyC b2CIJ = (EmptyC & EmptyI & EmptyJ) localB;
      fail("Should have thrown a ClassCastException");
    } catch (ClassCastException e) {
      // Expected.
    }
    try {
      EmptyB c2BI = (EmptyB & EmptyI) localC;
      fail("Should have thrown a ClassCastException");
    } catch (ClassCastException e) {
      // Expected.
    }
    try {
      EmptyJ jj = (EmptyB & EmptyJ) localB;
      fail("Should have thrown a ClassCastException");
    } catch (ClassCastException e) {
      // Expected.
    }
  }

  interface SimpleI {
    int fun();
  }
  interface SimpleJ {
    int foo();
    int bar();
  }
  interface SimpleK {
  }
  public void testIntersectionCastWithLambdaExpr() {
    SimpleI simpleI1 = (SimpleI & EmptyI) () -> { return 11; };
    assertEquals(11, simpleI1.fun());
    SimpleI simpleI2 = (EmptyI & SimpleI) () -> { return 22; };
    assertEquals(22, simpleI2.fun());
    EmptyI emptyI = (EmptyI & SimpleI) () -> { return 33; };
    try {
      ((EmptyA & SimpleI) () -> { return 33; }).fun();
      fail("Should have thrown a ClassCastException");
    } catch (ClassCastException e) {
      // expected.
    }
    try {
      ((SimpleI & SimpleJ) () -> { return 44; }).fun();
      fail("Should have thrown a ClassCastException");
    } catch (ClassCastException e) {
      // expected.
    }
    try {
      ((SimpleI & SimpleJ) () -> { return 44; }).foo();
      fail("Should have thrown a ClassCastException");
    } catch (ClassCastException e) {
      // expected.
    }
    try {
      ((SimpleI & SimpleJ) () -> { return 44; }).bar();
      fail("Should have thrown a ClassCastException");
    } catch (ClassCastException e) {
      // expected.
    }
    assertEquals(55, ((SimpleI & SimpleK) () -> { return 55; }).fun());
  }

  class SimpleA {
    public int bar() {
      return 11;
    }
  }

  class SimpleB extends SimpleA implements SimpleI {
    public int fun() {
      return 22;
    }
  }

  class SimpleC extends SimpleA implements SimpleI {
    public int fun() {
      return 33;
    }

    public int bar() {
      return 44;
    }
  }

  public void testIntersectionCastPolymorphism() {
    SimpleA bb = new SimpleB();
    assertEquals(22, ((SimpleB & SimpleI) bb).fun());
    assertEquals(11, ((SimpleB & SimpleI) bb).bar());
    SimpleA cc = new SimpleC();
    assertEquals(33, ((SimpleC & SimpleI) cc).fun());
    assertEquals(44, ((SimpleC & SimpleI) cc).bar());
    assertEquals(33, ((SimpleA & SimpleI) cc).fun());
    SimpleI ii = (SimpleC & SimpleI) cc;
    assertEquals(33, ii.fun());
  }

  interface ClickHandler {
    int onClick(int a);
  }
  private int addClickHandler(ClickHandler clickHandler) {
    return clickHandler.onClick(1);
  }
  private int addClickHandler(int a) {
    return addClickHandler(x -> { int temp = a; return temp; });
  }
  public void testLambdaCaptureParameter() {
    assertEquals(2, addClickHandler(2));
  }

  interface TestLambda_Inner {
    void f();
  }
  interface TestLambda_Outer {
    void accept(TestLambda_Inner t);
  }
  public void testLambda_call(TestLambda_Outer a) {
    a.accept(() -> { });
  }
  public void testLambdaNestingCaptureLocal() {
    int[] success = new int[] {0};
    testLambda_call(sam1 -> { testLambda_call(sam2 -> { success[0] = 10; }); });
    assertEquals(10, success[0]);
  }

  static class TestLambda_Class {
    public int[] s = new int[] {0};
    public void call(TestLambda_Outer a) {
      a.accept(() -> { });
    }
    class TestLambda_InnerClass {
      public int[] s = new int[] {0};
      public int test() {
        int[] s = new int[] {0};
        TestLambda_Class.this.call(
            sam0 -> TestLambda_Class.this.call(
                sam1 -> {
                  TestLambda_Class.this.call(
                    sam2 -> {
                      TestLambda_Class.this.s[0] = 10;
                      this.s[0] = 20;
                      s[0] = 30;
                    });
                  }));
        return s[0];
      }
    }
  }

  public void testLambdaNestingCaptureField() {
    TestLambda_Class a = new TestLambda_Class();
    a.call(sam1 -> { a.call(sam2 -> { a.s[0] = 20; }); });
    assertEquals(20, a.s[0]);
  }

  public void testLambdaMultipleNestingCaptureFieldAndLocal() {
    TestLambda_Class a = new TestLambda_Class();
    TestLambda_Class b = new TestLambda_Class();
    int [] s = new int [] {0};
    b.call(sam0 -> a.call(sam1 -> { a.call(sam2 -> { a.s[0] = 20; b.s[0] = 30; s[0] = 40; }); }));
    assertEquals(20, a.s[0]);
    assertEquals(30, b.s[0]);
    assertEquals(40, s[0]);
  }

  public void testLambdaMultipleNestingCaptureFieldAndLocalInnerClass() {
    TestLambda_Class a = new TestLambda_Class();
    TestLambda_Class.TestLambda_InnerClass b = a.new TestLambda_InnerClass();
    int result = b.test();
    assertEquals(10, a.s[0]);
    assertEquals(20, b.s[0]);
    assertEquals(30, result);
  }

  static class TestMF_A {
    public static String getId() {
      return "A";
    }
    public int getIdx() {
      return 1;
    }
  }
  static class TestMF_B {
    public static String getId() {
      return "B";
    }
    public int getIdx() {
      return 2;
    }
  }
  interface Function<T> {
    T apply();
  }
  private String f(Function<String> arg) {
    return arg.apply();
  }
  private int g(Function<Integer> arg) {
    return arg.apply().intValue();
  }

  public void testMethodRefWithSameName() {
    assertEquals("A", f(TestMF_A::getId));
    assertEquals("B", f(TestMF_B::getId));
    TestMF_A a = new TestMF_A();
    TestMF_B b = new TestMF_B();
    assertEquals(1, g(a::getIdx));
    assertEquals(2, g(b::getIdx));
  }

  // Test particular scenarios involving multiple path to inherit defaults.
  interface ITop {
    default String m() {
      return "ITop.m()";
    }
  }

  interface IRight extends ITop {
    default String m() {
      return "IRight.m()";
    }
  }

  interface ILeft extends ITop { }

  public void testMultipleDefaults_fromInterfaces_left() {
    class A implements ILeft, IRight { }

    assertEquals("IRight.m()", new A().m());
  }

  public void testMultipleDefaults_fromInterfaces_right() {
    class A implements IRight, ILeft { }

    assertEquals("IRight.m()", new A().m());
  }

  public void testMultipleDefaults_superclass_left() {
    class A implements ITop { }
    class B extends A implements ILeft, IRight { }

    assertEquals("IRight.m()", new B().m());
  }

  public void testMultipleDefaults_superclass_right() {
    class A implements ITop { }
    class B extends A implements IRight, ILeft { }

    assertEquals("IRight.m()", new B().m());
  }

  interface InterfaceWithThisReference {
    default String n() {
      return "default n";
    }
    default String callNUnqualified() {
      class Super implements InterfaceWithThisReference {
        public String n() {
          return "super n";
        }
      }
      return new Super() {
        public String callNUnqualified() {
          return "Object " + n();
        }
      }.callNUnqualified();
    }
    default String callNWithThis() {
      class Super implements InterfaceWithThisReference {
        public String n() {
          return "super n";
        }
      }
      return new Super() {
        public String callNWithThis() {
          return "Object " + this.n();
        }
      }.callNWithThis();
    }
    default String callNWithInterfaceThis() {
      class Super implements InterfaceWithThisReference {
        public String n() {
          return "super n";
        }
      }
      return new Super() {
        public String callNWithInterfaceThis() {
          // In this method this has interface Test as its type, but it refers to outer n();
          return "Object " + InterfaceWithThisReference.this.n();
        }
      }.callNWithInterfaceThis();
    }
    default String callNWithSuper() {
      class Super implements InterfaceWithThisReference {
        public String n() {
          return "super n";
        }
      }
      return new Super() {
        public String callNWithSuper() {
          // In this method this has interface Test as its type.
          return "Object " + super.n();
        }
      }.callNWithSuper();
    }
    default String callNWithInterfaceSuper() {
      return new InterfaceWithThisReference() {
        public String n() {
          return "this n";
        }
        public String callNWithInterfaceSuper() {
          // In this method this has interface Test as its type and refers to default n();
          return "Object " + InterfaceWithThisReference.super.n();
        }
      }.callNWithInterfaceSuper();
    }
  }

  public void testInterfaceThis() {
    class A implements InterfaceWithThisReference {
      public String n() {
        return "n";
      }
    }
    assertEquals("Object super n", new A().callNUnqualified());
    assertEquals("Object super n", new A().callNWithThis());
    assertEquals("Object n", new A().callNWithInterfaceThis());
    assertEquals("Object super n", new A().callNWithSuper());
    assertEquals("Object default n", new A().callNWithInterfaceSuper());
  }
}
