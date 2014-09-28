/*
 * Copyright 2014 Google Inc.
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


// These variables are used by property providers.
// (We also use them below.)
var $wnd = window;
var $doc = document;

/**
 * Construct an instance of the PropertyHelper.
 *
 * @constructor
 * @param {string} moduleName - the name of the gwt module
 * @param {Object} propertyProviders - A map of all property providers
 * @param {Object} propertyValues - A map of all possible property values
 */
function PropertyHelper(moduleName, propertyProviders, propertyValues) {
  this.moduleName = moduleName;
  this.propertyProviders = propertyProviders;
  this.propertyValues = propertyValues;
}

/**
 * Compute the binding property value for a given property name.
 *
 * @param {string} propName - the binding property name
 * @returns {string} the computed value of the binding property
 */
PropertyHelper.prototype.__computePropValue = function(propName) {
  var val = this.propertyProviders[propName]();
  // sanity check
  var allowedValuesMap = this.propertyValues[propName];
  if (val in allowedValuesMap) {
    return val;
  } else {
    // TODO(dankurka): trigger this error in the ui
    console.log("provider for " + propName + " returned unexpected value: '" + val + "'");
    throw "can't compute binding property value for " + propName;
  }
};

/**
 * Compute all binding properties for a given module.
 * @returns {Object} a map of from binding properties to their current values
 */
PropertyHelper.prototype.computeBindingProperties = function() {
  var result = {};
  for (var key in this.propertyValues) {
    if (values.hasOwnProperty(key)) {
      result[key] = this.__computePropValue(key);
    }
  }
  return result;
};

/**
 * Create a dialog.
 * @constructor
 */
function Dialog() {
  this.__dialog = $doc.createElement('div');
  this.__dialog.style.zIndex = 1000001;
  this.__dialog.style.position = 'fixed';
  this.__dialog.style.top = '20pt';
  this.__dialog.style.left = '20pt';
  this.__dialog.style.right = '20pt';
  this.__dialog.style.color = 'black';
  this.__dialog.style.background = 'white';
  this.__dialog.style.border = '4px solid #ccc';
  this.__dialog.style.padding = '1em';
  this.__dialog.style.borderRadius = '5px';
  this.__dialog.style.wordWrap = 'break-word';

  this.__overlay = $doc.createElement('div');
  this.__overlay.style.zIndex = 1000000;
  this.__overlay.style.position = 'absolute';
  this.__overlay.style.top = 0;
  this.__overlay.style.left = 0;
  this.__overlay.style.bottom = 0;
  this.__overlay.style.right = 0;
  this.__overlay.style.background = 'black'; // darken background
  this.__overlay.style.opacity = '0.5';
}

/**
 * Create a DOM node with a given text.
 * @returns {external:Node} the node with the given text.
 */
Dialog.prototype.createTextElement = function(tagName, fontSize, text) {
  var elt = $doc.createElement(tagName);
  elt.style.color = 'black';
  elt.style.background = 'white';
  elt.style.fontSize = fontSize;
  elt.appendChild($doc.createTextNode(text));
  return elt;
};

/**
 * Add a node to the dialog.
 * @param {external:Node} node - the node to add the the dialog
 */
Dialog.prototype.add = function(node) {
  this.__dialog.appendChild(node);
};

/**
 * Remove all children from the dialog.
 */
Dialog.prototype.clear = function() {
  while (this.__dialog.firstChild) {
    this.__dialog.removeChild(this.__dialog.firstChild);
  }
};

/**
 * Show the dialog (add it to the body).
 */
Dialog.prototype.show = function() {
  var body = $doc.getElementsByTagName('body')[0];
  body.appendChild(this.__overlay);
  body.appendChild(this.__dialog);
};

/**
 * Hide the dialog (remove it from the body).
 */
Dialog.prototype.hide = function() {
  var body = $doc.getElementsByTagName('body')[0];
  body.removeChild(this.__overlay);
  body.removeChild(this.__dialog);
};

/**
 * Construct a Recompiler object.
 * @constructor
 * @param {string} moduleName - the name of the gwt module
 * @param {Object} permutationProperties - A map of binding property names to their values.
 * @returns
 */
function Recompiler(moduleName, permutationProperties) {
  $wnd.__$sdm__recompiler = $wnd.__$sdm__recompiler || {};
  $wnd.__$sdm__recompiler.counter = $wnd.__$sdm__recompiler.counter || 0;
  $wnd.__$sdm__recompiler.callbacks = $wnd.__$sdm__recompiler.callback || {};
  this.__globals = $wnd.__$sdm__recompiler;
  this.__moduleName = moduleName;
  this.__permutationProperties = permutationProperties;
  this.__compiling = false;
}

/**
 * Build the url that triggers a compile on the code server.
 * @returns {string} the url
 */
Recompiler.prototype.__buildCompileUrl = function() {
  var url = this.getCodeServerBaseUrl() + 'recompile/' + this.__moduleName + '?';
  var props = [];
  for (var key in this.__permutationProperties) {
    props.push($wnd.encodeURIComponent(key) + '=' +
        $wnd.encodeURIComponent(this.__permutationProperties[key]));
  }
  return url + props.join('&') + '&';
};

