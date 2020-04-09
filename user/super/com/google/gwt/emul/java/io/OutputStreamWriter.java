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

import java.nio.charset.Charset;
import javaemul.internal.EmulatedCharset;

/**
 * A class for turning a character stream into a byte stream. Data written to the target input
 * stream is converted into bytes by the provided character converter. Only UTF-8 is supported for
 * now.
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
  public OutputStreamWriter(OutputStream out, final String charsetName) {
    this(out, Charset.availableCharsets().get(charsetName));
  }

  /**
   * Constructs a new OutputStreamWriter using {@code out} as the target stream to write converted
   * characters to and {@code cs} as the character encoding.
   *
   * @param out the target stream to write converted bytes to.
   * @param cs the {@code Charset} that specifies the character encoding.
   */
  public OutputStreamWriter(OutputStream out, Charset cs) {
    if (out == null) {
      throw new NullPointerException("OutputStream provided is null");
    }
    this.out = out;
    // Note that GWT will throw a JavascriptException rather than a NullPointerException if we
    // skip this check and the buffer array is null. This way we ensure that this implementation
    // behaves in the same way as the classes that are emulated.
    if (cs == null) {
      throw new NullPointerException("charset == null");
    }
    charset = cs;
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
