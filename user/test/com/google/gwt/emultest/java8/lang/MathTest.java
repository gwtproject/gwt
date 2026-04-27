/*
 * Copyright 2010 Google Inc.
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

package com.google.gwt.emultest.java8.lang;

import com.google.gwt.junit.client.GWTTestCase;

import java.math.BigInteger;
import java.util.ArrayList;

/**
 * Tests for JRE emulation of java.lang.Math.
 */
public class MathTest extends GWTTestCase {

  private static final Integer[] ALL_INTEGER_CANDIDATES = getAllIntegerCandidates();
  private static final Long[] ALL_LONG_CANDIDATES = getAllLongCandidates();
  private static final double EPS = 1E-15;

  @Override
  public String getModuleName() {
    return "com.google.gwt.emultest.EmulSuite";
  }

  public void testAddExact() {
    for (int a : ALL_INTEGER_CANDIDATES) {
      for (int b : ALL_INTEGER_CANDIDATES) {
        BigInteger expectedResult = BigInteger.valueOf(a).add(BigInteger.valueOf(b));
        boolean expectedSuccess = fitsInInt(expectedResult);
        try {
          assertEquals(a + b, Math.addExact(a, b));
          assertTrue(expectedSuccess);
        } catch (ArithmeticException e) {
          assertFalse(expectedSuccess);
        }
      }
    }
  }

  public void testAddExactLongs() {
    for (long a : ALL_LONG_CANDIDATES) {
      for (long b : ALL_LONG_CANDIDATES) {
        BigInteger expectedResult = BigInteger.valueOf(a).add(BigInteger.valueOf(b));
        boolean expectedSuccess = fitsInLong(expectedResult);
        try {
          assertEquals(a + b, Math.addExact(a, b));
          assertTrue(expectedSuccess);
        } catch (ArithmeticException e) {
          assertFalse(expectedSuccess);
        }
      }
    }
  }

  public void testDecrementExact() {
    for (int a : ALL_INTEGER_CANDIDATES) {
      BigInteger expectedResult = BigInteger.valueOf(a).subtract(BigInteger.ONE);
      boolean expectedSuccess = fitsInInt(expectedResult);
      try {
        assertEquals(a - 1, Math.decrementExact(a));
        assertTrue(expectedSuccess);
      } catch (ArithmeticException e) {
        assertFalse(expectedSuccess);
      }
    }
  }

  public void testDecrementExactLong() {
    for (long a : ALL_LONG_CANDIDATES) {
      BigInteger expectedResult = BigInteger.valueOf(a).subtract(BigInteger.ONE);
      boolean expectedSuccess = fitsInLong(expectedResult);
      try {
        assertEquals(a - 1, Math.decrementExact(a));
        assertTrue(expectedSuccess);
      } catch (ArithmeticException e) {
        assertFalse(expectedSuccess);
      }
    }
  }

  public void testFloorDiv() {
    assertEquals(0, Math.floorDiv(0, 1));
    assertEquals(1, Math.floorDiv(4, 3));
    assertEquals(-2, Math.floorDiv(4, -3));
    assertEquals(-2, Math.floorDiv(-4, 3));
    assertEquals(1, Math.floorDiv(-4, -3));
    assertEquals(1, Math.floorDiv(Integer.MIN_VALUE, Integer.MIN_VALUE));
    assertEquals(1, Math.floorDiv(Integer.MAX_VALUE, Integer.MAX_VALUE));
    assertEquals(Integer.MIN_VALUE, Math.floorDiv(Integer.MIN_VALUE, 1));
    assertEquals(Integer.MAX_VALUE, Math.floorDiv(Integer.MAX_VALUE, 1));

    // special case
    assertEquals(Integer.MIN_VALUE, Math.floorDiv(Integer.MIN_VALUE, -1));

    assertThrowsArithmetic(() -> Math.floorDiv(1, 0));
  }

  public void testFloorDivLongs() {
    assertEquals(0L, Math.floorDiv(0L, 1L));
    assertEquals(1L, Math.floorDiv(4L, 3L));
    assertEquals(-2L, Math.floorDiv(4L, -3L));
    assertEquals(-2L, Math.floorDiv(-4L, 3L));
    assertEquals(1L, Math.floorDiv(-4L, -3L));
    assertEquals(1L, Math.floorDiv(Long.MIN_VALUE, Long.MIN_VALUE));
    assertEquals(1L, Math.floorDiv(Long.MAX_VALUE, Long.MAX_VALUE));
    assertEquals(Long.MIN_VALUE, Math.floorDiv(Long.MIN_VALUE, 1L));
    assertEquals(Long.MAX_VALUE, Math.floorDiv(Long.MAX_VALUE, 1L));

    // special case
    assertEquals(Long.MIN_VALUE, Math.floorDiv(Long.MIN_VALUE, -1));

    assertThrowsArithmetic(() -> Math.floorDiv(1L, 0L));
  }

