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
//  number=$Revision: 4753 $
//  date=$Date: 2010-02-26 01:32:00 -0500 (Fri, 26 Feb 2010) $
//  type=root

/**
 * JS implementation of CurrencyList for locale "ar_IQ".
 */
public class CurrencyListImpl_ar_IQ extends CurrencyListImpl_ar {

  @Override
  public native CurrencyData getDefault() /*-{
    return [ "IQD", "د.ع.‏", 0, "د.ع.‏", "din"];
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
    };
  }-*/;

  private native JavaScriptObject loadCurrencyNamesOverride() /*-{
    return {
    };
  }-*/;
}
