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
 * JS implementation of CurrencyList for locale "pt_PT".
 */
public class CurrencyListImpl_pt_PT extends CurrencyListImpl_pt {
  
  @Override
  public native CurrencyData getDefault() /*-{
    return [ "EUR", "€", 2, "€", "€"];
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
      "AED": [ "AED", "DH", 2, "DH", "dh"],
      "AFA": [ "AFA", "AFA", 130, "AFA", "AFA"],
      "AFN": [ "AFN", "AFN", 0, "AFN", "Af."],
      "ANG": [ "ANG", "ANG", 2, "ANG", "ANG"],
      "AWG": [ "AWG", "AWG", 2, "AWG", "Afl."],
      "BAD": [ "BAD", "BAD", 130, "BAD", "BAD"],
      "BAM": [ "BAM", "BAM", 2, "BAM", "KM"],
      "BEC": [ "BEC", "BEC", 130, "BEC", "BEC"],
      "BRL": [ "BRL", "BR$", 2, "R$", "R$"],
      "BYB": [ "BYB", "BYB", 130, "BYB", "BYB"],
      "CAD": [ "CAD", "CA$", 2, "C$", "$"],
      "CYP": [ "CYP", "CYP", 130, "CYP", "CYP"],
      "CZK": [ "CZK", "Kč", 2, "Kč", "Kč"],
      "ECV": [ "ECV", "ECV", 130, "ECV", "ECV"],
      "GHC": [ "GHC", "GHC", 130, "GHC", "GHC"],
      "GMD": [ "GMD", "GMD", 2, "GMD", "GMD"],
      "GNF": [ "GNF", "GNF", 0, "GNF", "FG"],
      "GTQ": [ "GTQ", "GTQ", 2, "GTQ", "Q"],
      "HNL": [ "HNL", "L", 2, "L", "L"],
      "KWD": [ "KWD", "KWD", 3, "KWD", "din"],
      "KYD": [ "KYD", "KYD", 2, "KYD", "$"],
      "MKD": [ "MKD", "MKD", 2, "MKD", "din"],
      "MLF": [ "MLF", "MLF", 130, "MLF", "MLF"],
      "MWK": [ "MWK", "MWK", 2, "MWK", "MWK"],
      "MXP": [ "MXP", "MXP", 130, "MXP", "MXP"],
      "MXV": [ "MXV", "MXV", 130, "MXV", "MXV"],
      "NIC": [ "NIC", "NIC", 130, "NIC", "NIC"],
      "NIO": [ "NIO", "NIO", 2, "NIO", "C$"],
      "PLN": [ "PLN", "PLN", 2, "PLN", "zł"],
      "PLZ": [ "PLZ", "PLZ", 130, "PLZ", "PLZ"],
      "QAR": [ "QAR", "QAR", 2, "QAR", "Rial"],
      "SGD": [ "SGD", "S$", 2, "S$", "$"],
      "THB": [ "THB", "฿", 2, "THB", "฿"],
      "TZS": [ "TZS", "TZS", 0, "TZS", "TSh"],
      "USD": [ "USD", "US$", 2, "US$", "$"],
      "WST": [ "WST", "WST", 2, "WST", "WST"],
      "XEU": [ "XEU", "XEU", 130, "XEU", "XEU"],
      "XXX": [ "XXX", "XXX", 130, "XXX", "XXX"],
      "YUD": [ "YUD", "YUD", 130, "YUD", "YUD"],
      "YUM": [ "YUM", "YUM", 130, "YUM", "YUM"],
      "YUN": [ "YUN", "YUN", 130, "YUN", "YUN"],
      "ZWD": [ "ZWD", "ZWD", 128, "ZWD", "ZWD"],
    };
  }-*/;
  
  private native JavaScriptObject loadCurrencyNamesOverride() /*-{
    return {
      "AED": "Dirham dos Emirados Árabes Unidos",
      "AFA": "Afeghani (1927-2002)",
      "AFN": "Afgani afegão",
      "ANG": "Florim das Antilhas Holandesas",
      "AWG": "Florim de Aruba",
      "BAD": "Dinar da Bósnia-Herzegóvina",
      "BAM": "Marco bósnio-herzegóvino conversível",
      "BEC": "Franco belga (convertível)",
      "BRL": "Real brasileiro",
      "BYB": "Rublo novo bielorusso (1994-1999)",
      "CAD": "Dólar canadiano",
      "CYP": "Libra de Chipre",
      "CZK": "Coroa da República Checa",
      "ECV": "Unidad de Valor Constante (UVC) do Equador",
      "GHC": "Cedi do Gana",
      "GMD": "Dalasi da Gâmbia",
      "GNF": "Franco Guineense",
      "GTQ": "Quetzal da Guatemala",
      "HNL": "Lempira das Honduras",
      "KWD": "Dinar do Koweit",
      "KYD": "Dólar das Ilhas Caimão",
      "MKD": "Dinar Macedónio",
      "MLF": "Franco do Mali",
      "MWK": "Cuacha do Malawi",
      "MXP": "Peso Plata mexicano (1861-1992)",
      "MXV": "Unidad de Inversion (UDI) mexicana",
      "NIC": "Córdoba nicaraguano",
      "NIO": "Córdoba Ouro nicaraguano",
      "PLN": "Zloti polaco",
      "PLZ": "Zloti polaco (1950-1995)",
      "QAR": "Rial do Catar",
      "SGD": "Dólar de Singapura",
      "THB": "Baht da Tailândia",
      "TZS": "Xelim de Tanzânia",
      "USD": "Dólar dos Estados Unidos",
      "WST": "Tala de Samoa Ocidental",
      "XEU": "Unidade da Moeda Europeia",
      "XXX": "Moeda Desconhecida",
      "YUD": "Dinar forte jugoslavo",
      "YUM": "Super Dinar jugoslavo",
      "YUN": "Dinar conversível jugoslavo",
      "ZWD": "Dólar do Zimbabwe",
    };
  }-*/;
}
