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
package com.google.gwt.dev.codeserver;

import com.google.gwt.core.client.ScriptInjector;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.junit.client.GWTTestCase;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.TextResource;

/**
 * A GwtTestCase for the JavaScript that is part of super dev mode.
 *
 * Since inside of the GWT SDK there is not a lot of JavaScript it does not make sense to add a
 * extra testing framework for JavaScript. Rather this class bundles the JavaScript that should
 * be tested as a TextResource and injects it into the current page. This way we can write test
 * against it using JSNI.
 */
public class CodeServerGwtTest extends GWTTestCase {

  interface Resource extends ClientBundle {
    Resource INSTANCE = GWT.create(Resource.class);
    @Source("lib.js")
    TextResource recompileNocacheJs();
  }

  private boolean injected;

  @Override
  public String getModuleName() {
    return "com.google.gwt.dev.codeserver.CodeServerTest";
  }

  @Override
  protected void gwtSetUp() throws Exception {
    ensureJsInjected();
  }

  public native void testPropertyHelper_withProperInput() /*-{
    // setup property providers and values for the test
    var mocks = (function(){
      var propProviders = {};
      var propValues = {};
      propProviders['mgwt.density'] = function(){
        return 'mid';
      };

      propValues['mgwt.density'] = {'high':0,'mid':1,'xhigh':2};

      propProviders['mgwt.formfactor'] = function(){
        return 'desktop';
      };

      propValues['mgwt.formfactor'] = {'desktop':0,'phone':1,'tablet':2};
      return {provider: propProviders, values : propValues};
    })();

    // Actual test
    var PropertyHelper = $wnd.__gwt_sdm.lib.PropertyHelper;
    var propertyHelper = new PropertyHelper('testModule', mocks.provider, mocks.values);
    var result = propertyHelper.computeBindingProperties();

    var length = Object.keys(result).length;
    if (length != 2) {
      throw "PropertyHelper did not return two entries: " + length;
    }

    if (result['mgwt.formfactor'] != 'desktop') {
      throw "PropertyHelper did not contain right value for mgwt.formfactor " + result['mgwt.formfactor'];
    }

    if (result['mgwt.density'] != 'mid') {
      throw "PropertyHelper did not contain right value for mgwt.density " + result['mgwt.density'];
    }
  }-*/;

  public native void testRecompiler() /*-{
    var Recompiler = $wnd.__gwt_sdm.lib.Recompiler;
    var recompiler = new Recompiler('testModule', {prop1: 'val1', prop2 : 'val2'});

    var jsonpUrl = '';
    var callbackCalled = false;

    // patch up functions of recompiler that need the actual SDM environment
    recompiler.getCodeServerBaseUrl = function() {
      return "http://mytesthost:7812/";
    };

    recompiler.__jsonp = function(url, callback) {
      jsonpUrl = url;
      callback({status : 'ok'});
    };

    // do the test
    recompiler.compile(function(result) {
      callbackCalled = true;
      //compile is done
      if (result.status != 'ok') {
        throw 'result status was not ok ' + result.status;
      }

      if (jsonpUrl != 'http://mytesthost:7812/recompile/testModule?prop1=val1&prop2=val2') {
        throw 'wrong value for jsonpUrl ' + jsonpUrl;
      }
    });

    if (!callbackCalled) {
      throw 'callback for successful recompile was not executed';
    }
  }-*/;

  private void ensureJsInjected() {
    if(injected) {
      return;
    }
    String js = Resource.INSTANCE.recompileNocacheJs().getText();
    ScriptInjector.fromString(js).inject();
  }
}
