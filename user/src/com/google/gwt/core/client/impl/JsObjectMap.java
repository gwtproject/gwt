/*
 * Copyright 2014 Google Inc.
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
package com.google.gwt.core.client.impl;

import com.google.gwt.core.client.JavaScriptObject;

/**
 * A simple wrapper around JavaScriptObject to provide {@link java.util.Map}-like semantics.
 *
 * <p>Note: This class is experimental and be changed or removed at any time.
 *
 * @param <T> the type of mapped values
 */
public final class JsObjectMap<T> extends JavaScriptObject implements InternalJsMap<T> {

  public static native <T> JsObjectMap<T> create() /*-{
    return {};
  }-*/;

  protected JsObjectMap() {
  }

  public native T get(String key) /*-{
    return this[':' + key];
  }-*/;

  public native void set(String key, T value) /*-{
    return this[':' + key] = value;
  }-*/;

  public native void remove(String key) /*-{
    delete this[':' + key];
  }-*/;

  public native boolean contains(String key) /*-{
    return (':' + key) in this;
  }-*/;

  public native String[] keys() /*-{
    var key, keys = [];
    for (key in this) {
      // only keys that start with a colon ':' count
      if (key.charCodeAt(0) == 58) {
        key = key.substring(1);
        keys.push(key);
      }
    }
    return keys;
  }-*/;
}
