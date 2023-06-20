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

import static com.google.gwt.emultest.java9.util.ListTest.assertIsImmutableListOf;

import com.google.gwt.emultest.java.util.EmulTestBase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Tests for java.util.List Java 10 API emulation.
 */
public class ListTest extends EmulTestBase {
  public void testCopyOf() {
    assertIsImmutableListOf(List.copyOf(List.of("a", "b")), "a", "b");
    assertIsImmutableListOf(List.copyOf(Arrays.asList("a", "b")), "a", "b");

    ArrayList<String> arrayList = new ArrayList<>();
    arrayList.add("a");
    arrayList.add("b");
    List<String> copy = List.copyOf(arrayList);
    assertIsImmutableListOf(copy, "a", "b");

    // verify that mutating the original doesn't affect the copy
    arrayList.add("c");
    assertEquals(2, copy.size());
    assertFalse(copy.contains("c"));

    arrayList.remove(0);
    assertEquals(2, copy.size());
    assertTrue(copy.contains("a"));

    // ensure that null values in the collection result in a NPE
    try {
      List.copyOf(Arrays.asList("a", null));
      fail("Expected NullPointerException passing copy a collection with a null value");
    } catch (NullPointerException ignore) {
      // expected
    }
  }
}
