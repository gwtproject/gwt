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
package com.google.gwt.core.client.interop;

import com.google.gwt.junit.client.GWTTestCase;

/**
 * Tests JsFunction functionality.
 */
public class JsFunctionTest extends GWTTestCase {

  @Override
  public String getModuleName() {
    return "com.google.gwt.core.Core";
  }

  public void testJsFunctionBasic() {
    MyJsFunctionInterface jsFunctionInterface = new MyJsFunctionInterface() {
      @Override
      public int foo(int a) {
        return a + 2;
      }
    };
    assertEquals(12, jsFunctionInterface.foo(10));
    assertEquals(12, callAsFunction(jsFunctionInterface, 10));
    assertEquals(12, callAsCallBackFunction(jsFunctionInterface, 10));
  }

  public void testJsFunctionSubInterface() {
    MyJsFunctionSubInterface jsFunctionSubInterface = new MyJsFunctionSubInterface() {
        @Override
      public int foo(int a) {
        return a + 3;
      }
    };
    assertEquals(13, jsFunctionSubInterface.foo(10));
    assertEquals(13, callAsFunction(jsFunctionSubInterface, 10));
    assertEquals(13, callAsCallBackFunction(jsFunctionSubInterface, 10));
  }

  public void testJsFunctionSubImpl() {
    MyJsFunctionInterfaceSubImpl jsFunctionInterfaceSubImpl = new MyJsFunctionInterfaceSubImpl();
    assertEquals(21, jsFunctionInterfaceSubImpl.foo(10));
    assertEquals(21, callAsFunction(jsFunctionInterfaceSubImpl, 10));
    assertEquals(21, callAsCallBackFunction(jsFunctionInterfaceSubImpl, 10));
  }

  public void testJsFunctionMultipleInheritance() {
    MyJsFunctionMultipleInheritance jsFunctionMultipleInheritance =
        new MyJsFunctionMultipleInheritance();
    assertEquals(21, jsFunctionMultipleInheritance.foo(10));
    assertEquals(21, callAsFunction(jsFunctionMultipleInheritance, 10));
    assertEquals(21, callAsCallBackFunction(jsFunctionMultipleInheritance, 10));
  }

  public void testJsFunctionIdentity() {
    MyJsFunctionIdentityInterface id = new MyJsFunctionIdentityInterface() {
      @Override
      public Object identity() {
        return this;
      }
    };
    assertEquals(id, callAsFunction(id));
  }

  public void testJsFunctionInteraction() {
    MyJsFunctionInterfaceImpl jsFunctionInterfaceImpl = new MyJsFunctionInterfaceImpl();
    // public JsType method works fine at Java side.
    assertEquals(5, jsFunctionInterfaceImpl.bar());
    // public JsType method works fine at JS side.
    assertEquals(5, callIntFunction(jsFunctionInterfaceImpl, "bar"));

    // SAM works fine at Java side.
    assertEquals(11, jsFunctionInterfaceImpl.foo(10));
    // SAM can be called as a function at JS side.
    assertEquals(11, callAsFunction(jsFunctionInterfaceImpl, 10));
    assertEquals(11, callAsCallBackFunction(jsFunctionInterfaceImpl, 10));

    // public JsType fields works fine both at Java and JS side.
    assertEquals(10, jsFunctionInterfaceImpl.publicField);
    assertEquals(10, getField(jsFunctionInterfaceImpl, "publicField"));
    setField(jsFunctionInterfaceImpl, "publicField", 100);
    assertEquals(100, jsFunctionInterfaceImpl.publicField);
    assertEquals(100, getField(jsFunctionInterfaceImpl, "publicField"));
  }

  public void testJsFunctionAccess() {
    MyJsFunctionInterface intf = new MyJsFunctionInterface() {
      public int publicField;
      @Override
      public int foo(int a) {
        return a;
      }
    };
    JsTypeTest.assertJsTypeDoesntHaveFields(intf, "foo");
    JsTypeTest.assertJsTypeDoesntHaveFields(intf, "publicField");
  }

  private static native Object callAsFunction(Object obj) /*-{
    return obj();
  }-*/;

  private static native int callAsFunction(Object obj, int arg) /*-{
    return obj(arg);
  }-*/;

  private static native int callAsCallBackFunction(Object obj, int arg) /*-{
    var onCall = function(f, arg) { return f(arg); };
    return onCall(obj, arg);
  }-*/;

  private static native void setField(Object object, String fieldName, int value) /*-{
    object[fieldName] = value;
  }-*/;

  private static native int getField(Object object, String fieldName) /*-{
    return object[fieldName];
  }-*/;

  private static native int callIntFunction(Object object, String functionName) /*-{
    return object[functionName]();
  }-*/;
}
