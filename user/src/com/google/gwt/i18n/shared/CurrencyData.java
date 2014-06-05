/*
 * Copyright 2009 Google Inc.
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
 * Information about a currency.
 */
public interface CurrencyData {

  /**
   * Returns the ISO4217 code for this currency.
   */
  String getCurrencyCode();

  /**
   * Returns the default symbol to use for this currency.
   */
  String getCurrencySymbol();

  /**
   * Returns the default number of decimal positions for this currency.
   */
  int getDefaultFractionDigits();

  /**
   * Returns true if this currency is deprecated and should not be returned by
   * default in currency lists.
   */
  boolean isDeprecated();

  String currencyPattern();
  
  String decimalSeparator();
  
  String groupingSeparator();
  
  int getRounding();
}
