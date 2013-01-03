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
package com.google.gwt.i18n.client.cldr;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.i18n.shared.CurrencyData;

// DO NOT EDIT - GENERATED FROM CLDR AND ICU DATA
//  cldrVersion=21.0
//  number=$Revision: 6472 Google $
//  date=$Date: 2012-01-27 18:53:35 -0500 (Fri, 27 Jan 2012) $
//  type=root

/**
 * JS implementation of CurrencyList for locale "ne".
 */
public class CurrencyListImpl_ne extends CurrencyListImpl {

  @Override
  public native CurrencyData getDefault() /*-{
    return [ "NPR", "नेरू", 2, "नेरू", "Rs"];
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
      "AFA": [ "AFA", "AFA", 130, "AFA", "AFA"],
      "AFN": [ "AFN", "AFN", 0, "AFN", "Af."],
      "ALL": [ "ALL", "ALL", 0, "ALL", "Lek"],
      "AUD": [ "AUD", "AU$", 2, "AU$", "$"],
      "BRL": [ "BRL", "R$", 2, "R$", "R$"],
      "CNY": [ "CNY", "CN¥", 2, "RMB¥", "¥"],
      "EUR": [ "EUR", "€", 2, "€", "€"],
      "GBP": [ "GBP", "UK£", 2, "GB£", "£"],
      "INR": [ "INR", "Rs.", 2, "Rs", "₹"],
      "JPY": [ "JPY", "JP¥", 0, "JP¥", "¥"],
      "NOK": [ "NOK", "NOkr", 2, "NOkr", "kr"],
      "NPR": [ "NPR", "नेरू", 2, "नेरू", "Rs"],
      "PHP": [ "PHP", "PHP", 2, "PHP", "₱"],
      "RUB": [ "RUB", "руб.", 2, "руб.", "руб."],
      "USD": [ "USD", "US$", 2, "US$", "$"],
      "XXX": [ "XXX", "XXX", 130, "XXX", "XXX"],
    };
  }-*/;

  private native JavaScriptObject loadCurrencyNamesOverride() /*-{
    return {
      "AFA": "अफ्गानी(१९२७-२००२)",
      "AFN": "अफ्गानी",
      "ALL": "अल्बानियन लेक",
      "AUD": "अष्ट्रेलियन डलर",
      "BRL": "ब्राजिलियन रियल",
      "CNY": "चिनिँया युआन रेनिबी",
      "EUR": "युरो",
      "GBP": "बेलायती पाउण्ड स्टर्लिङ",
      "INR": "भारती रूपिँया",
      "JPY": "जापानी येन",
      "NOK": "नर्वेजियाली क्रोन",
      "NPR": "NPR",
      "PHP": "फिलिपिनी पेसो",
      "RUB": "रूसी रूबल",
      "USD": "संयुक्त राज्य डलर",
      "XXX": "अपरिचित वा अवैध मुद्रा",
    };
  }-*/;
}
