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
package com.google.gwt.junit.client.impl;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.junit.client.GWTTestCase;
import com.google.gwt.junit.client.impl.JUnitHost.TestInfo;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.ServiceDefTarget;

import java.util.HashMap;

/**
 * The entry point class for GWTTestCases.
 *
 * This is the main test running logic. Each time a test completes, the results
 * are reported back through {@link #junitHost}, and the next method to run is
 * returned. This process repeats until the next method to run is null.
 */
public class GWTRunner implements EntryPoint {

  /**
   * The RPC callback object for {@link GWTRunner#junitHost}. When
   * {@link #onSuccess} is called, it's time to run the next test case.
   */
  private final class TestBlockListener implements AsyncCallback<TestInfo[]> {

    /**
     * The number of times we've failed to communicate with the server on the
     * current test batch.
     */
    private int curRetryCount = 0;

    /**
     * A call to junitHost failed.
     */
    @Override
    public void onFailure(Throwable caught) {
      if (curRetryCount++ < MAX_RETRY_COUNT) {
        reportWarning("Retrying syncing back to junit backend. (Exception: " + caught + ")");
        // Try the call again
        new Timer() {
          @Override
          public void run() {
            syncToServer();
          }
        }.schedule(1000);
      } else {
        reportFatalError("Cannot sync back to junit backend: " + caught);
      }
    }

    /**
     * A call to junitHost succeeded; run the next test case.
     */
    @Override
    public void onSuccess(TestInfo[] nextTestBlock) {
      curRetryCount = 0;
      currentBlock = nextTestBlock;
      currentTestIndex = 0;
      currentResults.clear();
      if (currentBlock != null && currentBlock.length > 0) {
        doRunTest();
      }
    }
  }

  /**
   * The singleton instance.
   */
  static GWTRunner sInstance;

  /**
   * The maximum number of times to retry communication with the server per test batch.
   */
  private static final int MAX_RETRY_COUNT = 3;

  public static GWTRunner get() {
    return sInstance;
  }

  /**
   * The current block of tests to execute.
   */
  private TestInfo[] currentBlock;

  /**
   * Active test within current block of tests.
   */
  private int currentTestIndex = 0;

  /**
   * Results for all test cases in the current block.
   */
  private HashMap<TestInfo, JUnitResult> currentResults = new HashMap<TestInfo, JUnitResult>();

  /**
   * If set, all remaining tests will fail with the failure message.
   */
  private String failureMessage;

  /**
   * The remote service to communicate with.
   */
  private final JUnitHostAsync junitHost = (JUnitHostAsync) GWT.create(JUnitHost.class);

  /**
   * Handles all {@link TestBlock TestBlocks}.
   */
  private final TestBlockListener testBlockListener = new TestBlockListener();

  private GWTTestAccessor testAccessor;

  // TODO(FINDBUGS): can this be a private constructor to avoid multiple
  // instances?
  public GWTRunner() {
    sInstance = this;

    // Bind junitHost to the appropriate url.
    ServiceDefTarget endpoint = (ServiceDefTarget) junitHost;
    String url = GWT.getModuleBaseURL() + "junithost";
    endpoint.setServiceEntryPoint(url);

    // Null out the default uncaught exception handler since we will control it.
    GWT.setUncaughtExceptionHandler(null);
  }

  @Override
  public void onModuleLoad() {
    testAccessor = new GWTTestAccessor();

    // Kick off the test running process by getting the first method to run from the server.
    syncToServer();
  }

  public void reportResultsAndGetNextMethod(JUnitResult result) {
    if (failureMessage != null) {
      RuntimeException ex = new RuntimeException(failureMessage);
      result.setException(ex);
    }
    TestInfo currentTest = getCurrentTest();
    currentResults.put(currentTest, result);
    ++currentTestIndex;
    if (currentTestIndex < currentBlock.length) {
      // Run the next test after a short delay.
      Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
        @Override
        public void execute() {
          doRunTest();
        }
      });
    } else {
      syncToServer();
    }
  }

  /**
   * Executes a test on provided test class instance.
   */
  public void executeTestMethod(GWTTestCase testCase, String className, String methodName)
      throws Throwable {
    testAccessor.invoke(testCase, className, methodName);
  }

  private void doRunTest() {
    // Make sure the module matches.
    String currentModule = GWT.getModuleName();
    String newModule = getCurrentTest().getTestModule();
    if (currentModule.equals(newModule)) {
      // The module is correct.
      runTest();
    } else {
      String newHref = Window.Location.getHref().replace(currentModule, newModule);
      Window.Location.replace(newHref);
      currentBlock = null;
      currentTestIndex = 0;
    }
  }

  private TestInfo getCurrentTest() {
    return currentBlock[currentTestIndex];
  }

  private void runTest() {
    // Dynamically create a new test case.
    TestInfo currentTest = getCurrentTest();
    GWTTestCase testCase = null;
    try {
      testCase = testAccessor.newInstance(currentTest.getTestClass());
    } catch (Throwable e) {
      RuntimeException ex = new RuntimeException(currentTest
          + ": could not instantiate the requested class", e);
      JUnitResult result = new JUnitResult();
      result.setException(ex);
      reportResultsAndGetNextMethod(result);
      return;
    }
    testCase.init(currentTest.getTestClass(), currentTest.getTestMethod());
    testCase.__doRunTest();
  }

  /**
   * Fail all tests with the specified message.
   */
  private void setFailureMessage(String message) {
    failureMessage = message;
  }

  private void syncToServer() {
    junitHost.reportResultsAndGetTestBlock(currentResults, testBlockListener);
  }

  private static native void reportFatalError(String errorMsg)/*-{
    $wnd.junitError("/fatal", errorMsg);
  }-*/;

  private static native void reportWarning(String errorMsg)/*-{
    $wnd.junitError("", errorMsg);
  }-*/;
}

