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
package com.google.gwt.emultest.java11.util.function;

import com.google.gwt.emultest.java.util.EmulTestBase;
import java.util.function.Predicate;

/**
 * A class that tests the functionality of the {@link Predicate} interface.
 */
public class PredicateTest extends EmulTestBase {
  public void testIsEqual() {
    Predicate<String> stringPredicate = Predicate.isEqual("test");
    assertTrue(stringPredicate.test("test"));
    assertFalse(stringPredicate.test("other string"));
  }

  public void testTest() {
    Predicate<Integer> evenNumberPredicate = x -> x % 2 == 0;
    assertTrue(evenNumberPredicate.test(2));
    assertFalse(evenNumberPredicate.test(3));
  }

  public void testNegate() {
    Predicate<Integer> evenNumberPredicate = x -> x % 2 == 0;
    Predicate<Integer> notEvenPredicate = evenNumberPredicate.negate();
    assertTrue(notEvenPredicate.test(3));
    assertFalse(notEvenPredicate.test(2));
  }

  public void testAnd() {
    Predicate<Integer> evenNumberPredicate = x -> x % 2 == 0;
    Predicate<Integer> greaterThanFourPredicate = x -> x > 4;
    Predicate<Integer> combinedPredicate = evenNumberPredicate.and(greaterThanFourPredicate);
    assertTrue(combinedPredicate.test(6));
    assertFalse(combinedPredicate.test(4));
  }

  public void testOr() {
    Predicate<Integer> evenNumberPredicate = x -> x % 2 == 0;
    Predicate<Integer> lessThanOrEqualToFourPredicate = x -> x <= 4;
    Predicate<Integer> combinedPredicate = evenNumberPredicate.or(lessThanOrEqualToFourPredicate);
    assertTrue(combinedPredicate.test(3));
    assertFalse(combinedPredicate.test(5));
  }

  public void testNot() {
    Predicate<Integer> evenNumberPredicate = x -> x % 2 == 0;
    Predicate<Integer> notEvenPredicate = Predicate.not(evenNumberPredicate);
    assertTrue(notEvenPredicate.test(3));
    assertFalse(notEvenPredicate.test(2));
  }
}
