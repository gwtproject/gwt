/*
 * Copyright 2025 GWT Project Authors
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
package com.google.gwt.dev.util.log.perf;

import jdk.jfr.SettingControl;

import java.util.Set;

/**
 * A SettingControl for boolean settings. Defaults to true.
 */
public class BooleanSettingControl extends SettingControl {
  private boolean enabled;
  @Override
  public String combine(Set<String> set) {
    if (set.contains("true")) {
      return "true";
    }
    if (set.contains("false")) {
      return "false";
    }
    return "true";
  }

  @Override
  public void setValue(String s) {
    enabled = "true".equals(s);
  }

  @Override
  public String getValue() {
    return Boolean.toString(enabled);
  }

  public boolean get() {
    return enabled;
  }
}
