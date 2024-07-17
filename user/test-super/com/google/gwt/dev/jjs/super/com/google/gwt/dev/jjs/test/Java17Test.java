/*
 * Copyright 2019 Google Inc.
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
}
