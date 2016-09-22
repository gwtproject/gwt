package com.google.gwt.i18n.server;

import com.google.gwt.i18n.shared.impl.cldr.DateTimeFormatInfoImpl_en;

import junit.framework.TestCase;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.TimeZone;

public class DateTimeFormatTest extends TestCase {
  private static final String FORMAT = "EEE, d MMM yyyy HH:mm:ss Z";
  private static java.util.TimeZone defaultTimeZone;

  private static String gwtFormat(Date date, int timeZoneOffsetInHours) {
    DateTimeFormat dateTimeFormat = new DateTimeFormat(FORMAT);
    return dateTimeFormat.format(date, com.google.gwt.i18n.client.TimeZone.createTimeZone(
        -timeZoneOffsetInHours * 60));
  }

  private static String jvmFormat(Date date, int timeZoneOffsetInHours) {
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat(FORMAT);
    simpleDateFormat.setTimeZone(TimeZone.getTimeZone(ZoneId.ofOffset("", ZoneOffset.ofHours(
        timeZoneOffsetInHours))));
    return simpleDateFormat.format(date);
  }

  private static void setupBrowserTimeZone(String ID) {
    java.util.TimeZone.setDefault(java.util.TimeZone.getTimeZone(ID));
  }

  @Before
  public void setUp() throws Exception {
    defaultTimeZone = java.util.TimeZone.getDefault();
  }

  @After
  public void tearDown() throws Exception {
    java.util.TimeZone.setDefault(defaultTimeZone);
  }

  public void testDstStartAndFixedDateSwitchesDstAgain() {
    setupBrowserTimeZone("Africa/Cairo");
    Date date = new Date(1461888000000L);
    int timeZoneOffsetInHours = 0;
    String expected = "Fri, 29 Apr 2016 00:00:00 +0000";
    assertEquals(expected, jvmFormat(date, timeZoneOffsetInHours));
    assertEquals(expected, gwtFormat(date, timeZoneOffsetInHours));
  }

  public void testDstStartAndFixedDateDoesntSwitchDst() {
    setupBrowserTimeZone("America/Los_Angeles");
    Date date = new Date(1457863200000L);
    int timeZoneOffsetInHours = -10;
    String expected = "Sun, 13 Mar 2016 00:00:00 -1000";
    assertEquals(expected, jvmFormat(date, timeZoneOffsetInHours));
    assertEquals(expected, gwtFormat(date, timeZoneOffsetInHours));
  }

  public void testDstEndAndFixedDateDoesntSwitchDst() {
    setupBrowserTimeZone("America/Los_Angeles");
    Date date = new Date(1478422800000L);
    int timeZoneOffsetInHours = -10;
    String expected = "Sat, 5 Nov 2016 23:00:00 -1000";
    assertEquals(expected, jvmFormat(date, timeZoneOffsetInHours));
    assertEquals(expected, gwtFormat(date, timeZoneOffsetInHours));
  }

  public void testDstEndAndFixedDateSwitchesDstAgain() {
    setupBrowserTimeZone("America/Campo_Grande");
    Date date = new Date(1476576000000L);
    int timeZoneOffsetInHours = 0;
    String expected = "Sun, 16 Oct 2016 00:00:00 +0000";
    assertEquals(expected, jvmFormat(date, timeZoneOffsetInHours));
    assertEquals(expected, gwtFormat(date, timeZoneOffsetInHours));
  }

  public void testWithoutAnyDstSwitch() {
    setupBrowserTimeZone("Europe/Malta");
    Date date = new Date(1465171200000L);
    int timeZoneOffsetInHours = 0;
    String expected = "Mon, 6 Jun 2016 00:00:00 +0000";
    assertEquals(expected, jvmFormat(date, timeZoneOffsetInHours));
    assertEquals(expected, gwtFormat(date, timeZoneOffsetInHours));
  }

  /**
   * Constructor is protected, hence this class.
   */
  private static class DateTimeFormat extends com.google.gwt.i18n.shared.DateTimeFormat {
    DateTimeFormat(String pattern) {
      super(pattern, new DateTimeFormatInfoImpl_en());
    }
  }
}
