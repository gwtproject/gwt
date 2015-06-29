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
package com.google.j2cl.emul.core.client;

/**
 * A factory to create instances of {@link JsDate}.
 */
public class JsDateFactory {
  /**
   * Creates a new date with the current time.
   */
  public static native JsDate create() /*-{
    return new Date();
  }-*/;

  /**
   * Creates a new date with the specified internal representation, which is the
   * number of milliseconds since midnight on January 1st, 1970. This is the
   * same representation returned by {@link #getTime()}.
   */
  public static native JsDate create(double milliseconds) /*-{
    return new Date(milliseconds);
  }-*/;

  /**
   * Creates a new date using the specified values.
   */
  public static native JsDate create(int year, int month) /*-{
    return new Date(year, month);
  }-*/;

  /**
   * Creates a new date using the specified values.
   */
  public static native JsDate create(int year, int month, int dayOfMonth) /*-{
    return new Date(year, month, dayOfMonth);
  }-*/;

  /**
   * Creates a new date using the specified values.
   */
  public static native JsDate create(int year, int month, int dayOfMonth, int hours) /*-{
    return new Date(year, month, dayOfMonth, hours);
  }-*/;

  /**
   * Creates a new date using the specified values.
   */
  public static native JsDate create(int year, int month, int dayOfMonth, int hours,
      int minutes) /*-{
    return new Date(year, month, dayOfMonth, hours, minutes);
  }-*/;

  /**
   * Creates a new date using the specified values.
   */
  public static native JsDate create(int year, int month, int dayOfMonth, int hours,
      int minutes, int seconds) /*-{
    return new Date(year, month, dayOfMonth, hours, minutes, seconds);
  }-*/;

  /**
   * Creates a new date using the specified values.
   */
  public static native JsDate create(int year, int month, int dayOfMonth, int hours,
      int minutes, int seconds, int millis) /*-{
    return new Date(year, month, dayOfMonth, hours, minutes, seconds, millis);
  }-*/;

  /**
   * Creates a new date from a string to be parsed.
   */
  public static native JsDate create(String dateString) /*-{
    return new Date(dateString);
  }-*/;

  /**
   * Returns the numeric value corresponding to the current time -
   * the number of milliseconds elapsed since 1 January 1970 00:00:00 UTC.
   */
  public static native double now() /*-{
      // IE8 does not have Date.now
      // when removing IE8 support we change this to Date.now()
      if (Date.now) {
          // Date.now vs Date.getTime() performance comparison:
          // http://jsperf.com/date-now-vs-new-date/8
          return Date.now();
      }
      return (new Date()).getTime();
  }-*/;

  /**
   * Parses a string representation of a date and time and returns the internal
   * millisecond representation. If the string cannot be parsed, the returned
   * value will be <code>NaN</code>. Use {@link Double#isNaN(double)} to check
   * the result.
   */
  public static native double parse(String dateString) /*-{
    return Date.parse(dateString);
  }-*/;

  // CHECKSTYLE_OFF: Matching the spec.
  /**
   * Returns the internal millisecond representation of the specified UTC date
   * and time.
   */
  public static native double UTC(int year, int month, int dayOfMonth, int hours,
      int minutes, int seconds, int millis) /*-{
    return Date.UTC(year, month, dayOfMonth, hours, minutes, seconds, millis);
  }-*/;

  // CHECKSTYLE_ON

}

