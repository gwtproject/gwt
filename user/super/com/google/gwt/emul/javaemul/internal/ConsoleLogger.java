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
package javaemul.internal;

import jsinterop.annotations.JsProperty;
import jsinterop.annotations.JsType;

/**
 * A helper to print log messages to console.
 * <p> Note that, this is not a public API and can change/disappear in any release.
 */
public class ConsoleLogger {
  public static ConsoleLogger createIfSupported() {
    return getConsole() != null ? new ConsoleLogger() : null;
  }

  public void log(String level, String message) {
    LogFn logFn = JsUtils.getProperty(getConsole(), level);
    logFn.call(getConsole(), message);
  }

  public void log(String level, Throwable t) {
    log(level, t, "Exception: ", true);
  }

  private void log(String level, Throwable t, String label, boolean expanded) {
    groupStart(label + t.toString(), expanded);
    log(level, getBackingError(t, t.getBackingJsObject()));
    Throwable cause = t.getCause();
    if (cause != null) {
      log(level, cause, "Caused by: ", false);
    }
    for (Throwable suppressed : t.getSuppressed()) {
      log(level, suppressed, "Suppressed: ", false);
    }
    groupEnd();
  }

  private void groupStart(String msg, boolean expanded) {
    getGroupStartFn(expanded).call(getConsole(), msg);
  }

  private LogFn getGroupStartFn(boolean expanded) {
    // Not all browsers support grouping:
    if (!expanded && getConsole().groupCollapsed != null) {
      return getConsole().groupCollapsed;
    } else if (getConsole().group != null) {
      return getConsole().group;
    } else {
      return getConsole().log;
    }
  }

  private void groupEnd() {
    LogFn groupEndFn = getGroupEndFn();
    groupEndFn.call(getConsole(), "");
  }

  private LogFn getGroupEndFn() {
    if (getConsole().groupEnd != null) {
      return getConsole().groupEnd;
    } else {
      return (c, msg) ->  { };
    }
  }

  private native String getBackingError(Throwable t, Object backingError) /*-{
    // Converts CollectorLegacy (IE8/IE9/Safari5) function stack to something readable.
    function stringify(fnStack) {
      if (!fnStack || fnStack.length == 0) {
        return "";
      }
      return "\t" + fnStack.join("\n\t");
    }

    return backingError && (backingError.stack || stringify(t["fnStack"]));
  }-*/;

  @JsType(isNative = true, namespace = "<window>", name = "Function")
  private interface LogFn {
    void call(Console objThis, String message);
  }

  @JsType(isNative = true, namespace = "<window>")
  private static class Console {
    public LogFn log;
    public LogFn group;
    public LogFn groupCollapsed;
    public LogFn groupEnd;
  }

  @JsProperty(namespace = "<window>")
  private static native Console getConsole();
}
