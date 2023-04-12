/*
 * Copyright 2020 Google Inc.
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

import java.util.AbstractMap;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Tests for java.util.Map Java 9 API emulation.
 */
public class MapTest extends EmulTestBase {

  public void testOf() {
    assertIsImmutableMapOf(Map.of());
    assertIsImmutableMapOf(Map.of("a", 1), "a");
    assertIsImmutableMapOf(
        Map.of("a", 1, "b", 2),
        "a", "b"
    );
    assertIsImmutableMapOf(
        Map.of("a", 1, "b", 2, "c", 3),
        "a", "b", "c"
    );
    assertIsImmutableMapOf(
        Map.of("a", 1, "b", 2, "c", 3, "d", 4),
        "a", "b", "c", "d"
    );
    assertIsImmutableMapOf(
        Map.of("a", 1, "b", 2, "c", 3, "d", 4, "e", 5),
        "a", "b", "c", "d", "e"
    );
    assertIsImmutableMapOf(
        Map.of("a", 1, "b", 2, "c", 3, "d", 4, "e", 5, "f", 6),
        "a", "b", "c", "d", "e", "f"
    );
    assertIsImmutableMapOf(
        Map.of("a", 1, "b", 2, "c", 3, "d", 4, "e", 5, "f", 6, "g", 7),
        "a", "b", "c", "d", "e", "f", "g"
    );
    assertIsImmutableMapOf(
        Map.of("a", 1, "b", 2, "c", 3, "d", 4, "e", 5, "f", 6, "g", 7, "h", 8),
        "a", "b", "c", "d", "e", "f", "g", "h"
    );
    assertIsImmutableMapOf(
        Map.of("a", 1, "b", 2, "c", 3, "d", 4, "e", 5, "f", 6, "g", 7, "h", 8, "i", 9),
        "a", "b", "c", "d", "e", "f", "g", "h", "i"
    );
    assertIsImmutableMapOf(
        Map.of("a", 1, "b", 2, "c", 3, "d", 4, "e", 5, "f", 6, "g", 7, "h", 8, "i", 9, "j", 10),
        "a", "b", "c", "d", "e", "f", "g", "h", "i", "j"
    );

    // ensure NullPointerException if either key or value are null for any param
    assertNPE("Map.of(1)", () -> Map.of(null, 1));
    assertNPE("Map.of(1)", () -> Map.of("a", null));
    assertNPE("Map.of(2)", () -> Map.of("a", 1, null, 2));
    assertNPE("Map.of(2)", () -> Map.of("a", 1, "b", null));
    assertNPE("Map.of(3)", () -> Map.of("a", 1, "b", 2, null, 3));
    assertNPE("Map.of(3)", () -> Map.of("a", 1, "b", 2, "c", null));
    assertNPE("Map.of(4)", () -> Map.of("a", 1, "b", 2, "c", 3, null, 4));
    assertNPE("Map.of(4)", () -> Map.of("a", 1, "b", 2, "c", 3, "d", null));
    assertNPE("Map.of(5)", () -> Map.of("a", 1, "b", 2, "c", 3, "d", 4, null, 5));
    assertNPE("Map.of(5)", () -> Map.of("a", 1, "b", 2, "c", 3, "d", 4, "e", null));
    assertNPE("Map.of(6)", () -> Map.of("a", 1, "b", 2, "c", 3, "d", 4, "e", 5, null, 6));
    assertNPE("Map.of(6)", () -> Map.of("a", 1, "b", 2, "c", 3, "d", 4, "e", 5, "f", null));
    assertNPE("Map.of(7)", () -> Map.of("a", 1, "b", 2, "c", 3, "d", 4, "e", 5, "f", 6,
        null, 7));
    assertNPE("Map.of(7)", () -> Map.of("a", 1, "b", 2, "c", 3, "d", 4, "e", 5, "f", 6,
        "g", null));
    assertNPE("Map.of(8)", () -> Map.of("a", 1, "b", 2, "c", 3, "d", 4, "e", 5, "f", 6,
        "g", 7, null, 8));
    assertNPE("Map.of(8)", () -> Map.of("a", 1, "b", 2, "c", 3, "d", 4, "e", 5, "f", 6,
        "g", 7, "h", null));
    assertNPE("Map.of(9)", () -> Map.of("a", 1, "b", 2, "c", 3, "d", 4, "e", 5, "f", 6,
        "g", 7, "h", 8, null, 9));
    assertNPE("Map.of(9)", () -> Map.of("a", 1, "b", 2, "c", 3, "d", 4, "e", 5, "f", 6,
        "g", 7, "h", 8, "i", null));
    assertNPE("Map.of(10)", () -> Map.of("a", 1, "b", 2, "c", 3, "d", 4, "e", 5, "f", 6,
        "g", 7, "h", 8, "i", 9, null, 10));
    assertNPE("Map.of(10)", () -> Map.of("a", 1, "b", 2, "c", 3, "d", 4, "e", 5, "f", 6,
        "g", 7, "h", 8, "i", 9, "j", null));

    // ensure IllegalArgumentException if any key is repeated
    assertIAE("Map.of(2)", () -> Map.of("a", 1, "a", 2));
    assertIAE("Map.of(3)", () -> Map.of("a", 1, "b", 2, "a", 3));
    assertIAE("Map.of(4)", () -> Map.of("a", 1, "b", 2, "c", 3, "a", 4));
    assertIAE("Map.of(5)", () -> Map.of("a", 1, "b", 2, "c", 3, "d", 4, "a", 5));
    assertIAE("Map.of(6)", () -> Map.of("a", 1, "b", 2, "c", 3, "d", 4, "e", 5, "a", 6));
    assertIAE("Map.of(7)", () -> Map.of("a", 1, "b", 2, "c", 3, "d", 4, "e", 5, "f", 6, "a", 7));
    assertIAE("Map.of(8)", () -> Map.of("a", 1, "b", 2, "c", 3, "d", 4, "e", 5, "f", 6, "g", 7,
        "a", 8));
    assertIAE("Map.of(9)", () -> Map.of("a", 1, "b", 2, "c", 3, "d", 4, "e", 5, "f", 6, "g", 7,
        "h", 8, "a", 9));
    assertIAE("Map.of(10)", () -> Map.of("a", 1, "b", 2, "c", 3, "d", 4, "e", 5, "f", 6, "g", 7,
        "h", 8, "i", 9, "a", 10));
  }

