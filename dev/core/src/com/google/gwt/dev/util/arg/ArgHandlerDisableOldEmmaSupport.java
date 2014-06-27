/*
 * Copyright 2007 Google Inc.
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

import com.google.gwt.util.tools.ArgHandlerFlag;

/**
 * Disable special treatment for the old Emma library.<br />
 *
 * This specific treatment is incompatible with the way the new Emma
 * library is implemented. Impacts only the DevMode and the
 * JUnitShell in htmlunit mode.
 */
public final class ArgHandlerDisableOldEmmaSupport extends ArgHandlerFlag {

  private final OptionDisableOldEmmaSupport disableOldEmmaSupportOption;

  public <T extends OptionDisableOldEmmaSupport>
  ArgHandlerDisableOldEmmaSupport(T option) {
    this.disableOldEmmaSupportOption = option;

    addTagValue("-XdisableOldEmmaSupport", true);
  }

  @Override
  public String getPurposeSnippet() {
    return "Tells the JUnit or Dev mode whether support for the old Emma library "
        + "should be disabled.";
  }

  @Override
  public String getLabel() {
    return "disableOldEmmaSupport";
  }

  @Override
  public boolean isUndocumented() {
    return false;
  }

  @Override
  public boolean setFlag(boolean value) {
    disableOldEmmaSupportOption.setDisableOldEmmaSupport(value);

    return true;
  }

  @Override
  public boolean isExperimental() {
    return false;
  }

  @Override
  public boolean getDefaultValue() {
    return disableOldEmmaSupportOption.shouldDisableOldEmmaSupport();
  }
}
