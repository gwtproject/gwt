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

import static com.google.gwt.core.shared.impl.InternalPreconditions.checkNotNull;

import com.google.gwt.lang.Array;

/**
 * Skeletal implementation of the Collection interface. <a
 * href="http://java.sun.com/j2se/1.5.0/docs/api/java/util/AbstractCollection.html">[Sun
 * docs]</a>
 *
 * @param <E> the element type.
 *
 */
public abstract class AbstractCollection<E> implements Collection<E> {

  protected AbstractCollection() {
  }

  @Override
  public boolean add(E o) {
    throw new UnsupportedOperationException("Add not supported on this collection");
  }

  @Override
  public boolean addAll(Collection<? extends E> c) {
    checkNotNull(c);

    boolean changed = false;
    for (E e : c) {
      changed |= add(e);
    }
    return changed;
  }

  @Override
  public void clear() {
    for (Iterator<E> iter = iterator(); iter.hasNext();) {
      iter.next();
      iter.remove();
    }
  }

  @Override
  public boolean contains(Object o) {
    return advanceToFind(o, false);
  }

  @Override
  public boolean containsAll(Collection<?> c) {
    checkNotNull(c);

    for (Object e : c) {
      if (!contains(e)) {
        return false;
      }
    }
    return true;
  }

  public boolean isEmpty() {
    return size() == 0;
  }

  public abstract Iterator<E> iterator();

  @Override
  public boolean remove(Object o) {
    return advanceToFind(o, true);
  }

  @Override
  public boolean removeAll(Collection<?> c) {
    checkNotNull(c);

    boolean changed = false;
    for (Iterator<?> iter = iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (c.contains(o)) {
        iter.remove();
        changed = true;
      }
    }
    return changed;
  }

  @Override
  public boolean retainAll(Collection<?> c) {
    checkNotNull(c);

    boolean changed = false;
    for (Iterator<?> iter = iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (!c.contains(o)) {
        iter.remove();
        changed = true;
      }
    }
    return changed;
  }

  @Override
  public abstract int size();

  @Override
  public Object[] toArray() {
    return toArray(new Object[size()]);
  }

  @Override
  public <T> T[] toArray(T[] a) {
    int size = size();
    if (a.length < size) {
      a = Array.createFrom(a, size);
    }
    Object[] result = a;
    Iterator<E> it = iterator();
    for (int i = 0; i < size; ++i) {
      result[i] = it.next();
    }
    if (a.length > size) {
      a[size] = null;
    }
    return a;
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder("[");
    boolean comma = false;
    for (E e : this) {
      if (comma) {
        sb.append(", ");
      } else {
        comma = true;
      }
      sb.append(e == this ? "(this Collection)" : String.valueOf(e));
    }
    sb.append("]");
    return sb.toString();
  }

  private boolean advanceToFind(Object o, boolean remove) {
    for (Iterator<E> iter = iterator(); iter.hasNext();) {
      E e = iter.next();
      if (Objects.equals(o, e)) {
        if (remove) {
          iter.remove();
        }
        return true;
      }
    }
    return false;
  }
}
