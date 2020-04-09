/*
 * Copyright 2018 Google Inc.
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

  /**
   * Constructs a new OutputStreamWriter using {@code out} as the target stream to write converted
   * characters to and {@code charsetName} as the character encoding. If the encoding cannot be
   * found, an UnsupportedEncodingException error is thrown.
   *
   * @param out the target stream to write converted bytes to.
   * @param charsetName the string describing the desired character encoding.
   * @throws NullPointerException if {@code charsetName} is {@code null}.
   */
  public OutputStreamWriter(OutputStream out, String charsetName) {
    this(out, Charset.forName(charsetName));
  }

  /**
   * Constructs a new OutputStreamWriter using {@code out} as the target stream to write converted
   * characters to and {@code charset} as the character encoding.
   *
   * @param out the target stream to write converted bytes to.
   * @param charset the {@code Charset} that specifies the character encoding.
   */
  public OutputStreamWriter(OutputStream out, Charset charset) {
    this.out = checkNotNull(out);
    this.charset = checkNotNull(charset);
  }

  /**
   * Closes the underlying OutputStream.
   *
   * @throws IOException if an error occurs while closing this writer.
   */
  @Override
  public void close() throws IOException {
    out.close();
  }

  /**
   * Flushes the underlying OutputStream.
   *
   * @throws IOException if an error occurs while flushing this writer.
   */
  @Override
  public void flush() throws IOException {
    out.flush();
  }

  /**
   * Returns the canonical name of the encoding used by this writer to convert characters to bytes.
   * Most callers should probably keep track of the String or Charset they passed in; this method
   * may not return the same name.
   */
  public String getEncoding() {
    return charset.name();
  }

  /**
   * Writes {@code count} characters starting at {@code offset} to underlying stream {@code out}.
   * The characters are immediately converted to bytes by the getByteArray method and written to
   * underlying output stream.
   *
   * @param buffer the array containing characters to write.
   * @param offset the index of the first character in {@code buf} to write.
   * @param count the maximum number of characters to write.
   * @throws IndexOutOfBoundsException if {@code offset < 0} or {@code count < 0}, or if {@code
   *     offset + count} is greater than the size of {@code buf}.
   * @throws IOException if this writer has already been closed or another I/O error occurs.
   */
  @Override
  public void write(char[] buffer, int offset, int count) throws IOException {
    IOUtils.checkOffsetAndCount(buffer, offset, count);
    byte[] byteBuffer = ((EmulatedCharset) charset).getBytes(buffer, offset, count);
    out.write(byteBuffer, 0, byteBuffer.length);
  }
}
