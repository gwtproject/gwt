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
//  date=$Date: 2012-01-27 12:47:35 -0500 (Fri, 27 Jan 2012) $
//  number=$Revision: 6465 $
//  type=root

/**
 * JS implementation of CurrencyList for locale "zh_Hans_HK".
 */
public class CurrencyListImpl_zh_Hans_HK extends CurrencyListImpl_zh {
  
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
      "AWG": [ "AWG", "AWG", 2, "AWG", "Afl."],
      "BAM": [ "BAM", "BAM", 2, "BAM", "KM"],
      "FKP": [ "FKP", "FKP", 2, "FKP", "£"],
      "HKD": [ "HKD", "$", 2, "HK$", "$"],
      "KYD": [ "KYD", "KYD", 2, "KYD", "$"],
      "KZT": [ "KZT", "KZT", 2, "KZT", "₸"],
      "NIO": [ "NIO", "NIO", 2, "NIO", "C$"],
      "RUB": [ "RUB", "руб.", 2, "руб.", "руб."],
      "UAH": [ "UAH", "UAH", 2, "UAH", "₴"],
    };
  }-*/;
  
  private native JavaScriptObject loadCurrencyNamesOverride() /*-{
    return {
      "AWG": "阿鲁巴弗罗林",
      "BAM": "波斯尼亚-黑塞哥维那可兑换马克",
      "FKP": "福克兰群岛镑",
      "HKD": "港元",
      "KYD": "开曼群岛元",
      "KZT": "哈萨克斯坦腾格",
      "NIO": "尼加拉瓜科多巴",
      "RUB": "俄罗斯卢布",
      "UAH": "乌克兰赫夫纳",
    };
  }-*/;
}
