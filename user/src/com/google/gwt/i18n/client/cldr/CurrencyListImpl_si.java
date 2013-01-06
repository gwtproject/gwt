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
//  type=root

/**
 * JS implementation of CurrencyList for locale "si".
 */
public class CurrencyListImpl_si extends CurrencyListImpl {
  
  @Override
  public native CurrencyData getDefault() /*-{
    return [ "LKR", "රු.", 2, "SLRs", "Rs"];
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
      "AED": [ "AED", "DH", 2, "DH", "dh"],
      "AUD": [ "AUD", "AU$", 2, "AU$", "$"],
      "BDT": [ "BDT", "Tk", 2, "Tk", "৳"],
      "BHD": [ "BHD", "BHD", 3, "BHD", "din"],
      "CNY": [ "CNY", "CN¥", 2, "RMB¥", "¥"],
      "EUR": [ "EUR", "€", 2, "€", "€"],
      "GBP": [ "GBP", "UK£", 2, "GB£", "£"],
      "HKD": [ "HKD", "HK$", 2, "HK$", "$"],
      "INR": [ "INR", "Rs.", 2, "Rs", "₹"],
      "JOD": [ "JOD", "JOD", 3, "JOD", "din"],
      "JPY": [ "JPY", "JP¥", 0, "JP¥", "¥"],
      "LKR": [ "LKR", "රු.", 2, "SLRs", "Rs"],
      "NOK": [ "NOK", "NOkr", 2, "NOkr", "kr"],
      "NPR": [ "NPR", "NPR", 2, "NPR", "Rs"],
      "NZD": [ "NZD", "NZ$", 2, "NZ$", "$"],
      "OMR": [ "OMR", "OMR", 3, "OMR", "Rial"],
      "RUB": [ "RUB", "руб.", 2, "руб.", "руб."],
      "SAR": [ "SAR", "SR", 2, "SR", "Rial"],
      "THB": [ "THB", "฿", 2, "THB", "฿"],
      "USD": [ "USD", "US$", 2, "US$", "$"],
    };
  }-*/;
  
  private native JavaScriptObject loadCurrencyNamesOverride() /*-{
    return {
      "AED": "එක්සත් අරාබි එමිරේට්ස් ඩිරාම්",
      "AUD": "ඔස්ට්‍රේලියානු ඩොලර්",
      "BDT": "බංග්ලාදේශ් ටකා",
      "BHD": "බහරේන් ඩිනාර්",
      "CNY": "චීන යුආන්",
      "EUR": "යුරෝ",
      "GBP": "බ්‍රිතාන්‍ය ස්ටර්ලින් පවුම්",
      "HKD": "හොංකොං ඩොලර්",
      "INR": "ඉන්දියන් රුපියල්",
      "JOD": "ජෝර්දාන් ඩිනාර්",
      "JPY": "ජපන් යෙන්",
      "LKR": "ලංකා රුපියල්",
      "NOK": "නොර්වීජියන් ක්‍රෝන්",
      "NPR": "නේපාල් රුපියල්",
      "NZD": "නවසීලන්ත ඩොලර්",
      "OMR": "ඕමාන් රියාල්",
      "RUB": "රුසියන් රූබල්",
      "SAR": "සවුදි රියාල්",
      "THB": "තායි බාත්",
      "USD": "ඇමෙරිකන් ඩොලර්",
    };
  }-*/;
}
