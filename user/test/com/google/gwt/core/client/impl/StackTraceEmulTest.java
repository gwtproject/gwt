/*
 * Copyright 2009 Google Inc.
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

import static com.google.gwt.core.client.impl.StackTraceExamples.JAVA;
import static com.google.gwt.core.client.impl.StackTraceExamples.TYPE_ERROR;

import junit.framework.AssertionFailedError;

/**
 * Tests {@link StackTraceCreator} in the emulated mode.
 */
public class StackTraceEmulTest extends StackTraceNativeTest {

  @Override
  public String getModuleName() {
    return "com.google.gwt.core.Core";
  }

  @Override
  public void testCollectorType() {
    assertTrue(StackTraceCreator.collector instanceof StackTraceCreator.CollectorEmulated);
  }

  /**
   * Verifies throw/try/catch doesn't poison the emulated stack frames.
   */
  public void testJseLineNumbers() {
    Exception exception = StackTraceExamples.getLiveException(TYPE_ERROR);
    String[] methodNames = getTraceJse(TYPE_ERROR);

    String fileName = "StackTraceExamples.java";
    StackTraceElement[] expectedTrace = new StackTraceElement[] {
        createSTE(methodNames[0], fileName, 80),
        createSTE(methodNames[1], fileName, 76),
        createSTE(methodNames[2], fileName, 92),
        createSTE(methodNames[3], fileName, 58),
        createSTE(methodNames[4], fileName, 49),
        createSTE(methodNames[5], fileName, 40)
    };

    int traceOffset = getTraceOffset(exception.getStackTrace(),
        expectedTrace[0].getMethodName());
    assertTrace(expectedTrace, exception, traceOffset);
  }

  /**
   * Verifies throw/try/catch doesn't poison the emulated stack frames.
   */
  public void testViaSample() {
    StackTraceElement[] start = sample();

    Exception e = StackTraceExamples.getLiveException(JAVA);
    assertTrue(e.getStackTrace().length > 0);

    StackTraceElement[] end = sample();
    assertTraceMethodNames(start, end);
  }

  /**
   * Verifies throw/try/catch with JSE doesn't poison the emulated stack frames.
   */
  public void testJseViaSample() {
    StackTraceElement[] start = sample();

    Exception e = StackTraceExamples.getLiveException(TYPE_ERROR);
    assertTrue(e.getStackTrace().length > 0);

    StackTraceElement[] end = sample();
    assertTraceMethodNames(start, end);
  }

  private static void assertTraceMethodNames(StackTraceElement[] start, StackTraceElement[] end) {
    assertEquals("length", start.length, end.length);
    for (int i = 0, j = start.length; i < j; i++) {
      assertEquals("frame " + i, start[i].getMethodName(), end[i].getMethodName());
    }
  }

  private void assertTrace(StackTraceElement[] expected, Exception t, int offset) {
    StackTraceElement[] trace = t.getStackTrace();

    for (int i = 0; i < expected.length; i++) {
      StackTraceElement actualElement = trace[i + offset];
      if (actualElement.equals(expected[i])) {
        continue;
      }
      AssertionFailedError e = new AssertionFailedError("Incorrect frame at " + i + " - "
          + " Expected: " + expected[i] + " Actual: " + actualElement);
      e.initCause(t);
      throw e;
    }
  }

  private static StackTraceElement[] sample() {
    return new Throwable().getStackTrace();
  }

  private static StackTraceElement createSTE(String methodName, String fileName, int lineNumber) {
    return new StackTraceElement("Unknown", methodName, fileName, lineNumber);
  }
}
