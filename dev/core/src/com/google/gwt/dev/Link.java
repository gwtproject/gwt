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
package com.google.gwt.dev;

import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.linker.Artifact;
import com.google.gwt.core.ext.linker.ArtifactSet;
import com.google.gwt.core.ext.linker.EmittedArtifact;
import com.google.gwt.core.ext.linker.EmittedArtifact.Visibility;
import com.google.gwt.core.ext.linker.SelectionProperty;
import com.google.gwt.core.ext.linker.impl.BinaryOnlyArtifactWrapper;
import com.google.gwt.core.ext.linker.impl.JarEntryEmittedArtifact;
import com.google.gwt.core.ext.linker.impl.StandardCompilationResult;
import com.google.gwt.core.ext.linker.impl.StandardLinkerContext;
import com.google.gwt.dev.CompileTaskRunner.CompileTask;
import com.google.gwt.dev.cfg.BindingProperties;
import com.google.gwt.dev.cfg.BindingProperty;
import com.google.gwt.dev.cfg.ModuleDef;
import com.google.gwt.dev.cfg.ModuleDefLoader;
import com.google.gwt.dev.cfg.PropertyCombinations;
import com.google.gwt.dev.cfg.ResourceLoader;
import com.google.gwt.dev.cfg.ResourceLoaders;
import com.google.gwt.dev.jjs.PermutationResult;
import com.google.gwt.dev.jjs.impl.codesplitter.CodeSplitter;
import com.google.gwt.dev.resource.ResourceOracle;
import com.google.gwt.dev.util.NullOutputFileSet;
import com.google.gwt.dev.util.OutputFileSet;
import com.google.gwt.dev.util.OutputFileSetOnDirectory;
import com.google.gwt.dev.util.OutputFileSetOnJar;
import com.google.gwt.dev.util.PersistenceBackedObject;
import com.google.gwt.dev.util.Util;
import com.google.gwt.dev.util.arg.ArgHandlerDeployDir;
import com.google.gwt.dev.util.arg.ArgHandlerExtraDir;
import com.google.gwt.dev.util.arg.ArgHandlerJsInteropMode;
import com.google.gwt.dev.util.arg.ArgHandlerSaveSourceOutput;
import com.google.gwt.dev.util.arg.ArgHandlerWarDir;
import com.google.gwt.dev.util.arg.OptionDeployDir;
import com.google.gwt.dev.util.arg.OptionExtraDir;
import com.google.gwt.dev.util.arg.OptionJsInteropMode;
import com.google.gwt.dev.util.arg.OptionSaveSourceOutput;
import com.google.gwt.dev.util.arg.OptionWarDir;
import com.google.gwt.dev.util.log.speedtracer.CompilerEventType;
import com.google.gwt.dev.util.log.speedtracer.SpeedTracerLogger;
import com.google.gwt.dev.util.log.speedtracer.SpeedTracerLogger.Event;
import com.google.gwt.thirdparty.guava.common.collect.Sets;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;

/**
 * Performs the last phase of compilation, merging the compilation outputs.
 */
public class Link {
  /**
   * Options for Link.
   */
  public interface LinkOptions extends OptionExtraDir,
      OptionWarDir, OptionDeployDir, OptionSaveSourceOutput, CompileTaskOptions,
      OptionJsInteropMode {
  }

  static class ArgProcessor extends CompileArgProcessor {
    public ArgProcessor(LinkOptions options) {
      super(options);
      registerHandler(new ArgHandlerExtraDir(options));
      registerHandler(new ArgHandlerWarDir(options));
      registerHandler(new ArgHandlerDeployDir(options));
      registerHandler(new ArgHandlerSaveSourceOutput(options));
      registerHandler(new ArgHandlerJsInteropMode(options));
    }

    @Override
    protected String getName() {
      return Link.class.getName();
    }
  }