  public void testFloorDivLongInt() {
    assertEquals(0L, Math.floorDiv(0L, 1));
    assertEquals(1L, Math.floorDiv(4L, 3));
    assertEquals((long) Integer.MAX_VALUE * 2 + 2,
            Math.floorDiv(Long.MIN_VALUE, Integer.MIN_VALUE));
    assertEquals((long) Integer.MAX_VALUE * 2 + 4,
            Math.floorDiv(Long.MAX_VALUE, Integer.MAX_VALUE));
    assertThrowsArithmetic(() -> Math.floorDiv(1L, 0));
  }

  public void testFloorMod() {
    assertEquals(0, Math.floorMod(0, 1));
    assertEquals(1, Math.floorMod(4, 3));
    assertEquals(-2, Math.floorMod(4, -3));
    assertEquals(2, Math.floorMod(-4, 3));
    assertEquals(-1, Math.floorMod(-4, -3));
    assertEquals(0, Math.floorMod(Integer.MIN_VALUE, Integer.MIN_VALUE));
    assertEquals(0, Math.floorMod(Integer.MAX_VALUE, Integer.MAX_VALUE));
    assertEquals(0, Math.floorMod(Integer.MIN_VALUE, 1));
    assertEquals(0, Math.floorMod(Integer.MAX_VALUE, 1));
    assertThrowsArithmetic(() -> Math.floorMod(1, 0));
  }

  public void testFloorModLongs() {
    assertEquals(0L, Math.floorMod(0L, 1L));
    assertEquals(1L, Math.floorMod(4L, 3L));
    assertEquals(-2L, Math.floorMod(4L, -3L));
    assertEquals(2L, Math.floorMod(-4L, 3L));
    assertEquals(-1L, Math.floorMod(-4L, -3L));
    assertEquals(0L, Math.floorMod(Long.MIN_VALUE, Long.MIN_VALUE));
    assertEquals(0L, Math.floorMod(Long.MAX_VALUE, Long.MAX_VALUE));
    assertEquals(0L, Math.floorMod(Long.MIN_VALUE, 1L));
    assertEquals(0L, Math.floorMod(Long.MAX_VALUE, 1L));
    assertThrowsArithmetic(() -> Math.floorMod(1L, 0L));
  }

  public void testFloorModLongInt() {
    assertEquals(0, Math.floorMod(0L, 1L));
    assertEquals(1, Math.floorMod(4L, 3L));
    assertEquals(0, Math.floorMod(Long.MIN_VALUE, Integer.MIN_VALUE));
    assertEquals(1, Math.floorMod(Long.MAX_VALUE, Integer.MAX_VALUE));
    assertThrowsArithmetic(() -> Math.floorMod(1L, 0));
  }

  public void testIncrementExact() {
    for (int a : ALL_INTEGER_CANDIDATES) {
      BigInteger expectedResult = BigInteger.valueOf(a).add(BigInteger.ONE);
      boolean expectedSuccess = fitsInInt(expectedResult);
      try {
        assertEquals(a + 1, Math.incrementExact(a));
        assertTrue(expectedSuccess);
      } catch (ArithmeticException e) {
        assertFalse(expectedSuccess);
      }
    }
  }

  public void testIncrementExactLong() {
    for (long a : ALL_LONG_CANDIDATES) {
      BigInteger expectedResult = BigInteger.valueOf(a).add(BigInteger.ONE);
      boolean expectedSuccess = fitsInLong(expectedResult);
      try {
        assertEquals(a + 1, Math.incrementExact(a));
        assertTrue(expectedSuccess);
      } catch (ArithmeticException e) {
        assertFalse(expectedSuccess);
      }
    }
  }

  public void testMultiplyExact() {
    for (int a : ALL_INTEGER_CANDIDATES) {
      for (int b : ALL_INTEGER_CANDIDATES) {
        BigInteger expectedResult = BigInteger.valueOf(a).multiply(BigInteger.valueOf(b));
        boolean expectedSuccess = fitsInInt(expectedResult);
        try {
          assertEquals(a * b, Math.multiplyExact(a, b));
          assertTrue(expectedSuccess);
        } catch (ArithmeticException e) {
          assertFalse(expectedSuccess);
        }
      }
    }
  }

