/*
 * Copyright 2011 Google Inc.
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

package com.google.gwt.storage.client;

import static java.util.Collections.singleton;

import com.google.gwt.core.client.GWT;
import com.google.gwt.junit.client.GWTTestCase;
import com.google.gwt.testing.TestUtils;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * Tests representing the contract of {@link Map}. Concrete subclasses of this
 * base class test conformance of concrete {@link Map} subclasses to that
 * contract.
 *
 * TODO: Descriptive assertion messages, with hints as to probable fixes.
 * TODO: Add another constructor parameter indicating whether the class under
 * test is ordered, and check the order if so.
 * TODO: Refactor to share code with SetTestBuilder.
 *
 * @param <K> the type of keys used by the maps under test
 * @param <V> the type of mapped values used the maps under test
 */
public abstract class MapInterfaceTest<K, V> extends GWTTestCase {

  protected final boolean supportsPut;
  protected final boolean supportsRemove;
  protected final boolean supportsClear;
  protected final boolean allowsNullKeys;
  protected final boolean allowsNullValues;
  protected final boolean supportsIteratorRemove;

  /**
   * Creates a new, empty instance of the class under test.
   *
   * @return a new, empty map instance.
   * @throws UnsupportedOperationException if it's not possible to make an empty
   *           instance of the class under test.
   */
  protected abstract Map<K, V> makeEmptyMap()
      throws UnsupportedOperationException;

  /**
   * Creates a new, non-empty instance of the class under test.
   *
   * @return a new, non-empty map instance.
   * @throws UnsupportedOperationException if it's not possible to make a
   *           non-empty instance of the class under test.
   */
  protected abstract Map<K, V> makePopulatedMap()
      throws UnsupportedOperationException;

  /**
   * Creates a new key that is not expected to be found in
   * {@link #makePopulatedMap()}.
   *
   * @return a key.
   * @throws UnsupportedOperationException if it's not possible to make a key
   *           that will not be found in the map.
   */
  protected abstract K getKeyNotInPopulatedMap()
      throws UnsupportedOperationException;

  /**
   * Creates a new value that is not expected to be found in
   * {@link #makePopulatedMap()}.
   *
   * @return a value.
   * @throws UnsupportedOperationException if it's not possible to make a value
   *           that will not be found in the map.
   */
  protected abstract V getValueNotInPopulatedMap()
      throws UnsupportedOperationException;

  /**
   * Constructor that assigns {@code supportsIteratorRemove} the same value as
   * {@code supportsRemove}.
   */
  protected MapInterfaceTest(boolean allowsNullKeys, boolean allowsNullValues,
      boolean supportsPut, boolean supportsRemove, boolean supportsClear) {
    this(allowsNullKeys, allowsNullValues, supportsPut, supportsRemove,
        supportsClear, supportsRemove);
  }

  /**
   * Constructor with an explicit {@code supportsIteratorRemove} parameter.
   */
  protected MapInterfaceTest(boolean allowsNullKeys, boolean allowsNullValues,
      boolean supportsPut, boolean supportsRemove, boolean supportsClear,
      boolean supportsIteratorRemove) {
    this.supportsPut = supportsPut;
    this.supportsRemove = supportsRemove;
    this.supportsClear = supportsClear;
    this.allowsNullKeys = allowsNullKeys;
    this.allowsNullValues = allowsNullValues;
    this.supportsIteratorRemove = supportsIteratorRemove;
  }

  /**
   * Used by tests that require a map, but don't care whether it's populated or
   * not.
   *
   * @return a new map instance.
   */
  protected Map<K, V> makeEitherMap() {
    try {
      return makePopulatedMap();
    } catch (UnsupportedOperationException e) {
      return makeEmptyMap();
    }
  }

  protected final boolean supportsValuesHashCode(Map<K, V> map) {
    // get the first non-null value
    Collection<V> values = map.values();
    for (V value : values) {
      if (value != null) {
        try {
          value.hashCode();
        } catch (Exception e) {
          return false;
        }
        return true;
      }
    }
    return true;
  }

