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
//  date=$Date: 2012-02-07 13:32:35 -0500 (Tue, 07 Feb 2012) $
//  number=$Revision: 6546 $
//  type=root

/**
 * JS implementation of CurrencyList for locale "vai".
 */
public class CurrencyListImpl_vai extends CurrencyListImpl {
  
  @Override
  public native CurrencyData getDefault() /*-{
    return [ "LRD", "$", 2, "$", "$"];
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
      "AED": [ "AED", "DH", 2, "DH", "dh"],
      "AOA": [ "AOA", "AOA", 2, "AOA", "Kz"],
      "AUD": [ "AUD", "AU$", 2, "AU$", "$"],
      "BHD": [ "BHD", "BHD", 3, "BHD", "din"],
      "BIF": [ "BIF", "BIF", 0, "BIF", "FBu"],
      "BWP": [ "BWP", "BWP", 2, "BWP", "P"],
      "CAD": [ "CAD", "CA$", 2, "C$", "$"],
      "CDF": [ "CDF", "CDF", 2, "CDF", "FrCD"],
      "CHF": [ "CHF", "CHF", 2, "CHF", "CHF"],
      "CNY": [ "CNY", "CN¥", 2, "RMB¥", "¥"],
      "CVE": [ "CVE", "CVE", 2, "CVE", "CVE"],
      "DJF": [ "DJF", "Fdj", 0, "Fdj", "Fdj"],
      "DZD": [ "DZD", "DZD", 2, "DZD", "din"],
      "EGP": [ "EGP", "LE", 2, "LE", "E£"],
      "ERN": [ "ERN", "ERN", 2, "ERN", "Nfk"],
      "ETB": [ "ETB", "ETB", 2, "ETB", "Birr"],
      "EUR": [ "EUR", "€", 2, "€", "€"],
      "GBP": [ "GBP", "UK£", 2, "GB£", "£"],
      "GHC": [ "GHC", "GHC", 130, "GHC", "GHC"],
      "GMD": [ "GMD", "GMD", 2, "GMD", "GMD"],
      "GNS": [ "GNS", "GNS", 130, "GNS", "GNS"],
      "INR": [ "INR", "Rs.", 2, "Rs", "₹"],
      "JPY": [ "JPY", "JP¥", 0, "JP¥", "¥"],
      "KES": [ "KES", "Ksh", 2, "Ksh", "Ksh"],
      "KMF": [ "KMF", "KMF", 0, "KMF", "CF"],
      "LRD": [ "LRD", "$", 2, "$", "$"],
      "LSL": [ "LSL", "LSL", 2, "LSL", "LSL"],
      "LYD": [ "LYD", "LYD", 3, "LYD", "din"],
      "MAD": [ "MAD", "MAD", 2, "MAD", "MAD"],
      "MGA": [ "MGA", "MGA", 0, "MGA", "Ar"],
      "MRO": [ "MRO", "MRO", 0, "MRO", "MRO"],
      "MUR": [ "MUR", "MUR", 0, "MUR", "Rs"],
      "MWK": [ "MWK", "MWK", 2, "MWK", "MWK"],
      "MZM": [ "MZM", "MZM", 130, "MZM", "MZM"],
      "NAD": [ "NAD", "NAD", 2, "NAD", "$"],
      "NGN": [ "NGN", "NGN", 2, "NGN", "₦"],
      "RWF": [ "RWF", "RWF", 0, "RWF", "RF"],
      "SAR": [ "SAR", "SR", 2, "SR", "Rial"],
      "SCR": [ "SCR", "SCR", 2, "SCR", "SCR"],
      "SDG": [ "SDG", "SDG", 2, "SDG", "SDG"],
      "SHP": [ "SHP", "SHP", 2, "SHP", "£"],
      "SLL": [ "SLL", "SLL", 0, "SLL", "SLL"],
      "SOS": [ "SOS", "SOS", 0, "SOS", "SOS"],
      "STD": [ "STD", "STD", 0, "STD", "Db"],
      "SZL": [ "SZL", "SZL", 2, "SZL", "SZL"],
      "TND": [ "TND", "TND", 3, "TND", "din"],
      "TZS": [ "TZS", "TZS", 0, "TZS", "TSh"],
      "UGX": [ "UGX", "UGX", 0, "UGX", "UGX"],
      "USD": [ "USD", "US$", 2, "US$", "$"],
      "ZAR": [ "ZAR", "ZAR", 2, "ZAR", "R"],
      "ZMK": [ "ZMK", "ZMK", 0, "ZMK", "ZWK"],
      "ZWD": [ "ZWD", "ZWD", 128, "ZWD", "ZWD"],
    };
  }-*/;
  
