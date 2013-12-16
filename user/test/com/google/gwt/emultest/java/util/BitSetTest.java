/*
 * Copyright 2009 Google Inc.
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

import com.google.gwt.junit.client.GWTTestCase;
import com.google.gwt.user.client.Random;

import java.util.BitSet;
import java.util.HashSet;

/**
 * Tests BitSet class.
 */
public class BitSetTest extends GWTTestCase {

  /**
   * This class is used to describe numerical patterns.
   */
  protected interface Pattern {
    boolean contains(int i);
  }

  // count used for looping tests
  protected static final int TEST_SIZE = 509;

  // this number has to be bigger than 100
  protected static final int BIG_NUMBER = (TEST_SIZE) * 10 + 101;

  protected static void assertTrue(BitSet set, int index) {
    if (set.get(index) != true) {
      fail("expected=true set[" + index + "]=false");
    }
  }

  protected static void assertFalse(BitSet set, int index) {
    assertFalse("expected=false set[" + index + "]=true", set.get(index));
    if (set.get(index) != false) {
      fail("expected=false set[" + index + "]=true");
    }
  }

  protected static BitSet createSetOfMultiples(int multiple) {
    BitSet set = new BitSet(TEST_SIZE);
    for (int i = 0; i < TEST_SIZE; i += multiple) {
      set.set(i);
    }
    return set;
  }

  // this checks to see the values given are true, and the others around are not
  protected static void checkValues(BitSet set, int... values) {
    int valueIndex = 0;
    while (valueIndex != values.length) {
      int setIndex = Math.max(0, values[valueIndex] - 64);
      int count = -1;
      while (count != 64) {
        if (values[valueIndex] == setIndex) {
          assertTrue(set, setIndex);
          if (++valueIndex == values.length) {
            return;
          }
          count = 0;
        } else {
          assertFalse(set, setIndex);
        }
        setIndex++;
        count++;
      }
    }

    assertEquals(values.length, set.cardinality());
  }

  protected static void checkEqualityTrue(BitSet setA, BitSet setB) {
    assertTrue(setA.equals(setB));
    assertTrue(setB.equals(setA));
    assertTrue(setA.equals(setA));
    assertTrue(setB.equals(setB));
  }

  protected static void checkEqualityFalse(BitSet setA, BitSet setB) {
    assertFalse(setA.equals(setB));
    assertFalse(setB.equals(setA));
  }

  // Checks that the values in the given range are the only true values
  protected static void checkRange(BitSet set, int fromIndex, int toIndex) {
    for (int i = fromIndex; i < toIndex; i++) {
      assertTrue(set, i);
    }
    assertEquals(toIndex - fromIndex, set.cardinality());
  }

  // Checks that the values in the given range are the only true values
  protected static void checkRange(BitSet set, int fromIndex1, int toIndex1,
      int fromIndex2, int toIndex2) {
    for (int i = fromIndex1; i < toIndex1; i++) {
      assertTrue(set, i);
    }
    for (int i = fromIndex2; i < toIndex2; i++) {
      assertTrue(set, i);
    }
    assertEquals(toIndex1 - fromIndex1 + toIndex2 - fromIndex2,
        set.cardinality());
  }

  protected static void checkPattern(BitSet set, Pattern pattern) {
    for (int i = 0; i < TEST_SIZE; i++) {
      boolean contained = pattern.contains(i);
      if (contained != set.get(i)) {
        fail("expected=" + contained + " set[" + i + "]=" + !contained);
      }
    }
  }

  @Override
  public String getModuleName() {
    return "com.google.gwt.emultest.EmulSuite";
  }

  public void testConstructor() {
    BitSet set = new BitSet();

    // test what we know to be true of a new BitSet
    assertTrue(set.isEmpty());
    assertEquals(0, set.length());
    assertEquals(0, set.cardinality());

    // test exceptions
    try {
      set = new BitSet(-1);
      fail("exception expected");
    } catch (NegativeArraySizeException e) {
      // expected
    }

    try {
      set = new BitSet(0);
    } catch (NegativeArraySizeException e) {
      fail("unexpected exception");
    }

    try {
      set = new BitSet(BIG_NUMBER);
    } catch (NegativeArraySizeException e) {
      fail("unexpected exception");
    }
  }

