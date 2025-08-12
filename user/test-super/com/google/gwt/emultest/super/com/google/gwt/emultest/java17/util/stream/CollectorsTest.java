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

import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Tests for java.lang.String Java 12 API emulation.
 */
public class CollectorsTest extends EmulTestBase {

  public void testTeeing() {
    Collector<Double, ?, Double> teeing = Collectors.teeing(
        Collectors.reducing(Double::sum),
        Collectors.counting(),
        (sum, count) -> sum.orElse(0.0) / count);
    assertEquals(4.0, Stream.of(2.0, 4.0, 6.0).collect(teeing), 0.01);
  }

}