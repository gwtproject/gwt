/*
 * Copyright 2008 Google Inc.
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
package com.google.gwt.dev.jjs;

import com.google.gwt.core.ext.PropertyOracle;
import com.google.gwt.core.ext.PropertyOracles;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.linker.Artifact;
import com.google.gwt.core.ext.linker.ArtifactSet;
import com.google.gwt.core.ext.linker.CompilationMetricsArtifact;
import com.google.gwt.core.ext.linker.EmittedArtifact;
import com.google.gwt.core.ext.linker.EmittedArtifact.Visibility;
import com.google.gwt.core.ext.linker.ModuleMetricsArtifact;
import com.google.gwt.core.ext.linker.PrecompilationMetricsArtifact;
import com.google.gwt.core.ext.linker.StatementRanges;
import com.google.gwt.core.ext.linker.SymbolData;
import com.google.gwt.core.ext.linker.SyntheticArtifact;
import com.google.gwt.core.ext.linker.impl.StandardSymbolData;
import com.google.gwt.core.ext.soyc.Range;
import com.google.gwt.core.ext.soyc.SourceMapRecorder;
import com.google.gwt.core.ext.soyc.coderef.DependencyGraphRecorder;
import com.google.gwt.core.ext.soyc.coderef.EntityRecorder;
import com.google.gwt.core.ext.soyc.impl.SizeMapRecorder;
import com.google.gwt.core.ext.soyc.impl.SplitPointRecorder;
import com.google.gwt.core.ext.soyc.impl.StoryRecorder;
import com.google.gwt.core.linker.SoycReportLinker;
import com.google.gwt.dev.CompilerContext;
import com.google.gwt.dev.Permutation;
import com.google.gwt.dev.PrecompileTaskOptions;
import com.google.gwt.dev.cfg.ConfigurationProperty;
import com.google.gwt.dev.cfg.EntryMethodHolderGenerator;
import com.google.gwt.dev.cfg.ModuleDef;
import com.google.gwt.dev.javac.CompilationProblemReporter;
import com.google.gwt.dev.javac.CompilationState;
import com.google.gwt.dev.javac.StandardGeneratorContext;
import com.google.gwt.dev.javac.typemodel.TypeOracle;
import com.google.gwt.dev.jdt.RebindPermutationOracle;
import com.google.gwt.dev.jjs.UnifiedAst.AST;
import com.google.gwt.dev.jjs.ast.Context;
import com.google.gwt.dev.jjs.ast.JBinaryOperation;
import com.google.gwt.dev.jjs.ast.JBinaryOperator;
import com.google.gwt.dev.jjs.ast.JBlock;
import com.google.gwt.dev.jjs.ast.JClassType;
import com.google.gwt.dev.jjs.ast.JDeclaredType;
import com.google.gwt.dev.jjs.ast.JMethod;
import com.google.gwt.dev.jjs.ast.JMethodBody;
import com.google.gwt.dev.jjs.ast.JMethodCall;
import com.google.gwt.dev.jjs.ast.JProgram;
import com.google.gwt.dev.jjs.ast.JStatement;
import com.google.gwt.dev.jjs.ast.JVisitor;
import com.google.gwt.dev.jjs.impl.ArrayNormalizer;
import com.google.gwt.dev.jjs.impl.AssertionNormalizer;
import com.google.gwt.dev.jjs.impl.AssertionRemover;
import com.google.gwt.dev.jjs.impl.AstDumper;
import com.google.gwt.dev.jjs.impl.CastNormalizer;
import com.google.gwt.dev.jjs.impl.CatchBlockNormalizer;
import com.google.gwt.dev.jjs.impl.DeadCodeElimination;
import com.google.gwt.dev.jjs.impl.EnumOrdinalizer;
import com.google.gwt.dev.jjs.impl.EqualityNormalizer;
import com.google.gwt.dev.jjs.impl.Finalizer;
import com.google.gwt.dev.jjs.impl.FixAssignmentsToUnboxOrCast;
import com.google.gwt.dev.jjs.impl.GenerateJavaScriptAST;
import com.google.gwt.dev.jjs.impl.ImplementClassLiteralsAsFields;
import com.google.gwt.dev.jjs.impl.JavaToJavaScriptMap;
import com.google.gwt.dev.jjs.impl.JsAbstractTextTransformer;
import com.google.gwt.dev.jjs.impl.JsFunctionClusterer;
import com.google.gwt.dev.jjs.impl.JsoDevirtualizer;
import com.google.gwt.dev.jjs.impl.LongCastNormalizer;
import com.google.gwt.dev.jjs.impl.LongEmulationNormalizer;
import com.google.gwt.dev.jjs.impl.MakeCallsStatic;
import com.google.gwt.dev.jjs.impl.MethodCallTightener;
import com.google.gwt.dev.jjs.impl.MethodInliner;
import com.google.gwt.dev.jjs.impl.OptimizerStats;
import com.google.gwt.dev.jjs.impl.PostOptimizationCompoundAssignmentNormalizer;
import com.google.gwt.dev.jjs.impl.Pruner;
import com.google.gwt.dev.jjs.impl.RecordRebinds;
import com.google.gwt.dev.jjs.impl.ResolveRebinds;
import com.google.gwt.dev.jjs.impl.SameParameterValueOptimizer;
import com.google.gwt.dev.jjs.impl.SourceInfoCorrelator;
import com.google.gwt.dev.jjs.impl.TypeTightener;
import com.google.gwt.dev.jjs.impl.UnifyAst;
import com.google.gwt.dev.jjs.impl.codesplitter.CodeSplitters;
import com.google.gwt.dev.jjs.impl.codesplitter.MultipleDependencyGraphRecorder;
import com.google.gwt.dev.jjs.impl.codesplitter.ReplaceRunAsyncs;
import com.google.gwt.dev.jjs.impl.gflow.DataflowOptimizer;
import com.google.gwt.dev.js.BaselineCoverageGatherer;
import com.google.gwt.dev.js.ClosureJsRunner;
import com.google.gwt.dev.js.CoverageInstrumentor;
import com.google.gwt.dev.js.EvalFunctionsAtTopScope;
import com.google.gwt.dev.js.FreshNameGenerator;
import com.google.gwt.dev.js.JsBreakUpLargeVarStatements;
import com.google.gwt.dev.js.JsDuplicateCaseFolder;
import com.google.gwt.dev.js.JsDuplicateFunctionRemover;
import com.google.gwt.dev.js.JsInliner;
import com.google.gwt.dev.js.JsNormalizer;
import com.google.gwt.dev.js.JsObfuscateNamer;
import com.google.gwt.dev.js.JsPrettyNamer;
import com.google.gwt.dev.js.JsReportGenerationVisitor;
import com.google.gwt.dev.js.JsSourceGenerationVisitorWithSizeBreakdown;
import com.google.gwt.dev.js.JsStackEmulator;
import com.google.gwt.dev.js.JsStaticEval;
import com.google.gwt.dev.js.JsStringInterner;
import com.google.gwt.dev.js.JsSymbolResolver;
import com.google.gwt.dev.js.JsUnusedFunctionRemover;
import com.google.gwt.dev.js.SizeBreakdown;
import com.google.gwt.dev.js.ast.JsContext;
import com.google.gwt.dev.js.ast.JsForIn;
import com.google.gwt.dev.js.ast.JsFunction;
import com.google.gwt.dev.js.ast.JsLabel;
import com.google.gwt.dev.js.ast.JsName;
import com.google.gwt.dev.js.ast.JsNameOf;
import com.google.gwt.dev.js.ast.JsNameRef;
import com.google.gwt.dev.js.ast.JsNode;
import com.google.gwt.dev.js.ast.JsParameter;
import com.google.gwt.dev.js.ast.JsProgram;
import com.google.gwt.dev.js.ast.JsVars;
import com.google.gwt.dev.js.ast.JsVisitor;
import com.google.gwt.dev.util.DefaultTextOutput;
import com.google.gwt.dev.util.Empty;
import com.google.gwt.dev.util.Memory;
import com.google.gwt.dev.util.Name.SourceName;
import com.google.gwt.dev.util.Pair;
import com.google.gwt.dev.util.Util;
import com.google.gwt.dev.util.arg.OptionOptimize;
import com.google.gwt.dev.util.log.speedtracer.CompilerEventType;
import com.google.gwt.dev.util.log.speedtracer.SpeedTracerLogger;
import com.google.gwt.dev.util.log.speedtracer.SpeedTracerLogger.Event;
import com.google.gwt.soyc.SoycDashboard;
import com.google.gwt.soyc.io.ArtifactsOutputDirectory;
import com.google.gwt.thirdparty.guava.common.collect.Lists;
import com.google.gwt.thirdparty.guava.common.collect.Maps;
import com.google.gwt.thirdparty.guava.common.collect.Multimap;

import org.xml.sax.SAXException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.zip.GZIPInputStream;

import javax.xml.parsers.ParserConfigurationException;

/**
 * Compiles the Java <code>JProgram</code> representation into its corresponding JavaScript source.
 */
