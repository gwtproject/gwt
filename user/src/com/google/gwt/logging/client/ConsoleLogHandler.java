/*
 * Copyright 2010 Google Inc.
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

package com.google.gwt.logging.client;

import com.google.web.bindery.event.shared.UmbrellaException;

import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;

/**
 * A Handler that prints logs to the window.console.
 * <p>
 * Note we are consciously using 'window' rather than '$wnd' to avoid issues
 * similar to http://code.google.com/p/fbug/issues/detail?id=2914
 */
public class ConsoleLogHandler extends Handler {

  public ConsoleLogHandler() {
    setFormatter(new TextLogFormatter(false));
    setLevel(Level.ALL);
  }

  @Override
  public void close() {
    // No action needed
  }

  @Override
  public void flush() {
    // No action needed
  }

  @Override
  public void publish(LogRecord record) {
    if (!isSupported() || !isLoggable(record)) {
      return;
    }
    String msg = getFormatter().format(record);
    int val = record.getLevel().intValue();
    if (val >= Level.SEVERE.intValue()) {
      error(msg);
    } else if (val >= Level.WARNING.intValue()) {
      warn(msg);
    } else if (val >= Level.INFO.intValue()) {
      info(msg);
    } else {
      log(msg);
    }
    Throwable e = record.getThrown();
    if (e != null) {
      logException(e, false);
    }
  }

  private void logException(Throwable t, boolean isCause) {
    String msg = t.toString();
    if (isCause) {
      msg = "caused by: " + msg;
    }
    groupStart(msg);
    log(t);
    if (t instanceof UmbrellaException) {
      UmbrellaException umbrella = (UmbrellaException) t;
      for (Throwable cause : umbrella.getCauses()) {
        logException(cause, true);
      }
    } else if (t.getCause() != null) {
      logException(t.getCause(), true);
    }
    groupEnd();
  }

  private native boolean isSupported() /*-{
    return !!window.console;
  }-*/;

  private native void error(String message) /*-{
    window.console.error(message);
  }-*/;

  private native void warn(String message) /*-{
    window.console.warn(message);
  }-*/;

  private native void info(String message) /*-{
    window.console.info(message);
  }-*/;

  private native void log(String message) /*-{
    window.console.log(message);
  }-*/;

  private native void log(Throwable t) /*-{
    var logError = console.error || console.log;
    var backingError = t.__gwt$backingJsError;
    logError.call(console, backingError && backingError.stack);
  }-*/;

  private native void groupStart(String msg) /*-{
    var groupStart = console.groupCollapsed || console.group || console.error
        || console.log;
    groupStart.call(console, msg);
  }-*/;

  private native void groupEnd() /*-{
    var groupEnd = console.groupEnd || function() {};
    groupEnd.call(console);
  }-*/;
}
