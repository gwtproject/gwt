/*
 * Copyright 2014 Google Inc.
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
import com.google.gwt.core.ext.linker.PrecompilationMetricsArtifact;
import com.google.gwt.core.ext.linker.SyntheticArtifact;
import com.google.gwt.core.ext.soyc.coderef.DependencyGraphRecorder;
import com.google.gwt.core.ext.soyc.impl.DependencyRecorder;
import com.google.gwt.core.linker.SoycReportLinker;
import com.google.gwt.dev.CompilerContext;
import com.google.gwt.dev.Permutation;
import com.google.gwt.dev.jdt.RebindPermutationOracle;
import com.google.gwt.dev.jjs.ast.JBlock;
import com.google.gwt.dev.jjs.ast.JClassType;
import com.google.gwt.dev.jjs.ast.JDeclaredType;
import com.google.gwt.dev.jjs.ast.JExpression;
import com.google.gwt.dev.jjs.ast.JGwtCreate;
import com.google.gwt.dev.jjs.ast.JMethod;
import com.google.gwt.dev.jjs.ast.JMethodCall;
import com.google.gwt.dev.jjs.ast.JProgram;
import com.google.gwt.dev.jjs.ast.JReboundEntryPoint;
import com.google.gwt.dev.jjs.impl.ControlFlowAnalyzer;
import com.google.gwt.dev.jjs.impl.DeadCodeElimination;
import com.google.gwt.dev.jjs.impl.Finalizer;
import com.google.gwt.dev.jjs.impl.HandleCrossFragmentReferences;
import com.google.gwt.dev.jjs.impl.JavaToJavaScriptMap;
import com.google.gwt.dev.jjs.impl.MakeCallsStatic;
import com.google.gwt.dev.jjs.impl.Pruner;
import com.google.gwt.dev.jjs.impl.RemoveEmptySuperCalls;
import com.google.gwt.dev.jjs.impl.ReplaceGetClassOverrides;
import com.google.gwt.dev.jjs.impl.codesplitter.CodeSplitter;
import com.google.gwt.dev.jjs.impl.codesplitter.CodeSplitters;
import com.google.gwt.dev.jjs.impl.codesplitter.MultipleDependencyGraphRecorder;
import com.google.gwt.dev.js.JsLiteralInterner;
import com.google.gwt.dev.js.JsVerboseNamer;
import com.google.gwt.dev.js.ast.JsLiteral;
import com.google.gwt.dev.js.ast.JsName;
import com.google.gwt.dev.util.Pair;
import com.google.gwt.dev.util.arg.OptionOptimize;
import com.google.gwt.dev.util.log.speedtracer.CompilerEventType;
import com.google.gwt.dev.util.log.speedtracer.SpeedTracerLogger;
import com.google.gwt.dev.util.log.speedtracer.SpeedTracerLogger.Event;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Compiles the Java <code>JProgram</code> representation into its corresponding library Js source.
 * <br />
 *
 * Care is taken to ensure that the resulting Js source will be valid for runtime linking, such as
 * performing only local optimizations, running only local stages of Generators, gathering and
 * enqueueing rebind information for runtime usage and outputting Js source with names that are
 * stable across libraries.
 */
public class MonolithicJavaToJavaScriptCompiler extends JavaToJavaScriptCompiler {

  private class MonolithicPermutationCompiler extends PermutationCompiler {

    public MonolithicPermutationCompiler(Permutation permutation) {
      super(permutation);
    }

    @Override
    protected void optimizeJava() throws InterruptedException {
      if (options.getOptimizationLevel() == OptionOptimize.OPTIMIZE_LEVEL_DRAFT) {
        optimizeJavaForDraft();
      } else {
        optimizeJavaToFixedPoint();
      }
      RemoveEmptySuperCalls.exec(jprogram);
    }

    @Override
    protected void postNormalizationOptimizeJava() {
      Pruner.exec(jprogram, false);
      ReplaceGetClassOverrides.exec(jprogram);
    }

    @Override
    protected Map<JsName, JsLiteral> runDetailedNamer(PropertyOracle[] propertyOracles) {
      Map<JsName, JsLiteral> internedTextByVariableName =
          JsLiteralInterner.exec(jprogram, jsProgram, JsLiteralInterner.INTERN_ALL);
      JsVerboseNamer.exec(jsProgram, propertyOracles);
      return internedTextByVariableName;
    }

