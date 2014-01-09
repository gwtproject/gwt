/*
 * Copyright 2014 Google Inc.
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
package com.google.gwt.dev;

import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.linker.ArtifactSet;
import com.google.gwt.dev.CompileTaskRunner.CompileTask;
import com.google.gwt.dev.CompilerContext.Builder;
import com.google.gwt.dev.cfg.Libraries.IncompatibleLibraryVersionException;
import com.google.gwt.dev.cfg.LibraryGroup;
import com.google.gwt.dev.cfg.ModuleDef;
import com.google.gwt.dev.cfg.ModuleDefLoader;
import com.google.gwt.dev.cfg.ZipLibraryWriter;
import com.google.gwt.dev.javac.LibraryGroupUnitCache;
import com.google.gwt.dev.jjs.PermutationResult;
import com.google.gwt.dev.shell.CheckForUpdates;
import com.google.gwt.dev.shell.CheckForUpdates.UpdateResult;
import com.google.gwt.dev.util.Memory;
import com.google.gwt.dev.util.PersistenceBackedObject;
import com.google.gwt.dev.util.Util;
import com.google.gwt.dev.util.arg.ArgHandlerDeployDir;
import com.google.gwt.dev.util.arg.ArgHandlerExtraDir;
import com.google.gwt.dev.util.arg.ArgHandlerLibraries;
import com.google.gwt.dev.util.arg.ArgHandlerLink;
import com.google.gwt.dev.util.arg.ArgHandlerLocalWorkers;
import com.google.gwt.dev.util.arg.ArgHandlerOutputLibrary;
import com.google.gwt.dev.util.arg.ArgHandlerSaveSourceOutput;
import com.google.gwt.dev.util.arg.ArgHandlerWarDir;
import com.google.gwt.dev.util.arg.ArgHandlerWorkDirOptional;
import com.google.gwt.dev.util.log.speedtracer.CompilerEventType;
import com.google.gwt.dev.util.log.speedtracer.SpeedTracerLogger;
import com.google.gwt.dev.util.log.speedtracer.SpeedTracerLogger.Event;
import com.google.gwt.thirdparty.guava.common.base.Preconditions;
import com.google.gwt.thirdparty.guava.common.base.Strings;
import com.google.gwt.thirdparty.guava.common.collect.Sets;
import com.google.gwt.util.tools.Utility;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.FutureTask;

/**
 * The executable entry point for modular GWT Java to JavaScript compilation.
 */
public class SeparateCompiler {

  static class ArgProcessor extends PrecompileTaskArgProcessor {
    public ArgProcessor(CompilerOptions options) {
      super(options);

      registerHandler(new ArgHandlerLocalWorkers(options));

      // Override the ArgHandlerWorkDirRequired in the super class.
      registerHandler(new ArgHandlerWorkDirOptional(options));
      registerHandler(new ArgHandlerLink(options));
      registerHandler(new ArgHandlerOutputLibrary(options));
      registerHandler(new ArgHandlerLibraries(options));

      registerHandler(new ArgHandlerWarDir(options));
      registerHandler(new ArgHandlerDeployDir(options));
      registerHandler(new ArgHandlerExtraDir(options));
      registerHandler(new ArgHandlerSaveSourceOutput(options));
    }

    @Override
    protected String getName() {
      return SeparateCompiler.class.getName();
    }
  }

  public static void main(String[] args) {
    Memory.initialize();
    SpeedTracerLogger.init();

    final CompilerOptions options = new CompilerOptionsImpl();
    if (new ArgProcessor(options).processArgs(args)) {
      CompileTask task = new CompileTask() {
        @Override
        public boolean run(TreeLogger logger) throws UnableToCompleteException {
          FutureTask<UpdateResult> updater = null;
          if (!options.isUpdateCheckDisabled()) {
            updater =
                CheckForUpdates.checkForUpdatesInBackgroundThread(logger, CheckForUpdates.ONE_DAY);
          }
          boolean success = new SeparateCompiler(options).run(logger);
          if (success) {
            CheckForUpdates.logUpdateAvailable(logger, updater);
          }
          return success;
        }
      };
      CompileTaskRunner.runWithAppropriateLogger(options, task);
    }
  }

  private CompilerContext compilerContext;
  private CompilerContext.Builder compilerContextBuilder;
  private final CompilerOptionsImpl options;

  public SeparateCompiler(CompilerOptions compilerOptions) {
    this.options = new CompilerOptionsImpl(compilerOptions);
    this.compilerContextBuilder = new CompilerContext.Builder();
    this.compilerContext = compilerContextBuilder.options(options).build();
  }

