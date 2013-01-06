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
package com.google.gwt.i18n.client.cldr;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.i18n.shared.CurrencyData;

// DO NOT EDIT - GENERATED FROM CLDR AND ICU DATA:
//  cldrVersion=21.0
//  date=$Date: 2012-02-07 13:32:35 -0500 (Tue, 07 Feb 2012) $
//  number=$Revision: 6546 Google $
//  type=root

/**
 * JS implementation of CurrencyList for locale "my".
 */
public class CurrencyListImpl_my extends CurrencyListImpl {
  
  @Override
  public native CurrencyData getDefault() /*-{
    return [ "MMK", "K", 0, "K", "K"];
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
      "ANG": [ "ANG", "ANG", 2, "ANG", "ANG"],
      "ARP": [ "ARP", "ARP", 130, "ARP", "ARP"],
      "ARS": [ "ARS", "AR$", 2, "AR$", "$"],
      "AUD": [ "AUD", "AU$", 2, "AU$", "$"],
      "AWG": [ "AWG", "AWG", 2, "AWG", "Afl."],
      "BDT": [ "BDT", "Tk", 2, "Tk", "৳"],
      "BEF": [ "BEF", "BEF", 130, "BEF", "BEF"],
      "BIF": [ "BIF", "BIF", 0, "BIF", "FBu"],
      "BMD": [ "BMD", "BMD", 2, "BMD", "$"],
      "BND": [ "BND", "BND", 2, "BND", "$"],
      "BOP": [ "BOP", "BOP", 130, "BOP", "BOP"],
      "BRL": [ "BRL", "R$", 2, "R$", "R$"],
      "BSD": [ "BSD", "BSD", 2, "BSD", "$"],
      "BUK": [ "BUK", "BUK", 130, "BUK", "BUK"],
      "BWP": [ "BWP", "BWP", 2, "BWP", "P"],
      "BYB": [ "BYB", "BYB", 130, "BYB", "BYB"],
      "BYR": [ "BYR", "BYR", 0, "BYR", "BYR"],
      "BZD": [ "BZD", "BZD", 2, "BZD", "$"],
      "CAD": [ "CAD", "CA$", 2, "C$", "$"],
      "CHF": [ "CHF", "CHF", 2, "CHF", "CHF"],
      "CLP": [ "CLP", "CL$", 0, "CL$", "$"],
      "CNY": [ "CNY", "CN¥", 2, "RMB¥", "¥"],
      "COP": [ "COP", "COL$", 0, "COL$", "$"],
      "CUP": [ "CUP", "$MN", 2, "$MN", "$"],
      "CYP": [ "CYP", "CYP", 130, "CYP", "CYP"],
      "DEM": [ "DEM", "DEM", 130, "DEM", "DEM"],
      "DKK": [ "DKK", "kr", 2, "kr", "kr"],
      "DOP": [ "DOP", "RD$", 2, "RD$", "$"],
      "EGP": [ "EGP", "LE", 2, "LE", "E£"],
      "ESP": [ "ESP", "ESP", 128, "ESP", "ESP"],
      "EUR": [ "EUR", "€", 2, "€", "€"],
      "FJD": [ "FJD", "FJD", 2, "FJD", "$"],
      "FKP": [ "FKP", "FKP", 2, "FKP", "£"],
      "FRF": [ "FRF", "FRF", 130, "FRF", "FRF"],
      "GBP": [ "GBP", "UK£", 2, "GB£", "£"],
      "GIP": [ "GIP", "GIP", 2, "GIP", "£"],
      "GNF": [ "GNF", "GNF", 0, "GNF", "FG"],
      "HKD": [ "HKD", "HK$", 2, "HK$", "$"],
      "IDR": [ "IDR", "IDR", 0, "IDR", "Rp"],
      "ILP": [ "ILP", "ILP", 130, "ILP", "ILP"],
      "INR": [ "INR", "Rs.", 2, "Rs", "₹"],
      "JMD": [ "JMD", "JA$", 2, "JA$", "$"],
      "JPY": [ "JPY", "JP¥", 0, "JP¥", "¥"],
      "KHR": [ "KHR", "KHR", 2, "KHR", "Riel"],
      "KPW": [ "KPW", "KPW", 0, "KPW", "₩"],
      "KRW": [ "KRW", "₩", 0, "KR₩", "₩"],
      "KYD": [ "KYD", "KYD", 2, "KYD", "$"],
      "LBP": [ "LBP", "LBP", 0, "LBP", "L£"],
      "LKR": [ "LKR", "SLRs", 2, "SLRs", "Rs"],
      "LRD": [ "LRD", "LRD", 2, "LRD", "$"],
      "MMK": [ "MMK", "K", 0, "K", "K"],
      "MXN": [ "MXN", "MX$", 2, "Mex$", "$"],
      "MYR": [ "MYR", "RM", 2, "RM", "RM"],
      "NAD": [ "NAD", "NAD", 2, "NAD", "$"],
      "NOK": [ "NOK", "NOkr", 2, "NOkr", "kr"],
      "NPR": [ "NPR", "NPR", 2, "NPR", "Rs"],
      "NZD": [ "NZD", "NZ$", 2, "NZ$", "$"],
      "PHP": [ "PHP", "PHP", 2, "PHP", "₱"],
      "PKR": [ "PKR", "PKRs.", 0, "PKRs.", "Rs"],
      "PLN": [ "PLN", "PLN", 2, "PLN", "zł"],
      "RUB": [ "RUB", "руб.", 2, "руб.", "руб."],
      "RUR": [ "RUR", "RUR", 130, "RUR", "RUR"],
      "RWF": [ "RWF", "RWF", 0, "RWF", "RF"],
      "SBD": [ "SBD", "SBD", 2, "SBD", "$"],
      "SDG": [ "SDG", "SDG", 2, "SDG", "SDG"],
      "SDP": [ "SDP", "SDP", 130, "SDP", "SDP"],
      "SEK": [ "SEK", "kr", 2, "kr", "kr"],
      "SGD": [ "SGD", "S$", 2, "S$", "$"],
      "SRD": [ "SRD", "SRD", 2, "SRD", "$"],
      "SUR": [ "SUR", "SUR", 130, "SUR", "SUR"],
      "THB": [ "THB", "฿", 2, "THB", "฿"],
      "TRL": [ "TRL", "TRL", 128, "TRL", "TRL"],
      "TRY": [ "TRY", "YTL", 2, "YTL", "YTL"],
      "TWD": [ "TWD", "NT$", 2, "NT$", "NT$"],
      "USD": [ "USD", "US$", 2, "US$", "$"],
      "USN": [ "USN", "USN", 130, "USN", "USN"],
      "USS": [ "USS", "USS", 130, "USS", "USS"],
      "VND": [ "VND", "₫", 24, "₫", "₫"],
      "XAG": [ "XAG", "XAG", 130, "XAG", "XAG"],
      "XAU": [ "XAU", "XAU", 130, "XAU", "XAU"],
      "XBB": [ "XBB", "XBB", 130, "XBB", "XBB"],
      "XDR": [ "XDR", "XDR", 130, "XDR", "XDR"],
      "XOF": [ "XOF", "CFA", 0, "CFA", "CFA"],
      "XPT": [ "XPT", "XPT", 130, "XPT", "XPT"],
      "XTS": [ "XTS", "XTS", 130, "XTS", "XTS"],
      "XXX": [ "XXX", "XXX", 130, "XXX", "XXX"],
      "ZWD": [ "ZWD", "ZWD", 128, "ZWD", "ZWD"],
    };
  }-*/;
  
