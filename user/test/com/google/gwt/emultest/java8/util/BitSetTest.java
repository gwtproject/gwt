/*
 * Copyright 2016 Google Inc.
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
package com.google.gwt.emultest.java8.util;

import com.google.gwt.emultest.java.util.EmulTestBase;

import java.util.BitSet;
import java.util.stream.IntStream;

/**
 * Java 8 methods to test in java.util.BitSet.
 */
public class BitSetTest extends EmulTestBase {

  public void testStream() {
    BitSet set = new BitSet();
    IntStream stream = set.stream();
    assertEquals(new int[0], stream.toArray());
    assertStreamClosed(stream);

    stream = set.stream();
    set.set(10);
    assertEquals(new int[] {10}, stream.toArray());
    assertStreamClosed(stream);

    stream = set.stream();
    set.set(50);
    set.set(51);
    set.set(100);
    assertEquals(new int[] {10, 50, 51, 100}, stream.toArray());
    assertStreamClosed(stream);

    stream = set.stream();
    set.clear(0, 100);
    assertEquals(new int[] {100}, stream.toArray());
    assertStreamClosed(stream);

    stream = set.stream();
    set.clear();
    assertEquals(new int[0], stream.toArray());
    assertStreamClosed(stream);
  }

  private static void assertStreamClosed(IntStream stream) {
    try {
      stream.toArray();
      fail("stream must be closed");
    } catch (IllegalStateException expected) {
    }
  }
}
