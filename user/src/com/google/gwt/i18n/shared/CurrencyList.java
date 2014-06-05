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

import java.util.Iterator;

/**
 * Access to currency data by code or iteration.
 * <p>
 * Use {@link LocaleInfo#currencyList()} to get an instance of this
 * interface for a locale.
 */
public interface CurrencyList extends Iterable<CurrencyData> {
  
  /**
   * Return the default currency data for this locale.
   */
  CurrencyData getDefault();

  /**
   * Returns an iterator for the list of currencies.
   * 
   * Deprecated currencies will not be included.
   */
  @Override
  Iterator<CurrencyData> iterator();

  /**
   * Returns an iterator for the list of currencies, optionally including
   * deprecated ones. 
   * 
   * @param includeDeprecated true if deprecated currencies should be included
   */
  Iterator<CurrencyData> iterator(boolean includeDeprecated);

  /**
   * Lookup a currency based on the ISO4217 currency code.
   * 
   * @param currencyCode ISO4217 currency code
   * @return currency data, or null if code not found
   */
  CurrencyData lookup(String currencyCode);

  /**
   * Lookup a currency name based on the ISO4217 currency code.
   * 
   * @param currencyCode ISO4217 currency code
   * @return name of the currency, or null if code not found
   */
  String lookupName(String currencyCode);
}
