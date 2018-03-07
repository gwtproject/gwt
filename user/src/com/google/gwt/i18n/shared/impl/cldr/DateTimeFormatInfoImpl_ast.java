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
 * Implementation of DateTimeFormatInfo for the "ast" locale.
 */
public class DateTimeFormatInfoImpl_ast extends DateTimeFormatInfoImpl {

  @Override
  public String dateFormatFull() {
    return "EEEE, d MMMM 'de' y";
  }

  @Override
  public String dateFormatLong() {
    return "d MMMM 'de' y";
  }

  @Override
  public String dateFormatMedium() {
    return "d MMM y";
  }

  @Override
  public String dateFormatShort() {
    return "d/M/yy";
  }

  @Override
  public String dateTimeFull(String timePattern, String datePattern) {
    return datePattern + " 'a' 'les' " + timePattern;
  }

  @Override
  public String dateTimeLong(String timePattern, String datePattern) {
    return datePattern + " 'a' 'les' " + timePattern;
  }

  @Override
  public String dateTimeMedium(String timePattern, String datePattern) {
    return datePattern + ", " + timePattern;
  }

  @Override
  public String[] erasFull() {
    return new String[] {
        "enantes de Cristu",
        "después de Cristu"
    };
  }

  @Override
  public String[] erasShort() {
    return new String[] {
        "e.C.",
        "d.C."
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
    return "d/M";
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
    return "LLLL 'de' y";
  }

  @Override
  public String formatYearMonthFullDay() {
    return "d MMMM 'de' y";
  }

  @Override
  public String formatYearMonthNum() {
    return "M/y";
  }

  @Override
  public String formatYearMonthNumDay() {
    return "d/M/y";
  }

  @Override
  public String formatYearMonthWeekdayDay() {
    return "EEE, d MMM y";
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
        "de xineru",
        "de febreru",
        "de marzu",
        "d’abril",
        "de mayu",
        "de xunu",
        "de xunetu",
        "d’agostu",
        "de setiembre",
        "d’ochobre",
        "de payares",
        "d’avientu"
    };
  }

  @Override
  public String[] monthsFullStandalone() {
    return new String[] {
        "xineru",
        "febreru",
        "marzu",
        "abril",
        "mayu",
        "xunu",
        "xunetu",
        "agostu",
        "setiembre",
        "ochobre",
        "payares",
        "avientu"
    };
  }

  @Override
  public String[] monthsNarrow() {
    return new String[] {
        "X",
        "F",
        "M",
        "A",
        "M",
        "X",
        "X",
        "A",
        "S",
        "O",
        "P",
        "A"
    };
  }

  @Override
  public String[] monthsShort() {
    return new String[] {
        "xin",
        "feb",
        "mar",
        "abr",
        "may",
        "xun",
        "xnt",
        "ago",
        "set",
        "och",
        "pay",
        "avi"
    };
  }

  @Override
  public String[] monthsShortStandalone() {
    return new String[] {
        "Xin",
        "Feb",
        "Mar",
        "Abr",
        "May",
        "Xun",
        "Xnt",
        "Ago",
        "Set",
        "Och",
        "Pay",
        "Avi"
    };
  }

  @Override
  public String[] quartersFull() {
    return new String[] {
        "1er trimestre",
        "2u trimestre",
        "3er trimestre",
        "4u trimestre"
    };
  }

  @Override
  public String[] quartersShort() {
    return new String[] {
        "1T",
        "2T",
        "3T",
        "4T"
    };
  }

  @Override
  public String[] weekdaysFull() {
    return new String[] {
        "domingu",
        "llunes",
        "martes",
        "miércoles",
        "xueves",
        "vienres",
        "sábadu"
    };
  }

  @Override
  public String[] weekdaysNarrow() {
    return new String[] {
        "D",
        "L",
        "M",
        "M",
        "X",
        "V",
        "S"
    };
  }

  @Override
  public String[] weekdaysShort() {
    return new String[] {
        "dom",
        "llu",
        "mar",
        "mié",
        "xue",
        "vie",
        "sáb"
    };
  }
}
