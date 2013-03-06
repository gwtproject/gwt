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
 * that {@link #printStackTrace} will work like as it is coming from the original exception.
 * <p>
 * This class is especially useful for logging and testing as the emulated Throwable class does not
 * serialize recursively and does not serialize the stack trace. This class, as an alternative, can
 * be used transfer the Throwable without losing any of these data, even if the underlying Throwable
 * is not serializable.
 * <p>
 * Please note that, to get more useful stack traces from client side, this class needs to be used
 * in conjunction with {@link com.google.gwt.core.server.StackTraceDeobfuscator}.
 */
public final class SerializableThrowable extends Throwable {

  /**
   * Create a new {@link SerializableThrowable} from provided throwable instance. This will return
   * the passed object itself if it is already a SerializableThrowable.
   */
  public static SerializableThrowable fromThrowable(Throwable throwable) {
    if (throwable instanceof SerializableThrowable) {
      return (SerializableThrowable) throwable;
    } else if (throwable != null) {
      return createSerializable(throwable);
    } else {
      return null;
    }
  }

  private String typeName;
  private boolean exactTypeKnown;
  private StackTraceElement[] dummyFieldToIncludeTheTypeInSerialization;

  /**
   * Constructs a new SerializableThrowable with no detail message.
   */
  public SerializableThrowable() { /* EMPTY */}

  /**
   * Constructs a new SerializableThrowable with the specified detail message.
   */
  public SerializableThrowable(String message) {
    super(message);
  }

  @Override
  public Throwable fillInStackTrace() {
    // This is a no-op for optimization as we don't need stack traces to be auto-filled.
    return this;
  }

  /**
   * Sets the designated Throwable's type name.
   *
   * @param typeName the class name of the underlying designated throwable.
   * @param isExactType {@code false} if provided type name is not the exact type.
   *@see #isExactDesignatedTypeKnown()
   */
  public void setDesignatedType(String typeName, boolean isExactType) {
    this.typeName = typeName;
    this.exactTypeKnown = isExactType;
  }

  /**
   * Returns the designated throwable's type name.
   *
   * @see #isExactDesignatedTypeKnown()
   */
  public String getDesignatedType() {
    return typeName;
  }

  /**
   * Return {@code true} if provided type name is the exact type of the throwable that is designed
   * by this instance. This can return {@code false} if the class metadata is not available in the
   * runtime. In that case {@link #getDesignatedType()} will return the type resolved by best-effort
   * and may not be the exact type; instead it can be one of the ancestors of the real type that
   * this instance designates.
   */
  public boolean isExactDesignatedTypeKnown() {
    return exactTypeKnown;
  }

  /**
   * Initializes the cause of this throwable.
   * <p>
   * This method will convert the cause to {@link SerializableThrowable} if it is not already.
   */
  @Override
  public Throwable initCause(Throwable cause) {
    return super.initCause(fromThrowable(cause));
  }

  @Override
  public String toString() {
    String type = exactTypeKnown ? typeName : (typeName + "(EXACT_TYPE_UNKNOWN)");
    String msg = getMessage();
    return msg == null ? type : (type + ": " + msg);
  }

  private static SerializableThrowable createSerializable(Throwable t) {
    SerializableThrowable throwable = new SerializableThrowable(t.getMessage());
    throwable.setStackTrace(t.getStackTrace());
    throwable.initCause(t.getCause());
    if (isClassMetadataAvailable()) {
      throwable.setDesignatedType(t.getClass().getName(), true);
    } else {
      String resolvedName = resolveClassName(t);
      throwable.setDesignatedType(resolvedName, resolvedName.equals(t.getClass().getName()));
    }
    return throwable;
  }

  // TODO(goktug): Replace when availability of class metadata can be checked in compile-time so
  // that #resolveClassName will be compiled out.
  private static boolean isClassMetadataAvailable() {
    return !GWT.isScript()
        || SerializableThrowable.class.getName().endsWith(".SerializableThrowable");
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
