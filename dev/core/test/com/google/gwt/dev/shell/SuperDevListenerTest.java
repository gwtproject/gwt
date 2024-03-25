/*
 * Copyright 2024 Google Inc.
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
package com.google.gwt.dev.shell;

import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.dev.DevMode;
import com.google.gwt.dev.HostedModeOptionsMock;
import com.google.gwt.dev.jjs.JsOutputOption;
import com.google.gwt.dev.util.arg.OptionMethodNameDisplayMode;
import com.google.gwt.dev.util.arg.SourceLevel;
import com.google.gwt.thirdparty.guava.common.collect.ImmutableList;
import com.google.gwt.util.regexfilter.WhitelistRegexFilter;

import junit.framework.TestCase;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

/**
 * A wide variety of tests on {@link SuperDevListener}.
 */
public class SuperDevListenerTest extends TestCase {
  private static final int TEST_PORT = 9998;
  private static final String MODULE_NAME = "Test_Module_Name";
  private final TreeLogger treeLogger = new FailErrorLogger();
  private final DevMode.HostedModeOptions options = new HostedModeOptionsMock();
  private final WhitelistRegexFilter whitelistRegexFilter = new WhitelistRegexFilter();
  private File warDir;
  @Override
  public void setUp() throws IOException {
    warDir = Files.createTempDirectory("war-file").toFile();
    warDir.deleteOnExit();
    setUpOptions(options);
  }

  private void setUpOptions(DevMode.HostedModeOptions optionsToPrepare) {
    optionsToPrepare.setCodeServerPort(TEST_PORT);
    optionsToPrepare.setSourceLevel(SourceLevel.JAVA17);
    optionsToPrepare.setWarDir(warDir);
    optionsToPrepare.setOutput(JsOutputOption.DETAILED);
    optionsToPrepare.setGenerateJsInteropExports(false);
    optionsToPrepare.setIncrementalCompileEnabled(true);
    optionsToPrepare.setMethodNameDisplayMode(OptionMethodNameDisplayMode.Mode.NONE);
    optionsToPrepare.setStrict(false);
    optionsToPrepare.setModuleNames(ImmutableList.of(MODULE_NAME));
  }

  public void testDefaultArgs() {
    final SuperDevListener superDevListener = new SuperDevListener(treeLogger, options);
    final List<String> codeServerArgs = superDevListener.getCodeServerArgs();
    assertEquals("Wrong default arguments", 10, codeServerArgs.size());
    assertTrue("Precompile flag should set", codeServerArgs.contains("-noprecompile"));
    assertTrue("Port should set", codeServerArgs.contains("-port"));
    assertTrue("Port should set", codeServerArgs.contains(Integer.toString(TEST_PORT)));
    assertTrue("SourceLevel should set", codeServerArgs.contains("-sourceLevel"));
    assertTrue("SourceLevel should set", codeServerArgs.contains(SourceLevel.JAVA17.getStringValue()));
    assertTrue("LauncherDir should set", codeServerArgs.contains("-launcherDir"));
    assertTrue("LauncherDir should set", codeServerArgs.contains(warDir.getAbsolutePath()));
    assertTrue("Style should set", codeServerArgs.contains("-style"));
    assertTrue("Style should set", codeServerArgs.contains("DETAILED"));
    assertTrue("Module name should set", codeServerArgs.contains(MODULE_NAME));
  }

  public void testDefaultNonSuperDevModePort() {
    final int port = 9997;
    options.setCodeServerPort(port);
    final SuperDevListener superDevListener = new SuperDevListener(treeLogger, options);
    final List<String> codeServerArgs = superDevListener.getCodeServerArgs();
    assertTrue("Port should set", codeServerArgs.contains("-port"));
    assertTrue("SuperDevMode Default port should set", codeServerArgs.contains("9876"));
  }

  public void testWithJsInteropAndSingleIncludeAndExclude() {
    options.setGenerateJsInteropExports(true);
    options.getJsInteropExportFilter().add("-com.google.gwt.exclude.First");
    options.getJsInteropExportFilter().add("com.google.gwt.include.First");

    final SuperDevListener superDevListener = new SuperDevListener(treeLogger, options);
    final List<String> codeServerArgs = superDevListener.getCodeServerArgs();
    assertTrue("GenerateJsInteropExports should set", codeServerArgs.contains("-generateJsInteropExports"));
    assertTrue("ExcludeJsInteropExports should set", codeServerArgs.contains("-excludeJsInteropExports"));
    assertTrue("ExcludeJsInteropExports should set", codeServerArgs.contains("com.google.gwt.exclude.First"));
    assertTrue("IncludeJsInteropExports should set", codeServerArgs.contains("-includeJsInteropExports"));
    assertTrue("IncludeJsInteropExports should set", codeServerArgs.contains("com.google.gwt.include.First"));
  }

