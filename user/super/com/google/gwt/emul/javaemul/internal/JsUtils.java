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

import javaemul.internal.annotations.DoNotAutobox;
import javaemul.internal.annotations.UncheckedCast;
import jsinterop.annotations.JsMethod;
import jsinterop.annotations.JsType;

/** Provides an interface for simple JavaScript idioms that can not be expressed in Java. */
@SuppressWarnings("unusable-by-js")
public final class JsUtils {

  @JsMethod(namespace = "<window>", name = "Date.now")
  public static native double getTime();

  @JsMethod(namespace = "<window>")
  public static native int parseInt(String s, int radix);

  @JsMethod(namespace = "<window>")
  public static native double parseFloat(String str);

  @JsMethod(namespace = "<window>", name = "typeof")
  public static native String typeOf(Object obj);

  public static String toPrecision(double value, int precision) {
    NativeNumber number = JsUtils.uncheckedCast(value);
    return number.toPrecision(precision);
  }

  public static String intToString(int value, int radix) {
    return numberToString(value, radix);
  }

  public static String uintToString(int value, int radix) {
    return numberToString(toDoubleFromUnsignedInt(value), radix);
  }

  @JsMethod
  public static native int toDoubleFromUnsignedInt(int value) /*-{
    return value >>> 0;
  }-*/;

  private static String numberToString(double value, int radix) {
    NativeNumber number = JsUtils.uncheckedCast(value);
    return number.toString(radix);
  }

  @JsType(isNative = true, name = "Number", namespace = "<window>")
  private interface NativeNumber {
    String toString(int radix);

    String toPrecision(int precision);
  }

  public static native boolean isUndefined(Object value) /*-{
    return value === undefined;
  }-*/;

  public static native double unsafeCastToDouble(Object number) /*-{
   return number;
  }-*/;

  public static native boolean unsafeCastToBoolean(Object bool) /*-{
   return bool;
  }-*/;

  @UncheckedCast
  public static native <T> T uncheckedCast(@DoNotAutobox Object o) /*-{
    return o;
  }-*/;

  @UncheckedCast
  public static native <T> T getProperty(Object map, String key) /*-{
    return map[key];
  }-*/;
}

