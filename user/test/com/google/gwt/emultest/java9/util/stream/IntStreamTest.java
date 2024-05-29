/*
 * Copyright 2023 Google Inc.
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
package com.google.gwt.emultest.java9.util.stream;

import com.google.gwt.emultest.java.util.EmulTestBase;

import java.util.stream.IntStream;

/**
 * Tests for java.util.stream.IntStream Java 9 API emulation.
 */
public class IntStreamTest extends EmulTestBase {
  public void testIterate() {
    assertEquals(
        new int[] {10, 11, 12, 13, 14},
        IntStream.iterate(0, i -> i < 15, i -> i + 1).skip(10).toArray());

    // Check that the functions are called the correct number of times with a hasNext limiting
    // stream size
    {
      int[] nextCalledCount = {0};
      int[] hasNextCalledCount = {0};
      int[] array = IntStream.iterate(0, val -> {
        hasNextCalledCount[0]++;
        return val < 5;
      }, val -> {
        nextCalledCount[0]++;
        return val + 1;
      }).toArray();

      // Verify that the next was called for each value after the seed (plus one for testing that
      // the stream is over)
      assertEquals(array.length, nextCalledCount[0]);
      // Verify that the hasNext function was called once for each value (plus one as above)
      assertEquals(array.length + 1, hasNextCalledCount[0]);
      // Sanity check the values returned
      assertEquals(new int[]{0, 1, 2, 3, 4}, array);
    }

    // Same test repeated, but instead limit() will stop the stream from continuing
    {
      int[] nextCalledCount = {0};
      int[] hasNextCalledCount = {0};
      int[] array = IntStream.iterate(0, val -> {
        hasNextCalledCount[0]++;
        return val < 5;
      }, val -> {
        nextCalledCount[0]++;
        return val + 1;
      }).limit(3).toArray();

      // Verify that the next was called for each value after the seed
      assertEquals(array.length - 1, nextCalledCount[0]);
      // Verify that the hasNext function was called once for each value
      assertEquals(array.length, hasNextCalledCount[0]);
      // Sanity check the values returned
      assertEquals(new int[]{0, 1, 2}, array);
    }
  }

  public void testTakeWhile() {
    assertEquals(
        new int[] {1, 2},
        IntStream.of(1, 2, 3, 4, 5).takeWhile(i -> i < 3).toArray()
    );
    assertEquals(0, IntStream.of(1, 2, 3, 4, 5).takeWhile(i -> i > 2).count());

    assertEquals(
        new int[] {0, 1, 2, 3, 4},
        IntStream.iterate(0, i -> i + 1).takeWhile(i -> i < 5).toArray()
    );
  }

  public void testDropWhile() {
    assertEquals(
        new int[] {3, 4, 5},
        IntStream.of(1, 2, 3, 4, 5).dropWhile(i -> i < 3).toArray()
    );
    assertEquals(
        new int[] {1, 2, 3, 4, 5},
        IntStream.of(1, 2, 3, 4, 5).dropWhile(i -> i > 2).toArray()
    );

    // pass an infinite stream to dropWhile, ensure it handles it
    assertEquals(
        new int[] {5, 6, 7, 8, 9},
        IntStream.iterate(0, i -> i + 1).dropWhile(i -> i < 5).limit(5).toArray()
    );
  }
}