public abstract class JavaToJavaScriptCompiler {

  /**
   * Compiles a particular permutation, based on a precompiled unified AST.
   */
  protected abstract class PermutationCompiler {

    private Permutation permutation;

    public PermutationCompiler(Permutation permutation) {
      this.permutation = permutation;
    }

    /**
     * Compiles and returns a particular permutation, based on a precompiled unified AST.
     */
    public PermutationResult compilePermutation(UnifiedAst unifiedAst)
        throws UnableToCompleteException {
      Event jjsCompilePermutationEvent = SpeedTracerLogger.start(
          CompilerEventType.JJS_COMPILE_PERMUTATION, "name", permutation.prettyPrint());
      long permStartMs = System.currentTimeMillis();
      try {
        long startTimeMs = System.currentTimeMillis();
        List<Map<Range, SourceInfo>> sourceInfoMaps = new ArrayList<Map<Range, SourceInfo>>();

        PropertyOracle[] propertyOracles = permutation.getPropertyOracles();
        int permutationId = permutation.getId();
        if (logger.isLoggable(TreeLogger.INFO)) {
          logger.log(TreeLogger.INFO, "Compiling permutation " + permutationId + "...");
        }

        printPermutationTrace(permutation);

        AST ast = unifiedAst.getFreshAst();
        jprogram = ast.getJProgram();
        jsProgram = ast.getJsProgram();

        Map<StandardSymbolData, JsName> symbolTable =
            new TreeMap<StandardSymbolData, JsName>(new SymbolData.ClassIdentComparator());

        ResolveRebinds.exec(jprogram, permutation.getOrderedRebindAnswers());

        // Traverse the AST to figure out which lines are instrumentable for
        // coverage. This has to happen before optimizations because functions might
        // be optimized out; we want those marked as "not executed", not "not
        // instrumentable".
        Multimap<String, Integer> instrumentableLines = null;
        if (System.getProperty("gwt.coverage") != null) {
          instrumentableLines = BaselineCoverageGatherer.exec(jprogram);
        }

        optimizeJava();
        removeEmptySuperCalls();
        normalizeSemantics();
        finalPrune();

        // (7) Generate a JavaScript code DOM from the Java type declarations
        jprogram.typeOracle.recomputeAfterOptimizations();
        Pair<? extends JavaToJavaScriptMap, Set<JsNode>> genAstResult = GenerateJavaScriptAST.exec(
            jprogram, jsProgram, options.getOutput(), symbolTable, propertyOracles);

        JavaToJavaScriptMap jjsmap = genAstResult.getLeft();

        // (8) Normalize the JS AST.
        // Fix invalid constructs created during JS AST gen.
        JsNormalizer.exec(jsProgram);

        /*
         * If coverage is enabled, instrument the AST to record location info.
         */
        if (System.getProperty("gwt.coverage") != null) {
          CoverageInstrumentor.exec(jsProgram, instrumentableLines);
        }

        // Resolve all unresolved JsNameRefs.
        JsSymbolResolver.exec(jsProgram);
        // Move all function definitions to a top-level scope, to reduce weirdness
        EvalFunctionsAtTopScope.exec(jsProgram, jjsmap);

        // (9) Optimize the JS AST.
        if (options.getOptimizationLevel() > OptionOptimize.OPTIMIZE_LEVEL_DRAFT) {
          optimizeJs(genAstResult.getRight());

          /*
           * Coalesce redundant labels in switch statements.
           */
          JsDuplicateCaseFolder.exec(jsProgram);
        }

        /*
         * Creates new variables, must run before code splitter and namer.
         */
        JsStackEmulator.exec(jprogram, jsProgram, propertyOracles, jjsmap);

        Pair<SyntheticArtifact, MultipleDependencyGraphRecorder> dependenciesAndRecorder =
            splitJsIntoFragments(propertyOracles, permutationId, jjsmap);
        Map<JsName, String> obfuscateMap = obfuscateJs(propertyOracles);

        JsBreakUpLargeVarStatements.exec(jsProgram, propertyOracles);

        // (12) Generate the final output text.
        boolean isSourceMapsEnabled = PropertyOracles.findBooleanProperty(
            logger, propertyOracles, "compiler.useSourceMaps", "true", true, false, false);
        String[] jsFragments = new String[jsProgram.getFragmentCount()];
        StatementRanges[] ranges = new StatementRanges[jsFragments.length];
        SizeBreakdown[] sizeBreakdowns = options.isJsonSoycEnabled() || options.isSoycEnabled()
            || options.isCompilerMetricsEnabled() ? new SizeBreakdown[jsFragments.length] : null;
        generateJavaScriptCode(jjsmap, jsFragments, ranges, sizeBreakdowns, sourceInfoMaps,
            isSourceMapsEnabled || options.isJsonSoycEnabled());

        PermutationResult permutationResult =
            new PermutationResultImpl(jsFragments, permutation, makeSymbolMap(symbolTable), ranges);

        addSyntheticArtifacts(unifiedAst, permutation, startTimeMs, permutationId, jjsmap,
            dependenciesAndRecorder, obfuscateMap, isSourceMapsEnabled, jsFragments, sizeBreakdowns,
            sourceInfoMaps, permutationResult);

        return permutationResult;
      } catch (Throwable e) {
        throw CompilationProblemReporter.logAndTranslateException(logger, e);
      } finally {
        jjsCompilePermutationEvent.end();
        logTrackingStats();
        if (logger.isLoggable(TreeLogger.TRACE)) {
          logger.log(TreeLogger.TRACE,
              "Permutation took " + (System.currentTimeMillis() - permStartMs) + " ms");
        }
      }
    }

