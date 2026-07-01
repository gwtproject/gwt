/*
 * Copyright 2024 Google Inc.
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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * Test for {@link StackTraceDeobfuscator}.
 */
public class StackTraceDeobfuscatorTest extends TestCase {

  /**
   * Records every file name that reaches {@link #openInputStream} and serves data from an in-memory
   * map, so the test can observe what the strong name resolves to.
   */
  private static class RecordingDeobfuscator extends StackTraceDeobfuscator {
    final Map<String, String> files = new HashMap<String, String>();
    String lastRequested;

    @Override
    protected InputStream openInputStream(String fileName) throws IOException {
      lastRequested = fileName;
      String data = files.get(fileName);
      if (data == null) {
        throw new IOException("Missing resource: " + fileName);
      }
      return new ByteArrayInputStream(data.getBytes(StandardCharsets.UTF_8));
    }
  }

  private static StackTraceElement element(String method) {
    return new StackTraceElement("Foo", method, "Foo.java", 1);
  }

  public void testValidStrongNameStillResolves() {
    RecordingDeobfuscator d = new RecordingDeobfuscator();
    d.files.put("ABCDEF0123.symbolMap",
        "a,@com.example.Foo::bar()V,bar,Foo.java,42,0\n");

    d.resymbolize(new StackTraceElement[] {element("a")}, "ABCDEF0123");

    assertEquals("ABCDEF0123.symbolMap", d.lastRequested);
  }

  public void testTraversingStrongNameIsRejected() {
    RecordingDeobfuscator d = new RecordingDeobfuscator();
    StackTraceElement original = element("a");

    StackTraceElement[] result =
        d.resymbolize(new StackTraceElement[] {original}, "../../etc/passwd");

    // The malicious name must never be turned into a file request.
    assertNull(d.lastRequested);
    // Deobfuscation is a best-effort no-op for an unusable strong name.
    assertEquals(original, result[0]);
  }
}
