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
package com.google.gwt.i18n.shared.cldr;

import com.google.gwt.i18n.shared.CurrencyData;
import com.google.gwt.i18n.shared.impl.CurrencyDataImpl;

import java.util.Map;;

// DO NOT EDIT - GENERATED FROM CLDR AND ICU DATA:
//  cldrVersion=21.0
//  date=$Date: 2012-02-07 13:32:35 -0500 (Tue, 07 Feb 2012) $
//  number=$Revision: 6546 $
//  type=root

/**
 *  * Pure Java implementation of CurrencyList for locale "mas".
 */
public class CurrencyListImpl_mas extends CurrencyListImpl {
  
  @Override
  public CurrencyData getDefault() {
    return new CurrencyDataImpl("KES", "Ksh", 2, "Ksh", "Ksh");
  }
  
  @Override
  protected Map<String, CurrencyData> loadCurrencies() {
    Map<String, CurrencyData> result = super.loadCurrencies();
    result.put("AED", new CurrencyDataImpl("AED", "DH", 2, "DH", "dh"));
    result.put("AOA", new CurrencyDataImpl("AOA", "AOA", 2, "AOA", "Kz"));
    result.put("AUD", new CurrencyDataImpl("AUD", "AU$", 2, "AU$", "$"));
    result.put("BHD", new CurrencyDataImpl("BHD", "BHD", 3, "BHD", "din"));
    result.put("BIF", new CurrencyDataImpl("BIF", "BIF", 0, "BIF", "FBu"));
    result.put("BWP", new CurrencyDataImpl("BWP", "BWP", 2, "BWP", "P"));
    result.put("CAD", new CurrencyDataImpl("CAD", "CA$", 2, "C$", "$"));
    result.put("CDF", new CurrencyDataImpl("CDF", "CDF", 2, "CDF", "FrCD"));
    result.put("CHF", new CurrencyDataImpl("CHF", "CHF", 2, "CHF", "CHF"));
    result.put("CNY", new CurrencyDataImpl("CNY", "CN¥", 2, "RMB¥", "¥"));
    result.put("CVE", new CurrencyDataImpl("CVE", "CVE", 2, "CVE", "CVE"));
    result.put("DJF", new CurrencyDataImpl("DJF", "Fdj", 0, "Fdj", "Fdj"));
    result.put("DZD", new CurrencyDataImpl("DZD", "DZD", 2, "DZD", "din"));
    result.put("EGP", new CurrencyDataImpl("EGP", "LE", 2, "LE", "E£"));
    result.put("ERN", new CurrencyDataImpl("ERN", "ERN", 2, "ERN", "Nfk"));
    result.put("ETB", new CurrencyDataImpl("ETB", "ETB", 2, "ETB", "Birr"));
    result.put("EUR", new CurrencyDataImpl("EUR", "€", 2, "€", "€"));
    result.put("GBP", new CurrencyDataImpl("GBP", "UK£", 2, "GB£", "£"));
    result.put("GHC", new CurrencyDataImpl("GHC", "GHC", 130, "GHC", "GHC"));
    result.put("GMD", new CurrencyDataImpl("GMD", "GMD", 2, "GMD", "GMD"));
    result.put("GNS", new CurrencyDataImpl("GNS", "GNS", 130, "GNS", "GNS"));
    result.put("INR", new CurrencyDataImpl("INR", "Rs.", 2, "Rs", "₹"));
    result.put("JPY", new CurrencyDataImpl("JPY", "JP¥", 0, "JP¥", "¥"));
    result.put("KES", new CurrencyDataImpl("KES", "Ksh", 2, "Ksh", "Ksh"));
    result.put("KMF", new CurrencyDataImpl("KMF", "KMF", 0, "KMF", "CF"));
    result.put("LRD", new CurrencyDataImpl("LRD", "LRD", 2, "LRD", "$"));
    result.put("LSL", new CurrencyDataImpl("LSL", "LSL", 2, "LSL", "LSL"));
    result.put("LYD", new CurrencyDataImpl("LYD", "LYD", 3, "LYD", "din"));
    result.put("MAD", new CurrencyDataImpl("MAD", "MAD", 2, "MAD", "MAD"));
    result.put("MGA", new CurrencyDataImpl("MGA", "MGA", 0, "MGA", "Ar"));
    result.put("MRO", new CurrencyDataImpl("MRO", "MRO", 0, "MRO", "MRO"));
    result.put("MUR", new CurrencyDataImpl("MUR", "MUR", 0, "MUR", "Rs"));
    result.put("MWK", new CurrencyDataImpl("MWK", "MWK", 2, "MWK", "MWK"));
    result.put("MZM", new CurrencyDataImpl("MZM", "MZM", 130, "MZM", "MZM"));
    result.put("NAD", new CurrencyDataImpl("NAD", "NAD", 2, "NAD", "$"));
    result.put("NGN", new CurrencyDataImpl("NGN", "NGN", 2, "NGN", "₦"));
    result.put("RWF", new CurrencyDataImpl("RWF", "RWF", 0, "RWF", "RF"));
    result.put("SAR", new CurrencyDataImpl("SAR", "SR", 2, "SR", "Rial"));
    result.put("SCR", new CurrencyDataImpl("SCR", "SCR", 2, "SCR", "SCR"));
    result.put("SDG", new CurrencyDataImpl("SDG", "SDG", 2, "SDG", "SDG"));
    result.put("SHP", new CurrencyDataImpl("SHP", "SHP", 2, "SHP", "£"));
    result.put("SLL", new CurrencyDataImpl("SLL", "SLL", 0, "SLL", "SLL"));
    result.put("SOS", new CurrencyDataImpl("SOS", "SOS", 0, "SOS", "SOS"));
    result.put("STD", new CurrencyDataImpl("STD", "STD", 0, "STD", "Db"));
    result.put("SZL", new CurrencyDataImpl("SZL", "SZL", 2, "SZL", "SZL"));
    result.put("TND", new CurrencyDataImpl("TND", "TND", 3, "TND", "din"));
    result.put("TZS", new CurrencyDataImpl("TZS", "TZS", 0, "TZS", "TSh"));
    result.put("UGX", new CurrencyDataImpl("UGX", "UGX", 0, "UGX", "UGX"));
    result.put("USD", new CurrencyDataImpl("USD", "US$", 2, "US$", "$"));
    result.put("XAF", new CurrencyDataImpl("XAF", "FCFA", 0, "FCFA", "FCFA"));
    result.put("XOF", new CurrencyDataImpl("XOF", "CFA", 0, "CFA", "CFA"));
    result.put("ZAR", new CurrencyDataImpl("ZAR", "ZAR", 2, "ZAR", "R"));
    result.put("ZMK", new CurrencyDataImpl("ZMK", "ZMK", 0, "ZMK", "ZWK"));
    result.put("ZWD", new CurrencyDataImpl("ZWD", "ZWD", 128, "ZWD", "ZWD"));
    return result;
  }
  
