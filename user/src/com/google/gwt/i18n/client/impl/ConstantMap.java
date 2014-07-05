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

import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Set;

/**
 * Map used when creating <code>Constants</code> maps. This class is to be
 * used only by the GWT code. The map is immediately wrapped in
 * Collections.unmodifiableMap(..) preventing any changes after construction.
 */
public class ConstantMap extends AbstractMap<String, String> {

  private final HashMap<String, String> map = new HashMap<String, String>();

  public ConstantMap(String keys[], String values[]) {
    for (int i = 0; i < keys.length; ++i) {
      assert keys[i] != null;
      assert values[i] != null;
      map.put(keys[i], values[i]);
    }
  }

  @Override
  public boolean containsKey(Object key) {
    return get(key) != null;
  }

  @Override
  public Set<Entry<String, String>> entrySet() {
    return map.entrySet();
  }

  @Override
  public String get(Object key) {
    return (key instanceof String) ? get((String) key) : null;
  }

  public String get(String key) {
    return map.get(key);
  }

  @Override
  public Set<String> keySet() {
    return map.keySet();
  }

  @Override
  public int size() {
    return map.size();
  }
}
