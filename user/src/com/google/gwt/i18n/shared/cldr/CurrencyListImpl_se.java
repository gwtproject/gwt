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
//  date=$Date: 2012-02-07 13:32:35 -0500 (Tue, 07 Feb 2012) $
//  type=root

/**
 * Pure Java implementation of CurrencyList for locale "se".
 */
public class CurrencyListImpl_se extends CurrencyListImpl {

  @Override
  public CurrencyData getDefault() {
    return new CurrencyDataImpl("NOK", "NOkr", 2, "NOkr", "kr");
  }

  @Override
  protected Map<String, CurrencyData> loadCurrencies() {
    Map<String, CurrencyData> result = super.loadCurrencies();
    result.put("EUR", new CurrencyDataImpl("EUR", "€", 2, "€", "€"));
    result.put("FIM", new CurrencyDataImpl("FIM", "FIM", 130, "FIM", "FIM"));
    result.put("NOK", new CurrencyDataImpl("NOK", "NOkr", 2, "NOkr", "kr"));
    result.put("SEK", new CurrencyDataImpl("SEK", "kr", 2, "kr", "kr"));
    result.put("XAG", new CurrencyDataImpl("XAG", "XAG", 130, "XAG", "XAG"));
    result.put("XAU", new CurrencyDataImpl("XAU", "XAU", 130, "XAU", "XAU"));
    return result;
  }

  @Override
  protected Map<String, String> loadCurrencyNames() {
    Map<String, String> result = super.loadCurrencyNames();
    result.put("EUR", "euro");
    result.put("FIM", "suoma márkki");
    result.put("NOK", "norgga kruvdno");
    result.put("SEK", "ruoŧŧa kruvdno");
    result.put("XAG", "uns silba");
    result.put("XAU", "uns golli");
    return result;
  }
}
