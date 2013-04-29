/*
 * Copyright 2011 Google Inc.
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

package com.google.gwt.dev.codeserver;

import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.dev.CompilerOptions;
import com.google.gwt.dev.jjs.JsOutputOption;

import java.io.File;
import java.util.List;

/**
 * An implementation of CompilerOptions where all mutating methods throw
 * UnsupportedOperationException.
 * (This removes clutter in subclasses that don't implement mutation.)
 */
abstract class UnmodifiableCompilerOptions implements CompilerOptions {

  @Override
  public final void addModuleName(String moduleName) {
    throw new UnsupportedOperationException();
  }

  @Override
  @Deprecated
  public final void setAggressivelyOptimize(boolean aggressivelyOptimize) {
    throw new UnsupportedOperationException();
  }

  @Override
  public final void setCastCheckingDisabled(boolean disabled) {
    throw new UnsupportedOperationException();
  }

  @Override
  public final void setClassMetadataDisabled(boolean disabled) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void setClosureCompilerEnabled(boolean enabled) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void setClusterSimilarFunctions(boolean enabled) {
    throw new UnsupportedOperationException();
  }

  @Override
  public final void setCompilerMetricsEnabled(boolean enabled) {
    throw new UnsupportedOperationException();
  }

  @Override
  public final void setDeployDir(File dir) {
    throw new UnsupportedOperationException();
  }

  @Override
  public final void setDisableUpdateCheck(boolean disabled) {
    throw new UnsupportedOperationException();
  }

  @Override
  public final void setEnableAssertions(boolean enableAssertions) {
    throw new UnsupportedOperationException();
  }

  @Override
  public final void setEnabledGeneratingOnShards(boolean allowed) {
    throw new UnsupportedOperationException();
  }

  @Override
  public final void setExtraDir(File extraDir) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void setFragmentCount(int numFragments) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void setFragmentsMerge(int numFragments) {
    throw new UnsupportedOperationException();
  }

  @Override
  public final void setGenDir(File dir) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void setInlineLiteralParameters(boolean inlineLiteralParameters) {
    throw new UnsupportedOperationException();
  }

  @Override
  public final void setLocalWorkers(int localWorkers) {
    throw new UnsupportedOperationException();
  }

  @Override
  public final void setLogLevel(TreeLogger.Type logLevel) {
    throw new UnsupportedOperationException();
  }

  @Override
  public final void setMaxPermsPerPrecompile(int maxPerms) {
    throw new UnsupportedOperationException();
  }

  @Override
  public final void setModuleNames(List<String> moduleNames) {
    throw new UnsupportedOperationException();
  }

  @Override
  public final void setOptimizationLevel(int level) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void setOptimizeDataflow(boolean enabled) {
    throw new UnsupportedOperationException();
  }

  @Override
  public final void setOptimizePrecompile(boolean optimize) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void setOrdinalizeEnums(boolean enabled) {
    throw new UnsupportedOperationException();
  }

  @Override
  public final void setOutDir(File outDir) {
    throw new UnsupportedOperationException();
  }


  @Override
  public final void setOutput(JsOutputOption obfuscated) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void setRemoveDuplicateFunctions(boolean enabled) {
    throw new UnsupportedOperationException();
  }

  @Override
  public final void setRunAsyncEnabled(boolean enabled) {
    throw new UnsupportedOperationException();
  }

  @Override
  public final void setSoycEnabled(boolean enabled) {
    throw new UnsupportedOperationException();
  }

  @Override
  public final void setSoycExtra(boolean soycExtra) {
    throw new UnsupportedOperationException();
  }

  @Override
  public final void setSoycHtmlDisabled(boolean disabled) {
    throw new UnsupportedOperationException();
  }

  @Override
  public final void setStrict(boolean strict) {
    throw new UnsupportedOperationException();
  }

  @Override
  public final void setUseGuiLogger(boolean useGuiLogger) {
    throw new UnsupportedOperationException();
  }

  @Override
  public final void setValidateOnly(boolean validateOnly) {
    throw new UnsupportedOperationException();
  }

  @Override
  public final void setWarDir(File dir) {
    throw new UnsupportedOperationException();
  }

  @Override
  public final void setWorkDir(File dir) {
    throw new UnsupportedOperationException();
  }
}
