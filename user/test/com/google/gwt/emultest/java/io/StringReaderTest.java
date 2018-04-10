/*
 * Copyright 2018 Google Inc.
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
package com.google.gwt.emultest.java.io;

import com.google.gwt.junit.client.GWTTestCase;

import java.io.StringReader;
import java.util.Arrays;

/**
 * Unit test for the {@link java.io.StringReader} emulated class.
 */
public class StringReaderTest extends GWTTestCase {
  @Override
  public String getModuleName() {
    return "com.google.gwt.emultest.EmulSuite";
  }

  public void testEmptyString() throws Exception {
    StringReader reader = new StringReader("");
    assertEquals(-1, reader.read());
    char[] buf = new char[5];
    assertEquals(-1, reader.read(buf));
    assertEquals(-1, reader.read(buf, 2, 0));
  }

  public void testString() throws Exception {
    StringReader reader = new StringReader("The q\u00DCuick brown fox jumped over the lazy dog");
    assertEquals('T', reader.read());

    char[] buf = new char[10];
    assertEquals(6, reader.read(buf, 3, 6));
    assertEquals("\u0000\u0000\u0000he q\u00DCi\u0000", String.valueOf(buf));

    assertEquals('k', reader.read());
    assertEquals(' ', reader.read());

    assertEquals(2, reader.read(buf, 0, 2));
    // First 2 characters now filled.
    assertEquals("br\u0000he q\u00DCi\u0000", String.valueOf(buf));

    assertEquals(10, reader.read(buf));
    assertEquals("own fox ju", String.valueOf(buf));

    char[] four = new char[4];
    assertEquals(4, reader.read(four));
    assertEquals("mped", String.valueOf(four));

    char[] emptyBuf = new char[0];
    assertEquals(0, reader.read(emptyBuf));

    assertEquals(10, reader.read(buf));
    assertEquals(" over the ", String.valueOf(buf));

    char[] eight = new char[8];
    assertEquals(7, reader.read(eight));
    assertEquals("lazy dog\u0000", String.valueOf(eight));

    assertEquals(-1, reader.read());
    assertEquals(-1, reader.read(eight));
    assertEquals(-1, reader.read(eight, 0, 0));
    assertEquals(-1, reader.read(eight, 0, 1));
  }

  public void testSkip_stringOverBufferSize() throws Exception {
    StringReader reader = new StringReader(repeat('x', 1025));

    assertEquals(1025, reader.skip(2000));

    assertEquals(-1, reader.read());
  }

  public void testSkip_longString() throws Exception {
    int n = 10000;
    char[] arr = new char[n];
    for (char i = 0; i < n; i++) {
      arr[i] = i;
    }

    StringReader reader = new StringReader(String.valueOf(arr));
    assertEquals(5000, reader.skip(5000));
    assertEquals(5000, reader.read());
    assertEquals(5001, reader.read());

    assertEquals(4000, reader.skip(4000));
    assertEquals(9002, reader.read());

    assertEquals(997, reader.skip(4000));
    assertEquals(-1, reader.read());
  }

  private static String repeat(char c, int count) {
    char[] arr = new char[count];
    Arrays.fill(arr, c);
    return String.valueOf(arr);
  }
}
