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

import static java.nio.charset.StandardCharsets.UTF_8;

import com.google.gwt.junit.client.GWTTestCase;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;

/** Unit test for the {@link java.io.PrintStream} emulated class. */
public class PrintStreamTest extends GWTTestCase {

  /** {@link java.io.PrintStream} object being tested. */
  private PrintStream ps;

  /** Underlying output stream used by the {@link PrintStream} object. */
  private ByteArrayOutputStream baos;

  private static class MockPrintStream extends PrintStream {

    public MockPrintStream(OutputStream os) {
      super(os);
    }

    @Override
    public void clearError() {
      super.clearError();
    }

    @Override
    public void setError() {
      super.setError();
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
    baos = new ByteArrayOutputStream();
    ps = new PrintStream(baos);
  }

  public void testPrintCharArray() {
    char[] charArray = {'a', 'b', 'c', '"', '&', '<', '>'};
    ps.print(charArray);
    ps.close();
    assertTrue(
        "Incorrect char[] written",
        Arrays.equals(new String(charArray).getBytes(UTF_8), baos.toByteArray()));
  }

  public void testPrintChar() {
    ps.print('t');
    ps.close();
    assertEquals("Incorrect char written", "t", new String(baos.toByteArray(), UTF_8));
  }

  public void testPrintDouble() {
    ps.print(2345.76834720202);
    ps.close();
    assertEquals(
        "Incorrect double written", "2345.76834720202", new String(baos.toByteArray(), UTF_8));
  }

  public void testPrintInt() {
    ps.print(768347202);
    ps.close();
    assertEquals("Incorrect int written", "768347202", new String(baos.toByteArray(), UTF_8));
  }

  public void testPrintLong() {
    ps.print(919728433988L);
    ps.close();
    assertEquals("Incorrect long written", "919728433988", new String(baos.toByteArray(), UTF_8));
  }

  public void testPrintFloat() {
    ps.print(29.08764);
    ps.close();
    assertEquals("Incorrect float written", "29.08764", new String(baos.toByteArray(), UTF_8));
  }

  public void testPrintNullObject() {
    ps.print((Object) null);
    ps.close();
    assertEquals("null should be written", "null", new String(baos.toByteArray(), UTF_8));
  }

  public void testPrintObject() {
    ps.print(new ArrayList<String>());
    ps.close();
    assertEquals("Incorrect Object written", "[]", new String(baos.toByteArray(), UTF_8));
  }

  public void testPrintNullString() {
    ps.print((String) null);
    ps.close();
    assertEquals("null should be written", "null", new String(baos.toByteArray(), UTF_8));
  }

  public void testPrintString() {
    ps.print("Hello World");
    ps.close();
    assertEquals("Incorrect String written", "Hello World", new String(baos.toByteArray(), UTF_8));
  }

  public void testPrintBoolean() {
    ps.print(true);
    ps.close();
    assertEquals("Incorrect boolean written", "true", new String(baos.toByteArray(), UTF_8));
  }

  public void testPrintln() {
    ps.println();
    ps.close();
    ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
    assertEquals("Newline not written", '\n', bais.read());
  }

  public void testPrintlnCharArray() {
    char[] charArray = {'a', 'b', 'c', '"', '&', '<', '>'};
    ps.println(charArray);
    ps.close();
    ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
    byte[] outBytes = new byte[7];
    bais.read(outBytes, 0, 7);
    assertTrue(
        "Incorrect char[] written", Arrays.equals(new String(charArray).getBytes(UTF_8), outBytes));
    assertEquals("Newline not written", '\n', bais.read());
  }

  public void testPrintlnChar() {
    ps.println('t');
    ps.close();
    ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
    assertEquals("Incorrect char written", 't', bais.read());
    assertEquals("Newline not written", '\n', bais.read());
  }

  public void testPrintlnDouble() {
    ps.println(2345.76834720202);
    ps.close();
    ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
    byte[] outBytes = new byte[16];
    bais.read(outBytes, 0, 16);
    assertEquals("Incorrect double written", "2345.76834720202", new String(outBytes, UTF_8));
    assertEquals("Newline not written", '\n', bais.read());
  }

  public void testPrintlnInt() {
    ps.println(768347202);
    ps.close();
    ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
    byte[] outBytes = new byte[9];
    bais.read(outBytes, 0, 9);
    assertEquals("Incorrect int written", "768347202", new String(outBytes, UTF_8));
    assertEquals("Newline not written", '\n', bais.read());
  }

  public void testPrintlnLong() {
    ps.println(919728433988L);
    ps.close();
    ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
    byte[] outBytes = new byte[12];
    bais.read(outBytes, 0, 12);
    assertEquals("Incorrect double written", "919728433988", new String(outBytes, UTF_8));
    assertEquals("Newline not written", '\n', bais.read());
  }

  public void testPrintlnFloat() {
    ps.println(29.08764);
    ps.close();
    ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
    byte[] outBytes = new byte[8];
    bais.read(outBytes, 0, 8);
    assertEquals("Incorrect float written", "29.08764", new String(outBytes, 0, 8, UTF_8));
    assertEquals("Newline not written", '\n', bais.read());
  }

  public void testPrintlnNullObject() {
    ps.println((Object) null);
    ps.close();
    ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
    byte[] outBytes = new byte[4];
    bais.read(outBytes, 0, 4);
    assertEquals("null should be written", "null", new String(outBytes, UTF_8));
    assertEquals("Newline not written", '\n', bais.read());
  }

  public void testPrintlnObject() {
    ps.println(new ArrayList<String>());
    ps.close();
    ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
    byte[] outBytes = new byte[2];
    bais.read(outBytes, 0, 2);
    assertEquals("Incorrect Object written", "[]", new String(outBytes, UTF_8));
    assertEquals("Newline not written", '\n', bais.read());
  }

  public void testPrintlnNullString() {
    ps.println((String) null);
    ps.close();
    ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
    byte[] outBytes = new byte[4];
    bais.read(outBytes, 0, 4);
    assertEquals("null should be written", "null", new String(outBytes, UTF_8));
    assertEquals("Newline not written", '\n', bais.read());
  }

  public void testPrintlnString() {
    ps.println("Hello World");
    ps.close();
    ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
    byte[] outBytes = new byte[11];
    bais.read(outBytes, 0, 11);
    assertEquals("Incorrect String written", "Hello World", new String(outBytes, UTF_8));
    assertEquals("Newline not written", '\n', bais.read());
  }

  public void testPrintlnBoolean() {
    ps.println(true);
    ps.close();
    ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
    byte[] outBytes = new byte[4];
    bais.read(outBytes, 0, 4);
    assertEquals("Incorrect boolean written", "true", new String(outBytes, UTF_8));
    assertEquals("Newline not written", '\n', bais.read());
  }

  public void testCheckError() {
    ps =
        new PrintStream(
            new OutputStream() {

              @Override
              public void write(int b) throws IOException {
                throw new IOException();
              }

              @Override
              public void write(byte[] b, int o, int l) throws IOException {
                throw new IOException();
              }
            });
    ps.print("Hello World");
    assertTrue(ps.checkError());
  }

  public void testClearError() {
    MockPrintStream ps = new MockPrintStream(baos);
    assertFalse(ps.checkError());
    ps.setError();
    assertTrue(ps.checkError());
    ps.clearError();
    assertFalse(ps.checkError());
    ps.close();
  }
}