    protected abstract void finalPrune();

    protected abstract void optimizeJava() throws InterruptedException;

    /**
     * Removes calls to no-op super constructors.
     */
    protected abstract void removeEmptySuperCalls();

    protected abstract Map<JsName, String> runDetailedNamer(
        PropertyOracle[] propertyOracles, Map<JsName, String> obfuscateMap);

    protected abstract Pair<SyntheticArtifact, MultipleDependencyGraphRecorder> splitJsIntoFragments(
        PropertyOracle[] propertyOracles, int permutationId, JavaToJavaScriptMap jjsmap);

    private CompilationMetricsArtifact addCompilerMetricsArtifact(UnifiedAst unifiedAst,
        Permutation permutation, long startTimeMs, SizeBreakdown[] sizeBreakdowns,
        PermutationResult permutationResult) {
      CompilationMetricsArtifact compilationMetrics = null;
      // TODO: enable this when ClosureCompiler is enabled
      if (options.isCompilerMetricsEnabled()) {
        if (options.isClosureCompilerEnabled()) {
          logger.log(TreeLogger.WARN, "Incompatible options: -XenableClosureCompiler and "
              + "-XcompilerMetric; ignoring -XcompilerMetric.");
        } else {
          compilationMetrics = new CompilationMetricsArtifact(permutation.getId());
          compilationMetrics.setCompileElapsedMilliseconds(
              System.currentTimeMillis() - startTimeMs);
          compilationMetrics.setElapsedMilliseconds(
              System.currentTimeMillis() - ManagementFactory.getRuntimeMXBean().getStartTime());
          compilationMetrics.setJsSize(sizeBreakdowns);
          compilationMetrics.setPermutationDescription(permutation.prettyPrint());
          permutationResult.addArtifacts(Lists.newArrayList(
              unifiedAst.getModuleMetrics(), unifiedAst.getPrecompilationMetrics(),
              compilationMetrics));
        }
      }
      return compilationMetrics;
    }

    private void addSourceMapArtifacts(int permutationId, JavaToJavaScriptMap jjsmap,
        Pair<SyntheticArtifact, MultipleDependencyGraphRecorder> dependenciesAndRecorder,
        boolean isSourceMapsEnabled, SizeBreakdown[] sizeBreakdowns,
        List<Map<Range, SourceInfo>> sourceInfoMaps, PermutationResult permutationResult) {
      if (options.isJsonSoycEnabled()) {
        // TODO: enable this when ClosureCompiler is enabled
        if (options.isClosureCompilerEnabled()) {
          logger.log(TreeLogger.WARN, "Incompatible options: -XenableClosureCompiler and "
              + "-XjsonSoyc; ignoring -XjsonSoyc.");
        } else {
          // Is a super set of SourceMapRecorder.makeSourceMapArtifacts().
          permutationResult.addArtifacts(EntityRecorder.makeSoycArtifacts(
              permutationId, sourceInfoMaps, jjsmap, sizeBreakdowns,
              ((DependencyGraphRecorder) dependenciesAndRecorder.getRight()), jprogram));
        }
      } else if (isSourceMapsEnabled) {
        // TODO: enable this when ClosureCompiler is enabled
        if (options.isClosureCompilerEnabled()) {
          logger.log(TreeLogger.WARN, "Incompatible options: -XenableClosureCompiler and "
              + "compiler.useSourceMaps=true; ignoring compiler.useSourceMaps=true.");
        } else {
          logger.log(TreeLogger.INFO, "Source Maps Enabled");
          permutationResult.addArtifacts(
              SourceMapRecorder.makeSourceMapArtifacts(permutationId, sourceInfoMaps));
        }
      }
    }

    private void addSoycArtifacts(UnifiedAst unifiedAst, int permutationId,
        JavaToJavaScriptMap jjsmap,
        Pair<SyntheticArtifact, MultipleDependencyGraphRecorder> dependenciesAndRecorder,
        Map<JsName, String> obfuscateMap, String[] js, SizeBreakdown[] sizeBreakdowns,
        List<Map<Range, SourceInfo>> sourceInfoMaps, PermutationResult permutationResult,
        CompilationMetricsArtifact compilationMetrics)
        throws IOException, UnableToCompleteException {
      // TODO: enable this when ClosureCompiler is enabled
      if (options.isClosureCompilerEnabled()) {
        if (options.isSoycEnabled()) {
          logger.log(TreeLogger.WARN, "Incompatible options: -XenableClosureCompiler and "
              + "-compileReport; ignoring -compileReport.");
        }
      } else {
        permutationResult.addArtifacts(makeSoycArtifacts(permutationId, js, sizeBreakdowns,
            options.isSoycExtra() ? sourceInfoMaps : null, dependenciesAndRecorder.getLeft(),
            jjsmap, obfuscateMap, unifiedAst.getModuleMetrics(),
            unifiedAst.getPrecompilationMetrics(), compilationMetrics,
            options.isSoycHtmlDisabled()));
      }
    }

    private void addSyntheticArtifacts(UnifiedAst unifiedAst, Permutation permutation,
        long startTimeMs, int permutationId, JavaToJavaScriptMap jjsmap,
        Pair<SyntheticArtifact, MultipleDependencyGraphRecorder> dependenciesAndRecorder,
        Map<JsName, String> obfuscateMap, boolean isSourceMapsEnabled, String[] jsFragments,
        SizeBreakdown[] sizeBreakdowns, List<Map<Range, SourceInfo>> sourceInfoMaps,
        PermutationResult permutationResult) throws IOException, UnableToCompleteException {
      CompilationMetricsArtifact compilationMetrics = addCompilerMetricsArtifact(
          unifiedAst, permutation, startTimeMs, sizeBreakdowns, permutationResult);
      addSoycArtifacts(unifiedAst, permutationId, jjsmap, dependenciesAndRecorder, obfuscateMap,
          jsFragments, sizeBreakdowns, sourceInfoMaps, permutationResult, compilationMetrics);
      addSourceMapArtifacts(permutationId, jjsmap, dependenciesAndRecorder, isSourceMapsEnabled,
          sizeBreakdowns, sourceInfoMaps, permutationResult);
    }

