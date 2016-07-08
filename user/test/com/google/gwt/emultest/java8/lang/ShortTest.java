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
 * Tests for the JRE Short type.
 */
public class ShortTest extends GWTTestCase {

  private static final int[] UNSIGNED_INTS = {
      0,
      1,
      2,
      3,
      0x1234,
      0x5a43,
      0x6cf7,
      0xff1a,
      0xfffd,
      0xfffe,
      0xffff
  };

  @Override
  public String getModuleName() {
    return "com.google.gwt.emultest.EmulSuite";
  }

  public void testToUnsignedInt() {
    for (int a : UNSIGNED_INTS) {
      assertEquals(a, Short.toUnsignedInt((short) a));
    }
  }

  public void testToUnsignedLong() {
    for (int a : UNSIGNED_INTS) {
      assertEquals((long) a, Short.toUnsignedLong((short) a));
    }
  }
}

