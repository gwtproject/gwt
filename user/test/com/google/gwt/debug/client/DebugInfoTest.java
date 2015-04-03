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
package com.google.gwt.debug.client;

import com.google.gwt.junit.client.GWTTestCase;

/**
 * Test Case for {@link DebugInfo} when <code>gwt.enableDebugId</code> is enabled.
 */
public class DebugInfoTest extends GWTTestCase {

  @Override
  public String getModuleName() {
    return "com.google.gwt.debug.Debug";
  }

  /**
   * Test that the {@link DebugInfo#isDebugIdEnabled()} method works correctly.
   */
  public void testIsDebugIdEnabled() {
    assertTrue(DebugInfo.isDebugIdEnabled());
  }
}