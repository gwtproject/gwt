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

import static javaemul.internal.InternalPreconditions.checkNotNull;

import javaemul.internal.JsUtils;
import jsinterop.annotations.JsMethod;

/**
 * Wraps a primitive <code>double</code> as an object.
 */
public final class Double extends Number implements Comparable<Double> {
  public static final double MAX_VALUE = 1.7976931348623157e+308;
  public static final double MIN_VALUE = 4.9e-324;
  public static final double MIN_NORMAL = 2.2250738585072014e-308;
  public static final int MAX_EXPONENT = 1023;
                             // ==Math.getExponent(Double.MAX_VALUE);
  public static final int MIN_EXPONENT = -1022;
                             // ==Math.getExponent(Double.MIN_NORMAL);

  public static final double NaN = 0d / 0d;
  public static final double NEGATIVE_INFINITY = -1d / 0d;
  public static final double POSITIVE_INFINITY = 1d / 0d;
  public static final int SIZE = 64;
  public static final int BYTES = SIZE / Byte.SIZE;
  public static final Class<Double> TYPE = double.class;

  public static int compare(double x, double y) {
    if (x < y) {
      return -1;
    }
    if (x > y) {
      return 1;
    }
    if (x == y) {
      return x == 0 ? Double.compare(1 / x, 1 / y) : 0;
    }

    if (isNaN(x)) {
      if (isNaN(y)) {
        return 0;
      } else {
        return 1;
      }
    } else {
      return -1;
    }
  }

  public static long doubleToLongBits(double value) {
    // Return a canonical NaN
    if (isNaN(value)) {
      return 0x7ff8000000000000L;
    }

    return doubleToRawLongBits(value);
  }

  // This method is kept private since it returns canonical NaN in Firefox.
  private static long doubleToRawLongBits(double value) {
    return JsUtils.doubleToRawLongBits(value);
  }

  public static int hashCode(double d) {
    return (int) d;
  }

  @JsMethod(namespace = "<window>")
  public static native boolean isFinite(double x);

  public static boolean isInfinite(double x) {
    return !isNaN(x) && !isFinite(x);
  }

  @JsMethod(namespace = "<window>")
  public static native boolean isNaN(double x);

  public static double longBitsToDouble(long bits) {
    return JsUtils.longBitsToDouble(bits);
  }

  public static double max(double a, double b) {
    return Math.max(a, b);
  }

  public static double min(double a, double b) {
    return Math.min(a, b);
  }

  public static double parseDouble(String s) throws NumberFormatException {
    return __parseAndValidateDouble(s);
  }

  public static double sum(double a, double b) {
    return a + b;
  }

  public static String toString(double b) {
    return String.valueOf(b);
  }

  public static Double valueOf(double d) {
    return new Double(d);
  }

  public static Double valueOf(String s) throws NumberFormatException {
    return new Double(s);
  }

  public Double(double value) {
    /*
     * Call to $create(value) must be here so that the method is referenced and not
     * pruned before new Double(value) is replaced by $create(value) by
     * RewriteConstructorCallsForUnboxedTypes.
     */
    $create(value);
  }

  public Double(String s) {
    /*
     * Call to $create(value) must be here so that the method is referenced and not
     * pruned before new Double(value) is replaced by $create(value) by
     * RewriteConstructorCallsForUnboxedTypes.
     */
    $create(s);
  }

  @Override
  public byte byteValue() {
    return (byte) doubleValue();
  }

  @Override
  public int compareTo(Double b) {
    return compare(doubleValue(), b.doubleValue());
  }

  @Override
  public double doubleValue() {
    return JsUtils.unsafeCastToDouble(checkNotNull(this));
  }

  @Override
  public boolean equals(Object o) {
    return checkNotNull(this) == o;
  }

  @Override
  public float floatValue() {
    return (float) doubleValue();
  }

  /**
   * Performance caution: using Double objects as map keys is not recommended.
   * Using double values as keys is generally a bad idea due to difficulty
   * determining exact equality. In addition, there is no efficient JavaScript
   * equivalent of <code>doubleToIntBits</code>. As a result, this method
   * computes a hash code by truncating the whole number portion of the double,
   * which may lead to poor performance for certain value sets if Doubles are
   * used as keys in a {@link java.util.HashMap}.
   */
  @Override
  public int hashCode() {
    return hashCode(doubleValue());
  }

  @Override
  public int intValue() {
    return (int) doubleValue();
  }

  public boolean isInfinite() {
    return isInfinite(doubleValue());
  }

  public boolean isNaN() {
    return isNaN(doubleValue());
  }

  @Override
  public long longValue() {
    return (long) doubleValue();
  }

  @Override
  public short shortValue() {
    return (short) doubleValue();
  }

  @Override
  public String toString() {
    return toString(doubleValue());
  }

  // CHECKSTYLE_OFF: Utility Methods for unboxed Double.
  protected static Double $create(double x) {
    return JsUtils.uncheckedCast(x);
  }

  protected static Double $create(String s) {
    return JsUtils.uncheckedCast(Double.parseDouble(s));
  }

  @JsMethod
  protected static boolean $isInstance(Object instance) {
    return "number".equals(JsUtils.typeOf(instance));
  }
  //CHECKSTYLE_ON: End utility methods
}
