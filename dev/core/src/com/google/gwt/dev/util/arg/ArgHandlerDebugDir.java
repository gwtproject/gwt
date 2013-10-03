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

import com.google.gwt.util.tools.ArgHandlerDir;

import java.io.File;

/**
 * Argument handler for processing the -debugSource option.
 */
public final class ArgHandlerDebugDir extends ArgHandlerDir {

  private final OptionDebugDir option;

  public ArgHandlerDebugDir(OptionDebugDir option) {
    this.option = option;
  }

  @Override
  public String getPurpose() {
    return "The directory or jar where the compiler writes source files useful to debuggers. "
        + "Default: not written.";
  }

  @Override
  public String getTag() {
    return "-debugSource";
  }

  @Override
  public void setDir(File dir) {
    option.setDebugDir(dir);
  }
}
