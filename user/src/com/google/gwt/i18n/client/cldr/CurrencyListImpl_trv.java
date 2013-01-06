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
//  type=trv

/**
 * JS implementation of CurrencyList for locale "trv".
 */
public class CurrencyListImpl_trv extends CurrencyListImpl {
  
  @Override
  public native CurrencyData getDefault() /*-{
    return [ "TWD", "NT$", 2, "NT$", "NT$"];
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
      "AUD": [ "AUD", "AU$", 2, "AU$", "$"],
      "BRL": [ "BRL", "R$", 2, "R$", "R$"],
      "CNY": [ "CNY", "CN¥", 2, "RMB¥", "¥"],
      "EUR": [ "EUR", "€", 2, "€", "€"],
      "GBP": [ "GBP", "UK£", 2, "GB£", "£"],
      "HKD": [ "HKD", "HK$", 2, "HK$", "$"],
      "INR": [ "INR", "Rs.", 2, "Rs", "₹"],
      "JPY": [ "JPY", "JP¥", 0, "JP¥", "¥"],
      "MOP": [ "MOP", "MOP", 2, "MOP", "MOP"],
      "NOK": [ "NOK", "NOkr", 2, "NOkr", "kr"],
      "RUB": [ "RUB", "руб.", 2, "руб.", "руб."],
      "TWD": [ "TWD", "NT$", 2, "NT$", "NT$"],
      "USD": [ "USD", "US$", 2, "US$", "$"],
      "XXX": [ "XXX", "XXX", 130, "XXX", "XXX"],
    };
  }-*/;
  
  private native JavaScriptObject loadCurrencyNamesOverride() /*-{
    return {
      "AUD": "pila Autaria",
      "BRL": "pila Pajey",
      "CNY": "pila Ipaw",
      "EUR": "pila Irow",
      "GBP": "pila Inglis",
      "HKD": "pila Hong Kong",
      "INR": "pila Intia",
      "JPY": "pila Nihong",
      "MOP": "pila Macao",
      "NOK": "pila Nowey",
      "RUB": "pila Ruski",
      "TWD": "pila Taiwan",
      "USD": "pila America",
      "XXX": "ini klayi pila ni",
    };
  }-*/;
}
