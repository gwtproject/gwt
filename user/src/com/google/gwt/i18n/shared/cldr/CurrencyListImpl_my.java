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
 * Pure Java implementation of CurrencyList for locale "my".
 */
public class CurrencyListImpl_my extends CurrencyListImpl {

  @Override
  public CurrencyData getDefault() {
    return new CurrencyDataImpl("MMK", "K", 0, "K", "K");
  }

  @Override
  protected Map<String, CurrencyData> loadCurrencies() {
    Map<String, CurrencyData> result = super.loadCurrencies();
    result.put("ANG", new CurrencyDataImpl("ANG", "ANG", 2, "ANG", "ANG"));
    result.put("ARP", new CurrencyDataImpl("ARP", "ARP", 130, "ARP", "ARP"));
    result.put("ARS", new CurrencyDataImpl("ARS", "AR$", 2, "AR$", "$"));
    result.put("AUD", new CurrencyDataImpl("AUD", "AU$", 2, "AU$", "$"));
    result.put("AWG", new CurrencyDataImpl("AWG", "AWG", 2, "AWG", "Afl."));
    result.put("BDT", new CurrencyDataImpl("BDT", "Tk", 2, "Tk", "৳"));
    result.put("BEF", new CurrencyDataImpl("BEF", "BEF", 130, "BEF", "BEF"));
    result.put("BIF", new CurrencyDataImpl("BIF", "BIF", 0, "BIF", "FBu"));
    result.put("BMD", new CurrencyDataImpl("BMD", "BMD", 2, "BMD", "$"));
    result.put("BND", new CurrencyDataImpl("BND", "BND", 2, "BND", "$"));
    result.put("BOP", new CurrencyDataImpl("BOP", "BOP", 130, "BOP", "BOP"));
    result.put("BRL", new CurrencyDataImpl("BRL", "R$", 2, "R$", "R$"));
    result.put("BSD", new CurrencyDataImpl("BSD", "BSD", 2, "BSD", "$"));
    result.put("BUK", new CurrencyDataImpl("BUK", "BUK", 130, "BUK", "BUK"));
    result.put("BWP", new CurrencyDataImpl("BWP", "BWP", 2, "BWP", "P"));
    result.put("BYB", new CurrencyDataImpl("BYB", "BYB", 130, "BYB", "BYB"));
    result.put("BYR", new CurrencyDataImpl("BYR", "BYR", 0, "BYR", "BYR"));
    result.put("BZD", new CurrencyDataImpl("BZD", "BZD", 2, "BZD", "$"));
    result.put("CAD", new CurrencyDataImpl("CAD", "CA$", 2, "C$", "$"));
    result.put("CHF", new CurrencyDataImpl("CHF", "CHF", 2, "CHF", "CHF"));
    result.put("CLP", new CurrencyDataImpl("CLP", "CL$", 0, "CL$", "$"));
    result.put("CNY", new CurrencyDataImpl("CNY", "CN¥", 2, "RMB¥", "¥"));
    result.put("COP", new CurrencyDataImpl("COP", "COL$", 0, "COL$", "$"));
    result.put("CUP", new CurrencyDataImpl("CUP", "$MN", 2, "$MN", "$"));
    result.put("CYP", new CurrencyDataImpl("CYP", "CYP", 130, "CYP", "CYP"));
    result.put("DEM", new CurrencyDataImpl("DEM", "DEM", 130, "DEM", "DEM"));
    result.put("DKK", new CurrencyDataImpl("DKK", "kr", 2, "kr", "kr"));
    result.put("DOP", new CurrencyDataImpl("DOP", "RD$", 2, "RD$", "$"));
    result.put("EGP", new CurrencyDataImpl("EGP", "LE", 2, "LE", "E£"));
    result.put("ESP", new CurrencyDataImpl("ESP", "ESP", 128, "ESP", "ESP"));
    result.put("EUR", new CurrencyDataImpl("EUR", "€", 2, "€", "€"));
    result.put("FJD", new CurrencyDataImpl("FJD", "FJD", 2, "FJD", "$"));
    result.put("FKP", new CurrencyDataImpl("FKP", "FKP", 2, "FKP", "£"));
    result.put("FRF", new CurrencyDataImpl("FRF", "FRF", 130, "FRF", "FRF"));
    result.put("GBP", new CurrencyDataImpl("GBP", "UK£", 2, "GB£", "£"));
    result.put("GIP", new CurrencyDataImpl("GIP", "GIP", 2, "GIP", "£"));
    result.put("GNF", new CurrencyDataImpl("GNF", "GNF", 0, "GNF", "FG"));
    result.put("HKD", new CurrencyDataImpl("HKD", "HK$", 2, "HK$", "$"));
    result.put("IDR", new CurrencyDataImpl("IDR", "IDR", 0, "IDR", "Rp"));
    result.put("ILP", new CurrencyDataImpl("ILP", "ILP", 130, "ILP", "ILP"));
    result.put("INR", new CurrencyDataImpl("INR", "Rs.", 2, "Rs", "₹"));
    result.put("JMD", new CurrencyDataImpl("JMD", "JA$", 2, "JA$", "$"));
    result.put("JPY", new CurrencyDataImpl("JPY", "JP¥", 0, "JP¥", "¥"));
    result.put("KHR", new CurrencyDataImpl("KHR", "KHR", 2, "KHR", "Riel"));
    result.put("KPW", new CurrencyDataImpl("KPW", "KPW", 0, "KPW", "₩"));
    result.put("KRW", new CurrencyDataImpl("KRW", "₩", 0, "KR₩", "₩"));
    result.put("KYD", new CurrencyDataImpl("KYD", "KYD", 2, "KYD", "$"));
    result.put("LBP", new CurrencyDataImpl("LBP", "LBP", 0, "LBP", "L£"));
    result.put("LKR", new CurrencyDataImpl("LKR", "SLRs", 2, "SLRs", "Rs"));
    result.put("LRD", new CurrencyDataImpl("LRD", "LRD", 2, "LRD", "$"));
    result.put("MMK", new CurrencyDataImpl("MMK", "K", 0, "K", "K"));
    result.put("MXN", new CurrencyDataImpl("MXN", "MX$", 2, "Mex$", "$"));
    result.put("MYR", new CurrencyDataImpl("MYR", "RM", 2, "RM", "RM"));
    result.put("NAD", new CurrencyDataImpl("NAD", "NAD", 2, "NAD", "$"));
    result.put("NOK", new CurrencyDataImpl("NOK", "NOkr", 2, "NOkr", "kr"));
    result.put("NPR", new CurrencyDataImpl("NPR", "NPR", 2, "NPR", "Rs"));
    result.put("NZD", new CurrencyDataImpl("NZD", "NZ$", 2, "NZ$", "$"));
    result.put("PHP", new CurrencyDataImpl("PHP", "PHP", 2, "PHP", "₱"));
    result.put("PKR", new CurrencyDataImpl("PKR", "PKRs.", 0, "PKRs.", "Rs"));
    result.put("PLN", new CurrencyDataImpl("PLN", "PLN", 2, "PLN", "zł"));
    result.put("RUB", new CurrencyDataImpl("RUB", "руб.", 2, "руб.", "руб."));
    result.put("RUR", new CurrencyDataImpl("RUR", "RUR", 130, "RUR", "RUR"));
    result.put("RWF", new CurrencyDataImpl("RWF", "RWF", 0, "RWF", "RF"));
    result.put("SBD", new CurrencyDataImpl("SBD", "SBD", 2, "SBD", "$"));
    result.put("SDG", new CurrencyDataImpl("SDG", "SDG", 2, "SDG", "SDG"));
    result.put("SDP", new CurrencyDataImpl("SDP", "SDP", 130, "SDP", "SDP"));
    result.put("SEK", new CurrencyDataImpl("SEK", "kr", 2, "kr", "kr"));
    result.put("SGD", new CurrencyDataImpl("SGD", "S$", 2, "S$", "$"));
    result.put("SRD", new CurrencyDataImpl("SRD", "SRD", 2, "SRD", "$"));
    result.put("SUR", new CurrencyDataImpl("SUR", "SUR", 130, "SUR", "SUR"));
    result.put("THB", new CurrencyDataImpl("THB", "฿", 2, "THB", "฿"));
    result.put("TRL", new CurrencyDataImpl("TRL", "TRL", 128, "TRL", "TRL"));
    result.put("TRY", new CurrencyDataImpl("TRY", "YTL", 2, "YTL", "YTL"));
    result.put("TWD", new CurrencyDataImpl("TWD", "NT$", 2, "NT$", "NT$"));
    result.put("USD", new CurrencyDataImpl("USD", "US$", 2, "US$", "$"));
    result.put("USN", new CurrencyDataImpl("USN", "USN", 130, "USN", "USN"));
    result.put("USS", new CurrencyDataImpl("USS", "USS", 130, "USS", "USS"));
    result.put("VND", new CurrencyDataImpl("VND", "₫", 24, "₫", "₫"));
    result.put("XAG", new CurrencyDataImpl("XAG", "XAG", 130, "XAG", "XAG"));
    result.put("XAU", new CurrencyDataImpl("XAU", "XAU", 130, "XAU", "XAU"));
    result.put("XBB", new CurrencyDataImpl("XBB", "XBB", 130, "XBB", "XBB"));
    result.put("XDR", new CurrencyDataImpl("XDR", "XDR", 130, "XDR", "XDR"));
    result.put("XOF", new CurrencyDataImpl("XOF", "CFA", 0, "CFA", "CFA"));
    result.put("XPT", new CurrencyDataImpl("XPT", "XPT", 130, "XPT", "XPT"));
    result.put("XTS", new CurrencyDataImpl("XTS", "XTS", 130, "XTS", "XTS"));
    result.put("XXX", new CurrencyDataImpl("XXX", "XXX", 130, "XXX", "XXX"));
    result.put("ZWD", new CurrencyDataImpl("ZWD", "ZWD", 128, "ZWD", "ZWD"));
    return result;
  }

