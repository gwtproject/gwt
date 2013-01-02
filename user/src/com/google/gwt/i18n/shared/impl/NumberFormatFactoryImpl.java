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

import com.google.gwt.i18n.shared.CurrencyData;
import com.google.gwt.i18n.shared.CurrencyList;
import com.google.gwt.i18n.shared.NumberConstants;
import com.google.gwt.i18n.shared.NumberFormat;
import com.google.gwt.i18n.shared.NumberFormatFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * Implementation of {@link NumberFormatFactory}.
 */
public class NumberFormatFactoryImpl implements NumberFormatFactory {

  // Protects all cached* fields and latinNumberConstants/defaultNumberConstants
  private final Object lock = new Object[0];

  private final Map<String, NumberFormat> cachedCurrencyFormats = new HashMap<String, NumberFormat>();
  private NumberFormat cachedDecimalFormat;
  private NumberFormat cachedScientificFormat;
  private NumberFormat cachedPercentFormat;

  private final NumberConstants numberConstants;
  private final CurrencyList currencyList;
  
  /**
   * @param numberConstants
   * @param currencyList
   */
  NumberFormatFactoryImpl(NumberConstants numberConstants, CurrencyList currencyList) {
    this.numberConstants = numberConstants;
    this.currencyList = currencyList;
  }

  private NumberFormat getFormat(String pattern, CurrencyData currencyData,
      boolean userPattern) {
    synchronized (lock) {
      return new NumberFormatImpl(numberConstants, pattern, currencyData, userPattern);
    }
  }

  @Override
  public NumberFormat getCurrencyFormat() {
    return getCurrencyFormat(currencyList.getDefault());
  }

  @Override
  public NumberFormat getCurrencyFormat(CurrencyData currencyData) {
    NumberFormat nf;
    synchronized (lock) {
      nf = cachedCurrencyFormats.get(currencyData.getCurrencyCode());
      if (nf == null) {
        nf = getFormat(numberConstants.currencyPattern(), currencyData, false);
        cachedCurrencyFormats.put(currencyData.getCurrencyCode(), nf);
      }
    }
    return nf;
  }

  @Override
  public NumberFormat getCurrencyFormat(String currencyCode) {
    return getCurrencyFormat(currencyList.lookup(currencyCode));
  }

  @Override
  public NumberFormat getDecimalFormat() {
    synchronized (lock) {
      if (cachedDecimalFormat == null) {
        cachedDecimalFormat = getFormat(numberConstants.decimalPattern(),
            currencyList.getDefault(), false);
      }
    }
    return cachedDecimalFormat;
  }

  @Override
  public NumberFormat getFormat(String pattern) {
    return getFormat(pattern, currencyList.getDefault(), true);
  }

  @Override
  public NumberFormat getFormat(String pattern, CurrencyData currencyData) {
    return getFormat(pattern, currencyData, true);
  }

  @Override
  public NumberFormat getFormat(String pattern, String currencyCode) {
    return getFormat(pattern, currencyList.lookup(currencyCode), true);
  }

  @Override
  public NumberFormat getGlobalCurrencyFormat() {
    return getGlobalCurrencyFormat(currencyList.getDefault());
  }

  @Override
  public NumberFormat getGlobalCurrencyFormat(CurrencyData currencyData) {
    synchronized (lock) {
      return getFormat(numberConstants.globalCurrencyPattern(), currencyData, false);
    }
  }

  @Override
  public NumberFormat getGlobalCurrencyFormat(String currencyCode) {
    return getGlobalCurrencyFormat(currencyList.lookup(currencyCode));
  }

  @Override
  public NumberFormat getPercentFormat() {
    synchronized (lock) {
      if (cachedPercentFormat == null) {
        cachedPercentFormat = getFormat(numberConstants.percentPattern(),
            currencyList.getDefault(), false);
      }
      return cachedPercentFormat;
    }
  }

  @Override
  public NumberFormat getScientificFormat() {
    synchronized (lock) {
      if (cachedScientificFormat == null) {
        cachedScientificFormat = getFormat(numberConstants.scientificPattern(),
            currencyList.getDefault(), false);
      }
      return cachedScientificFormat;
    }
  }

  @Override
  public NumberFormat getSimpleCurrencyFormat() {
    return getSimpleCurrencyFormat(currencyList.getDefault());
  }

  @Override
  public NumberFormat getSimpleCurrencyFormat(CurrencyData currencyData) {
    synchronized (lock) {
      return getFormat(numberConstants.simpleCurrencyPattern(), currencyData, false);
    }
  }

  @Override
  public NumberFormat getSimpleCurrencyFormat(String currencyCode) {
    return getSimpleCurrencyFormat(currencyList.lookup(currencyCode));
  }
}
