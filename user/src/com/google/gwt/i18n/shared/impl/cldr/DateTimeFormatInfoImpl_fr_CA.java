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
 * Implementation of DateTimeFormatInfo for the "fr_CA" locale.
 */
public class DateTimeFormatInfoImpl_fr_CA extends DateTimeFormatInfoImpl_fr {

  @Override
  public String[] ampms() {
    return new String[] {
        "a.m.",
        "p.m."
    };
  }

  @Override
  public String dateFormatShort() {
    return "yy-MM-dd";
  }

  @Override
  public String dateTimeMedium(String timePattern, String datePattern) {
    return datePattern + " " + timePattern;
  }

  @Override
  public int firstDayOfTheWeek() {
    return 0;
  }

  @Override
  public String formatHour12Minute() {
    return "h 'h' mm a";
  }

  @Override
  public String formatHour12MinuteSecond() {
    return "h 'h' mm 'min' ss 's' a";
  }

  @Override
  public String formatHour24Minute() {
    return "HH 'h' mm";
  }

  @Override
  public String formatHour24MinuteSecond() {
    return "HH 'h' mm 'min' ss 's'";
  }

  @Override
  public String formatMinuteSecond() {
    return "mm 'min' ss 's'";
  }

  @Override
  public String formatMonthNumDay() {
    return "M-d";
  }

  @Override
  public String formatYearMonthNum() {
    return "y-MM";
  }

  @Override
  public String formatYearMonthNumDay() {
    return "y-MM-dd";
  }

  @Override
  public String[] monthsShort() {
    return new String[] {
        "janv.",
        "févr.",
        "mars",
        "avr.",
        "mai",
        "juin",
        "juill.",
        "août",
        "sept.",
        "oct.",
        "nov.",
        "déc."
    };
  }

  @Override
  public String timeFormatFull() {
    return "HH 'h' mm 'min' ss 's' zzzz";
  }

  @Override
  public String timeFormatLong() {
    return "HH 'h' mm 'min' ss 's' z";
  }

  @Override
  public String timeFormatMedium() {
    return "HH 'h' mm 'min' ss 's'";
  }

  @Override
  public String timeFormatShort() {
    return "HH 'h' mm";
  }
}
