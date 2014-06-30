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
package com.google.gwt.junit;

import com.google.gwt.junit.client.TimeoutException;
import com.google.gwt.junit.client.impl.JUnitHost.TestInfo;
import com.google.gwt.junit.client.impl.JUnitResult;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;

/**
 * A message queue to pass data between {@link JUnitShell} and
 * {@link com.google.gwt.junit.server.JUnitHostImpl} in a thread-safe manner.
 * 
 * <p>
 * The public methods are called by the servlet to find out what test to execute
 * next, and to report the results of the last test to run.
 * </p>
 * 
 * <p>
 * The protected methods are called by the shell to fetch test results and drive
 * the next test the client should run.
 * </p>
 */
public class JUnitMessageQueue {

  /**
   * Marker object that indicates a test is still running.
   */
  private static final JUnitResult RUNNING = new JUnitResult();

  /**
   * The lock used to synchronize access to clientStatuses.
   */
  private final Object clientStatusesLock = new Object();

  /**
   * Set to true when the last test block has been added. This is used to tell
   * clients that all tests are complete.
   */
  private volatile boolean isLastTestBlockAvailable;

  /**
   * The list of test blocks to run. Visible for testing.
   */
  final BlockingDeque<TestInfo[]> testBlocks = new LinkedBlockingDeque<TestInfo[]>();

  /**
   * Maps the TestInfo to the results from each clientId. If JUnitResult is
   * null, it means that the client requested the test but did not report the
   * results yet.
   */
  private final Map<TestInfo, JUnitResult> testResults = new ConcurrentHashMap<TestInfo, JUnitResult>();

  /**
   * Only instantiable within this package.
   */
  JUnitMessageQueue() { }

  /**
   * Called by the servlet to query for for the next block to test.
   * 
   * @param timeout how long to wait for an answer
   * @return the next test to run, or <code>null</code> if the last test block received.
   */
  public TestInfo[] getTestBlock(long timeout) throws TimeoutException {
    // The client has finished all of the tests.
    if (isLastTestBlockAvailable && testBlocks.isEmpty()) {
      return null;
    }

    TestInfo[] tests = peekTestBlock(timeout);
    if (tests == null) {
      throw new TimeoutException("No tests fetched within " + timeout + "ms.");
    }

    // Mark all the test block as running.
    testResults.putAll(createResults(tests, RUNNING));
    return tests;
  }

  private TestInfo[] peekTestBlock(long timeout) {
    // This block is safe only because there is no other thread adding to the front of the queue.
    try {
      TestInfo[] tests = testBlocks.pollFirst(timeout, TimeUnit.MILLISECONDS);
      if (tests != null) {
        testBlocks.addFirst(tests);
      }
      return tests;
    } catch (InterruptedException e) {
      e.printStackTrace();
      System.exit(1);
      return null;
    }
  }

  /**
   * Reports a failure from a client that cannot startup.
   * 
   * @param result the failure result
   */
  public void reportFatalLaunch(JUnitResult result) {
    // Fatal launch error, cause this client to fail the whole block.
    reportResults(createResults(testBlocks.peek(), result));
  }

  /**
   * Called by the servlet to report the results of the last test to run.
   *
   * @param results the result of running the test block
   */
  public void reportResults(Map<TestInfo, JUnitResult> results) {
    assert results != null : "results cannot be null";

    // Drop the last test block.
    testBlocks.poll();

    // Cache the test results.
    testResults.putAll(results);

    // Notify so that waitForTestResults is unblocked.
    synchronized (clientStatusesLock) {
      clientStatusesLock.notifyAll();
    }
  }

  /**
   * Called by the shell to add test blocks to test.
   * 
   * @param isLastBlock true if this is the last test block that will be added
   */
  void addTestBlocks(List<TestInfo[]> newTestBlocks, boolean isLastBlock) {
    assert !isLastTestBlockAvailable : "Cannot add test blocks after the last block is added";

    testBlocks.addAll(newTestBlocks);
    isLastTestBlockAvailable = isLastBlock;
  }

  /**
   * Returns how many clients have requested the currently-running test.
   * 
   * @param testInfo the {@link TestInfo} that the clients retrieved
   */
  boolean isClientRetrievedTest(TestInfo testInfo) {
    return testResults.containsKey(testInfo);
  }

  /**
   * Fetches the result of a completed test.
   */
  JUnitResult getResult(TestInfo testInfo) {
    JUnitResult result = testResults.get(testInfo);
    return result == RUNNING ? null : result;
  }

  /**
   * Returns true iff any there are no results, missing results, or any of the
   * test results is an exception other than those in {@code
   * THROWABLES_NOT_RETRIED}.
   */
  boolean needsRerunning(TestInfo testInfo) {
    JUnitResult result = testResults.get(testInfo);
    if (result == null || result == RUNNING) {
      return true;
    }
    return isNonFatalFailure(result);
  }

  private boolean isNonFatalFailure(JUnitResult result) {
    return result.isAnyException()
        && !result.isExceptionOf(Error.class)
        && !result.isExceptionOf(JUnitFatalLaunchException.class);
  }

  void removeResults(TestInfo testInfo) {
    testResults.remove(testInfo);
  }

  void waitForResults(int millis) {
    synchronized (clientStatusesLock) {
      try {
        clientStatusesLock.wait(millis);
      } catch (InterruptedException e) {
      }
    }
  }

  private static Map<TestInfo, JUnitResult> createResults(TestInfo[] tests, JUnitResult result) {
    Map<TestInfo, JUnitResult> results = new HashMap<TestInfo, JUnitResult>();
    for (TestInfo testInfo : tests) {
      results.put(testInfo, result);
    }
    return results;
  }
}
