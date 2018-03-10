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
package com.google.gwt.i18n.client.impl.cldr;
// DO NOT EDIT - GENERATED FROM CLDR AND ICU DATA

/**
 * Implementation of DateTimeFormatInfo for the "ug" locale.
 */
public class DateTimeFormatInfoImpl_ug extends DateTimeFormatInfoImpl {

  @Override
  public String[] ampms() {
    return new String[] {
        "چ.ب",
        "چ.ك"
    };
  }

  @Override
  public String dateFormatFull() {
    return "y d-MMMM، EEEE";
  }

  @Override
  public String dateFormatLong() {
    return "d-MMMM، y";
  }

  @Override
  public String dateFormatMedium() {
    return "d-MMM، y";
  }

  @Override
  public String dateTimeMedium(String timePattern, String datePattern) {
    return datePattern + "، " + timePattern;
  }

  @Override
  public String dateTimeShort(String timePattern, String datePattern) {
    return datePattern + "، " + timePattern;
  }

  @Override
  public String[] erasFull() {
    return new String[] {
        "مىلادىيەدىن بۇرۇن",
        "مىلادىيە"
    };
  }

  @Override
  public String[] erasShort() {
    return new String[] {
        "BCE",
        "مىلادىيە"
    };
  }

  @Override
  public int firstDayOfTheWeek() {
    return 0;
  }

  @Override
  public String formatMonthAbbrevDay() {
    return "d-MMM";
  }

  @Override
  public String formatMonthFullDay() {
    return "d-MMMM";
  }

  @Override
  public String formatMonthFullWeekdayDay() {
    return "d-MMMM، EEEE";
  }

  @Override
  public String formatMonthNumDay() {
    return "d-M";
  }

  @Override
  public String formatYearMonthAbbrevDay() {
    return "y d-MMM";
  }

  @Override
  public String formatYearMonthFullDay() {
    return "d-MMMM، y";
  }

  @Override
  public String formatYearMonthNum() {
    return "M-y";
  }

  @Override
  public String formatYearMonthNumDay() {
    return "y-d-M";
  }

  @Override
  public String formatYearMonthWeekdayDay() {
    return "y d-MMM، EEE";
  }

  @Override
  public String[] monthsFull() {
    return new String[] {
        "يانۋار",
        "فېۋرال",
        "مارت",
        "ئاپرېل",
        "ماي",
        "ئىيۇن",
        "ئىيۇل",
        "ئاۋغۇست",
        "سېنتەبىر",
        "ئۆكتەبىر",
        "نويابىر",
        "دېكابىر"
    };
  }

  @Override
  public String[] monthsNarrow() {
    return new String[] {
        "1",
        "2",
        "3",
        "4",
        "5",
        "6",
        "7",
        "8",
        "9",
        "10",
        "11",
        "12"
    };
  }

  @Override
  public String[] monthsShort() {
    return new String[] {
        "يانۋار",
        "فېۋرال",
        "مارت",
        "ئاپرېل",
        "ماي",
        "ئىيۇن",
        "ئىيۇل",
        "ئاۋغۇست",
        "سېنتەبىر",
        "ئۆكتەبىر",
        "نويابىر",
        "دېكابىر"
    };
  }

  @Override
  public String[] quartersFull() {
    return new String[] {
        "بىرىنچى پەسىل",
        "ئىككىنچى پەسىل",
        "ئۈچىنچى پەسىل",
        "تۆتىنچى پەسىل"
    };
  }

  @Override
  public String[] quartersShort() {
    return new String[] {
        "1-پەسىل",
        "2-پەسىل",
        "3-پەسىل",
        "4-پەسىل"
    };
  }

  @Override
  public String timeFormatFull() {
    return "h:mm:ss a zzzz";
  }

  @Override
  public String timeFormatLong() {
    return "h:mm:ss a z";
  }

  @Override
  public String timeFormatMedium() {
    return "h:mm:ss a";
  }

  @Override
  public String timeFormatShort() {
    return "h:mm a";
  }

  @Override
  public String[] weekdaysFull() {
    return new String[] {
        "يەكشەنبە",
        "دۈشەنبە",
        "سەيشەنبە",
        "چارشەنبە",
        "پەيشەنبە",
        "جۈمە",
        "شەنبە"
    };
  }

  @Override
  public String[] weekdaysNarrow() {
    return new String[] {
        "ي",
        "د",
        "س",
        "چ",
        "پ",
        "ج",
        "ش"
    };
  }

  @Override
  public String[] weekdaysShort() {
    return new String[] {
        "يە",
        "دۈ",
        "سە",
        "چا",
        "پە",
        "جۈ",
        "شە"
    };
  }
}
