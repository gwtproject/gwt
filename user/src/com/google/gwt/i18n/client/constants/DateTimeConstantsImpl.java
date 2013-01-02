/*
 * Copyright 2008 Google Inc.
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
package com.google.gwt.i18n.client.constants;

import com.google.gwt.i18n.client.Constants;

/**
 * DateTimeConstantsImpl class encapsulate a collection of DateTime formatting
 * symbols for use with DateTime format and parse services. This class extends
 * GWT's Constants class. The actual symbol collections are defined in a set of
 * property files named like "DateTimeConstants_xx.properties". GWT will will
 * perform late binding to the property file that specific to user's locale.
 * 
 * @deprecated use {@link com.google.gwt.i18n.shared.DateTimeFormatInfo}
 * instead.  This class will be removed in the second major release after 2.6.
 */
@Deprecated
public interface DateTimeConstantsImpl extends Constants, DateTimeConstants {
  @Override
  String[] ampms();

  @Override
  String[] dateFormats();

  @Override
  String[] eraNames();

  @Override
  String[] eras();

  @Override
  String firstDayOfTheWeek();

  @Override
  String[] months();

  @Override
  String[] narrowMonths();

  @Override
  String[] narrowWeekdays();

  @Override
  String[] quarters();

  @Override
  String[] shortMonths();

  @Override
  String[] shortQuarters();

  @Override
  String[] shortWeekdays();

  @Override
  String[] standaloneMonths();

  @Override
  String[] standaloneNarrowMonths();

  @Override
  String[] standaloneNarrowWeekdays();

  @Override
  String[] standaloneShortMonths();

  @Override
  String[] standaloneShortWeekdays();

  @Override
  String[] standaloneWeekdays();

  @Override
  String[] timeFormats();

  @Override
  String[] weekdays();

  @Override
  String[] weekendRange();
}
