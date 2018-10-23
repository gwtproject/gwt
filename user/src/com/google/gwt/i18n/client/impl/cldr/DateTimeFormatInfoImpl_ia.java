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
 * Implementation of DateTimeFormatInfo for the "ia" locale.
 */
public class DateTimeFormatInfoImpl_ia extends DateTimeFormatInfoImpl {

  @Override
  public String dateFormatFull() {
    return "EEEE 'le' d 'de' MMMM y";
  }

  @Override
  public String dateFormatLong() {
    return "d 'de' MMMM y";
  }

  @Override
  public String dateFormatMedium() {
    return "d MMM y";
  }

  @Override
  public String dateFormatShort() {
    return "dd-MM-y";
  }

  @Override
  public String dateTimeFull(String timePattern, String datePattern) {
    return datePattern + " 'a' " + timePattern;
  }

  @Override
  public String dateTimeLong(String timePattern, String datePattern) {
    return datePattern + " 'a' " + timePattern;
  }

  @Override
  public String[] erasFull() {
    return new String[] {
        "ante Christo",
        "post Christo"
    };
  }

  @Override
  public String[] erasShort() {
    return new String[] {
        "a.Chr.",
        "p.Chr."
    };
  }

  @Override
  public String formatMonthAbbrevDay() {
    return "d MMM";
  }

  @Override
  public String formatMonthFullDay() {
    return "d 'de' MMMM";
  }

  @Override
  public String formatMonthFullWeekdayDay() {
    return "EEEE d MMMM";
  }

  @Override
  public String formatMonthNumDay() {
    return "dd-MM";
  }

  @Override
  public String formatYearMonthAbbrev() {
    return "MMM y";
  }

  @Override
  public String formatYearMonthAbbrevDay() {
    return "d MMM y";
  }

  @Override
  public String formatYearMonthFull() {
    return "MMMM y";
  }

  @Override
  public String formatYearMonthFullDay() {
    return "d 'de' MMMM y";
  }

  @Override
  public String formatYearMonthNum() {
    return "MM-y";
  }

  @Override
  public String formatYearMonthNumDay() {
    return "dd-MM-y";
  }

  @Override
  public String formatYearMonthWeekdayDay() {
    return "EEE d MMM y";
  }

  @Override
  public String formatYearQuarterFull() {
    return "QQQQ 'de' y";
  }

  @Override
  public String formatYearQuarterShort() {
    return "Q y";
  }

  @Override
  public String[] monthsFull() {
    return new String[] {
        "januario",
        "februario",
        "martio",
        "april",
        "maio",
        "junio",
        "julio",
        "augusto",
        "septembre",
        "octobre",
        "novembre",
        "decembre"
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
  public String[] monthsNarrowStandalone() {
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
        "jan",
        "feb",
        "mar",
        "apr",
        "mai",
        "jun",
        "jul",
        "aug",
        "sep",
        "oct",
        "nov",
        "dec"
    };
  }

  @Override
  public String[] quartersFull() {
    return new String[] {
        "1me trimestre",
        "2nde trimestre",
        "3tie trimestre",
        "4te trimestre"
    };
  }

  @Override
  public String[] quartersShort() {
    return new String[] {
        "T1",
        "T2",
        "T3",
        "T4"
    };
  }

  @Override
  public String[] weekdaysFull() {
    return new String[] {
        "dominica",
        "lunedi",
        "martedi",
        "mercuridi",
        "jovedi",
        "venerdi",
        "sabbato"
    };
  }

  @Override
  public String[] weekdaysNarrow() {
    return new String[] {
        "d",
        "l",
        "m",
        "m",
        "j",
        "v",
        "s"
    };
  }

  @Override
  public String[] weekdaysShort() {
    return new String[] {
        "dom",
        "lun",
        "mar",
        "mer",
        "jov",
        "ven",
        "sab"
    };
  }
}
