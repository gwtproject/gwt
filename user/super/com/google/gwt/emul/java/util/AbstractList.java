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

import static com.google.gwt.core.shared.impl.InternalPreconditions.checkCriticalPositionIndexes;
import static com.google.gwt.core.shared.impl.InternalPreconditions.checkElement;
import static com.google.gwt.core.shared.impl.InternalPreconditions.checkElementIndex;
import static com.google.gwt.core.shared.impl.InternalPreconditions.checkNotNull;
import static com.google.gwt.core.shared.impl.InternalPreconditions.checkPositionIndex;
import static com.google.gwt.core.shared.impl.InternalPreconditions.checkState;

/**
 * Skeletal implementation of the List interface. <a
 * href="http://java.sun.com/j2se/1.5.0/docs/api/java/util/AbstractList.html">[Sun
 * docs]</a>
 * 
 * @param <E> the element type.
 */
public abstract class AbstractList<E> extends AbstractCollection<E> implements
    List<E> {

  private class IteratorImpl implements Iterator<E> {
    /*
     * i is the index of the item that will be returned on the next call to
     * next() last is the index of the item that was returned on the previous
     * call to next() or previous (for ListIterator), -1 if no such item exists.
     */

    int i = 0, last = -1;

    public boolean hasNext() {
      return i < AbstractList.this.size();
    }

    public E next() {
      checkElement(hasNext());

      return AbstractList.this.get(last = i++);
    }

    public void remove() {
      checkState(last != -1);

      AbstractList.this.remove(last);
      i = last;
      last = -1;
    }
  }

  /**
   * Implementation of <code>ListIterator</code> for abstract lists.
   */
  private final class ListIteratorImpl extends IteratorImpl implements
      ListIterator<E> {
    /*
     * i is the index of the item that will be returned on the next call to
     * next() last is the index of the item that was returned on the previous
     * call to next() or previous (for ListIterator), -1 if no such item exists.
     */

    private ListIteratorImpl() {
      // Nothing to do
    }

    private ListIteratorImpl(int start) {
      checkPositionIndex(start, AbstractList.this.size());

      i = start;
    }

    public void add(E o) {
      AbstractList.this.add(i, o);
      i++;
      last = -1;
    }

    public boolean hasPrevious() {
      return i > 0;
    }

    public int nextIndex() {
      return i;
    }

    public E previous() {
      checkElement(hasPrevious());

      return AbstractList.this.get(last = --i);
    }

    public int previousIndex() {
      return i - 1;
    }

    public void set(E o) {
      checkState(last != -1);

      AbstractList.this.set(last, o);
    }
  }

  private static class SubList<E> extends AbstractList<E> {
    private final List<E> wrapped;
    private final int fromIndex;
    private int size;

    public SubList(List<E> wrapped, int fromIndex, int toIndex) {
      checkCriticalPositionIndexes(fromIndex, toIndex, wrapped.size());

      this.wrapped = wrapped;
      this.fromIndex = fromIndex;
      this.size = toIndex - fromIndex;
    }

    @Override
    public void add(int index, E element) {
      checkPositionIndex(index, size);

      wrapped.add(fromIndex + index, element);
      size++;
    }

    @Override
    public E get(int index) {
      checkElementIndex(index, size);

      return wrapped.get(fromIndex + index);
    }

    @Override
    public E remove(int index) {
      checkElementIndex(index, size);

      E result = wrapped.remove(fromIndex + index);
      size--;
      return result;
    }

    @Override
    public E set(int index, E element) {
      checkElementIndex(index, size);

      return wrapped.set(fromIndex + index, element);
    }

    @Override
    public int size() {
      return size;
    }
  }

  protected transient int modCount;

  protected AbstractList() {
  }

  @Override
  public boolean add(E obj) {
    add(size(), obj);
    return true;
  }

  public void add(int index, E element) {
    throw new UnsupportedOperationException("Add not supported on this list");
  }

  @Override
  public boolean addAll(int index, Collection<? extends E> c) {
    checkNotNull(c);

    boolean changed = false;
    for (E e : c) {
      add(index++, e);
      changed = true;
    }
    return changed;
  }

  @Override
  public void clear() {
    removeRange(0, size());
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) {
      return true;
    }

    if (!(o instanceof List)) {
      return false;
    }

    List<?> other = (List<?>) o;
    if (size() != other.size()) {
      return false;
    }

    Iterator<?> iterOther = other.iterator();
    for (E elem : this) {
      Object elemOther = iterOther.next();
      if (!Objects.equals(elem, elemOther)) {
        return false;
      }
    }

    return true;
  }

  public abstract E get(int index);

  @Override
  public int hashCode() {
    return Collections.hashCode(this);
  }

  public int indexOf(Object toFind) {
    for (int i = 0, n = size(); i < n; ++i) {
      if (Objects.equals(toFind, get(i))) {
        return i;
      }
    }
    return -1;
  }

  @Override
  public Iterator<E> iterator() {
    return new IteratorImpl();
  }

  public int lastIndexOf(Object toFind) {
    for (int i = size() - 1; i > -1; --i) {
      if (Objects.equals(toFind, get(i))) {
        return i;
      }
    }
    return -1;
  }

  public ListIterator<E> listIterator() {
    return listIterator(0);
  }

  public ListIterator<E> listIterator(int from) {
    return new ListIteratorImpl(from);
  }

  public E remove(int index) {
    throw new UnsupportedOperationException("Remove not supported on this list");
  }

  public E set(int index, E o) {
    throw new UnsupportedOperationException("Set not supported on this list");
  }

  public List<E> subList(int fromIndex, int toIndex) {
    return new SubList<E>(this, fromIndex, toIndex);
  }

  protected void removeRange(int fromIndex, int endIndex) {
    ListIterator<E> iter = listIterator(fromIndex);
    for (int i = fromIndex; i < endIndex; ++i) {
      iter.next();
      iter.remove();
    }
  }
}
