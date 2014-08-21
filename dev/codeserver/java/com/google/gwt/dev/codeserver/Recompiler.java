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

import com.google.gwt.core.ext.Linker;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.TreeLogger.Type;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.linker.CrossSiteIframeLinker;
import com.google.gwt.core.linker.IFrameLinker;
import com.google.gwt.dev.Compiler;
import com.google.gwt.dev.CompilerContext;
import com.google.gwt.dev.CompilerOptions;
import com.google.gwt.dev.IncrementalBuilder;
import com.google.gwt.dev.IncrementalBuilder.BuildResultStatus;
import com.google.gwt.dev.MinimalRebuildCache;
import com.google.gwt.dev.cfg.BindingProperty;
import com.google.gwt.dev.cfg.ConfigProps;
import com.google.gwt.dev.cfg.ConfigurationProperty;
import com.google.gwt.dev.cfg.ModuleDef;
import com.google.gwt.dev.cfg.ModuleDefLoader;
import com.google.gwt.dev.cfg.ResourceLoader;
import com.google.gwt.dev.cfg.ResourceLoaders;
import com.google.gwt.dev.javac.UnitCacheSingleton;
import com.google.gwt.dev.resource.impl.ResourceOracleImpl;
import com.google.gwt.dev.resource.impl.ZipFileClassPathEntry;
import com.google.gwt.dev.util.log.CompositeTreeLogger;
import com.google.gwt.dev.util.log.PrintWriterTreeLogger;
import com.google.gwt.thirdparty.guava.common.base.Joiner;
import com.google.gwt.thirdparty.guava.common.collect.ImmutableMap;
import com.google.gwt.thirdparty.guava.common.collect.Maps;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Recompiles a GWT module on demand.
 */
class Recompiler {

  private final AppSpace appSpace;
  private final String originalModuleName;
  private IncrementalBuilder incrementalBuilder;
  private final TreeLogger logger;
  private String serverPrefix;
  private int compilesDone = 0;
  private MinimalRebuildCache minimalRebuildCache = new MinimalRebuildCache();

  // after renaming
  private AtomicReference<String> moduleName = new AtomicReference<String>(null);

  private final AtomicReference<CompileDir> lastBuild = new AtomicReference<CompileDir>();

  private InputSummary lastBuildInput;

  private CompileDir publishedCompileDir;
  private final AtomicReference<ResourceLoader> resourceLoader =
      new AtomicReference<ResourceLoader>();
  private final CompilerContext.Builder compilerContextBuilder = new CompilerContext.Builder();
  private CompilerContext compilerContext;
  private Options options;

  Recompiler(AppSpace appSpace, String moduleName, Options options, TreeLogger logger) {
    this.appSpace = appSpace;
    this.originalModuleName = moduleName;
    this.options = options;
    this.logger = logger;
    this.serverPrefix = options.getPreferredHost() + ":" + options.getPort();
    compilerContext = compilerContextBuilder.build();
  }

  CompileDir recompile(Map<String, String> bindingProperties, AtomicReference<Progress> progress)
      throws UnableToCompleteException {
    if (options.shouldCompilePerFile()) {
      return compile(bindingProperties, progress, minimalRebuildCache);
    }
    return compile(bindingProperties, progress, new MinimalRebuildCache());
  }

  synchronized CompileDir compile(Map<String, String> bindingProperties,
      AtomicReference<Progress> progress) throws UnableToCompleteException {
    return compile(bindingProperties, progress, new MinimalRebuildCache());
  }

