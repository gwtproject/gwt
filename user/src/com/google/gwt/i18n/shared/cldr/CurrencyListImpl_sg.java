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
 *  * Pure Java implementation of CurrencyList for locale "sg".
 */
public class CurrencyListImpl_sg extends CurrencyListImpl {
  
  @Override
  public CurrencyData getDefault() {
    return new CurrencyDataImpl("XAF", "FCFA", 0, "FCFA", "FCFA");
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
    result.put("AED", "dirâm tî âEmirâti tî Arâbo Ôko");
    result.put("AOA", "kwânza tî Angoläa");
    result.put("AUD", "dolära tî Ostralïi");
    result.put("BHD", "dolùara tî Bahrâina");
    result.put("BIF", "farânga tî Burundïi");
    result.put("BWP", "pûla tî Botswana");
    result.put("CAD", "dolära tî kanadäa");
    result.put("CDF", "farânga tî Kongöo");
    result.put("CHF", "farânga tî Sûîsi");
    result.put("CNY", "yuan renminbi tî Shîni");
    result.put("CVE", "eskûêdo tî Kâpo-Vêre");
    result.put("DJF", "farânga tî Dibutïi");
    result.put("DZD", "dinäri tî Alzerïi");
    result.put("EGP", "pôndo tî Kâmitâ");
    result.put("ERN", "nakafa tî Eritrëe");
    result.put("ETB", "bir tî Etiopïi");
    result.put("EUR", "zoröo");
    result.put("GBP", "pôndo tî Anglëe");
    result.put("GHC", "sêdi tî Ganäa");
    result.put("GMD", "dalasi tî gambïi");
    result.put("GNS", "sili tî Ginëe");
    result.put("INR", "rupïi tî Ênnde");
    result.put("JPY", "yêni tî Zapön");
    result.put("KES", "shilîngi tî Kenyäa");
    result.put("KMF", "farânga tî Kömôro");
    result.put("LRD", "dolära tî Liberïa");
    result.put("LSL", "loti tî Lesôtho");
    result.put("LYD", "dinäar tî Libïi");
    result.put("MAD", "dirâm tî Marôko");
    result.put("MGA", "ariâri tî Madagasikära");
    result.put("MRO", "ugîya tî Moritanïi");
    result.put("MUR", "rupïi tî Mörîsi");
    result.put("MWK", "kwâtia tî Malawïi");
    result.put("MZM", "metikala tî Mozambîka");
    result.put("NAD", "dolära tî Namibïi");
    result.put("NGN", "nâîra tî Nizerïa");
    result.put("RWF", "farânga tî Ruandäa");
    result.put("SAR", "riâli tî Saûdi Arabïi");
    result.put("SCR", "rupïi tî Sëyshêle");
    result.put("SDG", "pôndo tî Sudäan");
    result.put("SHP", "pôndo tî Zûâ Sênt-Helêna");
    result.put("SLL", "leône tî Sierâ-Leône");
    result.put("SOS", "shilîngi tî Somalïi");
    result.put("STD", "dôbra tî Sâô Tomë na Prinsîpe");
    result.put("SZL", "lilangùeni tî Swazïlânde");
    result.put("TND", "dinära tî Tunizïi");
    result.put("TZS", "shilîngi tî Tanzanïi");
    result.put("UGX", "shilîngi tî Ugandäa");
    result.put("USD", "dol$ara ttî äLetäa-Ôko tî Amerîka");
    result.put("XAF", "farânga CFA (BEAC)");
    result.put("XOF", "farânga CFA (BCEAO)");
    result.put("ZAR", "rânde tî Mbongo-Afrîka");
    result.put("ZMK", "kwâtia tî Zambïi");
    result.put("ZWD", "dolära tî Zimbäbwe");
    return result;
  }
}
