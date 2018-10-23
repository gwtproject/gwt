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
 * Implementation of DateTimeFormatInfo for the "kk" locale.
 */
public class DateTimeFormatInfoImpl_kk extends DateTimeFormatInfoImpl {

  @Override
  public String dateFormatFull() {
    return "y 'ж'. d MMMM, EEEE";
  }

  @Override
  public String dateFormatLong() {
    return "y 'ж'. d MMMM";
  }

  @Override
  public String dateFormatMedium() {
    return "y 'ж'. dd MMM";
  }

  @Override
  public String dateFormatShort() {
    return "dd.MM.yy";
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
        "Біздің заманымызға дейін",
        "біздің заманымыз"
    };
  }

  @Override
  public String[] erasShort() {
    return new String[] {
        "б.з.д.",
        "б.з."
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
    return "d MMMM, EEEE";
  }

  @Override
  public String formatMonthNumDay() {
    return "dd.MM";
  }

  @Override
  public String formatYearMonthAbbrev() {
    return "y 'ж'. MMM";
  }

  @Override
  public String formatYearMonthAbbrevDay() {
    return "y 'ж'. d MMM";
  }

  @Override
  public String formatYearMonthFull() {
    return "y 'ж'. MMMM";
  }

  @Override
  public String formatYearMonthFullDay() {
    return "y 'ж'. d MMMM";
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
    return "y 'ж'. d MMM, EEE";
  }

  @Override
  public String formatYearQuarterFull() {
    return "y 'ж'. QQQQ";
  }

  @Override
  public String formatYearQuarterShort() {
    return "y 'ж'. Q";
  }

  @Override
  public String[] monthsFull() {
    return new String[] {
        "қаңтар",
        "ақпан",
        "наурыз",
        "сәуір",
        "мамыр",
        "маусым",
        "шілде",
        "тамыз",
        "қыркүйек",
        "қазан",
        "қараша",
        "желтоқсан"
    };
  }

  @Override
  public String[] monthsFullStandalone() {
    return new String[] {
        "Қаңтар",
        "Ақпан",
        "Наурыз",
        "Сәуір",
        "Мамыр",
        "Маусым",
        "Шілде",
        "Тамыз",
        "Қыркүйек",
        "Қазан",
        "Қараша",
        "Желтоқсан"
    };
  }

  @Override
  public String[] monthsNarrow() {
    return new String[] {
        "Қ",
        "А",
        "Н",
        "С",
        "М",
        "М",
        "Ш",
        "Т",
        "Қ",
        "Қ",
        "Қ",
        "Ж"
    };
  }

  @Override
  public String[] monthsShort() {
    return new String[] {
        "қаң.",
        "ақп.",
        "нау.",
        "сәу.",
        "мам.",
        "мау.",
        "шіл.",
        "там.",
        "қыр.",
        "қаз.",
        "қар.",
        "жел."
    };
  }

  @Override
  public String[] quartersFull() {
    return new String[] {
        "І тоқсан",
        "ІІ тоқсан",
        "ІІІ тоқсан",
        "IV тоқсан"
    };
  }

  @Override
  public String[] quartersShort() {
    return new String[] {
        "І тқс.",
        "ІІ тқс.",
        "ІІІ тқс.",
        "IV тқс."
    };
  }

  @Override
  public String[] weekdaysFull() {
    return new String[] {
        "жексенбі",
        "дүйсенбі",
        "сейсенбі",
        "сәрсенбі",
        "бейсенбі",
        "жұма",
        "сенбі"
    };
  }

  @Override
  public String[] weekdaysNarrow() {
    return new String[] {
        "Ж",
        "Д",
        "С",
        "С",
        "Б",
        "Ж",
        "С"
    };
  }

  @Override
  public String[] weekdaysShort() {
    return new String[] {
        "жс",
        "дс",
        "сс",
        "ср",
        "бс",
        "жм",
        "сб"
    };
  }
}
