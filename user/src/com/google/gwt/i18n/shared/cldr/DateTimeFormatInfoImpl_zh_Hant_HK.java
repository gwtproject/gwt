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
package com.google.gwt.i18n.shared.cldr;
// DO NOT EDIT - GENERATED FROM CLDR AND ICU DATA
//  cldrVersion=21.0
//  number=$Revision: 6546 Google $
//  type=root
//  date=$Date: 2012-02-07 13:32:35 -0500 (Tue, 07 Feb 2012) $

/**
 * Implementation of DateTimeFormatInfo for the "zh_Hant_HK" locale.
 */
public class DateTimeFormatInfoImpl_zh_Hant_HK extends DateTimeFormatInfoImpl_zh_Hant {

  @Override
  public String dateFormatMedium() {
    return "y年M月d日";
  }

  @Override
  public String dateFormatShort() {
    return "yy年M月d日";
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
  public String dateTimeMedium(String timePattern, String datePattern) {
    return datePattern + timePattern;
  }

  @Override
  public String dateTimeShort(String timePattern, String datePattern) {
    return datePattern + timePattern;
  }

  @Override
  public String formatHour12MinuteSecond() {
    return "ahh:mm:ss";
  }

  @Override
  public String formatMonthNumDay() {
    return "M-d";
  }

  @Override
  public String timeFormatFull() {
    return "ah:mm:ss [zzzz]";
  }

  @Override
  public String timeFormatLong() {
    return "ah:mm:ss [z]";
  }

  @Override
  public String timeFormatMedium() {
    return "ahh:mm:ss";
  }
}
