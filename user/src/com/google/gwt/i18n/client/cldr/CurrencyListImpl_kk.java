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
 * JS implementation of CurrencyList for locale "kk".
 */
public class CurrencyListImpl_kk extends CurrencyListImpl {
  
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
      "AED": [ "AED", "DH", 2, "DH", "dh"],
      "AFN": [ "AFN", "AFN", 0, "AFN", "Af."],
      "ALL": [ "ALL", "ALL", 0, "ALL", "Lek"],
      "AMD": [ "AMD", "AMD", 0, "AMD", "Dram"],
      "ARS": [ "ARS", "AR$", 2, "AR$", "$"],
      "AUD": [ "AUD", "AU$", 2, "AU$", "$"],
      "AZM": [ "AZM", "AZM", 130, "AZM", "AZM"],
      "AZN": [ "AZN", "AZN", 2, "AZN", "man."],
      "BBD": [ "BBD", "BBD", 2, "BBD", "$"],
      "BDT": [ "BDT", "Tk", 2, "Tk", "৳"],
      "BGN": [ "BGN", "BGN", 2, "BGN", "lev"],
      "BHD": [ "BHD", "BHD", 3, "BHD", "din"],
      "BIF": [ "BIF", "BIF", 0, "BIF", "FBu"],
      "BND": [ "BND", "BND", 2, "BND", "$"],
      "BRL": [ "BRL", "R$", 2, "R$", "R$"],
      "BYR": [ "BYR", "BYR", 0, "BYR", "BYR"],
      "CAD": [ "CAD", "CA$", 2, "C$", "$"],
      "CHF": [ "CHF", "CHF", 2, "CHF", "CHF"],
      "CNY": [ "CNY", "CN¥", 2, "RMB¥", "¥"],
      "CZK": [ "CZK", "Kč", 2, "Kč", "Kč"],
      "DKK": [ "DKK", "kr", 2, "kr", "kr"],
      "EUR": [ "EUR", "€", 2, "€", "€"],
      "GBP": [ "GBP", "UK£", 2, "GB£", "£"],
      "HKD": [ "HKD", "HK$", 2, "HK$", "$"],
      "IDR": [ "IDR", "IDR", 0, "IDR", "Rp"],
      "INR": [ "INR", "Rs.", 2, "Rs", "₹"],
      "JMD": [ "JMD", "JA$", 2, "JA$", "$"],
      "JOD": [ "JOD", "JOD", 3, "JOD", "din"],
      "JPY": [ "JPY", "JP¥", 0, "JP¥", "¥"],
      "KGS": [ "KGS", "KGS", 2, "KGS", "KGS"],
      "KRW": [ "KRW", "₩", 0, "KR₩", "₩"],
      "KWD": [ "KWD", "KWD", 3, "KWD", "din"],
      "KZT": [ "KZT", "₸", 2, "₸", "₸"],
      "MXN": [ "MXN", "MX$", 2, "Mex$", "$"],
      "NAD": [ "NAD", "NAD", 2, "NAD", "$"],
      "NOK": [ "NOK", "NOkr", 2, "NOkr", "kr"],
      "NPR": [ "NPR", "NPR", 2, "NPR", "Rs"],
      "NZD": [ "NZD", "NZ$", 2, "NZ$", "$"],
      "OMR": [ "OMR", "OMR", 3, "OMR", "Rial"],
      "PLN": [ "PLN", "PLN", 2, "PLN", "zł"],
      "RUB": [ "RUB", "руб.", 2, "руб.", "руб."],
      "SAR": [ "SAR", "SR", 2, "SR", "Rial"],
      "SEK": [ "SEK", "kr", 2, "kr", "kr"],
      "THB": [ "THB", "฿", 2, "THB", "฿"],
      "TRY": [ "TRY", "YTL", 2, "YTL", "YTL"],
      "TWD": [ "TWD", "NT$", 2, "NT$", "NT$"],
      "USD": [ "USD", "US$", 2, "US$", "$"],
      "XXX": [ "XXX", "XXX", 130, "XXX", "XXX"],
      "ZAR": [ "ZAR", "ZAR", 2, "ZAR", "R"],
    };
  }-*/;
  
  private native JavaScriptObject loadCurrencyNamesOverride() /*-{
    return {
      "AED": "Дихрам (БАЭ)",
      "AFN": "Афгани",
      "ALL": "Албания лекі",
      "AMD": "Армения драмы",
      "ARS": "Аргентина пессосы",
      "AUD": "Австралия доллары",
      "AZM": "Азербайджан манаты (1993-2006)",
      "AZN": "Азербайджан манаты",
      "BBD": "Барбадиан доллары",
      "BDT": "Бангладеш Такасы",
      "BGN": "Болгар леві",
      "BHD": "Бахрейн динары",
      "BIF": "Бурундиан франкі",
      "BND": "Бруней доллары",
      "BRL": "Бразилия реалы",
      "BYR": "Беларус рублі",
      "CAD": "Канада доллары",
      "CHF": "Швейцар франкі",
      "CNY": "Қытай юаны",
      "CZK": "Чех кронасы",
      "DKK": "Дат кроны",
      "EUR": "Еуро",
      "GBP": "Британия фунты",
      "HKD": "Гонконг доллары",
      "IDR": "Индонезия рупиі",
      "INR": "Үнді рупия",
      "JMD": "Ямайка доллары",
      "JOD": "Йорданиан динары",
      "JPY": "Жапон иені",
      "KGS": "Қырғызстан сомы",
      "KRW": "Оңтүстік Корея воны",
      "KWD": "Кувейт динары",
      "KZT": "Қазақстан теңгесі",
      "MXN": "Мексика пессосы",
      "NAD": "Намибия доллары",
      "NOK": "Норвегия кроны",
      "NPR": "Непал Рупиі",
      "NZD": "Жаңа Зеландия доллары",
      "OMR": "Оман риалы",
      "PLN": "Польша злотасы",
      "RUB": "Ресей рубль",
      "SAR": "Сауд риалы",
      "SEK": "Швед кроны",
      "THB": "Тай баты",
      "TRY": "Түрік лирасы",
      "TWD": "Жаңа Тайван доллары",
      "USD": "АҚШ доллары",
      "XXX": "Белгісіз валюта",
      "ZAR": "Оңтүстік Африка рэнді",
    };
  }-*/;
}
