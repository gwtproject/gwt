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

import javaemul.internal.LongUtils;
import javaemul.internal.annotations.HasNoSideEffects;

/** Wraps a primitive <code>long</code> as an object. */
public final class Long extends Number implements Comparable<Long> {

  /** Use nested class to avoid clinit on outer. */
  static class BoxedValues {
    // Box values according to JLS - between -128 and 127
    static Long[] boxedValues = new Long[256];

    @HasNoSideEffects
    private static Long get(long l) {
      int rebase = (int) l + 128;
      Long result = BoxedValues.boxedValues[rebase];
      if (result == null) {
        result = BoxedValues.boxedValues[rebase] = new Long(l);
      }
      return result;
    }
  }

  public static final long MAX_VALUE = 0x7fffffffffffffffL;
  public static final long MIN_VALUE = 0x8000000000000000L;
  public static final int SIZE = 64;
  public static final int BYTES = SIZE / Byte.SIZE;
  public static final Class<Long> TYPE = long.class;

  public static int bitCount(long l) {
    int high = LongUtils.getHighBits(l);
    int low = (int) l;
    return Integer.bitCount(high) + Integer.bitCount(low);
  }

  public static int compare(long x, long y) {
    if (x < y) {
      return -1;
    } else if (x > y) {
      return 1;
    } else {
      return 0;
    }
  }

  public static Long decode(String s) throws NumberFormatException {
    __Decode decode = __decodeNumberString(s);
    return valueOf(decode.payload, decode.radix);
  }

  public static int hashCode(long l) {
    return LongUtils.getHighBits(l) ^ (int) l;
  }

  public static long highestOneBit(long l) {
    int high = LongUtils.getHighBits(l);
    if (high != 0) {
      return LongUtils.fromBits(0, Integer.highestOneBit(high));
    } else {
      return LongUtils.fromBits(Integer.highestOneBit((int) l), 0);
    }
  }

  public static long lowestOneBit(long i) {
    return i & -i;
  }

  public static long max(long a, long b) {
    return Math.max(a, b);
  }

  public static long min(long a, long b) {
    return Math.min(a, b);
  }

  public static int numberOfLeadingZeros(long l) {
    int high = LongUtils.getHighBits(l);
    if (high != 0) {
      return Integer.numberOfLeadingZeros(high);
    } else {
      return Integer.numberOfLeadingZeros((int) l) + 32;
    }
  }

  public static int numberOfTrailingZeros(long l) {
    int low = (int) l;
    if (low != 0) {
      return Integer.numberOfTrailingZeros(low);
    } else {
      return Integer.numberOfTrailingZeros(LongUtils.getHighBits(l)) + 32;
    }
  }

  public static long parseLong(String s) throws NumberFormatException {
    return parseLong(s, 10);
  }

  public static long parseLong(String s, int radix) throws NumberFormatException {
    return __parseAndValidateLong(s, radix);
  }

  public static long reverse(long l) {
    int high = LongUtils.getHighBits(l);
    int low = (int) l;
    return LongUtils.fromBits(Integer.reverse(high), Integer.reverse(low));
  }

  public static long reverseBytes(long l) {
    int high = LongUtils.getHighBits(l);
    int low = (int) l;
    return LongUtils.fromBits(Integer.reverseBytes(high), Integer.reverseBytes(low));
  }

  public static long rotateLeft(long i, int distance) {
    while (distance-- > 0) {
      i = i << 1 | ((i < 0) ? 1 : 0);
    }
    return i;
  }

  public static long rotateRight(long i, int distance) {
    long ui = i & MAX_VALUE; // avoid sign extension
    long carry = (i < 0) ? 0x4000000000000000L : 0; // MIN_VALUE rightshifted 1
    while (distance-- > 0) {
      long nextcarry = ui & 1;
      ui = carry | (ui >> 1);
      carry = (nextcarry == 0) ? 0 : 0x4000000000000000L;
    }
    if (carry != 0) {
      ui = ui | MIN_VALUE;
    }
    return ui;
  }

  public static int signum(long i) {
    if (i == 0) {
      return 0;
    } else if (i < 0) {
      return -1;
    } else {
      return 1;
    }
  }

  public static long sum(long a, long b) {
    return a + b;
  }

  public static String toBinaryString(long value) {
    return toPowerOfTwoUnsignedString(value, 1);
  }

  public static String toHexString(long value) {
    return toPowerOfTwoUnsignedString(value, 4);
  }

  public static String toOctalString(long value) {
    return toPowerOfTwoUnsignedString(value, 3);
  }

  public static String toString(long value) {
    return String.valueOf(value);
  }

  public static String toString(long value, int intRadix) {
    if (intRadix == 10 || intRadix < Character.MIN_RADIX || intRadix > Character.MAX_RADIX) {
      return String.valueOf(value);
    }

    int intValue = (int) value;
    if (intValue == value) {
      return Integer.toString(intValue, intRadix);
    }

    /*
     * If v is positive, negate it. This is the opposite of what one might expect. It is necessary
     * because the range of the negative values is strictly larger than that of the positive values:
     * there is no positive value corresponding to Long.MIN_VALUE.
     */
    boolean negative = value < 0;
    if (!negative) {
      value = -value;
    }

    int bufLen = intRadix < 8 ? 65 : 23; // Max chars in result (conservative)
    char[] buf = new char[bufLen];
    int cursor = bufLen;

    // Convert radix to long before hand to avoid costly conversion on each iteration.
    long radix = intRadix;
    do {
      long q = value / radix;
      buf[--cursor] = Character.forDigit((int) (radix * q - value));
      value = q;
    } while (value != 0);

    if (negative) {
      buf[--cursor] = '-';
    }

    return String.valueOf(buf, cursor, bufLen - cursor);
  }

  public static Long valueOf(long l) {
    if (l > -129 && l < 128) {
      return BoxedValues.get(l);
    }
    return new Long(l);
  }

  public static Long valueOf(String s) throws NumberFormatException {
    return valueOf(s, 10);
  }

  public static Long valueOf(String s, int radix) throws NumberFormatException {
    return valueOf(parseLong(s, radix));
  }

  private static String toPowerOfTwoUnsignedString(long value, int shift) {
    final int radix = 1 << shift;

    int highBits = LongUtils.getHighBits(value);
    if (highBits == 0) {
      return Integer.toUnsignedString((int) value, radix);
    }

    final int mask = radix - 1;
    final int bufSize = 64 / shift + 1;
    char[] buf = new char[bufSize];
    int pos = bufSize;
    do {
      buf[--pos] = Character.forDigit(((int) value) & mask);
      value >>>= shift;
    } while (value != 0);

    return String.valueOf(buf, pos, bufSize - pos);
  }

  private final transient long value;

  public Long(long value) {
    this.value = value;
  }

  public Long(String s) {
    value = parseLong(s);
  }

  @Override
  public byte byteValue() {
    return (byte) value;
  }

  @Override
  public int compareTo(Long b) {
    return compare(value, b.value);
  }

  @Override
  public double doubleValue() {
    return value;
  }

  @Override
  public boolean equals(Object o) {
    return (o instanceof Long) && (((Long) o).value == value);
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
    return (int) value;
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
