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

import elemental.json.JsonArray;
import elemental.json.JsonBoolean;
import elemental.json.JsonException;
import elemental.json.JsonFactory;
import elemental.json.JsonNull;
import elemental.json.JsonNumber;
import elemental.json.JsonObject;
import elemental.json.JsonString;
import elemental.json.JsonValue;

/**
 * JSNI based implementation of JsonFactory.
 */
public class JsJsonFactory implements JsonFactory {

  private static native JsonValue parse0(String jsonString) /*-{
    // assume Chrome, safe and non-broken JSON.parse impl
    var value = $wnd.JSON.parse(jsonString);
    return @com.google.gwt.core.client.GWT::isScript()() || value == null ? value : Object(value)
  }-*/;

  @Override
  public JsonString create(String string) {
    return JsJsonString.create(string);
  }

  @Override
  public JsonNumber create(double number) {
    return JsJsonNumber.create(number);
  }

  @Override
  public JsonBoolean create(boolean bool) {
    return JsJsonBoolean.create(bool);
  }

  @Override
  public JsonArray createArray() {
    return JsJsonArray.create();
  }

  @Override
  public JsonNull createNull() {
    return JsJsonNull.create();
  }

  @Override
  public JsonObject createObject() {
    return JsJsonObject.create();
  }

  @Override
  @SuppressWarnings({"unchecked"})
  public JsonValue parse(String jsonString) throws JsonException {
    try {
      return parse0(jsonString);
    } catch (Exception e) {
      throw new JsonException("Can't parse " + jsonString);
    }
  }
}
