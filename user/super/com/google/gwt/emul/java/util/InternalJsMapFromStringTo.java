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
package java.util;

import com.google.gwt.core.client.JavaScriptObject;

/**
 * A simple wrapper around JavaScriptObject to provide {@link java.util.Map}-like semantics.
 *
 * <p>Note: This class is experimental and be changed or removed at any time.
 *
 */
abstract class InternalJsMapFromStringTo<V> {

  private static class JsEs5ObjectMapImpl<V> extends InternalJsMapFromStringTo<V> {
    JavaScriptObject backingMap = createMap();

    private native JavaScriptObject createMap() /*-{
      return Object.create(null);
    }-*/;

    @Override
    public native V get(String key) /*-{
      return this.@java.util.InternalJsMapFromStringTo.JsEs5ObjectMapImpl::backingMap[key];
    }-*/;

    @Override
    public native void set(String key, V value) /*-{
      this.@java.util.InternalJsMapFromStringTo.JsEs5ObjectMapImpl::backingMap[key] = value;
    }-*/;

    @Override
    public native void remove(String key) /*-{
      delete this.@java.util.InternalJsMapFromStringTo.JsEs5ObjectMapImpl::backingMap[key];
    }-*/;

    @Override
    public native boolean contains(String key) /*-{
      return key in this.@java.util.InternalJsMapFromStringTo.JsEs5ObjectMapImpl::backingMap;
    }-*/;

    @Override
    public native String[] keys() /*-{
      return Object.keys(this.@java.util.InternalJsMapFromStringTo.JsEs5ObjectMapImpl::backingMap);
    }-*/;
  }

  private static class JsObjectMapImpl<V> extends InternalJsMapFromStringTo<V> {
    JavaScriptObject backingMap = createMap();
    JavaScriptObject keys = JavaScriptObject.createArray();

    private native JavaScriptObject createMap() /*-{
      return {};
    }-*/;

    @Override
    public native V get(String key) /*-{
      return this.@java.util.InternalJsMapFromStringTo.JsObjectMapImpl::backingMap[':' + key];
    }-*/;

    @Override
    public native void set(String key, V value) /*-{
      var originalKey = key;
      var map = this.@java.util.InternalJsMapFromStringTo.JsObjectMapImpl::backingMap;
      key = ':' + key;
      if (!(key in map)) {
        this.@java.util.InternalJsMapFromStringTo.JsObjectMapImpl::keys.push(originalKey);
      }
      map[key] = value;
    }-*/;

    @Override
    public native void remove(String key) /*-{
      key = ':' + key;
      var map = this.@java.util.InternalJsMapFromStringTo.JsObjectMapImpl::backingMap;
      if (key in map) {
        this.@java.util.InternalJsMapFromStringTo.JsObjectMapImpl::keys = [];
        delete map[key];
      }
    }-*/;

    @Override
    public native boolean contains(String key) /*-{
      return (':' + key) in this.@java.util.InternalJsMapFromStringTo.JsObjectMapImpl::backingMap;
    }-*/;

    @Override
    public native String[] keys() /*-{
      var keys = this.@java.util.InternalJsMapFromStringTo.JsObjectMapImpl::keys;
      if (!keys.length) {
        for (var key in this.@java.util.InternalJsMapFromStringTo.JsObjectMapImpl::backingMap) {
          // only keys that start with a colon ':' count
          if (key.charCodeAt(0) == 58) {
            key = key.substring(1);
            keys.push(key);
          }
        }
      }
      return keys;
    }-*/;
  }

  static <V> InternalJsMapFromStringTo<V> create() {
    return isEs5ObjectSupported() ? new JsEs5ObjectMapImpl<V>() : new JsObjectMapImpl<V>();
  }

  private static native boolean isEs5ObjectSupported() /*-{
    return !!Object.create && !!Object.keys;
  }-*/;

  abstract V get(String key);

  abstract void set(String key, V value);

  abstract void remove(String key);

  abstract boolean contains(String key);

  abstract String[] keys();
}
