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
 *  A simple wrapper interface exposing a native JS Date object.
 */
public interface JsDate {
  /**
   * Returns the day of the month.
   */
  public int getDate();

  /**
   * Returns the day of the week, from <code>0</code> (Sunday) to <code>6</code>
   * Saturday.
   */
  public int getDay();

  /**
   * Returns the four-digit year.
   */
  public int getFullYear();

  /**
   * Returns the hour, between <code>0</code> (midnight) and <code>23</code>.
   */
  public int getHours();

  /**
   * Returns the milliseconds, between <code>0</code> and <code>999</code>.
   */
  public int getMilliseconds();

  /**
   * Returns the minutes, between <code>0</code> and <code>59</code>.
   */
  public int getMinutes();

  /**
   * Returns the month, from <code>0</code> (January) to <code>11</code>
   * December.
   */
  public int getMonth();

  /**
   * Returns the seconds, between <code>0</code> and <code>59</code>.
   */
  public int getSeconds();

  /**
   * Returns the internal millisecond representation of the date, the number of
   * milliseconds since midnight on January 1st, 1970. This is the same
   * representation returned by {@link #getTime()}.
   */
  public double getTime();

  /**
   * Returns the difference, in minutes, between the local and UTC
   * representations of this date. The value returned is affected by whether or
   * not daylight savings time would be in effect on specified date.
   */
  public int getTimezoneOffset();

  /**
   * Returns the day of the month, in UTC.
   */
  public int getUTCDate();

  /**
   * Returns the day of the week, from <code>0</code> (Sunday) to <code>6</code>
   * Saturday, in UTC.
   */
  public int getUTCDay();

  /**
   * Returns the four-digit year, in UTC.
   */
  public int getUTCFullYear();

  /**
   * Returns the hour, between <code>0</code> (midnight) and <code>23</code>, in
   * UTC.
   */
  public int getUTCHours();

  /**
   * Returns the milliseconds, between <code>0</code> and <code>999</code>, in
   * UTC.
   */
  public int getUTCMilliseconds();

  /**
   * Returns the minutes, between <code>0</code> and <code>59</code>, in UTC.
   */
  public int getUTCMinutes();

  /**
   * Returns the month, from <code>0</code> (January) to <code>11</code>
   * December, in UTC.
   */
  public int getUTCMonth();

  /**
   * Returns the seconds, between <code>0</code> and <code>59</code>, in UTC.
   */
  public int getUTCSeconds();

  /**
   * Returns the year minus 1900.
   *
   * @deprecated Use {@link #getFullYear()}.
   */
  @Deprecated
  public int getYear();

  /**
   * Sets the day of the month. Returns the millisecond representation of the
   * adjusted date.
   */
  public double setDate(int dayOfMonth);

  /**
   * Sets the year. Returns the millisecond representation of the adjusted date.
   */
  public double setFullYear(int year);

  /**
   * Sets the year and month. Returns the millisecond representation of the
   * adjusted date.
   */
  public double setFullYear(int year, int month);

  /**
   * Sets the year, month, and day. Returns the millisecond representation of
   * the adjusted date.
   */
  public double setFullYear(int year, int month, int day);

  /**
   * Sets the hour. Returns the millisecond representation of the adjusted date.
   */
  public double setHours(int hours);

  /**
   * Sets the hour and minutes. Returns the millisecond representation of the
   * adjusted date.
   */
  public double setHours(int hours, int mins);

  /**
   * Sets the hour, minutes, and seconds. Returns the millisecond representation
   * of the adjusted date.
   */
  public double setHours(int hours, int mins, int secs);

  /**
   * Sets the hour, minutes, seconds, and milliseconds. Returns the millisecond
   * representation of the adjusted date.
   */
  public double setHours(int hours, int mins, int secs, int ms);

  /**
   * Sets the minutes. Returns the millisecond representation of the adjusted
   * date.
   */
  public double setMinutes(int minutes);

  /**
   * Sets the minutes and seconds. Returns the millisecond representation of the
   * adjusted date.
   */
  public double setMinutes(int minutes, int seconds);

  /**
   * Sets the minutes, seconds, and milliseconds. Returns the millisecond
   * representation of the adjusted date.
   */
  public double setMinutes(int minutes, int seconds, int millis);

