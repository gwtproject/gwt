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
package com.google.gwt.junit;

import com.google.gwt.core.shared.SerializableThrowable;
import com.google.gwt.junit.client.ExceptionAsserter;
import com.google.gwt.junit.client.ExpectedFailure;

import junit.framework.Assert;

/**
 * A default {@link ExceptionAsserter} that checks exception type and message.
 */
class DefaultExceptionAsserter extends Assert implements ExceptionAsserter {

  @Override
  public void assertException(Throwable throwable, ExpectedFailure annotation) {
    assertAssignable(annotation.withType(), getExceptionClass(throwable));
    assertTrue(getExceptionMessage(throwable).contains(annotation.withMessage()));
  }

  private static void assertAssignable(Class<?> expected, Class<?> exceptionClass) {
    if (!expected.isAssignableFrom(exceptionClass)) {
      fail("expected assignable to: " + expected + " found: " + exceptionClass);
    }
  }

  private Class<?> getExceptionClass(Throwable t) {
    if (t instanceof SerializableThrowable) {
      try {
        SerializableThrowable throwableWithClassName = (SerializableThrowable) t;
        return Class.forName(throwableWithClassName.getDesignatedType());
      } catch (Exception e) {
        // Nothing to do here, just fallback to #getClass
      }
    }
    return t.getClass();
  }

  private String getExceptionMessage(Throwable t) {
    return t.getMessage() == null ? "" : t.getMessage();
  }
}