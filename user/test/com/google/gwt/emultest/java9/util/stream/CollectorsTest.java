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

import static com.google.gwt.emultest.java8.util.stream.CollectorsTest.applyItems;
import static java.util.stream.Collectors.filtering;
import static java.util.stream.Collectors.flatMapping;
import static java.util.stream.Collectors.toList;

import com.google.gwt.emultest.java.util.EmulTestBase;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collector;

/**
 * Tests for java.util.stream.Collectors Java 9 API emulation.
 */
public class CollectorsTest extends EmulTestBase {

  public void testFlatMapping() {
    // since applyItems tests the same inputs multiple times, we need fresh stream instances as they can't be reused
    Collector<Collection<String>, ?, List<String>> flatMapping = flatMapping(Collection::stream,
        toList());
    applyItems(Arrays.asList("a", "b"), flatMapping, Collections.singletonList("a"),
        Collections.singletonList("b"));
    applyItems(Arrays.asList("c", "d"), flatMapping, Collections.emptyList(), Arrays.asList("c", "d"));

    Collector<Collection<String>, ?, List<String>> flatMappingToNull = flatMapping(items -> {
      if (items.size() % 2 == 0) {
        // Return null instead of  empty
        return null;
      }
      return items.stream();
    }, toList());
    applyItems(Arrays.asList("a"), flatMappingToNull, Arrays.asList("a"), Arrays.asList("b", "c"));
  }

  public void testFiltering() {
    Collector<String, ?, List<String>> filtering = filtering(s -> s.equals("a"), toList());
    applyItems(Collections.singletonList("a"), filtering, "a", "b");
    applyItems(Collections.emptyList(), filtering, "c", "d");
    applyItems(Arrays.asList("a", "a"), filtering, "a", "a");
  }
}
