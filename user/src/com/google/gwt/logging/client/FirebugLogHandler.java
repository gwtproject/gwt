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

import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;

/**
 * A Handler that prints logs to window.console which is used by Firebug.
 * <p>
 * Note we are consciously using 'window' rather than '$wnd' to avoid issues
 * similar to http://code.google.com/p/fbug/issues/detail?id=2914
 * <p>
 * Note this handler will only be used in old versions of Firebug which expose
 * their versions in 'window.console.firebug'; newer versions will use the
 * {@link ConsoleLogHandler} instead. See
 * http://code.google.com/p/fbug/issues/detail?id=4772
 * Because of this, this handle will log a warning when initialized.
 * 
 * @deprecated use {@link ConsoleLogHandler} instead.
 */
// TODO(t.broyer): make sure to remove the window.console.firebug test in
// ConsoleLogHandler when we remove FirebugLogHandler. It's only there to
// avoid double-logging.
@Deprecated
public class FirebugLogHandler extends Handler {
  
  public FirebugLogHandler() {
    if (isSupported()) {
      warn("FirebugLogHandler is deprecated, use the ConsoleLogHandler instead.");
    }
    setFormatter(new TextLogFormatter(true));
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
    if (val <= Level.FINE.intValue()) {
      debug(msg);
    } else if (val < Level.WARNING.intValue()) {
      info(msg);
    } else if (val < Level.SEVERE.intValue()) {
      warn(msg);
    } else {
      error(msg);
    }
  }
  
  private native void debug(String message) /*-{
    window.console.debug(message);
  }-*/;

  private native void error(String message) /*-{
    window.console.error(message);
  }-*/;

  private native void info(String message) /*-{
    window.console.info(message);
  }-*/;

  private native boolean isSupported() /*-{
    return !!(window.console && window.console.firebug);
  }-*/;

  private native void warn(String message) /*-{
    window.console.warn(message);
  }-*/;

}
