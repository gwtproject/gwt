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
  public static native boolean isFinite(double x) /*-{
    return isFinite(x);
  }-*/;

  public static native boolean equalsInFinity(double x) /*-{
    return x === Infinity;
  }-*/;

  public static native boolean equalsMinusInFinity(double x) /*-{
    return x === -Infinity;
  }-*/;

  public static native boolean isNaN(double x) /*-{
    return isNaN(x);
  }-*/;

  public static native double parseFloat(String str) /*-{
    return parseFloat(str);
  }-*/;

  public static native int parseInt(String s, int radix) /*-{
    return parseInt(s, radix);
  }-*/;
  
  public static native boolean isUndefined(Object value) /*-{
    return value === undefined;
  }-*/;
  
  // TODO(goktug): replace unsafeCast with a real cast when the compiler can optimize it.
  public static native String unsafeCastToString(Object string) /*-{
   return string;
  }-*/;
  
  // TODO(goktug): replace unsafeCast with a real cast when the compiler can optimize it.
  public static native int unsafeCastToInt(Object o) /*-{
    return o;
  }-*/;

  // TODO(goktug): replace unsafeCast with a real cast when the compiler can optimize it.
  public static native int unsafeCastToInt(double d) /*-{
    return d;
  }-*/;

  public static native boolean isPropertyUndefined(Object map, String key) /*-{
    return map[key] === undefined;
  }-*/;

  // TODO(dankurka): this only exists because of dev mode (we can not refer to an int as Object in
  // dev mode). Delete once dev mode is gone.
  public static native int getIntPropertyFromObject(Object map, String key) /*-{
    return map[key];
  }-*/;
  
  public static native Object getPropertyFromObject(Object map, String key) /*-{
    return map[key];
  }-*/;

  public static native void setIntPropertyOnObject(Object map, String key, int value) /*-{
    map[key] = value;
  }-*/;

  public static native Object createNativeObject() /*-{
    return {};
  }-*/;
}

