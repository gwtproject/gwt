/*
 * Copyright 2024 GWT Project Authors
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
 * Dummy test case. Java17Test is super sourced so that GWT can be compiled by Java 8.
 *
 * NOTE: Make sure this class has the same test methods of its supersourced variant.
 */
@DoNotRunWith(Platform.Devel)
public class Java17Test extends GWTTestCase {

  @Override
  public String getModuleName() {
    return "com.google.gwt.dev.jjs.Java17Test";
  }

  @Override
  public void runTest() throws Throwable {
    // Only run these tests if -sourceLevel 17 (or greater) is enabled.
    if (isGwtSourceLevel17()) {
      super.runTest();
    }
  }

  public void testTextBlocks() {
    assertFalse(isGwtSourceLevel17());
  }

  public void testSealedClassesPermitted() {
    assertFalse(isGwtSourceLevel17());
  }

  public void testRecordClasses() {
    assertFalse(isGwtSourceLevel17());
  }

  public void testJsTypeRecords() {
    assertFalse(isGwtSourceLevel17());
  }

  public void testInstanceOfPatternMatching() {
    assertFalse(isGwtSourceLevel17());
  }

  public void testInstanceOfPatternMatchingWithSideEffectExpression() {
    assertFalse(isGwtSourceLevel17());
  }

  public void testInstanceOfPatternMatchingWithAnd() {
    assertFalse(isGwtSourceLevel17());
  }

  public void testInstanceOfPatternMatchingWithCondition() {
    assertFalse(isGwtSourceLevel17());
  }

  public void testInstanceOfPatternMatchingWithAsNotCondition() {
    assertFalse(isGwtSourceLevel17());
  }

  public void testMultipleInstanceOfPatternMatchingWithSameVariableName() {
    assertFalse(isGwtSourceLevel17());
  }

  public void testMultipleInstanceOfPatternMatchingWithSameVariableNameWithDifferentTypes() {
    assertFalse(isGwtSourceLevel17());
  }

  public void testInstanceOfPatternMatchingIsFalse() {
    assertFalse(isGwtSourceLevel17());
  }

  public void testInstanceOfPatternMatchingInLambda() {
    assertFalse(isGwtSourceLevel17());
  }

  public void testInstanceOfPatternMatchingAsReturn() {
    assertFalse(isGwtSourceLevel17());
  }

  public void testNegativeInstanceOfPatternOutsideIfScope() {
    assertFalse(isGwtSourceLevel17());
  }

  public void testSwitchExpressionOnConstant() {
    assertFalse(isGwtSourceLevel17());
  }

  public void testSwitchWithMultipleCaseValues() {
    assertFalse(isGwtSourceLevel17());
  }

  public void testSwitchInSubExpr() {
    assertFalse(isGwtSourceLevel17());
  }

  public void testSwitchExprInlining() {
    assertFalse(isGwtSourceLevel17());
  }

  public void testInlinedStringConstantsInCase() {
    assertFalse(isGwtSourceLevel17());
  }
  public void testCaseArrowLabelsVoidExpression() {
    assertFalse(isGwtSourceLevel17());
  }

  private boolean isGwtSourceLevel17() {
    return JUnitShell.getCompilerOptions().getSourceLevel().compareTo(SourceLevel.JAVA17) >= 0;
  }
}