  public void testRandom() {
    for (int loop = 0; loop < 10; loop++) {
      BitSet setA = new BitSet();
      HashSet<Integer> hashA = new HashSet<Integer>();
      for (int i = 0; i < 0x20; i++) {
        int next = Random.nextInt() & 0x7f;
        setA.set(next);
        hashA.add(next);
      }
      // check the size
      assertEquals(hashA.size(), setA.cardinality());

      BitSet setB = new BitSet();
      HashSet<Integer> hashB = new HashSet<Integer>();
      for (int i = 0; i < 0x18; i++) {
        int next = (Random.nextInt() & 0x3f) + 0x20;
        setB.set(next);
        hashB.add(next);
      }
      // check the size
      assertEquals(hashB.size(), setB.cardinality());

      BitSet setAnd = (BitSet)setA.clone();
      setAnd.and(setB);
      int countAnd = 0;

      BitSet setAndNot = (BitSet)setA.clone();
      setAndNot.andNot(setB);
      int countAndNot = 0;

      BitSet setOr = (BitSet)setA.clone();
      setOr.or(setB);
      int countOr = 0;

      BitSet setXor = (BitSet)setA.clone();
      setXor.xor(setB);
      int countXor = 0;

      // verify bitwise operations
      for (int i = 0; i < 0x80; i++) {
        boolean a = setA.get(i);
        boolean b = setB.get(i);

        boolean and = a & b;
        assertEquals(and, setAnd.get(i));
        if (and) {
          countAnd++;
        }

        boolean andNot = a & !b;
        assertEquals(andNot, setAndNot.get(i));
        if (andNot) {
          countAndNot++;
        }

        boolean or = a | b;
        assertEquals(or, setOr.get(i));
        if (or) {
          countOr++;
        }

        boolean xor = a ^ b;
        assertEquals(xor, setXor.get(i));
        if (xor) {
          countXor++;
        }
      }
      assertEquals(countAnd, setAnd.cardinality());
      assertEquals(countAndNot, setAndNot.cardinality());
      assertEquals(countOr, setOr.cardinality());
      assertEquals(countXor, setXor.cardinality());

      assertFalse(setAnd.intersects(setAndNot));
      assertFalse(setAnd.intersects(setXor));
      if (!setAnd.isEmpty()) {
        assertTrue(setOr.intersects(setAnd));
        assertTrue(setA.intersects(setAnd));
        assertTrue(setB.intersects(setAnd));
      }
      assertTrue(setA.intersects(setOr));
      assertTrue(setB.intersects(setOr));
    }
  }

  public void testAnd() {
    Pattern multiplesOf6 = new Pattern() {
      public boolean contains(int i) {
        return i % 6 == 0;
      }
    };

    // setA will contain all multiples of 2
    BitSet setA = createSetOfMultiples(2);

    // setB will contain all multiples of 3
    BitSet setB = createSetOfMultiples(3);

    // and()ing the sets should give multiples of 6
    setA.and(setB);

    // verify by checking multiples of 6
    checkPattern(setA, multiplesOf6);

    // and()ing a set to itself should do nothing
    setA.and(setA);

    // verify by checking multiples of 6
    checkPattern(setA, multiplesOf6);

    // and()ing with a set identical to itself should do nothing
    setA.and((BitSet) setA.clone());

    // verify by checking multiples of 6
    checkPattern(setA, multiplesOf6);

    // and()ing with all trues should do nothing
    BitSet trueSet = new BitSet(TEST_SIZE);
    trueSet.set(0, TEST_SIZE);
    setA.and(trueSet);

    // verify by checking multiples of 6
    checkPattern(setA, multiplesOf6);

    // and()ing with all trues in a larger set should do nothing
    trueSet.set(TEST_SIZE, TEST_SIZE * 2);
    setA.and(trueSet);

    // verify by checking multiples of 6
    checkPattern(setA, multiplesOf6);
    // there were "TEST_SIZE" extra trues, so lets verify those came out false
    for (int i = TEST_SIZE; i < TEST_SIZE * 2; i++) {
      assertFalse(setA.get(i));
    }

    // and()ing with an empty set should result in an empty set
    setA.and(new BitSet());
    assertEquals(0, setA.length());

    // these close bits should not intersect
    setB = new BitSet();
    setA.set(0);
    setB.set(1);
    setA.and(setB);
    assertTrue(setA.isEmpty());

    // these bits should not intersect
    setB = new BitSet();
    setA.set(0);
    setB.set(BIG_NUMBER);
    setA.and(setB);
    assertTrue(setA.isEmpty());
    setA.set(0);
    setB.and(setA);
    assertTrue(setB.isEmpty());
  }

  public void testAndNot() {
    Pattern multiplesOf2Not3 = new Pattern() {
      public boolean contains(int i) {
        return i % 2 == 0 && i % 3 != 0;
      }
    };

    // setA will contain all multiples of 2
    BitSet setA = createSetOfMultiples(2);

    // setB will contain all multiples of 3
    BitSet setB = createSetOfMultiples(3);

    // andNot() the sets
    setA.andNot(setB);

    // verify by checking for multiples of 2 that are not multiples of 3
    checkPattern(setA, multiplesOf2Not3);

    // andNot()ing with an empty set should do nothing
    setA.andNot(new BitSet());

    // verify by checking for multiples of 2 that are not multiples of 3
    checkPattern(setA, multiplesOf2Not3);

    // andNot()ing with all trues should result in an empty set
    BitSet trueSet = new BitSet(TEST_SIZE * 2);
    trueSet.set(0, TEST_SIZE * 2);
    setA.andNot(trueSet);
    assertTrue(setA.isEmpty());

    // save setB in setA
    setA = (BitSet) setB.clone();

    // andNot()ing a set to itself should result in an empty set
    setB.andNot(setB);
    assertTrue(setB.isEmpty());

    // andNot()ing a set identical to itself should result in an empty set
    setA.andNot((BitSet) setA.clone());
    assertTrue(setA.isEmpty());
  }

