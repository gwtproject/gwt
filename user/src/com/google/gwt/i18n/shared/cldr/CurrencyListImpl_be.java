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
//  type=be
//  date=$Date: 2012-02-07 13:32:35 -0500 (Tue, 07 Feb 2012) $

/**
 * Pure Java implementation of CurrencyList for locale "be".
 */
public class CurrencyListImpl_be extends CurrencyListImpl {

  @Override
  public CurrencyData getDefault() {
    return new CurrencyDataImpl("BYR", "BYR", 0, "BYR", "BYR");
  }

  @Override
  protected Map<String, CurrencyData> loadCurrencies() {
    Map<String, CurrencyData> result = super.loadCurrencies();
    result.put("AUD", new CurrencyDataImpl("AUD", "AU$", 2, "AU$", "$"));
    result.put("BRL", new CurrencyDataImpl("BRL", "R$", 2, "R$", "R$"));
    result.put("BYB", new CurrencyDataImpl("BYB", "Руб", 130, "Руб", "Руб"));
    result.put("BYR", new CurrencyDataImpl("BYR", "BYR", 0, "BYR", "BYR"));
    result.put("CNY", new CurrencyDataImpl("CNY", "CN¥", 2, "RMB¥", "¥"));
    result.put("ERN", new CurrencyDataImpl("ERN", "ERN", 2, "ERN", "Nfk"));
    result.put("EUR", new CurrencyDataImpl("EUR", "€", 2, "€", "€"));
    result.put("GBP", new CurrencyDataImpl("GBP", "UK£", 2, "GB£", "£"));
    result.put("INR", new CurrencyDataImpl("INR", "Rs.", 2, "Rs", "₹"));
    result.put("JPY", new CurrencyDataImpl("JPY", "¥", 0, "JP¥", "¥"));
    result.put("NOK", new CurrencyDataImpl("NOK", "NOkr", 2, "NOkr", "kr"));
    result.put("RUB", new CurrencyDataImpl("RUB", "рас. руб.", 2, "руб.", "руб."));
    result.put("USD", new CurrencyDataImpl("USD", "$", 2, "US$", "$"));
    result.put("XXX", new CurrencyDataImpl("XXX", "XXX", 130, "XXX", "XXX"));
    return result;
  }

  @Override
  protected Map<String, String> loadCurrencyNames() {
    Map<String, String> result = super.loadCurrencyNames();
    result.put("AUD", "аўстралійскі даляр");
    result.put("BRL", "бразільскі рэал");
    result.put("BYB", "BYB");
    result.put("BYR", "беларускі рубель");
    result.put("CNY", "кітайскі юань");
    result.put("ERN", "эрытрэйская накфа");
    result.put("EUR", "еўра");
    result.put("GBP", "англійскі фунт");
    result.put("INR", "індыйская рупія");
    result.put("JPY", "японская іена");
    result.put("NOK", "нарвэская крона");
    result.put("RUB", "рускі рубель");
    result.put("USD", "долар ЗША");
    result.put("XXX", "невядомая або недапушчальная валюта");
    return result;
  }
}
