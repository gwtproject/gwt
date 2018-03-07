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
package com.google.gwt.i18n.shared.impl.cldr;
// DO NOT EDIT - GENERATED FROM CLDR AND ICU DATA

/**
 * Implementation of DateTimeFormatInfo for the "fo" locale.
 */
public class DateTimeFormatInfoImpl_fo extends DateTimeFormatInfoImpl {

  @Override
  public String dateFormatFull() {
    return "EEEE, d. MMMM y";
  }

  @Override
  public String dateFormatLong() {
    return "d. MMMM y";
  }

  @Override
  public String dateFormatMedium() {
    return "dd.MM.y";
  }

  @Override
  public String dateFormatShort() {
    return "dd.MM.yy";
  }

  @Override
  public String dateTimeFull(String timePattern, String datePattern) {
    return datePattern + " 'kl'. " + timePattern;
  }

  @Override
  public String dateTimeLong(String timePattern, String datePattern) {
    return datePattern + " 'kl'. " + timePattern;
  }

  @Override
  public String dateTimeMedium(String timePattern, String datePattern) {
    return datePattern + ", " + timePattern;
  }

  @Override
  public String dateTimeShort(String timePattern, String datePattern) {
    return datePattern + ", " + timePattern;
  }

  @Override
  public String[] erasFull() {
    return new String[] {
        "fyri Krist",
        "eftir Krist"
    };
  }

  @Override
  public String[] erasShort() {
    return new String[] {
        "f.Kr.",
        "e.Kr."
    };
  }

  @Override
  public String formatDay() {
    return "d.";
  }

  @Override
  public String formatMonthAbbrevDay() {
    return "d. MMM";
  }

  @Override
  public String formatMonthFullDay() {
    return "d. MMMM";
  }

  @Override
  public String formatMonthFullWeekdayDay() {
    return "EEEE d. MMMM";
  }

  @Override
  public String formatMonthNumDay() {
    return "dd.MM";
  }

  @Override
  public String formatYearMonthAbbrev() {
    return "MMM y";
  }

  @Override
  public String formatYearMonthAbbrevDay() {
    return "d. MMM y";
  }

  @Override
  public String formatYearMonthFull() {
    return "MMMM y";
  }

  @Override
  public String formatYearMonthFullDay() {
    return "d. MMMM y";
  }

  @Override
  public String formatYearMonthNum() {
    return "MM.y";
  }

  @Override
  public String formatYearMonthNumDay() {
    return "dd.MM.y";
  }

  @Override
  public String formatYearMonthWeekdayDay() {
    return "EEE d. MMM y";
  }

  @Override
  public String formatYearQuarterFull() {
    return "QQQQ 'í' y";
  }

  @Override
  public String formatYearQuarterShort() {
    return "Q 'í' y";
  }

  @Override
  public String[] monthsFull() {
    return new String[] {
        "januar",
        "februar",
        "mars",
        "apríl",
        "mai",
        "juni",
        "juli",
        "august",
        "september",
        "oktober",
        "november",
        "desember"
    };
  }

  @Override
  public String[] monthsShort() {
    return new String[] {
        "jan.",
        "feb.",
        "mar.",
        "apr.",
        "mai",
        "jun.",
        "jul.",
        "aug.",
        "sep.",
        "okt.",
        "nov.",
        "des."
    };
  }

  @Override
  public String[] monthsShortStandalone() {
    return new String[] {
        "jan",
        "feb",
        "mar",
        "apr",
        "mai",
        "jun",
        "jul",
        "aug",
        "sep",
        "okt",
        "nov",
        "des"
    };
  }

  @Override
  public String[] quartersFull() {
    return new String[] {
        "1. ársfjórðingur",
        "2. ársfjórðingur",
        "3. ársfjórðingur",
        "4. ársfjórðingur"
    };
  }

  @Override
  public String[] quartersShort() {
    return new String[] {
        "1. ársfj.",
        "2. ársfj.",
        "3. ársfj.",
        "4. ársfj."
    };
  }

  @Override
  public String[] weekdaysFull() {
    return new String[] {
        "sunnudagur",
        "mánadagur",
        "týsdagur",
        "mikudagur",
        "hósdagur",
        "fríggjadagur",
        "leygardagur"
    };
  }

  @Override
  public String[] weekdaysNarrow() {
    return new String[] {
        "S",
        "M",
        "T",
        "M",
        "H",
        "F",
        "L"
    };
  }

  @Override
  public String[] weekdaysShort() {
    return new String[] {
        "sun.",
        "mán.",
        "týs.",
        "mik.",
        "hós.",
        "frí.",
        "ley."
    };
  }

  @Override
  public String[] weekdaysShortStandalone() {
    return new String[] {
        "sun",
        "mán",
        "týs",
        "mik",
        "hós",
        "frí",
        "ley"
    };
  }
}
