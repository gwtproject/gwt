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

import elemental.json.JsonType;
import elemental.json.JsonValue;

/**
 * JSO backed implementation of JsonValue.
 */
public class JsJsonValue extends JavaScriptObject implements JsonValue {

  static native String getJsType(Object obj) /*-{
    return typeof obj;
  }-*/;

  static native boolean isArray(Object obj) /*-{
    // ensure that array detection works cross-frame
    return Object.prototype.toString.call(obj) == '[object Array]';
  }-*/;

  private static native boolean isNull(JsJsonValue jsJsonValue) /*-{
    // TODO(cromwellian): if this moves to GWT, we may have to support more leniency
    return jsJsonValue === null;
  }-*/;

  protected JsJsonValue() {
  }

  @Override
  final public native boolean asBoolean() /*-{
    var value = @com.google.gwt.core.client.GWT::isScript()() || this == null ? this : this.valueOf();
    return !!value;
  }-*/;

  @Override
  final public native double asNumber() /*-{
    var value = @com.google.gwt.core.client.GWT::isScript()() || this == null ? this : this.valueOf();
    return value == null ? 0 : +value;
  }-*/;

  @Override
  // avoid casts, as compiler will throw CCE trying to cast a raw JS String to an interface
  final public native String asString() /*-{
    return this == null ? null : "" + this;
  }-*/;

  @Override
  final public JsonType getType() {
    if (isNull(this)) {
      return JsonType.NULL;
    }
    String jsType = getJsType(this);
    if ("string".equals(jsType)) {
      return JsonType.STRING;
    }
    if ("number".equals(jsType)) {
      return JsonType.NUMBER;
    }
    if ("boolean".equals(jsType)) {
      return JsonType.BOOLEAN;
    }
    if ("object".equals(jsType)) {
      return isArray(this) ? JsonType.ARRAY : JsonType.OBJECT;
    }
    assert false : "Unknown Json Type";
    return null;
  }

  @Override
  final public native boolean jsEquals(JsonValue value) /*-{
    return this === value;
  }-*/;

  @Override
  final public native String toJson() /*-{
    // skip hashCode field
    return $wnd.JSON.stringify(this, function(keyName, value) {
      if (keyName != "$H") {
        return value;
      }
      // skip hashCode property and return undefined
    });
  }-*/;

  @Override
  final public native Object toNative() /*-{
    return this;
  }-*/;
}
