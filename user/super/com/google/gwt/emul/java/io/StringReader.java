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

import static javaemul.internal.InternalPreconditions.checkArgument;

/**
 * Reads characters from a string.
 */
public class StringReader extends Reader {
  private final String text;
  private int position;
  private int mark;

  /**
   * Constructs a reader which will read from the given string.
   */
  public StringReader(String text) {
    this.text = text;
  }

  @Override
  public void close() throws IOException { }

  /**
   * Reads up to the specified number of characters from the string.
   */
  @Override
  public int read(char[] buf, int off, int readLength) {
    if (position >= text.length()) {
      return -1;
    }
    int length = Math.min(text.length() - position, readLength);
    text.getChars(position, position + length, buf, off);
    position += length;
    return length;
  }

  @Override
  public boolean markSupported() {
    return true;
  }

  @Override
  public void mark(int readAheadLimit) throws IOException {
    checkArgument(readAheadLimit >= 0);
    mark = position;
  }

  @Override
  public void reset() throws IOException {
    position = mark;
  }
}
