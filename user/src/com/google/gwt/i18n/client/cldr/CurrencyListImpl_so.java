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
import com.google.gwt.i18n.shared.CurrencyData;

// DO NOT EDIT - GENERATED FROM CLDR AND ICU DATA
//  cldrVersion=21.0
//  number=$Revision: 6546 $
//  date=$Date: 2012-02-07 13:32:35 -0500 (Tue, 07 Feb 2012) $
//  type=root

/**
 * JS implementation of CurrencyList for locale "so".
 */
public class CurrencyListImpl_so extends CurrencyListImpl {

  @Override
  public native CurrencyData getDefault() /*-{
    return [ "SOS", "SOS", 0, "SOS", "SOS"];
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
      "DJF": [ "DJF", "Fdj", 0, "Fdj", "Fdj"],
      "ETB": [ "ETB", "ETB", 2, "ETB", "Birr"],
      "EUR": [ "EUR", "€", 2, "€", "€"],
      "SAR": [ "SAR", "SR", 2, "SR", "Rial"],
      "SOS": [ "SOS", "SOS", 0, "SOS", "SOS"],
      "TZS": [ "TZS", "TZS", 0, "TZS", "TSh"],
      "USD": [ "USD", "US$", 2, "US$", "$"],
      "XXX": [ "XXX", "XXX", 130, "XXX", "XXX"],
    };
  }-*/;

  private native JavaScriptObject loadCurrencyNamesOverride() /*-{
    return {
      "DJF": "Faran Jabbuuti",
      "ETB": "Birta Itoobbiya",
      "EUR": "Yuuroo",
      "SAR": "Riyaalka Sacuudiga",
      "SOS": "Shilin soomaali",
      "TZS": "Shilin Tansaani",
      "USD": "Doollar maraykan",
      "XXX": "Lacag aan la qoon ama aan saxnayn",
    };
  }-*/;
}
