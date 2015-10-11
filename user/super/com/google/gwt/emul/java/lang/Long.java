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
 * Wraps a primitive <code>long</code> as an object.
 */
public final class Long extends Number implements Comparable<Long> {

  /**
   * Use nested class to avoid clinit on outer.
   */
  static class BoxedValues {
    // Box values according to JLS - between -128 and 127
    static Long[] boxedValues = new Long[256];
  }

  public static final long MAX_VALUE = 0x7fffffffffffffffL;
  public static final long MIN_VALUE = 0x8000000000000000L;
  public static final int SIZE = 64;
  public static final int BYTES = SIZE / Byte.SIZE;
  public static final Class<Long> TYPE = long.class;

  public static int bitCount(long i) {
    int high = (int) (i >> 32);
    int low = (int) i;
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

  public static int compareUnsigned(long a, long b) {
    return compare(a ^ MIN_VALUE, b ^ MIN_VALUE);
  }

  public static Long decode(String s) throws NumberFormatException {
    __Decode decode = __decodeNumberString(s);
    return valueOf(decode.payload, decode.radix);
  }

  public static long divideUnsigned(long dividend, long divisor) {
    if (divisor < 0) { // i.e., divisor >= 2^63:
      if (compare(dividend, divisor) < 0) {
        return 0; // dividend < divisor
      } else {
        return 1; // dividend >= divisor
      }
    }

    // Optimization - use signed division if dividend < 2^63
    if (dividend >= 0) {
      return dividend / divisor;
    }

    /*
     * Otherwise, approximate the quotient, check, and correct if necessary. Our approximation is
     * guaranteed to be either exact or one less than the correct value. This follows from fact
     * that floor(floor(x)/i) == floor(x/i) for any real x and integer i != 0. The proof is not
     * quite trivial.
     */
    long quotient = ((dividend >>> 1) / divisor) << 1;
    long rem = dividend - quotient * divisor;
    if (compare(rem, divisor) >= 0) {
      return quotient + 1;
    } else {
      return quotient;
    }
  }

  public static int hashCode(long l) {
    return (int) l;
  }

  public static long highestOneBit(long i) {
    int high = (int) (i >> 32);
    if (high != 0) {
      return ((long) Integer.highestOneBit(high)) << 32;
    } else {
      return Integer.highestOneBit((int) i);
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

  public static int numberOfLeadingZeros(long i) {
    int high = (int) (i >> 32);
    if (high != 0) {
      return Integer.numberOfLeadingZeros(high);
    } else {
      return Integer.numberOfLeadingZeros((int) i) + 32;
    }
  }

  public static int numberOfTrailingZeros(long i) {
    int low = (int) i;
    if (low != 0) {
      return Integer.numberOfTrailingZeros(low);
    } else {
      return Integer.numberOfTrailingZeros((int) (i >> 32)) + 32;
    }
  }

  public static long parseLong(String s) throws NumberFormatException {
    return parseLong(s, 10);
  }

  public static long parseLong(String s, int radix) throws NumberFormatException {
    return __parseAndValidateLong(s, radix);
  }

  public static long parseUnsignedLong(String s) throws NumberFormatException {
    return parseUnsignedLong(s, 10);
  }

  public static long parseUnsignedLong(String s, int radix) throws NumberFormatException {
    if (s == null)  {
      throw NumberFormatException.forNullInputString();
    }

    int len = s.length();
    if (len == 0 || s.charAt(0) == '-') {
      throw NumberFormatException.forInputString(s);
    }

    // Long.MAX_VALUE in Character.MAX_RADIX is 13 digits
    // Long.MAX_VALUE in base 10 is 19 digits
    if (len <= 12 || (radix == 10 && len <= 18)) {
      return parseLong(s, radix);
    }

    long first = parseLong(s.substring(0, len - 1), radix);
    int second = Character.digit(s.charAt(len - 1), radix);
    if (second < 0) {
      throw NumberFormatException.forInputString(s);
    }
    long result = first * radix + second;
    // check for overflow
    if (compareUnsigned(result, first) < 0) {
      throw NumberFormatException.forInputString(s);
    }
    return result;
  }

  public static long remainderUnsigned(long dividend, long divisor) {
    if (divisor < 0) { // i.e., divisor >= 2^63:
      if (compare(dividend, divisor) < 0) {
        return dividend; // dividend < divisor
      } else {
        return dividend - divisor; // dividend >= divisor
      }
    }

    // Optimization - use signed modulus if dividend < 2^63
    if (dividend >= 0) {
      return dividend % divisor;
    }

    /*
     * Otherwise, approximate the quotient, check, and correct if necessary. Our approximation is
     * guaranteed to be either exact or one less than the correct value. This follows from fact
     * that floor(floor(x)/i) == floor(x/i) for any real x and integer i != 0. The proof is not
     * quite trivial.
     */
    long quotient = ((dividend >>> 1) / divisor) << 1;
    long rem = dividend - quotient * divisor;
    if (compare(rem, divisor) >= 0) {
      return rem - divisor;
    } else {
      return rem;
    }
  }

  public static long reverse(long i) {
    int high = (int) (i >>> 32);
    int low = (int) i;
    return ((long) Integer.reverse(low) << 32)
        | (Integer.reverse(high) & 0xffffffffL);
  }

  public static long reverseBytes(long i) {
    int high = (int) (i >>> 32);
    int low = (int) i;
    return ((long) Integer.reverseBytes(low) << 32)
        | (Integer.reverseBytes(high) & 0xffffffffL);
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
    return toUnsignedString(value, 2);
  }

  public static String toHexString(long value) {
    return toUnsignedString(value, 16);
  }

  public static String toOctalString(long value) {
    return toUnsignedString(value, 8);
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

  public static String toUnsignedString(long value) {
    return toUnsignedString(value, 10);
  }

  public static String toUnsignedString(long value, int intRadix) {
    if (fitsInUint(value)) {
      return Integer.toUnsignedString((int) value, intRadix);
    }

    if (intRadix < Character.MIN_RADIX || intRadix > Character.MAX_RADIX) {
      intRadix = 10;
    }

    if (isPowerOfTwo(intRadix)) {
      return toPowerOfTwoUnsignedString(value, intRadix);
    }

    if (value >= 0) {
      return toString(value, intRadix);
    }

    // Convert radix to long before hand to avoid costly conversion on each iteration.
    long radix = intRadix;
    if (intRadix == 10) {
      long quotient = divideUnsigned(value, radix);
      int rem = (int) (value - quotient * radix);
      return toString(quotient) + rem;
    }

    int bufLen = intRadix < 8 ? 65 : 23; // Max chars in result (conservative)
    char[] buf = new char[bufLen];
    int cursor = bufLen;
    if (value < 0) {
      // Separate off the last digit using unsigned division. That will leave
      // a number that is nonnegative as a signed integer.
      long quotient = divideUnsigned(value, radix);
      int rem = (int) (value - quotient * radix);
      buf[--cursor] = Character.forDigit(rem, intRadix);
      value = quotient;
    }

    // Simple modulo/division approach
    while (value > 0) {
      buf[--cursor] = Character.forDigit((int) (value % radix), intRadix);
      value /= radix;
    }

    return new String(buf, cursor, buf.length - cursor);
  }

  private static String toPowerOfTwoUnsignedString(long value, int radix) {
    final int shift = log2(radix);

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

  private static boolean isPowerOfTwo(int x) {
    return (x & (x - 1)) == 0;
  }

  private static int log2(int x) {
    return (Integer.SIZE - 1) - Integer.numberOfLeadingZeros(x);
  }

  static boolean fitsInUint(long value) {
    int highBits = (int) (value >> 32);
    return highBits == 0;
  }

  public static Long valueOf(long i) {
    if (i > -129 && i < 128) {
      int rebase = (int) i + 128;
      Long result = BoxedValues.boxedValues[rebase];
      if (result == null) {
        BoxedValues.boxedValues[rebase] = result = new Long(i);
      }
      return result;
    }
    return new Long(i);
  }

  public static Long valueOf(String s) throws NumberFormatException {
    return valueOf(s, 10);
  }

  public static Long valueOf(String s, int radix) throws NumberFormatException {
    return valueOf(parseLong(s, radix));
  }

  private final transient long value;

  public Long(long value) {
    this.value = value;
  }

  public Long(String s) {
    this(parseLong(s));
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
