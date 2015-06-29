/*
 * Copyright 2015 Google Inc.
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

import java.util.Map.Entry;

/**
 * A simple wrapper around JavaScriptObject to provide {@link java.util.Map}-like semantics where
 * the key type is string.
 *
 * @param <K> key type
 * @param <V> value type
 */
public interface InternalJsStringMapInterface<K, V> {

  Object createMap();

  boolean contains(String key);

  V get(String key);

  V put(String key, V value);

  V remove(String key);

  boolean containsValue(Object value);

  Iterator<Entry<K, V>> entries();

  String[] keys();

  Entry<K, V> newMapEntry(final String key);

  /**
   * Bridge method from JSNI that keeps us from having to make polymorphic calls
   * in JSNI. By putting the polymorphism in Java code, the compiler can do a
   * better job of optimizing in most cases.
   */
  boolean equalsBridge(Object value1, Object value2);
  
  void setHost(AbstractHashMap<K, V> host);
}
