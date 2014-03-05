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
package com.google.gwt.dev.util.arg;

import com.google.gwt.dev.js.JsNamespaceOption;
import com.google.gwt.util.tools.ArgHandlerString;

/**
 * When enabled, the GWT compiler will place most JavaScript globals into
 * namespaces instead of putting them all on the "window" object. This reduces
 * the number of globals but increases code size.
 */
public class ArgHandlerNamespace extends ArgHandlerString {
  private final OptionNamespace option;

  public ArgHandlerNamespace(OptionNamespace option) {
    this.option = option;
  }

  @Override
  public String getPurpose() {
    return "Puts most JavaScript globals into namespaces. "
        + "Default: PACKAGE for -draftCompile, otherwise NONE";
  }

  @Override
  public boolean isExperimental() {
    return true;
  }

  @Override
  public String getTag() {
    return "-Xnamespace";
  }

  @Override
  public String[] getTagArgs() {
    return new String[] {"PACKAGE, NONE"};
  }

  @Override
  public boolean setString(String newValue) {
    if (newValue.equalsIgnoreCase("PACKAGE")) {
      option.setNamespace(JsNamespaceOption.BY_JAVA_PACKAGE);
    } else if (newValue.equalsIgnoreCase("NONE")) {
      option.setNamespace(JsNamespaceOption.NONE);
    } else {
      return false;
    }
    return true;
  }
}
