/*
 * Copyright 2018 Google Inc.
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

import com.google.gwt.dev.util.arg.SourceLevel;
import com.google.gwt.junit.DoNotRunWith;
import com.google.gwt.junit.JUnitShell;
import com.google.gwt.junit.Platform;
import com.google.gwt.junit.client.GWTTestCase;

/**
 * Dummy test case. Java10Test is super sourced so that GWT can be compiled by Java 8.
 *
 * NOTE: Make sure this class has the same test methods of its supersourced variant.
 */
@DoNotRunWith(Platform.Devel)
public class Java10Test extends GWTTestCase {

  @Override
  public String getModuleName() {
    return "com.google.gwt.dev.jjs.Java10Test";
  }

  @Override
  public void runTest() throws Throwable {
    // Only run these tests if -sourceLevel 10 (or greater) is enabled.
    if (isGwtSourceLevel10()) {
      super.runTest();
    }
  }

  public void testLocalVarType_DenotableTypes() {
    assertFalse(isGwtSourceLevel10());
  }

  public void testLocalVarType_Anonymous() {
    assertFalse(isGwtSourceLevel10());
  }

  public void testLocalVarType_Ternary() {
    assertFalse(isGwtSourceLevel10());
  }

  public void testLocalVarType_LambdaCapture() {
    assertFalse(isGwtSourceLevel10());
  }

  public void testLocalVarType_VarArg() {
    assertFalse(isGwtSourceLevel10());
  }

  public void testLocalVarType_LocalClass() {
    assertFalse(isGwtSourceLevel10());
  }

  public void testLocalVarType_ForLoop() {
    assertFalse(isGwtSourceLevel10());
  }

  public void testLocalVarType_EnhancedForLoopArray() {
    assertFalse(isGwtSourceLevel10());
  }

  public void testLocalVarType_EnhancedNestedForLoopArray() {
    assertFalse(isGwtSourceLevel10());
  }

  public void testLocalVarType_EnhancedForLoopIterable() {
    assertFalse(isGwtSourceLevel10());
  }

  private boolean isGwtSourceLevel10() {
    return JUnitShell.getCompilerOptions().getSourceLevel().compareTo(SourceLevel.JAVA10) >= 0;
  }
}
