/*
 * Copyright 2024 GWT Project Authors
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

import java.util.Arrays;
import java.util.List;

import jsinterop.annotations.*;

/**
 * Tests Java 17 features. It is super sourced so that gwt can be compiles under Java 11.
 *
 * IMPORTANT: For each test here there must exist the corresponding method in the non super sourced
 * version.
 *
 * Eventually this test will graduate and not be super sourced.
 */
@GwtScriptOnly
public class Java17Test extends GWTTestCase {

  public interface TextBlock {
    String text = """
        line 1
        line 2
        line 3
        """;
  }

  public sealed class Shape permits Square, Circle {
    public static int count = 0;

    public static Shape returnAndIncrement(Shape shape){
      count++;
      return shape;
    }

  }

  public final class Square extends Shape {
    public int getLength() {
      return 10;
    }
  }

  public final class Circle extends Shape {
    public int getDiameter() {
      return 10;
    }
  }

  public interface TestSupplier {
    boolean run();
  }

  public class Foo {
    private Shape shape;

    public Foo() {
      shape = new Square();
    }

    public TestSupplier isSquare() {
      return () -> shape instanceof Square square && square.getLength() > 0;
    }
  }

  public class Bar {
    private Shape shape;

    public Bar() {
      shape = new Square();
    }

    public boolean isSquare() {
      return shape instanceof Square square && square.getLength() > 0;
    }
  }
  @Override
  public String getModuleName() {
    return "com.google.gwt.dev.jjs.test.Java17Test";
  }

  public void testTextBlocks() {
    List<String> lines = Arrays.asList(TextBlock.text.split("\n"));
    assertEquals(3, lines.size());
    assertEquals("line 1", lines.get(0));
    assertEquals("line 2", lines.get(1));
    assertEquals("line 3", lines.get(2));
  }

  public void testSealedClassesPermitted() {
    Shape square = new Square();
    Shape circle =  new Circle();

    checkIfCompiled(square, circle);
  }

  private void checkIfCompiled(Shape square, Shape circle) {
    assertTrue(square instanceof Square);
    assertTrue(circle instanceof Circle);
  }

  /**
   * Sample nested record. Has no components. Implements a single interface
   */
  record InnerRecord() implements Comparable<InnerRecord> {
    /**
     * Simple accessor-looking method to ensure it doesn't participate in hashcode/tostring.
     */
    public String value() {
      return "Hello";
    }

    @Override
    public int compareTo(InnerRecord other) {
      return 0;
    }
  }

  /**
   * Record with a single static field.
   */
  record RecordWithStaticField() {
    public static String foo = "bar";
  }

  /**
   * Record type that takes a record as a component
   */
  record RecordWithReferenceType(TopLevelRecord refType){}

  public void testRecordClasses() {
    /**
     * Sample local record.
     */
    record LocalRecord() {
      @Override
      public String toString() {
        return "Example";
      }
    }

    assertTrue(new InnerRecord() instanceof Record);
    assertTrue(new InnerRecord() instanceof Comparable);

    assertFalse(new InnerRecord().toString().contains("Hello"));
    assertTrue(new InnerRecord().toString().startsWith("InnerRecord"));
    assertEquals(0, new InnerRecord().hashCode());
    assertEquals(new InnerRecord(), new InnerRecord());

    assertEquals("Example", new LocalRecord().toString());

    TopLevelRecord withValues = new TopLevelRecord("Banana", 7);
    assertTrue(withValues.toString().contains("7"));
    assertTrue(withValues.toString().contains("Banana"));
    assertEquals("Banana", withValues.name());
    assertEquals(7, withValues.count());
    assertEquals("bar", RecordWithStaticField.foo);
    // Under the current implementation this next line would fail - this is not inconsistent with the spec,
    // but it is different than what the JVM does.
//    assertEquals(0, new TopLevelRecord("", 0).hashCode());
    assertFalse(0 == new TopLevelRecord("", 7).hashCode());
    assertFalse(0 == new TopLevelRecord("Pear", 0).hashCode());

    assertFalse(new InnerRecord().equals(new LocalRecord()));
    assertFalse(new InnerRecord().equals(null));
    assertFalse(new LocalRecord().equals(null));

    RecordWithReferenceType sameA = new RecordWithReferenceType(new TopLevelRecord("a", 1));
    RecordWithReferenceType sameB = new RecordWithReferenceType(new TopLevelRecord("a", 1));
    RecordWithReferenceType different = new RecordWithReferenceType(new TopLevelRecord("a", 2));
    // check that an instance is equal to itself
    assertEquals(sameA, sameA);
    assertEquals(sameA.hashCode(), sameA.hashCode());
    //check that an instance is equal to a different record instance with same values
    assertEquals(sameA, sameB);
    assertEquals(sameA.hashCode(), sameB.hashCode());

    assertFalse(sameA.equals(different));
    assertFalse(sameA.hashCode() == different.hashCode());

    assertFalse(sameA.equals(null));
  }

