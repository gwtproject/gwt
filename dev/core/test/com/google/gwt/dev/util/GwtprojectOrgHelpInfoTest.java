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

import com.google.gwt.dev.About;

import junit.framework.TestCase;

import java.net.URL;

public class GwtprojectOrgHelpInfoTest extends TestCase {

    private static final String BASE =
            "https://gwtproject.org/doc/latest/gwt-dev-help/";

    public void testNoHashAddsHashAndVersion() {
        String version = About.getGwtVersion();

        GwtprojectOrgHelpInfo info =
                new GwtprojectOrgHelpInfo("docs/getting-started");

        URL url = info.getURL();
        assertNotNull(url);
        assertEquals(
                BASE + "docs/getting-started#" + version,
                url.toString());
    }

    public void testExistingFragmentAppendsVersionAsPath() {
        String version = About.getGwtVersion();

        GwtprojectOrgHelpInfo info =
                new GwtprojectOrgHelpInfo("docs#intro");

        URL url = info.getURL();
        assertNotNull(url);
        assertEquals(
                BASE + "docs#intro/" + version,
                url.toString());
    }

    public void testNestedFragmentAppendsVersionAsPath() {
        String version = About.getGwtVersion();

        GwtprojectOrgHelpInfo info =
                new GwtprojectOrgHelpInfo("docs#main/section");

        URL url = info.getURL();
        assertNotNull(url);
        assertEquals(
                BASE + "docs#main/section/" + version,
                url.toString());
    }

    public void testEmptyFragmentAddsVersion() {
        String version = About.getGwtVersion();

        GwtprojectOrgHelpInfo info =
                new GwtprojectOrgHelpInfo("docs#");

        URL url = info.getURL();
        assertNotNull(url);
        assertEquals(
                BASE + "docs#" + version,
                url.toString());
    }
}
