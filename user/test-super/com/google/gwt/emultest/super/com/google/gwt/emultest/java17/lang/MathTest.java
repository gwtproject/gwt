/*
 * Copyright 2025 GWT Project Authors
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
package com.google.gwt.emultest.java17.lang;

import com.google.gwt.emultest.java.util.EmulTestBase;

/**
 * Tests for java.lang.String Java 12 API emulation.
 */
public class MathTest extends EmulTestBase {

  public void testAbsExact() {
    assertEquals(Integer.MAX_VALUE, Math.absExact(hideFromCompiler(Integer.MAX_VALUE)));
    assertEquals(0, Math.absExact(hideFromCompiler(0)));
    assertEquals(1, Math.absExact(hideFromCompiler(-1)));
    assertThrowsArithmetic(() -> Math.absExact(hideFromCompiler(Integer.MIN_VALUE)));
    assertEquals(Long.MAX_VALUE, Math.absExact(hideFromCompiler(Long.MAX_VALUE)));
    assertEquals(0, Math.absExact(hideFromCompiler(0l)));
    assertEquals(1, Math.absExact(hideFromCompiler(-1l)));
    assertThrowsArithmetic(() -> Math.absExact(hideFromCompiler(Long.MIN_VALUE)));
  }

  public void testAbsExactWithFolding() {
    assertEquals(Integer.MAX_VALUE, Math.absExact(Integer.MAX_VALUE));
    assertEquals(0, Math.absExact(0));
    assertEquals(1, Math.absExact(-1));
    assertThrowsArithmetic(() -> Math.absExact(Integer.MIN_VALUE));
    assertEquals(Long.MAX_VALUE, Math.absExact(Long.MAX_VALUE));
    assertEquals(0, Math.absExact(0L));
    assertEquals(1, Math.absExact(-1L));
    assertThrowsArithmetic(() -> Math.absExact(Long.MIN_VALUE));
  }

  private void assertThrowsArithmetic(Runnable check) {
    try {
      check.run();
      fail("Should have failed");
    } catch (ArithmeticException ex) {
      // good
    }
  }
}
