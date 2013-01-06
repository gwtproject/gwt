/*
 * Copyright 2013 Google Inc.
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
package com.google.gwt.i18n.client.cldr;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.i18n.shared.CurrencyData;

// DO NOT EDIT - GENERATED FROM CLDR AND ICU DATA:
//  cldrVersion=21.0
//  date=$Date: 2012-02-07 13:32:35 -0500 (Tue, 07 Feb 2012) $
//  number=$Revision: 6546 $
//  type=AF

/**
 * JS implementation of CurrencyList for locale "fa_AF".
 */
public class CurrencyListImpl_fa_AF extends CurrencyListImpl_fa {
  
  @Override
  public native CurrencyData getDefault() /*-{
    return [ "AFN", "؋", 0, "؋", "Af."];
  }-*/;
  
  @Override
  protected JavaScriptObject loadCurrencies() {
    return overrideMap(super.loadCurrencies(), loadCurrenciesOverride());
  }
  
  @Override
  protected JavaScriptObject loadCurrencyNames() {
    return overrideMap(super.loadCurrencyNames(), loadCurrencyNamesOverride());
  }
  
  private native JavaScriptObject loadCurrenciesOverride() /*-{
    return {
      "AFN": [ "AFN", "؋", 0, "؋", "Af."],
      "AUD": [ "AUD", "AU$", 2, "AU$", "$"],
      "BND": [ "BND", "BND", 2, "BND", "$"],
      "BYR": [ "BYR", "BYR", 0, "BYR", "BYR"],
      "CAD": [ "CAD", "CA$", 2, "C$", "$"],
      "CHF": [ "CHF", "CHF", 2, "CHF", "CHF"],
      "DKK": [ "DKK", "kr", 2, "kr", "kr"],
      "JPY": [ "JPY", "JP¥", 0, "JP¥", "¥"],
      "MXN": [ "MXN", "MX$", 2, "Mex$", "$"],
      "NLG": [ "NLG", "NLG", 130, "NLG", "NLG"],
      "NOK": [ "NOK", "NOkr", 2, "NOkr", "kr"],
      "SEK": [ "SEK", "kr", 2, "kr", "kr"],
      "SGD": [ "SGD", "S$", 2, "S$", "$"],
      "TJS": [ "TJS", "TJS", 2, "TJS", "Som"],
      "USD": [ "USD", "US$", 2, "US$", "$"],
    };
  }-*/;
  
  private native JavaScriptObject loadCurrencyNamesOverride() /*-{
    return {
      "AFN": "افغانی افغانستان",
      "AUD": "دالر آسترالیا",
      "BND": "دالر برونی",
      "BYR": "روبل روسیهٔ سفید",
      "CAD": "دالر کانادا",
      "CHF": "فرانک سویس",
      "DKK": "کرون دنمارک",
      "JPY": "ین جاپان",
      "MXN": "پزوی مکسیکو",
      "NLG": "گیلدر هالند",
      "NOK": "کرون ناروی",
      "SEK": "کرون سویدن",
      "SGD": "دالر سینگاپور",
      "TJS": "سامانی تاجکستان",
      "USD": "دالر امریکا",
    };
  }-*/;
}
