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
package java.internal;

/**
 * Helper methods to support numbers in the JRE.
 */
public class NumberHelper {
  /**
   * Stores a regular expression object to verify the format of float values.
   */
  private static Object floatRegex;

  /**
   * Stores a regular expression object to verify the format of unscaled bigdecimal values.
   */
  private static Object bigDecimalUnscaledRegex;

  public static boolean isValidDouble(String str) {
    if (floatRegex == null) {
      floatRegex = createFloatRegex();
    }
    return regexTest(floatRegex, str);
  }

  public static boolean isValidBigUnscaledDecimal(String str) {
    if (bigDecimalUnscaledRegex == null) {
      bigDecimalUnscaledRegex = createBigDecimalUnscaledRegex();
    }

    return regexTest(bigDecimalUnscaledRegex, str);
  }

  public static native double parseInt(String value, int base) /*-{
    return parseInt(value, base);
  }-*/;

  private static native Object createFloatRegex() /*-{
    return /^\s*[+-]?(NaN|Infinity|((\d+\.?\d*)|(\.\d+))([eE][+-]?\d+)?[dDfF]?)\s*$/;
  }-*/;

  private static native Object createBigDecimalUnscaledRegex() /*-{
    return /^[+-]?\d*$/i;
  }-*/;

  private static native boolean regexTest(Object regex, String value) /*-{
    return regex.test(value);
  }-*/;
}

