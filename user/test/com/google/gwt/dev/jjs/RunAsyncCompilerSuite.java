/*
 * Copyright 2014 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package com.google.gwt.dev.jjs;

import com.google.gwt.dev.jjs.test.RunAsyncContentTest;
import com.google.gwt.dev.jjs.test.RunAsyncFailureTest;
import com.google.gwt.dev.jjs.test.RunAsyncMetricsIntegrationTest;
import com.google.gwt.dev.jjs.test.RunAsyncTest;
import com.google.gwt.junit.tools.GWTTestSuite;

import junit.framework.Test;

/**
 * The runasync compiler suite.
 */
public class RunAsyncCompilerSuite {

  public static Test suite() {
    GWTTestSuite suite = new GWTTestSuite("RunAsync test for com.google.gwt.dev.jjs");

    // $JUnit-BEGIN$
    suite.addTestSuite(RunAsyncContentTest.class);
    suite.addTestSuite(RunAsyncFailureTest.class);
    suite.addTestSuite(RunAsyncMetricsIntegrationTest.class);
    suite.addTestSuite(RunAsyncTest.class);
    // $JUnit-END$

    return suite;
  }
}
