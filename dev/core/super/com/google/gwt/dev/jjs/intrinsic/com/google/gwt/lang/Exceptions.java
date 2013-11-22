/*
 * Copyright 2008 Google Inc.
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
package com.google.gwt.lang;

import com.google.gwt.core.client.JavaScriptException;

/**
 * This is a magic class the compiler uses to throw and check exceptions.
 */
final class Exceptions {

  static Object wrap(Object e) {
    if (e instanceof Throwable) {
      return e;
    }
    return e == null ? new JavaScriptException(null) : getCachableJavaScriptException(e);
  }

  static Object unwrap(Object e) {
    if (e instanceof JavaScriptException) {
      JavaScriptException jse = ((JavaScriptException) e);
      if (jse.isThrownSet()) {
        return jse.getThrown();
      }
    }
    return e;
  }

  private static native JavaScriptException getCachableJavaScriptException(Object e)/*-{
    var jse = e.__gwt$exception;
    if (!jse) {
      jse = @com.google.gwt.core.client.JavaScriptException::new(Ljava/lang/Object;)(e);
      try {
        // See https://code.google.com/p/google-web-toolkit/issues/detail?id=8449
        e.__gwt$exception = jse;
      } catch (e) {
        // The exception is not cachable
      }
    }
    return jse;
  }-*/;

  static AssertionError makeAssertionError() {
    return new AssertionError();
  }

  /*
   * We use nonstandard naming here so it's easy for the compiler to map to
   * method names based on primitive type name.
   */
  // CHECKSTYLE_OFF
  static AssertionError makeAssertionError_boolean(boolean message) {
    return new AssertionError(message);
  }

  static AssertionError makeAssertionError_char(char message) {
    return new AssertionError(message);
  }

  static AssertionError makeAssertionError_double(double message) {
    return new AssertionError(message);
  }

  static AssertionError makeAssertionError_float(float message) {
    return new AssertionError(message);
  }

  static AssertionError makeAssertionError_int(int message) {
    return new AssertionError(message);
  }

  static AssertionError makeAssertionError_long(long message) {
    return new AssertionError(message);
  }

  static AssertionError makeAssertionError_Object(Object message) {
    return new AssertionError(message);
  }

  /**
   * Use by the try-with-resources construct. Look at
   * {@link com.google.gwt.dev.jjs.impl.GwtAstBuilder.createCloseBlockFor}.
   *
   * @param resource a resource implementing the AutoCloseable interface.
   * @param mainException  an exception being propagated.
   * @return an exception to propagate or {@code null} if none.
   */
  static Throwable safeClose(AutoCloseable resource, Throwable mainException) {
    if (resource == null) {
      return mainException;
    }

    try {
      resource.close();
    } catch (Throwable e) {
      if (mainException == null) {
        return e;
      }
      mainException.addSuppressed(e);
    }
    return mainException;
  }
  // CHECKSTYLE_ON
}
