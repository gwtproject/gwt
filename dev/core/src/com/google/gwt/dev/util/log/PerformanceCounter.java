/*
 * Copyright 2014 Google Inc.
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
package com.google.gwt.dev.util.log;

import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.TreeLogger.Type;
import com.google.gwt.thirdparty.guava.common.base.Preconditions;

import java.util.regex.Pattern;

/**
 * Defines a key for each performance-related counter.
 */
public enum PerformanceCounter {

  /**
   * The number of types that UnifyAst considers to be part of the module being compiled.
   */
  DECLARED_TYPES_IN_MODULE("DeclaredTypesInModule");

  // Note: this constraint is used in JobEvent which is a public API (for Super Dev Mode).
  // Allowing more characters in keys may break data collection.
  private static final Pattern VALID_KEY = Pattern.compile("^[A-Z][A-Za-z0-9]*$");

  static {
    // Cannot check this in the enum constructor.
    for (PerformanceCounter counter : values()) {
      Preconditions.checkState(isValidKey(counter.key), "invalid key: %s", counter.key);
    }
  }

  final String key;

  /**
   * @param key the string key to appear in output. Must be an identifier beginning
   * with a capital letter and not containing underscores.
   */
  PerformanceCounter(String key) {
    this.key = key;
  }

  /**
   * Adds the given amount to the counter.
   * @param logger the destination where the count will be logged.
   */
  public void increment(TreeLogger logger, long amountToAdd) {
    Preconditions.checkNotNull(logger);
    Preconditions.checkArgument(amountToAdd >= 0,
        "attempted to increment a counter using a negative number");
    if (logger instanceof CountingTreeLogger) {
      ((CountingTreeLogger) logger).incrementCounter(this, amountToAdd);
    } else {
      // Just log it.
      logger.log(Type.DEBUG, key + " += " + amountToAdd);
    }
  }

  /**
   * Returns true if the given name can be used as a key for a compiler counter.
   */
  public static boolean isValidKey(String name) {
    return VALID_KEY.matcher(name).matches();
  }
}