  /**
   * Checks all the properties that should always hold of a map. Also calls
   * {@link #assertMoreInvariants} to check invariants that are peculiar to
   * specific implementations.
   *
   * @see #assertMoreInvariants
   * @param map the map to check.
   */
  protected final void assertInvariants(Map<K, V> map) {
    Set<K> keySet = map.keySet();
    Collection<V> valueCollection = map.values();
    Set<Entry<K, V>> entrySet = map.entrySet();

    assertEquals(map.size() == 0, map.isEmpty());
    assertEquals(map.size(), keySet.size());
    assertEquals(keySet.size() == 0, keySet.isEmpty());
    assertEquals(!keySet.isEmpty(), keySet.iterator().hasNext());

    int expectedKeySetHash = 0;
    for (K key : keySet) {
      V value = map.get(key);
      expectedKeySetHash += key != null ? key.hashCode() : 0;
      assertTrue(map.containsKey(key));
      assertTrue(map.containsValue(value));
      assertTrue(valueCollection.contains(value));
      assertTrue(valueCollection.containsAll(Collections.singleton(value)));
      assertTrue(entrySet.contains(mapEntry(key, value)));
      assertTrue(allowsNullKeys || (key != null));
    }
    assertEquals(expectedKeySetHash, keySet.hashCode());

    assertEquals(map.size(), valueCollection.size());
    assertEquals(valueCollection.size() == 0, valueCollection.isEmpty());
    assertEquals(!valueCollection.isEmpty(),
        valueCollection.iterator().hasNext());
    for (V value : valueCollection) {
      assertTrue(map.containsValue(value));
      assertTrue(allowsNullValues || (value != null));
    }

    assertEquals(map.size(), entrySet.size());
    assertEquals(entrySet.size() == 0, entrySet.isEmpty());
    assertEquals(!entrySet.isEmpty(), entrySet.iterator().hasNext());
    assertFalse(entrySet.contains("foo"));

    boolean supportsValuesHashCode = supportsValuesHashCode(map);
    if (supportsValuesHashCode) {
      int expectedEntrySetHash = 0;
      for (Entry<K, V> entry : entrySet) {
        assertTrue(map.containsKey(entry.getKey()));
        assertTrue(map.containsValue(entry.getValue()));
        int expectedHash = (entry.getKey() == null ? 0
            : entry.getKey().hashCode())
            ^ (entry.getValue() == null ? 0 : entry.getValue().hashCode());
        assertEquals(expectedHash, entry.hashCode());
        expectedEntrySetHash += expectedHash;
      }
      assertEquals(expectedEntrySetHash, entrySet.hashCode());
      assertTrue(entrySet.containsAll(new HashSet<Entry<K, V>>(entrySet)));
      assertTrue(entrySet.equals(new HashSet<Entry<K, V>>(entrySet)));
    }

    Object[] entrySetToArray1 = entrySet.toArray();
    assertEquals(map.size(), entrySetToArray1.length);
    assertTrue(Arrays.asList(entrySetToArray1).containsAll(entrySet));

    Entry<?, ?>[] entrySetToArray2 = new Entry<?, ?>[map.size() + 2];
    entrySetToArray2[map.size()] = mapEntry("foo", 1);
    assertSame(entrySetToArray2, entrySet.toArray(entrySetToArray2));
    assertNull(entrySetToArray2[map.size()]);
    assertTrue(Arrays.asList(entrySetToArray2).containsAll(entrySet));

    Object[] valuesToArray1 = valueCollection.toArray();
    assertEquals(map.size(), valuesToArray1.length);
    assertTrue(Arrays.asList(valuesToArray1).containsAll(valueCollection));

    Object[] valuesToArray2 = new Object[map.size() + 2];
    valuesToArray2[map.size()] = "foo";
    assertSame(valuesToArray2, valueCollection.toArray(valuesToArray2));
    assertNull(valuesToArray2[map.size()]);
    assertTrue(Arrays.asList(valuesToArray2).containsAll(valueCollection));

    if (supportsValuesHashCode) {
      int expectedHash = 0;
      for (Entry<K, V> entry : entrySet) {
        expectedHash += entry.hashCode();
      }
      assertEquals(expectedHash, map.hashCode());
    }

    assertMoreInvariants(map);
  }

  /**
   * Override this to check invariants which should hold true for a particular
   * implementation, but which are not generally applicable to every instance of
   * Map.
   *
   * @param map the map whose additional invariants to check.
   */
  protected void assertMoreInvariants(Map<K, V> map) {
  }

  public void testClear() {
    final Map<K, V> map;
    try {
      map = makePopulatedMap();
    } catch (UnsupportedOperationException e) {
      return;
    }

    if (supportsClear) {
      map.clear();
      assertTrue(map.isEmpty());
    } else {
      try {
        map.clear();
        fail("Expected UnsupportedOperationException.");
      } catch (UnsupportedOperationException e) {
        // Expected.
      }
    }
    assertInvariants(map);
  }

  public void testContainsKey() {
    final Map<K, V> map;
    final K unmappedKey;
    try {
      map = makePopulatedMap();
      unmappedKey = getKeyNotInPopulatedMap();
    } catch (UnsupportedOperationException e) {
      return;
    }
    assertFalse(map.containsKey(unmappedKey));
    assertTrue(map.containsKey(map.keySet().iterator().next()));
    if (allowsNullKeys) {
      map.containsKey(null);
    } else {
      try {
        map.containsKey(null);
        fail("Should have thrown NullPointerException");
      } catch (NullPointerException expected) {
      }
    }
    assertInvariants(map);
  }

  public void testContainsValue() {
    final Map<K, V> map;
    final V unmappedValue;
    try {
      map = makePopulatedMap();
      unmappedValue = getValueNotInPopulatedMap();
    } catch (UnsupportedOperationException e) {
      return;
    }
    assertFalse(map.containsValue(unmappedValue));
    assertTrue(map.containsValue(map.values().iterator().next()));
    if (allowsNullValues) {
      map.containsValue(null);
    } else {
      try {
        map.containsKey(null);
        fail("Should have thrown NullPointerException");
      } catch (NullPointerException expected) {
      }
    }
    assertInvariants(map);
  }

  public void testEntrySet() {
    final Map<K, V> map;
    final Set<Entry<K, V>> entrySet;
    try {
      map = makePopulatedMap();
    } catch (UnsupportedOperationException e) {
      return;
    }
    assertInvariants(map);

    entrySet = map.entrySet();
    final K unmappedKey;
    final V unmappedValue;
    try {
      unmappedKey = getKeyNotInPopulatedMap();
      unmappedValue = getValueNotInPopulatedMap();
    } catch (UnsupportedOperationException e) {
      return;
    }
    for (Entry<K, V> entry : entrySet) {
      assertFalse(unmappedKey.equals(entry.getKey()));
      assertFalse(unmappedValue.equals(entry.getValue()));
    }
  }

