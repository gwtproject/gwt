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

  public static final AnimationSchedulerImpl INSTANCE = new AnimationSchedulerImpl();

  private class AnimationHandle1 extends AnimationHandle {

    private JavaScriptObject holder;

    public AnimationHandle1(JavaScriptObject holder) {
      this.holder = holder;
    }
    @Override
    public void cancel() {
      AnimationSchedulerImpl.this.cancel(cancelFunction, holder);
    }
  }

  private JavaScriptObject requestFunction;
  private JavaScriptObject cancelFunction;

  private AnimationSchedulerImpl() {
    initializeAnimationFunctions();
  }

  @Override
  public AnimationHandle requestAnimationFrame(AnimationCallback callback, Element element) {
    JavaScriptObject id = requestAnimationFrameImpl(callback, element, requestFunction);
    return new AnimationHandle1(id);
  }

  private native void cancel(JavaScriptObject cancelFunction, JavaScriptObject holder) /*-{
    // not calling window.cancelAnimationFrame with window as this breaks FireFox and Safari.
    cancelFunction.call($wnd, holder.id);
  }-*/;

  private native void initializeAnimationFunctions()/*-{
    var prefix = ['moz', 'ms', 'webkit'];

    // prefer the unprefixed version
    var request = $wnd.requestAnimationFrame;
    var cancel = $wnd.cancelAnimationFrame || $wnd.cancelRequestAnimationFrame;

    // try with vendor prefixes
    for (var i = 0; i < prefix.length && !request; i++) {
      var vendor = prefix[i];
      request = $wnd[vendor + 'RequestAnimationFrame'];
      cancel = $wnd[vendor + 'CancelAnimationFrame'];
      cancel = cancel || $wnd[vendor + 'CancelRequestAnimationFrame'];
    }

    // polyfill request if no implementation is present
    if (!request) {
      var lastTime = 0;
      request = function(callback, element) {
        var currTime = +new Date();
        var timeToCall = Math.max(0, 16 - (currTime - lastTime));
        lastTime = currTime + timeToCall;
        return $wnd.setTimeout(function() {
          callback(currTime + timeToCall);
        }, timeToCall);
      };
    }

    // polyfill cancel if no implementation is present
    if (!cancel) {
      cancel = function(id){
        $wnd.clearTimeout(id);
      };
    }

    this.@com.google.gwt.animation.client.AnimationSchedulerImpl::requestFunction = request;
    this.@com.google.gwt.animation.client.AnimationSchedulerImpl::cancelFunction = cancel;
  }-*/;

  private native JavaScriptObject requestAnimationFrameImpl(AnimationCallback callback, Element element, JavaScriptObject requestFunction) /*-{
    var wrapper = $entry(function(now) {
      // modern browsers call the animate function with a high res timer
      // emulate for older browsers
      if(!now) {
        var now = +new Date();
      }
      callback.@com.google.gwt.animation.client.AnimationScheduler.AnimationCallback::execute(D)(now);
    });

    // not calling window.requestAnimationFrame with window as this breaks FireFox and Safari.
    var id = requestFunction.call($wnd, wrapper, element);

    // some platforms seem to return numbers, others opaque handles
    // just to be safe wrap the result in a JSO.
    return {id: id};
  }-*/;
}
