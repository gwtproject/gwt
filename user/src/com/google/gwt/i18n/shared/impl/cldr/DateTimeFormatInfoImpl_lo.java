/*
 * Copyright 2014 Google Inc.
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
//  cldrVersion=25
//  date=$Date: 2014-03-01 06:57:43 +0100 (Sat, 01 Mar 2014) $
//  number=$Revision: 9852 $
//  type=lo

/**
 * Implementation of DateTimeFormatInfo for the "lo" locale.
 */
public class DateTimeFormatInfoImpl_lo extends DateTimeFormatInfoImpl {

  @Override
  public String[] ampms() {
    return new String[] {
        "ກ່ອນທ່ຽງ",
        "ຫຼັງທ່ຽງ"
    };
  }

  @Override
  public String dateFormatFull() {
    return "EEEE ທີ d MMMM G y";
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
    return "d/M/y";
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
        "ກ່ອນ ຄ.ສ.",
        "ຄ.ສ."
    };
  }

  @Override
  public String[] erasShort() {
    return new String[] {
        "ກ່ອນ ຄ.ສ.",
        "ຄ.ສ."
    };
  }

  @Override
  public int firstDayOfTheWeek() {
    return 0;
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
    return "EEEE d MMMM";
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
    return "MMMM y";
  }

  @Override
  public String formatYearMonthFullDay() {
    return "d MMMM y";
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
    return "QQQQ y";
  }

  @Override
  public String formatYearQuarterShort() {
    return "Q y";
  }

  @Override
  public String[] monthsFull() {
    return new String[] {
        "ມັງກອນ",
        "ກຸມພາ",
        "ມີນາ",
        "ເມສາ",
        "ພຶດສະພາ",
        "ມິຖຸນາ",
        "ກໍລະກົດ",
        "ສິງຫາ",
        "ກັນຍາ",
        "ຕຸລາ",
        "ພະຈິກ",
        "ທັນວາ"
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
        "ມ.ກ.",
        "ກ.ພ.",
        "ມ.ນ.",
        "ມ.ສ.",
        "ພ.ພ.",
        "ມິ.ຖ.",
        "ກ.ລ.",
        "ສ.ຫ.",
        "ກ.ຍ.",
        "ຕ.ລ.",
        "ພ.ຈ.",
        "ທ.ວ."
    };
  }

  @Override
  public String[] quartersFull() {
    return new String[] {
        "ໄຕຣມາດ 1",
        "ໄຕຣມາດ 2",
        "ໄຕຣມາດ 3",
        "ໄຕຣມາດ 4"
    };
  }

  @Override
  public String[] quartersShort() {
    return new String[] {
        "ຕມ1",
        "ຕມ2",
        "ຕມ3",
        "ຕມ4"
    };
  }

  @Override
  public String timeFormatFull() {
    return "H ໂມງ m ນາທີ ss ວິນາທີ zzzz";
  }

  @Override
  public String timeFormatLong() {
    return "H ໂມງ m ນາທີ ss ວິນາທີ z";
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
        "ວັນອາທິດ",
        "ວັນຈັນ",
        "ວັນອັງຄານ",
        "ວັນພຸດ",
        "ວັນພະຫັດ",
        "ວັນສຸກ",
        "ວັນເສົາ"
    };
  }

  @Override
  public String[] weekdaysNarrow() {
    return new String[] {
        "1",
        "2",
        "3",
        "4",
        "5",
        "6",
        "7"
    };
  }

  @Override
  public String[] weekdaysNarrowStandalone() {
    return new String[] {
        "ທ",
        "ຈ",
        "ຄ",
        "​ພຸ",
        "ພ",
        "​ສຸ",
        "ສ"
    };
  }

  @Override
  public String[] weekdaysShort() {
    return new String[] {
        "ວັນອາທິດ",
        "ວັນຈັນ",
        "ວັນອັງຄານ",
        "ວັນພຸດ",
        "ວັນພະຫັດ",
        "ວັນສຸກ",
        "ວັນເສົາ"
    };
  }
}
