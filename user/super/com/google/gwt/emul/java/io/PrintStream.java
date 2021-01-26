/*
 * Copyright 2006 Google Inc.
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
package java.io;

/**
 * See <a href="http://java.sun.com/j2se/1.5.0/docs/api/java/io/PrintStream.html">the official Java
 * API doc</a> for details.
 */
public class PrintStream extends FilterOutputStream {

  /** Indicates whether or not this PrintStream has incurred an error. */
  private boolean ioError = false;

  public PrintStream(OutputStream out) {
    super(out);
  }

  public void print(boolean x) {
    print(String.valueOf(x));
  }

  public void print(char x) {
    print(String.valueOf(x));
  }

  public void print(char[] x) {
    if (x == null) {
      throw new NullPointerException("null array");
    }
    print(new String(x, 0, x.length));
  }

  public void print(double x) {
    print(String.valueOf(x));
  }

  public void print(float x) {
    print(String.valueOf(x));
  }

  public void print(int x) {
    print(String.valueOf(x));
  }

  public void print(long x) {
    print(String.valueOf(x));
  }

  public void print(Object x) {
    print(String.valueOf(x));
  }

  public void print(String s) {
    if (out == null) {
      setError();
      return;
    }
    if (s == null) {
      print("null");
      return;
    }

    try {
      write(s.getBytes());
    } catch (IOException e) {
      setError();
    }
  }

  public void println() {
    newline();
  }

  public void println(boolean x) {
    println(String.valueOf(x));
  }

  public void println(char x) {
    println(String.valueOf(x));
  }

  public void println(char[] x) {
    if (x == null) {
      throw new NullPointerException("null array");
    }
    println(new String(x, 0, x.length));
  }

  public void println(double x) {
    println(String.valueOf(x));
  }

  public void println(float x) {
    println(String.valueOf(x));
  }

  public void println(int x) {
    println(String.valueOf(x));
  }

  public void println(long x) {
    println(String.valueOf(x));
  }

  public void println(Object x) {
    println(String.valueOf(x));
  }

  public void println(String s) {
    print(s);
    newline();
  }

  /**
   * Ensures that all pending data is sent out to the target stream. It also flushes the target
   * stream. If an I/O error occurs, this stream's error state is set to {@code true}.
   */
  @Override
  public void flush() {
    if (out != null) {
      try {
        out.flush();
        return;
      } catch (IOException e) {
        // Ignored, fall through to setError
      }
    }
    setError();
  }

  /**
   * Closes this print stream. Flushes this stream and then closes the target stream. If an I/O
   * error occurs, this stream's error state is set to {@code true}.
   */
  @Override
  public void close() {
    flush();
    if (out != null) {
      try {
        out.close();
      } catch (IOException e) {
        setError();
      } finally {
        out = null;
      }
    }
  }

  /**
   * Writes {@code count} bytes from {@code buffer} starting at {@code offset} to the target stream.
   * If autoflush is set, this stream gets flushed after writing the buffer.
   *
   * <p>This stream's error flag is set to {@code true} if this stream is closed or an I/O error
   * occurs.
   *
   * @param buffer the buffer to be written.
   * @param offset the index of the first byte in {@code buffer} to write.
   * @param length the number of bytes in {@code buffer} to write.
   * @throws IndexOutOfBoundsException if {@code offset < 0} or {@code count < 0}, or if {@code
   *     offset + count} is bigger than the length of {@code buffer}.
   * @see #flush()
   */
  @Override
  public void write(byte[] buffer, int offset, int length) {
    // Force buffer null check first!
    IOUtils.checkOffsetAndCount(buffer, offset, length);
    if (out == null) {
      setError();
      return;
    }
    try {
      out.write(buffer, offset, length);
    } catch (IOException e) {
      setError();
    }
  }

  /**
   * Writes one byte to the target stream. Only the least significant byte of the integer {@code
   * oneByte} is written. This stream is flushed if {@code oneByte} is equal to the character {@code
   * '\n'} and this stream is set to autoflush.
   *
   * <p>This stream's error flag is set to {@code true} if it is closed or an I/O error occurs.
   *
   * @param oneByte the byte to be written
   */
  @Override
  public void write(int oneByte) {
    if (out == null) {
      setError();
      return;
    }
    try {
      out.write(oneByte);
      int b = oneByte & 0xFF;
      // 0x0A is ASCII newline, 0x15 is EBCDIC newline.
      boolean isNewline = b == 0x0A || b == 0x15;
      if (isNewline) {
        flush();
      }
    } catch (IOException e) {
      setError();
    }
  }

  /**
   * Flushes this stream and returns the value of the error flag.
   *
   * @return {@code true} if either an {@code IOException} has been thrown previously or if {@code
   *     setError()} has been called; {@code false} otherwise.
   * @see #setError()
   */
  public boolean checkError() {
    flush();
    return ioError;
  }

  /** Sets the error flag of this print stream to true. */
  protected void setError() {
    ioError = true;
  }

  /** Put the line separator character onto the print stream. */
  private void newline() {
    print('\n');
  }
}
