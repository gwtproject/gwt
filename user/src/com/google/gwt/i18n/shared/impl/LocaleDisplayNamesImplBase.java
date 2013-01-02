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

import com.google.gwt.i18n.shared.LocaleDisplayNames;

import java.util.HashMap;
import java.util.Map;

/**
 * Base implementation of non-client implementations of LocaleDisplayNames.
 */
public class LocaleDisplayNamesImplBase implements LocaleDisplayNames {

  private final Object lock = new Object[0];
  private Map<String, String> displayNames;
  
  @Override
  public final String getDisplayName(String localeName) {
    synchronized (lock) {
      displayNames = loadDisplayNames();
    }
    return displayNames.get(localeName);
  }

  protected Map<String, String> loadDisplayNames() {
    return new HashMap<String, String>();
  }
}
