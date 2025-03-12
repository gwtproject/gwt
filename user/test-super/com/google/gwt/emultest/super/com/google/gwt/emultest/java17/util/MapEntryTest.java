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
package com.google.gwt.emultest.java17.util;

import com.google.gwt.emultest.java.util.EmulTestBase;

import java.util.AbstractMap;
import java.util.Map;

public class MapEntryTest extends EmulTestBase {

  public void testCopyOf() {
    AbstractMap.SimpleEntry<String, Integer> mutableEntry = new AbstractMap.SimpleEntry<>("a", 4);
    Map.Entry<String, Integer> copy = Map.Entry.copyOf(mutableEntry);
    assertEquals(mutableEntry.getKey(), copy.getKey());
    assertEquals(mutableEntry.getValue(), copy.getValue());
    assertNotSame(mutableEntry, copy);
    Map.Entry<String, Integer> otherCopy = Map.Entry.copyOf(copy);
    assertSame(copy, otherCopy);
  }
}
