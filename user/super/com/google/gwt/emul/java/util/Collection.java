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

/**
 * General-purpose interface for storing collections of objects. <a
 * href="http://java.sun.com/j2se/1.5.0/docs/api/java/util/Collection.html">[Sun
 * docs]</a>
 *
 * @param <E> element type
 */
public interface Collection<E> extends Iterable<E> {

  boolean add(E o);

  boolean addAll(Collection<? extends E> c);

  void clear();

  boolean contains(Object o);

  boolean containsAll(Collection<?> c);

  @Override
  boolean equals(Object o);

  @Override
  int hashCode();

  boolean isEmpty();

  @Override
  Iterator<E> iterator();

  boolean remove(Object o);

  boolean removeAll(Collection<?> c);

  boolean retainAll(Collection<?> c);

  int size();

  Object[] toArray();

  <T> T[] toArray(T[] a);
}