  public void testEntrySetForEmptyMap() {
    final Map<K, V> map;
    try {
      map = makeEmptyMap();
    } catch (UnsupportedOperationException e) {
      return;
    }
    assertInvariants(map);
  }

  public void testEntrySetContainsEntryNullKeyPresent() {
    if (!allowsNullKeys || !supportsPut) {
      return;
    }
    final Map<K, V> map;
    final Set<Entry<K, V>> entrySet;
    try {
      map = makeEitherMap();
    } catch (UnsupportedOperationException e) {
      return;
    }
    assertInvariants(map);

    entrySet = map.entrySet();
    final V unmappedValue;
    try {
      unmappedValue = getValueNotInPopulatedMap();
    } catch (UnsupportedOperationException e) {
      return;
    }

    map.put(null, unmappedValue);
    Entry<K, V> entry = mapEntry(null, unmappedValue);
    assertTrue(entrySet.contains(entry));
    assertFalse(entrySet.contains(mapEntry(null, null)));
  }

  public void testEntrySetContainsEntryNullKeyMissing() {
    final Map<K, V> map;
    final Set<Entry<K, V>> entrySet;
    try {
      map = makeEitherMap();
    } catch (UnsupportedOperationException e) {
      return;
    }
    assertInvariants(map);

    entrySet = map.entrySet();
    final V unmappedValue;
    try {
      unmappedValue = getValueNotInPopulatedMap();
    } catch (UnsupportedOperationException e) {
      return;
    }
    Entry<K, V> entry = mapEntry(null, unmappedValue);
    assertFalse(entrySet.contains(entry));
    assertFalse(entrySet.contains(mapEntry(null, null)));
  }

  public void testEntrySetIteratorRemove() {
    final Map<K, V> map;
    try {
      map = makePopulatedMap();
    } catch (UnsupportedOperationException e) {
      return;
    }

    Set<Entry<K, V>> entrySet = map.entrySet();
    Iterator<Entry<K, V>> iterator = entrySet.iterator();
    if (supportsIteratorRemove) {
      int initialSize = map.size();
      Entry<K, V> entry = iterator.next();
      iterator.remove();
      assertEquals(initialSize - 1, map.size());
      assertFalse(entrySet.contains(entry));
      assertInvariants(map);
      try {
        iterator.remove();
        fail("Expected IllegalStateException.");
      } catch (IllegalStateException e) {
        // Expected.
      }
    } else {
      try {
        iterator.next();
        iterator.remove();
        fail("Expected UnsupportedOperationException.");
      } catch (UnsupportedOperationException e) {
        // Expected.
      }
    }
    assertInvariants(map);
  }

  public void testEntrySetRemove() {
    final Map<K, V> map;
    try {
      map = makePopulatedMap();
    } catch (UnsupportedOperationException e) {
      return;
    }

    Set<Entry<K, V>> entrySet = map.entrySet();
    if (supportsRemove) {
      int initialSize = map.size();
      boolean didRemove = entrySet.remove(entrySet.iterator().next());
      assertTrue(didRemove);
      assertEquals(initialSize - 1, map.size());
    } else {
      try {
        entrySet.remove(entrySet.iterator().next());
        fail("Expected UnsupportedOperationException.");
      } catch (UnsupportedOperationException e) {
        // Expected.
      }
    }
    assertInvariants(map);
  }

  public void testEntrySetRemoveMissingKey() {
    final Map<K, V> map;
    final K key;
    try {
      map = makeEitherMap();
      key = getKeyNotInPopulatedMap();
    } catch (UnsupportedOperationException e) {
      return;
    }

    Set<Entry<K, V>> entrySet = map.entrySet();
    Entry<K, V> entry = mapEntry(key, getValueNotInPopulatedMap());
    int initialSize = map.size();
    if (supportsRemove) {
      boolean didRemove = entrySet.remove(entry);
      assertFalse(didRemove);
    } else {
      try {
        boolean didRemove = entrySet.remove(entry);
        assertFalse(didRemove);
      } catch (UnsupportedOperationException optional) {
      }
    }
    assertEquals(initialSize, map.size());
    assertFalse(map.containsKey(key));
    assertInvariants(map);
  }

  public void testEntrySetRemoveDifferentValue() {
    final Map<K, V> map;
    try {
      map = makePopulatedMap();
    } catch (UnsupportedOperationException e) {
      return;
    }

    Set<Entry<K, V>> entrySet = map.entrySet();
    K key = map.keySet().iterator().next();
    Entry<K, V> entry = mapEntry(key, getValueNotInPopulatedMap());
    int initialSize = map.size();
    if (supportsRemove) {
      boolean didRemove = entrySet.remove(entry);
      assertFalse(didRemove);
    } else {
      try {
        boolean didRemove = entrySet.remove(entry);
        assertFalse(didRemove);
      } catch (UnsupportedOperationException optional) {
      }
    }
    assertEquals(initialSize, map.size());
    assertTrue(map.containsKey(key));
    assertInvariants(map);
  }

  public void testEntrySetRemoveNullKeyPresent() {
    if (!allowsNullKeys || !supportsPut || !supportsRemove) {
      return;
    }
    final Map<K, V> map;
    final Set<Entry<K, V>> entrySet;
    try {
      map = makeEitherMap();
    } catch (UnsupportedOperationException e) {
      return;
    }
    assertInvariants(map);

    entrySet = map.entrySet();
    final V unmappedValue;
    try {
      unmappedValue = getValueNotInPopulatedMap();
    } catch (UnsupportedOperationException e) {
      return;
    }

    map.put(null, unmappedValue);
    assertEquals(unmappedValue, map.get(null));
    assertTrue(map.containsKey(null));
    Entry<K, V> entry = mapEntry(null, unmappedValue);
    assertTrue(entrySet.remove(entry));
    assertNull(map.get(null));
    assertFalse(map.containsKey(null));
  }

