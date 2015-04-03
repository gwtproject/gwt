/*
 * Copyright 2007 Google Inc.
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
package java.util;

import com.google.gwt.core.client.JsDate;

import java.io.Serializable;

/**
 * Represents a date and time.
 */
public class Date implements Cloneable, Comparable<Date>, Serializable {

  /**
   * Encapsulates static data to avoid Date itself having a static initializer.
   */
  private static class StringData {
    public static final String[] DAYS = {
        "Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};

    public static final String[] MONTHS = {
        "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct",
        "Nov", "Dec"};
  }

  public static long parse(String s) {
    double parsed = JsDate.parse(s);
    if (Double.isNaN(parsed)) {
      throw new IllegalArgumentException();
    }
    return (long) parsed;
  }

  // CHECKSTYLE_OFF: Matching the spec.
  public static long UTC(int year, int month, int date, int hrs, int min,
      int sec) {
    return (long) JsDate.UTC(year + 1900, month, date, hrs, min, sec, 0);
  }

  // CHECKSTYLE_ON

  /**
   * Ensure a number is displayed with two digits.
   *
   * @return a two-character base 10 representation of the number
   */
  protected static String padTwo(int number) {
    if (number < 10) {
      return "0" + number;
    } else {
      return String.valueOf(number);
    }
  }

  /**
   * Package private factory for JSNI use, to allow cheap creation of dates from
   * doubles.
   */
  static Date createFrom(double milliseconds) {
    return new Date(milliseconds, false);
  }

  /**
   * JavaScript Date instance.
   */
  private final JsDate jsdate;

  public Date() {
    jsdate = JsDate.create();
  }

  public Date(int year, int month, int date) {
    this(year, month, date, 0, 0, 0);
  }

  public Date(int year, int month, int date, int hrs, int min) {
    this(year, month, date, hrs, min, 0);
  }

  public Date(int year, int month, int date, int hrs, int min, int sec) {
    jsdate = JsDate.create();
    jsdate.setFullYear(year + 1900, month, date);
    jsdate.setHours(hrs, min, sec, 0);
    fixDaylightSavings(hrs);
  }

  public Date(long date) {
    jsdate = JsDate.create(date);
  }

  public Date(String date) {
    this(Date.parse(date));
  }

  /**
   * For use by {@link #createFrom(double)}, should inline away.
   */
  Date(double milliseconds, boolean dummyArgForOverloadResolution) {
    jsdate = JsDate.create(milliseconds);
  }

  public boolean after(Date when) {
    return getTime() > when.getTime();
  }

  public boolean before(Date when) {
    return getTime() < when.getTime();
  }

  public Object clone() {
    return new Date(getTime());
  }

  public int compareTo(Date other) {
    return Long.compare(getTime(), other.getTime());
  }

  @Override
  public boolean equals(Object obj) {
    return ((obj instanceof Date) && (getTime() == ((Date) obj).getTime()));
  }

  public int getDate() {
    return jsdate.getDate();
  }

  public int getDay() {
    return jsdate.getDay();
  }

  public int getHours() {
    return jsdate.getHours();
  }

  public int getMinutes() {
    return jsdate.getMinutes();
  }

  public int getMonth() {
    return jsdate.getMonth();
  }

  public int getSeconds() {
    return jsdate.getSeconds();
  }

  public long getTime() {
    return (long) jsdate.getTime();
  }

  public int getTimezoneOffset() {
    return jsdate.getTimezoneOffset();
  }

  public int getYear() {
    return jsdate.getFullYear() - 1900;
  }

  @Override
  public int hashCode() {
    long time = getTime();
    return (int) (time ^ (time >>> 32));
  }

  public void setDate(int date) {
    int hours = jsdate.getHours();
    jsdate.setDate(date);
    fixDaylightSavings(hours);
  }

  public void setHours(int hours) {
    jsdate.setHours(hours);
    fixDaylightSavings(hours);
  }

  public void setMinutes(int minutes) {
    int hours = getHours() + minutes / 60;
    jsdate.setMinutes(minutes);
    fixDaylightSavings(hours);
  }

  public void setMonth(int month) {
    int hours = jsdate.getHours();
    jsdate.setMonth(month);
    fixDaylightSavings(hours);
  }

  public void setSeconds(int seconds) {
    int hours = getHours() + seconds / (60 * 60);
    jsdate.setSeconds(seconds);
    fixDaylightSavings(hours);
  }

  public void setTime(long time) {
    jsdate.setTime(time);
  }

