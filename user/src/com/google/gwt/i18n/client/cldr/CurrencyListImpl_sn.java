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
 * JS implementation of CurrencyList for locale "sn".
 */
public class CurrencyListImpl_sn extends CurrencyListImpl {

  @Override
  public native CurrencyData getDefault() /*-{
    return [ "USD", "US$", 2, "US$", "$"];
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
      "AED": "Diramu re United Arab Emirates",
      "AOA": "Kwanza ye Angola",
      "AUD": "Dora re Australia",
      "BHD": "Dhinari re Bhahareni",
      "BIF": "Furenki re Bhurundi",
      "BWP": "Pura re Botswana",
      "CAD": "Dora re Kanada",
      "CDF": "Furenki re Kongo",
      "CHF": "Furenki re Swisi",
      "CNY": "Yuan Renminbi ye China",
      "CVE": "Dhora re Escudo",
      "DJF": "Furenki re Jibhuti",
      "DZD": "Dhinari re Aljeria",
      "EGP": "Paundi re Ijipita",
      "ERN": "Nakfa re Eritrea",
      "ETB": "Dhora re Etiopia",
      "EUR": "Yuro",
      "GBP": "Paundi ye Bhiriteni",
      "GHC": "Sedi ye Ghana",
      "GMD": "Dalasi ye Gambia",
      "GNS": "Furenki re Gine",
      "INR": "Rupe re India",
      "JPY": "Yeni ye Japani",
      "KES": "Shiringi ye Kenya",
      "KMF": "Furenki re Komoro",
      "LRD": "Dora re Liberia",
      "LSL": "Loti ye Lesoto",
      "LYD": "Dinari re Libya",
      "MAD": "Dirham ye Moroko",
      "MGA": "Furenki re Malagasi",
      "MRO": "Ugwiya ye Moritania",
      "MUR": "Rupi ye Morishasi",
      "MWK": "Kwacha ye Malawi",
      "MZM": "Metika ye Mozambiki",
      "NAD": "Dora re Namibia",
      "NGN": "Naira ye Nijeria",
      "RWF": "Furenki re Ruwanda",
      "SAR": "Riyali re Saudi",
      "SCR": "Rupi re Seyisheri",
      "SDG": "Dinari re Sudani",
      "SDP": "Paundi re Sudani",
      "SHP": "Paundi re Senti Helena",
      "SLL": "Leoni",
      "SOS": "Shiringi re Somalia",
      "STD": "Dobra re Sao Tome ne Principe",
      "SZL": "Lilangeni",
      "TND": "Dinari re Tunisia",
      "TZS": "Shiringi re Tanzania",
      "UGX": "Shiringi re Uganda",
      "USD": "Dora re Amerika",
      "XAF": "Furenki CFA BEAC",
      "XOF": "Furenki CFA BCEAO",
      "ZAR": "Randi",
      "ZMK": "Kwacha ye Zambia",
      "ZWD": "Dora re Zimbabwe",
    };
  }-*/;
}
