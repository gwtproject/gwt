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

// DO NOT EDIT - GENERATED FROM CLDR AND ICU DATA:
//  cldrVersion=21.0
//  date=$Date: 2012-01-25 19:20:54 -0500 (Wed, 25 Jan 2012) $
//  number=$Revision: 6450 $
//  type=root

/**
 * JS implementation of CurrencyList for locale "mgh".
 */
public class CurrencyListImpl_mgh extends CurrencyListImpl {
  
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
      "MZN": [ "MZN", "MTn", 2, "MTn", "MTn"],
    };
  }-*/;
  
  private native JavaScriptObject loadCurrencyNamesOverride() /*-{
    return {
      "MZN": "MZN",
    };
  }-*/;
}
