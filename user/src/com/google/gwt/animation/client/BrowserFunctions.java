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

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;

/**
 * Returns the appropriate browser-specific JavaScript functions depending on configuration.
 */
class BrowserFunctions {
  private final Config config;

  BrowserFunctions() {
    config = GWT.create(Config.class);
  }

  /**
   * Searches for a requestAnimationFrame function, based on configuration.
   * Returns null if none was found.
   */
  JavaScriptObject getRequestAnimationFrame() {
    if (config.tryUnprefixedApi()) {
      JavaScriptObject result = getUnprefixedRequestAnimationFrame();
      if (result != null) {
        return result;
      }
    }

    if (config.tryPrefixedApi()) {
      return getPrefixedRequestAnimationFrame();
    }

    return null;
  }

  protected JavaScriptObject getPrefixedRequestAnimationFrame() {
    return null;
  }

  private native JavaScriptObject getUnprefixedRequestAnimationFrame() /*-{
    return $wnd.requestAnimationFrame;
  }-*/;
}
