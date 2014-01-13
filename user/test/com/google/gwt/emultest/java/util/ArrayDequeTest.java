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
package com.google.gwt.emultest.java.util;

import org.apache.commons.collections.TestCollection;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.Deque;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Tests ArrayDeque class.
 */
public class ArrayDequeTest extends TestCollection {

  public void testAdd() throws Exception {
    Object o1 = new Object();
    Object o2 = new Object();
    Object o3 = new Object();

    ArrayDeque<Object> deque = new ArrayDeque<Object>();
    assertTrue(deque.add(o1));
    checkDequeSizeAndContent(deque, o1);
    assertTrue(deque.add(o2));
    checkDequeSizeAndContent(deque, o1, o2);
    assertTrue(deque.add(o3));
    checkDequeSizeAndContent(deque, o1, o2, o3);

    try {
      deque.add(null);
      fail();
    } catch (NullPointerException expected) {
    }
  }

  public void testAddAll() throws Exception {
    Object o1 = new Object();
    Object o2 = new Object();

    ArrayDeque<Object> deque = new ArrayDeque<Object>();
    assertTrue(deque.addAll(Arrays.asList(o1, o2)));
    checkDequeSizeAndContent(deque, o1, o2);

    try {
      deque = new ArrayDeque<Object>();
      deque.addAll(Arrays.asList(o1, null, o2));
      fail();
    } catch (NullPointerException expected) {
    }
  }

  public void testAddFirst() {
    Object o1 = new Object();
    Object o2 = new Object();
    Object o3 = new Object();

    ArrayDeque<Object> deque = new ArrayDeque<Object>();
    deque.addFirst(o1);
    checkDequeSizeAndContent(deque, o1);
    deque.addFirst(o2);
    checkDequeSizeAndContent(deque, o2, o1);
    deque.addFirst(o3);
    checkDequeSizeAndContent(deque, o3, o2, o1);

    try {
      deque.addFirst(null);
      fail();
    } catch (NullPointerException expected) {
    }
  }

  public void testAddLast() {
    Object o1 = new Object();
    Object o2 = new Object();
    Object o3 = new Object();

    ArrayDeque<Object> deque = new ArrayDeque<Object>();
    deque.addLast(o1);
    checkDequeSizeAndContent(deque, o1);
    deque.addLast(o2);
    checkDequeSizeAndContent(deque, o1, o2);
    deque.addLast(o3);
    checkDequeSizeAndContent(deque, o1, o2, o3);

    try {
      deque.addLast(null);
      fail();
    } catch (NullPointerException expected) {
    }
  }

  public void testConstructorFromCollection() {
    assertEquals(0, new ArrayDeque<Object>(new ArrayList<Object>()).size());
    try {
      new ArrayDeque<Object>((Collection) null);
      fail();
    } catch (NullPointerException expected) {
    }

    try {
      new ArrayDeque<Object>(Arrays.asList(new Object(), null, new Object()));
      fail();
    } catch (NullPointerException expected) {
    }

    Collection<Object> collection = new ArrayList<Object>(Arrays.asList(getFullNonNullElements()));
    checkDequeSizeAndContent(new ArrayDeque<Object>(collection), getFullNonNullElements());
  }

  public void testContains() {
    Object o1 = new Object();
    Object o2 = new Object();
    Object o3 = new Object();

    ArrayDeque<Object> deque = new ArrayDeque<Object>();
    assertFalse(deque.contains(o3));
    assertFalse(deque.contains(null));
    assertTrue(deque.add(o1));
    assertTrue(deque.add(o2));
    assertTrue(deque.add(o1));
    assertTrue(deque.add(o3));
    assertTrue(deque.contains(o1));
    assertTrue(deque.contains(o2));
    assertTrue(deque.contains(o3));
    assertFalse(deque.contains(null));
    deque.clear();
    assertTrue(deque.isEmpty());
    assertFalse(deque.contains(o1));
    assertFalse(deque.contains(o2));
    assertFalse(deque.contains(o3));
  }

