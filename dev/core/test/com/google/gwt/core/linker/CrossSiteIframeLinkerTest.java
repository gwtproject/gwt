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
package com.google.gwt.core.linker;

import com.google.gwt.core.ext.LinkerContext;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.linker.ArtifactSet;
import com.google.gwt.core.ext.linker.ConfigurationProperty;
import com.google.gwt.core.ext.linker.SelectionProperty;
import com.google.gwt.core.linker.SymbolMapsLinker.ScriptFragmentEditsArtifact;

import junit.framework.TestCase;

import java.util.SortedSet;
import java.util.TreeSet;

/**
 * Tests the deferred fragments written by {@link CrossSiteIframeLinker}.
 */
public class CrossSiteIframeLinkerTest extends TestCase {

  private static class MockLinkerContext implements LinkerContext {

    @Override
    public SortedSet<ConfigurationProperty> getConfigurationProperties() {
      return new TreeSet<ConfigurationProperty>();
    }

    @Override
    public String getModuleFunctionName() {
      return "mockFunc";
    }

    @Override
    public long getModuleLastModified() {
      return 0;
    }

    @Override
    public String getModuleName() {
      return "mock";
    }

    @Override
    public SortedSet<SelectionProperty> getProperties() {
      return new TreeSet<SelectionProperty>();
    }

    @Override
    public boolean isOutputCompact() {
      return false;
    }

    @Override
    public String optimizeJavaScript(TreeLogger logger, String jsProgram) {
      return jsProgram;
    }
  }

  private static class TestDirectInstallLinker extends DirectInstallLinker {
    String deferredFragment(int fragment, String js, ArtifactSet artifacts)
        throws UnableToCompleteException {
      return generateDeferredFragment(TreeLogger.NULL, new MockLinkerContext(), fragment, js,
          artifacts, null);
    }
  }

  public void testDeferredFragmentRunsDirectlyWithoutInstallCode()
      throws UnableToCompleteException {
    ArtifactSet artifacts = new ArtifactSet();
    String code = "var fragmentCode = 1;";

    String out = new TestDirectInstallLinker().deferredFragment(2, code + "\n", artifacts);

    assertTrue(out, out.startsWith("$wnd.mockFunc.runAsyncCallback2(null);\n" + code + "\n"));
    assertFalse("fragment code must not be passed as a string", out.contains("\"" + code));

    // The source map of the fragment has to be shifted by exactly the lines in
    // front of its code.
    SortedSet<ScriptFragmentEditsArtifact> edits =
        artifacts.find(ScriptFragmentEditsArtifact.class);
    assertEquals(1, edits.size());
    assertEquals(2, edits.first().getFragment());
    String[] lines = out.split("\n", -1);
    assertEquals(code, lines[edits.first().getPrefixLineCount()]);
  }
}