  protected static void assertIsImmutableMapOf(Map<String, Integer> map, String... contents) {
    assertEquals(contents.length, map.size());
    for (int i = 0; i < contents.length; i++) {
      assertTrue(map.containsKey(contents[i]));
      assertFalse(map.containsKey(contents[i] + "nope"));
      assertEquals(i + 1, (int) map.get(contents[i]));
    }

    // quick check that the map is immutable
    try {
      map.put("another item", 1);
      fail("Set should be unmodifiable: add(T)");
    } catch (UnsupportedOperationException ignored) {
      // success
    }

    if (contents.length > 1) {
      // Without any items, remove(T) defaults to iterating items present, so we only test from
      // present items
      try {
        map.remove(contents[0]);
        fail("Map should be unmodifiable: remove(T)");
      } catch (UnsupportedOperationException ignored) {
        // success
      }

      try {
        map.clear();
        fail("Set should be unmodifiable: clear()");
      } catch (UnsupportedOperationException ignored) {
        // expected
      }
    }
  }

  public void testEntry() {
    Map.Entry<String, String> entry = Map.entry("a", "b");

    assertEquals("a", entry.getKey());
    assertEquals("b", entry.getValue());

    try {
      entry.setValue("z");
      fail("Entry should be immutable: setValue");
    } catch (UnsupportedOperationException ignore) {
      // expected
    }

    assertNPE("Map.entry", () -> {
      Map.entry(null, "value");
    });
    assertNPE("Map.entry", () -> {
      Map.entry("key", null);
    });
  }

  public void testOfEntries() {
    Map<String, Integer> map = Map.ofEntries(
        Map.entry("a", 1),
        Map.entry("b", 2)
    );

    assertIsImmutableMapOf(map, "a", "b");

    // ensure NullPointerException if any entry is null, if any key is null, or value is null
    assertNPE("Map.ofEntries", () -> {
      Map.ofEntries(
          Map.entry("a", "b"),
          null
      );
    });
    assertNPE("Map.ofEntries", () -> {
      Map.ofEntries(
          Map.entry("a", "b"),
          Map.entry("c", null)
      );
    });
    assertNPE("Map.ofEntries", () -> {
      Map.ofEntries(
          Map.entry("a", "b"),
          Map.entry(null, "d")
      );
    });

    // ensure IllegalArgumentException if any pair has the same key (same or different value)
    assertIAE("Map.ofEntries", () -> {
      Map.ofEntries(
          Map.entry("a", "b"),
          Map.entry("a", "b")
      );
    });
    assertIAE("Map.ofEntries", () -> {
      Map.ofEntries(
          Map.entry("a", "b"),
          Map.entry("a", "c")
      );
    });
  }
}
