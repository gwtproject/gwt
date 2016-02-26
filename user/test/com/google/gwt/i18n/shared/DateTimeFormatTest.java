/*
 * Copyright 2016 itdesign GmbH
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
package com.google.gwt.i18n.shared;

import com.google.gwt.i18n.shared.impl.cldr.DateTimeFormatInfoImpl_en;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

@RunWith(Parameterized.class)
public class DateTimeFormatTest {
  private static final String DATE_PATTERN = "yyyy/MM/dd HH:mm:ss";

  private static java.util.TimeZone defaultTimeZone;

  @AfterClass
  public static void afterClass() {
    java.util.TimeZone.setDefault(defaultTimeZone);
  }
  
  @BeforeClass
  public static void beforeClass() {
    defaultTimeZone = java.util.TimeZone.getDefault();
  }

  @Parameters
  public static Iterable<Object[]> data() {
    return Arrays.asList(new Object[][] { //
        // correcting date: +1h, daylight saving switch occurs when adding diff to date
        {"Africa/Cairo", "2016/04/29 00:00:00", 2016, 4, 29, 0, 0},
        // correcting date: +1h, NO daylight saving switch occurs when adding diff to date
        {"Australia/Currie", "2016/10/02 00:00:00", 2016, 10, 2, 0, 0},
        // correcting date: -1h, NO daylight saving switch occurs when adding diff to date
        {"Asia/Amman", "2016/10/27 23:00:00", 2016, 10, 27, 23, 0},
        // correcting date: -1h, daylight saving switch occurs when adding diff to date
        {"America/Campo_Grande", "2016/10/16 00:00:00", 2016, 10, 16, 0, 0},
        // no daylight saving switch at all
        {"Europe/Malta", "2016/06/06 00:00:00", 2016, 06, 06, 0, 0}});
  }

  private final String browserTimeZone;
  private final String expected;
  private final int year;
  private final int month;
  private final int day;
  private final int hour;
  private final int minute;

  public DateTimeFormatTest(String browserTimeZone, String expected, int year, int month, int day, int hour,
      int minute) {
    this.browserTimeZone = browserTimeZone;
    this.expected = expected;
    this.year = year;
    this.month = month;
    this.day = day;
    this.hour = hour;
    this.minute = minute;
  }

  public static Date createDate(int year, int month, int day, int hour, int minute) {
    Calendar cal = Calendar.getInstance(java.util.TimeZone.getTimeZone("UTC"));
    cal.set(Calendar.YEAR, year);
    cal.set(Calendar.MONTH, month - 1);
    cal.set(Calendar.DAY_OF_MONTH, day);
    cal.set(Calendar.HOUR_OF_DAY, hour);
    cal.set(Calendar.MINUTE, minute);
    cal.set(Calendar.SECOND, 0);
    cal.set(Calendar.MILLISECOND, 0);
    return cal.getTime();
  }
  
  private static String formatGwtDate(Date date, TimeZone formatInTimeZone,
      java.util.TimeZone browserTimeZone) {
    java.util.TimeZone.setDefault(browserTimeZone);
    DateTimeFormatInfoImpl_en formatInfo = new DateTimeFormatInfoImpl_en();
    DateTimeFormat dateTimeFormat = new DateTimeFormat(DATE_PATTERN, formatInfo);
    String format = dateTimeFormat.format(date, formatInTimeZone);
    return format;
  }
  
  @Test
  public void test() {
    Date date = createDate(year, month, day, hour, minute);
    TimeZone utc = com.google.gwt.i18n.client.TimeZone.createTimeZone(0);
    Assert.assertEquals(expected, formatGwtDate(date, utc, java.util.TimeZone.getTimeZone(browserTimeZone)));
  }
}