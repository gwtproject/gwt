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
}