  public void testEntrySetRemoveNullKeyMissing() {
    final Map<K, V> map;
    try {
      map = makeEitherMap();
    } catch (UnsupportedOperationException e) {
      return;
    }

    Set<Entry<K, V>> entrySet = map.entrySet();
    Entry<K, V> entry = mapEntry(null, getValueNotInPopulatedMap());
    int initialSize = map.size();
    if (supportsRemove) {
      boolean didRemove = entrySet.remove(entry);
      assertFalse(didRemove);
    } else {
      try {
        boolean didRemove = entrySet.remove(entry);
        assertFalse(didRemove);
      } catch (UnsupportedOperationException optional) {
      }
    }
    assertEquals(initialSize, map.size());
    assertInvariants(map);
  }

  public void testEntrySetRemoveAll() {
    final Map<K, V> map;
    try {
      map = makePopulatedMap();
    } catch (UnsupportedOperationException e) {
      return;
    }

    Set<Entry<K, V>> entrySet = map.entrySet();
    Set<Entry<K, V>> entriesToRemove = singleton(entrySet.iterator().next());
    if (supportsRemove) {
      int initialSize = map.size();
      boolean didRemove = entrySet.removeAll(entriesToRemove);
      assertTrue(didRemove);
      assertEquals(initialSize - entriesToRemove.size(), map.size());
      for (Entry<K, V> entry : entriesToRemove) {
        assertFalse(entrySet.contains(entry));
      }
    } else {
      try {
        entrySet.removeAll(entriesToRemove);
        fail("Expected UnsupportedOperationException.");
      } catch (UnsupportedOperationException e) {
        // Expected.
      }
    }
    assertInvariants(map);
  }

  public void testEntrySetRemoveAllNullFromEmpty() {
    final Map<K, V> map;
    try {
      map = makeEmptyMap();
    } catch (UnsupportedOperationException e) {
      return;
    }

    Set<Entry<K, V>> entrySet = map.entrySet();
    if (supportsRemove) {
      try {
        entrySet.removeAll(null);
        fail("Should have thrown NullPointerException");
      } catch (NullPointerException expected) {
      }
    } else {
      try {
        entrySet.removeAll(null);
        fail("Expected UnsupportedOperationException.");
      } catch (UnsupportedOperationException expected) {
      }
    }
    assertInvariants(map);
  }

  public void testEntrySetRetainAll() {
    final Map<K, V> map;
    try {
      map = makePopulatedMap();
    } catch (UnsupportedOperationException e) {
      return;
    }

    Set<Entry<K, V>> entrySet = map.entrySet();
    Set<Entry<K, V>> entriesToRetain = singleton(entrySet.iterator().next());
    if (supportsRemove) {
      boolean shouldRemove = (entrySet.size() > entriesToRetain.size());
      boolean didRemove = entrySet.retainAll(entriesToRetain);
      assertEquals(shouldRemove, didRemove);
      assertEquals(entriesToRetain.size(), map.size());
      for (Entry<K, V> entry : entriesToRetain) {
        assertTrue(entrySet.contains(entry));
      }
    } else {
      try {
        entrySet.retainAll(entriesToRetain);
        fail("Expected UnsupportedOperationException.");
      } catch (UnsupportedOperationException e) {
        // Expected.
      }
    }
    assertInvariants(map);
  }

  public void testEntrySetRetainAllNullFromEmpty() {
    final Map<K, V> map;
    try {
      map = makeEmptyMap();
    } catch (UnsupportedOperationException e) {
      return;
    }

    Set<Entry<K, V>> entrySet = map.entrySet();
    if (supportsRemove) {
      try {
        entrySet.retainAll(null);
        failForMissingNPE(map);
      } catch (NullPointerException expected) {
      }
    } else {
      try {
        entrySet.retainAll(null);
        fail("Expected UnsupportedOperationException.");
      } catch (UnsupportedOperationException expected) {
      }
    }
    assertInvariants(map);
  }

  public void testEntrySetClear() {
    final Map<K, V> map;
    try {
      map = makePopulatedMap();
    } catch (UnsupportedOperationException e) {
      return;
    }

    Set<Entry<K, V>> entrySet = map.entrySet();
    if (supportsClear) {
      entrySet.clear();
      assertTrue(entrySet.isEmpty());
    } else {
      try {
        entrySet.clear();
        fail("Expected UnsupportedOperationException.");
      } catch (UnsupportedOperationException expected) {
      }
    }
    assertInvariants(map);
  }

  public void testEntrySetAddAndAddAll() {
    final Map<K, V> map;
    try {
      map = makeEitherMap();
    } catch (UnsupportedOperationException e) {
      return;
    }

    Set<Entry<K, V>> entrySet = map.entrySet();
    final Entry<K, V> entryToAdd = mapEntry(null, null);
    try {
      entrySet.add(entryToAdd);
      fail("Expected UnsupportedOperationException.");
    } catch (UnsupportedOperationException expected) {
    }
    assertInvariants(map);

    try {
      entrySet.addAll(singleton(entryToAdd));
      fail("Expected UnsupportedOperationException.");
    } catch (UnsupportedOperationException expected) {
    }
    assertInvariants(map);
  }

