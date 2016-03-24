/*
 * Copyright 2007 Google Inc.
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

import java.io.Serializable;

import javaemul.internal.JsUtils;
import javaemul.internal.NativeRegExp;
import jsinterop.annotations.JsMethod;

/**
 * Abstract base class for numeric wrapper classes.
 */
public abstract class Number implements Serializable {

  /**
   * Stores a regular expression object to verify the format of float values.
   */
  private static NativeRegExp floatRegex;

  // CHECKSTYLE_OFF: A special need to use unusual identifiers to avoid
  // introducing name collisions.

  static class __Decode {
    public final String payload;
    public final int radix;

    public __Decode(int radix, String payload) {
      this.radix = radix;
      this.payload = payload;
    }
  }

  /**
   * Use nested class to avoid clinit on outer.
   */
  static class __ParseLong {
    /**
     * The number of digits (excluding minus sign and leading zeros) to process
     * at a time.  The largest value expressible in maxDigits digits as well as
     * the factor radix^maxDigits must be strictly less than 2^31.
     */
    private static final int[] maxDigitsForRadix = {-1, -1, // unused
      30, // base 2
      19, // base 3
      15, // base 4
      13, // base 5
      11, 11, // base 6-7
      10, // base 8
      9, 9, // base 9-10
      8, 8, 8, 8, // base 11-14
      7, 7, 7, 7, 7, 7, 7, // base 15-21
      6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, // base 22-35
      5 // base 36
    };

    /**
     * A table of values radix*maxDigitsForRadix[radix].
     */
    private static final int[] maxDigitsRadixPower = new int[37];

    /**
     * The largest number of digits (excluding minus sign and leading zeros) that
     * can fit into a long for a given radix between 2 and 36, inclusive.
     */
    private static final int[] maxLengthForRadix = {-1, -1, // unused
      63, // base 2
      40, // base 3
      32, // base 4
      28, // base 5
      25, // base 6
      23, // base 7
      21, // base 8
      20, // base 9
      19, // base 10
      19, // base 11
      18, // base 12
      18, // base 13
      17, // base 14
      17, // base 15
      16, // base 16
      16, // base 17
      16, // base 18
      15, // base 19
      15, // base 20
      15, // base 21
      15, // base 22
      14, // base 23
      14, // base 24
      14, // base 25
      14, // base 26
      14, // base 27
      14, // base 28
      13, // base 29
      13, // base 30
      13, // base 31
      13, // base 32
      13, // base 33
      13, // base 34
      13, // base 35
      13  // base 36
    };

    /**
     * A table of floor(MAX_VALUE / maxDigitsRadixPower).
     */
    private static final long[] maxValueForRadix = new long[37];

    static {
      for (int i = 2; i <= 36; i++) {
        maxDigitsRadixPower[i] = (int) Math.pow(i, maxDigitsForRadix[i]);
        maxValueForRadix[i] = Long.MAX_VALUE / maxDigitsRadixPower[i];
      }
    }
  }

  @JsMethod
  private static boolean $isInstance(Object instance) {
    return "number".equals(JsUtils.typeOf(instance)) || instanceOfJavaLangNumber(instance);
  }

  private static native boolean instanceOfJavaLangNumber(Object instance) /*-{
    // Note: The instanceof Number here refers to java.lang.Number in j2cl.
    return instance instanceof Number;
  }-*/;

  /**
   * Use nested class to avoid clinit on outer.
   */
  static class __ParseUnsignedLong {
    /**
     * The largest number of digits (excluding leading zeros) that
     * can fit into an unsigned long for a given radix between 2 and 36, inclusive.
     */
    private static final int[] maxLengthForRadix = {-1, -1, // unused
        64, // base 2
        41, // base 3
        32, // base 4
        28, // base 5
        25, // base 6
        23, // base 7
        22, // base 8
        21, // base 9
        20, // base 10
        19, // base 11
        18, // base 12
        18, // base 13
        17, // base 14
        17, // base 15
        16, // base 16
        16, // base 17
        16, // base 18
        16, // base 19
        15, // base 20
        15, // base 21
        15, // base 22
        15, // base 23
        14, // base 24
        14, // base 25
        14, // base 26
        14, // base 27
        14, // base 28
        14, // base 29
        14, // base 30
        13, // base 31
        13, // base 32
        13, // base 33
        13, // base 34
        13, // base 35
        13, // base 36
    };

