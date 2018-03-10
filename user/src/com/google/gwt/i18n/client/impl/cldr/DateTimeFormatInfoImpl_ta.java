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
 * Implementation of DateTimeFormatInfo for the "ta" locale.
 */
public class DateTimeFormatInfoImpl_ta extends DateTimeFormatInfoImpl {

  @Override
  public String[] ampms() {
    return new String[] {
        "முற்பகல்",
        "பிற்பகல்"
    };
  }

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
    return datePattern + " ’அன்று’ " + timePattern;
  }

  @Override
  public String dateTimeLong(String timePattern, String datePattern) {
    return datePattern + " ’அன்று’ " + timePattern;
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
        "கிறிஸ்துவுக்கு முன்",
        "அன்னோ டோமினி"
    };
  }

  @Override
  public String[] erasShort() {
    return new String[] {
        "கி.மு.",
        "கி.பி."
    };
  }

  @Override
  public int firstDayOfTheWeek() {
    return 0;
  }

  @Override
  public String formatHour12Minute() {
    return "a h:mm";
  }

  @Override
  public String formatHour12MinuteSecond() {
    return "a h:mm:ss";
  }

  @Override
  public String formatMonthFullDay() {
    return "d MMMM";
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
    return "EEE, d MMM, y";
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
        "ஜனவரி",
        "பிப்ரவரி",
        "மார்ச்",
        "ஏப்ரல்",
        "மே",
        "ஜூன்",
        "ஜூலை",
        "ஆகஸ்ட்",
        "செப்டம்பர்",
        "அக்டோபர்",
        "நவம்பர்",
        "டிசம்பர்"
    };
  }

  @Override
  public String[] monthsNarrow() {
    return new String[] {
        "ஜ",
        "பி",
        "மா",
        "ஏ",
        "மே",
        "ஜூ",
        "ஜூ",
        "ஆ",
        "செ",
        "அ",
        "ந",
        "டி"
    };
  }

  @Override
  public String[] monthsShort() {
    return new String[] {
        "ஜன.",
        "பிப்.",
        "மார்.",
        "ஏப்.",
        "மே",
        "ஜூன்",
        "ஜூலை",
        "ஆக.",
        "செப்.",
        "அக்.",
        "நவ.",
        "டிச."
    };
  }

  @Override
  public String[] quartersFull() {
    return new String[] {
        "ஒன்றாம் காலாண்டு",
        "இரண்டாம் காலாண்டு",
        "மூன்றாம் காலாண்டு",
        "நான்காம் காலாண்டு"
    };
  }

  @Override
  public String[] quartersShort() {
    return new String[] {
        "காலா.1",
        "காலா.2",
        "காலா.3",
        "காலா.4"
    };
  }

  @Override
  public String timeFormatFull() {
    return "a h:mm:ss zzzz";
  }

  @Override
  public String timeFormatLong() {
    return "a h:mm:ss z";
  }

  @Override
  public String timeFormatMedium() {
    return "a h:mm:ss";
  }

  @Override
  public String timeFormatShort() {
    return "a h:mm";
  }

  @Override
  public String[] weekdaysFull() {
    return new String[] {
        "ஞாயிறு",
        "திங்கள்",
        "செவ்வாய்",
        "புதன்",
        "வியாழன்",
        "வெள்ளி",
        "சனி"
    };
  }

  @Override
  public String[] weekdaysNarrow() {
    return new String[] {
        "ஞா",
        "தி",
        "செ",
        "பு",
        "வி",
        "வெ",
        "ச"
    };
  }

  @Override
  public String[] weekdaysShort() {
    return new String[] {
        "ஞாயி.",
        "திங்.",
        "செவ்.",
        "புத.",
        "வியா.",
        "வெள்.",
        "சனி"
    };
  }

  @Override
  public int weekendStart() {
    return 0;
  }
}
