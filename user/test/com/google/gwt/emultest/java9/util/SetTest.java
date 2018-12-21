package com.google.gwt.emultest.java9.util;

import com.google.gwt.emultest.java.util.EmulTestBase;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Set;

public class SetTest extends EmulTestBase {
  public void testOf() {
    assertIsImmutableSetOf(Set.of());
    assertIsImmutableSetOf(Set.of("a"), "a");
    assertIsImmutableSetOf(
        Set.of("a", "b"),
        "a", "b"
    );
    assertIsImmutableSetOf(
        Set.of("a", "b", "c"),
        "a", "b", "c"
    );
    assertIsImmutableSetOf(
        Set.of("a", "b", "c", "d"),
        "a", "b", "c", "d"
    );
    assertIsImmutableSetOf(
        Set.of("a", "b", "c", "d", "e"),
        "a", "b", "c", "d", "e"
    );
    assertIsImmutableSetOf(
        Set.of("a", "b", "c", "d", "e", "f"),
        "a", "b", "c", "d", "e", "f"
    );
    assertIsImmutableSetOf(
        Set.of("a", "b", "c", "d", "e", "f", "g"),
        "a", "b", "c", "d", "e", "f", "g"
    );
    assertIsImmutableSetOf(
        Set.of("a", "b", "c", "d", "e", "f", "g", "h"),
        "a", "b", "c", "d", "e", "f", "g", "h"
    );
    assertIsImmutableSetOf(
        Set.of("a", "b", "c", "d", "e", "f", "g", "h", "i"),
        "a", "b", "c", "d", "e", "f", "g", "h", "i"
    );
    assertIsImmutableSetOf(
        Set.of("a", "b", "c", "d", "e", "f", "g", "h", "i", "j"),
        "a", "b", "c", "d", "e", "f", "g", "h", "i", "j"
    );
    assertIsImmutableSetOf(
        Set.of("a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k"),
        "a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k"
    );

    // ensure NPE if any element is null
    assertNPE("Set.of(1)", () -> Set.of((String) null));
    assertNPE("Set.of(2)", () -> Set.of("a", null));
    assertNPE("Set.of(3)", () -> Set.of("a", "b", null));
    assertNPE("Set.of(4)", () -> Set.of("a", "b", "c", null));
    assertNPE("Set.of(5)", () -> Set.of("a", "b", "c", "d", null));
    assertNPE("Set.of(6)", () -> Set.of("a", "b", "c", "d", "e", null));
    assertNPE("Set.of(7)", () -> Set.of("a", "b", "c", "d", "e", "f", null));
    assertNPE("Set.of(8)", () -> Set.of("a", "b", "c", "d", "e", "f", "g", null));
    assertNPE("Set.of(9)", () -> Set.of("a", "b", "c", "d", "e", "f", "g", "h", null));
    assertNPE("Set.of(10)", () -> Set.of("a", "b", "c", "d", "e", "f", "g", "h", "i", null));
    assertNPE("Set.of(...)", () -> Set.of("a", "b", "c", "d", "e", "f", "g", "h", "i", "j", null));

    // ensure IAE if any element is duplicated
    assertIAE("Set.of(2)", () -> Set.of("a", "a"));
    assertIAE("Set.of(3)", () -> Set.of("a", "b", "a"));
    assertIAE("Set.of(4)", () -> Set.of("a", "b", "c", "a"));
    assertIAE("Set.of(5)", () -> Set.of("a", "b", "c", "d", "a"));
    assertIAE("Set.of(6)", () -> Set.of("a", "b", "c", "d", "e", "a"));
    assertIAE("Set.of(7)", () -> Set.of("a", "b", "c", "d", "e", "f", "a"));
    assertIAE("Set.of(8)", () -> Set.of("a", "b", "c", "d", "e", "f", "g", "a"));
    assertIAE("Set.of(9)", () -> Set.of("a", "b", "c", "d", "e", "f", "g", "h", "a"));
    assertIAE("Set.of(10)", () -> Set.of("a", "b", "c", "d", "e", "f", "g", "h", "i", "a"));
    assertIAE("Set.of(...)", () -> Set.of("a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "a"));
  }

  protected static void assertIsImmutableSetOf(Set<String> set, String... contents) {

    assertEquals(contents.length, set.size());
    for (int i = 0; i < contents.length; i++) {
      assertTrue(set.contains(contents[i]));
      assertFalse(set.contains(contents[i] + "nope"));
    }


    // quick test that the set impl is sane, aside from the above
    if (contents.length == 0) {
      assertFalse(set.iterator().hasNext());
    } else {
      Iterator<String> itr = set.iterator();
      assertTrue(itr.hasNext());

      assertContains(contents, itr.next());

      assertEquals(contents.length > 1, itr.hasNext());
    }

    // quick check that the set is immutable
    try {
      set.add("another item");
      fail("Set should be unmodifiable: add(T)");
    } catch (UnsupportedOperationException ignored) {
      // success
    }


    if (contents.length > 1) {
      // Without any items, remove(T) defaults to iterating items present, so we only test from
      // present items
      try {
        set.remove(contents[0]);
        fail("Set should be unmodifiable: remove(T)");
      } catch (UnsupportedOperationException ignored) {
        // success
      }

      // This will actually succeed if the collection is empty, since the base implementation
      // invokes the iterator and removes each item - an empty collection does no iteration,
      // so the operation trivially passes
      try {
        set.clear();
        fail("Set should be unmodifiable: clear()");
      } catch (UnsupportedOperationException ignored) {
        // success
      }
    }
  }

  private static void assertContains(String[] contents, String value) {
    for (String item : contents) {
      if (item.equals(value)) {
        return;
      }
    }
    fail("Failed to find '" + value + "' in " + Arrays.toString(contents));
  }

  public static void assertNPE(String methodName, Runnable runnable) {
    try {
      runnable.run();
      fail("Expected NPE from calling " + methodName);
    } catch (NullPointerException ignored) {
      // expected
    }
  }

  public static void assertIAE(String methodName, Runnable runnable) {
    try {
      runnable.run();
      fail("Expected IAE from calling " + methodName);
    } catch (IllegalArgumentException ignored) {
      // expected
    }
  }
}
