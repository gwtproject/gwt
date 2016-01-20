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

import jsinterop.annotations.JsOverlay;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;

/**
 * Tests for JsOverlay methods are inlined.
 */
@DoNotRunWith(Platform.Devel)
public class JsOverlayMethodOptimizationTest extends OptimizationTestBase {

  @JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "Object")
  private static class NativeType {
    private native int getOne();

    @JsOverlay
    final boolean contains(String s1, String s2) {
      return s1.contains(s2);
    }

    @JsOverlay
    final int callGetOne() {
      // If NativeType.contains gets inlined by Java passes, then String.contains is statically
      // evaluated.
      return contains("1234","1") ? this.getOne() : 2;
    }
  }

  public static int callGetOne(NativeType obj) {
    return obj.callGetOne();
  }

  private static native String getGeneratedFunctionDefinition() /*-{
    return function() {
      @JsOverlayMethodOptimizationTest::callGetOne(*)({});
    }.toString();
  }-*/;

  /**
   * Tests whether JsOverlay is inlined by the Java optimization passes rather than being just
   * devirtualized.
   */
  public void testJsOverlayIsInlined() throws Exception {
    String functionDef = getGeneratedFunctionDefinition();
    assertFunctionMatches(functionDef, "({}.getOne())");
  }
}
