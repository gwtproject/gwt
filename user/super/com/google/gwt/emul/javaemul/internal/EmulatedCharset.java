/*
 * Copyright 2015 Google Inc.
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
package javaemul.internal;

import java.nio.charset.Charset;

/**
 * Provides Charset implementations.
 */
public abstract class EmulatedCharset extends Charset {

  public static final EmulatedCharset UTF_8 = new UtfCharset("UTF-8");

  public static final EmulatedCharset ISO_LATIN_1 = new LatinCharset("ISO-LATIN-1");

  public static final EmulatedCharset ISO_8859_1 = new LatinCharset("ISO-8859-1");

  private static class LatinCharset extends EmulatedCharset {
    public LatinCharset(String name) {
      super(name);
    }

    @Override
    public byte[] getBytes(char[] buffer, int offset, int count) {
      int n = offset + count;
      byte[] bytes = new byte[count];
      for (int i = offset; i < n; ++i) {
        bytes[i] = (byte) (buffer[i] & 255);
      }
      return bytes;
    }

    @Override
    public byte[] getBytes(String str) {
      int n = str.length();
      byte[] bytes = new byte[n];
      for (int i = 0; i < n; ++i) {
        bytes[i] = (byte) (str.charAt(i) & 255);
      }
      return bytes;
    }

    @Override
    public char[] decodeString(byte[] bytes, int ofs, int len) {
      char[] chars = new char[len];
      for (int i = 0; i < len; ++i) {
        chars[i] = (char) (bytes[ofs + i] & 255);
      }
      return chars;
    }
  }

  private static class UtfCharset extends EmulatedCharset {
    public UtfCharset(String name) {
      super(name);
    }

    @Override
    public char[] decodeString(byte[] bytes, int ofs, int len) {
      // TODO(jat): consider using decodeURIComponent(escape(bytes)) instead
      int charCount = 0;
      for (int i = 0; i < len; ) {
        ++charCount;
        byte ch = bytes[ofs + i];
        if ((ch & 0xC0) == 0x80) {
          throw new IllegalArgumentException("Invalid UTF8 sequence");
        } else if ((ch & 0x80) == 0) {
          ++i;
        } else if ((ch & 0xE0) == 0xC0) {
          i += 2;
        } else if ((ch & 0xF0) == 0xE0) {
          i += 3;
        } else if ((ch & 0xF8) == 0xF0) {
          i += 4;
        } else {
          // no 5+ byte sequences since max codepoint is less than 2^21
          throw new IllegalArgumentException("Invalid UTF8 sequence");
        }
        if (i > len) {
          throw new IndexOutOfBoundsException("Invalid UTF8 sequence");
        }
      }
      char[] chars = new char[charCount];
      int outIdx = 0;
      int count = 0;
      for (int i = 0; i < len; ) {
        int ch = bytes[ofs + i++];
        if ((ch & 0x80) == 0) {
          count = 1;
          ch &= 127;
        } else if ((ch & 0xE0) == 0xC0) {
          count = 2;
          ch &= 31;
        } else if ((ch & 0xF0) == 0xE0) {
          count = 3;
          ch &= 15;
        } else if ((ch & 0xF8) == 0xF0) {
          count = 4;
          ch &= 7;
        } else if ((ch & 0xFC) == 0xF8) {
          count = 5;
          ch &= 3;
        }
        while (--count > 0) {
          byte b = bytes[ofs + i++];
          if ((b & 0xC0) != 0x80) {
            throw new IllegalArgumentException("Invalid UTF8 sequence at "
                + (ofs + i - 1) + ", byte=" + Integer.toHexString(b));
          }
          ch = (ch << 6) | (b & 63);
        }
        outIdx += Character.toChars(ch, chars, outIdx);
      }
      return chars;
    }

    @Override
    public byte[] getBytes(char[] buffer, int offset, int count) {
      int n = offset + count;
      byte[] bytes = new byte[0];
      for (int i = offset; i < n; ) {
        int ch = Character.codePointAt(buffer, i, n);
        i += Character.charCount(ch);
        encodeUtf8(bytes, ch);
      }
      return bytes;
    }

    @Override
    public byte[] getBytes(String str) {
      // TODO(jat): consider using unescape(encodeURIComponent(bytes)) instead
      int n = str.length();
      byte[] bytes = new byte[0];
      for (int i = 0; i < n;) {
        int ch = str.codePointAt(i);
        i += Character.charCount(ch);
        encodeUtf8(bytes, ch);
      }
      return bytes;
    }

    /**
     * Encode a single character in UTF8.
     *
     * @param bytes byte array to store character in
     * @param codePoint character to encode
     * @throws IllegalArgumentException if codepoint >= 2^26
     */
    private void encodeUtf8(byte[] bytes, int codePoint) {
      if (codePoint < (1 << 7)) {
        ArrayHelper.push(bytes, (byte) (codePoint & 127));
      } else if (codePoint < (1 << 11)) {
        // 110xxxxx 10xxxxxx
        ArrayHelper.push(bytes, (byte) (((codePoint >> 6) & 31) | 0xC0));
        ArrayHelper.push(bytes, (byte) ((codePoint & 63) | 0x80));
      } else if (codePoint < (1 << 16)) {
        // 1110xxxx 10xxxxxx 10xxxxxx
        ArrayHelper.push(bytes, (byte) (((codePoint >> 12) & 15) | 0xE0));
        ArrayHelper.push(bytes, (byte) (((codePoint >> 6) & 63) | 0x80));
        ArrayHelper.push(bytes, (byte) ((codePoint & 63) | 0x80));
      } else if (codePoint < (1 << 21)) {
        // 11110xxx 10xxxxxx 10xxxxxx 10xxxxxx
        ArrayHelper.push(bytes, (byte) (((codePoint >> 18) & 7) | 0xF0));
        ArrayHelper.push(bytes, (byte) (((codePoint >> 12) & 63) | 0x80));
        ArrayHelper.push(bytes, (byte) (((codePoint >> 6) & 63) | 0x80));
        ArrayHelper.push(bytes, (byte) ((codePoint & 63) | 0x80));
      } else if (codePoint < (1 << 26)) {
        // 111110xx 10xxxxxx 10xxxxxx 10xxxxxx 10xxxxxx
        ArrayHelper.push(bytes, (byte) (((codePoint >> 24) & 3) | 0xF8));
        ArrayHelper.push(bytes, (byte) (((codePoint >> 18) & 63) | 0x80));
        ArrayHelper.push(bytes, (byte) (((codePoint >> 12) & 63) | 0x80));
        ArrayHelper.push(bytes, (byte) (((codePoint >> 6) & 63) | 0x80));
        ArrayHelper.push(bytes, (byte) ((codePoint & 63) | 0x80));
      } else {
        throw new IllegalArgumentException("Character out of range: " + codePoint);
      }
    }
  }

  public EmulatedCharset(String name) {
    super(name, null);
  }

  public abstract byte[] getBytes(String string);

  public abstract byte[] getBytes(char[] buffer, int offset, int count);

  public abstract char[] decodeString(byte[] bytes, int ofs, int len);
}