  public void testDescendingIterator() {
    Object o1 = new Object();
    Object o2 = new Object();
    Object o3 = new Object();

    ArrayDeque<Object> deque = new ArrayDeque<Object>();
    Iterator<Object> it = deque.descendingIterator();
    assertFalse(it.hasNext());
    try {
      it.next();
      fail();
    } catch (NoSuchElementException expected) {
    }

    deque.add(o1);
    deque.add(o2);
    deque.add(o3);
    it = deque.descendingIterator();
    assertTrue(it.hasNext());
    assertEquals(o3, it.next());
    assertTrue(it.hasNext());
    assertEquals(o2, it.next());
    assertTrue(it.hasNext());
    assertEquals(o1, it.next());
    assertFalse(it.hasNext());
    try {
      it.next();
      fail();
    } catch (NoSuchElementException expected) {
    }
    checkDequeSizeAndContent(deque, o1, o2, o3);

    deque = new ArrayDeque<Object>();
    deque.add(o1);
    deque.add(o2);
    deque.add(o3);
    it = deque.descendingIterator();
    assertTrue(it.hasNext());
    assertEquals(o3, it.next());
    it.remove();
    assertEquals(2, deque.size());
    assertTrue(it.hasNext());
    assertEquals(o2, it.next());
    assertTrue(it.hasNext());
    assertEquals(o1, it.next());
    it.remove();
    checkDequeSizeAndContent(deque, o2);
  }

  public void testElement() {
    Object o1 = new Object();
    Object o2 = new Object();

    ArrayDeque<Object> deque = new ArrayDeque<Object>();
    try {
      deque.element();
      fail();
    } catch (NoSuchElementException expected) {
    }

    deque.add(o1);
    assertEquals(o1, deque.element());
    checkDequeSizeAndContent(deque, o1);

    deque.add(o2);
    assertEquals(o1, deque.element());
    checkDequeSizeAndContent(deque, o1, o2);

    deque.clear();
    assertTrue(deque.isEmpty());
    try {
      deque.element();
      fail();
    } catch (NoSuchElementException expected) { }
  }

  public void testFailFastIterator() {
    ArrayDeque<Object> deque = new ArrayDeque<Object>(Arrays.asList(getFullNonNullElements()));
    Iterator<Object> it = deque.iterator();
    it.next();
    deque.removeFirst();
    try {
      it.next();
    } catch (ConcurrentModificationException e) {
      fail();
    }
    deque.removeLast();
    try {
      it.next();
      fail();
    } catch (ConcurrentModificationException expected) { }

    deque = new ArrayDeque<Object>(Arrays.asList(getFullNonNullElements()));
    it = deque.iterator();
    it.next();
    deque.clear();
    try {
      it.next();
      fail();
    } catch (ConcurrentModificationException expected) { }

    deque = new ArrayDeque<Object>(Arrays.asList(getFullNonNullElements()));
    it = deque.iterator();
    it.next();
    deque.addFirst(new Object());
    try {
      it.next();
    } catch (ConcurrentModificationException e) {
      fail();
    }
    deque.addLast(new Object());
    try {
      it.next();
      fail();
    } catch (ConcurrentModificationException expected) { }

    deque = new ArrayDeque<Object>(Arrays.asList(getFullNonNullElements()));
    it = deque.iterator();
    it.next();
    it.next();
    deque.removeFirst();
    try {
      it.remove();
    } catch (ConcurrentModificationException e) {
      fail();
    }

    deque = new ArrayDeque<Object>(Arrays.asList(getFullNonNullElements()));
    it = deque.iterator();
    it.next();
    it.next();
    deque.removeFirst();
    deque.removeFirst();
    try {
      it.remove();
      fail();
    } catch (ConcurrentModificationException expected) { }
  }

