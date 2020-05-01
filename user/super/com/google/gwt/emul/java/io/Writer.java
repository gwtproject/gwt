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
package java.io;

import static javaemul.internal.InternalPreconditions.checkNotNull;

import java.util.Objects;

/**
 * See <a href="http://java.sun.com/j2se/1.5.0/docs/api/java/io/Writer.html">the official Java API
 * doc</a> for details.
 */
public abstract class Writer implements Appendable, Closeable, Flushable {

  protected Writer() {}

  protected Writer(Object lock) {}

  public abstract void close() throws IOException;

  public abstract void flush() throws IOException;

  public void write(char[] buf) throws IOException {
    // Ensure we throw a NullPointerException instead of a JavascriptException in case the
    // given buffer is null.
    checkNotNull(buf);
    write(buf, 0, buf.length);
  }

  public abstract void write(char[] buf, int offset, int count) throws IOException;

  public void write(int oneChar) throws IOException {
    char[] oneCharArray = new char[1];
    oneCharArray[0] = (char) oneChar;
    write(oneCharArray);
  }

  public void write(String str) throws IOException {
    // Ensure we throw a NullPointerException instead of a JavascriptException in case the
    // given string is null.
    checkNotNull(str);
    write(str, 0, str.length());
  }

  public void write(String str, int offset, int count) throws IOException {
    char[] buf = new char[count];
    str.getChars(offset, offset + count, buf, 0);
    write(buf, 0, buf.length);
  }

  public Writer append(char c) throws IOException {
    write(c);
    return this;
  }

  public Writer append(CharSequence csq) throws IOException {
    write(Objects.toString(csq));
    return this;
  }

  public Writer append(CharSequence csq, int start, int end) throws IOException {
    if (csq == null) {
      csq = "null";
    }
    write(csq.subSequence(start, end).toString());
    return this;
  }
}
