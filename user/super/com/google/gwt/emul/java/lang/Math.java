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

import static javaemul.internal.InternalPreconditions.checkCriticalArithmetic;

import javaemul.internal.JsUtils;
import jsinterop.annotations.JsMethod;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;

/**
 * Math utility methods and constants.
 */
public final class Math {

  public static final double E = 2.7182818284590452354;
  public static final double PI = 3.14159265358979323846;

  private static final double PI_OVER_180 = PI / 180.0;
  private static final double PI_UNDER_180 = 180.0 / PI;

  @JsMethod(namespace = JsPackage.GLOBAL, name = "Math.abs")
  public static native double abs(double x);

  @JsMethod(namespace = JsPackage.GLOBAL, name = "Math.abs")
  public static native float abs(float x);

  @JsMethod(namespace = JsPackage.GLOBAL, name = "Math.abs")
  public static native int abs(int x);

  public static long abs(long x) {
    return x < 0 ? -x : x;
  }

  public static int absExact(int v) {
    checkCriticalArithmetic(v != Integer.MIN_VALUE);
    return abs(v);
  }

  public static long absExact(long v) {
    checkCriticalArithmetic(v != Long.MIN_VALUE);
    return abs(v);
  }

  @JsMethod(namespace = JsPackage.GLOBAL, name = "Math.acos")
  public static native double acos(double x);

  @JsMethod(namespace = JsPackage.GLOBAL, name = "Math.asin")
  public static native double asin(double x);

  public static int addExact(int x, int y) {
    double r = (double) x + (double) y;
    checkCriticalArithmetic(isSafeIntegerRange(r));
    return (int) r;
  }

  public static long addExact(long x, long y) {
    long r = x + y;
    // "Hacker's Delight" 2-12 Overflow if both arguments have the opposite sign of the result
    checkCriticalArithmetic(((x ^ r) & (y ^ r)) >= 0);
    return r;
  }

  @JsMethod(namespace = JsPackage.GLOBAL, name = "Math.atan")
  public static native double atan(double x);

  @JsMethod(namespace = JsPackage.GLOBAL, name = "Math.atan2")
  public static native double atan2(double y, double x);

  @JsMethod(namespace = JsPackage.GLOBAL, name = "Math.cbrt")
  public static native double cbrt(double x);

  @JsMethod(namespace = JsPackage.GLOBAL, name = "Math.ceil")
  public static native double ceil(double x);

  public static double copySign(double magnitude, double sign) {
    return isNegative(sign) ? -abs(magnitude) : abs(magnitude);
  }

  private static boolean isNegative(double d) {
    return d < 0 || 1 / d < 0;
  }

  public static float copySign(float magnitude, float sign) {
    return (float) copySign((double) magnitude, (double) sign);
  }

  @JsMethod(namespace = JsPackage.GLOBAL, name = "Math.cos")
  public static native double cos(double x);

  @JsMethod(namespace = JsPackage.GLOBAL, name = "Math.cosh")
  public static native double cosh(double x);

  public static int decrementExact(int x) {
    checkCriticalArithmetic(x != Integer.MIN_VALUE);
    return x - 1;
  }

  public static long decrementExact(long x) {
    checkCriticalArithmetic(x != Long.MIN_VALUE);
    return x - 1;
  }

  @JsMethod(namespace = JsPackage.GLOBAL, name = "Math.exp")
  public static native double exp(double x);

  @JsMethod(namespace = JsPackage.GLOBAL, name = "Math.expm1")
  public static native double expm1(double d);

  @JsMethod(namespace = JsPackage.GLOBAL, name = "Math.floor")
  public static native double floor(double x);

  public static int floorDiv(int dividend, int divisor) {
    checkCriticalArithmetic(divisor != 0);
    // round down division if the signs are different and modulo not zero
    return ((dividend ^ divisor) >= 0 ? dividend / divisor : ((dividend + 1) / divisor) - 1);
  }

  public static long floorDiv(long dividend, long divisor) {
    checkCriticalArithmetic(divisor != 0);
    // round down division if the signs are different and modulo not zero
    return ((dividend ^ divisor) >= 0 ? dividend / divisor : ((dividend + 1) / divisor) - 1);
  }

  public static long floorDiv(long dividend, int divisor) {
    return floorDiv(dividend, (long) divisor);
  }

  public static int floorMod(int dividend, int divisor) {
    checkCriticalArithmetic(divisor != 0);
    return ((dividend % divisor) + divisor) % divisor;
  }

  public static long floorMod(long dividend, long divisor) {
    checkCriticalArithmetic(divisor != 0);
    return ((dividend % divisor) + divisor) % divisor;
  }

  public static int floorMod(long dividend, int divisor) {
    return (int) floorMod(dividend, (long) divisor);
  }

