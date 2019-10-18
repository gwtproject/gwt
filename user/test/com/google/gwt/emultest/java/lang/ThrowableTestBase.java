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
package com.google.gwt.emultest.java.lang;

import com.google.gwt.junit.client.GWTTestCase;
import jsinterop.annotations.JsFunction;
import jsinterop.annotations.JsMethod;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsProperty;
import jsinterop.annotations.JsType;

/**
 * Base class that provides utilities for testing subclasses of Throwable.
 */
public abstract class ThrowableTestBase extends GWTTestCase {

  @JsFunction
  interface Thrower {
    void throwException() throws Throwable;
  }

  @JsMethod
  protected static native Throwable createJsException(Object wrapped) /*-{
    return @com.google.gwt.core.client.JavaScriptException::new(Ljava/lang/Object;)(wrapped);
  }-*/;

  @JsMethod
  protected static native void throwNative(Object e) /*-{
    throw e;
  }-*/;

  @JsMethod
  protected static native Object catchNative(Thrower thrower) /*-{
    try {
      thrower();
    } catch (e) {
      return e;
    }
  }-*/;

  protected static Throwable catchJava(Thrower thrower) {
    try {
      thrower.throwException();
    } catch (Throwable e) {
      return e;
    }
    return null;
  }

  protected static Thrower createThrower(final Throwable e) {
    return new Thrower() {
      @Override
      public void throwException() throws Throwable {
        throw e;
      }
    };
  }

  protected static Thrower createNativeThrower(final Object e) {
    return new Thrower() {
      @Override public void throwException() {
        throwNative(e);
      }
    };
  }

  // java throw -> jsni catch -> jsni throw -> java catch
  protected static Throwable javaNativeJavaSandwich(Throwable e) {
    return catchJava(createNativeThrower(catchNative(createThrower(e))));
  }

  protected static BackingJsObject getBackingJsObject(Throwable t) {
    return (BackingJsObject) catchNative(createThrower(t));
  }

  /** A JavaScript object backing a Throwable. */
  @JsType(isNative = true, name = "*", namespace = JsPackage.GLOBAL)
  interface BackingJsObject {
    @JsProperty
    Object getCause();

    @JsProperty
    Object[] getSuppressed();
  }
}
