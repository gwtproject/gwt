/*
 * Copyright 2008 Google Inc.
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

package com.google.gwt.user.client;

import com.google.gwt.core.client.Duration;
import com.google.gwt.junit.client.GWTTestCase;

public class TimerCancelTest extends GWTTestCase {

  private final class CountingTimer extends Timer {
    @Override
    public void run() {
      timerCount++;
    }
  }

  private int timerCount;

  @Override
  protected void gwtSetUp() throws Exception {
    timerCount = 0;
  }

  public void testCancelTimer() {
    final Timer canceledTimer = new CountingTimer();

    Timer cancelingTimer = new Timer() {
      @Override
      public void run() {
        assertEquals(0, timerCount);
        canceledTimer.cancel();
      }
    };
    cancelingTimer.schedule(50);
    canceledTimer.schedule(100);

    double start = Duration.currentTimeMillis();
    while (Duration.currentTimeMillis() - start <= 200) {
      // Busy wait so that both timers will be added to the event loop queue the next time it is
      // updated
    }

    delayTestFinish(500);
    new Timer() {
      @Override
      public void run() {
        assertEquals(0, timerCount);
        finishTest();
      }
    }.schedule(300);
  }

  public void testRestartTimer() {
    final Timer restartedTimer = new CountingTimer();

    Timer cancelingTimer = new Timer() {
      @Override
      public void run() {
        assertEquals(0, timerCount);
        restartedTimer.cancel();
        restartedTimer.schedule(100);
      }
    };

    cancelingTimer.schedule(50);
    restartedTimer.schedule(100);

    double start = Duration.currentTimeMillis();
    while (Duration.currentTimeMillis() - start <= 200) {
      // Busy wait so that both timers will be added to the event loop queue the next time it is
      // updated
    }

    delayTestFinish(500);
    new Timer() {
      @Override
      public void run() {
        assertEquals(1, timerCount);
        finishTest();
      }
    }.schedule(400);
  }

  @Override
  public String getModuleName() {
    return "com.google.gwt.user.UserTest";
  }

}
