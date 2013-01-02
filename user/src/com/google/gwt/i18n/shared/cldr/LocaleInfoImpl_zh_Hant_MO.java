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

import com.google.gwt.i18n.shared.CurrencyList;
import com.google.gwt.i18n.shared.DateTimeFormatInfo;
import com.google.gwt.i18n.shared.NumberConstants;

// DO NOT EDIT - GENERATED FROM CLDR AND ICU DATA:
//  cldrVersion=21.0
//  date=$Date: 2011-11-24 04:07:30 -0500 (Thu, 24 Nov 2011) $
//  number=$Revision: 6255 $
//  type=MO
public class LocaleInfoImpl_zh_Hant_MO extends LocaleInfoImpl_zh_Hant {

  @Override
  public String getLocaleName() {
    return "zh_Hant_MO";
  }

  @Override
  protected CurrencyList createCurrencyList() {
    return new com.google.gwt.i18n.shared.cldr.CurrencyListImpl_zh_Hant_MO();
  }

  @Override
  protected DateTimeFormatInfo createDateTimeFormatInfo() {
    return new com.google.gwt.i18n.shared.cldr.DateTimeFormatInfoImpl_zh_Hant_MO();
  }

  @Override
  protected NumberConstants createNumberConstants() {
    return new com.google.gwt.i18n.shared.cldr.NumberConstantsImpl_zh_Hant_MO();
  }
}
