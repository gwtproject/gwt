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

import static javaemul.internal.InternalPreconditions.checkArgument;
import static javaemul.internal.InternalPreconditions.checkNotNull;

import java.io.Serializable;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;
import jsinterop.annotations.JsIgnore;
import jsinterop.annotations.JsNonNull;
import jsinterop.annotations.JsType;

/**
 * Abstract interface for maps.
 *
 * @param <K> key type.
 * @param <V> value type.
 */
@JsType
public interface Map<K, V> {

  @JsIgnore
  static <K, V> Map<K, V> of() {
    return Collections.unmodifiableMap(Collections.emptyMap());
  }

  @JsIgnore
  static <K, V> Map<K, V> of(K key, V value) {
    return ofEntries(
            entry(key, value)
    );
  }

  @JsIgnore
  static <K, V> Map<K, V> of(K k1, V v1, K k2, V v2) {
    return ofEntries(
            entry(k1, v1),
            entry(k2, v2)
    );
  }

  @JsIgnore
  static <K, V> Map<K, V> of(
      K k1, V v1,
      K k2, V v2,
      K k3, V v3
  ) {
    return ofEntries(
            entry(k1, v1),
            entry(k2, v2),
            entry(k3, v3)
    );
  }

  @JsIgnore
  static <K, V> Map<K, V> of(
      K k1, V v1,
      K k2, V v2,
      K k3, V v3,
      K k4, V v4
  ) {
    return ofEntries(
            entry(k1, v1),
            entry(k2, v2),
            entry(k3, v3),
            entry(k4, v4)
    );
  }

  @JsIgnore
  static <K, V> Map<K, V> of(
      K k1, V v1,
      K k2, V v2,
      K k3, V v3,
      K k4, V v4,
      K k5, V v5
  ) {
    return ofEntries(
            entry(k1, v1),
            entry(k2, v2),
            entry(k3, v3),
            entry(k4, v4),
            entry(k5, v5)
    );
  }

  @JsIgnore
  static <K, V> Map<K, V> of(
      K k1, V v1,
      K k2, V v2,
      K k3, V v3,
      K k4, V v4,
      K k5, V v5,
      K k6, V v6
  ) {
    return ofEntries(
            entry(k1, v1),
            entry(k2, v2),
            entry(k3, v3),
            entry(k4, v4),
            entry(k5, v5),
            entry(k6, v6)
    );
  }

  @JsIgnore
  static <K, V> Map<K, V> of(
      K k1, V v1,
      K k2, V v2,
      K k3, V v3,
      K k4, V v4,
      K k5, V v5,
      K k6, V v6,
      K k7, V v7
  ) {
    return ofEntries(
            entry(k1, v1),
            entry(k2, v2),
            entry(k3, v3),
            entry(k4, v4),
            entry(k5, v5),
            entry(k6, v6),
            entry(k7, v7)
    );
  }

  @JsIgnore
  static <K, V> Map<K, V> of(
      K k1, V v1,
      K k2, V v2,
      K k3, V v3,
      K k4, V v4,
      K k5, V v5,
      K k6, V v6,
      K k7, V v7,
      K k8, V v8
  ) {
    return ofEntries(
            entry(k1, v1),
            entry(k2, v2),
            entry(k3, v3),
            entry(k4, v4),
            entry(k5, v5),
            entry(k6, v6),
            entry(k7, v7),
            entry(k8, v8)
    );
  }

  @JsIgnore
  static <K, V> Map<K, V> of(
      K k1, V v1,
      K k2, V v2,
      K k3, V v3,
      K k4, V v4,
      K k5, V v5,
      K k6, V v6,
      K k7, V v7,
      K k8, V v8,
      K k9, V v9
  ) {
    return ofEntries(
            entry(k1, v1),
            entry(k2, v2),
            entry(k3, v3),
            entry(k4, v4),
            entry(k5, v5),
            entry(k6, v6),
            entry(k7, v7),
            entry(k8, v8),
            entry(k9, v9)
    );
  }

  @JsIgnore
  static <K, V> Map<K, V> of(
      K k1, V v1,
      K k2, V v2,
      K k3, V v3,
      K k4, V v4,
      K k5, V v5,
      K k6, V v6,
      K k7, V v7,
      K k8, V v8,
      K k9, V v9,
      K k10, V v10
  ) {
    return ofEntries(
            entry(k1, v1),
            entry(k2, v2),
            entry(k3, v3),
            entry(k4, v4),
            entry(k5, v5),
            entry(k6, v6),
            entry(k7, v7),
            entry(k8, v8),
            entry(k9, v9),
            entry(k10, v10)
    );
  }

  @JsIgnore
  static <K, V> Entry<K, V> entry(K key, V value) {
    // This isn't quite consistent with the javadoc, since this is serializable, while entry()
    // need not be serializable.
    return new AbstractMap.SimpleImmutableEntry<>(
        checkNotNull(key),
        checkNotNull(value)
    );
  }

