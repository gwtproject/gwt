/*
 * Copyright 2012 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy
 * of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */

/**
 * @fileoverview Stub for non-compiled modules.
 *
 * This script forces the proper reload + compilation in Super Dev Mode.
 * Assumes "providers" and "values are already defined.
 * This script will be wrapped in a function.
 */
var scriptLocation;
var getPropMap;

var key = '__gwtDevModeHook:' + moduleName;
if (window.sessionStorage[key]) {

  // already on
  scriptLocation = window.sessionStorage[key];
  getPropMap = null; // dev_mode_on.js will get it from __gwt_activeModules.

} else {
  // Someone included nocache.js directly from the codeserver.

  // Find out own location. This script will be replaced after the compile is finished.
  $wnd.__gwt_activeModules = $wnd.__gwt_activeModules || {};
  $wnd.__gwt_activeModules[moduleName] = $wnd.__gwt_activeModules[moduleName] || {};
  var scriptBase = computeScriptBase();
  if (!scriptBase) {
    return; // not found; already alerted.
  }
  scriptLocation = scriptBase + "/" + moduleName + ".nocache.js";

  // There is no original app so we need to calculate the binding properties.
  function computePropValue(propName){
    var val = providers[propName]();
    // sanity check
    var allowedValuesMap = values[propName];
    if (val in allowedValuesMap) {
      return val;
    } else {
      console.log("provider for " + propName + " returned unexpected value: '" + val + "'");
      throw "can't compute binding property value for " + propName;
    }
  }

  getPropMap = function () {
    var result = {};
    for (var key in values) {
      if (values.hasOwnProperty(key)) {
        result[key] = computePropValue(key);
      }
    }
    return result;
  };
}

// Get the Super Dev Mode Server URL: use the HTML a.href parsing.
var a = document.createElement('a');
a.href = scriptLocation;
var devServerUrl = a.protocol + '//' + a.host;

// Load the bookmarklet.
window.__gwt_bookmarklet_params = {
  'server_url' : devServerUrl + '/',
  'module_name': moduleName,
  'getPropMap': getPropMap
};
var script = document.createElement('script');
script.src = devServerUrl + '/dev_mode_on.js';
document.getElementsByTagName('head')[0].appendChild(script);