  public void testFailFastDescendingIterator() {
    ArrayDeque<Object> deque = new ArrayDeque<Object>(Arrays.asList(getFullNonNullElements()));
    Iterator<Object> it = deque.descendingIterator();
    it.next();
    deque.removeLast();
    try {
      it.next();
    } catch (ConcurrentModificationException e) {
      fail();
    }
    deque.removeFirst();
    try {
      it.next();
      fail();
    } catch (ConcurrentModificationException expected) { }

    deque = new ArrayDeque<Object>(Arrays.asList(getFullNonNullElements()));
    it = deque.descendingIterator();
    it.next();
    deque.clear();
    try {
      it.next();
      fail();
    } catch (ConcurrentModificationException expected) { }

    deque = new ArrayDeque<Object>(Arrays.asList(getFullNonNullElements()));
    it = deque.descendingIterator();
    it.next();
    deque.addLast(new Object());
    try {
      it.next();
    } catch (ConcurrentModificationException e) {
      fail();
    }
    deque.addFirst(new Object());
    try {
      it.next();
      fail();
    } catch (ConcurrentModificationException expected) { }

    deque = new ArrayDeque<Object>(Arrays.asList(getFullNonNullElements()));
    it = deque.descendingIterator();
    it.next();
    it.next();
    deque.removeLast();
    try {
      it.remove();
    } catch (ConcurrentModificationException e) {
      fail();
    }

    deque = new ArrayDeque<Object>(Arrays.asList(getFullNonNullElements()));
    it = deque.descendingIterator();
    it.next();
    it.next();
    deque.removeLast();
    deque.removeLast();
    try {
      it.remove();
      fail();
    } catch (ConcurrentModificationException expected) { }
  }

  public void testGetFirst() {
    Object o1 = new Object();
    Object o2 = new Object();

    ArrayDeque<Object> deque = new ArrayDeque<Object>();
    try {
      deque.getFirst();
      fail();
    } catch (NoSuchElementException expected) {
    }

    deque.add(o1);
    assertEquals(o1, deque.getFirst());
    checkDequeSizeAndContent(deque, o1);

    deque.add(o2);
    assertEquals(o1, deque.getFirst());
    checkDequeSizeAndContent(deque, o1, o2);

    deque.clear();
    assertTrue(deque.isEmpty());
    try {
      deque.getFirst();
      fail();
    } catch (NoSuchElementException expected) {
    }
  }

  public void testGetLast() {
    Object o1 = new Object();
    Object o2 = new Object();

    ArrayDeque<Object> deque = new ArrayDeque<Object>();
    try {
      deque.getLast();
      fail();
    } catch (NoSuchElementException expected) {
    }

    deque.add(o1);
    assertEquals(o1, deque.getLast());
    checkDequeSizeAndContent(deque, o1);

    deque.add(o2);
    assertEquals(o2, deque.getLast());
    checkDequeSizeAndContent(deque, o1, o2);

    deque.clear();
    assertTrue(deque.isEmpty());
    try {
      deque.getLast();
      fail();
    } catch (NoSuchElementException expected) {
    }
  }

  public void testOffer() {
    Object o1 = new Object();
    Object o2 = new Object();
    Object o3 = new Object();

    ArrayDeque<Object> deque = new ArrayDeque<Object>();
    assertTrue(deque.offer(o1));
    checkDequeSizeAndContent(deque, o1);
    assertTrue(deque.offer(o2));
    checkDequeSizeAndContent(deque, o1, o2);
    assertTrue(deque.offer(o3));
    checkDequeSizeAndContent(deque, o1, o2, o3);

    try {
      deque.offer(null);
      fail();
    } catch (NullPointerException expected) {
    }
  }

  public void testOfferFirst() {
    Object o1 = new Object();
    Object o2 = new Object();
    Object o3 = new Object();

    ArrayDeque<Object> deque = new ArrayDeque<Object>();
    assertTrue(deque.offerFirst(o1));
    checkDequeSizeAndContent(deque, o1);
    assertTrue(deque.offerFirst(o2));
    checkDequeSizeAndContent(deque, o2, o1);
    assertTrue(deque.offerFirst(o3));
    checkDequeSizeAndContent(deque, o3, o2, o1);

    try {
      deque.offerFirst(null);
      fail();
    } catch (NullPointerException expected) {
    }
  }

