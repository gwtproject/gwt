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

package com.google.gwt.dev.util.log.metrics;

/**
 * Gets an instance of {@link com.google.gwt.dev.util.log.metrics.CompilerMetricsCollector}
 * for sending data to a metrics collector.
 */
public class CompilerMetricsCollectorFactory {
  private static final NoOpCompilerMetricsCollector defaultCollector =
      new NoOpCompilerMetricsCollector();

  private static CompilerMetricsCollector theCollector;

  static {
    setCollector(createCollector(System.getProperty("gwt.metrics.collectorClass")));
  }

  /**
   * Determines whether collection of compiler metrics is enabled. Returns
   * true if the current collector is <strong>not</strong> the default "no-op"
   * instance.
   */
  public static boolean isCollectionEnabled() {
    return theCollector != defaultCollector;
  }

  /**
   * Returns an instance of {@code CompilerMetricsCollector} for sending compiler metrics to a
   * metric collector.
   */
  public static CompilerMetricsCollector getCollector() {
    return theCollector;
  }

  /**
   * Creates a {@code CompilerMetricsCollector} from a given class name. The object is
   * instantiated via reflection.
   *
   * @return the new collector instance if the creation was successful or null on
   *         failure
   */
  static CompilerMetricsCollector createCollector(String className) {
    // Create the instance!
    CompilerMetricsCollector collector = null;
    if (className != null) {
      try {
        Class<?> clazz = Class.forName(className);
        collector = (CompilerMetricsCollector) clazz.newInstance();
      } catch (Exception e) {
        // print error and skip metric collection...
        new Exception("Unexpected failure while trying to load collector class: " + className
            + ". Collecting of compiler metrics will be disabled.", e).printStackTrace();
        return null;
      }
    }
    return collector;
  }

  /**
   * Defines the {@code CompilerMetricsCollector} returned by this factory. Exposed for
   * unit testing purposes (to support mock collector objects). If set to null, a
   * default collector (whose methods do nothing) will be returned by subsequent
   * calls to {@code getCollector}, not null.
   */
  static void setCollector(CompilerMetricsCollector collector) {
    theCollector = collector == null ? defaultCollector : collector;
  }

  /**
   * Prevents this class from being instantiated. Instead use static method
   * {@link #getCollector()}.
   */
  private CompilerMetricsCollectorFactory() {
  }
}
