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

import com.google.gwt.core.client.JavaScriptObject;

import elemental.js.util.JsArrayOf;
import elemental.json.JsonArray;
import elemental.json.JsonObject;
import elemental.json.JsonValue;

/**
 * Client-side implementation of JsonArray.
 */
final public class JsJsonArray extends JsJsonValue
    implements JsonArray {

  public static JsonArray create() {
    return (JsonArray) JavaScriptObject.createArray();
  }

  protected JsJsonArray() {
  }

  @Override
  @SuppressWarnings({"unchecked"})
  public final native JsonValue get(int index) /*-{
    var value = this[index];
    return @com.google.gwt.core.client.GWT::isScript()() || value == null ? value : Object(value);
  }-*/;

  @Override
  public JsonArray getArray(int index) {
    return (JsonArray) get(index);
  }

  @Override
  public native boolean getBoolean(int index) /*-{
    return this[index];
  }-*/;

  @Override
  public native double getNumber(int index) /*-{
    return this[index];
  }-*/;

  @Override
  public JsonObject getObject(int index) {
    return (JsonObject) get(index);
  }

  @Override
  public native String getString(int index) /*-{
    return this[index];
  }-*/;

  @Override
  public native int length() /*-{
    return this.length;
  }-*/;

  @Override
  public void remove(int index) {
    this.<JsArrayOf>cast().removeByIndex(index);
  }

  @Override
  public native void set(int index, JsonValue value) /*-{
    this[index] = @com.google.gwt.core.client.GWT::isScript()() || value == null ? value : value.valueOf();
  }-*/;

  @Override
  public native void set(int index, String string) /*-{
    this[index] = string;
  }-*/;

  @Override
  public native void set(int index, double number) /*-{
    this[index] = number;
  }-*/;

  @Override
  public native void set(int index, boolean bool) /*-{
    this[index] = bool;
  }-*/;
}