  /**
   * Concrete class to implement link options.
   */
  static class LinkOptionsImpl extends CompileTaskOptionsImpl implements
      LinkOptions {

    private File deployDir;
    private File extraDir;
    private File warDir;
    private File debugSourceDir;

    public LinkOptionsImpl() {
    }

    public LinkOptionsImpl(LinkOptions other) {
      copyFrom(other);
    }

    public void copyFrom(LinkOptions other) {
      super.copyFrom(other);
      setDeployDir(other.getDeployDir());
      setExtraDir(other.getExtraDir());
      setSaveSourceOutput(other.getSaveSourceOutput());
      setWarDir(other.getWarDir());
    }

    @Override
    public File getSaveSourceOutput() {
      return debugSourceDir;
    }

    @Override
    public File getDeployDir() {
      return (deployDir == null) ? new File(warDir, "WEB-INF/deploy")
          : deployDir;
    }

    @Override
    public File getExtraDir() {
      return extraDir;
    }

    @Override
    public File getWarDir() {
      return warDir;
    }

    @Override
    public void setSaveSourceOutput(File dest) {
      this.debugSourceDir = dest;
    }

    @Override
    public void setDeployDir(File dir) {
      deployDir = dir;
    }

    @Override
    public void setExtraDir(File extraDir) {
      this.extraDir = extraDir;
    }

    @Override
    public void setWarDir(File warDir) {
      this.warDir = warDir;
    }
  }

  public static void link(TreeLogger logger, ModuleDef module, ResourceOracle publicResourceOracle,
      ArtifactSet generatedArtifacts, Permutation[] permutations,
      List<PersistenceBackedObject<PermutationResult>> resultFiles,
      Set<PermutationResult> libraries, PrecompileTaskOptions precompileOptions,
      LinkOptions linkOptions)
      throws UnableToCompleteException, IOException {
    StandardLinkerContext linkerContext =
        new StandardLinkerContext(logger, module, publicResourceOracle,
            precompileOptions.getOutput());
    ArtifactSet artifacts = doSimulatedShardingLink(
        logger, module, linkerContext, generatedArtifacts, permutations, resultFiles);

    doProduceOutput(logger, artifacts, linkerContext, module, precompileOptions.shouldSaveSource(),
        linkOptions);
  }

  /**
   * This link operation is performed on a CompilePerms shard for one
   * permutation. It sees the generated artifacts for one permutation compile,
   * and it runs the per-permutation part of each shardable linker.
   */
  public static void linkOnePermutationToJar(TreeLogger logger,
      ModuleDef module, ResourceOracle publicResourceOracle, ArtifactSet generatedArtifacts,
      PermutationResult permResult, File jarFile,
      PrecompileTaskOptions precompileOptions) throws UnableToCompleteException {
    try {
      if (jarFile.exists()) {
        boolean success = jarFile.delete();
        if (!success) {
          logger.log(TreeLogger.ERROR, "Linker output file " + jarFile.getName()
              + " already exists and can't be deleted.");
        }
      }
      JarOutputStream jar = new JarOutputStream(new FileOutputStream(jarFile));

      StandardLinkerContext linkerContext = new StandardLinkerContext(logger,
          module, publicResourceOracle, precompileOptions.getOutput());

      StandardCompilationResult compilation = new StandardCompilationResult(
          permResult);
      addSelectionPermutations(compilation, permResult.getPermutation(),
          linkerContext);
      ArtifactSet permArtifacts = new ArtifactSet(generatedArtifacts);
      permArtifacts.addAll(permResult.getArtifacts());
      permArtifacts.add(compilation);

      ArtifactSet linkedArtifacts = linkerContext.invokeLinkForOnePermutation(
          logger, compilation, permArtifacts);

      // Write the data of emitted artifacts
      for (EmittedArtifact art : linkedArtifacts.find(EmittedArtifact.class)) {
        Visibility visibility = art.getVisibility();
        String jarEntryPath = visibility.name() + "/";
        if (visibility == Visibility.Public) {
          jarEntryPath += art.getPartialPath();
        } else {
          jarEntryPath += prefixArtifactPath(art, linkerContext);
        }
        ZipEntry ze = new ZipEntry(jarEntryPath);
        ze.setTime(OutputFileSetOnJar.normalizeTimestamps ? 0 : art.getLastModified());
        jar.putNextEntry(ze);
        art.writeTo(logger, jar);
        jar.closeEntry();
      }

      // Serialize artifacts marked as Transferable
      int numSerializedArtifacts = 0;
      // The raw type Artifact is to work around a Java compiler bug:
      // http://bugs.sun.com/view_bug.do?bug_id=6548436
      for (Artifact art : linkedArtifacts) {
        if (art.isTransferableFromShards() && !(art instanceof EmittedArtifact)) {
          String jarEntryPath = "arts/" + numSerializedArtifacts++;
          ZipEntry ze = new ZipEntry(jarEntryPath);
          if (OutputFileSetOnJar.normalizeTimestamps) {
            ze.setTime(0);
          }
          jar.putNextEntry(ze);
          Util.writeObjectToStream(jar, art);
          jar.closeEntry();
        }
      }

      jar.close();
    } catch (IOException e) {
      logger.log(TreeLogger.ERROR, "Error linking", e);
      throw new UnableToCompleteException();
    }
  }

