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
 * Implementation of DateTimeFormatInfo for the "br" locale.
 */
public class DateTimeFormatInfoImpl_br extends DateTimeFormatInfoImpl {

  @Override
  public String[] ampms() {
    return new String[] {
        "A.M.",
        "G.M."
    };
  }

  @Override
  public String dateFormatFull() {
    return "EEEE d MMMM y";
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
    return "dd/MM/y";
  }

  @Override
  public String dateTimeFull(String timePattern, String datePattern) {
    return datePattern + " 'da' " + timePattern;
  }

  @Override
  public String dateTimeLong(String timePattern, String datePattern) {
    return datePattern + " 'da' " + timePattern;
  }

  @Override
  public String dateTimeMedium(String timePattern, String datePattern) {
    return datePattern + ", " + timePattern;
  }

  @Override
  public String[] erasFull() {
    return new String[] {
        "a-raok Jezuz-Krist",
        "goude Jezuz-Krist"
    };
  }

  @Override
  public String[] erasShort() {
    return new String[] {
        "a-raok J.K.",
        "goude J.K."
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
    return "EEEE d MMMM";
  }

  @Override
  public String formatMonthNumDay() {
    return "dd/MM";
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
    return "MM/y";
  }

  @Override
  public String formatYearMonthNumDay() {
    return "dd/MM/y";
  }

  @Override
  public String formatYearMonthWeekdayDay() {
    return "EEE d MMM y";
  }

  @Override
  public String formatYearQuarterFull() {
    return "QQQQ y";
  }

  @Override
  public String formatYearQuarterShort() {
    return "Q y";
  }

  @Override
  public String[] monthsFull() {
    return new String[] {
        "Genver",
        "Cʼhwevrer",
        "Meurzh",
        "Ebrel",
        "Mae",
        "Mezheven",
        "Gouere",
        "Eost",
        "Gwengolo",
        "Here",
        "Du",
        "Kerzu"
    };
  }

  @Override
  public String[] monthsNarrow() {
    return new String[] {
        "01",
        "02",
        "03",
        "04",
        "05",
        "06",
        "07",
        "08",
        "09",
        "10",
        "11",
        "12"
    };
  }

  @Override
  public String[] monthsShort() {
    return new String[] {
        "Gen.",
        "Cʼhwe.",
        "Meur.",
        "Ebr.",
        "Mae",
        "Mezh.",
        "Goue.",
        "Eost",
        "Gwen.",
        "Here",
        "Du",
        "Kzu."
    };
  }

  @Override
  public String[] quartersFull() {
    return new String[] {
        "1añ trimiziad",
        "2l trimiziad",
        "3e trimiziad",
        "4e trimiziad"
    };
  }

  @Override
  public String[] quartersShort() {
    return new String[] {
        "1añ trim.",
        "2l trim.",
        "3e trim.",
        "4e trim."
    };
  }

  @Override
  public String[] weekdaysFull() {
    return new String[] {
        "Sul",
        "Lun",
        "Meurzh",
        "Mercʼher",
        "Yaou",
        "Gwener",
        "Sadorn"
    };
  }

  @Override
  public String[] weekdaysNarrow() {
    return new String[] {
        "Su",
        "L",
        "Mz",
        "Mc",
        "Y",
        "G",
        "Sa"
    };
  }

  @Override
  public String[] weekdaysShort() {
    return new String[] {
        "Sul",
        "Lun",
        "Meu.",
        "Mer.",
        "Yaou",
        "Gwe.",
        "Sad."
    };
  }
}
