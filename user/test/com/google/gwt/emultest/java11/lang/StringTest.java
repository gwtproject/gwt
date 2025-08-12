/*
 * Copyright 2024 GWT Project Authors
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
package com.google.gwt.emultest.java11.lang;

import com.google.gwt.emultest.java.util.EmulTestBase;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * Tests for java.lang.String Java 11 API emulation.
 */
public class StringTest extends EmulTestBase {
  public void testIsBlank() {
    assertTrue(hideFromCompiler("").isBlank());
    assertTrue(hideFromCompiler("  ").isBlank());
    assertFalse(hideFromCompiler("x ").isBlank());
    assertTrue(hideFromCompiler("\u001c").isBlank());
    assertFalse(hideFromCompiler("\u00a0").isBlank());
  }

  public void testStrip() {
    stripRightAsssertEquals("", "");
    stripRightAsssertEquals("", "  ");
    stripRightAsssertEquals("x", " x ");
    stripRightAsssertEquals("x", "\u001cx\u001c");
    stripRightAsssertEquals("\u00a0x\u00a0", "\u00a0x\u00a0 ");
    stripRightAsssertEquals("\uffefx\u180e", "\uffefx\u180e ");
  }

  public void testStripLeading() {
    stripRightLeadingAsssertEquals("", "");
    stripRightLeadingAsssertEquals("", "  ");
    stripRightLeadingAsssertEquals("x ", " x ");
    stripRightLeadingAsssertEquals("x\u001c", "\u001cx\u001c");
    stripRightLeadingAsssertEquals("\u00a0x\u00a0", "\u00a0x\u00a0");
  }

  public void testStripTrailing() {
    stripRightTrailingAsssertEquals("", "");
    stripRightTrailingAsssertEquals("", "  ");
    stripRightTrailingAsssertEquals(" x", " x ");
    stripRightTrailingAsssertEquals("\u001cx", "\u001cx\u001c");
    stripRightTrailingAsssertEquals("\u00a0x\u00a0", "\u00a0x\u00a0 ");
  }

  private void stripRightAsssertEquals(String expected, String arg) {
    assertEquals(expected, hideFromCompiler(arg).strip());
  }

  private void stripRightLeadingAsssertEquals(String expected, String arg) {
    assertEquals(expected, hideFromCompiler(arg).stripLeading());
  }

  private void stripRightTrailingAsssertEquals(String expected, String arg) {
    assertEquals(expected, hideFromCompiler(arg).stripTrailing());
  }

  public void testRepeat() {
    assertEquals("", hideFromCompiler("foo").repeat(0));
    assertEquals("foo", hideFromCompiler("foo").repeat(1));
    assertEquals("foofoofoo", hideFromCompiler("foo").repeat(3));
    try {
      String noFoo = hideFromCompiler("foo").repeat(-1);
      throw new Error("Should fail with negative arg");
    } catch (IllegalArgumentException ex) {
      assertEquals("count is negative: -1", ex.getMessage());
    }
  }

  public void testLines() {
    assertEquals(Arrays.asList("a", "b", "c", "d"),
        "a\rb\nc\r\nd".lines().collect(Collectors.toList()));
    assertEquals(Arrays.asList("a"),
        "a\n".lines().collect(Collectors.toList()));
    assertEquals(Arrays.asList("a"),
        "a\r\n".lines().collect(Collectors.toList()));
    assertEquals(Arrays.asList(),
        "".lines().collect(Collectors.toList()));
    assertEquals(Arrays.asList(""),
        "\n".lines().collect(Collectors.toList()));
    assertEquals(Arrays.asList(""),
        "\r\n".lines().collect(Collectors.toList()));
    assertEquals(Arrays.asList("", ""),
        "\n\r\n".lines().collect(Collectors.toList()));
    assertEquals(Arrays.asList("", "", "c"),
        "\n\r\nc".lines().collect(Collectors.toList()));
  }
}
