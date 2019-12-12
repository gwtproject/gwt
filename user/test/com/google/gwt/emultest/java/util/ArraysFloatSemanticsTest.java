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

/** Tests {@link Arrays} (incorrect) Float semantics. */
public class ArraysFloatSemanticsTest extends EmulTestBase {

  public void testEquals() throws Exception {
    if (TestUtils.isJvm()) {
      return;
    }
    assertTrue(Arrays.equals(new Float[] {-0.0f}, new Float[] {0.0f}));
    assertFalse(Arrays.equals(new Float[] {Float.NaN}, new Float[] {Float.NaN}));

    assertTrue(Arrays.equals(new float[] {-0.0f}, new float[] {0.0f}));
    assertFalse(Arrays.equals(new float[] {Float.NaN}, new float[] {Float.NaN}));
  }

  public void testBinarySearch() throws Exception {
    if (TestUtils.isJvm()) {
      return;
    }
    assertEquals(0, Arrays.binarySearch(new float[] {-0.0f}, 0.0f));
    assertEquals(0, Arrays.binarySearch(new float[] {0.0f}, Float.NaN));
    assertEquals(0, Arrays.binarySearch(new float[] {0.0f, Float.NaN}, Float.NaN));
  }
}