  public void testMultiplyExactLongs() {
    for (long a : ALL_LONG_CANDIDATES) {
      for (long b : ALL_LONG_CANDIDATES) {
        BigInteger expectedResult = BigInteger.valueOf(a).multiply(BigInteger.valueOf(b));
        boolean expectedSuccess = fitsInLong(expectedResult);
        try {
          assertEquals(a * b, Math.multiplyExact(a, b));
          assertTrue(expectedSuccess);
        } catch (ArithmeticException e) {
          assertFalse(expectedSuccess);
        }
      }
    }
  }

  public void testMultiplyExactLongInt() {
    for (long a : ALL_LONG_CANDIDATES) {
      for (int b : ALL_INTEGER_CANDIDATES) {
        BigInteger expectedResult = BigInteger.valueOf(a).multiply(BigInteger.valueOf(b));
        boolean expectedSuccess = fitsInLong(expectedResult);
        try {
          assertEquals(a * b, Math.multiplyExact(a, b));
          assertTrue(expectedSuccess);
        } catch (ArithmeticException e) {
          assertFalse(expectedSuccess);
        }
      }
    }
  }

  public void testMultiplyFull() {
    assertEquals(1L, Long.MAX_VALUE
        - Math.multiplyFull(Integer.MAX_VALUE, Integer.MAX_VALUE) * 2 - 4L * Integer.MAX_VALUE);
    assertEquals(-1L, Long.MAX_VALUE
        - 2 * Math.multiplyFull(Integer.MIN_VALUE, Integer.MIN_VALUE));

    assertEquals(1L, hideFromCompiler(Long.MAX_VALUE)
        - Math.multiplyFull(Integer.MAX_VALUE, Integer.MAX_VALUE) * 2 - 4L * Integer.MAX_VALUE);
    assertEquals(-1L, hideFromCompiler(Long.MAX_VALUE)
        - 2 * Math.multiplyFull(Integer.MIN_VALUE, Integer.MIN_VALUE));
  }

  public void testCbrt() {
    assertEquals(-2, Math.cbrt(-8), EPS);
    assertEquals(Double.POSITIVE_INFINITY, Math.cbrt(Double.POSITIVE_INFINITY), EPS);
    assertEquals(Double.NEGATIVE_INFINITY, Math.cbrt(Double.NEGATIVE_INFINITY), EPS);

    assertEquals(-2, Math.cbrt(hideFromCompiler(-8)), EPS);
    assertEquals(Double.POSITIVE_INFINITY,
        Math.cbrt(hideFromCompiler(Double.POSITIVE_INFINITY)), EPS);
    assertEquals(Double.NEGATIVE_INFINITY,
        Math.cbrt(hideFromCompiler(Double.NEGATIVE_INFINITY)), EPS);
  }

  public void testNegateExact() {
    for (int a : ALL_INTEGER_CANDIDATES) {
      BigInteger expectedResult = BigInteger.valueOf(a).negate();
      boolean expectedSuccess = fitsInInt(expectedResult);
      try {
        assertEquals(-a, Math.negateExact(a));
        assertTrue(expectedSuccess);
      } catch (ArithmeticException e) {
        assertFalse(expectedSuccess);
      }
    }
  }

  public void testNegateExactLong() {
    for (long a : ALL_LONG_CANDIDATES) {
      BigInteger expectedResult = BigInteger.valueOf(a).negate();
      boolean expectedSuccess = fitsInLong(expectedResult);
      try {
        assertEquals(-a, Math.negateExact(a));
        assertTrue(expectedSuccess);
      } catch (ArithmeticException e) {
        assertFalse(expectedSuccess);
      }
    }
  }

  public void testSubtractExact() {
    for (int a : ALL_INTEGER_CANDIDATES) {
      for (int b : ALL_INTEGER_CANDIDATES) {
        BigInteger expectedResult = BigInteger.valueOf(a).subtract(BigInteger.valueOf(b));
        boolean expectedSuccess = fitsInInt(expectedResult);
        try {
          assertEquals(a - b, Math.subtractExact(a, b));
          assertTrue(expectedSuccess);
        } catch (ArithmeticException e) {
          assertFalse(expectedSuccess);
        }
      }
    }
  }

