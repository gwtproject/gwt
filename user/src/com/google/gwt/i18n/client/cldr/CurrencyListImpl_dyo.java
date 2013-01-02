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
//  type=dyo
//  date=$Date: 2012-02-07 13:32:35 -0500 (Tue, 07 Feb 2012) $

/**
 * JS implementation of CurrencyList for locale "dyo".
 */
public class CurrencyListImpl_dyo extends CurrencyListImpl {

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
      "AOA": [ "AOA", "AOA", 2, "AOA", "Kz"],
      "AUD": [ "AUD", "AU$", 2, "AU$", "$"],
      "BHD": [ "BHD", "BHD", 3, "BHD", "din"],
      "BIF": [ "BIF", "BIF", 0, "BIF", "FBu"],
      "BWP": [ "BWP", "BWP", 2, "BWP", "P"],
      "CAD": [ "CAD", "CA$", 2, "C$", "$"],
      "CDF": [ "CDF", "CDF", 2, "CDF", "FrCD"],
      "CNY": [ "CNY", "CN¥", 2, "RMB¥", "¥"],
      "CVE": [ "CVE", "CVE", 2, "CVE", "CVE"],
      "DJF": [ "DJF", "Fdj", 0, "Fdj", "Fdj"],
      "DZD": [ "DZD", "DZD", 2, "DZD", "din"],
      "EGP": [ "EGP", "LE", 2, "LE", "E£"],
      "ERN": [ "ERN", "ERN", 2, "ERN", "Nfk"],
      "ETB": [ "ETB", "ETB", 2, "ETB", "Birr"],
      "EUR": [ "EUR", "€", 2, "€", "€"],
      "GHC": [ "GHC", "GHC", 130, "GHC", "GHC"],
      "GMD": [ "GMD", "GMD", 2, "GMD", "GMD"],
      "GNS": [ "GNS", "GNS", 130, "GNS", "GNS"],
      "INR": [ "INR", "Rs.", 2, "Rs", "₹"],
      "JPY": [ "JPY", "JP¥", 0, "JP¥", "¥"],
      "KES": [ "KES", "Ksh", 2, "Ksh", "Ksh"],
      "KMF": [ "KMF", "KMF", 0, "KMF", "CF"],
      "LRD": [ "LRD", "LRD", 2, "LRD", "$"],
      "LYD": [ "LYD", "LYD", 3, "LYD", "din"],
      "MGA": [ "MGA", "MGA", 0, "MGA", "Ar"],
      "MRO": [ "MRO", "MRO", 0, "MRO", "MRO"],
      "MWK": [ "MWK", "MWK", 2, "MWK", "MWK"],
      "XAF": [ "XAF", "FCFA", 0, "FCFA", "FCFA"],
      "XOF": [ "XOF", "CFA", 0, "CFA", "CFA"],
    };
  }-*/;

  private native JavaScriptObject loadCurrencyNamesOverride() /*-{
    return {
      "AOA": "kwanza yati Angola",
      "AUD": "dolaar yati Ostraalia",
      "BHD": "dinaar yati Bahrayn",
      "BIF": "fraaŋ yati Burundi",
      "BWP": "pula yati Boswana",
      "CAD": "dolaar yati Kanada",
      "CDF": "fraaŋ yati Kongo",
      "CNY": "yuan yati Siin",
      "CVE": "eskuudo yati Kap Ver",
      "DJF": "fraaŋ yati Jibuti",
      "DZD": "dinaar yati Alseri",
      "EGP": "liiverey yati Esípt",
      "ERN": "nafka yati Eritree",
      "ETB": "birr yati Ecoopi",
      "EUR": "euro",
      "GHC": "cedi yati Gaana",
      "GMD": "dalasi yati Gambi",
      "GNS": "sili yati Giné",
      "INR": "rupii yati End",
      "JPY": "yen yati Sapoŋ",
      "KES": "silliŋ yati Keniya",
      "KMF": "fraaŋ yati Komor",
      "LRD": "dolaar yati Liberia",
      "LYD": "dinaar yati Libia",
      "MGA": "ariari yati Madagaskaar",
      "MRO": "ugiiya yati Mooritanii",
      "MWK": "kwacha yati Malawi",
      "XAF": "seefa BEAC",
      "XOF": "seefa yati BCEAO",
    };
  }-*/;
}
