/*
 * Copyright 2015 Google Inc.
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
package com.google.gwt.emultest.java.lang;

import com.google.gwt.core.client.JavaScriptException;
import com.google.gwt.junit.DoNotRunWith;
import com.google.gwt.junit.Platform;

/**
 * Unit tests for JsException behavior.
 */
public class JsExceptionTest extends ThrowableTestBase {

  private static native Object makeJSO() /*-{
    return {
      toString : function() {
        return "jso";
      },
      name : "myName",
      message : "myDescription",
    };
  }-*/;

  private static Object makeJavaObject() {
    Object o = new Object() {
      @Override public String toString() {
        return "myLameObject";
      }
    };
    return o;
  }

  private static boolean keepFinallyAlive = false;

  private static Thrower wrapWithFinally(final Thrower thrower) {
    return new Thrower() {
      @Override public void throwException() throws Throwable {
        try {
          thrower.throwException();
        } finally {
          keepFinallyAlive = true;
        }
      }
    };
  }

  @Override
  public String getModuleName() {
    return "com.google.gwt.emultest.EmulSuite";
  }

  public void testCatchJava() {
    Object jso = makeJSO();
    JavaScriptException e = new JavaScriptException(jso);
    assertJsException(jso, catchJava(createThrower(e)));
  }

  public void testCatchNative() {
    Object jso = makeJSO();
    JavaScriptException e = new JavaScriptException(jso);
    assertSame(jso, catchNative(createThrower(e)));
  }

  public void testCatchNativePropagatedFromFinally() {
    Object jso = makeJSO();
    JavaScriptException e = new JavaScriptException(jso);
    assertSame(jso, catchNative(wrapWithFinally(createThrower(e))));
    assertTrue(keepFinallyAlive);
  }

  public void testJavaNativeJavaSandwichCatch() {
    Object jso = makeJSO();
    JavaScriptException e = new JavaScriptException(jso);
    assertJsException(jso, javaNativeJavaSandwich(e));
  }

  public void testCatchThrowNative() {
    Object e;

    e = makeJSO();
    assertJsException(e, catchJava(createNativeThrower(e)));

    e = "testing";
    assertJsException(e, catchJava(createNativeThrower(e)));

    e = null;
    assertJsException(e, catchJava(createNativeThrower(e)));

    e = makeJavaObject();
    assertJsException(e, catchJava(createNativeThrower(e)));

    e = new JavaScriptException(makeJSO());
    assertSame(e, catchJava(createNativeThrower(e)));
  }

  // jsni throw -> java catch -> java throw -> jsni catch
  public void testNativeJavaNativeSandwichCatch() {
    Object e;

    e = makeJSO();
    assertSame(e, nativeJavaNativeSandwich(e));

    e = "testing";
    assertEquals(e, nativeJavaNativeSandwich(e));
    // Devmode will not preserve the same String instance
    assertEquals(e, nativeJavaNativeSandwich(e));

    e = null;
    assertSame(e, nativeJavaNativeSandwich(e));

    e = makeJavaObject();
    assertSame(e, nativeJavaNativeSandwich(e));
  }

  private Object nativeJavaNativeSandwich(Object e) {
    return catchNative(createThrower(catchJava(createNativeThrower(e))));
  }

  @DoNotRunWith(Platform.HtmlUnitUnknown)
  public void testTypeError() {
    try {
      throwTypeError();
      fail();
    } catch (RuntimeException e) {
      assertTypeError(e);
      e = (RuntimeException) javaNativeJavaSandwich(e);
      assertTypeError(e);
    }
  }

  private static native void throwTypeError() /*-{
    "dummy".notExistsWillThrowTypeError();
  }-*/;

  private static void assertTypeError(RuntimeException e) {
    assertTrue(e.toString().startsWith(e.getClass().getName() + ": TypeError"));
  }

  @DoNotRunWith(Platform.HtmlUnitUnknown)
  public void testSvgError() {
    try {
      throwSvgError();
      fail();
    } catch (RuntimeException e) {
      assertTrue(e.toString(), e.toString().contains("NS_ERROR_FAILURE"));
      e = (RuntimeException) javaNativeJavaSandwich(e);
      assertTrue(e.toString().contains("NS_ERROR_FAILURE"));
    }
  }

  private static native void throwSvgError() /*-{
    // In Firefox, this throws an object (not Error):
    $doc.createElementNS("http://www.w3.org/2000/svg", "text").getBBox();

    // For other browsers, make sure an exception is thrown to keep the test simple
    throw new Error("NS_ERROR_FAILURE");
  }-*/;

  private static void assertJsException(Object expected, Throwable exception) {
    assertTrue(exception instanceof RuntimeException);
    assertEquals(expected, getBackingJsObject(exception));
  }

  private static native Object getBackingJsObject(Throwable e) /*-{
    return e.backingJsObject;
  }-*/;
}
