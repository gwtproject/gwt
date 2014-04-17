/*
 * Copyright 2014 Google Inc.
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
package com.google.gwt.dev;

import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.dev.cfg.ModuleDef;
import com.google.gwt.dev.cfg.RuntimeRebindRuleGenerator;
import com.google.gwt.dev.resource.Resource;
import com.google.gwt.thirdparty.guava.common.base.Function;
import com.google.gwt.thirdparty.guava.common.collect.Iterables;
import com.google.gwt.thirdparty.guava.common.collect.Lists;
import com.google.gwt.thirdparty.guava.common.collect.Sets;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import javax.annotation.Nullable;

/**
 * Represents a module in a module tree, knows how to build that module into an output library and
 * checks output library freshness.
 */
public class BuildTarget {

  enum OutputFreshness {
    FRESH, STALE, UNKNOWN;
  }

  private static final Function<BuildTarget, String> LIBRARY_PATH_FUNCTION =
      new Function<BuildTarget, String>() {
          @Override
        public String apply(@Nullable BuildTarget buildTarget) {
          return buildTarget.computeLibraryPath();
        }
      };

  // VisibleForTesting
  static String formatCompilingModuleMessage(String canonicalModuleName) {
    return "Compiling module " + canonicalModuleName;
  }

  // VisibleForTesting
  static String formatReusingCachedLibraryMessage(String canonicalModuleName) {
    return "Reusing cached library for " + canonicalModuleName;
  }

  private final BuildTargetOptions buildTargetOptions;
  private final String canonicalModuleName;
  private final List<BuildTarget> dependencyBuildTargets;
  private ModuleDef module;
  private OutputFreshness outputFreshness = OutputFreshness.UNKNOWN;
  private Set<BuildTarget> transitiveDependencyBuildTargets;

  BuildTarget(String canonicalModuleName, BuildTargetOptions buildTargetOptions,
      BuildTarget... dependencyBuildTargets) {
    this.canonicalModuleName = canonicalModuleName;
    this.buildTargetOptions = buildTargetOptions;
    this.dependencyBuildTargets = Arrays.asList(dependencyBuildTargets);
  }

  void computeOutputFreshness(TreeLogger logger) {
    if (outputFreshness != OutputFreshness.UNKNOWN) {
      return;
    }

    for (BuildTarget dependencyBuildTarget : dependencyBuildTargets) {
      dependencyBuildTarget.computeOutputFreshness(logger);
    }

    if (module == null) {
      logger.log(TreeLogger.SPAM,
          "library " + canonicalModuleName + " is stale: the module hasn't been loaded yet");
      outputFreshness = OutputFreshness.STALE;
      return;
    }

    for (BuildTarget dependencyBuildTarget : dependencyBuildTargets) {
      if (dependencyBuildTarget.outputFreshness == OutputFreshness.STALE) {
        logger.log(TreeLogger.SPAM,
            "library " + canonicalModuleName + " is stale: has a stale dependency");
        outputFreshness = OutputFreshness.STALE;
        return;
      }
    }

    File libraryFile = new File(computeLibraryPath());
    if (!libraryFile.exists()) {
      logger.log(TreeLogger.SPAM,
          "library " + canonicalModuleName + " is stale: the library file is missing");
      outputFreshness = OutputFreshness.STALE;
      return;
    }
    long libraryFileLastModified = libraryFile.lastModified();
    module.refresh();
    if (libraryFileLastModified < module.getResourceLastModified()) {
      Set<Resource> newerResources = module.getResourcesNewerThan(libraryFileLastModified);
      TreeLogger branch = logger.branch(TreeLogger.SPAM,
          "library " + canonicalModuleName + " is stale: library is older than some resource(s)");
      for (Resource newerResource : newerResources) {
        branch.log(TreeLogger.SPAM, newerResource.getPath() + " has changed");
      }
      outputFreshness = OutputFreshness.STALE;
      return;
    }

    logger.log(TreeLogger.SPAM, "library " + canonicalModuleName + " is fresh");
    outputFreshness = OutputFreshness.FRESH;
  }

