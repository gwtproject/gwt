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

import com.google.gwt.junit.client.impl.JUnitHost.TestInfo;
import com.google.gwt.junit.client.impl.JUnitResult;

import junit.framework.TestCase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Tests of {@link JUnitMessageQueue}.
 */
public class JUnitMessageQueueTest extends TestCase {

  public void testAddTestBlocks() {
    JUnitMessageQueue queue = new JUnitMessageQueue();
    List<TestInfo[]> expectedBlocks = new ArrayList<TestInfo[]>();

    // Add some blocks.
    {
      List<TestInfo[]> testBlocks = createTestBlocks(5, 3);
      queue.addTestBlocks(testBlocks, false);
      expectedBlocks.addAll(testBlocks);
      assertEquals(expectedBlocks.toArray(), queue.testBlocks.toArray());
    }

    // Add last block.
    {
      List<TestInfo[]> testBlocks = createTestBlocks(3, 1);
      queue.addTestBlocks(testBlocks, true);
      expectedBlocks.addAll(testBlocks);
      assertEquals(expectedBlocks.toArray(), queue.testBlocks.toArray());
    }

    // Try to add more blocks.
    {
      List<TestInfo[]> testBlocks = createTestBlocks(1, 1);
      try {
        queue.addTestBlocks(testBlocks, false);
        fail("Expected IllegalArgumentException");
      } catch (AssertionError e) {
        // expected.
      }
      assertEquals(expectedBlocks.toArray(), queue.testBlocks.toArray());
    }
  }

  public void testGetTestBlock() {
    final long timeout = 15000;
    JUnitMessageQueue queue = createQueue(2, 3);

    Iterator<TestInfo[]> iter = queue.testBlocks.iterator();
    TestInfo[] testBlock0 = iter.next();
    TestInfo[] testBlock1 = iter.next();

    // Get the first test block.
    assertEquals(testBlock0, queue.getTestBlock(timeout));
    // Get the first test block second time - should be same.
    assertEquals(testBlock0, queue.getTestBlock(timeout));

    queue.reportResults(createTestResults());
    // Get the second test block.
    assertEquals(testBlock1, queue.getTestBlock(timeout));

    queue.reportResults(createTestResults());
    // Get the third test block.
    assertNull(queue.getTestBlock(timeout));
  }

  public void testNeedRerunningExceptions() {
    JUnitMessageQueue queue = createQueue(1, 1);
    TestInfo testInfo = queue.testBlocks.iterator().next()[0];
    HashMap<TestInfo, JUnitResult> results = new HashMap<TestInfo, JUnitResult>();
    JUnitResult junitResult = new JUnitResult();
    junitResult.setException(new JUnitFatalLaunchException());
    results.put(testInfo, junitResult);
    queue.reportResults(results);
    assertFalse(queue.needsRerunning(testInfo));
  }

  public void testRetries() {
    JUnitMessageQueue queue = createQueue(1, 1);
    TestInfo testInfo = queue.testBlocks.iterator().next()[0];
    Map<TestInfo, JUnitResult> results = new HashMap<TestInfo, JUnitResult>();
    JUnitResult junitResult = new JUnitResult();
    junitResult.setException(new RuntimeException());
    results.put(testInfo, junitResult);
    queue.reportResults(results);
    assertTrue(queue.needsRerunning(testInfo));
    JUnitResult result = queue.getResult(testInfo);
    assertNotNull(result.getException());

    queue.removeResults(testInfo);

    queue.reportResults(createTestResults());
    assertFalse(queue.needsRerunning(testInfo));
    // check that the updated result appears now.
    result = queue.getResult(testInfo);
    assertNull(result.getException());
  }

  /**
   * Assert that two arrays are the same size and contain the same elements.
   * Ordering does not matter.
   *
   * @param expected the expected array
   * @param actual the actual array
   */
  private void assertSimilar(String[] expected, String[] actual) {
    assertEquals(new HashSet<String>(Arrays.asList(expected)),
        new HashSet<String>(Arrays.asList(actual)));
  }

  private void assertEquals(Object[] expected, Object[] actual) {
    assertEquals(Arrays.asList(expected), Arrays.asList(actual));
  }

  /**
   * Create a {@link JUnitMessageQueue} with the specified number of blocks.
   *
   * @param numBlocks the number of test blocks to add
   * @param testsPerBlock the number of tests per block
   * @return the message queue
   */
  private JUnitMessageQueue createQueue(int numBlocks, int testsPerBlock) {
    JUnitMessageQueue queue = new JUnitMessageQueue();
    queue.addTestBlocks(createTestBlocks(numBlocks, testsPerBlock), true);
    return queue;
  }

  /**
   * Create a list of test blocks.
   *
   * @param numBlocks the number of test blocks to add
   * @param testsPerBlock the number of tests per block
   * @return the test blocks
   */
  private List<TestInfo[]> createTestBlocks(int numBlocks, int testsPerBlock) {
    List<TestInfo[]> testBlocks = new ArrayList<TestInfo[]>();
    for (int i = 0; i < numBlocks; i++) {
      TestInfo[] testBlock = new TestInfo[testsPerBlock];
      for (int test = 0; test < testsPerBlock; test++) {
        testBlock[test] = new TestInfo("testModule" + i, "testClass",
            "testMethod" + test);
      }
      testBlocks.add(testBlock);
    }
    return testBlocks;
  }

  /**
   * Create some fake test results.
   */
  private Map<TestInfo, JUnitResult> createTestResults() {
    final int numTests = 5;
    Map<TestInfo, JUnitResult> results = new HashMap<TestInfo, JUnitResult>();
    for (int i = 0; i < numTests; i++) {
      TestInfo testInfo = new TestInfo("testModule0", "testClass", "testMethod" + i);
      results.put(testInfo, new JUnitResult());
    }
    return results;
  }
}
