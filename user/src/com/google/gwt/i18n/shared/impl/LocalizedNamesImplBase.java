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
package com.google.gwt.i18n.shared.impl;

import com.google.gwt.i18n.shared.LocalizedNames;

import java.util.HashMap;
import java.util.Map;

/**
 * A base class for client-side implementations of the {@link
 * com.google.gwt.i18n.client.LocalizedNames} interface.
 */
public abstract class LocalizedNamesImplBase implements LocalizedNames {

  protected Map<String, String> nameMap = null;
  private String[] sortedRegionCodes;

  @Override
  public String[] getLikelyRegionCodes() {
    return new String[0];
  }

  @Override
  public String getRegionName(String regionCode) {
    if (nameMap == null) {
      ensureNameMap();
    }
    return nameMap.get(regionCode);
  }

  @Override
  public final String[] getSortedRegionCodes() {
    if (sortedRegionCodes == null) {
      sortedRegionCodes = loadSortedRegionCodes();
    }
    return sortedRegionCodes;
  }

  /**
   * Ensure {@link #nameMap} has been created and populated.  Implementations
   * should call the superclass version of this method and then override the
   * values, unless there is so little commonality that this would be wasted,
   * in which case they should create {@link #nameMap} and populate it.
   */
  protected void ensureNameMap() {
    nameMap = new HashMap<String, String>();
  }

  protected abstract String[] loadSortedRegionCodes();
}
