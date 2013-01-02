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
package com.google.gwt.i18n.shared.cldr;

import com.google.gwt.i18n.shared.CurrencyData;
import com.google.gwt.i18n.shared.impl.CurrencyDataImpl;

import java.util.Map;

// DO NOT EDIT - GENERATED FROM CLDR AND ICU DATA
//  cldrVersion=21.0
//  number=$Revision: 6546 Google $
//  type=root
//  date=$Date: 2012-02-07 13:32:35 -0500 (Tue, 07 Feb 2012) $

/**
 * Pure Java implementation of CurrencyList for locale "fo".
 */
public class CurrencyListImpl_fo extends CurrencyListImpl {

  @Override
  public CurrencyData getDefault() {
    return new CurrencyDataImpl("DKK", "kr", 2, "kr", "kr");
  }

  @Override
  protected Map<String, CurrencyData> loadCurrencies() {
    Map<String, CurrencyData> result = super.loadCurrencies();
    result.put("DKK", new CurrencyDataImpl("DKK", "kr", 2, "kr", "kr"));
    result.put("EUR", new CurrencyDataImpl("EUR", "€", 2, "€", "€"));
    result.put("ISK", new CurrencyDataImpl("ISK", "kr", 0, "kr", "kr"));
    result.put("NOK", new CurrencyDataImpl("NOK", "NOkr", 2, "NOkr", "kr"));
    result.put("SEK", new CurrencyDataImpl("SEK", "kr", 2, "kr", "kr"));
    result.put("XAG", new CurrencyDataImpl("XAG", "XAG", 130, "XAG", "XAG"));
    result.put("XAU", new CurrencyDataImpl("XAU", "XAU", 130, "XAU", "XAU"));
    result.put("XPD", new CurrencyDataImpl("XPD", "XPD", 130, "XPD", "XPD"));
    result.put("XPT", new CurrencyDataImpl("XPT", "XPT", 130, "XPT", "XPT"));
    return result;
  }

  @Override
  protected Map<String, String> loadCurrencyNames() {
    Map<String, String> result = super.loadCurrencyNames();
    result.put("DKK", "donsk króna");
    result.put("EUR", "euro");
    result.put("ISK", "íslendsk króna");
    result.put("NOK", "norsk króna");
    result.put("SEK", "svensk króna");
    result.put("XAG", "unse sølv");
    result.put("XAU", "unse guld");
    result.put("XPD", "unse palladium");
    result.put("XPT", "unse platin");
    return result;
  }
}
