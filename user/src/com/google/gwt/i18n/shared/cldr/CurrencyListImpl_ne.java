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
//  date=$Date: 2012-01-27 18:53:35 -0500 (Fri, 27 Jan 2012) $
//  number=$Revision: 6472 Google $
//  type=root

/**
 *  * Pure Java implementation of CurrencyList for locale "ne".
 */
public class CurrencyListImpl_ne extends CurrencyListImpl {
  
  @Override
  public CurrencyData getDefault() {
    return new CurrencyDataImpl("NPR", "नेरू", 2, "नेरू", "Rs");
  }
  
  @Override
  protected Map<String, CurrencyData> loadCurrencies() {
    Map<String, CurrencyData> result = super.loadCurrencies();
    result.put("AFA", new CurrencyDataImpl("AFA", "AFA", 130, "AFA", "AFA"));
    result.put("AFN", new CurrencyDataImpl("AFN", "AFN", 0, "AFN", "Af."));
    result.put("ALL", new CurrencyDataImpl("ALL", "ALL", 0, "ALL", "Lek"));
    result.put("AUD", new CurrencyDataImpl("AUD", "AU$", 2, "AU$", "$"));
    result.put("BRL", new CurrencyDataImpl("BRL", "R$", 2, "R$", "R$"));
    result.put("CNY", new CurrencyDataImpl("CNY", "CN¥", 2, "RMB¥", "¥"));
    result.put("EUR", new CurrencyDataImpl("EUR", "€", 2, "€", "€"));
    result.put("GBP", new CurrencyDataImpl("GBP", "UK£", 2, "GB£", "£"));
    result.put("INR", new CurrencyDataImpl("INR", "Rs.", 2, "Rs", "₹"));
    result.put("JPY", new CurrencyDataImpl("JPY", "JP¥", 0, "JP¥", "¥"));
    result.put("NOK", new CurrencyDataImpl("NOK", "NOkr", 2, "NOkr", "kr"));
    result.put("NPR", new CurrencyDataImpl("NPR", "नेरू", 2, "नेरू", "Rs"));
    result.put("PHP", new CurrencyDataImpl("PHP", "PHP", 2, "PHP", "₱"));
    result.put("RUB", new CurrencyDataImpl("RUB", "руб.", 2, "руб.", "руб."));
    result.put("USD", new CurrencyDataImpl("USD", "US$", 2, "US$", "$"));
    result.put("XXX", new CurrencyDataImpl("XXX", "XXX", 130, "XXX", "XXX"));
    return result;
  }
  
  @Override
  protected Map<String, String> loadCurrencyNames() {
    Map<String, String> result = super.loadCurrencyNames();
    result.put("AFA", "अफ्गानी(१९२७-२००२)");
    result.put("AFN", "अफ्गानी");
    result.put("ALL", "अल्बानियन लेक");
    result.put("AUD", "अष्ट्रेलियन डलर");
    result.put("BRL", "ब्राजिलियन रियल");
    result.put("CNY", "चिनिँया युआन रेनिबी");
    result.put("EUR", "युरो");
    result.put("GBP", "बेलायती पाउण्ड स्टर्लिङ");
    result.put("INR", "भारती रूपिँया");
    result.put("JPY", "जापानी येन");
    result.put("NOK", "नर्वेजियाली क्रोन");
    result.put("NPR", "NPR");
    result.put("PHP", "फिलिपिनी पेसो");
    result.put("RUB", "रूसी रूबल");
    result.put("USD", "संयुक्त राज्य डलर");
    result.put("XXX", "अपरिचित वा अवैध मुद्रा");
    return result;
  }
}
