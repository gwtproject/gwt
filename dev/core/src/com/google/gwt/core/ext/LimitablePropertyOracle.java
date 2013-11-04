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
package com.google.gwt.core.ext;

import com.google.gwt.thirdparty.guava.common.collect.ImmutableSet;

/**
 * Provides deferred binding property values and allows for setting some
 * limitations on property access.
 */
public interface LimitablePropertyOracle extends PropertyOracle {

  /**
   * Sets the list of names of properties for which access is allowed. A null indicates that all
   * properties are accessible.<br />
   *
   * Making allowed property access explicit makes it possible to close the loop and know that
   * generators that optionally declare their list of accessed properties are doing so accurately.
   * Having this list makes it possible to optimize generator execution during separate compilation.
   */
  void setAccessiblePropertyNames(ImmutableSet<String> accessiblePropertyNames);
}
