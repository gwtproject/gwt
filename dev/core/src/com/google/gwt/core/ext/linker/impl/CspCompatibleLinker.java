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

package com.google.gwt.core.ext.linker.impl;

import com.google.gwt.core.ext.LinkerContext;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.linker.ArtifactSet;
import com.google.gwt.core.ext.linker.CompilationResult;
import com.google.gwt.core.ext.linker.LinkerOrder;
import com.google.gwt.core.ext.linker.Shardable;
import com.google.gwt.core.linker.CrossSiteIframeLinker;

/**
 * A linker that respects CSP (Content Security Policy) and
 * adds a script tag directly to the main document.
 */
@LinkerOrder(LinkerOrder.Order.PRIMARY)
@Shardable
public class CspCompatibleLinker extends CrossSiteIframeLinker {

  @Override
  public String getDescription() {
    return "csp_linker";
  }

  @Override
  protected String getJsInstallLocation(LinkerContext context) {
    return "com/google/gwt/core/ext/linker/impl/installLocationMainWindowCsp.js";
  }

  @Override
  protected String getJsInstallScript(LinkerContext context) {
    return "com/google/gwt/core/ext/linker/impl/installScriptDirect.js";
  }

  @Override
  protected boolean shouldInstallCode(LinkerContext context) {
    return false;
  }

  @Override
  protected String wrapPrimaryFragment(TreeLogger logger, LinkerContext context, String script,
                                       ArtifactSet artifacts, CompilationResult result) throws UnableToCompleteException {

    StringBuilder out = new StringBuilder();
    out.append("(function($wnd) {\n");
    out.append("window.$wnd = $wnd;\n");
    out.append(super.wrapPrimaryFragment(logger, context, script, artifacts, result));
    out.append("\n})(window);\n");
    return out.toString();
  }
}
