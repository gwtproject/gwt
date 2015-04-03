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
package com.google.gwt.dev.shell;

import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.linker.ArtifactSet;
import com.google.gwt.dev.CompilerContext;
import com.google.gwt.dev.PrecompileTaskOptionsImpl;
import com.google.gwt.dev.RebindCache;
import com.google.gwt.dev.cfg.ModuleDef;
import com.google.gwt.dev.cfg.Rule;
import com.google.gwt.dev.javac.CompilationState;
import com.google.gwt.dev.javac.StandardGeneratorContext;
import com.google.gwt.dev.util.log.speedtracer.DevModeEventType;
import com.google.gwt.dev.util.log.speedtracer.SpeedTracerLogger;
import com.google.gwt.dev.util.log.speedtracer.SpeedTracerLogger.Event;

import java.io.File;
import java.util.Deque;

/**
 * Provides an environment for a {@link com.google.gwt.dev.shell.ModuleSpace}
 * that works appropriately for the development shell.
 */
public class ShellModuleSpaceHost implements ModuleSpaceHost {

  // TODO(jat): hack to try and serialize rebinds
  private static final Object rebindLock = new Object[0];

  protected final CompilationState compilationState;

  protected final File genDir;

  private final ArtifactAcceptor artifactAcceptor;

  private CompilingClassLoader classLoader;

  private final TreeLogger logger;

  private final ModuleDef module;

  private StandardRebindOracle rebindOracle;

  private ModuleSpace space;

  private RebindCache rebindCache;

  /**
   * @param module the module associated with the hosted module space
   */
  public ShellModuleSpaceHost(TreeLogger logger,
      CompilationState compilationState, ModuleDef module, File genDir,
      ArtifactAcceptor artifactAcceptor, RebindCache rebindCache) {
    this.logger = logger;
    this.compilationState = compilationState;
    this.module = module;
    this.genDir = genDir;
    this.artifactAcceptor = artifactAcceptor;
    this.rebindCache = rebindCache;
  }

  @Override
  public CompilingClassLoader getClassLoader() {
    checkForModuleSpace();
    return classLoader;
  }

  @Override
  public String[] getEntryPointTypeNames() {
    checkForModuleSpace();
    return module.getEntryPointTypeNames();
  }

  @Override
  public TreeLogger getLogger() {
    return logger;
  }

  @Override
  public void onModuleReady(ModuleSpace readySpace)
      throws UnableToCompleteException {
    this.space = readySpace;

    Event moduleSpaceHostReadyEvent = SpeedTracerLogger.start(DevModeEventType.MODULE_SPACE_HOST_READY);
    try {
      // Establish an environment for JavaScript property providers to run.
      //
      ModuleSpacePropertyOracle propOracle = new ModuleSpacePropertyOracle(
          module.getProperties(), module.getActiveLinkerNames(), readySpace);

      // Set up the rebind oracle for the module.
      // It has to wait until now because we need to inject javascript.
      //
      Deque<Rule> rules = module.getRules();
      PrecompileTaskOptionsImpl options = new PrecompileTaskOptionsImpl();
      options.setGenDir(genDir);
      CompilerContext compilerContext =
          new CompilerContext.Builder().module(module).options(options).build();
      StandardGeneratorContext genCtx =
          new StandardGeneratorContext(compilerContext, compilationState, new ArtifactSet(), false);

      // Only enable generator result caching if we have a valid rebindCache
      genCtx.setGeneratorResultCachingEnabled((rebindCache != null));

      rebindOracle = new StandardRebindOracle(propOracle, rules, genCtx);
      rebindOracle.setRebindCache(rebindCache);

      // Create a completely isolated class loader which owns all classes
      // associated with a particular module. This effectively builds a
      // separate 'domain' for each running module, so that they all behave as
      // though they are running separately. This allows the shell to run
      // multiple modules, both in succession and simultaneously, without getting
      // confused.
      //
      // Note that the compiling class loader has no parent. This keeps it from
      // accidentally 'escaping' its domain and loading classes from the system
      // class loader (the one that loaded the shell itself).
      //
      classLoader = new CompilingClassLoader(logger, compilationState, readySpace);
    } finally {
      moduleSpaceHostReadyEvent.end();
    }
  }

  @Override
  public String rebind(TreeLogger logger, String sourceTypeName)
      throws UnableToCompleteException {
    synchronized (rebindLock) {
      checkForModuleSpace();
      return rebindOracle.rebind(logger, sourceTypeName, new ArtifactAcceptor() {
        @Override
        public void accept(TreeLogger logger, ArtifactSet newlyGeneratedArtifacts)
        throws UnableToCompleteException {
          artifactAcceptor.accept(logger, newlyGeneratedArtifacts);
        }
      });
    }
  }

  private void checkForModuleSpace() {
    if (space == null) {
      throw new IllegalStateException("Module initialization error");
    }
  }
}
