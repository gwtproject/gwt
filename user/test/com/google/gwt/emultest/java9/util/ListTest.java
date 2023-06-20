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

import java.util.Iterator;
import java.util.List;

/**
 * Tests for java.util.List Java 9 API emulation.
 */
public class ListTest extends EmulTestBase {

  public void testOf() {
    assertIsImmutableListOf(List.of());
    assertIsImmutableListOf(List.of("a"), "a");
    assertIsImmutableListOf(
        List.of("a", "b"),
        "a", "b"
    );
    assertIsImmutableListOf(
        List.of("a", "b", "c"),
        "a", "b", "c"
    );
    assertIsImmutableListOf(
        List.of("a", "b", "c", "d"),
        "a", "b", "c", "d"
    );
    assertIsImmutableListOf(
        List.of("a", "b", "c", "d", "e"),
        "a", "b", "c", "d", "e"
    );
    assertIsImmutableListOf(
        List.of("a", "b", "c", "d", "e", "f"),
        "a", "b", "c", "d", "e", "f"
    );
    assertIsImmutableListOf(
        List.of("a", "b", "c", "d", "e", "f", "g"),
        "a", "b", "c", "d", "e", "f", "g"
    );
    assertIsImmutableListOf(
        List.of("a", "b", "c", "d", "e", "f", "g", "h"),
        "a", "b", "c", "d", "e", "f", "g", "h"
    );
    assertIsImmutableListOf(
        List.of("a", "b", "c", "d", "e", "f", "g", "h", "i"),
        "a", "b", "c", "d", "e", "f", "g", "h", "i"
    );
    assertIsImmutableListOf(
        List.of("a", "b", "c", "d", "e", "f", "g", "h", "i", "j"),
        "a", "b", "c", "d", "e", "f", "g", "h", "i", "j"
    );
    assertIsImmutableListOf(
        List.of("a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k"),
        "a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k"
    );

    // ensure that NPE is thrown if a value is null
    assertNPE("of", () -> List.of((String) null));
    assertNPE("of", () -> List.of("a", null));
    assertNPE("of", () -> List.of("a", "b", null));
    assertNPE("of", () -> List.of("a", "b", "c", null));
    assertNPE("of", () -> List.of("a", "b", "c", "d", null));
    assertNPE("of", () -> List.of("a", "b", "c", "d", "e", null));
    assertNPE("of", () -> List.of("a", "b", "c", "d", "e", "f", null));
    assertNPE("of", () -> List.of("a", "b", "c", "d", "e", "f", "g", null));
    assertNPE("of", () -> List.of("a", "b", "c", "d", "e", "f", "g", "h", null));
    assertNPE("of", () -> List.of("a", "b", "c", "d", "e", "f", "g", "h", "i", null));
    assertNPE("of", () -> List.of("a", "b", "c", "d", "e", "f", "g", "h", "i", "j", null));
  }

  public static void assertIsImmutableListOf(List<String> list, String... contents) {
    assertEquals(contents, list);

    // quick test that the list impl is sane
    if (contents.length == 0) {
      assertFalse(list.iterator().hasNext());
    } else {
      Iterator<String> itr = list.iterator();
      assertTrue(itr.hasNext());
      assertEquals(contents[0], itr.next());
      assertEquals(contents.length > 1, itr.hasNext());
    }

    // quick check that the list is immutable
    try {
      list.add("another item");
      fail("List should be unmodifiable: add(T)");
    } catch (UnsupportedOperationException ignored) {
      // success
    }

    try {
      list.remove(0);
      fail("List should be unmodifiable: remove(int)");
    } catch (UnsupportedOperationException ignored) {
      // success
    }

    // if any, remove an item actually in the list
    if (contents.length > 0) {
      // Without any items, remove(T) defaults to iterating items present, so we only test from
      // present items
      try {
        list.remove(contents[0]);
        fail("List should be unmodifiable: remove(T)");
      } catch (UnsupportedOperationException ignored) {
        // success
      }
    }

    // Remove an item that will not be in the list
    try {
      list.remove("not present");
      fail("List should be unmodifiable: remove(T)");
    } catch (UnsupportedOperationException ignored) {
      // success
    }

    try {
      list.clear();
      fail("List should be unmodifiable: clear()");
    } catch (UnsupportedOperationException ignored) {
      // success
    }
  }
}
