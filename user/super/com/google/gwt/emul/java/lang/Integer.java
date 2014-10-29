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
 * Wraps a primitive <code>int</code> as an object.
 */
public final class Integer extends Number implements Comparable<Integer> {

  public static final int MAX_VALUE = 0x7fffffff;
  public static final int MIN_VALUE = 0x80000000;
  public static final int SIZE = 32;
  public static final Class<Integer> TYPE = int.class;

  /**
   * Use nested class to avoid clinit on outer.
   */
  private static class BoxedValues {
    // Box values according to JLS - between -128 and 127
    private static final Integer[] boxedValues = new Integer[256];
  }

  public static int bitCount(int i) {
    // Based on Henry S. Warren, Jr: "Hacker's Delight", 2nd edition, Figure 5-1.
    i = i - ((i >> 1) & 0x55555555);
    i = (i & 0x33333333) + ((i >> 2) & 0x33333333);
    i = (i + (i >> 4)) & 0x0f0f0f0f;
    i = i + (i >> 8);
    i = i + (i >> 16);
    return i & 0x0000003f;
  }

  public static int compare(int x, int y) {
    return x < y ? -1 : (x == y ? 0 : 1);
  }

  public static Integer decode(String s) throws NumberFormatException {
    return valueOf(__decodeAndValidateInt(s, MIN_VALUE, MAX_VALUE));
  }

  /**
   * @skip
   * 
   * Here for shared implementation with Arrays.hashCode
   */
  public static int hashCode(int i) {
    return i;
  }

  public static int highestOneBit(int i) {
    // Based on Henry S. Warren, Jr: "Hacker's Delight", 2nd edition, Figure 3-1.
    i |= (i >>  1);
    i |= (i >>  2);
    i |= (i >>  4);
    i |= (i >>  8);
    i |= (i >> 16);
    return i - (i >>> 1);
  }

  public static int lowestOneBit(int i) {
    // Based on Henry S. Warren, Jr: "Hacker's Delight", 2nd edition, Section 2-1.
    return i & -i;
  }

  public static int numberOfLeadingZeros(int i) {
    // Based on Henry S. Warren, Jr: "Hacker's Delight", 2nd edition, Figure 5-12.
    if (i < 0) {
      return 0;
    }

    if (i == 0) {
      return SIZE;
    }

    int y, m, n;

    y = -(i >> 16);
    m = (y >> 16) & 16;
    n = 16 - m;
    i = i >> m;

    y = i - 0x100;
    m = (y >> 16) & 8;
    n += m;
    i <<= m;

    y = i - 0x1000;
    m = (y >> 16) & 4;
    n += m;
    i <<= m;

    y = i - 0x4000;
    m = (y >> 16) & 2;
    n += m;
    i <<= m;

    y = i >> 14;
    m = y & ~(y >> 1);
    return n + 2 - m;
  }

  public static int numberOfTrailingZeros(int i) {
    // Based on Henry S. Warren, Jr: "Hacker's Delight", 2nd edition, Figure 5-18.
    if (i == 0) {
      return SIZE;
    }

    int n, y;

    n = 31;
    y = i << 16;
    if (y != 0) {
      n = n - 16;
      i = y;
    }
    y = i << 8;
    if (y != 0) {
      n = n - 8;
      i = y;
    }
    y = i << 4;
    if (y != 0) {
      n = n - 4;
      i = y;
    }
    y = i << 2;
    if (y != 0) {
      n = n - 2;
      i = y;
    }
    return n - ((i << 1) >>> 31);
  }

  public static int parseInt(String s) throws NumberFormatException {
    return parseInt(s, 10);
  }

  public static int parseInt(String s, int radix) throws NumberFormatException {
    return __parseAndValidateInt(s, radix, MIN_VALUE, MAX_VALUE);
  }

  public static int reverse(int i) {
    // Based on Henry S. Warren, Jr: "Hacker's Delight", 2nd edition, Figure 7-1.
    i = (i & 0x55555555) << 1 | (i >>> 1) & 0x55555555;
    i = (i & 0x33333333) << 2 | (i >>> 2) & 0x33333333;
    i = (i & 0x0f0f0f0f) << 4 | (i >>> 4) & 0x0f0f0f0f;
    i = (i << 24) | ((i & 0xff00) << 8) |
        ((i >>> 8) & 0xff00) | (i >>> 24);
    return i;
  }

  public static int reverseBytes(int i) {
    return ((i & 0xff) << 24) | ((i & 0xff00) << 8) | ((i & 0xff0000) >> 8)
        | ((i & 0xff000000) >>> 24);
  }

  public static int rotateLeft(int i, int distance) {
    // Based on Henry S. Warren, Jr: "Hacker's Delight", 2nd edition, Section 2-15.
    return (i << distance) | (i >>> -distance);
  }

  public static int rotateRight(int i, int distance) {
    // Based on Henry S. Warren, Jr: "Hacker's Delight", 2nd edition, Section 2-15.
    return (i >>> distance) | (i << -distance);
  }

  public static int signum(int i) {
    // Based on Henry S. Warren, Jr: "Hacker's Delight", 2nd edition, Section 2-8.
    return (i >> 31) | (-i >>> 31);
  }

  public static String toBinaryString(int value) {
    return toUnsignedRadixString(value, 2);
  }

  public static String toHexString(int value) {
    return toUnsignedRadixString(value, 16);
  }

  public static String toOctalString(int value) {
    return toUnsignedRadixString(value, 8);
  }

  public static String toString(int value) {
    return String.valueOf(value);
  }

  public static String toString(int value, int radix) {
    if (radix == 10 || radix < Character.MIN_RADIX || radix > Character.MAX_RADIX) {
      return String.valueOf(value);
    }
    return toRadixString(value, radix);
  }

  public static Integer valueOf(int i) {
    if (i > -129 && i < 128) {
      int rebase = i + 128;
      Integer result = BoxedValues.boxedValues[rebase];
      if (result == null) {
        result = BoxedValues.boxedValues[rebase] = new Integer(i);
      }
      return result;
    }
    return new Integer(i);
  }

  public static Integer valueOf(String s) throws NumberFormatException {
    return valueOf(s, 10);
  }

  public static Integer valueOf(String s, int radix) throws NumberFormatException {
    return valueOf(parseInt(s, radix));
  }

  private static native String toRadixString(int value, int radix) /*-{
    return value.toString(radix);
  }-*/;

  private static native String toUnsignedRadixString(int value, int radix) /*-{
    // ">>> 0" converts the value to unsigned number.
    return (value >>> 0).toString(radix);
  }-*/;

  private final transient int value;

  public Integer(int value) {
    this.value = value;
  }

  public Integer(String s) {
    value = parseInt(s);
  }

  @Override
  public byte byteValue() {
    return (byte) value;
  }

  public int compareTo(Integer b) {
    return compare(value, b.value);
  }

  @Override
  public double doubleValue() {
    return value;
  }

  @Override
  public boolean equals(Object o) {
    return (o instanceof Integer) && (((Integer) o).value == value);
  }

  @Override
  public float floatValue() {
    return value;
  }

  @Override
  public int hashCode() {
    return hashCode(value);
  }

  @Override
  public int intValue() {
    return value;
  }

  @Override
  public long longValue() {
    return value;
  }

  @Override
  public short shortValue() {
    return (short) value;
  }

  @Override
  public String toString() {
    return toString(value);
  }

}
