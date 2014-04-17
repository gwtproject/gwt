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
package com.google.gwt.dev;

import com.google.gwt.dev.cfg.CombinedResourceOracle;
import com.google.gwt.dev.cfg.ImmutableLibraryGroup;
import com.google.gwt.dev.cfg.LibraryGroup;
import com.google.gwt.dev.cfg.LibraryGroupBuildResourceOracle;
import com.google.gwt.dev.cfg.LibraryGroupPublicResourceOracle;
import com.google.gwt.dev.cfg.LibraryWriter;
import com.google.gwt.dev.cfg.ModuleDef;
import com.google.gwt.dev.cfg.NullLibraryWriter;
import com.google.gwt.dev.javac.MemoryUnitCache;
import com.google.gwt.dev.javac.UnitCache;
import com.google.gwt.dev.resource.ResourceOracle;
import com.google.gwt.thirdparty.guava.common.collect.Sets;

import java.util.Set;

/**
 * Contains most global read-only compiler state and makes it easily accessible to the far flung
 * reaches of the compiler call graph without the constant accumulation of more and more function
 * parameters.
 */
public class CompilerContext {

  /**
   * A small set of compile metrics that should be used to warn incremental compile users when a
   * module is getting too large.
   */
  public static class TinyCompileSummary {

    private int typesForGeneratorsCount;
    private int typesForAstCount;
    private int staticSourceFilesCount;
    private int generatedSourceFilesCount;
    private int cachedStaticSourceFilesCount;
    private int cachedGeneratedSourceFilesCount;

    public int getTypesForGeneratorsCount() {
      return typesForGeneratorsCount;
    }

    public void setTypesForGeneratorsCount(int typesForGeneratorsCount) {
      this.typesForGeneratorsCount = typesForGeneratorsCount;
    }

    public int getTypesForAstCount() {
      return typesForAstCount;
    }

    public void setTypesForAstCount(int typesForAstCount) {
      this.typesForAstCount = typesForAstCount;
    }

    public int getStaticSourceFilesCount() {
      return staticSourceFilesCount;
    }

    public void setStaticSourceFilesCount(int staticSourceFilesCount) {
      this.staticSourceFilesCount = staticSourceFilesCount;
    }

    public int getGeneratedSourceFilesCount() {
      return generatedSourceFilesCount;
    }

    public void setGeneratedSourceFilesCount(int generatedSourceFilesCount) {
      this.generatedSourceFilesCount = generatedSourceFilesCount;
    }

    public int getCachedStaticSourceFilesCount() {
      return cachedStaticSourceFilesCount;
    }

    public void setCachedStaticSourceFilesCount(int cachedStaticSourceFilesCount) {
      this.cachedStaticSourceFilesCount = cachedStaticSourceFilesCount;
    }

    public int getCachedGeneratedSourceFilesCount() {
      return cachedGeneratedSourceFilesCount;
    }

    public void setCachedGeneratedSourceFilesCount(int cachedGeneratedSourceFilesCount) {
      this.cachedGeneratedSourceFilesCount = cachedGeneratedSourceFilesCount;
    }
  }

  /**
   * CompilerContext builder.
   */
  public static class Builder {

    private ResourceOracle buildResourceOracle;
    private boolean compileMonolithic = true;
    private LibraryGroup libraryGroup = new ImmutableLibraryGroup();
    private LibraryWriter libraryWriter = new NullLibraryWriter();
    private ModuleDef module;
    private PrecompileTaskOptions options = new PrecompileTaskOptionsImpl();
    private ResourceOracle publicResourceOracle;
    private ResourceOracle sourceResourceOracle;
    private UnitCache unitCache = new MemoryUnitCache();

    public CompilerContext build() {
      initializeResourceOracles();

      CompilerContext compilerContext = new CompilerContext();
      compilerContext.buildResourceOracle = buildResourceOracle;
      compilerContext.libraryWriter = libraryWriter;
      compilerContext.libraryGroup = libraryGroup;
      compilerContext.module = module;
      compilerContext.compileMonolithic = compileMonolithic;
      compilerContext.options = options;
      compilerContext.publicResourceOracle = publicResourceOracle;
      compilerContext.sourceResourceOracle = sourceResourceOracle;
      compilerContext.unitCache = unitCache;
      return compilerContext;
    }

    /**
     * Sets whether compilation should proceed monolithically or separately.
     */
    public Builder compileMonolithic(boolean compileMonolithic) {
      this.compileMonolithic = compileMonolithic;
      return this;
    }

    /**
     * Sets the libraryGroup and uses it to set resource oracles as well.
     */
    public Builder libraryGroup(LibraryGroup libraryGroup) {
      this.libraryGroup = libraryGroup;
      return this;
    }

