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
package com.google.gwt.user.client;

import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;

import java.util.ArrayList;

/**
 * A simplified, browser-safe timer class. This class serves the same purpose as
 * java.util.Timer, but is simplified because of the single-threaded
 * environment.
 * 
 * <p>
 * To schedule a timer, simply create a subclass of it (overriding {@link #run})
 * and call {@link #schedule} or {@link #scheduleRepeating}.
 * </p>
 * 
 * <p>
 * NOTE: If you are using a timer to schedule a UI animation, use
 * {@link com.google.gwt.animation.client.AnimationScheduler} instead. The
 * browser can optimize your animation for maximum performance.
 * </p>
 * 
 * <p>
 * <h3>Example</h3>
 * {@example com.google.gwt.examples.TimerExample}
 * </p>
 */
public abstract class Timer {

  private static ArrayList<Timer> timers = new ArrayList<Timer>();

  static {
    hookWindowClosing();
  }

  private static native void clearInterval(int id) /*-{
    @com.google.gwt.core.client.impl.Impl::clearInterval(I)(id);
  }-*/;

  private static native void clearTimeout(int id) /*-{
    @com.google.gwt.core.client.impl.Impl::clearTimeout(I)(id);
  }-*/;

  private static native int createInterval(Timer timer, int period, int cancelCounter) /*-{
    return @com.google.gwt.core.client.impl.Impl::setInterval(Lcom/google/gwt/core/client/JavaScriptObject;I)(
        $entry(function() { timer.@com.google.gwt.user.client.Timer::fire(I)(cancelCounter); }),
      period);
  }-*/;

  private static native int createTimeout(Timer timer, int delay, int cancelCounter) /*-{
    return @com.google.gwt.core.client.impl.Impl::setTimeout(Lcom/google/gwt/core/client/JavaScriptObject;I)(
      $entry(function() { timer.@com.google.gwt.user.client.Timer::fire(I)(cancelCounter); }),
      delay);
  }-*/;

  private static void hookWindowClosing() {
    // Catch the window closing event.
    Window.addCloseHandler(new CloseHandler<Window>() {

      public void onClose(CloseEvent<Window> event) {
        while (timers.size() > 0) {
          timers.get(0).cancel();
        }
      }
    });
  }

  private boolean isRepeating;

  private int timerId;

  /**
   * Workaround for broken clearTimeout in IE. Keeps track of whether cancel has been called since
   * schedule was called. See https://code.google.com/p/google-web-toolkit/issues/detail?id=8101
   */
  private int cancelCounter = 0;

  /**
   * Cancels this timer.
   */
  public void cancel() {
    cancelCounter++;
    if (isRepeating) {
      clearInterval(timerId);
    } else {
      clearTimeout(timerId);
    }
    timers.remove(this);
  }

  /**
   * This method will be called when a timer fires. Override it to implement the
   * timer's logic.
   */
  public abstract void run();

  /**
   * Schedules a timer to elapse in the future.
   * 
   * @param delayMillis how long to wait before the timer elapses, in
   *          milliseconds
   */
  public void schedule(int delayMillis) {
    if (delayMillis < 0) {
      throw new IllegalArgumentException("must be non-negative");
    }
    cancel();
    isRepeating = false;
    timerId = createTimeout(this, delayMillis, cancelCounter);
    timers.add(this);
  }

  /**
   * Schedules a timer that elapses repeatedly.
   * 
   * @param periodMillis how long to wait before the timer elapses, in
   *          milliseconds, between each repetition
   */
  public void scheduleRepeating(int periodMillis) {
    if (periodMillis <= 0) {
      throw new IllegalArgumentException("must be positive");
    }
    cancel();
    isRepeating = true;
    timerId = createInterval(this, periodMillis, cancelCounter);
    timers.add(this);
  }

  /*
   * Called by native code when this timer fires.
   *
   * Only call run() if cancelCounter has not changed since the timer was scheduled.
   */
  final void fire(int scheduleCancelCounter) {
    if (scheduleCancelCounter != cancelCounter) {
      return;
    }

    // If this is a one-shot timer, remove it from the timer list. This will
    // allow it to be garbage collected.
    if (!isRepeating) {
      timers.remove(this);
    }

    // Run the timer's code.
    run();
  }
}
