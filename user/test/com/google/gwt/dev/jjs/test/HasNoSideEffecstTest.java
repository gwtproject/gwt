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
package com.google.gwt.dev.jjs.test;

import com.google.gwt.core.client.impl.DoNotInline;
import com.google.gwt.core.client.impl.HasNoSideEffects;
import com.google.gwt.junit.DoNotRunWith;
import com.google.gwt.junit.Platform;
import com.google.gwt.junit.client.GWTTestCase;

/**
 * Tests for {@link HasNoSideEffects}.
 */
@DoNotRunWith(Platform.Devel)
public class HasNoSideEffecstTest extends GWTTestCase {

  @Override
  public String getModuleName() {
    return "com.google.gwt.dev.jjs.CompilerSuite";
  }

  @DoNotInline
  private static boolean execute() {
    throw new RuntimeException("I lied, I have a side efect but compiler should have trusted me!");
  }

  private static boolean someOtherFunction() {
    return true;
  }

  @HasNoSideEffects
  private static boolean sideEffectFree() {
    return execute();
  }

  public void testMethodRemoval() {
    boolean rv = sideEffectFree();
    // How can we make sure sideEffectFree() gets inlined before the static evaluation of
    // the next statement?
    // And how can we detect and fail if the test no longer tests the propagation of side effect
    // info during inlining?
    assertTrue(someOtherFunction() || rv);
  }
}
