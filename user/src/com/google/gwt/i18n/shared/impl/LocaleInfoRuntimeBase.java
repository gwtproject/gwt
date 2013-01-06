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
package com.google.gwt.i18n.shared.impl;

import com.google.gwt.i18n.shared.CurrencyList;
import com.google.gwt.i18n.shared.DateTimeFormatFactory;
import com.google.gwt.i18n.shared.DateTimeFormatInfo;
import com.google.gwt.i18n.shared.ListPatterns;
import com.google.gwt.i18n.shared.LocaleDisplayNames;
import com.google.gwt.i18n.shared.LocaleInfo;
import com.google.gwt.i18n.shared.LocalizedNames;
import com.google.gwt.i18n.shared.NumberConstants;
import com.google.gwt.i18n.shared.NumberFormatFactory;
import com.google.gwt.i18n.shared.VariantSelector;

/**
 * Base class for {@link LocaleInfo} implementations for runtime locales.
 */
public abstract class LocaleInfoRuntimeBase implements LocaleInfo {
  // TODO(jat): actually put this into use

  private com.google.gwt.i18n.shared.LocaleInfo instance;

  @Override
  public final DateTimeFormatFactory dateTimes() {
    ensureInstance();
    return instance.dateTimes();
  }

  @Override
  public final CurrencyList getCurrencyList() {
    ensureInstance();
    return instance.getCurrencyList();
  }

  @Override
  public final DateTimeFormatInfo getDateTimeFormatInfo() {
    ensureInstance();
    return instance.getDateTimeFormatInfo();
  }

  @Override
  public final ListPatterns getListPatterns() {
    ensureInstance();
    return instance.getListPatterns();
  }

  @Override
  public final LocaleDisplayNames getLocaleDisplayNames() {
    ensureInstance();
    return instance.getLocaleDisplayNames();
  }

  @Override
  public final java.lang.String getLocaleName() {
    ensureInstance();
    return instance.getLocaleName();
  }

  @Override
  public final LocalizedNames getLocalizedNames() {
    ensureInstance();
    return instance.getLocalizedNames();
  }

  @Override
  public final NumberConstants getNumberConstants() {
    ensureInstance();
    return instance.getNumberConstants();
  }

  @Override
  public final VariantSelector getOrdinalRule() {
    ensureInstance();
    return instance.getOrdinalRule();
  }

  @Override
  public final VariantSelector getPluralRule() {
    ensureInstance();
    return instance.getPluralRule();
  }

  @Override
  public final boolean isRTL() {
    ensureInstance();
    return instance.isRTL();
  }

  @Override
  public final NumberFormatFactory numbers() {
    ensureInstance();
    return instance.numbers();
  }

  @Override
  public final NumberFormatFactory numbers(boolean forceLatinDigits) {
    ensureInstance();
    return instance.numbers(forceLatinDigits);
  }

  /**
   * Create a concrete {@link LocaleInfo} instance for the specified locale.
   * 
   * @param locale
   * @return
   */
  protected abstract LocaleInfo createInstance(String locale);

  private void ensureInstance() {
    if (instance != null) {
      return;
    }
    instance = createInstance(com.google.gwt.i18n.client.LocaleInfo.getRuntimeLocale());
  }
}
