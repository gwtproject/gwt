/*
 * Copyright 2008 Google Inc.
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
package com.google.gwt.i18n.shared.impl;

import junit.framework.TestCase;

/**
 * Test {@link NumberFormatImpl#toScaledString(StringBuilder, double)}.
 */
public class NumberFormatImplTest extends TestCase {

  public void testToScaledString() {
    StringBuilder buf = new StringBuilder();
    int scale = NumberFormatImpl.toScaledString(buf, .1);
    String str = buf.toString();
    assertStartsWith("100", str.substring(str.length() + scale));
    assertAllZeros(str, str.length() + scale);
    buf = new StringBuilder();
    scale = NumberFormatImpl.toScaledString(buf, 12345e38);
    str = buf.toString();
    assertStartsWith("12345", str);
    assertEquals(43, scale + str.length());
  }

  private void assertAllZeros(String str, int prefixLen) {
    if (prefixLen > str.length()) {
      prefixLen = str.length();
    }
    for (int i = 0; i < prefixLen; ++i) {
      assertEquals('0', str.charAt(i));
    }
  }

  private void assertStartsWith(String prefix, String str) {
    assertTrue(str + " does not start with " + prefix, str.startsWith(prefix));
  }
}
