/*
 * Copyright 2014 Google Inc.
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
//  cldrVersion=25
//  date=$Date: 2013-08-29 04:32:04 +0200 (Thu, 29 Aug 2013) $
//  number=$Revision: 9287 $
//  type=nr

/**
 * Implementation of DateTimeFormatInfo for the "nr" locale.
 */
public class DateTimeFormatInfoImpl_nr extends DateTimeFormatInfoImpl {

  @Override
  public String[] erasFull() {
    return new String[] {
        "BC",
        "AD"
    };
  }

  @Override
  public int firstDayOfTheWeek() {
    return 0;
  }

  @Override
  public String formatMonthFullWeekdayDay() {
    return "EEEE, MMMM d";
  }

  @Override
  public String formatMonthNumDay() {
    return "M/d";
  }

  @Override
  public String formatYearMonthAbbrev() {
    return "MMM y";
  }

  @Override
  public String formatYearMonthAbbrevDay() {
    return "MMM d, y";
  }

  @Override
  public String formatYearMonthFull() {
    return "MMMM y";
  }

  @Override
  public String formatYearMonthFullDay() {
    return "MMMM d, y";
  }

  @Override
  public String formatYearMonthNum() {
    return "M/y";
  }

  @Override
  public String formatYearMonthNumDay() {
    return "M/d/y";
  }

  @Override
  public String formatYearMonthWeekdayDay() {
    return "EEE, MMM d, y";
  }

  @Override
  public String formatYearQuarterFull() {
    return "QQQQ y";
  }

  @Override
  public String formatYearQuarterShort() {
    return "Q y";
  }

  @Override
  public String[] monthsFull() {
    return new String[] {
        "Janabari",
        "uFeberbari",
        "uMatjhi",
        "u-Apreli",
        "Meyi",
        "Juni",
        "Julayi",
        "Arhostosi",
        "Septemba",
        "Oktoba",
        "Usinyikhaba",
        "Disemba"
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
        "Jan",
        "Feb",
        "Mat",
        "Apr",
        "Mey",
        "Jun",
        "Jul",
        "Arh",
        "Sep",
        "Okt",
        "Usi",
        "Dis"
    };
  }

  @Override
  public String[] quartersFull() {
    return new String[] {
        "Q1",
        "Q2",
        "Q3",
        "Q4"
    };
  }

  @Override
  public String[] weekdaysFull() {
    return new String[] {
        "uSonto",
        "uMvulo",
        "uLesibili",
        "Lesithathu",
        "uLesine",
        "ngoLesihlanu",
        "umGqibelo"
    };
  }

  @Override
  public String[] weekdaysShort() {
    return new String[] {
        "Son",
        "Mvu",
        "Bil",
        "Tha",
        "Ne",
        "Hla",
        "Gqi"
    };
  }
}
