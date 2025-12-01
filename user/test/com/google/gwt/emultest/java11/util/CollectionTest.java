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
package com.google.gwt.emultest.java11.util;

import com.google.gwt.emultest.java.util.EmulTestBase;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

/**
 * Tests for java.util.Collection Java 11 API emulation.
 * <p>
 * We can't easily test Collection methods themselves, so trying a few basic implementations.
 */
public class CollectionTest extends EmulTestBase {

  public void testListArrayOfMethodRef() {
    String[] arr = List.of("a", "b").toArray(String[]::new);
    assertEquals(2, arr.length);
    assertTrue(arr instanceof String[]);

    assertEquals("a", arr[0]);
    assertEquals("b", arr[1]);
  }

  public void testSetArrayOfMethodRef() {
    Set<String> ab = Set.of("a", "b");
    String[] arr = ab.toArray(String[]::new);
    assertEquals(2, arr.length);
    assertTrue(arr instanceof String[]);
    assertTrue(ab.containsAll(Arrays.asList(arr)));
  }
}
