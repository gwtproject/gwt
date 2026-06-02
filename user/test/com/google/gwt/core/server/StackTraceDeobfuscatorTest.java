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
package com.google.gwt.core.server;

import junit.framework.TestCase;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Tests for {@link StackTraceDeobfuscator}.
 */
public class StackTraceDeobfuscatorTest extends TestCase {

  private static class RecordingDeobfuscator extends StackTraceDeobfuscator {
    final List<String> opened = new ArrayList<String>();

    @Override
    protected InputStream openInputStream(String fileName) throws IOException {
      opened.add(fileName);
      throw new IOException("no such resource: " + fileName);
    }
  }

  private static StackTraceElement[] trace() {
    return new StackTraceElement[] {new StackTraceElement("C", "m", "C.java", 1)};
  }

  public void testTraversalStrongNameIsNotUsedToBuildPath() {
    RecordingDeobfuscator d = new RecordingDeobfuscator();
    d.resymbolize(trace(), "../../../../../../etc/passwd");
    assertTrue("strong name with path separators must not reach openInputStream: " + d.opened,
        d.opened.isEmpty());
  }

  public void testValidStrongNameStillLoadsSymbolMap() {
    RecordingDeobfuscator d = new RecordingDeobfuscator();
    d.resymbolize(trace(), "0F2C4A6E8B1D3F5709ABCDEF12345678");
    assertEquals("0F2C4A6E8B1D3F5709ABCDEF12345678.symbolMap", d.opened.get(0));
  }
}
