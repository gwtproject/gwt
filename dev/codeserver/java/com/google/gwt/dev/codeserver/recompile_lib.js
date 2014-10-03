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

// Export into our name space
// We do not consider any of these classes a public API and they will be changed as needed.
$namespace.lib = $namespace.lib || {};

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
    // IE8 only has console defined if its dev tools have been opened before
    if ($wnd.console && $wnd.console.log) {
      $wnd.console.log("provider for " + propName
          + " returned unexpected value: '" + val + "'");
    }
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
    if (this.propertyValues.hasOwnProperty(key)) {
      result[key] = this.__computePropValue(key);
    }
  }
  return result;
};

// Export PropertyHelper to namespace
$namespace.lib.PropertyHelper = PropertyHelper;

/**
 * Create a dialog.
 * @constructor
 */
function Dialog() {
  var dialog = $doc.createElement('div');
  dialog.style.zIndex = 1000001;
  dialog.style.position = 'fixed';
  dialog.style.top = '20pt';
  dialog.style.left = '20pt';
  dialog.style.right = '20pt';
  dialog.style.color = 'black';
  dialog.style.background = 'white';
  dialog.style.border = '4px solid #ccc';
  dialog.style.padding = '1em';
  dialog.style.borderRadius = '5px';
  dialog.style.wordWrap = 'break-word';
  this.__dialog = dialog;

  var overlay = $doc.createElement('div');
  overlay.style.zIndex = 1000000;
  overlay.style.position = 'absolute';
  overlay.style.top = 0;
  overlay.style.left = 0;
  overlay.style.bottom = 0;
  overlay.style.right = 0;
  overlay.style.background = 'black'; // darken background
  overlay.style.opacity = '0.5';
  this.__overlay = overlay;
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
  $doc.body.appendChild(this.__overlay);
  $doc.body.appendChild(this.__dialog);
};

/**
 * Hide the dialog (remove it from the body).
 */
Dialog.prototype.hide = function() {
  $doc.body.removeChild(this.__overlay);
  $doc.body.removeChild(this.__dialog);
};

//Export Dialog to namespace
$namespace.lib.Dialog = Dialog;

/**
 * Construct a Recompiler object.
 * @constructor
 * @param {string} moduleName - the name of the gwt module
 * @param {Object} permutationProperties - A map of binding property names (string) to their values (string).
 * @returns
 */
function Recompiler(moduleName, permutationProperties) {
  if ($wnd.__gwt_sdm_globals) {
    this.__globals = $wnd.__gwt_sdm_globals;
  } else {
    this.__globals = {
      callbackCounter: new Date().getTime(), // avoid cache hits
      callbacks: {}
    };
    $wnd.__gwt_sdm_globals = this.__globals;
  }
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
  return url + props.join('&');
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

  var url = url + '&_callback=__gwt_sdm_globals.callbacks.' + callback_id;
  var script = $doc.createElement('script');
  script.src = url;
  var $head = $doc.head || $doc.getElementsByTagName('head')[0];
  $head.appendChild(script);
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
  var suffix = '/' + moduleName + '.recompile.nocache.js';
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
  throw "unable to find the script tag that includes " + moduleName + ".recompile.nocache.js";
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

//Export Recompiler to namespace
$namespace.lib.Recompiler = Recompiler;

function MetaTagParser(moduleName) {
  this.__parsed = false;
  this.__metaProperties = null;
  this.__moduleName = moduleName;
}

MetaTagParser.prototype.__getMetaTags = function() {
  return $doc.getElementsByTagName('meta');
};

MetaTagParser.prototype.__parse = function() {
  var metaProps = {};
  var metas = this.__getMetaTags();

  for (var i = 0, n = metas.length; i < n; ++i) {
    var meta = metas[i];
    var name = meta.getAttribute('name');
    var content = meta.getAttribute('content');

    if (name) {
      name = name.replace(this.__moduleName + '::', '');
      if (name.indexOf('::') >= 0) {
        // It's for a different module
        continue;
      }
    }
    if (name == 'gwt:property' && content) {
      var value;
      var eq = content.indexOf('=');
      if (eq >= 0) {
        name = content.substring(0, eq);
        value = content.substring(eq+1);
      } else {
        name = content;
        value = '';
      }
      metaProps[name] = value;
    }
  }
  return metaProps;
};

MetaTagParser.prototype.get = function() {
  if (!this.__parsed) {
    this.__metaProperties = this.__parse();
    this.__parsed = true;
  }
  return this.__metaProperties;
};

//Export MetaTagParser to namespace
$namespace.lib.MetaTagParser = MetaTagParser;

/**
 * BaseUrlProvider provides the url to the original server that the app has been loaded from.
 * This is not the url of super dev mode.
 *
 * @constructor
 * @param {string} moduleName - the module for which we should determine the base url.
 */
function BaseUrlProvider(moduleName) {
  this.__moduleName = moduleName;
}

BaseUrlProvider.prototype.__getScriptTags = function() {
  return $doc.getElementsByTagName('script');
};

BaseUrlProvider.prototype.getBaseUrl = function() {
  var expectedSuffix = this.__moduleName + '.nocache.js';
  var scriptTags = this.__getScriptTags();
  for (var i = 0;; i++) {
    var tag = scriptTags[i];
    if (!tag) {
      break;
    }
    var candidate = tag.src;
    var lastMatch = candidate.lastIndexOf(expectedSuffix);
    if (lastMatch == candidate.length - expectedSuffix.length) {
      // Assumes that either the URL is absolute, or it's relative
      // and the html file is hosted by this code server.
      return candidate.substring(0, lastMatch);
    }
  }

  throw 'Unable to compute base url for module: ' + this.__moduleName;
};

//Export BaseUrlProvider to namespace
$namespace.lib.BaseUrlProvider = BaseUrlProvider;
