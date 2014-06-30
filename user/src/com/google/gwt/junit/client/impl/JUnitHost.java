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

import com.google.gwt.junit.client.TimeoutException;
import com.google.gwt.user.client.rpc.RemoteService;

import java.io.Serializable;
import java.util.HashMap;

/**
 * An interface for {@link com.google.gwt.junit.client.GWTTestCase} to
 * communicate with the test process through RPC.
 */
public interface JUnitHost extends RemoteService {

  /**
   * Returned from the server to tell the system what test to run next.
   */
  public static class TestInfo implements Serializable {
    private String testClass;
    private String testMethod;
    private String testModule;

    public TestInfo(String testModule, String testClass, String testMethod) {
      this.testModule = testModule;
      this.testClass = testClass;
      this.testMethod = testMethod;
    }

    /**
     * Constructor for serialization.
     */
    TestInfo() {
    }

    @Override
    public boolean equals(Object o) {
      if (o instanceof TestInfo) {
        TestInfo other = (TestInfo) o;
        return equals(testModule, other.testModule)
            && equals(testClass, other.testClass)
            && equals(testMethod, other.testMethod);
      }
      return false;
    }

    /*
     * Helper method for TestInfo.equals.
     *
     * TODO: Replace with Objects.equals() once we can rely on JDK7.
     */
    private static boolean equals(Object a, Object b) {
      return a == null ? b == null : a.equals(b);
    }

    public String getTestClass() {
      return testClass;
    }

    public String getTestMethod() {
      return testMethod;
    }

    public String getTestModule() {
      return testModule;
    }

    @Override
    public int hashCode() {
      return toString().hashCode();
    }

    @Override
    public String toString() {
      return testModule + ":" + testClass + "." + testMethod;
    }
  }

  /**
   * Reports results for the last method run and gets the name of next method to run.
   *
   * @param results the results of executing the test
   * @return the next test block
   * @throws TimeoutException if the wait for the next method times out.
   */
  TestInfo[] reportResultsAndGetTestBlock(HashMap<TestInfo, JUnitResult> results)
      throws TimeoutException;
}

