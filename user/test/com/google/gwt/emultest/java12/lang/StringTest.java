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
package com.google.gwt.emultest.java12.lang;

import com.google.gwt.dev.util.arg.SourceLevel;
import com.google.gwt.emultest.java.util.EmulTestBase;
import com.google.gwt.junit.JUnitShell;

/**
 * Tests for java.lang.String Java 12 API emulation.
 */
public class StringTest extends EmulTestBase {

  @Override
  public void runTest() throws Throwable {
    // Only run these tests if -sourceLevel 17 (or greater) is enabled.
    if (isGwtSourceLevel17()) {
      super.runTest();
    }
  }

  public void testTransform() {
    assertFalse(isGwtSourceLevel17());
  }

  public void testIndent() {
    assertFalse(isGwtSourceLevel17());
  }

  private boolean isGwtSourceLevel17() {
    return JUnitShell.getCompilerOptions().getSourceLevel().compareTo(SourceLevel.JAVA17) >= 0;
  }
}