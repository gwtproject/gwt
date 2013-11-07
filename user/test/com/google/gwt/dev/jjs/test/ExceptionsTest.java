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
package com.google.gwt.dev.jjs.test;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.junit.client.GWTTestCase;

/**
 * Tests exception functionality.
 */
public class ExceptionsTest extends GWTTestCase {

  private static class A {
    int field;
    String method() {
      return "in A::method()";
    }
  }

  @Override
  public String getModuleName() {
    return "com.google.gwt.dev.jjs.EnumsSuite";
  }

  public void testNullPointerExceptions() {
    A o = null;
    // To prevent the compiler from prunning.
    if (always2(0) == 0) {
      o = new A();
      o.field = 0;
    }

    try {
      o.toString();
      fail("Should have thrown a NullPointerException (1)");
    } catch (NullPointerException e) {
    }

    try {
      o.method();
      if (!GWT.isScript()) {
        // This method is statified in optimized mode.
        // TODO(rluble): In the future the compiler should introduce a check when statifying or
        // devirtualizing;
        fail("Should have thrown a NullPointerException (2) ");
      }
    } catch (NullPointerException e) {
    }

    try {
      o.field = 1;
      fail("Should have thrown a NullPointerException (3)");
    } catch (NullPointerException e) {
    }

    if (always2(0) == 0) {
      new Integer(o.field).toString();
    }
  }

  public void testNullPointerExceptionsOnArrays() {
    // To prevent the compiler from prunning.
    A[] array = null;

    if (always2(0) == 0) {
      array = new A[10];
      array[1] = new A();
    }

    A a =  null;
    try {
      array.getClass().toString();
      fail("Should have thrown a NullPointerException (1)");
    } catch (NullPointerException e) {
    }

    try {
      // The assignment might be optimized away by copy propagation.
      a = array[1];
      fail("Should have thrown a NullPointerException (2) ");
    } catch (NullPointerException e) {
    }

    try {
      array[0].toString();
      fail("Should have thrown a NullPointerException (3)");
    } catch (NullPointerException e) {
    }

    try {
      array[0].method();
      if (!GWT.isScript()) {
        // This method is statified in optimized mode.
        // TODO(rluble): In the future the compiler should introduce a check when statifying or
        // devirtualizing;
        fail("Should have thrown a NullPointerException (4)");
      }
    } catch (NullPointerException e) {
    }

    if (always2(0) == 0) {
      a.toString();
    }
  }

  public void testNullPointerExceptionsOnStrings() {
    String string = null;

    // To prevent the compiler from prunning.
    if (always2(0) == 0) {
      string = "";
    }

    String a = null;
    String b = null;
    Character c = null;
    try {
      string.charAt(2);
      fail("Should have thrown a NullPointerException (1)");
    } catch (NullPointerException e) {
    }

    try {
      // This method is statified in optimized mode.

      a = string.concat("s");

      // Concat is translated directly to JavaScript as +.
      if (GWT.isScript()) {
        assertEquals("nulls",a);
      } else {
        // TODO(rluble): In the future the compiler should introduce a check when statifying or
        // devirtualizing;
        fail("Should have thrown a NullPointerException (2) ");
      }
    } catch (NullPointerException e) {
    }

    try {
      // This method is statified in optimized mode.

      c = string.charAt(0);

      fail("Should have thrown a NullPointerException (2) ");
    } catch (NullPointerException e) {
    }

    try {
      // This method is statified in optimized mode.
      b = string.concat(null);
      if (!GWT.isScript()) {
        fail("Should have thrown a NullPointerException (3) ");
      }
      assertEquals("string" + 0,  "string" + b);
    } catch (NullPointerException e) {
    }

    if (always2(0) == 0) {
      a.getClass();
      b.getClass();
    }
  }

  // A small recursive function to trick the compiler; needed to avoid optimization which
  // happens even on -drafCompile.
  // TODO(rluble): Once draftCompile stops optimizing we can remove this an mark the test so
  // that it only runs on draf mode.
  private int always2(int val) {
    if (val < 2) {
      return always2(val + 1);
    } else {
      return val;
    }
  }
}
