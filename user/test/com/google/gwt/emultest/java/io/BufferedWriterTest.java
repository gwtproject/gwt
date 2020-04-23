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
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;

/** Unit test for the {@link java.io.BufferedWriter} emulated class. */
public class BufferedWriterTest extends GWTTestCase {

  /** Default buffer size for the {@link java.io.BufferedWriter} object being tested. */
  private static final int DEFAULT_BUFFER_SIZE = 2;

  /** Array of characters that exceeds the default buffer size. */
  private static final char[] BIG_CHAR_ARRAY = {'a', 'b', 'c', 'd', 'e', 'f', 'g'};

  /** {@link java.io.BufferedWriter} object being tested. */
  private BufferedWriter writer;

  /** Underliying writer used by the {@link BufferedWriter} object. */
  private SinkWriter sink;

  /**
   * {@link java.io.Writer} we pass to the {@link java.io.BufferedWriter} object being tested. It
   * allows checking that the characters written by the buffered writter is what we expect.
   */
  private static class SinkWriter extends Writer {

    private boolean closed;

    private boolean flushed;

    private ArrayList<Character> chars;

    SinkWriter() {
      closed = false;
      flushed = false;
      chars = new ArrayList<>(1024);
    }

    @Override
    public void close() {
      closed = true;
    }

    @Override
    public void flush() {
      flushed = true;
    }

    public ArrayList<Character> getChars() {
      return chars;
    }

    public boolean isClosed() {
      return closed;
    }

    public boolean isFlushed() {
      return flushed;
    }

    /**
     * Converts {@code outputChars} to primitive character array.
     *
     * @return primitive char array
     */
    public char[] toCharArray() {
      char[] charArray = new char[chars.size()];
      for (int i = 0; i < chars.size(); i++) {
        charArray[i] = chars.get(i);
      }
      return charArray;
    }

    @Override
    public void write(char[] cbuf, int off, int len) {
      for (int i = off; i < (off + len); i++) {
        chars.add(cbuf[i]);
      }
    }
  }

  /** Sets module name so that javascript compiler can operate. */
  @Override
  public String getModuleName() {
    return "com.google.gwt.emultest.EmulSuite";
  }

  @Override
  protected void gwtSetUp() throws Exception {
    super.gwtSetUp();
    sink = new SinkWriter();
    writer = new BufferedWriter(sink, DEFAULT_BUFFER_SIZE);
  }

  public void testWriteChar() throws IOException {
    writer.write('a');
    assertTrue(sink.getChars().isEmpty());

    writer.write('b');
    assertTrue(sink.getChars().isEmpty());

    writer.write('c');
    assertTrue(Arrays.equals(new char[] {'a', 'b'}, sink.toCharArray()));
  }

  public void testWriteArrayUsingNullArray() throws IOException {
    final char[] b = null;
    try {
      writer.write(b, 0, 2);
      fail("should have thrown NullPointerException");
    } catch (NullPointerException expected) {
    }
  }

  public void testWriteArrayUsingNegativeOffsetValue() throws IOException {
    final char[] b = {'a', 'b'};
    try {
      writer.write(b, -1, 1);
      fail("should have thrown IndexOutOfBoundsException");
    } catch (IndexOutOfBoundsException expected) {
    }
  }

  public void testWriteArrayUsingNegativeLengthValue() throws IOException {
    final char[] b = {'a', 'b'};
    try {
      writer.write(b, 0, -1);
      fail("should have thrown IndexOutOfBoundsException");
    } catch (IndexOutOfBoundsException expected) {
    }
  }

  public void testWriteArrayUsingInvalidRangeValue() throws IOException {
    final char[] b = {'a', 'b'};
    try {
      writer.write(b, 1, 2);
      fail("should have thrown IndexOutOfBoundsException");
    } catch (IndexOutOfBoundsException expected) {
    }
  }

  public void testWriteArraySmallerThanBufferSize() throws IOException {
    final char[] b = {'a'};
    writer.write(b, 0, 1);
    assertTrue(sink.getChars().isEmpty());
  }

  public void testWriteArrayEqualThanBufferSize() throws IOException {
    final int len = DEFAULT_BUFFER_SIZE;
    writer.write(BIG_CHAR_ARRAY, 1, len);
    assertTrue(Arrays.equals(Arrays.copyOfRange(BIG_CHAR_ARRAY, 1, len + 1), sink.toCharArray()));
  }

  public void testWriteArrayLargerThanBufferSize() throws IOException {
    final int len = DEFAULT_BUFFER_SIZE + 1;
    writer.write(BIG_CHAR_ARRAY, 1, len);
    assertTrue(Arrays.equals(Arrays.copyOfRange(BIG_CHAR_ARRAY, 1, len + 1), sink.toCharArray()));
  }

