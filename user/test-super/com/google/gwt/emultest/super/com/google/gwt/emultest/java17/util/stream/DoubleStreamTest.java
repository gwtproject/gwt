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

import java.util.stream.DoubleStream;

public class DoubleStreamTest extends EmulTestBase {

  public void testMapMulti() {
    DoubleStream.DoubleMapMultiConsumer doubling = (num, callback) -> {
      callback.accept(num);
      callback.accept(num + num);
    };
    assertEquals(new double[0],
        hideFromCompiler(DoubleStream.of()).mapMulti(doubling).toArray());
    assertEquals(new double[]{1.0, 2.0, 3.0, 6.0},
        hideFromCompiler(DoubleStream.of(1.0, 3.0)).mapMulti(doubling).toArray());
    assertEquals(new double[0],
        hideFromCompiler(DoubleStream.of(1.0, 2.0)).mapMulti((a, b) -> {
        }).toArray());
  }
}
