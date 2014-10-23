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

import com.google.gwt.dev.util.arg.OptionMethodNameDisplayMode.Mode;
import com.google.gwt.thirdparty.guava.common.base.Joiner;
import com.google.gwt.util.tools.ArgHandlerString;

/**
 * Argument handler set setting the display name setting in the compiler.
 */
public class ArgHandlerMethodNameDisplayMode extends ArgHandlerString {

  private final OptionMethodNameDisplayMode option;

  public ArgHandlerMethodNameDisplayMode(OptionMethodNameDisplayMode option) {
    this.option = option;
  }

  @Override
  public String getPurpose() {
    return "Emit extra information allow chrome dev tools to display Java identifiers in many" +
        " places instead of JavaScript functions.";
  }

  @Override
  public String getTag() {
    return "-XmethodNameDisplayMode";
  }

  @Override
  public boolean isExperimental() {
    return true;
  }

  @Override
  public boolean setString(String value) {
    Mode methodNameDisplayMode =
        Mode.valueOf(value.toUpperCase());
    if (methodNameDisplayMode == null) {
      return false;
    }
    option.setMethodNameDisplayMode(methodNameDisplayMode);
    return true;
  }

  @Override
  public String[] getTagArgs() {
    return new String[] { Joiner.on(" | ").join(Mode.values()) };
  }
}
