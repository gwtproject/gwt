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

  public boolean checkError() {
    flush();
    return ioError;
  }

  protected void setError() {
    ioError = true;
  }

  protected void clearError() {
    ioError = false;
  }

  private void newline() {
    print('\n');
  }
}
