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

import com.google.gwt.util.tools.ArgHandlerFlag;

/**
 * Removes duplicate functions to shrink output.
 */
public final class ArgHandlerDisableRemoveDuplicateFunctions extends ArgHandlerFlag {

  private final OptionRemoveDuplicateFunctions option;

  public ArgHandlerDisableRemoveDuplicateFunctions(OptionRemoveDuplicateFunctions option) {
    this.option = option;

    addTagValue("-XdisableRemoveDuplicateFunctions", false);
  }

  @Override
  public String getPurposeSnippet() {
    return "Removing duplicate functions. Will interfere with stacktrace "
        + "deobfuscation and so is only honored when compiler.stackMode is set to strip.";
  }

  @Override
  public String getLabel() {
    return "removeDuplicateFunctions";
  }

  @Override
  public boolean isUndocumented() {
    return true;
  }

  @Override
  public boolean setFlag(boolean value) {
    option.setRemoveDuplicateFunctions(value);
    return true;
  }

  @Override
  public boolean isExperimental() {
    return true;
  }

  @Override
  public boolean getDefaultValue() {
    return option.shouldRemoveDuplicateFunctions();
  }
}
