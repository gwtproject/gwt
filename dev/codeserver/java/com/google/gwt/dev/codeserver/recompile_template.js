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
(function(){
  var $wnd = window;
  var $doc = $wnd.document;
  var $namespace = {};
  var moduleName = __MODULE_NAME__;

  __LIB_JS__

  // Because GWT linker architecture allows property providers to use global variables
  // we need to make sure that these are defined in the scope of property providers.
  // The parameters of the propertyProvidersHolder needs to be kept in sync with the
  // parameters being passed in PropertyHelper.__getProvidersAndValues.
  // TODO(dankurka): refactor linkers and templates to not use global symbols anymore.
  var propertyProvidersHolder = function(__gwt_getMetaProperty, __gwt_isKnownPropertyValue){
    __PROPERTY_PROVIDERS__
    return {values: values, providers: providers};
  };

  var executeMain = function() {
    __MAIN__
  };

  if (/loaded|complete/.test($doc.readyState)) {
    executeMain();
  } else {
    //defer app script insertion until the body is ready
    if($wnd.addEventListener){
      $wnd.addEventListener('load', executeMain, false);
    } else{
      $wnd.attachEvent('onload', executeMain);
    }
  }
})();