  public void testCardinality() {
    BitSet set = new BitSet(TEST_SIZE);

    // test the empty count
    assertEquals(0, set.cardinality());

    // test a count of 1
    set.set(0);
    assertEquals(1, set.cardinality());

    // test a count of 2
    set.set(BIG_NUMBER);
    assertEquals(2, set.cardinality());

    // clear them both and test again
    set.clear(0);
    set.clear(BIG_NUMBER);
    assertEquals(0, set.cardinality());

    // test different multiples
    for (int multiple = 1; multiple < 33; multiple++) {
      set = new BitSet();
      set.set(BIG_NUMBER + multiple);
      for (int i = 1; i < 33; i++) {
        set.set(i * multiple);
        assertEquals(i + 1, set.cardinality());
      }
    }

    // test powers of 2
    set = new BitSet();
    int count = 0;
    for (int i = 1; i < TEST_SIZE; i += i) {
      set.set(i);
      count++;
    }
    assertEquals(count, set.cardinality());

    // test a long run
    set = new BitSet();
    for (int i = 0; i < TEST_SIZE; i++) {
      set.set(i);
      assertEquals(i + 1, set.cardinality());
    }
  }

  public void testClear() {
    BitSet set = new BitSet(TEST_SIZE);
    for (int i = 0; i < TEST_SIZE; i++) {
      set.set(i);
    }

    set.clear();
    assertTrue(set.isEmpty());

    set = new BitSet();
    set.set(BIG_NUMBER);
    set.clear();
    assertFalse(set.get(BIG_NUMBER));
  }

  public void testClearInt() {
    // clear(int)

    BitSet set = new BitSet();
    set.set(0);
    set.clear(0);
    assertFalse(set.get(0));
    set.set(BIG_NUMBER);
    checkValues(set, BIG_NUMBER);
    set.clear(BIG_NUMBER);
    assertFalse(set.get(BIG_NUMBER));

    set = new BitSet();
    set.set(1);
    set.set(2);
    set.set(3);
    set.set(18);
    set.set(40);
    set.clear(2);
    checkValues(set, 1, 3, 18, 40);
    set.clear(9);
    checkValues(set, 1, 3, 18, 40);
    set.clear(18);
    checkValues(set, 1, 3, 40);
    set.clear(7);
    set.clear(6);
    checkValues(set, 1, 3, 40);
    set.clear(3);
    checkValues(set, 1, 40);
    set.clear(40);
    checkValues(set, 1);
    set.clear(1);
    assertTrue(set.isEmpty());

    // test exceptions
    try {
      set.clear(-1, 2);
      fail("exception expected");
    } catch (IndexOutOfBoundsException e) {
      // expected
    }

    try {
      set.clear(3, 1);
      fail("exception expected");
    } catch (IndexOutOfBoundsException e) {
      // expected
    }

    try {
      set.clear(2, 2);
    } catch (IndexOutOfBoundsException e) {
      fail("unexpected exception");
    }
  }

