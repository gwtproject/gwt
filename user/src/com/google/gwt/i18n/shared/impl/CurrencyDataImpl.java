/*
 * Copyright 2014 Google Inc.
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
import com.google.gwt.i18n.shared.Localizable;

/**
 * A POJO for currency data.
 */
public class CurrencyDataImpl implements CurrencyData, Localizable {

  protected final String currencyCode;
  protected final String currencySymbol;

  /**
   * Flags and # of decimal digits.
   * 
   * <pre>
   *       d0-d2: # of decimal digits for this currency, 0-7
   *       d3:    currency symbol goes after number, 0=before
   *       d4:    currency symbol position is based on d3
   *       d5:    space is forced, 0=no space present
   *       d6:    spacing around currency symbol is based on d5
   * </pre>
   */
  protected final int flagsAndPrecision;

  protected final String currencyPattern;
  protected final String decimalSeparator;
  protected final String groupingSeparator;
  protected final int rounding;

  /**
   * Create a new CurrencyData whose portable symbol is the same as its local
   * symbol.
   */
  public CurrencyDataImpl(String currencyCode, String currencySymbol, int flagsAndPrecision) {
    this(currencyCode, currencySymbol, flagsAndPrecision, null, null, null, 0);
  }

  public CurrencyDataImpl(String currencyCode, String currencySymbol,
      int flagsAndPrecision, String currencyPattern, String decimalSeparator,
      String groupingSeparator, int rounding) {
    this.currencyCode = currencyCode;
    this.currencySymbol = currencySymbol;
    this.flagsAndPrecision = flagsAndPrecision;
    this.currencyPattern = currencyPattern;
    this.decimalSeparator = decimalSeparator;
    this.groupingSeparator = groupingSeparator;
    this.rounding = rounding;
  }

  @Override
  public String currencyPattern() {
    return currencyPattern;
  }

  @Override
  public String decimalSeparator() {
    return decimalSeparator;
  }

  @Override
  public String getCurrencyCode() {
    return currencyCode;
  }

  @Override
  public String getCurrencySymbol() {
    return currencySymbol;
  }

  @Override
  public int getDefaultFractionDigits() {
    return CurrencyDataHelper.getDefaultFractionDigits(flagsAndPrecision);
  }

  @Override
  public int getRounding() {
    return rounding;
  }

  @Override
  public String groupingSeparator() {
    return groupingSeparator;
  }

  @Override
  public boolean isDeprecated() {
    return CurrencyDataHelper.isDeprecated(flagsAndPrecision);
  }
}