  private synchronized CompileDir compile(Map<String, String> bindingProperties,
      AtomicReference<Progress> progress, MinimalRebuildCache minimalRebuildCache)
      throws UnableToCompleteException {
    if (compilesDone == 0) {
      System.setProperty("java.awt.headless", "true");
      if (System.getProperty("gwt.speedtracerlog") == null) {
        System.setProperty("gwt.speedtracerlog",
            appSpace.getSpeedTracerLogFile().getAbsolutePath());
      }
      compilerContext = compilerContextBuilder.unitCache(
          UnitCacheSingleton.get(logger, appSpace.getUnitCacheDir())).build();
    }

    long startTime = System.currentTimeMillis();
    int compileId = ++compilesDone;
    CompileDir compileDir = makeCompileDir(compileId);
    TreeLogger compileLogger = makeCompileLogger(compileDir);

    boolean listenerFailed = false;
    try {
      options.getRecompileListener().startedCompile(originalModuleName, compileId, compileDir);
    } catch (Exception e) {
      compileLogger.log(TreeLogger.Type.WARN, "listener threw exception", e);
      listenerFailed = true;
    }

    boolean success = false;
    try {
      if (options.shouldCompileIncremental()) {
        // Just have one message for now.
        progress.set(new Progress.Compiling(moduleName.get(), compilesDone, 0, 1, "Compiling"));

        success = compileIncremental(compileLogger, compileDir);
      } else {
        success = compileMonolithic(compileLogger, bindingProperties, compileDir, progress,
            minimalRebuildCache);
      }
    } finally {
      try {
        options.getRecompileListener().finishedCompile(originalModuleName, compilesDone, success);
      } catch (Exception e) {
        compileLogger.log(TreeLogger.Type.WARN, "listener threw exception", e);
        listenerFailed = true;
      }
    }

    if (!success) {
      compileLogger.log(TreeLogger.Type.ERROR, "Compiler returned false");
      throw new UnableToCompleteException();
    }

    // keep the minimal rebuild cache for the next compile
    this.minimalRebuildCache = minimalRebuildCache;

    long elapsedTime = System.currentTimeMillis() - startTime;
    compileLogger.log(TreeLogger.Type.INFO,
        String.format("%.3fs total -- Compile completed", elapsedTime / 1000d));

    if (options.isCompileTest() && listenerFailed) {
      throw new UnableToCompleteException();
    }

    return publishedCompileDir;
  }

  synchronized CompileDir noCompile() throws UnableToCompleteException {
    long startTime = System.currentTimeMillis();
    CompileDir compileDir = makeCompileDir(++compilesDone);
    TreeLogger compileLogger = makeCompileLogger(compileDir);

    ModuleDef module = loadModule(compileLogger);
    String newModuleName = module.getName();  // includes any rename.
    moduleName.set(newModuleName);

    lastBuild.set(compileDir);

    try {
      // Prepare directory.
      File outputDir = new File(
          compileDir.getWarDir().getCanonicalPath() + "/" + getModuleName());
      if (!outputDir.exists()) {
        if (!outputDir.mkdir()) {
          compileLogger.log(TreeLogger.Type.WARN, "cannot create directory: " + outputDir);
        }
      }

      // Creates a "module_name.nocache.js" that just forces a recompile.
      String moduleScript = PageUtil.loadResource(Recompiler.class, "nomodule.nocache.js");
      moduleScript = moduleScript.replace("__MODULE_NAME__", getModuleName());
      PageUtil.writeFile(outputDir.getCanonicalPath() + "/" + getModuleName() + ".nocache.js",
          moduleScript);

    } catch (IOException e) {
      compileLogger.log(TreeLogger.Type.ERROR, "Error creating uncompiled module.", e);
    }
    long elapsedTime = System.currentTimeMillis() - startTime;
    compileLogger.log(TreeLogger.Type.INFO, "Module setup completed in " + elapsedTime + " ms");
    return compileDir;
  }

