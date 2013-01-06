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
package com.google.gwt.i18n.client;

import com.google.gwt.i18n.client.constants.DateTimeConstants;
import com.google.gwt.i18n.shared.DateTimeFormatInfo;

/**
 * Adapter that makes a {@link com.google.gwt.i18n.client.DateTimeFormatInfo}
 * implementation suitable for use with something that wants a
 * {@link DateTimeConstants}.
 * 
 * @deprecated use {@link DateTimeFormatInfo} instead
 */
@Deprecated
class DateTimeConstantsAdapter implements DateTimeConstants {

  private final DateTimeFormatInfo dtfi;
  
  public DateTimeConstantsAdapter(DateTimeFormatInfo dtfi) {
    this.dtfi = dtfi;
  }

 @Override
 public String[] ampms() {
    return dtfi.ampms();
  }

  @Override
  public String[] dateFormats() {
    return new String[] {
        dtfi.dateFormatFull(), dtfi.dateFormatLong(), dtfi.dateFormatMedium(),
        dtfi.dateFormatShort(),
    };
  }

  @Override
  public String[] eraNames() {
    return dtfi.erasFull();
  }

  @Override
  public String[] eras() {
    return dtfi.erasShort();
  }

  @Override
  public String firstDayOfTheWeek() {
    return String.valueOf(dtfi.firstDayOfTheWeek() + 1);
  }

  @Override
  public String[] months() {
    return dtfi.monthsFull();
  }

  @Override
  public String[] narrowMonths() {
    return dtfi.monthsNarrow();
  }

  @Override
  public String[] narrowWeekdays() {
    return dtfi.weekdaysNarrow();
  }

  @Override
  public String[] quarters() {
    return dtfi.quartersFull();
  }

  @Override
  public String[] shortMonths() {
    return dtfi.monthsShort();
  }

  @Override
  public String[] shortQuarters() {
    return dtfi.quartersShort();
  }

  @Override
  public String[] shortWeekdays() {
    return dtfi.weekdaysShort();
  }

  @Override
  public String[] standaloneMonths() {
    return dtfi.monthsFullStandalone();
  }

  @Override
  public String[] standaloneNarrowMonths() {
    return dtfi.monthsNarrowStandalone();
  }

  @Override
  public String[] standaloneNarrowWeekdays() {
    return dtfi.weekdaysNarrowStandalone();
  }

  @Override
  public String[] standaloneShortMonths() {
    return dtfi.monthsShortStandalone();
  }

  @Override
  public String[] standaloneShortWeekdays() {
    return dtfi.weekdaysShortStandalone();
  }

  @Override
  public String[] standaloneWeekdays() {
    return dtfi.weekdaysFullStandalone();
  }

  @Override
  public String[] timeFormats() {
    return new String[] {
        dtfi.timeFormatFull(), dtfi.timeFormatLong(), dtfi.timeFormatMedium(),
        dtfi.dateFormatShort(),
    };
  }

  @Override
  public String[] weekdays() {
    return dtfi.weekdaysFull();
  }

  @Override
  public String[] weekendRange() {
    return new String[] {
        String.valueOf(dtfi.weekendStart() + 1),
        String.valueOf(dtfi.weekendEnd() + 1),
    };
  }
}