    /**
     * Generate JavaScript code from the given JavaScript ASTs. Also produces information about that
     * transformation.
     */
    private void generateJavaScriptCode(JavaToJavaScriptMap jjsMap, String[] jsFragments,
        StatementRanges[] ranges, SizeBreakdown[] sizeBreakdowns,
        List<Map<Range, SourceInfo>> sourceInfoMaps, boolean sourceMapsEnabled) {
      boolean useClosureCompiler = options.isClosureCompilerEnabled();
      if (useClosureCompiler) {
        ClosureJsRunner runner = new ClosureJsRunner();
        runner.compile(jprogram, jsProgram, jsFragments, options.getOutput());
        return;
      }

      for (int i = 0; i < jsFragments.length; i++) {
        DefaultTextOutput out = new DefaultTextOutput(options.getOutput().shouldMinimize());
        JsSourceGenerationVisitorWithSizeBreakdown v;

        if (sourceInfoMaps != null) {
          v = new JsReportGenerationVisitor(out, jjsMap);
        } else {
          v = new JsSourceGenerationVisitorWithSizeBreakdown(out, jjsMap);
        }
        v.accept(jsProgram.getFragmentBlock(i));

        StatementRanges statementRanges = v.getStatementRanges();
        String code = out.toString();
        Map<Range, SourceInfo> infoMap = (sourceInfoMaps != null) ? v.getSourceInfoMap() : null;

        JsAbstractTextTransformer transformer =
            new JsAbstractTextTransformer(code, statementRanges, infoMap) {
                @Override
              public void exec() {
              }

                @Override
              protected void updateSourceInfoMap() {
              }
            };

        /**
         * Reorder function decls to improve compression ratios. Also restructures the top level
         * blocks into sub-blocks if they exceed 32767 statements.
         */
        Event functionClusterEvent = SpeedTracerLogger.start(CompilerEventType.FUNCTION_CLUSTER);
        // TODO(cromwellian) move to the Js AST, re-enable sourcemaps + clustering
        if (!sourceMapsEnabled && options.shouldClusterSimilarFunctions()
        // only cluster for obfuscated mode
            && options.getOutput() == JsOutputOption.OBFUSCATED) {
          transformer = new JsFunctionClusterer(transformer);
          transformer.exec();
        }
        functionClusterEvent.end();

        jsFragments[i] = transformer.getJs();
        ranges[i] = transformer.getStatementRanges();
        if (sizeBreakdowns != null) {
          sizeBreakdowns[i] = v.getSizeBreakdown();
        }
        if (sourceInfoMaps != null) {
          sourceInfoMaps.add(transformer.getSourceInfoMap());
        }
      }
    }

    private Collection<? extends Artifact<?>> makeSoycArtifacts(int permutationId, String[] js,
        SizeBreakdown[] sizeBreakdowns, List<Map<Range, SourceInfo>> sourceInfoMaps,
        SyntheticArtifact dependencies, JavaToJavaScriptMap jjsmap,
        Map<JsName, String> obfuscateMap, ModuleMetricsArtifact moduleMetricsArtifact,
        PrecompilationMetricsArtifact precompilationMetricsArtifact,
        CompilationMetricsArtifact compilationMetrics, boolean htmlReportsDisabled)
        throws IOException, UnableToCompleteException {
      Memory.maybeDumpMemory("makeSoycArtifactsStart");
      List<SyntheticArtifact> soycArtifacts = new ArrayList<SyntheticArtifact>();

      ByteArrayOutputStream baos = new ByteArrayOutputStream();

      Event soycEvent = SpeedTracerLogger.start(CompilerEventType.MAKE_SOYC_ARTIFACTS);

      Event recordSplitPoints = SpeedTracerLogger.start(
          CompilerEventType.MAKE_SOYC_ARTIFACTS, "phase", "recordSplitPoints");
      SplitPointRecorder.recordSplitPoints(jprogram, baos, logger);
      SyntheticArtifact splitPoints = new SyntheticArtifact(
          SoycReportLinker.class, "splitPoints" + permutationId + ".xml.gz", baos.toByteArray());
      soycArtifacts.add(splitPoints);
      recordSplitPoints.end();

      SyntheticArtifact sizeMaps = null;
      if (sizeBreakdowns != null) {
        Event recordSizeMap = SpeedTracerLogger.start(
            CompilerEventType.MAKE_SOYC_ARTIFACTS, "phase", "recordSizeMap");
        baos.reset();
        SizeMapRecorder.recordMap(logger, baos, sizeBreakdowns, jjsmap, obfuscateMap);
        sizeMaps = new SyntheticArtifact(
            SoycReportLinker.class, "stories" + permutationId + ".xml.gz", baos.toByteArray());
        soycArtifacts.add(sizeMaps);
        recordSizeMap.end();
      }

      if (sourceInfoMaps != null) {
        Event recordStories = SpeedTracerLogger.start(
            CompilerEventType.MAKE_SOYC_ARTIFACTS, "phase", "recordStories");
        baos.reset();
        StoryRecorder.recordStories(logger, baos, sourceInfoMaps, js);
        soycArtifacts.add(new SyntheticArtifact(
            SoycReportLinker.class, "detailedStories" + permutationId + ".xml.gz",
            baos.toByteArray()));
        recordStories.end();
      }

      if (dependencies != null) {
        soycArtifacts.add(dependencies);
      }

      // Set all of the main SOYC artifacts private.
      for (SyntheticArtifact soycArtifact : soycArtifacts) {
        soycArtifact.setVisibility(Visibility.Private);
      }

      if (!htmlReportsDisabled && sizeBreakdowns != null) {
        Event generateCompileReport = SpeedTracerLogger.start(
            CompilerEventType.MAKE_SOYC_ARTIFACTS, "phase", "generateCompileReport");
        ArtifactsOutputDirectory outDir = new ArtifactsOutputDirectory();
        SoycDashboard dashboard = new SoycDashboard(outDir);
        dashboard.startNewPermutation(Integer.toString(permutationId));
        try {
          dashboard.readSplitPoints(openWithGunzip(splitPoints));
          if (sizeMaps != null) {
            dashboard.readSizeMaps(openWithGunzip(sizeMaps));
          }
          if (dependencies != null) {
            dashboard.readDependencies(openWithGunzip(dependencies));
          }
          Memory.maybeDumpMemory("soycReadDependenciesEnd");
        } catch (ParserConfigurationException e) {
          throw new InternalCompilerException(
              "Error reading compile report information that was just generated", e);
        } catch (SAXException e) {
          throw new InternalCompilerException(
              "Error reading compile report information that was just generated", e);
        }
        dashboard.generateForOnePermutation();
        if (moduleMetricsArtifact != null && precompilationMetricsArtifact != null
            && compilationMetrics != null) {
          dashboard.generateCompilerMetricsForOnePermutation(
              moduleMetricsArtifact, precompilationMetricsArtifact, compilationMetrics);
        }
        soycArtifacts.addAll(outDir.getArtifacts());
        generateCompileReport.end();
      }

      soycEvent.end();

      return soycArtifacts;
    }

