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

import java.io.Serializable;

import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;

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
    double parsed = NativeDate.parse(s);
    if (Double.isNaN(parsed)) {
      throw new IllegalArgumentException();
    }
    return (long) parsed;
  }

  // CHECKSTYLE_OFF: Matching the spec.
  public static long UTC(int year, int month, int date, int hrs, int min,
      int sec) {
    return (long) NativeDate.UTC(year + 1900, month, date, hrs, min, sec, 0);
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
   * JavaScript Date instance.
   */
  private final NativeDate jsdate;

  public Date() {
    jsdate = new NativeDate();
  }

  public Date(int year, int month, int date) {
    this(year, month, date, 0, 0, 0);
  }

  public Date(int year, int month, int date, int hrs, int min) {
    this(year, month, date, hrs, min, 0);
  }

  public Date(int year, int month, int date, int hrs, int min, int sec) {
    jsdate = new NativeDate();
    jsdate.setFullYear(year + 1900, month, date);
    jsdate.setHours(hrs, min, sec, 0);
  }

  public Date(long date) {
    jsdate = new NativeDate(date);
  }

  public Date(String date) {
    this(Date.parse(date));
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

  @Override
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
  }

  public void setHours(int hours) {
    jsdate.setHours(hours);
  }

  public void setMinutes(int minutes) {
    int hours = getHours() + minutes / 60;
    jsdate.setMinutes(minutes);
  }

  public void setMonth(int month) {
    int hours = jsdate.getHours();
    jsdate.setMonth(month);
  }

  public void setSeconds(int seconds) {
    int hours = getHours() + seconds / (60 * 60);
    jsdate.setSeconds(seconds);
  }

  public void setTime(long time) {
    jsdate.setTime(time);
  }

  public void setYear(int year) {
    int hours = jsdate.getHours();
    jsdate.setFullYear(year + 1900);
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

  @JsType(isNative = true, name = "Date", namespace = JsPackage.GLOBAL)
  private static class NativeDate {
    // CHECKSTYLE_OFF: Matching the spec.
    public static native double UTC(int year, int month, int dayOfMonth, int hours,
        int minutes, int seconds, int millis);
    // CHECKSTYLE_ON
    public static native double parse(String dateString);
    public NativeDate() { }
    public NativeDate(double milliseconds) { }
    public NativeDate(int year, int month, int dayOfMonth, int hours,
        int minutes, int seconds, int millis) { }
    public native int getDate();
    public native int getDay();
    public native int getFullYear();
    public native int getHours();
    public native int getMilliseconds();
    public native int getMinutes();
    public native int getMonth();
    public native int getSeconds();
    public native double getTime();
    public native int getTimezoneOffset();
    public native int getUTCDate();
    public native int getUTCFullYear();
    public native int getUTCHours();
    public native int getUTCMinutes();
    public native int getUTCMonth();
    public native int getUTCSeconds();
    public native void setDate(int dayOfMonth);
    public native void setFullYear(int year);
    public native void setFullYear(int year, int month, int day);
    public native void setHours(int hours);
    public native void setHours(int hours, int mins, int secs, int ms);
    public native void setMinutes(int minutes);
    public native void setMonth(int month);
    public native void setSeconds(int seconds);
    public native void setTime(double milliseconds);
    public native String toLocaleString();
  }
}