  public static void main(String[] args) {
    boolean success = false;
    Event linkEvent = SpeedTracerLogger.start(CompilerEventType.LINK);
    /*
     * NOTE: main always exits with a call to System.exit to terminate any
     * non-daemon threads that were started in Generators. Typically, this is to
     * shutdown AWT related threads, since the contract for their termination is
     * still implementation-dependent.
     */
    final LinkOptions options = new LinkOptionsImpl();

    if (new ArgProcessor(options).processArgs(args)) {
      CompileTask task = new CompileTask() {
        @Override
        public boolean run(TreeLogger logger) throws UnableToCompleteException {
          return new Link(options).run(logger);
        }
      };
      if (CompileTaskRunner.runWithAppropriateLogger(options, task)) {
        success = true;
      }
    }
    linkEvent.end();
    System.exit(success ? 0 : 1);
  }

  /**
   * In a parallel build, artifact sets are thinned down in transit between
   * compilation and linking. All emitted artifacts are changed to binary
   * emitted artifacts, and all other artifacts are dropped except @Transferable
   * ones. This method simulates the thinning that happens in a parallel build.
   */
  @SuppressWarnings("rawtypes")
  public static ArtifactSet simulateTransferThinning(ArtifactSet artifacts,
      StandardLinkerContext context) {
    ArtifactSet thinnedArtifacts = new ArtifactSet();
    // The raw type Artifact is to work around a compiler bug:
    // http://bugs.sun.com/view_bug.do?bug_id=6548436
    for (Artifact artifact : artifacts) {
      if (artifact instanceof EmittedArtifact) {
        EmittedArtifact emittedArtifact = (EmittedArtifact) artifact;
        String path = getFullArtifactPath(emittedArtifact, context);
        thinnedArtifacts.add(new BinaryOnlyArtifactWrapper(path,
            emittedArtifact));
      } else if (artifact.isTransferableFromShards()) {
        thinnedArtifacts.add(artifact);
      }
    }

    return thinnedArtifacts;
  }

  /**
   * Add to a compilation result all of the selection permutations from its
   * associated permutation.
   */
  private static void addSelectionPermutations(
      StandardCompilationResult compilation, Permutation permutation,
      StandardLinkerContext linkerContext) {
    for (BindingProperties properties : permutation.getProperties().getSoftProperties()) {
      compilation.addSelectionPermutation(computeSelectionPermutation(linkerContext, properties));
      compilation.addSoftPermutation(computeSoftPermutation(linkerContext, properties));
    }
  }

  /**
   * Choose an output file set for the given <code>dirOrJar</code> based on its
   * name, whether it's null, and whether it already exists as a directory.
   */
  static OutputFileSet chooseOutputFileSet(File dirOrJar,
      String pathPrefix) throws IOException {
    if (dirOrJar == null) {
      return new NullOutputFileSet();
    }

    String name = dirOrJar.getName();
    if (!dirOrJar.isDirectory() && (name.endsWith(".war")
        || name.endsWith(".jar") || name.endsWith(".zip"))) {
      return new OutputFileSetOnJar(dirOrJar, pathPrefix);
    } else {
      Util.recursiveDelete(new File(dirOrJar, pathPrefix), true);
      return new OutputFileSetOnDirectory(dirOrJar, pathPrefix);
    }
  }

  /**
   * Return a map giving the value of each non-trivial selection property.
   */
  private static Map<SelectionProperty, String> computeSelectionPermutation(
      StandardLinkerContext linkerContext, BindingProperties properties) {
    BindingProperty[] orderedProps = properties.getOrderedProps();
    String[] orderedPropValues = properties.getOrderedPropValues();
    Map<SelectionProperty, String> unboundProperties = new HashMap<SelectionProperty, String>();
    for (int i = 0; i < orderedProps.length; i++) {
      SelectionProperty key = linkerContext.getProperty(orderedProps[i].getName());
      if (key.tryGetValue() != null) {
        /*
         * The view of the Permutation doesn't include properties with defined
         * values.
         */
        continue;
      } else if (key.isDerived()) {
        /*
         * The property provider does not need to be invoked, because the value
         * is determined entirely by other properties.
         */
        continue;
      }
      unboundProperties.put(key, orderedPropValues[i]);
    }
    return unboundProperties;
  }

