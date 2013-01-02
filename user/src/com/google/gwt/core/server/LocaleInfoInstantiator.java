/*
 * Copyright 2013 Google Inc.
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
package com.google.gwt.core.server;

import com.google.gwt.i18n.shared.CurrencyList;
import com.google.gwt.i18n.shared.DateTimeFormatFactory;
import com.google.gwt.i18n.shared.DateTimeFormatInfo;
import com.google.gwt.i18n.shared.GwtLocale;
import com.google.gwt.i18n.shared.ListPatterns;
import com.google.gwt.i18n.shared.LocaleDisplayNames;
import com.google.gwt.i18n.shared.LocaleInfo;
import com.google.gwt.i18n.shared.LocalizedNames;
import com.google.gwt.i18n.shared.NumberConstants;
import com.google.gwt.i18n.shared.NumberFormatFactory;
import com.google.gwt.i18n.shared.VariantSelector;

/**
 * Instantiate {@link LocaleInfo} instances.  This is different than other
 * CLDR instances in that the locale name needs to match the requested value.
 */
public class LocaleInfoInstantiator extends CldrInstantiator {

  private static class LocaleInfoExt implements LocaleInfo {
    private final String localeName;
    private final LocaleInfo localeInfo;

    public LocaleInfoExt(GwtLocale locale, LocaleInfo localeInfo) {
      localeName = locale.toString();
      this.localeInfo = localeInfo;
    }

    @Override
    public String getLocaleName() {
      return localeName;
    }

    @Override
    public DateTimeFormatFactory dateTimes() {
      return localeInfo.dateTimes();
    }

    @Override
    public CurrencyList getCurrencyList() {
      return localeInfo.getCurrencyList();
    }

    @Override
    public DateTimeFormatInfo getDateTimeFormatInfo() {
      return localeInfo.getDateTimeFormatInfo();
    }

    @Override
    public ListPatterns getListPatterns() {
      return localeInfo.getListPatterns();
    }

    @Override
    public LocaleDisplayNames getLocaleDisplayNames() {
      return localeInfo.getLocaleDisplayNames();
    }

    @Override
    public LocalizedNames getLocalizedNames() {
      return localeInfo.getLocalizedNames();
    }

    @Override
    public NumberConstants getNumberConstants() {
      return localeInfo.getNumberConstants();
    }

    @Override
    public VariantSelector getOrdinalRule() {
      return localeInfo.getOrdinalRule();
    }

    @Override
    public VariantSelector getPluralRule() {
      return localeInfo.getPluralRule();
    }

    @Override
    public boolean isRTL() {
      return localeInfo.isRTL();
    }

    @Override
    public NumberFormatFactory numbers() {
      return localeInfo.numbers();
    }

    @Override
    public NumberFormatFactory numbers(boolean forceLatinDigits) {
      return localeInfo.numbers(forceLatinDigits);
    }
  }

  @Override
  @SuppressWarnings("unchecked")
  public <T> T createForLocale(Class<T> clazz, GwtLocale locale) {
    if (!LocaleInfo.class.equals(clazz)) {
      return null;
    }
    LocaleInfo localeInfo = (LocaleInfo) super.createForLocale(clazz, locale);
    if (locale.toString().equals(localeInfo.getLocaleName())) {
      return (T) localeInfo;
    }
    return (T) new LocaleInfoExt(locale, localeInfo);
  }
}
