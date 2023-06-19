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

import java.util.stream.DoubleStream;

/**
 * Tests for java.util.stream.DoubleStream Java 9 API emulation.
 */
public class DoubleStreamTest extends EmulTestBase {
  public void testIterate() {
    assertEquals(
        new double[] {10, 11, 12, 13, 14},
        DoubleStream.iterate(0, i -> i < 15, i -> i + 1).skip(10).toArray());
  }

  public void testTakeWhile() {
    assertEquals(
        new double[] {1, 2},
        DoubleStream.of(1, 2, 3, 4, 5).takeWhile(i -> i < 3).toArray()
    );
    assertEquals(0, DoubleStream.of(1, 2, 3, 4, 5).takeWhile(i -> i > 2).count());
  }

  public void testDropWhile() {
    assertEquals(
        new double[] {3, 4, 5},
        DoubleStream.of(1, 2, 3, 4, 5).dropWhile(i -> i < 3).toArray()
    );
    assertEquals(
        new double[] {1, 2, 3, 4, 5},
        DoubleStream.of(1, 2, 3, 4, 5).dropWhile(i -> i > 2).toArray()
    );
  }
}
