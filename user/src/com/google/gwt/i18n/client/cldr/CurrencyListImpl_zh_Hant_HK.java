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
//  type=root

/**
 * JS implementation of CurrencyList for locale "zh_Hant_HK".
 */
public class CurrencyListImpl_zh_Hant_HK extends CurrencyListImpl_zh_Hant {
  
  @Override
  public native CurrencyData getDefault() /*-{
    return [ "HKD", "$", 2, "HK$", "$"];
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
      "BAD": [ "BAD", "BAD", 130, "BAD", "BAD"],
      "BAM": [ "BAM", "BAM", 2, "BAM", "KM"],
      "CAD": [ "CAD", "CA$", 2, "C$", "$"],
      "HKD": [ "HKD", "$", 2, "HK$", "$"],
      "ITL": [ "ITL", "ITL", 128, "ITL", "ITL"],
      "NZD": [ "NZD", "NZ$", 2, "NZ$", "$"],
      "RSD": [ "RSD", "RSD", 0, "RSD", "din"],
      "SGD": [ "SGD", "S$", 2, "S$", "$"],
      "TWD": [ "TWD", "NT$", 2, "NT$", "NT$"],
      "XAF": [ "XAF", "FCFA", 0, "FCFA", "FCFA"],
      "XOF": [ "XOF", "CFA", 0, "CFA", "CFA"],
      "XPF": [ "XPF", "CFPF", 0, "CFPF", "FCFP"],
    };
  }-*/;
  
  private native JavaScriptObject loadCurrencyNamesOverride() /*-{
    return {
      "AUD": "澳元",
      "BAD": "波斯尼亞-黑塞哥維那第納爾",
      "BAM": "波斯尼亞-黑塞哥維那可轉換馬克",
      "CAD": "加元",
      "HKD": "港元",
      "ITL": "意大利里拉",
      "NZD": "紐西蘭元",
      "RSD": "塞爾維亞第納爾",
      "SGD": "新加坡元",
      "TWD": "新台幣",
      "XAF": "中非法郎",
      "XOF": "多哥非洲共同體法郎",
      "XPF": "太平洋法郎",
    };
  }-*/;
}
