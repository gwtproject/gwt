/*
 * Copyright 2015 Google Inc.
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

package com.google.gwt.emultest.java.nio.charset;

import com.google.gwt.emultest.java.util.EmulTestBase;

import org.junit.Test;

import java.nio.charset.Charset;
import java.nio.charset.UnsupportedCharsetException;

/**
 * Unit test for the {@link java.nio.charset.Charset} emulated class.
 */
public class CharsetTest extends EmulTestBase {

  public void testIso88591() {
    assertEquals("ISO-8859-1", Charset.forName("ISO-8859-1").name());
  }

  public void testUtf8() {
    assertEquals("UTF-8", Charset.forName("UTF-8").name());
  }

  @Test(expected = IllegalArgumentException.class)
  public void testForName_null() {
    Charset.forName(null);
  }

  @Test(expected = UnsupportedCharsetException.class)
  public void testForName_unsupported() {
    Charset.forName("qwer");
  }
}
