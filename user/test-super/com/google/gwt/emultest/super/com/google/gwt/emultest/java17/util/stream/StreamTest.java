/*
 * Copyright 2025 GWT Project Authors
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
package com.google.gwt.emultest.java17.util.stream;

import com.google.gwt.emultest.java.util.EmulTestBase;

import java.util.Arrays;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.DoubleConsumer;
import java.util.function.IntConsumer;
import java.util.function.LongConsumer;
import java.util.stream.Stream;

public class StreamTest extends EmulTestBase {

  public void testToList() {
    assertEquals(Arrays.asList(),
        hideFromCompiler(Stream.of()).toList());
    assertEquals(Arrays.asList("a", "b"),
        hideFromCompiler(Stream.of("a", "b")).toList());
  }

  public void testMapMulti() {
    BiConsumer<String, Consumer<String>> doubling = (str, callback) -> {
      callback.accept(str);
      callback.accept(str + str);
    };
    assertEquals(Arrays.asList(),
        this.<Stream<String>>hideFromCompiler(Stream.of()).<String>mapMulti(doubling).toList());
    assertEquals(Arrays.asList("a", "aa", "b", "bb"),
        hideFromCompiler(Stream.of("a", "b")).<String>mapMulti(doubling).toList());
    assertEquals(Arrays.asList(),
        hideFromCompiler(Stream.of("a", "b")).mapMulti((a, b) -> {
        }).toList());
  }

  public void testMapMultiToInt() {
    BiConsumer<String, IntConsumer> doubling = (str, callback) -> {
      callback.accept(str.length());
      callback.accept(str.length() * 2);
    };
    assertEquals(new int[]{1, 2, 3, 6},
        hideFromCompiler(Stream.of("a", "bbb")).mapMultiToInt(doubling).toArray());
    assertEquals(new int[0],
        hideFromCompiler(Stream.<String>of()).mapMultiToInt(doubling).toArray());
    assertEquals(new int[0],
        hideFromCompiler(Stream.of("a", "b")).mapMultiToInt((a, b) -> {
        }).toArray());
  }

  public void testMapMultiToLong() {
    BiConsumer<String, LongConsumer> doubling = (str, callback) -> {
      callback.accept(str.length());
      callback.accept(str.length() * 2L);
    };
    assertEquals(new long[]{1L, 2L, 3L, 6L},
        hideFromCompiler(Stream.of("a", "bbb")).mapMultiToLong(doubling).toArray());
    assertEquals(new long[0],
        hideFromCompiler(Stream.<String>of()).mapMultiToLong(doubling).toArray());
    assertEquals(new long[0],
        hideFromCompiler(Stream.of("a", "b")).mapMultiToLong((a, b) -> {
        }).toArray());
  }

  public void testMapMultiToDouble() {
    BiConsumer<String, DoubleConsumer> doubling = (str, callback) -> {
      callback.accept(str.length());
      callback.accept(str.length() * 2);
    };
    assertEquals(new double[]{1.0, 2.0, 3.0, 6.0},
        hideFromCompiler(Stream.of("a", "bbb")).mapMultiToDouble(doubling).toArray());
    assertEquals(new double[0],
        hideFromCompiler(Stream.<String>of()).mapMultiToDouble(doubling).toArray());
    assertEquals(new double[0],
        hideFromCompiler(Stream.of("a", "b")).mapMultiToDouble((a, b) -> {
        }).toArray());
  }
}
