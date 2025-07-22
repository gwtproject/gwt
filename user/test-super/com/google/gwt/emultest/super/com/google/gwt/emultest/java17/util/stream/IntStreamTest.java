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

import java.util.stream.IntStream;
import java.util.stream.Stream;

public class IntStreamTest extends EmulTestBase {

  public void testMapMulti() {
    IntStream.IntMapMultiConsumer doubling = (num, callback) -> {
      callback.accept(num);
      callback.accept(num + num);
    };
    assertEquals(new int[0],
        hideFromCompiler(IntStream.of()).mapMulti(doubling).toArray());
    assertEquals(new int[]{1, 2, 3, 6},
        hideFromCompiler(IntStream.of(1, 3)).mapMulti(doubling).toArray());
    assertEquals(new int[0],
        hideFromCompiler(IntStream.of(1, 3)).mapMulti((a, b) -> {
        }).toArray());
  }
}