  private static Map<SelectionProperty, String> computeSoftPermutation(
      StandardLinkerContext linkerContext, BindingProperties properties) {
    BindingProperty[] orderedProps = properties.getOrderedProps();
    String[] orderedPropValues = properties.getOrderedPropValues();
    Map<SelectionProperty, String> softProperties = new HashMap<SelectionProperty, String>();
    for (int i = 0; i < orderedProps.length; i++) {
      if (orderedProps[i].getCollapsedValuesSets().isEmpty()) {
        continue;
      }

      SelectionProperty key = linkerContext.getProperty(orderedProps[i].getName());
      softProperties.put(key, orderedPropValues[i]);
    }
    return softProperties;
  }

  /**
   * Emit final output.
   */
  private static void doProduceOutput(TreeLogger logger, ArtifactSet artifacts,
      StandardLinkerContext linkerContext, ModuleDef module, boolean saveSources,
      LinkOptions options) throws IOException, UnableToCompleteException {

    // == create output filesets ==

    String destPrefix = module.getName() + "/";

    OutputFileSet outFileSet = chooseOutputFileSet(options.getWarDir(), destPrefix);
    OutputFileSet extraFileSet = chooseOutputFileSet(options.getExtraDir(), destPrefix);

    // allow -deploy and -extra to point to the same directory/jar
    OutputFileSet deployFileSet;
    if (options.getDeployDir().equals(options.getExtraDir())) {
      deployFileSet = extraFileSet;
    } else {
      deployFileSet = chooseOutputFileSet(options.getDeployDir(), destPrefix);
    }

    // == write the output ==

    linkerContext.produceOutput(logger, artifacts, Visibility.Public,
        outFileSet);
    linkerContext.produceOutput(logger, artifacts, Visibility.Deploy,
        deployFileSet);
    linkerContext.produceOutput(logger, artifacts, Visibility.Private,
        extraFileSet);

    if (saveSources) {
      // Assume that all source code is available in the compiler's classpath.
      // (This will have to be adjusted to work with Super Dev Mode.)
      ResourceLoader loader = ResourceLoaders.forClassLoader(Thread.currentThread());
      SourceSaver.save(logger, artifacts, loader, options, destPrefix, extraFileSet);
    }

    outFileSet.close();
    extraFileSet.close();
    if (deployFileSet != extraFileSet) {
      deployFileSet.close();
    }

    logger.log(TreeLogger.INFO, "Link succeeded");
  }

  /**
   * This link operation simulates sharded linking even though all generating
   * and linking is happening on the same computer. It can tolerate
   * non-shardable linkers.
   */
  private static ArtifactSet doSimulatedShardingLink(TreeLogger logger, ModuleDef module,
      StandardLinkerContext linkerContext, ArtifactSet generatedArtifacts, Permutation[] perms,
      List<PersistenceBackedObject<PermutationResult>> resultFiles)
      throws UnableToCompleteException {
    ArtifactSet combinedArtifacts = new ArtifactSet();
    for (int i = 0; i < perms.length; ++i) {
      ArtifactSet newArtifacts = finishPermutation(
          logger, perms[i], resultFiles.get(i), linkerContext, generatedArtifacts);
      combinedArtifacts.addAll(newArtifacts);
    }

    combinedArtifacts.addAll(linkerContext.getArtifactsForPublicResources(
        logger, module));

    ArtifactSet legacyLinkedArtifacts = linkerContext.invokeLegacyLinkers(
        logger, combinedArtifacts);

    ArtifactSet thinnedArtifacts = simulateTransferThinning(
        legacyLinkedArtifacts, linkerContext);

    return linkerContext.invokeFinalLink(logger, thinnedArtifacts);
  }

  /**
   * Add a compilation to a linker context. Also runs the shardable part of all
   * linkers that support sharding.
   *
   * @return the new artifacts generated by the shardable part of this link
   *         operation
   */
  private static ArtifactSet finishPermutation(TreeLogger logger, Permutation perm,
      PersistenceBackedObject<PermutationResult> resultFile, StandardLinkerContext linkerContext,
      ArtifactSet generatedArtifacts)
      throws UnableToCompleteException {
    PermutationResult permResult = resultFile.newInstance(logger);
    StandardCompilationResult compilation =
        new StandardCompilationResult(permResult);
    addSelectionPermutations(compilation, perm, linkerContext);
    logScriptSize(logger, perm.getId(), compilation);

    ArtifactSet permArtifacts = new ArtifactSet(generatedArtifacts);
    permArtifacts.addAll(permResult.getArtifacts());
    permArtifacts.add(compilation);
    permArtifacts.freeze();
    return linkerContext.invokeLinkForOnePermutation(logger, compilation,
        permArtifacts);
  }

