/*
 * Copyright 2008 Google Inc.
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
import com.google.gwt.core.ext.typeinfo.TypeOracle;
import com.google.gwt.dev.CompilerContext;
import com.google.gwt.dev.cfg.LibraryGroup;
import com.google.gwt.dev.cfg.MockLibrary;
import com.google.gwt.dev.javac.testing.impl.JavaResourceBase;
import com.google.gwt.dev.resource.Resource;
import com.google.gwt.thirdparty.guava.common.collect.Lists;
import com.google.gwt.thirdparty.guava.common.collect.Sets;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Utilities for tests that build a type oracle and watch for errors.
 *
 */
public class TypeOracleTestingUtils {

  public static CompilationState buildCompilationState(TreeLogger logger, Set<Resource> resources,
      Set<GeneratedUnit> generatedUnits) {
    try {
      CompilationState state =
          CompilationStateBuilder.buildFrom(logger, new CompilerContext(), resources);
      state.addGeneratedCompilationUnits(logger, generatedUnits);
      return state;
    } catch (UnableToCompleteException e) {
      throw new RuntimeException(e);
    }
  }

  public static CompilationState buildStandardCompilationStateWith(TreeLogger logger,
      Resource... resources) {
    return buildCompilationState(logger, standardBuildersPlus(new HashSet<Resource>(Arrays
        .asList(resources))), Collections.<GeneratedUnit> emptySet());
  }

  public static CompilationUnitTypeOracleUpdater buildUpdater(
      TreeLogger logger, Set<Resource> resources, Set<GeneratedUnit> generatedUnits) {
    return buildCompilationState(logger, resources, generatedUnits).getTypeOracleUpdater();
  }

  public static CompilationUnitTypeOracleUpdater buildUpdaterWith(
      TreeLogger logger, Set<Resource> resources) {
    return buildUpdater(logger, resources, Collections.<GeneratedUnit> emptySet());
  }

  public static CompilationUnitTypeOracleUpdater buildStandardUpdaterWith(
      TreeLogger logger, Resource... resources) {
    return buildStandardUpdaterWith(logger, new HashSet<Resource>(Arrays.asList(resources)));
  }

  public static CompilationUnitTypeOracleUpdater buildStandardUpdaterWith(
      TreeLogger logger, Set<Resource> resources) {
    return buildUpdaterWith(logger, standardBuildersPlus(resources));
  }

  public static TypeOracle buildStandardTypeOracleWith(TreeLogger logger,
      Resource... resources) {
    return buildStandardTypeOracleWith(logger, new HashSet<Resource>(
        Arrays.asList(resources)));
  }

  public static TypeOracle buildStandardTypeOracleWith(TreeLogger logger,
      Set<Resource> resources) {
    return buildTypeOracle(logger, standardBuildersPlus(resources));
  }

  public static TypeOracle buildStandardTypeOracleWith(TreeLogger logger,
      Set<Resource> resources, Set<GeneratedUnit> generatedUnits) {
    return buildTypeOracle(logger, standardBuildersPlus(resources),
        generatedUnits);
  }

  public static TypeOracle buildLibraryTypeOracle(TreeLogger logger, Set<Resource> resources) {
    return buildLibraryTypeOracle(logger, resources, Collections.<GeneratedUnit> emptySet());
  }

  public static TypeOracle buildLibraryTypeOracle(TreeLogger logger, Set<Resource> resources,
      Set<GeneratedUnit> generatedUnits) {
    try {
      // Compile resources to compilation units and place in an old library.
      CompilerContext oldCompilerContext = new CompilerContext();
      oldCompilerContext.getOptions().setStrict(true);
      CompilationState oldState =
          CompilationStateBuilder.buildFrom(logger, oldCompilerContext, resources);
      oldState.addGeneratedCompilationUnits(logger, generatedUnits);
      MockLibrary oldLibrary = new MockLibrary("OldLib");
      Collection<CompilationUnit> oldCompilationUnits =
          Lists.newArrayList(oldState.getCompilationUnits());
      CompilationUnitInvalidator.retainValidUnits(logger, oldCompilationUnits,
          oldState.getValidClasses(), new CompilationErrorsIndexImpl());
      for (CompilationUnit compilationUnit : oldCompilationUnits) {
        oldLibrary.addCompilationUnit(compilationUnit);
      }

      // Create and return a LibraryTypeOracle that doesn't have any compilation units loaded but
      // which can lazy load all of them out of a previously compiled library.
      LibraryGroup libraryGroup = LibraryGroup.fromLibraries(Lists.newArrayList(oldLibrary), false);
      CompilerContext newCompilerContext =
          new CompilerContext.Builder().compileMonolithic(false).libraryGroup(libraryGroup).build();
      newCompilerContext.getOptions().setStrict(true);
      CompilationState state = CompilationStateBuilder.buildFrom(logger, newCompilerContext,
          Sets.<Resource> newHashSet());
      return state.getTypeOracle();
    } catch (UnableToCompleteException e) {
      throw new RuntimeException(e);
    }
  }

  public static TypeOracle buildTypeOracle(TreeLogger logger,
      Set<Resource> resources) {
    return buildTypeOracle(logger, resources,
        Collections.<GeneratedUnit> emptySet());
  }

  public static TypeOracle buildTypeOracle(TreeLogger logger,
      Set<Resource> resources, Set<GeneratedUnit> generatedUnits) {
    try {
      CompilerContext compilerContext = new CompilerContext();
      compilerContext.getOptions().setStrict(true);
      CompilationState state =
          CompilationStateBuilder.buildFrom(logger, compilerContext, resources);
      state.addGeneratedCompilationUnits(logger, generatedUnits);
      return state.getTypeOracle();
    } catch (UnableToCompleteException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Compilation resources for basic classes like Object and String.
   */
  private static Set<Resource> standardBuildersPlus(Set<Resource> resources) {
    Set<Resource> result = new HashSet<Resource>();
    for (Resource standardResource : JavaResourceBase.getStandardResources()) {
      result.add(standardResource);
    }
    result.addAll(resources);
    return result;
  }
}
