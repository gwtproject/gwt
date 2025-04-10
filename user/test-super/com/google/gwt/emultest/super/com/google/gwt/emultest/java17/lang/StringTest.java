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
package com.google.gwt.emultest.java17.lang;

import com.google.gwt.emultest.java.util.EmulTestBase;

/**
 * Tests for java.lang.String Java 12 API emulation.
 */
public class StringTest extends EmulTestBase {

  public void testTransform() {
    int stringLength = hideFromCompiler("foo").transform(String::length);
    assertEquals(3, stringLength);
  }

  public void testIndent() {
    assertEquals("  x\n", hideFromCompiler("x").indent(2));
    assertEquals("x\n", hideFromCompiler("  x").indent(-2));
    assertEquals("  x\n  y\n", hideFromCompiler("x\ny").indent(2));
    assertEquals("  x\n  y\n", hideFromCompiler("x\r\ny").indent(2));
    assertEquals("  x\n  y\n", hideFromCompiler("x\ry").indent(2));
    assertEquals("x\ny\n", hideFromCompiler("  x\n  y").indent(-2));
    assertEquals("x\ny\n", hideFromCompiler("  x\r\n  y").indent(-2));
    assertEquals("x\ny\n", hideFromCompiler("  x\r  y").indent(-2));
  }

  public void testStripIndent() {
    assertEquals("", hideFromCompiler("").stripIndent());
    assertEquals("x", hideFromCompiler("x").stripIndent());
    assertEquals("x", hideFromCompiler("  x").stripIndent());
    assertEquals("x\n", hideFromCompiler("x\r\n").stripIndent());
    assertEquals("x\ny", hideFromCompiler("  x\n  y").stripIndent());
    assertEquals("x\ny", hideFromCompiler("  x\r\n  y").stripIndent());
    assertEquals(" x\ny", hideFromCompiler("  x\n y").stripIndent());
    assertEquals("x\n y", hideFromCompiler(" x\n  y").stripIndent());
    assertEquals("x\n\ny", hideFromCompiler("  x\n      \n  y").stripIndent());
    assertEquals(" x\ny", hideFromCompiler("\t x\r\n y").stripIndent());
  }

  public void testTranslateEscapes() {
    assertEquals("\b \r\n\t\f",
        hideFromCompiler("\\b\\s\\r\\n\\t\\f").translateEscapes());
    assertEquals("\u0001\u0011\u00d1",
        hideFromCompiler("\\1\\21\\321").translateEscapes());
    assertEquals("\u00ff\u001f8\u00200",
        hideFromCompiler("\\377\\378\\400").translateEscapes());
    assertEquals("ab",
        hideFromCompiler("a\\\nb").translateEscapes());
  }
}