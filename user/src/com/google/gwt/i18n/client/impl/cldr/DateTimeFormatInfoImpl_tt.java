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
 * Implementation of DateTimeFormatInfo for the "tt" locale.
 */
public class DateTimeFormatInfoImpl_tt extends DateTimeFormatInfoImpl {

  @Override
  public String dateFormatFull() {
    return "d MMMM, y 'ел', EEEE";
  }

  @Override
  public String dateFormatLong() {
    return "d MMMM, y 'ел'";
  }

  @Override
  public String dateFormatMedium() {
    return "d MMM, y 'ел'";
  }

  @Override
  public String dateFormatShort() {
    return "dd.MM.y";
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
        "безнең эрага кадәр",
        "безнең эра"
    };
  }

  @Override
  public String[] erasShort() {
    return new String[] {
        "б.э.к.",
        "б.э."
    };
  }

  @Override
  public String formatHour24Minute() {
    return "H:mm";
  }

  @Override
  public String formatHour24MinuteSecond() {
    return "H:mm:ss";
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
    return "MMM, y 'ел'";
  }

  @Override
  public String formatYearMonthAbbrevDay() {
    return "d MMM, y 'ел'";
  }

  @Override
  public String formatYearMonthFull() {
    return "MMMM, y 'ел'";
  }

  @Override
  public String formatYearMonthFullDay() {
    return "d MMMM, y 'ел'";
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
    return "EEE, d MMM, y 'ел'";
  }

  @Override
  public String formatYearQuarterFull() {
    return "QQQQ, y 'ел'";
  }

  @Override
  public String formatYearQuarterShort() {
    return "Q, y 'ел'";
  }

  @Override
  public String[] monthsFull() {
    return new String[] {
        "гыйнвар",
        "февраль",
        "март",
        "апрель",
        "май",
        "июнь",
        "июль",
        "август",
        "сентябрь",
        "октябрь",
        "ноябрь",
        "декабрь"
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
        "гыйн.",
        "фев.",
        "мар.",
        "апр.",
        "май",
        "июнь",
        "июль",
        "авг.",
        "сент.",
        "окт.",
        "нояб.",
        "дек."
    };
  }

  @Override
  public String[] quartersFull() {
    return new String[] {
        "1 нче квартал",
        "2 нче квартал",
        "3 нче квартал",
        "4 нче квартал"
    };
  }

  @Override
  public String[] quartersShort() {
    return new String[] {
        "1 нче кв.",
        "2 нче кв.",
        "3 нче кв.",
        "4 нче кв."
    };
  }

  @Override
  public String timeFormatFull() {
    return "H:mm:ss zzzz";
  }

  @Override
  public String timeFormatLong() {
    return "H:mm:ss z";
  }

  @Override
  public String timeFormatMedium() {
    return "H:mm:ss";
  }

  @Override
  public String timeFormatShort() {
    return "H:mm";
  }

  @Override
  public String[] weekdaysFull() {
    return new String[] {
        "якшәмбе",
        "дүшәмбе",
        "сишәмбе",
        "чәршәмбе",
        "пәнҗешәмбе",
        "җомга",
        "шимбә"
    };
  }

  @Override
  public String[] weekdaysNarrow() {
    return new String[] {
        "Я",
        "Д",
        "С",
        "Ч",
        "П",
        "Җ",
        "Ш"
    };
  }

  @Override
  public String[] weekdaysShort() {
    return new String[] {
        "якш.",
        "дүш.",
        "сиш.",
        "чәр.",
        "пәнҗ.",
        "җом.",
        "шим."
    };
  }
}
