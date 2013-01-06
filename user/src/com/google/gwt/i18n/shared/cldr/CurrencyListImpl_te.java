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
 *  * Pure Java implementation of CurrencyList for locale "te".
 */
public class CurrencyListImpl_te extends CurrencyListImpl {
  
  @Override
  public CurrencyData getDefault() {
    return new CurrencyDataImpl("INR", "Rs.", 2, "Rs", "₹");
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
    result.put("XAG", new CurrencyDataImpl("XAG", "XAG", 130, "XAG", "XAG"));
    result.put("XAU", new CurrencyDataImpl("XAU", "XAU", 130, "XAU", "XAU"));
    result.put("XCD", new CurrencyDataImpl("XCD", "EC$", 2, "EC$", "$"));
    result.put("XOF", new CurrencyDataImpl("XOF", "CFA", 0, "CFA", "CFA"));
    result.put("XPF", new CurrencyDataImpl("XPF", "CFPF", 0, "CFPF", "FCFP"));
    result.put("XPT", new CurrencyDataImpl("XPT", "XPT", 130, "XPT", "XPT"));
    result.put("XXX", new CurrencyDataImpl("XXX", "XXX", 130, "XXX", "XXX"));
    result.put("YER", new CurrencyDataImpl("YER", "YER", 0, "YER", "Rial"));
    result.put("ZAR", new CurrencyDataImpl("ZAR", "ZAR", 2, "ZAR", "R"));
    result.put("ZMK", new CurrencyDataImpl("ZMK", "ZMK", 0, "ZMK", "ZWK"));
    return result;
  }
  