  @Override
  protected Map<String, String> loadCurrencyNames() {
    Map<String, String> result = super.loadCurrencyNames();
    result.put("ANG", "နယ်သာလန် အန်တီလန် ဂင်းဒါး");
    result.put("ARP", "အာဂျင်တီးနား ပီဆို (၁၉၈၃-၁၉၈၅)");
    result.put("ARS", "အာဂျင်တီးနား ပီဆို");
    result.put("AUD", "ဩစတြေးလျှား ဒေါ်လာ");
    result.put("AWG", "အရူဘန် ဂင်းဒါး");
    result.put("BDT", "ဘင်္ဂလားဒေ့ရှ် တာကာ");
    result.put("BEF", "ဘယ်လ်ဂျီယမ် ဖရန့်");
    result.put("BIF", "ဘူရွန်ဒီ ဖရန့်");
    result.put("BMD", "ဘာမူဒါ ဒေါ်လာ");
    result.put("BND", "ဘရူနိုင်း ဒေါ်လာ");
    result.put("BOP", "ဘိုလီးဘီးယား ပီဆို");
    result.put("BRL", "ဘရာဇီး ရီးယဲ");
    result.put("BSD", "ဘဟားမား ဒေါ်လာ");
    result.put("BUK", "ဗမာ ကျပ်");
    result.put("BWP", "ဘော့စ်ဝါနာ ပုလ");
    result.put("BYB", "ဘီလာရုစ် ရူဘယ်အသစ် (၁၉၉၄-၁၉၉၉)");
    result.put("BYR", "ဘီလာရုစ် ရူဘယ်");
    result.put("BZD", "ဘေလီဇ် ဒေါ်လာ");
    result.put("CAD", "ကနေဒါ ဒေါ်လာ");
    result.put("CHF", "ဆွစ် ဖရန့်");
    result.put("CLP", "ချီလီ ပီဆို");
    result.put("CNY", "တရုတ် ယွမ်");
    result.put("COP", "ကိုလံဘီယာ ပီဆို");
    result.put("CUP", "ကျူးဘား ပီဆို");
    result.put("CYP", "ဆိုက်ပရက်စ် ပေါင်");
    result.put("DEM", "ဂျာမဏီ မတ်");
    result.put("DKK", "ဒိန်းမတ် ခရိုဏာ");
    result.put("DOP", "ဒိုမီနီကန် ပီဆို");
    result.put("EGP", "အီဂျစ် ပေါင်");
    result.put("ESP", "စပိန် ပယ်စေးတာ");
    result.put("EUR", "ယူရို");
    result.put("FJD", "ဖီဂျီ ဒေါ်လာ");
    result.put("FKP", "ဖောက်ကလန် ကျွန်းစု ပေါင်");
    result.put("FRF", "ပြင်သစ် ဖရန့်");
    result.put("GBP", "ဗြိတိသျှ ပေါင်");
    result.put("GIP", "ဂျီဘရော်လ်တာ ပေါင်");
    result.put("GNF", "ဂီရာနာ ဖရန့်");
    result.put("HKD", "ဟောင်ကောင် ဒေါ်လာ");
    result.put("IDR", "အင်ဒိုနီးရှား ရူပီးယား");
    result.put("ILP", "အစ္စရေး ပေါင်");
    result.put("INR", "အိန္ဒြိယ ရူပီး");
    result.put("JMD", "ဂျမေနီကာ ဒေါ်လာ");
    result.put("JPY", "ဂျပန်ယန်း");
    result.put("KHR", "ကမ္ဘောဒီးယား ရီးယဲ");
    result.put("KPW", "မြောက်ကိုးရီးယား ဝမ်");
    result.put("KRW", "တောင်ကိုးရီးယား ဝမ်");
    result.put("KYD", "ကေမန် ကျွန်းစု ဒေါ်လာ");
    result.put("LBP", "လက်ဘနွန် ပေါင်");
    result.put("LKR", "သီရိလင်္ကာ ရူပီး");
    result.put("LRD", "လိုင်ဘေးရီးယား ဒေါ်လာ");
    result.put("MMK", "မြန်မာ ကျပ်");
    result.put("MXN", "မက္ကဆီကို ပီဆို");
    result.put("MYR", "မလေးရှား ရင်းဂစ်");
    result.put("NAD", "နမ်မီးဘီးယား ဒေါ်လာ");
    result.put("NOK", "နော်ဝေ ခရိုဏာ");
    result.put("NPR", "နီပေါ ရူပီး");
    result.put("NZD", "နယူးဇီလန် ဒေါ်လာ");
    result.put("PHP", "ဖိလစ်ပိုင် ပီဆို");
    result.put("PKR", "ပါကစ္စတန် ရူပီး");
    result.put("PLN", "ပိုလန် ဇ‌လော့တီ");
    result.put("RUB", "ရုရှ ရူဘယ်");
    result.put("RUR", "ရုရှ ရူဘယ် (၁၉၉၁-၁၉၉၈)");
    result.put("RWF", "ရဝန်ဒါ ဖရန့်");
    result.put("SBD", "ဆော်လမွန်ကျွန်းစု ဒေါ်လာ");
    result.put("SDG", "ဆူဒန် ပေါင်");
    result.put("SDP", "ဆူဒန် ပေါင်အဟောင်း");
    result.put("SEK", "ဆွီဒင် ခရိုဏာ");
    result.put("SGD", "စင်္ကာပူ ဒေါ်လာ");
    result.put("SRD", "ဆူရိနမ် ဒေါ်လာ");
    result.put("SUR", "ဆိုဗီယက် ရူဗယ်");
    result.put("THB", "ထိုင်းဘတ်");
    result.put("TRL", "ရှေးဟောင်းတူရကီ လိုင်ရာ");
    result.put("TRY", "တူရကီ လိုင်ရာ");
    result.put("TWD", "ထိုင်ဝမ် ဒေါ်လာအသစ်");
    result.put("USD", "အမေရိကန် ဒေါ်လာ");
    result.put("USN", "အမေရိကန် ဒေါ်လာ (နောက်နေ့)");
    result.put("USS", "အမေရိကန် ဒေါ်လာ (တနေ့တည်း)");
    result.put("VND", "ဗီယက်နမ် ဒေါင်");
    result.put("XAG", "ငွေ");
    result.put("XAU", "ရွှေ");
    result.put("XBB", "ဥရောပငွေကြေးစံနစ်");
    result.put("XDR", "အထူးထုတ်ယူခွင့်");
    result.put("XOF", "CFA ဖရန့် BCEAO");
    result.put("XPT", "ပလက်တီနမ်");
    result.put("XTS", "စမ်းသပ် ငွေကြေး ကုဒ်");
    result.put("XXX", "မသိ သို့မဟုတ် မရှိသော ငွေကြေး");
    result.put("ZWD", "ဇင်ဘာဘွေ ဒေါ်လာ");
    return result;
  }
}
