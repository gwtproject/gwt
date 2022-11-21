/*
 * Copyright 2008 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package com.google.gwt.user.server.rpc;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.util.zip.GZIPOutputStream;

import javax.servlet.http.HttpServletResponse;

/**
 * A filter output stream for an {@link HttpServletResponse}'s
 * {@link HttpServletResponse#getOutputStream() output stream} which starts out by writing to a
 * size-limited {@code byte} buffer and defers the decision of whether or not to use GZIP
 * compression to the point where the content written to it exceeds the buffer size or the stream is
 * {@link #flush() flushed} or {@link #close() closed}. If the stream is {@link #close() closed}
 * before the buffer size is exceeded, the buffer is written to the {@link HttpServletResponse}'s
 * {@link HttpServletResponse#getOutputStream() output stream} without compression activated. If the
 * stream is {@link #flush() flushed} before compression was activated, the buffer content is
 * flushed to the response's output stream, the buffer is released, and compression will not be
 * activated for this stream ever. {@link #flush() Flushing} the stream after compression has been
 * activated will delegate to the {@link GZIPOutputStream}'s {@link GZIPOutputStream#flush() flush}
 * method.
 * <p>
 * 
 * The stream is not thread-safe. Multiple threads trying to write to it concurrently may end up in
 * a race condition, may overwrite buffer content and may cause inconsistencies when crossing the
 * size threshold for compression.
 */
public class LazyGzipCompressingOutputStream extends FilterOutputStream {
  /**
   * If set, the decision about compression has not yet been made. Bytes written will be stored in
   * the buffer until its size is exceeded. Then, the decision for compression is made. When
   * {@link #flush()} is called, bytes buffered to far will be written to the output stream and the
   * buffer will be cleared as the decision has then been fixed.
   */
  private byte[] buffer;
  private int bytesWrittenToBuffer;
  private final HttpServletResponse response;
  
  public LazyGzipCompressingOutputStream(HttpServletResponse response,
      int sizeLimitForNoCompression) throws IOException {
    super(response.getOutputStream());
    buffer = new byte[sizeLimitForNoCompression];
    this.response = response;
  }

  @Override
  public void write(int b) throws IOException {
    if (buffer == null) {
      super.write(b);
    } else {
      if (bytesWrittenToBuffer < buffer.length) {
        buffer[bytesWrittenToBuffer++] = (byte) b;
      } else {
        activateCompression();
        super.write(b);
      }
    }
  }

  @Override
  public void write(byte[] b, int off, int len) throws IOException {
    if (buffer == null) {
      super.write(b, off, len);
    } else {
      if (bytesWrittenToBuffer + len <= buffer.length) {
        System.arraycopy(b, off, buffer, bytesWrittenToBuffer, len);
        bytesWrittenToBuffer += len;
      } else {
        activateCompression();
        super.write(b, off, len);
      }
    }
  }

  @Override
  public void flush() throws IOException {
    if (buffer != null) {
      flushBuffer();
    }
    super.flush();
  }

  @Override
  public void close() throws IOException {
    if (buffer != null) {
      flushBuffer();
    }
    super.close();
  }

  /**
   * Sets the GZIP compression header on the {@link #response}, wraps the {@link #out} stream with a
   * {@link GZIPOutputStream}, and {@link #flushBuffer() flushes the buffer to it and sets it to
   * null}.
   */
  private void activateCompression() throws IOException {
    RPCServletUtils.setGzipEncodingHeader(response);
    out = new GZIPOutputStream(out);
    flushBuffer();
  }

  /**
   * Flushes the {@link #buffer}'s current contents to the current {@link #out} stream and sets the
   * {@link #buffer} to {@code null}, thereby fixing the {@link #out} stream and with it the
   * compression decision.
   */
  private void flushBuffer() throws IOException {
    out.write(buffer, 0, bytesWrittenToBuffer);
    buffer = null;
  }
}