  public void setYear(int year) {
    int hours = jsdate.getHours();
    jsdate.setFullYear(year + 1900);
    fixDaylightSavings(hours);
  }

  public String toGMTString() {
    return jsdate.getUTCDate() + " " + StringData.MONTHS[jsdate.getUTCMonth()]
        + " " + jsdate.getUTCFullYear() + " " + padTwo(jsdate.getUTCHours())
        + ":" + padTwo(jsdate.getUTCMinutes()) + ":"
        + padTwo(jsdate.getUTCSeconds()) + " GMT";
  }

  public String toLocaleString() {
    return jsdate.toLocaleString();
  }

  @Override
  public String toString() {
    // Compute timezone offset. The value that getTimezoneOffset returns is
    // backwards for the transformation that we want.
    int offset = -jsdate.getTimezoneOffset();
    String hourOffset = ((offset >= 0) ? "+" : "") + (offset / 60);
    String minuteOffset = padTwo(Math.abs(offset) % 60);

    return StringData.DAYS[jsdate.getDay()] + " "
        + StringData.MONTHS[jsdate.getMonth()] + " " + padTwo(jsdate.getDate())
        + " " + padTwo(jsdate.getHours()) + ":" + padTwo(jsdate.getMinutes())
        + ":" + padTwo(jsdate.getSeconds()) + " GMT" + hourOffset
        + minuteOffset + " " + jsdate.getFullYear();
  }

  private static final long ONE_HOUR_IN_MILLISECONDS = 60 * 60 * 1000;

  /*
   * Some browsers have the following behavior:
   *
   * GAP
   * // Assume a U.S. time zone with daylight savings
   * // Set a non-existent time: 2:00 am Sunday March 8, 2009
   * var date = new Date(2009, 2, 8, 2, 0, 0);
   * var hours = date.getHours(); // returns 1
   *
   * The equivalent Java code will return 3.
   *
   * OVERLAP
   * // Assume a U.S. time zone with daylight savings
   * // Set to an ambiguous time: 1:30 am Sunday November 1, 2009
   * var date = new Date(2009, 10, 1, 1, 30, 0);
   * var nextHour = new Date(date.getTime() + 60*60*1000);
   * var hours = nextHour.getHours(); // returns 1
   *
   * The equivalent Java code will return 2.
   *
   * To compensate, fixDaylightSavings adjusts the date to match Java semantics.
   */

  /**
   * Detects if the requested time falls into a non-existent time range due to local time advancing
   * into daylight savings time or is ambiguous due to going out of daylight savings. If so, adjust
   * accordingly.
   */
  private void fixDaylightSavings(int requestedHours) {
    requestedHours %= 24;
    if (jsdate.getHours() != requestedHours) {
      // Hours passed to the constructor don't match the hours in the created JavaScript Date; this
      // might be due either because they are outside 0-24 range, there was overflow from
      // minutes:secs:millis or because we are in the situation GAP and has to be fixed.
      JsDate copy = JsDate.create(jsdate.getTime());
      copy.setDate(copy.getDate() + 1);
      int timeDiff = jsdate.getTimezoneOffset() - copy.getTimezoneOffset();

      // If the time zone offset is changing, advance the hours and
      // minutes from the initially requested time by the change amount
      if (timeDiff > 0) {
        // The requested time falls into a non-existent time range due to
        // local time advancing into daylight savings time. If so, push the requested
        // time forward out of the non-existent range.
        int timeDiffHours = timeDiff / 60;
        int timeDiffMinutes = timeDiff % 60;
        int day = jsdate.getDate();
        int badHours = jsdate.getHours();
        if (badHours + timeDiffHours >= 24) {
          day++;
        }
        JsDate newTime = JsDate.create(jsdate.getFullYear(), jsdate.getMonth(),
            day, requestedHours + timeDiffHours, jsdate.getMinutes() + timeDiffMinutes,
            jsdate.getSeconds(), jsdate.getMilliseconds());
        jsdate.setTime(newTime.getTime());
      }
    }

    // Check for situation OVERLAP by advancing the clock by 1 hour and see if getHours() returns
    // the same. This solves issues like Safari returning '3/21/2015 23:00' when time is set to
    // '2/22/2015'.
    double originalTimeInMillis = jsdate.getTime();
    jsdate.setTime(originalTimeInMillis + ONE_HOUR_IN_MILLISECONDS);
    if (jsdate.getHours() != requestedHours) {
      // We are not in the duplicated hour, so revert the change.
      jsdate.setTime(originalTimeInMillis);
    }
  }
}
