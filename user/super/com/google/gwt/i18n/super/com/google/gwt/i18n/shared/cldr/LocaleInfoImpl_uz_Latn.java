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

import com.google.gwt.core.client.GwtScriptOnly;

import com.google.gwt.i18n.shared.CurrencyList;
import com.google.gwt.i18n.shared.DateTimeFormatInfo;
import com.google.gwt.i18n.shared.LocaleDisplayNames;
import com.google.gwt.i18n.shared.LocalizedNames;

// DO NOT EDIT - GENERATED FROM CLDR AND ICU DATA:
//  cldrVersion=21.0
//  date=$Date: 2011-05-10 16:26:37 -0400 (Tue, 10 May 2011) $
//  number=$Revision: 5882 $
//  type=root
@GwtScriptOnly   // use the pure Java version in DevMode for speed
public class LocaleInfoImpl_uz_Latn extends LocaleInfoImpl_uz {

  @Override
  public String getLocaleName() {
    return "uz_Latn";
  }

  @Override
  protected CurrencyList createCurrencyList() {
    return new com.google.gwt.i18n.client.cldr.CurrencyListImpl_uz_Latn();
  }

  @Override
  protected DateTimeFormatInfo createDateTimeFormatInfo() {
    return new com.google.gwt.i18n.shared.cldr.DateTimeFormatInfoImpl_uz_Latn();
  }

  @Override
  protected LocalizedNames createLocalizedNames() {
    return new com.google.gwt.i18n.client.cldr.LocalizedNamesImpl_uz_Latn();
  }
}
