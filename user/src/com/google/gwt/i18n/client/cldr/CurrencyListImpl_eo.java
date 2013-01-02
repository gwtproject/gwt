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

// DO NOT EDIT - GENERATED FROM CLDR AND ICU DATA
//  cldrVersion=21.0
//  number=$Revision: 5806 $
//  type=root
//  date=$Date: 2011-05-02 14:42:02 -0400 (Mon, 02 May 2011) $

/**
 * JS implementation of CurrencyList for locale "eo".
 */
public class CurrencyListImpl_eo extends CurrencyListImpl {

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
      "INR": [ "INR", "Rs.", 2, "Rs", "₹"],
      "JPY": [ "JPY", "JP¥", 0, "JP¥", "¥"],
      "NOK": [ "NOK", "NOkr", 2, "NOkr", "kr"],
      "RUB": [ "RUB", "руб.", 2, "руб.", "руб."],
      "USD": [ "USD", "US$", 2, "US$", "$"],
      "XAG": [ "XAG", "XAG", 130, "XAG", "XAG"],
      "XAU": [ "XAU", "XAU", 130, "XAU", "XAU"],
      "XBB": [ "XBB", "XBB", 130, "XBB", "XBB"],
      "XFO": [ "XFO", "XFO", 130, "XFO", "XFO"],
      "XPD": [ "XPD", "XPD", 130, "XPD", "XPD"],
      "XPT": [ "XPT", "XPT", 130, "XPT", "XPT"],
    };
  }-*/;

  private native JavaScriptObject loadCurrencyNamesOverride() /*-{
    return {
      "AUD": "aŭstrala dolaro",
      "BRL": "brazila realo",
      "CNY": "ĉina juano",
      "EUR": "eŭro",
      "GBP": "brita sterlinga funto",
      "INR": "hinda rupio",
      "JPY": "japana eno",
      "NOK": "norvega krono",
      "RUB": "rusa rublo",
      "USD": "usona dolaro",
      "XAG": "arĝento",
      "XAU": "oro",
      "XBB": "eŭropa monunuo",
      "XFO": "franca ora franko",
      "XPD": "paladio",
      "XPT": "plateno",
    };
  }-*/;
}
