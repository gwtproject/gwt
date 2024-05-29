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
package com.google.gwt.emultest.java10.util;

import static com.google.gwt.emultest.java9.util.SetTest.assertIsImmutableSetOf;

import com.google.gwt.emultest.java.util.EmulTestBase;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Tests for java.util.Set Java 10 API emulation.
 */
public class SetTest extends EmulTestBase {
  public void testCopyOf() {
    assertIsImmutableSetOf(Set.copyOf(Set.of("a", "b")), "a", "b");
    assertIsImmutableSetOf(Set.copyOf(Arrays.asList("a", "b")), "a", "b");

    HashSet<String> hashSet = new HashSet<>();
    hashSet.add("a");
    hashSet.add("b");
    Set<String> copy = Set.copyOf(hashSet);
    assertIsImmutableSetOf(copy, "a", "b");

    // verify that mutating the original has no effect on the copy
    hashSet.add("c");
    assertEquals(2, copy.size());
    assertFalse(copy.contains("c"));

    hashSet.remove("a");
    assertEquals(2, copy.size());
    assertTrue(copy.contains("a"));

    // ensure that null value result in a NPE
    try {
      Set.copyOf(Arrays.asList("a", null));
      fail("Expected NullPointerException from null item in collection passed to copyOf");
    } catch (NullPointerException ignored) {
      // expected
    }

    // ensure that duplicate values result in smaller output
    assertIsImmutableSetOf(Set.copyOf(Arrays.asList("a", "a")), "a");
  }
}
