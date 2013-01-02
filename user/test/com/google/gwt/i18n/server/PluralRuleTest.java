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

import com.google.gwt.core.server.CldrInstantiator;
import com.google.gwt.i18n.shared.GwtLocale;
import com.google.gwt.i18n.shared.LocaleInfo;
import com.google.gwt.i18n.shared.VariantSelector;

import com.ibm.icu.text.PluralRules;
import com.ibm.icu.util.ULocale;

import java.util.HashSet;
import java.util.Set;

/**
 * Check plural rules.
 */
public class PluralRuleTest extends PluralRuleTestBase {

  private static Set<String> BAD_LOCALES = new HashSet<String>();
  
  static {
    // The CLDR version GWT is based on doesn't include these plural rules
    BAD_LOCALES.add("ast");
    BAD_LOCALES.add("ckb");
    BAD_LOCALES.add("ks");
    BAD_LOCALES.add("ky");

    // GWT is correct according the CLDR version it is based on
    BAD_LOCALES.add("he");
  }

  @Override
  protected VariantSelector getGwtRules(GwtLocale locale) {
    LocaleInfo localeInfo = CldrInstantiator.createInstance(LocaleInfo.class, locale);
    return localeInfo.getPluralRule();
  }

  @Override
  protected PluralRules getIcuRules(ULocale locale) {
    return PluralRules.forLocale(locale);
  }

  @Override
  protected boolean skipLocale(String localeName) {
    String[] split = localeName.split("_");
    return BAD_LOCALES.contains(localeName) || BAD_LOCALES.contains(split[0]);
  }
}
