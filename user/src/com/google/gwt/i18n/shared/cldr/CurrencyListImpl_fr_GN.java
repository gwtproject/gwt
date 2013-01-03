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
//  number=$Revision: 5798 $
//  date=$Date: 2011-05-02 02:05:34 -0400 (Mon, 02 May 2011) $
//  type=root

/**
 * Pure Java implementation of CurrencyList for locale "fr_GN".
 */
public class CurrencyListImpl_fr_GN extends CurrencyListImpl_fr {

  @Override
  public CurrencyData getDefault() {
    return new CurrencyDataImpl("GNF", "FG", 0, "FG", "FG");
  }

  @Override
  protected Map<String, CurrencyData> loadCurrencies() {
    Map<String, CurrencyData> result = super.loadCurrencies();
    result.put("GNF", new CurrencyDataImpl("GNF", "FG", 0, "FG", "FG"));
    return result;
  }

  @Override
  protected Map<String, String> loadCurrencyNames() {
    Map<String, String> result = super.loadCurrencyNames();
    result.put("GNF", "franc guinéen");
    return result;
  }
}
