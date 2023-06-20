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
package com.google.gwt.emultest.java10.util.stream;

import static com.google.gwt.emultest.java8.util.stream.CollectorsTest.applyItems;
import static java.util.stream.Collectors.toUnmodifiableList;
import static java.util.stream.Collectors.toUnmodifiableMap;
import static java.util.stream.Collectors.toUnmodifiableSet;

import com.google.gwt.emultest.java.util.EmulTestBase;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * Tests for java.util.stream.Collectors Java 10 API emulation.
 */
public class CollectorsTest extends EmulTestBase {
  private static <T> boolean unmodifiableCollection(Collection<T> c, T existingSample, T newSample) {
    try {
      c.remove(existingSample);
      return false;
    } catch (Exception ignore) {
      // expected
    }
    try {
      c.add(newSample);
      return false;
    } catch (Exception ignore) {
      // expected
    }
    Iterator<T> itr = c.iterator();
    itr.next();
    try {
      itr.remove();
      return false;
    } catch (Exception e) {
      // expected
    }
    return true;
  }

  public void testToUnmodifiableList() {
    applyItems(List.of("a", "b"), toUnmodifiableList(), "a", "b", (a, b) -> {
      if (!a.equals(b)) {
        return false;
      }

      // check both, so it is obvious we got the right one
      if (!unmodifiableCollection(a, "a", "z")) {
        return false;
      }
      if (!unmodifiableCollection(b, "a", "z")) {
        return false;
      }

      return true;
    });

    // verify nulls fail
    try {
      Stream.of("a").map(ignore -> null).collect(toUnmodifiableList());
      fail("Expected NPE");
    } catch (NullPointerException ignore) {
      // expected
    }
  }

  public void testToUnmodifiableMap() {
    // verify simple cases copy all values and results are unmodifiable
    applyItems(Map.of("a", 0, "b", 1), toUnmodifiableMap(Function.identity(),
        k -> k.charAt(0) - 'a'), "a", "b", (a, b) -> {
      if (!a.equals(b)) {
        return false;
      }

      // check both, so it is obvious we got the right one
      if (!unmodifiableMap(a, "a", 0, "z", 100)) {
        return false;
      }
      if (!unmodifiableMap(b, "a", 0, "z", 100)) {
        return false;
      }

      return true;
    });

    // verify merge works with only one key (but this is just passing through to the toMap func anyway...)
    applyItems(Map.of("a", 2), toUnmodifiableMap(Function.identity(), ignore -> 1, Integer::sum),
        "a", "a");

    // verify nulls blow up for both keys and values
    try {
      Stream.of("a").collect(toUnmodifiableMap(obj -> null, Function.identity()));
      fail("Expected NPE");
    } catch (NullPointerException ignore) {
      // expected
    }
    try {
      Stream.of("a").collect(toUnmodifiableMap(Function.identity(), obj -> null));
      fail("Expected NPE");
    } catch (Exception ignore) {
      // expected
    }
  }

  private <K, V> boolean unmodifiableMap(Map<K, V> a, K existingKey, V existingValue, K newKey,
                                         V newValue) {
    if (!unmodifiableCollection(a.keySet(), existingKey, newKey)) {
      return false;
    }
    if (!unmodifiableCollection(a.values(), existingValue, newValue)) {
      return false;
    }

    try {
      a.put(newKey, newValue);
      return false;
    } catch (Exception ignore) {
      // expected
    }
    try {
      a.remove(existingKey);
      return false;
    } catch (Exception ignore) {
      // expected
    }

    return true;
  }

  public void testToUnmodifiableSet() {
    applyItems(Set.of("a", "b"), toUnmodifiableSet(), "a", "b", (a, b) -> {
      if (!a.equals(b)) {
        return false;
      }
      if (!unmodifiableCollection(a, "a", "z")) {
        return false;
      }
      if (!unmodifiableCollection(b, "a", "z")) {
        return false;
      }
      return true;
    });

    // verify nulls fail
    try {
      Stream.of("a").map(ignore -> null).collect(toUnmodifiableSet());
      fail("Expected NPE");
    } catch (NullPointerException ignore) {
      // expected
    }
  }
}
