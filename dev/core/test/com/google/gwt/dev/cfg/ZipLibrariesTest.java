/*
 * Copyright 2013 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package com.google.gwt.dev.cfg;

import com.google.gwt.dev.javac.CompilationUnit;
import com.google.gwt.dev.javac.MockCompilationUnit;
import com.google.gwt.dev.javac.testing.impl.MockResource;
import com.google.gwt.dev.util.Util;
import com.google.gwt.thirdparty.guava.common.collect.Sets;

import junit.framework.TestCase;

import java.io.File;
import java.io.IOException;
import java.util.Set;

/**
 * Tests for ZipLibrary and ZipLibraryBuilder.
 */
public class ZipLibrariesTest extends TestCase {

  public void testRoundTrip() throws IOException {
    File zipLibraryFile = File.createTempFile("Test", ".gwtlib");
    zipLibraryFile.deleteOnExit();

    // Data
    String expectedLibraryName = "BazLib";
    final String expectedResourceContents =
        "<html><head><title>Index</title></head><body>Hi</body></html>";
    Set<String> expectedRanGeneratorNames =
        Sets.newHashSet("UiBinderGenerator", "PlatinumGenerator");
    Set<String> expectedUserAgentConfigurationValues = Sets.newHashSet("webkit");
    Set<String> expectedLocaleConfigurationValues = Sets.newHashSet("en", "fr");
    Set<String> expectedDependencyLibraryNames = Sets.newHashSet("FooLib", "BarLib");
    MockCompilationUnit expectedCompilationUnit =
        new MockCompilationUnit("com.google.gwt.core.client.RuntimeRebinder", "blah");

    // Put data in the library and save it.
    ZipLibraryBuilder zipLibraryBuilder = new ZipLibraryBuilder(zipLibraryFile.getPath());
    zipLibraryBuilder.setLibraryName(expectedLibraryName);
    zipLibraryBuilder.addPublicResource(new MockResource("index.html") {
        @Override
      public CharSequence getContent() {
        return expectedResourceContents;
      }
    });
    zipLibraryBuilder.addNewConfigurationPropertyValuesByName(
        "user.agent", expectedUserAgentConfigurationValues);
    zipLibraryBuilder.addNewConfigurationPropertyValuesByName(
        "locale", expectedLocaleConfigurationValues);
    for (String generatorName : expectedRanGeneratorNames) {
      zipLibraryBuilder.addRanGeneratorName(generatorName);
    }
    zipLibraryBuilder.addDependencyLibraryNames(expectedDependencyLibraryNames);
    zipLibraryBuilder.addCompilationUnit(expectedCompilationUnit);
    zipLibraryBuilder.write();

    // Read data back from disk.
    ZipLibrary zipLibrary = new ZipLibrary(zipLibraryFile.getPath());
    CompilationUnit actualCompilationUnit =
        zipLibrary.getCompilationUnitByTypeName("com.google.gwt.core.client.RuntimeRebinder");

    // Compare it.
    assertEquals(expectedLibraryName, zipLibrary.getLibraryName());
    assertEquals(expectedResourceContents,
        Util.readStreamAsString(zipLibrary.getPublicResourceByPath("index.html").openContents()));
    assertEquals(expectedRanGeneratorNames, zipLibrary.getRanGeneratorNames());
    assertEquals(expectedUserAgentConfigurationValues,
        zipLibrary.getNewConfigurationPropertyValuesByName().get("user.agent"));
    assertEquals(expectedLocaleConfigurationValues,
        zipLibrary.getNewConfigurationPropertyValuesByName().get("locale"));
    assertEquals(expectedDependencyLibraryNames, zipLibrary.getDependencyLibraryNames());
    assertEquals(
        expectedCompilationUnit.getResourceLocation(), actualCompilationUnit.getResourceLocation());
    assertEquals(expectedCompilationUnit.getTypeName(), actualCompilationUnit.getTypeName());
  }
}
