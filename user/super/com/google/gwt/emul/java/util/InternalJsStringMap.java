/*
 * Copyright 2014 Google Inc.
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

import static com.google.gwt.core.shared.impl.InternalPreconditions.checkElement;
import static com.google.gwt.core.shared.impl.InternalPreconditions.checkState;
import static java.util.ConcurrentModificationDetector.structureChanged;

import java.util.ES6MapFactory.ES6Iterator;
import java.util.ES6MapFactory.ES6IteratorEntry;
import java.util.ES6MapFactory.ES6Map;
import java.util.Map.Entry;

/**
 * A simple wrapper around JavaScript Map for key type is string.
 */
class InternalJsStringMap<K, V> implements Iterable<Entry<K, V>> {

  private AbstractHashMap<K, V> host;
  private final ES6Map<V> backingMap;

  public InternalJsStringMap(ES6Map<V> backingMap, AbstractHashMap<K, V> host) {
    this.backingMap = backingMap;
    this.host = host;
  }

  public boolean contains(String key) {
    return !isUndefined(backingMap.get(key));
  }

  public V get(String key) {
    return backingMap.get(key);
  }

  public V put(String key, V value) {
    V oldValue = backingMap.get(key);

    if (isUndefined(oldValue)) {
      structureChanged(host);
    }

    backingMap.set(key, toNullIfUndefined(value));

    return oldValue;
  }

  public V remove(String key) {
    V value = backingMap.get(key);
    if (!isUndefined(value)) {
      backingMap.delete(key);
      structureChanged(host);
    }

    return value;
  }

  public int size() {
    return backingMap.size();
  }

  @Override
  public Iterator<Entry<K, V>> iterator() {
    return new Iterator<Map.Entry<K,V>>() {
      ES6Iterator<V> entries = backingMap.entries();
      ES6IteratorEntry<V> current = entries.next();
      ES6IteratorEntry<V> last;

      @Override
      public boolean hasNext() {
        return !current.done();
      }
      @Override
      public Entry<K, V> next() {
        checkElement(hasNext());

        last = current;
        current = entries.next();
        return newMapEntry(last.getKey());
      }
      @Override
      public void remove() {
        checkState(last != null);

        InternalJsStringMap.this.remove(last.getKey());
        last = null;
      }
    };
  }

  private Entry<K, V> newMapEntry(final String key) {
    return new AbstractMapEntry<K, V>() {
      @SuppressWarnings("unchecked")
      @Override
      public K getKey() {
        return (K) key;
      }
      @Override
      public V getValue() {
        return get(key);
      }
      @Override
      public V setValue(V object) {
        return put(key, object);
      }
    };
  }

  private static <T> T toNullIfUndefined(T value) {
    return isUndefined(value) ? null : value;
  }

  private static native boolean isUndefined(Object value) /*-{
    return value === undefined;
  }-*/;
}
