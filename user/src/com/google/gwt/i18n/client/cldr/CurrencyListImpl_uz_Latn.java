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
//  date=$Date: 2011-05-10 16:26:37 -0400 (Tue, 10 May 2011) $
//  number=$Revision: 5882 $
//  type=root

/**
 * JS implementation of CurrencyList for locale "uz_Latn".
 */
public class CurrencyListImpl_uz_Latn extends CurrencyListImpl_uz {
  
  @Override
  public native CurrencyData getDefault() /*-{
    return [ "UZS", "soʼm", 0, "soʼm", "soʼm"];
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
      "BRL": [ "BRL", "R$", 2, "R$", "R$"],
      "CNY": [ "CNY", "CN¥", 2, "RMB¥", "¥"],
      "EUR": [ "EUR", "€", 2, "€", "€"],
      "GBP": [ "GBP", "UK£", 2, "GB£", "£"],
      "INR": [ "INR", "Rs.", 2, "Rs", "₹"],
      "JPY": [ "JPY", "JP¥", 0, "JP¥", "¥"],
      "RUB": [ "RUB", "руб.", 2, "руб.", "руб."],
      "USD": [ "USD", "US$", 2, "US$", "$"],
      "UZS": [ "UZS", "soʼm", 0, "soʼm", "soʼm"],
    };
  }-*/;
  
  private native JavaScriptObject loadCurrencyNamesOverride() /*-{
    return {
      "BRL": "Brazil reali",
      "CNY": "Xitoy yuani",
      "EUR": "Evro",
      "GBP": "Ingliz funt sterlingi",
      "INR": "Hind rupiyasi",
      "JPY": "Yapon yenasi",
      "RUB": "Rus rubli",
      "USD": "AQSH dollari",
      "UZS": "Oʼzbekiston soʼm",
    };
  }-*/;
}