/**
 * Issue a compile request for the module of this Recompiler class.
 * @param {Function} finishCallback - the callback to invoke once compile finishes
 */
Recompiler.prototype.compile = function(finishCallback) {
  var that = this;
  this.__compiling = true;
  var compileUrl = this.__buildCompileUrl();
  this.__jsonp(compileUrl, function(data) {
    that.__compiling = false;
    finishCallback(data);
  });
};

/**
 * Issue a jsonp request.
 * @param {string} url - the url to use
 * @param {Function} callback - the callback to invoke
 */
Recompiler.prototype.__jsonp = function(url, callback) {
  var that = this;
  var callback_id = 'c' + this.__globals.counter++;
  this.__globals.callbacks[callback_id] = function(json) {
    delete that.__globals.callbacks[callback_id];
    callback(json);
  };

  var url = url + '_callback=__$sdm__recompiler.callbacks.' + callback_id;
  var script = $doc.createElement('script');
  script.src = url;
  $doc.getElementsByTagName('head')[0].appendChild(script);
};

/**
 * Get protocol and host from a url.
 * @param {string} url
 */
Recompiler.prototype.__getBaseUrl = function(url) {
  var a = $doc.createElement('a');
  a.href = url;
  return a.protocol + '//' + a.host + '/';
};

/**
 * Determine wheter the given url is the recompile.nochache.js
 * @param {string} url
 * @returns {boolean}
 */
Recompiler.prototype.__isRecompileNoCacheJs = function(url) {
  // Remove trailing query string and/or fragment
  url = url.split("?")[0].split("#")[0];
  var suffix = '/' + moduleName + '.recompilenocache.js';
  var startPos =  url.length - suffix.length;
  return url.indexOf(suffix, startPos) == startPos;
};

/**
 * Get the base url of the code server.
 * @returns {string} the url of the code server
 */
Recompiler.prototype.getCodeServerBaseUrl = function() {
  var scriptTagsToSearch = $doc.getElementsByTagName('script');
  for (var i = 0; ; i++) {
    var tag = scriptTagsToSearch[i];
    if (!tag) {
      break; // end of list; not found
    }
    if (tag && this.__isRecompileNoCacheJs(tag.src)) {
      return this.__getBaseUrl(tag.src);
    }
  }
  throw "unable to find the script tag that includes " + moduleName + "recompile.nocache.js";
};

/**
 * Load the compiled application
 */
Recompiler.prototype.loadApp = function() {
  var url = this.getCodeServerBaseUrl() + this.__moduleName + '/' + this.__moduleName + '.nocache.js';
  var scriptTag = $doc.createElement('script');
  scriptTag.src = url;
  var $head = $doc.head || $doc.getElementsByTagName('head')[0];
  $head.insertBefore(scriptTag, $head.firstElementChild || $head.children[0]);
};

Recompiler.prototype.getLogUrl = function() {
  return this.getCodeServerBaseUrl() + 'log/' + this.__moduleName;
};

/**
 * Construct the main class.
 *
 * @constructor
 * @param {string} moduleName
 * @param {Object} propertyProviders
 * @param {Object} propertyValues
 */
function Main(moduleName, propertyProviders, propertyValues){
  var propertyHelper = new PropertyHelper(moduleName, propertyProviders, propertyValues);
  this.__moduleName = moduleName;
  this.__dialog = new Dialog();
  this.__recompiler = new Recompiler(moduleName, propertyHelper.computeBindingProperties());
  // Publish a global variable to let others know that we have been loaded
  $wnd.__$sdm__recompiler = $wnd.__$sdm__recompiler || {};
  $wnd.__$sdm__recompiler.loaded = true;
}

/**
 * Compile the current gwt module.
 */
Main.prototype.compile = function() {
  var that = this;
  this.__dialog.clear();
  this.__dialog.add(this.__dialog.createTextElement("div", "12pt", "Compiling " + this.__moduleName));
  this.__dialog.show();
  this.__recompiler.compile(function(result) {
    that.__dialog.clear();
    if (result.status != 'ok') {
      that.__renderError(result);
    } else {
      that.__dialog.hide();
      that.__recompiler.loadApp();
    }
  });
};

/**
 * Render an error if compile failed.
 * @param {object} result - the jsonp object from the compile server.
 */
Main.prototype.__renderError = function(result) {
  var that = this;
  var link = this.__dialog.createTextElement('a', '16pt', result.status);
  link.setAttribute('href', this.__recompiler.getLogUrl());
  link.setAttribute('target', 'gwt_dev_mode_log');
  link.style.color = 'red';
  link.style.textDecoration = 'underline';
  this.__dialog.add(link);

  var button = this.__dialog.createTextElement('button', '12pt', 'Try Again');
  button.onclick = function() {
    that.compile();
  };
  button.style.marginLeft = '10px';
  this.__dialog.add(button);
};


new Main(moduleName, providers, values).compile();


