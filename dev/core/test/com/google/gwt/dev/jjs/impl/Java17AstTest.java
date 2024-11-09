/*
 * Copyright 2024 Google Inc.
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

import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.dev.javac.testing.impl.JavaResourceBase;
import com.google.gwt.dev.jjs.ast.JAbstractMethodBody;
import com.google.gwt.dev.jjs.ast.JDeclaredType;
import com.google.gwt.dev.jjs.ast.JInterfaceType;
import com.google.gwt.dev.jjs.ast.JProgram;
import com.google.gwt.dev.jjs.ast.JStringLiteral;
import com.google.gwt.dev.jjs.ast.JType;

import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.List;

/**
 * Tests that {@link GwtAstBuilder} correctly builds the AST for
 * features introduced in Java 17.
 */
public class Java17AstTest extends FullCompileTestBase {

  @Override
  public void setUp() throws Exception {
    super.setUp();
    addAll(JavaResourceBase.createMockJavaResource("test.TextBlock",
        "package test;",
        "public interface TextBlock {",
        "String text =\"\"\"",
        "line 1",
        "line 2",
        "line 3",
        "\"\"\";",
        "}"
    ));

    addAll(JavaResourceBase.createMockJavaResource("test.Shape",
        "package test;",
        "public sealed class Shape permits Square, Circle {",
        "private static int count = 0;",
        "public static Shape returnAndIncrement(Shape shape) {",
        "count++;",
        "return shape;",
        "}",
        "}"
    ));

    addAll(JavaResourceBase.createMockJavaResource("test.Square",
        "package test;",
        "public final class Square extends Shape {",

        "public int getLength() {",
        "return 10;",
        "}",
        "public double getSide() {",
        "return 0;",
        "}",
        "}"
    ));

    addAll(JavaResourceBase.createMockJavaResource("test.Circle",
        "package test;",
        "public final class Circle extends Shape {",
        "public double getDiameter() {",
        "return 0;",
        "}",
        "}"
    ));

    addAll(JavaResourceBase.createMockJavaResource("test.Months",
        "package test;",
        "public enum Months {",
        "JANUARY, FEBRUARY, MARCH, APRIL, MAY, JUNE, JULY, AUGUST, SEPTEMBER, OCTOBER, NOVEMBER, DECEMBER;",
        "}"
    ));

    addAll(JavaResourceBase.createMockJavaResource("test.TestSupplier",
        "package test;",
        "public interface TestSupplier {",
        "  boolean run();",
        "}"
    ));

    addAll(JavaResourceBase.createMockJavaResource("test.Polygon",
        "package test;",
        "public class Polygon {",
        "  public int sides;",
        "}"
    ));
  }

  public void testTextBlocks() throws Exception {
    JProgram program = compileSnippet("void", "new TextBlock(){};");
    JInterfaceType textBlock = (JInterfaceType) findType(program, "TextBlock");
    JStringLiteral initializer = (JStringLiteral) textBlock.getFields().get(0).getInitializer();
    String multiLineString = initializer.getValue();
    List<String> lines = Arrays.asList(multiLineString.split("\n"));
    assertEquals(3, lines.size());
    assertEquals("line 1", lines.get(0));
    assertEquals("line 2", lines.get(1));
    assertEquals("line 3", lines.get(2));
  }

  public void testSealedClassesPermitted() throws Exception {
    compileSnippet("void", "Shape square = new Square();");
    compileSnippet("void", "Shape circle = new Circle();");
  }

  public void testSealedClassesNotPermitted() {
    try {
      addSnippetClassDecl("public final class Rectangle extends Shape {" +
          "}");
      compileSnippet("void", "Shape rectangle = new Rectangle();");
      fail("Compile should have failed but succeeded.");
    } catch (Exception e) {
      if (!(e.getCause() instanceof UnableToCompleteException)
          && !(e instanceof UnableToCompleteException)) {
        e.printStackTrace();
        fail();
      }
    }
  }

  public void testRecordSyntax() throws UnableToCompleteException {
    addSnippetClassDecl("public record Point(int x, int y) {}");
    compileSnippet("void", "Point rectangle = new Point(0, 0);");
  }

  public void testSwitchExpressions() throws UnableToCompleteException {
    compileSnippet("void", "var month = Months.JUNE;" +
        "var result = switch(month) {\n" +
        "    case JANUARY, JUNE, JULY -> 3;\n" +
        "    case FEBRUARY, SEPTEMBER, OCTOBER, NOVEMBER, DECEMBER -> 1;\n" +
        "    case MARCH, MAY, APRIL, AUGUST -> 2;\n" +
        "    default -> 0;" +
        "};");
  }

