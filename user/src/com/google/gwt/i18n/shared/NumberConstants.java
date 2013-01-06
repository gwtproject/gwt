/*
 * Copyright 2007 Google Inc.
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
 * NumberConstants class encapsulate a collection of number formatting 
 * symbols for use with number format and parse services.
 * <p>
 * Use {@link LocaleInfo#getNumberConstants()} to get an instance of this
 * interface for a locale.
 */
public interface NumberConstants {

  /**
   * Returns the locale-specific string representing a NaN.
   */
  String notANumber();

  /**
   * Returns the locale-specific default currency pattern.
   */
  String currencyPattern();

  /**
   * Returns the locale-specific default number format pattern.
   */
  String decimalPattern();

  /**
   * Returns the locale-specific separator between integer and fraction digits.
   */
  String decimalSeparator();

  /**
   * Returns the locale-specific default currency code.
   */
  String defCurrencyCode();

  /**
   * Returns the locale-specific string separating the mantissa from the exponent.
   */
  String exponentialSymbol();

  /**
   * Returns the locale-specific global currency pattern.
   */
  String globalCurrencyPattern();

  /**
   * Returns the locale-specific separator between groups of digits.
   */
  String groupingSeparator();

  /**
   * Returns the locale-specific string representing infinity.
   */
  String infinity();

  /**
   * Returns the locale-specific symbol to represent negative numbers.
   */
  String minusSign();

  /**
   * Returns the locale-specific separator between groups of digits for currency amounts.
   */
  String monetaryGroupingSeparator();

  /**
   * Returns the locale-specific separator between integer and fraction digits for currency amounts.
   */
  String monetarySeparator();

  /**
   * Returns the locale-specific string representing a percentage.
   */
  String percent();

  /**
   * Returns the locale-specific percent pattern.
   */
  String percentPattern();

  /**
   * Returns the locale-specific string representing per-thousand.
   */
  String perMill();

  /**
   * Returns the locale-specific symbol to represent positive numbers.
   */
  String plusSign();

  /**
   * Returns the locale-specific default scientific pattern.
   */
  String scientificPattern();

  /**
   * Returns the locale-specific simple currency pattern.
   */
  String simpleCurrencyPattern();

  /**
   * Returns the locale-specific default zero digit.
   * <p>
   * <b>Note:</b> this must always be a single character, and is represented as a string here for
   * legacy reasons.
   */
  String zeroDigit();
}
