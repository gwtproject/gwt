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
package com.google.gwt.i18n.shared.cldr;
// DO NOT EDIT - GENERATED FROM CLDR AND ICU DATA
//  cldrVersion=21.0
//  number=$Revision: 6546 $
//  type=root
//  date=$Date: 2012-02-07 13:32:35 -0500 (Tue, 07 Feb 2012) $

/**
 * Implementation of DateTimeFormatInfo for the "zu" locale.
 */
public class DateTimeFormatInfoImpl_zu extends DateTimeFormatInfoImpl {

  @Override
  public String dateFormatFull() {
    return "EEEE dd MMMM y";
  }

  @Override
  public String dateFormatLong() {
    return "d MMMM y";
  }

  @Override
  public String dateFormatMedium() {
    return "d MMM y";
  }

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
  public String formatYear() {
    return "Y";
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
    return "d MMMM y";
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
        "Januwari",
        "Februwari",
        "Mashi",
        "Apreli",
        "Meyi",
        "Juni",
        "Julayi",
        "Agasti",
        "Septhemba",
        "Okthoba",
        "Novemba",
        "Disemba"
    };
  }

  @Override
  public String[] monthsFullStandalone() {
    return new String[] {
        "uJanuwari",
        "uFebruwari",
        "uMashi",
        "u-Apreli",
        "uMeyi",
        "uJuni",
        "uJulayi",
        "uAgasti",
        "uSepthemba",
        "u-Okthoba",
        "uNovemba",
        "uDisemba"
    };
  }

  @Override
  public String[] monthsShort() {
    return new String[] {
        "Jan",
        "Feb",
        "Mas",
        "Apr",
        "Mey",
        "Jun",
        "Jul",
        "Aga",
        "Sep",
        "Okt",
        "Nov",
        "Dis"
    };
  }

  @Override
  public String[] quartersFull() {
    return new String[] {
        "ikota yoku-1",
        "ikota yesi-2",
        "ikota yesi-3",
        "ikota yesi-4"
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
        "Sonto",
        "Msombuluko",
        "Lwesibili",
        "Lwesithathu",
        "uLwesine",
        "Lwesihlanu",
        "Mgqibelo"
    };
  }

  @Override
  public String[] weekdaysNarrow() {
    return new String[] {
        "S",
        "M",
        "B",
        "T",
        "S",
        "H",
        "M"
    };
  }

  @Override
  public String[] weekdaysShort() {
    return new String[] {
        "Son",
        "Mso",
        "Bil",
        "Tha",
        "Sin",
        "Hla",
        "Mgq"
    };
  }
}