    // Calculated as 0xffffffffffffffff / radix.
    private static final long[] maxDividendForRadix = {-1, -1, // unused
        9223372036854775807L, // base 2
        6148914691236517205L, // base 3
        4611686018427387903L, // base 4
        3689348814741910323L, // base 5
        3074457345618258602L, // base 6
        2635249153387078802L, // base 7
        2305843009213693951L, // base 8
        2049638230412172401L, // base 9
        1844674407370955161L, // base 10
        1676976733973595601L, // base 11
        1537228672809129301L, // base 12
        1418980313362273201L, // base 13
        1317624576693539401L, // base 14
        1229782938247303441L, // base 15
        1152921504606846975L, // base 16
        1085102592571150095L, // base 17
        1024819115206086200L, // base 18
        970881267037344821L, // base 19
        922337203685477580L, // base 20
        878416384462359600L, // base 21
        838488366986797800L, // base 22
        802032351030850070L, // base 23
        768614336404564650L, // base 24
        737869762948382064L, // base 25
        709490156681136600L, // base 26
        683212743470724133L, // base 27
        658812288346769700L, // base 28
        636094623231363848L, // base 29
        614891469123651720L, // base 30
        595056260442243600L, // base 31
        576460752303423487L, // base 32
        558992244657865200L, // base 33
        542551296285575047L, // base 34
        527049830677415760L, // base 35
        512409557603043100L, // base 36
    };

    // Calculated as 0xffffffffffffffff % radix.
    private static final int[] maxRemainderForRadix = {-1, -1, // unused
        1, // base 2
        0, // base 3
        3, // base 4
        0, // base 5
        3, // base 6
        1, // base 7
        7, // base 8
        6, // base 9
        5, // base 10
        4, // base 11
        3, // base 12
        2, // base 13
        1, // base 14
        0, // base 15
        15, // base 16
        0, // base 17
        15, // base 18
        16, // base 19
        15, // base 20
        15, // base 21
        15, // base 22
        5, // base 23
        15, // base 24
        15, // base 25
        15, // base 26
        24, // base 27
        15, // base 28
        23, // base 29
        15, // base 30
        15, // base 31
        31, // base 32
        15, // base 33
        17, // base 34
        15, // base 35
        15, // base 36
    };
  }

  /**
   * @skip
   *
   * This function will determine the radix that the string is expressed in
   * based on the parsing rules defined in the Javadocs for Integer.decode() and
   * invoke __parseAndValidateInt.
   */
  protected static int __decodeAndValidateInt(String s, int lowerBound,
      int upperBound) throws NumberFormatException {
    __Decode decode = __decodeNumberString(s);
    return __parseAndValidateInt(decode.payload, decode.radix, lowerBound,
        upperBound);
  }

  protected static __Decode __decodeNumberString(String s) {
    final boolean negative;
    if (s.startsWith("-")) {
      negative = true;
      s = s.substring(1);
    } else {
      negative = false;
      if (s.startsWith("+")) {
        s = s.substring(1);
      }
    }

    final int radix;
    if (s.startsWith("0x") || s.startsWith("0X")) {
      s = s.substring(2);
      radix = 16;
    } else if (s.startsWith("#")) {
      s = s.substring(1);
      radix = 16;
    } else if (s.startsWith("0")) {
      radix = 8;
    } else {
      radix = 10;
    }

    if (negative) {
      s = "-" + s;
    }
    return new __Decode(radix, s);
  }

  /**
   * @skip
   *
   * This function contains common logic for parsing a String as a floating-
   * point number and validating the range.
   */
  protected static double __parseAndValidateDouble(String s) throws NumberFormatException {
    if (!__isValidDouble(s)) {
      throw NumberFormatException.forInputString(s);
    }
    return parseFloat(s);
  }

  private static native double parseFloat(String str) /*-{
    return parseFloat(str);
  }-*/;

  /**
   * @skip
   *
   * This function contains common logic for parsing a String in a given radix
   * and validating the result.
   */
  protected static int __parseAndValidateInt(String s, int radix, int lowerBound, int upperBound)
      throws NumberFormatException {
    if (s == null) {
      throw NumberFormatException.forNullInputString();
    }
    if (radix < Character.MIN_RADIX || radix > Character.MAX_RADIX) {
      throw NumberFormatException.forRadix(radix);
    }

    int length = s.length();
    int startIndex = (length > 0) && (s.charAt(0) == '-' || s.charAt(0) == '+') ? 1 : 0;

    for (int i = startIndex; i < length; i++) {
      if (Character.digit(s.charAt(i), radix) == -1) {
        throw NumberFormatException.forInputString(s);
      }
    }

    int toReturn = JsUtils.parseInt(s, radix);
    // isTooLow is separated into its own variable to avoid a bug in BlackBerry OS 7. See
    // https://code.google.com/p/google-web-toolkit/issues/detail?id=7291.
    boolean isTooLow = toReturn < lowerBound;
    if (Double.isNaN(toReturn)) {
      throw NumberFormatException.forInputString(s);
    } else if (isTooLow || toReturn > upperBound) {
      throw NumberFormatException.forInputString(s);
    }

    return toReturn;
  }

  /**
   * @skip
   *
   * This function contains common logic for parsing a String in a given radix
   * and validating the result.
   */
  protected static long __parseAndValidateLong(final String original, int radix)
      throws NumberFormatException {
    String s = validateDecimalAndStripZeroes(original, radix, __ParseLong.maxLengthForRadix);
    return __unsafeParseLong(s, radix, original);
  }