    @Override
    protected Pair<SyntheticArtifact, MultipleDependencyGraphRecorder> splitJsIntoFragments(
        PropertyOracle[] propertyOracles, int permutationId, JavaToJavaScriptMap jjsmap) {
      Pair<SyntheticArtifact, MultipleDependencyGraphRecorder> dependenciesAndRecorder = null;
      MultipleDependencyGraphRecorder dependencyRecorder = null;
      SyntheticArtifact dependencies = null;
      if (options.isRunAsyncEnabled()) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        int expectedFragmentCount = options.getFragmentCount();
        // -1 is the default value, we trap 0 just in case (0 is not a legal value in any case)
        if (expectedFragmentCount <= 0) {
          // Fragment count not set check fragments merge.
          int numberOfMerges = options.getFragmentsMerge();
          if (numberOfMerges > 0) {
            // + 1 for left over, + 1 for initial gave us the total number
            // of fragments without splitting.
            expectedFragmentCount =
                Math.max(0, jprogram.getRunAsyncs().size() + 2 - numberOfMerges);
          }
        }

        int minFragmentSize = PropertyOracles.findIntegerConfigurationProperty(
            propertyOracles, CodeSplitters.MIN_FRAGMENT_SIZE, 0);

        dependencyRecorder = chooseDependencyRecorder(baos);
        CodeSplitter.exec(logger, jprogram, jsProgram, jjsmap, expectedFragmentCount,
            minFragmentSize, dependencyRecorder);

        if (baos.size() == 0) {
          dependencyRecorder = recordNonSplitDependencies(baos);
        }
        if (baos.size() > 0) {
          dependencies = new SyntheticArtifact(
              SoycReportLinker.class, "dependencies" + permutationId + ".xml.gz",
              baos.toByteArray());
        }
      } else if (options.isSoycEnabled() || options.isJsonSoycEnabled()) {
        dependencyRecorder = recordNonSplitDependencies(new ByteArrayOutputStream());
      }
      dependenciesAndRecorder = Pair.<SyntheticArtifact, MultipleDependencyGraphRecorder> create(
          dependencies, dependencyRecorder);

      // No new JsNames or references to JSNames can be introduced after this
      // point.
      HandleCrossFragmentReferences.exec(logger, jsProgram, propertyOracles);

      return dependenciesAndRecorder;
    }

    private MultipleDependencyGraphRecorder chooseDependencyRecorder(OutputStream out) {
      MultipleDependencyGraphRecorder dependencyRecorder =
          MultipleDependencyGraphRecorder.NULL_RECORDER;
      if (options.isSoycEnabled() && options.isJsonSoycEnabled()) {
        dependencyRecorder = new DependencyGraphRecorder(out, jprogram);
      } else if (options.isSoycEnabled()) {
        dependencyRecorder = new DependencyRecorder(out);
      } else if (options.isJsonSoycEnabled()) {
        dependencyRecorder = new DependencyGraphRecorder(out, jprogram);
      }
      return dependencyRecorder;
    }

    /**
     * Perform the minimal amount of optimization to make sure the compile succeeds.
     */
    private void optimizeJavaForDraft() {
      Event draftOptimizeEvent = SpeedTracerLogger.start(CompilerEventType.DRAFT_OPTIMIZE);
      Finalizer.exec(jprogram);
      MakeCallsStatic.exec(options, jprogram);
      jprogram.typeOracle.recomputeAfterOptimizations();
      // needed for certain libraries that depend on dead stripping to work
      DeadCodeElimination.exec(jprogram);
      Pruner.exec(jprogram, true);
      jprogram.typeOracle.recomputeAfterOptimizations();
      draftOptimizeEvent.end();
    }

