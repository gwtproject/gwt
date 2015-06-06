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
package com.google.gwt.emultest.java.util;

import com.google.gwt.core.client.JavaScriptException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Queue;
import java.util.Set;

/**
 * Test various collections.
 */
public class CollectionsTest extends EmulTestBase {
  
  static class MyInt {
    int data;

    public MyInt(int value) {
      data = value;
    }

    public int compareTo(MyInt object) {
      return data > object.data ? 1 : (data < object.data ? -1 : 0);
    }
  }
  static Object[] objArray;
  
  static Object[] myobjArray;
  
  public static List<Integer> createRandomList() {
    ArrayList<Integer> l = new ArrayList<Integer>();
    l.add(new Integer(5));
    l.add(new Integer(2));
    l.add(new Integer(3));
    l.add(new Integer(1));
    l.add(new Integer(4));
    return l;
  }


  public static List<String> createSortedList() {
    ArrayList<String> l = new ArrayList<String>();
    l.add("a");
    l.add("b");
    l.add("c");
    return l;
  }

  private static Entry<String, String> dummyEntry() {
    return Collections.singletonMap("foo", "bar").entrySet().iterator().next();
  }

  {
    objArray = new Object[1000];
    myobjArray = new Object[1000];
    for (int i = 0; i < objArray.length; i++) {
      objArray[i] = new Integer(i);
      myobjArray[i] = new MyInt(i);
    }
  }

  public void testAsLifoQueue() {
    Object o1 = new Object();
    Object o2 = new Object();
    Object o3 = new Object();
    LinkedList<Object> deque = new LinkedList<Object>();
    Queue<Object> queueView = Collections.asLifoQueue(deque);

    assertTrue(queueView.isEmpty());
    assertEquals(0, queueView.size());

    queueView.add(o1);
    assertEquals(deque.getFirst(), o1);
    assertEquals(deque.element(), o1);
    queueView.add(o2);
    assertEquals(deque.getFirst(), o2);
    assertEquals(deque.element(), o2);
    queueView.offer(o3);
    assertEquals(deque.getFirst(), o3);
    assertEquals(deque.element(), o3);

    assertEquals(deque.size(), 3);
    assertEquals(deque.size(), queueView.size());

    assertEquals(deque.getFirst(), o3);
    assertEquals(queueView.element(), o3);
    assertEquals(queueView.peek(), o3);
    assertEquals(queueView.poll(), o3);

    assertEquals(deque.getFirst(), o2);
    assertEquals(queueView.element(), o2);
    assertEquals(queueView.peek(), o2);
    assertEquals(queueView.poll(), o2);

    assertEquals(deque.getFirst(), o1);
    assertEquals(queueView.element(), o1);
    assertEquals(queueView.peek(), o1);
    assertEquals(queueView.remove(), o1);

    assertTrue(deque.isEmpty());
    assertEquals(deque.size(), queueView.size());
    assertTrue(queueView.isEmpty());

    queueView.add(o1);
    queueView.add(o2);
    queueView.add(o3);

    Iterator<Object> dequeIterator = deque.iterator();
    Iterator<Object> queueIterator = queueView.iterator();
    while (dequeIterator.hasNext() && queueIterator.hasNext()) {
      assertEquals(dequeIterator.next(), queueIterator.next());
    }
    assertEquals(dequeIterator.hasNext(), queueIterator.hasNext());

    assertEquals(deque.toArray(), queueView.toArray());
    assertEquals(deque.toString(), queueView.toString());

    queueView.clear();
    assertTrue(deque.isEmpty());
    assertTrue(queueView.isEmpty());
  }

  /**
   * Test Collections.binarySearch(List, Object).
   * 
   * Verify the following cases: empty List odd numbers of elements even numbers
   * of elements not found value larger than all elements not found value
   * smaller than all elements
   */
  public void testBinarySearchObject() {
    List<String> a1 = new ArrayList<String>();
    int ret = Collections.binarySearch(a1, "");
    assertEquals(-1, ret);
    List<String> a2 = new ArrayList<String>(Arrays.asList(new String[] {
        "a", "g", "y"}));
    ret = Collections.binarySearch(a2, "c");
    assertEquals(-2, ret);
    ret = Collections.binarySearch(a2, "y");
    assertEquals(2, ret);
    List<String> a3 = new ArrayList<String>(Arrays.asList(new String[] {
        "b", "c", "x", "y"}));
    ret = Collections.binarySearch(a3, "z");
    assertEquals(-5, ret);
    ret = Collections.binarySearch(a3, "a");
    assertEquals(-1, ret);
    ret = Collections.binarySearch(a3, "b");
    assertEquals(0, ret);
  }

