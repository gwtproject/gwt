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

  public static native void createGwtOnLoad(JavaScriptObject registerRuntimeRebindsFunc,
      JavaScriptObject registerPropertyProvidersFunc, JavaScriptObject previousGwtOnLoad
      /*, initFunction1, initFunction2, ... */) /*-{
    if (registerRuntimeRebindsFunc) {
      registerRuntimeRebindsFunc();
    }

    if (registerPropertyProvidersFunc) {
      registerPropertyProvidersFunc();
    }

    // Init funcitons are passed as paramters after previousGwtOnLoad.
    var initFunctions = [];
    for (i = 3; i < arguments.length; i++) {
      initFunctions.push(arguments[i]);
    }

    // Redeclare gwtOnLoad so that it first calls the previously defined gwtOnLoad (from some
    // dependent module) and then performs the initialization of the module.
    // Assumes that gwtOnLoad is defined in the global scope.
    (function() {
      gwtOnLoad = function(errFn, modName, modBase, softPermutationId) {
        if (previousGwtOnLoad) {
          previousGwtOnLoad(errFn, modName, modBase, softPermutationId);
        }
        $moduleName = modName;
        $moduleBase = modBase;
        @CollapsedPropertyHolder::permutationId = softPermutationId;
        if (errFn) {
          try {
            for (i = 0; i < initFunctions.length; i++) {
              $entry(initFunctions[i])();
            }
          } catch(e) {
            errFn(modName, e);
          }
        } else {
          for (i = 0; i < initFunctions.length; i++) {
            $entry(initFunctions[i])();
          }
        }
      }
    })();
  }-*/;

  public static native JavaScriptObject nullIfUndefined(JavaScriptObject value) /*-{
    return value == null ? null : value;
  }-*/;

  private ModuleUtils() {
  }
}