  public void testWriteArrayMultipleTimes() throws IOException {
    writer.write(BIG_CHAR_ARRAY, 0, 1);
    // writer: [ 'a' ], sink: [ ]
    assertTrue(sink.getChars().isEmpty());

    writer.write(BIG_CHAR_ARRAY, 1, 2);
    // writer: [ ], sink: [ 'a', 'b', 'c' ]
    assertTrue(Arrays.equals(Arrays.copyOfRange(BIG_CHAR_ARRAY, 0, 3), sink.toCharArray()));

    writer.write(BIG_CHAR_ARRAY, 3, 3);
    // writer: [ ], sink: [ 'a', 'b', 'c', 'd', 'e', 'f' ]
    assertTrue(Arrays.equals(Arrays.copyOfRange(BIG_CHAR_ARRAY, 0, 6), sink.toCharArray()));

    writer.write(BIG_CHAR_ARRAY, 6, 1);
    // writer: [ 'g' ], sink: [ 'a', 'b', 'c', 'd', 'e', 'f' ]
    assertTrue(Arrays.equals(Arrays.copyOfRange(BIG_CHAR_ARRAY, 0, 6), sink.toCharArray()));
  }

  public void testWriteStringUsingNullString() throws IOException {
    final String s = null;
    try {
      writer.write(s, 0, 2);
      fail("should have thrown NullPointerException");
    } catch (NullPointerException expected) {
    }
  }

  public void testWriteStringUsingNegativeOffsetValue() throws IOException {
    final String s = "ab";
    try {
      writer.write(s, -1, 1);
      fail("should have thrown IndexOutOfBoundsException");
    } catch (IndexOutOfBoundsException expected) {
    }
  }

  public void testWriteStringUsingNegativeLengthValue() throws IOException {
    final String s = "ab";
    writer.write(s, 0, -1);
    assertTrue(sink.getChars().isEmpty());
  }

  public void testWriteStringUsingInvalidRangeValue() throws IOException {
    final String s = "ab";
    try {
      writer.write(s, 1, 2);
      fail("should have thrown IndexOutOfBoundsException");
    } catch (IndexOutOfBoundsException expected) {
    }
  }

  public void testWriteStringSmallerThanBufferSize() throws IOException {
    final String s = "a";
    writer.write(s, 0, 1);
    assertTrue(sink.getChars().isEmpty());
  }

  public void testWriteStringEqualThanBufferSize() throws IOException {
    final String s = new String(BIG_CHAR_ARRAY);
    writer.write(s, 0, 1);
    assertTrue(sink.getChars().isEmpty());
  }

  public void testWriteStringLargerThanBufferSize() throws IOException {
    final String s = new String(BIG_CHAR_ARRAY);
    final int len = DEFAULT_BUFFER_SIZE + 1;
    writer.write(s, 1, len);
    assertTrue(Arrays.equals(Arrays.copyOfRange(BIG_CHAR_ARRAY, 1, len), sink.toCharArray()));
  }

  public void testWriteStringMultipleTimes() throws IOException {
    final String s = new String(BIG_CHAR_ARRAY);
    writer.write(s, 0, 1);
    // writer: [ 'a' ], sink: [ ]
    assertTrue(sink.getChars().isEmpty());

    writer.write(s, 1, 2);
    // writer: [ 'c' ], sink: [ 'a', 'b' ]
    assertTrue(
        Arrays.equals(
            Arrays.copyOfRange(BIG_CHAR_ARRAY, 0, DEFAULT_BUFFER_SIZE), sink.toCharArray()));

    writer.write(s, 3, 3);
    // writer: [ ], sink: [ 'a', 'b', 'c', 'd', 'e', 'f' ]
    assertTrue(Arrays.equals(Arrays.copyOfRange(BIG_CHAR_ARRAY, 0, 6), sink.toCharArray()));

    writer.write(s, 6, 1);
    // writer: [ 'g' ], sink: [ 'a', 'b', 'c', 'd', 'e', 'f' ]
    assertTrue(Arrays.equals(Arrays.copyOfRange(BIG_CHAR_ARRAY, 0, 6), sink.toCharArray()));
  }

  public void testFlushWithEmptyBuffer() throws IOException {
    writer.flush();
    assertTrue(sink.getChars().isEmpty());
    assertTrue(sink.isFlushed());
  }

  public void testFlushWithNonEmptyBuffer() throws IOException {
    final char[] b = new char[] {'a'};
    writer.write(b, 0, 1);
    assertTrue(sink.getChars().isEmpty());

    writer.flush();
    assertTrue(Arrays.equals(b, sink.toCharArray()));
  }

  public void testCloseWithEmptyBuffer() throws IOException {
    writer.close();
    assertTrue(sink.getChars().isEmpty());
    assertTrue(sink.isClosed());
  }

  public void testCloseWithNonEmptyBuffer() throws IOException {
    final char[] b = new char[] {'a'};
    writer.write(b, 0, 1);
    assertTrue(sink.getChars().isEmpty());

    writer.close();
    assertTrue(Arrays.equals(b, sink.toCharArray()));
  }
}
