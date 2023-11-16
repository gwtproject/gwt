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
 * Implementation of DateTimeFormatInfo for the "bs_Cyrl" locale.
 */
public class DateTimeFormatInfoImpl_bs_Cyrl extends DateTimeFormatInfoImpl_bs {

  @Override
  public String[] ampms() {
    return new String[] {
        "пре подне",
        "поподне"
    };
  }

  @Override
  public String dateFormatFull() {
    return "EEEE, dd. MMMM y.";
  }

  @Override
  public String dateFormatLong() {
    return "dd. MMMM y.";
  }

  @Override
  public String dateFormatMedium() {
    return "dd.MM.y.";
  }

  @Override
  public String dateFormatShort() {
    return "d.M.yy.";
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
  public String[] erasFull() {
    return new String[] {
        "прије нове ере",
        "нове ере"
    };
  }

  @Override
  public String[] erasShort() {
    return new String[] {
        "п. н. е.",
        "н. е."
    };
  }

  @Override
  public String formatDay() {
    return "d";
  }

  @Override
  public String formatMonthAbbrevDay() {
    return "dd. MMM";
  }

  @Override
  public String formatMonthFullDay() {
    return "MMMM d";
  }

  @Override
  public String formatMonthFullWeekdayDay() {
    return "EEEE, dd. MMMM";
  }

  @Override
  public String formatMonthNumDay() {
    return "dd.MM.";
  }

  @Override
  public String formatYearMonthAbbrevDay() {
    return "dd. MMM y.";
  }

  @Override
  public String formatYearMonthFull() {
    return "y MMMM";
  }

  @Override
  public String formatYearMonthNum() {
    return "MM.y.";
  }

  @Override
  public String formatYearMonthNumDay() {
    return "dd.MM.y.";
  }

  @Override
  public String formatYearMonthWeekdayDay() {
    return "EEE, dd. MMM y.";
  }

  @Override
  public String formatYearQuarterFull() {
    return "y QQQQ";
  }

  @Override
  public String formatYearQuarterShort() {
    return "y Q";
  }

  @Override
  public String[] monthsFull() {
    return new String[] {
        "јануар",
        "фебруар",
        "март",
        "април",
        "мај",
        "јуни",
        "јули",
        "аугуст",
        "септембар",
        "октобар",
        "новембар",
        "децембар"
    };
  }

  @Override
  public String[] monthsNarrow() {
    return new String[] {
        "ј",
        "ф",
        "м",
        "а",
        "м",
        "ј",
        "ј",
        "а",
        "с",
        "о",
        "н",
        "д"
    };
  }

  @Override
  public String[] monthsShort() {
    return new String[] {
        "јан",
        "феб",
        "мар",
        "апр",
        "мај",
        "јун",
        "јул",
        "ауг",
        "сеп",
        "окт",
        "нов",
        "дец"
    };
  }

  @Override
  public String[] quartersFull() {
    return new String[] {
        "Прво тромесечје",
        "Друго тромесечје",
        "Треће тромесечје",
        "Четврто тромесечје"
    };
  }

  @Override
  public String[] quartersShort() {
    return new String[] {
        "К1",
        "К2",
        "К3",
        "К4"
    };
  }

  @Override
  public String[] weekdaysFull() {
    return new String[] {
        "недјеља",
        "понедјељак",
        "уторак",
        "сриједа",
        "четвртак",
        "петак",
        "субота"
    };
  }

  @Override
  public String[] weekdaysNarrow() {
    return new String[] {
        "н",
        "п",
        "у",
        "с",
        "ч",
        "п",
        "с"
    };
  }

  @Override
  public String[] weekdaysShort() {
    return new String[] {
        "нед",
        "пон",
        "уто",
        "сри",
        "чет",
        "пет",
        "суб"
    };
  }
}
