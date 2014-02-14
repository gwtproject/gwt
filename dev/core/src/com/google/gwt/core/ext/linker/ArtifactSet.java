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
import com.google.gwt.thirdparty.guava.common.collect.ForwardingIterator;
import com.google.gwt.thirdparty.guava.common.collect.ImmutableSortedSet;
import com.google.gwt.thirdparty.guava.common.collect.Multimap;
import com.google.gwt.thirdparty.guava.common.collect.Multimaps;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicReference;

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

  private final boolean isView;

  private SortedSet<Artifact<?>> treeSet = new TreeSet<Artifact<?>>();

  /**
   * Groups set members by their concrete type.
   *
   * <p>This is maintained to speed-up the find operation.  It is generated on
   * the first call to {@link #find(Class)} and kept up-to-date during each
   * subsequent ArtifactSet mutation.
   *
   * <p>For find, all members that are assignable to a type can be quickly
   * located by iterating through the keys of this index, avoiding the need to
   * individually check each member of the set; since they share a concrete
   * type, they will collectively be assignable or not assignable to the type
   * being found.
   */
  private transient Multimap<Class<?>, Artifact<?>> byType = null;

  /**
   * Caches the results of {@link #find(Class)}.  If an entry for the Class
   * being found is located in the cache, it can be returned immediately.
   *
   * <p>If a set is mutated, the cache is cleared.
   */
  private transient AtomicReference<Map<Class<?>, SortedSet<?>>> cache;

  public ArtifactSet() {
    this(new TreeSet<Artifact<?>>(),
        new AtomicReference<Map<Class<?>, SortedSet<?>>>(), false);
  }

  public ArtifactSet(Collection<? extends Artifact<?>> copyFrom) {
    this();
    addAll(copyFrom);
  }

  private ArtifactSet(
      SortedSet<Artifact<?>> backingSet,
      AtomicReference<Map<Class<?>, SortedSet<?>>> cache,
      boolean isView) {
    this.treeSet = backingSet;
    this.cache = cache;
    this.isView = isView;
  }

  /**
   * Groups all set members by their concrete type and puts the result into
   * the 'byType' index.
   */
  private void generateTypeIndex() {
    byType = Multimaps.newSortedSetMultimap(
        new HashMap<Class<?>, Collection<Artifact<?>>>(), TREE_SETS);
    for (Artifact a : treeSet) {
      byType.put(a.getClass(), a);
    }
  }

  /**
   * If the 'byType' index exists, adds a newly added member of the ArtifactSet
   * to the index.
   */
  private void maybeIndexAddition(Artifact<?> o) {
    if (byType != null) {
      byType.put(o.getClass(), o);
    }
  }

  /**
   * If the 'byType' index exists, removes a recently removed member of the
   * ArtifactSet from the index.
   */
  private void maybeIndexRemoval(Object o) {
    if (byType != null) {
      byType.remove(o.getClass(), o);
    }
  }

  /**
   * Wraps a low-level subSet, headSet, or tailSet in such a way that 'byType'
   * and 'cache' will be correctly maintained for this ArtifactSet when the
   * views are mutated.
   */
  private SortedSet<Artifact<?>> createView(SortedSet<Artifact<?>> backing) {
    return new ArtifactSet(backing, cache, true);
  }

  @Override
  public boolean add(Artifact<?> o) {
    if (treeSet.add(o)) {
      cache.set(null);
      maybeIndexAddition(o);
      return true;
    }
    return false;
  }

  @Override
  public boolean addAll(Collection<? extends Artifact<?>> c) {
    if (treeSet.addAll(c)) {
      cache.set(null);
      for (Artifact<?> a : c) {
        maybeIndexAddition(a);
      }
      return true;
    }
    return false;
  }

  @Override
  public void clear() {
    cache.set(null);
    treeSet.clear();
    byType = null;
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
   * <p>The returned SortedSet is immutable.
   *
   * @param <T> the desired type of Artifact
   * @param artifactType the desired type of Artifact
   * @return all Artifacts in the ArtifactSet assignable to the desired type
   */
  public <T extends Artifact<? super T>> SortedSet<T> find(
      Class<T> artifactType) {
    // The views are secretly ArtifactSet (it isn't advertised on the interface)
    // so that we can maintain the byType index and the cache.  find() does not
    // reliably work for them, and the method shouldn't normally be exposed and
    // accessible for a view.
    if (isView) {
      throw new UnsupportedOperationException(
          "Cannot find() for views created via headSet/tailSet/subSet");
    }

    if (cache.get() == null) {
      // Create the cache since it did not previously exist.
      cache.set(new HashMap<Class<?>, SortedSet<?>>());
    } else {
      // The cache previously existed, so see if it has an entry that matches
      // artifact type.
      SortedSet<?> s = cache.get().get(artifactType);
      if (s != null) {
        return (SortedSet<T>) s;
      }
    }

    // Bummer. Cache miss.

    // Create a type index if it did not previously exist.  The type index is
    // necessary below.
    if (byType == null) {
      generateTypeIndex();
    }

    // Using the type index, gather all members assignable to artifactType.
    ImmutableSortedSet.Builder<T> builder = ImmutableSortedSet.<T>naturalOrder();
    for (Map.Entry<Class<?>, Collection<Artifact<?>>> entry : byType.asMap().entrySet()) {
      if (artifactType.isAssignableFrom(entry.getKey())) {
        builder.addAll((SortedSet<? extends T>) entry.getValue());
      }
    }
    SortedSet<T> toReturn = builder.build();

    // Cache the result
    cache.get().put(artifactType, toReturn);
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
    return createView(treeSet.headSet(toElement));
  }

  @Override
  public boolean isEmpty() {
    return treeSet.isEmpty();
  }

  @Override
  public Iterator<Artifact<?>> iterator() {
    return new ForwardingIterator<Artifact<?>>() {
      private final Iterator<Artifact<?>> backing = treeSet.iterator();

      @Override
      protected Iterator<Artifact<?>> delegate() {
        return backing;
      }

      @Override
      public void remove() {
        delegate().remove();
        cache.set(null);
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
      cache.set(null);
      maybeIndexRemoval(o);
      return true;
    }
    return false;
  }

  @Override
  public boolean removeAll(Collection<?> c) {
    if (treeSet.removeAll(c)) {
      cache.set(null);
      for (Object o : c) {
        maybeIndexRemoval(o);
      }
      return true;
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
      cache.set(null);
      if (byType != null) {
        byType.values().retainAll(c);
      }
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
    return createView(treeSet.subSet(fromElement, toElement));
  }

  @Override
  public SortedSet<Artifact<?>> tailSet(Artifact<?> fromElement) {
    return createView(treeSet.tailSet(fromElement));
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
