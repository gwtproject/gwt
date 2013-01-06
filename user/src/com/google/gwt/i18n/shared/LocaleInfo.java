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
package com.google.gwt.i18n.shared;

/**
 * Provides access to information about a locale.
 * <p>
 * <b>Each call to GWT.create for a given locale property value will return the
 * same instance.
 */
public interface LocaleInfo extends Localizable {

  /**
   * Returns a factory for creating {@code DateTimeFormatImpl} instances.  This is
   * guaranteed to always return the same factory for a given {@link LocaleInfo}
   * instance.
   */
  DateTimeFormatFactory dateTimes();
 
  /**
   * Returns a {@link CurrencyList} instance for this locale.
   */
  CurrencyList getCurrencyList();

  /**
   * Returns a {@link DateTimeFormatInfo} instance for this locale.
   */
  DateTimeFormatInfo getDateTimeFormatInfo();

  /**
   * Returns a {@link ListPatterns} instance for this locale.
   */
  ListPatterns getListPatterns();

  /**
   * Returns an instance of {@link LocaleDisplayNames} for this locale.
   * 
   * <b>NOTE:</b> using this method will add a significant amount of size to
   * your application, as names for every locale will be included.
   */
  LocaleDisplayNames getLocaleDisplayNames();

  /**
   * Returns the name of this locale, such as "default, "en_US", etc.
   */
  String getLocaleName();

  /**
   * @return an implementation of {@link LocalizedNames} for this locale.
   */
  LocalizedNames getLocalizedNames();

  /**
   * Returns a {@link NumberConstants} instance for this locale.
   */
  NumberConstants getNumberConstants();

  /**
   * Returns a {@link VariantSelector} instance that implements the ordinal
   * rules for this locale (ie, 1st, 2nd, 3rd, etc).
   */
  VariantSelector getOrdinalRule();

  /**
   * Returns a {@link VariantSelector} instance that implements the plural
   * rules for this locale (ie, 1 tree, 2 trees).
   */
  VariantSelector getPluralRule();

  /**
   * Returns true if this locale is right-to-left instead of left-to-right.
   */
  boolean isRTL();

  /**
   * Returns a factory for creating {@link NumberFormat} instances.  This is
   * guaranteed to always return the same factory for a given {@link LocaleInfo}
   * instance.
   */
  NumberFormatFactory numbers();

  /**
   * Returns a factory for creating {@link NumberFormat} instances.  This is
   * guaranteed to always return the same factory for a given {@link LocaleInfo}
   * instance and forceLatinDigits value.
   * 
   * @param forceLatinDigits true if latin digits should be used for number
   *     formatting and parsing rather than native digits
   */
  NumberFormatFactory numbers(boolean forceLatinDigits);
}
