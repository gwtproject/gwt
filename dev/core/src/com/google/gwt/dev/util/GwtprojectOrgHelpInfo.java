/*
 * Copyright 2025 GWT Project Authors
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
package com.google.gwt.dev.util;

import com.google.gwt.core.ext.TreeLogger;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Reference to a help document on the GWT project website.
 */
public class GwtprojectOrgHelpInfo extends TreeLogger.HelpInfo {
  private URL url;

  public GwtprojectOrgHelpInfo(String relativeUrl) {
    try {
      url = new URL("https://gwtproject.org" + relativeUrl);
    } catch (MalformedURLException ignored) {
      // ignore, url will be null
    }
  }

  @Override
  public URL getURL() {
    return url;
  }
}
