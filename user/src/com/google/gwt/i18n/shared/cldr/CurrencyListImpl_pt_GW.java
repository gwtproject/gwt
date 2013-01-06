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
//  date=$Date: 2011-05-02 02:05:34 -0400 (Mon, 02 May 2011) $
//  number=$Revision: 5798 $
//  type=root

/**
 *  * Pure Java implementation of CurrencyList for locale "pt_GW".
 */
public class CurrencyListImpl_pt_GW extends CurrencyListImpl_pt {
  
  @Override
  public CurrencyData getDefault() {
    return new CurrencyDataImpl("XOF", "CFA", 0, "CFA", "CFA");
  }
  
  @Override
  protected Map<String, CurrencyData> loadCurrencies() {
    Map<String, CurrencyData> result = super.loadCurrencies();
    result.put("AED", new CurrencyDataImpl("AED", "DH", 2, "DH", "dh"));
    result.put("AFA", new CurrencyDataImpl("AFA", "AFA", 130, "AFA", "AFA"));
    result.put("AFN", new CurrencyDataImpl("AFN", "AFN", 0, "AFN", "Af."));
    result.put("ANG", new CurrencyDataImpl("ANG", "ANG", 2, "ANG", "ANG"));
    result.put("AWG", new CurrencyDataImpl("AWG", "AWG", 2, "AWG", "Afl."));
    result.put("BAD", new CurrencyDataImpl("BAD", "BAD", 130, "BAD", "BAD"));
    result.put("BAM", new CurrencyDataImpl("BAM", "BAM", 2, "BAM", "KM"));
    result.put("BEC", new CurrencyDataImpl("BEC", "BEC", 130, "BEC", "BEC"));
    result.put("BRL", new CurrencyDataImpl("BRL", "BR$", 2, "R$", "R$"));
    result.put("BYB", new CurrencyDataImpl("BYB", "BYB", 130, "BYB", "BYB"));
    result.put("CAD", new CurrencyDataImpl("CAD", "CA$", 2, "C$", "$"));
    result.put("CYP", new CurrencyDataImpl("CYP", "CYP", 130, "CYP", "CYP"));
    result.put("CZK", new CurrencyDataImpl("CZK", "Kč", 2, "Kč", "Kč"));
    result.put("ECV", new CurrencyDataImpl("ECV", "ECV", 130, "ECV", "ECV"));
    result.put("GHC", new CurrencyDataImpl("GHC", "GHC", 130, "GHC", "GHC"));
    result.put("GMD", new CurrencyDataImpl("GMD", "GMD", 2, "GMD", "GMD"));
    result.put("GNF", new CurrencyDataImpl("GNF", "GNF", 0, "GNF", "FG"));
    result.put("GTQ", new CurrencyDataImpl("GTQ", "GTQ", 2, "GTQ", "Q"));
    result.put("HNL", new CurrencyDataImpl("HNL", "L", 2, "L", "L"));
    result.put("KWD", new CurrencyDataImpl("KWD", "KWD", 3, "KWD", "din"));
    result.put("KYD", new CurrencyDataImpl("KYD", "KYD", 2, "KYD", "$"));
    result.put("MKD", new CurrencyDataImpl("MKD", "MKD", 2, "MKD", "din"));
    result.put("MLF", new CurrencyDataImpl("MLF", "MLF", 130, "MLF", "MLF"));
    result.put("MWK", new CurrencyDataImpl("MWK", "MWK", 2, "MWK", "MWK"));
    result.put("MXP", new CurrencyDataImpl("MXP", "MXP", 130, "MXP", "MXP"));
    result.put("MXV", new CurrencyDataImpl("MXV", "MXV", 130, "MXV", "MXV"));
    result.put("NIC", new CurrencyDataImpl("NIC", "NIC", 130, "NIC", "NIC"));
    result.put("NIO", new CurrencyDataImpl("NIO", "NIO", 2, "NIO", "C$"));
    result.put("PLN", new CurrencyDataImpl("PLN", "PLN", 2, "PLN", "zł"));
    result.put("PLZ", new CurrencyDataImpl("PLZ", "PLZ", 130, "PLZ", "PLZ"));
    result.put("QAR", new CurrencyDataImpl("QAR", "QAR", 2, "QAR", "Rial"));
    result.put("SGD", new CurrencyDataImpl("SGD", "S$", 2, "S$", "$"));
    result.put("THB", new CurrencyDataImpl("THB", "฿", 2, "THB", "฿"));
    result.put("TZS", new CurrencyDataImpl("TZS", "TZS", 0, "TZS", "TSh"));
    result.put("USD", new CurrencyDataImpl("USD", "US$", 2, "US$", "$"));
    result.put("WST", new CurrencyDataImpl("WST", "WST", 2, "WST", "WST"));
    result.put("XEU", new CurrencyDataImpl("XEU", "XEU", 130, "XEU", "XEU"));
    result.put("XXX", new CurrencyDataImpl("XXX", "XXX", 130, "XXX", "XXX"));
    result.put("YUD", new CurrencyDataImpl("YUD", "YUD", 130, "YUD", "YUD"));
    result.put("YUM", new CurrencyDataImpl("YUM", "YUM", 130, "YUM", "YUM"));
    result.put("YUN", new CurrencyDataImpl("YUN", "YUN", 130, "YUN", "YUN"));
    result.put("ZWD", new CurrencyDataImpl("ZWD", "ZWD", 128, "ZWD", "ZWD"));
    return result;
  }
  
