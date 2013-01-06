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
 * JS implementation of CurrencyList for locale "tzm".
 */
public class CurrencyListImpl_tzm extends CurrencyListImpl {
  
  @Override
  public native CurrencyData getDefault() /*-{
    return [ "MAD", "MAD", 2, "MAD", "MAD"];
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
      "LRD": [ "LRD", "LRD", 2, "LRD", "$"],
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
      "SDP": [ "SDP", "SDP", 130, "SDP", "SDP"],
      "SHP": [ "SHP", "SHP", 2, "SHP", "£"],
      "SLL": [ "SLL", "SLL", 0, "SLL", "SLL"],
      "SOS": [ "SOS", "SOS", 0, "SOS", "SOS"],
      "STD": [ "STD", "STD", 0, "STD", "Db"],
      "SZL": [ "SZL", "SZL", 2, "SZL", "SZL"],
      "TND": [ "TND", "TND", 3, "TND", "din"],
      "TZS": [ "TZS", "TZS", 0, "TZS", "TSh"],
      "UGX": [ "UGX", "UGX", 0, "UGX", "UGX"],
      "USD": [ "USD", "US$", 2, "US$", "$"],
      "XAF": [ "XAF", "FCFA", 0, "FCFA", "FCFA"],
      "XOF": [ "XOF", "CFA", 0, "CFA", "CFA"],
      "ZAR": [ "ZAR", "ZAR", 2, "ZAR", "R"],
      "ZMK": [ "ZMK", "ZMK", 0, "ZMK", "ZWK"],
      "ZWD": [ "ZWD", "ZWD", 128, "ZWD", "ZWD"],
    };
  }-*/;
  
  private native JavaScriptObject loadCurrencyNamesOverride() /*-{
    return {
      "AED": "Derhem Uymarati",
      "AOA": "Kwanza Unguli",
      "AUD": "Ḍular Usṭrali",
      "BHD": "Ḍinar Ubaḥrayni",
      "BIF": "Frank Uburundi",
      "BWP": "Pula Ubutswani",
      "CAD": "Ḍular Ukanadi",
      "CDF": "Frank Ukunguli",
      "CHF": "Frank Uswisri",
      "CNY": "Ywan Renminbi Ucinwi",
      "CVE": "Iskudu Ukabuvirdyani",
      "DJF": "Frank Uğibuti",
      "DZD": "Ḍinar Udzayri",
      "EGP": "Junih Umiṣṛi",
      "ERN": "Nakfa Uyritri",
      "ETB": "Birr Uyityuppi",
      "EUR": "Uṛu",
      "GBP": "Junih Usterlini Ubriṭani",
      "GHC": "Sidi Uɣani",
      "GMD": "Dalasi Agambi",
      "GNS": "Frank Uɣini",
      "INR": "Rupi Uḥindi",
      "JPY": "Yann Ujappuni",
      "KES": "Cillin Ukini",
      "KMF": "Frank Uqumuri",
      "LRD": "Ḍular Ulibiri",
      "LSL": "Luti Ulusuṭi",
      "LYD": "Ḍinar Ulibi",
      "MAD": "Derhem Umeṛṛuki",
      "MGA": "Aryari Umalɣaci",
      "MRO": "Uqiyya Umuritani",
      "MUR": "Rupi Umurisi",
      "MWK": "Kwača Umalawi",
      "MZM": "Mitikal Umuzambiqi",
      "NAD": "Ḍular Unamibi",
      "NGN": "Nayra Unijiri",
      "RWF": "Frank Urwandi",
      "SAR": "Ryal Usaεudi",
      "SCR": "Rupi Usicili",
      "SDG": "Junih Usudani",
      "SDP": "Junih Usudani (1956–2007)",
      "SHP": "Junih Usantehilini",
      "SLL": "Lyun Usirralyuni",
      "SOS": "Cilin Uṣumali",
      "STD": "Dubra Usawṭumi",
      "SZL": "Lilanjini Uswazi",
      "TND": "Ḍinar Utunsi",
      "TZS": "Cilin Uṭanzani",
      "UGX": "Cilin Uɣandi (1966-1987)",
      "USD": "Ḍular Umirikani",
      "XAF": "Frank CFA (BEAC)",
      "XOF": "Frank CFA (BCEAO)",
      "ZAR": "Rand Ufriki Unzul",
      "ZMK": "Kwača Uzambi",
      "ZWD": "Ḍular Uzimbabwi",
    };
  }-*/;
}
