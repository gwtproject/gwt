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
 * Implementation of DateTimeFormatInfo for the "es_AR" locale.
 */
public class DateTimeFormatInfoImpl_es_AR extends DateTimeFormatInfoImpl_es_419 {

  @Override
  public String[] ampms() {
    return new String[] {
        "a. m.",
        "p. m."
    };
  }

  @Override
  public int firstDayOfTheWeek() {
    return 0;
  }

  @Override
  public String formatHour12MinuteSecond() {
    return "hh:mm:ss";
  }

  @Override
  public String formatYearMonthAbbrev() {
    return "MMM y";
  }

  @Override
  public String formatYearMonthAbbrevDay() {
    return "d 'de' MMM 'de' y";
  }

  @Override
  public String formatYearMonthNum() {
    return "M-y";
  }

  @Override
  public String formatYearMonthWeekdayDay() {
    return "EEE, d MMM y";
  }

  @Override
  public String[] quartersFull() {
    return new String[] {
        "1.er trimestre",
        "2.º trimestre",
        "3.er trimestre",
        "4.º trimestre"
    };
  }

  @Override
  public String[] weekdaysNarrow() {
    return new String[] {
        "D",
        "L",
        "M",
        "M",
        "J",
        "V",
        "S"
    };
  }
}