  @Override
  protected Map<String, String> loadCurrencyNames() {
    Map<String, String> result = super.loadCurrencyNames();
    result.put("AED", "Dirham dos Emirados Árabes Unidos");
    result.put("AFA", "Afeghani (1927-2002)");
    result.put("AFN", "Afgani afegão");
    result.put("ANG", "Florim das Antilhas Holandesas");
    result.put("AWG", "Florim de Aruba");
    result.put("BAD", "Dinar da Bósnia-Herzegóvina");
    result.put("BAM", "Marco bósnio-herzegóvino conversível");
    result.put("BEC", "Franco belga (convertível)");
    result.put("BRL", "Real brasileiro");
    result.put("BYB", "Rublo novo bielorusso (1994-1999)");
    result.put("CAD", "Dólar canadiano");
    result.put("CYP", "Libra de Chipre");
    result.put("CZK", "Coroa da República Checa");
    result.put("ECV", "Unidad de Valor Constante (UVC) do Equador");
    result.put("GHC", "Cedi do Gana");
    result.put("GMD", "Dalasi da Gâmbia");
    result.put("GNF", "Franco Guineense");
    result.put("GTQ", "Quetzal da Guatemala");
    result.put("HNL", "Lempira das Honduras");
    result.put("KWD", "Dinar do Koweit");
    result.put("KYD", "Dólar das Ilhas Caimão");
    result.put("MKD", "Dinar Macedónio");
    result.put("MLF", "Franco do Mali");
    result.put("MWK", "Cuacha do Malawi");
    result.put("MXP", "Peso Plata mexicano (1861-1992)");
    result.put("MXV", "Unidad de Inversion (UDI) mexicana");
    result.put("NIC", "Córdoba nicaraguano");
    result.put("NIO", "Córdoba Ouro nicaraguano");
    result.put("PLN", "Zloti polaco");
    result.put("PLZ", "Zloti polaco (1950-1995)");
    result.put("QAR", "Rial do Catar");
    result.put("SGD", "Dólar de Singapura");
    result.put("THB", "Baht da Tailândia");
    result.put("TZS", "Xelim de Tanzânia");
    result.put("USD", "Dólar dos Estados Unidos");
    result.put("WST", "Tala de Samoa Ocidental");
    result.put("XEU", "Unidade da Moeda Europeia");
    result.put("XXX", "Moeda Desconhecida");
    result.put("YUD", "Dinar forte jugoslavo");
    result.put("YUM", "Super Dinar jugoslavo");
    result.put("YUN", "Dinar conversível jugoslavo");
    result.put("ZWD", "Dólar do Zimbabwe");
    return result;
  }
}
