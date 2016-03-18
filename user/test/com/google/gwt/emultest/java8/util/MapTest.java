/*
 * Copyright 2016 Google Inc.
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
package com.google.gwt.emultest.java8.util;

import java.util.AbstractMap;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Tests for java.util.Map Java 8 API emulation.
 */
public class MapTest extends AbstractJava8MapTest {

  public void testEntryComparingByKey() {
    final Comparator<Map.Entry<String, String>> entryComparator = Map.Entry.comparingByKey();

    AbstractMap.SimpleEntry<String, String> entry1 = new AbstractMap.SimpleEntry<>("a", "A");
    AbstractMap.SimpleEntry<String, String> entry2 = new AbstractMap.SimpleEntry<>("b", "B");

    assertEquals(-1, entryComparator.compare(entry1, entry2));
    assertEquals(1, entryComparator.compare(entry2, entry1));
    assertEquals(0, entryComparator.compare(entry1, entry1));
    assertEquals(0, entryComparator.compare(entry2, entry2));
    assertEquals(0, entryComparator.compare(entry1, new AbstractMap.SimpleEntry<>("a", "A")));
    assertEquals(0, entryComparator.compare(entry2, new AbstractMap.SimpleEntry<>("b", "B")));
  }

  public void testEntryComparingByKeyWithComparator() {
    final Comparator<Map.Entry<String, String>> entryComparator =
        Map.Entry.comparingByKey(Collections.reverseOrder());

    AbstractMap.SimpleEntry<String, String> entry1 = new AbstractMap.SimpleEntry<>("a", "A");
    AbstractMap.SimpleEntry<String, String> entry2 = new AbstractMap.SimpleEntry<>("b", "B");

    assertEquals(1, entryComparator.compare(entry1, entry2));
    assertEquals(-1, entryComparator.compare(entry2, entry1));
    assertEquals(0, entryComparator.compare(entry1, entry1));
    assertEquals(0, entryComparator.compare(entry2, entry2));
    assertEquals(0, entryComparator.compare(entry1, new AbstractMap.SimpleEntry<>("a", "A")));
    assertEquals(0, entryComparator.compare(entry2, new AbstractMap.SimpleEntry<>("b", "B")));
  }

  public void testEntryComparingByValue() {
    final Comparator<Map.Entry<String, String>> valueComparator = Map.Entry.comparingByValue();

    AbstractMap.SimpleEntry<String, String> entry1 = new AbstractMap.SimpleEntry<>("a", "A");
    AbstractMap.SimpleEntry<String, String> entry2 = new AbstractMap.SimpleEntry<>("b", "B");

    assertEquals(-1, valueComparator.compare(entry1, entry2));
    assertEquals(1, valueComparator.compare(entry2, entry1));
    assertEquals(0, valueComparator.compare(entry1, entry1));
    assertEquals(0, valueComparator.compare(entry2, entry2));
    assertEquals(0, valueComparator.compare(entry1, new AbstractMap.SimpleEntry<>("a", "A")));
    assertEquals(0, valueComparator.compare(entry2, new AbstractMap.SimpleEntry<>("b", "B")));
  }

  public void testEntryComparingByValueWithComparator() {
    final Comparator<Map.Entry<String, String>> valueComparator =
        Map.Entry.comparingByValue(Collections.reverseOrder());

    AbstractMap.SimpleEntry<String, String> entry1 = new AbstractMap.SimpleEntry<>("a", "A");
    AbstractMap.SimpleEntry<String, String> entry2 = new AbstractMap.SimpleEntry<>("b", "B");

    assertEquals(1, valueComparator.compare(entry1, entry2));
    assertEquals(-1, valueComparator.compare(entry2, entry1));
    assertEquals(0, valueComparator.compare(entry1, entry1));
    assertEquals(0, valueComparator.compare(entry2, entry2));
    assertEquals(0, valueComparator.compare(entry1, new AbstractMap.SimpleEntry<>("a", "A")));
    assertEquals(0, valueComparator.compare(entry2, new AbstractMap.SimpleEntry<>("b", "B")));
  }

  @Override
  protected Map<String, String> createMap() {
    return new TestMap<>();
  }

  private static class TestMap<K, V> implements Map<K, V> {
    private final Map<K, V> storage = new HashMap<>();

    @Override
    public int size() {
      return storage.size();
    }

    @Override
    public boolean isEmpty() {
      return storage.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
      return storage.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
      return storage.containsValue(value);
    }

    @Override
    public V get(Object key) {
      return storage.get(key);
    }

    @Override
    public V put(K key, V value) {
      return storage.put(key, value);
    }

    @Override
    public V remove(Object key) {
      return storage.remove(key);
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> m) {
      storage.putAll(m);
    }

    @Override
    public void clear() {
      storage.clear();
    }

    @Override
    public Set<K> keySet() {
      return storage.keySet();
    }

    @Override
    public Collection<V> values() {
      return storage.values();
    }

    @Override
    public Set<Entry<K, V>> entrySet() {
      return storage.entrySet();
    }
  }
}
