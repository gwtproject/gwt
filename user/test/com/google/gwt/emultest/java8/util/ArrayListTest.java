/*
 * Copyright 2016 Google Inc.
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

import java.util.ArrayList;
import java.util.List;

/**
 * Java 8 methods to test in java.util.ArrayList.
 */
public class ArrayListTest extends AbstractJava8ListTest {
  @Override
  protected List<String> createEmptyList() {
    return new ArrayList<>();
  }

  public void testSort() {
    ArrayList<String> list = new ArrayList<>();
    list.sort(null);

    Collections.addAll(list, "b", "a", "c");
    list.sort(null);
    assertEquals(asList("a", "b", "c"), list);

    list = new ArrayList<>();
    Collections.addAll(list, "b", "a", "c");
    list.sort(Collections.reverseOrder());
    assertEquals(asList("c", "b", "a"), list);
  }
}
