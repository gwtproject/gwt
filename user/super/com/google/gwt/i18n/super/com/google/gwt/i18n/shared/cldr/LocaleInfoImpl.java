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
import com.google.gwt.core.client.GWT;

import com.google.gwt.i18n.shared.CurrencyList;
import com.google.gwt.i18n.shared.DateTimeFormatInfo;
import com.google.gwt.i18n.shared.ListPatterns;
import com.google.gwt.i18n.shared.LocaleDisplayNames;
import com.google.gwt.i18n.shared.LocalizedNames;
import com.google.gwt.i18n.shared.NumberConstants;
import com.google.gwt.i18n.shared.impl.LocaleInfoImplBase;

// DO NOT EDIT - GENERATED FROM CLDR AND ICU DATA:
//  cldrVersion=21.0
//  date=$Date: 2012-02-08 14:09:21 -0500 (Wed, 08 Feb 2012) $
//  number=$Revision: 6549 Google $
//  type=root
@GwtScriptOnly   // use the pure Java version in DevMode for speed
public class LocaleInfoImpl extends LocaleInfoImplBase {

  @Override
  public String getLocaleName() {
    return "default";
  }

  @Override
  public boolean isRTL() {
    return false;
  }

  @Override
  protected CurrencyList createCurrencyList() {
    return new com.google.gwt.i18n.client.cldr.CurrencyListImpl();
  }

  @Override
  protected DateTimeFormatInfo createDateTimeFormatInfo() {
    return new com.google.gwt.i18n.shared.cldr.DateTimeFormatInfoImpl();
  }

  @Override
  protected ListPatterns createListPatterns() {
    return new com.google.gwt.i18n.shared.cldr.ListPatternsImpl();
  }

  @Override
  protected LocaleDisplayNames createLocaleDisplayNames() {
    return GWT.create(LocaleDisplayNames.class);
  }

  @Override
  protected LocalizedNames createLocalizedNames() {
    return new com.google.gwt.i18n.client.cldr.LocalizedNamesImpl();
  }

  @Override
  protected NumberConstants createNumberConstants() {
    return new com.google.gwt.i18n.shared.cldr.NumberConstantsImpl();
  }
}
