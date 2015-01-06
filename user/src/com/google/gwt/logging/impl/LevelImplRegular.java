/*
 * Copyright 2010 Google Inc.
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

package com.google.gwt.logging.impl;

import java.util.Locale;
import java.util.logging.Level;

/**
 * Implementation for the Level class when logging is enabled.
 */
public class LevelImplRegular implements LevelImpl {
  @Override
  public Level parse(String value) {
    String name = value.toUpperCase(Locale.ROOT);
    switch (name) {
      case "ALL":
        return Level.ALL;

      case "CONFIG":
        return Level.CONFIG;

      case "FINE":
        return Level.FINE;

      case "FINER":
        return Level.FINER;

      case "FINEST":
        return Level.FINEST;

      case "INFO":
        return Level.INFO;

      case "OFF":
        return Level.OFF;

      case "SEVERE":
        return Level.SEVERE;

      case "WARNING":
        return Level.WARNING;

      default:
        throw new IllegalArgumentException("Invalid level \"" + value + "\"");
    }
  }
}
