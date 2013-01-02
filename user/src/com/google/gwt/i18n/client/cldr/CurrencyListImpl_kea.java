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
 * JS implementation of CurrencyList for locale "kea".
 */
public class CurrencyListImpl_kea extends CurrencyListImpl {

  @Override
  public native CurrencyData getDefault() /*-{
    return [ "CVE", "CVE", 2, "CVE", "CVE"];
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
      "BRL": [ "BRL", "R$", 2, "R$", "R$"],
      "BWP": [ "BWP", "BWP", 2, "BWP", "P"],
      "CAD": [ "CAD", "CA$", 2, "C$", "$"],
      "CDF": [ "CDF", "CDF", 2, "CDF", "FrCD"],
      "CHF": [ "CHF", "CHF", 2, "CHF", "CHF"],
      "CNY": [ "CNY", "CN¥", 2, "RMB¥", "¥"],
      "CVE": [ "CVE", "CVE", 2, "CVE", "CVE"],
      "DJF": [ "DJF", "Fdj", 0, "Fdj", "Fdj"],
      "DKK": [ "DKK", "kr", 2, "kr", "kr"],
      "DZD": [ "DZD", "DZD", 2, "DZD", "din"],
      "EGP": [ "EGP", "LE", 2, "LE", "E£"],
      "ERN": [ "ERN", "ERN", 2, "ERN", "Nfk"],
      "ETB": [ "ETB", "ETB", 2, "ETB", "Birr"],
      "EUR": [ "EUR", "€", 2, "€", "€"],
      "GBP": [ "GBP", "UK£", 2, "GB£", "£"],
      "GHC": [ "GHC", "GHC", 130, "GHC", "GHC"],
      "GMD": [ "GMD", "GMD", 2, "GMD", "GMD"],
      "GNS": [ "GNS", "GNS", 130, "GNS", "GNS"],
      "HKD": [ "HKD", "HK$", 2, "HK$", "$"],
      "IDR": [ "IDR", "IDR", 0, "IDR", "Rp"],
      "INR": [ "INR", "Rs.", 2, "Rs", "₹"],
      "JPY": [ "JPY", "JP¥", 0, "JP¥", "¥"],
      "KES": [ "KES", "Ksh", 2, "Ksh", "Ksh"],
      "KMF": [ "KMF", "KMF", 0, "KMF", "CF"],
      "KRW": [ "KRW", "₩", 0, "KR₩", "₩"],
      "LRD": [ "LRD", "LRD", 2, "LRD", "$"],
      "LSL": [ "LSL", "LSL", 2, "LSL", "LSL"],
      "LYD": [ "LYD", "LYD", 3, "LYD", "din"],
      "MAD": [ "MAD", "MAD", 2, "MAD", "MAD"],
      "MGA": [ "MGA", "MGA", 0, "MGA", "Ar"],
      "MRO": [ "MRO", "MRO", 0, "MRO", "MRO"],
      "MUR": [ "MUR", "MUR", 0, "MUR", "Rs"],
      "MWK": [ "MWK", "MWK", 2, "MWK", "MWK"],
      "MXN": [ "MXN", "MX$", 2, "Mex$", "$"],
      "MZM": [ "MZM", "MZM", 130, "MZM", "MZM"],
      "NAD": [ "NAD", "NAD", 2, "NAD", "$"],
      "NGN": [ "NGN", "NGN", 2, "NGN", "₦"],
      "NOK": [ "NOK", "NOkr", 2, "NOkr", "kr"],
      "PLN": [ "PLN", "PLN", 2, "PLN", "zł"],
      "RUB": [ "RUB", "руб.", 2, "руб.", "руб."],
      "RWF": [ "RWF", "RWF", 0, "RWF", "RF"],
      "SAR": [ "SAR", "SR", 2, "SR", "Rial"],
      "SCR": [ "SCR", "SCR", 2, "SCR", "SCR"],
      "SDG": [ "SDG", "SDG", 2, "SDG", "SDG"],
      "SDP": [ "SDP", "SDP", 130, "SDP", "SDP"],
      "SEK": [ "SEK", "kr", 2, "kr", "kr"],
      "SHP": [ "SHP", "SHP", 2, "SHP", "£"],
      "SLL": [ "SLL", "SLL", 0, "SLL", "SLL"],
      "SOS": [ "SOS", "SOS", 0, "SOS", "SOS"],
      "STD": [ "STD", "STD", 0, "STD", "Db"],
      "SZL": [ "SZL", "SZL", 2, "SZL", "SZL"],
      "THB": [ "THB", "฿", 2, "THB", "฿"],
      "TND": [ "TND", "TND", 3, "TND", "din"],
      "TRY": [ "TRY", "YTL", 2, "YTL", "YTL"],
      "TWD": [ "TWD", "NT$", 2, "NT$", "NT$"],
      "TZS": [ "TZS", "TZS", 0, "TZS", "TSh"],
      "UGX": [ "UGX", "UGX", 0, "UGX", "UGX"],
      "USD": [ "USD", "US$", 2, "US$", "$"],
      "XAF": [ "XAF", "FCFA", 0, "FCFA", "FCFA"],
      "XOF": [ "XOF", "CFA", 0, "CFA", "CFA"],
      "XXX": [ "XXX", "XXX", 130, "XXX", "XXX"],
      "ZAR": [ "ZAR", "ZAR", 2, "ZAR", "R"],
      "ZMK": [ "ZMK", "ZMK", 0, "ZMK", "ZWK"],
      "ZWD": [ "ZWD", "ZWD", 128, "ZWD", "ZWD"],
    };
  }-*/;

