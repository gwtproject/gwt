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
//  type=root
//  date=$Date: 2012-02-07 13:32:35 -0500 (Tue, 07 Feb 2012) $

/**
 * JS implementation of CurrencyList for locale "rn".
 */
public class CurrencyListImpl_rn extends CurrencyListImpl {

  @Override
  public native CurrencyData getDefault() /*-{
    return [ "BIF", "FBu", 0, "FBu", "FBu"];
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
      "BIF": [ "BIF", "FBu", 0, "FBu", "FBu"],
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
      "AED": "Idiramu ryo muri Leta Zunze Ubumwe z'Abarabu",
      "AOA": "Ikwanza ryo muri Angola",
      "AUD": "Idolari ryo muri Ositaraliya",
      "BHD": "Idinari ry'iribahireyini",
      "BIF": "Ifaranga ry'Uburundi",
      "BWP": "Ipula ryo muri Botswana",
      "CAD": "Idolari rya Kanada",
      "CDF": "Ifaranga rya Kongo",
      "CHF": "Ifaranga ry'Ubusuwisi",
      "CNY": "Iyuwani ryo mu Bushinwa",
      "CVE": "Irikaboveridiyano ryo muri Esikudo",
      "DJF": "Ifaranga ryo muri Jibuti",
      "DZD": "Idinari ryo muri Alijeriya",
      "EGP": "Ipawundi rya Misiri",
      "ERN": "Irinakufa ryo muri Eritereya",
      "ETB": "Ibiri ryo muri Etiyopiya",
      "EUR": "Iyero",
      "GBP": "Ipawundi ryo mu Bwongereza",
      "GHC": "Icedi ryo muri Gana",
      "GMD": "Idalasi ryo muri Gambiya",
      "GNS": "Ifaranga ryo muri Gineya",
      "INR": "Irupiya ryo mu Buhindi",
      "JPY": "Iyeni ry'Ubuyapani",
      "KES": "Ishilingi rya Kenya",
      "KMF": "Ifaranga rya Komore",
      "LRD": "Idolari rya Liberiya",
      "LSL": "Iloti ryo muro Lesoto",
      "LYD": "Idinari rya Libiya",
      "MAD": "Idiramu ryo muri Maroke",
      "MGA": "Iriyari ryo muri Madagasikari",
      "MRO": "Ugwiya ryo muri Moritaniya",
      "MUR": "Irupiya ryo mu birwa bya Morise",
      "MWK": "Ikwaca ryo muri Malawi",
      "MZM": "Irimetikali ryo muri Mozambike",
      "NAD": "Idolari rya Namibiya",
      "NGN": "Inayira ryo muri Nijeriya",
      "RWF": "Ifaranga ry'u Rwanda",
      "SAR": "Iriyari ryo muri Arabiya Sawudite",
      "SCR": "Irupiya ryo mu birwa bya Sayisheli",
      "SDG": "Ipawundi rya Sudani",
      "SHP": "Ipawundi rya Sente Helena",
      "SLL": "Ilewone",
      "SOS": "Ishilingi ryo muri Somaliya",
      "STD": "Idobura ryo muri Sawotome na Perensipe",
      "SZL": "Ililangeni",
      "TND": "Idinari ryo muri Tuniziya",
      "TZS": "Ishilingi rya Tanzaniya",
      "UGX": "Ishilingi ry'Ubugande",
      "USD": "Idolari ry'abanyamerika",
      "ZAR": "Irandi ryo muri Afurika y'Epfo",
      "ZMK": "Ikwaca ryo muri Zambiya",
      "ZWD": "Idolari ryo muri Zimbabwe",
    };
  }-*/;
}