  public void testSubtractExactLongs() {
    for (long a : ALL_LONG_CANDIDATES) {
      for (long b : ALL_LONG_CANDIDATES) {
        BigInteger expectedResult = BigInteger.valueOf(a).subtract(BigInteger.valueOf(b));
        boolean expectedSuccess = fitsInLong(expectedResult);
        try {
          assertEquals(a - b, Math.subtractExact(a, b));
          assertTrue(expectedSuccess);
        } catch (ArithmeticException e) {
          assertFalse(expectedSuccess);
        }
      }
    }
  }

  public void testToIntExact() {
    final long[] longs = {0, -1, 1, Integer.MIN_VALUE, Integer.MAX_VALUE,
        Integer.MIN_VALUE - 1L, Integer.MAX_VALUE + 1L, Long.MIN_VALUE, Long.MAX_VALUE};
    for (long a : longs) {
      boolean expectedSuccess = (int) a == a;
      try {
        assertEquals((int) a, Math.toIntExact(a));
        assertTrue(expectedSuccess);
      } catch (ArithmeticException e) {
        assertFalse(expectedSuccess);
      }
    }
  }

  public void testLog1p() {
    assertEquals(Math.log(2), Math.log1p(1), EPS);
    assertEquals(1, Math.log1p(1E-30) * 1E30, EPS);
    assertEquals(-1, Math.log1p(-1E-30) * 1E30, EPS);

    assertEquals(Math.log(2), Math.log1p(hideFromCompiler(1)), EPS);
    assertEquals(1, Math.log1p(hideFromCompiler(1E-30)) * 1E30, EPS);
    assertEquals(-1, Math.log1p(hideFromCompiler(-1E-30)) * 1E30, EPS);
  }

  public void testExpM1() {
    assertEquals(Math.E - 1, Math.expm1(1), EPS);
    assertEquals(1, Math.expm1(1E-30) * 1E30, EPS);
    assertEquals(-1, Math.expm1(-1E-30) * 1E30, EPS);

    assertEquals(Math.E - 1, Math.expm1(hideFromCompiler(1)), EPS);
    assertEquals(1, Math.expm1(hideFromCompiler(1E-30)) * 1E30, EPS);
    assertEquals(-1, Math.expm1(hideFromCompiler(-1E-30)) * 1E30, EPS);
  }

  public void testIEEEremainderWithFolding() {
    assertEquals(1.0, Math.IEEEremainder(7.0, 3.0), EPS);
    assertEquals(-1.0, Math.IEEEremainder(8.0, 3.0), EPS);
    assertEquals(0.0, Math.IEEEremainder(6.0, 3.0), EPS);
    assertEquals(0.0, Math.IEEEremainder(9.0, 3.0), EPS);
    assertEquals(-1.0, Math.IEEEremainder(-7.0, 3.0), EPS);
    assertEquals(1.0, Math.IEEEremainder(7.0, -3.0), EPS);
    assertEquals(-1.0, Math.IEEEremainder(-7.0, -3.0), EPS);
    assertEquals(0.5, Math.IEEEremainder(2.5, 1.0), EPS);
    assertEquals(0.5, Math.IEEEremainder(2.5, 2.0), EPS);
    assertEquals(0.2, Math.IEEEremainder(5.2, 1.0), EPS);

    assertEquals(0.0, Math.IEEEremainder(4.5, 1.5), EPS);
    assertEquals(1.5, Math.IEEEremainder(7.5, 3.0), EPS);
    assertEquals(-1.5, Math.IEEEremainder(-7.5, -3.0), EPS);
    assertEquals(1.5, Math.IEEEremainder(7.5, -3.0), EPS);
    assertEquals(-1.5, Math.IEEEremainder(-7.5, 3.0), EPS);
    // Remainder with 0 divisor is NaN
    assertTrue(Double.isNaN(Math.IEEEremainder(5.0, 0.0)));
    // 0 divided by anything is 0
    assertEquals(0.0, Math.IEEEremainder(0.0, 2.0), EPS);
    // Infinity cases produce NaN
    assertTrue(Double.isNaN(Math.IEEEremainder(Double.POSITIVE_INFINITY, 2.0)));
    assertTrue(Double.isNaN(Math.IEEEremainder(Double.NEGATIVE_INFINITY, 2.0)));
    assertEquals(2, Math.IEEEremainder(2, Double.POSITIVE_INFINITY), EPS);
    assertEquals(2, Math.IEEEremainder(2, Double.NEGATIVE_INFINITY), EPS);
    // Any finite number divided by infinity -> same number
    assertEquals(5.0, Math.IEEEremainder(5.0, Double.POSITIVE_INFINITY), EPS);
    assertTrue(Double.isNaN(Math.IEEEremainder(Double.NaN, 2.0)));
    assertTrue(Double.isNaN(Math.IEEEremainder(5.0, Double.NaN)));
    assertTrue(Double.isNaN(Math.IEEEremainder(Double.NaN, Double.NaN)));
  }

