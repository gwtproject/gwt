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
package com.google.gwt.i18n.client;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.i18n.shared.CurrencyData;
import com.google.gwt.i18n.shared.LocaleInfo;
import com.google.gwt.i18n.shared.NumberFormatFactory;

/**
 * Formats and parses numbers using locale-sensitive patterns.
 *
 * @deprecated use {@link com.google.gwt.i18n.shared.NumberFormatFactory} instead
 */
@Deprecated
public class NumberFormat implements com.google.gwt.i18n.shared.NumberFormat {

  private static final LocaleInfo localeInfo = GWT.create(LocaleInfo.class);
  private static final NumberFormatFactory latinFactory = localeInfo.numbers(true);
  private static final NumberFormatFactory nativeFactory = localeInfo.numbers(false);
  
  private static boolean useLatinDigits = false;

  /**
   * Returns true if all new NumberFormat instances will use latin digits and
   * related characters rather than the localized ones.
   */
  public static boolean forcedLatinDigits() {
    return useLatinDigits;
  }

  /**
   * Provides the standard currency format for the current locale.
   *
   * @return a <code>NumberFormat</code> capable of producing and consuming
   *         currency format for the default locale
   */
  public static NumberFormat getCurrencyFormat() {
    return new NumberFormat(factory().getCurrencyFormat());
  }

  /**
   * Provides the standard currency format for the current locale using a
   * specified currency.
   *
   * @param currencyData currency data to use
   * @return a <code>NumberFormat</code> capable of producing and consuming
   *         currency format for the current locale
   */
  public static NumberFormat getCurrencyFormat(CurrencyData currencyData) {
    return new NumberFormat(factory().getCurrencyFormat(currencyData));
  }

  /**
   * Provides the standard currency format for the current locale using a
   * specified currency.
   *
   * @param currencyCode valid currency code, as defined in
   *     com.google.gwt.i18n.client.constants.CurrencyCodeMapConstants.properties
   * @return a <code>NumberFormat</code> capable of producing and consuming
   *         currency format for the current locale
   * @throws IllegalArgumentException if the currency code is unknown
   */
  public static NumberFormat getCurrencyFormat(String currencyCode) {
    return new NumberFormat(factory().getCurrencyFormat(currencyCode));
  }

  /**
   * Provides the standard decimal format for the default locale.
   *
   * @return a <code>NumberFormat</code> capable of producing and consuming
   *         decimal format for the default locale
   */
  public static NumberFormat getDecimalFormat() {
    return new NumberFormat(factory().getDecimalFormat());
  }
  /**
   * Gets a <code>NumberFormat</code> instance for the default locale using
   * the specified pattern and the default currencyCode.
   *
   * @param pattern pattern for this formatter
   * @return a NumberFormat instance
   * @throws IllegalArgumentException if the specified pattern is invalid
   */
  public static NumberFormat getFormat(String pattern) {
    return new NumberFormat(factory().getFormat(pattern));
  }

  /**
   * Gets a custom <code>NumberFormat</code> instance for the default locale
   * using the specified pattern and currency code.
   *
   * @param pattern pattern for this formatter
   * @param currencyData currency data
   * @return a NumberFormat instance
   * @throws IllegalArgumentException if the specified pattern is invalid
   */
  public static NumberFormat getFormat(String pattern, CurrencyData currencyData) {
    return new NumberFormat(factory().getFormat(pattern, currencyData));
  }

  /**
   * Gets a custom <code>NumberFormat</code> instance for the default locale
   * using the specified pattern and currency code.
   *
   * @param pattern pattern for this formatter
   * @param currencyCode international currency code
   * @return a NumberFormat instance
   * @throws IllegalArgumentException if the specified pattern is invalid
   *     or the currency code is unknown
   */
  public static NumberFormat getFormat(String pattern, String currencyCode) {
    return new NumberFormat(factory().getFormat(pattern, currencyCode));
  }

  /**
   * Provides the global currency format for the current locale, using its
   * default currency.
   * 
   * @return a <code>NumberFormat</code> capable of producing and consuming
   *         currency format for the current locale
   */
  public static NumberFormat getGlobalCurrencyFormat() {
    return new NumberFormat(factory().getGlobalCurrencyFormat());
  }

