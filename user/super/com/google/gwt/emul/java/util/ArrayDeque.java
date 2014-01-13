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

import static javaemul.internal.InternalPreconditions.checkCriticalElement;
import static javaemul.internal.InternalPreconditions.checkElement;
import static javaemul.internal.InternalPreconditions.checkNotNull;
import static javaemul.internal.InternalPreconditions.checkState;

import java.util.function.Consumer;

import javaemul.internal.ArrayHelper;

/**
 * A {@link Deque} based on circular buffer that is implemented with an array and head/tail pointers.
 * Array deques have no capacity restrictions; they grow as necessary to support usage.
 * Null elements are prohibited. This class is likely to be faster than {@link Stack}
 * when used as a stack, and faster than {@link LinkedList} when used as a queue.
 * <a href="http://docs.oracle.com/javase/7/docs/api/java/util/ArrayDeque.html">ArrayDeque</a>
 *
 * @param <E> the element type.
 */
public class ArrayDeque<E> extends AbstractCollection<E> implements Deque<E>, Cloneable {

  private final class IteratorImpl implements Iterator<E> {
    /**
     * Index of element to be returned by subsequent call to next.
     */
    private int currentIndex = head;

    /**
     * Tail recorded at construction (also in remove), to stop
     * iterator and also to check for comodification.
     */
    private int fence = tail;

    /**
     * Index of element returned by most recent call to next.
     * Reset to -1 if element is deleted by a call to remove.
     */
    private int lastIndex = -1;

    @Override
    public boolean hasNext() {
      return currentIndex != fence;
    }

    @Override
    public E next() {
      checkCriticalElement(hasNext());

      E e = array[currentIndex];
      // OpenJDK ArrayDeque doesn't catch all possible comodifications,
      // but does catch the ones that corrupt traversal
      if (fence != tail || e == null) {
        throw new ConcurrentModificationException();
      }
      lastIndex = currentIndex;
      currentIndex = (currentIndex + 1) & (array.length - 1);
      return e;
    }

    @Override
    public void remove() {
      checkState(lastIndex >= 0);

      // OpenJDK ArrayDeque doesn't catch all possible comodifications
      if (lastIndex < head || lastIndex >= tail) {
        throw new ConcurrentModificationException();
      }

      if (removeAtIndex(lastIndex)) {
        // if left-shifted, undo increment in next()
        currentIndex = (currentIndex - 1) & (array.length - 1);
        fence = tail;
      }
      lastIndex = -1;
    }
  }

  private final class DescendingIteratorImpl implements Iterator<E> {
    private int currentIndex = tail;
    private int fence = head;
    private int lastIndex = -1;

    @Override
    public boolean hasNext() {
      return currentIndex != fence;
    }

    @Override
    public E next() {
      checkCriticalElement(hasNext());

      currentIndex = (currentIndex - 1) & (array.length - 1);
      E e = array[currentIndex];
      if (fence != head || e == null) {
        throw new ConcurrentModificationException();
      }
      lastIndex = currentIndex;
      return e;
    }

    @Override
    public void remove() {
      checkState(lastIndex >= 0);

      if (lastIndex < head || lastIndex >= tail) {
        throw new ConcurrentModificationException();
      }

      if (!removeAtIndex(lastIndex)) {
        // if right-shifted, undo decrement in next()
        currentIndex = (currentIndex + 1) & (array.length - 1);
        fence = head;
      }
      lastIndex = -1;
    }
  }

  private final class SpliteratorImpl implements Spliterator<E> {
    private int currentIndex = head;
    private int fence = tail;

    @Override
    public void forEachRemaining(Consumer<? super E> consumer) {
      checkNotNull(consumer);
      while (consumeNext(consumer)) { }
    }

    @Override
    public boolean tryAdvance(Consumer<? super E> consumer) {
      checkNotNull(consumer);
      return consumeNext(consumer);
    }

    private boolean consumeNext(Consumer<? super E> consumer) {
      if (currentIndex == fence) {
        return false;
      }

      E e = array[currentIndex];
      if (fence != tail || e == null) {
        throw new ConcurrentModificationException();
      }
      currentIndex = (currentIndex + 1) & (array.length - 1);
      consumer.accept(e);
      return true;
    }

    @Override
    public Spliterator<E> trySplit() {
      // TODO: implement in the future.
      return null;
    }

    @Override
    public long estimateSize() {
      return (long) ((tail - currentIndex) & (array.length - 1));
    }

    @Override
    public int characteristics() {
      return Spliterator.NONNULL | Spliterator.ORDERED
          | Spliterator.SIZED | Spliterator.SUBSIZED;
    }
  }

  /**
   * The minimum capacity that we'll use for a newly created deque.
   * Must be a power of 2.
   */
  private static final int MIN_INITIAL_CAPACITY = 8;

  /**
   * Allocates and returns array to hold the given number of elements.
   *
   * @param numElements the number of elements to hold
   */
  @SuppressWarnings("unchecked")
  private static <E> E[] reallocateArray(E[] array, int numElements) {
    int capacity = Math.max(numElements, MIN_INITIAL_CAPACITY);
    // Find the best power of two to hold elements because deque isn't kept full.
    capacity = nextPowerOfTwo(capacity);
    checkState(capacity > 0, "deque size is too big");
    return ArrayHelper.createFrom(array, capacity);
  }

  /**
   * Returns a number that is greater than {@code num} and is a power of two.
   * If passed {@code num} is not positive integer or next power of two overflows then
   * returned value is non-positive.
   * E.g., if num == 32, returns 64. if num == 31, returns 32.
   *
   * @param num positive integer.
   */
  private static int nextPowerOfTwo(int num) {
    return Integer.highestOneBit(num) << 1;
  }