  public void testClearIntIntAndSetIntInt() {
    // clear(int, int) and set(int, int)

    BitSet set = new BitSet();

    set.set(7);
    set.set(50);
    set.set(BIG_NUMBER);
    set.clear(0, BIG_NUMBER);
    checkValues(set, BIG_NUMBER);
    set.clear(0, BIG_NUMBER + 1);
    assertTrue(set.isEmpty());

    set.set(0, 65);
    checkRange(set, 0, 65);
    set.clear(0, 63);
    checkRange(set, 63, 65);
    set.clear(63, 65);
    assertTrue(set.isEmpty());

    set.set(0, 128);
    set.clear(0, 64);
    checkRange(set, 64, 128);
    set.clear(0, 129);
    assertTrue(set.isEmpty());

    set.set(0, 65);
    checkRange(set, 0, 65);

    set.clear(0, 16);
    checkRange(set, 16, 65);

    set.set(0, 16);
    checkRange(set, 0, 65);
    set.clear(5, 5);
    set.clear(7, 14);
    set.clear(15, 42);
    set.clear(43, 55);
    set.clear(58, 62);
    checkValues(set, 0, 1, 2, 3, 4, 5, 6, 14, 42, 55, 56, 57, 62, 63, 64);
    set.clear(0, 65);
    assertTrue(set.isEmpty());

    set.set(0, 33);
    checkRange(set, 0, 33);
    set.clear(0, 8);
    checkRange(set, 8, 33);

    for (int i = 0; i < 33; i++) {
      // this shouldn't change anything
      set.clear(i, i);
      assertEquals(25, set.cardinality());
      // nor should this
      set.set(i, i);
      assertEquals(25, set.cardinality());
    }

    for (int i = 0; i < 65; i++) {
      set.set(0, 128);
      set.clear(i, 128 - i);
      checkRange(set, 0, i, 128 - i, 128);
    }
    set.clear(0, 128);
    assertTrue(set.isEmpty());

    set.set(7, 100);
    checkRange(set, 7, 100);

    set = new BitSet();
    set.set(BIG_NUMBER, BIG_NUMBER);
    assertEquals(0, set.cardinality());
    set.set(BIG_NUMBER, BIG_NUMBER + 1);
    checkRange(set, BIG_NUMBER, BIG_NUMBER + 1);
    set.clear(BIG_NUMBER, BIG_NUMBER);
    checkValues(set, BIG_NUMBER);
    set.clear(BIG_NUMBER, BIG_NUMBER + 1);
    assertEquals(0, set.cardinality());

    set = new BitSet();
    set.set(10, 12);
    set.clear(11, 1000);
    checkValues(set, 10);

    set = new BitSet();
    set.set(10, 12);
    set.clear(0, 10);
    checkValues(set, 10, 11);
    set.clear(10, 12);
    assertTrue(set.isEmpty());

    set = new BitSet();
    set.set(1, 20);
    set.clear(5, 10);
    checkRange(set, 1, 5, 10, 20);

    set = new BitSet();
    set.set(1, 10);
    set.clear(5, 15);
    checkRange(set, 1, 5);

    // test clear(int, int) exceptions
    try {
      set.clear(-1, 2);
      fail("exception expected");
    } catch (IndexOutOfBoundsException e) {
      // expected
    }

    try {
      set.clear(3, 1);
      fail("expected exception");
    } catch (IndexOutOfBoundsException e) {
      // expected
    }

    try {
      set.clear(2, 2);
    } catch (IndexOutOfBoundsException e) {
      fail("unexpected exception");
    }

    // test set(int, int) exceptions
    try {
      set.set(-1, 2);
      fail("exception expected");
    } catch (IndexOutOfBoundsException e) {
      // expected
    }

    try {
      set.set(3, 1);
      fail("exception expected");
    } catch (IndexOutOfBoundsException e) {
      // expected
    }

    try {
      set.set(2, 2);
    } catch (IndexOutOfBoundsException e) {
      fail("unexpected exception");
    }
  }

  public void testClone() {
    BitSet set = new BitSet();
    set.set(2);
    set.set(4);
    set.set(32);
    set.set(BIG_NUMBER);
    BitSet clone = (BitSet) set.clone();
    checkValues(clone, 2, 4, 32, BIG_NUMBER);
    assertTrue(set.equals(clone));
    assertEquals(4, clone.cardinality());
  }

  public void testEquals() {
    BitSet setA = new BitSet();
    BitSet setB = new BitSet();
    checkEqualityTrue(setA, setB);

    setA.set(0);
    setB.set(0);
    checkEqualityTrue(setA, setB);

    setA.set(BIG_NUMBER);
    setB.set(BIG_NUMBER);
    checkEqualityTrue(setA, setB);

    setA.clear(0);
    setB.clear(0);
    checkEqualityTrue(setA, setB);

    setA.clear(BIG_NUMBER);
    setB.clear(BIG_NUMBER);
    checkEqualityTrue(setA, setB);

    setA.set(0);
    setB.set(1);
    checkEqualityFalse(setA, setB);

    setA.set(2);
    setB.set(2);
    checkEqualityFalse(setA, setB);

    setA = new BitSet();
    setB = new BitSet();
    setA.set(Math.max(0, BIG_NUMBER - 8));
    setB.set(BIG_NUMBER + 1);
    checkEqualityFalse(setA, setB);
  }

  public void testFlipInt() {
    // flip(int)

    BitSet set = new BitSet();
    set.flip(0);
    assertTrue(set.get(0));
    set.flip(0);
    assertFalse(set.get(0));
    set.flip(BIG_NUMBER);
    assertTrue(set.get(BIG_NUMBER));
    set.flip(BIG_NUMBER);
    assertFalse(set.get(BIG_NUMBER));

    set = new BitSet();
    set.flip(1);
    set.flip(2);
    set.flip(3);
    set.flip(4);
    set.flip(6);
    set.flip(4);
    set.flip(6);
    set.flip(6);
    set.flip(6);
    set.flip(8);
    set.flip(10);
    set.flip(8);
    set.flip(2);
    set.flip(8);
    checkValues(set, 1, 3, 8, 10);
    set.flip(8);
    checkValues(set, 1, 3, 10);
    set.flip(3);
    checkValues(set, 1, 10);
    set.flip(10);
    set.flip(11);
    checkValues(set, 1, 11);
    set.flip(1);
    checkValues(set, 11);
    set.flip(11);
    assertTrue(set.isEmpty());

    // test exceptions
    try {
      set.flip(-1);
      fail("exception expected");
    } catch (IndexOutOfBoundsException e) {
      // expected
    }

    try {
      set.flip(BIG_NUMBER);
      set.flip(BIG_NUMBER);
    } catch (IndexOutOfBoundsException e) {
      fail("unexpected exception");
    }
  }

