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
package com.google.gwt.i18n.shared;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.junit.client.GWTTestCase;

public abstract class LocaleInfo_en_US_TestBase extends GWTTestCase {

  @Override
  public abstract String getModuleName();

  public void testCaching() {
    LocaleInfo li1 = GWT.create(LocaleInfo.class);
    LocaleInfo li2 = GWT.create(LocaleInfo.class);
    assertSame(li1, li2);
  }

  public void testCurrencyList() {
    LocaleInfo localeInfo = GWT.create(LocaleInfo.class);
    CurrencyList clist = localeInfo.getCurrencyList();
    assertEquals("USD", clist.getDefault().getCurrencyCode());
    assertEquals("US Dollar", clist.lookupName("USD"));
    assertEquals(2, clist.lookup("USD").getDefaultFractionDigits());
  }

  public void testLocaleName() {
    LocaleInfo localeInfo = GWT.create(LocaleInfo.class);
    assertEquals("en_US", localeInfo.getLocaleName());
  }

  public void testListPatterns() {
    LocaleInfo localeInfo = GWT.create(LocaleInfo.class);
    ListPatterns listPatterns = localeInfo.getListPatterns();
    String result = listPatterns.formatEntry(0, 2, "0", "1");
    assertEquals("0 and 1", result);
    result = listPatterns.formatEntry(2, 4, "2", "3");
    for (int i = 1; i >= 0; i--) {
      result = listPatterns.formatEntry(i, 4, String.valueOf(i), result);
    }
    assertEquals("0, 1, 2, and 3", result);
  }

  public void testLocalizedNames() {
    LocaleInfo localeInfo = GWT.create(LocaleInfo.class);
    LocalizedNames names = localeInfo.getLocalizedNames();
    assertEquals("United States", names.getRegionName("US"));
  }

  public void testNumberConstants() {
    LocaleInfo localeInfo = GWT.create(LocaleInfo.class);
    NumberConstants nconst = localeInfo.getNumberConstants();
    assertEquals(".", nconst.decimalSeparator());
  }

  @Override
  protected abstract void gwtSetUp() throws Exception;
}