  public void testInstanceOfPatternMatching() throws UnableToCompleteException {
    JProgram program = compileSnippet("void", "Shape shape1 = new Circle();" +
        "if(shape1 instanceof Circle circle) {" +
        "double diameter = circle.getDiameter();" +
        "}"
    );

    JAbstractMethodBody onModuleLoadMethod = findMethod(program, "onModuleLoad")
        .getBody();

    assertTrue(onModuleLoadMethod
        .toSource()
        .contains("Circle circle;"));

    assertTrue(onModuleLoadMethod
        .toSource()
        .contains("Shape $instanceOfExpr_3;"));

    assertTrue(onModuleLoadMethod
        .toSource()
        .contains(
            "($instanceOfExpr_3 = shape1) instanceof Circle && null != (circle = (Circle) $instanceOfExpr_3)"));
  }

  public void testInstanceOfPatternMatchingWithSideEffectsExpression()
      throws UnableToCompleteException {
    JProgram program = compileSnippet("void", "Shape shape1 = new Circle();" +
        "if(Shape.returnAndIncrement(shape1) instanceof Circle circle) {" +
        "double diameter = circle.getDiameter();" +
        "}"
    );

    JAbstractMethodBody onModuleLoadMethod = findMethod(program, "onModuleLoad")
        .getBody();

    assertTrue(onModuleLoadMethod
        .toSource()
        .contains("Circle circle;"));

    assertTrue(onModuleLoadMethod
        .toSource()
        .contains("Shape $instanceOfExpr_3;"));

    assertTrue(onModuleLoadMethod
        .toSource()
        .contains(
            "($instanceOfExpr_3 = Shape.returnAndIncrement(shape1)) instanceof Circle && null != (circle = (Circle) $instanceOfExpr_3)"));
  }

  public void testInstanceOfPatternMatchingWithAnd() throws UnableToCompleteException {
    JProgram program = compileSnippet("void",
        "Shape shape1 = new Circle();\n" +
        "Shape shape2 = new Square();\n" +
        "if(shape1 instanceof Circle circle && shape2 instanceof Square square) {\n" +
        "double diameter = circle.getDiameter();\n" +
        "}\n"
    );

    JAbstractMethodBody onModuleLoadMethod = findMethod(program, "onModuleLoad")
        .getBody();

    assertTrue(onModuleLoadMethod
        .toSource()
        .contains("Circle circle;"));

    assertTrue(onModuleLoadMethod
        .toSource()
        .contains("Shape $instanceOfExpr_4;"));

    assertTrue(onModuleLoadMethod
        .toSource()
        .contains("Square square;"));

    assertTrue(onModuleLoadMethod
        .toSource()
        .contains("Shape $instanceOfExpr_6"));

    assertTrue(onModuleLoadMethod
        .toSource()
        .contains(
            "($instanceOfExpr_4 = shape1) instanceof Circle && null != (circle = (Circle) $instanceOfExpr_4)"));

    assertTrue(onModuleLoadMethod
        .toSource()
        .contains(
            "($instanceOfExpr_6 = shape2) instanceof Square && null != (square = (Square) $instanceOfExpr_6)"));
  }

  public void testSwitchExpressionsWithYield() throws UnableToCompleteException {
    compileSnippet("void", "    int i = switch(1) {\n" +
        "      case 1:\n" +
        "        yield 2;\n" +
        "      default:\n" +
        "        yield 7;\n" +
        "    };");
  }

  public void testInstanceOfPatternMatchingWithCondition() throws UnableToCompleteException {
    JProgram program = compileSnippet("void",
        "Shape shape2 = new Square();\n" +
        "if(shape2 instanceof Square square && square.getLength() > 0) {\n" +
        "double diameter = square.getSide();\n" +
        "}\n"
    );

    JAbstractMethodBody onModuleLoadMethod = findMethod(program, "onModuleLoad")
        .getBody();

    assertTrue(onModuleLoadMethod
        .toSource()
        .contains("Square square;"));

    assertTrue(onModuleLoadMethod
        .toSource()
        .contains("Shape $instanceOfExpr_3;"));

    assertTrue(onModuleLoadMethod
        .toSource()
        .contains(
            "($instanceOfExpr_3 = shape2) instanceof Square && null != (square = (Square) $instanceOfExpr_3)"));
  }

  public void testInstanceOfPatternMatchingWithAsNotCondition() throws UnableToCompleteException {
    JProgram program = compileSnippet("void",
        "Shape shape1 = new Square();\n" +
        "if(!(shape1 instanceof Square square && square.getLength() > 0)) {\n" +
        "}\n"
    );

    JAbstractMethodBody onModuleLoadMethod = findMethod(program, "onModuleLoad")
        .getBody();

    assertTrue(onModuleLoadMethod
        .toSource()
        .contains("Square square;"));

    assertTrue(onModuleLoadMethod
        .toSource()
        .contains("Shape $instanceOfExpr_3;"));

    assertTrue(onModuleLoadMethod
        .toSource()
        .contains(
            "($instanceOfExpr_3 = shape1) instanceof Square && null != (square = (Square) $instanceOfExpr_3)"));
  }

