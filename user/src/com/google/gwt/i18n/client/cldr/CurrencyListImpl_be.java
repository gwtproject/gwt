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
//  number=$Revision: 6546 Google $
//  type=be

/**
 * JS implementation of CurrencyList for locale "be".
 */
public class CurrencyListImpl_be extends CurrencyListImpl {
  
  @Override
  public native CurrencyData getDefault() /*-{
    return [ "BYR", "BYR", 0, "BYR", "BYR"];
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
      "BYB": [ "BYB", "Руб", 130, "Руб", "Руб"],
      "BYR": [ "BYR", "BYR", 0, "BYR", "BYR"],
      "CNY": [ "CNY", "CN¥", 2, "RMB¥", "¥"],
      "ERN": [ "ERN", "ERN", 2, "ERN", "Nfk"],
      "EUR": [ "EUR", "€", 2, "€", "€"],
      "GBP": [ "GBP", "UK£", 2, "GB£", "£"],
      "INR": [ "INR", "Rs.", 2, "Rs", "₹"],
      "JPY": [ "JPY", "¥", 0, "JP¥", "¥"],
      "NOK": [ "NOK", "NOkr", 2, "NOkr", "kr"],
      "RUB": [ "RUB", "рас. руб.", 2, "руб.", "руб."],
      "USD": [ "USD", "$", 2, "US$", "$"],
      "XXX": [ "XXX", "XXX", 130, "XXX", "XXX"],
    };
  }-*/;
  
  private native JavaScriptObject loadCurrencyNamesOverride() /*-{
    return {
      "AUD": "аўстралійскі даляр",
      "BRL": "бразільскі рэал",
      "BYB": "BYB",
      "BYR": "беларускі рубель",
      "CNY": "кітайскі юань",
      "ERN": "эрытрэйская накфа",
      "EUR": "еўра",
      "GBP": "англійскі фунт",
      "INR": "індыйская рупія",
      "JPY": "японская іена",
      "NOK": "нарвэская крона",
      "RUB": "рускі рубель",
      "USD": "долар ЗША",
      "XXX": "невядомая або недапушчальная валюта",
    };
  }-*/;
}
