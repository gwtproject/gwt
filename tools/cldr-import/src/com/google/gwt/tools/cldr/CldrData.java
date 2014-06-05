/*
 * Copyright 2014 Google Inc.
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
package com.google.gwt.tools.cldr;

import com.google.gwt.i18n.server.GwtLocaleFactoryImpl;
import com.google.gwt.i18n.shared.GwtLocale;
import com.google.gwt.i18n.shared.GwtLocaleFactory;
import com.google.gwt.tools.cldr.QueryMatchCallback;
import com.google.gwt.tools.cldr.XPathQuery;

import org.unicode.cldr.util.CLDRFile;
import org.unicode.cldr.util.Factory;
import org.unicode.cldr.util.XPathParts;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Main entry point for processing CLDR data - keeps track of which
 * processors are interested in which xpaths, and dependencies between
 * processors.
 */
public class CldrData {

  /**
   * A wrapper around a {@link QueryMatchCallback} that ties it to a
   * query and sequencing for multiple callbacks for matching queries.
   */
  private static class QueryCallback implements Comparable<QueryCallback> {
    public final XPathQuery query;
    public final QueryMatchCallback callback;
    public final int seq;

    public QueryCallback(XPathQuery query, QueryMatchCallback callback, int seq) {
      this.query = query;
      this.callback = callback;
      this.seq = seq;
    }

    @Override
    public String toString() {
      return query.toString() + " => " + callback.getClass().getSimpleName();
    }

    @Override
    public int hashCode() {
      return query.hashCode() + callback.hashCode() * 17 + seq * 31;
    }

    @Override
    public boolean equals(Object obj) {
      if (!(obj instanceof QueryCallback)) {
        return false;
      }
      QueryCallback other = (QueryCallback) obj;
      return query.equals(other.query)
          && callback.equals(other.callback)
          && seq == other.seq;
    }

    @Override
    public int compareTo(QueryCallback o) {
      return seq - o.seq;
    }
  }

  private static final GwtLocaleFactory localeFactory = new GwtLocaleFactoryImpl();

  public static final int DEFAULT_SEQUENCE = 50;

  public static final String MINIMUM_CLDR_VERSION = "24";

  private final Map<GwtLocale, String> allLocales;
  private final Factory cldrFactory;
  private final List<QueryCallback> localeCallbacks;
  private final LocaleData localeData;
  private final Map<String, List<QueryCallback>> supplementalCallbacks;
  private final RegionLanguageData regionLanguageData;
  private final List<Runnable> postProcessors;

  public CldrData(String sourceDir, String localeList) {
    cldrFactory = Factory.make(sourceDir, ".*");
    if (MINIMUM_CLDR_VERSION.compareTo(CLDRFile.GEN_VERSION) > 0) {
      throw new RuntimeException("Requires at least CLDR " + MINIMUM_CLDR_VERSION + ", found "
          + CLDRFile.GEN_VERSION);
    }
    Set<String> locales = cldrFactory.getAvailable();
    if (localeList != null) {
      Set<String> newLocales = new HashSet<String>();
      newLocales.add("root");  // always include root or things break
      for (String locale : localeList.split(",")) {
        if (!locales.contains(locale)) {
          System.err.println("Ignoring non-existent locale " + locale);
          continue;
        }
        newLocales.add(locale);
      }
      locales = newLocales;
    }
    localeData = new LocaleData(localeFactory, locales);
    allLocales = localeData.getAllLocalesMap();
    localeCallbacks = new ArrayList<QueryCallback>();
    supplementalCallbacks = new HashMap<String, List<QueryCallback>>();
    postProcessors = new ArrayList<Runnable>();
    regionLanguageData = new RegionLanguageData(this);
  }

  public LocaleData getLocaleData() {
    return localeData;
  }

  public RegionLanguageData getRegionLanguageData() {
    return regionLanguageData;
  }

  /**
   * Register a callback for a locale-specific query, with default sequencing.
   * 
   * @param query
   * @param callback
   */
  public void registerLocale(XPathQuery query, QueryMatchCallback callback) {
    registerLocale(query, callback, DEFAULT_SEQUENCE);
  }

  /**
   * Register a callback for a locale-specific query, with specific sequencing.
   * 
   * @param query
   * @param callback
   * @param seq
   */
  public void registerLocale(XPathQuery query, QueryMatchCallback callback, int seq) {
    localeCallbacks.add(new QueryCallback(query, callback, seq));
  }

  /**
   * Register a callback for a query against a supplemental file, with default
   * sequencing.
   * 
   * @param supp
   * @param query
   * @param callback
   */
  public void registerSupplemental(String supp, XPathQuery query, QueryMatchCallback callback) {
    registerSupplemental(supp, query, callback, DEFAULT_SEQUENCE);
  }

  /**
   * Register a callback for a query against a supplemental file, with
   * specific sequencing.
   * 
   * @param supp
   * @param query
   * @param callback
   * @param seq
   */
  public void registerSupplemental(String supp, XPathQuery query, QueryMatchCallback callback,
      int seq) {
    List<QueryCallback> list = supplementalCallbacks.get(supp);
    if (list == null) {
      list = new ArrayList<QueryCallback>();
      supplementalCallbacks.put(supp, list);
    }
    list.add(new QueryCallback(query, callback, seq));
  }

  /**
   * Process all data, starting with supplemental data, then all requested
   * locales, then post-processing.
   */
  public void processData() {
    Set<String> keySet = supplementalCallbacks.keySet();
    String[] supplementalFiles = keySet.toArray(new String[keySet.size()]);
    cldrFactory.getSupplementalData();
    System.out.println("  loading supplemental data");
    long start = new Date().getTime();
    for (String supplemental : supplementalFiles) {
      System.out.println("    " + supplemental);
      processCallbacks(getSupplementalData(supplemental), null,
          supplementalCallbacks.get(supplemental));
    }
    long delta = new Date().getTime() - start;
    System.out.println("    (" + delta + "ms)");
    System.out.println("  loading per-locale data for " + allLocales.size() + " locales");
    start = new Date().getTime();
    for (GwtLocale locale : allLocales.keySet()) {
      System.out.println("    " + locale.toString());
      CLDRFile cldr = cldrFactory.make(allLocales.get(locale), true);
      processCallbacks(cldr, locale, localeCallbacks);
    }
    for (Runnable runnable : postProcessors) {
      runnable.run();
    }
    delta = new Date().getTime() - start;
    System.out.println("    (" + delta + "ms)");
  }

  private void processCallbacks(CLDRFile file, GwtLocale locale, List<QueryCallback> callbacks) {
    Collections.sort(callbacks);
    Iterator<String> iterator = file.iterator();
    XPathParts parts = new XPathParts();
    while (iterator.hasNext()) {
      String path = iterator.next();
      parts.set(file.getFullXPath(path));
      String value = file.getStringValue(path);
      for (QueryCallback callback : callbacks) {
        if (callback.query.matches(parts)) {
          callback.callback.process(locale, parts, value);
        }
      }
    }
  }

  private CLDRFile getSupplementalData(String fileName) {
    try {
      return cldrFactory.make(fileName, false);
    } catch (RuntimeException e) {
      return Factory.make(cldrFactory.getSupplementalDirectory().getPath(), ".*").make(fileName,
          false);
    }
  }

  public Factory getCldrFactory() {
    return cldrFactory;
  }

  public Set<GwtLocale> allLocales() {
    return Collections.unmodifiableSet(allLocales.keySet());
  }

  public void addPostProcessor(Runnable runnable) {
    postProcessors.add(runnable);
  }
}