    private SymbolData[] makeSymbolMap(Map<StandardSymbolData, JsName> symbolTable) {
      // Keep tracks of a list of referenced name. If it is not used, don't
      // add it to symbol map.
      final Set<String> nameUsed = new HashSet<String>();
      final Map<JsName, Integer> nameToFragment = new HashMap<JsName, Integer>();

      for (int i = 0; i < jsProgram.getFragmentCount(); i++) {
        final Integer fragId = i;
        new JsVisitor() {
            @Override
          public void endVisit(JsForIn x, JsContext ctx) {
            if (x.getIterVarName() != null) {
              nameUsed.add(x.getIterVarName().getIdent());
            }
          }

            @Override
          public void endVisit(JsFunction x, JsContext ctx) {
            if (x.getName() != null) {
              nameToFragment.put(x.getName(), fragId);
              nameUsed.add(x.getName().getIdent());
            }
          }

            @Override
          public void endVisit(JsLabel x, JsContext ctx) {
            nameUsed.add(x.getName().getIdent());
          }

            @Override
          public void endVisit(JsNameOf x, JsContext ctx) {
            if (x.getName() != null) {
              nameUsed.add(x.getName().getIdent());
            }
          }

            @Override
          public void endVisit(JsNameRef x, JsContext ctx) {
            // Obviously this isn't even that accurate. Some of them are
            // variable names, some of the are property. At least this
            // this give us a safe approximation. Ideally we need
            // the code removal passes to remove stuff in the scope objects.
            if (x.isResolved()) {
              nameUsed.add(x.getName().getIdent());
            }
          }

            @Override
          public void endVisit(JsParameter x, JsContext ctx) {
            nameUsed.add(x.getName().getIdent());
          }

            @Override
          public void endVisit(JsVars.JsVar x, JsContext ctx) {
            nameUsed.add(x.getName().getIdent());
          }

        }.accept(jsProgram.getFragmentBlock(i));
      }

      // TODO(acleung): This is a temp fix. Once we know this is safe. We
      // new to rewrite it to avoid extra ArrayList creations.
      // Or we should just consider serializing it as an ArrayList if
      // it is that much trouble to determine the true size.
      List<SymbolData> result = new ArrayList<SymbolData>();

      for (Map.Entry<StandardSymbolData, JsName> entry : symbolTable.entrySet()) {
        StandardSymbolData symbolData = entry.getKey();
        symbolData.setSymbolName(entry.getValue().getShortIdent());
        Integer fragNum = nameToFragment.get(entry.getValue());
        if (fragNum != null) {
          symbolData.setFragmentNumber(fragNum);
        }
        if (nameUsed.contains(entry.getValue().getIdent())) {
          result.add(symbolData);
        }
      }

      return result.toArray(new SymbolData[result.size()]);
    }

    /**
     * Transform patterns that can't be represented in JS (such as multiple catch blocks) into
     * equivalent but compatible patterns and take JVM semantics (such as numeric casts) that are
     * not explicit in the AST and make them explicit.<br />
     *
     * These passes can not be reordering because of subtle interdependencies.
     */
    private void normalizeSemantics() {
      JsoDevirtualizer.exec(jprogram);
      CatchBlockNormalizer.exec(jprogram);
      PostOptimizationCompoundAssignmentNormalizer.exec(jprogram);
      LongCastNormalizer.exec(jprogram);
      LongEmulationNormalizer.exec(jprogram);
      CastNormalizer.exec(jprogram, options.isCastCheckingDisabled());
      ArrayNormalizer.exec(jprogram, options.isCastCheckingDisabled());
      EqualityNormalizer.exec(jprogram);
    }

    private Map<JsName, String> obfuscateJs(PropertyOracle[] propertyOracles) {
      Map<JsName, String> obfuscateMap = null;
      switch (options.getOutput()) {
        case OBFUSCATED:
          obfuscateMap = runObfuscateNamer(propertyOracles);
          break;
        case PRETTY:
          obfuscateMap = runPrettyNamer(propertyOracles);
          break;
        case DETAILED:
          obfuscateMap = runDetailedNamer(propertyOracles, obfuscateMap);
          break;
        default:
          throw new InternalCompilerException("Unknown output mode");
      }
      return obfuscateMap;
    }

    /**
     * Open an emitted artifact and gunzip its contents.
     */
    private GZIPInputStream openWithGunzip(EmittedArtifact artifact)
        throws IOException, UnableToCompleteException {
      return new GZIPInputStream(artifact.getContents(TreeLogger.NULL));
    }

    private void optimizeJs(Collection<JsNode> toInline) throws InterruptedException {
      List<OptimizerStats> allOptimizerStats = new ArrayList<OptimizerStats>();
      int counter = 0;
      while (true) {
        counter++;
        if (Thread.interrupted()) {
          throw new InterruptedException();
        }
        Event optimizeJsEvent = SpeedTracerLogger.start(CompilerEventType.OPTIMIZE_JS);

        OptimizerStats stats = new OptimizerStats("Pass " + counter);

        // Remove unused functions, possible
        stats.add(JsStaticEval.exec(jsProgram));
        // Inline JavaScript function invocations
        stats.add(JsInliner.exec(jsProgram, toInline));
        // Remove unused functions, possible
        stats.add(JsUnusedFunctionRemover.exec(jsProgram));

        // Save the stats to print out after optimizers finish.
        allOptimizerStats.add(stats);

        optimizeJsEvent.end();
        int optimizationLevel = options.getOptimizationLevel();
        if ((optimizationLevel < OptionOptimize.OPTIMIZE_LEVEL_MAX && counter > optimizationLevel)
            || !stats.didChange()) {
          break;
        }
      }

      printJsOptimizeTrace(allOptimizerStats);
    }

    private void printJsOptimizeTrace(List<OptimizerStats> allOptimizerStats) {
      if (JProgram.isTracingEnabled()) {
        System.out.println("");
        System.out.println("               JavaScript Optimization Stats");
        System.out.println("");
        for (OptimizerStats stats : allOptimizerStats) {
          System.out.println(stats.prettyPrint());
        }
      }
    }

    private void printPermutationTrace(Permutation permutation) {
      if (JProgram.isTracingEnabled()) {
        System.out.println("-------------------------------------------------------------");
        System.out.println("|                     (new permutation)                     |");
        System.out.println("-------------------------------------------------------------");
        System.out.println("Properties: " + permutation.prettyPrint());
      }
    }