  public void testFlipIntInt() {
    // flip(int, int)

    BitSet set = new BitSet();
    set.flip(0, BIG_NUMBER);
    checkRange(set, 0, BIG_NUMBER);
    set.flip(0, BIG_NUMBER - 1);
    checkValues(set, BIG_NUMBER - 1);
    set.clear(0, BIG_NUMBER);
    assertTrue(set.isEmpty());

    set.flip(0, 33);
    set.flip(0, 8);
    checkRange(set, 8, 33);

    // current state is set.set(8,33)
    for (int i = 0; i < 33; i++) {
      // this shouldn't change anything
      set.flip(i, i);
      assertEquals(25, set.cardinality());
    }

    // current state is set.set(8,33)
    set.flip(0, 8);
    set.flip(7, 21);
    set.flip(22, 27);
    checkValues(set, 0, 1, 2, 3, 4, 5, 6, 21, 27, 28, 29, 30, 31, 32);

    set = new BitSet();
    set.flip(10, 12);
    set.flip(11, 1000);
    checkRange(set, 10, 11, 12, 1000);

    set = new BitSet();
    set.flip(10, 12);
    set.flip(0, 10);
    checkRange(set, 0, 12);
    set.flip(0, 12);
    assertTrue(set.isEmpty());

    set.flip(0, 64);
    set.flip(0, 63);
    checkValues(set, 63);
    set.flip(63, 64);
    assertTrue(set.isEmpty());

    set.flip(0, 130);
    checkRange(set, 0, 130);
    set.flip(0, 66);
    checkRange(set, 66, 130);
    set.flip(65, 131);
    checkRange(set, 65, 66, 130, 131);

    set = new BitSet();
    set.flip(1, 20);
    set.flip(5, 10);
    checkRange(set, 1, 5, 10, 20);

    set = new BitSet();
    set.flip(1, 10);
    set.flip(5, 15);
    checkRange(set, 1, 5, 10, 15);

    // test exceptions
    try {
      set.flip(-1, 2);
      fail("exception expected");
    } catch (IndexOutOfBoundsException e) {
      // expected
    }

    try {
      set.flip(3, 1);
      fail("exception expected");
    } catch (IndexOutOfBoundsException e) {
      // expected
    }

    try {
      set.flip(2, 2);
    } catch (IndexOutOfBoundsException e) {
      fail("unexpected exception");
    }
  }

  public void testGetIntAndSetInt() {
    // get(int) and set(int)

    BitSet set = new BitSet();
    set.set(0);
    assertTrue(set.get(0));
    assertFalse(set.get(1));
    assertFalse(set.get(2));
    assertFalse(set.get(100));

    set.set(BIG_NUMBER);
    assertFalse(set.get(BIG_NUMBER - 1));
    assertTrue(set.get(BIG_NUMBER));
    assertFalse(set.get(BIG_NUMBER + 1));

    set = new BitSet();
    set.set(0);
    set.set(4);
    set.set(7);
    set.set(10);
    set.set(31);
    set.set(32);
    set.set(33);
    set.set(69);
    checkValues(set, 0, 4, 7, 10, 31, 32, 33, 69);

    for (int loop = 0; loop < 8; loop++) {
      set = new BitSet();
      HashSet<Integer> hash = new HashSet<Integer>();
      for (int i = 0; i < 0x100; i++) {
        int next = Random.nextInt() & 0x3ff;
        set.set(next);
        hash.add(next);
      }

      // make sure the set contains everything in the hash
      for (int i : hash) {
        assertTrue(set.get(i));
      }
      // make sure they are the same length, and thus, equal
      assertEquals(hash.size(), set.cardinality());
    }

    // test get() exceptions
    try {
      set.get(-1);
      fail("exception expected");
    } catch (IndexOutOfBoundsException e) {
      // expected
    }

    try {
      set.get(BIG_NUMBER);
    } catch (IndexOutOfBoundsException e) {
      fail("unexpected exception");
    }

    // test set() exceptions
    try {
      set.set(-1);
      fail("exception expected");
    } catch (IndexOutOfBoundsException e) {
      // expected
    }

    try {
      set.set(BIG_NUMBER);
    } catch (IndexOutOfBoundsException e) {
      fail("unexpected exception");
    }
  }