    /**
     * Dependency information is normally recorded during code splitting, and it results in multiple
     * dependency graphs. If the code splitter doesn't run, then this method can be used instead to
     * record a single dependency graph for the whole program.
     */
    private DependencyRecorder recordNonSplitDependencies(OutputStream out) {
      DependencyRecorder deps;
      if (options.isSoycEnabled() && options.isJsonSoycEnabled()) {
        deps = new DependencyGraphRecorder(out, jprogram);
      } else if (options.isSoycEnabled()) {
        deps = new DependencyRecorder(out);
      } else if (options.isJsonSoycEnabled()) {
        deps = new DependencyGraphRecorder(out, jprogram);
      } else {
        return null;
      }
      deps.open();
      deps.startDependencyGraph("initial", null);

      ControlFlowAnalyzer cfa = new ControlFlowAnalyzer(jprogram);
      cfa.setDependencyRecorder(deps);
      cfa.traverseEntryMethods();
      deps.endDependencyGraph();
      deps.close();
      return deps;
    }
  }

  private class MonolithicPrecompiler extends Precompiler {

    public MonolithicPrecompiler(RebindPermutationOracle rpo) {
      super(rpo);
    }

    @Override
    protected void beforeUnifyAst(Set<String> allRootTypes) throws UnableToCompleteException {
    }

    @Override
    protected void checkEntryPoints(String[] entryPointTypeNames, String[] additionalRootTypes) {
      if (entryPointTypeNames.length + additionalRootTypes.length == 0) {
        throw new IllegalArgumentException("entry point(s) required");
      }
    }

    @Override
    protected void createJProgram() {
      jprogram = new JProgram();
    }

    @Override
    protected JMethodCall createReboundModuleLoad(SourceInfo info, JDeclaredType reboundEntryType,
        String originalMainClassName, JDeclaredType enclosingType)
        throws UnableToCompleteException {
      if (!(reboundEntryType instanceof JClassType)) {
        logger.log(TreeLogger.ERROR,
            "Module entry point class '" + originalMainClassName + "' must be a class", null);
        throw new UnableToCompleteException();
      }

      JClassType entryClass = (JClassType) reboundEntryType;
      if (entryClass.isAbstract()) {
        logger.log(TreeLogger.ERROR,
            "Module entry point class '" + originalMainClassName + "' must not be abstract", null);
        throw new UnableToCompleteException();
      }

      JMethod entryMethod = findMainMethodRecurse(entryClass);
      if (entryMethod == null) {
        logger.log(TreeLogger.ERROR,
            "Could not find entry method 'onModuleLoad()' method in entry point class '"
            + originalMainClassName + "'", null);
        throw new UnableToCompleteException();
      }

      if (entryMethod.isAbstract()) {
        logger.log(TreeLogger.ERROR, "Entry method 'onModuleLoad' in entry point class '"
            + originalMainClassName + "' must not be abstract", null);
        throw new UnableToCompleteException();
      }

      JExpression qualifier = null;
      if (!entryMethod.isStatic()) {
        qualifier = JGwtCreate.createInstantiationExpression(info, entryClass, enclosingType);

        if (qualifier == null) {
          logger.log(TreeLogger.ERROR,
              "No default (zero argument) constructor could be found in entry point class '"
              + originalMainClassName
              + "' to qualify a call to non-static entry method 'onModuleLoad'", null);
          throw new UnableToCompleteException();
        }
      }
      return new JMethodCall(info, qualifier, entryMethod);
    }

    @Override
    protected void populateEntryPointRootTypes(
        String[] entryPointTypeNames, Set<String> allRootTypes) throws UnableToCompleteException {
      // Find all the possible rebinds for declared entry point types.
      for (String element : entryPointTypeNames) {
        String[] all = rpo.getAllPossibleRebindAnswers(logger, element);
        Collections.addAll(allRootTypes, all);
      }
      rpo.getGeneratorContext().finish(logger);
    }

    @Override
    protected void rebindEntryPoint(SourceInfo info, JMethod bootStrapMethod, JBlock block,
        String mainClassName, JDeclaredType mainType) throws UnableToCompleteException {
      String[] resultTypeNames = rpo.getAllPossibleRebindAnswers(logger, mainClassName);
      List<JClassType> resultTypes = new ArrayList<JClassType>();
      List<JExpression> entryCalls = new ArrayList<JExpression>();
      for (String resultTypeName : resultTypeNames) {
        JDeclaredType resultType = jprogram.getFromTypeMap(resultTypeName);
        if (resultType == null) {
          logger.log(TreeLogger.ERROR, "Could not find module entry point class '" + resultTypeName
              + "' after rebinding from '" + mainClassName + "'", null);
          throw new UnableToCompleteException();
        }

        JMethodCall onModuleLoadCall = createReboundModuleLoad(
            info, resultType, mainClassName, bootStrapMethod.getEnclosingType());
        resultTypes.add((JClassType) resultType);
        entryCalls.add(onModuleLoadCall);
      }
      if (resultTypes.size() == 1) {
        block.addStmt(entryCalls.get(0).makeStatement());
      } else {
        JReboundEntryPoint reboundEntryPoint =
            new JReboundEntryPoint(info, mainType, resultTypes, entryCalls);
        block.addStmt(reboundEntryPoint);
      }
    }

    private JMethod findMainMethodRecurse(JDeclaredType declaredType) {
      for (JDeclaredType it = declaredType; it != null; it = it.getSuperClass()) {
        JMethod result = findMainMethod(it);
        if (result != null) {
          return result;
        }
      }
      return null;
    }
  }

  /**
   * Constructs a JavaToJavaScriptCompiler with customizations for compiling independent libraries.
   */
  public MonolithicJavaToJavaScriptCompiler(TreeLogger logger, CompilerContext compilerContext) {
    super(logger, compilerContext);
  }

  @Override
  public PermutationResult compilePermutation(UnifiedAst unifiedAst, Permutation permutation)
      throws UnableToCompleteException {
    return new MonolithicPermutationCompiler(permutation).compilePermutation(unifiedAst);
  }

  @Override
  public UnifiedAst precompile(RebindPermutationOracle rpo, String[] entryPointTypeNames,
      String[] additionalRootTypes, boolean singlePermutation,
      PrecompilationMetricsArtifact precompilationMetrics) throws UnableToCompleteException {
    return new MonolithicPrecompiler(rpo).precompile(
        entryPointTypeNames, additionalRootTypes, singlePermutation, precompilationMetrics);
  }
}
