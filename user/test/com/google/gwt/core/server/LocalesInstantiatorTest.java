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

import com.google.gwt.i18n.shared.LocaleInfo;
import com.google.gwt.i18n.shared.Locales;

import junit.framework.TestCase;

/**
 * Test for {@link LocalesInstantiator}.
 */
public class LocalesInstantiatorTest extends TestCase {
  // changes will be required when we have Locales get info from a linker

  private LocalesInstantiator inst = new LocalesInstantiator();
  private Locales locales = inst.create(Locales.class, null);

  public void testGetLocale() {
    LocaleInfo localeInfo = locales.getLocale("en_US");
    assertNotNull(localeInfo);
    assertEquals("en_US", localeInfo.getLocaleName());
    localeInfo = locales.getLocale("de");
    assertNotNull(localeInfo);
    assertEquals("de", localeInfo.getLocaleName());
  }

  public void testGetLocaleNativeDisplayName() {
    assertEquals("English", locales.getLocaleNativeDisplayName("en"));
    assertEquals("U.S. English", locales.getLocaleNativeDisplayName("en_US"));
    assertEquals("Deutsch", locales.getLocaleNativeDisplayName("de"));
  }
}