  public void testOfferLast() {
    Object o1 = new Object();
    Object o2 = new Object();
    Object o3 = new Object();

    ArrayDeque<Object> deque = new ArrayDeque<Object>();
    assertTrue(deque.offerLast(o1));
    checkDequeSizeAndContent(deque, o1);
    assertTrue(deque.offerLast(o2));
    checkDequeSizeAndContent(deque, o1, o2);
    assertTrue(deque.offerLast(o3));
    checkDequeSizeAndContent(deque, o1, o2, o3);

    try {
      deque.offerLast(null);
      fail();
    } catch (NullPointerException expected) {
    }
  }

  public void testPeek() {
    Object o1 = new Object();
    Object o2 = new Object();
    Object o3 = new Object();

    ArrayDeque<Object> deque = new ArrayDeque<Object>();
    assertNull(deque.peek());

    deque.add(o1);
    assertEquals(o1, deque.peek());
    checkDequeSizeAndContent(deque, o1);

    deque.add(o2);
    assertEquals(o1, deque.peek());
    checkDequeSizeAndContent(deque, o1, o2);

    deque.addFirst(o3);
    assertEquals(o3, deque.peek());
    checkDequeSizeAndContent(deque, o3, o1, o2);

    deque.clear();
    assertTrue(deque.isEmpty());
    assertNull(deque.peek());
  }

  public void testPeekFirst() {
    Object o1 = new Object();
    Object o2 = new Object();
    Object o3 = new Object();

    ArrayDeque<Object> deque = new ArrayDeque<Object>();
    assertNull(deque.peekFirst());

    deque.add(o1);
    assertEquals(o1, deque.peekFirst());
    checkDequeSizeAndContent(deque, o1);

    deque.add(o2);
    assertEquals(o1, deque.peekFirst());
    checkDequeSizeAndContent(deque, o1, o2);

    deque.addFirst(o3);
    assertEquals(o3, deque.peekFirst());
    checkDequeSizeAndContent(deque, o3, o1, o2);

    deque.clear();
    assertTrue(deque.isEmpty());
    assertNull(deque.peekFirst());
  }

  public void testPeekLast() {
    Object o1 = new Object();
    Object o2 = new Object();
    Object o3 = new Object();

    ArrayDeque<Object> deque = new ArrayDeque<Object>();
    assertNull(deque.peekLast());

    deque.add(o1);
    assertEquals(o1, deque.peekLast());
    checkDequeSizeAndContent(deque, o1);

    deque.add(o2);
    assertEquals(o2, deque.peekLast());
    checkDequeSizeAndContent(deque, o1, o2);

    deque.addFirst(o3);
    assertEquals(o2, deque.peekLast());
    checkDequeSizeAndContent(deque, o3, o1, o2);

    deque.clear();
    assertTrue(deque.isEmpty());
    assertNull(deque.peekLast());
  }

  public void testPoll() {
    Object o1 = new Object();
    Object o2 = new Object();

    ArrayDeque<Object> deque = new ArrayDeque<Object>();
    assertNull(deque.poll());

    deque.add(o1);
    assertEquals(o1, deque.poll());
    assertTrue(deque.isEmpty());

    deque.add(o1);
    deque.add(o2);
    assertEquals(o1, deque.poll());
    checkDequeSizeAndContent(deque, o2);
    assertEquals(o2, deque.poll());
    assertTrue(deque.isEmpty());
    assertNull(deque.poll());
  }

  public void testPollFirst() {
    Object o1 = new Object();
    Object o2 = new Object();

    ArrayDeque<Object> deque = new ArrayDeque<Object>();
    assertNull(deque.pollFirst());

    deque.add(o1);
    assertEquals(o1, deque.pollFirst());
    assertTrue(deque.isEmpty());
    assertNull(deque.pollFirst());

    deque.add(o1);
    deque.add(o2);
    assertEquals(o1, deque.pollFirst());
    checkDequeSizeAndContent(deque, o2);
    assertEquals(o2, deque.pollFirst());
    assertTrue(deque.isEmpty());
    assertNull(deque.pollFirst());
  }

