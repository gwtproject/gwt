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
package java.lang;

import static javaemul.internal.InternalPreconditions.checkCriticalArgument;
import static javaemul.internal.InternalPreconditions.checkNotNull;
import static javaemul.internal.InternalPreconditions.checkState;

import java.io.PrintStream;
import java.io.Serializable;

import javaemul.internal.annotations.DoNotInline;

import jsinterop.annotations.JsMethod;
import jsinterop.annotations.JsNonNull;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsProperty;
import jsinterop.annotations.JsType;

/**
 * See <a
 * href="http://java.sun.com/j2se/1.5.0/docs/api/java/lang/Throwable.html">the
 * official Java API doc</a> for details.
 */
public class Throwable implements Serializable {

  private static final Object UNINITIALIZED = "__noinit__";

  /*
   * NOTE: We cannot use custom field serializers because we need the client and
   * server to use different serialization strategies to deal with this type.
   * The client uses the generated field serializers which can use JSNI. That
   * leaves the server free to special case Throwable so that only the
   * detailMessage field is serialized.
   * See SerializabilityUtil.
   */
  private String detailMessage;
  private transient Throwable cause;
  private transient Throwable[] suppressedExceptions;
  private transient StackTraceElement[] stackTrace = new StackTraceElement[0];
  private transient boolean disableSuppression;
  private transient boolean writableStackTrace = true;

  @JsProperty
  private transient Object backingJsObject = UNINITIALIZED;

  public Throwable() {
    fillInStackTrace();
    initializeBackingError();
  }

  public Throwable(String message) {
    this.detailMessage = message;
    fillInStackTrace();
    initializeBackingError();
  }

  public Throwable(String message, Throwable cause) {
    this.cause = cause;
    this.detailMessage = message;
    fillInStackTrace();
    initializeBackingError();
  }

  public Throwable(Throwable cause) {
    this.detailMessage = (cause == null) ? null : cause.toString();
    this.cause = cause;
    fillInStackTrace();
    initializeBackingError();
  }

  /**
   * Constructor that allows subclasses disabling exception suppression and stack traces.
   * Those features should only be disabled in very specific cases.
   */
  protected Throwable(String message, Throwable cause, boolean enableSuppression,
      boolean writableStackTrace) {
    this.cause = cause;
    this.detailMessage = message;
    this.writableStackTrace = writableStackTrace;
    this.disableSuppression = !enableSuppression;
    if (writableStackTrace) {
      fillInStackTrace();
    }
    initializeBackingError();
  }

  Throwable(Object backingJsObject) {
    fillInStackTrace();
    setBackingJsObject(backingJsObject);
    detailMessage = String.valueOf(backingJsObject);
  }

  private void initializeBackingError() {
    setBackingJsObject(fixIE(createError(toString(detailMessage))));

    captureStackTrace();
  }

  // TODO(goktug): set 'name' property to class name and 'message' to detailMessage instead when
  // they are respected by dev tools logging.
  Object createError(String msg) {
    return new NativeError(msg);
  }

  // Called by J2CL transpiler. Do not remove!
  void privateInitError(Object error) { }

  @SuppressWarnings("unusable-by-js")
  private static native Object fixIE(Object e) /*-{
    // In IE -unlike every other browser-, the stack property is not defined until you throw it.
    if (!("stack" in e)) {
      try { throw e; } catch(ignored) {}
    }
    return e;
  }-*/;

  @SuppressWarnings("unusable-by-js")
  private native void captureStackTrace() /*-{
    @com.google.gwt.core.client.impl.StackTraceCreator::captureStackTrace(*)(this);
  }-*/;

  public Object getBackingJsObject() {
    return backingJsObject;
  }

  private void setBackingJsObject(Object backingJsObject) {
    this.backingJsObject = backingJsObject;
    linkBack(backingJsObject);
  }

  private native void linkBack(Object error) /*-{

    if (error instanceof Object) {
      try {
        // This may throw exception in strict mode.
        error.__java$exception = this;

        if (navigator.userAgent.toLowerCase().indexOf("msie") != -1 && $doc.documentMode < 9) {
          return;
        }

        var throwable = this;
        Object.defineProperties(error, {
          cause: {
            get: function() {
              var cause = throwable.@Throwable::getCause()();
              return cause && cause.@Throwable::getBackingJsObject()();
            }
          },
          suppressed: {
            get: function() {
              return throwable.@Throwable::getBackingSuppressed()();
            }
          }
        });
      } catch (ignored) {}
    }
  }-*/;

  private Object[] getBackingSuppressed() {
    // local variable as the whole method ends up being inlined three times
    Throwable[] suppressed = getSuppressed();
    Object[] result = new Object[suppressed.length];
    for (int i = 0; i < suppressed.length; i++) {
      result[i] = suppressed[i].backingJsObject;
    }
    return result;
  }

