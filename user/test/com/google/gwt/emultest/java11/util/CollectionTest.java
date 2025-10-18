package com.google.gwt.emultest.java11.util;

import com.google.gwt.emultest.java.util.EmulTestBase;

import java.util.List;
import java.util.Set;

/**
 * Tests for java.util.Collection Java 11 API emulation.
 * <p>
 * We can't easily test Collection methods themselves, so trying a few basic implementations.
 */
public class CollectionTest extends EmulTestBase {

  public void testListArrayOfMethodRef() {
    String[] arr = List.of("a", "b").toArray(String[]::new);
    assertEquals(2, arr.length);
    assertTrue(arr instanceof String[]);

    assertEquals("a", arr[0]);
    assertEquals("b", arr[1]);
  }

  public void testSetArrayOfMethodRef() {
    String[] arr = Set.of("a", "b").toArray(String[]::new);
    assertEquals(2, arr.length);
    assertTrue(arr instanceof String[]);

    // Order isn't guaranteed in a Set, so we compare against a known array, which will use the
    // same traversal as long as nothing else has changed
    Object[] objArr = Set.of("a", "b").toArray();
    for (int i = 0; i < arr.length; i++) {
      assertEquals(objArr[i], arr[i]);
    }
  }
}
