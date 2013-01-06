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
//  number=$Revision: 6546 Google $
//  type=root

/**
 *  * Pure Java implementation of CurrencyList for locale "ms".
 */
public class CurrencyListImpl_ms extends CurrencyListImpl {
  
  @Override
  public CurrencyData getDefault() {
    return new CurrencyDataImpl("MYR", "RM", 2, "RM", "RM");
  }
  
  @Override
  protected Map<String, CurrencyData> loadCurrencies() {
    Map<String, CurrencyData> result = super.loadCurrencies();
    result.put("AED", new CurrencyDataImpl("AED", "DH", 2, "DH", "dh"));
    result.put("AFN", new CurrencyDataImpl("AFN", "AFN", 0, "AFN", "Af."));
    result.put("ALL", new CurrencyDataImpl("ALL", "ALL", 0, "ALL", "Lek"));
    result.put("AMD", new CurrencyDataImpl("AMD", "AMD", 0, "AMD", "Dram"));
    result.put("ANG", new CurrencyDataImpl("ANG", "ANG", 2, "ANG", "ANG"));
    result.put("AOA", new CurrencyDataImpl("AOA", "AOA", 2, "AOA", "Kz"));
    result.put("ARS", new CurrencyDataImpl("ARS", "AR$", 2, "AR$", "$"));
    result.put("AUD", new CurrencyDataImpl("AUD", "AU$", 2, "AU$", "$"));
    result.put("AWG", new CurrencyDataImpl("AWG", "AWG", 2, "AWG", "Afl."));
    result.put("AZN", new CurrencyDataImpl("AZN", "AZN", 2, "AZN", "man."));
    result.put("BAM", new CurrencyDataImpl("BAM", "BAM", 2, "BAM", "KM"));
    result.put("BBD", new CurrencyDataImpl("BBD", "BBD", 2, "BBD", "$"));
    result.put("BDT", new CurrencyDataImpl("BDT", "Tk", 2, "Tk", "৳"));
    result.put("BGN", new CurrencyDataImpl("BGN", "BGN", 2, "BGN", "lev"));
    result.put("BHD", new CurrencyDataImpl("BHD", "BHD", 3, "BHD", "din"));
    result.put("BIF", new CurrencyDataImpl("BIF", "BIF", 0, "BIF", "FBu"));
    result.put("BMD", new CurrencyDataImpl("BMD", "BMD", 2, "BMD", "$"));
    result.put("BND", new CurrencyDataImpl("BND", "BND", 2, "BND", "$"));
    result.put("BOB", new CurrencyDataImpl("BOB", "BOB", 2, "BOB", "Bs"));
    result.put("BRL", new CurrencyDataImpl("BRL", "R$", 2, "R$", "R$"));
    result.put("BSD", new CurrencyDataImpl("BSD", "BSD", 2, "BSD", "$"));
    result.put("BTN", new CurrencyDataImpl("BTN", "BTN", 2, "BTN", "Nu."));
    result.put("BWP", new CurrencyDataImpl("BWP", "BWP", 2, "BWP", "P"));
    result.put("BYR", new CurrencyDataImpl("BYR", "BYR", 0, "BYR", "BYR"));
    result.put("BZD", new CurrencyDataImpl("BZD", "BZD", 2, "BZD", "$"));
    result.put("CAD", new CurrencyDataImpl("CAD", "CA$", 2, "C$", "$"));
    result.put("CDF", new CurrencyDataImpl("CDF", "CDF", 2, "CDF", "FrCD"));
    result.put("CHF", new CurrencyDataImpl("CHF", "CHF", 2, "CHF", "CHF"));
    result.put("CLP", new CurrencyDataImpl("CLP", "CL$", 0, "CL$", "$"));
    result.put("CNY", new CurrencyDataImpl("CNY", "CN¥", 2, "RMB¥", "¥"));
    result.put("COP", new CurrencyDataImpl("COP", "COL$", 0, "COL$", "$"));
    result.put("CRC", new CurrencyDataImpl("CRC", "CR₡", 0, "CR₡", "₡"));
    result.put("CUC", new CurrencyDataImpl("CUC", "CUC", 2, "CUC", "$"));
    result.put("CUP", new CurrencyDataImpl("CUP", "$MN", 2, "$MN", "$"));
    result.put("CVE", new CurrencyDataImpl("CVE", "CVE", 2, "CVE", "CVE"));
    result.put("CZK", new CurrencyDataImpl("CZK", "Kč", 2, "Kč", "Kč"));
    result.put("DJF", new CurrencyDataImpl("DJF", "Fdj", 0, "Fdj", "Fdj"));
    result.put("DKK", new CurrencyDataImpl("DKK", "kr", 2, "kr", "kr"));
    result.put("DOP", new CurrencyDataImpl("DOP", "RD$", 2, "RD$", "$"));
    result.put("DZD", new CurrencyDataImpl("DZD", "DZD", 2, "DZD", "din"));
    result.put("EGP", new CurrencyDataImpl("EGP", "LE", 2, "LE", "E£"));
    result.put("ERN", new CurrencyDataImpl("ERN", "ERN", 2, "ERN", "Nfk"));
    result.put("ETB", new CurrencyDataImpl("ETB", "ETB", 2, "ETB", "Birr"));
    result.put("EUR", new CurrencyDataImpl("EUR", "€", 2, "€", "€"));
    result.put("FJD", new CurrencyDataImpl("FJD", "FJD", 2, "FJD", "$"));
    result.put("FKP", new CurrencyDataImpl("FKP", "FKP", 2, "FKP", "£"));
    result.put("GBP", new CurrencyDataImpl("GBP", "UK£", 2, "GB£", "£"));
    result.put("GEL", new CurrencyDataImpl("GEL", "GEL", 2, "GEL", "GEL"));
    result.put("GHS", new CurrencyDataImpl("GHS", "GHS", 2, "GHS", "GHS"));
    result.put("GIP", new CurrencyDataImpl("GIP", "GIP", 2, "GIP", "£"));
    result.put("GMD", new CurrencyDataImpl("GMD", "GMD", 2, "GMD", "GMD"));
    result.put("GNF", new CurrencyDataImpl("GNF", "GNF", 0, "GNF", "FG"));
    result.put("GTQ", new CurrencyDataImpl("GTQ", "GTQ", 2, "GTQ", "Q"));
    result.put("GYD", new CurrencyDataImpl("GYD", "GYD", 0, "GYD", "$"));
    result.put("HKD", new CurrencyDataImpl("HKD", "HK$", 2, "HK$", "$"));
    result.put("HNL", new CurrencyDataImpl("HNL", "L", 2, "L", "L"));
    result.put("HRK", new CurrencyDataImpl("HRK", "HRK", 2, "HRK", "kn"));
    result.put("HTG", new CurrencyDataImpl("HTG", "HTG", 2, "HTG", "HTG"));
    result.put("HUF", new CurrencyDataImpl("HUF", "HUF", 0, "HUF", "Ft"));
    result.put("IDR", new CurrencyDataImpl("IDR", "IDR", 0, "IDR", "Rp"));
    result.put("ILS", new CurrencyDataImpl("ILS", "₪", 2, "IL₪", "₪"));
    result.put("INR", new CurrencyDataImpl("INR", "Rs.", 2, "Rs", "₹"));
    result.put("IQD", new CurrencyDataImpl("IQD", "IQD", 0, "IQD", "din"));
    result.put("IRR", new CurrencyDataImpl("IRR", "IRR", 0, "IRR", "Rial"));
    result.put("ISK", new CurrencyDataImpl("ISK", "kr", 0, "kr", "kr"));
    result.put("JMD", new CurrencyDataImpl("JMD", "JA$", 2, "JA$", "$"));
    result.put("JOD", new CurrencyDataImpl("JOD", "JOD", 3, "JOD", "din"));
    result.put("JPY", new CurrencyDataImpl("JPY", "JP¥", 0, "JP¥", "¥"));
    result.put("KES", new CurrencyDataImpl("KES", "Ksh", 2, "Ksh", "Ksh"));
    result.put("KGS", new CurrencyDataImpl("KGS", "KGS", 2, "KGS", "KGS"));
    result.put("KHR", new CurrencyDataImpl("KHR", "KHR", 2, "KHR", "Riel"));
    result.put("KMF", new CurrencyDataImpl("KMF", "KMF", 0, "KMF", "CF"));
    result.put("KPW", new CurrencyDataImpl("KPW", "KPW", 0, "KPW", "₩"));
    result.put("KRW", new CurrencyDataImpl("KRW", "₩", 0, "KR₩", "₩"));
    result.put("KWD", new CurrencyDataImpl("KWD", "KWD", 3, "KWD", "din"));
    result.put("KYD", new CurrencyDataImpl("KYD", "KYD", 2, "KYD", "$"));
    result.put("KZT", new CurrencyDataImpl("KZT", "KZT", 2, "KZT", "₸"));
    result.put("LAK", new CurrencyDataImpl("LAK", "LAK", 0, "LAK", "₭"));
    result.put("LBP", new CurrencyDataImpl("LBP", "LBP", 0, "LBP", "L£"));
    result.put("LKR", new CurrencyDataImpl("LKR", "SLRs", 2, "SLRs", "Rs"));
    result.put("LRD", new CurrencyDataImpl("LRD", "LRD", 2, "LRD", "$"));
    result.put("LSL", new CurrencyDataImpl("LSL", "LSL", 2, "LSL", "LSL"));
    result.put("LTL", new CurrencyDataImpl("LTL", "LTL", 2, "LTL", "Lt"));
    result.put("LVL", new CurrencyDataImpl("LVL", "LVL", 2, "LVL", "Ls"));
    result.put("LYD", new CurrencyDataImpl("LYD", "LYD", 3, "LYD", "din"));
    result.put("MAD", new CurrencyDataImpl("MAD", "MAD", 2, "MAD", "MAD"));
    result.put("MDL", new CurrencyDataImpl("MDL", "MDL", 2, "MDL", "MDL"));
    result.put("MGA", new CurrencyDataImpl("MGA", "MGA", 0, "MGA", "Ar"));
    result.put("MKD", new CurrencyDataImpl("MKD", "MKD", 2, "MKD", "din"));
    result.put("MMK", new CurrencyDataImpl("MMK", "MMK", 0, "MMK", "K"));
    result.put("MNT", new CurrencyDataImpl("MNT", "MN₮", 0, "MN₮", "₮"));
    result.put("MOP", new CurrencyDataImpl("MOP", "MOP", 2, "MOP", "MOP"));
    result.put("MRO", new CurrencyDataImpl("MRO", "MRO", 0, "MRO", "MRO"));
    result.put("MUR", new CurrencyDataImpl("MUR", "MUR", 0, "MUR", "Rs"));
    result.put("MVR", new CurrencyDataImpl("MVR", "MVR", 2, "MVR", "MVR"));
    result.put("MWK", new CurrencyDataImpl("MWK", "MWK", 2, "MWK", "MWK"));
    result.put("MXN", new CurrencyDataImpl("MXN", "MX$", 2, "Mex$", "$"));
    result.put("MYR", new CurrencyDataImpl("MYR", "RM", 2, "RM", "RM"));
    result.put("NAD", new CurrencyDataImpl("NAD", "NAD", 2, "NAD", "$"));
    result.put("NGN", new CurrencyDataImpl("NGN", "NGN", 2, "NGN", "₦"));
    result.put("NIO", new CurrencyDataImpl("NIO", "NIO", 2, "NIO", "C$"));
    result.put("NOK", new CurrencyDataImpl("NOK", "NOkr", 2, "NOkr", "kr"));
    result.put("NPR", new CurrencyDataImpl("NPR", "NPR", 2, "NPR", "Rs"));
    result.put("NZD", new CurrencyDataImpl("NZD", "NZ$", 2, "NZ$", "$"));
    result.put("OMR", new CurrencyDataImpl("OMR", "OMR", 3, "OMR", "Rial"));
    result.put("PAB", new CurrencyDataImpl("PAB", "B/.", 2, "B/.", "B/."));
    result.put("PEN", new CurrencyDataImpl("PEN", "S/.", 2, "S/.", "S/."));
    result.put("PGK", new CurrencyDataImpl("PGK", "PGK", 2, "PGK", "PGK"));
    result.put("PHP", new CurrencyDataImpl("PHP", "PHP", 2, "PHP", "₱"));
    result.put("PKR", new CurrencyDataImpl("PKR", "PKRs.", 0, "PKRs.", "Rs"));
    result.put("PLN", new CurrencyDataImpl("PLN", "PLN", 2, "PLN", "zł"));
    result.put("PYG", new CurrencyDataImpl("PYG", "PYG", 0, "PYG", "Gs"));
    result.put("QAR", new CurrencyDataImpl("QAR", "QAR", 2, "QAR", "Rial"));
    result.put("RSD", new CurrencyDataImpl("RSD", "RSD", 0, "RSD", "din"));
    result.put("RUB", new CurrencyDataImpl("RUB", "руб.", 2, "руб.", "руб."));
    result.put("RWF", new CurrencyDataImpl("RWF", "RWF", 0, "RWF", "RF"));
    result.put("SAR", new CurrencyDataImpl("SAR", "SR", 2, "SR", "Rial"));
    result.put("SBD", new CurrencyDataImpl("SBD", "SBD", 2, "SBD", "$"));
    result.put("SCR", new CurrencyDataImpl("SCR", "SCR", 2, "SCR", "SCR"));
    result.put("SDG", new CurrencyDataImpl("SDG", "SDG", 2, "SDG", "SDG"));
    result.put("SEK", new CurrencyDataImpl("SEK", "kr", 2, "kr", "kr"));
    result.put("SGD", new CurrencyDataImpl("SGD", "S$", 2, "S$", "$"));
    result.put("SHP", new CurrencyDataImpl("SHP", "SHP", 2, "SHP", "£"));
    result.put("SLL", new CurrencyDataImpl("SLL", "SLL", 0, "SLL", "SLL"));
    result.put("SOS", new CurrencyDataImpl("SOS", "SOS", 0, "SOS", "SOS"));
    result.put("SRD", new CurrencyDataImpl("SRD", "SRD", 2, "SRD", "$"));
    result.put("STD", new CurrencyDataImpl("STD", "STD", 0, "STD", "Db"));
    result.put("SYP", new CurrencyDataImpl("SYP", "SYP", 0, "SYP", "£"));
    result.put("SZL", new CurrencyDataImpl("SZL", "SZL", 2, "SZL", "SZL"));
    result.put("THB", new CurrencyDataImpl("THB", "฿", 2, "THB", "฿"));
    result.put("TJS", new CurrencyDataImpl("TJS", "TJS", 2, "TJS", "Som"));
    result.put("TMT", new CurrencyDataImpl("TMT", "TMT", 2, "TMT", "TMT"));
    result.put("TND", new CurrencyDataImpl("TND", "TND", 3, "TND", "din"));
    result.put("TOP", new CurrencyDataImpl("TOP", "TOP", 2, "TOP", "T$"));
    result.put("TRY", new CurrencyDataImpl("TRY", "YTL", 2, "YTL", "YTL"));
    result.put("TTD", new CurrencyDataImpl("TTD", "TTD", 2, "TTD", "$"));
    result.put("TWD", new CurrencyDataImpl("TWD", "NT$", 2, "NT$", "NT$"));
    result.put("TZS", new CurrencyDataImpl("TZS", "TZS", 0, "TZS", "TSh"));
    result.put("UAH", new CurrencyDataImpl("UAH", "UAH", 2, "UAH", "₴"));
    result.put("UGX", new CurrencyDataImpl("UGX", "UGX", 0, "UGX", "UGX"));
    result.put("USD", new CurrencyDataImpl("USD", "US$", 2, "US$", "$"));
    result.put("UYU", new CurrencyDataImpl("UYU", "UY$", 2, "UY$", "$"));
    result.put("UZS", new CurrencyDataImpl("UZS", "UZS", 0, "UZS", "soʼm"));
    result.put("VEF", new CurrencyDataImpl("VEF", "VEF", 2, "VEF", "Bs"));
    result.put("VND", new CurrencyDataImpl("VND", "₫", 24, "₫", "₫"));
    result.put("VUV", new CurrencyDataImpl("VUV", "VUV", 0, "VUV", "VUV"));
    result.put("WST", new CurrencyDataImpl("WST", "WST", 2, "WST", "WST"));
    result.put("XAF", new CurrencyDataImpl("XAF", "FCFA", 0, "FCFA", "FCFA"));
    result.put("XCD", new CurrencyDataImpl("XCD", "EC$", 2, "EC$", "$"));
    result.put("XOF", new CurrencyDataImpl("XOF", "CFA", 0, "CFA", "CFA"));
    result.put("XPF", new CurrencyDataImpl("XPF", "CFPF", 0, "CFPF", "FCFP"));
    result.put("XXX", new CurrencyDataImpl("XXX", "XXX", 130, "XXX", "XXX"));
    result.put("YER", new CurrencyDataImpl("YER", "YER", 0, "YER", "Rial"));
    result.put("ZAR", new CurrencyDataImpl("ZAR", "ZAR", 2, "ZAR", "R"));
    result.put("ZMK", new CurrencyDataImpl("ZMK", "ZMK", 0, "ZMK", "ZWK"));
    return result;
  }
  
