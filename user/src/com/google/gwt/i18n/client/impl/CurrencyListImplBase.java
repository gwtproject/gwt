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
package com.google.gwt.i18n.client.impl;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.i18n.shared.CurrencyData;
import com.google.gwt.i18n.shared.CurrencyList;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Base class for JS implementations of {@link CurrencyList}.
 */
public abstract class CurrencyListImplBase implements CurrencyList {

  /**
   * Add all entries in {@code override} to the original map, replacing
   * any existing entries.  This is used by subclasses that need to slightly
   * alter the data used by the parent locale.
   */
  protected static final native JavaScriptObject overrideMap(
      JavaScriptObject original, JavaScriptObject override) /*-{
    for (var key in override) {
      if (override.hasOwnProperty(key)) {
        original[key] = override[key];
      }
    }
    return original;
  }-*/;
  private static native String lookupNameNative(JavaScriptObject nameMap, String currencyCode) /*-{
    return nameMap[currencyCode];
  }-*/;

  private static native CurrencyDataJso lookupNative(JavaScriptObject curMap, String currencyCode) /*-{
    return curMap[currencyCode];
  }-*/;

  private JavaScriptObject currencies;

  private JavaScriptObject currencyNames;

  @Override
  public abstract CurrencyData getDefault();

  @Override
  public Iterator<CurrencyData> iterator() {
    return iterator(false);
  }

  @Override
  public Iterator<CurrencyData> iterator(boolean includeDeprecated) {
    if (currencies == null) {
      currencies = loadCurrencies();
    }
    List<CurrencyData> result = new ArrayList<CurrencyData>();
    copyNative(result, includeDeprecated);
    return result.iterator();
  }

  @Override
  public CurrencyData lookup(String currencyCode) {
    if (currencies == null) {
      currencies = loadCurrencies();
    }
    return lookupNative(currencies, currencyCode);
  }

  @Override
  public String lookupName(String currencyCode) {
    if (currencyNames == null) {
      currencyNames = loadCurrencyNames();
    }
    return lookupNameNative(currencyNames, currencyCode);
  }

  protected native void copyNative(List<CurrencyData> result, boolean includeDeprecated) /*-{
    var c = this.@com.google.gwt.i18n.client.impl.CurrencyListImplBase::currencies;
    for (var k in c) {
      var v = c[k];
      if (includeDeprecated
          || !@com.google.gwt.i18n.shared.impl.CurrencyDataHelper::isDeprecated(I)(v[2])) {
        result.@java.util.List::add(Ljava/lang/Object;)(v);
      }
    }
  }-*/;

  protected native JavaScriptObject loadCurrencies() /*-{
    return {};
  }-*/;

  protected JavaScriptObject loadCurrencyNames() {
    return JavaScriptObject.createObject();
  }

}