  @JsIgnore
  static <K, V> Map<K, V> ofEntries(Entry<? extends K,? extends V>... entries) {
    Map<K, V> map = new HashMap<>();

    for (int i = 0; i < entries.length; i++) {
      // TODO this perhaps can be optimized if we know the entry is an instance of
      //  AbstractMap.SimpleImmutableEntry, or something more specialized?
      Entry<? extends K, ? extends V> entry = checkNotNull(entries[i]);
      checkArgument(map.put(checkNotNull(entry.getKey()), checkNotNull(entry.getValue())) == null,
              "Can't add multiple entries with the same key");
    }

    return Collections.unmodifiableMap(map);
  }

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
      return comparingByKey(Comparator.naturalOrder());
    }

    static <K, V> Comparator<Map.Entry<K, V>> comparingByKey(Comparator<? super K> cmp) {
      checkNotNull(cmp);
      return (Comparator<Map.Entry<K, V>> & Serializable)
          (a, b) -> cmp.compare(a.getKey(), b.getKey());
    }

    static <K, V extends Comparable<? super V>> Comparator<Map.Entry<K,V>> comparingByValue() {
      return comparingByValue(Comparator.naturalOrder());
    }

    static <K, V> Comparator<Map.Entry<K, V>> comparingByValue(Comparator<? super V> cmp) {
      checkNotNull(cmp);
      return (Comparator<Map.Entry<K, V>> & Serializable)
          (a, b) -> cmp.compare(a.getValue(), b.getValue());
    }

    static <K,V> Map.Entry<K,V> copyOf(Map.Entry<? extends K,? extends V> e) {
      if (e instanceof AbstractMap.SimpleImmutableEntry) {
        return (Entry<K, V>) e;
      }
      return entry(e.getKey(), e.getValue());
    }
  }

  void clear();

  @JsIgnore
  default V compute(K key, BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
    checkNotNull(remappingFunction);

    V value = remappingFunction.apply(key, get(key));
    if (value != null) {
      put(key, value);
    } else {
      remove(key);
    }
    return value;
  }

  @JsIgnore
  default V computeIfAbsent(K key, Function<? super K, ? extends V> remappingFunction) {
    checkNotNull(remappingFunction);

    V value = get(key);
    if (value == null) {
      value = remappingFunction.apply(key);
      if (value != null) {
        put(key, value);
      }
    }
    return value;
  }

  @JsIgnore
  default V computeIfPresent(
      K key, BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
    checkNotNull(remappingFunction);

    V value = get(key);
    if (value != null) {
      value = remappingFunction.apply(key, value);
      if (value != null) {
        put(key, value);
      } else {
        remove(key);
      }
    }
    return value;
  }

  boolean containsKey(Object key);

  boolean containsValue(Object value);

  @JsIgnore
  Set<Entry<K, V>> entrySet();

  @JsIgnore
  default void forEach(BiConsumer<? super K, ? super V> consumer) {
    checkNotNull(consumer);
    for (Entry<K, V> entry : entrySet()) {
      consumer.accept(entry.getKey(), entry.getValue());
    }
  }

  V get(Object key);

  default V getOrDefault(Object key, V defaultValue) {
    V currentValue = get(key);
    return (currentValue == null && !containsKey(key)) ? defaultValue : currentValue;
  }

  boolean isEmpty();

  @JsNonNull
  Set<K> keySet();

  @JsIgnore
  default V merge(K key, V value, BiFunction<? super V, ? super V, ? extends V> remappingFunction) {
    checkNotNull(remappingFunction);
    checkNotNull(value);

    V currentValue = get(key);
    V newValue = currentValue == null ? value : remappingFunction.apply(currentValue, value);
    if (newValue == null) {
      remove(key);
    } else {
      put(key, newValue);
    }
    return newValue;
  }

  V put(K key, V value);

  default V putIfAbsent(K key, V value) {
    V currentValue = get(key);
    return currentValue != null ? currentValue : put(key, value);
  }

  void putAll(Map<? extends K, ? extends V> t);

  V remove(Object key);

  @JsIgnore
  default boolean remove(Object key, Object value) {
    Object currentValue = get(key);
    if (!Objects.equals(currentValue, value) || (currentValue == null && !containsKey(key))) {
      return false;
    }
    remove(key);
    return true;
  }

  default V replace(K key, V value) {
    return containsKey(key) ? put(key, value) : null;
  }

  @JsIgnore
  default boolean replace(K key, V oldValue, V newValue) {
    Object currentValue = get(key);
    if (!Objects.equals(currentValue, oldValue) || (currentValue == null && !containsKey(key))) {
      return false;
    }
    put(key, newValue);
    return true;
  }

  @JsIgnore
  default void replaceAll(BiFunction<? super K, ? super V, ? extends V> function) {
    checkNotNull(function);
    for (Entry<K, V> entry : entrySet()) {
      entry.setValue(function.apply(entry.getKey(), entry.getValue()));
    }
  }

  int size();

  @JsNonNull Collection<V> values();

  static <K,V> Map<K,V> copyOf(Map<? extends K,? extends V> map) {
    // TODO if the given map is immutable and has no nulls, return it
    return map.entrySet().stream().collect(Collectors.toUnmodifiableMap(Entry::getKey,
        Entry::getValue));
  }
}
