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

import java.nio.charset.Charset;
import javaemul.internal.EmulatedCharset;

/**
 * See <a href="http://java.sun.com/j2se/1.5.0/docs/api/java/io/OutputStreamWriter.html">the
 * official Java API doc</a> for details.
 */
public class OutputStreamWriter extends Writer {

  private final OutputStream out;

  private final Charset charset;

  public OutputStreamWriter(OutputStream out, String charsetName) {
    this(out, Charset.forName(charsetName));
  }

  public OutputStreamWriter(OutputStream out, Charset charset) {
    this.out = checkNotNull(out);
    this.charset = checkNotNull(charset);
  }

  @Override
  public void close() throws IOException {
    out.close();
  }

  @Override
  public void flush() throws IOException {
    out.flush();
  }

  public String getEncoding() {
    return charset.name();
  }

  @Override
  public void write(char[] buffer, int offset, int count) throws IOException {
    IOUtils.checkOffsetAndCount(buffer, offset, count);
    byte[] byteBuffer = ((EmulatedCharset) charset).getBytes(buffer, offset, count);
    out.write(byteBuffer, 0, byteBuffer.length);
  }
}
