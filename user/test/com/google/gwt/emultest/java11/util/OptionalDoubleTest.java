/*
 * Copyright 2023 Google Inc.
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

import java.util.OptionalDouble;

/**
 * Tests for java.util.OptionalDouble Java 11 API emulation.
 */
public class OptionalDoubleTest extends EmulTestBase {
  public void testIsEmpty() {
    assertTrue(OptionalDouble.empty().isEmpty());
    assertFalse(OptionalDouble.of(78.9).isEmpty());
  }
}