  public void testGetIntInt() {
    // get(int, int)

    BitSet set = new BitSet();

    set.set(1);
    assertFalse(set.get(1, 1).get(0));
    assertTrue(set.get(1, 2).get(0));
    assertTrue(set.get(0, 2).get(1));

    set.set(32);
    set.set(50);
    set.set(BIG_NUMBER);

    BitSet subSet = set.get(0, BIG_NUMBER);
    checkValues(subSet, 1, 32, 50);

    subSet = set.get(1, BIG_NUMBER);
    checkValues(subSet, 0, 31, 49);

    subSet = set.get(2, BIG_NUMBER + 1);
    checkValues(subSet, 30, 48, BIG_NUMBER - 2);

    subSet = set.get(32, BIG_NUMBER * 2);
    checkValues(subSet, 0, 18, BIG_NUMBER - 32);
    assertEquals(3, subSet.cardinality());

    subSet = set.get(0, BIG_NUMBER + 1);
    assertEquals(set, subSet);

    set = new BitSet();
    for (int i = 8; i < 33; i++) {
      set.set(i);
    }
    for (int i = 0; i < 33; i++) {
      assertTrue(set.get(i, i).isEmpty());
    }

    // test exceptions
    try {
      set.get(-1, 2);
      fail("exception expected");
    } catch (IndexOutOfBoundsException e) {
      // expected
    }

    try {
      set.get(3, 1);
      fail("exception expected");
    } catch (IndexOutOfBoundsException e) {
      // expected
    }

    try {
      set.get(2, 2);
    } catch (IndexOutOfBoundsException e) {
      fail("unexpected exception");
    }
  }

  public void testHashCode() {
    // this is an unimportant test
    // Java's official BitSet doesn't pass this test

    HashSet<Integer> hashValues = new HashSet<Integer>();

    // count the collisions
    int collisions = 0;

    // hash an empty set
    hashValues.add(new BitSet().hashCode());

    // hash the set of {TEST_SIZE + 1}
    BitSet set = new BitSet();
    set.set(TEST_SIZE + 1);
    assertTrue(hashValues.add(set.hashCode()));

    // hash the set of {0, TEST_SIZE + 1}
    set.set(0);
    assertTrue(hashValues.add(set.hashCode()));

    // hash the set of {0}
    set = new BitSet();
    set.set(0);
    assertTrue(hashValues.add(set.hashCode()));

    for (int multiple = 1; multiple < 33; multiple++) {
      set = new BitSet();
      set.set(0);

      // fill a set with multiples
      for (int i = multiple; i < TEST_SIZE; i += multiple) {
        set.set(i);
        // hash the current set, except in the case of {0}
        if (i != 0) {
          if (hashValues.add(set.hashCode()) == false) {
            collisions++;
          }
          set.set(TEST_SIZE + 1);
          if (i != 0 && (hashValues.add(set.hashCode()) == false)) {
            collisions++;
          }
          set.clear(TEST_SIZE + 1);
        }
      }
    }

    assertEquals(0, collisions);
  }

  public void testIntersects() {
    final int prime = 37;
    for (int multiple = 1; multiple < prime; multiple++) {
      int size = prime * multiple + 1;

      // setA will contain all multiples of "multiple" up to "size"
      BitSet setA = new BitSet(size);
      for (int i = multiple; i < size; i += multiple) {
        setA.set(i);
      }

      // setB will contain all multiples of "prime" up to "size"
      BitSet setB = new BitSet();
      for (int i = prime; i < size; i += prime) {
        setB.set(i);
      }

      // the two sets should only intersect on the very last bit
      assertTrue(setA.intersects(setB));
      setA.clear(size - 1);
      assertFalse(setA.intersects(setB));

      // the inverse of a set should not intersect itself
      setB = new BitSet();
      for (int i = 0; i < prime; i++) {
        setB.set(i, !setA.get(i));
      }
      assertFalse(setA.intersects(setB));

      // a set intersects itself if it has any bits set
      assertTrue(setA.intersects(setA));
      setA = new BitSet();
      assertFalse(setA.intersects(setA));

      // an empty set doesn't intersect itself
      assertFalse(new BitSet().intersects(new BitSet()));
    }
  }

  public void testIsEmpty() {
    BitSet set = new BitSet();
    assertTrue(set.isEmpty());
    set.set(0);
    assertFalse(set.isEmpty());
    set = new BitSet();
    set.set(BIG_NUMBER);
    assertFalse(set.isEmpty());
  }

  public void testLength() {
    BitSet set = new BitSet();
    assertEquals(0, set.length());

    set.set(100);
    set.set(BIG_NUMBER);
    assertEquals(BIG_NUMBER + 1, set.length());
    set.clear(BIG_NUMBER);
    assertEquals(101, set.length());
    set.clear(100);
    assertEquals(0, set.length());
    set.set(0);
    assertEquals(1, set.length());

    set = new BitSet();
    for (int i = 0; i < 640; i++) {
      set.set(i);
      assertEquals(i + 1, set.length());
    }
    for (int i = 0; i < 639; i++) {
      set.clear(i);
      assertEquals(640, set.length());
    }
    set.clear(639);
    assertEquals(0, set.length());
  }

