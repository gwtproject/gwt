/*
 * Copyright 2026 Google Inc.
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

import com.google.gwt.thirdparty.guava.common.io.MoreFiles;
import com.google.gwt.thirdparty.guava.common.io.RecursiveDeleteOption;

import junit.framework.TestCase;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

/**
 * Tests for {@link LauncherDir}.
 */
public class LauncherDirTest extends TestCase {

  public void testPathToPublicResourceAllowsNestedResource() throws IOException {
    File moduleOutputDir = Files.createTempDirectory("launcherdir").toFile();
    try {
      File resolved = LauncherDir.pathToPublicResource(moduleOutputDir, "img/../img/logo.png");
      assertEquals(new File(moduleOutputDir, "img/logo.png").getCanonicalFile(), resolved);
    } finally {
      MoreFiles.deleteRecursively(moduleOutputDir.toPath(), RecursiveDeleteOption.ALLOW_INSECURE);
    }
  }

  public void testPathToPublicResourceRejectsEscapingResource() throws IOException {
    File moduleOutputDir = Files.createTempDirectory("launcherdir").toFile();
    try {
      String outsideName = moduleOutputDir.getName() + "-outside";

      try {
        LauncherDir.pathToPublicResource(moduleOutputDir, "../" + outsideName);
        fail("Expected public resource path escaping the module output directory to be rejected");
      } catch (IOException expected) {
        assertTrue(expected.getMessage().contains("escapes module output directory"));
      }

      assertFalse(new File(moduleOutputDir.getParentFile(), outsideName).exists());
    } finally {
      MoreFiles.deleteRecursively(moduleOutputDir.toPath(), RecursiveDeleteOption.ALLOW_INSECURE);
    }
  }
}