  String getCanonicalModuleName() {
    return canonicalModuleName;
  }

  List<BuildTarget> getDependencyBuildTargets() {
    return dependencyBuildTargets;
  }

  boolean isOutputFresh() {
    return outputFreshness == OutputFreshness.FRESH;
  }

  boolean link(TreeLogger logger) {
    return build(logger, true);
  }

  void setModule(ModuleDef module) {
    this.module = module;
  }

  void setOutputFreshness(OutputFreshness outputFreshness) {
    this.outputFreshness = outputFreshness;
  }

  private boolean build(TreeLogger logger) {
    return build(logger, false);
  }

  private boolean build(TreeLogger logger, boolean link) {
    if (outputFreshness == OutputFreshness.FRESH) {
      logger.log(TreeLogger.SPAM, formatReusingCachedLibraryMessage(canonicalModuleName));
      return true;
    }

    // Build all my dependencies before myself.
    for (BuildTarget dependencyBuildTarget : dependencyBuildTargets) {
      // If any dependency fails to build.
      if (!dependencyBuildTarget.build(logger)) {
        // Then I have failed to build as well.
        return false;
      }
    }

    TreeLogger branch =
        logger.branch(TreeLogger.INFO, formatCompilingModuleMessage(canonicalModuleName));
    boolean buildSucceeded;
    try {
      RuntimeRebindRuleGenerator.RUNTIME_REBIND_RULE_SOURCES_BY_SHORT_NAME.clear();
      LibraryCompiler libraryCompiler = new LibraryCompiler(computeCompileOptions(link));
      libraryCompiler.setResourceLoader(buildTargetOptions.getResourceLoader());
      buildSucceeded = libraryCompiler.run(branch);
      module = libraryCompiler.getModule();
    } catch (Throwable t) {
      logger.log(TreeLogger.ERROR, t.getMessage());
      return false;
    }
    outputFreshness = OutputFreshness.FRESH;
    return buildSucceeded;
  }

  private CompilerOptions computeCompileOptions(boolean link) {
    CompilerOptions compilerOptions = new CompilerOptionsImpl();
    // Must compile the canonical name, not name, since after module-renames there may be more
    // than one module in the classpath with the same name and we don't want to find and recompile
    // the same one over and over.
    compilerOptions.setModuleNames(Lists.newArrayList(canonicalModuleName));
    compilerOptions.setLink(link);
    compilerOptions.setLogLevel(TreeLogger.ERROR);
    compilerOptions.setGenDir(new File(buildTargetOptions.getGenDir()));
    compilerOptions.setWorkDir(new File(buildTargetOptions.getOutputDir()));
    compilerOptions.setLibraryPaths(Lists.newArrayList(
        Iterables.transform(getTransitiveDependencyBuildTargets(), LIBRARY_PATH_FUNCTION)));
    compilerOptions.setFinalProperties(buildTargetOptions.getFinalProperties());

    if (!link) {
      compilerOptions.setOutputLibraryPath(computeLibraryPath());
    } else {
      compilerOptions.setWarDir(new File(buildTargetOptions.getWarDir()));
    }
    return compilerOptions;
  }

  private String computeLibraryPath() {
    return buildTargetOptions.getOutputDir() + "/" + canonicalModuleName + ".gwtlib";
  }

  private Set<BuildTarget> getTransitiveDependencyBuildTargets() {
    if (transitiveDependencyBuildTargets == null) {
      transitiveDependencyBuildTargets = Sets.newHashSet();
      transitiveDependencyBuildTargets.addAll(dependencyBuildTargets);
      for (BuildTarget buildTarget : dependencyBuildTargets) {
        transitiveDependencyBuildTargets.addAll(buildTarget.getTransitiveDependencyBuildTargets());
      }
    }
    return transitiveDependencyBuildTargets;
  }
}
