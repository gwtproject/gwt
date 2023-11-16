/*
 * Copyright 2020 Google Inc.
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
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Unit test for the {@link java.io.Writer} emulated class.
 */
public class WriterTest extends GWTTestCase {

  private TestWriter writer;

  /**
   * Instatiable version of {@link java.io.Writer} for testing purposes.
   */
  private static class TestWriter extends Writer {

    private List<Character> outputChars = new ArrayList<>(1024);

    @Override
    public void close() {
    }

    @Override
    public void flush() {
    }

    @Override
    public void write(char[] cbuf, int off, int len) {
      for (int i = off; i < (off + len); i++) {
        outputChars.add(cbuf[i]);
      }
    }

   /**
    * Converts {@code outputChars} to primitive character array.
    *
    * @return primitive char array
    */
    public char[] toCharArray() {
      if (outputChars.isEmpty()) {
        return null;
      }
      char[] charArray = new char[outputChars.size()];
      for (int i = 0; i < outputChars.size(); i++) {
        charArray[i] = outputChars.get(i);
      }
      return charArray;
    }
  }

  /**
   * Sets module name so that javascript compiler can operate.
   */
  @Override
  public String getModuleName() {
    return "com.google.gwt.emultest.EmulSuite";
  }

  @Override
  protected void gwtSetUp() throws Exception {
    super.gwtSetUp();
    writer = new TestWriter();
  }

  public void testAppendChar() throws IOException {
    Writer w = writer.append('a');
    assertEquals(writer, w);

    assertTrue(Arrays.equals(new char[] { 'a' }, writer.toCharArray()));

    w = writer.append('b');
    assertEquals(writer, w);
    assertTrue(Arrays.equals(new char[] { 'a', 'b' }, writer.toCharArray()));

    w = writer.append('c');
    assertEquals(writer, w);
    assertTrue(Arrays.equals(new char[] { 'a', 'b', 'c' }, writer.toCharArray()));
  }

  public void testAppendNullCharSequence() throws IOException {
    final Writer w = writer.append(null);
    assertEquals(writer, w);
    assertTrue(Arrays.equals("null".toCharArray(), writer.toCharArray()));
  }

  public void testAppendEmptyCharSequence() throws IOException {
    final CharSequence csq = "";
    final Writer w = writer.append(csq);
    assertEquals(writer, w);
    assertNull(writer.toCharArray());
  }

  public void testAppendNonEmptyCharSequence() throws IOException {
    final CharSequence csq = "hola";
    final Writer w = writer.append(csq);
    assertEquals(writer, w);
    assertTrue(Arrays.equals("hola".toCharArray(), writer.toCharArray()));
  }

  public void testAppendSubCharSequenceUsingNegativeStartValue() throws IOException {
    final CharSequence csq = "hola";
    try {
      writer.append(csq, -1, 2);
      fail("should have thrown StringIndexOutOfBoundsException");
    } catch (StringIndexOutOfBoundsException expected) {
    }
  }

  public void testAppendSubCharSequenceUsingNegativeEndValue() throws IOException {
    final CharSequence csq = "hola";
    try {
      writer.append(csq, 0, -1);
      fail("should have thrown StringIndexOutOfBoundsException");
    } catch (StringIndexOutOfBoundsException expected) {
    }
  }

  public void testAppendSubCharSequenceStartIsGreaterThanEnd() throws IOException {
    final CharSequence csq = "hola";
    try {
      writer.append(csq, 2, 1);
      fail("should have thrown StringIndexOutOfBoundsException");
    } catch (StringIndexOutOfBoundsException expected) {
    }
  }

  public void testAppendNullSubCharSequence() throws IOException {
    final Writer w = writer.append(null, 1, "null".length() - 1);
    assertEquals(writer, w);
    assertTrue(Arrays.equals("ul".toCharArray(), writer.toCharArray()));
  }

  public void testAppendEmptySubCharSequence() throws IOException {
    final CharSequence csq = "";
    final Writer w = writer.append(csq, 0, 0);
    assertEquals(writer, w);
    assertNull(writer.toCharArray());
  }

  public void testAppendNonEmptySubCharSequence() throws IOException {
    final CharSequence csq = "hola";
    final Writer w = writer.append(csq, 1, "hola".length() - 1);
    assertEquals(writer, w);
    assertTrue(Arrays.equals("ol".toCharArray(), writer.toCharArray()));
  }

  public void testWriteChar() throws IOException {
    writer.write('a');
    assertTrue(Arrays.equals(new char[] { 'a' }, writer.toCharArray()));

    writer.write('b');
    assertTrue(Arrays.equals(new char[] { 'a', 'b' }, writer.toCharArray()));

    writer.write('c');
    assertTrue(Arrays.equals(new char[] { 'a', 'b', 'c' }, writer.toCharArray()));
  }

  public void testWriteEmptyCharArray() throws IOException {
    final char[] charArray = new char[] { };
    writer.write(charArray);
    assertNull(writer.toCharArray());
  }

  public void testWriteNonEmptyCharArray() throws IOException {
    final char[] charArray = "hola".toCharArray();
    writer.write(charArray);
    assertTrue(Arrays.equals(charArray, writer.toCharArray()));
  }

  public void testWriteEmptyString() throws IOException {
    final String str = "";
    writer.write(str);
    assertNull(writer.toCharArray());
  }

  public void testWriteNullString() throws IOException {
    try {
      final String str = null;
      writer.write(str);
      fail("should have thrown NullPointerException");
    } catch (NullPointerException expected) {
    }
  }

  public void testWriteNonEmptyString() throws IOException {
    final String str = "hola";
    writer.write(str);
    assertTrue(Arrays.equals(str.toCharArray(), writer.toCharArray()));
  }

  public void testWriteSubStringUsingNegativeStartValue() throws IOException {
    final String str = "hola";
    try {
      writer.append(str, -1, 2);
      fail("should have thrown StringIndexOutOfBoundsException");
    } catch (StringIndexOutOfBoundsException expected) {
    }
  }

  public void testWriteSubStringUsingNegativeEndValue() throws IOException {
    final String str = "hola";
    try {
      writer.append(str, 0, -1);
      fail("should have thrown StringIndexOutOfBoundsException");
    } catch (StringIndexOutOfBoundsException expected) {
    }
  }

  public void testWriteSubStringStartIsGreaterThanEnd() throws IOException {
    final String str = "hola";
    try {
      writer.append(str, 2, 1);
      fail("should have thrown StringIndexOutOfBoundsException");
    } catch (StringIndexOutOfBoundsException expected) {
    }
  }

  public void testWriteEmptySubstring() throws IOException {
    final String str = "";
    writer.write(str, 0, 0);
    assertNull(writer.toCharArray());
  }

  public void testWriteNonEmptySubstring() throws IOException {
    final String str = "hola";
    writer.write(str, 1, 2);
    assertTrue(Arrays.equals("ol".toCharArray(), writer.toCharArray()));
  }
}
