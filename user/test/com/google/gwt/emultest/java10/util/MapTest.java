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

import static com.google.gwt.emultest.java9.util.MapTest.assertIsImmutableMapOf;

import com.google.gwt.emultest.java.util.EmulTestBase;

import java.util.HashMap;
import java.util.Map;

/**
 * Tests for java.util.Map Java 10 API emulation.
 */
public class MapTest extends EmulTestBase {
  public void testCopyOf() {
    assertIsImmutableMapOf(Map.copyOf(Map.of("a", 1)), "a");

    HashMap<String, Integer> hashMap = new HashMap<>();
    hashMap.put("a", 1);
    Map<String, Integer> copy = Map.copyOf(hashMap);
    assertIsImmutableMapOf(copy, "a");

    // verify that mutating the original has no effect on the copy
    hashMap.put("b", 2);
    assertFalse(copy.containsKey("b"));
    assertEquals(1, copy.size());

    hashMap.put("a", 5);
    assertEquals(1, (int) copy.get("a"));

    // ensure that null values result in a NPE
    HashMap<String, Integer> mapWithNullKey = new HashMap<>();
    mapWithNullKey.put(null, 1);
    try {
      Map.copyOf(mapWithNullKey);
      fail("expected NullPointerException from copyOf with a null key");
    } catch (NullPointerException ignored) {
      // expected
    }

    HashMap<String, Integer> mapWithNullValue = new HashMap<>();
    mapWithNullValue.put("key", null);
    try {
      Map.copyOf(mapWithNullValue);
      fail("expected NullPointerException from copyOf with a null value");
    } catch (NullPointerException ignored) {
      // expected
    }
  }
}
