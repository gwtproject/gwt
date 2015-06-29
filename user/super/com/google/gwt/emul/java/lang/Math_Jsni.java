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
package java.lang;

/**
 * Math methods that require Jsni for GWT or super sourcing for j2cl.
 */
public class Math_Jsni {

  static native double acos(double x) /*-{
    return Math.acos(x);
  }-*/;

  static native double asin(double x) /*-{
    return Math.asin(x);
  }-*/;

  static native double atan(double x) /*-{
    return Math.atan(x);
  }-*/;

  static native double atan2(double y, double x) /*-{
    return Math.atan2(y, x);
  }-*/;

  static double cbrt(double x) {
    return Math_Jsni.pow(x, 1.0 / 3.0);
  }

  static native double ceil(double x) /*-{
    return Math.ceil(x);
  }-*/;

  static native double cos(double x) /*-{
    return Math.cos(x);
  }-*/;

  static native double cosh(double x) /*-{
    return (Math.exp(x) + Math.exp(-x)) / 2.0;
  }-*/;

  static native double exp(double x) /*-{
    return Math.exp(x);
  }-*/;

  static native double floor(double x) /*-{
    return Math.floor(x);
  }-*/;

  static double hypot(double x, double y) {
    return sqrt(x * x + y * y);
  }

  static native double log(double x) /*-{
    return Math.log(x);
  }-*/;

  static native double log10(double x) /*-{
    return Math.log(x) * Math.LOG10E;
  }-*/;

  static native double pow(double x, double exp) /*-{
    return Math.pow(x, exp);
  }-*/;

  static native double random() /*-{
    return Math.random();
  }-*/;

  static native int round(double x) /*-{
    return Math.round(x);
  }-*/;

  static native double sin(double x) /*-{
    return Math.sin(x);
  }-*/;

  static native double sinh(double x) /*-{
    return (Math.exp(x) - Math.exp(-x)) / 2.0;
  }-*/;

  static native double sqrt(double x) /*-{
    return Math.sqrt(x);
  }-*/;

  static native double tan(double x) /*-{
    return Math.tan(x);
  }-*/;

  static native double tanh(double x) /*-{
    if (x == Infinity) {
      return 1.0;
    }
    var e2x = Math.exp(2.0 * x);
    return (e2x - 1) / (e2x + 1);
  }-*/;

  static native double round0(double x) /*-{
    return Math.round(x);
  }-*/;

  static native double roundDouble(double x) /*-{
    return Math.round(x);
  }-*/;
}
