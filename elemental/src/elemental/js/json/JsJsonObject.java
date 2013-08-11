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
package elemental.js.json;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArrayString;

import elemental.json.JsonArray;
import elemental.json.JsonObject;
import elemental.json.JsonValue;

/**
 * Client-side implementation of JsonObject interface.
 */
final public class JsJsonObject extends JsJsonValue
    implements JsonObject {

  public static JsonObject create() {
    return (JsonObject) JavaScriptObject.createObject();
  }

  private static native String[] reinterpretCast(JsArrayString arrayString) /*-{
    return arrayString;
  }-*/;

  protected JsJsonObject() {
  }

  @Override
  @SuppressWarnings({"unchecked"})
  public final native JsonValue get(String key) /*-{
    var value = this[key];
    return @com.google.gwt.core.client.GWT::isScript()() || value == null ?
      value : Object(value);
  }-*/;

  @Override
  public JsonArray getArray(String key) {
    return (JsonArray) get(key);
  }

  @Override
  public native boolean getBoolean(String key) /*-{
    return this[key];
  }-*/;

  @Override
  public native double getNumber(String key) /*-{
    return this[key];
  }-*/;

  @Override
  public JsonObject getObject(String key) {
    return (JsonObject) get(key);
  }

  @Override
  public native String getString(String key) /*-{
    return this[key];
  }-*/;

  @Override
  public native boolean hasKey(String key) /*-{
    return key in this;
  }-*/;

  @Override
  public String[] keys() {
    JsArrayString keys = keys0();
    if (GWT.isScript()) {
      return reinterpretCast(keys);
    }
    String[] dest = new String[keys.length()];
    for (int i = 0; i < keys.length(); i++) {
      dest[i] = keys.get(i);
    }
    return dest;
  }

  @Override
  public native void put(String key, JsonValue value) /*-{
    this[key] = @com.google.gwt.core.client.GWT::isScript()() || value == null ?
      value : value.valueOf();
  }-*/;

  @Override
  public native void put(String key, String value)  /*-{
    this[key] = value;
  }-*/;

  @Override
  public native void put(String key, double value) /*-{
    this[key] = value;
  }-*/;

  @Override
  public native void put(String key, boolean value) /*-{
    this[key] = value;
  }-*/;

  @Override
  public native void remove(String key) /*-{
    delete this[key];
  }-*/;

  private native JsArrayString keys0() /*-{
    var keys = [],
      key;
    for(key in this) {
      if (this.hasOwnProperty(key) && key != '$H') {
        keys.push(key);
      }
    }
    return keys;
  }-*/;
}
