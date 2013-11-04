/*
 * Copyright 2013 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package com.google.gwt.dev.cfg;

import com.google.gwt.thirdparty.guava.common.base.Preconditions;
import com.google.gwt.thirdparty.guava.common.collect.ImmutableSet;

/**
 * Static utility methods pertaining to PropertyOracle instances.
 */
public class PropertyOracles {

  /**
   * Checks whether the provided property name can be found in the provided list of "accessible"
   * property names. If not found the precondition check fails with a message informing the user
   * (who is almost certainly a developer modifying a generator class) how to correct their
   * generator.
   */
  public static void checkPropertyAccess(
      ImmutableSet<String> accessiblePropertyNames, String propertyName) {
    Preconditions.checkState(
        accessiblePropertyNames == null || accessiblePropertyNames.contains(propertyName),
            "The current generator is not registered to access property " + propertyName
            + " but attempted to access it anyway. It is likely that the generator's "
            + "getAccessedPropertyNames() list needs updating.");
  }
}
