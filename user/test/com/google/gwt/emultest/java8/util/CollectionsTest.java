/*
 * Copyright 2017 Google Inc.
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;
import java.util.function.Consumer;

/** Tests for Collections that require Java8 syntax. */
public class CollectionsTest extends EmulTestBase {

  public void testUnmodifiableList() {
    List<String> list = Collections.unmodifiableList(Arrays.asList("1", "2", "3"));
    doTestModificationsToList(list);
    doTestModificationsToListViaIterator(list);
  }

  public void testUnmodifiableList_emptyList() {
    List<String> list = Collections.unmodifiableList(new ArrayList<>());
    doTestModificationsToList(list);
  }

  private void doTestModificationsToList(List<String> list) {
    assertUnmodifiableContract(list, l -> l.add("4"));
    assertUnmodifiableContract(list, l -> l.add(0, "5"));
    assertUnmodifiableContract(list, l -> l.addAll(Arrays.asList("6")));
    assertUnmodifiableContract(list, l -> l.addAll(0, Arrays.asList("7")));
    assertUnmodifiableContract(list, l -> l.addAll(Arrays.asList()));
    assertUnmodifiableContract(list, l -> l.clear());
    assertUnmodifiableContract(list, l -> l.replaceAll((s) -> s + "asdf"));
    assertUnmodifiableContract(list, l -> l.remove("1"));
    assertUnmodifiableContract(list, l -> l.remove(0));
    assertUnmodifiableContract(list, l -> l.removeAll(Arrays.asList("1")));
    assertUnmodifiableContract(list, l -> l.removeIf((s) -> true));
    assertUnmodifiableContract(list, l -> l.retainAll(Arrays.asList("4")));
    assertUnmodifiableContract(list, l -> l.set(0, "24"));
    assertUnmodifiableContract(
        list, l -> l.sort((s1, s2) -> Integer.valueOf(s2) - Integer.valueOf(s1)));
    assertUnmodifiableContract(list, l -> l.subList(0, 0).remove(0));
  }
 
  private void doTestModificationsToListViaIterator(List<String> list) {
    assertUnmodifiableContractThroughIterator(list, i -> i.add("4"));
    assertUnmodifiableContractThroughIterator(list, i -> i.remove());
    assertUnmodifiableContractThroughIterator(list, i -> i.set("4"));
  }

  private static void assertUnmodifiableContractThroughIterator(
      List<String> list, Consumer<ListIterator<String>> consumer) {
    assertUnmodifiableContract(
        list,
        l -> {
          ListIterator<String> listIterator = l.listIterator();
          listIterator.next();
          consumer.accept(listIterator);
        });

    assertUnmodifiableContract(
        list,
        l -> {
          ListIterator<String> listIterator = l.listIterator(1);
          listIterator.next();
          consumer.accept(listIterator);
        });
  }

  private static void assertUnmodifiableContract(
      List<String> list, Consumer<List<String>> consumer) {
    List<?> originalContent = new ArrayList<>(list);
    try {
      consumer.accept(list);
      fail();
    } catch (UnsupportedOperationException e) {
    }
    assertEquals(originalContent, list);
  }
}
