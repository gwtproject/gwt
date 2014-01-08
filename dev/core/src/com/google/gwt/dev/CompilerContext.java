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
import com.google.gwt.dev.cfg.LibraryGroup;
import com.google.gwt.dev.cfg.LibraryGroupBuildResourceOracle;
import com.google.gwt.dev.cfg.LibraryGroupPublicResourceOracle;
import com.google.gwt.dev.cfg.LibraryWriter;
import com.google.gwt.dev.cfg.ModuleDef;
import com.google.gwt.dev.cfg.NullLibraryWriter;
import com.google.gwt.dev.javac.UnitCache;
import com.google.gwt.dev.resource.ResourceOracle;
import com.google.gwt.thirdparty.guava.common.collect.Multimap;

import java.util.Set;

/**
 * Contains most global read-only compiler state and makes it easily accessible to the far flung
 * reaches of the compiler call graph without the constant accumulation of more and more function
 * parameters.
 */
public class CompilerContext {

  /**
   * CompilerContext builder.
   */
  public static class Builder {

    private ResourceOracle buildResourceOracle;
    private LibraryGroup libraryGroup;
    private LibraryWriter libraryWriter = new NullLibraryWriter();
    private ModuleDef module;
    private PrecompileTaskOptions options = new PrecompileTaskOptionsImpl();
    private ResourceOracle publicResourceOracle;
    private ResourceOracle sourceResourceOracle;
    private UnitCache unitCache;

    public CompilerContext build() {
      initializeResourceOracles();

      CompilerContext compilerContext = new CompilerContext();
      compilerContext.buildResourceOracle = buildResourceOracle;
      compilerContext.libraryWriter = libraryWriter;
      compilerContext.libraryGroup = libraryGroup;
      compilerContext.module = module;
      compilerContext.options = options;
      compilerContext.publicResourceOracle = publicResourceOracle;
      compilerContext.sourceResourceOracle = sourceResourceOracle;
      compilerContext.unitCache = unitCache;
      return compilerContext;
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
  private LibraryGroup libraryGroup;
  private LibraryWriter libraryWriter = new NullLibraryWriter();
  private ModuleDef module;

  // TODO(stalcup): split this into module parsing, precompilation, compilation, and linking option
  // sets.
  private PrecompileTaskOptions options = new PrecompileTaskOptionsImpl();
  private ResourceOracle publicResourceOracle;
  private ResourceOracle sourceResourceOracle;
  private UnitCache unitCache;

  /**
   * Walks the parts of the library dependency graph that have not run the given generator
   * referenced by name and accumulates and returns a map from binding property name to newly legal
   * values that were declared in those libraries.<br />
   *
   * The resulting map represents the set of binding property changes that have not yet been taken
   * into account in the output of a particular generator and which may need to trigger the
   * re-execution of said generator.
   */
  public Multimap<String, String> gatherNewBindingPropertyValuesForGenerator(String generatorName) {
    Multimap<String, String> newBindingPropertyValues =
        getLibraryGroup().gatherNewBindingPropertyValuesForGenerator(generatorName);
    newBindingPropertyValues.putAll(libraryWriter.getNewBindingPropertyValuesByName());
    return newBindingPropertyValues;
  }

  /**
   * Walks the parts of the library dependency graph that have not run the given generator
   * referenced by name and accumulates and returns a map from configuration property name to newly
   * set values that were declared in those libraries.<br />
   *
   * The resulting map represents the set of configuration property value changes that have not yet
   * been taken into account in the output of a particular generator and which may need to trigger
   * the re-execution of said generator.
   */
  public Multimap<String, String> gatherNewConfigurationPropertyValuesForGenerator(
      String generatorName) {
    Multimap<String, String> newConfigurationPropertyValues =
        getLibraryGroup().gatherNewConfigurationPropertyValuesForGenerator(generatorName);
    newConfigurationPropertyValues.putAll(libraryWriter.getNewConfigurationPropertyValuesByName());
    return newConfigurationPropertyValues;
  }

  public Set<String> gatherNewReboundTypeNamesForGenerator(String generatorName) {
    Set<String> newReboundTypeNames =
        getLibraryGroup().gatherNewReboundTypeNamesForGenerator(generatorName);
    newReboundTypeNames.addAll(libraryWriter.getReboundTypeNames());
    return newReboundTypeNames;
  }

  public Set<String> gatherOldReboundTypeNamesForGenerator(String generatorName) {
    return getLibraryGroup().gatherOldReboundTypeNamesForGenerator(generatorName);
  }

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

  public ResourceOracle getPublicResourceOracle() {
    return publicResourceOracle;
  }

  public ResourceOracle getSourceResourceOracle() {
    return sourceResourceOracle;
  }

  public UnitCache getUnitCache() {
    return unitCache;
  }
}
