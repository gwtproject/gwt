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
 * Implementation of DateTimeFormatInfo for the "pt_PT" locale.
 */
public class DateTimeFormatInfoImpl_pt_PT extends DateTimeFormatInfoImpl_pt {

  @Override
  public String[] ampms() {
    return new String[] {
        "a.m.",
        "p.m."
    };
  }

  @Override
  public String dateFormatMedium() {
    return "dd/MM/y";
  }

  @Override
  public String dateFormatShort() {
    return "dd/MM/yy";
  }

  @Override
  public String dateTimeFull(String timePattern, String datePattern) {
    return datePattern + " 'às' " + timePattern;
  }

  @Override
  public String dateTimeLong(String timePattern, String datePattern) {
    return datePattern + " 'às' " + timePattern;
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
  public String formatMonthAbbrevDay() {
    return "d/MM";
  }

  @Override
  public String formatMonthFullWeekdayDay() {
    return "cccc, d 'de' MMMM";
  }

  @Override
  public String formatMonthNumDay() {
    return "dd/MM";
  }

  @Override
  public String formatYearMonthAbbrev() {
    return "MM/y";
  }

  @Override
  public String formatYearMonthAbbrevDay() {
    return "d/MM/y";
  }

  @Override
  public String formatYearMonthWeekdayDay() {
    return "EEE, d/MM/y";
  }

  @Override
  public String[] quartersFull() {
    return new String[] {
        "1.º trimestre",
        "2.º trimestre",
        "3.º trimestre",
        "4.º trimestre"
    };
  }

  @Override
  public String[] weekdaysShort() {
    return new String[] {
        "domingo",
        "segunda",
        "terça",
        "quarta",
        "quinta",
        "sexta",
        "sábado"
    };
  }
}
