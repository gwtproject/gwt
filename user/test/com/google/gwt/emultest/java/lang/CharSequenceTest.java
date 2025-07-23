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
package com.google.gwt.emultest.java.lang;

import com.google.gwt.emultest.java.util.EmulTestBase;

import java.util.List;
import java.util.stream.Collectors;

public class CharSequenceTest extends EmulTestBase {

  public void testCompare() {
    assertEquals(-1, CharSequence.compare(hideFromCompiler("a"), "b"));
    assertEquals(1, CharSequence.compare(hideFromCompiler("b"), "a"));
    assertEquals(0, CharSequence.compare(hideFromCompiler("a"), "a"));
    assertEquals(-1, CharSequence.compare(hideFromCompiler("a"),
        new StringBuilder("b")));
  }

  public void testCodePoints() {
    assertEquals(List.of(), collectCodePoints(""));
    assertEquals(List.of("a", "b", "c"), collectCodePoints("abc"));
    assertEquals(List.of("\uD83D\uDE0D"), collectCodePoints("\uD83D\uDE0D"));
    assertEquals(List.of("x", "\uD83D\uDE0D", "y"), collectCodePoints("x\uD83D\uDE0Dy"));
  }

  private List<String> collectCodePoints(String str) {
    return str.codePoints().mapToObj(cp -> new String(Character.toChars(cp)))
        .collect(Collectors.toList());
  }

}
