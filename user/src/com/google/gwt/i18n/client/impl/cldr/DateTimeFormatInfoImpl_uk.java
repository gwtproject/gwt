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
 * Implementation of DateTimeFormatInfo for the "uk" locale.
 */
public class DateTimeFormatInfoImpl_uk extends DateTimeFormatInfoImpl {

  @Override
  public String[] ampms() {
    return new String[] {
        "дп",
        "пп"
    };
  }

  @Override
  public String dateFormatFull() {
    return "EEEE, d MMMM y 'р'.";
  }

  @Override
  public String dateFormatLong() {
    return "d MMMM y 'р'.";
  }

  @Override
  public String dateFormatMedium() {
    return "d MMM y 'р'.";
  }

  @Override
  public String dateFormatShort() {
    return "dd.MM.yy";
  }

  @Override
  public String dateTimeFull(String timePattern, String datePattern) {
    return datePattern + " 'о' " + timePattern;
  }

  @Override
  public String dateTimeLong(String timePattern, String datePattern) {
    return datePattern + " 'о' " + timePattern;
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
        "до нашої ери",
        "нашої ери"
    };
  }

  @Override
  public String[] erasShort() {
    return new String[] {
        "до н. е.",
        "н. е."
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
    return "dd.MM";
  }

  @Override
  public String formatYearMonthAbbrev() {
    return "LLL y";
  }

  @Override
  public String formatYearMonthAbbrevDay() {
    return "d MMM y";
  }

  @Override
  public String formatYearMonthFull() {
    return "LLLL y";
  }

  @Override
  public String formatYearMonthFullDay() {
    return "d MMMM y 'р'.";
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
    return "EEE, d MMM y";
  }

  @Override
  public String formatYearQuarterFull() {
    return "QQQQ y 'р'.";
  }

  @Override
  public String formatYearQuarterShort() {
    return "Q y";
  }

  @Override
  public String[] monthsFull() {
    return new String[] {
        "січня",
        "лютого",
        "березня",
        "квітня",
        "травня",
        "червня",
        "липня",
        "серпня",
        "вересня",
        "жовтня",
        "листопада",
        "грудня"
    };
  }

  @Override
  public String[] monthsFullStandalone() {
    return new String[] {
        "січень",
        "лютий",
        "березень",
        "квітень",
        "травень",
        "червень",
        "липень",
        "серпень",
        "вересень",
        "жовтень",
        "листопад",
        "грудень"
    };
  }

  @Override
  public String[] monthsNarrow() {
    return new String[] {
        "с",
        "л",
        "б",
        "к",
        "т",
        "ч",
        "л",
        "с",
        "в",
        "ж",
        "л",
        "г"
    };
  }

  @Override
  public String[] monthsNarrowStandalone() {
    return new String[] {
        "С",
        "Л",
        "Б",
        "К",
        "Т",
        "Ч",
        "Л",
        "С",
        "В",
        "Ж",
        "Л",
        "Г"
    };
  }

  @Override
  public String[] monthsShort() {
    return new String[] {
        "січ.",
        "лют.",
        "бер.",
        "квіт.",
        "трав.",
        "черв.",
        "лип.",
        "серп.",
        "вер.",
        "жовт.",
        "лист.",
        "груд."
    };
  }

  @Override
  public String[] monthsShortStandalone() {
    return new String[] {
        "січ",
        "лют",
        "бер",
        "кві",
        "тра",
        "чер",
        "лип",
        "сер",
        "вер",
        "жов",
        "лис",
        "гру"
    };
  }

  @Override
  public String[] quartersFull() {
    return new String[] {
        "1-й квартал",
        "2-й квартал",
        "3-й квартал",
        "4-й квартал"
    };
  }

  @Override
  public String[] quartersShort() {
    return new String[] {
        "1-й кв.",
        "2-й кв.",
        "3-й кв.",
        "4-й кв."
    };
  }

  @Override
  public String[] weekdaysFull() {
    return new String[] {
        "неділя",
        "понеділок",
        "вівторок",
        "середа",
        "четвер",
        "пʼятниця",
        "субота"
    };
  }

  @Override
  public String[] weekdaysNarrow() {
    return new String[] {
        "Н",
        "П",
        "В",
        "С",
        "Ч",
        "П",
        "С"
    };
  }

  @Override
  public String[] weekdaysShort() {
    return new String[] {
        "нд",
        "пн",
        "вт",
        "ср",
        "чт",
        "пт",
        "сб"
    };
  }
}
