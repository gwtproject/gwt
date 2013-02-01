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
package com.google.gwt.animation.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.Element;

/**
 * An implementation that attempts to use <code>requestAnimationFrame</code> or a
 * prefixed variant, depending on configuration. Since browser support is in flux,
 * assumes as little as possible about the JavaScript API.
 * @see <a href="http://www.w3.org/TR/animation-timing/">Timing control for
 *     script-based animations</a>
 */
class AnimationSchedulerImplNative extends AnimationSchedulerImpl {
  private final BrowserFunctions browserFunctions;
  private JavaScriptObject requestAnimationFrame;

  public AnimationSchedulerImplNative() {
    browserFunctions = GWT.create(BrowserFunctions.class);
  }

  /**
   * A handle that remembers whether it was cancelled.
   */
  private class AnimationHandleImpl extends AnimationHandle {
    @SuppressWarnings("unused")
    private boolean cancelled;

    @Override
    public void cancel() {
      cancelled = true;
    }
  }

  @Override
  public AnimationHandle requestAnimationFrame(AnimationCallback callback, Element element) {
    assert requestAnimationFrame != null : "isNativelySupported didn't return true";
    AnimationHandleImpl handle = new AnimationHandleImpl();
    requestAnimationFrameImpl(requestAnimationFrame, callback, handle);
    return handle;
  }

  @Override
  protected final boolean isNativelySupported() {
    requestAnimationFrame = browserFunctions.getRequestAnimationFrame();
    return requestAnimationFrame != null;
  }

  /**
   * Request an animation frame. To avoid depending on the JavaScript function's return value,
   * we check a boolean on the handle to see whether the animation was cancelled.
   *
   * @param func the function to call
   * @param callback the user callback to execute
   * @param handle the handle object
   */
  private native void requestAnimationFrameImpl(
      JavaScriptObject func, AnimationCallback callback, AnimationHandleImpl handle) /*-{
    var wrapper = $entry(function() {
      if (!handle.@com.google.gwt.animation.client.AnimationSchedulerImplNative.AnimationHandleImpl::cancelled) {
        // Ignore any time parameter that we were called with.
        var now = @com.google.gwt.core.client.Duration::currentTimeMillis()();
        callback.@com.google.gwt.animation.client.AnimationScheduler.AnimationCallback::execute(D)(now);
      }
    });
    func(wrapper);
  }-*/;
}
