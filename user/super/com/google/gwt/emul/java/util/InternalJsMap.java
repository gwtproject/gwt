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
final class InternalJsMap<V> {

  interface JsMap {
    JavaScriptObject createMap();

    Object get(JavaScriptObject map, String key);

    void set(JavaScriptObject map, String key, Object value);

    void remove(JavaScriptObject map, String key);

    boolean contains(JavaScriptObject map, String key);

    String[] keys(JavaScriptObject map);
  }

  private static class JsEs5ObjectMapImpl implements JsMap {

    @Override
    public native JavaScriptObject createMap() /*-{
      return Object.create(null);
    }-*/;

    @Override
    public native Object get(JavaScriptObject map, String key) /*-{
      return map[key];
    }-*/;

    @Override
    public native void set(JavaScriptObject map, String key, Object value) /*-{
      return map[key] = value;
    }-*/;

    @Override
    public native void remove(JavaScriptObject map, String key) /*-{
      delete map[key];
    }-*/;

    @Override
    public native boolean contains(JavaScriptObject map, String key) /*-{
      return key in map;
    }-*/;

    @Override
    public native String[] keys(JavaScriptObject map) /*-{
      return Object.keys(map);
    }-*/;
  }

  private static class JsObjectMapImpl implements JsMap {

    @Override
    public native JavaScriptObject createMap() /*-{
      return {};
    }-*/;

    @Override
    public native Object get(JavaScriptObject map, String key) /*-{
      return this[':' + key];
    }-*/;

    @Override
    public native void set(JavaScriptObject map, String key, Object value) /*-{
      return this[':' + key] = value;
    }-*/;

    @Override
    public native void remove(JavaScriptObject map, String key) /*-{
      delete this[':' + key];
    }-*/;

    @Override
    public native boolean contains(JavaScriptObject map, String key) /*-{
      return (':' + key) in this;
    }-*/;

    @Override
    public native String[] keys(JavaScriptObject map) /*-{
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

  private static final JsMap IMPL = createImpl();

  private static JsMap createImpl() {
    return isEs5ObjectSupported() ? new JsEs5ObjectMapImpl() : new JsObjectMapImpl();
  }

  private static native boolean isEs5ObjectSupported() /*-{
    return !!Object.create && !!Object.keys;
  }-*/;

  private final JavaScriptObject map = IMPL.createMap();

  @SuppressWarnings("unchecked")
  public V get(String key) {
    return (V) IMPL.get(map, key);
  }

  public void set(String key, V value) {
    IMPL.set(map, key, value);
  }

  public void remove(String key) {
    IMPL.remove(map, key);
  }

  public boolean contains(String key) {
    return IMPL.contains(map, key);
  }

  public String[] keys() {
    return IMPL.keys(map);
  }
}
