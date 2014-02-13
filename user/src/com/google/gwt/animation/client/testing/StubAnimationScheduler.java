/*
 * Copyright 2011 Google Inc.
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
package com.google.gwt.animation.client.testing;

import com.google.gwt.animation.client.AnimationScheduler;
import com.google.gwt.dom.client.Element;

import java.util.ArrayList;
import java.util.List;

/**
 * A stub implementation of {@link AnimationScheduler}.
 * <p>
 * Callbacks are recorded and can be retrieved using {@link #getAnimationCallbacks()}
 * to, for example, execute them manually.
 * <p>
 * You can also execute all recorded callbacks (and clear the
 * list) using {@link #tick(double)}.
 */
public class StubAnimationScheduler extends AnimationScheduler {

  /**
   * A handle to the requested animation frame created by
   * {@link #requestAnimationFrame(AnimationCallback, Element)}.
   */
  public class StubAnimationHandle extends AnimationHandle {

    private final AnimationCallback callback;

    public StubAnimationHandle(AnimationCallback callback) {
      this.callback = callback;
    }

    @Override
    public void cancel() {
      callbacks.remove(callback);
    }
  }

  private final List<AnimationCallback> callbacks = new ArrayList<AnimationCallback>();

  /**
   * Get the list of all animation callbacks that have been requested and have
   * not been canceled.
   * 
   * @return the list of callbacks.
   */
  public List<AnimationCallback> getAnimationCallbacks() {
    return callbacks;
  }

  @Override
  public double currentTimeStamp() {
    // Note: use System instead of Duration so we don't require GWTTestCase
    return System.currentTimeMillis();
  }

  @Override
  public StubAnimationHandle requestAnimationFrame(AnimationCallback callback, Element element) {
    callbacks.add(callback);
    return new StubAnimationHandle(callback);
  }

  /**
   * Execute all the recorded callbacks at the given timestamp, and clears the list.
   * <p>
   * The timestamp should be computed relative to a previous call to {@link #currentTimeStamp()}.
   * <p>
   * <b>Implementation note:</b> exceptions thrown by callbacks will bubble and interrupt
   * the execution. The state of the {@link StubAnimationScheduler} is undefined after
   * such exceptions. If you want to handle exceptions thrown by callbacks, retrieve them
   * using {@link #getAnimationCallbacks()} and execute them manually.
   * 
   * @param timestamp the timestamp passed to the callbacks.
   */
  public void tick(double timestamp) {
    List<AnimationCallback> cbs = new ArrayList<AnimationCallback>(this.callbacks);
    this.callbacks.clear();
    for (AnimationCallback callback : cbs) {
      callback.execute(timestamp);
    }
  }
}
