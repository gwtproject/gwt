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
 * Implementation of DateTimeFormatInfo for the "sq" locale.
 */
public class DateTimeFormatInfoImpl_sq extends DateTimeFormatInfoImpl {

  @Override
  public String[] ampms() {
    return new String[] {
        "p.d.",
        "m.d."
    };
  }

  @Override
  public String dateFormatFull() {
    return "EEEE, d MMMM y";
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
  public String dateFormatShort() {
    return "d.M.yy";
  }

  @Override
  public String dateTimeFull(String timePattern, String datePattern) {
    return datePattern + " 'në' " + timePattern;
  }

  @Override
  public String dateTimeLong(String timePattern, String datePattern) {
    return datePattern + " 'në' " + timePattern;
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
        "para Krishtit",
        "mbas Krishtit"
    };
  }

  @Override
  public String[] erasShort() {
    return new String[] {
        "p.K.",
        "mb.K."
    };
  }

  @Override
  public String formatMonthAbbrevDay() {
    return "d MMM";
  }

  @Override
  public String formatMonthFullDay() {
    return "d MMMM";
  }

  @Override
  public String formatMonthFullWeekdayDay() {
    return "EEEE, d MMMM";
  }

  @Override
  public String formatMonthNumDay() {
    return "d.M";
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
    return "d MMMM y";
  }

  @Override
  public String formatYearMonthNum() {
    return "M.y";
  }

  @Override
  public String formatYearMonthNumDay() {
    return "d.M.y";
  }

  @Override
  public String formatYearMonthWeekdayDay() {
    return "EEE, d MMM y";
  }

  @Override
  public String formatYearQuarterFull() {
    return "QQQQ, y";
  }

  @Override
  public String formatYearQuarterShort() {
    return "Q, y";
  }

  @Override
  public String[] monthsFull() {
    return new String[] {
        "janar",
        "shkurt",
        "mars",
        "prill",
        "maj",
        "qershor",
        "korrik",
        "gusht",
        "shtator",
        "tetor",
        "nëntor",
        "dhjetor"
    };
  }

  @Override
  public String[] monthsNarrow() {
    return new String[] {
        "j",
        "sh",
        "m",
        "p",
        "m",
        "q",
        "k",
        "g",
        "sh",
        "t",
        "n",
        "dh"
    };
  }

  @Override
  public String[] monthsShort() {
    return new String[] {
        "jan",
        "shk",
        "mar",
        "pri",
        "maj",
        "qer",
        "korr",
        "gush",
        "sht",
        "tet",
        "nën",
        "dhj"
    };
  }

  @Override
  public String[] quartersFull() {
    return new String[] {
        "tremujori i parë",
        "tremujori i dytë",
        "tremujori i tretë",
        "tremujori i katërt"
    };
  }

  @Override
  public String[] quartersShort() {
    return new String[] {
        "tremujori I",
        "tremujori II",
        "tremujori III",
        "tremujori IV"
    };
  }

  @Override
  public String timeFormatFull() {
    return "h:mm:ss a, zzzz";
  }

  @Override
  public String timeFormatLong() {
    return "h:mm:ss a, z";
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
        "e diel",
        "e hënë",
        "e martë",
        "e mërkurë",
        "e enjte",
        "e premte",
        "e shtunë"
    };
  }

  @Override
  public String[] weekdaysNarrow() {
    return new String[] {
        "d",
        "h",
        "m",
        "m",
        "e",
        "p",
        "sh"
    };
  }

  @Override
  public String[] weekdaysShort() {
    return new String[] {
        "Die",
        "Hën",
        "Mar",
        "Mër",
        "Enj",
        "Pre",
        "Sht"
    };
  }

  @Override
  public String[] weekdaysShortStandalone() {
    return new String[] {
        "die",
        "hën",
        "mar",
        "mër",
        "enj",
        "pre",
        "sht"
    };
  }
}
