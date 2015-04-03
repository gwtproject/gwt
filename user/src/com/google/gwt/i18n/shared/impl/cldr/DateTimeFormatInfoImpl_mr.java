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
 * Implementation of DateTimeFormatInfo for the "mr" locale.
 */
public class DateTimeFormatInfoImpl_mr extends DateTimeFormatInfoImpl {

  @Override
  public String dateFormatFull() {
    return "EEEE, d MMMM, y";
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
  public String dateFormatShort() {
    return "d/M/yy";
  }

  @Override
  public String dateTimeFull(String timePattern, String datePattern) {
    return datePattern + " 'रोजी' " + timePattern;
  }

  @Override
  public String dateTimeLong(String timePattern, String datePattern) {
    return datePattern + " 'रोजी' " + timePattern;
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
        "ईसवीसनपूर्व",
        "ईसवीसन"
    };
  }

  @Override
  public String[] erasShort() {
    return new String[] {
        "ईसापूर्व",
        "सन"
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
    return "d MMM, y";
  }

  @Override
  public String formatYearMonthFull() {
    return "MMMM y";
  }

  @Override
  public String formatYearMonthFullDay() {
    return "d MMMM, y";
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
    return "EEE, d, MMM y";
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
        "जानेवारी",
        "फेब्रुवारी",
        "मार्च",
        "एप्रिल",
        "मे",
        "जून",
        "जुलै",
        "ऑगस्ट",
        "सप्टेंबर",
        "ऑक्टोबर",
        "नोव्हेंबर",
        "डिसेंबर"
    };
  }

  @Override
  public String[] monthsNarrow() {
    return new String[] {
        "जा",
        "फे",
        "मा",
        "ए",
        "मे",
        "जू",
        "जु",
        "ऑ",
        "स",
        "ऑ",
        "नो",
        "डि"
    };
  }

  @Override
  public String[] monthsShort() {
    return new String[] {
        "जाने",
        "फेब्रु",
        "मार्च",
        "एप्रि",
        "मे",
        "जून",
        "जुलै",
        "ऑग",
        "सप्टें",
        "ऑक्टो",
        "नोव्हें",
        "डिसें"
    };
  }

  @Override
  public String[] quartersFull() {
    return new String[] {
        "प्रथम तिमाही",
        "द्वितीय तिमाही",
        "तृतीय तिमाही",
        "चतुर्थ तिमाही"
    };
  }

  @Override
  public String[] quartersShort() {
    return new String[] {
        "ति1",
        "ति2",
        "ति3",
        "ति4"
    };
  }

  @Override
  public String timeFormatFull() {
    return "h:mm:ss a zzzz";
  }

  @Override
  public String timeFormatLong() {
    return "h:mm:ss a z";
  }

  @Override
  public String timeFormatMedium() {
    return "h:mm:ss a";
  }

  @Override
  public String timeFormatShort() {
    return "h:mm a";
  }

  @Override
  public String[] weekdaysFull() {
    return new String[] {
        "रविवार",
        "सोमवार",
        "मंगळवार",
        "बुधवार",
        "गुरुवार",
        "शुक्रवार",
        "शनिवार"
    };
  }

  @Override
  public String[] weekdaysNarrow() {
    return new String[] {
        "र",
        "सो",
        "मं",
        "बु",
        "गु",
        "शु",
        "श"
    };
  }

  @Override
  public String[] weekdaysShort() {
    return new String[] {
        "रवि",
        "सोम",
        "मंगळ",
        "बुध",
        "गुरु",
        "शुक्र",
        "शनि"
    };
  }

  @Override
  public int weekendStart() {
    return 0;
  }
}
