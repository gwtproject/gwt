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

/** Tests for Collections that require Java8 syntax */
public class CollectionsTest extends EmulTestBase {
  public void testImmutabilityForEmptyLists() {
    List<String> list = Collections.unmodifiableList(new ArrayList<>());
    doTestImmutabilityForLists(list);
  }

  public void testImmutabilityForLists() {
    List<String> list = Collections.unmodifiableList(Arrays.asList("1", "2", "3"));
    doTestImmutabilityForLists(list);
  }

  private void doTestImmutabilityForLists(List<String> list) {
    tryModify(list, () -> list.add("4"));
    tryModify(list, () -> list.add(0, "5"));
    tryModify(list, () -> list.addAll(Arrays.asList("6")));
    tryModify(list, () -> list.addAll(0, Arrays.asList("7")));
    tryModify(list, () -> list.clear());
    tryModify(
        list,
        () -> {
          ListIterator<String> stringListIterator = list.listIterator();
          stringListIterator.next();
          stringListIterator.add("4");
        });
    tryModify(
        list,
        () -> {
          ListIterator<String> stringListIterator = list.listIterator();
          stringListIterator.next();
          stringListIterator.remove();
        });
    tryModify(
        list,
        () -> {
          ListIterator<String> stringListIterator = list.listIterator();
          stringListIterator.next();
          stringListIterator.set("4");
        });

    tryModify(
        list,
        () -> {
          ListIterator<String> stringListIterator = list.listIterator();
          stringListIterator.next();
          stringListIterator.remove();
        });

    tryModify(
        list,
        () -> {
          ListIterator<String> stringListIterator = list.listIterator(1);
          stringListIterator.next();
          stringListIterator.add("4");
        });
    tryModify(
        list,
        () -> {
          ListIterator<String> stringListIterator = list.listIterator(1);
          stringListIterator.next();
          stringListIterator.remove();
        });
    tryModify(
        list,
        () -> {
          ListIterator<String> stringListIterator = list.listIterator(1);
          stringListIterator.next();
          stringListIterator.set("4");
        });

    tryModify(
        list,
        () -> {
          ListIterator<String> stringListIterator = list.listIterator(1);
          stringListIterator.next();
          stringListIterator.remove();
        });
    tryModify(list, () -> list.replaceAll((s) -> s + "asdf"));
    tryModify(list, () -> list.remove("1"));
    tryModify(list, () -> list.remove(0));
    tryModify(list, () -> list.removeAll(Arrays.asList("1")));
    tryModify(list, () -> list.removeIf((s) -> true));
    tryModify(list, () -> list.retainAll(Arrays.asList("4")));
    tryModify(list, () -> list.set(0, "24"));
    tryModify(list, () -> list.sort((s1, s2) -> Integer.valueOf(s2) - Integer.valueOf(s1)));
    tryModify(list, () -> list.subList(0, 1).remove(0));
  }

  private static void tryModify(List<?> list, Runnable runnable) {
    List<?> copiedList = new ArrayList<>(list);
    try {
      runnable.run();
      fail();
    } catch (RuntimeException e) {
    }
    assertEquals(copiedList.size(), list.size());
    for (int i = 0; i < list.size(); i++) {
      assertEquals(copiedList.get(i), list.get(i));
    }
  }
}