  public void testNextClearBit() {
    BitSet set = new BitSet();

    set = new BitSet();
    for (int i = 0; i < 10; i++) {
      assertEquals(i, set.nextClearBit(i));
    }

    set.set(0, 9);
    set.set(10, 50);
    for (int i = 1; i < 9; i++) {
      assertEquals(9, set.nextClearBit(i));
    }
    for (int i = 10; i < 50; i++) {
      assertEquals(50, set.nextClearBit(i));
    }

    set = new BitSet();
    set.set(61);
    for (int i = 0; i < 100; i++) {
      if (i == 60) {
        set.clear(61);
      }
      set.set(i);
      assertEquals(i + 1, set.nextClearBit(0));
    }

    // test exceptions
    try {
      set.nextClearBit(-1);
      fail("exception expected");
    } catch (IndexOutOfBoundsException e) {
      // expected
    }

    try {
      assertEquals(BIG_NUMBER, set.nextClearBit(BIG_NUMBER));
    } catch (IndexOutOfBoundsException e) {
      fail("unexpected exception");
    }
  }

  public void testNextSetBit() {
    BitSet set = new BitSet();

    assertEquals(-1, set.nextSetBit(0));
    set.set(0);
    set.set(1);
    assertEquals(0, set.nextSetBit(0));

    set = new BitSet();
    set.set(BIG_NUMBER);
    assertEquals(BIG_NUMBER, set.nextSetBit(0));
    assertEquals(BIG_NUMBER, set.nextSetBit(BIG_NUMBER));
    assertEquals(-1, set.nextSetBit(BIG_NUMBER + 1));

    for (int i = 0; i < TEST_SIZE; i++) {
      set.set(BIG_NUMBER + i);
    }
    set.set(TEST_SIZE / 2);
    assertEquals(TEST_SIZE / 2, set.nextSetBit(0));

    // test exceptions
    try {
      set.nextSetBit(-1);
      fail("exception expected");
    } catch (IndexOutOfBoundsException e) {
      // expected
    }

    try {
      assertEquals(new BitSet().nextSetBit(BIG_NUMBER), -1);
    } catch (IndexOutOfBoundsException e) {
      fail("unexpected exception");
    }
  }

  public void testOr() {
    Pattern multiplesOf2And5 = new Pattern() {
      public boolean contains(int i) {
        return i % 2 == 0 || i % 5 == 0;
      }
    };

    // setA will contain all multiples of 2
    BitSet setA = createSetOfMultiples(2);

    // setB will contain all multiples of 5
    BitSet setB = createSetOfMultiples(5);

    // or() the two sets to get both multiples of 2 and 5
    setA.or(setB);

    // verify multiples of 2 and 5
    checkPattern(setA, multiplesOf2And5);

    // or()ing a set to itself should do nothing
    setA.or(setA);

    // verify multiples of 2 and 5
    checkPattern(setA, multiplesOf2And5);

    // or()ing with set identical to itself should do nothing
    setA.or((BitSet) setA.clone());

    // verify multiples of 2 and 5
    checkPattern(setA, multiplesOf2And5);

    // or()ing with an empty set (all falses) should do nothing
    setA.or(new BitSet());

    // verify multiples of 2 and 5
    checkPattern(setA, multiplesOf2And5);

    // or()ing with all trues should result in all trues
    BitSet trueSet = new BitSet(TEST_SIZE * 2);
    trueSet.set(0, TEST_SIZE * 2);
    setA.or(trueSet);
    assertEquals(TEST_SIZE * 2, setA.cardinality());
  }

  public void testSetIntBoolean() {
    // set(int, boolean)

    BitSet set = new BitSet();
    set.set(0, true);
    assertTrue(set.get(0));
    set.set(0, false);
    assertFalse(set.get(0));
    set.set(BIG_NUMBER, true);
    assertTrue(set.get(BIG_NUMBER));
    set.set(BIG_NUMBER, false);
    assertFalse(set.get(BIG_NUMBER));

    set = new BitSet();
    set.set(1, true);
    set.set(2, true);
    set.set(3, true);
    set.set(4, true);
    set.set(6, true);
    set.set(4, false);
    set.set(6, true);
    set.set(6, false);
    set.set(6, false);
    set.set(8, true);
    set.set(10, true);
    set.set(8, false);
    set.set(2, false);
    set.set(8, true);
    checkValues(set, 1, 3, 8, 10);
    set.set(8, false);
    checkValues(set, 1, 3, 10);
    set.set(3, false);
    checkValues(set, 1, 10);
    set.set(10, false);
    set.set(11, true);
    checkValues(set, 1, 11);
    set.set(1, false);
    checkValues(set, 11);
    set.set(11, false);
    assertTrue(set.isEmpty());
  }