    public Builder libraryWriter(LibraryWriter libraryWriter) {
      this.libraryWriter = libraryWriter;
      return this;
    }

    /**
     * Sets the module and uses it to set resource oracles as well.
     */
    public Builder module(ModuleDef module) {
      this.module = module;
      return this;
    }

    public Builder options(PrecompileTaskOptions options) {
      this.options = options;
      return this;
    }

    public Builder unitCache(UnitCache unitCache) {
      this.unitCache = unitCache;
      return this;
    }

    /**
     * Initialize source, build, and public resource oracles using the most complete currently
     * available combination of moduleDef and libraryGroup.<br />
     *
     * When executing as part of a monolithic compilation there will likely only be a moduleDef
     * available. That will result in sourcing resource oracles only from it, which is what
     * monolithic compilation expects.<br />
     *
     * When executing as part of a separate compilation there will likely be both a moduleDef and
     * libraryGroup available. That will result in sourcing resource oracles from a mixed
     * combination, which is what separate compilation expects.
     */
    private void initializeResourceOracles() {
      if (libraryGroup != null) {
        if (module != null) {
          sourceResourceOracle = module.getSourceResourceOracle();
          buildResourceOracle = new CombinedResourceOracle(
              module.getBuildResourceOracle(), new LibraryGroupBuildResourceOracle(libraryGroup));
          publicResourceOracle = new CombinedResourceOracle(
              module.getPublicResourceOracle(), new LibraryGroupPublicResourceOracle(libraryGroup));
        } else {
          sourceResourceOracle = null;
          buildResourceOracle = new LibraryGroupBuildResourceOracle(libraryGroup);
          publicResourceOracle = new LibraryGroupPublicResourceOracle(libraryGroup);
        }
      } else {
        if (module != null) {
          sourceResourceOracle = module.getSourceResourceOracle();
          buildResourceOracle = module.getBuildResourceOracle();
          publicResourceOracle = module.getPublicResourceOracle();
        } else {
          sourceResourceOracle = null;
          buildResourceOracle = null;
          publicResourceOracle = null;
        }
      }
    }
  }

  private ResourceOracle buildResourceOracle;
  /**
   * Whether compilation should proceed monolithically or separately. It is an example of a
   * configuration property that is not assignable by command line args. If more of these accumulate
   * they should be grouped together instead of floating free here.
   */
  private boolean compileMonolithic = true;
  private LibraryGroup libraryGroup = new ImmutableLibraryGroup();
  private LibraryWriter libraryWriter = new NullLibraryWriter();
  private ModuleDef module;
  private TinyCompileSummary tinyCompileSummary = new TinyCompileSummary();

  // TODO(stalcup): split this into module parsing, precompilation, compilation, and linking option
  // sets.
  private PrecompileTaskOptions options = new PrecompileTaskOptionsImpl();
  private ResourceOracle publicResourceOracle;
  private ResourceOracle sourceResourceOracle;
  private UnitCache unitCache = new MemoryUnitCache();

  public ResourceOracle getBuildResourceOracle() {
    return buildResourceOracle;
  }

  public LibraryGroup getLibraryGroup() {
    return libraryGroup;
  }

  public LibraryWriter getLibraryWriter() {
    return libraryWriter;
  }

  public ModuleDef getModule() {
    return module;
  }

  public PrecompileTaskOptions getOptions() {
    return options;
  }

  public Set<String> getProcessedReboundTypeSourceNamesForGenerator(String generatorName) {
    Set<String> processedReboundTypeSourceNames = Sets.newHashSet();
    processedReboundTypeSourceNames.addAll(
        getLibraryWriter().getProcessedReboundTypeSourceNamesByGenerator().get(generatorName));
    processedReboundTypeSourceNames.addAll(
        getLibraryGroup().getProcessedReboundTypeSourceNamesForGenerator(generatorName));
    return processedReboundTypeSourceNames;
  }

  public ResourceOracle getPublicResourceOracle() {
    return publicResourceOracle;
  }

  public Set<String> getRequestedReboundTypeSourceNames() {
    Set<String> requestedReboundTypeSourceNames = Sets.newHashSet();
    requestedReboundTypeSourceNames.addAll(getLibraryWriter().getRequestedReboundTypeSourceNames());
    requestedReboundTypeSourceNames.addAll(getLibraryGroup().getRequestedReboundTypeSourceNames());
    return requestedReboundTypeSourceNames;
  }

  public ResourceOracle getSourceResourceOracle() {
    return sourceResourceOracle;
  }

  public UnitCache getUnitCache() {
    return unitCache;
  }

  public boolean shouldCompileMonolithic() {
    return compileMonolithic;
  }

  public TinyCompileSummary getTinyCompileSummary() {
    return tinyCompileSummary;
  }
}
