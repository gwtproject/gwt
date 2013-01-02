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
//  number=$Revision: 5912 $
//  type=root
//  date=$Date: 2011-06-19 12:53:49 -0400 (Sun, 19 Jun 2011) $

/**
 * Pure Java implementation of CurrencyList for locale "uz".
 */
public class CurrencyListImpl_uz extends CurrencyListImpl {

  @Override
  protected Map<String, CurrencyData> loadCurrencies() {
    Map<String, CurrencyData> result = super.loadCurrencies();
    result.put("BRL", new CurrencyDataImpl("BRL", "R$", 2, "R$", "R$"));
    result.put("CNY", new CurrencyDataImpl("CNY", "CN¥", 2, "RMB¥", "¥"));
    result.put("EUR", new CurrencyDataImpl("EUR", "€", 2, "€", "€"));
    result.put("GBP", new CurrencyDataImpl("GBP", "UK£", 2, "GB£", "£"));
    result.put("INR", new CurrencyDataImpl("INR", "Rs.", 2, "Rs", "₹"));
    result.put("JPY", new CurrencyDataImpl("JPY", "JP¥", 0, "JP¥", "¥"));
    result.put("RUB", new CurrencyDataImpl("RUB", "руб.", 2, "руб.", "руб."));
    result.put("USD", new CurrencyDataImpl("USD", "US$", 2, "US$", "$"));
    result.put("UZS", new CurrencyDataImpl("UZS", "сўм", 0, "сўм", "soʼm"));
    return result;
  }

  @Override
  protected Map<String, String> loadCurrencyNames() {
    Map<String, String> result = super.loadCurrencyNames();
    result.put("BRL", "Бразил реали");
    result.put("CNY", "Хитой юани");
    result.put("EUR", "Евро");
    result.put("GBP", "Инглиз фунт стерлинги");
    result.put("INR", "Ҳинд рупияси");
    result.put("JPY", "Япон йенаси");
    result.put("RUB", "Рус рубли");
    result.put("USD", "АҚШ доллари");
    result.put("UZS", "Ўзбекистон сўм");
    return result;
  }
}
