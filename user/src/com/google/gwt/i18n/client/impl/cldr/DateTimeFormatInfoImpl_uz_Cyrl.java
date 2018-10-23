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
 * Implementation of DateTimeFormatInfo for the "uz_Cyrl" locale.
 */
public class DateTimeFormatInfoImpl_uz_Cyrl extends DateTimeFormatInfoImpl_uz {

  @Override
  public String[] ampms() {
    return new String[] {
        "ТО",
        "ТК"
    };
  }

  @Override
  public String dateFormatFull() {
    return "EEEE, dd MMMM, y";
  }

  @Override
  public String dateFormatLong() {
    return "d MMMM, y";
  }

  @Override
  public String dateFormatMedium() {
    return "d MMM, y";
  }

  @Override
  public String dateTimeFull(String timePattern, String datePattern) {
    return datePattern + " " + timePattern;
  }

  @Override
  public String dateTimeLong(String timePattern, String datePattern) {
    return datePattern + " " + timePattern;
  }

  @Override
  public String dateTimeMedium(String timePattern, String datePattern) {
    return datePattern + " " + timePattern;
  }

  @Override
  public String dateTimeShort(String timePattern, String datePattern) {
    return datePattern + " " + timePattern;
  }

  @Override
  public String[] erasFull() {
    return new String[] {
        "милоддан аввалги",
        "милодий"
    };
  }

  @Override
  public String[] erasShort() {
    return new String[] {
        "м.а.",
        "милодий"
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
  public String formatYearMonthAbbrevDay() {
    return "d MMM, y";
  }

  @Override
  public String formatYearMonthFullDay() {
    return "d MMMM, y";
  }

  @Override
  public String formatYearMonthNum() {
    return "MM/y";
  }

  @Override
  public String[] monthsFull() {
    return new String[] {
        "январ",
        "феврал",
        "март",
        "апрел",
        "май",
        "июн",
        "июл",
        "август",
        "сентябр",
        "октябр",
        "ноябр",
        "декабр"
    };
  }

  @Override
  public String[] monthsNarrow() {
    return new String[] {
        "Я",
        "Ф",
        "М",
        "А",
        "М",
        "И",
        "И",
        "А",
        "С",
        "О",
        "Н",
        "Д"
    };
  }

  @Override
  public String[] monthsShort() {
    return new String[] {
        "янв",
        "фев",
        "мар",
        "апр",
        "май",
        "июн",
        "июл",
        "авг",
        "сен",
        "окт",
        "ноя",
        "дек"
    };
  }

  @Override
  public String[] quartersFull() {
    return new String[] {
        "1-чорак",
        "2-чорак",
        "3-чорак",
        "4-чорак"
    };
  }

  @Override
  public String[] quartersShort() {
    return new String[] {
        "1-ч",
        "2-ч",
        "3-ч",
        "4-ч"
    };
  }

  @Override
  public String timeFormatFull() {
    return "HH:mm:ss (zzzz)";
  }

  @Override
  public String timeFormatLong() {
    return "HH:mm:ss (z)";
  }

  @Override
  public String[] weekdaysFull() {
    return new String[] {
        "якшанба",
        "душанба",
        "сешанба",
        "чоршанба",
        "пайшанба",
        "жума",
        "шанба"
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
        "Ж",
        "Ш"
    };
  }

  @Override
  public String[] weekdaysShort() {
    return new String[] {
        "якш",
        "душ",
        "сеш",
        "чор",
        "пай",
        "жум",
        "шан"
    };
  }
}