  public void testWithJsInteropIncludesAndExcludes() {
    options.setGenerateJsInteropExports(true);
    options.getJsInteropExportFilter().add("-com.google.gwt.exclude.First");
    options.getJsInteropExportFilter().add("com.google.gwt.include.First");
    options.getJsInteropExportFilter().add("-com.google.gwt.exclude.Second");
    options.getJsInteropExportFilter().add("com.google.gwt.include.Second");

    final SuperDevListener superDevListener = new SuperDevListener(treeLogger, options);
    final List<String> codeServerArgs = superDevListener.getCodeServerArgs();
    final int generateJsExportsIndex = codeServerArgs.indexOf("-generateJsInteropExports") + 1;
    final List<String> expectedJsExports = createExpectedJsIncludesAndExcludesgetStrings();

    assertTrue("GenerateJsInteropExports should set", generateJsExportsIndex > 0);
    final List<String> actualJsExports = codeServerArgs.subList(generateJsExportsIndex, codeServerArgs.size());
    for (int expectedIndex = 0; expectedIndex < expectedJsExports.size(); expectedIndex++) {
      assertEquals("Setting for JS export not found", expectedJsExports.get(expectedIndex),
              actualJsExports.get(expectedIndex));
    }
  }

  public void testWithJsInteropAndCustomRegexFilter() {
    final HostedModeOptionsMockWithCustomRegexFilter customOptions = new HostedModeOptionsMockWithCustomRegexFilter();
    setUpOptions(customOptions);
    customOptions.setGenerateJsInteropExports(true);
    customOptions.getJsInteropExportFilter().add("-com.google.gwt.exclude.First");
    customOptions.getJsInteropExportFilter().add("com.google.gwt.include.First");
    customOptions.getJsInteropExportFilter().add("-com.google.gwt.exclude.Second");
    customOptions.getJsInteropExportFilter().add("com.google.gwt.include.Second");
    customOptions.getJsInteropExportFilter().add("+com.google.gwt.include.Third");
    customOptions.getJsInteropExportFilter().add("*com.google.gwt.include.Fourth");

    final SuperDevListener superDevListener = new SuperDevListener(treeLogger, customOptions);
    final List<String> codeServerArgs = superDevListener.getCodeServerArgs();
    final int generateJsExportsIndex = codeServerArgs.indexOf("-generateJsInteropExports") + 1;
    final List<String> expectedJsExports = createExpectedJsIncludesAndExcludesgetStrings();
    expectedJsExports.add("-includeJsInteropExports");
    expectedJsExports.add("com.google.gwt.include.Third");
    expectedJsExports.add("-includeJsInteropExports");
    expectedJsExports.add("*com.google.gwt.include.Fourth");

    assertTrue("GenerateJsInteropExports should set", generateJsExportsIndex > 0);
    final List<String> actualJsExports = codeServerArgs.subList(generateJsExportsIndex, codeServerArgs.size());
    for (int expectedIndex = 0; expectedIndex < expectedJsExports.size(); expectedIndex++) {
      assertEquals("Setting for JS export not found", expectedJsExports.get(expectedIndex),
              actualJsExports.get(expectedIndex));
    }
  }

  private static List<String> createExpectedJsIncludesAndExcludesgetStrings() {
    final List<String> expectedJsExports = new ArrayList<>();
    expectedJsExports.add("-excludeJsInteropExports");
    expectedJsExports.add("com.google.gwt.exclude.First");
    expectedJsExports.add("-includeJsInteropExports");
    expectedJsExports.add("com.google.gwt.include.First");
    expectedJsExports.add("-excludeJsInteropExports");
    expectedJsExports.add("com.google.gwt.exclude.Second");
    expectedJsExports.add("-includeJsInteropExports");
    expectedJsExports.add("com.google.gwt.include.Second");
    return expectedJsExports;
  }

  private static class HostedModeOptionsMockWithCustomRegexFilter extends HostedModeOptionsMock {
    private final WhitelistRegexFilter whitelistRegexFilter = new CustomWhitelistRegexFilter();
    @Override
    public WhitelistRegexFilter getJsInteropExportFilter() {
      return whitelistRegexFilter;
    }
  }
  private static class CustomWhitelistRegexFilter extends WhitelistRegexFilter {
    private final List<String> values = new ArrayList<>();
    @Override
    public List<String> getValues() {
      return values;
    }
    @Override
    public void add(String regex) {
      values.add(regex);
    }
    @Override
    public void addAll(List<String> newValues) {
      values.addAll(newValues);
    }
  }
}