    private Map<JsName, String> runObfuscateNamer(PropertyOracle[] propertyOracles) {
      Map<JsName, String> obfuscateMap;
      obfuscateMap = JsStringInterner.exec(jprogram, jsProgram);
      FreshNameGenerator freshNameGenerator = JsObfuscateNamer.exec(jsProgram, propertyOracles);
      if (options.shouldRemoveDuplicateFunctions()
          && JsStackEmulator.getStackMode(propertyOracles) == JsStackEmulator.StackMode.STRIP) {
        JsDuplicateFunctionRemover.exec(jsProgram, freshNameGenerator);
      }
      return obfuscateMap;
    }

    private Map<JsName, String> runPrettyNamer(PropertyOracle[] propertyOracles) {
      // We don't intern strings in pretty mode to improve readability
      JsPrettyNamer.exec(jsProgram, propertyOracles);
      return Maps.newHashMap();
    }
  }

  /**
   * Performs a precompilation, creating a unified AST.
   */
  protected abstract class Precompiler {

    protected RebindPermutationOracle rpo;

    public Precompiler(RebindPermutationOracle rpo) {
      this.rpo = rpo;
    }

    protected abstract void beforeUnifyAst(Set<String> allRootTypes)
        throws UnableToCompleteException;

    protected abstract void checkEntryPoints(
        String[] entryPointTypeNames, String[] additionalRootTypes);

    protected abstract void createJProgram();

    protected abstract JMethodCall createReboundModuleLoad(SourceInfo info,
        JDeclaredType reboundEntryType, String originalMainClassName, JDeclaredType enclosingType)
        throws UnableToCompleteException;

    protected abstract void populateEntryPointRootTypes(
        String[] entryPointTypeNames, Set<String> allRootTypes) throws UnableToCompleteException;

    /**
     * Performs a precompilation, returning a unified AST.
     */
    protected final UnifiedAst precompile(String[] entryPointTypeNames,
        String[] additionalRootTypes, boolean singlePermutation,
        PrecompilationMetricsArtifact precompilationMetrics) throws UnableToCompleteException {
      try {
        Set<String> allRootTypes = new TreeSet<String>();
        jsProgram = new JsProgram();
        if (additionalRootTypes == null) {
          additionalRootTypes = Empty.STRINGS;
        }

        checkEntryPoints(entryPointTypeNames, additionalRootTypes);
        createJProgram();
        CompilationState compilationState = rpo.getCompilationState();
        Memory.maybeDumpMemory("CompStateBuilt");
        TypeOracle typeOracle = compilationState.getTypeOracle();
        populateRootTypes(allRootTypes, entryPointTypeNames, additionalRootTypes, typeOracle);
        String entryMethodHolderTypeName =
            buildEntryMethodHolder(rpo.getGeneratorContext(), allRootTypes);
        beforeUnifyAst(allRootTypes);
        unifyJavaAst(entryPointTypeNames, allRootTypes, entryMethodHolderTypeName);

        List<String> finalTypeOracleTypes = Lists.newArrayList();
        if (precompilationMetrics != null) {
          for (com.google.gwt.core.ext.typeinfo.JClassType type : typeOracle.getTypes()) {
            finalTypeOracleTypes.add(type.getPackage().getName() + "." + type.getName());
          }
          precompilationMetrics.setFinalTypeOracleTypes(finalTypeOracleTypes);
        }

        // Free up memory.
        rpo.clear();

        if (options.isSoycEnabled() || options.isJsonSoycEnabled()) {
          SourceInfoCorrelator.exec(jprogram);
        }

        // Compute all super type/sub type info
        jprogram.typeOracle.computeBeforeAST();

        Memory.maybeDumpMemory("AstOnly");
        AstDumper.maybeDumpAST(jprogram);

        // See if we should run the EnumNameObfuscator
        if (module != null) {
          ConfigurationProperty enumNameObfuscationProp =
              (ConfigurationProperty) module.getProperties().find(ENUM_NAME_OBFUSCATION_PROPERTY);
          if (enumNameObfuscationProp != null
              && Boolean.parseBoolean(enumNameObfuscationProp.getValue())) {
            EnumNameObfuscator.exec(jprogram, logger);
          }
        }

        // (3) Perform Java AST normalizations.
        FixAssignmentsToUnboxOrCast.exec(jprogram);

        if (options.isEnableAssertions()) {
          // Turn into assertion checking calls.
          AssertionNormalizer.exec(jprogram);
        } else {
          // Remove all assert statements.
          AssertionRemover.exec(jprogram);
        }

        // Fix up GWT.runAsync()
        if (module != null && options.isRunAsyncEnabled()) {
          ReplaceRunAsyncs.exec(logger, jprogram);
          CodeSplitters.pickInitialLoadSequence(logger, jprogram, module.getProperties());
        }

        ImplementClassLiteralsAsFields.exec(jprogram);

        optimizeJava(singlePermutation);

        Set<String> rebindRequests = new HashSet<String>();
        RecordRebinds.exec(jprogram, rebindRequests);

        if (options.isCompilerMetricsEnabled()) {
          precompilationMetrics.setAstTypes(getReferencedJavaClasses());
        }

        Event createUnifiedAstEvent = SpeedTracerLogger.start(CompilerEventType.CREATE_UNIFIED_AST);
        UnifiedAst result = new UnifiedAst(
            options, new AST(jprogram, jsProgram), singlePermutation, rebindRequests);
        createUnifiedAstEvent.end();
        return result;
      } catch (Throwable e) {
        throw CompilationProblemReporter.logAndTranslateException(logger, e);
      } finally {
        logTrackingStats();
      }
    }

    protected abstract void rebindEntryPoint(SourceInfo info, JMethod bootStrapMethod, JBlock block,
        String mainClassName, JDeclaredType mainType) throws UnableToCompleteException;

    /**
     * Creates (and returns the name for) a new class to serve as the container for the invocation
     * of registered entry point methods as part of module bootstrapping.<br />
     *
     * The resulting class will be invoked during bootstrapping like FooEntryMethodHolder.init(). By
     * generating the class on the fly and naming it to match the current module, the resulting
     * holder class can work in both monolithic and separate compilation schemes.
     */
    private String buildEntryMethodHolder(
        StandardGeneratorContext context, Set<String> allRootTypes)
        throws UnableToCompleteException {
      EntryMethodHolderGenerator entryMethodHolderGenerator = new EntryMethodHolderGenerator();
      String entryMethodHolderTypeName =
          entryMethodHolderGenerator.generate(logger, context, module.getCanonicalName());
      context.finish(logger);
      // Ensures that unification traverses and keeps the class.
      allRootTypes.add(entryMethodHolderTypeName);
      // Ensures that JProgram knows to index this class's methods so that later bootstrap
      // construction code is able to locate the FooEntryMethodHolder.init() function.
      jprogram.addIndexedTypeName(entryMethodHolderTypeName);
      return entryMethodHolderTypeName;
    }