  private boolean compileIncremental(TreeLogger compileLogger, CompileDir compileDir) {
    BuildResultStatus buildResultStatus;
    // Perform a compile.
    if (incrementalBuilder == null) {
      // If it's the first compile.
      ResourceLoader resources = ResourceLoaders.forClassLoader(Thread.currentThread());
      resources = ResourceLoaders.forPathAndFallback(options.getSourcePath(), resources);
      this.resourceLoader.set(resources);

      incrementalBuilder = new IncrementalBuilder(originalModuleName,
          compileDir.getWarDir().getPath(), compileDir.getWorkDir().getPath(),
          compileDir.getGenDir().getPath(), resourceLoader.get());
      buildResultStatus = incrementalBuilder.build(compileLogger);
    } else {
      // If it's a rebuild.
      incrementalBuilder.setWarDir(compileDir.getWarDir().getPath());
      buildResultStatus = incrementalBuilder.rebuild(compileLogger);
    }

    if (incrementalBuilder.isRootModuleKnown()) {
      moduleName.set(incrementalBuilder.getRootModuleName());
    }
    // Unlike a monolithic compile, the incremental builder can successfully build but have no new
    // output (for example when no files have changed). So it's important to only publish the new
    // compileDir if it actually contains output.
    if (buildResultStatus.isSuccess() && buildResultStatus.outputChanged()) {
      publishedCompileDir = compileDir;
    }
    lastBuild.set(compileDir); // makes compile log available over HTTP

    return buildResultStatus.isSuccess();
  }

  private boolean compileMonolithic(TreeLogger compileLogger, Map<String, String> bindingProperties,
      CompileDir compileDir, AtomicReference<Progress> progress, MinimalRebuildCache rebuildCache)
      throws UnableToCompleteException {

    progress.set(new Progress.Compiling(moduleName.get(), compilesDone, 0, 2, "Loading modules"));

    CompilerOptions loadOptions = new CompilerOptionsImpl(compileDir, originalModuleName, options);
    compilerContext = compilerContextBuilder.options(loadOptions).build();

    ModuleDef module = loadModule(compileLogger);
    bindingProperties = restrictPermutations(logger, module, bindingProperties);

    // Propagates module rename.
    String newModuleName = module.getName();
    moduleName.set(newModuleName);

    // Check if we can skip the compile altogether.
    InputSummary input = new InputSummary(bindingProperties, module);
    if (input.equals(lastBuildInput)) {
      compileLogger.log(Type.INFO, "skipped compile because no input files have changed");
      return true;
    }

    progress.set(new Progress.Compiling(newModuleName, compilesDone, 1, 2, "Compiling"));
    // TODO: use speed tracer to get more compiler events?

    CompilerOptions runOptions = new CompilerOptionsImpl(compileDir, newModuleName, options);
    compilerContext = compilerContextBuilder.options(runOptions).build();

    boolean success = new Compiler(runOptions, rebuildCache).run(compileLogger, module);
    if (success) {
      publishedCompileDir = compileDir;
      lastBuildInput = input;
    } else {
      // always recompile after an error
      lastBuildInput = null;
    }
    lastBuild.set(compileDir); // makes compile log available over HTTP

    return success;
  }

  /**
   * Returns the log from the last compile. (It may be a failed build.)
   */
  File getLastLog() {
    return lastBuild.get().getLogFile();
  }

  String getModuleName() {
    return moduleName.get();
  }

  ResourceLoader getResourceLoader() {
    return resourceLoader.get();
  }

  private TreeLogger makeCompileLogger(CompileDir compileDir)
      throws UnableToCompleteException {
    try {
      PrintWriterTreeLogger fileLogger =
          new PrintWriterTreeLogger(compileDir.getLogFile());
      fileLogger.setMaxDetail(options.getLogLevel());
      return new CompositeTreeLogger(logger, fileLogger);
    } catch (IOException e) {
      logger.log(TreeLogger.ERROR, "unable to open log file: " + compileDir.getLogFile(), e);
      throw new UnableToCompleteException();
    }
  }