  private native JavaScriptObject loadCurrencyNamesOverride() /*-{
    return {
      "AED": "Diren di Emiradus Arabi Unidu",
      "AOA": "Kuanza",
      "AUD": "Dola australianu",
      "BHD": "Dinar di Barain",
      "BIF": "Franku borundes",
      "BRL": "Rial brazileru",
      "BWP": "Pula di Botsuana",
      "CAD": "Dola kanadianu",
      "CDF": "Franku kongoles",
      "CHF": "Franku suisu",
      "CNY": "Iuan xines",
      "CVE": "Skudu Kabuverdianu",
      "DJF": "Franku di Djibuti",
      "DKK": "Kuroa dinamarkeza",
      "DZD": "Dinar arjelinu",
      "EGP": "Libra ejipsiu",
      "ERN": "Nafka di Eritreia",
      "ETB": "Bir etiopi",
      "EUR": "Euro",
      "GBP": "Libra sterlina britaniku",
      "GHC": "Sedi di Gana",
      "GMD": "Dalasi",
      "GNS": "Sili",
      "HKD": "Dola di Ong Kong",
      "IDR": "Rupia indoneziu",
      "INR": "Rupia indianu",
      "JPY": "Ieni japones",
      "KES": "Xelin kenianu",
      "KMF": "Franku di Komoris",
      "KRW": "Won sul-koreanu",
      "LRD": "Dola liberianu",
      "LSL": "Loti di Lezotu",
      "LYD": "Dinar libiu",
      "MAD": "Diren marokinu",
      "MGA": "Ariari di Madagaskar",
      "MRO": "Ougia",
      "MUR": "Rupia di Maurisias",
      "MWK": "Kuaxa di Malaui",
      "MXN": "Pezu mexikanu",
      "MZM": "Metikal",
      "NAD": "Dola namibianu",
      "NGN": "Naira",
      "NOK": "Kuroa norueges",
      "PLN": "Zloty polaku",
      "RUB": "Rublu rusu",
      "RWF": "Franku ruandes",
      "SAR": "Rial saudita",
      "SCR": "Rupia di Seixelis",
      "SDG": "Libra sudanes",
      "SDP": "Libra sudanes antigu",
      "SEK": "Kuroa sueku",
      "SHP": "Libra di Santa Ilena",
      "SLL": "Leone di Sera Leoa",
      "SOS": "Xelin somalianu",
      "STD": "Dobra di Sãu Tume i Prinsipi",
      "SZL": "Lilanjeni",
      "THB": "Baht tailandes",
      "TND": "Dinar tunizianu",
      "TRY": "Lira turku",
      "TWD": "Dola Novu di Taiwan",
      "TZS": "Xelin di Tanzania",
      "UGX": "Xelin ugandensi",
      "USD": "Dola merkanu",
      "XAF": "Franku CFA BEAC",
      "XOF": "Franku CFA BCEAO",
      "XXX": "mueda diskonxedu",
      "ZAR": "Rand sulafrikanu",
      "ZMK": "Kuaxa zambianu",
      "ZWD": "Dola di Zimbabue",
    };
  }-*/;
}
