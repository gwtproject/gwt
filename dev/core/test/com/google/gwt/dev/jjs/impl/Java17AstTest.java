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
import com.google.gwt.dev.jjs.InternalCompilerException;
import com.google.gwt.dev.jjs.ast.JInterfaceType;
import com.google.gwt.dev.jjs.ast.JProgram;
import com.google.gwt.dev.jjs.ast.JStringLiteral;
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
        "}"
    ));

    addAll(JavaResourceBase.createMockJavaResource("test.Square",
        "package test;",
        "public final class Square extends Shape {",
        "}"
    ));

    addAll(JavaResourceBase.createMockJavaResource("test.Circle",
        "package test;",
        "public final class Circle extends Shape {",
        "}"
    ));

    addAll(JavaResourceBase.createMockJavaResource("test.Months",
        "package test;",
        "public enum Months {",
        "JANUARY, FEBRUARY, MARCH, APRIL, MAY, JUNE, JULY, AUGUST, SEPTEMBER, OCTOBER, NOVEMBER, DECEMBER;",
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

  public void testRecordsNotSupported() {
    try {
      addSnippetClassDecl("public record Point(int x, int y) {}");
      compileSnippet("void", "Point rectangle = new Point(0, 0);");
      fail("Compile should have failed but succeeded.");
    } catch (Exception e) {
      if (!(e.getCause() instanceof UnableToCompleteException)
          && !(e instanceof UnableToCompleteException)) {
        e.printStackTrace();
        fail();
      }
    }
  }

  public void testSwitchExpressionsNotSupported() {
    try {
      compileSnippet("void", "var month = Months.JUNE;" +
          "var result = switch(month) {\n" +
          "    case JANUARY, JUNE, JULY -> 3;\n" +
          "    case FEBRUARY, SEPTEMBER, OCTOBER, NOVEMBER, DECEMBER -> 1;\n" +
          "    case MARCH, MAY, APRIL, AUGUST -> 2;\n" +
          "    default -> 0;" +
          "};");
      fail("Compile should have failed but succeeded, switch expression is not supported.");
    } catch (Exception e) {
      if (!(e.getCause() instanceof InternalCompilerException)
          && !(e instanceof InternalCompilerException)) {
        e.printStackTrace();
        fail();
      }
      assertEquals("Switch expressions not yet supported", e.getMessage());
    }
  }

  public void testSwitchExpressionsInitializerShouldFail() {
    try {
      compileSnippet("void", "    int i = switch(1) {\n" +
          "      case 1:\n" +
          "        yield 2;\n" +
          "      default:\n" +
          "        yield 7;\n" +
          "    };");
      fail("Compile should have failed but succeeded, switch expressions as initializer should fail.");
    } catch (Exception e) {
      if (!(e.getCause() instanceof InternalCompilerException)
          && !(e instanceof InternalCompilerException)) {
        e.printStackTrace();
        fail();
      }
      assertEquals("Switch expressions not yet supported", e.getMessage());
    }
  }

  @Override
  protected void optimizeJava() {
  }
}
