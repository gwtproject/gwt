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
//  number=$Revision: 6546 Google $
//  type=root
//  date=$Date: 2012-02-07 13:32:35 -0500 (Tue, 07 Feb 2012) $

/**
 * Pure Java implementation of CurrencyList for locale "gu".
 */
public class CurrencyListImpl_gu extends CurrencyListImpl {

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
    result.put("AED", "યુનાઇટેડ અરબ એમિરેટ્સ");
    result.put("AFN", "અફ્ગાન અફ્ગાની");
    result.put("ALL", "અલ્બેનિયન લેક");
    result.put("AMD", "અર્મેનિયન ડ્રેમ");
    result.put("ANG", "નેધરલેંડ એંટિલિન ગિલ્ડર");
    result.put("AOA", "અંગોલિયન ક્વાન્ઝા");
    result.put("ARS", "અર્જેન્ટીના પેસો");
    result.put("AUD", "ઑસ્ટ્રેલિયન ડૉલર");
    result.put("AWG", "અરુબન ફ્લોરિન");
    result.put("AZN", "અઝરબૈજાની મનાત");
    result.put("BAM", "બોસ્નિયા અને હર્ઝેગોવિના રૂપાંતર યોગ્ય માર્ક");
    result.put("BBD", "બાર્બાડિયન ડોલર");
    result.put("BDT", "બાંગલાદેશી ટાકા");
    result.put("BGN", "બલ્ગેરીયન લેવ");
    result.put("BHD", "બેહરિની દિનાર");
    result.put("BIF", "બુરુન્ડિયન ફ્રેંક");
    result.put("BMD", "બર્મુડન ડોલર");
    result.put("BND", "બ્રુનેઇ ડોલર");
    result.put("BOB", "બોલિવિયન બોલિવિયાનો");
    result.put("BRL", "બ્રાઝિલીયન રિઆલ");
    result.put("BSD", "બહામિયન ડોલર");
    result.put("BTN", "ભુતાનિઝ એંગુલ્ત્રમ");
    result.put("BWP", "બોત્સવાનન પુલા");
    result.put("BYR", "બેલારુશિયન રબલ");
    result.put("BZD", "બેલિઝ ડોલર");
    result.put("CAD", "કેનેડિયન ડૉલર");
    result.put("CDF", "કોંગોલિઝ ફ્રેંક");
    result.put("CHF", "સ્વિસ ફ્રેંક");
    result.put("CLP", "ચિલિઅન પેસો");
    result.put("CNY", "ચાઇનિઝ યુઆન");
    result.put("COP", "કોલમ્બિયન પેસો");
    result.put("CRC", "કોસ્ટા રિકન કોલોન");
    result.put("CUC", "ક્યુબન રૂપાંતર યોગ્ય પેસો");
    result.put("CUP", "ક્યુબન પેસો");
    result.put("CVE", "કેપ વર્દિયન એસ્કુડો");
    result.put("CZK", "ચેક રીપબ્લિક કોરુના");
    result.put("DJF", "જિબુટિયન ફ્રેંક");
    result.put("DKK", "ડેનિશ ક્રોન");
    result.put("DOP", "ડોમિનિકન પેસો");
    result.put("DZD", "અલ્જિરિયન દિનાર");
    result.put("EGP", "ઇજિપ્તિયન પાઉન્ડ");
    result.put("ERN", "એરિટ્રેયન નક્ફા");
    result.put("ETB", "એથિયોપિયન બિર");
    result.put("EUR", "યુરો");
    result.put("FJD", "ફિજિઅન ડોલર");
    result.put("FKP", "ફૉકલેન્ડ આઇલેંડ્સ પાઉન્ડ");
    result.put("GBP", "બ્રિટિશ પાઉન્ડ સ્ટરલિંગ");
    result.put("GEL", "જ્યોર્જિઅન લારી");
    result.put("GHS", "ઘાનાઇયન સેડી");
    result.put("GIP", "જીબ્રાલ્ટર પાઉન્ડ");
    result.put("GMD", "ગેમ્બિયન દલાસી");
    result.put("GNF", "ગિનીયન ફ્રેંક");
    result.put("GTQ", "ગ્વાટેમાલા કુઇટ્સલ");
    result.put("GYD", "ગયાનિઝ ડોલર");
    result.put("HKD", "હોંગ કોંગ ડૉલર");
    result.put("HNL", "હોન્ડ્યુરન લેમ્પિરા");
    result.put("HRK", "ક્રોએશિયન ક્યુના");
    result.put("HTG", "હાઇટિઇન ગોર્ડ");
    result.put("HUF", "હંગેરીયન ફોરિન્ત");
    result.put("IDR", "ઇન્ડોનેશિય રૂપીયા");
    result.put("ILS", "ઇઝરેઇલ ન્યુ શેકેલ");
    result.put("INR", "ભારતીય રૂપીયા");
    result.put("IQD", "ઇરાકી દિનાર");
    result.put("IRR", "ઇરાનિયન રિયાલ");
    result.put("ISK", "આઇસ્લેંડિક ક્રોના");
    result.put("JMD", "જમૈકિયન ડોલર");
    result.put("JOD", "જોરડનનું દિનાર");
    result.put("JPY", "જાપાનીઝ યેન");
    result.put("KES", "કેન્યેન શિલિંગ");
    result.put("KGS", "કિર્ગિસ્તાની સોમ");
    result.put("KHR", "કેમ્બોડિયન રીઅલ");
    result.put("KMF", "કોમોરિઅન ફ્રેંક");
    result.put("KPW", "ઉત્તર કોરિયન વન");
    result.put("KRW", "દક્ષિણ કોરિયન વન");
    result.put("KWD", "કુવૈતી દીનાર");
    result.put("KYD", "કેયમેન આઇલેંડ્સ ડોલર");
    result.put("KZT", "કઝાકિસ્તાની ટેંગ");
    result.put("LAK", "લાઓશિયન કિપ");
    result.put("LBP", "લેબેનિઝ પાઉન્ડ");
    result.put("LKR", "શ્રી લંકન રૂપી");
    result.put("LRD", "લિબેરિયન ડોલર");
    result.put("LSL", "લેસોથો લોતી");
    result.put("LTL", "લિથુએનિયન લિતાસ");
    result.put("LVL", "લાતવિયન લેત્સ");
    result.put("LYD", "લિબ્યન દિનાર");
    result.put("MAD", "મોરોકન દિરામ");
    result.put("MDL", "મોલડોવેન લિયુ");
    result.put("MGA", "માલાગેસી અરીઆરી");
    result.put("MKD", "મેસેડોનિયન દિનાર");
    result.put("MMK", "મયાન્મા ક્યાત");
    result.put("MNT", "મોંગોલિયન ટગરિક");
    result.put("MOP", "માકાનિઝ પતાકા");
    result.put("MRO", "મોરીશેનિયન ઓગુયા");
    result.put("MUR", "મૌરીશીઅન રૂપી");
    result.put("MVR", "માલ્દિવિયન રુફિયા");
    result.put("MWK", "માલાવિયન ક્વાચા");
    result.put("MXN", "મેક્સિકન પેસો");
    result.put("MYR", "મલેશિયન રિંગ્ગેટ");
    result.put("NAD", "નામિબિયા ડોલર");
    result.put("NGN", "નાઇજીરિયન નૈરા");
    result.put("NIO", "નિકારાગુઅન કોર્ડોબા");
    result.put("NOK", "નૉર્વેજિયન ક્રોન");
    result.put("NPR", "નેપાલિઝ રૂપી");
    result.put("NZD", "ન્યૂઝિલેંડ ડૉલર");
    result.put("OMR", "ઓમાની રિયાલ");
    result.put("PAB", "પનામેનિયન બાલ્બોઆ");
    result.put("PEN", "પેરુવિયન ન્યુવો સોલ");
    result.put("PGK", "પાપુઆ ન્યૂ ગિનીયન કિના");
    result.put("PHP", "ફિલીપાઇન પેસો");
    result.put("PKR", "પાકિસ્તાની રૂપી");
    result.put("PLN", "પોલિસ ઝ્લોટી");
    result.put("PYG", "પરાગ્વેયન ગૌરાની");
    result.put("QAR", "કતારી રિયાલ");
    result.put("RSD", "સર્બિયન દિનાર");
    result.put("RUB", "રશિયન રબલ");
    result.put("RWF", "રવાંડા ફ્રેંક");
    result.put("SAR", "સાઉદી રિયાલ");
    result.put("SBD", "સોલોમન આઇલેંડ્સ ડોલર");
    result.put("SCR", "સેશેલોઈ રૂપી");
    result.put("SDG", "સુદાનિઝ પાઉન્ડ");
    result.put("SEK", "સ્વીડિશ ક્રોના");
    result.put("SGD", "સિંગાપુર ડૉલર");
    result.put("SHP", "સેંટ હેલેના પાઉન્ડ");
    result.put("SLL", "સિએરા લિઓનિઅન લિઓન");
    result.put("SOS", "સોમાલી શિલિંગ");
    result.put("SRD", "સૂરીનામિઝ ડોલર");
    result.put("STD", "સાઓ ટૉમ એન્ડ પ્રિંસાઇપ ડોબ્રા");
    result.put("SYP", "સાઇરિયન પાઉન્ડ");
    result.put("SZL", "સ્વાઝી લિલાન્ગેની");
    result.put("THB", "થાઇ બાહ્ત");
    result.put("TJS", "તાજિકિસ્તાન સોમોની");
    result.put("TMT", "તુર્કમેનિસ્તાન મનત");
    result.put("TND", "ટ્યુનિશિયા દિનાર");
    result.put("TOP", "ટોંગન પ'અંગા");
    result.put("TRY", "તુર્કિશ લિરા");
    result.put("TTD", "ત્રિનિદાદ અને ટોબેગો");
    result.put("TWD", "ન્યુ તાઇવાન ડૉલર");
    result.put("TZS", "તાન્ઝાનિયા શિલિંગ");
    result.put("UAH", "યુક્રેનિયન હ્રિવિનિયા");
    result.put("UGX", "યુગાંડન શિલિંગ");
    result.put("USD", "યિએસ ડોલર");
    result.put("UYU", "ઉરુગ્વેયન પેસો");
    result.put("UZS", "ઉઝ્બેકિસ્તાન સોમ");
    result.put("VEF", "વેનેઝ્વીલિયન બોલિવર");
    result.put("VND", "વિયેતનામીસ ડોંગ");
    result.put("VUV", "વનૌતુ વાતુ");
    result.put("WST", "સમોઅન તાલા");
    result.put("XAF", "CFA ફ્રેંકBEAC");
    result.put("XCD", "ઇસ્ટ કેરિબિયન ડોલર");
    result.put("XOF", "CFA ફ્રેંક BCEAO");
    result.put("XPF", "CFP ફ્રેંક");
    result.put("XXX", "અજ્ઞાત ચલણ");
    result.put("YER", "યેમેની રિઆલ");
    result.put("ZAR", "દક્ષિણ આફ્રિકી રેંડ");
    result.put("ZMK", "ઝામ્બિયન ક્વાચા");
    return result;
  }
}
