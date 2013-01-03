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
//  number=$Revision: 4582 $
//  date=$Date: 2010-02-05 11:51:25 -0500 (Fri, 05 Feb 2010) $
//  type=root

/**
 * Pure Java implementation of CurrencyList for locale "sr_Cyrl_BA".
 */
public class CurrencyListImpl_sr_Cyrl_BA extends CurrencyListImpl_sr_Cyrl {

  @Override
  public CurrencyData getDefault() {
    return new CurrencyDataImpl("BAM", "КМ.", 2, "КМ.", "KM");
  }

  @Override
  protected Map<String, CurrencyData> loadCurrencies() {
    Map<String, CurrencyData> result = super.loadCurrencies();
    result.put("BAM", new CurrencyDataImpl("BAM", "КМ.", 2, "КМ.", "KM"));
    return result;
  }

  @Override
  protected Map<String, String> loadCurrencyNames() {
    Map<String, String> result = super.loadCurrencyNames();
    result.put("BAM", "Конвертибилна Марка");
    return result;
  }
}
