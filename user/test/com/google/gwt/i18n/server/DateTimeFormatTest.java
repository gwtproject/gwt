/*
 * Copyright 2016 Google Inc.
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
package com.google.gwt.i18n.server;

import com.google.gwt.i18n.shared.DateTimeFormatInfo;
import com.google.gwt.i18n.shared.TimeZone;
import com.google.gwt.i18n.shared.impl.cldr.DateTimeFormatInfoImpl_en;

import junit.framework.TestCase;

import org.junit.AfterClass;
import org.junit.BeforeClass;

import java.util.Calendar;
import java.util.Date;

/**
 * Test date formatting around daylight saving time switch.
 */
public class DateTimeFormatTest extends TestCase {

  /**
   * Constructor is protected, hence this class.
   */
  private static class DateTimeFormat extends com.google.gwt.i18n.shared.DateTimeFormat {
    DateTimeFormat(String pattern, DateTimeFormatInfo dtfi) {
      super(pattern, dtfi);
    }
  }

  private static final String DATE_PATTERN = "EEE, d MMM yyyy HH:mm:ss Z";

  private static java.util.TimeZone defaultTimeZone;

  @AfterClass
  public static void afterClass() {
    java.util.TimeZone.setDefault(defaultTimeZone);
  }

  @BeforeClass
  public static void beforeClass() {
    defaultTimeZone = java.util.TimeZone.getDefault();
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

  private static String formatGwtDate(Date date, int timeZoneOffsetInHours, String browserTimeZone) {
    return formatGwtDate(date,
        com.google.gwt.i18n.client.TimeZone.createTimeZone(timeZoneOffsetInHours * 60),
        java.util.TimeZone.getTimeZone(browserTimeZone)
        );
  }

  public void testDstStartAndFixedDateSwitchesDstAgain() {
    Date testDate = createDate(2016, 4, 29, 0, 0);
    String formattedDate = formatGwtDate(testDate, 0, "Africa/Cairo");
    assertEquals("Fri, 29 Apr 2016 00:00:00 +0000", formattedDate);
  }

  public void testDstStartAndFixedDateDoesntSwitchDst() {
    Date testDate = createDate(2016, 3, 13, 10, 0);
    String formattedDate = formatGwtDate(testDate, 10, "America/Los_Angeles");
    assertEquals("Sun, 13 Mar 2016 00:00:00 -1000", formattedDate);
  }

  public void testDstEndAndFixedDateDoesntSwitchDst() {
    Date testDate = createDate(2016, 11, 6, 9, 0);
    String formattedDate = formatGwtDate(testDate, 10, "America/Los_Angeles");
    assertEquals("Sat, 5 Nov 2016 23:00:00 -1000", formattedDate);
  }

  public void testDstEndAndFixedDateSwitchesDstAgain() {
    Date testDate = createDate(2016, 10, 16, 0, 0);
    String formattedDate = formatGwtDate(testDate, 0, "America/Campo_Grande");
    assertEquals("Sun, 16 Oct 2016 00:00:00 +0000", formattedDate);
  }

  public void testWithoutAnyDstSwitch() {
    Date testDate = createDate(2016, 06, 06, 0, 0);
    String formattedDate = formatGwtDate(testDate, 0, "Europe/Malta");
    assertEquals("Mon, 6 Jun 2016 00:00:00 +0000", formattedDate);
  }
}
