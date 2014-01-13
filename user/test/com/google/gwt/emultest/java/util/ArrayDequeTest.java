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
import java.util.Deque;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Tests ArrayDeque class.
 */
public class ArrayDequeTest extends TestCollection {

  public void testAddFirst() {
    Object o1 = new Object();
    Object o2 = new Object();
    Object o3 = new Object();

    ArrayDeque<Object> l = new ArrayDeque<Object>();
    l.addFirst(o1);
    checkDequeSizeAndContent(l, o1);
    l.addFirst(o2);
    checkDequeSizeAndContent(l, o2, o1);
    l.addFirst(o3);
    checkDequeSizeAndContent(l, o3, o2, o1);
  }

  public void testAddLast() {
    Object o1 = new Object();
    Object o2 = new Object();
    Object o3 = new Object();

    ArrayDeque<Object> l = new ArrayDeque<Object>();
    l.addLast(o1);
    checkDequeSizeAndContent(l, o1);
    l.addLast(o2);
    checkDequeSizeAndContent(l, o1, o2);
    l.addLast(o3);
    checkDequeSizeAndContent(l, o1, o2, o3);
  }

  public void testDescendingIterator() {
    Object o1 = new Object();
    Object o2 = new Object();
    Object o3 = new Object();

    ArrayDeque<Object> l = new ArrayDeque<Object>();
    Iterator<Object> it = l.descendingIterator();
    assertFalse(it.hasNext());
    try {
      it.next();
      fail();
    } catch (NoSuchElementException ignored) {
    }

    l.add(o1);
    l.add(o2);
    l.add(o3);
    it = l.descendingIterator();
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
    } catch (NoSuchElementException ignored) {
    }
    checkDequeSizeAndContent(l, o1, o2, o3);

