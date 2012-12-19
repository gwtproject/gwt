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

import com.google.gwt.dom.client.ButtonElement;
import com.google.gwt.dom.client.Document;
import com.google.gwt.junit.ExpectedFailure;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.EventListener;
import com.google.gwt.user.client.Timer;

/**
 * This class tests GwtTestCase in async mode.
 *
 * Note: This test requires some test methods to be executed in a specific order.
 */
public class GWTTestCaseAsyncTest extends GWTTestCase {

  public String getModuleName() {
    return "com.google.gwt.junit.OrderedJUnitTest";
  }

  // The following tests (all prefixed with test_) are intended to test the
  // interaction of synchronous failures (within event handlers) with various
  // other types of failures and successes. All of them are expected to fail
  // with the message "Expected failure".
  //
  // Nomenclature for these tests:
  // DTF => delayTestFinish()
  // SF => synchronous failure (from event handler)
  // FT => finishTest()
  // F => fail()
  // R => return;

  @ExpectedFailure(withMessage = "test_dtf_sf")
  public void test_dtf_sf() {
    delayTestFinish();
    synchronousFailure("test_dtf_sf");
  }

  @ExpectedFailure(withMessage = "test_dtf_sf_f")
  public void test_dtf_sf_f() {
    delayTestFinish();
    synchronousFailure("test_dtf_sf_f");
    failNow("test_dtf_sf_f");
  }

  @ExpectedFailure(withMessage = "test_dtf_sf_ft")
  public void test_dtf_sf_ft() {
    delayTestFinish();
    synchronousFailure("test_dtf_sf_ft");
    finishTest();
  }

  // Issue: http://code.google.com/p/google-web-toolkit/issues/detail?id=7846
  @ExpectedFailure(withMessage = "test_dtf_sf_r_f")
  public void _suppressed_test_dtf_sf_r_f() {
    delayTestFinish();
    synchronousFailure("test_dtf_sf_r_f");
    failLater("test_dtf_sf_r_f");
  }

  @ExpectedFailure(withMessage = "test_dtf_sf_r_ft")
  public void test_dtf_sf_r_ft() {
    delayTestFinish();
    synchronousFailure("test_dtf_sf_r_ft");
    finishTestLater();
  }

  @ExpectedFailure(withMessage = "test_sf")
  public void test_sf() {
    synchronousFailure("test_sf");
  }

  @ExpectedFailure(withMessage = "test_sf_dtf_f")
  public void test_sf_dtf_f() {
    synchronousFailure("test_sf_dtf_f");
    delayTestFinish();
    failNow("test_sf_dtf_f");
  }

  @ExpectedFailure(withMessage = "test_sf_dtf_ft")
  public void test_sf_dtf_ft() {
    synchronousFailure("test_sf_dtf_ft");
    delayTestFinish();
    finishTest();
  }

  // Issue: http://code.google.com/p/google-web-toolkit/issues/detail?id=7846
  @ExpectedFailure(withMessage = "test_sf_dtf_r_f")
  public void _suppressed_test_sf_dtf_r_f() {
    synchronousFailure("test_sf_dtf_r_f");
    delayTestFinish();
    failLater("test_sf_dtf_r_f");
  }

  @ExpectedFailure(withMessage = "test_sf_dtf_r_ft")
  public void test_sf_dtf_r_ft() {
    synchronousFailure("test_sf_dtf_r_ft");
    delayTestFinish();
    finishTestLater();
  }

  @ExpectedFailure(withMessage = "test_sf_f")
  public void test_sf_f() {
    synchronousFailure("test_sf_f");
    failNow("test_sf_f");
  }

  /**
   * Fails normally.
   */
  @ExpectedFailure(withMessage = "testDelayFail")
  public void testDelayFail() {
    delayTestFinish(100);
    fail("testDelayFail");
    finishTest();
  }

  /**
   * Completes normally.
   */
  public void testDelayNormal() {
    delayTestFinish(100);
    finishTest();
  }

  /**
   * Async fails.
   */
  @ExpectedFailure(withMessage = "testFailAsync")
  public void testFailAsync() {
    delayTestFinish(1000);
    failLater("testFailAsync");
  }

  /**
   * Tests the case where a JUnit exception is thrown from an event handler, but
   * after this test method has completed successfully.
   * 
   * This test should *not* fail, but the next one should.
   */
  public void testLateFail() {
    // Leave the test in synchronous mode, but crank up a timer to fail in 2.5s.
    failLater("testLateFail", 2500);
    // We don't actually assert anything here. This test exists solely to make
    // the next one fail.
  }

  /**
   * Second half of the previous test.
   */
  @ExpectedFailure(withMessage = "testLateFail")
  public void testLateFail_assert() {
    // Go into async mode from 5s, finishing in 4. The timer from the previous
    // test will go off and call fail() before finishTest() is called.
    delayTestFinish(5000);
  }

  /**
   * Completes normally.
   */
  public void testSpuriousFinishTest() {
    try {
      finishTest();
      fail("finishTest should have failed");
    } catch (IllegalStateException e) {
    }
  }

  /**
   * Times out.
   */
  @ExpectedFailure(withType = TimeoutException.class)
  public void testTimeoutAsync() {
    delayTestFinish(100);
    finishTestLater(200);
  }
  
  /**
   * Completes async.
   */
  public void testNormalAsync() {
    delayTestFinish(200);
    finishTestLater(100);
  }

  /**
   * Completes async.
   */
  public void testRepeatingNormal() {
    delayTestFinish(200);
    new Timer() {
      private int i = 0;

      public void run() {
        if (++i < 4) {
          delayTestFinish(200);
        } else {
          cancel();
          finishTest();
        }
      }
    }.scheduleRepeating(100);
  }

  // Call delayTestFinish() with enough time so that failLater() will
  // definitely fail.
  private void delayTestFinish() {
    delayTestFinish(2500);
  }

  private void failLater(final String failMsg) {
    failLater(failMsg, 100);
  }

  private void failLater(final String failMsg, int delay) {
    new Timer() {
      @Override
      public void run() {
        failNow(failMsg);
      }
    }.schedule(delay);
  }

  private void failNow(String failMsg) {
    fail("Expected failure (" + failMsg + ")");
  }

  private void finishTestLater() {
    finishTestLater(1);
  }

  private void finishTestLater(int delay) {
    new Timer() {
      @Override
      public void run() {
        finishTest();
      }
    }.schedule(delay);
  }

  // Trigger a test failure synchronously, but from within an event handler.
  // (The exception thrown from fail() will get caught by the GWT
  // UncaughtExceptionHandler).
  private void synchronousFailure(final String failMsg) {
    ButtonElement btn = Document.get().createPushButtonElement();
    Document.get().getBody().appendChild(btn);
    Event.sinkEvents(btn, Event.ONCLICK);

    EventListener listener = new EventListener() {
      public void onBrowserEvent(Event event) {
        failNow(failMsg);
      }
    };

    DOM.setEventListener(btn.<com.google.gwt.user.client.Element>cast(), listener);
    btn.click();
  }
}
