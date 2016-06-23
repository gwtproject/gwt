/*
 * Copyright 2015 Google Inc.
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
package javaemul.internal;

/**
 * Provides an interface for simple JavaScript idioms that can not be expressed in Java.
 */
public class JsUtils {

  public static native int compare(String a, String b) /*-{
    return a == b ? 0 : (a < b ? -1 : 1);
  }-*/;

  public static native boolean isFinite(double d) /*-{
    return isFinite(d);
  }-*/;

  public static native boolean isNaN(double d) /*-{
    return isNaN(d);
  }-*/;

  public static native int parseInt(String s, int radix) /*-{
    return parseInt(s, radix);
  }-*/;

  public static native boolean isUndefined(Object value) /*-{
    return value === undefined;
  }-*/;

  public static native String unsafeCastToString(Object string) /*-{
   return string;
  }-*/;

  public static native double unsafeCastToDouble(Object number) /*-{
   return number;
  }-*/;

  public static native boolean unsafeCastToBoolean(Object bool) /*-{
   return bool;
  }-*/;

  public static native <T> T getProperty(Object map, String key) /*-{
    return map[key];
  }-*/;

  public static native void setPropertySafe(Object map, String key, Object value) /*-{
    try {
      // This may throw exception in strict mode.
      map[key] = value;
    } catch(ignored) { }
  }-*/;

  public static native int getIntProperty(Object map, String key) /*-{
    return map[key];
  }-*/;

  public static native void setIntProperty(Object map, String key, int value) /*-{
    map[key] = value;
  }-*/;

  public static native String typeOf(Object o) /*-{
    return typeof o;
  }-*/;

  public static native boolean hasComparableTypeMarker(Object o) /*-{
    return o.$implements__java_lang_Comparable;
  }-*/;

  public static native boolean hasCharSequenceTypeMarker(Object o) /*-{
    return o.$implements__java_lang_CharSequence;
  }-*/;
}

