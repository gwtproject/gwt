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
package com.google.gwt.i18n.client.cldr;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.i18n.shared.CurrencyData;

// DO NOT EDIT - GENERATED FROM CLDR AND ICU DATA
//  cldrVersion=21.0
//  number=$Revision: 6546 $
//  date=$Date: 2012-02-07 13:32:35 -0500 (Tue, 07 Feb 2012) $
//  type=root

/**
 * JS implementation of CurrencyList for locale "cy".
 */
public class CurrencyListImpl_cy extends CurrencyListImpl {

  @Override
  public native CurrencyData getDefault() /*-{
    return [ "GBP", "UK£", 2, "GB£", "£"];
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
      "ARS": [ "ARS", "AR$", 2, "AR$", "$"],
      "AUD": [ "AUD", "AU$", 2, "AU$", "$"],
      "BRL": [ "BRL", "R$", 2, "R$", "R$"],
      "BSD": [ "BSD", "BSD", 2, "BSD", "$"],
      "CAD": [ "CAD", "CA$", 2, "C$", "$"],
      "CHF": [ "CHF", "CHF", 2, "CHF", "CHF"],
      "CNY": [ "CNY", "CN¥", 2, "RMB¥", "¥"],
      "EUR": [ "EUR", "€", 2, "€", "€"],
      "GBP": [ "GBP", "UK£", 2, "GB£", "£"],
      "INR": [ "INR", "Rs.", 2, "Rs", "₹"],
      "JPY": [ "JPY", "JP¥", 0, "JP¥", "¥"],
      "KRW": [ "KRW", "₩", 0, "KR₩", "₩"],
      "MXN": [ "MXN", "MX$", 2, "Mex$", "$"],
      "RUB": [ "RUB", "руб.", 2, "руб.", "руб."],
      "TRY": [ "TRY", "YTL", 2, "YTL", "YTL"],
      "USD": [ "USD", "US$", 2, "US$", "$"],
      "ZAR": [ "ZAR", "ZAR", 2, "ZAR", "R"],
    };
  }-*/;

  private native JavaScriptObject loadCurrencyNamesOverride() /*-{
    return {
      "ARS": "Peso yr Ariannin",
      "AUD": "doler Awstralia",
      "BRL": "Real Brasil",
      "BSD": "Doler y Bahamas",
      "CAD": "Doler Canada",
      "CHF": "Ffranc y Swistir",
      "CNY": "Yuan Renminbi Tseina",
      "EUR": "Ewro",
      "GBP": "Punt Sterling Prydain",
      "INR": "Rwpî India",
      "JPY": "Yen Siapan",
      "KRW": "Won De Corea",
      "MXN": "Peso Mecsico",
      "RUB": "Rwbl Rwsia",
      "TRY": "Lira Twrci",
      "USD": "Doler yr UDA",
      "ZAR": "Rand De Affrica",
    };
  }-*/;
}
