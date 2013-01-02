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
package com.google.gwt.i18n.shared.cldr;

import com.google.gwt.i18n.shared.CurrencyData;
import com.google.gwt.i18n.shared.impl.CurrencyDataImpl;

import java.util.Map;

// DO NOT EDIT - GENERATED FROM CLDR AND ICU DATA
//  cldrVersion=21.0
//  number=$Revision: 6546 $
//  type=dyo
//  date=$Date: 2012-02-07 13:32:35 -0500 (Tue, 07 Feb 2012) $

/**
 * Pure Java implementation of CurrencyList for locale "dyo".
 */
public class CurrencyListImpl_dyo extends CurrencyListImpl {

  @Override
  protected Map<String, CurrencyData> loadCurrencies() {
    Map<String, CurrencyData> result = super.loadCurrencies();
    result.put("AOA", new CurrencyDataImpl("AOA", "AOA", 2, "AOA", "Kz"));
    result.put("AUD", new CurrencyDataImpl("AUD", "AU$", 2, "AU$", "$"));
    result.put("BHD", new CurrencyDataImpl("BHD", "BHD", 3, "BHD", "din"));
    result.put("BIF", new CurrencyDataImpl("BIF", "BIF", 0, "BIF", "FBu"));
    result.put("BWP", new CurrencyDataImpl("BWP", "BWP", 2, "BWP", "P"));
    result.put("CAD", new CurrencyDataImpl("CAD", "CA$", 2, "C$", "$"));
    result.put("CDF", new CurrencyDataImpl("CDF", "CDF", 2, "CDF", "FrCD"));
    result.put("CNY", new CurrencyDataImpl("CNY", "CN¥", 2, "RMB¥", "¥"));
    result.put("CVE", new CurrencyDataImpl("CVE", "CVE", 2, "CVE", "CVE"));
    result.put("DJF", new CurrencyDataImpl("DJF", "Fdj", 0, "Fdj", "Fdj"));
    result.put("DZD", new CurrencyDataImpl("DZD", "DZD", 2, "DZD", "din"));
    result.put("EGP", new CurrencyDataImpl("EGP", "LE", 2, "LE", "E£"));
    result.put("ERN", new CurrencyDataImpl("ERN", "ERN", 2, "ERN", "Nfk"));
    result.put("ETB", new CurrencyDataImpl("ETB", "ETB", 2, "ETB", "Birr"));
    result.put("EUR", new CurrencyDataImpl("EUR", "€", 2, "€", "€"));
    result.put("GHC", new CurrencyDataImpl("GHC", "GHC", 130, "GHC", "GHC"));
    result.put("GMD", new CurrencyDataImpl("GMD", "GMD", 2, "GMD", "GMD"));
    result.put("GNS", new CurrencyDataImpl("GNS", "GNS", 130, "GNS", "GNS"));
    result.put("INR", new CurrencyDataImpl("INR", "Rs.", 2, "Rs", "₹"));
    result.put("JPY", new CurrencyDataImpl("JPY", "JP¥", 0, "JP¥", "¥"));
    result.put("KES", new CurrencyDataImpl("KES", "Ksh", 2, "Ksh", "Ksh"));
    result.put("KMF", new CurrencyDataImpl("KMF", "KMF", 0, "KMF", "CF"));
    result.put("LRD", new CurrencyDataImpl("LRD", "LRD", 2, "LRD", "$"));
    result.put("LYD", new CurrencyDataImpl("LYD", "LYD", 3, "LYD", "din"));
    result.put("MGA", new CurrencyDataImpl("MGA", "MGA", 0, "MGA", "Ar"));
    result.put("MRO", new CurrencyDataImpl("MRO", "MRO", 0, "MRO", "MRO"));
    result.put("MWK", new CurrencyDataImpl("MWK", "MWK", 2, "MWK", "MWK"));
    result.put("XAF", new CurrencyDataImpl("XAF", "FCFA", 0, "FCFA", "FCFA"));
    result.put("XOF", new CurrencyDataImpl("XOF", "CFA", 0, "CFA", "CFA"));
    return result;
  }

  @Override
  protected Map<String, String> loadCurrencyNames() {
    Map<String, String> result = super.loadCurrencyNames();
    result.put("AOA", "kwanza yati Angola");
    result.put("AUD", "dolaar yati Ostraalia");
    result.put("BHD", "dinaar yati Bahrayn");
    result.put("BIF", "fraaŋ yati Burundi");
    result.put("BWP", "pula yati Boswana");
    result.put("CAD", "dolaar yati Kanada");
    result.put("CDF", "fraaŋ yati Kongo");
    result.put("CNY", "yuan yati Siin");
    result.put("CVE", "eskuudo yati Kap Ver");
    result.put("DJF", "fraaŋ yati Jibuti");
    result.put("DZD", "dinaar yati Alseri");
    result.put("EGP", "liiverey yati Esípt");
    result.put("ERN", "nafka yati Eritree");
    result.put("ETB", "birr yati Ecoopi");
    result.put("EUR", "euro");
    result.put("GHC", "cedi yati Gaana");
    result.put("GMD", "dalasi yati Gambi");
    result.put("GNS", "sili yati Giné");
    result.put("INR", "rupii yati End");
    result.put("JPY", "yen yati Sapoŋ");
    result.put("KES", "silliŋ yati Keniya");
    result.put("KMF", "fraaŋ yati Komor");
    result.put("LRD", "dolaar yati Liberia");
    result.put("LYD", "dinaar yati Libia");
    result.put("MGA", "ariari yati Madagaskaar");
    result.put("MRO", "ugiiya yati Mooritanii");
    result.put("MWK", "kwacha yati Malawi");
    result.put("XAF", "seefa BEAC");
    result.put("XOF", "seefa yati BCEAO");
    return result;
  }
}
