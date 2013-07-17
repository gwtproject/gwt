/*
 * Copyright 2013 Google Inc.
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
package com.google.web.bindery.requestfactory.gwt.rebind;

import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.dev.cfg.ModuleDef;
import com.google.gwt.dev.cfg.ModuleDefLoader;

import com.google.gwt.dev.javac.typemodel.JClassType;
import com.google.gwt.dev.javac.typemodel.TypeOracle;
import com.google.gwt.dev.util.log.PrintWriterTreeLogger;
import com.google.web.bindery.requestfactory.gwt.rebind.model.RequestMethod;

import junit.framework.TestCase;

import java.io.PrintWriter;

/**
 * Tests for {@link RequestFactoryGenerator}.
 */
public class RequestFactoryGeneratorTest extends TestCase {

  private TreeLogger logger;
  private TypeOracle oracle;
  private RequestFactoryGenerator generator;

  @Override
  public void setUp() throws Exception {
    super.setUp();
    logger = createCompileLogger();
    ModuleDef moduleDef = ModuleDefLoader.loadFromClassPath(
        logger, "com.google.web.bindery.requestfactory.gwt.rebind.RequestFactoryGeneratorTest");
    oracle = moduleDef.getCompilationState(logger).getTypeOracle();
    generator = new RequestFactoryGenerator();
  }

  private static TreeLogger createCompileLogger() {
    PrintWriterTreeLogger logger = new PrintWriterTreeLogger(new PrintWriter(
        System.err, true));
    logger.setMaxDetail(TreeLogger.ERROR);
    return logger;
  }

  public void testEnumWorks() {
    JClassType clazz = oracle.findType(
        "com.google.web.bindery.requestfactory.gwt.rebind.sources.Example1");
    assertNotNull(clazz);
    RequestMethod.Builder method = new RequestMethod.Builder();
    method.setDeclarationMethod(clazz, clazz.getMethods()[0]);
    assertEquals(1, generator.findExtraEnums(method.build()).size());
  }
}