  @SuppressWarnings("CheckStyle.MethodName")
  public static double IEEEremainder(double v, double m) {
    double ratio = v / m;
    double closest = Math.ceil(ratio);
    double frac = Math.abs(closest - ratio);
    if (frac > 0.5 || frac == 0.5 && (closest % 2 != 0)) {
      closest = Math.floor(ratio);
    }
    // if closest == 0 and m == inf, avoid multiplication
    return closest == 0 ? v : v - m * closest;
  }

  public static int getExponent(double v) {
    int[] intBits = JsUtils.doubleToRawIntBits(v);
    return ((intBits[1] >> 20) & 2047) - Double.MAX_EXPONENT;
  }

  public static int getExponent(float v) {
    return ((JsUtils.floatToRawIntBits(v) >> 23) & 255) - Float.MAX_EXPONENT;
  }

  public static double ulp(double v) {
    if (!Double.isFinite(v)) {
      return Math.abs(v);
    }
    int exponent = Math.getExponent(v);
    if (exponent == -1023) {
      return Double.MIN_VALUE;
    }
    return Math.pow(2, exponent - 52);
  }

  public static float ulp(float v) {
    int exponent = Math.getExponent(v);
    if (exponent == -Float.MAX_EXPONENT) {
      return Float.MIN_VALUE;
    }
    return (float) Math.pow(2, exponent - 23);
  }

  @JsMethod(namespace = JsPackage.GLOBAL, name = "Math.hypot")
  public static native double hypot(double x, double y);

  public static int incrementExact(int x) {
    checkCriticalArithmetic(x != Integer.MAX_VALUE);
    return x + 1;
  }

  public static long incrementExact(long x) {
    checkCriticalArithmetic(x != Long.MAX_VALUE);
    return x + 1;
  }

  @JsMethod(namespace = JsPackage.GLOBAL, name = "Math.log")
  public static native double log(double x);

  @JsMethod(namespace = JsPackage.GLOBAL, name = "Math.log10")
  public static native double log10(double x);

  @JsMethod(namespace = JsPackage.GLOBAL, name = "Math.log1p")
  public static native double log1p(double x);

  @JsMethod(namespace = JsPackage.GLOBAL, name = "Math.max")
  public static native double max(double x, double y);

  @JsMethod(namespace = JsPackage.GLOBAL, name = "Math.max")
  public static native float max(float x, float y);

  @JsMethod(namespace = JsPackage.GLOBAL, name = "Math.max")
  public static native int max(int x, int y);

  public static long max(long x, long y) {
    return x > y ? x : y;
  }

  @JsMethod(namespace = JsPackage.GLOBAL, name = "Math.min")
  public static native double min(double x, double y);

  @JsMethod(namespace = JsPackage.GLOBAL, name = "Math.min")
  public static native float min(float x, float y);

  @JsMethod(namespace = JsPackage.GLOBAL, name = "Math.min")
  public static native int min(int x, int y);

  public static long min(long x, long y) {
    return x < y ? x : y;
  }

  public static long multiplyFull(int x, int y) {
    return (long) x * (long) y;
  }

  public static int multiplyExact(int x, int y) {
    double r = (double) x * (double) y;
    checkCriticalArithmetic(isSafeIntegerRange(r));
    return (int) r;
  }

  public static long multiplyExact(long x, int y) {
    if (y == -1) {
      return negateExact(x);
    }
    if (y == 0) {
      return 0;
    }
    long r = x * y;
    checkCriticalArithmetic(r / y == x);
    return r;
  }

  public static long multiplyExact(long x, long y) {
    if (y == -1) {
      return negateExact(x);
    }
    if (y == 0) {
      return 0;
    }
    long r = x * y;
    checkCriticalArithmetic(r / y == x);
    return r;
  }

  public static int negateExact(int x) {
    checkCriticalArithmetic(x != Integer.MIN_VALUE);
    return -x;
  }

  public static long negateExact(long x) {
    checkCriticalArithmetic(x != Long.MIN_VALUE);
    return -x;
  }

  @JsMethod(namespace = JsPackage.GLOBAL, name = "Math.pow")
  public static native double pow(double x, double exp);

  @JsMethod(namespace = JsPackage.GLOBAL, name = "Math.random")
  public static native double random();

  public static double rint(double x) {
    // Floating point has a mantissa with an accuracy of 52 bits so
    // any number bigger than 2^52 is effectively a finite integer value.
    // This case also filters out NaN and infinite values.
    if (abs(x) < (double) (1L << 52)) {
      double mod2 = x % 2;
      if ((mod2 == -1.5) || (mod2 == 0.5)) {
        x = floor(x);
      } else {
        x = round(x);
      }
    }
    return x;
  }

  public static long round(double x) {
    return (long) NativeMath.round(x);
  }

  public static int round(float x) {
    return (int) NativeMath.round(x);
  }

  public static int subtractExact(int x, int y) {
    double r = (double) x - (double) y;
    checkCriticalArithmetic(isSafeIntegerRange(r));
    return (int) r;
  }

