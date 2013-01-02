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
//  number=$Revision: 5798 $
//  date=$Date: 2011-05-02 02:05:34 -0400 (Mon, 02 May 2011) $
//  type=root

/**
 * JS implementation of CurrencyList for locale "ia".
 */
public class CurrencyListImpl_ia extends CurrencyListImpl {

  @Override
  public native CurrencyData getDefault() /*-{
    return [ "SEK", "kr", 2, "kr", "kr"];
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
      "CAD": [ "CAD", "CA$", 2, "C$", "$"],
      "CHF": [ "CHF", "CHF", 2, "CHF", "CHF"],
      "DEM": [ "DEM", "DEM", 130, "DEM", "DEM"],
      "DKK": [ "DKK", "kr", 2, "kr", "kr"],
      "EUR": [ "EUR", "€", 2, "€", "€"],
      "FRF": [ "FRF", "FRF", 130, "FRF", "FRF"],
      "GBP": [ "GBP", "UK£", 2, "GB£", "£"],
      "JPY": [ "JPY", "JP¥", 0, "JP¥", "¥"],
      "NOK": [ "NOK", "NOkr", 2, "NOkr", "kr"],
      "SEK": [ "SEK", "kr", 2, "kr", "kr"],
      "USD": [ "USD", "US$", 2, "US$", "$"],
    };
  }-*/;

  private native JavaScriptObject loadCurrencyNamesOverride() /*-{
    return {
      "AUD": "Dollares australian",
      "CAD": "Dollares canadian",
      "CHF": "Francos suisse",
      "DEM": "Marcos german",
      "DKK": "Coronas danese",
      "EUR": "Euros",
      "FRF": "francos francese",
      "GBP": "Libras sterling britannic",
      "JPY": "Yen japonese",
      "NOK": "Coronas norvegian",
      "SEK": "Coronas svedese",
      "USD": "Dollares statounitese",
    };
  }-*/;
}
