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
 * JS implementation of CurrencyList for locale "yo".
 */
public class CurrencyListImpl_yo extends CurrencyListImpl {
  
  @Override
  public native CurrencyData getDefault() /*-{
    return [ "NGN", "₦", 2, "₦", "₦"];
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
      "NGN": [ "NGN", "₦", 2, "₦", "₦"],
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
      "AED": "Diami ti Awon Orílẹ́ède Arabu",
      "AOA": "Wansa ti Orílẹ́ède Àngólà",
      "AUD": "Dọla ti Orílẹ́ède Ástràlìá",
      "BHD": "Dina ti Orílẹ́ède Báránì",
      "BIF": "Faransi ti Orílẹ́ède Bùùrúndì",
      "BWP": "Pula ti Orílẹ́ède Bọ̀tìsúwánà",
      "CAD": "Dọla ti Orílẹ́ède Kánádà",
      "CDF": "Faransi ti Orílẹ́ède Kóngò",
      "CHF": "Faransi ti Orílẹ́ède Siwisi",
      "CNY": "Reminibi ti Orílẹ́ède ṣáínà",
      "CVE": "Kabofediano ti Orílẹ́ède Esuodo",
      "DJF": "Faransi ti Orílẹ́ède Dibouti",
      "DZD": "Dina ti Orílẹ́ède Àlùgèríánì",
      "EGP": "pọọn ti Orílẹ́ède Egipiti",
      "ERN": "Nakifa ti Orílẹ́ède Eriteriani",
      "ETB": "Biri ti Orílẹ́ède Eutopia",
      "EUR": "Uro",
      "GBP": "Pọọn ti Orílẹ́ède Bírítísì",
      "GHC": "ṣidi ti Orílẹ́ède Gana",
      "GMD": "Dalasi ti Orílẹ́ède Gamibia",
      "GNS": "Faransi ti Orílẹ́ède Gini",
      "INR": "Rupi ti Orílẹ́ède Indina",
      "JPY": "Yeni ti Orílẹ́ède Japani",
      "KES": "ṣiili ti Orílẹ́ède Kenya",
      "KMF": "Faransi ti Orílẹ́ède ṣomoriani",
      "LRD": "Dọla ti Orílẹ́ède Liberia",
      "LSL": "Loti ti Orílẹ́ède Lesoto",
      "LYD": "Dina ti Orílẹ́ède Libiya",
      "MAD": "Dirami ti Orílẹ́ède Moroko",
      "MGA": "Faransi ti Orílẹ́ède Malagasi",
      "MRO": "Ouguiya ti Orílẹ́ède Maritania",
      "MUR": "Rupi ti Orílẹ́ède Maritiusi",
      "MWK": "Kaṣa ti Orílẹ́ède Malawi",
      "MZM": "Metika ti Orílẹ́ède Mosamibiki",
      "NAD": "Dọla ti Orílẹ́ède Namibia",
      "NGN": "Naira ti Orílẹ́ède Nàìjíríà",
      "RWF": "Faransi ti Orílẹ́ède Ruwanda",
      "SAR": "Riya ti Orílẹ́ède Saudi",
      "SCR": "Rupi ti Orílẹ́ède Sayiselesi",
      "SDG": "Dina ti Orílẹ́ède Sudani",
      "SDP": "Pọọun ti Orílẹ́ède Sudani",
      "SHP": "Pọọun ti Orílẹ́ède ̣Elena",
      "SLL": "Lioni",
      "SOS": "Sile ti Orílẹ́ède Somali",
      "STD": "Dobira ti Orílẹ́ède Sao tome Ati Pirisipe",
      "SZL": "Lilangeni",
      "TND": "Dina ti Orílẹ́ède Tunisia",
      "TZS": "Sile ti Orílẹ́ède Tansania",
      "UGX": "Siile ti Orílẹ́ède Uganda",
      "USD": "Dọla ti Orílẹ́ède Amerika",
      "XAF": "Faransi ti Orílẹ́ède BEKA",
      "XOF": "Faransi ti Orílẹ́ède BIKEAO",
      "ZAR": "Randi ti Orílẹ́ède Ariwa Afirika",
      "ZMK": "Kawaṣa ti Orílẹ́ède Saabia",
      "ZWD": "Dọla ti Orílẹ́ède Siibabuwe",
    };
  }-*/;
}
