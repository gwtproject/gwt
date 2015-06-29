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
 * A simple wrapper around JavaScriptObject to provide {@link java.util.Map}-like semantics for any
 * key type.
 *
 * @param <K> key type
 * @param <V> value type
 */
public interface InternalJsHashCodeMapInterface<K, V> {

  Object createMap();

  V put(K key, V value);

  V remove(Object key);

  Map.Entry<K, V> getEntry(Object key);

  boolean containsValue(Object value);

  Iterator<Entry<K, V>> entries();
  
  void setHost(AbstractHashMap<K, V> host);
}
