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
//  date=$Date: 2012-01-27 12:47:35 -0500 (Fri, 27 Jan 2012) $
//  number=$Revision: 6465 $
//  type=root

/**
 *  * Pure Java implementation of CurrencyList for locale "zh_Hans_HK".
 */
public class CurrencyListImpl_zh_Hans_HK extends CurrencyListImpl_zh {
  
  @Override
  public CurrencyData getDefault() {
    return new CurrencyDataImpl("HKD", "$", 2, "HK$", "$");
  }
  
  @Override
  protected Map<String, CurrencyData> loadCurrencies() {
    Map<String, CurrencyData> result = super.loadCurrencies();
    result.put("AWG", new CurrencyDataImpl("AWG", "AWG", 2, "AWG", "Afl."));
    result.put("BAM", new CurrencyDataImpl("BAM", "BAM", 2, "BAM", "KM"));
    result.put("FKP", new CurrencyDataImpl("FKP", "FKP", 2, "FKP", "£"));
    result.put("HKD", new CurrencyDataImpl("HKD", "$", 2, "HK$", "$"));
    result.put("KYD", new CurrencyDataImpl("KYD", "KYD", 2, "KYD", "$"));
    result.put("KZT", new CurrencyDataImpl("KZT", "KZT", 2, "KZT", "₸"));
    result.put("NIO", new CurrencyDataImpl("NIO", "NIO", 2, "NIO", "C$"));
    result.put("RUB", new CurrencyDataImpl("RUB", "руб.", 2, "руб.", "руб."));
    result.put("UAH", new CurrencyDataImpl("UAH", "UAH", 2, "UAH", "₴"));
    return result;
  }
  
  @Override
  protected Map<String, String> loadCurrencyNames() {
    Map<String, String> result = super.loadCurrencyNames();
    result.put("AWG", "阿鲁巴弗罗林");
    result.put("BAM", "波斯尼亚-黑塞哥维那可兑换马克");
    result.put("FKP", "福克兰群岛镑");
    result.put("HKD", "港元");
    result.put("KYD", "开曼群岛元");
    result.put("KZT", "哈萨克斯坦腾格");
    result.put("NIO", "尼加拉瓜科多巴");
    result.put("RUB", "俄罗斯卢布");
    result.put("UAH", "乌克兰赫夫纳");
    return result;
  }
}
