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
  public static final Class<Double> TYPE = double.class;

  public static int compare(double x, double y) {
    return DevirtualizedDouble.compare(x, y);
  }

  public static long doubleToLongBits(double value) {
    return DevirtualizedDouble.doubleToLongBits(value);
  }

  /**
   * @skip Here for shared implementation with Arrays.hashCode
   */
  public static int hashCode(double d) {
    return DevirtualizedDouble.hashCode(d);
  }

  public static boolean isInfinite(double x) {
    return DevirtualizedDouble.isInfinite(x);
  }

  public static boolean isNaN(double x) {
    return DevirtualizedDouble.isNaN(x);
  }

  public static double longBitsToDouble(long bits) {
    return DevirtualizedDouble.longBitsToDouble(bits);
  }

  public static double parseDouble(String s) throws NumberFormatException {
    return DevirtualizedDouble.parseDouble(s);
  }

  public static String toString(double d) {
    return DevirtualizedDouble.toString(d);
  }

  public static Double valueOf(double d) {
    return DevirtualizedDouble.valueOf(d);
  }

  public static Double valueOf(String s) throws NumberFormatException {
    return DevirtualizedDouble.valueOf(s);
  }

  public Double(double value) {
    /*
     * Call to $createDouble(value) must be here so that the method is referenced and not
     * pruned before new Double(value) is replaced by $createDouble(value) by
     * RewriteConstructorCallsForUnboxedTypes.
     */
    $createDouble(value);
  }

  public Double(String s) {
    /*
     * Call to $createDouble(value) must be here so that the method is referenced and not
     * pruned before new Double(value) is replaced by $createDouble(value) by
     * RewriteConstructorCallsForUnboxedTypes.
     */
    $createDouble(s);
  }

  @Override
  public byte byteValue() {
    return DevirtualizedDouble.byteValue(this);
  }

  @Override
  public int compareTo(Double d) {
    return DevirtualizedDouble.compareTo(this, d);
  }

  @Override
  public double doubleValue() {
    return DevirtualizedDouble.doubleValue(this);
  }

  @Override
  public boolean equals(Object o) {
    return DevirtualizedDouble.equals(this, o);
  }

  @Override
  public float floatValue() {
    return DevirtualizedDouble.floatValue(this);
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
    return DevirtualizedDouble.hashCode(this);
  }

  @Override
  public int intValue() {
    return DevirtualizedDouble.intValue(this);
  }

  public boolean isInfinite() {
    return DevirtualizedDouble.isInfinite(this);
  }

  public boolean isNaN() {
    return DevirtualizedDouble.isNaN(this);
  }

  @Override
  public long longValue() {
    return DevirtualizedDouble.longValue(this);
  }

  @Override
  public short shortValue() {
    return DevirtualizedDouble.shortValue(this);
  }

  @Override
  public String toString() {
    return DevirtualizedDouble.toString(this);
  }

  // CHECKSTYLE_OFF: Utility Methods for unboxed Double.
  static Double $createDouble(double x) {
    return DevirtualizedDouble.$createDouble(x);
  }

  static Double $createDouble(String s) {
    return DevirtualizedDouble.$createDouble(s);
  }
  // CHECKSTYLE_ON: End utility methods
}
