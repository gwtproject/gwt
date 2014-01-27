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
package com.google.gwt.core.ext.linker;

import com.google.gwt.thirdparty.guava.common.base.Supplier;
import com.google.gwt.thirdparty.guava.common.collect.Multimap;
import com.google.gwt.thirdparty.guava.common.collect.Multimaps;

import java.io.ObjectInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * Provides stable ordering and de-duplication of artifacts.
 */
public final class ArtifactSet implements SortedSet<Artifact<?>>, Serializable {

  private static final Supplier<SortedSet<Artifact<?>>> TREE_SETS =
      new Supplier<SortedSet<Artifact<?>>>() {
    @Override
    public SortedSet<Artifact<?>> get() {
      return new TreeSet<Artifact<?>>();
    }
  };

  private SortedSet<Artifact<?>> treeSet = new TreeSet<Artifact<?>>();
  private transient Multimap<Class<?>, Artifact<?>> byType = createTypeMap();
  private transient Map<Class<?>, SortedSet<?>> cache = null;

  public ArtifactSet() {
  }

  public ArtifactSet(Collection<? extends Artifact<?>> copyFrom) {
    addAll(copyFrom);
  }

  private static Multimap<Class<?>, Artifact<?>> createTypeMap() {
    return Multimaps.newSortedSetMultimap(
        new HashMap<Class<?>, Collection<Artifact<?>>>(), TREE_SETS);
  }

  private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
    in.defaultReadObject();
    byType = createTypeMap();
    for (Artifact a : treeSet) {
      byType.put(a.getClass(), a);
    }
  }

  @Override
  public boolean add(Artifact<?> o) {
    if (treeSet.add(o)) {
      cache = null;
      byType.put(o.getClass(), o);
      return true;
    }
    return false;
  }

  @Override
  public boolean addAll(Collection<? extends Artifact<?>> c) {
    if (treeSet.addAll(c)) {
      cache = null;
      for (Artifact<?> a : c) {
        byType.put(a.getClass(), a);
      }
    }
    return false;
  }

  @Override
  public void clear() {
    cache = null;
    treeSet.clear();
    byType.clear();
  }

  @Override
  public Comparator<? super Artifact<?>> comparator() {
    return treeSet.comparator();
  }

  @Override
  public boolean contains(Object o) {
    return treeSet.contains(o);
  }

  @Override
  public boolean containsAll(Collection<?> c) {
    return treeSet.containsAll(c);
  }

  @Override
  public boolean equals(Object o) {
    return treeSet.equals(o);
  }

  /**
   * Find all Artifacts assignable to some base type. The returned value will be
   * a snapshot of the values in the ArtifactSet. An example of how this could
   * be used:
   * 
   * <pre>
   *   for (EmittedArtifact ea : artifactSet.find(EmittedArtifact.class)) {
   *     ...
   *   }
   * </pre>
   * 
   * @param <T> the desired type of Artifact
   * @param artifactType the desired type of Artifact
   * @return all Artifacts in the ArtifactSet assignable to the desired type
   */
  public <T extends Artifact<? super T>> SortedSet<T> find(
      Class<T> artifactType) {
    if (cache == null) {
      cache = new HashMap<Class<?>, SortedSet<?>>();
    } else {
      SortedSet<?> s = cache.get(artifactType);
      if (s != null) {
        return (SortedSet<T>) s;
      }
    }

    SortedSet<T> toReturn = new TreeSet();
    for (Map.Entry<Class<?>, Collection<Artifact<?>>> entry : byType.asMap().entrySet()) {
      if (artifactType.isAssignableFrom(entry.getKey())) {
        toReturn.addAll((SortedSet<? extends T>) entry.getValue());
      }
    }
    toReturn = Collections.unmodifiableSortedSet(toReturn);
    cache.put(artifactType, toReturn);
    return toReturn;
  }

  @Override
  public Artifact<?> first() {
    return treeSet.first();
  }

  /**
   * Prevent further modification of the ArtifactSet. Any attempts to alter
   * the ArtifactSet after invoking this method will result in an
   * UnsupportedOperationException.
   */
  public void freeze() {
    if (treeSet instanceof TreeSet<?>) {
      treeSet = Collections.unmodifiableSortedSet(treeSet);
    }
  }

  @Override
  public int hashCode() {
    return treeSet.hashCode();
  }

  @Override
  public SortedSet<Artifact<?>> headSet(Artifact<?> toElement) {
    return Collections.unmodifiableSortedSet(treeSet.headSet(toElement));
  }

  @Override
  public boolean isEmpty() {
    return treeSet.isEmpty();
  }

  @Override
  public Iterator<Artifact<?>> iterator() {
    final Iterator<Artifact<?>> backing = treeSet.iterator();
    return new Iterator<Artifact<?>>() {
      @Override
      public Artifact<?> next() {
        return backing.next();
      }

      @Override
      public boolean hasNext() {
        return backing.hasNext();
      }

      @Override
      public void remove() {
        cache = null;
        backing.remove();
      }
    };
  }

  @Override
  public Artifact<?> last() {
    return treeSet.last();
  }

  @Override
  public boolean remove(Object o) {
    if (treeSet.remove(o)) {
      cache = null;
      byType.remove(o.getClass(), o);
      return true;
    }
    return false;
  }

  @Override
  public boolean removeAll(Collection<?> c) {
    if (treeSet.removeAll(c)) {
      cache = null;
      for (Object o : c) {
        byType.remove(o.getClass(), o);
      }
    }
    return false;
  }

  /**
   * Possibly replace an existing Artifact.
   * 
   * @param artifact the replacement Artifact
   * @return <code>true</code> if an equivalent Artifact was already present.
   */
  public boolean replace(Artifact<?> artifact) {
    boolean toReturn = remove(artifact);
    add(artifact);
    return toReturn;
  }

  @Override
  public boolean retainAll(Collection<?> c) {
    if (treeSet.retainAll(c)) {
      cache = null;
      byType.values().retainAll(c);
      return true;
    }
    return false;
  }

  @Override
  public int size() {
    return treeSet.size();
  }

  @Override
  public SortedSet<Artifact<?>> subSet(Artifact<?> fromElement,
      Artifact<?> toElement) {
    return Collections.unmodifiableSortedSet(treeSet.subSet(fromElement, toElement));
  }

  @Override
  public SortedSet<Artifact<?>> tailSet(Artifact<?> fromElement) {
    return Collections.unmodifiableSortedSet(treeSet.tailSet(fromElement));
  }

  @Override
  public Object[] toArray() {
    return treeSet.toArray();
  }

  @Override
  public <T> T[] toArray(T[] a) {
    return treeSet.toArray(a);
  }

  @Override
  public String toString() {
    return treeSet.toString();
  }
}