  /**
   * Simple record with one property accessor, one default method accessor
   */
  @JsType(namespace = "java17")
  public record JsRecord1(@JsProperty String name, int value) { }

  /**
   * Simple native type to verify JsRecord1.
   */
  @JsType(name = "JsRecord1", namespace = "java17", isNative = true)
  public static class JsObject1 {
    public String name;
    public native int value();
    public JsObject1(String name, int value) { }
  }

  /**
   * Record with explicit method accessor
   */
  @JsType(namespace = "java17")
  public record JsRecord2(@JsMethod String name, int value) { }

  /**
   * Simple native type to verify JsRecord2.
   */
  @JsType(name = "JsRecord2", namespace = "java17", isNative = true)
  public static class JsObject2 {
    public JsObject2(String name, int value) { }

    public native String name();
    public native int value();
  }

  /**
   * Record with exported properties and methods.
   */
  public record JsRecord3(String red, JsRecord1 green, JsRecord2 blue) {
    @JsProperty
    public String getFlavor() {
      return "grape";
    }
    @JsMethod
    public int countBeans() {
      return 7;
    }
  }

  /**
   * Represented as an interface since there is no constructor to call or use to type check.
   */
  @JsType(isNative = true)
  public interface JsObject3 {
    @JsProperty
    String getFlavor();
    int countBeans();
  }

  public void testJsTypeRecords() {
    // Test with default accessor (method) and a property accessor
    JsRecord1 r1 = new JsRecord1("foo", 7);
    assertEquals("foo", r1.name());
    assertEquals(7, r1.value());
    assertEquals(new JsRecord1("foo", 7), r1);

    // Create an instance from JS, verify it is the same
    JsObject1 o1 = new JsObject1("foo", 7);
    assertEquals("foo", o1.name);
    assertEquals(7, o1.value());
    assertEquals(o1.toString(), r1.toString());
    assertEquals(o1, r1);

    // Repeat the test with methods explicitly configured for accessors
    JsRecord2 r2 = new JsRecord2("foo", 7);
    assertEquals("foo", r2.name());
    assertEquals(7, r2.value());
    assertEquals(new JsRecord2("foo", 7), r2);

    // Create an instance from JS, verify it is the same
    JsObject2 o2 = new JsObject2("foo", 7);
    assertEquals("foo", o2.name());
    assertEquals(7, o2.value());
    assertEquals(o2.toString(), r2.toString());
    assertEquals(o2, r2);

    // Test an object with exposed properties and methods
    JsRecord3 r3 = new JsRecord3("fork", r1, r2);
    assertEquals("grape", r3.getFlavor());
    assertEquals(7, r3.countBeans());

    // Cast the instance to JS, verify it is the same
    JsObject3 o3 = (JsObject3) (Object) r3;
    assertEquals("grape", r3.getFlavor());
    assertEquals(7, r3.countBeans());
  }

  public void testInstanceOfPatternMatching() {
    Shape shape1 = new Circle();
    if (shape1 instanceof Circle circle) {
      circle.getDiameter();
      assertTrue(true);
      return;
    }
    fail();
  }

  public void testInstanceOfPatternMatchingWithSideEffectExpression() {
    Shape shape1 = new Circle();
    if (Shape.returnAndIncrement(shape1) instanceof Circle circle) {
      circle.getDiameter();
      assertTrue(true);
      assertEquals(1, Shape.count);
      return;
    }
    fail();
  }

  public void testInstanceOfPatternMatchingWithAnd() {
    Shape shape1 = new Circle();
    Shape shape2 = new Square();

    if (shape1 instanceof Circle circle && shape2 instanceof Square square) {
      circle.getDiameter();
      square.getLength();
      assertTrue(true);
      return;
    }
    fail();
  }

  public void testInstanceOfPatternMatchingWithCondition() {
    Shape shape2 = new Square();
    if (shape2 instanceof Square square && square.getLength() > 0) {
      square.getLength();
      assertTrue(true);
      return;
    }
    fail();
  }

  public void testInstanceOfPatternMatchingWithAsNotCondition() {
    Shape shape1 = new Square();
    if (!(shape1 instanceof Square square && square.getLength() > 10)) {
      assertTrue(true);
      return;
    }
    fail();
  }

  public void testMultipleInstanceOfPatternMatchingWithSameVariableName() {
    Shape shape1 = new Square();
    Shape shape2 = new Square();
    boolean a = false;
    boolean b = false;
    if (shape1 instanceof Square square && square.getLength() > 0) {
      a = true;
    }
    if (shape2 instanceof Square square && square.getLength() > 0) {
      b = true;
    }
    assertTrue(a && b);
  }

  public void testMultipleInstanceOfPatternMatchingWithSameVariableNameWithDifferentTypes() {
    Shape shape1 = new Square();
    Shape shape2 = new Circle();
    boolean a = false;
    boolean b = false;
    if (shape1 instanceof Square shp && shp.getLength() > 0) {
      a = true;
    }
    if (shape2 instanceof Circle shp && shp.getDiameter() > 0) {
      b = true;
    }
    assertTrue(a && b);
  }