  /**
   * Test Collections.binarySearch(List, Object, Comparator).
   * 
   * Verify the following cases: empty List odd numbers of elements even numbers
   * of elements not found value larger than all elements not found value
   * smaller than all elements null Comparator uses natural ordering
   */
  public void testBinarySearchObjectComparator() {
    Comparator<String> inverseSort = new Comparator<String>() {
      @Override
      public int compare(String o1, String o2) {
        return o2.compareTo(o1);
      }
    };
    List<String> a1 = new ArrayList<String>();
    int ret = Collections.binarySearch(a1, "", inverseSort);
    assertEquals(-1, ret);
    List<String> a2 = new ArrayList<String>(Arrays.asList(new String[] {
        "y", "g", "a"}));
    ret = Collections.binarySearch(a2, "c", inverseSort);
    assertEquals(-3, ret);
    ret = Collections.binarySearch(a2, "a", inverseSort);
    assertEquals(2, ret);
    List<String> a3 = new ArrayList<String>(Arrays.asList(new String[] {
        "y", "x", "c", "b"}));
    ret = Collections.binarySearch(a3, "a", inverseSort);
    assertEquals(-5, ret);
    ret = Collections.binarySearch(a3, "z", inverseSort);
    assertEquals(-1, ret);
    ret = Collections.binarySearch(a3, "y", inverseSort);
    assertEquals(0, ret);

    List<String> a4 = new ArrayList<String>(Arrays.asList(new String[] {
        "a", "b", "c", "d", "e"}));
    ret = Collections.binarySearch(a4, "d", null); // should not NPE
    assertEquals(3, ret);
  }

  public void testEntrySetToArrayOversized() {
    Map<String, String> delegate = new HashMap<String, String>();
    delegate.put("key", "value");
    Map<String, String> unmodifiable = Collections.unmodifiableMap(delegate);

    @SuppressWarnings("unchecked")
    Entry<String, String>[] oversizedArray = new Entry[3];
    oversizedArray[0] = dummyEntry();
    oversizedArray[1] = dummyEntry();
    oversizedArray[2] = dummyEntry();

    Entry<String, String>[] result = unmodifiable.entrySet().toArray(
        oversizedArray);
    assertSame(result, oversizedArray);
    assertEquals("key", result[0].getKey());
    assertEquals("value", result[0].getValue());
    assertNull("The element after last should be null.", result[1]);
  }

  public void testFill() {
    List<String> a = createSortedList();
    Collections.fill(a, null);
    assertEquals(new Object[a.size()], a);

    List<Integer> b = createRandomList();
    Collections.fill(b, null);
    assertEquals(new Object[b.size()], b);
  }

  public void testListCopy() {
    List<Integer> src = new ArrayList<Integer>(Arrays.asList(1, 2, 3));
    List<Integer> dest = new ArrayList<Integer>(Arrays.asList(1, 2));

    try {
      Collections.copy(dest, src);
      fail();
    } catch (IndexOutOfBoundsException expected) {
    }

    dest = new ArrayList<Integer>(Arrays.asList(5, 6, 7, 8));
    Collections.copy(dest, src);
    assertEquals(new Integer[]{1, 2, 3, 8}, dest);

    dest = new ArrayList<Integer>(Arrays.asList(5, 6, 7));
    Collections.copy(dest, src);
    assertEquals(new Integer[]{1, 2, 3}, dest);
  }

  public void testNewSetFromMap() {
    Object o1 = new Object();
    Object o2 = new Object();
    Object o3 = new Object();

    try {
      HashMap<Object, Boolean> nonEmptyMap = new HashMap<Object, Boolean>();
      nonEmptyMap.put(o1, true);
      Collections.newSetFromMap(nonEmptyMap);
      fail();
    } catch (IllegalArgumentException e) {
    }

    Set<Object> set = Collections.newSetFromMap(new HashMap<Object, Boolean>());

    set.add(o1);
    assertTrue(set.contains(o1));
    assertEquals(1, set.size());

    set.add(o2);
    assertTrue(set.contains(o2));
    assertEquals(2, set.size());

    set.add(o3);
    assertTrue(set.contains(o3));
    assertEquals(3, set.size());

    set.remove(o2);
    assertFalse(set.contains(o2));
    assertEquals(2, set.size());

    set.clear();
    assertEquals(0, set.size());
  }

  public void testReverse() {
    List<String> a = createSortedList();
    Collections.reverse(a);
    Object[] x = {"c", "b", "a"};
    assertEquals(x, a);

    List<Integer> b = createRandomList();
    Collections.reverse(b);
    Collections.reverse(b);
    assertEquals(b, createRandomList());
  }
  
