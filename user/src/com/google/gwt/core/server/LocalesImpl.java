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
package com.google.gwt.core.server;

import com.google.gwt.i18n.shared.GwtLocale;
import com.google.gwt.i18n.shared.GwtLocaleFactory;
import com.google.gwt.i18n.shared.LocaleInfo;
import com.google.gwt.i18n.shared.Locales;

import org.apache.tapestry.util.text.LocalizedProperties;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * A server-side implementation of {@link Locales}.
 */
public class LocalesImpl implements Locales {
  
  private static String localeSuffix(GwtLocale locale) {
    return locale.isDefault() ? "" : "_" + locale.toString();
  }

  private final String[] localeNames;
  private final Map<String, LocaleInfo> localeInfoImpls;
  private final Map<String, String> localeNativeDisplayNames; 

  private final boolean hasAnyRtl;

  public LocalesImpl(GwtLocaleFactory factory) {
    ClassLoader cl = this.getClass().getClassLoader();
    ArrayList<String> localeNamesTemp = new ArrayList<String>();
    BufferedReader rdr = null;
    try {
      rdr = new BufferedReader(new InputStreamReader(cl.getResourceAsStream(
          "com/google/gwt/i18n/shared/cldr/AllLocales.txt")));
      String line;
      while ((line = rdr.readLine()) != null) {
        int comment = line.indexOf('#');
        if (comment >= 0) {
          line = line.substring(0, comment);
        }
        line = line.trim();
        if (line.isEmpty()) {
          continue;
        }
        localeNamesTemp.add(line);
      }
    } catch (IOException e) {
      // TODO: should we log this, or perhaps fail?
    } finally {
      try {
        if (rdr != null) {
          rdr.close();
        }
      } catch (IOException e) {
      }
    }
    localeNames = localeNamesTemp.toArray(new String[localeNamesTemp.size()]);
    Arrays.sort(localeNames);
    localeInfoImpls = new HashMap<String, LocaleInfo>();
    localeNativeDisplayNames = new HashMap<String, String>();
    boolean hasAnyRtlTemp = false;
    for (String localeName : localeNames) {
      GwtLocale locale = factory.fromString(localeName);
      LocaleInfo localeInfo = CldrInstantiator.createInstance(LocaleInfo.class, locale);
      hasAnyRtlTemp = hasAnyRtlTemp || localeInfo.isRTL();
      localeInfoImpls.put(localeName, localeInfo);
      for (GwtLocale search : locale.getCompleteSearchList()) {
        LocalizedProperties props = loadProperties(cl,
            "com/google/gwt/i18n/shared/cldr/LocaleDisplayNames" + localeSuffix(search)
            + ".properties");
        if (props != null) {
          localeNativeDisplayNames.put(localeName, props.getProperty(search.toString()));
          break;
        }
      }
    }
    hasAnyRtl = hasAnyRtlTemp;
  }

  @Override
  public String[] getAvailableLocaleNames() {
    return localeNames;
  }

  @Override
  public LocaleInfo getLocale(String localeName) {
    return localeInfoImpls.get(localeName);
  }

  @Override
  public String getLocaleCookieName() {
    return null;
  }

  @Override
  public String getLocaleNativeDisplayName(String localeName) {
    return localeNativeDisplayNames.get(localeName);
  }    

  @Override
  public String getLocaleQueryParam() {
    return null;
  }

  @Override
  public boolean hasAnyRtl() {
    return hasAnyRtl;
  }

  private LocalizedProperties loadProperties(ClassLoader cl, String path) {
    InputStream istr = null;
    try {
      istr = cl.getResourceAsStream(path);
      if (istr != null) {
        LocalizedProperties props = new LocalizedProperties();
        props.load(istr);
        return props;
      }
    } catch (IOException e) {
      return null;
    } finally {
      if (istr != null) {
        try {
          istr.close();
        } catch (IOException e) {
          // ignore
        }
      }
    }
    return null;
  }
}