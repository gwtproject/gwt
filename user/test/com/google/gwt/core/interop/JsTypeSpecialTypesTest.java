/*
 * Copyright 2015 Google Inc.
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
package com.google.gwt.core.interop;

import com.google.gwt.junit.client.GWTTestCase;

import jsinterop.annotations.JsFunction;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;

/**
 * Tests special JsType functionality.
 */
@SuppressWarnings("cast")
public class JsTypeSpecialTypesTest extends GWTTestCase {

  @Override
  public String getModuleName() {
    return "com.google.gwt.core.Interop";
  }

  @JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "Array")
  static class NativeArray {
  }

  public void testNativeArray() {
    Object object = new Object[10];

    assertNotNull((NativeArray) object);
    assertTrue(object instanceof NativeArray);
    assertTrue(object instanceof NativeObject);
    assertFalse(object instanceof NativeFunction);
    assertFalse(object instanceof NativeString);
    assertFalse(object instanceof NativeNumber);

    Object nativeArray = new NativeArray();
    assertNotNull((NativeArray[]) nativeArray);
    assertTrue(nativeArray instanceof NativeArray[]);
    assertTrue(nativeArray instanceof NativeObject);
    assertTrue(nativeArray instanceof NativeArray);
    assertFalse(nativeArray instanceof NativeFunction);
    assertFalse(nativeArray instanceof NativeString);
    assertFalse(nativeArray instanceof NativeNumber);
  }

  @JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "Function")
  static class NativeFunction {
  }

  @JsFunction
  interface  SomeFunctionalInterface {
    void m();
  }

  public void testNativeFunction() {
    Object object = new SomeFunctionalInterface() {
          @Override
          public void m() {
          }
        };

    assertNotNull((NativeFunction) object);
    assertTrue(object instanceof NativeFunction);
    assertTrue(object instanceof NativeObject);
    assertFalse(object instanceof NativeFunction);
    assertFalse(object instanceof NativeString);
    assertFalse(object instanceof NativeNumber);

    SomeFunctionalInterface nativeFunction = (SomeFunctionalInterface) new NativeFunction();
    assertTrue(nativeFunction instanceof SomeFunctionalInterface);
    assertTrue(nativeFunction instanceof NativeObject);
    assertFalse(nativeFunction instanceof NativeArray);
    assertTrue(nativeFunction instanceof NativeFunction);
    assertFalse(nativeFunction instanceof NativeString);
    assertFalse(nativeFunction instanceof NativeNumber);
  }

  @JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "Number")
  static class NativeNumber {
    public NativeNumber(double number) { }
    public native NativeNumber valueOf();
  }

  public void testNativeNumber() {
    Object object = new Double(1);

    assertNotNull((NativeNumber) object);
    assertTrue(object instanceof NativeNumber);
    assertFalse(object instanceof NativeObject);
    assertFalse(object instanceof NativeFunction);
    assertFalse(object instanceof NativeString);
    assertFalse(object instanceof NativeArray);

    // new NativeString() returns a boxed JS number. Java Double object are only interchangeable
    // with unboxed JS numbers.
    Object nativeNumber = new NativeNumber(10.0).valueOf();
    assertNotNull((Double) nativeNumber);
    assertTrue(nativeNumber instanceof Double);
    assertEquals(10.0, (Double) nativeNumber);
    assertFalse(nativeNumber instanceof NativeObject);
    assertFalse(nativeNumber instanceof NativeArray);
    assertFalse(nativeNumber instanceof NativeFunction);
    assertFalse(nativeNumber instanceof NativeString);
    assertTrue(nativeNumber instanceof NativeNumber);
  }

  @JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "String")
  static class NativeString {
    public NativeString(String someString) { }
    public native NativeString valueOf();
  }

  public void testNativeString() {
    Object object = "Hello";

    assertNotNull((NativeString) object);
    assertTrue(object instanceof NativeString);
    assertFalse(object instanceof NativeObject);
    assertFalse(object instanceof NativeFunction);
    assertFalse(object instanceof NativeArray);
    assertFalse(object instanceof NativeNumber);

    // new NativeString() returns a boxed JS string. Java String objects are only interchangeable
    // with unboxed JS strings.
    Object nativeString = new NativeString("Hello").valueOf();
    assertNotNull((String) nativeString);
    assertTrue(nativeString instanceof String);
    assertEquals("Hello", nativeString);
    assertFalse(nativeString instanceof NativeObject);
    assertFalse(nativeString instanceof NativeArray);
    assertFalse(nativeString instanceof NativeFunction);
    assertTrue(nativeString instanceof NativeString);
    assertFalse(nativeString instanceof NativeNumber);
  }

  @JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "Object")
  static class NativeObject {
  }

  public void testNativeObject() {
    Object object = new Object();

    assertNotNull((NativeObject) object);
    assertTrue(object instanceof NativeObject);
    assertFalse(object instanceof NativeArray);
    assertFalse(object instanceof NativeFunction);
    assertFalse(object instanceof NativeString);
    assertFalse(object instanceof NativeNumber);

    Object nativeObject = new NativeObject();
    assertNotNull((Object) nativeObject);
    assertTrue(nativeObject instanceof Object);
    assertTrue(nativeObject instanceof NativeObject);
    assertFalse(nativeObject instanceof NativeArray);
    assertFalse(nativeObject instanceof NativeFunction);
    assertFalse(nativeObject instanceof NativeString);
    assertFalse(nativeObject instanceof NativeNumber);
  }

  private static native Object getUndefined() /*-{
  }-*/;

  public void testNullAndUndefined() {
    Object object = null;

    assertFalse(object instanceof NativeObject);
    assertFalse(object instanceof NativeArray);
    assertFalse(object instanceof NativeFunction);
    assertFalse(object instanceof NativeString);
    assertFalse(object instanceof NativeNumber);

    object = getUndefined();
    assertFalse(object instanceof NativeObject);
    assertFalse(object instanceof NativeArray);
    assertFalse(object instanceof NativeFunction);
    assertFalse(object instanceof NativeString);
    assertFalse(object instanceof NativeNumber);
  }
}