  public void testMultipleInstanceOfPatternMatchingWithSameVariableName() throws UnableToCompleteException {
    JProgram program = compileSnippet("void",
        "Shape shape1 = new Square();\n" +
        "Shape shape2 = new Square();\n" +
        "if(shape1 instanceof Square square && square.getLength() > 0) {\n" +
        "}\n" +
        "if(shape2 instanceof Square square && square.getLength() > 0) {\n" +
        "}\n"
    );

    JAbstractMethodBody onModuleLoadMethod = findMethod(program, "onModuleLoad")
        .getBody();

    assertEquals(1, StringUtils.countMatches(onModuleLoadMethod.toSource(), "Square square;"));

    assertTrue(onModuleLoadMethod
        .toSource()
        .contains("Shape $instanceOfExpr_4;"));

    assertTrue(onModuleLoadMethod
        .toSource()
        .contains("Shape $instanceOfExpr_6;"));

    assertTrue(onModuleLoadMethod
        .toSource()
        .contains(
            "($instanceOfExpr_4 = shape1) instanceof Square && null != (square = (Square) $instanceOfExpr_4)"));

    assertTrue(onModuleLoadMethod
        .toSource()
        .contains(
            "($instanceOfExpr_6 = shape2) instanceof Square && null != (square = (Square) $instanceOfExpr_6)"));
  }

  public void testInstanceOfPatternMatchingInLambda() throws UnableToCompleteException {
    addSnippetClassDecl("public class Foo {\n" +
        "private Shape shape;\n" +
        "public Foo(){\n" +
        "shape = new Square();\n" +
        "}\n" +
        "public TestSupplier isSquare(){\n" +
        "return () -> shape instanceof Square square && square.getLength() > 0;\n" +
        "}\n" +
        "}");

    JProgram program = compileSnippet("void", "Foo foo = new Foo();");
    JType foo = findType(program, "test.EntryPoint.Foo");
    JAbstractMethodBody lambda = findMethod((JDeclaredType) foo, "lambda$0")
        .getBody();

    assertTrue(lambda
        .toSource()
        .contains("Square square;"));

    assertTrue(lambda
        .toSource()
        .contains("Shape $instanceOfExpr_2;"));

    assertTrue(lambda
        .toSource()
        .contains(
            "($instanceOfExpr_2 = this.shape) instanceof Square && null != (square = (Square) $instanceOfExpr_2)"));
  }

  public void testInstanceOfPatternMatchingAsReturn() throws UnableToCompleteException {

    addSnippetClassDecl("public class Foo {\n" +
        "private Shape shape;\n" +
        "public Foo(){\n" +
        "shape = new Square();\n" +
        "}\n" +
        "public boolean isSquare(){\n" +
        "return shape instanceof Square square && square.getLength() > 0;\n" +
        "}\n" +
        "}");
    JProgram program = compileSnippet("void", "Foo foo = new Foo();");
    JType foo = findType(program, "test.EntryPoint.Foo");
    JAbstractMethodBody isSquare = findMethod((JDeclaredType) foo, "isSquare")
        .getBody();

    assertTrue(isSquare
        .toSource()
        .contains("Square square;"));

    assertTrue(isSquare
        .toSource()
        .contains("Shape $instanceOfExpr_2;"));

    assertTrue(isSquare
        .toSource()
        .contains(
            "($instanceOfExpr_2 = this.shape) instanceof Square && null != (square = (Square) $instanceOfExpr_2)"));
  }

  public void testInstanceOfPatternMatchingWithConditionalOperator()
      throws UnableToCompleteException {
      compileSnippet("void", "var a = true ? \"a\":'a';\n" +
          "if(a instanceof CharSequence p) {\n" +
          "     int b= p.length();\n" +
          "}");
  }

  // The JDT ast nodes for both switch statements and expressions extend Expression, and specific
  // ast builder traversals previously. Test switch expressions/statements where statements/blocks
  // can be encountered.
  public void testSwitchesWithoutBlocks() throws UnableToCompleteException {
    compileSnippet("void",
        "for (int i = 0; i < 10; i++) " +
            "  switch(i) { " +
            "    case 1: break;" +
            "    case 2: " +
            "    default:" +
            "  }");
    compileSnippet("void",
        "for (int i : new int[] {1, 2, 3, 4}) " +
            "  switch(i) { " +
            "    case 1:" +
            "    case 2:" +
            "    default:" +
            "  }");
    compileSnippet("void",
        "switch (4) {" +
            "case 1:" +
            "  switch (2) {" +
            "    case 2:" +
            "    case 4:" +
            "    default:" +
            "  }" +
            "}");
    compileSnippet("void",
        "if (true == false) " +
            "  switch (7) {" +
            "    case 4: {" +
            "      break;" +
            "    }" +
            "  }" +
            "else " +
            "  switch (8) {" +
            "    case 9:" +
            "  }");

    compileSnippet("void",
        "while(true)" +
            "  switch(99) { " +
            "    default:" +
            "  }");

    compileSnippet("void",
        "do" +
            "  switch(0) { " +
            "    default:" +
            "  }" +
            "while (false);");

    compileSnippet("void",
        "foo:" +
            "  switch(123) { " +
            "    default:" +
            "  }");
  }

  @Override
  protected void optimizeJava() {
  }
}
