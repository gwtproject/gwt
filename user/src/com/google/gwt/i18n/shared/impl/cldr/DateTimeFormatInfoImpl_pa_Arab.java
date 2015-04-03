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
 * Implementation of DateTimeFormatInfo for the "pa_Arab" locale.
 */
public class DateTimeFormatInfoImpl_pa_Arab extends DateTimeFormatInfoImpl_pa {

  @Override
  public String dateFormatFull() {
    return "EEEE, dd MMMM y";
  }

  @Override
  public String dateFormatShort() {
    return "dd/MM/y";
  }

  @Override
  public String dateTimeShort(String timePattern, String datePattern) {
    return datePattern + " " + timePattern;
  }

  @Override
  public String[] erasFull() {
    return new String[] {
        "ايساپورو",
        "سں"
    };
  }

  @Override
  public String[] erasShort() {
    return new String[] {
        "BCE",
        "CE"
    };
  }

  @Override
  public int firstDayOfTheWeek() {
    return 0;
  }

  @Override
  public String formatMonthAbbrevDay() {
    return "MMM d";
  }

  @Override
  public String formatMonthFullDay() {
    return "MMMM d";
  }

  @Override
  public String formatMonthFullWeekdayDay() {
    return "MMMM d, EEEE";
  }

  @Override
  public String formatYearMonthNum() {
    return "y-MM";
  }

  @Override
  public String formatYearMonthWeekdayDay() {
    return "y MMM d, EEE";
  }

  @Override
  public String[] monthsFull() {
    return new String[] {
        "جنوری",
        "فروری",
        "مارچ",
        "اپریل",
        "مئ",
        "جون",
        "جولائی",
        "اگست",
        "ستمبر",
        "اکتوبر",
        "نومبر",
        "دسمبر"
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
        "جنوری",
        "فروری",
        "مارچ",
        "اپریل",
        "مئ",
        "جون",
        "جولائی",
        "اگست",
        "ستمبر",
        "اکتوبر",
        "نومبر",
        "دسمبر"
    };
  }

  @Override
  public String[] quartersFull() {
    return new String[] {
        "چوتھاي پہلاں",
        "چوتھاي دوجا",
        "چوتھاي تيجا",
        "چوتھاي چوتھا"
    };
  }

  @Override
  public String[] quartersShort() {
    return new String[] {
        "چوتھاي پہلاں",
        "چوتھاي دوجا",
        "چوتھاي تيجا",
        "چوتھاي چوتھا"
    };
  }

  @Override
  public String[] weekdaysFull() {
    return new String[] {
        "اتوار",
        "پیر",
        "منگل",
        "بُدھ",
        "جمعرات",
        "جمعہ",
        "ہفتہ"
    };
  }

  @Override
  public String[] weekdaysNarrow() {
    return new String[] {
        "S",
        "M",
        "T",
        "W",
        "T",
        "F",
        "S"
    };
  }

  @Override
  public String[] weekdaysShort() {
    return new String[] {
        "اتوار",
        "پیر",
        "منگل",
        "بُدھ",
        "جمعرات",
        "جمعہ",
        "ہفتہ"
    };
  }
}
