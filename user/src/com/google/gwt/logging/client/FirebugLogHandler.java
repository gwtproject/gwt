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

/**
 * A Handler that prints logs to window.console which is used by Firebug.
 * <p>
 * Note we are consciously using 'window' rather than '$wnd' to avoid issues
 * similar to http://code.google.com/p/fbug/issues/detail?id=2914
 * <p>
 * Note this handle will only be used in old versions of Firebug which expose
 * their versions in 'window.console.firebug'; newer versions will use the
 * {@link ConsoleLogHandler} instead. See
 * http://code.google.com/p/fbug/issues/detail?id=4772
 * Because of this, this handle will log a warning when initialized.
 */
// TODO(t.broyer): make sure to remove the window.console.firebug test in
// ConsoleLogHandler when we remove FirebugLogHandler. It's only there to
// avoid double-logging.
@Deprecated
public class FirebugLogHandler extends ConsoleLogHandler {
  
  public FirebugLogHandler() {
    super();
    if (isSupported()) {
      warn("FirebugLogHandler is deprecated, use the ConsoleLogHandler instead.");
    }
  }

  @Override
  native void debug(String message) /*-{
    window.console.debug(message);
  }-*/;

  @Override
  native boolean isSupported() /*-{
    return !!(window.console && window.console.firebug);
  }-*/;
}