  public void testIEEEremainder() {
    assertEquals(1.0, Math.IEEEremainder(hideFromCompiler(7.0), 3.0), EPS);
    assertEquals(-1.0, Math.IEEEremainder(hideFromCompiler(8.0), 3.0), EPS);
    assertEquals(0.0, Math.IEEEremainder(hideFromCompiler(6.0), 3.0), EPS);
    assertEquals(0.0, Math.IEEEremainder(hideFromCompiler(9.0), 3.0), EPS);
    assertEquals(-1.0, Math.IEEEremainder(hideFromCompiler(-7.0), 3.0), EPS);
    assertEquals(1.0, Math.IEEEremainder(hideFromCompiler(7.0), -3.0), EPS);
    assertEquals(-1.0, Math.IEEEremainder(hideFromCompiler(-7.0), -3.0), EPS);
    assertEquals(0.5, Math.IEEEremainder(hideFromCompiler(2.5), 1.0), EPS);
    assertEquals(0.5, Math.IEEEremainder(hideFromCompiler(2.5), 2.0), EPS);
    assertEquals(0.2, Math.IEEEremainder(hideFromCompiler(5.2), 1.0), EPS);

    assertEquals(0.0, Math.IEEEremainder(hideFromCompiler(4.5), 1.5), EPS);
    assertEquals(1.5, Math.IEEEremainder(hideFromCompiler(7.5), 3.0), EPS);
    assertEquals(-1.5, Math.IEEEremainder(hideFromCompiler(-7.5), -3.0), EPS);
    assertEquals(1.5, Math.IEEEremainder(hideFromCompiler(7.5), -3.0), EPS);
    assertEquals(-1.5, Math.IEEEremainder(hideFromCompiler(-7.5), 3.0), EPS);
    // Remainder with 0 divisor is NaN
    assertTrue(Double.isNaN(Math.IEEEremainder(hideFromCompiler(5.0), 0.0)));
    // 0 divided by anything is 0
    assertEquals(0.0, Math.IEEEremainder(hideFromCompiler(0.0), 2.0), EPS);
    // Infinity cases produce NaN
    assertTrue(Double.isNaN(Math.IEEEremainder(hideFromCompiler(Double.POSITIVE_INFINITY), 2.0)));
    assertTrue(Double.isNaN(Math.IEEEremainder(hideFromCompiler(Double.NEGATIVE_INFINITY), 2.0)));
    assertEquals(2, Math.IEEEremainder(hideFromCompiler(2), Double.POSITIVE_INFINITY), EPS);
    assertEquals(2, Math.IEEEremainder(hideFromCompiler(2), Double.NEGATIVE_INFINITY), EPS);
    // Any finite number divided by infinity -> same number
    assertEquals(5.0, Math.IEEEremainder(hideFromCompiler(5.0), Double.POSITIVE_INFINITY), EPS);
    assertTrue(Double.isNaN(Math.IEEEremainder(hideFromCompiler(Double.NaN), 2.0)));
    assertTrue(Double.isNaN(Math.IEEEremainder(hideFromCompiler(5.0), Double.NaN)));
    assertTrue(Double.isNaN(Math.IEEEremainder(hideFromCompiler(Double.NaN), Double.NaN)));
  }

