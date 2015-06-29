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
package java.lang;

/**
 * Number_Helper contains GWT's JSNI calls to implement Numbers in the JRE. It can be super sourced
 * for j2cl.
 */
public class Number_Helper {
  private static Object floatRegex;

  private static Object unscaledRegex;

  // CHECKSTYLE_OFF: A special need to use unusual identifiers to avoid
  // introducing name collisions.
  public static native boolean __isValidDouble(String str) /*-{
    var floatRegex = @Number_Helper::floatRegex;
    if (!floatRegex) {
      // Disallow '.' with no digits on either side
      floatRegex = @Number_Helper::floatRegex =
          /^\s*[+-]?(NaN|Infinity|((\d+\.?\d*)|(\.\d+))([eE][+-]?\d+)?[dDfF]?)\s*$/;
    }
    return floatRegex.test(str);
  }-*/;

  public static native boolean __isNaN(double x) /*-{
    return isNaN(x);
  }-*/;

  public static native double __parseDouble(String str) /*-{
    return parseFloat(str);
  }-*/;

  public static native int __parseInt(String s, int radix) /*-{
    return parseInt(s, radix);
  }-*/;

  public static native boolean __isfinite(double x) /*-{
    return isFinite(x);
  }-*/;

  // CHECKSTYLE_ON
  public static native String toRadixString(int value, int radix) /*-{
    return value.toString(radix);
  }-*/;

  public static native String toUnsignedRadixString(int value, int radix) /*-{
    // ">>> 0" converts the value to unsigned number.
    return (value >>> 0).toString(radix);
  }-*/;

  public static native double parseUnscaled(String str) /*-{
    var unscaledRegex = @Number_Helper::unscaledRegex;
    if (!unscaledRegex) {
      unscaledRegex = @Number_Helper::unscaledRegex = /^[+-]?\d*$/i;
    }
    if (unscaledRegex.test(str)) {
      return parseInt(str, 10);
    } else {
      return Number.NaN;
    }
  }-*/;

  /**
   * Convert a double to a string with {@code digits} precision.  The resulting
   * string may still be in exponential notation.
   * 
   * @param d double value
   * @param digits number of digits of precision to include
   * @return non-localized string representation of {@code d}
   */
  public  static native String toPrecision(double d, int digits) /*-{
    return d.toPrecision(digits);
  }-*/;

  /**
   * Converts an integral double to an unsigned integer; ie 2^31 will be
   * returned as 0x80000000.
   *
   * @param val
   * @return val as an unsigned int
   */
  public static native int toUnsignedInt(double val) /*-{
    return val | 0;
  }-*/;

  public static native String[] parseMathContextValue(String val) /*-{
    return /^precision=(\d+)\ roundingMode=(\w+)$/.exec(val);
  }-*/;
}
