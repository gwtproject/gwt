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

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.Element;

/**
 * An implementation using the <code>requestAnimationFrame</code>, or possibly a prefixed variant.
 * Since browser support is in flux, assumes as little as possible about the
 * JavaScript API. In particular, we only pass in the callback and don't look
 * at the return value. Also, the callback doesn't look at any parameters.
 *
 * <p>(This API was unprefixed in Chrome 24, despite not being standardized yet.)
 *
 * @see <a
 *      href="http://www.w3.org/TR/animation-timing/">Timing control for script-based animations</a>
 */
abstract class AnimationSchedulerImplNative extends AnimationSchedulerImpl {
  private JavaScriptObject requestAnimationFrame;

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
    Config config = getConfig();
    if (config.tryUnprefixedApi()) {
      requestAnimationFrame = getUnprefixedFunction();
      if (requestAnimationFrame != null) {
        return true;
      }
    }

    if (config.tryPrefixedApi()) {
      requestAnimationFrame = getPrefixedFunction();
      if (requestAnimationFrame != null) {
        return true;
      }
    }

    return false;
  }

  /**
   * Returns the appropriate configuration for this permutation.
   */
  protected abstract Config getConfig();

  /**
   * Returns the prefixed version of requestAnimationFrame, or null if not found.
   */
  protected abstract JavaScriptObject getPrefixedFunction();

  private native JavaScriptObject getUnprefixedFunction() /*-{
    return $wnd.requestAnimationFrame;
  }-*/;

  /**
   * Request an animation frame. To avoid depending on a request ID, we
   * create a JavaScriptObject and add an expando named "cancelled" to indicate
   * that the request was cancelled. The callback wrapper checks the expando before
   * executing the user callback.
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
