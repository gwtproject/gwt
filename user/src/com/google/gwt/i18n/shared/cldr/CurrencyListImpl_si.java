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
//  number=$Revision: 6546 $
//  date=$Date: 2012-02-07 13:32:35 -0500 (Tue, 07 Feb 2012) $
//  type=root

/**
 * Pure Java implementation of CurrencyList for locale "si".
 */
public class CurrencyListImpl_si extends CurrencyListImpl {

  @Override
  public CurrencyData getDefault() {
    return new CurrencyDataImpl("LKR", "රු.", 2, "SLRs", "Rs");
  }

  @Override
  protected Map<String, CurrencyData> loadCurrencies() {
    Map<String, CurrencyData> result = super.loadCurrencies();
    result.put("AED", new CurrencyDataImpl("AED", "DH", 2, "DH", "dh"));
    result.put("AUD", new CurrencyDataImpl("AUD", "AU$", 2, "AU$", "$"));
    result.put("BDT", new CurrencyDataImpl("BDT", "Tk", 2, "Tk", "৳"));
    result.put("BHD", new CurrencyDataImpl("BHD", "BHD", 3, "BHD", "din"));
    result.put("CNY", new CurrencyDataImpl("CNY", "CN¥", 2, "RMB¥", "¥"));
    result.put("EUR", new CurrencyDataImpl("EUR", "€", 2, "€", "€"));
    result.put("GBP", new CurrencyDataImpl("GBP", "UK£", 2, "GB£", "£"));
    result.put("HKD", new CurrencyDataImpl("HKD", "HK$", 2, "HK$", "$"));
    result.put("INR", new CurrencyDataImpl("INR", "Rs.", 2, "Rs", "₹"));
    result.put("JOD", new CurrencyDataImpl("JOD", "JOD", 3, "JOD", "din"));
    result.put("JPY", new CurrencyDataImpl("JPY", "JP¥", 0, "JP¥", "¥"));
    result.put("LKR", new CurrencyDataImpl("LKR", "රු.", 2, "SLRs", "Rs"));
    result.put("NOK", new CurrencyDataImpl("NOK", "NOkr", 2, "NOkr", "kr"));
    result.put("NPR", new CurrencyDataImpl("NPR", "NPR", 2, "NPR", "Rs"));
    result.put("NZD", new CurrencyDataImpl("NZD", "NZ$", 2, "NZ$", "$"));
    result.put("OMR", new CurrencyDataImpl("OMR", "OMR", 3, "OMR", "Rial"));
    result.put("RUB", new CurrencyDataImpl("RUB", "руб.", 2, "руб.", "руб."));
    result.put("SAR", new CurrencyDataImpl("SAR", "SR", 2, "SR", "Rial"));
    result.put("THB", new CurrencyDataImpl("THB", "฿", 2, "THB", "฿"));
    result.put("USD", new CurrencyDataImpl("USD", "US$", 2, "US$", "$"));
    return result;
  }

  @Override
  protected Map<String, String> loadCurrencyNames() {
    Map<String, String> result = super.loadCurrencyNames();
    result.put("AED", "එක්සත් අරාබි එමිරේට්ස් ඩිරාම්");
    result.put("AUD", "ඔස්ට්‍රේලියානු ඩොලර්");
    result.put("BDT", "බංග්ලාදේශ් ටකා");
    result.put("BHD", "බහරේන් ඩිනාර්");
    result.put("CNY", "චීන යුආන්");
    result.put("EUR", "යුරෝ");
    result.put("GBP", "බ්‍රිතාන්‍ය ස්ටර්ලින් පවුම්");
    result.put("HKD", "හොංකොං ඩොලර්");
    result.put("INR", "ඉන්දියන් රුපියල්");
    result.put("JOD", "ජෝර්දාන් ඩිනාර්");
    result.put("JPY", "ජපන් යෙන්");
    result.put("LKR", "ලංකා රුපියල්");
    result.put("NOK", "නොර්වීජියන් ක්‍රෝන්");
    result.put("NPR", "නේපාල් රුපියල්");
    result.put("NZD", "නවසීලන්ත ඩොලර්");
    result.put("OMR", "ඕමාන් රියාල්");
    result.put("RUB", "රුසියන් රූබල්");
    result.put("SAR", "සවුදි රියාල්");
    result.put("THB", "තායි බාත්");
    result.put("USD", "ඇමෙරිකන් ඩොලර්");
    return result;
  }
}
