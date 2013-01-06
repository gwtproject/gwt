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
//  date=$Date: 2011-09-27 11:37:06 -0400 (Tue, 27 Sep 2011) $
//  number=$Revision: 6177 $
//  type=root

/**
 *  * Pure Java implementation of CurrencyList for locale "pa_Arab".
 */
public class CurrencyListImpl_pa_Arab extends CurrencyListImpl_pa {
  
  @Override
  public CurrencyData getDefault() {
    return new CurrencyDataImpl("PKR", "ر", 0, "PKRs.", "Rs");
  }
  
  @Override
  protected Map<String, CurrencyData> loadCurrencies() {
    Map<String, CurrencyData> result = super.loadCurrencies();
    result.put("AFN", new CurrencyDataImpl("AFN", "AFN", 0, "AFN", "Af."));
    result.put("EUR", new CurrencyDataImpl("EUR", "€", 2, "€", "€"));
    result.put("INR", new CurrencyDataImpl("INR", "ر [INR]", 2, "Rs", "₹"));
    result.put("PKR", new CurrencyDataImpl("PKR", "ر", 0, "PKRs.", "Rs"));
    result.put("XXX", new CurrencyDataImpl("XXX", "XXX", 130, "XXX", "XXX"));
    return result;
  }
  
  @Override
  protected Map<String, String> loadCurrencyNames() {
    Map<String, String> result = super.loadCurrencyNames();
    result.put("AFN", "AFN");
    result.put("EUR", "يورو");
    result.put("INR", "روپئیہ [INR]");
    result.put("PKR", "روپئیہ");
    result.put("XXX", "XXX");
    return result;
  }
}
