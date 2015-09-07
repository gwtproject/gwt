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

/**
 * Methods that are used by Character as well as Strings, but are not part of Characters public API.
 */
public class Characters {
  public static int codePointAt(CharSequence cs, int index, int limit) {
    char hiSurrogate = cs.charAt(index++);
    char loSurrogate;
    if (Character.isHighSurrogate(hiSurrogate) && index < limit
        && Character.isLowSurrogate(loSurrogate = cs.charAt(index))) {
      return Character.toCodePoint(hiSurrogate, loSurrogate);
    }
    return hiSurrogate;
  }

  public static int codePointBefore(CharSequence cs, int index, int start) {
    char loSurrogate = cs.charAt(--index);
    char highSurrogate;
    if (Character.isLowSurrogate(loSurrogate) && index > start
        && Character.isHighSurrogate(highSurrogate = cs.charAt(index - 1))) {
      return Character.toCodePoint(highSurrogate, loSurrogate);
    }
    return loSurrogate;
  }

  /**
   * Computes the high surrogate character of the UTF16 representation of a
   * non-BMP code point. See {@link #getLowSurrogate}.
   *
   * @param codePoint requested codePoint, required to be >=
   *          MIN_SUPPLEMENTARY_CODE_POINT
   * @return high surrogate character
   */
 public  static char getHighSurrogate(int codePoint) {
    return (char) (Character.MIN_HIGH_SURROGATE
        + (((codePoint - Character.MIN_SUPPLEMENTARY_CODE_POINT) >> 10) & 1023));
  }

  /**
   * Computes the low surrogate character of the UTF16 representation of a
   * non-BMP code point. See {@link #getHighSurrogate}.
   *
   * @param codePoint requested codePoint, required to be >=
   *          MIN_SUPPLEMENTARY_CODE_POINT
   * @return low surrogate character
   */
  public static char getLowSurrogate(int codePoint) {
    return (char) (Character.MIN_LOW_SURROGATE
        + ((codePoint - Character.MIN_SUPPLEMENTARY_CODE_POINT) & 1023));
  }

  private Characters() {}
}