    /**
     * This method can be used to fetch the list of referenced class.
     *
     * This method is intended to support compiler metrics in the precompile phase.
     */
    private String[] getReferencedJavaClasses() {
      class ClassNameVisitor extends JVisitor {
        List<String> classNames = new ArrayList<String>();

      @Override
        public boolean visit(JClassType x, Context ctx) {
          classNames.add(x.getName());
          return true;
        }
      }
      ClassNameVisitor v = new ClassNameVisitor();
      v.accept(jprogram);
      return v.classNames.toArray(new String[v.classNames.size()]);
    }

    /**
     * Create a variable assignment to invoke a call to the statistics collector.
     *
     * <pre>
     * Stats.isStatsAvailable() &&
     * Stats.onModuleStart("mainClassName");
     * </pre>
     */
    private JStatement makeStatsCalls(SourceInfo info, String mainClassName) {
      JMethod isStatsAvailableMethod = jprogram.getIndexedMethod("Stats.isStatsAvailable");
      JMethod onModuleStartMethod = jprogram.getIndexedMethod("Stats.onModuleStart");

      JMethodCall availableCall = new JMethodCall(info, null, isStatsAvailableMethod);
      JMethodCall onModuleStartCall = new JMethodCall(info, null, onModuleStartMethod);
      onModuleStartCall.addArg(jprogram.getLiteralString(info, mainClassName));

      JBinaryOperation amp = new JBinaryOperation(
          info, jprogram.getTypePrimitiveBoolean(), JBinaryOperator.AND, availableCall,
          onModuleStartCall);

      return amp.makeStatement();
    }

    private void optimizeJava(boolean singlePermutation) throws InterruptedException {
      if (options.getOptimizationLevel() > OptionOptimize.OPTIMIZE_LEVEL_DRAFT
          && !singlePermutation) {
        if (options.isOptimizePrecompile()) {
          /*
           * Go ahead and optimize early, so that each permutation will run faster. This code path
           * is used by the Compiler entry point. We assume that we will not be able to perfectly
           * parallelize the permutation compiles, so let's optimize as much as possible the common
           * AST. In some cases, this might also have the side benefit of reducing the total
           * permutation count.
           */
          optimizeJavaToFixedPoint();
        } else {
          /*
           * Do only minimal early optimizations. This code path is used by the Precompile entry
           * point. The external system might be able to perfectly parallelize the permutation
           * compiles, so let's avoid doing potentially superlinear optimizations on the unified
           * AST.
           */
          optimizeJavaOneTime("Early Optimization", jprogram.getNodeCount());
        }
      }
    }

    private void populateRootTypes(Set<String> allRootTypes, String[] entryPointTypeNames,
        String[] additionalRootTypes, TypeOracle typeOracle) throws UnableToCompleteException {
      populateEntryPointRootTypes(entryPointTypeNames, allRootTypes);
      Collections.addAll(allRootTypes, additionalRootTypes);
      allRootTypes.addAll(JProgram.CODEGEN_TYPES_SET);
      allRootTypes.addAll(jprogram.getTypeNamesToIndex());
      /*
       * Add all SingleJsoImpl types that we know about. It's likely that the concrete types are
       * never explicitly referenced.
       */
      for (com.google.gwt.core.ext.typeinfo.JClassType singleJsoIntf :
          typeOracle.getSingleJsoImplInterfaces()) {
        allRootTypes.add(typeOracle.getSingleJsoImpl(singleJsoIntf).getQualifiedSourceName());
      }
    }

    private void rebindEntryPoints(String[] mainClassNames, String entryMethodHolderTypeName)
        throws UnableToCompleteException {
      Event findEntryPointsEvent = SpeedTracerLogger.start(CompilerEventType.FIND_ENTRY_POINTS);
      JMethod bootStrapMethod = jprogram.getIndexedMethod(
          SourceName.getShortClassName(entryMethodHolderTypeName) + ".init");

      JMethodBody body = (JMethodBody) bootStrapMethod.getBody();
      JBlock block = body.getBlock();
      SourceInfo info = block.getSourceInfo().makeChild();

      // Also remember $entry, which we'll handle specially in GenerateJsAst
      JMethod registerEntry = jprogram.getIndexedMethod("Impl.registerEntry");
      jprogram.addEntryMethod(registerEntry);

      for (String mainClassName : mainClassNames) {
        block.addStmt(makeStatsCalls(info, mainClassName));
        JDeclaredType mainType = jprogram.getFromTypeMap(mainClassName);

        if (mainType == null) {
          logger.log(TreeLogger.ERROR,
              "Could not find module entry point class '" + mainClassName + "'", null);
          throw new UnableToCompleteException();
        }

        JMethod mainMethod = findMainMethod(mainType);
        if (mainMethod != null && mainMethod.isStatic()) {
          JMethodCall onModuleLoadCall = new JMethodCall(info, null, mainMethod);
          block.addStmt(onModuleLoadCall.makeStatement());
          continue;
        }

        rebindEntryPoint(info, bootStrapMethod, block, mainClassName, mainType);
      }

      jprogram.addEntryMethod(bootStrapMethod);
      findEntryPointsEvent.end();
    }

    private void unifyJavaAst(
        String[] entryPointTypeNames, Set<String> allRootTypes, String entryMethodHolderTypeName)
        throws UnableToCompleteException {
      UnifyAst unifyAst = new UnifyAst(logger, compilerContext, jprogram, jsProgram, rpo);
      unifyAst.addRootTypes(allRootTypes);
      rebindEntryPoints(entryPointTypeNames, entryMethodHolderTypeName);
      unifyAst.exec();
    }
  }

  private static class PermutationResultImpl implements PermutationResult {

    private final ArtifactSet artifacts = new ArtifactSet();
    private final byte[][] js;
    private final String jsStrongName;
    private final Permutation permutation;
    private final byte[] serializedSymbolMap;
    private final StatementRanges[] statementRanges;

    public PermutationResultImpl(String[] jsFragments, Permutation permutation,
        SymbolData[] symbolMap, StatementRanges[] statementRanges) {
      byte[][] bytes = new byte[jsFragments.length][];
      for (int i = 0; i < jsFragments.length; ++i) {
        bytes[i] = Util.getBytes(jsFragments[i]);
      }
      this.js = bytes;
      this.jsStrongName = Util.computeStrongName(bytes);
      this.permutation = permutation;
      try {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Util.writeObjectToStream(baos, (Object) symbolMap);
        this.serializedSymbolMap = baos.toByteArray();
      } catch (IOException e) {
        throw new RuntimeException("Should never happen with in-memory stream", e);
      }
      this.statementRanges = statementRanges;
    }