  @Override
  protected Map<String, String> loadCurrencyNames() {
    Map<String, String> result = super.loadCurrencyNames();
    result.put("AED", "Dirham Emiriah Arab Bersatu");
    result.put("AFN", "Afghani Afghanistan");
    result.put("ALL", "Lek Albania");
    result.put("AMD", "Dram Armenia");
    result.put("ANG", "Guilder Antillean Netherland");
    result.put("AOA", "Kwanza Angola");
    result.put("ARS", "Peso Argentina");
    result.put("AUD", "Dolar Australia");
    result.put("AWG", "Florin Aruba");
    result.put("AZN", "Manat Azerbaijan");
    result.put("BAM", "Mark Boleh Tukar Bosnia-Herzegovina");
    result.put("BBD", "Dolar Barbados");
    result.put("BDT", "Taka Bangladesh");
    result.put("BGN", "Lev Bulgaria");
    result.put("BHD", "Dinar Bahrain");
    result.put("BIF", "Franc Burundia");
    result.put("BMD", "Dolar Bermuda");
    result.put("BND", "Dolar Brunei");
    result.put("BOB", "Boliviano Bolivia");
    result.put("BRL", "Real Brazil");
    result.put("BSD", "Dolar Bahamas");
    result.put("BTN", "Ngultrum Bhutan");
    result.put("BWP", "Pula Botswana");
    result.put("BYR", "Ruble Belarus");
    result.put("BZD", "Dolar Belize");
    result.put("CAD", "Dolar Kanada");
    result.put("CDF", "Franc Congo");
    result.put("CHF", "Franc Switzerland");
    result.put("CLP", "Peso Chile");
    result.put("CNY", "Yuan Cina");
    result.put("COP", "Peso Colombia");
    result.put("CRC", "Colón Costa Rica");
    result.put("CUC", "Peso Boleh Tukar Cuba");
    result.put("CUP", "Peso Cuba");
    result.put("CVE", "Escudo Tanjung Verde");
    result.put("CZK", "Koruna Republik Czech");
    result.put("DJF", "Franc Djibouti");
    result.put("DKK", "Krone Denmark");
    result.put("DOP", "Peso Dominican");
    result.put("DZD", "Dinar Algeria");
    result.put("EGP", "Paun Mesir");
    result.put("ERN", "Nakfa Eritrea");
    result.put("ETB", "Birr Ethiopia");
    result.put("EUR", "Euro");
    result.put("FJD", "Dolar Fiji");
    result.put("FKP", "Paun Kepulauan Falkland");
    result.put("GBP", "Paun Sterling British");
    result.put("GEL", "Lari Georgia");
    result.put("GHS", "Cedi Ghana");
    result.put("GIP", "Paun Gibraltar");
    result.put("GMD", "Dalasi Gambia");
    result.put("GNF", "Franc Guinea");
    result.put("GTQ", "Quetzal Guatemala");
    result.put("GYD", "Dolar Guyana");
    result.put("HKD", "Dolar Hong Kong");
    result.put("HNL", "Lempira Honduras");
    result.put("HRK", "Kuna Croatia");
    result.put("HTG", "Gourde Haiti");
    result.put("HUF", "Forint Hungary");
    result.put("IDR", "Rupiah Indonesia");
    result.put("ILS", "Sheqel Baru Israel");
    result.put("INR", "Rupee India");
    result.put("IQD", "Dinar Iraq");
    result.put("IRR", "Rial Iran");
    result.put("ISK", "Króna Iceland");
    result.put("JMD", "Dolar Jamaica");
    result.put("JOD", "Dinar Jordan");
    result.put("JPY", "Yen Jepun");
    result.put("KES", "Syiling Kenya");
    result.put("KGS", "Som Kyrgystani");
    result.put("KHR", "Riel Kemboja");
    result.put("KMF", "Franc Comoria");
    result.put("KPW", "Won Korea Utara");
    result.put("KRW", "Won Korea Selatan");
    result.put("KWD", "Dinar Kuwait");
    result.put("KYD", "Dolar Kepulauan Cayman");
    result.put("KZT", "Tenge Kazakhstan");
    result.put("LAK", "Kip Laos");
    result.put("LBP", "Paun Lubnan");
    result.put("LKR", "Rupee Sri Lanka");
    result.put("LRD", "Dolar Liberia");
    result.put("LSL", "Loti Lesotho");
    result.put("LTL", "Litas Lithuania");
    result.put("LVL", "Lats Latvia");
    result.put("LYD", "Dinar Libya");
    result.put("MAD", "Dirham Maghribi");
    result.put("MDL", "Leu Moldova");
    result.put("MGA", "Ariary Malagasy");
    result.put("MKD", "Denar Macedonia");
    result.put("MMK", "Kyat Myanma");
    result.put("MNT", "Tugrik Mongolia");
    result.put("MOP", "Pataca Macau");
    result.put("MRO", "Ouguiya Mauritania");
    result.put("MUR", "Rupee Mauritia");
    result.put("MVR", "Rufiyaa Maldives");
    result.put("MWK", "Kwacha Malawi");
    result.put("MXN", "Peso Mexico");
    result.put("MYR", "Ringgit Malaysia");
    result.put("NAD", "Dolar Namibia");
    result.put("NGN", "Naira Nigeria");
    result.put("NIO", "Córdoba Nicaragua");
    result.put("NOK", "Krone Norway");
    result.put("NPR", "Rupee Nepal");
    result.put("NZD", "Dolar New Zealand");
    result.put("OMR", "Rial Oman");
    result.put("PAB", "Balboa Panama");
    result.put("PEN", "Nuevo Sol Peru");
    result.put("PGK", "Kina Papua New Guinea");
    result.put("PHP", "Peso Filipina");
    result.put("PKR", "Rupee Pakistan");
    result.put("PLN", "Zloty Poland");
    result.put("PYG", "Guarani Paraguay");
    result.put("QAR", "Rial Qatar");
    result.put("RSD", "Dinar Serbia");
    result.put("RUB", "Ruble Rusia");
    result.put("RWF", "Franc Rwanda");
    result.put("SAR", "Riyal Saudi");
    result.put("SBD", "Dolar Kepulauan Solomon");
    result.put("SCR", "Rupee Seychelles");
    result.put("SDG", "Paun Sudan");
    result.put("SEK", "Krona Sweden");
    result.put("SGD", "Dolar Singapura");
    result.put("SHP", "Paun Saint Helena");
    result.put("SLL", "Leone Sierra Leone");
    result.put("SOS", "Syiling Somali");
    result.put("SRD", "Dolar Surinam");
    result.put("STD", "São Tomé dan Príncipe Dobra");
    result.put("SYP", "Paun Syria");
    result.put("SZL", "Lilangeni Swazi");
    result.put("THB", "Baht Thai");
    result.put("TJS", "Somoni Tajikistan");
    result.put("TMT", "Manat Turkmenistan");
    result.put("TND", "Dinar Tunisia");
    result.put("TOP", "Tongan Paʻanga");
    result.put("TRY", "Lira Turki");
    result.put("TTD", "Dolar Trinidad dan Tobago");
    result.put("TWD", "Dolar Taiwan Baru");
    result.put("TZS", "Syiling Tanzania");
    result.put("UAH", "Hryvnia Ukraine");
    result.put("UGX", "Syiling Uganda");
    result.put("USD", "Dolar AS");
    result.put("UYU", "Peso Uruguay");
    result.put("UZS", "Som Uzbekistan");
    result.put("VEF", "Bolívar Venezuela");
    result.put("VND", "Dong Vietnam");
    result.put("VUV", "Vatu Vanuatu");
    result.put("WST", "Tala Samoa");
    result.put("XAF", "Franc CFA BEAC");
    result.put("XCD", "Dolar Caribbean Timur");
    result.put("XOF", "Franc CFA BCEAO");
    result.put("XPF", "Franc CFP");
    result.put("XXX", "Mata Wang Tidak Diketahui");
    result.put("YER", "Rial Yaman");
    result.put("ZAR", "Rand Afrika Selatan");
    result.put("ZMK", "Kwacha Zambia");
    return result;
  }
}
