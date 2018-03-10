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
 * Implementation of DateTimeFormatInfo for the "uz" locale.
 */
public class DateTimeFormatInfoImpl_uz extends DateTimeFormatInfoImpl {

  @Override
  public String[] ampms() {
    return new String[] {
        "TO",
        "TK"
    };
  }

  @Override
  public String dateFormatFull() {
    return "EEEE, d-MMMM, y";
  }

  @Override
  public String dateFormatLong() {
    return "d-MMMM, y";
  }

  @Override
  public String dateFormatMedium() {
    return "d-MMM, y";
  }

  @Override
  public String dateFormatShort() {
    return "dd/MM/yy";
  }

  @Override
  public String dateTimeFull(String timePattern, String datePattern) {
    return datePattern + ", " + timePattern;
  }

  @Override
  public String dateTimeLong(String timePattern, String datePattern) {
    return datePattern + ", " + timePattern;
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
        "miloddan avvalgi",
        "milodiy"
    };
  }

  @Override
  public String[] erasShort() {
    return new String[] {
        "m.a.",
        "milodiy"
    };
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
    return "EEEE, d-MMMM";
  }

  @Override
  public String formatMonthNumDay() {
    return "dd/MM";
  }

  @Override
  public String formatYearMonthAbbrev() {
    return "MMM, y";
  }

  @Override
  public String formatYearMonthAbbrevDay() {
    return "d-MMM, y";
  }

  @Override
  public String formatYearMonthFull() {
    return "MMMM, y";
  }

  @Override
  public String formatYearMonthFullDay() {
    return "d-MMMM, y";
  }

  @Override
  public String formatYearMonthNum() {
    return "MM.y";
  }

  @Override
  public String formatYearMonthNumDay() {
    return "dd/MM/y";
  }

  @Override
  public String formatYearMonthWeekdayDay() {
    return "EEE, d-MMM, y";
  }

  @Override
  public String formatYearQuarterFull() {
    return "y, QQQQ";
  }

  @Override
  public String formatYearQuarterShort() {
    return "y, Q";
  }

  @Override
  public String[] monthsFull() {
    return new String[] {
        "yanvar",
        "fevral",
        "mart",
        "aprel",
        "may",
        "iyun",
        "iyul",
        "avgust",
        "sentabr",
        "oktabr",
        "noyabr",
        "dekabr"
    };
  }

  @Override
  public String[] monthsFullStandalone() {
    return new String[] {
        "Yanvar",
        "Fevral",
        "Mart",
        "Aprel",
        "May",
        "Iyun",
        "Iyul",
        "Avgust",
        "Sentabr",
        "Oktabr",
        "Noyabr",
        "Dekabr"
    };
  }

  @Override
  public String[] monthsNarrow() {
    return new String[] {
        "Y",
        "F",
        "M",
        "A",
        "M",
        "I",
        "I",
        "A",
        "S",
        "O",
        "N",
        "D"
    };
  }

  @Override
  public String[] monthsShort() {
    return new String[] {
        "yan",
        "fev",
        "mar",
        "apr",
        "may",
        "iyn",
        "iyl",
        "avg",
        "sen",
        "okt",
        "noy",
        "dek"
    };
  }

  @Override
  public String[] monthsShortStandalone() {
    return new String[] {
        "Yan",
        "Fev",
        "Mar",
        "Apr",
        "May",
        "Iyn",
        "Iyl",
        "Avg",
        "Sen",
        "Okt",
        "Noy",
        "Dek"
    };
  }

  @Override
  public String[] quartersFull() {
    return new String[] {
        "1-chorak",
        "2-chorak",
        "3-chorak",
        "4-chorak"
    };
  }

  @Override
  public String[] quartersShort() {
    return new String[] {
        "1-ch",
        "2-ch",
        "3-ch",
        "4-ch"
    };
  }

  @Override
  public String timeFormatFull() {
    return "H:mm:ss (zzzz)";
  }

  @Override
  public String timeFormatLong() {
    return "H:mm:ss (z)";
  }

  @Override
  public String[] weekdaysFull() {
    return new String[] {
        "yakshanba",
        "dushanba",
        "seshanba",
        "chorshanba",
        "payshanba",
        "juma",
        "shanba"
    };
  }

  @Override
  public String[] weekdaysNarrow() {
    return new String[] {
        "Y",
        "D",
        "S",
        "C",
        "P",
        "J",
        "S"
    };
  }

  @Override
  public String[] weekdaysShort() {
    return new String[] {
        "Yak",
        "Dush",
        "Sesh",
        "Chor",
        "Pay",
        "Jum",
        "Shan"
    };
  }
}
