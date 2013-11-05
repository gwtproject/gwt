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

/**
 * A fast way to create strings using multiple appends. This is implemented
 * using a {@link StringBufferImpl} that is chosen with deferred binding.
 * 
 * Most methods will give expected performance results. Exceptions are
 * {@link #setCharAt(int, char)}, which is O(n), and {@link #length()}, which
 * forces a {@link #toString()} and thus should not be used many times on the
 * same <code>StringBuilder</code>.
 * 
 * This class is an exact clone of {@link StringBuffer} except for the name. Any
 * change made to one should be mirrored in the other.
 */
public class StringBuilder implements CharSequence, Appendable {

  /**
   * Package protected to allow {@link StringBuffer} to reuse it.
   */
  static String reverseString(String s) {
    int length = s.length();

    if (length <= 1) {
      return s;
    }

    char[] buffer = new char[length];

    buffer[0] = s.charAt(length - 1);

    for (int i = 1; i < length; i++) {
      buffer[i] = s.charAt(length - 1 - i);
      if (Character.isSurrogatePair(buffer[i], buffer[i - 1])) {
        swap(buffer, i - 1, i);
      }
    }

    return new String(buffer);
  }

  private static void swap(char[] buffer, int f, int s) {
    char tmp = buffer[f];
    buffer[f] = buffer[s];
    buffer[s] = tmp;
  }

  /**
   * Do not initialize field in class body.
   * Initializing variables in the class body leads to hidden class changes and
   * need to be avoided in performance critical code.
   */
  private String data;

  public StringBuilder() {
    init();
  }

  public StringBuilder(CharSequence s) {
    this(s.toString());
  }

  /**
   * This implementation does not track capacity; using this constructor is
   * functionally equivalent to using the zero-argument constructor.
   */
  @SuppressWarnings("unused")
  public StringBuilder(int ignoredCapacity) {
    init();
  }

  public StringBuilder(String s) {
    init();
    append(s);
  }

  public StringBuilder append(boolean x) {
    data += x;
    return this;
  }

  public StringBuilder append(char x) {
    appendValue(String.valueOf(x));
    return this;
  }

  public StringBuilder append(char[] x) {
    appendValue(String.valueOf(x));
    return this;
  }

  public StringBuilder append(char[] x, int start, int len) {
    appendValue(String.valueOf(x, start, len));
    return this;
  }

  public StringBuilder append(CharSequence x) {
    appendValue(x);
    return this;
  }

  public StringBuilder append(CharSequence x, int start, int end) {
    if (x == null) {
      x = "null";
    }
    appendValue(x.subSequence(start, end));
    return this;
  }

  public StringBuilder append(double x) {
    data += x;
    return this;
  }

  public StringBuilder append(float x) {
    data += x;
    return this;
  }

  public StringBuilder append(int x) {
    data += x;
    return this;
  }

  public StringBuilder append(long x) {
    appendValue(String.valueOf(x));
    return this;
  }

  public StringBuilder append(Object x) {
    appendValue(x);
    return this;
  }

  public StringBuilder append(String x) {
    appendValue(x);
    return this;
  }

  public StringBuilder append(StringBuffer x) {
    appendValue(x);
    return this;
  }

  public StringBuilder appendCodePoint(int x) {
    appendValue(String.valueOf(Character.toChars(x)));
    return this;
  }

  /**
   * This implementation does not track capacity; always returns
   * {@link Integer#MAX_VALUE}.
   */
  public int capacity() {
    return Integer.MAX_VALUE;
  }

  public char charAt(int index) {
    return data.charAt(index);
  }

  public StringBuilder delete(int start, int end) {
    return replace(start, end, "");
  }

  public StringBuilder deleteCharAt(int start) {
    return delete(start, start + 1);
  }

  /**
   * This implementation does not track capacity; calling this method has no
   * effect.
   */
  @SuppressWarnings("unused")
  public void ensureCapacity(int ignoredCapacity) {
  }

  public void getChars(int srcStart, int srcEnd, char[] dst, int dstStart) {
    String.__checkBounds(length(), srcStart, srcEnd);
    String.__checkBounds(dst.length, dstStart, dstStart + (srcEnd - srcStart));
    String s = toString();
    while (srcStart < srcEnd) {
      dst[dstStart++] = s.charAt(srcStart++);
    }
  }

  public int indexOf(String x) {
    return data.indexOf(x);
  }

  public int indexOf(String x, int start) {
    return data.indexOf(x, start);
  }

  public StringBuilder insert(int index, boolean x) {
    return insert(index, String.valueOf(x));
  }

  public StringBuilder insert(int index, char x) {
    return insert(index, String.valueOf(x));
  }

  public StringBuilder insert(int index, char[] x) {
    return insert(index, String.valueOf(x));
  }

  public StringBuilder insert(int index, char[] x, int offset, int len) {
    return insert(index, String.valueOf(x, offset, len));
  }

  public StringBuilder insert(int index, CharSequence chars) {
    return insert(index, chars.toString());
  }

  public StringBuilder insert(int index, CharSequence chars, int start, int end) {
    return insert(index, chars.subSequence(start, end).toString());
  }

  public StringBuilder insert(int index, double x) {
    return insert(index, String.valueOf(x));
  }

  public StringBuilder insert(int index, float x) {
    return insert(index, String.valueOf(x));
  }

  public StringBuilder insert(int index, int x) {
    return insert(index, String.valueOf(x));
  }

  public StringBuilder insert(int index, long x) {
    return insert(index, String.valueOf(x));
  }

  public StringBuilder insert(int index, Object x) {
    return insert(index, String.valueOf(x));
  }

  public StringBuilder insert(int index, String x) {
    return replace(index, index, x);
  }

  public int lastIndexOf(String s) {
    return data.lastIndexOf(s);
  }

  public int lastIndexOf(String s, int start) {
    return data.lastIndexOf(s, start);
  }

  public int length() {
    return data.length();
  }

  public StringBuilder replace(int start, int end, String toInsert) {
    data = data.substring(0, start) + toInsert + data.substring(end);
    return this;
  }

  public StringBuilder reverse() {
    data = reverseString(data);
    return this;
  }

  /**
   * Warning! This method is <b>much</b> slower than the JRE implementation. If
   * you need to do character level manipulation, you are strongly advised to
   * use a char[] directly.
   */
  public void setCharAt(int index, char x) {
    replace(index, index + 1, String.valueOf(x));
  }

  public void setLength(int newLength) {
    int oldLength = length();
    if (newLength < oldLength) {
      delete(newLength, oldLength);
    } else if (newLength > oldLength) {
      append(new char[newLength - oldLength]);
    }
  }

  public CharSequence subSequence(int start, int end) {
    return data.substring(start, end);
  }

  public String substring(int begin) {
    return data.substring(begin);
  }

  public String substring(int begin, int end) {
    return data.substring(begin, end);
  }

  @Override
  public String toString() {
    return data;
  }

  public void trimToSize() {
  }

  private void appendValue(Object o) {
    data += o;
  }

  /**
   * Initializes all fields, instead of initializing them in the class body.
   * Initializing variables in the class body leads to hidden class changes and
   * need to be avoided in performance critical code.
   */
  private void init() {
    data = "";
  }
}