  public void testEntrySetSetValue() {
    // TODO: Investigate the extent to which, in practice, maps that support
    // put() also support Entry.setValue().
    if (!supportsPut) {
      return;
    }

    final Map<K, V> map;
    final V valueToSet;
    try {
      map = makePopulatedMap();
      valueToSet = getValueNotInPopulatedMap();
    } catch (UnsupportedOperationException e) {
      return;
    }

    Set<Entry<K, V>> entrySet = map.entrySet();
    Entry<K, V> entry = entrySet.iterator().next();
    final V oldValue = entry.getValue();
    final V returnedValue = entry.setValue(valueToSet);
    assertEquals(oldValue, returnedValue);
    assertTrue(entrySet.contains(mapEntry(entry.getKey(), valueToSet)));
    assertEquals(valueToSet, map.get(entry.getKey()));
    assertInvariants(map);
  }

  public void testEntrySetSetValueSameValue() {
    // TODO: Investigate the extent to which, in practice, maps that support
    // put() also support Entry.setValue().
    if (!supportsPut) {
      return;
    }

    final Map<K, V> map;
    try {
      map = makePopulatedMap();
    } catch (UnsupportedOperationException e) {
      return;
    }

    Set<Entry<K, V>> entrySet = map.entrySet();
    Entry<K, V> entry = entrySet.iterator().next();
    final V oldValue = entry.getValue();
    final V returnedValue = entry.setValue(oldValue);
    assertEquals(oldValue, returnedValue);
    assertTrue(entrySet.contains(mapEntry(entry.getKey(), oldValue)));
    assertEquals(oldValue, map.get(entry.getKey()));
    assertInvariants(map);
  }

  public void testEqualsForEqualMap() {
    final Map<K, V> map;
    try {
      map = makePopulatedMap();
    } catch (UnsupportedOperationException e) {
      return;
    }

    assertEquals(map, map);
    assertEquals(makePopulatedMap(), map);
    assertFalse(map.equals(Collections.emptyMap()));
    // no-inspection ObjectEqualsNull
    assertFalse(map.equals(null));
  }

  /*
   * equals does not apply to Storage because there's only one instance so two
   * maps will always be equal.
   */
  public void disabled_testEqualsForLargerMap() {
    if (!supportsPut) {
      return;
    }

    final Map<K, V> map;
    final Map<K, V> largerMap;
    try {
      map = makePopulatedMap();
      largerMap = makePopulatedMap();
      largerMap.put(getKeyNotInPopulatedMap(), getValueNotInPopulatedMap());
    } catch (UnsupportedOperationException e) {
      return;
    }

    assertFalse(map.equals(largerMap));
  }

  /*
   * equals does not apply to Storage because there's only one instance so two
   * maps will always be equal.
   */
  public void disabled_testEqualsForSmallerMap() {
    if (!supportsRemove) {
      return;
    }

    final Map<K, V> map;
    final Map<K, V> smallerMap;
    try {
      map = makePopulatedMap();
      smallerMap = makePopulatedMap();
      smallerMap.remove(smallerMap.keySet().iterator().next());
    } catch (UnsupportedOperationException e) {
      return;
    }

    assertFalse(map.equals(smallerMap));
  }

  public void testEqualsForEmptyMap() {
    final Map<K, V> map;
    try {
      map = makeEmptyMap();
    } catch (UnsupportedOperationException e) {
      return;
    }

    assertEquals(map, map);
    assertEquals(makeEmptyMap(), map);
    assertEquals(Collections.emptyMap(), map);
    assertFalse(map.equals(Collections.emptySet()));
    // noinspection ObjectEqualsNull
    assertFalse(map.equals(null));
  }

  public void testGet() {
    final Map<K, V> map;
    try {
      map = makePopulatedMap();
    } catch (UnsupportedOperationException e) {
      return;
    }

    for (Entry<K, V> entry : map.entrySet()) {
      assertEquals(entry.getValue(), map.get(entry.getKey()));
    }

    K unmappedKey = null;
    try {
      unmappedKey = getKeyNotInPopulatedMap();
    } catch (UnsupportedOperationException e) {
      return;
    }
    assertNull(map.get(unmappedKey));
  }

  public void testGetForEmptyMap() {
    final Map<K, V> map;
    K unmappedKey = null;
    try {
      map = makeEmptyMap();
      unmappedKey = getKeyNotInPopulatedMap();
    } catch (UnsupportedOperationException e) {
      return;
    }
    assertNull(map.get(unmappedKey));
  }

  public void testGetNull() {
    final Map<K, V> map;
    try {
      map = makeEitherMap();
    } catch (UnsupportedOperationException e) {
      return;
    }

    if (allowsNullKeys) {
      V value = map.get(null);
      if (!allowsNullValues) {
        assertEquals(map.containsKey(null), value != null);
      }
    } else {
      try {
        map.get(null);
      } catch (NullPointerException expected) {
        // in GWT client.
      }
    }
    assertInvariants(map);
  }

  public void testHashCode() {
    final Map<K, V> map;
    try {
      map = makePopulatedMap();
    } catch (UnsupportedOperationException e) {
      return;
    }
    assertInvariants(map);
  }

  public void testHashCodeForEmptyMap() {
    final Map<K, V> map;
    try {
      map = makeEmptyMap();
    } catch (UnsupportedOperationException e) {
      return;
    }
    assertInvariants(map);
  }

