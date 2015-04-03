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
package com.google.gwt.testing;

import com.google.gwt.regexp.shared.MatchResult;
import com.google.gwt.regexp.shared.RegExp;

/**
 * Utility functions needed by various tests.
 */
public class TestUtils {

  private static final String VERSION_REGEXP = "1\\.([0-9]+)(\\..*)?";

  public static int getJdkVersion() {
    String versionString = System.getProperty("java.version", "none");
    if (versionString.equals("none")) {
      return -1;
    }

    return getMajorVersion(versionString);
  }

  private static int getMajorVersion(String versionString) {
    MatchResult matchResult = RegExp.compile(VERSION_REGEXP).exec(versionString);
    assert matchResult.getGroup(0).equals(versionString);
    return Integer.parseInt(matchResult.getGroup(1));
  }
}