  private native JavaScriptObject loadCurrencyNamesOverride() /*-{
    return {
      "AED": "ꖳꕯꔤꗳ ꕉꕟꔬ ꗡꕆꔓꔻ ꔵꕌꕆ",
      "AOA": "ꕉꖐꕞ ꖴꕎꘋꕤ",
      "AUD": "ꖺꔻꖤꔃꔷꕩ ꕜꕞꕌ",
      "BHD": "ꕑꗸꘋ",
      "BIF": "ꖜꖩꔺ ꖢꕟꘋꕃ",
      "BWP": "ꕷꖬꕎꕯ ꖛꕞ",
      "CAD": "ꕪꕯꕜ ꕜꕞꕌ",
      "CDF": "ꖏꖐꕱ ꖢꕟꘋꕃ",
      "CHF": "ꖬꔃꕤ ꖨꕮꕊ ꖢꕟꘋꕃ",
      "CNY": "ꕦꕇꔧ ꖳꕎꘋ ꔓꕆꘋꔬ",
      "CVE": "ꗡꔻꖴꖁ ꕪꕷꗲꗡꔵꕩꖆ",
      "DJF": "ꕀꖜꔳ ꖢꕟꘋꕃ",
      "DZD": "ꕉꔷꕀꔸꕩ ꔵꕯ",
      "EGP": "ꕆꔻꕞ ꗁꖻꘋ",
      "ERN": "ꔀꔸꔳꕟ ꗁꖻꘋ",
      "ETB": "ꔤꕿꖎꔪꕩ ꔫꔤ",
      "EUR": "ꖳꖄ",
      "GBP": "ꔛꔟꔻ ꗁꖻꘋ ꔻꗳꔷꘋ",
      "GHC": "ꕭꕌꕯ ꔻꔵ",
      "GMD": "ꕭꔭꕩ ꕜꕞꔻ",
      "GNS": "ꕅꔤꕇ ꖢꕟꘋꕃ",
      "INR": "ꔤꔺꕩ ꖩꔪ",
      "JPY": "ꕧꕐꕇꔧ ꘂꘋ",
      "KES": "ꔞꕰ ꔻꔝꘋ",
      "KMF": "ꖏꖒꖄ ꖢꕟꘋꕃ",
      "LRD": "ꕞꔤꔫꕩ ꕜꕞꕌ",
      "LSL": "ꔷꖇꕿ ꖃꔳ",
      "LYD": "ꔷꔫꕩ ꔵꕯ",
      "MAD": "ꗞꕟꖏ ꔵꕌꕆ",
      "MGA": "ꕮꕞꕭꕌꔻ ꕉꔸꕩꔸ",
      "MRO": "ꗞꔸꕚꕇꕰ ꖳꕅꕩ",
      "MUR": "ꗞꔓꗔ ꖩꔪ",
      "MWK": "ꕮꕞꕌꔨ ꖴꕎꕦ",
      "MZM": "ꗞꕤꔭꕃ ꕆꔳꕪ",
      "NAD": "ꕯꕆꔫꕩ ꕜꕞꕌ",
      "NGN": "ꕯꔤꕀꔸꕩ ꕯꔤꕟ",
      "RWF": "ꕟꖙꕡ ꖢꕟꘋꕃ",
      "SAR": "ꕢꖙꔵ ꔸꕩꔷ",
      "SCR": "ꔖꗼꔷ ꖩꔪ",
      "SDG": "ꖬꗵꘋ ꗁꖻꘋ",
      "SHP": "ꔻꘋ ꗥꔷꕯ ꗁꖻꘋ",
      "SLL": "ꔷꗚꘋ",
      "SOS": "ꖇꕮꔷ ꔻꔝꘋ",
      "STD": "ꕢꕴ ꕿꔈ ꗪ ꕉ ꕗꕴ ꖁꖜꕟ",
      "SZL": "ꔷꕞꔟꕇ",
      "TND": "ꖤꕇꔻꕩ ꔵꕯ",
      "TZS": "ꕚꘋꕤꕇꕰ ꔻꔝꘋ",
      "UGX": "ꖳꕭꕡ ꔻꔝꘋ",
      "USD": "ꕶꕱ ꕜꕞ",
      "ZAR": "ꕉꔱꔸꕪ ꗛꔤ ꔒꘋꗣ ꗏ ꕟꘋꔵ",
      "ZMK": "ꕤꔭꕩ ꖴꕎꕦ",
      "ZWD": "ꔽꕓꖜꔃ ꕜꕞ",
    };
  }-*/;
}
