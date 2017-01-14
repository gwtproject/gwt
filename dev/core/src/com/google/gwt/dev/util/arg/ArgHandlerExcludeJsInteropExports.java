/*
 * Copyright 2016 Google Inc.
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

import com.google.gwt.util.tools.ArgHandler;

/**
 * Add exclusion patterns to the generation of JsInterop exports.
 */
public class ArgHandlerExcludeJsInteropExports extends ArgHandler {
  private final OptionGenerateJsInteropExports options;

  public ArgHandlerExcludeJsInteropExports(OptionGenerateJsInteropExports options) {
    this.options = options;
  }
  @Override
  public String getPurpose() {
    return "Exclude exporting of matching classes for JsInterop purposes."
        + " Flag could be set multiple times to expand the pattern.";
  }

  @Override
  public String getTag() {
    return "-excludeJsInteropExports";
  }

  @Override
  public String[] getTagArgs() {
    return new String[] {"regex"};
  }

  @Override
  public int handle(String[] args, int startIndex) {
    if (startIndex + 1 < args.length) {
      String regex = "-" + args[startIndex + 1];
      try {
        options.getJsInteropExportFilter().add(regex);
        return 1;
      } catch (IllegalArgumentException e) {
        System.err.println("Not a valid regex\n" + e.getMessage());
        return -1;
      }
    }
    return -1;
  }
}
