/*
 * Copyright 2010 Google Inc.
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
package elemental.js.util;

import com.google.gwt.junit.client.GWTTestCase;

import elemental.util.ArrayOf;
import elemental.util.ArrayOfInt;
import elemental.util.ArrayOfString;
import elemental.util.Collections;
import elemental.util.MapFromIntTo;
import elemental.util.MapFromIntToString;

import java.util.HashMap;

/**
 * Tests {@link MapFromIntTo} and {@link MapFromIntToString}.
 */
public class MapFromIntTests extends GWTTestCase {

  @Override
  public String getModuleName() {
    return "elemental.Elemental";
  }

  /**
   * Tests {@link MapFromIntTo}.
   */
  public void testMapsFromInts() {
    // This is our test subject.
    final MapFromIntTo<TestItem> map = Collections.mapFromIntTo();

    // These are his keys.
    final int[] keys = new int[] {1, 2, 3};

    // These are the values for those keys.
    final TestItem[] vals = new TestItem[] {new TestItem(0), new TestItem(1), new TestItem(2)};

    // Let's put those values in.
    for (int i = 0, n = keys.length; i < n; ++i) {
      map.put(keys[i], vals[i]);
    }

    // Are they all in the right place?
    for (int i = 0, n = keys.length; i < n; ++i) {
      assertTrue(map.hasKey(keys[i]));
      assertEquals(vals[i], map.get(keys[i]));
    }

    // These are some new values.
    final TestItem[] newVals = new TestItem[] {new TestItem(3), new TestItem(4), new TestItem(5)};

    // Let's update those keys, ok.
    for (int i = 0, n = keys.length; i < n; ++i) {
      map.put(keys[i], newVals[i]);
    }

    // Are they all in the right place?
    for (int i = 0, n = keys.length; i < n; ++i) {
      assertTrue(map.hasKey(keys[i]));
      assertEquals(newVals[i], map.get(keys[i]));
    }

    assertMapSamelitude(keys, newVals, map);

    // Let's remove a key, did it go away?
    map.remove(keys[0]);
    assertNull(map.get(keys[0]));
    assertFalse(map.hasKey(keys[0]));
  }

  /**
   * Tests {@link MapFromIntToString}.
   */
  public void testMapsFromIntstoStrings() {
    // This is our test subject.
    final MapFromIntToString map = Collections.mapFromIntToString();

    // These are his keys.
    final int[] keys = new int[] {1, 2, 3};

    // These are the values for those keys.
    final String[] vals = new String[] {"val-0", "val-1", "val-2"};

    // Let's put those values in.
    for (int i = 0, n = keys.length; i < n; ++i) {
      map.put(keys[i], vals[i]);
    }

    // Are they all in the right place?
    for (int i = 0, n = keys.length; i < n; ++i) {
      assertTrue(map.hasKey(keys[i]));
      assertEquals(vals[i], map.get(keys[i]));
    }

    // These are some new values.
    final String[] newVals = new String[] {"val-3", "val-4", "val-5"};

    // Let's update those keys, ok.
    for (int i = 0, n = keys.length; i < n; ++i) {
      map.put(keys[i], newVals[i]);
    }

    // Are they all in the right place?
    for (int i = 0, n = keys.length; i < n; ++i) {
      assertTrue(map.hasKey(keys[i]));
      assertEquals(newVals[i], map.get(keys[i]));
    }

    assertMapSamelitude(keys, newVals, map);

    // Let's remove a key, did it go away?
    map.remove(keys[0]);
    assertNull(map.get(keys[0]));
    assertFalse(map.hasKey(keys[0]));
  }

  static void assertMapSamelitude(int[] keys, Object[] values, MapFromIntTo map) {
    HashMap<Integer, Object> expected = new HashMap<Integer, Object>();
    for (int i = 0; i < keys.length; i++) {
      expected.put(keys[i], values[i]);
    }
    HashMap<Integer, Object> actual = new HashMap<Integer, Object>();
    ArrayOfInt mapKeys = map.keys();
    ArrayOf mapValues = map.values();
    for (int i = 0; i < mapKeys.length(); i++) {
      actual.put(mapKeys.get(i), mapValues.get(i));
    }
    assertEquals(keys.length, mapKeys.length());
    assertEquals(values.length, mapValues.length());
    assertTrue(expected.equals(actual));
  }

  static void assertMapSamelitude(int[] keys, String[] values, MapFromIntToString map) {
    HashMap<Integer, String> expected = new HashMap<Integer, String>();
    for (int i = 0; i < keys.length; i++) {
      expected.put(keys[i], values[i]);
    }
    HashMap<Integer, String> actual = new HashMap<Integer, String>();
    ArrayOfInt mapKeys = map.keys();
    ArrayOfString mapValues = map.values();
    for (int i = 0; i < mapKeys.length(); i++) {
      actual.put(mapKeys.get(i), mapValues.get(i));
    }
    assertEquals(keys.length, mapKeys.length());
    assertEquals(values.length, mapValues.length());
    assertTrue(expected.equals(actual));
  }
}
