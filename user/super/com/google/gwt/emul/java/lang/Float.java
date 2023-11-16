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

import javaemul.internal.JsUtils;

/**
 * Wraps a primitive <code>float</code> as an object.
 */
public final class Float extends Number implements Comparable<Float> {
  public static final float MAX_VALUE = 3.4028235e+38f;
  public static final float MIN_VALUE = 1.4e-45f;
  public static final int MAX_EXPONENT = 127;
  public static final int MIN_EXPONENT = -126;
  public static final float MIN_NORMAL = 1.1754943508222875E-38f;
  public static final float NaN = 0f / 0f;
  public static final float NEGATIVE_INFINITY = -1f / 0f;
  public static final float POSITIVE_INFINITY = 1f / 0f;
  public static final int SIZE = 32;
  public static final int BYTES = SIZE / Byte.SIZE;
  public static final Class<Float> TYPE = float.class;

  private static final long POWER_31_INT = 2147483648L;

  public static int compare(float x, float y) {
    return Double.compare(x, y);
  }

  public static int floatToIntBits(float value) {
    // Return a canonical NaN
    if (isNaN(value)) {
      return 0x7fc00000;
    }

    return floatToRawIntBits(value);
  }

  // This method is kept private since it returns canonical NaN in Firefox.
  private static int floatToRawIntBits(float value) {
    return JsUtils.floatToRawIntBits(value);
  }

  /**
   * @param f
   * @return hash value of float (currently just truncated to int)
   */
  public static int hashCode(float f) {
    return (int) f;
  }

  public static float intBitsToFloat(int bits) {
    return JsUtils.intBitsToFloat(bits);
  }

  public static boolean isFinite(float x) {
    return Double.isFinite(x);
  }

  public static boolean isInfinite(float x) {
    return Double.isInfinite(x);
  }

  public static boolean isNaN(float x) {
    return Double.isNaN(x);
  }

  public static float max(float a, float b) {
    return Math.max(a, b);
  }

  public static float min(float a, float b) {
    return Math.min(a, b);
  }

  public static float parseFloat(String s) throws NumberFormatException {
    double doubleValue = __parseAndValidateDouble(s);
    if (doubleValue > Float.MAX_VALUE) {
      return Float.POSITIVE_INFINITY;
    } else if (doubleValue < -Float.MAX_VALUE) {
      return Float.NEGATIVE_INFINITY;
    }
    return (float) doubleValue;
  }

  public static float sum(float a, float b) {
    return a + b;
  }

  public static String toString(float b) {
    return String.valueOf(b);
  }

  public static Float valueOf(float f) {
    return new Float(f);
  }

  public static Float valueOf(String s) throws NumberFormatException {
    return new Float(s);
  }

  private final transient float value;

  public Float(double value) {
    this.value = (float) value;
  }

  public Float(float value) {
    this.value = value;
  }

  public Float(String s) {
    value = parseFloat(s);
  }

  @Override
  public byte byteValue() {
    return (byte) value;
  }

  @Override
  public int compareTo(Float b) {
    return compare(value, b.value);
  }

  @Override
  public double doubleValue() {
    return value;
  }

  @Override
  public boolean equals(Object o) {
    // Make sure Float follow the same semantic as Double for consistency.
    return (o instanceof Float) && Double.valueOf(value).equals(Double.valueOf(((Float) o).value));
  }

  @Override
  public float floatValue() {
    return value;
  }

  /**
   * Performance caution: using Float objects as map keys is not recommended.
   * Using floating point values as keys is generally a bad idea due to
   * difficulty determining exact equality. In addition, there is no efficient
   * JavaScript equivalent of <code>floatToIntBits</code>. As a result, this
   * method computes a hash code by truncating the whole number portion of the
   * float, which may lead to poor performance for certain value sets if Floats
   * are used as keys in a {@link java.util.HashMap}.
   */
  @Override
  public int hashCode() {
    return hashCode(value);
  }

  @Override
  public int intValue() {
    return (int) value;
  }

  public boolean isInfinite() {
    return isInfinite(value);
  }

  public boolean isNaN() {
    return isNaN(value);
  }

  @Override
  public long longValue() {
    return (long) value;
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
