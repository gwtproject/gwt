/*
 * Copyright 2015 Google Inc.
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
package emul.java.io;

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

  private ArrayList<Character> outputChars;

  private Writer writer;

  /**
   * Instatiable version of {@link java.io.Writer} for testing purposes.
   */
  private static class TestWriter extends Writer {

    private List<Character> outputChars;

    public TestWriter(List<Character> outputChars) {
      this.outputChars = outputChars;
    }

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
    outputChars = new ArrayList<>(1024);
    writer = new TestWriter(outputChars);
  }

  public void testAppendChar() throws IOException {
    Writer w = writer.append('a');
    assertEquals(writer, w);

    char[] expected = new char[] { 'a' };
    assertTrue(Arrays.equals(expected, toCharArray(outputChars)));

    w = writer.append('b');
    assertEquals(writer, w);
    expected = new char[] { 'a', 'b' };
    assertTrue(Arrays.equals(expected, toCharArray(outputChars)));

    w = writer.append('c');
    assertEquals(writer, w);
    expected = new char[] { 'a', 'b', 'c' };
    assertTrue(Arrays.equals(expected, toCharArray(outputChars)));
  }

  public void testAppendNullCharSequence() throws IOException {
    final Writer w = writer.append(null);
    assertEquals(writer, w);
    assertTrue(Arrays.equals("null".toCharArray(), toCharArray(outputChars)));
  }

  public void testAppendEmptyCharSequence() throws IOException {
    final CharSequence csq = "";
    final Writer w = writer.append(csq);
    assertEquals(writer, w);
    assertTrue(outputChars.isEmpty());
  }

  public void testAppendNonEmptyCharSequence() throws IOException {
    final CharSequence csq = "hola";
    final Writer w = writer.append(csq);
    assertEquals(writer, w);
    assertTrue(Arrays.equals("hola".toCharArray(), toCharArray(outputChars)));
  }

  public void testAppendSubCharSequenceUsingNegativeStartValue() throws IOException {
    final CharSequence csq = "hola";
    try {
      writer.append(csq, -1, 2);
      fail("should have thrown IndexOutOfBoundsException");
    } catch (IndexOutOfBoundsException expected) {
    }
  }

  public void testAppendSubCharSequenceUsingNegativeEndValue() throws IOException {
    final CharSequence csq = "hola";
    try {
      writer.append(csq, 0, -1);
      fail("should have thrown IndexOutOfBoundsException");
    } catch (IndexOutOfBoundsException expected) {
    }
  }

  public void testAppendSubCharSequenceStartIsGreaterThanEnd() throws IOException {
    final CharSequence csq = "hola";
    try {
      writer.append(csq, 2, 1);
      fail("should have thrown IndexOutOfBoundsException");
    } catch (IndexOutOfBoundsException expected) {
    }
  }

  public void testAppendNullSubCharSequence() throws IOException {
    final Writer w = writer.append(null, 1, "null".length() - 1);
    assertEquals(writer, w);
    assertTrue(Arrays.equals("ul".toCharArray(), toCharArray(outputChars)));
  }

  public void testAppendEmptySubCharSequence() throws IOException {
    final CharSequence csq = "";
    final Writer w = writer.append(csq, 0, 0);
    assertEquals(writer, w);
    assertTrue(outputChars.isEmpty());
  }

  public void testAppendNonEmptySubCharSequence() throws IOException {
    final CharSequence csq = "hola";
    final Writer w = writer.append(csq, 1, "hola".length() - 1);
    assertEquals(writer, w);
    assertTrue(Arrays.equals("ol".toCharArray(), toCharArray(outputChars)));
  }

  public void testWriteChar() throws IOException {
    writer.write('a');
    char[] expected = new char[] { 'a' };
    assertTrue(Arrays.equals(expected, toCharArray(outputChars)));

    writer.write('b');
    expected = new char[] { 'a', 'b' };
    assertTrue(Arrays.equals(expected, toCharArray(outputChars)));

    writer.write('c');
    expected = new char[] { 'a', 'b', 'c' };
    assertTrue(Arrays.equals(expected, toCharArray(outputChars)));
  }

  public void testWriteEmptyCharArray() throws IOException {
    final char[] charArray = new char[] { };
    writer.write(charArray);
    assertTrue(outputChars.isEmpty());
  }

  public void testWriteNonEmptyCharArray() throws IOException {
    final char[] charArray = "hola".toCharArray();
    writer.write(charArray);
    assertTrue(Arrays.equals(charArray, toCharArray(outputChars)));
  }

  public void testWriteEmptyString() throws IOException {
    final String str = "";
    writer.write(str);
    assertTrue(outputChars.isEmpty());
  }

  public void testWriteNonEmptyString() throws IOException {
    final String str = "hola";
    writer.write(str);
    assertTrue(Arrays.equals(str.toCharArray(), toCharArray(outputChars)));
  }

  public void testWriteSubStringUsingNegativeStartValue() throws IOException {
    final String str = "hola";
    try {
      writer.append(str, -1, 2);
      fail("should have thrown IndexOutOfBoundsException");
    } catch (IndexOutOfBoundsException expected) {
    }
  }

  public void testWriteSubStringUsingNegativeEndValue() throws IOException {
    final String str = "hola";
    try {
      writer.append(str, 0, -1);
      fail("should have thrown IndexOutOfBoundsException");
    } catch (IndexOutOfBoundsException expected) {
    }
  }

  public void testWriteSubStringStartIsGreaterThanEnd() throws IOException {
    final String str = "hola";
    try {
      writer.append(str, 2, 1);
      fail("should have thrown IndexOutOfBoundsException");
    } catch (IndexOutOfBoundsException expected) {
    }
  }

  public void testWriteEmptySubstring() throws IOException {
    final String str = "";
    writer.write(str, 0, 0);
    assertTrue(outputChars.isEmpty());
  }

  public void testWriteNonEmptySubstring() throws IOException {
    final String str = "hola";
    writer.write(str, 1, 2);
    assertTrue(Arrays.equals("ol".toCharArray(), toCharArray(outputChars)));
  }

  /**
   * Converts {@link ArrayList} containing {@link Character} to primitive character array.
   *
   * @param arrayList ArrayList containing characters
   * @return primitive char array
   */
  private static char[] toCharArray(ArrayList<Character> arrayList) {
    char[] charArray = new char[arrayList.size()];
    for (int i = 0; i < arrayList.size(); i++) {
      charArray[i] = arrayList.get(i);
    }
    return charArray;
  }
}
