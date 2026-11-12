/*
 * Copyright 2026 GWT Project Authors
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
package com.google.gwt.dev.jjs.test;

import com.google.gwt.junit.DoNotRunWith;
import com.google.gwt.junit.Platform;

/**
 * Tests GWT.runAsync with the cross-site iframe linker and installCode=false.
 * Fragments must run from the script tags that load them, so installing their
 * code as an inline script, which a strict Content-Security-Policy blocks,
 * fails the tests.
 */
@DoNotRunWith(Platform.Devel)
public class CrossSiteIframeDirectInstallRunAsyncTest extends RunAsyncTest {
  @Override
  public String getModuleName() {
    return "com.google.gwt.dev.jjs.CompilerSuiteCrossSiteIframeDirectInstall";
  }

  @Override
  protected void gwtSetUp() throws Exception {
    forbidInlineScriptInstall();
  }

  private static native void forbidInlineScriptInstall() /*-{
    __gwtModuleFunction.__installRunAsyncCode = function(code) {
      throw new Error("runAsync fragment was installed as an inline script");
    };
  }-*/;
}