  public void testPutNewKey() {
    final Map<K, V> map;
    try {
      map = makeEitherMap();
    } catch (UnsupportedOperationException e) {
      return;
    }

    final K keyToPut;
    final V valueToPut;
    try {
      keyToPut = getKeyNotInPopulatedMap();
      valueToPut = getValueNotInPopulatedMap();
    } catch (UnsupportedOperationException e) {
      return;
    }
    if (supportsPut) {
      int initialSize = map.size();
      V oldValue = map.put(keyToPut, valueToPut);
      assertEquals(valueToPut, map.get(keyToPut));
      assertTrue(map.containsKey(keyToPut));
      assertTrue(map.containsValue(valueToPut));
      assertEquals(initialSize + 1, map.size());
      assertNull(oldValue);
    } else {
      try {
        map.put(keyToPut, valueToPut);
        fail("Expected UnsupportedOperationException.");
      } catch (UnsupportedOperationException e) {
        // Expected.
      }
    }
    assertInvariants(map);
  }

  public void testPutExistingKey() {
    final Map<K, V> map;
    final K keyToPut;
    final V valueToPut;
    try {
      map = makePopulatedMap();
      valueToPut = getValueNotInPopulatedMap();
    } catch (UnsupportedOperationException e) {
      return;
    }
    keyToPut = map.keySet().iterator().next();
    if (supportsPut) {
      int initialSize = map.size();
      map.put(keyToPut, valueToPut);
      assertEquals(valueToPut, map.get(keyToPut));
      assertTrue(map.containsKey(keyToPut));
      assertTrue(map.containsValue(valueToPut));
      assertEquals(initialSize, map.size());
    } else {
      try {
        map.put(keyToPut, valueToPut);
        fail("Expected UnsupportedOperationException.");
      } catch (UnsupportedOperationException e) {
        // Expected.
      }
    }
    assertInvariants(map);
  }

  public void testPutNullKey() {
    if (!supportsPut) {
      return;
    }
    final Map<K, V> map;
    try {
      map = makeEitherMap();
    } catch (UnsupportedOperationException e) {
      return;
    }
    final V valueToPut;
    try {
      valueToPut = getValueNotInPopulatedMap();
    } catch (UnsupportedOperationException e) {
      return;
    }
    if (allowsNullKeys) {
      final V oldValue = map.get(null);
      final V returnedValue = map.put(null, valueToPut);
      assertEquals(oldValue, returnedValue);
      assertEquals(valueToPut, map.get(null));
      assertTrue(map.containsKey(null));
      assertTrue(map.containsValue(valueToPut));
    } else {
      try {
        map.put(null, valueToPut);
        fail("Expected RuntimeException");
      } catch (RuntimeException e) {
        // Expected.
      }
    }
    assertInvariants(map);
  }

  public void testPutNullValue() {
    if (!supportsPut) {
      return;
    }
    final Map<K, V> map;
    try {
      map = makeEitherMap();
    } catch (UnsupportedOperationException e) {
      return;
    }
    final K keyToPut;
    try {
      keyToPut = getKeyNotInPopulatedMap();
    } catch (UnsupportedOperationException e) {
      return;
    }
    if (allowsNullValues) {
      int initialSize = map.size();
      final V oldValue = map.get(keyToPut);
      final V returnedValue = map.put(keyToPut, null);
      assertEquals(oldValue, returnedValue);
      assertNull(map.get(keyToPut));
      assertTrue(map.containsKey(keyToPut));
      assertTrue(map.containsValue(null));
      assertEquals(initialSize + 1, map.size());
    } else {
      try {
        map.put(keyToPut, null);
        fail("Expected RuntimeException");
      } catch (RuntimeException expected) {
      }
    }
    assertInvariants(map);
  }

  public void testPutNullValueForExistingKey() {
    if (!supportsPut) {
      return;
    }
    final Map<K, V> map;
    final K keyToPut;
    try {
      map = makePopulatedMap();
      keyToPut = map.keySet().iterator().next();
    } catch (UnsupportedOperationException e) {
      return;
    }
    if (allowsNullValues) {
      int initialSize = map.size();
      final V oldValue = map.get(keyToPut);
      final V returnedValue = map.put(keyToPut, null);
      assertEquals(oldValue, returnedValue);
      assertNull(map.get(keyToPut));
      assertTrue(map.containsKey(keyToPut));
      assertTrue(map.containsValue(null));
      assertEquals(initialSize, map.size());
    } else {
      try {
        map.put(keyToPut, null);
        fail("Expected RuntimeException");
      } catch (RuntimeException expected) {
      }
    }
    assertInvariants(map);
  }

  public void testPutAllNewKey() {
    final Map<K, V> map;
    try {
      map = makeEitherMap();
    } catch (UnsupportedOperationException e) {
      return;
    }
    final K keyToPut;
    final V valueToPut;
    try {
      keyToPut = getKeyNotInPopulatedMap();
      valueToPut = getValueNotInPopulatedMap();
    } catch (UnsupportedOperationException e) {
      return;
    }
    final Map<K, V> mapToPut = Collections.singletonMap(keyToPut, valueToPut);
    if (supportsPut) {
      int initialSize = map.size();
      map.putAll(mapToPut);
      assertEquals(valueToPut, map.get(keyToPut));
      assertTrue(map.containsKey(keyToPut));
      assertTrue(map.containsValue(valueToPut));
      assertEquals(initialSize + 1, map.size());
    } else {
      try {
        map.putAll(mapToPut);
        fail("Expected UnsupportedOperationException.");
      } catch (UnsupportedOperationException expected) {
      }
    }
    assertInvariants(map);
  }

