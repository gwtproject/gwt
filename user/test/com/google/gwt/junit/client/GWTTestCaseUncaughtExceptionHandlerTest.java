/*
 * Copyright 2012 Google Inc.
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

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.GWT.UncaughtExceptionHandler;
import com.google.gwt.junit.ExpectedFailure;

/**
 * This class tests GwtTestCase in async mode.
 *
 * Note: This test requires some test methods to be executed in a specific order.
 */
public class GWTTestCaseUncaughtExceptionHandlerTest extends GWTTestCaseTestBase {

  private static boolean isMyHandlerPreserved;
  private static Throwable lastExceptionHandledByMyHandler;

  private static class MyHandler implements UncaughtExceptionHandler {
    private boolean shouldThrow;
    private Throwable e;

    public void onUncaughtException(Throwable e) {
      this.e = e;
      if (shouldThrow) {
        throw new RuntimeException("die");
      }
    }
  }

  private MyHandler myHandler;

  @Override
  protected void gwtSetUp() throws Exception {
    myHandler = new MyHandler();
    GWT.setUncaughtExceptionHandler(myHandler);
  }

  @Override
  protected void gwtTearDown() throws Exception {
    isMyHandlerPreserved = myHandler == GWT.getUncaughtExceptionHandler();
    lastExceptionHandledByMyHandler = myHandler.e;
    GWT.setUncaughtExceptionHandler(null);
  }

  @ExpectedFailure(withMessage = "fail")
  public void testFailWithUncaughtExceptionHandler() {
    failViaUncaughtException("fail");
  }

  @ExpectedFailure(withMessage = "fail")
  public void testFailWithUncaughtExceptionHandler_beforeSyncronousFailure() {
    failViaUncaughtException("fail");
    failNow("failNow");
  }

  @ExpectedFailure(withMessage = "fail")
  public void testFailWithUncaughtExceptionHandler_beforeException() {
    failViaUncaughtException("fail");
    throw new RuntimeException();
  }

  // Needs to run after any test case uses failViaUncaughtException
  public void testOriginalHandler() {
    assertTrue(isMyHandlerPreserved);
    assertTrue(lastExceptionHandledByMyHandler instanceof AssertionError);
    AssertionError assertionError = (AssertionError) lastExceptionHandledByMyHandler;
    assertEquals("Expected failure (fail)", assertionError.getMessage());
  }

  @ExpectedFailure(withMessage = "fail")
  public void testOriginalHandlerThrowsException() {
    myHandler.shouldThrow = true;
    failViaUncaughtException("fail");
  }
}
