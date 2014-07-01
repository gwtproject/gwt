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

/**
 * This startup script is used when we run superdevmode from a hosted server.
 */

// Because we don't import properties.js, we set some needed variables.
$wnd = window;
$doc = document;
$wnd.__gwt_activeModules = [];
$wnd.__gwt_activeModules['__MODULE_NAME__'] = {
  'moduleName' : '__MODULE_NAME__',
  'bindings' : function() {
    return {};
  }
}

// Reuse compute script base
__COMPUTE_SCRIPT_BASE__

if ('sessionStorage' in $wnd) {
  // We use a different key for each module so that we can turn on dev mode
  // independently for each.
  var devModeKey = '__gwtDevModeHook:__MODULE_NAME__';

  // Compute the superdevmode url, so as the user does not need bookmarks
  var hostName = $wnd.location.hostname;
  var devModeUrl = 'http://' + hostName + ':__SUPERDEV_PORT__/__MODULE_NAME__/__MODULE_NAME__.nocache.js';

  // Save supderdevmode url in session
  $wnd.sessionStorage[devModeKey] = devModeUrl;
  // Save the original module base. (Returned by GWT.getModuleBaseURL.)
  $wnd[devModeKey + ':moduleBase'] = computeScriptBase();
  // Needed in the real nocache.js logic
  $wnd.__gwt_activeModules['__MODULE_NAME__'].canRedirect = true;
  $wnd.__gwt_activeModules['__MODULE_NAME__'].superdevmode = true;

  // Insert the superdevmode nocache script in the first position of head
  var devModeScript = $doc.createElement('script');
  devModeScript.src = devModeUrl;
  var head = $doc.getElementsByTagName('head')[0];
  head.insertBefore(devModeScript, head.firstElementChild || head.children[0]);
} else {
  $wnd.alert('Unable to load Super Dev Mode version of __MODULE_NAME__ because this browser does not support sessionStorage');
}
