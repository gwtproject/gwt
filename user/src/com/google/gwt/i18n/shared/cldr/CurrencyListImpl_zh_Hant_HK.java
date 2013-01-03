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
//  type=root
//  date=$Date: 2012-02-07 13:32:35 -0500 (Tue, 07 Feb 2012) $

/**
 * Pure Java implementation of CurrencyList for locale "zh_Hant_HK".
 */
public class CurrencyListImpl_zh_Hant_HK extends CurrencyListImpl_zh_Hant {

  @Override
  public CurrencyData getDefault() {
    return new CurrencyDataImpl("HKD", "$", 2, "HK$", "$");
  }

  @Override
  protected Map<String, CurrencyData> loadCurrencies() {
    Map<String, CurrencyData> result = super.loadCurrencies();
    result.put("AUD", new CurrencyDataImpl("AUD", "AU$", 2, "AU$", "$"));
    result.put("BAD", new CurrencyDataImpl("BAD", "BAD", 130, "BAD", "BAD"));
    result.put("BAM", new CurrencyDataImpl("BAM", "BAM", 2, "BAM", "KM"));
    result.put("CAD", new CurrencyDataImpl("CAD", "CA$", 2, "C$", "$"));
    result.put("HKD", new CurrencyDataImpl("HKD", "$", 2, "HK$", "$"));
    result.put("ITL", new CurrencyDataImpl("ITL", "ITL", 128, "ITL", "ITL"));
    result.put("NZD", new CurrencyDataImpl("NZD", "NZ$", 2, "NZ$", "$"));
    result.put("RSD", new CurrencyDataImpl("RSD", "RSD", 0, "RSD", "din"));
    result.put("SGD", new CurrencyDataImpl("SGD", "S$", 2, "S$", "$"));
    result.put("TWD", new CurrencyDataImpl("TWD", "NT$", 2, "NT$", "NT$"));
    result.put("XAF", new CurrencyDataImpl("XAF", "FCFA", 0, "FCFA", "FCFA"));
    result.put("XOF", new CurrencyDataImpl("XOF", "CFA", 0, "CFA", "CFA"));
    result.put("XPF", new CurrencyDataImpl("XPF", "CFPF", 0, "CFPF", "FCFP"));
    return result;
  }

  @Override
  protected Map<String, String> loadCurrencyNames() {
    Map<String, String> result = super.loadCurrencyNames();
    result.put("AUD", "澳元");
    result.put("BAD", "波斯尼亞-黑塞哥維那第納爾");
    result.put("BAM", "波斯尼亞-黑塞哥維那可轉換馬克");
    result.put("CAD", "加元");
    result.put("HKD", "港元");
    result.put("ITL", "意大利里拉");
    result.put("NZD", "紐西蘭元");
    result.put("RSD", "塞爾維亞第納爾");
    result.put("SGD", "新加坡元");
    result.put("TWD", "新台幣");
    result.put("XAF", "中非法郎");
    result.put("XOF", "多哥非洲共同體法郎");
    result.put("XPF", "太平洋法郎");
    return result;
  }
}