  @Override
  protected Map<String, String> loadCurrencyNames() {
    Map<String, String> result = super.loadCurrencyNames();
    result.put("AED", "Iropiyianí ɔ́ɔ̄ lmarabu");
    result.put("AOA", "Iropiyianí e Angola");
    result.put("AUD", "Iropiyianí e Austria");
    result.put("BHD", "Iropiyianí e Bahareini");
    result.put("BIF", "Iropiyianí e Burundi");
    result.put("BWP", "Iropiyianí e Botswana");
    result.put("CAD", "Iropiyianí e Kanada");
    result.put("CDF", "Iropiyianí e Kongo");
    result.put("CHF", "Iropiyianí e Uswisi");
    result.put("CNY", "Iropiyianí e China");
    result.put("CVE", "Iropiyianí e Kepuvede");
    result.put("DJF", "Iropiyianí e Jibuti");
    result.put("DZD", "Iropiyianí e Algeria");
    result.put("EGP", "Iropiyianí e Misri");
    result.put("ERN", "Iropiyianí e Eritrea");
    result.put("ETB", "Iropiyianí e Uhabeshi");
    result.put("EUR", "Iropiyianí e yuro");
    result.put("GBP", "Iropiyianí e Nkɨ́resa");
    result.put("GHC", "Iropiyianí e Ghana");
    result.put("GMD", "Iropiyianí e Gambia");
    result.put("GNS", "Iropiyianí e Gine");
    result.put("INR", "Iropiyianí e India");
    result.put("JPY", "Iropiyianí e Japani");
    result.put("KES", "Iropiyianí e Kenya");
    result.put("KMF", "Iropiyianí e Komoro");
    result.put("LRD", "Iropiyianí e Liberia");
    result.put("LSL", "Iropiyianí e Lesoto");
    result.put("LYD", "Iropiyianí e Libya");
    result.put("MAD", "Iropiyianí e Moroko");
    result.put("MGA", "Iropiyianí e Bukini");
    result.put("MRO", "Iropiyianí e Moritania");
    result.put("MUR", "Iropiyianí e Morisi");
    result.put("MWK", "Iropiyianí e Malawi");
    result.put("MZM", "Iropiyianí e Msumbiji");
    result.put("NAD", "Iropiyianí e Namibia");
    result.put("NGN", "Iropiyianí e Nijeria");
    result.put("RWF", "Iropiyianí e Rwanda");
    result.put("SAR", "Iropiyianí e Saudi");
    result.put("SCR", "Iropiyianí e Shelisheli");
    result.put("SDG", "Iropiyianí e Sudani");
    result.put("SHP", "Iropiyianí e Santahelena");
    result.put("SLL", "Iropiyianí e leoni");
    result.put("SOS", "Iropiyianí e Somalia");
    result.put("STD", "Iropiyianí e Saotome");
    result.put("SZL", "Iropiyianí e lilangeni");
    result.put("TND", "Iropiyianí e Tunisia");
    result.put("TZS", "Iropiyianí e Tanzania");
    result.put("UGX", "Iropiyianí e Uganda");
    result.put("USD", "Iropiyianí ɔ́ɔ̄ lamarekani");
    result.put("XAF", "Iropiyianí e CFA BEAC");
    result.put("XOF", "Iropiyianí e CFA BCEAO");
    result.put("ZAR", "Iropiyianí e Afrika Kusini");
    result.put("ZMK", "Iropiyianí e Sambia");
    result.put("ZWD", "Iropiyianí e Simbabwe");
    return result;
  }
}