  private static String getFullArtifactPath(EmittedArtifact emittedArtifact,
      StandardLinkerContext context) {
    String path = emittedArtifact.getPartialPath();
    if (emittedArtifact.getVisibility() != Visibility.Public) {
      path = prefixArtifactPath(emittedArtifact, context);
    }
    return path;
  }

  /**
   * Logs the total script size for this permutation, as calculated by
   * CodeSplitter2#totalScriptSize(int[]).
   */
  private static void logScriptSize(TreeLogger logger, int permId,
      StandardCompilationResult compilation) {
    if (!logger.isLoggable(TreeLogger.TRACE)) {
      return;
    }

    String[] javaScript = compilation.getJavaScript();

    int[] jsLengths = new int[javaScript.length];
    for (int i = 0; i < javaScript.length; i++) {
      jsLengths[i] = javaScript[i].length();
    }

    // TODO(acleung): This is broken for CodeSplitter2.
    int totalSize = CodeSplitter.computeTotalSize(jsLengths);

    if (logger.isLoggable(TreeLogger.TRACE)) {
      logger.log(TreeLogger.TRACE, "Permutation " + permId + " (strong name "
          + compilation.getStrongName() + ") has an initial download size of "
          + javaScript[0].length() + " and total script size of " + totalSize);
    }
  }

  /**
   * Prefix an artifact's partial path with the linker name and make sure it is
   * a relative pathname.
   *
   * @param art
   * @param linkerContext
   * @return prefixed path
   */
  private static String prefixArtifactPath(EmittedArtifact art,
      StandardLinkerContext linkerContext) {
    String pathWithLinkerName = linkerContext.getExtraPathForLinker(
        art.getLinker(), art.getPartialPath());
    if (pathWithLinkerName.startsWith("/")) {
      // This happens if the linker has no extra path
      pathWithLinkerName = pathWithLinkerName.substring(1);
    }
    return pathWithLinkerName;
  }

  private static ArtifactSet scanCompilePermResults(TreeLogger logger,
      List<File> resultFiles) throws IOException, UnableToCompleteException {
    final ArtifactSet artifacts = new ArtifactSet();

    for (File resultFile : resultFiles) {
      JarFile jarFile = null;
      try {
        jarFile = new JarFile(resultFile);
      } catch (ZipException ze) {
        logger.log(TreeLogger.ERROR, "Error opening " + resultFile
            + " as jar file.", ze);
        throw new UnableToCompleteException();
      }

      Enumeration<JarEntry> entries = jarFile.entries();
      while (entries.hasMoreElements()) {
        JarEntry entry = entries.nextElement();
        if (entry.isDirectory()) {
          continue;
        }

        String path;
        Artifact<?> artForEntry = null;

        String entryName = entry.getName();
        if (entryName.startsWith("arts/")) {
          try {
            artForEntry = Util.readStreamAsObject(new BufferedInputStream(
                jarFile.getInputStream(entry)), Artifact.class);
            assert artForEntry.isTransferableFromShards();
          } catch (ClassNotFoundException e) {
            logger.log(TreeLogger.ERROR,
                "Failed trying to deserialize an artifact", e);
            throw new UnableToCompleteException();
          }
        } else {
          int slash = entryName.indexOf('/');
          if (slash >= 0) {
            try {
              Visibility visibility = Visibility.valueOf(entryName.substring(0,
                  slash));
              path = entryName.substring(slash + 1);
              JarEntryEmittedArtifact jarArtifact = new JarEntryEmittedArtifact(
                  path, resultFile, entry);
              jarArtifact.setVisibility(visibility);
              artForEntry = jarArtifact;
            } catch (IllegalArgumentException e) {
              // silently ignore paths with invalid visibilities
              continue;
            }
          }
        }

        artifacts.add(artForEntry);
      }

      jarFile.close();
    }

    return artifacts;
  }

  private final LinkOptionsImpl options;

  private final CompilerContext.Builder compilerContextBuilder = new CompilerContext.Builder();

  private CompilerContext compilerContext;

  public Link(LinkOptions options) {
    this.options = new LinkOptionsImpl(options);
    compilerContext =
        compilerContextBuilder.options(new PrecompileTaskOptionsImpl(options)).build();
  }

