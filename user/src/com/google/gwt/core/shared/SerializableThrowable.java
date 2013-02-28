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
package com.google.gwt.core.shared;

import com.google.gwt.core.client.JavaScriptException;

/**
 * A {@link Throwable} class capable of transferring any underlying Throwable with its causes and
 * stack traces. It overrides {@code #toString} to mimic original {@link Throwable#toString()} so
 * that {@link #printStackTrace} will work like it is coming from the original exception.
 * <p>
 * This class is especially useful for logging and testing as the emulated Throwable class does not
 * serialize recursively and does not serialize the stack trace. This class, as an alternative, can
 * be used transfer the Throwable without losing any of these data, even if the underlying Throwable
 * is not serializable.
 * <p>
 * Please note that, to get more useful stack traces from client side, this class needs to be used
 * in conjunction with {@link com.google.gwt.core.server.StackTraceDeobfuscator}.
 */
//TODO(goktug): temporarily not final as extended by old SerializableThrowable
public /*final*/ class SerializableThrowable extends Throwable {

  /**
   * Create a new ThrowableSerializableProxy from a Throwable.
   */
  public static SerializableThrowable toSerializable(Throwable t) {
    if (t instanceof SerializableThrowable) {
      return (SerializableThrowable) t;
    } else if (t != null) {
      return createSerializable(t);
    } else {
      return null;
    }
  }

  private static SerializableThrowable createSerializable(Throwable t) {
    SerializableThrowable throwable = new SerializableThrowable();
    throwable.setMessage(t.getMessage());
    throwable.setStackTrace(t.getStackTrace());
    throwable.initCause(t.getCause());
    if (isClassMetadataAvailable()) {
      throwable.setOriginalClassName(t.getClass().getName());
    } else {
      throwable.setOriginalClassName(resolveClassName(t));
      throwable.exactTypeUnknown = true;
    }
    return throwable;
  }

  private String typeName;
  private boolean exactTypeUnknown;
  private String message;
  private StackTraceElement[] stackTrace;
  private SerializableThrowable cause;

  public void setOriginalClassName(String typeName) {
    this.typeName = typeName;
  }

  public String getOriginalClassName() {
    return typeName;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public void setExactTypeUnknown(boolean exactTypeUnknown) {
    this.exactTypeUnknown = exactTypeUnknown;
  }

  public boolean isExactTypeUnknown() {
    return exactTypeUnknown;
  }

  @Override
  public String getMessage() {
    return message;
  }

  @Override
  public Throwable initCause(Throwable cause) {
    if (this.cause != null) {
      throw new IllegalStateException("Can't overwrite cause");
    }
    if (cause == this) {
      throw new IllegalArgumentException("Self-causation not permitted");
    }
    this.cause = toSerializable(cause);
    return this;
  }

  @Override
  public SerializableThrowable getCause() {
    return cause;
  }

  @Override
  public void setStackTrace(StackTraceElement[] stackTace) {
    this.stackTrace = stackTace;
    this.stackTraceInSync = false;
  }

  @Override
  public StackTraceElement[] getStackTrace() {
    return stackTrace;
  }

  @Override
  public Throwable fillInStackTrace() {
    // This is a no-op as we don't need stack traces to be auto filled.
    return this;
  }

  @Override
  public String toString() {
    // We cannot guarantee that the stack trace in sync by setStackTrace due RPC
    // serialization/deserialization.
    // However, we care about super stack trace only for printing purposes, so it is safe to sync it
    // here as toString as is always the first step in printing.
    ensureSuperStackTrace();

    String msg = getMessage();
    return msg == null ? getTypeInfo() : (getTypeInfo() + ": " + msg);
  }

  private transient volatile boolean stackTraceInSync = false;

  private void ensureSuperStackTrace() {
    if (!stackTraceInSync) {
      super.setStackTrace(stackTrace);
      for (SerializableThrowable t = cause; t != null; t = t.cause) {
        cause.ensureSuperStackTrace();
      }
      this.stackTraceInSync = true;
    }
  }

  private String getTypeInfo() {
    return !exactTypeUnknown ? typeName : (typeName + "(EXACT_TYPE_UNKNOWN)");
  }

  // TODO(goktug): Replace when availability of class metadata can be checked in compile time so
  // that resolveClassName will be compiled out.
  private static boolean isClassMetadataAvailable() {
    return SerializableThrowable.class.getName().endsWith(".SerializableThrowable");
  }

  /**
   * Returns best effort class name by checking against some common exception types.
   */
  private static String resolveClassName(Throwable t) {
    try {
      throw t;
    } catch (NullPointerException e) {
      return "java.lang.NullPointerException";
    } catch (JavaScriptException e) {
      return "com.google.gwt.core.client.JavaScriptException";
    } catch (RuntimeException e) {
      return "java.lang.RuntimeException";
    } catch (Exception e) {
      return "java.lang.Exception";
    } catch (Error e) {
      return "java.lang.Error";
    } catch (Throwable e) {
      return "java.lang.Throwable";
    }
  }
}
