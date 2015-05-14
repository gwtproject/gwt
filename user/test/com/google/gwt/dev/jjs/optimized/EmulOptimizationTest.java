/*
 * Copyright 2014 Google Inc.
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
package com.google.gwt.dev.jjs.optimized;

import com.google.gwt.junit.DoNotRunWith;
import com.google.gwt.junit.Platform;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Tests compiled out preconditions on some selected JRE emulation classes.
 */
@DoNotRunWith(Platform.Devel)
public class EmulOptimizationTest extends OptimizationTest {

  private static ArrayList<String> list = new ArrayList<String>();
  public static void accessArrayList() {
    list.get(0);
  }

  private static native String getGeneratedFunctionDefinitionForArrayList() /*-{
    return function() {
      @EmulOptimizationTest::accessArrayList()();
    }.toString();
  }-*/;

  public void testArrayList() throws Exception {
    String functionDef = getGeneratedFunctionDefinitionForArrayList();
    assertFunctionMatches(functionDef, "<obf>.<obf>[0]");
  }

  private static HashMap<String, Object> map = new HashMap<String, Object>();

  public static void accessMap() {
    map.get("ABC");
  }

  private static native String getGeneratedFunctionDefinitionForHashMap() /*-{
    return function() {
      @EmulOptimizationTest::accessMap()();
    }.toString();
  }-*/;

  // Test is disabled because HashMap method specialization and inlining is broken.
  public void _disabled_testHashMap() throws Exception {
    String functionDef = getGeneratedFunctionDefinitionForHashMap();
    assertFunctionMatches(functionDef, "<obf>.<obf>.<obf>['ABC']");
  }
}
