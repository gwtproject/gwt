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

/**
 * A TreeLogger that supports {@link PerformanceCounter}.
 * (This class exists only to avoid making incrementCounter a public API.)
 */
abstract class CountingTreeLogger extends TreeLogger {

  /**
   * This should normally be called via {@link PerformanceCounter#increment}.
   */
  abstract void incrementCounter(PerformanceCounter key, long amountToAdd);
}
