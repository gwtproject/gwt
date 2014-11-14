/*
 * Copyright 2008 Google Inc.
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

import static com.google.gwt.core.shared.impl.InternalPreconditions.checkArgument;
import static com.google.gwt.core.shared.impl.InternalPreconditions.checkElement;
import static com.google.gwt.core.shared.impl.InternalPreconditions.checkState;

import static java.util.ConcurrentModificationDetector.checkStructuralChange;
import static java.util.ConcurrentModificationDetector.recordLastKnownStructure;
import static java.util.ConcurrentModificationDetector.structureChanged;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.impl.SpecializeMethod;

/**
 * Implementation of Map interface based on a hash table. <a
 * href="http://java.sun.com/j2se/1.5.0/docs/api/java/util/HashMap.html">[Sun
 * docs]</a>
 * 
 * @param <K> key type
 * @param <V> value type
 */
abstract class AbstractHashMap<K, V> extends AbstractMap<K, V> {

  private final class EntrySet extends AbstractSet<Entry<K, V>> {

    @Override
    public void clear() {
      AbstractHashMap.this.clear();
    }

    @Override
    public boolean contains(Object o) {
      if (o instanceof Map.Entry) {
        return containsEntry((Map.Entry<?, ?>) o);
      }
      return false;
    }

    @Override
    public Iterator<Entry<K, V>> iterator() {
      return new EntrySetIterator();
    }

    @Override
    public boolean remove(Object entry) {
      if (contains(entry)) {
        Object key = ((Map.Entry<?, ?>) entry).getKey();
        AbstractHashMap.this.remove(key);
        return true;
      }
      return false;
    }

    @Override
    public int size() {
      return AbstractHashMap.this.size();
    }
  }

  /**
   * Iterator for <code>EntrySet</code>.
   */
  private final class EntrySetIterator implements Iterator<Entry<K, V>> {
    private Iterator<Entry<K, V>> stringMapEntries = stringMap.entries();
    private Iterator<Entry<K, V>> current = stringMapEntries;
    private Iterator<Entry<K, V>> last;

    public EntrySetIterator() {
      recordLastKnownStructure(AbstractHashMap.this, this);
    }

    @Override
    public boolean hasNext() {
      if (current.hasNext()) {
        return true;
      }
      if (current != stringMapEntries) {
        return false;
      }
      current = hashCodeMap.entries();
      return current.hasNext();
    }

    @Override
    public Entry<K, V> next() {
      checkStructuralChange(AbstractHashMap.this, this);
      checkElement(hasNext());

      last = current;
      return current.next();
    }

    @Override
    public void remove() {
      checkState(last != null);
      checkStructuralChange(AbstractHashMap.this, this);

      last.remove();
      last = null;
      recordLastKnownStructure(AbstractHashMap.this, this);
    }
  }

  /**
   * A map of integral hashCodes onto entries.
   */
  private transient InternalJsHashCodeMap<K, V> hashCodeMap;

  /**
   * A map of Strings onto values.
   */
  private transient InternalJsStringMap<K, V> stringMap;

  private int size;

  public AbstractHashMap() {
    reset();
  }

  public AbstractHashMap(int ignored) {
    // This implementation of HashMap has no need of initial capacities.
    this(ignored, 0);
  }

  public AbstractHashMap(int ignored, float alsoIgnored) {
    // This implementation of HashMap has no need of load factors or capacities.
    checkArgument(ignored >= 0, "Negative initial capacity");
    checkArgument(alsoIgnored >= 0, "Non-positive load factor");

    reset();
  }

  public AbstractHashMap(Map<? extends K, ? extends V> toBeCopied) {
    reset();
    this.putAll(toBeCopied);
  }

  @Override
  public void clear() {
    reset();
  }

  private void reset() {
    InternalJsMapFactory factory = GWT.create(InternalJsMapFactory.class);
    hashCodeMap = factory.createJsHashCodeMap();
    hashCodeMap.host = this;
    stringMap = factory.createJsStringMap();
    stringMap.host = this;
    size = 0;
    structureChanged(this);
  }

