/*
 * Copyright 2013 Google Inc.
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
package com.google.gwt.junit.client;


import static java.util.Arrays.asList;

import java.util.ArrayList;
import java.util.List;

/**
 * This class tests inherited tests are properly executed.
 *
 * Note: This test requires some test methods to be executed in a specific order.
 */
public class GWTTestCaseInheritanceTest extends InheritedTest {

  private static List<String> executions = new ArrayList<String>();

  @Override
  protected void gwtTearDown() throws Exception {
    executions.add(getName());
  }

  @Override
  public void testOverridden() {
    // Success!
  }

  @ExpectedFailure
  public void testFail() {
    fail("failed in purpose");
  }

  /**
   * This is the last test to be executed (under_score forces that). Will assert all test runs.
   */
  public void test_assertExecution() {
    assertEquals(asList("testFail", "testOverridden", "testSuccess"), executions);
  }
}

// A test class to inherit
class InheritedTest extends GWTTestCaseTestBase {

  public void testSuccess() {
    // Success!
  }

  public void testOverridden() {
    fail("Should not have failed because of override");
  }
}