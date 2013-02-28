/*
 * Copyright 2010 Google Inc.
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

package com.google.gwt.core.client.impl;

/**
 * @deprecated use {@link com.google.gwt.core.shared.SerializableThrowable} instead.
 */
@Deprecated
public class SerializableThrowable extends com.google.gwt.core.shared.SerializableThrowable {

  /**
   * @deprecated use com.google.gwt.core.client.SerializableThrowable instead.
   */
  @Deprecated
  public static class ThrowableWithClassName
      extends com.google.gwt.core.shared.SerializableThrowable {

    public ThrowableWithClassName(String message, Throwable cause, String typeName) {
      this(message, typeName);
      initCause(cause);
    }

    public ThrowableWithClassName(String message, String typeName) {
      setMessage(message);
      setOriginalClassName(typeName);
    }

    public String getExceptionClass() {
      return getOriginalClassName();
    }
  }

  protected SerializableThrowable() {
    // for serialization
  }

  public SerializableThrowable(Throwable t) {
    setMessage(t.getMessage());
    setStackTrace(t.getStackTrace());
    initCause(t.getCause());
    setOriginalClassName(t.getClass().getName());
  }

  /**
   * Create a Throwable from this SerializableThrowable.
   */
  public Throwable getThrowable() {
    return this;
  }
}
