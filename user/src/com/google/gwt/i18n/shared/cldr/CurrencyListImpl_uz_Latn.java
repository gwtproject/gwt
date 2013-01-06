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
package com.google.gwt.i18n.shared.cldr;

import com.google.gwt.i18n.shared.CurrencyData;
import com.google.gwt.i18n.shared.impl.CurrencyDataImpl;

import java.util.Map;;

// DO NOT EDIT - GENERATED FROM CLDR AND ICU DATA:
//  cldrVersion=21.0
//  date=$Date: 2011-05-10 16:26:37 -0400 (Tue, 10 May 2011) $
//  number=$Revision: 5882 $
//  type=root

/**
 *  * Pure Java implementation of CurrencyList for locale "uz_Latn".
 */
public class CurrencyListImpl_uz_Latn extends CurrencyListImpl_uz {
  
  @Override
  public CurrencyData getDefault() {
    return new CurrencyDataImpl("UZS", "soʼm", 0, "soʼm", "soʼm");
  }
  
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
    result.put("UZS", new CurrencyDataImpl("UZS", "soʼm", 0, "soʼm", "soʼm"));
    return result;
  }
  
  @Override
  protected Map<String, String> loadCurrencyNames() {
    Map<String, String> result = super.loadCurrencyNames();
    result.put("BRL", "Brazil reali");
    result.put("CNY", "Xitoy yuani");
    result.put("EUR", "Evro");
    result.put("GBP", "Ingliz funt sterlingi");
    result.put("INR", "Hind rupiyasi");
    result.put("JPY", "Yapon yenasi");
    result.put("RUB", "Rus rubli");
    result.put("USD", "AQSH dollari");
    result.put("UZS", "Oʼzbekiston soʼm");
    return result;
  }
}