  /**
   * Call to add an exception that was suppressed. Used by try-with-resources.
   */
  public final void addSuppressed(Throwable exception) {
    checkNotNull(exception, "Cannot suppress a null exception.");
    checkCriticalArgument(exception != this, "Exception can not suppress itself.");

    if (disableSuppression) {
      return;
    }

    if (suppressedExceptions == null) {
      suppressedExceptions = new Throwable[] { exception };
    } else {
      // TRICK: This is not correct Java (would give an OOBE, but it works in JS and
      // this code will only be executed in JS.
      suppressedExceptions[suppressedExceptions.length] = exception;
    }
  }

  /**
   * Populates the stack trace information for the Throwable.
   *
   * @return this
   */
  @DoNotInline
  public Throwable fillInStackTrace() {
    if (writableStackTrace) {
      // If this is the first run, let constructor initialize it.
      // (We need to initialize the backingJsObject from constructor as our own implementation of
      // fillInStackTrace is not guaranteed to be executed.)
      if (backingJsObject != UNINITIALIZED) {
        initializeBackingError();
      }

      // Invalidate the cached trace
      this.stackTrace = null;
    }
    return this;
  }

  public Throwable getCause() {
    return cause;
  }

  public String getLocalizedMessage() {
    return getMessage();
  }

  public String getMessage() {
    return detailMessage;
  }

  /**
   * Returns the stack trace for the Throwable if it is available.
   * <p> Availability of stack traces in script mode depends on module properties and browser.
   * See: https://code.google.com/p/google-web-toolkit/wiki/WebModeExceptions#Emulated_Stack_Data
   */
  public StackTraceElement[] getStackTrace() {
    if (stackTrace == null) {
      stackTrace = constructJavaStackTrace();
    }
    return stackTrace;
  }

  @SuppressWarnings("unusable-by-js")
  private native StackTraceElement[] constructJavaStackTrace() /*-{
    return @com.google.gwt.core.client.impl.StackTraceCreator::constructJavaStackTrace(*)(this);
  }-*/;

  /**
   * Returns the array of Exception that this one suppressedExceptions.
   */
  public final Throwable[] getSuppressed() {
    if (suppressedExceptions == null) {
      suppressedExceptions = new Throwable[0];
    }

    return suppressedExceptions;
  }

  public Throwable initCause(Throwable cause) {
    checkState(this.cause == null, "Can't overwrite cause");
    checkCriticalArgument(cause != this, "Self-causation not permitted");
    this.cause = cause;
    return this;
  }

  public void printStackTrace() {
    printStackTrace(System.err);
  }

  public void printStackTrace(PrintStream out) {
    printStackTraceImpl(out, "", "");
  }

  private void printStackTraceImpl(PrintStream out, String prefix, String ident) {
    out.println(ident + prefix + this);
    printStackTraceItems(out, ident);

    for (Throwable t : getSuppressed()) {
      t.printStackTraceImpl(out, "Suppressed: ", "\t" + ident);
    }

    Throwable theCause = getCause();
    if (theCause != null) {
      theCause.printStackTraceImpl(out, "Caused by: ", ident);
    }
  }

  private void printStackTraceItems(PrintStream out, String ident) {
    for (StackTraceElement element : getStackTrace()) {
      out.println(ident + "\tat " + element);
    }
  }

  public void setStackTrace(StackTraceElement[] stackTrace) {
    int length = stackTrace.length;
    StackTraceElement[] copy = new StackTraceElement[length];
    for (int i = 0; i < length; ++i) {
      copy[i] = checkNotNull(stackTrace[i]);
    }
    this.stackTrace = copy;
  }

  @Override
  public String toString() {
    return toString(getLocalizedMessage());
  }

  // A private method to avoid polymorphic calls from constructor.
  private String toString(String message) {
    String className = getClass().getName();
    return message == null ? className : className + ": " + message;
  }

  @JsMethod
  public static @JsNonNull Throwable of(Object e) {
    // If the JS error is already mapped to a Java Exception, use it.
    if (e != null) {
      Throwable throwable = ((HasJavaThrowable) e).getJavaThrowable();
      if (throwable != null) {
        return throwable;
      }
    }

    // If the JS error is being seen for the first time, map it best corresponding Java exception.
    return e instanceof NativeTypeError ? new NullPointerException(e) : new JsException(e);
  }

  @JsType(isNative = true, name = "Error", namespace = "<window>")
  private static class NativeError {
    NativeError(String msg) { }
  }

  @JsType(isNative = true, name = "TypeError", namespace = "<window>")
  private static class NativeTypeError { }

  @SuppressWarnings("unusable-by-js")
  @JsType(isNative = true, name = "?", namespace = JsPackage.GLOBAL)
  private interface HasJavaThrowable {
    @JsProperty(name = "__java$exception")
    Throwable getJavaThrowable();
  }
}
