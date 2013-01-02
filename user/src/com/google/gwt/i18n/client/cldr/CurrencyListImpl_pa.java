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
package com.google.gwt.i18n.client.cldr;

import com.google.gwt.core.client.JavaScriptObject;

// DO NOT EDIT - GENERATED FROM CLDR AND ICU DATA
//  cldrVersion=21.0
//  number=$Revision: 6546 $
//  type=root
//  date=$Date: 2012-02-07 13:32:35 -0500 (Tue, 07 Feb 2012) $

/**
 * JS implementation of CurrencyList for locale "pa".
 */
public class CurrencyListImpl_pa extends CurrencyListImpl {

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
      "AFN": [ "AFN", "AFN", 0, "AFN", "Af."],
      "EUR": [ "EUR", "€", 2, "€", "€"],
      "INR": [ "INR", "Rs.", 2, "Rs", "₹"],
      "XXX": [ "XXX", "XXX", 130, "XXX", "XXX"],
    };
  }-*/;

  private native JavaScriptObject loadCurrencyNamesOverride() /*-{
    return {
      "AFN": "ਅਫ਼ਗਾਨੀ",
      "EUR": "ਯੂਰੋ",
      "INR": "ਰੁਪਿਯ",
      "XXX": "ਅਣਜਾਣ",
    };
  }-*/;
}
