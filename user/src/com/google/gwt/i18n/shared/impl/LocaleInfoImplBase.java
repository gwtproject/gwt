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
import com.google.gwt.i18n.shared.Localizable;
import com.google.gwt.i18n.shared.LocalizedNames;
import com.google.gwt.i18n.shared.NumberConstants;
import com.google.gwt.i18n.shared.NumberFormatFactory;
import com.google.gwt.i18n.shared.VariantSelector;

/**
 * Base class for {@link LocaleInfo} implementations.
 */
public abstract class LocaleInfoImplBase implements LocaleInfo, Localizable {

  /**
   * A placeholder that always returns OTHER.
   */
  private static class EmptyVariantSelector implements VariantSelector {

    private final VariantForm[] forms = new VariantForm[0];

    @Override
    public String getFormDescription(VariantForm form) {
      return null;
    }

    @Override
    public VariantForm[] getForms() {
      return forms;
    }

    @Override
    public VariantForm select(double n) {
      return VariantForm.OTHER;
    }
  }
  
  // protects all mutable fields
  private final Object lock = new Object[0];
  private CurrencyList currencyList;
  private DateTimeFormatFactory dateTimes;
  private DateTimeFormatInfo dateTimeFormatInfo;
  private NumberConstants latinNumberConstants;
  private ListPatterns listPatterns;
  private LocaleDisplayNames localeDisplayNames;
  private LocalizedNames localizedNames;
  private NumberConstants numberConstants;
  private NumberFormatFactory numbers[] = new NumberFormatFactory[2];
  private VariantSelector ordinalRule;
  
  private VariantSelector pluralRule;

  @Override
  public final DateTimeFormatFactory dateTimes() {
    synchronized (lock) {
      if (dateTimes == null) {
        dateTimes = createDateTimeFormatFactory();
      }
    }
    return dateTimes;
  }

  @Override
  public final CurrencyList getCurrencyList() {
    synchronized (lock) {
      if (currencyList == null) {
        currencyList = createCurrencyList();
      }
    }
    return currencyList;
  }

  @Override
  public final DateTimeFormatInfo getDateTimeFormatInfo() {
    synchronized (lock) {
      if (dateTimeFormatInfo == null) {
        dateTimeFormatInfo = createDateTimeFormatInfo();
      }
    }
    return dateTimeFormatInfo;
  }

  @Override
  public final ListPatterns getListPatterns() {
    synchronized (lock) {
      if (listPatterns == null) {
        listPatterns = createListPatterns();
      }
    }
    return listPatterns;
  }

  @Override
  public final LocaleDisplayNames getLocaleDisplayNames() {
    synchronized (lock) {
      if (localeDisplayNames == null) {
        localeDisplayNames = createLocaleDisplayNames();
      }
    }
    return localeDisplayNames;
  }

  @Override
  public abstract String getLocaleName();

  @Override
  public final LocalizedNames getLocalizedNames() {
    synchronized (lock) {
      if (localizedNames == null) {
        localizedNames = createLocalizedNames();
      }
    }
    return localizedNames;
  }

  @Override
  public final NumberConstants getNumberConstants() {
    synchronized (lock) {
      if (numberConstants == null) {
        numberConstants = createNumberConstants();
      }
    }
    return numberConstants;
  }

  @Override
  public VariantSelector getOrdinalRule() {
    synchronized (lock) {
      if (ordinalRule == null) {
        ordinalRule = createOrdinalRule();
      }
    }
    return ordinalRule;
  }

  @Override
  public VariantSelector getPluralRule() {
    synchronized (lock) {
      if (pluralRule == null) {
        pluralRule = createPluralRule();
      }
    }
    return pluralRule;
  }

  @Override
  public abstract boolean isRTL();

  @Override
  public final NumberFormatFactory numbers() {
    return numbers(false);
  }

  @Override
  public final NumberFormatFactory numbers(boolean forceLatinDigits) {
    int idx = forceLatinDigits ? 1 : 0;
    synchronized (lock) {
      if (numbers[idx] == null) {
        numbers[idx] = createNumberFormatFactory(forceLatinDigits);
      }
    }
    return numbers[idx];
  }

  /**
   * @return a {@link CurrencyList} instance.
   */
  protected abstract CurrencyList createCurrencyList();

  /**
   * @return a {@link DateTimeFormatFactory} instance.
   */
  protected DateTimeFormatFactory createDateTimeFormatFactory() {
    return new DateTimeFormatFactoryImpl(getDateTimeFormatInfo());
  }

  /**
   * @return a {@link DateTimeFormatInfo} instance.
   */
  protected abstract DateTimeFormatInfo createDateTimeFormatInfo();

  /**
   * @return a {@link ListPatterns} instance.
   */
  protected abstract ListPatterns createListPatterns();

  /**
   * @return a {@link LocaleDisplayNames} instance.
   */
  protected abstract LocaleDisplayNames createLocaleDisplayNames();

  /**
   * @return a {@link LocalizedNames} instance.
   */
  protected abstract LocalizedNames createLocalizedNames();

  /**
   * @return a {@link NumberConstants} instance.
   */
  protected abstract NumberConstants createNumberConstants();

  /**
   * @return a {@link NumberFormatFactory} instance.
   */
  protected NumberFormatFactory createNumberFormatFactory(boolean forceLatinDigits) {
    NumberConstants numConstants = getNumberConstants();
    if (forceLatinDigits) {
      synchronized (lock) {
        if (latinNumberConstants == null) {
          latinNumberConstants = NumberFormatImpl.createLatinNumberConstants(numConstants);
        }
      }
      numConstants = latinNumberConstants;
    }
    return new NumberFormatFactoryImpl(numConstants, getCurrencyList());
  }

  /**
   * @return an ordinal rule {@link VariantSelector}.
   */
  protected VariantSelector createOrdinalRule() {
    // Default if nothing is defined is to do nothing special
    return new EmptyVariantSelector();
  }

  /**
   * @return a plural rule {@link VariantSelector}.
   */
  protected VariantSelector createPluralRule() {
    // Default if nothing is defined is to do nothing special
    return new EmptyVariantSelector();
  }
}
