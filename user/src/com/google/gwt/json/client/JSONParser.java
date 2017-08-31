/*
 * Copyright 2008 Google Inc.
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
package com.google.gwt.json.client;

import com.google.gwt.core.client.JavaScriptException;
import com.google.gwt.core.client.JavaScriptObject;

/**
 * Parses the string representation of a JSON object into a set of
 * JSONValue-derived objects.
 * 
 * @see com.google.gwt.json.client.JSONValue
 */
public class JSONParser {

  static final JavaScriptObject typeMap = initTypeMap();

  /**
   * Evaluates a JSON string and returns its JSONValue representation. The
   * browser's {@code JSON.parse function} is used.
   *
   * @param jsonString a JSON object to parse
   * @return a JSONValue that has been built by parsing the JSON string
   * @throws NullPointerException if <code>jsonString</code> is <code>null</code>
   * @throws IllegalArgumentException if <code>jsonString</code> is empty
   */
  public static JSONValue parseStrict(String jsonString) {
    if (jsonString == null) {
      throw new NullPointerException();
    }
    if (jsonString.length() == 0) {
      throw new IllegalArgumentException("empty argument");
    }
    try {
      return evaluate(jsonString);
    } catch (JavaScriptException ex) {
      throw new JSONException(ex);
    }
  }
  
  static void throwJSONException(String message) {
    throw new JSONException(message);
  }

  static void throwUnknownTypeException(String typeString) {
    throw new JSONException("Unexpected typeof result '" + typeString
        + "'; please report this bug to the GWT team");
  }

  /**
   * Called from {@link #initTypeMap()}.
   */
  private static JSONValue createBoolean(boolean v) {
    return JSONBoolean.getInstance(v);
  }

  /**
   * Called from {@link #initTypeMap()}.
   */
  private static JSONValue createNumber(double v) {
    return new JSONNumber(v);
  }

  /**
   * Called from {@link #initTypeMap()}. If we get here, <code>o</code> is
   * either <code>null</code> (not <code>undefined</code>) or a JavaScript
   * object.
   */
  private static native JSONValue createObject(Object o) /*-{
    if (!o) {
      return @com.google.gwt.json.client.JSONNull::getInstance()();
    }
    var v = o.valueOf ? o.valueOf() : o;
    if (v !== o) {
      // It was a primitive wrapper, unwrap it and try again.
      var func = @com.google.gwt.json.client.JSONParser::typeMap[typeof v];
      return func ? func(v) : @com.google.gwt.json.client.JSONParser::throwUnknownTypeException(Ljava/lang/String;)(typeof v);
    } else if (o instanceof Array || o instanceof $wnd.Array) {
      // Looks like an Array; wrap as JSONArray.
      // NOTE: this test can fail for objects coming from a different window,
      // but we know of no reliable tests to determine if something is an Array
      // in all cases.
      return @com.google.gwt.json.client.JSONArray::new(Lcom/google/gwt/core/client/JavaScriptObject;)(o);
    } else {
      // This is a basic JavaScript object; wrap as JSONObject.
      // Subobjects will be created on demand.
      return @com.google.gwt.json.client.JSONObject::new(Lcom/google/gwt/core/client/JavaScriptObject;)(o);
    }
  }-*/;

  /**
   * Called from {@link #initTypeMap()}.
   */
  private static JSONValue createString(String v) {
    return new JSONString(v);
  }

  /**
   * Called from {@link #initTypeMap()}. This method returns a <code>null</code>
   * pointer, representing JavaScript <code>undefined</code>.
   */
  private static JSONValue createUndefined() {
    return null;
  }

  /**
   * This method converts <code>jsonString</code> into a JSONValue.
   * One of two code paths is taken:
   * 1) Call JSON.parse, or
   * 2) Validate the input and call eval()
   */
  private static native JSONValue evaluate(String json) /*-{
    var v;
    try {
      v = JSON.parse(json);
    } catch (e) {
      return @com.google.gwt.json.client.JSONParser::throwJSONException(Ljava/lang/String;)("Error parsing JSON: " + e);
    }
    var func = @com.google.gwt.json.client.JSONParser::typeMap[typeof v];
    return func ? func(v) : @com.google.gwt.json.client.JSONParser::throwUnknownTypeException(Ljava/lang/String;)(typeof v);
  }-*/;

  private static native JavaScriptObject initTypeMap() /*-{
    return {
      "boolean": @com.google.gwt.json.client.JSONParser::createBoolean(Z),
      "number": @com.google.gwt.json.client.JSONParser::createNumber(D),
      "string": @com.google.gwt.json.client.JSONParser::createString(Ljava/lang/String;),
      "object": @com.google.gwt.json.client.JSONParser::createObject(Ljava/lang/Object;),
      "function": @com.google.gwt.json.client.JSONParser::createObject(Ljava/lang/Object;),
      "undefined": @com.google.gwt.json.client.JSONParser::createUndefined(),
    }
  }-*/;

  /**
   * Not instantiable.
   */
  private JSONParser() {
  }
}