  /**
   * Sets the month. Returns the millisecond representation of the adjusted
   * date.
   */
  public double setMonth(int month);

  /**
   * Sets the month and day. Returns the millisecond representation of the
   * adjusted date.
   */
  public double setMonth(int month, int dayOfMonth);

  /**
   * Sets the seconds. Returns the millisecond representation of the adjusted
   * date.
   */
  public double setSeconds(int seconds);

  /**
   * Sets the seconds and milliseconds. Returns the millisecond representation
   * of the adjusted date.
   */
  public double setSeconds(int seconds, int millis);

  /**
   * Sets the internal date representation. Returns the
   * <code>milliseconds</code> argument.
   */
  public double setTime(double milliseconds);

  /**
   * Sets the day of the month, in UTC. Returns the millisecond representation
   * of the adjusted date.
   */
  public double setUTCDate(int dayOfMonth);

  /**
   * Sets the year, in UTC. Returns the millisecond representation of the
   * adjusted date.
   */
  public double setUTCFullYear(int year);

  /**
   * Sets the year and month, in UTC. Returns the millisecond representation of
   * the adjusted date.
   */
  public double setUTCFullYear(int year, int month);

  /**
   * Sets the year, month, and day, in UTC. Returns the millisecond
   * representation of the adjusted date.
   */
  public double setUTCFullYear(int year, int month, int day);

  /**
   * Sets the hour, in UTC. Returns the millisecond representation of the
   * adjusted date.
   */
  public double setUTCHours(int hours);

  /**
   * Sets the hour and minutes, in UTC. Returns the millisecond representation
   * of the adjusted date.
   */
  public double setUTCHours(int hours, int mins);

  /**
   * Sets the hour, minutes, and seconds, in UTC. Returns the millisecond
   * representation of the adjusted date.
   */
  public double setUTCHours(int hours, int mins, int secs);

  /**
   * Sets the hour, minutes, seconds, and milliseconds, in UTC. Returns the
   * millisecond representation of the adjusted date.
   */
  public double setUTCHours(int hours, int mins, int secs, int ms);

  /**
   * Sets the minutes, in UTC. Returns the millisecond representation of the
   * adjusted date.
   */
  public double setUTCMinutes(int minutes);

  /**
   * Sets the minutes and seconds, in UTC. Returns the millisecond
   * representation of the adjusted date.
   */
  public double setUTCMinutes(int minutes, int seconds);

  /**
   * Sets the minutes, seconds, and milliseconds, in UTC. Returns the
   * millisecond representation of the adjusted date.
   */
  public double setUTCMinutes(
      int minutes, int seconds, int millis);

  /**
   * Sets the month, in UTC. Returns the millisecond representation of the
   * adjusted date.
   */
  public double setUTCMonth(int month);

  /**
   * Sets the month and day, in UTC. Returns the millisecond representation of
   * the adjusted date.
   */
  public double setUTCMonth(int month, int dayOfMonth);

  /**
   * Sets the seconds, in UTC. Returns the millisecond representation of the
   * adjusted date.
   */
  public double setUTCSeconds(int seconds);

  /**
   * Sets the seconds and milliseconds, in UTC. Returns the millisecond
   * representation of the adjusted date.
   */
  public double setUTCSeconds(int seconds, int millis);

  /**
   * Sets a two-digit year.
   *
   * @deprecated Use {@link #setFullYear(int)}.
   */
  @Deprecated
  public double setYear(int year);

  /**
   * Returns a date string in the local time zone.
   */
  public String toDateString();

  /**
   * Returns a date and time string in GMT.
   *
   * @deprecated Use {@link #toUTCString()}.
   */
  @Deprecated
  public String toGMTString();

  /**
   * Returns a date string in the local time zone according to local formatting
   * conventions.
   */
  public String toLocaleDateString();

  /**
   * Returns a date and time string in the local time zone according to local
   * formatting conventions.
   */
  public String toLocaleString();

  /**
   * Returns a time string in the local time zone according to local formatting
   * conventions.
   */
  public String toLocaleTimeString();

  /**
   * Returns a time string in the local time zone.
   */
  public String toTimeString();

  /**
   * Returns a date and time string in UTC.
   */
  public String toUTCString();

  /**
   * Returns the millisecond representation, as {@link #getTime()}.
   */
  public double valueOf();
}
