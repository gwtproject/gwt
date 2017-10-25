/*
 * Copyright 2017 Google Inc.
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
package com.google.gwt.dev;

/**
 * Helper Class to identify JRE.
 * 
 * This is a modified version of EnvUtil.java found in the logback project at
 * 
 * https://github.com/qos-ch/logback/blob/master/logback-core/src/main/java/
 * ch/qos/logback/core/util/EnvUtil.java
 *
 */
public class JreUtil {

  public static int getJDKVersion(String javaVersionStr) {
    int version = 0;

    for (final char ch : javaVersionStr.toCharArray()) {
      if (Character.isDigit(ch)) {
        version = version * 10 + ch - 48;
      } else if (version == 1) {
        version = 0;
      } else {
        break;
      }
    }
    return version;
  }

  public static boolean isJDK9OrHigher() {
    return isJDK_N_OrHigher(9);
  }

  private static boolean isJDK_N_OrHigher(int n) {
    final String javaVersionStr = System.getProperty("java.version", "");
    if (javaVersionStr.isEmpty()) {
      return false;
    }

    final int version = getJDKVersion(javaVersionStr);
    return version > 0 && n <= version;
  }

}