  public void testPutAllExistingKey() {
    final Map<K, V> map;
    final K keyToPut;
    final V valueToPut;
    try {
      map = makePopulatedMap();
      valueToPut = getValueNotInPopulatedMap();
    } catch (UnsupportedOperationException e) {
      return;
    }
    keyToPut = map.keySet().iterator().next();
    final Map<K, V> mapToPut = Collections.singletonMap(keyToPut, valueToPut);
    int initialSize = map.size();
    if (supportsPut) {
      map.putAll(mapToPut);
      assertEquals(valueToPut, map.get(keyToPut));
      assertTrue(map.containsKey(keyToPut));
      assertTrue(map.containsValue(valueToPut));
    } else {
      try {
        map.putAll(mapToPut);
        fail("Expected UnsupportedOperationException.");
      } catch (UnsupportedOperationException expected) {
      }
    }
    assertEquals(initialSize, map.size());
    assertInvariants(map);
  }

  public void testRemove() {
    final Map<K, V> map;
    final K keyToRemove;
    try {
      map = makePopulatedMap();
    } catch (UnsupportedOperationException e) {
      return;
    }
    keyToRemove = map.keySet().iterator().next();
    if (supportsRemove) {
      int initialSize = map.size();
      V expectedValue = map.get(keyToRemove);
      V oldValue = map.remove(keyToRemove);
      assertEquals(expectedValue, oldValue);
      assertFalse(map.containsKey(keyToRemove));
      assertEquals(initialSize - 1, map.size());
    } else {
      try {
        map.remove(keyToRemove);
        fail("Expected UnsupportedOperationException.");
      } catch (UnsupportedOperationException e) {
        // Expected.
      }
    }
    assertInvariants(map);
  }

  public void testRemoveMissingKey() {
    final Map<K, V> map;
    final K keyToRemove;
    try {
      map = makePopulatedMap();
      keyToRemove = getKeyNotInPopulatedMap();
    } catch (UnsupportedOperationException e) {
      return;
    }
    if (supportsRemove) {
      int initialSize = map.size();
      assertNull(map.remove(keyToRemove));
      assertEquals(initialSize, map.size());
    } else {
      try {
        map.remove(keyToRemove);
        fail("Expected UnsupportedOperationException.");
      } catch (UnsupportedOperationException e) {
        // Expected.
      }
    }
    assertInvariants(map);
  }

  public void testSize() {
    final Map<K, V> map;
    try {
      map = makeEitherMap();
    } catch (UnsupportedOperationException e) {
      return;
    }
    assertInvariants(map);
  }

  public void testKeySetClear() {
    final Map<K, V> map;
    try {
      map = makeEitherMap();
    } catch (UnsupportedOperationException e) {
      return;
    }

    Set<K> keySet = map.keySet();
    if (supportsClear) {
      keySet.clear();
      assertTrue(keySet.isEmpty());
    } else {
      try {
        keySet.clear();
        fail("Expected UnsupportedOperationException.");
      } catch (UnsupportedOperationException expected) {
      }
    }
    assertInvariants(map);
  }

  public void testKeySetRemoveAllNullFromEmpty() {
    final Map<K, V> map;
    try {
      map = makeEmptyMap();
    } catch (UnsupportedOperationException e) {
      return;
    }

    Set<K> keySet = map.keySet();
    if (supportsRemove) {
      try {
        keySet.removeAll(null);
        fail("Should have thrown NullPointerException");
      } catch (NullPointerException expected) {
      }
    } else {
      try {
        keySet.removeAll(null);
        fail("Expected UnsupportedOperationException.");
      } catch (UnsupportedOperationException expected) {
      }
    }
    assertInvariants(map);
  }

  public void testKeySetRetainAllNullFromEmpty() {
    final Map<K, V> map;
    try {
      map = makeEmptyMap();
    } catch (UnsupportedOperationException e) {
      return;
    }

    Set<K> keySet = map.keySet();
    if (supportsRemove) {
      try {
        keySet.retainAll(null);
        failForMissingNPE(map);
      } catch (NullPointerException expected) {
      }
    } else {
      try {
        keySet.retainAll(null);
        fail("Expected UnsupportedOperationException.");
      } catch (UnsupportedOperationException expected) {
      }
    }
    assertInvariants(map);
  }

  public void testValues() {
    final Map<K, V> map;
    final Collection<V> valueCollection;
    try {
      map = makePopulatedMap();
    } catch (UnsupportedOperationException e) {
      return;
    }
    assertInvariants(map);

    valueCollection = map.values();
    final V unmappedValue;
    try {
      unmappedValue = getValueNotInPopulatedMap();
    } catch (UnsupportedOperationException e) {
      return;
    }
    for (V value : valueCollection) {
      assertFalse(unmappedValue.equals(value));
    }
  }

  public void testValuesIteratorRemove() {
    final Map<K, V> map;
    try {
      map = makePopulatedMap();
    } catch (UnsupportedOperationException e) {
      return;
    }

    Collection<V> valueCollection = map.values();
    Iterator<V> iterator = valueCollection.iterator();
    if (supportsIteratorRemove) {
      int initialSize = map.size();
      iterator.next();
      iterator.remove();
      assertEquals(initialSize - 1, map.size());
      // (We can't assert that the values collection no longer contains the
      // removed value, because the underlying map can have multiple mappings
      // to the same value.)
      assertInvariants(map);
      try {
        iterator.remove();
        fail("Expected IllegalStateException.");
      } catch (IllegalStateException e) {
        // Expected.
      }
    } else {
      try {
        iterator.next();
        iterator.remove();
        fail("Expected UnsupportedOperationException.");
      } catch (UnsupportedOperationException e) {
        // Expected.
      }
    }
    assertInvariants(map);
  }

