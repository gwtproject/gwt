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
package com.google.gwt.emultest.java.util;

import com.google.gwt.testing.TestUtils;
import java.util.Arrays;

/** Tests {@link Arrays} (incorrect) Double semantics. */
public class ArraysDoubleSemanticsTest extends EmulTestBase {

  public void testEquals() throws Exception {
    // Semantics doesn't match JVM.
    if (TestUtils.isJvm()) {
      return;
    }

    assertTrue(Arrays.equals(new Double[] {-0.0d}, new Double[] {0.0d}));
    assertFalse(Arrays.equals(new Double[] {Double.NaN}, new Double[] {Double.NaN}));

    assertTrue(Arrays.equals(new double[] {-0.0d}, new double[] {0.0d}));
    assertFalse(Arrays.equals(new double[] {Double.NaN}, new double[] {Double.NaN}));
  }

  public void testBinarySearch() throws Exception {
    // Semantics doesn't match JVM.
    if (TestUtils.isJvm()) {
      return;
    }
    assertEquals(0, Arrays.binarySearch(new double[] {-0.0d}, 0.0d));
    assertEquals(0, Arrays.binarySearch(new double[] {0.0d}, Double.NaN));
    assertEquals(0, Arrays.binarySearch(new double[] {0.0d, Double.NaN}, Double.NaN));
  }
}
