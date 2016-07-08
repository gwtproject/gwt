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
 * Tests for the JRE Long type.
 */
public class LongTest extends GWTTestCase {

  @Override
  public String getModuleName() {
    return "com.google.gwt.emultest.EmulSuite";
  }

  public void testCompareUnsigned() {
    // max value
    assertTrue(Long.compareUnsigned(0, 0xffffffffffffffffL) < 0);
    assertTrue(Long.compareUnsigned(0xffffffffffffffffL, 0) > 0);

    // both with high bit set
    assertTrue(Long.compareUnsigned(0xff1a618b7f65ea12L, 0xffffffffffffffffL) < 0);
    assertTrue(Long.compareUnsigned(0xffffffffffffffffL, 0xff1a618b7f65ea12L) > 0);

    // one with high bit set
    assertTrue(Long.compareUnsigned(0x5a4316b8c153ac4dL, 0xff1a618b7f65ea12L) < 0);
    assertTrue(Long.compareUnsigned(0xff1a618b7f65ea12L, 0x5a4316b8c153ac4dL) > 0);

    // neither with high bit set
    assertTrue(Long.compareUnsigned(0x5a4316b8c153ac4dL, 0x6cf78a4b139a4e2aL) < 0);
    assertTrue(Long.compareUnsigned(0x6cf78a4b139a4e2aL, 0x5a4316b8c153ac4dL) > 0);

    // same value
    assertTrue(Long.compareUnsigned(0xff1a618b7f65ea12L, 0xff1a618b7f65ea12L) == 0);
  }

  public void testDivideUnsigned() {
    assertEquals(2, Long.divideUnsigned(14, 5));
    assertEquals(0, Long.divideUnsigned(0, 50));
    assertEquals(1, Long.divideUnsigned(0xfffffffffffffffeL, 0xfffffffffffffffdL));
    assertEquals(0, Long.divideUnsigned(0xfffffffffffffffdL, 0xfffffffffffffffeL));
    assertEquals(281479271743488L, Long.divideUnsigned(0xfffffffffffffffeL, 65535));
    assertEquals(0x7fffffffffffffffL, Long.divideUnsigned(0xfffffffffffffffeL, 2));
    assertEquals(3689348814741910322L, Long.divideUnsigned(0xfffffffffffffffeL, 5));
  }

  public void testRemainderUnsigned() {
    assertEquals(4, Long.remainderUnsigned(14, 5));
    assertEquals(0, Long.remainderUnsigned(0, 50));
    assertEquals(1, Long.remainderUnsigned(0xfffffffffffffffeL, 0xfffffffffffffffdL));
    assertEquals(0xfffffffffffffffdL,
        Long.remainderUnsigned(0xfffffffffffffffdL, 0xfffffffffffffffeL));
    assertEquals(65534L, Long.remainderUnsigned(0xfffffffffffffffeL, 65535));
    assertEquals(0, Long.remainderUnsigned(0xfffffffffffffffeL, 2));
    assertEquals(4, Long.remainderUnsigned(0xfffffffffffffffeL, 5));
  }
}