  /**
   * @tests java.util.Collections#rotate(java.util.List, int)
   */
  public void testRotate2() {
    List list = new ArrayList();
    try {
      Collections.rotate(list, 5);
    } catch (UnsupportedOperationException e) {
      fail("Unexpected UnsupportedOperationException for empty list, "
          + e);
    }

    list.add(0, "zero");
    list.add(1, "one");
    list.add(2, "two");
    list.add(3, "three");
    list.add(4, "four");

    Collections.rotate(list, Integer.MIN_VALUE);
    assertEquals("Rotated incorrectly at position 0, ", "three",
        (String) list.get(0));
    assertEquals("Rotated incorrectly at position 1, ", "four",
        (String) list.get(1));
    assertEquals("Rotated incorrectly at position 2, ", "zero",
        (String) list.get(2));
    assertEquals("Rotated incorrectly at position 3, ", "one",
        (String) list.get(3));
    assertEquals("Rotated incorrectly at position 4, ", "two",
        (String) list.get(4));
  }

  /**
   * @tests java.util.Collections#rotate(java.util.List, int)
   */
  public void testRotateLjavaUtilListI() {
    // Test for method rotate(java.util.List, int)

    try {
      Collections.rotate(null, 0);
      fail("Expected NullPointerException for null list parameter");
    } catch (NullPointerException | JavaScriptException e) {
            //Expected
    }

    // Test rotating a Sequential Access List
    LinkedList list1 = new LinkedList();
    for (int i = 0; i < 10; i++) {
      list1.add(objArray[i]);
    }
    testRotate(list1, "Sequential Access");

    // Test rotating a Random Access List
    ArrayList list2 = new ArrayList();
    for (int i = 0; i < 10; i++) {
      list2.add(objArray[i]);
    }
    testRotate(list2, "Random Access");
  }
  
  public void testSort() {
    List<String> a = createSortedList();
    Collections.reverse(a);
    Collections.sort(a);
    assertEquals(createSortedList(), a);
  }

  public void testSortWithComparator() {
    Comparator<String> x = new Comparator<String>() {
      @Override
      public int compare(String s1, String s2) {
        // sort into reverse order
        return s2.compareTo(s1);
      }
    };
    List<String> a = createSortedList();
    Collections.sort(a, x);
    Object[] expected = {"c", "b", "a"};
    assertEquals(expected, a);
  }


  public void testToArray() {
    List<Integer> testList = createRandomList();
    Integer[] testArray = new Integer[testList.size()];
    testList.toArray(testArray);
    for (int i = 0; i < testList.size(); ++i) {
      Integer val = testList.get(i);
      assertEquals(val, testArray[i]);
    }
  }

  private String getString(List list) {
    StringBuffer buffer = new StringBuffer();
    for (int i = 0; i < list.size(); i++) {
      buffer.append(list.get(i));
    }
    return buffer.toString();
  }

  private void testRotate(List list, String type) {
    // rotate with positive distance
    Collections.rotate(list, 7);
    assertEquals("Test1: rotate modified the " + type
        + " list incorrectly,", "3456789012", getString(list));

    // rotate with negative distance
    Collections.rotate(list, -2);
    assertEquals("Test2: rotate modified the " + type
        + " list incorrectly,", "5678901234", getString(list));

    // rotate sublist with negative distance
    List subList = list.subList(1, 5);
    Collections.rotate(subList, -1);
    assertEquals("Test3: rotate modified the " + type
        + " list incorrectly,", "5789601234", getString(list));

    // rotate sublist with positive distance
    Collections.rotate(subList, 2);
    assertEquals("Test4: rotate modified the " + type
        + " list incorrectly,", "5967801234", getString(list));

    // rotate with positive distance that is larger than list size
    Collections.rotate(list, 23);
    assertEquals("Test5: rotate modified the " + type
        + " list incorrectly,", "2345967801", getString(list));

    // rotate with negative distance that is larger than list size
    Collections.rotate(list, -23);
    assertEquals("Test6: rotate modified the " + type
        + " list incorrectly,", "5967801234", getString(list));

    // rotate with 0 and equivalent distances, this should make no
    // modifications to the list
    Collections.rotate(list, 0);
    assertEquals("Test7: rotate modified the " + type
        + " list incorrectly,", "5967801234", getString(list));

    Collections.rotate(list, -30);
    assertEquals("Test8: rotate modified the " + type
        + " list incorrectly,", "5967801234", getString(list));

    Collections.rotate(list, 30);
    assertEquals("Test9: rotate modified the " + type
        + " list incorrectly,", "5967801234", getString(list));
  }
}
