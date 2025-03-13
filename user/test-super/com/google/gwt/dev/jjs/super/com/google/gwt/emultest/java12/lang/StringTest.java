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
package com.google.gwt.emultest.java11.lang;

import com.google.gwt.emultest.java.util.EmulTestBase;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * Tests for java.lang.String Java 12 API emulation.
 */
public class StringTest extends EmulTestBase {

  public void testTransform() {
    assertEquals(3, hideFromCompiler("foo").transform(String::length));
  }

  public void testIndent() {
    assertEquals("  x", hideFromCompiler("x").indent(2));
    assertEquals("x", hideFromCompiler("  x").indent(-2));
    assertEquals("  x\n  y", hideFromCompiler("x\ny").indent(2));
    assertEquals("  x\r\n  y", hideFromCompiler("x\r\ny").indent(2));
    assertEquals("  x\r  y", hideFromCompiler("x\ry").indent(2));
    assertEquals("x\ny", hideFromCompiler("  x\n  y").indent(-2));
    assertEquals("x\r\ny", hideFromCompiler("  x\r\n  y").indent(-2));
    assertEquals("x\ry", hideFromCompiler("  x\r  y").indent(-2));
  }

  private <T> T hideFromCompiler(T value) {
    if (Math.random() < -1) {
      // Can never happen, but fools the compiler enough not to optimize this call.
      fail();
    }
    return value;
  }
}