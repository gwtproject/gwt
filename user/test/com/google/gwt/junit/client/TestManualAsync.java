/*
 * Copyright 2007 Google Inc.
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

import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;

/**
 * This test must be run manually to inspect for correct results. Many of these
 * tests are designed to fail in specific ways, the rest should succeed. The
 * name of each test method indicates how it should behave.
 */
public class TestManualAsync extends GWTTestCaseTest {

  /**
   * Fails normally.
   */
  public void testDelayFail() {
    delayTestFinish(100);
    fail("Expected failure");
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
   * Fails normally.
   */
  public void testFail() {
    fail("Expected failure");
  }

  /**
   * Async fails.
   */
  public void testFailAsync() {
    delayTestFinish(200);
    new Timer() {
      public void run() {
        fail("Expected failure");
      }
    }.schedule(100);
  }

  /**
   * Completes normally.
   */
  public void testNormal() {
  }

  /**
   * Completes async.
   */
  public void testNormalAsync() {
    delayTestFinish(200);
    new Timer() {
      public void run() {
        finishTest();
      }
    }.schedule(100);
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

  /**
   * Fails async.
   */
  public void testSetUpTearDownFailAsync() {
    assertEquals(1, setupTeardownFlag);
    delayTestFinish(1000);
    new Timer() {
      @Override
      public void run() {
        fail("Expected failure");
      }
    }.schedule(1);

    new Timer() {
      @Override
      public void run() {
        /*
         * The failing test should have triggered tearDown.
         */
        if (setupTeardownFlag != 2) {
          // Must use window alert to grind the test to a halt in this failure.
          Window.alert("Bad async failure tearDown behavior not catchable by JUnit");
        }
      }
    }.schedule(100);
  }

  /**
   * Times out async.
   */
  public void testSetUpTearDownTimeoutAsync() {
    assertEquals(1, setupTeardownFlag);
    delayTestFinish(1);
    new Timer() {
      @Override
      public void run() {
        /*
         * The failing test should have triggered tearDown.
         */
        if (setupTeardownFlag != 2) {
          // Must use window alert to grind the test to a halt in this failure.
          Window.alert("Bad async timeout tearDown behavior not catchable by JUnit");
        }
      }
    }.schedule(100);
  }

  /**
   * Completes normally.
   */
  public void testSpuriousFinishTest() {
    try {
      finishTest();
      fail("Unexpected failure");
    } catch (IllegalArgumentException e) {
    }
  }

  /**
   * Times out.
   */
  public void testTimeoutAsync() {
    delayTestFinish(100);
    new Timer() {
      public void run() {
        finishTest();
      }
    }.schedule(200);
  }

}
