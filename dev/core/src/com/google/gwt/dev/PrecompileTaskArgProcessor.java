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

package com.google.gwt.dev;

import com.google.gwt.dev.util.arg.ArgHandlerCompileReport;
import com.google.gwt.dev.util.arg.ArgHandlerCompilerMetrics;
import com.google.gwt.dev.util.arg.ArgHandlerDisableAggressiveOptimization;
import com.google.gwt.dev.util.arg.ArgHandlerDisableCastChecking;
import com.google.gwt.dev.util.arg.ArgHandlerDisableClassMetadata;
import com.google.gwt.dev.util.arg.ArgHandlerDisableClusterSimilarFunctions;
import com.google.gwt.dev.util.arg.ArgHandlerDisableGeneratingOnShards;
import com.google.gwt.dev.util.arg.ArgHandlerDisableInlineLiteralParameters;
import com.google.gwt.dev.util.arg.ArgHandlerDisableOptimizeDataflow;
import com.google.gwt.dev.util.arg.ArgHandlerDisableOrdinalizeEnums;
import com.google.gwt.dev.util.arg.ArgHandlerDisableRemoveDuplicateFunctions;
import com.google.gwt.dev.util.arg.ArgHandlerDisableRunAsync;
import com.google.gwt.dev.util.arg.ArgHandlerDisableSoycHtml;
import com.google.gwt.dev.util.arg.ArgHandlerDisableUpdateCheck;
import com.google.gwt.dev.util.arg.ArgHandlerDraftCompile;
import com.google.gwt.dev.util.arg.ArgHandlerDumpSignatures;
import com.google.gwt.dev.util.arg.ArgHandlerEnableAssertions;
import com.google.gwt.dev.util.arg.ArgHandlerEnableClosureCompiler;
import com.google.gwt.dev.util.arg.ArgHandlerFragmentCount;
import com.google.gwt.dev.util.arg.ArgHandlerFragmentMerge;
import com.google.gwt.dev.util.arg.ArgHandlerGenDir;
import com.google.gwt.dev.util.arg.ArgHandlerMaxPermsPerPrecompile;
import com.google.gwt.dev.util.arg.ArgHandlerOptimize;
import com.google.gwt.dev.util.arg.ArgHandlerScriptStyle;
import com.google.gwt.dev.util.arg.ArgHandlerSoyc;
import com.google.gwt.dev.util.arg.ArgHandlerSoycDetailed;
import com.google.gwt.dev.util.arg.ArgHandlerStrict;
import com.google.gwt.dev.util.arg.ArgHandlerValidateOnlyFlag;

class PrecompileTaskArgProcessor extends CompileArgProcessor {
  public PrecompileTaskArgProcessor(PrecompileTaskOptions options) {
    super(options);
    registerHandler(new ArgHandlerCompileReport(options));
    registerHandler(new ArgHandlerCompilerMetrics(options));
    registerHandler(new ArgHandlerDisableAggressiveOptimization(options));
    registerHandler(new ArgHandlerDisableCastChecking(options));
    registerHandler(new ArgHandlerDisableClassMetadata(options));
    registerHandler(new ArgHandlerDisableClusterSimilarFunctions(options));
    registerHandler(new ArgHandlerDisableRemoveDuplicateFunctions(options));
    registerHandler(new ArgHandlerDisableGeneratingOnShards(options));
    registerHandler(new ArgHandlerDisableInlineLiteralParameters(options));
    registerHandler(new ArgHandlerDisableOptimizeDataflow(options));
    registerHandler(new ArgHandlerDisableOrdinalizeEnums(options));
    registerHandler(new ArgHandlerDisableRunAsync(options));
    registerHandler(new ArgHandlerDisableSoycHtml(options));
    registerHandler(new ArgHandlerDisableUpdateCheck(options));
    registerHandler(new ArgHandlerDraftCompile(options));
    registerHandler(new ArgHandlerDumpSignatures());
    registerHandler(new ArgHandlerEnableAssertions(options));
    registerHandler(new ArgHandlerEnableClosureCompiler(options));
    registerHandler(new ArgHandlerFragmentCount(options));
    registerHandler(new ArgHandlerFragmentMerge(options));
    registerHandler(new ArgHandlerGenDir(options));
    registerHandler(new ArgHandlerMaxPermsPerPrecompile(options));
    registerHandler(new ArgHandlerOptimize(options));
    registerHandler(new ArgHandlerScriptStyle(options));
    registerHandler(new ArgHandlerSoyc(options));
    registerHandler(new ArgHandlerSoycDetailed(options));
    registerHandler(new ArgHandlerStrict(options));
    registerHandler(new ArgHandlerValidateOnlyFlag(options));
  }

  @Override
  protected String getName() {
    return Precompile.class.getName();
  }
}
