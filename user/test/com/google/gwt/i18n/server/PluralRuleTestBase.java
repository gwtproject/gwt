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
package com.google.gwt.i18n.server;

import com.google.gwt.core.server.LocalesImpl;
import com.google.gwt.i18n.shared.GwtLocale;
import com.google.gwt.i18n.shared.GwtLocaleFactory;
import com.google.gwt.i18n.shared.Locales;
import com.google.gwt.i18n.shared.VariantSelector;
import com.google.gwt.i18n.shared.VariantSelector.VariantForm;

import com.ibm.icu.text.PluralRules;
import com.ibm.icu.util.ULocale;

import junit.framework.TestCase;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

/**
 * Test that compares an ICU plural rule to the GWT one generated from CLDR.
 * We have to be careful to avoid version skew, as the rules will get updated
 * and included into ICU and GWT at different times.
 */
public abstract class PluralRuleTestBase extends TestCase {

  protected final GwtLocaleFactory factory = new GwtLocaleFactoryImpl();

  public void testAllLocales() {
    ULocale[] icuLocales = PluralRules.getAvailableULocales();
    Locales locales = new LocalesImpl(factory);
    Set<String> gwtLocales = new HashSet<String>();
    gwtLocales.addAll(Arrays.asList(locales.getAvailableLocaleNames()));
    for (ULocale icuLocale : icuLocales) {
      GwtLocale gwtLocale = factory.fromString(icuLocale.getName());
      String localeName = gwtLocale.toString();
      if (gwtLocales.contains(localeName) && !skipLocale(localeName)) {
        compareLocale(localeName);
      }
    }
  }

  /**
   * Check if a locale should be skipped, because ICU is incorrect.
   * 
   * @param localeName
   * @return true if we should not test this locale
   */
  protected boolean skipLocale(String localeName) {
    return false;
  }

  protected void compareLocale(String localeName) {
    ULocale icuLocale = ULocale.forLanguageTag(localeName);
    PluralRules icu = getIcuRules(icuLocale);
    GwtLocale gwtLocale = factory.fromString(localeName);
    VariantSelector gwt = getGwtRules(gwtLocale);

    // try all the ICU-supplied samples
    for (String keyword : icu.getKeywords()) {
      for (double n : icu.getSamples(keyword)) {
        checkGwtForm(localeName, gwt, keyword, n);        
      }
    }
    
    // test integers
    for (int base = 0; base < 6000; base += 1000) {
      for (int i = 0; i < 100; ++i) {
        compareValue(localeName, icu, gwt, base + i);
      }
    }
    for (int i = 0; i < 100; ++i) {
      compareValue(localeName, icu, gwt, 1000000 + i);
    }

    // test fractions
    for (int i = 0; i < 1000; ++i) {
      compareValue(localeName, icu, gwt, i / 10.0);
    }
  }

  protected void compareValue(String localeName, PluralRules icu, VariantSelector gwt, double n) {
    String icuForm = icu.select(n);
    checkGwtForm(localeName, gwt, icuForm, n);
  }

  public void checkGwtForm(String localeName, VariantSelector gwt, String icuForm, double n) {
    VariantForm gwtForm = gwt.select(n);
    assertEquals("Mismatch in locale " + localeName + ", value " + n, icuForm,
        gwtForm.toString().toLowerCase(Locale.ENGLISH));
  }

  /**
   * Get the GWT plural rules (which may be either cardinal or ordinal) for
   * the specified locale.
   * 
   * @param localeName
   * @return {@link VariantSelector} instance
   */
  protected abstract VariantSelector getGwtRules(GwtLocale locale);

  /**
   * Get the ICU plural rules (which may be either cardinal or ordinal) for
   * the specified locale.
   * 
   * @param localeName
   * @return {@link PluralRules} instance
   */
  protected abstract PluralRules getIcuRules(ULocale locale);
}
