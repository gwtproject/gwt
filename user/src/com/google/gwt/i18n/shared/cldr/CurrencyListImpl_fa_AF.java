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
//  number=$Revision: 6546 $
//  date=$Date: 2012-02-07 13:32:35 -0500 (Tue, 07 Feb 2012) $
//  type=AF

/**
 * Pure Java implementation of CurrencyList for locale "fa_AF".
 */
public class CurrencyListImpl_fa_AF extends CurrencyListImpl_fa {

  @Override
  public CurrencyData getDefault() {
    return new CurrencyDataImpl("AFN", "؋", 0, "؋", "Af.");
  }

  @Override
  protected Map<String, CurrencyData> loadCurrencies() {
    Map<String, CurrencyData> result = super.loadCurrencies();
    result.put("AFN", new CurrencyDataImpl("AFN", "؋", 0, "؋", "Af."));
    result.put("AUD", new CurrencyDataImpl("AUD", "AU$", 2, "AU$", "$"));
    result.put("BND", new CurrencyDataImpl("BND", "BND", 2, "BND", "$"));
    result.put("BYR", new CurrencyDataImpl("BYR", "BYR", 0, "BYR", "BYR"));
    result.put("CAD", new CurrencyDataImpl("CAD", "CA$", 2, "C$", "$"));
    result.put("CHF", new CurrencyDataImpl("CHF", "CHF", 2, "CHF", "CHF"));
    result.put("DKK", new CurrencyDataImpl("DKK", "kr", 2, "kr", "kr"));
    result.put("JPY", new CurrencyDataImpl("JPY", "JP¥", 0, "JP¥", "¥"));
    result.put("MXN", new CurrencyDataImpl("MXN", "MX$", 2, "Mex$", "$"));
    result.put("NLG", new CurrencyDataImpl("NLG", "NLG", 130, "NLG", "NLG"));
    result.put("NOK", new CurrencyDataImpl("NOK", "NOkr", 2, "NOkr", "kr"));
    result.put("SEK", new CurrencyDataImpl("SEK", "kr", 2, "kr", "kr"));
    result.put("SGD", new CurrencyDataImpl("SGD", "S$", 2, "S$", "$"));
    result.put("TJS", new CurrencyDataImpl("TJS", "TJS", 2, "TJS", "Som"));
    result.put("USD", new CurrencyDataImpl("USD", "US$", 2, "US$", "$"));
    return result;
  }

  @Override
  protected Map<String, String> loadCurrencyNames() {
    Map<String, String> result = super.loadCurrencyNames();
    result.put("AFN", "افغانی افغانستان");
    result.put("AUD", "دالر آسترالیا");
    result.put("BND", "دالر برونی");
    result.put("BYR", "روبل روسیهٔ سفید");
    result.put("CAD", "دالر کانادا");
    result.put("CHF", "فرانک سویس");
    result.put("DKK", "کرون دنمارک");
    result.put("JPY", "ین جاپان");
    result.put("MXN", "پزوی مکسیکو");
    result.put("NLG", "گیلدر هالند");
    result.put("NOK", "کرون ناروی");
    result.put("SEK", "کرون سویدن");
    result.put("SGD", "دالر سینگاپور");
    result.put("TJS", "سامانی تاجکستان");
    result.put("USD", "دالر امریکا");
    return result;
  }
}