    @Override
    public void addArtifacts(Collection<? extends Artifact<?>> newArtifacts) {
      this.artifacts.addAll(newArtifacts);
    }

    @Override
    public ArtifactSet getArtifacts() {
      return artifacts;
    }

    @Override
    public byte[][] getJs() {
      return js;
    }

    @Override
    public String getJsStrongName() {
      return jsStrongName;
    }

    @Override
    public Permutation getPermutation() {
      return permutation;
    }

    @Override
    public byte[] getSerializedSymbolMap() {
      return serializedSymbolMap;
    }

    @Override
    public StatementRanges[] getStatementRanges() {
      return statementRanges;
    }
  }

  /**
   * Ending optimization passes when the rate of change has reached this value results in gaining
   * nearly all of the impact while avoiding the long tail of costly but low-impact passes.
   */
  private static final float EFFICIENT_CHANGE_RATE = 0.01f;

  private static final String ENUM_NAME_OBFUSCATION_PROPERTY = "compiler.enum.obfuscate.names";

  /**
   * Continuing to apply optimizations till the rate of change reaches this value causes the AST to
   * reach a fixed point.
   */
  private static final int FIXED_POINT_CHANGE_RATE = 0;

  /**
   * Limits the number of optimization passes against the possible danger of an AST that does not
   * converge.
   */
  private static final int MAX_PASSES = 100;

  static {
    InternalCompilerException.preload();
  }

  protected final CompilerContext compilerContext;

  protected JProgram jprogram;

  protected JsProgram jsProgram;

  protected final TreeLogger logger;

  protected final ModuleDef module;

  protected final PrecompileTaskOptions options;

  public JavaToJavaScriptCompiler(TreeLogger logger, CompilerContext compilerContext) {
    this.logger = logger;
    this.compilerContext = compilerContext;
    this.module = compilerContext.getModule();
    this.options = compilerContext.getOptions();
  }

  /**
   * Compiles and returns a particular permutation, based on a precompiled unified AST.
   */
  public abstract PermutationResult compilePermutation(
      UnifiedAst unifiedAst, Permutation permutation) throws UnableToCompleteException;

  /**
   * Performs a precompilation, returning a unified AST.
   */
  public UnifiedAst precompile(RebindPermutationOracle rpo, String[] entryPointTypeNames,
      String[] additionalRootTypes, boolean singlePermutation) throws UnableToCompleteException {
    return precompile(rpo, entryPointTypeNames, additionalRootTypes, singlePermutation, null);
  }

  /**
   * Performs a precompilation, returning a unified AST.
   */
  public abstract UnifiedAst precompile(RebindPermutationOracle rpo, String[] entryPointTypeNames,
      String[] additionalRootTypes, boolean singlePermutation,
      PrecompilationMetricsArtifact precompilationMetrics) throws UnableToCompleteException;

  protected final JMethod findMainMethod(JDeclaredType declaredType) {
    for (JMethod method : declaredType.getMethods()) {
      if (method.getName().equals("onModuleLoad")) {
        if (method.getParams().size() == 0) {
          return method;
        }
      }
    }
    return null;
  }

  protected final void optimizeJavaToFixedPoint() throws InterruptedException {
    Event optimizeEvent = SpeedTracerLogger.start(CompilerEventType.OPTIMIZE);

    List<OptimizerStats> allOptimizerStats = new ArrayList<OptimizerStats>();
    int passCount = 0;
    int nodeCount = jprogram.getNodeCount();
    int lastNodeCount;

    boolean atMaxLevel = options.getOptimizationLevel() == OptionOptimize.OPTIMIZE_LEVEL_MAX;
    int passLimit = atMaxLevel ? MAX_PASSES : options.getOptimizationLevel();
    float minChangeRate = atMaxLevel ? FIXED_POINT_CHANGE_RATE : EFFICIENT_CHANGE_RATE;
    while (true) {
      passCount++;
      if (passCount > passLimit) {
        break;
      }
      if (Thread.interrupted()) {
        optimizeEvent.end();
        throw new InterruptedException();
      }
      AstDumper.maybeDumpAST(jprogram);
      OptimizerStats stats = optimizeJavaOneTime("Pass " + passCount, nodeCount);
      allOptimizerStats.add(stats);
      lastNodeCount = nodeCount;
      nodeCount = jprogram.getNodeCount();

      float nodeChangeRate = stats.getNumMods() / (float) lastNodeCount;
      float sizeChangeRate = (lastNodeCount - nodeCount) / (float) lastNodeCount;
      if (nodeChangeRate <= minChangeRate && sizeChangeRate <= minChangeRate) {
        break;
      }
    }

    if (options.shouldOptimizeDataflow()) {
      // Just run it once, because it is very time consuming
      allOptimizerStats.add(DataflowOptimizer.exec(jprogram));
    }

    printJavaOptimizeTrace(allOptimizerStats);

    optimizeEvent.end();
  }

  /*
   * This method is intended as a central location for producing optional tracking output. This will
   * be called after all optimization/normalization passes have completed.
   */
  private void logTrackingStats() {
    EnumOrdinalizer.Tracker eot = EnumOrdinalizer.getTracker();
    if (eot != null) {
      eot.logResultsDetailed(logger, TreeLogger.WARN);
    }
  }

  private OptimizerStats optimizeJavaOneTime(String passName, int numNodes) {
    Event optimizeEvent = SpeedTracerLogger.start(CompilerEventType.OPTIMIZE, "phase", "loop");
    // Clinits might have become empty become empty.
    jprogram.typeOracle.recomputeAfterOptimizations();
    OptimizerStats stats = new OptimizerStats(passName);
    stats.add(Pruner.exec(jprogram, true).recordVisits(numNodes));
    stats.add(Finalizer.exec(jprogram).recordVisits(numNodes));
    stats.add(MakeCallsStatic.exec(jprogram).recordVisits(numNodes));
    stats.add(TypeTightener.exec(jprogram).recordVisits(numNodes));
    stats.add(MethodCallTightener.exec(jprogram).recordVisits(numNodes));
    stats.add(DeadCodeElimination.exec(jprogram).recordVisits(numNodes));
    stats.add(MethodInliner.exec(jprogram).recordVisits(numNodes));
    if (options.shouldInlineLiteralParameters()) {
      stats.add(SameParameterValueOptimizer.exec(jprogram).recordVisits(numNodes));
    }
    if (options.shouldOrdinalizeEnums()) {
      stats.add(EnumOrdinalizer.exec(jprogram).recordVisits(numNodes));
    }
    optimizeEvent.end();
    return stats;
  }

  private void printJavaOptimizeTrace(List<OptimizerStats> allOptimizerStats) {
    if (JProgram.isTracingEnabled()) {
      System.out.println("");
      System.out.println("                Java Optimization Stats");
      System.out.println("");
      for (OptimizerStats stats : allOptimizerStats) {
        System.out.println(stats.prettyPrint());
      }
    }
  }
}
