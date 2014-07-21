package com.google.gwt.core.client.impl;

import static com.google.gwt.core.client.impl.Coercions.ensureInt;

import com.google.gwt.junit.client.GWTTestCase;

import java.util.Random;

/**
 * Tests for {@link Coercions}.
 */
public class CoercionsTest extends GWTTestCase {
  @Override
  public String getModuleName() {
    return "com.google.gwt.core.Core";
  }

  @SuppressWarnings("NumericOverflow")
  public void testEnsureInt() {
    Random random = new Random();
    // This variable holds value of 1.
    // We use it to prevent static compiler optimizations.
    int _1 = random.nextInt(1) + 1;
    assertEquals(Integer.MIN_VALUE, ensureInt(Integer.MAX_VALUE + _1));
  }
}
