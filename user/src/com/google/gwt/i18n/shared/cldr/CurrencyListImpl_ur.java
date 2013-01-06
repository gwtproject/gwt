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
 *  * Pure Java implementation of CurrencyList for locale "ur".
 */
public class CurrencyListImpl_ur extends CurrencyListImpl {
  
  @Override
  public CurrencyData getDefault() {
    return new CurrencyDataImpl("PKR", "PKRs", 0, "PKRs.", "Rs");
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
    result.put("DEM", new CurrencyDataImpl("DEM", "DEM", 130, "DEM", "DEM"));
    result.put("DJF", new CurrencyDataImpl("DJF", "Fdj", 0, "Fdj", "Fdj"));
    result.put("DKK", new CurrencyDataImpl("DKK", "kr", 2, "kr", "kr"));
    result.put("DOP", new CurrencyDataImpl("DOP", "RD$", 2, "RD$", "$"));
    result.put("DZD", new CurrencyDataImpl("DZD", "DZD", 2, "DZD", "din"));
    result.put("EEK", new CurrencyDataImpl("EEK", "EEK", 130, "EEK", "EEK"));
    result.put("EGP", new CurrencyDataImpl("EGP", "LE", 2, "LE", "E£"));
    result.put("ERN", new CurrencyDataImpl("ERN", "ERN", 2, "ERN", "Nfk"));
    result.put("ETB", new CurrencyDataImpl("ETB", "ETB", 2, "ETB", "Birr"));
    result.put("EUR", new CurrencyDataImpl("EUR", "€", 2, "€", "€"));
    result.put("FJD", new CurrencyDataImpl("FJD", "FJD", 2, "FJD", "$"));
    result.put("FKP", new CurrencyDataImpl("FKP", "FKP", 2, "FKP", "£"));
    result.put("FRF", new CurrencyDataImpl("FRF", "FRF", 130, "FRF", "FRF"));
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
    result.put("PKR", new CurrencyDataImpl("PKR", "PKRs", 0, "PKRs.", "Rs"));
    result.put("PLN", new CurrencyDataImpl("PLN", "PLN", 2, "PLN", "zł"));
    result.put("PYG", new CurrencyDataImpl("PYG", "PYG", 0, "PYG", "Gs"));
    result.put("QAR", new CurrencyDataImpl("QAR", "QAR", 2, "QAR", "Rial"));
    result.put("RON", new CurrencyDataImpl("RON", "RON", 2, "RON", "RON"));
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
    result.put("SIT", new CurrencyDataImpl("SIT", "SIT", 130, "SIT", "SIT"));
    result.put("SKK", new CurrencyDataImpl("SKK", "SKK", 130, "SKK", "SKK"));
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
    result.put("USD", new CurrencyDataImpl("USD", "$", 2, "US$", "$"));
    result.put("UYU", new CurrencyDataImpl("UYU", "UY$", 2, "UY$", "$"));
    result.put("UZS", new CurrencyDataImpl("UZS", "UZS", 0, "UZS", "soʼm"));
    result.put("VEB", new CurrencyDataImpl("VEB", "VEB", 130, "VEB", "VEB"));
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
    result.put("AED", "متحدہ عرب اماراتی درہم");
    result.put("AFN", "افغان افغانی");
    result.put("ALL", "البانیا کا لیک");
    result.put("AMD", "آرمینیائی ڈرم");
    result.put("ANG", "نیدر لینڈز انٹیلیئن گلڈر");
    result.put("AOA", "انگولا کا کوانزا");
    result.put("ARS", "ارجنٹائن پیسہ");
    result.put("AUD", "آسٹریلین ڈالر");
    result.put("AWG", "اروبن فلورِن");
    result.put("AZN", "آذربائجانی منات");
    result.put("BAM", "بوسنیا ہرزیگووینا کا قابل منتقلی نشان");
    result.put("BBD", "باربیڈین ڈالر");
    result.put("BDT", "بنگلہ دیشی ٹکا");
    result.put("BGN", "بلغارین لیو");
    result.put("BHD", "بحرینی دینار");
    result.put("BIF", "برونڈیئن فرینک");
    result.put("BMD", "برمودا کا ڈالر");
    result.put("BND", "برونئی ڈالر");
    result.put("BOB", "بولیوین بولیویانو");
    result.put("BRL", "برازیلی ریئل");
    result.put("BSD", "بہامانی ڈالر");
    result.put("BTN", "بھوٹانی گُلٹرم");
    result.put("BWP", "بوتسوانا کا پولا");
    result.put("BYR", "بیلاروسی روبل");
    result.put("BZD", "بیلائز ڈالر");
    result.put("CAD", "کنیڈین ڈالر");
    result.put("CDF", "کانگولیز فرینک");
    result.put("CHF", "سوئس فرانکس");
    result.put("CLP", "چلّین پیسہ");
    result.put("CNY", "یوآن رینمنبی");
    result.put("COP", "کولمبین پیسہ");
    result.put("CRC", "کوسٹا ریکا کا کولن");
    result.put("CUC", "کیوبا کا قابل منتقلی پیسو");
    result.put("CUP", "کیوبا کا پیسو");
    result.put("CVE", "کیپ ورڈی کا اسکیوڈو");
    result.put("CZK", "چیک کرونا");
    result.put("DEM", "ڈچ مارکس");
    result.put("DJF", "جبوتی کا فرینک");
    result.put("DKK", "ڈنمارک کرونر");
    result.put("DOP", "ڈومنیکن پیسو");
    result.put("DZD", "الجیریائی دینار");
    result.put("EEK", "ایسٹونین کرون");
    result.put("EGP", "مصری پائونڈ");
    result.put("ERN", "اریٹیریا کا نافکا");
    result.put("ETB", "ایتھوپیائی بِرّ");
    result.put("EUR", "یورو");
    result.put("FJD", "فجی کا ڈالر");
    result.put("FKP", "فاکلینڈ آئلینڈز پونڈ");
    result.put("FRF", "فرانسیسی فرانک");
    result.put("GBP", "انگلستانی پاونڈ سٹرلنگ");
    result.put("GEL", "جارجیائی لاری");
    result.put("GHS", "گھانا کا سیڈی");
    result.put("GIP", "جبل الطارق پونڈ");
    result.put("GMD", "گامبیا کا ڈلاسی");
    result.put("GNF", "گنی کا فرینک");
    result.put("GTQ", "گواٹے مالا کا کوئٹزل");
    result.put("GYD", "گویانیز ڈالر");
    result.put("HKD", "ھانگ کانگ ڈالر");
    result.put("HNL", "ہونڈوران لیمپیرا");
    result.put("HRK", "کروشین کونا");
    result.put("HTG", "ہیتی کا گؤرڈی");
    result.put("HUF", "ہنگرین فورنٹ");
    result.put("IDR", "انڈونیشین روپیہ");
    result.put("ILS", "اسرائیلی شیکل");
    result.put("INR", "انڈین روپیہ");
    result.put("IQD", "عراقی دینار");
    result.put("IRR", "ایرانی ریال");
    result.put("ISK", "آئس لینڈ کا کرونا");
    result.put("JMD", "جمائیکن ڈالر");
    result.put("JOD", "اردنی دینار");
    result.put("JPY", "جاپانی ین");
    result.put("KES", "کینیائی شلنگ");
    result.put("KGS", "کرغستانی سوم");
    result.put("KHR", "کمبوڈیائی ریئل");
    result.put("KMF", "کوموریئن فرینک");
    result.put("KPW", "شمالی کوریائی وون");
    result.put("KRW", "جنوبی کوریائی وون");
    result.put("KWD", "کویتی دینار");
    result.put("KYD", "کیمین آئلینڈز ڈالر");
    result.put("KZT", "قزاخستانی ٹینگ");
    result.put("LAK", "لاؤشیائی کِپ");
    result.put("LBP", "لبانی پونڈ");
    result.put("LKR", "سری لنکائی روپیہ");
    result.put("LRD", "لائبریائی ڈالر");
    result.put("LSL", "لیسوتھو لوٹی");
    result.put("LTL", "لیتھوانی لیٹاس");
    result.put("LVL", "لاتویائی لیٹس");
    result.put("LYD", "لیبیائی دینار");
    result.put("MAD", "مراکشی درہم");
    result.put("MDL", "مالدیپ کا لیو");
    result.put("MGA", "ملاگاسی اریاری");
    result.put("MKD", "مقدونیائی دینار");
    result.put("MMK", "میانما کیاٹ");
    result.put("MNT", "منگولیائی ٹگرِ");
    result.put("MOP", "میکانیز پٹاکا");
    result.put("MRO", "موریطانیائی اوگوئیا");
    result.put("MUR", "ماریشس کا روپیہ");
    result.put("MVR", "مالدیپ کا روفیہ");
    result.put("MWK", "ملاوی کواچا");
    result.put("MXN", "میکسیکی پیسہ");
    result.put("MYR", "ملیشیائی رنگِٹ");
    result.put("NAD", "نامیبیائی ڈالر");
    result.put("NGN", "نائیجیریائی نائرا");
    result.put("NIO", "نکارا گوا کا کورڈوبا");
    result.put("NOK", "ناروے کرونر");
    result.put("NPR", "نیپالی روپیہ");
    result.put("NZD", "نیوزی لینڈ ڈالر");
    result.put("OMR", "عمانی ریال");
    result.put("PAB", "پنامہ کا بالبوآ");
    result.put("PEN", "پیروین نیووسول");
    result.put("PGK", "پاپوآ نیو گنی کا کینا");
    result.put("PHP", "فلپائینی پیسہ");
    result.put("PKR", "پاکستانی روپیہ");
    result.put("PLN", "پولش نیو زلوٹی");
    result.put("PYG", "پیراگوئے کا گوآرنی");
    result.put("QAR", "قطری ریال");
    result.put("RON", "نیا رومانیائی لیو");
    result.put("RSD", "سربین دینار");
    result.put("RUB", "روسی روبل");
    result.put("RWF", "روانڈا کا فرینک");
    result.put("SAR", "سعودی ریال");
    result.put("SBD", "سولومن آئلینڈز ڈالر");
    result.put("SCR", "سشلی کا روپیہ");
    result.put("SDG", "سوڈانی پونڈ");
    result.put("SEK", "سویڈن کرونر");
    result.put("SGD", "سنگا پور ڈالر");
    result.put("SHP", "سینٹ ہیلینا پونڈ");
    result.put("SIT", "سلوانین ٹولر");
    result.put("SKK", "سلووک کرونا");
    result.put("SLL", "سیئرا لیون لیون");
    result.put("SOS", "صومالی شلنگ");
    result.put("SRD", "سورینامی ڈالر");
    result.put("STD", "ساؤ ٹوم اور پرنسپے ڈوبرا");
    result.put("SYP", "شامی پونڈ");
    result.put("SZL", "سوازی لیلانجینی");
    result.put("THB", "تھائی باہت");
    result.put("TJS", "تاجکستانی سومونی");
    result.put("TMT", "ترکمانستانی منات");
    result.put("TND", "تیونیسیائی دینار");
    result.put("TOP", "ٹونگن پانگا");
    result.put("TRY", "ترکی لیرا");
    result.put("TTD", "ترینیداد اور ٹوباگو کا ڈالر");
    result.put("TWD", "نیو تائیوان ڈالر");
    result.put("TZS", "تنزانیائی شلنگ");
    result.put("UAH", "یوکرینیائی ہریونیا");
    result.put("UGX", "یوگانڈا شلنگ");
    result.put("USD", "امریکی ڈالر");
    result.put("UYU", "یوروگویان پیسو");
    result.put("UZS", "ازبکستان سوم");
    result.put("VEB", "وینزویلا بولیور");
    result.put("VEF", "وینزوئیلا کا بولیور");
    result.put("VND", "ویتنامی ڈانگ");
    result.put("VUV", "وینوواتو واتو");
    result.put("WST", "ساموآ کا ٹالا");
    result.put("XAF", "CFA فرینک BEAC");
    result.put("XCD", "مشرقی کریبیا کا ڈالر");
    result.put("XOF", "CFA فرینک BCEAO");
    result.put("XPF", "CFP فرینک");
    result.put("XXX", "نامعلوم کرنسی");
    result.put("YER", "یمنی ریال");
    result.put("ZAR", "جنوبی افریقی رانڈ");
    result.put("ZMK", "زامبیائی کواچا");
    return result;
  }
}
