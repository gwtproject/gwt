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
package com.google.gwt.animation.client;

import com.google.gwt.dom.client.Element;

/**
 * An implementation using the unprefixed <code>requestAnimationFrame</code>.
 * Since browser support is in flux, assumes as little as possible about the
 * JavaScript API. In particular, we only pass in the callback and don't look
 * at the return value. Also, the callback doesn't look at any parameters.
 *
 * <p>(This API was unprefixed in Chrome 24, despite not being standardized yet.)
 * 
 * @see <a
 *      href="http://www.w3.org/TR/animation-timing/">Timing control for script-based animations</a>
 */
class AnimationSchedulerImplNative extends AnimationSchedulerImpl {

  /**
   * A handle that remembers whether it was cancelled..
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
    AnimationHandleImpl handle = new AnimationHandleImpl();
    requestAnimationFrameImpl(callback, handle);
    return handle;
  }

  @Override
  protected native boolean isNativelySupported() /*-{
    return !!($wnd.requestAnimationFrame);
  }-*/;

  /**
   * Request an animation frame. To avoid depending on a request ID, we
   * create a JavaScriptObject and add an expando named "cancelled" to indicate
   * that the request was cancelled. The callback wrapper checks the expando before
   * executing the user callback.
   *
   * @param callback the user callback to execute
   * @param handle the handle object
   */
  private native void requestAnimationFrameImpl(AnimationCallback callback,
                                                AnimationHandleImpl handle) /*-{
    var wrapper = $entry(function() {
      if (!handle.@com.google.gwt.animation.client.AnimationSchedulerImplNative.AnimationHandleImpl::cancelled) {
        // Ignore any time parameter that we were called with.
        var now = @com.google.gwt.core.client.Duration::currentTimeMillis()();
        callback.@com.google.gwt.animation.client.AnimationScheduler.AnimationCallback::execute(D)(now);
      }
    });
    $wnd.requestAnimationFrame(wrapper);
  }-*/;
}
