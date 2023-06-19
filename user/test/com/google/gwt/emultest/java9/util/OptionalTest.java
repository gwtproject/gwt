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

import java.util.Optional;
import java.util.stream.Stream;

/**
 * Tests for java.util.Optional Java 9 API emulation.
 */
public class OptionalTest extends EmulTestBase {
  public void testIfPresentOrElse() {
    int[] called = {0};
    Optional.of("value").ifPresentOrElse(value -> {
      assertEquals("value", value);
      called[0]++;
    }, () -> {
      fail("should not call empty action");
    });
    assertEquals(1, called[0]);
    called[0] = 0;
    Optional.empty().ifPresentOrElse(ignore -> {
      fail("Should not call present action");
    }, () -> called[0]++);
  }

  public void testOr() {
    Optional<String> or = Optional.of("value").or(() -> Optional.of("replacement"));
    assertTrue(or.isPresent());
    assertEquals("value", or.get());

    or = Optional.<String>empty().or(() -> Optional.of("replacement"));
    assertTrue(or.isPresent());
    assertEquals("replacement", or.get());

    or = Optional.of("value").or(() -> Optional.empty());
    assertTrue(or.isPresent());
    assertEquals("value", or.get());

    or = Optional.<String>empty().or(() -> Optional.empty());
    assertFalse(or.isPresent());
  }

  public void testStream() {
    assertEquals(0, Optional.empty().stream().count());
    assertEquals(1, Optional.of("foo").stream().count());

    assertEquals(
        new String[] {"a", "b", "c"},
        Stream.of(
            Optional.of("a"),
            Optional.empty(),
            Optional.of("b"),
            Optional.of("c")
        ).flatMap(Optional::stream).toArray(String[]::new)
    );
  }
}