  public void testPollLast() {
    Object o1 = new Object();
    Object o2 = new Object();

    ArrayDeque<Object> deque = new ArrayDeque<Object>();
    assertNull(deque.pollLast());

    deque.add(o1);
    assertEquals(o1, deque.pollLast());
    assertTrue(deque.isEmpty());
    assertNull(deque.pollFirst());

    deque.add(o1);
    deque.add(o2);
    assertEquals(o2, deque.pollLast());
    checkDequeSizeAndContent(deque, o1);
    assertEquals(o1, deque.pollLast());
    assertTrue(deque.isEmpty());
    assertNull(deque.pollLast());
  }

  public void testPop() {
    Object o1 = new Object();
    Object o2 = new Object();

    ArrayDeque<Object> deque = new ArrayDeque<Object>();
    try {
      deque.pop();
      fail();
    } catch (NoSuchElementException expected) {
    }

    deque.add(o1);
    assertEquals(o1, deque.pop());
    assertTrue(deque.isEmpty());

    deque.add(o1);
    deque.add(o2);
    assertEquals(o1, deque.pop());
    checkDequeSizeAndContent(deque, o2);
    assertEquals(o2, deque.pop());
    assertTrue(deque.isEmpty());
    try {
      deque.pop();
      fail();
    } catch (NoSuchElementException expected) {
    }
  }

  public void testPush() {
    Object o1 = new Object();
    Object o2 = new Object();
    Object o3 = new Object();

    ArrayDeque<Object> deque = new ArrayDeque<Object>();
    deque.push(o1);
    checkDequeSizeAndContent(deque, o1);
    deque.push(o2);
    checkDequeSizeAndContent(deque, o2, o1);
    deque.push(o3);
    checkDequeSizeAndContent(deque, o3, o2, o1);

    try {
      deque.push(null);
      fail();
    } catch (NullPointerException expected) {
    }
  }

  public void testRemove() {
    Object o1 = new Object();
    Object o2 = new Object();

    ArrayDeque<Object> deque = new ArrayDeque<Object>();
    try {
      deque.remove();
      fail();
    } catch (NoSuchElementException expected) {
    }

    deque.add(o1);
    assertEquals(o1, deque.remove());
    assertTrue(deque.isEmpty());

    deque.add(o1);
    deque.add(o2);
    assertEquals(o1, deque.remove());
    checkDequeSizeAndContent(deque, o2);
    assertEquals(o2, deque.removeFirst());
    assertTrue(deque.isEmpty());

    try {
      deque.remove();
      fail();
    } catch (NoSuchElementException expected) {
    }
  }

  public void testRemoveElement() {
    Object o1 = new Object();
    Object o2 = new Object();

    ArrayDeque<Object> deque = new ArrayDeque<Object>();
    assertFalse(deque.remove(o1));

    deque.add(o1);
    assertTrue(deque.remove(o1));
    assertTrue(deque.isEmpty());

    deque.add(o1);
    deque.add(o2);
    assertTrue(deque.remove(o1));
    checkDequeSizeAndContent(deque, o2);
    assertTrue(deque.remove(o2));
    assertTrue(deque.isEmpty());

    assertFalse(deque.remove(null));
  }

  public void testRemoveFirst() {
    Object o1 = new Object();
    Object o2 = new Object();

    ArrayDeque<Object> deque = new ArrayDeque<Object>();
    try {
      deque.removeFirst();
      fail();
    } catch (NoSuchElementException expected) {
    }

    deque.add(o1);
    assertEquals(o1, deque.removeFirst());
    assertTrue(deque.isEmpty());

    deque.add(o1);
    deque.add(o2);
    assertEquals(o1, deque.removeFirst());
    checkDequeSizeAndContent(deque, o2);
    assertEquals(o2, deque.removeFirst());
    assertTrue(deque.isEmpty());

    try {
      deque.removeFirst();
      fail();
    } catch (NoSuchElementException expected) {
    }
  }

