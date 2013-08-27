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
package com.google.gwt.dev.javac;

import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.dev.javac.testing.impl.JavaResourceBase;
import com.google.gwt.dev.resource.Resource;
import com.google.gwt.dev.util.Strings;
import com.google.gwt.dev.util.arg.SourceLevel;
import com.google.gwt.thirdparty.guava.common.collect.Lists;

import junit.framework.TestCase;

import org.eclipse.jdt.core.compiler.CategorizedProblem;

import java.util.Collection;
import java.util.List;

/**
 * Base class for all JdtCompiler tests.
 */
public abstract class JdtCompilerTestBase extends TestCase {
  protected void assertResourcesCompileSuccessfully(Resource... resources)
      throws UnableToCompleteException {
    assertUnitsCompiled(compile(resources));
  }

  protected List<CompilationUnit> compile(Resource... resources) throws UnableToCompleteException {
    List<CompilationUnitBuilder> builders = buildersFor(resources);
    return compileImpl(builders);
  }

  /**
   * Invokes the JDT to compile a set of units given their CompilationUnitBuilders.
   *
   * Subclasses can override this method to provide custom comple options.
   */
  protected List<CompilationUnit> compileImpl(Collection<CompilationUnitBuilder> builders)
      throws UnableToCompleteException {
    return  JdtCompiler.compile(TreeLogger.NULL, builders);
  }

  protected static void addAll(Collection<CompilationUnitBuilder> units,
                               Resource... sourceFiles) {
    for (Resource sourceFile : sourceFiles) {
      units.add(CompilationUnitBuilder.create(sourceFile));
    }
  }

  protected static void assertOnlyLastUnitHasErrors(List<CompilationUnit> units,
      String... errorPatterns) {
    assertUnitsCompiled(units.subList(0, units.size() - 1));
    assertUnitHasErrors(units.get(units.size() - 1), errorPatterns);
  }

  protected static void assertUnitHasErrors(CompilationUnit unit, String... expectedErrors) {
    assertTrue(unit.isError());
    int j = 0;
    for (int i = 0; i < expectedErrors.length; i++, j++) {
      String expectedError = expectedErrors[i];
      for (; j < unit.getProblems().length && !unit.getProblems()[j].isError(); j++) {
        // skip warnings.
      }
      if (unit.getProblems().length <= j) {
        fail("No error for satisfying pattern " + expectedErrors[i]);
        continue;
      }
      CategorizedProblem problem = unit.getProblems()[j];
      assertTrue("Error message: \"" + problem.getMessage() + "\" does not match expected \""
          + expectedError + "\"", unit.getProblems()[j].getMessage().equals(expectedErrors[i]));
   }

    for (; j < unit.getProblems().length; j++) {
      CategorizedProblem problem = unit.getProblems()[j];
      if (problem.isError()) {
        fail("Unexpected error: " + problem.getMessage());
      }
    }
  }

  protected static void assertUnitsCompiled(Collection<CompilationUnit> units) {
    for (CompilationUnit unit : units) {
      if (unit.isError()) {
        String[] messages = new String[unit.getProblems().length];
        int i = 0;
        for (CategorizedProblem pb : unit.getProblems()) {
          messages[i] = pb.getMessage();
        }
        fail(Strings.join(messages, "\n"));
      }
      assertTrue(unit.getCompiledClasses().size() > 0);
    }
  }

  protected static List<CompilationUnitBuilder> buildersFor(Resource... resources) {
    List<CompilationUnitBuilder> builders = Lists.newArrayList();
    addAll(builders, JavaResourceBase.getStandardResources());
    addAll(builders, resources);
    return builders;
  }
}