  public boolean run(TreeLogger logger) throws UnableToCompleteException {
    loop_modules : for (String moduleName : options.getModuleNames()) {
      ModuleDef module =
          ModuleDefLoader.loadFromClassPath(logger, compilerContext, moduleName);
      compilerContext = compilerContextBuilder.module(module).build();
      ResourceOracle publicResourceOracle = compilerContext.getPublicResourceOracle();

      File compilerWorkDir = options.getCompilerWorkDir(moduleName);

      // Look for the compilerOptions file output from running AnalyzeModule
      PrecompileTaskOptions precompileOptions = AnalyzeModule.readAnalyzeModuleOptionsFile(
          logger, compilerWorkDir);

      PrecompilationResult precompileResults = null;
      if (precompileOptions == null) {
        // Check for the output from Precompile where precompiling has
        // been delegated to shards.
        File precompilationFile = new File(compilerWorkDir,
            Precompile.PRECOMPILE_FILENAME);
        precompileResults = CompilePerms.readPrecompilationFile(logger,
            precompilationFile);
        if (precompileResults == null) {
          return false;
        }
        if (precompileResults instanceof PrecompileTaskOptions) {
          precompileOptions = (PrecompileTaskOptions) precompileResults;
        }
      }

      if (precompileOptions != null) {
        /**
         * Precompiling happened on the shards.
         */
        if (!doLinkFinal(
            logger, compilerWorkDir, module, publicResourceOracle, precompileOptions)) {
          return false;
        }
        continue loop_modules;
      } else {
        /**
         * Precompiling happened on the start node.
         */
        Precompilation precomp = (Precompilation) precompileResults;
        Permutation[] perms = precomp.getPermutations();
        List<PersistenceBackedObject<PermutationResult>> resultFiles =
            CompilePerms.makeResultFiles(compilerWorkDir, perms, compilerContext.getOptions());

        // Check that all files are present
        for (PersistenceBackedObject<PermutationResult> file : resultFiles) {
          if (!file.exists()) {
            logger.log(TreeLogger.ERROR,
                "File not found '" + file.getPath() + "'; please compile all permutations");
            return false;
          }
        }

        TreeLogger branch = logger.branch(TreeLogger.INFO, "Linking module "
            + module.getName());

        try {
          link(branch, module, publicResourceOracle, precomp.getGeneratedArtifacts(), perms,
              resultFiles, Sets.<PermutationResult> newHashSet(),
              precomp.getUnifiedAst().getOptions(), options);
        } catch (IOException e) {
          logger.log(TreeLogger.ERROR,
              "Unexpected exception while producing output", e);
          throw new UnableToCompleteException();
        }
      }
    }
    return true;
  }

  /**
   * Do a final link, assuming the precompiles were done on the CompilePerms
   * shards. Returns true if successful.
   */
  private boolean doLinkFinal(TreeLogger logger, File compilerWorkDir, ModuleDef module,
      ResourceOracle publicResourceOracle, PrecompileTaskOptions precompileOptions)
      throws UnableToCompleteException {
    int numPermutations = new PropertyCombinations(module.getProperties(),
        module.getActiveLinkerNames()).collapseProperties().size();
    List<File> resultFiles = new ArrayList<File>(numPermutations);
    for (int i = 0; i < numPermutations; ++i) {
      File f = CompilePerms.makePermFilename(compilerWorkDir, i);
      if (!f.exists()) {
        logger.log(TreeLogger.ERROR, "File not found '" + f.getAbsolutePath()
            + "'; please compile all permutations");
        return false;
      }
      resultFiles.add(f);
    }

    TreeLogger branch = logger.branch(TreeLogger.INFO, "Linking module "
        + module.getName());
    StandardLinkerContext linkerContext = new StandardLinkerContext(branch,
        module, publicResourceOracle, precompileOptions.getOutput());

    try {
      ArtifactSet artifacts = scanCompilePermResults(logger, resultFiles);
      artifacts.addAll(linkerContext.getArtifactsForPublicResources(logger,
          module));
      artifacts = linkerContext.invokeFinalLink(logger, artifacts);

      doProduceOutput(logger, artifacts, linkerContext, module,
          precompileOptions.shouldSaveSource(), options);
    } catch (IOException e) {
      logger.log(TreeLogger.ERROR, "Exception during final linking", e);
      throw new UnableToCompleteException();
    }

    return true;
  }
}
