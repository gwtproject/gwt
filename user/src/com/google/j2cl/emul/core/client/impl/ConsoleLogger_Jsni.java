/*
 * Copyright 2015 Google Inc.
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
package com.google.j2cl.emul.core.client.impl;

/**
 * A helper to print log messages to console.
 * <p> Note that, this is not a public API and can change/disappear in any release.
 */
public class ConsoleLogger_Jsni {

  static native boolean isSupported() /*-{
    return !!window.console;
  }-*/;

  static native void log(String level, String message) /*-{
    console[level](message);
  }-*/;

  static native void groupStart(String msg, boolean expanded) /*-{
    // Not all browsers support grouping:
    var groupStart = (!expanded && console.groupCollapsed) || console.group || console.log;
    groupStart.call(console, msg);
  }-*/;

  static native void groupEnd() /*-{
    var groupEnd = console.groupEnd || function(){};
    groupEnd.call(console);
  }-*/;

  static native String getBackingError(Throwable t) /*-{
    // Converts CollectorLegacy (IE8/IE9/Safari5) function stack to something readable.
    function stringify(fnStack) {
      if (!fnStack || fnStack.length == 0) {
        return "";
      }
      return "\t" + fnStack.join("\n\t");
    }
    var backingError = t.__gwt$backingJsError;
    return backingError && (backingError.stack || stringify(backingError.fnStack));
  }-*/;
}
