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

import com.google.gwt.lang.Array;

import java.io.Serializable;

/**
 * Resizable-array implementation of the {@link Deque} interface. Array
 * deques have no capacity restrictions; they grow as necessary to support
 * usage.
 * Null elements are prohibited. This class is likely to be faster than
 * {@link Stack} when used as a stack, and faster than {@link LinkedList}
 * when used as a queue.
 * <a href="http://docs.oracle.com/javase/7/docs/api/java/util/ArrayDeque.html">ArrayDeque</a>
 *
 * @param <E> the element type.
 */
public class ArrayDeque<E> extends AbstractCollection<E> implements Deque<E>,
    Cloneable, Serializable {

  /**
   * The minimum capacity that we'll use for a newly created deque.
   * Must be a power of 2.
   */
  private static final int MIN_INITIAL_CAPACITY = 8;

  private static native void setCapacity(Object[] array, int newSize) /*-{
      array.length = newSize;
  }-*/;

  private final class IteratorImpl implements Iterator<E> {
    /**
     * Index of element to be returned by subsequent call to next.
     */
    private int cursor = head;

    /**
     * Tail recorded at construction (also in remove), to stop
     * iterator and also to check for comodification.
     */
    private int fence = tail;

    /**
     * Index of element returned by most recent call to next.
     * Reset to -1 if element is deleted by a call to remove.
     */
    private int lastRet = -1;

    @Override
    public boolean hasNext() {
      return cursor != fence;
    }

    @Override
    public E next() {
      if (!hasNext()) {
        throw new NoSuchElementException();
      }

      E e = array[cursor];
      // This check doesn't catch all possible comodifications,
      // but does catch the ones that corrupt traversal
      if (tail != fence || e == null) {
        throw new ConcurrentModificationException();
      }
      lastRet = cursor;
      cursor = (cursor + 1) & (array.length - 1);
      return e;
    }

    @Override
    public void remove() {
      if (lastRet < 0) {
        throw new IllegalStateException();
      }

      if (delete(lastRet)) { // if left-shifted, undo increment in next()
        cursor = (cursor - 1) & (array.length - 1);
        fence = tail;
      }
      lastRet = -1;
    }
  }

  /**
   * This class is nearly a mirror-image of IteratorImpl, using
   * tail instead of head for initial cursor, and head instead of
   * tail for fence.
   */
  private final class DescendingIteratorImpl implements Iterator<E> {
    private int cursor = tail;
    private int fence = head;
    private int lastRet = -1;

    @Override
    public boolean hasNext() {
      return cursor != fence;
    }

    @Override
    public E next() {
      if (!hasNext()) {
        throw new NoSuchElementException();
      }

      cursor = (cursor - 1) & (array.length - 1);
      E e = array[cursor];
      if (head != fence || e == null) {
        throw new ConcurrentModificationException();
      }
      lastRet = cursor;
      return e;
    }

    @Override
    public void remove() {
      if (lastRet < 0) {
        throw new IllegalStateException();
      }

      if (!delete(lastRet)) {
        cursor = (cursor + 1) & (array.length - 1);
        fence = head;
      }
      lastRet = -1;
    }
  }

  /**
   * This field holds a JavaScript array.
   */
  private transient E[] array = (E[]) new Object[0];

  /**
   * Ensures that RPC will consider type parameter E to be exposed. It will be
   * pruned by dead code elimination.
   */
  @SuppressWarnings("unused")
  private E exposeElement;

  /**
   * The index of the element at the head of the deque (which is the
   * element that would be removed by remove() or pop()); or an
   * arbitrary number equal to tail if the deque is empty.
   */
  private transient int head;

  /**
   * The index at which the next element would be added to the tail
   * of the deque (via addLast(E), add(E), or push(E)).
   */
  private transient int tail;

  public ArrayDeque() {
    setCapacity(array, 16);
  }

  public ArrayDeque(Collection<? extends E> c) {
    allocateElements(c.size());
    c.toArray(array);
  }

  public ArrayDeque(int numElements) {
    allocateElements(numElements);
  }

  @Override
  public boolean add(E e) {
    addLast(e);
    return true;
  }

  @Override
  public void addFirst(E e) {
    if (e == null) {
      throw new NullPointerException();
    }

    head = (head - 1) & (array.length - 1);
    array[head] = e;
    if (head == tail) {
      doubleCapacity();
    }
  }

  @Override
  public void addLast(E e) {
    if (e == null) {
      throw new NullPointerException();
    }

    array[tail] = e;
    tail = (tail + 1) & (array.length - 1);
    if (tail == head) {
      doubleCapacity();
    }
  }

  @Override
  public void clear() {
    if (head != tail) {
      int mask = array.length - 1;
      for (int i = head; i != tail; i = (i + 1) & mask) {
        array[i] = null;
      }
      head = 0;
      tail = 0;
    }
  }

  public Object clone() {
    return new ArrayDeque<E>(this);
  }

  @Override
  public boolean contains(Object o) {
    return indexOf(o) != -1;
  }

  @Override
  public Iterator<E> descendingIterator() {
    return new DescendingIteratorImpl();
  }

  @Override
  public E element() {
    return getFirst();
  }

  @Override
  public E getFirst() {
    throwIfEmpty();
    return array[head];
  }

  @Override
  public E getLast() {
    throwIfEmpty();
    return array[(tail - 1) & (array.length - 1)];
  }

  @Override
  public boolean isEmpty() {
    return head == tail;
  }

  @Override
  public Iterator<E> iterator() {
    return new IteratorImpl();
  }

  @Override
  public boolean offer(E e) {
    return offerLast(e);
  }

  @Override
  public boolean offerFirst(E e) {
    addFirst(e);
    return true;
  }

  @Override
  public boolean offerLast(E e) {
    addLast(e);
    return true;
  }

  @Override
  public E peek() {
    return peekFirst();
  }

  @Override
  public E peekFirst() {
    if (isEmpty()) {
      return null;
    } else {
      return getFirst();
    }
  }

  @Override
  public E peekLast() {
    if (isEmpty()) {
      return null;
    } else {
      return getLast();
    }
  }

  @Override
  public E poll() {
    return pollFirst();
  }

  @Override
  public E pollFirst() {
    if (isEmpty()) {
      return null;
    } else {
      return removeFirst();
    }
  }

  @Override
  public E pollLast() {
    if (isEmpty()) {
      return null;
    } else {
      return removeLast();
    }
  }

  @Override
  public E pop() {
    return removeFirst();
  }

  @Override
  public void push(E e) {
    addFirst(e);
  }

  @Override
  public E remove() {
    return removeFirst();
  }

  @Override
  public boolean remove(Object o) {
    return removeFirstOccurrence(o);
  }

  @Override
  public E removeFirst() {
    throwIfEmpty();
    E e = array[head];
    array[head] = null;
    head = (head + 1) & (array.length - 1);
    return e;
  }

  @Override
  public boolean removeFirstOccurrence(Object o) {
    int i = indexOf(o);
    if (i != -1) {
      delete(i);
      return true;
    }
    return false;
  }

  @Override
  public E removeLast() {
    throwIfEmpty();
    tail = (tail - 1) & (array.length - 1);
    E e = array[tail];
    array[tail] = null;
    return e;
  }

  @Override
  public boolean removeLastOccurrence(Object o) {
    if (o == null) {
      return false;
    }

    int mask = array.length - 1;
    int i = (tail - 1) & mask;
    E e;
    while ((e = array[i]) != null) {
      if (o.equals(e)) {
        delete(i);
        return true;
      }
      i = (i - 1) & mask;
    }
    return false;
  }

  @Override
  public int size() {
    return (tail - head) & (array.length - 1);
  }

  @Override
  public <T> T[] toArray(T[] out) {
    int size = size();
    if (out.length < size) {
      out = Array.createFrom(out, size);
    }
    copyElements(out);
    if (out.length > size) {
      out[size] = null;
    }
    return out;
  }

  /**
   * Allocate empty array to hold the given number of elements.
   *
   * @param numElements the number of elements to hold
   */
  private void allocateElements(int numElements) {
    int initialCapacity = MIN_INITIAL_CAPACITY;
    // Find the best power of two to hold elements.
    // Tests "<=" because arrays aren't kept full.
    if (numElements >= initialCapacity) {
      initialCapacity = numElements;
      initialCapacity |= (initialCapacity >>>  1);
      initialCapacity |= (initialCapacity >>>  2);
      initialCapacity |= (initialCapacity >>>  4);
      initialCapacity |= (initialCapacity >>>  8);
      initialCapacity |= (initialCapacity >>> 16);
      initialCapacity++;
      checkCapacity(initialCapacity);
    }
    setCapacity(array, initialCapacity);
  }

  /**
   * Checks if calculated capacity is overflown.
   */
  private void checkCapacity(int capacity) {
    if (capacity < 0) {
      throw new IllegalStateException("deque size is too big");
    }
  }

  /**
   * Copies the elements from our element array into the specified array,
   * in order (from first to last element in the deque). It is assumed
   * that the array is large enough to hold all elements in the deque.
   */
  @SuppressWarnings("unchecked")
  private void copyElements(Object[] out) {
    if (head < tail) {
      for (int i = 0, j = head; j < tail; i++, j++) {
        out[i] = array[j];
      }
    } else {
      int i = 0;
      for (int j = head, length = array.length; j < length; i++, j++) {
        out[i] = array[i];
      }
      for (int j = 0; j < tail; i++, j++) {
        out[i] = array[j];
      }
    }
  }

  /**
   * Removes the element at the specified position in the elements array,
   * adjusting head and tail as necessary. This can result in motion of
   * elements backwards or forwards in the array.
   *
   * <p>This method is called delete rather than remove to emphasize
   * that its semantics differ from those of {@link List#remove(int)}.
   *
   * @return true if elements moved backwards
   */
  private boolean delete(int i) {
    int mask = array.length - 1;
    int startDistance = (i - head) & mask;
    int endDistance = (tail - i) & mask;
    if (startDistance < endDistance) {
      while (i != head) {
        int prevOffset = (i - 1) & mask;
        array[i] = array[prevOffset];
        i = prevOffset;
      }
      array[head] = null;
      head = (head + 1) & mask;
      return false;
    } else {
      tail = (tail - 1) & mask;
      while (i != tail) {
        int nextOffset = (i + 1) & mask;
        array[i] = array[nextOffset];
        i = nextOffset;
      }
      array[tail] = null;
      return true;
    }
  }

  /**
   * Double the capacity of this deque. Call only when full, i.e.,
   * when head and tail have wrapped around to become equal.
   */
  @SuppressWarnings("unchecked")
  private void doubleCapacity() {
    assert head == tail;
    int length = array.length;
    int newCapacity = length << 1;
    checkCapacity(newCapacity);
    Object[] newArray = new Object[0];
    setCapacity(newArray, newCapacity);
    copyElements(newArray);
    array = (E[]) newArray;
    head = 0;
    tail = length;
  }

  private int indexOf(Object o) {
    if (o == null) {
      return -1;
    }

    int mask = array.length - 1;
    int i = head;
    E e;
    while ((e = array[i]) != null) {
      if (o.equals(e)) {
        return i;
      }
      i = (i + 1) & mask;
    }
    return -1;
  }

  private void throwIfEmpty() {
    if (isEmpty()) {
      throw new NoSuchElementException();
    }
  }
}