  /**
   * This field holds a JavaScript array.
   */
  @SuppressWarnings("unchecked")
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
    this(MIN_INITIAL_CAPACITY);
  }

  public ArrayDeque(Collection<? extends E> c) {
    this(checkNotNull(c).size());
    addAll(c);
  }

  public ArrayDeque(int numElements) {
    array = reallocateArray(array, numElements);
  }

  @Override
  public boolean add(E e) {
    addLast(e);
    return true;
  }

  @Override
  public void addFirst(E e) {
    checkNotNull(e);

    head = (head - 1) & (array.length - 1);
    array[head] = e;
    ensureCapacity();
  }

  @Override
  public void addLast(E e) {
    checkNotNull(e);

    array[tail] = e;
    tail = (tail + 1) & (array.length - 1);
    ensureCapacity();
  }

  @Override
  public void clear() {
    if (head != tail) {
      final int mask = array.length - 1;
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
    E e = peekFirstElement();
    checkElement(e != null);
    return e;
  }

  @Override
  public E getLast() {
    E e = peekLastElement();
    checkElement(e != null);
    return e;
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
    return peekFirstElement();
  }

  @Override
  public E peekLast() {
    return peekLastElement();
  }

  @Override
  public E poll() {
    return pollFirst();
  }

  @Override
  public E pollFirst() {
    int h = head;
    E e = array[h];
    if (e == null) {
      return null;
    }
    array[h] = null;
    h = (h + 1) & (array.length - 1);
    head = h;
    return e;
  }

  @Override
  public E pollLast() {
    int t = (tail - 1) & (array.length - 1);
    E e = array[t];
    if (e == null) {
      return null;
    }
    array[t] = null;
    tail = t;
    return e;
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
    E e = pollFirst();
    checkElement(e != null);
    return e;
  }

  @Override
  public boolean removeFirstOccurrence(Object o) {
    int i = indexOf(o);
    if (i != -1) {
      removeAtIndex(i);
      return true;
    }
    return false;
  }

  @Override
  public E removeLast() {
    E e = pollLast();
    checkElement(e != null);
    return e;
  }

  @Override
  public boolean removeLastOccurrence(Object o) {
    int i = lastIndexOf(o);
    if (i != -1) {
      removeAtIndex(i);
      return true;
    }
    return false;
  }

  @Override
  public int size() {
    return (tail - head) & (array.length - 1);
  }

  @Override
  public Spliterator<E> spliterator() {
    return new SpliteratorImpl();
  }

  @Override
  public <T> T[] toArray(T[] out) {
    int size = size();
    if (out.length < size) {
      out = ArrayHelper.clone(out, 0, size);
    }
    if (size > 0) {
      copyElements(out);
    }
    if (out.length > size) {
      out[size] = null;
    }
    return out;
  }

  /**
   * Copies the elements from deque's array into the specified array, in order from head to tail.
   * It is assumed that the array {@code dest} is large enough to hold deque's elements.
   * Note that the method copies contents of the deque even if deque is empty ({@code head == tail})
   * so the caller should check it beforehand.
   */
  private void copyElements(Object[] dest) {
    Object[] src = array;
    int destOfs = 0;
    if (head < tail) {
      int srcOfs = head;
      int length = tail - head;
      for (int i = 0; i < length; i++) {
        dest[destOfs++] = src[srcOfs++];
      }
    } else {
      int srcOfs = head;
      int length = src.length - head;
      for (int i = 0; i < length; i++) {
        dest[destOfs++] = src[srcOfs++];
      }
      srcOfs = 0;
      length = tail;
      for (int i = 0; i < length; i++) {
        dest[destOfs++] = src[srcOfs++];
      }
    }
  }

  /**
   * Increase the capacity of this deque when full, i.e.,
   * when head and tail have wrapped around to become equal.
   */
  private void ensureCapacity() {
    if (head == tail) {
      int numElements = array.length;
      E[] newArray = reallocateArray(array, numElements);
      copyElements(newArray);
      array = newArray;
      head = 0;
      tail = numElements;
    }
  }

  private int indexOf(Object o) {
    if (o == null) {
      return -1;
    }

    final int mask = array.length - 1;
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

  private int lastIndexOf(Object o) {
    if (o == null) {
      return -1;
    }

    final int mask = array.length - 1;
    int i = (tail - 1) & mask;
    E e;
    while ((e = array[i]) != null) {
      if (o.equals(e)) {
        return i;
      }
      i = (i - 1) & mask;
    }
    return -1;
  }

  private E peekFirstElement() {
    return array[head];
  }

  private E peekLastElement() {
    return array[(tail - 1) & (array.length - 1)];
  }

  /**
   * Removes the element at the specified position in the elements array,
   * adjusting head and tail as necessary. This can result in motion of
   * elements backwards or forwards in the array.
   *
   * @return true if elements moved backwards (left-shifted)
   */
  private boolean removeAtIndex(int i) {
    final int mask = array.length - 1;
    int startDistance = (i - head) & mask;
    int endDistance = (tail - i) & mask;
    boolean leftShifted = startDistance >= endDistance;
    if (leftShifted) {
      tail = (tail - 1) & mask;
      while (i != tail) {
        int nextOffset = (i + 1) & mask;
        array[i] = array[nextOffset];
        i = nextOffset;
      }
      array[tail] = null;
    } else {
      while (i != head) {
        int prevOffset = (i - 1) & mask;
        array[i] = array[prevOffset];
        i = prevOffset;
      }
      array[head] = null;
      head = (head + 1) & mask;
    }
    return leftShifted;
  }
}
