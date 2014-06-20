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
package com.google.gwt.lang;

import com.google.gwt.core.client.JavaScriptObject;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;

import javaemul.internal.EmulatedCharset;
import javaemul.internal.annotations.DoNotInline;

/**
 * This class is used to access the private, GWT-specific
 * castableTypeMap and typeMarker fields.
 */
final class Util {

  static native JavaScriptObject getCastableTypeMap(Object o) /*-{
    return o.@java.lang.Object::castableTypeMap;
  }-*/;

  static native void setTypeMarker(Object o) /*-{
      o.@java.lang.Object::typeMarker =
          @com.google.gwt.lang.JavaClassHierarchySetupUtil::typeMarkerFn(*);
  }-*/;

  static native boolean hasTypeMarker(Object o) /*-{
    return o.@java.lang.Object::typeMarker ===
        @com.google.gwt.lang.JavaClassHierarchySetupUtil::typeMarkerFn(*);
  }-*/;

  static native void setCastableTypeMap(Object o, JavaScriptObject castableTypeMap) /*-{
    o.@java.lang.Object::castableTypeMap = castableTypeMap;
  }-*/;

  @DoNotInline
  public static String makeEnumName(String enumName) {
    return enumName;
  }

  // ---- Utility Methods for unboxed String, Double, and Boolean

  static native Double $createDouble(double x) /*-{
    return x;
  }-*/;

  static Double $createDouble(String s) {
    return $createDouble(Double.parseDouble(s));
  }

  static native Boolean $createBoolean(boolean x) /*-{
    return x;
  }-*/;

  static Boolean $createBoolean(String x) {
    return $createBoolean(Boolean.parseBoolean(x));
  }

  /**
   * @skip
   */
  static String $createString() {
    return "";
  }

  /**
   * @skip
   */
  static String $createString(byte[] bytes) {
    return $createString(bytes, 0, bytes.length);
  }

  /**
   * @skip
   */
  static String $createString(byte[] bytes, int ofs, int len) {
    return $createString(bytes, ofs, len, EmulatedCharset.UTF_8);
  }

  /**
   * @skip
   */
  static String $createString(byte[] bytes, int ofs, int len, String charsetName)
      throws UnsupportedEncodingException {
    return $createString(bytes, ofs, len, String.getCharset(charsetName));
  }

  /**
   * @skip
   */
  static String $createString(byte[] bytes, int ofs, int len, Charset charset) {
    return String.valueOf(((EmulatedCharset) charset).decodeString(bytes, ofs, len));
  }

  /**
   * @skip
   */
  static String $createString(byte[] bytes, String charsetName)
      throws UnsupportedEncodingException {
    return $createString(bytes, 0, bytes.length, charsetName);
  }

  /**
   * @skip
   */
  static String $createString(byte[] bytes, Charset charset)
      throws UnsupportedEncodingException {
    return $createString(bytes, 0, bytes.length, charset.name());
  }

  /**
   * @skip
   */
  static String $createString(char value[]) {
    return String.valueOf(value);
  }

  /**
   * @skip
   */
  static String $createString(char value[], int offset, int count) {
    return String.valueOf(value, offset, count);
  }

  /**
   * @skip
   */
  static String $createString(int[] codePoints, int offset, int count) {
    char[] chars = new char[count * 2];
    int charIdx = 0;
    while (count-- > 0) {
      charIdx += Character.toChars(codePoints[offset++], chars, charIdx);
    }
    return String.valueOf(chars, 0, charIdx);
  }

  /**
   * @skip
   */
  static String $createString(String other) {
    return other;
  }

  /**
   * @skip
   */
  static String $createString(StringBuffer sb) {
    return String.valueOf(sb);
  }

  /**
   * @skip
   */
  static String $createString(StringBuilder sb) {
    return String.valueOf(sb);
  }
}
