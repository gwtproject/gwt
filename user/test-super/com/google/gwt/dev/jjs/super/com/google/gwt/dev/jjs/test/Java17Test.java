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

  }

  public final class Square extends Shape {

  }

  public final class Circle extends Shape {

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
}
