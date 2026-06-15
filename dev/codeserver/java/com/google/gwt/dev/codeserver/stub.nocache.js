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
 * This startup script is used when we run superdevmode from an app server.
 */
(function($wnd, $doc){
  // Compute some codeserver urls so as the user does not need bookmarklets
  var hostName = $wnd.location.hostname;
  var module = '__MODULE_NAME__';

  // The default Super Dev Mode server url. The user may override it (and retry)
  // through the prompt shown by the error handler below; the override is kept
  // for the session so the reload picks up the new server.
  var serverUrlKey = '__gwt_sdm__server_url';
  var defaultServerUrl = 'http://' + hostName + ':__SUPERDEV_PORT__';
  var serverUrl = defaultServerUrl;
  try {
    serverUrl = $wnd.sessionStorage.getItem(serverUrlKey) || defaultServerUrl;
  } catch (e) {
    // sessionStorage may be unavailable; fall back to the default url.
  }
  var nocacheUrl = serverUrl + '/recompile-requester/' + module;

  // Insert the superdevmode nocache script in the first position of the head
  var devModeScript = $doc.createElement('script');
  devModeScript.src = nocacheUrl;

  // Everybody except IE8 does fire an error event
  // This means that we do not detect a non running SDM with IE8.
  if (devModeScript.addEventListener) {
    var callback = function() {
      // Don't show the prompt twice (multimodule)
      if (!$wnd.__gwt__sdm__confirmed &&
           (!$wnd.__gwt_sdm__recompiler || !$wnd.__gwt_sdm__recompiler.loaded)) {
        $wnd.__gwt__sdm__confirmed = true;
        var newServerUrl = $wnd.prompt(
            "Couldn't load " +  module + " from Super Dev Mode\n" +
            "server at " + serverUrl + ".\n" +
            "Please make sure this server is ready.\n" +
            "Press OK to try again:",
            serverUrl);
        if (newServerUrl) {
          try {
            $wnd.sessionStorage.setItem(serverUrlKey, newServerUrl);
          } catch (e) {
            // Without storage the reload falls back to the default url.
          }
          $wnd.location.reload();
        }
      }
    };
    devModeScript.addEventListener("error", callback, true);
  }

  var injectScriptTag = function(){
    $doc.head.insertBefore(devModeScript, $doc.head.firstElementChild || $doc.head.children[0]);
  };

  if (/loaded|complete/.test($doc.readyState)) {
    injectScriptTag();
  } else {
    //defer app script insertion until the body is ready
    if($wnd.addEventListener){
      $wnd.addEventListener('load', injectScriptTag, false);
    } else{
      $wnd.attachEvent('onload', injectScriptTag);
    }
  }
})(window, document);
