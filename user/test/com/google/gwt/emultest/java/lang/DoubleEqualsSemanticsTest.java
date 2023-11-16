/*
 * Copyright 2019 Google Inc.
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
package com.google.gwt.emultest.java.lang;

import com.google.gwt.junit.client.GWTTestCase;
import com.google.gwt.testing.TestUtils;

/** Tests (incorrect) equals semantics for Double. */
public final class DoubleEqualsSemanticsTest extends GWTTestCase {

  @Override
  public String getModuleName() {
    return "com.google.gwt.emultest.EmulSuite";
  }

  public void testEquals() {
    // Semantics don't match JVM.
    if (TestUtils.isJvm()) {
      return;
    }

    // Should be assertTrue(Double.valueOf(Double.NaN).equals(Double.NaN));
    assertFalse(Double.valueOf(Double.NaN).equals(Double.NaN));
    // Should be assertFalse(Double.valueOf(0.0d).equals(-0.0d));
    assertTrue(Double.valueOf(0.0d).equals(-0.0d));

    // Also make sure the behavior doesn't change when Object trampoline is used.
    Object o;
    o = Double.NaN;
    // Should be assertTrue(o.equals(Double.NaN));
    assertFalse(o.equals(Double.NaN));
    o = 0.0d;
    // Should be assertFalse
    assertTrue(o.equals(-0.0d));
  }
}
