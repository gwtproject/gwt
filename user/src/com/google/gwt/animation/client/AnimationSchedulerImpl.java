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

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.Element;

/**
 * Scheduler implementation that tries to use window.requestAnimationFrame and
 * otherwise uses polyfills.
 */
public class AnimationSchedulerImpl extends AnimationScheduler {

  private class AnimationHandleImpl extends AnimationHandle {

    private JavaScriptObject holder;

    public AnimationHandleImpl(JavaScriptObject holder) {
      this.holder = holder;
    }

    @Override
    public void cancel() {
      AnimationSchedulerImpl.this.cancel(cancelFunction, holder);
    }
  }

  public static final AnimationSchedulerImpl INSTANCE = new AnimationSchedulerImpl();

  // TODO: replace with call to capabilities API?
  private static native boolean hasRequestAnimationFrame() /*-{
   return (!!$wnd.requestAnimationFrame && 
       (!!$wnd.cancelAnimationFrame || !!$wnd.cancelRequestAnimationFrame));
  }-*/;

  private JavaScriptObject requestFunction;
  private JavaScriptObject cancelFunction;

  private AnimationSchedulerImpl() {
    if (hasRequestAnimationFrame()) {
      initModern();
    } else {
      initPolyfill();
    }
  }

  @Override
  public AnimationHandle requestAnimationFrame(AnimationCallback callback, Element element) {
    JavaScriptObject id = requestAnimationFrameImpl(callback, element, requestFunction);
    return new AnimationHandleImpl(id);
  }

  private native void cancel(JavaScriptObject cancelFunction, JavaScriptObject holder) /*-{
    // not calling window.cancelAnimationFrame with window as this breaks FireFox and Safari.
    cancelFunction.call($wnd, holder.id);
  }-*/;

  private native void initModern() /*-{
    var request = $wnd.requestAnimationFrame;
    var cancel = $wnd.cancelAnimationFrame || $wnd.cancelRequestAnimationFrame;

    this.@com.google.gwt.animation.client.AnimationSchedulerImpl::requestFunction = request;
    this.@com.google.gwt.animation.client.AnimationSchedulerImpl::cancelFunction = cancel;
  }-*/;
  
  private native void initPolyfill() /*-{
    var lastTime = 0;
    var request = function(callback, element) {
      var currTime = @com.google.gwt.core.client.Duration::currentTimeMillis()();
      // Using 16 ms as a callback base will yield 60 fps (1000 / 16 = 66.66);
      var timeToCall = Math.max(0, 16 - (currTime - lastTime));
      lastTime = currTime + timeToCall;
      return $wnd.setTimeout(function() {
        callback(currTime + timeToCall);
      }, timeToCall);
    };

    var cancel = function(id){
      $wnd.clearTimeout(id);
    };

    this.@com.google.gwt.animation.client.AnimationSchedulerImpl::requestFunction = request;
    this.@com.google.gwt.animation.client.AnimationSchedulerImpl::cancelFunction = cancel;
  }-*/;

  private native JavaScriptObject requestAnimationFrameImpl(AnimationCallback callback,
      Element element, JavaScriptObject requestFunction) /*-{
    var wrapper = function(time){
      callback.@com.google.gwt.animation.client.AnimationScheduler.AnimationCallback::execute(D)(time);
    };

    // not calling window.requestAnimationFrame with window as this breaks FireFox and Safari.
    var id = requestFunction.call($wnd, $entry(wrapper), element);

    // some platforms seem to return numbers, others opaque handles
    // just to be safe wrap the result in a JSO.
    return {id: id};
  }-*/;
}