  private native JavaScriptObject loadCurrencyNamesOverride() /*-{
    return {
      "ANG": "နယ်သာလန် အန်တီလန် ဂင်းဒါး",
      "ARP": "အာဂျင်တီးနား ပီဆို (၁၉၈၃-၁၉၈၅)",
      "ARS": "အာဂျင်တီးနား ပီဆို",
      "AUD": "ဩစတြေးလျှား ဒေါ်လာ",
      "AWG": "အရူဘန် ဂင်းဒါး",
      "BDT": "ဘင်္ဂလားဒေ့ရှ် တာကာ",
      "BEF": "ဘယ်လ်ဂျီယမ် ဖရန့်",
      "BIF": "ဘူရွန်ဒီ ဖရန့်",
      "BMD": "ဘာမူဒါ ဒေါ်လာ",
      "BND": "ဘရူနိုင်း ဒေါ်လာ",
      "BOP": "ဘိုလီးဘီးယား ပီဆို",
      "BRL": "ဘရာဇီး ရီးယဲ",
      "BSD": "ဘဟားမား ဒေါ်လာ",
      "BUK": "ဗမာ ကျပ်",
      "BWP": "ဘော့စ်ဝါနာ ပုလ",
      "BYB": "ဘီလာရုစ် ရူဘယ်အသစ် (၁၉၉၄-၁၉၉၉)",
      "BYR": "ဘီလာရုစ် ရူဘယ်",
      "BZD": "ဘေလီဇ် ဒေါ်လာ",
      "CAD": "ကနေဒါ ဒေါ်လာ",
      "CHF": "ဆွစ် ဖရန့်",
      "CLP": "ချီလီ ပီဆို",
      "CNY": "တရုတ် ယွမ်",
      "COP": "ကိုလံဘီယာ ပီဆို",
      "CUP": "ကျူးဘား ပီဆို",
      "CYP": "ဆိုက်ပရက်စ် ပေါင်",
      "DEM": "ဂျာမဏီ မတ်",
      "DKK": "ဒိန်းမတ် ခရိုဏာ",
      "DOP": "ဒိုမီနီကန် ပီဆို",
      "EGP": "အီဂျစ် ပေါင်",
      "ESP": "စပိန် ပယ်စေးတာ",
      "EUR": "ယူရို",
      "FJD": "ဖီဂျီ ဒေါ်လာ",
      "FKP": "ဖောက်ကလန် ကျွန်းစု ပေါင်",
      "FRF": "ပြင်သစ် ဖရန့်",
      "GBP": "ဗြိတိသျှ ပေါင်",
      "GIP": "ဂျီဘရော်လ်တာ ပေါင်",
      "GNF": "ဂီရာနာ ဖရန့်",
      "HKD": "ဟောင်ကောင် ဒေါ်လာ",
      "IDR": "အင်ဒိုနီးရှား ရူပီးယား",
      "ILP": "အစ္စရေး ပေါင်",
      "INR": "အိန္ဒြိယ ရူပီး",
      "JMD": "ဂျမေနီကာ ဒေါ်လာ",
      "JPY": "ဂျပန်ယန်း",
      "KHR": "ကမ္ဘောဒီးယား ရီးယဲ",
      "KPW": "မြောက်ကိုးရီးယား ဝမ်",
      "KRW": "တောင်ကိုးရီးယား ဝမ်",
      "KYD": "ကေမန် ကျွန်းစု ဒေါ်လာ",
      "LBP": "လက်ဘနွန် ပေါင်",
      "LKR": "သီရိလင်္ကာ ရူပီး",
      "LRD": "လိုင်ဘေးရီးယား ဒေါ်လာ",
      "MMK": "မြန်မာ ကျပ်",
      "MXN": "မက္ကဆီကို ပီဆို",
      "MYR": "မလေးရှား ရင်းဂစ်",
      "NAD": "နမ်မီးဘီးယား ဒေါ်လာ",
      "NOK": "နော်ဝေ ခရိုဏာ",
      "NPR": "နီပေါ ရူပီး",
      "NZD": "နယူးဇီလန် ဒေါ်လာ",
      "PHP": "ဖိလစ်ပိုင် ပီဆို",
      "PKR": "ပါကစ္စတန် ရူပီး",
      "PLN": "ပိုလန် ဇ‌လော့တီ",
      "RUB": "ရုရှ ရူဘယ်",
      "RUR": "ရုရှ ရူဘယ် (၁၉၉၁-၁၉၉၈)",
      "RWF": "ရဝန်ဒါ ဖရန့်",
      "SBD": "ဆော်လမွန်ကျွန်းစု ဒေါ်လာ",
      "SDG": "ဆူဒန် ပေါင်",
      "SDP": "ဆူဒန် ပေါင်အဟောင်း",
      "SEK": "ဆွီဒင် ခရိုဏာ",
      "SGD": "စင်္ကာပူ ဒေါ်လာ",
      "SRD": "ဆူရိနမ် ဒေါ်လာ",
      "SUR": "ဆိုဗီယက် ရူဗယ်",
      "THB": "ထိုင်းဘတ်",
      "TRL": "ရှေးဟောင်းတူရကီ လိုင်ရာ",
      "TRY": "တူရကီ လိုင်ရာ",
      "TWD": "ထိုင်ဝမ် ဒေါ်လာအသစ်",
      "USD": "အမေရိကန် ဒေါ်လာ",
      "USN": "အမေရိကန် ဒေါ်လာ (နောက်နေ့)",
      "USS": "အမေရိကန် ဒေါ်လာ (တနေ့တည်း)",
      "VND": "ဗီယက်နမ် ဒေါင်",
      "XAG": "ငွေ",
      "XAU": "ရွှေ",
      "XBB": "ဥရောပငွေကြေးစံနစ်",
      "XDR": "အထူးထုတ်ယူခွင့်",
      "XOF": "CFA ဖရန့် BCEAO",
      "XPT": "ပလက်တီနမ်",
      "XTS": "စမ်းသပ် ငွေကြေး ကုဒ်",
      "XXX": "မသိ သို့မဟုတ် မရှိသော ငွေကြေး",
      "ZWD": "ဇင်ဘာဘွေ ဒေါ်လာ",
    };
  }-*/;
}
