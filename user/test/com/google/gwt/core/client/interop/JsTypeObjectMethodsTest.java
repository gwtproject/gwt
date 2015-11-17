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
package com.google.gwt.core.client.interop;

import com.google.gwt.junit.client.GWTTestCase;

import jsinterop.annotations.JsType;

/**
 * Tests JsType functionality.
 */
@SuppressWarnings("cast")
public class JsTypeObjectMethodsTest extends GWTTestCase {

  @Override
  public String getModuleName() {
    return "com.google.gwt.core.Core";
  }

  @JsType(isNative = true)
  interface SomeNativeObject {
  }

  public native SomeNativeObject createWithEqualsAndHashCode(int a, int b) /*-{
    return {a : a, b : b, hashCode: function() { return this.b }, equals :
        function(other) { return this.a == other.a; } };
  }-*/;

  public native SomeNativeObject createWithoutEqualsAndHashCode(int a, int b) /*-{
    return {a : a, b : b} ;
  }-*/;


  public void testHashCode() {
    assertEquals(3, createWithEqualsAndHashCode(1, 3).hashCode());
    SomeNativeObject o1 = createWithoutEqualsAndHashCode(1, 3);
    SomeNativeObject o2 = createWithoutEqualsAndHashCode(1, 3);
    assertTrue(o1.hashCode() != o2.hashCode());
  }

  public void testEquals() {
    assertEquals(createWithEqualsAndHashCode(1, 3), createWithEqualsAndHashCode(1, 4));
    SomeNativeObject o1 = createWithoutEqualsAndHashCode(1, 3);
    SomeNativeObject o2 = createWithoutEqualsAndHashCode(1, 3);
    assertTrue(createWithEqualsAndHashCode(1, 3).equals(createWithoutEqualsAndHashCode(1, 4)));
    assertFalse(createWithoutEqualsAndHashCode(1, 4).equals(createWithEqualsAndHashCode(1, 3)));
    assertFalse(o1.equals(o2));
  }
}
