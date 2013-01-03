/*
 * Copyright 2012 Google Inc.
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
package com.google.gwt.i18n.shared.impl;

import com.google.gwt.i18n.shared.DateTimeFormat.PredefinedFormat;
import com.google.gwt.i18n.shared.DateTimeFormatFactory;
import com.google.gwt.i18n.shared.DateTimeFormatInfo;
import com.google.gwt.i18n.shared.DefaultDateTimeFormatInfo;

/**
 * Implementation of {@link DateTimeFormatFactory}.
 */
public class DateTimeFormatFactoryImpl implements DateTimeFormatFactory {

  private final DateTimeFormatInfo dtfi;

  /**
   * @param dtfi
   */
  public DateTimeFormatFactoryImpl(DateTimeFormatInfo dtfi) {
    this.dtfi = dtfi;
  }

  @Override
  public DateTimeFormatImpl getFormat(PredefinedFormat predef) {
    if (DateTimeFormatImpl.usesFixedEnglishStrings(predef)) {
      String pattern;
      switch (predef) {
        case RFC_2822:
          pattern = DateTimeFormatImpl.RFC2822_PATTERN;
          break;
        case ISO_8601:
          pattern = DateTimeFormatImpl.ISO8601_PATTERN;
          break;
        default:
          throw new IllegalStateException("Unexpected predef type " + predef);
      }
      return getFormat(pattern, new DefaultDateTimeFormatInfo());
    }
    String pattern;
    switch (predef) {
      case DATE_FULL:
        pattern = dtfi.dateFormatFull();
        break;
      case DATE_LONG:
        pattern = dtfi.dateFormatLong();
        break;
      case DATE_MEDIUM:
        pattern = dtfi.dateFormatMedium();
        break;
      case DATE_SHORT:
        pattern = dtfi.dateFormatShort();
        break;
      case DATE_TIME_FULL:
        pattern = dtfi.dateTimeFull(dtfi.timeFormatFull(),
            dtfi.dateFormatFull());
        break;
      case DATE_TIME_LONG:
        pattern = dtfi.dateTimeLong(dtfi.timeFormatLong(),
            dtfi.dateFormatLong());
        break;
      case DATE_TIME_MEDIUM:
        pattern = dtfi.dateTimeMedium(dtfi.timeFormatMedium(),
            dtfi.dateFormatMedium());
        break;
      case DATE_TIME_SHORT:
        pattern = dtfi.dateTimeShort(dtfi.timeFormatShort(),
            dtfi.dateFormatShort());
        break;
      case DAY:
        pattern = dtfi.formatDay();
        break;
      case HOUR24_MINUTE:
        pattern = dtfi.formatHour24Minute();
        break;
      case HOUR24_MINUTE_SECOND:
        pattern = dtfi.formatHour24MinuteSecond();
        break;
      case HOUR_MINUTE:
        pattern = dtfi.formatHour12Minute();
        break;
      case HOUR_MINUTE_SECOND:
        pattern = dtfi.formatHour12MinuteSecond();
        break;
      case MINUTE_SECOND:
        pattern = dtfi.formatMinuteSecond();
        break;
      case MONTH:
        pattern = dtfi.formatMonthFull();
        break;
      case MONTH_ABBR:
        pattern = dtfi.formatMonthAbbrev();
        break;
      case MONTH_ABBR_DAY:
        pattern = dtfi.formatMonthAbbrevDay();
        break;
      case MONTH_DAY:
        pattern = dtfi.formatMonthFullDay();
        break;
      case MONTH_NUM_DAY:
        pattern = dtfi.formatMonthNumDay();
        break;
      case MONTH_WEEKDAY_DAY:
        pattern = dtfi.formatMonthFullWeekdayDay();
        break;
      case TIME_FULL:
        pattern = dtfi.timeFormatFull();
        break;
      case TIME_LONG:
        pattern = dtfi.timeFormatLong();
        break;
      case TIME_MEDIUM:
        pattern = dtfi.timeFormatMedium();
        break;
      case TIME_SHORT:
        pattern = dtfi.timeFormatShort();
        break;
      case YEAR:
        pattern = dtfi.formatYear();
        break;
      case YEAR_MONTH:
        pattern = dtfi.formatYearMonthFull();
        break;
      case YEAR_MONTH_ABBR:
        pattern = dtfi.formatYearMonthAbbrev();
        break;
      case YEAR_MONTH_ABBR_DAY:
        pattern = dtfi.formatYearMonthAbbrevDay();
        break;
      case YEAR_MONTH_DAY:
        pattern = dtfi.formatYearMonthFullDay();
        break;
      case YEAR_MONTH_NUM:
        pattern = dtfi.formatYearMonthNum();
        break;
      case YEAR_MONTH_NUM_DAY:
        pattern = dtfi.formatYearMonthNumDay();
        break;
      case YEAR_MONTH_WEEKDAY_DAY:
        pattern = dtfi.formatYearMonthWeekdayDay();
        break;
      case YEAR_QUARTER:
        pattern = dtfi.formatYearQuarterFull();
        break;
      case YEAR_QUARTER_ABBR:
        pattern = dtfi.formatYearQuarterShort();
        break;
      default:
        throw new IllegalArgumentException("Unexpected predefined format "
            + predef);
    }
    return getFormat(pattern, dtfi);
  }

  @Override
  public DateTimeFormatImpl getFormat(String pattern) {
    return getFormat(pattern, dtfi);
  }

  protected DateTimeFormatImpl getFormat(String pattern, DateTimeFormatInfo dtfi) {
    return new DateTimeFormatImpl(pattern, dtfi);
  }
}