  public void testRemoveFirstOccurrence() {
    Object o1 = new Object();
    Object o2 = new Object();
    Object o3 = new Object();

    ArrayDeque<Object> deque = new ArrayDeque<Object>();
    assertFalse(deque.removeFirstOccurrence(o1));

    deque.add(o1);
    assertTrue(deque.removeFirstOccurrence(o1));
    assertTrue(deque.isEmpty());

    deque = new ArrayDeque<Object>();
    deque.add(o1);
    deque.add(o2);
    deque.add(o3);
    assertTrue(deque.removeFirstOccurrence(o2));
    checkDequeSizeAndContent(deque, o1, o3);

    deque = new ArrayDeque<Object>();
    deque.add(o1);
    deque.add(o2);
    deque.add(o3);
    deque.add(o1);
    deque.add(o2);
    deque.add(o3);
    assertTrue(deque.removeFirstOccurrence(o2));
    checkDequeSizeAndContent(deque, o1, o3, o1, o2, o3);
    assertTrue(deque.removeFirstOccurrence(o2));
    checkDequeSizeAndContent(deque, o1, o3, o1, o3);
    assertTrue(deque.removeFirstOccurrence(o1));
    checkDequeSizeAndContent(deque, o3, o1, o3);
    assertTrue(deque.removeFirstOccurrence(o1));
    checkDequeSizeAndContent(deque, o3, o3);
    assertFalse(deque.removeFirstOccurrence(o1));

    assertFalse(deque.removeFirstOccurrence(null));
  }

  public void testRemoveLast() {
    Object o1 = new Object();
    Object o2 = new Object();

    ArrayDeque<Object> deque = new ArrayDeque<Object>();
    try {
      deque.removeLast();
      fail();
    } catch (NoSuchElementException expected) {
    }

    deque.add(o1);
    assertEquals(o1, deque.removeLast());
    assertTrue(deque.isEmpty());

    deque.add(o1);
    deque.add(o2);
    assertEquals(o2, deque.removeLast());
    checkDequeSizeAndContent(deque, o1);
    assertEquals(o1, deque.removeLast());
    assertEquals(0, deque.size());

    try {
      deque.removeLast();
      fail();
    } catch (NoSuchElementException expected) {
    }
  }

  public void testRemoveLastOccurrence() {
    Object o1 = new Object();
    Object o2 = new Object();
    Object o3 = new Object();

    ArrayDeque<Object> deque = new ArrayDeque<Object>();
    assertFalse(deque.removeLastOccurrence(o1));

    deque.add(o1);
    assertTrue(deque.removeLastOccurrence(o1));
    assertTrue(deque.isEmpty());

    deque = new ArrayDeque<Object>();
    deque.add(o1);
    deque.add(o2);
    deque.add(o3);
    assertTrue(deque.removeLastOccurrence(o2));
    checkDequeSizeAndContent(deque, o1, o3);

    deque = new ArrayDeque<Object>();
    deque.add(o1);
    deque.add(o2);
    deque.add(o3);
    deque.add(o1);
    deque.add(o2);
    deque.add(o3);
    assertTrue(deque.removeLastOccurrence(o2));
    checkDequeSizeAndContent(deque, o1, o2, o3, o1, o3);
    assertTrue(deque.removeLastOccurrence(o2));
    checkDequeSizeAndContent(deque, o1, o3, o1, o3);
    assertTrue(deque.removeLastOccurrence(o3));
    checkDequeSizeAndContent(deque, o1, o3, o1);
    assertTrue(deque.removeLastOccurrence(o3));
    checkDequeSizeAndContent(deque, o1, o1);
    assertFalse(deque.removeLastOccurrence(o3));

    assertFalse(deque.removeLastOccurrence(null));
  }

  /**
   * Null elements are prohibited in ArrayDeque.
   */
  @Override
  protected Object[] getFullElements() {
    return getFullNonNullElements();
  }

  @Override
  protected Collection makeConfirmedCollection() {
    return new ArrayList<Object>();
  }

  @Override
  protected Collection makeConfirmedFullCollection() {
    return new ArrayList<Object>(Arrays.asList(getFullElements()));
  }

  @Override
  protected Collection makeCollection() {
    return new ArrayDeque<Object>();
  }

  private void checkDequeSizeAndContent(Deque<?> deque, Object... expected) {
    assertEquals(expected.length, deque.size());
    int i = 0;
    for (Object e : deque) {
      assertEquals(expected[i++], e);
    }
    assertEquals(expected.length, i);
  }
}
