/*
 * Copyright 2009 Google Inc.
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
package com.google.gwt.junit;

import com.google.gwt.junit.client.DevModeOnCompiledScriptTest;
import com.google.gwt.junit.client.GWTTestCase;
import com.google.gwt.junit.client.GWTTestCaseAsyncTest;
import com.google.gwt.junit.client.GWTTestCaseSetupTearDownTest;
import com.google.gwt.junit.client.GWTTestCaseTest;
import com.google.gwt.junit.client.PropertyDefiningGWTTest;
import com.google.gwt.junit.client.impl.JUnitHost.TestInfo;
import com.google.gwt.junit.tools.GWTTestSuite;

import junit.framework.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Set;

/**
 * Tests of the junit package.
 */
public class JUnitSuite {
  public static Test suite() {
    GWTTestSuite suite =
        new GwtTestSuiteWithExpectedFailures("Test suite for com.google.gwt.junit");

    // client
    // Suppressed due to flakiness on Linux
    // suite.addTestSuite(BenchmarkTest.class);

    suite.addTestSuite(GWTTestCaseTest.class);
    suite.addTestSuite(GWTTestCaseAsyncTest.class);
    suite.addTestSuite(GWTTestCaseSetupTearDownTest.class);
    sortTestsInModule("com.google.gwt.junit.OrderedJUnitTest");

    suite.addTestSuite(DevModeOnCompiledScriptTest.class);

    // Must run after a GWTTestCase so JUnitShell is initialized.
    suite.addTestSuite(BatchingStrategyTest.class);
    suite.addTestSuite(CompileStrategyTest.class);

    suite.addTestSuite(FakeMessagesMakerTest.class);
    suite.addTestSuite(GWTMockUtilitiesTest.class);
    suite.addTestSuite(JUnitMessageQueueTest.class);
    suite.addTestSuite(GWTTestCaseNoClientTest.class);

    // Intended only to be run manually. See class comments
    // suite.addTestSuite(ParallelRemoteTest.class);

    // remote
    // Run manually only, launches servers that die on port contention
    // suite.addTestSuite(BrowserManagerServerTest.class);

    suite.addTestSuite(PropertyDefiningStrategyTest.class);
    suite.addTestSuite(PropertyDefiningGWTTest.class);

    return suite;
  }

  private static void sortTestsInModule(String moduleName) {
    // TestModuleInfo in GWTTestCase#ALL_GWT_TESTS accidentally forces the test execution order
    // that is derived from first time TestCase is instantiated. We need the change the order there
    // to control the execution order.
    String syntheticModuleName = moduleName + ".JUnit";
    Set<TestInfo> testInfos = GWTTestCase.ALL_GWT_TESTS.get(syntheticModuleName).getTests();
    ArrayList<TestInfo> sortedTestInfos = new ArrayList<TestInfo>(testInfos);
    Collections.sort(sortedTestInfos, new Comparator<TestInfo>() {
      @Override
      public int compare(TestInfo a, TestInfo b) {
        return getKey(a).compareTo(getKey(b));
      }

      private String getKey(TestInfo testInfo) {
        return testInfo.getTestClass() + "." + testInfo.getTestMethod();
      }
    });
    testInfos.clear();
    testInfos.addAll(sortedTestInfos);
  }
}
