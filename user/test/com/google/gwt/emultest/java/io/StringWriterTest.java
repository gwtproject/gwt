/*
 * Copyright 2025 Google Inc.
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

import java.io.IOException;
import java.io.StringWriter;

/** Unit test for the {@link java.io.StringWriter} emulated class. */
public class StringWriterTest extends GWTTestCase {

  private StringWriter sw;

  @Override
  public String getModuleName() {
    return "com.google.gwt.emultest.EmulSuite";
  }

  @Override
  protected void gwtSetUp() throws Exception {
    super.gwtSetUp();
    sw = new StringWriter();
  }

  public void testClose() {
    try {
      sw.close();
    } catch (IOException e) {
      fail("IOException closing StringWriter : " + e.getMessage());
    }
  }

  public void testFlush() {
    sw.flush();
    sw.write('c');
    assertEquals("Failed to flush char", "c", sw.toString());
  }

  public void testGetBuffer() {
    sw.write("This is a test string");
    String sb = sw.getBuffer().toString();
    assertEquals("Incorrect buffer returned", "This is a test string", sb);
  }

  public void testToString() {
    sw.write("This is a test string");
    assertEquals("Incorrect string returned", "This is a test string", sw.toString());
  }

  public void testWrite_charArrayWithOffsetAndLength() {
    char[] c = new char[1000];
    "This is a test string".getChars(0, 21, c, 0);
    sw.write(c, 0, 21);
    assertEquals("Chars not written properly", "This is a test string", sw.toString());
  }

  public void testWrite_charArray_negativeLength_throwsException() {
    StringWriter obj = new StringWriter();
    try {
      obj.write(new char[0], 0, -1);
      fail("should have thrown IndexOutOfBoundsException");
    } catch (IndexOutOfBoundsException expected) {
    }
  }

  public void testWrite_charArray_negativeOffset_throwsException() {
    StringWriter obj = new StringWriter();
    try {
      obj.write(new char[0], -1, 0);
      fail("should have thrown IndexOutOfBoundsException");
    } catch (IndexOutOfBoundsException expected) {
    }
  }

  public void testWrite_charArray_negativeOffsetAndLength_throwsException() {
    StringWriter obj = new StringWriter();
    try {
      obj.write(new char[0], -1, -1);
      fail("should have thrown IndexOutOfBoundsException");
    } catch (IndexOutOfBoundsException expected) {
    }
  }

  public void testWrite_int() {
    sw.write('c');
    assertEquals("Char not written properly", "c", sw.toString());
  }

  public void testWrite_string() {
    sw.write("This is a test string");
    assertEquals("String not written properly", "This is a test string", sw.toString());
  }

  public void testWrite_stringWithOffsetAndLength() {
    sw.write("This is a test string", 2, 2);
    assertEquals("String not written properly", "is", sw.toString());
  }

  public void testAppend_char() throws IOException {
    char testChar = ' ';
    StringWriter stringWriter = new StringWriter(20);
    stringWriter.append(testChar);
    assertEquals(String.valueOf(testChar), stringWriter.toString());
    stringWriter.close();
  }

  public void testAppend_charSequence() throws IOException {
    String testString = "My Test String";
    StringWriter stringWriter = new StringWriter(20);
    stringWriter.append(testString);
    assertEquals(String.valueOf(testString), stringWriter.toString());
    stringWriter.close();
  }

  public void testAppend_charSequenceWithStartAndEnd() throws IOException {
    String testString = "My Test String";
    StringWriter stringWriter = new StringWriter(20);
    stringWriter.append(testString, 1, 3);
    assertEquals(testString.substring(1, 3), stringWriter.toString());
    stringWriter.close();
  }
}
