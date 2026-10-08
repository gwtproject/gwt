/*
 * Copyright 2026 Google Inc.
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
package com.google.gwt.testing.server;

/**
 * Assertion helpers for server-side JUnit 3 tests, where the JUnit 4/5
 * {@code assertThrows} is not available.
 */
public class Assertions {

  /**
   * A block of code that may throw any {@link Throwable}, so it can be passed to
   * {@link #assertThrows} as a lambda.
   */
  public interface Executable {
    void execute() throws Throwable;
  }

  /**
   * Asserts that running {@code executable} throws an exception of
   * {@code expectedType} (or a subtype) and returns it, failing with
   * {@code message} if nothing is thrown or a different type is thrown.
   */
  public static <T extends Throwable> T assertThrows(Class<T> expectedType,
      Executable executable, String message) {
    try {
      executable.execute();
    } catch (Throwable actual) {
      if (expectedType.isInstance(actual)) {
        return expectedType.cast(actual);
      }
      throw new AssertionError(message + " but threw " + actual, actual);
    }
    throw new AssertionError(message);
  }

  private Assertions() {
  }
}