  /**
   * Validates string representation of the decimal and strips leading zeroes.
   */
  private static String validateDecimalAndStripZeroes(final String original, int radix,
      int[] maxDigitsForRadix) {
    if (original == null) {
      throw NumberFormatException.forNullInputString();
    }
    if (radix < Character.MIN_RADIX || radix > Character.MAX_RADIX) {
      throw NumberFormatException.forRadix(radix);
    }

    String s = original;
    int length = s.length();
    boolean negative = false;
    if (length > 0) {
      char c = s.charAt(0);
      if (c == '-' || c == '+') {
        s = s.substring(1);
        length--;
        negative = (c == '-');
      }
    }
    if (length == 0) {
      throw NumberFormatException.forInputString(original);
    }

    // Strip leading zeros
    while (s.length() > 0 && s.charAt(0) == '0') {
      s = s.substring(1);
      length--;
    }

    // Immediately eject numbers that are too long -- this avoids more complex
    // overflow handling below
    if (length > maxDigitsForRadix[radix]) {
      throw NumberFormatException.forInputString(original);
    }

    // Validate the digits
    for (int i = 0; i < length; i++) {
      if (Character.digit(s.charAt(i), radix) == -1) {
        throw NumberFormatException.forInputString(original);
      }
    }

    return negative ? '-' + s : s;
  }

  private static long __unsafeParseLong(String s, int radix, final String original) {
    boolean negative = s.charAt(0) == '-';
    if (negative) {
      s = s.substring(1);
    }

    long toReturn = 0;
    int maxDigits = __ParseLong.maxDigitsForRadix[radix];
    long radixPower = __ParseLong.maxDigitsRadixPower[radix];
    long minValue = -__ParseLong.maxValueForRadix[radix];

    int length = s.length();
    boolean firstTime = true;
    int head = length % maxDigits;
    if (head > 0) {
      // accumulate negative numbers, as -Long.MAX_VALUE == Long.MIN_VALUE + 1
      // (in other words, -Long.MIN_VALUE overflows, see issue 7308)
      toReturn = - JsUtils.parseInt(s.substring(0, head), radix);
      s = s.substring(head);
      length -= head;
      firstTime = false;
    }

    while (length >= maxDigits) {
      head = JsUtils.parseInt(s.substring(0, maxDigits), radix);
      s = s.substring(maxDigits);
      length -= maxDigits;
      if (!firstTime) {
        // Check whether multiplying by radixPower will overflow
        if (toReturn < minValue) {
          throw NumberFormatException.forInputString(original);
        }
        toReturn *= radixPower;
      } else {
        firstTime = false;
      }
      toReturn -= head;
    }

    // A positive value means we overflowed Long.MIN_VALUE
    if (toReturn > 0) {
      throw NumberFormatException.forInputString(original);
    }

    if (!negative) {
      toReturn = -toReturn;
      // A negative value means we overflowed Long.MAX_VALUE
      if (toReturn < 0) {
        throw NumberFormatException.forInputString(original);
      }
    }
    return toReturn;
  }

  /**
   * @skip
   *
   * This function contains common logic for parsing a String in a given radix
   * and validating the result.
   */
  protected static long __parseAndValidateUnsignedLong(final String original, int radix) {
    String s = validateDecimalAndStripZeroes(original, radix, __ParseUnsignedLong.maxLengthForRadix);

    if (s.charAt(0) == '-') {
      throw NumberFormatException.forInputString(original);
    }

    // extract last digit of the number to parse it as signed long and
    // then combine them together.
    int lastDigit = -1;
    int length = s.length();
    if (length >= __ParseLong.maxLengthForRadix[radix]) {
      lastDigit = Character.digit(s.charAt(length - 1), radix);
      s = s.substring(0, length - 1);
    }

    long value = __unsafeParseLong(s, radix, original);
    if (lastDigit == -1) {
      return value;
    }

    // check for overflow
    long maxDividend = __ParseUnsignedLong.maxDividendForRadix[radix];
    long maxRemainder = __ParseUnsignedLong.maxRemainderForRadix[radix];
    // if toReturn < 0 then highest bit is set and we're gonna overflow
    if (value < 0 ||
        maxDividend < value ||
        (maxDividend == value && maxRemainder < lastDigit)) {
      throw NumberFormatException.forInputString(original);
    }
    return value * radix + lastDigit;
  }

  /**
   * @skip
   *
   * @param str
   * @return {@code true} if the string matches the float format, {@code false} otherwise
   */
  private static boolean __isValidDouble(String str) {
    if (floatRegex == null) {
      floatRegex = createFloatRegex();
    }
    return floatRegex.test(str);
  }

  private static native NativeRegExp createFloatRegex() /*-{
    return /^\s*[+-]?(NaN|Infinity|((\d+\.?\d*)|(\.\d+))([eE][+-]?\d+)?[dDfF]?)\s*$/;
  }-*/;

  // CHECKSTYLE_ON

  public byte byteValue() {
    return (byte) intValue();
  }

  public abstract double doubleValue();

  public abstract float floatValue();

  public abstract int intValue();

  public abstract long longValue();

  public short shortValue() {
    return (short) intValue();
  }
}
