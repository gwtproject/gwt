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

import com.google.gwt.core.ext.linker.ArtifactSet;
import com.google.gwt.core.linker.SymbolMapsLinker.ScriptFragmentEditsArtifact;

import junit.framework.TestCase;

import java.util.SortedSet;

/**
 * Tests {@link SymbolMapsLinker.ScriptFragmentEditsArtifact}.
 */
public class SymbolMapsLinkerTest extends TestCase {

  public void testScriptFragmentEditsKeptPerFragment() {
    ArtifactSet set = new ArtifactSet();
    set.add(new ScriptFragmentEditsArtifact("strong", 0));
    set.add(new ScriptFragmentEditsArtifact("strong", 1));
    set.add(new ScriptFragmentEditsArtifact("other", 1));
    set.add(new ScriptFragmentEditsArtifact(null, 0));

    SortedSet<ScriptFragmentEditsArtifact> found = set.find(ScriptFragmentEditsArtifact.class);
    assertEquals(4, found.size());
  }

  public void testPrefixLineCount() {
    ScriptFragmentEditsArtifact edits = new ScriptFragmentEditsArtifact("strong", 1);
    assertEquals(0, edits.getPrefixLineCount());
    edits.prefixLines("a\nb\n");
    edits.prefixLines("c\n");
    assertEquals(3, edits.getPrefixLineCount());
  }
}
