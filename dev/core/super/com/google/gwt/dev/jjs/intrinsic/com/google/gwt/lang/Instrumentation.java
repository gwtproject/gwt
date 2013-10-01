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
package com.google.gwt.lang;

import com.google.gwt.core.client.JavaScriptObject;

/**
 * Utility class for instrumenting runtime JS.
 */
public class Instrumentation {

  /**
   * Given a function, return a new function which wraps it and calls
   * {window.console.time} before invocation, and {window.console.timeEnd}
   * after invocation.
   */
  public static native JavaScriptObject instrument(JavaScriptObject toInstrument, String name) /*-{
    return function() {
      var profileId = "method " + name + " " + window.performance.now();
      var logger = $wnd.console;
      if (logger) {
        logger.time(profileId);
      }
      var $tmp = toInstrument.apply(this, arguments);
      if (logger) {
        logger.timeEnd(profileId);
      }
      return $tmp;
    };
  }-*/;

  public static native JavaScriptObject profileFunction(JavaScriptObject toProfile, String name) /*-{
    return @com.google.gwt.lang.Instrumentation::instrument(Lcom/google/gwt/core/client/JavaScriptObject;Ljava/lang/String;)(toProfile, name);
  }-*/;
}
