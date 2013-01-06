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
//  date=$Date: 2012-02-07 13:32:35 -0500 (Tue, 07 Feb 2012) $
//  number=$Revision: 6546 $
//  type=root

/**
 *  * Pure Java implementation of CurrencyList for locale "so".
 */
public class CurrencyListImpl_so extends CurrencyListImpl {
  
  @Override
  public CurrencyData getDefault() {
    return new CurrencyDataImpl("SOS", "SOS", 0, "SOS", "SOS");
  }
  
  @Override
  protected Map<String, CurrencyData> loadCurrencies() {
    Map<String, CurrencyData> result = super.loadCurrencies();
    result.put("DJF", new CurrencyDataImpl("DJF", "Fdj", 0, "Fdj", "Fdj"));
    result.put("ETB", new CurrencyDataImpl("ETB", "ETB", 2, "ETB", "Birr"));
    result.put("EUR", new CurrencyDataImpl("EUR", "€", 2, "€", "€"));
    result.put("SAR", new CurrencyDataImpl("SAR", "SR", 2, "SR", "Rial"));
    result.put("SOS", new CurrencyDataImpl("SOS", "SOS", 0, "SOS", "SOS"));
    result.put("TZS", new CurrencyDataImpl("TZS", "TZS", 0, "TZS", "TSh"));
    result.put("USD", new CurrencyDataImpl("USD", "US$", 2, "US$", "$"));
    result.put("XXX", new CurrencyDataImpl("XXX", "XXX", 130, "XXX", "XXX"));
    return result;
  }
  
  @Override
  protected Map<String, String> loadCurrencyNames() {
    Map<String, String> result = super.loadCurrencyNames();
    result.put("DJF", "Faran Jabbuuti");
    result.put("ETB", "Birta Itoobbiya");
    result.put("EUR", "Yuuroo");
    result.put("SAR", "Riyaalka Sacuudiga");
    result.put("SOS", "Shilin soomaali");
    result.put("TZS", "Shilin Tansaani");
    result.put("USD", "Doollar maraykan");
    result.put("XXX", "Lacag aan la qoon ama aan saxnayn");
    return result;
  }
}
