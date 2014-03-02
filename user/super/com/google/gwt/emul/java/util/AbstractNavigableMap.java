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

/**
 * Skeletal implementation of a NavigableMap.
 */
abstract class AbstractNavigableMap<K, V> extends AbstractMap<K, V>
    implements NavigableMap<K, V> {

  class DescendingMap extends AbstractNavigableMap<K, V> {
    @Override
    public Entry<K, V> ceilingEntry(K key) {
      return ascendingMap().floorEntry(key);
    }

    @Override
    public Comparator<? super K> comparator() {
      return Collections.reverseOrder(ascendingMap().comparator());
    }

    @Override
    public NavigableMap<K, V> descendingMap() {
      return ascendingMap();
    }

    @Override
    public Entry<K, V> firstEntry() {
      return ascendingMap().lastEntry();
    }

    @Override
    public Entry<K, V> floorEntry(K key) {
      return ascendingMap().ceilingEntry(key);
    }

    @Override
    public NavigableMap<K, V> headMap(K toKey, boolean inclusive) {
      return ascendingMap().tailMap(toKey, inclusive).descendingMap();
    }

    @Override
    public Entry<K, V> higherEntry(K key) {
      return ascendingMap().lowerEntry(key);
    }

    @Override
    public Entry<K, V> lastEntry() {
      return ascendingMap().firstEntry();
    }

    @Override
    public Entry<K, V> lowerEntry(K key) {
      return ascendingMap().higherEntry(key);
    }

    @Override
    public V put(K key, V value) {
      return ascendingMap().put(key, value);
    }

    @Override
    public V remove(Object key) {
      return ascendingMap().remove(key);
    }

    @Override
    public int size() {
      return ascendingMap().size();
    }

    @Override
    public NavigableMap<K, V> subMap(
        K fromKey, boolean fromInclusive, K toKey, boolean toInclusive) {
      return ascendingMap().subMap(toKey, toInclusive, fromKey, fromInclusive)
          .descendingMap();
    }

    @Override
    public NavigableMap<K, V> tailMap(K fromKey, boolean inclusive) {
      return ascendingMap().headMap(fromKey, inclusive).descendingMap();
    }

    AbstractNavigableMap<K, V> ascendingMap() {
      return AbstractNavigableMap.this;
    }

    @Override
    Iterator<Entry<K, V>> descendingEntryIterator() {
      return ascendingMap().entryIterator();
    }

    @Override
    Iterator<Entry<K, V>> entryIterator() {
      return ascendingMap().descendingEntryIterator();
    }

    @Override
    Entry<K, V> getEntry(K key) {
      return ascendingMap().getEntry(key);
    }
  }

  class EntrySet extends AbstractSet<Entry<K, V>> {
    @SuppressWarnings("unchecked")
    @Override
    public boolean contains(Object o) {
      if (!(o instanceof Entry)) {
        return false;
      }
      Entry<K, V> entry = (Entry<K, V>) o;
      Entry<K, V> lookupEntry = getEntry(entry.getKey());
      return lookupEntry != null && Objects.equals(lookupEntry.getValue(), entry.getValue());
    }

    @Override
    public Iterator<Entry<K, V>> iterator() {
      return entryIterator();
    }

    @Override
    public boolean remove(Object o) {
      if (contains(o)) {
        Entry<?, ?> entry = (Entry<?, ?>) o;
        AbstractNavigableMap.this.remove(entry.getKey());
        return true;
      }
      return false;
    }

    @Override
    public int size() {
      return AbstractNavigableMap.this.size();
    }
  }

  private static final class NavigableKeySet<K, V> extends AbstractSet<K>
      implements NavigableSet<K> {

    private final NavigableMap<K, V> map;

    NavigableKeySet(NavigableMap<K, V> map) {
      this.map = map;
    }

    @Override
    public K ceiling(K k) {
      return map.ceilingKey(k);
    }

    @Override
    public Comparator<? super K> comparator() {
      return map.comparator();
    }

    @Override
    public Iterator<K> descendingIterator() {
      return descendingSet().iterator();
    }

    @Override
    public NavigableSet<K> descendingSet() {
      return new NavigableKeySet<K, V>(map.descendingMap());
    }

    @Override
    public K first() {
      return map.firstKey();
    }

    @Override
    public K floor(K k) {
      return map.floorKey(k);
    }

    @Override
    public SortedSet<K> headSet(K toElement) {
      return headSet(toElement, false);
    }

    @Override
    public NavigableSet<K> headSet(K toElement, boolean inclusive) {
      return map.headMap(toElement, inclusive).navigableKeySet();
    }

    @Override
    public K higher(K k) {
      return map.higherKey(k);
    }

    @Override
    public Iterator<K> iterator() {
      return new Iterator<K>() {
        final Iterator<Entry<K, V>> entryIterator = map.entrySet().iterator();

        @Override
        public boolean hasNext() {
          return entryIterator.hasNext();
        }

        @Override
        public K next() {
          return entryIterator.next().getKey();
        }

        @Override
        public void remove() {
          entryIterator.remove();
        }
      };
    }

    @Override
    public K last() {
      return map.lastKey();
    }

    @Override
    public K lower(K k) {
      return map.lowerKey(k);
    }

    @Override
    public K pollFirst() {
      return getKeyOrNull(map.pollFirstEntry());
    }

    @Override
    public K pollLast() {
      return getKeyOrNull(map.pollLastEntry());
    }

    @Override
    public int size() {
      return map.size();
    }

    @Override
    public NavigableSet<K> subSet(
        K fromElement, boolean fromInclusive, K toElement, boolean toInclusive) {
      return map.subMap(fromElement, fromInclusive, toElement, toInclusive)
          .navigableKeySet();
    }

    @Override
    public SortedSet<K> subSet(K fromElement, K toElement) {
      return subSet(fromElement, true, toElement, false);
    }

    @Override
    public SortedSet<K> tailSet(K fromElement) {
      return tailSet(fromElement, true);
    }

    @Override
    public NavigableSet<K> tailSet(K fromElement, boolean inclusive) {
      return map.tailMap(fromElement, inclusive).navigableKeySet();
    }
  }

  private static <K, V> K getKeyOrNSE(Entry<K, V> entry) {
    if (entry == null) {
      throw new NoSuchElementException();
    }
    return entry.getKey();
  }

  private static <K, V> K getKeyOrNull(Entry<K, V> entry) {
    return entry == null ? null : entry.getKey();
  }

  @Override
  public K ceilingKey(K key) {
    return getKeyOrNull(ceilingEntry(key));
  }

  @Override
  public NavigableSet<K> descendingKeySet() {
    return descendingMap().navigableKeySet();
  }

  @Override
  public NavigableMap<K, V> descendingMap() {
    return new DescendingMap();
  }

  @Override
  public Set<Entry<K, V>> entrySet() {
    return new EntrySet();
  }

  @Override
  public K firstKey() {
    return getKeyOrNSE(firstEntry());
  }

  @Override
  public K floorKey(K key) {
    return getKeyOrNull(floorEntry(key));
  }

  @Override
  public SortedMap<K, V> headMap(K toKey) {
    return headMap(toKey, false);
  }

  @Override
  public K higherKey(K key) {
    return getKeyOrNull(higherEntry(key));
  }

  @Override
  public Set<K> keySet() {
    return navigableKeySet();
  }

  @Override
  public K lastKey() {
    return getKeyOrNSE(lastEntry());
  }

  @Override
  public K lowerKey(K key) {
    return getKeyOrNull(lowerEntry(key));
  }

  @Override
  public NavigableSet<K> navigableKeySet() {
    return new NavigableKeySet<K, V>(this);
  }

  @Override
  public Entry<K, V> pollFirstEntry() {
    return pollEntry(firstEntry());
  }

  @Override
  public Entry<K, V> pollLastEntry() {
    return pollEntry(lastEntry());
  }

  @Override
  public abstract int size();

  @Override
  public SortedMap<K, V> subMap(K fromKey, K toKey) {
    return subMap(fromKey, true, toKey, false);
  }

  @Override
  public SortedMap<K, V> tailMap(K fromKey) {
    return tailMap(fromKey, true);
  }

  /**
   * Returns an iterator over the entries in this map in descending order.
   */
  abstract Iterator<Entry<K, V>> descendingEntryIterator();

  /**
   * Returns an iterator over the entries in this map in ascending order.
   */
  abstract Iterator<Entry<K, V>> entryIterator();

  /**
   * Finds an entry given a key and returns the node.
   */
  abstract Entry<K, V> getEntry(K key);

  private Entry<K, V> pollEntry(Entry<K, V> entry) {
    if (entry == null) {
      return null;
    }

    final K key = entry.getKey();
    final V value = entry.getValue();
    remove(key);
    return new SimpleImmutableEntry<K, V>(key, value);
  }
}
