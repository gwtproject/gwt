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

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.GWT.UncaughtExceptionHandler;
import com.google.gwt.junit.ExpectedFailure;

/**
 * This class tests GwtTestCase uncaught exception catching capabilities.
 *
 * Note: This classes uses naming conventions to configure the test case. Alternative would have
 * required different test cases for the permutations.
 */
public class GWTTestCaseUncaughtExceptionHandlerTest extends GWTTestCaseTestBase {

  private UncaughtExceptionHandler myHandler = new UncaughtExceptionHandler() {
    public void onUncaughtException(Throwable e) { /* NO_OP */}
  };

  private boolean throwsInGwtSetUp;
  private boolean throwsInGwtTearDown;
  private boolean installsOwnHandlerInGwtSetup;
  private boolean optsOutCatchUncaught;

  @Override
  public void setName(String name) {
    super.setName(name);
    String[] config = name.split("_");
    for (int i = 1; i < config.length; i++) {
      setConfig(config[i]);
    }
  }

  private void setConfig(String confToken) {
    if ("throwsInGwtSetUp".equals(confToken)) {
      throwsInGwtSetUp = true;
    } else if ("throwsInGwtTearDown".equals(confToken)) {
      throwsInGwtTearDown = true;
    } else if ("installsOwnHandlerInGwtSetup".equals(confToken)) {
      installsOwnHandlerInGwtSetup = true;
    } else if ("optsOutCatchUncaught".equals(confToken)) {
      optsOutCatchUncaught = true;
    } else {
      throw new RuntimeException("Unexpected token in test name: " + confToken);
    }
  }

  @Override
  protected void gwtSetUp() throws Exception {
    if (throwsInGwtSetUp) {
      failViaUncaughtException("fail");
    }
    if (optsOutCatchUncaught) {
      assertFalse(isGWTTestCaseHandlerSet());
    }
    if (installsOwnHandlerInGwtSetup) {
      GWT.setUncaughtExceptionHandler(myHandler);
    }
  }

  @Override
  protected void gwtTearDown() throws Exception {
    if (throwsInGwtTearDown) {
      failViaUncaughtException("fail");
    }
  }

  @Override
  public boolean catchUncaughtExceptions() {
    return !optsOutCatchUncaught;
  }

  public void testCatchUncaughtExceptions() {
    assertTrue(super.catchUncaughtExceptions()); // Default should be true
    assertTrue(isGWTTestCaseHandlerSet());
  }

  public void testCatchUncaughtExceptions_optsOutCatchUncaught() {
    assertFalse(isGWTTestCaseHandlerSet());
  }

  @ExpectedFailure(withMessage = "fail")
  public void testFailWithUncaughtExceptionHandler() {
    failViaUncaughtException("fail");
  }

  @ExpectedFailure(withMessage = "fail")
  public void testFailWithUncaughtExceptionHandlerBeforeSyncronousFailure() {
    failViaUncaughtException("fail");
    failNow("failNow");
  }

  @ExpectedFailure(withMessage = "fail")
  public void testFailWithUncaughtExceptionHandlerBeforeException() {
    failViaUncaughtException("fail");
    throw new RuntimeException();
  }

  @ExpectedFailure(withMessage = "fail")
  public void testFailWithUncaughtExceptionHandler_throwsInGwtSetUp() {
    // gwtSetUp is configured to throw exception
  }

  // http://code.google.com/p/google-web-toolkit/issues/detail?id=7888
  @ExpectedFailure(withMessage = "fail")
  public void _suppressed_testFailWithUncaughtExceptionHandler_throwsInGwtTearDown() {
    // gwtTearDown is configured to throw exception
  }

  @ExpectedFailure(withType = RuntimeException.class,
      withMessage = "GWTTestCase#catchUncaughtExceptions")
  public void testSetUpUncaughtHandlerCheck_installsOwnHandlerInGwtSetup() {
    // Nothing to do here, #setUp in test infra should fail.
  }

  public void testSetUpUncaughtHandlerCheck_installsOwnHandlerInGwtSetup_optsOutCatchUncaught() {
    // Nothing to do here, just #setup in test infra should NOT fail.
  }

  @ExpectedFailure(withType = RuntimeException.class,
      withMessage = "GWTTestCase#catchUncaughtExceptions")
  public void testTearDownUncaughtHandlerCheck() {
    GWT.setUncaughtExceptionHandler(myHandler);
  }

  public void testTearDownUncaughtHandlerCheck_optsOutCatchUncaught() {
    GWT.setUncaughtExceptionHandler(myHandler);
  }

  private boolean isGWTTestCaseHandlerSet() {
    // As some test cases, opted out from GWTTestCase UncaughtExceptionHandler management,
    // previously added user handlers may leak through test cases but the handler still should not
    // be the one from GWTTestCase.
    UncaughtExceptionHandler handler = GWT.getUncaughtExceptionHandler();
    return handler != null && handler.getClass() != myHandler.getClass();
  }
}
