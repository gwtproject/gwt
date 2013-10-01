/*
 * Copyright 2010 Google Inc.
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
package com.google.gwt.dev.cfg;

import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.dev.CompilerContext;
import com.google.gwt.dev.resource.Resource;
import com.google.gwt.dev.util.UnitTestTreeLogger;

import junit.framework.TestCase;

/**
 * Test for the module def loading
 */
public class ModuleDefLoaderTest extends TestCase {

  private CompilerContext compilerContext = new CompilerContext();

  public void assertHonorsStrictResources(boolean strictResources)
      throws UnableToCompleteException {
    TreeLogger logger = TreeLogger.NULL;
    compilerContext.getOptions().setEnforceStrictResources(strictResources);
    ModuleDefLoader.getModulesCache().clear();
    ModuleDef emptyModule = ModuleDefLoader.loadFromClassPath(
        logger, "com.google.gwt.dev.cfg.testdata.merging.Empty", compilerContext, false);
    Resource sourceFile =
        emptyModule.findSourceFile("com/google/gwt/dev/cfg/testdata/merging/client/InOne.java");
    Resource publicFile = emptyModule.findPublicFile("Public.java");
    if (strictResources) {
      // Empty.gwt.xml did not register any source or public paths and the strictResource setting is
      // blocking the implicit addition of any default entries. So these resource searches should
      // fail.
      assertNull(sourceFile);
      assertNull(publicFile);
    } else {
      assertNotNull(sourceFile);
      assertNotNull(publicFile);
    }
  }

  public void testAllowsImpreciseResources() throws Exception {
    assertHonorsStrictResources(false);
  }

  public void testRequiresStrictResources() throws Exception {
    assertHonorsStrictResources(true);
  }

  /**
   * Test of merging multiple modules in the same package space.
   * This exercises the interaction of include, exclude, and skip attributes.
   */
  public void testModuleMerging() throws Exception {
    TreeLogger logger = TreeLogger.NULL;
    ModuleDef one = ModuleDefLoader.loadFromClassPath(logger,
        "com.google.gwt.dev.cfg.testdata.merging.One", compilerContext, false);
    assertNotNull(one.findSourceFile("com/google/gwt/dev/cfg/testdata/merging/client/InOne.java"));
    assertNotNull(one.findSourceFile("com/google/gwt/dev/cfg/testdata/merging/client/Shared.java"));
    assertNull(one.findSourceFile("com/google/gwt/dev/cfg/testdata/merging/client/InTwo.java"));
    assertNull(one.findSourceFile("com/google/gwt/dev/cfg/testdata/merging/client/Toxic.java"));

    ModuleDef two = ModuleDefLoader.loadFromClassPath(logger,
        "com.google.gwt.dev.cfg.testdata.merging.Two", compilerContext, false);
    assertNotNull(two.findSourceFile("com/google/gwt/dev/cfg/testdata/merging/client/InOne.java"));
    assertNotNull(two.findSourceFile("com/google/gwt/dev/cfg/testdata/merging/client/Shared.java"));
    assertNotNull(two.findSourceFile("com/google/gwt/dev/cfg/testdata/merging/client/InTwo.java"));
    assertNull(two.findSourceFile("com/google/gwt/dev/cfg/testdata/merging/client/Toxic.java"));

    ModuleDef three = ModuleDefLoader.loadFromClassPath(logger,
        "com.google.gwt.dev.cfg.testdata.merging.Three", compilerContext, false);
    assertNotNull(three.findSourceFile("com/google/gwt/dev/cfg/testdata/merging/client/InOne.java"));
    assertNotNull(three.findSourceFile("com/google/gwt/dev/cfg/testdata/merging/client/Shared.java"));
    assertNull(three.findSourceFile("com/google/gwt/dev/cfg/testdata/merging/client/InTwo.java"));
    assertNull(three.findSourceFile("com/google/gwt/dev/cfg/testdata/merging/client/Toxic.java"));
  }

  /**
   * The top level module has an invalid name.
   */
  public void testModuleNamingInvalid() {
    UnitTestTreeLogger.Builder builder = new UnitTestTreeLogger.Builder();
    builder.setLowestLogLevel(TreeLogger.ERROR);
    builder.expectError("Invalid module name: 'com.google.gwt.dev.cfg.testdata.naming.Invalid..Foo'", null);
    UnitTestTreeLogger logger = builder.createLogger();
    try {
      ModuleDefLoader.loadFromClassPath(logger,
          "com.google.gwt.dev.cfg.testdata.naming.Invalid..Foo", compilerContext, false);
      fail("Expected exception from invalid module name.");
    } catch (UnableToCompleteException expected) {
    }
    logger.assertLogEntriesContainExpected();
  }

  public void testModuleNamingValid() throws Exception {
    TreeLogger logger = TreeLogger.NULL;
    //TreeLogger logger = new PrintWriterTreeLogger();

    ModuleDef module;
    module = ModuleDefLoader.loadFromClassPath(logger,
        "com.google.gwt.dev.cfg.testdata.naming.Foo-test", compilerContext, false);
    assertNotNull(module.findSourceFile("com/google/gwt/dev/cfg/testdata/naming/client/Mock.java"));

    module = ModuleDefLoader.loadFromClassPath(logger,
        "com.google.gwt.dev.cfg.testdata.naming.7Foo", compilerContext, false);
    assertNotNull(module.findSourceFile("com/google/gwt/dev/cfg/testdata/naming/client/Mock.java"));

    module = ModuleDefLoader.loadFromClassPath(logger,
        "com.google.gwt.dev.cfg.testdata.naming.Nested7Foo", compilerContext, false);
    assertNotNull(module.findSourceFile("com/google/gwt/dev/cfg/testdata/naming/client/Mock.java"));

    module = ModuleDefLoader.loadFromClassPath(logger,
        "com.google.gwt.dev.cfg.testdata.naming.Nested7Foo", compilerContext, false);
    assertNotNull(module.findSourceFile("com/google/gwt/dev/cfg/testdata/naming/client/Mock.java"));
  }

  /**
   * The top level module has a valid name, but the inherited one does not.
   */
  public void testModuleNestedNamingInvalid() {
    UnitTestTreeLogger.Builder builder = new UnitTestTreeLogger.Builder();
    builder.setLowestLogLevel(TreeLogger.ERROR);
    builder.expectError("Invalid module name: 'com.google.gwt.dev.cfg.testdata.naming.Invalid..Foo'", null);
    UnitTestTreeLogger logger = builder.createLogger();
    try {
      ModuleDefLoader.loadFromClassPath(logger,
          "com.google.gwt.dev.cfg.testdata.naming.NestedInvalid", compilerContext, false);
      fail("Expected exception from invalid module name.");
    } catch (UnableToCompleteException expected) {
    }
    logger.assertLogEntriesContainExpected();
  }
}
