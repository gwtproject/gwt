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
package com.google.gwt.lang;

import com.google.gwt.core.client.JavaScriptObject;

/**
 * This is an intrinsic class that contains helper methods for module startup.
 * <p>
 * This class should contain only static methods or fields.
 */
public final class ModuleUtils {
  /**
   * Module startup glue.
   * <p>
   * This function is responsible for calling the module entry points.
   * <p>
   * {@link ModuleUtils#gwtOnLoad} will have a global alias gwtOnLoad called
   * to startup the application.
   */
  public static native void gwtOnLoad(JavaScriptObject errFn, JavaScriptObject modName,
      JavaScriptObject modBase, JavaScriptObject softPermutationId) /*-{
    var initFnList = @ModuleUtils::getInitFnList()();
    $moduleName = modName;
    $moduleBase = modBase;
    @CollapsedPropertyHolder::permutationId = softPermutationId;
    if (errFn) {
      try {
        for (i = 0; i < initFnList.length; i++) {
          $entry(initFnList[i])();
        }
      } catch(e) {
        errFn(modName, e);
      }
    } else {
      for (i = 0; i < initFnList.length; i++) {
        $entry(initFnList[i])();
      }
    }
  }-*/;

  /**
   * Adds entry points to call during gwtOnLoad, uses JS arguments.
   */
  public static native void addInitFunctions() /*-{
    var initFnList = @ModuleUtils::getInitFnList()();
    for (i = 0;  i < arguments.length; i++) {
      initFnList.push(arguments[i]);
    }
  }-*/;

  private static native JavaScriptObject getInitFnList() /*-{
    if (@ModuleUtils::initFnList == null ) {
      @ModuleUtils::initFnList = [];
    }
    return @ModuleUtils::initFnList;
   }-*/;

  private static JavaScriptObject initFnList;

  private ModuleUtils() {
  }
}

