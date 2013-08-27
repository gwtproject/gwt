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
package com.google.gwt.core.linker;

import com.google.gwt.core.ext.LinkerContext;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.linker.Artifact;
import com.google.gwt.core.ext.linker.ArtifactSet;
import com.google.gwt.core.ext.linker.CompilationResult;
import com.google.gwt.core.ext.linker.EmittedArtifact;
import com.google.gwt.core.ext.linker.LinkerOrder;
import com.google.gwt.core.ext.linker.LinkerOrder.Order;
import com.google.gwt.core.ext.linker.Shardable;
import com.google.gwt.core.ext.linker.SyntheticArtifact;
import com.google.gwt.core.ext.linker.Transferable;
import com.google.gwt.core.ext.linker.impl.SelectionScriptLinker;
import com.google.gwt.dev.About;
import com.google.gwt.dev.util.DefaultTextOutput;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

/**
 * A Linker for producing a single JavaScript file from a GWT module that can
 * be run in the d8 command line profiler. The use of this Linker requires
 * that the module has exactly one distinct compilation result.
 *
 * <p>Note: This linker is considered experimental and is likely going
 * to have breaking changes in future.
 */
@LinkerOrder(Order.PRIMARY)
@Shardable
public class D8ScriptLinker extends SelectionScriptLinker {
  @Override
  public String getDescription() {
    return "D8 Single Script";
  }

  @Transferable
  private static class Script extends Artifact<Script> {
    private final String strongName;

    public Script(String strongName) {
      super(D8ScriptLinker.class);
      this.strongName = strongName;
    }

    @Override
    public int compareToComparableArtifact(Script that) {
      return strongName.compareTo(that.strongName);
    }

    @Override
    public Class<Script> getComparableArtifactType() {
      return Script.class;
    }

    public String getStrongName() {
      return strongName;
    }

    @Override
    public int hashCode() {
      return strongName.hashCode() * 17;
    }

    @Override
    public String toString() {
      return "Script " + strongName;
    }
  }

  @Override
  protected Collection<Artifact<?>> doEmitCompilation(TreeLogger logger,
      LinkerContext context, CompilationResult result, ArtifactSet artifacts)
      throws UnableToCompleteException {

    String[] js = result.getJavaScript();
    if (js.length != 1) {
      logger.branch(TreeLogger.ERROR,
          "The module must not have multiple fragments when using the "
              + getDescription() + " Linker.", null);
      throw new UnableToCompleteException();
    }

    Collection<Artifact<?>> toReturn = new ArrayList<Artifact<?>>();
    toReturn.add(new Script(result.getStrongName()));
    toReturn.addAll(emitSelectionInformation(result.getStrongName(), result));

    SyntheticArtifact artifact = emitString(logger, js[0], result.getStrongName()
        + ".js");
    toReturn.add(artifact);

    return toReturn;
  }

  @Override
  protected EmittedArtifact emitSelectionScript(TreeLogger logger,
      LinkerContext context, ArtifactSet artifacts)
      throws UnableToCompleteException {

    // Find the single Script result
    Set<Script> results = artifacts.find(Script.class);
    if (results.size() != 1) {
      logger.log(TreeLogger.ERROR, "The module must have exactly one distinct"
          + " permutation when using the " + getDescription() + " Linker; found " + results.size(),
          null);
      throw new UnableToCompleteException();
    }
    Script result = results.iterator().next();

    DefaultTextOutput out = new DefaultTextOutput(true);

    // Emit the selection script.
    String bootstrap = generateSelectionScript(logger, context, artifacts);
    bootstrap = context.optimizeJavaScript(logger, bootstrap);
    out.print(bootstrap);
    out.newlineOpt();

    out.print("var $gwt_version = \"" + About.getGwtVersionNum() + "\";");
    out.newlineOpt();
    out.print("var $wnd = window;");
    out.newlineOpt();
    out.print("var $doc = $wnd.document;");
    out.newlineOpt();
    out.print("var $moduleName, $moduleBase;");
    out.newlineOpt();

    out.print("var $strongName = '" + result.getStrongName() + "';");
    out.newlineOpt();
    out.print("load($strongName + '.js');");

    // Generate the call to tell the bootstrap code that we're ready to go.
    out.newlineOpt();
    out.print("if (" + context.getModuleFunctionName() + ") "
        + context.getModuleFunctionName() + ".onScriptLoad(gwtOnLoad);");
    out.newlineOpt();

    return emitString(logger, out.toString(), context.getModuleName()
        + ".nocache.js");
  }

  /**
   * Unimplemented. Normally required by
   * {@link #doEmitCompilation(TreeLogger, LinkerContext, CompilationResult, ArtifactSet)}.
   */
  @Override
  protected String getCompilationExtension(TreeLogger logger,
      LinkerContext context) throws UnableToCompleteException {
    throw new UnableToCompleteException();
  }

  /**
   * Unimplemented. Normally required by
   * {@link #doEmitCompilation(TreeLogger, LinkerContext, CompilationResult, ArtifactSet)}.
   */
  @Override
  protected String getModulePrefix(TreeLogger logger, LinkerContext context,
      String strongName) throws UnableToCompleteException {
    throw new UnableToCompleteException();
  }

  /**
   * Unimplemented. Normally required by
   * {@link #doEmitCompilation(TreeLogger, LinkerContext, CompilationResult, ArtifactSet)}.
   */
  @Override
  protected String getModuleSuffix(TreeLogger logger, LinkerContext context)
      throws UnableToCompleteException {
    throw new UnableToCompleteException();
  }

  @Override
  protected String getSelectionScriptTemplate(TreeLogger logger, LinkerContext context)
      throws UnableToCompleteException {
    return "com/google/gwt/core/linker/D8ScriptTemplate.js";
  }
}
