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
package com.google.gwt.dev.util.arg;

import junit.framework.TestCase;

public class SourceLevelTest extends TestCase {
  public void testVersionNumberComparisons() {
    assertTrue(SourceLevel.versionCompare("1.4.3.22", "1.04.3.22") == 0);
    assertTrue(SourceLevel.versionCompare("1.4.3.22.1", "1.4.3.22") > 0);
    assertTrue(SourceLevel.versionCompare("1.4.3.22.1", "1.4.3.32") < 0);
    assertTrue(SourceLevel.versionCompare("1.4.3.22", "1.4.3.22.1") < 0);
    assertTrue(SourceLevel.versionCompare("1.4.3.22.1", "1.4.3.22.2") < 0);

    assertTrue(SourceLevel.versionCompare("1.4.3.22.1_b4", "1.4.3.22_b2") > 0);
    assertTrue(SourceLevel.versionCompare("1.4.3.22_b11", "01.04.3.22_b1") > 0);

    try {
      SourceLevel.versionCompare("1.4.3.22.1.dodo", "1.4.3.22.1");
      fail("Should have thrown a IllegalArgumentException");
    } catch (IllegalArgumentException e) {
    }
    try {
      SourceLevel.versionCompare("1.4.3.22.1", "1.4.3.22.1.dodo");
      fail("Should have thrown a IllegalArgumentException");
    } catch (IllegalArgumentException e) {
    }
  }
}