  public static long subtractExact(long x, long y) {
    long r = x - y;
    // "Hacker's Delight" Overflow if the arguments have different signs and
    // the sign of the result is different than the sign of x
    checkCriticalArithmetic(((x ^ y) & (x ^ r)) >= 0);
    return r;
  }

  public static double scalb(double d, int scaleFactor) {
    if (scaleFactor >= 31 || scaleFactor <= -31) {
      return d * pow(2, scaleFactor);
    } else if (scaleFactor > 0) {
      return d * (1 << scaleFactor);
    } else if (scaleFactor == 0) {
      return d;
    } else {
      return d / (1 << -scaleFactor);
    }
  }

  public static float scalb(float f, int scaleFactor) {
    return (float) scalb((double) f, scaleFactor);
  }

  @JsMethod(namespace = JsPackage.GLOBAL, name = "Math.sign")
  public static native double signum(double d);

  public static float signum(float f) {
    return (float) signum((double) f);
  }

  @JsMethod(namespace = JsPackage.GLOBAL, name = "Math.sin")
  public static native double sin(double x);

  @JsMethod(namespace = JsPackage.GLOBAL, name = "Math.sinh")
  public static native double sinh(double x);

  @JsMethod(namespace = JsPackage.GLOBAL, name = "Math.sqrt")
  public static native double sqrt(double x);

  @JsMethod(namespace = JsPackage.GLOBAL, name = "Math.tan")
  public static native double tan(double x);

  @JsMethod(namespace = JsPackage.GLOBAL, name = "Math.tanh")
  public static native double tanh(double x);

  public static double toDegrees(double x) {
    return x * PI_UNDER_180;
  }

  public static int toIntExact(long x) {
    int ix = (int) x;
    checkCriticalArithmetic(ix == x);
    return ix;
  }

  public static double toRadians(double x) {
    return x * PI_OVER_180;
  }

  public static double nextAfter(double start, double direction) {
    // Simple case described by Javadoc:
    if (start == direction) {
      return direction;
    }

    // NaN special case, if either is NaN, return NaN.
    if (Double.isNaN(start) || Double.isNaN(direction)) {
      return Double.NaN;
    }

    // The javadoc 'special cases' for infinities and min_value are handled already by manipulating
    // the bits of the start value below. However, that approach used below doesn't work around
    // zeros - we have two zero values to deal with (positive and negative) with very different bit
    // representations (zero and Long.MIN_VALUE respectively).
    if (start == 0) {
      return direction > start ? Double.MIN_VALUE : -Double.MIN_VALUE;
    }

    // Convert to int bits and increment or decrement - the fact that two positive ieee754 float
    // values can be compared as ints (or two negative values, with the comparison inverted) means
    // that this trick works as naturally as A + 1 > A. NaNs and zeros were already handled above.
    long bits = Double.doubleToLongBits(start);
    bits += (direction > start) == (bits >= 0) ? 1 : -1;
    return Double.longBitsToDouble(bits);
  }

  public static float nextAfter(float start, double direction) {
    // Simple case described by Javadoc:
    if (start == direction) {
      return (float) direction;
    }

    // NaN special case, if either is NaN, return NaN.
    if (Float.isNaN(start) || Double.isNaN(direction)) {
      return Float.NaN;
    }
    // The javadoc 'special cases' for INFINITYs, MIN_VALUE, and MAX_VALUE are handled already by
    // manipulating the bits of the start value below. However, that approach used below doesn't
    // work around zeros - we have two zero values to deal with (positive and negative) with very
    // different bit representations (zero and Integer.MIN_VALUE respectively).
    if (start == 0) {
      return direction > start ? Float.MIN_VALUE : -Float.MIN_VALUE;
    }

    // Convert to int bits and increment or decrement - the fact that two positive ieee754 float
    // values can be compared as ints (or two negative values, with the comparison inverted) means
    // that this trick works as naturally as A + 1 > A. NaNs and zeros were already handled above.
    int bits = Float.floatToIntBits(start);
    bits += (direction > start) == (bits >= 0) ? 1 : -1;
    return Float.intBitsToFloat(bits);
  }

  public static double nextUp(double start) {
    return nextAfter(start, Double.POSITIVE_INFINITY);
  }

  public static float nextUp(float start) {
    return nextAfter(start, Double.POSITIVE_INFINITY);
  }

  public static double nextDown(double start) {
    return nextAfter(start, Double.NEGATIVE_INFINITY);
  }

  public static float nextDown(float start) {
    return nextAfter(start, Double.NEGATIVE_INFINITY);
  }

  private static boolean isSafeIntegerRange(double value) {
    return Integer.MIN_VALUE <= value && value <= Integer.MAX_VALUE;
  }

  @JsType(isNative = true, name = "Math", namespace = JsPackage.GLOBAL)
  private static class NativeMath {
    public static native double round(double x);
  }
}
