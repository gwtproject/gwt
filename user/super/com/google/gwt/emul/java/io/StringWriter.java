/*
 * Copyright 2026 GWT Project Authors
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

import static javaemul.internal.InternalPreconditions.checkArgument;

/**
 * See <a href="http://java.sun.com/j2se/1.5.0/docs/api/java/io/StringWriter.html">the official
 * Java API doc</a> for details.
 */
public class StringWriter extends Writer {

  private final StringBuffer buf = new StringBuffer();

  public StringWriter() {}

  public StringWriter(int initialSize) {
    checkArgument(initialSize >= 0);
  }

  public StringBuffer getBuffer() {
    return buf;
  }

  @Override
  public void close() throws IOException {}

  @Override
  public void flush() {}

  @Override
  public String toString() {
    return buf.toString();
  }

  @Override
  public void write(char[] chars, int offset, int count) {
    IOUtils.checkOffsetAndCount(chars, offset, count);
    if (count == 0) {
      return;
    }
    buf.append(chars, offset, count);
  }

  @Override
  public void write(int oneChar) {
    buf.append((char) oneChar);
  }

  @Override
  public void write(String str) {
    buf.append(str);
  }

  @Override
  public void write(String str, int offset, int count) {
    buf.append(str, offset, offset + count);
  }

  @Override
  public StringWriter append(char c) {
    write(c);
    return this;
  }

  @Override
  public StringWriter append(CharSequence csq) {
    write(String.valueOf(csq));
    return this;
  }

  @Override
  public StringWriter append(CharSequence csq, int start, int end) {
    if (csq == null) {
      csq = "null";
    }
    return append(csq.subSequence(start, end));
  }
}