  @Override
  protected Map<String, String> loadCurrencyNames() {
    Map<String, String> result = super.loadCurrencyNames();
    result.put("AED", "యునైటెడ్ ఆరబ్ ఎమిరేట్స్ దిరామ్");
    result.put("AFN", "ఆఫ్ఘాన్ ఆఫ్ఘాని");
    result.put("ALL", "ఆల్బేనియన్ లేక్");
    result.put("AMD", "అమెరికన్ డ్రామ్");
    result.put("ANG", "నెధర్లాండ్స్ ఏంటీల్లియన్ గిల్‌డూర్");
    result.put("AOA", "అంగోలాన్ క్వాన్‌జా");
    result.put("ARS", "అర్జెంటీనా పెసో");
    result.put("AUD", "ఆస్ట్రేలియన్ డాలర్");
    result.put("AWG", "అరుబన్ ఫ్లోరిన్");
    result.put("AZN", "అజర్బైజాన్ మానట్");
    result.put("BAM", "బోస్నియా-హెర్జగోవినా మార్పిడి చెయ్యగలిగే గుర్తు");
    result.put("BBD", "బర్బాడియన్ డాలర్");
    result.put("BDT", "బాంగ్లాదేశ్ టాకా");
    result.put("BGN", "బల్గేరియన్ లేవ్");
    result.put("BHD", "బహ్రైని దీనార్");
    result.put("BIF", "బురిండియన్ ఫ్రాంక్");
    result.put("BMD", "బెర్ముడన్ డాలర్");
    result.put("BND", "బ్రూనై డాలర్");
    result.put("BOB", "బొలీవియన్ బొలీవియానో");
    result.put("BRL", "బ్రజిలియన్ రియల్");
    result.put("BSD", "బహామియన్ డాలర్");
    result.put("BTN", "భూటానీయుల గుల్‌ట్రుమ్");
    result.put("BWP", "బోట్స్‌వానా పులా");
    result.put("BYR", "బెలరూసియన్ రూబుల్");
    result.put("BZD", "బెలీజ్ డాలర్");
    result.put("CAD", "కెనడియన్ డాలర్");
    result.put("CDF", "కొంగోలిస్ ఫ్రాంక్");
    result.put("CHF", "స్విస్ ఫ్రాంక్");
    result.put("CLP", "చిలియన్ పెసో");
    result.put("CNY", "చైనా దేశ యువాన్ రెన్‌మిన్‌బి");
    result.put("COP", "కొలంబియన్ పెసో");
    result.put("CRC", "కోస్టా రిసన్ కోలోన్");
    result.put("CUC", "కుబన్ మార్పిడి చెయ్యగలిగే పెసో");
    result.put("CUP", "క్యూబన్ పెసో");
    result.put("CVE", "కేప్ వెర్డియన్ ఎస్కుడో");
    result.put("CZK", "చెక్ రిపబ్లిక్ కోరునా");
    result.put("DJF", "జిబోటియన్ ఫ్రాంక్");
    result.put("DKK", "డానిష్ క్రోన్");
    result.put("DOP", "డోమినికన్ పెసో");
    result.put("DZD", "ఆల్గేరియన్ దీనార్");
    result.put("EGP", "ఈజిప్షియన్ పౌండ్");
    result.put("ERN", "ఎరిట్రీన్ నక్ఫా");
    result.put("ETB", "ఇథియోపియన్ బుర్");
    result.put("EUR", "యురొ");
    result.put("FJD", "ఫీజియన్ డాలర్");
    result.put("FKP", "ఫాక్‌ల్యాండ్ దీవులు పౌండ్");
    result.put("GBP", "బ్రిటిష్ పౌండ్ స్టెర్లింగ్");
    result.put("GEL", "జార్జియన్ లారి");
    result.put("GHS", "గానెయన్ సెడి");
    result.put("GIP", "జిబ్రల్‌టూర్ పౌండ్");
    result.put("GMD", "గాంబియన్ దలాసి");
    result.put("GNF", "గ్వినియన్ ఫ్రాంక్");
    result.put("GTQ", "గ్యుటెమాలన్ క్వెట్‌జిల్");
    result.put("GYD", "గుయనియాస్ డాలర్");
    result.put("HKD", "హాంకాంగ్ డాలర్");
    result.put("HNL", "హోండ్రురన్ లెమిపిరా");
    result.put("HRK", "క్రొయేషియన్ క్యూన");
    result.put("HTG", "హైటియన్ గ్వోర్డే");
    result.put("HUF", "హంగేరియన్ ఫోరిన్ట్");
    result.put("IDR", "ఇండోనేషియా రూపాయి");
    result.put("ILS", "ఐరాయిలి న్యూ షెక్యెల్");
    result.put("INR", "రూపాయి");
    result.put("IQD", "ఇరాకీ డైనర్");
    result.put("IRR", "ఇరానియన్ రీయల్");
    result.put("ISK", "ఐస్లాండిక్ క్రోనా");
    result.put("JMD", "జమైకన్ డాలర్");
    result.put("JOD", "జోర్‌డానియన్ డైనర్");
    result.put("JPY", "జపాను దేశ యెస్");
    result.put("KES", "కెన్యాన్ షిల్లింగ్");
    result.put("KGS", "కిర్గిస్థాని సౌమ్");
    result.put("KHR", "కాంబోడియన్ రీల్");
    result.put("KMF", "కొమోరియన్ ఫ్రాంక్");
    result.put("KPW", "ఉత్తర కొరియా వోన్");
    result.put("KRW", "దక్షిణ కొరియా వోన్");
    result.put("KWD", "కువైట్ దీనార్");
    result.put("KYD", "కేమాన్ దీవుల డాలర్");
    result.put("KZT", "ఖజికిస్థాన్ టెంగే");
    result.put("LAK", "లాటియన్ కిప్");
    result.put("LBP", "లెబనీస్ పౌండ్");
    result.put("LKR", "శ్రీలంక రూపాయి");
    result.put("LRD", "లిబేరియన్ డాలర్");
    result.put("LSL", "లెసోధో లోటి");
    result.put("LTL", "లిథోనియన్ లీటాస్");
    result.put("LVL", "లాత్వియన్ లాట్స్");
    result.put("LYD", "లిబియన్ దీనార్");
    result.put("MAD", "మోరోకన్ దిర్హుమ్");
    result.put("MDL", "మోల్‌డోవన్ ల్యూ");
    result.put("MGA", "మలగసీ అరియరీ");
    result.put("MKD", "మసిడోనియన్ దినార్");
    result.put("MMK", "మ్యాన్మా క్యాట్");
    result.put("MNT", "మంగోలియన్ టుగ్రిక్");
    result.put("MOP", "మకనీస్ పటాక");
    result.put("MRO", "మౌరిటానియన్ ఒగ్యియా");
    result.put("MUR", "మారిషన్ రూపాయి");
    result.put("MVR", "మాల్దీవియన్ రుఫియా");
    result.put("MWK", "మలావియన్ క్వాచా");
    result.put("MXN", "మెక్సికన్ పెసో");
    result.put("MYR", "మలేషియా రింగ్గిట్");
    result.put("NAD", "నమిబియన్ డాలర్");
    result.put("NGN", "నైజీరియన్ నెరు");
    result.put("NIO", "నికరగ్యుయన్ కొర్‌డుబు");
    result.put("NOK", "నార్వేజీయన్ క్రోన్");
    result.put("NPR", "నేపాలీయుల రూపాయి");
    result.put("NZD", "న్యూజిలాండ్ డాలర్");
    result.put("OMR", "ఒమాని రీయల్");
    result.put("PAB", "పనామనియన్ బల్బోయ");
    result.put("PEN", "పెరువియన్ న్యూవో సోల్");
    result.put("PGK", "పప్యూ న్యూ గ్యినియన్ కినా");
    result.put("PHP", "ఫిలిప్పిన్ పెసో");
    result.put("PKR", "పాకిస్థాన్ రూపాయి");
    result.put("PLN", "పోలిష్ జ్లోటీ");
    result.put("PYG", "పరగ్వాయన్ గ్వారని");
    result.put("QAR", "క్వాటరి రీయల్");
    result.put("RSD", "సెర్బియన్ దీనార్");
    result.put("RUB", "రష్యా రూబల్");
    result.put("RWF", "ర్వానడాన్ ఫ్రాంక్");
    result.put("SAR", "సౌది రియల్");
    result.put("SBD", "సోలోమన్ ఐలాండ్స్ డాలర్");
    result.put("SCR", "సెయిచెల్లోయిస్ రూపాయి");
    result.put("SDG", "సుడానీస్ పౌండ్");
    result.put("SEK", "స్వీడిష్ క్రోనా");
    result.put("SGD", "సింగపూర్ డాలర్");
    result.put("SHP", "సెయింట్ హెలెనా పౌండ్");
    result.put("SLL", "సీయిరు లియోనియన్ లీయోన్");
    result.put("SOS", "సొమాలి షిల్లింగ్");
    result.put("SRD", "సురినామీయుల డాలర్");
    result.put("STD", "సావో టోమ్ మరియు ప్రిన్సిపి డోబ్రా");
    result.put("SYP", "సిరీయన్ పౌండ్");
    result.put("SZL", "స్వాజి లిలాన్గేని");
    result.put("THB", "థై బాట్");
    result.put("TJS", "తజికిస్థాన్ సమోని");
    result.put("TMT", "టుర్క్‌మెనిస్థాని మనాట్");
    result.put("TND", "తునీషియన్ దీనార్");
    result.put("TOP", "టోంగాన్ పాంʻగా");
    result.put("TRY", "తుర్కిష్ లిరా");
    result.put("TTD", "ట్రినిడాడ్ మరియు టొబాగో డాలర్");
    result.put("TWD", "క్రొత్త తైవాన్ డాలర్");
    result.put("TZS", "టాంజానియన్ షిల్లింగ్");
    result.put("UAH", "ఉక్రయినియన్ హ్రివ్‌నియా");
    result.put("UGX", "యుగండన్ షిల్లింగ్");
    result.put("USD", "ఐక్య రాష్ట్ర అమెరిక డాలర్");
    result.put("UYU", "ఉరుగ్వెయన్ పెసో");
    result.put("UZS", "ఉజ్‌బెకిస్తాన్ సౌమ్");
    result.put("VEF", "వెనుజులా బోలివర్");
    result.put("VND", "వియత్నామీయుల డాంగ్");
    result.put("VUV", "వవాటు వటు");
    result.put("WST", "సమోయన్ తాలా");
    result.put("XAF", "సిఎఫ్‌ఎ ఫ్రాంక్ బిఇఏసి");
    result.put("XAG", "వెండి");
    result.put("XAU", "బంగారం");
    result.put("XCD", "తూర్పు కరిబ్బియన్ డాలర్");
    result.put("XOF", "సిఎఫ్‌ఎ ఫ్రాంక్ బిసిఈఏఓ");
    result.put("XPF", "సిఎఫ్‌పి ఫ్రాంక్");
    result.put("XPT", "ప్లాటినం");
    result.put("XXX", "తెలియని కరెన్సీ");
    result.put("YER", "ఎమునీ రీయల్");
    result.put("ZAR", "దక్షిణ ఆఫ్రికా ర్యాండ్");
    result.put("ZMK", "జాంబియన్ క్వాచా");
    return result;
  }
}