  /**
   * Provides the global currency format for the current locale, using a
   * specified currency.
   *
   * @param currencyData currency data to use
   * @return a <code>NumberFormat</code> capable of producing and consuming
   *         currency format for the current locale
   */
  public static NumberFormat getGlobalCurrencyFormat(CurrencyData currencyData) {
    return new NumberFormat(factory().getGlobalCurrencyFormat(currencyData));
  }

  /**
   * Provides the global currency format for the current locale, using a
   * specified currency.
   *
   * @param currencyCode valid currency code, as defined in
   *     com.google.gwt.i18n.client.constants.CurrencyCodeMapConstants.properties
   * @return a <code>NumberFormat</code> capable of producing and consuming
   *         currency format for the current locale
   * @throws IllegalArgumentException if the currency code is unknown
   */
  public static NumberFormat getGlobalCurrencyFormat(String currencyCode) {
    return new NumberFormat(factory().getGlobalCurrencyFormat(currencyCode));
  }


  /**
   * Provides the standard percent format for the default locale.
   *
   * @return a <code>NumberFormat</code> capable of producing and consuming
   *         percent format for the default locale
   */
  public static NumberFormat getPercentFormat() {
    return new NumberFormat(factory().getPercentFormat());
  }

  /**
   * Provides the standard scientific format for the default locale.
   *
   * @return a <code>NumberFormat</code> capable of producing and consuming
   *         scientific format for the default locale
   */
  public static NumberFormat getScientificFormat() {
    return new NumberFormat(factory().getScientificFormat());
  }

  /**
   * Provides the simple currency format for the current locale using its
   * default currency. Note that these formats may be ambiguous if the
   * currency isn't clear from other content on the page.
   *
   * @return a <code>NumberFormat</code> capable of producing and consuming
   *         currency format for the current locale
   */
  public static NumberFormat getSimpleCurrencyFormat() {
    return new NumberFormat(factory().getSimpleCurrencyFormat());
  }

  /**
   * Provides the simple currency format for the current locale using a
   * specified currency. Note that these formats may be ambiguous if the
   * currency isn't clear from other content on the page.
   *
   * @param currencyData currency data to use
   * @return a <code>NumberFormat</code> capable of producing and consuming
   *         currency format for the current locale
   */
  public static NumberFormat getSimpleCurrencyFormat(CurrencyData currencyData) {
    return new NumberFormat(factory().getSimpleCurrencyFormat(currencyData));
  }

  /**
   * Provides the simple currency format for the current locale using a
   * specified currency. Note that these formats may be ambiguous if the
   * currency isn't clear from other content on the page.
   * 
   * @param currencyCode valid currency code, as defined in
   *        com.google.gwt.i18n.client
   *        .constants.CurrencyCodeMapConstants.properties
   * @return a <code>NumberFormat</code> capable of producing and consuming
   *         currency format for the current locale
   * @throws IllegalArgumentException if the currency code is unknown
   */
  public static NumberFormat getSimpleCurrencyFormat(String currencyCode) {
    return new NumberFormat(factory().getSimpleCurrencyFormat(currencyCode));
  }

  /**
   * Specify whether all new NumberFormat instances will use latin digits
   * and related characters rather than the localized ones.
   *
   * @param useLatinDigits true if latin digits/etc should be used, false if
   *    localized digits/etc should be used.
   */
  public static void setForcedLatinDigits(boolean useLatinDigits) {
    NumberFormat.useLatinDigits = useLatinDigits;
  }

  private static NumberFormatFactory factory() {
    return useLatinDigits ? latinFactory : nativeFactory;
  }

  private com.google.gwt.i18n.shared.NumberFormat instance;
  
  protected NumberFormat(com.google.gwt.i18n.shared.NumberFormat numberFormat) {
    instance = numberFormat;
  }

  @Override
  public String format(double number) {
    return instance.format(number);
  }

  @Override
  public String format(Number number) {
    return instance.format(number);
  }

  @Override
  public String getPattern() {
    return instance.getPattern();
  }

  @Override
  public NumberFormat overrideFractionDigits(int digits) {
    instance = instance.overrideFractionDigits(digits);
    return this;
  }

  @Override
  public NumberFormat overrideFractionDigits(int minDigits, int maxDigits) {
    instance = instance.overrideFractionDigits(minDigits, maxDigits);
    return this;
  }

  @Override
  public double parse(String text) throws NumberFormatException {
    return instance.parse(text);
  }

  @Override
  public double parse(String text, int[] inOutPos) throws NumberFormatException {
    return instance.parse(text, inOutPos);
  }
}
