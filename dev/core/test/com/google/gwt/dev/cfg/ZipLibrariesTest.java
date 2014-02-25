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

import com.google.gwt.dev.cfg.Libraries.IncompatibleLibraryVersionException;
import com.google.gwt.dev.javac.CompilationUnit;
import com.google.gwt.dev.javac.CompiledClass;
import com.google.gwt.dev.javac.MockCompilationUnit;
import com.google.gwt.dev.javac.MockCompiledClass;
import com.google.gwt.dev.javac.testing.impl.MockResource;
import com.google.gwt.dev.util.Util;
import com.google.gwt.thirdparty.guava.common.collect.Lists;
import com.google.gwt.thirdparty.guava.common.collect.Sets;

import junit.framework.TestCase;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Set;

/**
 * Tests for ZipLibrary and ZipLibraryWriter.
 */
public class ZipLibrariesTest extends TestCase {

  private static class SimpleMockResource extends MockResource {

    public SimpleMockResource(String path) {
      super(path);
    }

    @Override
    public CharSequence getContent() {
      return "";
    }
  }

  public void testRoundTrip() throws IOException, IncompatibleLibraryVersionException {
    File zipFile = File.createTempFile("Test", ".gwtlib");
    zipFile.deleteOnExit();

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
        new MockCompilationUnit("com.google.gwt.lang.RuntimeRebinder", "blah");
    MockCompilationUnit expectedSuperSourceCompilationUnit = new MockCompilationUnit(
        "com.google.gwt.lang.SuperRuntimeRebinder", "superblah") {
      @Override
      public boolean isSuperSource() {
        return true;
      }
    };
    MockCompilationUnit expectedNestedTypeCompilationUnit =
        new MockCompilationUnit("com.google.gwt.user.Outer", "superblah") {
            @Override
          public Collection<CompiledClass> getCompiledClasses() {
            MockCompiledClass outerCompiledClass = new MockCompiledClass(null,
                "com/google/gwt/user/Outer", "com.google.gwt.user.Outer");
            MockCompiledClass innerCompiledClass = new MockCompiledClass(outerCompiledClass,
                "com/google/gwt/user/Outer$Inner", "com.google.gwt.user.Outer.Inner");
            return Lists.<CompiledClass> newArrayList(outerCompiledClass, innerCompiledClass);
          }
        };

    // Put data in the library and save it.
    ZipLibraryWriter zipLibraryWriter = new ZipLibraryWriter(zipFile.getPath());
    zipLibraryWriter.setLibraryName(expectedLibraryName);
    // Include unusual path characters.
    zipLibraryWriter.addPublicResource(new SimpleMockResource("ui:binder:com.foo.baz.TableView"));
    // Include specific expected contents.
    zipLibraryWriter.addPublicResource(new MockResource("index.html") {
        @Override
      public CharSequence getContent() {
        return expectedResourceContents;
      }
    });
    zipLibraryWriter.addNewConfigurationPropertyValuesByName(
        "user.agent", expectedUserAgentConfigurationValues);
    zipLibraryWriter.addNewConfigurationPropertyValuesByName(
        "locale", expectedLocaleConfigurationValues);
    for (String generatorName : expectedRanGeneratorNames) {
      zipLibraryWriter.addRanGeneratorName(generatorName);
    }
    zipLibraryWriter.addDependencyLibraryNames(expectedDependencyLibraryNames);
    zipLibraryWriter.addCompilationUnit(expectedCompilationUnit);
    zipLibraryWriter.addCompilationUnit(expectedSuperSourceCompilationUnit);
    zipLibraryWriter.addCompilationUnit(expectedNestedTypeCompilationUnit);
    zipLibraryWriter.write();

    // Read data back from disk.
    ZipLibrary zipLibrary = new ZipLibrary(zipFile.getPath());
    CompilationUnit actualCompilationUnit =
        zipLibrary.getCompilationUnitByTypeSourceName("com.google.gwt.lang.RuntimeRebinder");
    CompilationUnit actualSuperSourceCompilationUnit =
        zipLibrary.getCompilationUnitByTypeSourceName("com.google.gwt.lang.SuperRuntimeRebinder");

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
    // CompilationUnit
    assertEquals(
        expectedCompilationUnit.getResourceLocation(), actualCompilationUnit.getResourceLocation());
    assertEquals(expectedCompilationUnit.getTypeName(), actualCompilationUnit.getTypeName());
    // SuperSourceCompilationUnit
    assertEquals(expectedSuperSourceCompilationUnit.getResourceLocation(),
        actualSuperSourceCompilationUnit.getResourceLocation());
    assertEquals(expectedSuperSourceCompilationUnit.getTypeName(),
        actualSuperSourceCompilationUnit.getTypeName());
    assertTrue(zipLibrary.getNestedNamesByCompilationUnitName().get("com.google.gwt.user.Outer")
        .contains("com.google.gwt.user.Outer.Inner"));
  }

  public void testVersionNumberException() throws IOException {
    File zipFile = File.createTempFile("Test", ".gwtlib");
    zipFile.deleteOnExit();

    // Put data in the library and save it.
    ZipLibraryWriter zipLibraryWriter = new ZipLibraryWriter(zipFile.getPath());
    zipLibraryWriter.setLibraryName("BazLib");
    zipLibraryWriter.write();

    // Change the expected version number so that this next read should fail.
    ZipLibraries.versionNumber++;

    // Read data back from disk.
    try {
      new ZipLibrary(zipFile.getPath());
      fail("Expected zip library initialization to fail with a version "
          + "mismatch, but it didn't fail.");
    } catch (IncompatibleLibraryVersionException e) {
      // Expected behavior.
    }
  }
}
