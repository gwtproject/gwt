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

/** Tests (incorrect) equals semantics for Float. */
public final class FloatEqualsSemanticsTest extends GWTTestCase {

  @Override
  public String getModuleName() {
    return "com.google.gwt.emultest.EmulSuite";
  }

  public void testEquals() {
    // Semantics don't match JVM.
    if (TestUtils.isJvm()) {
      return;
    }

    // Should be assertTrue(Float.valueOf(Float.NaN).equals(Float.NaN));
    assertFalse(Float.valueOf(Float.NaN).equals(Float.NaN));
    // Should be assertFalse(Float.valueOf(0.0f).equals(-0.0f));
    assertTrue(Float.valueOf(0.0f).equals(-0.0f));
  }
}
