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
 *  * Pure Java implementation of CurrencyList for locale "ta".
 */
public class CurrencyListImpl_ta extends CurrencyListImpl {
  
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
    result.put("AED", "ஐக்கிய அரபு எமிரேட்டு டிர்ஹம்");
    result.put("AFN", "ஆஃப்கான் ஆப்கானி");
    result.put("ALL", "அல்பேனியன் லெக்");
    result.put("AMD", "அர்மேனியன் ட்ராம்");
    result.put("ANG", "நெதர்லேண்ட்ஸ் அன்டிலியன் கில்டர்");
    result.put("AOA", "அங்கோலாவின் க்வான்ஸா");
    result.put("ARS", "அர்ஜென்டினாவின் பேசோ");
    result.put("AUD", "ஆஸ்திரேலிய டாலர்");
    result.put("AWG", "அருபன் ஃப்ளோரின்");
    result.put("AZN", "அஜர்பைசானி மனத்");
    result.put("BAM", "போஸ்னியா-ஹெர்ஸேகோவினா கன்வெர்டிபில் மார்க்");
    result.put("BBD", "பெர்பேடியன் டாலர்");
    result.put("BDT", "பங்கலாதேஷி டாகா");
    result.put("BGN", "புல்கேரியன் லெவ்");
    result.put("BHD", "பஹ்ரைனி தினார்");
    result.put("BIF", "புருண்டியன் ஃப்ரான்க்");
    result.put("BMD", "பெர்முடன் டாலர்");
    result.put("BND", "புரூனை டாலர்");
    result.put("BOB", "பொலீவியன் பொலிவியானோ");
    result.put("BRL", "பிரேசிலியன் ரியல்");
    result.put("BSD", "பஹாமியன் டாலர்");
    result.put("BTN", "புடனேஷ் நிகுல்ட்ரம்");
    result.put("BWP", "போட்ஸ்வானா புலா");
    result.put("BYR", "பெலருஷியன் ரூபில்");
    result.put("BZD", "பெலீஷ் டாலர்");
    result.put("CAD", "கனடியன் டாலர்");
    result.put("CDF", "காங்கோல்ஸே ஃப்ராங்க்");
    result.put("CHF", "சுவிஸ் ஃப்ராங்க்");
    result.put("CLP", "சிலியன் பெசோ");
    result.put("CNY", "சீன யுவான்");
    result.put("COP", "கொலம்பியன் பெசோ");
    result.put("CRC", "கோஸ்டா ரிகன் கொலோன்");
    result.put("CUC", "கியூபன் கான்வெர்டிபில் பேசோ");
    result.put("CUP", "கியூபன் பெசோ");
    result.put("CVE", "கேப் வெர்டியன் எஸ்குடோ");
    result.put("CZK", "செக் குடியரசு கொருனா");
    result.put("DJF", "ஜிபவ்டியென் ஃப்ராங்க்");
    result.put("DKK", "டானிஷ் க்ரோன்");
    result.put("DOP", "டாமினிகன் பேசோ");
    result.put("DZD", "அல்ஜேரியன் தினார்");
    result.put("EGP", "எகிப்திய பவுண்டு");
    result.put("ERN", "இரிடிரியன் நக்ஃபா");
    result.put("ETB", "எத்தியோப்பியன் பிர்");
    result.put("EUR", "யூரோ");
    result.put("FJD", "ஃபிஜியன் டாலர்");
    result.put("FKP", "ஃபாக்லாந்து தீவுகள் பவுண்டு");
    result.put("GBP", "பிரிட்டிஷ் பவுன்ட் ஸ்டெர்லிங்");
    result.put("GEL", "ஜியார்ஜியன் லாரி");
    result.put("GHS", "கானியன் செடி");
    result.put("GIP", "கிப்ரால்டர் பவுண்ட்");
    result.put("GMD", "கேம்பியன் தலாசி");
    result.put("GNF", "கினியன் ஃப்ராங்க்");
    result.put("GTQ", "குவாடெமெலன் குயூட்ஸல்");
    result.put("GYD", "குவனீஸ் டாலர்");
    result.put("HKD", "ஹாங்காங் டாலர்");
    result.put("HNL", "ஹோன்டூரன் லெம்பீரா");
    result.put("HRK", "குரோஷியன் குனா");
    result.put("HTG", "ஹயேத்தியன் கோர்டே");
    result.put("HUF", "ஹங்கேரியன் ஃபோரின்ட்");
    result.put("IDR", "இந்தோனேஷியன் ருபியா");
    result.put("ILS", "இஸ்ரேலி நியூ ஷிகேல்");
    result.put("INR", "ரூபாய்");
    result.put("IQD", "ஈராக்கி தினார்");
    result.put("IRR", "இரானியன் ரியால்");
    result.put("ISK", "ஐஸ்லாண்டிக் க்ரோனா");
    result.put("JMD", "ஜமைக்கான் டாலர்");
    result.put("JOD", "ஜோர்டானியன் டைனர்");
    result.put("JPY", "ஜாப்பனிய யென்");
    result.put("KES", "கெனியன் ஷில்லிங்");
    result.put("KGS", "கிரிகிஸ்தானி சோம்");
    result.put("KHR", "கம்போடியன் ரியெல்");
    result.put("KMF", "கமோரியன் ஃப்ராங்க்");
    result.put("KPW", "வட கொரிய வான்");
    result.put("KRW", "தென் கொரிய வான்");
    result.put("KWD", "குவைத்தி தினார்");
    result.put("KYD", "கேமன் தீவுகள் டாலர்");
    result.put("KZT", "கஸகஸ்தானி டென்கே");
    result.put("LAK", "லவுட்டியன் கிப்");
    result.put("LBP", "லபனீஸ் பவுண்டு");
    result.put("LKR", "இலங்கை ரூபாய்");
    result.put("LRD", "லிபரியன் டாலர்");
    result.put("LSL", "லெசோதோ லோட்டி");
    result.put("LTL", "லிதுவேனியன் லிடஸ்");
    result.put("LVL", "லாத்வியன் லாட்ஸ்");
    result.put("LYD", "லிபியன் தினார்");
    result.put("MAD", "மொரோக்கோ திர்ஹாம்");
    result.put("MDL", "மால்டோவன் லியூ");
    result.put("MGA", "மலகாசி ஏரியரி");
    result.put("MKD", "மெசிடோனியன் தினார்");
    result.put("MMK", "மியான்மா கியாத்");
    result.put("MNT", "மங்கோலியன் டுக்ரிக்");
    result.put("MOP", "மெகனீஸ் படாகா");
    result.put("MRO", "மொரிஷியனியன் ஒகுய்யா");
    result.put("MUR", "மொரீஷியன் ருபீ");
    result.put("MVR", "மால்தீவியன் ருஃபியா");
    result.put("MWK", "மலாவின் குவாச்சா");
    result.put("MXN", "மெக்ஸிகன் பெசோ");
    result.put("MYR", "மலேஷியன் ரிங்கித்");
    result.put("NAD", "நமீபியன் டாலர்");
    result.put("NGN", "நைஜீரியன் நைரா");
    result.put("NIO", "நிகாராகுவான் கோர்டோபா");
    result.put("NOK", "நார்வேஜியன் க்ரோன்");
    result.put("NPR", "நேபாளீஸ் ருபீ");
    result.put("NZD", "நியூசிலாந்து டாலர்");
    result.put("OMR", "ஓமானி ரியால்");
    result.put("PAB", "பானாமானியன் பால்போபா");
    result.put("PEN", "பெரூவியன் நியூவோ சோல்");
    result.put("PGK", "பபுவா நியூ கினியன் கினா");
    result.put("PHP", "ஃபிலிபைன் பேசோ");
    result.put("PKR", "பாக்கிஸ்தானி ருபீ");
    result.put("PLN", "போலிஷ் ஸ்லாட்டி");
    result.put("PYG", "பராகுயான் குவாரானி");
    result.put("QAR", "கத்தாரி ரியால்");
    result.put("RSD", "செர்பியன் தினார்");
    result.put("RUB", "ரஷ்யன் ரூபல்");
    result.put("RWF", "ருவாண்டா ஃப்ராங்க்");
    result.put("SAR", "சவுதி ரியால்");
    result.put("SBD", "சாலமன் தீவுகள் டாலர்");
    result.put("SCR", "சிஷிலோயிஸ் ருபீ");
    result.put("SDG", "சுதனீஸ் பவுண்டு");
    result.put("SEK", "ஸ்வீடிஷ் க்ரோனா");
    result.put("SGD", "சிங்கப்பூர் டாலர்");
    result.put("SHP", "செயன்ட் ஹெலேனா பவுண்டு");
    result.put("SLL", "செய்ரா லியோனியன் லியோன்");
    result.put("SOS", "சொமாலி ஷில்லிங்");
    result.put("SRD", "சூரினாமீஸ் டாலர்");
    result.put("STD", "சாவ் டோமி மற்றும் பிரின்ஸ்பி டோப்ரா");
    result.put("SYP", "சிரியன் பவுன்ட்");
    result.put("SZL", "சுவாஸி லிலாங்கனி");
    result.put("THB", "தாய் பாட்");
    result.put("TJS", "தஜிகிஸ்தானி சோமோனி");
    result.put("TMT", "துர்க்மேனிஸ்தானி மனத்");
    result.put("TND", "துனிஷியன் தினார்");
    result.put("TOP", "தொங்கான் பங்கா");
    result.put("TRY", "துர்க்கிஸ் லீரா");
    result.put("TTD", "டிரினாட் மற்றும் டோபாகோ டாலர்");
    result.put("TWD", "புதிய தைவான் டாலர்");
    result.put("TZS", "தன்ஸானியன் ஷில்லிங்");
    result.put("UAH", "உக்ரைனியன் ஹிரைவ்னியா");
    result.put("UGX", "உகாண்டன் ஷில்லிங்");
    result.put("USD", "யூ.எஸ். டாலர்");
    result.put("UYU", "உருகுவேயன் பேசோ");
    result.put("UZS", "உஜ்பெகிஸ்தான் சோம்");
    result.put("VEF", "வெனிசுலியன் போலிவர்");
    result.put("VND", "வியட்நாமீஸ் டாங்");
    result.put("VUV", "வனுவாட்டு வாட்டு");
    result.put("WST", "சமோவான் தாலா");
    result.put("XAF", "CFA ஃப்ரேங்க் BEAC");
    result.put("XCD", "கிழக்கு கெரேபியன் டாலர்");
    result.put("XOF", "CFA ஃப்ரேங்க் BCEAO");
    result.put("XPF", "CFP ஃப்ராங்க்");
    result.put("XXX", "தெரியாத நாணயம்");
    result.put("YER", "யெமினி ரியால்");
    result.put("ZAR", "தென் ஆப்ரிக்க ராண்ட்");
    result.put("ZMK", "ஸாம்பியன் குவாசா");
    return result;
  }
}