  /**
   * Loads the module and configures it for SuperDevMode. (Does not restrict permutations.)
   */
  private ModuleDef loadModule(TreeLogger logger) throws UnableToCompleteException {

    // make sure we get the latest version of any modified jar
    ZipFileClassPathEntry.clearCache();
    ResourceOracleImpl.clearCache();
    ModuleDefLoader.clearModuleCache();

    ResourceLoader resources = ResourceLoaders.forClassLoader(Thread.currentThread());
    resources = ResourceLoaders.forPathAndFallback(options.getSourcePath(), resources);
    this.resourceLoader.set(resources);

    ModuleDef moduleDef = ModuleDefLoader.loadFromResources(
        logger, compilerContext, originalModuleName, resources, true);
    compilerContext = compilerContextBuilder.module(moduleDef).build();

    // A snapshot of the module's configuration before we modified it.
    ConfigProps config = new ConfigProps(moduleDef);

    // We need a cross-site linker. Automatically replace the default linker.
    if (IFrameLinker.class.isAssignableFrom(moduleDef.getActivePrimaryLinker())) {
      moduleDef.addLinker("xsiframe");
    }

    // Check that we have a compatible linker.
    Class<? extends Linker> linker = moduleDef.getActivePrimaryLinker();
    if (!CrossSiteIframeLinker.class.isAssignableFrom(linker)) {
      logger.log(TreeLogger.ERROR,
          "linkers other than CrossSiteIFrameLinker aren't supported. Found: " + linker.getName());
      throw new UnableToCompleteException();
    }

    // Print a nice error if the superdevmode hook isn't present
    if (config.getStrings("devModeRedirectEnabled").isEmpty()) {
      throw new RuntimeException("devModeRedirectEnabled isn't set for module: " +
          moduleDef.getName());
    }

    // Disable the redirect hook here to make sure we don't have an infinite loop.
    // (There is another check in the JavaScript, but just in case.)
    overrideConfig(moduleDef, "devModeRedirectEnabled", "false");

    // Turn off "installCode" if it's on because it makes debugging harder.
    // (If it's already off, don't change anything.)
    if (config.getBoolean("installCode", true)) {
      overrideConfig(moduleDef, "installCode", "false");
      // Make sure installScriptJs is set to the default for compiling without installCode.
      overrideConfig(moduleDef, "installScriptJs",
          "com/google/gwt/core/ext/linker/impl/installScriptDirect.js");
    }

    // override computeScriptBase.js to enable the "Compile" button
    overrideConfig(moduleDef, "computeScriptBaseJs",
        "com/google/gwt/dev/codeserver/computeScriptBase.js");
    // Fix bug with SDM and Chrome 24+ where //@ sourceURL directives cause X-SourceMap header to be ignored
    // Frustratingly, Chrome won't canonicalize a relative URL
    overrideConfig(moduleDef, "includeSourceMapUrl", "http://" + serverPrefix +
        WebServer.sourceMapLocationForModule(moduleDef.getName()));

    // If present, set some config properties back to defaults.
    // (Needed for Google's server-side linker.)
    maybeOverrideConfig(moduleDef, "includeBootstrapInPrimaryFragment", "false");
    maybeOverrideConfig(moduleDef, "permutationsJs",
        "com/google/gwt/core/ext/linker/impl/permutations.js");
    maybeOverrideConfig(moduleDef, "propertiesJs",
        "com/google/gwt/core/ext/linker/impl/properties.js");

    overrideBinding(moduleDef, "compiler.useSourceMaps", "true");
    overrideBinding(moduleDef, "superdevmode", "on");
    return moduleDef;
  }

  /**
   * Restricts the compiled permutations by applying the given binding properties, if possible.
   * In some cases, a different binding may be chosen instead.
   * @return a map of the actual properties used.
   */
  private Map<String, String> restrictPermutations(TreeLogger logger, ModuleDef moduleDef,
      Map<String, String> bindingProperties) {

    Map<String, String> chosenProps = Maps.newHashMap();

    for (Map.Entry<String, String> entry : bindingProperties.entrySet()) {
      String propName = entry.getKey();
      String propValue = entry.getValue();
      String actual = maybeSetBinding(logger, moduleDef, propName, propValue);
      if (actual != null) {
        chosenProps.put(propName, actual);
      }
    }

    return chosenProps;
  }

