/*
 * Copyright 2007 Google Inc.
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


import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * This class is used to create <code>Constants</code> maps
 * and is to be used only by the GWT code. The map is immediately wrapped in
 * Collections.unmodifiableMap(..) preventing any changes after construction.
 */
public final class ConstantMap {

  public static Map<String, String> of(String keys[], String values[]) {
    HashMap<String, String> map = new HashMap<String, String>();
    for (int i = 0; i < keys.length; ++i) {
      assert keys[i] != null;
      assert values[i] != null;
      map.put(keys[i], values[i]);
    }
    return Collections.unmodifiableMap(map);
  }

  private ConstantMap() { }
}
