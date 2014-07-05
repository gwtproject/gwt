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

import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * Map used when creating <code>Constants</code> maps. This class is to be
 * used only by the GWT code. The map is immediately wrapped in
 * Collections.unmodifiableMap(..) preventing any changes after construction.
 *
 * This class is deprecated and will be removed in future releases.
 */
@Deprecated
public class ConstantMap implements Map<String, String> {

  private final Map<String, String> map;

  public ConstantMap(String keys[], String values[]) {
    map = ConstantMapCreator.create(keys, values);
  }

  @Override
  public void clear() {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean containsKey(Object key) {
    return map.containsKey(key);
  }

  @Override
  public boolean containsValue(Object value) {
    return map.containsValue(value);
  }

  @Override
  public Set<Entry<String, String>> entrySet() {
    return map.entrySet();
  }

  @Override
  public boolean equals(Object o) {
    return map.equals(o);
  }

  @Override
  public String get(Object key) {
    return map.get(key);
  }

  @Override
  public int hashCode() {
    return map.hashCode();
  }

  @Override
  public boolean isEmpty() {
    return map.isEmpty();
  }

  @Override
  public Set<String> keySet() {
    return map.keySet();
  }

  @Override
  public String put(String key, String value) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void putAll(Map<? extends String, ? extends String> m) {
    throw new UnsupportedOperationException();
  }

  @Override
  public String remove(Object key) {
    throw new UnsupportedOperationException();
  }

  @Override
  public int size() {
    return map.size();
  }

  @Override
  public Collection<String> values() {
    return map.values();
  }
}