    l = new ArrayDeque<Object>();
    l.add(o1);
    l.add(o2);
    l.add(o3);
    it = l.descendingIterator();
    assertTrue(it.hasNext());
    assertEquals(o3, it.next());
    it.remove();
    assertEquals(2, l.size());
    assertTrue(it.hasNext());
    assertEquals(o2, it.next());
    assertTrue(it.hasNext());
    assertEquals(o1, it.next());
    it.remove();
    checkDequeSizeAndContent(l, o2);
  }

  public void testElement() {
    Object o1 = new Object();
    Object o2 = new Object();

    ArrayDeque<Object> l = new ArrayDeque<Object>();
    try {
      l.element();
      fail();
    } catch (NoSuchElementException ignored) {
    }

    l.add(o1);
    assertEquals(o1, l.element());
    checkDequeSizeAndContent(l, o1);

    l.add(o2);
    assertEquals(o1, l.element());
    checkDequeSizeAndContent(l, o1, o2);
  }

  public void testGetFirst() {
    Object o1 = new Object();
    Object o2 = new Object();

    ArrayDeque<Object> l = new ArrayDeque<Object>();
    try {
      l.getFirst();
      fail();
    } catch (NoSuchElementException ignored) {
    }

    l.add(o1);
    assertEquals(o1, l.getFirst());
    checkDequeSizeAndContent(l, o1);

    l.add(o2);
    assertEquals(o1, l.getFirst());
    checkDequeSizeAndContent(l, o1, o2);
  }

  public void testGetLast() {
    Object o1 = new Object();
    Object o2 = new Object();

    ArrayDeque<Object> l = new ArrayDeque<Object>();
    try {
      l.getLast();
      fail();
    } catch (NoSuchElementException ignored) {
    }

    l.add(o1);
    assertEquals(o1, l.getLast());
    checkDequeSizeAndContent(l, o1);

    l.add(o2);
    assertEquals(o2, l.getLast());
    checkDequeSizeAndContent(l, o1, o2);
  }

  public void testOffer() {
    Object o1 = new Object();
    Object o2 = new Object();
    Object o3 = new Object();

    ArrayDeque<Object> l = new ArrayDeque<Object>();
    assertTrue(l.offer(o1));
    checkDequeSizeAndContent(l, o1);
    assertTrue(l.offer(o2));
    checkDequeSizeAndContent(l, o1, o2);
    assertTrue(l.offer(o3));
    checkDequeSizeAndContent(l, o1, o2, o3);
  }

  public void testOfferFirst() {
    Object o1 = new Object();
    Object o2 = new Object();
    Object o3 = new Object();

    ArrayDeque<Object> l = new ArrayDeque<Object>();
    assertTrue(l.offerFirst(o1));
    checkDequeSizeAndContent(l, o1);
    assertTrue(l.offerFirst(o2));
    checkDequeSizeAndContent(l, o2, o1);
    assertTrue(l.offerFirst(o3));
    checkDequeSizeAndContent(l, o3, o2, o1);
  }

  public void testOfferLast() {
    Object o1 = new Object();
    Object o2 = new Object();
    Object o3 = new Object();

    ArrayDeque<Object> l = new ArrayDeque<Object>();
    assertTrue(l.offerLast(o1));
    checkDequeSizeAndContent(l, o1);
    assertTrue(l.offerLast(o2));
    checkDequeSizeAndContent(l, o1, o2);
    assertTrue(l.offerLast(o3));
    checkDequeSizeAndContent(l, o1, o2, o3);
  }

  public void testPeek() {
    Object o1 = new Object();
    Object o2 = new Object();

    ArrayDeque<Object> l = new ArrayDeque<Object>();
    assertNull(l.peek());

    l.add(o1);
    assertEquals(o1, l.peek());
    checkDequeSizeAndContent(l, o1);

    l.add(o2);
    assertEquals(o1, l.peek());
    checkDequeSizeAndContent(l, o1, o2);
  }

  public void testPeekFirst() {
    Object o1 = new Object();
    Object o2 = new Object();

    ArrayDeque<Object> l = new ArrayDeque<Object>();
    assertNull(l.peekFirst());

    l.add(o1);
    assertEquals(o1, l.peekFirst());
    checkDequeSizeAndContent(l, o1);

    l.add(o2);
    assertEquals(o1, l.peekFirst());
    checkDequeSizeAndContent(l, o1, o2);
  }

  public void testPeekLast() {
    Object o1 = new Object();
    Object o2 = new Object();

    ArrayDeque<Object> l = new ArrayDeque<Object>();
    assertNull(l.peekLast());

    l.add(o1);
    assertEquals(o1, l.peekLast());
    checkDequeSizeAndContent(l, o1);

    l.add(o2);
    assertEquals(o2, l.peekLast());
    checkDequeSizeAndContent(l, o1, o2);
  }

  public void testPoll() {
    Object o1 = new Object();
    Object o2 = new Object();

    ArrayDeque<Object> l = new ArrayDeque<Object>();
    assertNull(l.poll());

    l.add(o1);
    assertEquals(o1, l.poll());
    assertTrue(l.isEmpty());

    l.add(o1);
    l.add(o2);
    assertEquals(o1, l.poll());
    checkDequeSizeAndContent(l, o2);
  }

  public void testPollFirst() {
    Object o1 = new Object();
    Object o2 = new Object();

    ArrayDeque<Object> l = new ArrayDeque<Object>();
    assertNull(l.pollFirst());

    l.add(o1);
    assertEquals(o1, l.pollFirst());
    assertTrue(l.isEmpty());

    l.add(o1);
    l.add(o2);
    assertEquals(o1, l.pollFirst());
    checkDequeSizeAndContent(l, o2);
  }

  public void testPollLast() {
    Object o1 = new Object();
    Object o2 = new Object();

    ArrayDeque<Object> l = new ArrayDeque<Object>();
    assertNull(l.pollLast());

    l.add(o1);
    assertEquals(o1, l.pollLast());
    assertTrue(l.isEmpty());

    l.add(o1);
    l.add(o2);
    assertEquals(o2, l.pollLast());
    checkDequeSizeAndContent(l, o1);
  }

  public void testPop() {
    Object o1 = new Object();
    Object o2 = new Object();

    ArrayDeque<Object> l = new ArrayDeque<Object>();
    try {
      l.pop();
      fail();
    } catch (NoSuchElementException ignored) {
    }

    l.add(o1);
    assertEquals(o1, l.pop());
    assertTrue(l.isEmpty());

    l.add(o1);
    l.add(o2);
    assertEquals(o1, l.pop());
    checkDequeSizeAndContent(l, o2);
  }

  public void testPush() {
    Object o1 = new Object();
    Object o2 = new Object();
    Object o3 = new Object();

    ArrayDeque<Object> l = new ArrayDeque<Object>();
    l.push(o1);
    checkDequeSizeAndContent(l, o1);
    l.push(o2);
    checkDequeSizeAndContent(l, o2, o1);
    l.push(o3);
    checkDequeSizeAndContent(l, o3, o2, o1);
  }

  public void testRemove() {
    Object o1 = new Object();
    Object o2 = new Object();

    ArrayDeque<Object> l = new ArrayDeque<Object>();
    try {
      l.remove();
      fail();
    } catch (NoSuchElementException ignored) {
    }

    l.add(o1);
    assertEquals(o1, l.remove());
    assertTrue(l.isEmpty());

    l.add(o1);
    l.add(o2);
    assertEquals(o1, l.remove());
    checkDequeSizeAndContent(l, o2);
  }

  public void testRemoveFirst() {
    Object o1 = new Object();
    Object o2 = new Object();

    ArrayDeque<Object> l = new ArrayDeque<Object>();
    try {
      l.removeFirst();
      fail();
    } catch (NoSuchElementException ignored) {
    }

    l.add(o1);
    assertEquals(o1, l.removeFirst());
    assertTrue(l.isEmpty());

    l.add(o1);
    l.add(o2);
    assertEquals(o1, l.removeFirst());
    checkDequeSizeAndContent(l, o2);
  }

  public void testRemoveFirstOccurrence() {
    Object o1 = new Object();
    Object o2 = new Object();
    Object o3 = new Object();

    ArrayDeque<Object> l = new ArrayDeque<Object>();
    assertFalse(l.removeFirstOccurrence(o1));

    l.add(o1);
    assertTrue(l.removeFirstOccurrence(o1));
    assertTrue(l.isEmpty());

    l = new ArrayDeque<Object>();
    l.add(o1);
    l.add(o2);
    l.add(o3);
    assertTrue(l.removeFirstOccurrence(o2));
    checkDequeSizeAndContent(l, o1, o3);

    l = new ArrayDeque<Object>();
    l.add(o1);
    l.add(o2);
    l.add(o3);
    l.add(o1);
    l.add(o2);
    l.add(o3);
    assertTrue(l.removeFirstOccurrence(o2));
    checkDequeSizeAndContent(l, o1, o3, o1, o2, o3);
  }

  public void testRemoveLast() {
    Object o1 = new Object();
    Object o2 = new Object();

    ArrayDeque<Object> l = new ArrayDeque<Object>();
    try {
      l.removeLast();
      fail();
    } catch (NoSuchElementException ignored) {
    }

    l.add(o1);
    assertEquals(o1, l.removeLast());
    assertTrue(l.isEmpty());

    l.add(o1);
    l.add(o2);
    assertEquals(o2, l.removeLast());
    checkDequeSizeAndContent(l, o1);
  }

  public void testRemoveLastOccurrence() {
    Object o1 = new Object();
    Object o2 = new Object();
    Object o3 = new Object();

    ArrayDeque<Object> l = new ArrayDeque<Object>();
    assertFalse(l.removeLastOccurrence(o1));

    l.add(o1);
    assertTrue(l.removeLastOccurrence(o1));
    assertTrue(l.isEmpty());

    l = new ArrayDeque<Object>();
    l.add(o1);
    l.add(o2);
    l.add(o3);
    assertTrue(l.removeLastOccurrence(o2));
    checkDequeSizeAndContent(l, o1, o3);

    l = new ArrayDeque<Object>();
    l.add(o1);
    l.add(o2);
    l.add(o3);
    l.add(o1);
    l.add(o2);
    l.add(o3);
    assertTrue(l.removeLastOccurrence(o2));
    checkDequeSizeAndContent(l, o1, o2, o3, o1, o3);
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
  }
}
