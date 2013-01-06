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

// DO NOT EDIT - GENERATED FROM CLDR AND ICU DATA:
//  cldrVersion=21.0
//  date=$Date: 2012-02-07 13:32:35 -0500 (Tue, 07 Feb 2012) $
//  number=$Revision: 6546 $
//  type=root

/**
 * JS implementation of CurrencyList for locale "fur".
 */
public class CurrencyListImpl_fur extends CurrencyListImpl {
  
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
      "ARS": [ "ARS", "AR$", 2, "AR$", "$"],
      "ATS": [ "ATS", "ATS", 130, "ATS", "ATS"],
      "AUD": [ "AUD", "AU$", 2, "AU$", "$"],
      "BEF": [ "BEF", "BEF", 130, "BEF", "BEF"],
      "BIF": [ "BIF", "BIF", 0, "BIF", "FBu"],
      "BND": [ "BND", "BND", 2, "BND", "$"],
      "BRL": [ "BRL", "R$", 2, "R$", "R$"],
      "BYR": [ "BYR", "BYR", 0, "BYR", "BYR"],
      "CAD": [ "CAD", "CA$", 2, "C$", "$"],
      "CHF": [ "CHF", "CHF", 2, "CHF", "CHF"],
      "CNY": [ "CNY", "CN¥", 2, "RMB¥", "¥"],
      "CSD": [ "CSD", "CSD", 130, "CSD", "CSD"],
      "CUP": [ "CUP", "$MN", 2, "$MN", "$"],
      "CZK": [ "CZK", "Kč", 2, "Kč", "Kč"],
      "DEM": [ "DEM", "DEM", 130, "DEM", "DEM"],
      "DKK": [ "DKK", "kr", 2, "kr", "kr"],
      "DZD": [ "DZD", "DZD", 2, "DZD", "din"],
      "EUR": [ "EUR", "€", 2, "€", "€"],
      "FRF": [ "FRF", "FRF", 130, "FRF", "FRF"],
      "GBP": [ "GBP", "UK£", 2, "GB£", "£"],
      "HKD": [ "HKD", "HK$", 2, "HK$", "$"],
      "HRD": [ "HRD", "HRD", 130, "HRD", "HRD"],
      "HRK": [ "HRK", "HRK", 2, "HRK", "kn"],
      "IDR": [ "IDR", "IDR", 0, "IDR", "Rp"],
      "INR": [ "INR", "Rs.", 2, "Rs", "₹"],
      "IRR": [ "IRR", "IRR", 0, "IRR", "Rial"],
      "ITL": [ "ITL", "ITL", 128, "ITL", "ITL"],
      "JPY": [ "JPY", "JP¥", 0, "JP¥", "¥"],
      "KRW": [ "KRW", "₩", 0, "KR₩", "₩"],
      "LVL": [ "LVL", "LVL", 2, "LVL", "Ls"],
      "MXN": [ "MXN", "MX$", 2, "Mex$", "$"],
      "NAD": [ "NAD", "NAD", 2, "NAD", "$"],
      "NIO": [ "NIO", "NIO", 2, "NIO", "C$"],
      "NOK": [ "NOK", "NOkr", 2, "NOkr", "kr"],
      "NZD": [ "NZD", "NZ$", 2, "NZ$", "$"],
      "PKR": [ "PKR", "PKRs.", 0, "PKRs.", "Rs"],
      "PLN": [ "PLN", "PLN", 2, "PLN", "zł"],
      "RSD": [ "RSD", "RSD", 0, "RSD", "din"],
      "RUB": [ "RUB", "руб.", 2, "руб.", "руб."],
      "SAR": [ "SAR", "SR", 2, "SR", "Rial"],
      "SEK": [ "SEK", "kr", 2, "kr", "kr"],
      "SIT": [ "SIT", "SIT", 130, "SIT", "SIT"],
      "SKK": [ "SKK", "SKK", 130, "SKK", "SKK"],
      "THB": [ "THB", "฿", 2, "THB", "฿"],
      "TRL": [ "TRL", "TRL", 128, "TRL", "TRL"],
      "TRY": [ "TRY", "YTL", 2, "YTL", "YTL"],
      "TWD": [ "TWD", "NT$", 2, "NT$", "NT$"],
      "USD": [ "USD", "US$", 2, "US$", "$"],
      "USN": [ "USN", "USN", 130, "USN", "USN"],
      "USS": [ "USS", "USS", 130, "USS", "USS"],
      "XAG": [ "XAG", "XAG", 130, "XAG", "XAG"],
      "XAU": [ "XAU", "XAU", 130, "XAU", "XAU"],
      "XBA": [ "XBA", "XBA", 130, "XBA", "XBA"],
      "XBB": [ "XBB", "XBB", 130, "XBB", "XBB"],
      "XBC": [ "XBC", "XBC", 130, "XBC", "XBC"],
      "XBD": [ "XBD", "XBD", 130, "XBD", "XBD"],
      "XDR": [ "XDR", "XDR", 130, "XDR", "XDR"],
      "XFO": [ "XFO", "XFO", 130, "XFO", "XFO"],
      "XFU": [ "XFU", "XFU", 130, "XFU", "XFU"],
      "XPD": [ "XPD", "XPD", 130, "XPD", "XPD"],
      "XPT": [ "XPT", "XPT", 130, "XPT", "XPT"],
      "XRE": [ "XRE", "XRE", 130, "XRE", "XRE"],
      "XTS": [ "XTS", "XTS", 130, "XTS", "XTS"],
      "XXX": [ "XXX", "XXX", 130, "XXX", "XXX"],
      "ZAR": [ "ZAR", "ZAR", 2, "ZAR", "R"],
    };
  }-*/;
  
  private native JavaScriptObject loadCurrencyNamesOverride() /*-{
    return {
      "ARS": "Peso argjentin",
      "ATS": "Selin austriac",
      "AUD": "dolar australian",
      "BEF": "Franc de Belgjiche",
      "BIF": "Franc burundês",
      "BND": "Dolar dal Brunei",
      "BRL": "real brasilian",
      "BYR": "Rubli bielorùs",
      "CAD": "dolar canadês",
      "CHF": "franc svuizar",
      "CNY": "yuan cinês",
      "CSD": "Vieri dinar serp",
      "CUP": "Peso cuban",
      "CZK": "Corone de Republiche Ceche",
      "DEM": "Marc todesc",
      "DKK": "corone danese",
      "DZD": "Dinar algerin",
      "EUR": "euro",
      "FRF": "Franc francês",
      "GBP": "sterline britaniche",
      "HKD": "dolar di Hong Kong",
      "HRD": "Dinar cravuat",
      "HRK": "Kuna cravuate",
      "IDR": "rupiah indonesiane",
      "INR": "rupie indiane",
      "IRR": "Rial iranian",
      "ITL": "Lire taliane",
      "JPY": "yen gjaponês",
      "KRW": "won de Coree dal Sud",
      "LVL": "Lats leton",
      "MXN": "peso messican",
      "NAD": "Dolar namibian",
      "NIO": "Córdoba oro nicaraguan",
      "NOK": "corone norvegjese",
      "NZD": "Dollar neozelandês",
      "PKR": "Rupie pachistane",
      "PLN": "zloty polac",
      "RSD": "Dinar serp",
      "RUB": "rubli rus",
      "SAR": "riyal de Arabie Saudite",
      "SEK": "corone svedese",
      "SIT": "Talar sloven",
      "SKK": "Corone slovache",
      "THB": "baht tailandês",
      "TRL": "Viere Lire turche",
      "TRY": "lire turche",
      "TWD": "gnûf dolar taiwanês",
      "USD": "dolar american",
      "USN": "Dolar american (prossime zornade)",
      "USS": "Dolar american (stesse zornade)",
      "XAG": "Arint",
      "XAU": "Aur",
      "XBA": "Unitât composite europeane",
      "XBB": "Unitât monetarie europeane",
      "XBC": "Unitât di acont europeane (XBC)",
      "XBD": "Unitât di acont europeane (XBD)",
      "XDR": "Dirits speciâi di incas",
      "XFO": "Franc aur francês",
      "XFU": "Franc UIC francês",
      "XPD": "Paladi",
      "XPT": "Platin",
      "XRE": "fonts RINET",
      "XTS": "codiç di verifiche de monede",
      "XXX": "Monede no valide o no cognossude",
      "ZAR": "rand sudafrican",
    };
  }-*/;
}
