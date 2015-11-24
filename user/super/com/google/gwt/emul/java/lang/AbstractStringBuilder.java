/*
 * Copyright 2008 Google Inc.
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
package java.lang;

import static javaemul.internal.InternalPreconditions.checkStringBounds;

/**
 * A base class to share implementation between {@link StringBuffer} and {@link StringBuilder}.
 * <p>
 * Most methods will give expected performance results. Exception is {@link #setCharAt(int, char)},
 * which is O(n), and thus should not be used many times on the same <code>StringBuffer</code>.
 */
abstract class AbstractStringBuilder {

  abstract String getString();

  abstract void setString(String string);

  public int length() {
    return getString().length();
  }

  public void setLength(int newLength) {
    int oldLength = length();
    if (newLength < oldLength) {
      // Concat with empty string so that the compiler knows the field cannot be null.
      setString("" + getString().substring(0, newLength));
    } else if (newLength > oldLength) {
      setString(getString() + String.valueOf(new char[newLength - oldLength]));
    }
  }

  public int capacity() {
    // This implementation does not track capacity.
    return Integer.MAX_VALUE;
  }

  @SuppressWarnings("unused")
  public void ensureCapacity(int ignoredCapacity) {
    // This implementation does not track capacity
  }

  public void trimToSize() {
    // This implementation does not track capacity
  }

  public char charAt(int index) {
    return getString().charAt(index);
  }

  public void getChars(int srcStart, int srcEnd, char[] dst, int dstStart) {
    checkStringBounds(srcStart, srcEnd, length());
    checkStringBounds(dstStart, dstStart + (srcEnd - srcStart), dst.length);
    while (srcStart < srcEnd) {
      dst[dstStart++] = getString().charAt(srcStart++);
    }
  }

  /**
   * Warning! This method is <b>much</b> slower than the JRE implementation. If you need to do
   * character level manipulation, you are strongly advised to use a char[] directly.
   */
  public void setCharAt(int index, char x) {
    replace0(index, index + 1, String.valueOf(x));
  }

  public CharSequence subSequence(int start, int end) {
    return getString().substring(start, end);
  }

  public String substring(int begin) {
    return getString().substring(begin);
  }

  public String substring(int begin, int end) {
    return getString().substring(begin, end);
  }

  public int indexOf(String x) {
    return getString().indexOf(x);
  }

  public int indexOf(String x, int start) {
    return getString().indexOf(x, start);
  }

  public int lastIndexOf(String s) {
    return getString().lastIndexOf(s);
  }

  public int lastIndexOf(String s, int start) {
    return getString().lastIndexOf(s, start);
  }

  @Override
  public String toString() {
    return getString();
  }

  void append0(CharSequence x, int start, int end) {
    if (x == null) {
      x = "null";
    }
    setString(getString() + x.subSequence(start, end));
  }

  void appendCodePoint0(int x) {
    setString(getString() + String.valueOf(Character.toChars(x)));
  }

  void replace0(int start, int end, String toInsert) {
    setString(getString().substring(0, start) + toInsert + getString().substring(end));
  }

  void reverse0() {
    int length = getString().length();

    if (length <= 1) {
      return;
    }

    char[] buffer = new char[length];

    buffer[0] = getString().charAt(length - 1);

    for (int i = 1; i < length; i++) {
      buffer[i] = getString().charAt(length - 1 - i);
      if (Character.isSurrogatePair(buffer[i], buffer[i - 1])) {
        swap(buffer, i - 1, i);
      }
    }

    setString(new String(buffer));
  }

  private static void swap(char[] buffer, int f, int s) {
    char tmp = buffer[f];
    buffer[f] = buffer[s];
    buffer[s] = tmp;
  }
}