  public void testUlpWithFolding() {
    double ulpEps = 1e-50;
    assertEquals(2.220446049250313E-16, Math.ulp(1.0), ulpEps);
    assertEquals(2.220446049250313E-16, Math.ulp(-1.0), ulpEps);
    assertEquals(4.440892098500626E-16, Math.ulp(2.0), ulpEps);
    assertEquals(4.440892098500626E-16, Math.ulp(-2.0), ulpEps);
    assertEquals(4.440892098500626E-16, Math.ulp(3.0), ulpEps);
    assertEquals(4.440892098500626E-16, Math.ulp(-3.0), ulpEps);
    assertEquals(1.99584030953472E292, Math.ulp(Double.MAX_VALUE), EPS);
    assertEquals(1.99584030953472E292, Math.ulp(-Double.MAX_VALUE), EPS);
    assertEquals(Double.POSITIVE_INFINITY, Math.ulp(Double.NEGATIVE_INFINITY), EPS);
    assertEquals(Double.POSITIVE_INFINITY, Math.ulp(Double.POSITIVE_INFINITY), EPS);
    assertTrue(Double.isNaN(Math.ulp(Double.NaN)));
    assertEquals(Double.MIN_VALUE, Math.ulp(Double.MIN_NORMAL), 0);
    assertEquals(Double.MIN_VALUE, Math.ulp(-Double.MIN_NORMAL), 0);
    assertEquals(Double.MIN_VALUE, Math.ulp(0.0), 0);
    assertEquals(Double.MIN_VALUE, Math.ulp(-0.0), 0);
    assertEquals(Double.MIN_VALUE, Math.ulp(8.289046E-317), 0);
    assertEquals(Double.MIN_VALUE, Math.ulp(-8.289046E-317), 0);

    assertEquals(1.1920929E-7f, Math.ulp(1.0f), EPS);
    assertEquals(1.1920929E-7f, Math.ulp(-1.0f), EPS);
    assertEquals(2.3841858E-7f, Math.ulp(2.0f), EPS);
    assertEquals(2.3841858E-7f, Math.ulp(-2.0f), EPS);
    assertEquals(2.3841858E-7f, Math.ulp(3.0f), EPS);
    assertEquals(2.3841858E-7f, Math.ulp(-3.0f), EPS);
    assertEquals(2.028241E31f, Math.ulp(Float.MAX_VALUE), EPS);
    assertEquals(2.028241E31f, Math.ulp(-Float.MAX_VALUE), EPS);
    assertEquals(Float.MIN_VALUE, Math.ulp(Float.MIN_VALUE), 0);
    assertEquals(Float.MIN_VALUE, Math.ulp(-Float.MIN_VALUE), 0);
    assertEquals(Float.MIN_VALUE, Math.ulp(Float.MIN_NORMAL), 0);
    assertEquals(Float.MIN_VALUE, Math.ulp(-Float.MIN_NORMAL), 0);
    assertEquals(Float.MIN_VALUE, Math.ulp(0.0f), 0);
    assertEquals(Float.MIN_VALUE, Math.ulp(-0.0f), 0);
  }

  public void testUlp() {
    double ulpEps = 1e-50;
    assertEquals(2.220446049250313E-16, Math.ulp(hideFromCompiler(1.0)), ulpEps);
    assertEquals(2.220446049250313E-16, Math.ulp(hideFromCompiler(-1.0)), ulpEps);
    assertEquals(4.440892098500626E-16, Math.ulp(hideFromCompiler(2.0)), ulpEps);
    assertEquals(4.440892098500626E-16, Math.ulp(hideFromCompiler(-2.0)), ulpEps);
    assertEquals(4.440892098500626E-16, Math.ulp(hideFromCompiler(3.0)), ulpEps);
    assertEquals(4.440892098500626E-16, Math.ulp(hideFromCompiler(-3.0)), ulpEps);
    assertEquals(1.99584030953472E292, Math.ulp(hideFromCompiler(Double.MAX_VALUE)), EPS);
    assertEquals(1.99584030953472E292, Math.ulp(hideFromCompiler(-Double.MAX_VALUE)), EPS);
    assertEquals(Double.POSITIVE_INFINITY, Math.ulp(hideFromCompiler(Double.NEGATIVE_INFINITY)), EPS);
    assertEquals(Double.POSITIVE_INFINITY, Math.ulp(hideFromCompiler(Double.POSITIVE_INFINITY)), EPS);
    assertTrue(Double.isNaN(Math.ulp(hideFromCompiler(Double.NaN))));
    assertEquals(Double.MIN_VALUE, Math.ulp(hideFromCompiler(Double.MIN_NORMAL)), 0);
    assertEquals(Double.MIN_VALUE, Math.ulp(hideFromCompiler(-Double.MIN_NORMAL)), 0);
    assertEquals(Double.MIN_VALUE, Math.ulp(hideFromCompiler(0.0)), 0);
    assertEquals(Double.MIN_VALUE, Math.ulp(hideFromCompiler(-0.0)), 0);
    assertEquals(Double.MIN_VALUE, Math.ulp(hideFromCompiler(8.289046E-317)), 0);
    assertEquals(Double.MIN_VALUE, Math.ulp(hideFromCompiler(-8.289046E-317)), 0);

    assertEquals(1.1920929E-7f, Math.ulp(hideFromCompiler(1.0f)), EPS);
    assertEquals(1.1920929E-7f, Math.ulp(hideFromCompiler(-1.0f)), EPS);
    assertEquals(2.3841858E-7f, Math.ulp(hideFromCompiler(2.0f)), EPS);
    assertEquals(2.3841858E-7f, Math.ulp(hideFromCompiler(-2.0f)), EPS);
    assertEquals(2.3841858E-7f, Math.ulp(hideFromCompiler(3.0f)), EPS);
    assertEquals(2.3841858E-7f, Math.ulp(hideFromCompiler(-3.0f)), EPS);
    assertEquals(2.028241E31f, Math.ulp(hideFromCompiler(Float.MAX_VALUE)), EPS);
    assertEquals(2.028241E31f, Math.ulp(hideFromCompiler(-Float.MAX_VALUE)), EPS);
    assertEquals(Float.MIN_VALUE, Math.ulp(hideFromCompiler(Float.MIN_VALUE)), 0);
    assertEquals(Float.MIN_VALUE, Math.ulp(hideFromCompiler(-Float.MIN_VALUE)), 0);
    assertEquals(Float.MIN_VALUE, Math.ulp(hideFromCompiler(Float.MIN_NORMAL)), 0);
    assertEquals(Float.MIN_VALUE, Math.ulp(hideFromCompiler(-Float.MIN_NORMAL)), 0);
    assertEquals(Float.MIN_VALUE, Math.ulp(hideFromCompiler(0.0f)), 0);
    assertEquals(Float.MIN_VALUE, Math.ulp(hideFromCompiler(-0.0f)), 0);
  }

