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
//  number=$Revision: 5717 $
//  date=$Date: 2011-04-27 23:37:06 -0400 (Wed, 27 Apr 2011) $
//  type=KE

/**
 * Pure Java implementation of CurrencyList for locale "so_KE".
 */
public class CurrencyListImpl_so_KE extends CurrencyListImpl_so {

  @Override
  public CurrencyData getDefault() {
    return new CurrencyDataImpl("KES", "Ksh", 2, "Ksh", "Ksh");
  }

  @Override
  protected Map<String, CurrencyData> loadCurrencies() {
    Map<String, CurrencyData> result = super.loadCurrencies();
    result.put("KES", new CurrencyDataImpl("KES", "Ksh", 2, "Ksh", "Ksh"));
    return result;
  }

  @Override
  protected Map<String, String> loadCurrencyNames() {
    Map<String, String> result = super.loadCurrencyNames();
    result.put("KES", "KES");
    return result;
  }
}