  public void testValuesRemove() {
    final Map<K, V> map;
    try {
      map = makePopulatedMap();
    } catch (UnsupportedOperationException e) {
      return;
    }

    Collection<V> valueCollection = map.values();
    if (supportsRemove) {
      int initialSize = map.size();
      valueCollection.remove(valueCollection.iterator().next());
      assertEquals(initialSize - 1, map.size());
      // (We can't assert that the values collection no longer contains the
      // removed value, because the underlying map can have multiple mappings
      // to the same value.)
    } else {
      try {
        valueCollection.remove(valueCollection.iterator().next());
        fail("Expected UnsupportedOperationException.");
      } catch (UnsupportedOperationException e) {
        // Expected.
      }
    }
    assertInvariants(map);
  }

  public void testValuesRemoveMissing() {
    final Map<K, V> map;
    final V valueToRemove;
    try {
      map = makeEitherMap();
      valueToRemove = getValueNotInPopulatedMap();
    } catch (UnsupportedOperationException e) {
      return;
    }

    Collection<V> valueCollection = map.values();
    int initialSize = map.size();
    if (supportsRemove) {
      assertFalse(valueCollection.remove(valueToRemove));
    } else {
      try {
        assertFalse(valueCollection.remove(valueToRemove));
      } catch (UnsupportedOperationException e) {
        // Tolerated.
      }
    }
    assertEquals(initialSize, map.size());
    assertInvariants(map);
  }

  public void testValuesRemoveAll() {
    final Map<K, V> map;
    try {
      map = makePopulatedMap();
    } catch (UnsupportedOperationException e) {
      return;
    }

    Collection<V> valueCollection = map.values();
    Set<V> valuesToRemove = singleton(valueCollection.iterator().next());
    if (supportsRemove) {
      valueCollection.removeAll(valuesToRemove);
      for (V value : valuesToRemove) {
        assertFalse(valueCollection.contains(value));
      }
      for (V value : valueCollection) {
        assertFalse(valuesToRemove.contains(value));
      }
    } else {
      try {
        valueCollection.removeAll(valuesToRemove);
        fail("Expected UnsupportedOperationException.");
      } catch (UnsupportedOperationException expected) {
      }
    }
    assertInvariants(map);
  }

  public void testValuesRemoveAllNullFromEmpty() {
    final Map<K, V> map;
    try {
      map = makeEmptyMap();
    } catch (UnsupportedOperationException e) {
      return;
    }

    Collection<V> values = map.values();
    if (supportsRemove) {
      try {
        values.removeAll(null);
        failForMissingNPE(map);
      } catch (NullPointerException expected) {
      }
    } else {
      try {
        values.removeAll(null);
        fail("Expected UnsupportedOperationException.");
      } catch (UnsupportedOperationException expected) {
      }
    }
    assertInvariants(map);
  }

  public void testValuesRetainAll() {
    final Map<K, V> map;
    try {
      map = makePopulatedMap();
    } catch (UnsupportedOperationException e) {
      return;
    }

    Collection<V> valueCollection = map.values();
    Set<V> valuesToRetain = singleton(valueCollection.iterator().next());
    if (supportsRemove) {
      valueCollection.retainAll(valuesToRetain);
      for (V value : valuesToRetain) {
        assertTrue(valueCollection.contains(value));
      }
      for (V value : valueCollection) {
        assertTrue(valuesToRetain.contains(value));
      }
    } else {
      try {
        valueCollection.retainAll(valuesToRetain);
        fail("Expected UnsupportedOperationException.");
      } catch (UnsupportedOperationException e) {
        // Expected.
      }
    }
    assertInvariants(map);
  }

  public void testValuesRetainAllNullFromEmpty() {
    final Map<K, V> map;
    try {
      map = makeEmptyMap();
    } catch (UnsupportedOperationException e) {
      return;
    }

    Collection<V> values = map.values();
    if (supportsRemove) {
      try {
        values.retainAll(null);
        failForMissingNPE(map);
      } catch (NullPointerException expected) {
      }
    } else {
      try {
        values.retainAll(null);
        fail("Expected UnsupportedOperationException.");
      } catch (UnsupportedOperationException expected) {
      }
    }
    assertInvariants(map);
  }

  public void testValuesClear() {
    final Map<K, V> map;
    try {
      map = makePopulatedMap();
    } catch (UnsupportedOperationException e) {
      return;
    }

    Collection<V> valueCollection = map.values();
    if (supportsClear) {
      valueCollection.clear();
      assertTrue(valueCollection.isEmpty());
    } else {
      try {
        valueCollection.clear();
        fail("Expected UnsupportedOperationException.");
      } catch (UnsupportedOperationException e) {
        // Expected.
      }
    }
    assertInvariants(map);
  }

  private void failForMissingNPE(Map<K, V> map) {
    if (map.isEmpty() && !GWT.isScript() && TestUtils.getJdkVersion() < 8) {
      // JDK < 8 does not conform to the specification if the map is empty.
      return;
    }
    fail("Should have thrown NullPointerException");
  }

  private static <K, V> Entry<K, V> mapEntry(K key, V value) {
    return Collections.singletonMap(key, value).entrySet().iterator().next();
  }
}