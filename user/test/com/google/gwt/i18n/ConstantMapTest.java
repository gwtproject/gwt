/*
 * Copyright 2006 Google Inc.
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
package com.google.gwt.i18n;

import com.google.gwt.i18n.client.impl.ConstantMap;

import java.util.Arrays;
import java.util.Map;

/**
 * Test ConstantMap using Apache's tests.
 */
public class ConstantMapTest extends MapTestBase {

  @Override
  protected boolean isRemoveModifiable() {
    return false;
  }

  @Override
  protected Map<String, String> makeEmptyMap() {
    return ConstantMap.of(new String[] {}, new String[] {});
  }

  @Override
  protected Map<String, String> makeFullMap() {
    String[] keys = Arrays.asList(getSampleKeys()).toArray(new String[0]);
    String[] values = Arrays.asList(getSampleValues()).toArray(new String[0]);
    return ConstantMap.of(keys, values);
  }

  @Override
  protected boolean useNullKey() {
    return false;
  }

  @Override
  protected boolean useNullValue() {
    return false;
  }
}
