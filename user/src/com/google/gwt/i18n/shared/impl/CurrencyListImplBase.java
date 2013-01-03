package com.google.gwt.i18n.shared.impl;

import com.google.gwt.i18n.shared.CurrencyData;
import com.google.gwt.i18n.shared.CurrencyList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public abstract class CurrencyListImplBase implements CurrencyList {

  // protects modifications to currencies and currencyNames
  private final Object lock = new Object[0];

  private Map<String, CurrencyData> currencies;
  private Map<String, String> currencyNames;

  public CurrencyListImplBase() {
    super();
  }

  @Override
  public abstract CurrencyData getDefault();

  @Override
  public Iterator<CurrencyData> iterator() {
    return iterator(false);
  }

  @Override
  public Iterator<CurrencyData> iterator(boolean includeDeprecated) {
    ensureCurrencies();
    Collection<CurrencyData> result = currencies.values();
    if (!includeDeprecated) {
      List<CurrencyData> copy = new ArrayList<CurrencyData>();
      for (CurrencyData cdata : result) {
        if (!cdata.isDeprecated()) {
          copy.add(cdata);
        }
      }
      result = copy;
    }
    return result.iterator();
  }

  @Override
  public CurrencyData lookup(String currencyCode) {
    ensureCurrencies();
    return currencies.get(currencyCode);
  }

  @Override
  public String lookupName(String currencyCode) {
    synchronized (lock) {
      if (currencyNames == null) {
        currencyNames = loadCurrencyNames();
      }
    }
    return currencyNames.get(currencyCode);
  }

  protected Map<String, CurrencyData> loadCurrencies() {
    return new HashMap<String, CurrencyData>();
  }

  protected Map<String, String> loadCurrencyNames() {
    return new HashMap<String, String>();
  }

  private void ensureCurrencies() {
    synchronized (lock) {
      if (currencies == null) {
        currencies = loadCurrencies();
      }
    }
  }
}