  public void testSetIntIntBoolean() {
    // set(int, int, boolean)

    BitSet set = new BitSet();
    set.set(0, BIG_NUMBER, true);
    assertEquals(set.cardinality(), BIG_NUMBER);
    set.set(0, BIG_NUMBER - 1, false);
    assertEquals(set.cardinality(), 1);
    set.set(0, BIG_NUMBER, false);
    assertTrue(set.isEmpty());

    set.set(0, 32, true);
    set.set(0, 8, false);
    checkRange(set, 8, 32);
    set.set(0, 8, true);
    set.set(7, 21, false);
    set.set(22, 27, false);
    checkValues(set, 0, 1, 2, 3, 4, 5, 6, 21, 27, 28, 29, 30, 31);

    set = new BitSet();
    set.set(11, 1000, true);
    set.set(10, 12, false);
    checkRange(set, 12, 1000);
    assertEquals(988, set.cardinality());

    set = new BitSet();
    set.set(10, 12, true);
    set.set(0, 10, true);
    checkRange(set, 0, 12);
    set.set(0, 12, false);
    assertTrue(set.isEmpty());

    set = new BitSet();
    set.set(1, 20, true);
    set.set(5, 10, false);
    checkRange(set, 1, 5, 10, 20);

    set = new BitSet();
    set.set(1, 10, true);
    set.set(5, 10, false);
    set.set(10, 15, true);
    checkRange(set, 1, 5, 10, 15);

    // test exceptions
    try {
      set.set(-1, 2, true);
      fail("exception expected");
    } catch (IndexOutOfBoundsException e) {
      // expected
    }

    try {
      set.set(3, 1, true);
      fail("exception expected");
    } catch (IndexOutOfBoundsException e) {
      // expected
    }

    try {
      set.set(2, 2, true);
    } catch (IndexOutOfBoundsException e) {
      fail("unexpected exception");
    }

    try {
      set.set(-1, 2, false);
      fail("exception expected");
    } catch (IndexOutOfBoundsException e) {
      // expected
    }

    try {
      set.set(3, 1, false);
      fail("exception expected");
    } catch (IndexOutOfBoundsException e) {
      // expected
    }

    try {
      set.set(2, 2, false);
    } catch (IndexOutOfBoundsException e) {
      fail("unexpected exception");
    }
  }

  public void testSize() {
    // this is an unimportant test

    BitSet set = new BitSet(7);
    assertTrue(set.size() >= 7);
    set = new BitSet(BIG_NUMBER);
    assertTrue(set.size() >= BIG_NUMBER);
  }

  public void testToString() {
    BitSet set = new BitSet();
    assertEquals("{}", set.toString());

    set.set(32);
    assertEquals("{32}", set.toString());

    set.set(BIG_NUMBER);
    assertEquals("{32, " + BIG_NUMBER + "}", set.toString());

    set.set(1);
    assertEquals("{1, 32, " + BIG_NUMBER + "}", set.toString());

    set.set(2);
    assertEquals("{1, 2, 32, " + BIG_NUMBER + "}", set.toString());
  }

  public void testXor() {
    Pattern exclusiveMultiples = new Pattern() {
      public boolean contains(int i) {
        return (i % 2 == 0) ^ (i % 3 == 0);
      }
    };

    // setA will contain all multiples of 2
    BitSet setA = createSetOfMultiples(2);

    // setB will contain all multiples of 3
    BitSet setB = createSetOfMultiples(3);

    // xor()ing the sets should give exclusive multiples of 2 and 3
    setA.xor(setB);

    // verify by checking for exclusive multiples of 2 and 3
    checkPattern(setA, exclusiveMultiples);

    // xor()ing a set to an empty set should do nothing
    setA.xor(new BitSet());

    // verify by checking for exclusive multiples of 2 and 3
    checkPattern(setA, exclusiveMultiples);

    // xor()ing a set with all trues should flip each bit
    BitSet trueSet = new BitSet(TEST_SIZE * 2);
    trueSet.set(0, TEST_SIZE * 2);
    setA.xor(trueSet);

    // verify by checking for !(exclusive multiples of 2 and 3)
    checkPattern(setA, new Pattern() {
      public boolean contains(int i) {
        return !((i % 2 == 0) ^ (i % 3 == 0));
      }
    });
    // there were "TEST_SIZE" extra trues, so verify those came out as true
    for (int i = TEST_SIZE; i < TEST_SIZE * 2; i++) {
      assertTrue(setA.get(i));
    }

    // xor()ing a set to itself should result in an empty set
    setA.xor(setA);
    assertTrue(setA.isEmpty());

    // xor()ing a set identical to itself should result in an empty set
    setB.xor((BitSet) setB.clone());
    assertTrue(setB.isEmpty());
  }
}