  public void testHypot() {
    assertEquals(5.0, Math.hypot(3.0, 4.0), EPS);
    assertEquals(Double.POSITIVE_INFINITY, Math.hypot(1, Double.NEGATIVE_INFINITY), EPS);
    assertEquals(Double.POSITIVE_INFINITY, Math.hypot(Double.POSITIVE_INFINITY, 1), EPS);
    assertEquals(Double.POSITIVE_INFINITY, Math.hypot(Double.NaN, Double.NEGATIVE_INFINITY), EPS);

    assertEquals(5.0, Math.hypot(hideFromCompiler(3.0), 4.0), EPS);
    assertEquals(Double.POSITIVE_INFINITY, Math.hypot(hideFromCompiler(-1),
        Double.NEGATIVE_INFINITY), EPS);
    assertEquals(Double.POSITIVE_INFINITY, Math.hypot(hideFromCompiler(Double.POSITIVE_INFINITY),
        1), EPS);
    assertEquals(Double.POSITIVE_INFINITY, Math.hypot(hideFromCompiler(Double.NaN),
        Double.NEGATIVE_INFINITY), EPS);
  }

  public void testGetExponentWithFolding() {
    assertEquals(1, Math.getExponent(2d));
    assertEquals(1, Math.getExponent(3d));
    assertEquals(2, Math.getExponent(4d));
    assertEquals(-1023, Math.getExponent(0d));
    assertEquals(1023, Math.getExponent(Math.pow(2, 1023)));
    assertEquals(-1023, Math.getExponent(Math.pow(2, -1023)));
    assertEquals(1023, Math.getExponent(-Math.pow(2, 1023)));
    assertEquals(-1023, Math.getExponent(-Math.pow(2, -1023)));
    assertEquals(1024, Math.getExponent(Double.POSITIVE_INFINITY));
    assertEquals(1024, Math.getExponent(Double.NEGATIVE_INFINITY));
    assertEquals(1024, Math.getExponent(Double.NaN));

    assertEquals(2, Math.getExponent(4f));
    assertEquals(-127, Math.getExponent(0f));

    assertEquals(126, Math.getExponent((float) Math.pow(2, 126)));
    assertEquals(-126, Math.getExponent((float) Math.pow(2, -126)));
    assertEquals(126, Math.getExponent((float) -Math.pow(2, 126)));
    assertEquals(-126, Math.getExponent((float) -Math.pow(2, -126)));
    assertEquals(128, Math.getExponent((float) Math.pow(2, 500)));
    assertEquals(-127, Math.getExponent((float) Math.pow(2, -500)));
  }

