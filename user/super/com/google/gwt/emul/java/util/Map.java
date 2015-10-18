/*
 * Copyright 2007 Google Inc.
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
package java.util;

import static javaemul.internal.InternalPreconditions.checkCriticalNotNull;

import java.io.Serializable;

/**
 * Abstract interface for maps.
 *
 * @param <K> key type.
 * @param <V> value type.
 */
public interface Map<K, V> {

  /**
   * Represents an individual map entry.
   */
  interface Entry<K, V> {
    @Override
    boolean equals(Object o);

    K getKey();

    V getValue();

    @Override
    int hashCode();

    V setValue(V value);

    static <K extends Comparable<? super K>, V> Comparator<Map.Entry<K,V>> comparingByKey() {
      return (Comparator<Map.Entry<K, V>> & Serializable)
          (a, b) -> a.getKey().compareTo(b.getKey());
    }

    static <K, V extends Comparable<? super V>> Comparator<Map.Entry<K,V>> comparingByValue() {
      return (Comparator<Map.Entry<K, V>> & Serializable)
          (a, b) -> a.getValue().compareTo(b.getValue());
    }

    static <K, V> Comparator<Map.Entry<K, V>> comparingByKey(Comparator<? super K> cmp) {
      checkCriticalNotNull(cmp);
      return (Comparator<Map.Entry<K, V>> & Serializable)
          (a, b) -> cmp.compare(a.getKey(), b.getKey());
    }
  }

  void clear();

  boolean containsKey(Object key);

  boolean containsValue(Object value);

  Set<Entry<K, V>> entrySet();

  @Override
  boolean equals(Object o);

  V get(Object key);

  @Override
  int hashCode();

  boolean isEmpty();

  Set<K> keySet();

  V put(K key, V value);

  void putAll(Map<? extends K, ? extends V> t);

  V remove(Object key);

  int size();

  Collection<V> values();
}
