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
 * Implementation of DateTimeFormatInfo for the "bs" locale.
 */
public class DateTimeFormatInfoImpl_bs extends DateTimeFormatInfoImpl {

  @Override
  public String dateFormatFull() {
    return "EEEE, d. MMMM y.";
  }

  @Override
  public String dateFormatLong() {
    return "d. MMMM y.";
  }

  @Override
  public String dateFormatMedium() {
    return "d. MMM y.";
  }

  @Override
  public String dateFormatShort() {
    return "d. M. y.";
  }

  @Override
  public String dateTimeFull(String timePattern, String datePattern) {
    return datePattern + " 'u' " + timePattern;
  }

  @Override
  public String dateTimeLong(String timePattern, String datePattern) {
    return datePattern + " 'u' " + timePattern;
  }

  @Override
  public String[] erasFull() {
    return new String[] {
        "prije nove ere",
        "nove ere"
    };
  }

  @Override
  public String[] erasShort() {
    return new String[] {
        "p. n. e.",
        "n. e."
    };
  }

  @Override
  public String formatDay() {
    return "d.";
  }

  @Override
  public String formatHour12Minute() {
    return "hh:mm a";
  }

  @Override
  public String formatHour12MinuteSecond() {
    return "hh:mm:ss a";
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
    return "EEEE, d. MMMM";
  }

  @Override
  public String formatMonthNumDay() {
    return "d.M.";
  }

  @Override
  public String formatYear() {
    return "y.";
  }

  @Override
  public String formatYearMonthAbbrev() {
    return "MMM y.";
  }

  @Override
  public String formatYearMonthAbbrevDay() {
    return "d. MMM y.";
  }

  @Override
  public String formatYearMonthFull() {
    return "LLLL y.";
  }

  @Override
  public String formatYearMonthFullDay() {
    return "d. MMMM y.";
  }

  @Override
  public String formatYearMonthNum() {
    return "MM/y";
  }

  @Override
  public String formatYearMonthNumDay() {
    return "d.M.y.";
  }

  @Override
  public String formatYearMonthWeekdayDay() {
    return "EEE, d. MMM y.";
  }

  @Override
  public String formatYearQuarterFull() {
    return "QQQQ y.";
  }

  @Override
  public String formatYearQuarterShort() {
    return "Q y.";
  }

  @Override
  public String[] monthsFull() {
    return new String[] {
        "januar",
        "februar",
        "mart",
        "april",
        "maj",
        "juni",
        "juli",
        "august",
        "septembar",
        "oktobar",
        "novembar",
        "decembar"
    };
  }

  @Override
  public String[] monthsNarrow() {
    return new String[] {
        "j",
        "f",
        "m",
        "a",
        "m",
        "j",
        "j",
        "a",
        "s",
        "o",
        "n",
        "d"
    };
  }

  @Override
  public String[] monthsShort() {
    return new String[] {
        "jan",
        "feb",
        "mar",
        "apr",
        "maj",
        "jun",
        "jul",
        "aug",
        "sep",
        "okt",
        "nov",
        "dec"
    };
  }

  @Override
  public String[] quartersFull() {
    return new String[] {
        "Prvi kvartal",
        "Drugi kvartal",
        "Treći kvartal",
        "Četvrti kvartal"
    };
  }

  @Override
  public String[] quartersShort() {
    return new String[] {
        "KV1",
        "KV2",
        "KV3",
        "KV4"
    };
  }

  @Override
  public String[] weekdaysFull() {
    return new String[] {
        "nedjelja",
        "ponedjeljak",
        "utorak",
        "srijeda",
        "četvrtak",
        "petak",
        "subota"
    };
  }

  @Override
  public String[] weekdaysNarrow() {
    return new String[] {
        "N",
        "P",
        "U",
        "S",
        "Č",
        "P",
        "S"
    };
  }

  @Override
  public String[] weekdaysNarrowStandalone() {
    return new String[] {
        "n",
        "p",
        "u",
        "s",
        "č",
        "p",
        "s"
    };
  }

  @Override
  public String[] weekdaysShort() {
    return new String[] {
        "ned",
        "pon",
        "uto",
        "sri",
        "čet",
        "pet",
        "sub"
    };
  }
}
