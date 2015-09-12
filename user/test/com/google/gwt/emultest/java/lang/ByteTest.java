/*
 * Copyright 2007 Google Inc.
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
package com.google.gwt.emultest.java.lang;

import com.google.gwt.junit.client.GWTTestCase;

/**
 * Tests for the JRE Byte type.
 */
public class ByteTest extends GWTTestCase {

  private static final int[] UNSIGNED_INTS = {
      0,
      1,
      2,
      3,
      0x12,
      0x5a,
      0x6c,
      0xfd,
      0xfe,
      0xff
  };

  @Override
  public String getModuleName() {
    return "com.google.gwt.emultest.EmulSuite";
  }

  public void testConstants() {
    assertEquals(-128, Byte.MIN_VALUE);
    assertEquals(127, Byte.MAX_VALUE);
    assertEquals(8, Byte.SIZE);
  }

  public void testCompare() {
    assertTrue("Byte.compare failed for 1 < 2", Byte.compare((byte) 1, (byte) 2) < 0);
    assertTrue("Byte.compare failed for 2 > 1", Byte.compare((byte) 2, (byte) 1) > 0);
    assertEquals(0, Byte.compare((byte) 1, (byte) 1));
  }

  public void testStatics() {
    // test the new 1.5 statics... older stuff "assumed to work"
    assertEquals(0, Byte.valueOf((byte) 0).intValue());
    assertEquals(127, Byte.valueOf((byte) 127).intValue());
    assertEquals(-128, Byte.valueOf((byte) -128).intValue());
    assertEquals(-1, Byte.valueOf((byte) 255).intValue());
  }

  public void testToUnsignedInt() {
    for (int a : UNSIGNED_INTS) {
      assertEquals(a, Byte.toUnsignedInt((byte) a));
    }
  }

  public void testToUnsignedLong() {
    for (int a : UNSIGNED_INTS) {
      assertEquals((long) a, Byte.toUnsignedLong((byte) a));
    }
  }
}
