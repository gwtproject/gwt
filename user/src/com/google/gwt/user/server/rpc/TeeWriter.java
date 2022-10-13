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

import java.io.FilterWriter;
import java.io.IOException;
import java.io.Writer;

/**
 * A writer that wraps one writer and copies all output written to it not only to that one writer
 * but also to another writer ("tee"). It is permissible to use {@code null} as the other writer in
 * which case this {@link TeeWriter} behaves like a regular {@link FilterWriter}. The other writer
 * can be obtained by calling {@link #getOtherWriter()}. This can, e.g., be used with a
 * {@link StringWriter} from which the output written to this writer can later be obtained as a
 * single {@link String}.
 * 
 * @param <W> the type of the other writer to which output is sent in parallel
 */
public class TeeWriter<W extends Writer> extends FilterWriter {
  private W tee;
  private boolean written;

  public TeeWriter(Writer out, W tee) {
    super(out);
    this.tee = tee;
    written = false;
  }

  public W getOtherWriter() {
    return tee;
  }

  /**
   * If a "tee" writer hasn't been set at construction time and as long as nothing has been written
   * to this writer, a caller may use this method to set a "tee" writer once.
   * 
   * @throws IllegalStateException in case a "tee" has already been set (or formally,
   *           {@link #getOtherWriter()}{@code != null}) or data has already been written to this
   *           writer using any of the {@code write} methods.
   */
  public void setOtherWriter(W tee) {
    if (written) {
      throw new IllegalStateException("Data was already written to this writer");
    }
    if (getOtherWriter() != null) {
      throw new IllegalStateException(
          "A \"tee\" writer has already been provided and cannot be replaced");
    }
    this.tee = tee;
  }

  @Override
  public void write(int c) throws IOException {
    written = true;
    super.write(c);
    if (tee != null) {
      tee.write(c);
    }
  }

  @Override
  public void write(char[] cbuf, int off, int len) throws IOException {
    written = true;
    super.write(cbuf, off, len);
    if (tee != null) {
      tee.write(cbuf, off, len);
    }
  }

  @Override
  public void write(String str, int off, int len) throws IOException {
    written = true;
    super.write(str, off, len);
    if (tee != null) {
      tee.write(str, off, len);
    }
  }

  @Override
  public void flush() throws IOException {
    super.flush();
    if (tee != null) {
      tee.flush();
    }
  }

  @Override
  public void close() throws IOException {
    super.close();
    if (tee != null) {
      tee.close();
    }
  }
}