  @SpecializeMethod(params = {String.class}, target = "hasStringValue")
  @Override
  public boolean containsKey(Object key) {
    return key instanceof String ? hasStringValue(unsafeCast(key)) : hasHashValue(key);
  }

  @Override
  public boolean containsValue(Object value) {
    return stringMap.containsValue(value) || hashCodeMap.containsValue(value);
  }

  @Override
  public Set<Map.Entry<K, V>> entrySet() {
    return new EntrySet();
  }

  @SpecializeMethod(params = {String.class}, target = "getStringValue")
  @Override
  public V get(Object key) {
    return key instanceof String ? getStringValue(unsafeCast(key)) : getHashValue(key);
  }

  @SpecializeMethod(params = {String.class, Object.class}, target = "putStringValue")
  @Override
  public V put(K key, V value) {
    return key instanceof String ? putStringValue(unsafeCast(key), value) : putHashValue(key, value);
  }

  @SpecializeMethod(params = {String.class}, target = "removeStringValue")
  @Override
  public V remove(Object key) {
    return key instanceof String ? removeStringValue(unsafeCast(key)) : removeHashValue(key);
  }

  @Override
  public int size() {
    return size;
  }

  void elementRemoved() {
    size--;
    structureChanged(this);
  }

  void elementAdded() {
    size++;
    structureChanged(this);
  }

  /**
   * Subclasses must override to return a whether or not two keys or values are
   * equal.
   */
  abstract boolean equals(Object value1, Object value2);

  /**
   * Subclasses must override to return a hash code for a given key. The key is
   * guaranteed to be non-null and not a String.
   */
  abstract int getHashCode(Object key);

  /**
   * Returns the Map.Entry whose key is Object equal to <code>key</code>,
   * provided that <code>key</code>'s hash code is <code>hashCode</code>;
   * or <code>null</code> if no such Map.Entry exists at the specified
   * hashCode.
   */
  private V getHashValue(Object key) {
    return getEntryValueOrNull(hashCodeMap.getEntry(key));
  }

  /**
   * Returns the value for the given key in the stringMap. Returns
   * <code>null</code> if the specified key does not exist.
   */
  private V getStringValue(String key) {
    return key == null ? getHashValue(null) : stringMap.get(key);
  }

  /**
   * Returns true if the a key exists in the hashCodeMap that is Object equal to
   * <code>key</code>, provided that <code>key</code>'s hash code is
   * <code>hashCode</code>.
   */
  private boolean hasHashValue(Object key) {
    return hashCodeMap.getEntry(key) != null;
  }

  /**
   * Returns true if the given key exists in the stringMap.
   */
  private boolean hasStringValue(String key) {
    return key == null ? hasHashValue(null) : stringMap.contains(key);
  }

  /**
   * Sets the specified key to the specified value in the hashCodeMap. Returns
   * the value previously at that key. Returns <code>null</code> if the
   * specified key did not exist.
   */
  private V putHashValue(K key, V value) {
    return hashCodeMap.put(key, value);
  }

  /**
   * Sets the specified key to the specified value in the stringMap. Returns the
   * value previously at that key. Returns <code>null</code> if the specified
   * key did not exist.
   */
  private V putStringValue(String key, V value) {
    return key == null ? putHashValue(null, value) : stringMap.put(key, value);
  }

  /**
   * Removes the pair whose key is Object equal to <code>key</code> from
   * <code>hashCodeMap</code>, provided that <code>key</code>'s hash code
   * is <code>hashCode</code>. Returns the value that was associated with the
   * removed key, or null if no such key existed.
   */
  private V removeHashValue(Object key) {
    return hashCodeMap.remove(key);
  }

  /**
   * Removes the specified key from the stringMap and returns the value that was
   * previously there. Returns <code>null</code> if the specified key does not
   * exist.
   */
  private V removeStringValue(String key) {
    return key == null ? removeHashValue(null) : stringMap.remove(key);
  }

  // TODO(goktug): replace unsafeCast with a real cast when the compiler can optimize it.
  private static native String unsafeCast(Object string) /*-{
    return string;
  }-*/;
}
