/*
 * Copyright 2009 Google Inc.
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
package com.google.gwt.dev.jjs;

import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.linker.PrecompilationMetricsArtifact;
import com.google.gwt.dev.CompilerContext;
import com.google.gwt.dev.jdt.RebindPermutationOracle;

/**
 * Uses the default compiler {@link JavaToJavaScriptCompiler}.
 */
public class JavaScriptCompiler implements AbstractCompiler {

  @Override
  public UnifiedAst precompile(TreeLogger logger, CompilerContext compilerContext,
      RebindPermutationOracle rpo, String[] declEntryPts, String[] additionalRootTypes,
      boolean singlePermutation, PrecompilationMetricsArtifact precompilationMetrics)
      throws UnableToCompleteException {
    boolean compileMonolithic = compilerContext.shouldCompileMonolithic();
    JavaToJavaScriptCompiler javaToJavaScriptCompiler = compileMonolithic
        ? new MonolithicJavaToJavaScriptCompiler(logger, compilerContext)
        : new LibraryJavaToJavaScriptCompiler(logger, compilerContext);
    return javaToJavaScriptCompiler.precompile(
        rpo, declEntryPts, additionalRootTypes, singlePermutation, precompilationMetrics);
  }
}
