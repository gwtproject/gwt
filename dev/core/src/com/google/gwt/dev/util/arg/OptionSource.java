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

/**
 * An option that can indicates the Java source level compatibility.
 */
public interface OptionSource {

  /**
   * Java source level compatibility constants.
   */
  enum SourceLevel {
    _6("1.6", "6"),
    _7("1.7", "7");

    private final String stringValue;
    private final String altStringValue;

    private SourceLevel(String stringValue, String altStringValue) {
      this.stringValue = stringValue;
      this.altStringValue = altStringValue;
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
  }

  static final SourceLevel DEFAULT_SOURCE_LEVEL = SourceLevel._6;

  SourceLevel getSourceLevel();

  void setSourceLevel(SourceLevel level);
}
