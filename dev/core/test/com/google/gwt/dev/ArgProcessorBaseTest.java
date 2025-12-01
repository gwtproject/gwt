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
package com.google.gwt.dev;

import com.google.gwt.dev.jjs.JJSOptionsImpl;
import com.google.gwt.dev.jjs.JsOutputOption;
import com.google.gwt.dev.js.JsNamespaceOption;
import com.google.gwt.dev.util.arg.ArgHandlerDraftCompile;
import com.google.gwt.dev.util.arg.ArgHandlerOptimize;
import com.google.gwt.dev.util.arg.OptionMethodNameDisplayMode;
import com.google.gwt.dev.util.arg.SourceLevel;

/**
 * Test for {@link ArgProcessorBase}.
 */
public class ArgProcessorBaseTest extends ArgProcessorTestBase {

  private static class OptimizeArgProcessor extends ArgProcessorBase {
    public OptimizeArgProcessor(JJSOptionsImpl option) {
      registerHandler(new ArgHandlerDraftCompile(option));
      registerHandler(new ArgHandlerOptimize(option));
    }

    @Override
    protected String getName() {
      return this.getClass().getSimpleName();
    }
  }

  private final OptimizeArgProcessor argProcessor;
  private final JJSOptionsImpl options = new JJSOptionsImpl();

  public ArgProcessorBaseTest() {
    argProcessor = new OptimizeArgProcessor(options);
  }

  public void testOptionOrderIsPrecedenceArgs() {
    assertProcessSuccess(argProcessor);
    assertEquals(9, options.getOptimizationLevel());

    assertProcessSuccess(argProcessor, "-optimize", "5");
    assertEquals(5, options.getOptimizationLevel());

    assertProcessSuccess(argProcessor, "-optimize", "5", "-draftCompile");
    assertEquals(0, options.getOptimizationLevel());

    assertProcessSuccess(argProcessor,
        "-optimize", "5", "-draftCompile", "-optimize", "9");
    assertEquals(9, options.getOptimizationLevel());
  }

  public void testNoDraftMeansDefaults() {
    assertProcessSuccess(argProcessor);
    assertDefaults();

    assertProcessSuccess(argProcessor, "-nodraftCompile");
    assertDefaults();
  }

  /**
   * For each field in JJSOptionsImpl, verify it is set to the default value.
   */
  private void assertDefaults() {
    assertEquals(false, options.shouldAddRuntimeChecks());
    assertEquals(true, options.shouldClusterSimilarFunctions());
    assertEquals(false, options.isIncrementalCompileEnabled());
    assertEquals(false, options.isCompilerMetricsEnabled());
    assertEquals(false, options.isClassMetadataDisabled());
    assertEquals(false, options.isEnableAssertions());
    assertEquals(-1, options.getFragmentCount());
    assertEquals(true, options.shouldInlineLiteralParameters());
    assertEquals(false, options.isJsonSoycEnabled());
    assertEquals(JsNamespaceOption.NONE, options.getNamespace());
    assertEquals(9, options.getOptimizationLevel());
    assertEquals(false, options.shouldOptimizeDataflow());
    assertEquals(true, options.shouldOrdinalizeEnums());
    assertEquals(JsOutputOption.OBFUSCATED, options.getOutput());
    assertEquals(true, options.shouldRemoveDuplicateFunctions());
    assertEquals(true, options.isRunAsyncEnabled());
    assertEquals(SourceLevel.DEFAULT_SOURCE_LEVEL, options.getSourceLevel());
    assertEquals(false, options.isSoycEnabled());
    assertEquals(false, options.isSoycExtra());
    assertEquals(false, options.isSoycHtmlDisabled());
    assertEquals(false, options.isStrict());
    assertEquals(false, options.shouldGenerateJsInteropExports());
    assertEquals(false, options.useDetailedTypeIds());
    assertEquals(OptionMethodNameDisplayMode.Mode.NONE,
        options.getMethodNameDisplayMode());
    // deliberately skipping the jsInteropExportFilter field, empty state isn't exposed
    assertEquals(false, options.isClosureCompilerFormatEnabled());
  }
}
