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
 *
 * The main goal is to avoid installing bookmarkers for host:port/module
 * to load and recompile the application.
 */
(function($wnd, $doc){
  // This script supports IE8+
  if (!('sessionStorage' in $wnd)) {
    $wnd.alert('Unable to load Super Dev Mode version of __MODULE_NAME__ because this browser does not support sessionStorage');
    return;
  }

  //We don't import properties.js so we have to update active modules here
  $wnd.__gwt_activeModules = $wnd.__gwt_activeModules || [];
  $wnd.__gwt_activeModules['__MODULE_NAME__'] = {
    'moduleName' : '__MODULE_NAME__',
    'bindings' : function() {
      return {};
    }
  };

  // Reuse compute script base
  __COMPUTE_SCRIPT_BASE__;

  // document.head does not exist in IE8
  var $head = $doc.head || $doc.getElementsByTagName('head')[0];

  // Quick way to compute the user.agent.
  // This makes the first compilation run faster, for other browsers
  // we compile all permutations.
  var ua = $wnd.navigator.userAgent.toLowerCase();
  ua = /webkit/.test(ua)? 'safari' : /gecko/.test(ua)? 'gecko1_8' :
    (n = /msie (8|9|10)/.exec(ua)) ? 'ie' + n[1] : '';

  // We use a different key for each module so that we can turn on dev mode
  // independently for each.
  var devModeHookKey = '__gwtDevModeHook:__MODULE_NAME__';
  var devModeSessionKey = '__gwtDevModeSession:__MODULE_NAME__';

  // Compute some codeserver urls so as the user does not need bookmarkers
  var hostName = $wnd.location.hostname;
  var codsrvUrl = 'http://' + hostName + ':__SUPERDEV_PORT__';
  var codsrvNocacheUrl = codsrvUrl + '/__MODULE_NAME__/__MODULE_NAME__.nocache.js';
  // appending timestamp to avoid cache issues in IE
  var codsrvCompileUrl = codsrvUrl +
      '/recompile/__MODULE_NAME__?user.agent=' + ua +
      "&_callback=_compile_callback&" + new Date().getTime();

  // Save supder-devmode url in session
  $wnd.sessionStorage[devModeHookKey] = codsrvNocacheUrl;
  // Save user.agent in session
  if (ua) {
    $wnd.sessionStorage[devModeSessionKey] = 'user.agent=' + ua + '&';
  }

  // Set bookmarklet params in window
  $wnd.__gwt_bookmarklet_params = {'server_url': codsrvUrl};
  // Save the original module base. (Returned by GWT.getModuleBaseURL.)
  $wnd[devModeHookKey + ':moduleBase'] = computeScriptBase();

  // Needed in the real nocache.js logic
  $wnd.__gwt_activeModules['__MODULE_NAME__'].canRedirect = true;
  $wnd.__gwt_activeModules['__MODULE_NAME__'].superdevmode = true;

  // Insert the superdevmode nocache script in the first position of the head
  var devModeScript = $doc.createElement('script');
  devModeScript.src = codsrvNocacheUrl;

  // Show a link in a corner for recompiling the app.
  // The user can remove this: .gwt-sdm {display:none}
  var compileDiv = $doc.createElement('div');
  compileDiv.className = 'gwt-sdm compile';
  compileDiv.innerHTML = '<div></div>';
  compileDiv.title = 'Compile module: __MODULE_NAME__';
  // Use CSS so the app could change button style
  var compileStyle = $doc.createElement('style');
  $head.appendChild(compileStyle);
  compileStyle.language = 'text/css';
  var css =
    ".gwt-sdm{" +
      "position:fixed;" +
      "right:3px;" +
      "bottom:3px;" +
      "font-family:arial;" +
      "font-size:1.8em;" +
      "cursor:pointer;" +
      "color:#B62323;" +
      "text-shadow:grey 1px 1px 3px;" +
      "z-index:2147483646;" +
      "white-space:nowrap;" +
    "}" +
    ".gwt-sdm div{" +
      "position:absolute;" +
      "right:2px;" +
      "bottom:-3px;" +
      "font-size:0.3em;" +
      "opacity:1;" +
    "}" +
    ".gwt-sdm.compile:before{" +
      "content:'\u21bb';" +
    "}" +
    ".gwt-sdm.compile div:before{" +
      "content:'GWT';" +
    "}" +
    ".gwt-sdm.compiling div:before{" +
      "content:'COMPILING __MODULE_NAME__ ... ';" +
    "}" +
    ".gwt-sdm.error div:before{" +
      "content:'FAILED';" +
    "}" +
    ".gwt-sdm.compiling div{" +
      "font-size:0.5em;" +
    "}";
  if ('styleSheet' in compileStyle) {
    compileStyle.styleSheet.cssText = css;
  } else {
    compileStyle.appendChild($doc.createTextNode(css));
  }

  compileDiv.onclick = function() {
    compile();
  };

  // defer so as the body is ready
  setTimeout(function(){
    $head.insertBefore(devModeScript, $head.firstElementChild || $head.children[0]);
    $doc.body.appendChild(compileDiv);
  }, 1);

  // Compile this module
  function compile() {
    compileDiv.className = 'gwt-sdm compiling';
    // Insert the jsonp script to compile
    var compileScript = $doc.createElement('script');
    compileScript.src = codsrvCompileUrl;
    $head.appendChild(compileScript);
  }

  // Compile callback
  $wnd._compile_callback = function(r) {
    if (r && r.status && r.status == 'ok') {
      $wnd.location.reload();
      return;
    }
    compileDiv.className = 'gwt-sdm compile error';
  };

  // Run this block after the app has been loaded.
  setTimeout(function(){
    // Maintaining the hook key in session can cause problems
    // if we try to run classic code server so we remove it
    // after a while.
    $wnd.sessionStorage.removeItem(devModeHookKey);

    // Re-attach compile button, sometimes app clears the dom
    $doc.body.appendChild(compileDiv);
  }, 2000);

  function keyHandler(e) {
    if (e.ctrlKey && e.keyCode == 89) {
      compile();
    }
  }

  // Configure Ctrl-Y for recompiling the app
  if ($doc.attachEvent) {
    $doc.attachEvent('onkeyup', keyHandler);
  } else {
    $doc.addEventListener('keyup', keyHandler, false);
  }
})(window, document);