  public boolean reallyRun(TreeLogger logger) {
    ModuleDef module = compilerContext.getModule();
    long compileStartMs = System.currentTimeMillis();
    try {
      TreeLogger branch =
          logger.branch(TreeLogger.INFO, "Compiling module " + module.getCanonicalName());

      if (options.isValidateOnly()) {
        return !Precompile.validate(logger, compilerContext);
      }

      Precompilation precompilation = Precompile.precompile(branch, compilerContext);
      // TODO: move to precompile() after params are refactored
      if (!options.shouldSaveSource()) {
        precompilation.removeSourceArtifacts(logger);
      }

      Permutation[] allPerms = new Permutation[] {precompilation.getPermutations()[0]};
      List<PersistenceBackedObject<PermutationResult>> resultFiles =
          new ArrayList<PersistenceBackedObject<PermutationResult>>();
      resultFiles.add(compilerContext.getLibraryWriter().getPermutationResultHandle());
      CompilePerms.compile(
          branch, compilerContext, precompilation, allPerms, options.getLocalWorkers(),
          resultFiles);

      ArtifactSet generatedArtifacts = precompilation.getGeneratedArtifacts();
      compilerContext.getLibraryWriter().addGeneratedArtifacts(generatedArtifacts);

      // Save and close the current library. Needs to be done prior to linking since linking will
      // need to read its contained PermutationResult instance.
      compilerContext.getLibraryWriter().write(module.getResourceLastModified());

      if (compilerContext.getOptions().shouldLink()) {
        generatedArtifacts.addAll(compilerContext.getLibraryGroup().getGeneratedArtifacts());

        Set<PermutationResult> libraryPermutationResults = Sets.newLinkedHashSet();
        List<PersistenceBackedObject<PermutationResult>> permutationResultHandles =
            compilerContext.getLibraryGroup().getPermutationResultHandlesInLinkOrder();
        for (PersistenceBackedObject<PermutationResult> permutationResultHandle :
            permutationResultHandles) {
          libraryPermutationResults.add(permutationResultHandle.newInstance(logger));
        }

        Event linkEvent = SpeedTracerLogger.start(CompilerEventType.LINK);
        File absPath = new File(options.getWarDir(), module.getName());
        absPath = absPath.getAbsoluteFile();

        String logMessage = "Linking into " + absPath;
        if (options.getExtraDir() != null) {
          File absExtrasPath = new File(options.getExtraDir(), module.getName());
          absExtrasPath = absExtrasPath.getAbsoluteFile();
          logMessage += "; Writing extras to " + absExtrasPath;
        }
        Link.link(logger.branch(TreeLogger.TRACE, logMessage),
            module, compilerContext.getPublicResourceOracle(), generatedArtifacts, allPerms,
            resultFiles, libraryPermutationResults, options, options);
        linkEvent.end();
      }

      long durationMs = System.currentTimeMillis() - compileStartMs;
      branch.log(TreeLogger.INFO,
          "Compilation succeeded -- " + String.format("%.3f", durationMs / 1000d) + "s");
    } catch (UnableToCompleteException e) {
      // The real cause has been logged.
      return false;
    } catch (IOException e) {
      // The real cause has been logged.
      return false;
    }
    return true;
  }

  public boolean run(TreeLogger logger) throws UnableToCompleteException {
    try {
      normalizeOptions();
    } catch (IOException e) {
      logger.log(TreeLogger.ERROR, "Unable to create compiler work directory", e);
      return false;
    }

    LibraryGroup libraryGroup = loadLibraryGroup(logger);
    compilerContext = compilerContextBuilder.libraryGroup(libraryGroup)
        .libraryWriter(new ZipLibraryWriter(options.getOutputLibraryPath()))
        .unitCache(new LibraryGroupUnitCache(libraryGroup)).build();

    ModuleDef module = ModuleDefLoader.loadFromClassPath(
        logger, compilerContext, options.getModuleNames().get(0), false, false);

    return run(logger, compilerContextBuilder, module);
  }

  public boolean run(TreeLogger logger, Builder compilerContextBuilder, ModuleDef module) {
    this.compilerContextBuilder = compilerContextBuilder;
    compilerContext = compilerContextBuilder.module(module).build();
    return reallyRun(logger);
  }

  private LibraryGroup loadLibraryGroup(TreeLogger logger) throws UnableToCompleteException {
    LibraryGroup libraryGroup;
    try {
      libraryGroup = LibraryGroup.fromZipPaths(options.getLibraryPaths());
    } catch (IncompatibleLibraryVersionException e) {
      logger.log(TreeLogger.ERROR, e.getMessage());
      throw new UnableToCompleteException();
    }
    return libraryGroup;
  }

  private void normalizeOptions() throws IOException {
    Preconditions.checkArgument(options.getModuleNames().size() == 1);

    if (options.getWorkDir() == null) {
      options.setWorkDir(Utility.makeTemporaryDirectory(null, "gwtc"));
      Runtime.getRuntime().addShutdownHook(new Thread() {
        @Override
        public void run() {
          Util.recursiveDelete(options.getWorkDir(), false);
        }
      });
    }
    if ((options.isSoycEnabled() || options.isJsonSoycEnabled()) && options.getExtraDir() == null) {
      options.setExtraDir(new File("extras"));
    }
    if (Strings.isNullOrEmpty(options.getOutputLibraryPath())) {
      options.setOutputLibraryPath(
          options.getWorkDir().getPath() + "/" + options.getModuleNames().get(0) + ".gwtlib");
    }
    // Optimize early since permutation compiles will run in process.
    options.setOptimizePrecompile(true);
  }
}
