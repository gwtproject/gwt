/*
 * Copyright 2016 Google Inc.
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

package com.google.gwt.emultest.java8.lang;

import com.google.gwt.junit.client.GWTTestCase;

/**
 * Tests for the JRE Integer type.
 */
public class IntegerTest extends GWTTestCase {

  private static final long[] UNSIGNED_INTS = {
      0L,
      1L,
      2L,
      3L,
      0x12345678L,
      0x5a4316b8L,
      0x6cf78a4bL,
      0xff1a618bL,
      0xfffffffdL,
      0xfffffffeL,
      0xffffffffL
  };

  @Override
  public String getModuleName() {
    return "com.google.gwt.emultest.EmulSuite";
  }

  public void testCompareUnsigned() {
    // max value
    assertTrue(Integer.compareUnsigned(0, 0xffffffff) < 0);
    assertTrue(Integer.compareUnsigned(0xffffffff, 0) > 0);

    // both with high bit set
    assertTrue(Integer.compareUnsigned(0xff1a618b, 0xffffffff) < 0);
    assertTrue(Integer.compareUnsigned(0xffffffff, 0xff1a618b) > 0);

    // one with high bit set
    assertTrue(Integer.compareUnsigned(0x5a4316b8, 0xff1a618b) < 0);
    assertTrue(Integer.compareUnsigned(0xff1a618b, 0x5a4316b8) > 0);

    // neither with high bit set
    assertTrue(Integer.compareUnsigned(0x5a4316b8, 0x6cf78a4b) < 0);
    assertTrue(Integer.compareUnsigned(0x6cf78a4b, 0x5a4316b8) > 0);

    // same value
    assertTrue(Integer.compareUnsigned(0xff1a618b, 0xff1a618b) == 0);
  }

  public void testDivideUnsigned() {
    for (long a : UNSIGNED_INTS) {
      for (long b : UNSIGNED_INTS) {
        try {
          assertEquals((int) (a / b), Integer.divideUnsigned((int) a, (int) b));
          assertFalse(b == 0);
        } catch (ArithmeticException e) {
          assertEquals(0, b);
        }
      }
    }
  }

  public void testRemainderUnsigned() {
    for (long a : UNSIGNED_INTS) {
      for (long b : UNSIGNED_INTS) {
        try {
          assertEquals((int) (a % b), Integer.remainderUnsigned((int) a, (int) b));
          assertFalse(b == 0);
        } catch (ArithmeticException e) {
          assertEquals(0, b);
        }
      }
    }
  }

  public void testToUnsignedLong() {
    for (long a : UNSIGNED_INTS) {
      assertEquals(a, Integer.toUnsignedLong((int) a));
    }
  }
}

