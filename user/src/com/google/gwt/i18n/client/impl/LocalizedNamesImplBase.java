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
import com.google.gwt.i18n.client.Localizable;
import com.google.gwt.i18n.shared.LocalizedNames;

/**
 * A base class for client-side implementations of the {@link
 * com.google.gwt.i18n.shared.LocalizedNames} interface.
 */
public abstract class LocalizedNamesImplBase implements Localizable, LocalizedNames {

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

  private JavaScriptObject jsoNameMap;
  private String[] likelyRegionCodes;
  private String[] sortedRegionCodes;

  @Override
  public final String[] getLikelyRegionCodes() {
    if (likelyRegionCodes == null) {
      likelyRegionCodes = loadLikelyRegionCodes();
    }
    return likelyRegionCodes;
  }

  protected String[] loadLikelyRegionCodes() {
    return new String[0];
  }

  @Override
  public final String getRegionName(String regionCode) {
    if (jsoNameMap == null) {
      jsoNameMap = loadNameMap();
    }
    return lookupRegionName(regionCode);
  }

  @Override
  public final String[] getSortedRegionCodes() {
    if (sortedRegionCodes == null) {
      sortedRegionCodes = loadSortedRegionCodes();
    }
    return sortedRegionCodes;
  }

  /**
   * Load the code=>name map for use in JS.
   * 
   * @return a JSO containing a map of country codes to localized names
   */
  protected JavaScriptObject loadNameMap() {
    return JavaScriptObject.createObject();
  }

  protected abstract String[] loadSortedRegionCodes();

  private native String lookupRegionName(String regionCode) /*-{
    return this.@com.google.gwt.i18n.client.impl.LocalizedNamesImplBase::jsoNameMap[regionCode];
  }-*/;
}
