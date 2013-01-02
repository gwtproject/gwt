/*
 * Copyright 2008 Google Inc.
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
package com.google.gwt.i18n.client;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.i18n.shared.LocaleInfo;

import java.util.Iterator;

/**
 * Legacy implementation for compatibility purposes.
 * 
 * @deprecated use {@link com.google.gwt.i18n.shared.LocaleInfo#getCurrencyList()} instead
 */
@Deprecated
public class CurrencyList implements Iterable<CurrencyData> {

  private static class CurrencyWrapper implements CurrencyData {
    private final com.google.gwt.i18n.shared.CurrencyData currencyData;

    public CurrencyWrapper(com.google.gwt.i18n.shared.CurrencyData currencyData) {
      this.currencyData = currencyData;
    }
  
    @Override
    public String getCurrencyCode() {
      return currencyData.getCurrencyCode();
    }

    @Override
    public String getCurrencySymbol() {
      return currencyData.getCurrencySymbol();
    }

    @Override
    public int getDefaultFractionDigits() {
      return currencyData.getDefaultFractionDigits();
    }

    @Override
    public String getPortableCurrencySymbol() {
      return currencyData.getPortableCurrencySymbol();
    }

    @Override
    public String getSimpleCurrencySymbol() {
      return currencyData.getSimpleCurrencySymbol();
    }

    @Override
    public boolean isDeprecated() {
      return currencyData.isDeprecated();
    }

    @Override
    public boolean isSpaceForced() {
      return currencyData.isSpaceForced();
    }

    @Override
    public boolean isSpacingFixed() {
      return currencyData.isSpacingFixed();
    }

    @Override
    public boolean isSymbolPositionFixed() {
      return currencyData.isSymbolPositionFixed();
    }

    @Override
    public boolean isSymbolPrefix() {
      return currencyData.isSymbolPrefix();
    }
  };

  private final com.google.gwt.i18n.shared.CurrencyList currencyList;

  /**
   * Inner class to avoid CurrencyList.clinit calls and allow this to be
   * completely removed from the generated code if instance isn't referenced
   * (such as when all you call is CurrencyList.get().getDefault() ).
   */
  private static class CurrencyListInstance {
    private static LocaleInfo localeInfo = GWT.create(LocaleInfo.class);
    private static CurrencyList instance = new CurrencyList(localeInfo.getCurrencyList());
  }

  public CurrencyList(com.google.gwt.i18n.shared.CurrencyList currencyList) {
    this.currencyList = currencyList;
  }

  /**
   * Return the singleton instance of CurrencyList.
   */
  public static CurrencyList get() {
    return CurrencyListInstance.instance;
  }
  
  /**
   * Return the default currency data for this locale.
   * 
   * Generated implementations override this method.
   */
  public CurrencyData getDefault() {
    return new CurrencyWrapper(currencyList.getDefault());
  }

  /**
   * Returns an iterator for the list of currencies.
   * 
   * Deprecated currencies will not be included.
   */
  @Override
  public final Iterator<CurrencyData> iterator() {
    return iterator(false);
  }

  /**
   * Returns an iterator for the list of currencies, optionally including
   * deprecated ones. 
   * 
   * @param includeDeprecated true if deprecated currencies should be included
   */
  public final Iterator<CurrencyData> iterator(boolean includeDeprecated) {
    final Iterator<com.google.gwt.i18n.shared.CurrencyData> it = currencyList.iterator(includeDeprecated);
    return new Iterator<CurrencyData>() {
      @Override
      public boolean hasNext() {
        return it.hasNext();
      }

      @Override
      public CurrencyData next() {
        return new CurrencyWrapper(it.next());
      }

      @Override
      public void remove() {
        throw new UnsupportedOperationException();
      }
    };
  }

  /**
   * Lookup a currency based on the ISO4217 currency code.
   * 
   * @param currencyCode ISO4217 currency code
   * @return currency data, or null if code not found
   */
  public final CurrencyData lookup(String currencyCode) {
    return new CurrencyWrapper(currencyList.lookup(currencyCode));
  }

  /**
   * Lookup a currency name based on the ISO4217 currency code.
   * 
   * @param currencyCode ISO4217 currency code
   * @return name of the currency, or null if code not found
   */
  public final String lookupName(String currencyCode) {
    return currencyList.lookupName(currencyCode);
  }
}
