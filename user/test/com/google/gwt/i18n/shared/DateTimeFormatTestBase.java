/*
 * Copyright 2010 Google Inc.
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

import com.google.gwt.i18n.client.Messages;
import com.google.gwt.junit.client.GWTTestCase;

import java.util.Date;

/**
 * Base class for date/time format tests.
 */
public abstract class DateTimeFormatTestBase extends GWTTestCase {

  /**
   * The timezone used by any tests which use a fixed timezone.
   */
  protected static final TimeZone TEST_TIMEZONE = com.google.gwt.i18n.client.TimeZone.createTimeZone(300);

  /**
   * Class for getting customized date/time formats.
   */
  public interface MyFormats extends CustomDateTimeFormat {

    /**
     * Returns a pattern for abbreviated year, month, and date.
     */
    @Pattern("yMMMd")
    DateTimeFormat yearMonthDayAbbrev();

    /**
     * Returns a pattern for full year, month, and date.
     */
    @Pattern("yyyyMMMMd")
    DateTimeFormat yearMonthDayFull();

    /**
     * Returns a pattern for full year, month, and date.
     */
    @Pattern("MMMM d, yyyy")
    DateTimeFormat yearMonthDayFull2();
  }

  /**
   * Test date/time formats in messages.
   */
  public interface MyMessages extends Messages {
    @DefaultMessage("It is {0,localdatetime,dMMMy}")
    String getCustomizedDate(Date date);
  }
}