  /**
   * Attempts to set a binding property to the given value.
   * If the value is not allowed, see if we can find a value that will work.
   * There is a special case for "locale".
   * @return the value actually set, or null if unable to set the property
   */
  private static String maybeSetBinding(TreeLogger logger, ModuleDef module, String propName,
      String newValue) {

    logger = logger.branch(TreeLogger.Type.INFO, "binding: " + propName + "=" + newValue);

    BindingProperty binding = module.getProperties().findBindingProp(propName);
    if (binding == null) {
      logger.log(TreeLogger.Type.WARN, "undefined property: '" + propName + "'");
      return null;
    }

    if (!binding.isAllowedValue(newValue)) {

      String[] allowedValues = binding.getAllowedValues(binding.getRootCondition());
      logger.log(TreeLogger.Type.WARN, "property '" + propName +
          "' cannot be set to '" + newValue + "'");
      logger.log(TreeLogger.Type.INFO, "allowed values: " +
          Joiner.on(", ").join(allowedValues));

      // See if we can fall back on a reasonable default.
      if (allowedValues.length == 1) {
        // There is only one possibility, so use it.
        newValue = allowedValues[0];
      } else if (binding.getName().equals("locale")) {
        // TODO: come up with a more general solution. Perhaps fail
        // the compile and give the user a way to override the property?
        newValue = chooseDefault(binding, "default", "en", "en_US");
      } else {
        // There is more than one. Continue and possibly compile multiple permutations.
        logger.log(TreeLogger.Type.INFO, "continuing without " + propName +
            ". Sourcemaps may not work.");
        return null;
      }

      logger.log(TreeLogger.Type.INFO, "recovered with " + propName + "=" + newValue);
    }

    binding.setAllowedValues(binding.getRootCondition(), newValue);
    return newValue;
  }

  private static String chooseDefault(BindingProperty property, String... candidates) {
    for (String candidate : candidates) {
      if (property.isAllowedValue(candidate)) {
        return candidate;
      }
    }
    return property.getFirstLegalValue();
  }

  /**
   * Sets a binding even if it's set to a different value in the GWT application.
   */
  private static void overrideBinding(ModuleDef module, String propName, String newValue) {
    BindingProperty binding = module.getProperties().findBindingProp(propName);
    if (binding != null) {
      binding.setAllowedValues(binding.getRootCondition(), newValue);
    }
  }

  private static boolean maybeOverrideConfig(ModuleDef module, String propName, String newValue) {
    ConfigurationProperty config = module.getProperties().findConfigProp(propName);
    if (config != null) {
      config.setValue(newValue);
      return true;
    }
    return false;
  }

  private static void overrideConfig(ModuleDef module, String propName, String newValue) {
    if (!maybeOverrideConfig(module, propName, newValue)) {
      throw new RuntimeException("not found: " + propName);
    }
  }

  private CompileDir makeCompileDir(int compileId)
      throws UnableToCompleteException {
    return CompileDir.create(appSpace.getCompileDir(compileId), logger);
  }

  /**
   * Summarizes the inputs to a GWT compile. (Immutable.)
   * Two summaries should be equal if the compiler's inputs are equal (with high probability).
   */
  private static class InputSummary {
    private final ImmutableMap<String, String> bindingProperties;
    private final long moduleLastModified;
    private final long resourcesLastModified;
    private final long filenameHash;

    InputSummary(Map<String, String> bindingProperties, ModuleDef module) {
      this.bindingProperties = ImmutableMap.copyOf(bindingProperties);
      this.moduleLastModified = module.lastModified();
      this.resourcesLastModified = module.getResourceLastModified();
      this.filenameHash = module.getInputFilenameHash();
    }

    @Override
    public boolean equals(Object obj) {
      if (obj instanceof InputSummary) {
        InputSummary other = (InputSummary) obj;
        return bindingProperties.equals(other.bindingProperties) &&
            moduleLastModified == other.moduleLastModified &&
            resourcesLastModified == other.resourcesLastModified &&
            filenameHash == other.filenameHash;
      }
      return false;
    }

    @Override
    public int hashCode() {
      return Objects.hash(bindingProperties, moduleLastModified, resourcesLastModified,
          filenameHash);
    }
  }
}
