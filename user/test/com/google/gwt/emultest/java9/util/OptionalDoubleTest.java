/*
 * Copyright 2023 Google Inc.
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
package com.google.gwt.emultest.java9.util;

import com.google.gwt.emultest.java.util.EmulTestBase;

import java.util.OptionalDouble;
import java.util.stream.Stream;

/**
 * Tests for java.util.OptionalDouble Java 9 API emulation.
 */
public class OptionalDoubleTest extends EmulTestBase {
  public void testIfPresentOrElse() {
    int[] called = {0};
    OptionalDouble.of(10.0).ifPresentOrElse(value -> {
      assertEquals(10.0, value);
      called[0]++;
    }, () -> {
      fail("should not call empty action");
    });
    assertEquals(1, called[0]);
    called[0] = 0;
    OptionalDouble.empty().ifPresentOrElse(ignore -> {
      fail("Should not call present action");
    }, () -> called[0]++);
  }

  public void testStream() {
    assertEquals(0, OptionalDouble.empty().stream().count());
    assertEquals(1, OptionalDouble.of(10.0).stream().count());

    assertEquals(
        new double[] {10.0, 100.0, 1000.0},
        Stream.of(
            OptionalDouble.of(10.0),
            OptionalDouble.empty(),
            OptionalDouble.of(100.0),
            OptionalDouble.of(1000.0)
        ).flatMapToDouble(OptionalDouble::stream).toArray()
    );
  }
}