  public void testGetExponent() {
    assertEquals(1, Math.getExponent(hideFromCompiler(2d)));
    assertEquals(1, Math.getExponent(hideFromCompiler(3d)));
    assertEquals(2, Math.getExponent(hideFromCompiler(4d)));
    assertEquals(-1023, Math.getExponent(hideFromCompiler(0d)));
    assertEquals(1023, Math.getExponent(hideFromCompiler(Math.pow(2, 1023))));
    assertEquals(-1023, Math.getExponent(hideFromCompiler(Math.pow(2, -1023))));
    assertEquals(1023, Math.getExponent(hideFromCompiler(-Math.pow(2, 1023))));
    assertEquals(-1023, Math.getExponent(hideFromCompiler(-Math.pow(2, -1023))));
    assertEquals(1024, Math.getExponent(hideFromCompiler(Double.POSITIVE_INFINITY)));
    assertEquals(1024, Math.getExponent(hideFromCompiler(Double.NEGATIVE_INFINITY)));
    assertEquals(1024, Math.getExponent(hideFromCompiler(Double.NaN)));

    assertEquals(2, Math.getExponent(hideFromCompiler(4f)));
    assertEquals(-127, Math.getExponent(hideFromCompiler(0f)));

    assertEquals(126, Math.getExponent(hideFromCompiler((float) Math.pow(2, 126))));
    assertEquals(-126, Math.getExponent(hideFromCompiler((float) Math.pow(2, -126))));
    assertEquals(126, Math.getExponent(hideFromCompiler((float) -Math.pow(2, 126))));
    assertEquals(-126, Math.getExponent(hideFromCompiler((float) -Math.pow(2, -126))));
    assertEquals(128, Math.getExponent(hideFromCompiler((float) Math.pow(2, 500))));
    assertEquals(-127, Math.getExponent(hideFromCompiler((float) Math.pow(2, -500))));
  }

  private <T> T hideFromCompiler(T value) {
    if (Math.random() < -1) {
      // Can never happen, but fools the compiler enough not to optimize this call.
      fail();
    }
    return value;
  }

  private void assertThrowsArithmetic(Runnable check) {
    try {
      check.run();
      fail("Should have failed");
    } catch (ArithmeticException ex) {
      // good
    }
  }

  private static boolean fitsInInt(BigInteger big) {
    return big.bitLength() < Integer.SIZE;
  }

  private static boolean fitsInLong(BigInteger big) {
    return big.bitLength() < Long.SIZE;
  }

  private static Integer[] getAllIntegerCandidates() {
    ArrayList<Integer> candidates = new ArrayList<Integer>();
    candidates.add(0);
    candidates.add(-1);
    candidates.add(1);
    candidates.add(Integer.MAX_VALUE / 2);
    candidates.add(Integer.MAX_VALUE / 2 - 1);
    candidates.add(Integer.MAX_VALUE / 2 + 1);
    candidates.add(Integer.MIN_VALUE / 2);
    candidates.add(Integer.MIN_VALUE / 2 - 1);
    candidates.add(Integer.MIN_VALUE / 2 + 1);
    candidates.add(Integer.MAX_VALUE - 1);
    candidates.add(Integer.MAX_VALUE);
    candidates.add(Integer.MIN_VALUE + 1);
    candidates.add(Integer.MIN_VALUE);
    return candidates.toArray(new Integer[candidates.size()]);
  }

  private static Long[] getAllLongCandidates() {
    ArrayList<Long> candidates = new ArrayList<Long>();

    for (Integer x : getAllIntegerCandidates()) {
      candidates.add(x.longValue());
    }

    candidates.add(Long.MAX_VALUE / 2);
    candidates.add(Long.MAX_VALUE / 2 - 1);
    candidates.add(Long.MAX_VALUE / 2 + 1);
    candidates.add(Long.MIN_VALUE / 2);
    candidates.add(Long.MIN_VALUE / 2 - 1);
    candidates.add(Long.MIN_VALUE / 2 + 1);
    candidates.add(Integer.MAX_VALUE + 1L);
    candidates.add(Long.MAX_VALUE - 1L);
    candidates.add(Long.MAX_VALUE);
    candidates.add(Integer.MIN_VALUE - 1L);
    candidates.add(Long.MIN_VALUE + 1L);
    candidates.add(Long.MIN_VALUE);

    return candidates.toArray(new Long[candidates.size()]);
  }
}
