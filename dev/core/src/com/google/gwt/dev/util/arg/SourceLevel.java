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
package com.google.gwt.dev.util.arg;

import com.google.gwt.thirdparty.guava.common.annotations.VisibleForTesting;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Java source level compatibility constants.
 */
public enum SourceLevel {
  // Source levels must appear in ascending order for the default setting logic to work.
  JAVA8("1.8", "8"),
  JAVA9("9", "1.9"),
  JAVA10("10", "1.10"),
  JAVA11("11", "1.11"),
  JAVA17("17", "1.17");

  /**
   * A pattern that expresses version strings. It has two groups the prefix (a dotted integer
   * sequence) and a suffix (a regular string)
   * <p>
   * Examples: 1.6.7, 1.2_b10
   *
   */
  private static final Pattern VERSION_PATTERN =
      Pattern.compile("([0-9]+(?:\\.[0-9]+)*)((?:_[a-zA-Z0-9]+)?)");

  /**
   * The default java sourceLevel.
   */
  public static final SourceLevel DEFAULT_SOURCE_LEVEL = getJvmBestMatchingSourceLevel();

  private final String stringValue;
  private final String altStringValue;

  SourceLevel(String stringValue, String altStringValue) {
    this.stringValue = stringValue;
    this.altStringValue = altStringValue;
  }

  /**
   * Handles comparison between version numbers (the right way(TM)).
   *
   * Examples of version strings: 1.6.7, 1.2_b10
   *
   * @param v1 the first version to compare.
   * @param v2 the second version to compare.
   * @return a negative integer, zero, or a positive integer as the first argument is less than,
   *         equal to, or greater than the second.
   * @throws IllegalArgumentException if the version number are not proper (i.e. the do not comply
   *                                  with the following regular expression
   *                                  [0-9]+(.[0-9]+)*(_[a-zA-Z0-9]+)?
   */
  public static int versionCompare(String v1, String v2) {
    Matcher v1Matcher = VERSION_PATTERN.matcher(v1);
    Matcher v2Matcher = VERSION_PATTERN.matcher(v2);
    if (!v1Matcher.matches() || !v2Matcher.matches()) {
      throw new IllegalArgumentException(v1Matcher.matches() ? v2 : v1 + " is not a proper version"
          + " string");
    }

    String[] v1Prefix = v1Matcher.group(1).split("\\.");
    String[] v2Prefix = v2Matcher.group(1).split("\\.");
    for (int i = 0; i < v1Prefix.length; i++) {
      if (v2Prefix.length <= i) {
        return 1; // v1 > v2
      }
      int compare = Integer.parseInt(v1Prefix[i]) - Integer.parseInt(v2Prefix[i]);
      if (compare != 0) {
        return compare;
      }
    }
    // So far they are equal (or v2 is longer than v1)
    if (v2Prefix.length == v1Prefix.length) {
      // then it is up to the suffixes
      return v1Matcher.group(2).compareTo(v2Matcher.group(2));
    }

    // v2 is greater than v1,
    return -1;
  }

  /**
   * Returns a string value representation for the source level.
   */
  public String getStringValue() {
    return stringValue;
  }

  /**
   * Returns an alternate string value representation for the source level.
   */
  public String getAltStringValue() {
    return altStringValue;
  }

  @Override
  public String toString() {
    return stringValue;
  }

  /**
   * Returns the SourceLevel given the string or alternate string representation; returns {@code
   * null} if none is found.
   */
  public static SourceLevel fromString(String sourceLevelString) {
    if (sourceLevelString == null) {
      return null;
    }
    for (SourceLevel sourceLevel : SourceLevel.values()) {
      if (sourceLevel.stringValue.equals(sourceLevelString) ||
          sourceLevel.altStringValue.equals(sourceLevelString)) {
        return sourceLevel;
      }
    }
    return null;
  }

  private static SourceLevel getJvmBestMatchingSourceLevel() {
    String javaSpecLevel = System.getProperty("java.specification.version");
    return getBestMatchingVersion(javaSpecLevel);
  }

  @VisibleForTesting
  public static SourceLevel getBestMatchingVersion(String javaVersionString) {
    try {
      // Find the last version that is less than or equal to javaSpecLevel by iterating in reverse
      // order.
      SourceLevel[] sourceLevels = SourceLevel.values();
      for (int i = sourceLevels.length - 1; i >= 0; i--) {
        if (versionCompare(javaVersionString, sourceLevels[i].stringValue) >= 0) {
          // sourceLevel is <= javaSpecLevel, so keep this one.
          return sourceLevels[i];
        }
      }
    } catch (IllegalArgumentException e) {
      // If the version can not be parsed fallback to JAVA8.
    }
    // If everything fails set default to JAVA8.
    return JAVA8;
  }

  /**
   * Returns the highest supported version of the Java.
   */
  public static SourceLevel getHighest() {
    return SourceLevel.values()[SourceLevel.values().length - 1];
  }
}