  public void testInstanceOfPatternMatchingIsFalse() {
    Shape shape1 = new Square();
    if (shape1 instanceof Circle shp) {
      fail("Should have not reached this point.");
    }
    assertTrue(true);
  }

  public void testInstanceOfPatternMatchingInLambda() {
    Foo foo = new Foo();
    assertTrue(foo.isSquare().run());
  }

  public void testInstanceOfPatternMatchingAsReturn() {
    Bar bar = new Bar();
    assertTrue(bar.isSquare());
  }

  public void testNegativeInstanceOfPatternOutsideIfScope() {
    Object bar = new Bar();
    if (!(bar instanceof Bar b)) {
      throw new RuntimeException();
    }
    assertTrue(b.isSquare());
  }

  public void testSwitchExpressionOnConstant() {
    int value = switch(0) {
      default -> 17;
    };
    assertEquals(17, value);
  }

  public void testSwitchWithMultipleCaseValues() {
    for (int i = 0; i < 5; i++) {
      boolean reachedDefault = false;
      boolean isEven = switch(i) {
        case 0, 2:
          yield true;
        case 1, 3, 5:
          yield false;
        default:// default is required for switch exprs, and we will hit it for 4
          reachedDefault = true;
          yield true;
      };
      assertEquals(i == 4, reachedDefault);
      assertEquals("" + i, i % 2 == 0, isEven);
    }
  }

  public void testSwitchInSubExpr() {
    double value = Math.random();// non-constant value between 0 and 1
    boolean notCalled = true;
    if ((int) value % 5 == 3 && switch ((int) value / 5) {
      case 4:
        notCalled = false;
        yield true;
      default:
        notCalled = false;
        yield false;
    }) {
      fail("should not be reached");
    }
    assertTrue(notCalled);

    double result = (int) value % 7 == 2 ?
            switch((int)value / 7) {
              case 1:
                notCalled = false;
                yield 1.0;
              default:
                notCalled = false;
                yield 2.0;
            }
            : 4.0;
    assertTrue(notCalled);
  }

  public void testSwitchExprInlining() {
    enum HasSwitchMethod {
      A, RED, SUNDAY, JANUARY, ZERO;
      public static final int which(HasSwitchMethod whichSwitch) {
        return switch(whichSwitch) {
          case A -> 1;
          case RED -> 2;
          case SUNDAY, JANUARY -> 4;
          case ZERO -> 5;
        };
      }
      public static final int pick(HasSwitchMethod whichSwitch) {
        return 2 * switch(whichSwitch) {
          case A -> 1;
          case RED -> 2;
          case SUNDAY, JANUARY -> 4;
          case ZERO -> 5;
        };
      }
      public static final String select(HasSwitchMethod whichSwitch) {
        if (Math.random() > 2) {
          return "none";
        }
        return switch(whichSwitch) {
          case A -> "1";
          case RED -> "2";
          case SUNDAY, JANUARY -> "4";
          case ZERO -> "5";
        };
      }
    }

    HasSwitchMethod uninlinedValue = Math.random() > 2 ? HasSwitchMethod.A : HasSwitchMethod.RED;
    assertEquals(2, HasSwitchMethod.which(uninlinedValue));
    assertEquals(4, HasSwitchMethod.pick(uninlinedValue));
    assertEquals("hello 2", "hello " + HasSwitchMethod.select(uninlinedValue));
  }

  private static final String ONE = "1";
  private static final String TWO = "2";
  private static final String FOUR = "4";

  public void testInlinedStringConstantsInCase() {
    int value = switch(Math.random() > 2 ? "2" : "4") {
      case ONE, TWO -> 2;
      case FOUR -> 4;
      default -> 0;
    };
    assertEquals(4, value);
  }

  // https://github.com/gwtproject/gwt/issues/10044
  public void testCaseArrowLabelsVoidExpression() {
    // Each switch is extracted to its own method to avoid the early return bug.
    assertEquals("success", arrowWithVoidExpr());

    // Arrow with non-void expr
    assertEquals("success", arrowWithStringExpr());
    assertEquals("success", arrowWithIntExpr());

    // Arrow with a statement - doesn't fail as part of this bug. This exists to verify
    // that JDT won't give us a yield with a statement somehow.
    assertEquals("success", arrowWithStatement());
  }

  private static String arrowWithVoidExpr() {
    switch(0) {
      case 0 -> assertTrue(true);
    };
    return "success";
  }

  private static String arrowWithStringExpr() {
    switch(0) {
      case 0 -> new Object().toString();
    };
    return "success";
  }

  private static String arrowWithIntExpr() {
    switch(0) {
      case 0 -> new Object().hashCode();
    };
    return "success";
  }

  private static String arrowWithStatement() {
    switch(0) {
      case 0 -> {
        if (true) {
          new Object().toString();
        }
      }
    };
    return "success";
  }
}
