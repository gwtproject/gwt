/*
 * Copyright 2014 Google Inc.
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

import com.google.gwt.core.ext.PropertyOracle;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.impl.ResourceGeneratorUtilImpl;
import com.google.gwt.core.ext.linker.SyntheticArtifact;
import com.google.gwt.dev.CompilerContext;
import com.google.gwt.dev.cfg.BindingProperty;
import com.google.gwt.dev.cfg.ConfigurationProperty;
import com.google.gwt.dev.cfg.PropertyProviderRegistratorGenerator;
import com.google.gwt.dev.cfg.Rule;
import com.google.gwt.dev.cfg.RuleBaseline;
import com.google.gwt.dev.cfg.RuleGenerateWith;
import com.google.gwt.dev.cfg.RuntimeRebindRegistratorGenerator;
import com.google.gwt.dev.javac.CompilationUnit;
import com.google.gwt.dev.javac.LibraryGroupUnitCache;
import com.google.gwt.dev.javac.StandardGeneratorContext;
import com.google.gwt.dev.jdt.RebindPermutationOracle;
import com.google.gwt.dev.jjs.ast.JBlock;
import com.google.gwt.dev.jjs.ast.JDeclaredType;
import com.google.gwt.dev.jjs.ast.JMethod;
import com.google.gwt.dev.jjs.ast.JMethodCall;
import com.google.gwt.dev.jjs.ast.JProgram;
import com.google.gwt.dev.jjs.impl.DeadCodeElimination;
import com.google.gwt.dev.jjs.impl.Finalizer;
import com.google.gwt.dev.jjs.impl.JavaToJavaScriptMap;
import com.google.gwt.dev.jjs.impl.ReboundTypeRecorder;
import com.google.gwt.dev.jjs.impl.codesplitter.MultipleDependencyGraphRecorder;
import com.google.gwt.dev.js.JsVerboseNamer;
import com.google.gwt.dev.js.ast.JsName;
import com.google.gwt.dev.js.ast.JsProgram;
import com.google.gwt.dev.resource.impl.FileResource;
import com.google.gwt.dev.util.Pair;
import com.google.gwt.dev.util.log.speedtracer.CompilerEventType;
import com.google.gwt.dev.util.log.speedtracer.SpeedTracerLogger;
import com.google.gwt.dev.util.log.speedtracer.SpeedTracerLogger.Event;
import com.google.gwt.thirdparty.guava.common.base.Predicate;
import com.google.gwt.thirdparty.guava.common.collect.HashMultimap;
import com.google.gwt.thirdparty.guava.common.collect.Multimap;
import com.google.gwt.thirdparty.guava.common.collect.SetMultimap;
import com.google.gwt.thirdparty.guava.common.collect.Sets;

import java.io.File;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedSet;

import javax.annotation.Nullable;

/**
 * Compiles the Java <code>JProgram</code> representation into its corresponding library JavaScript source.
 * <br />
 * 
 * Care is taken to ensure that the resulting JavaScript source will be valid for runtime linking,
 * such as performing only local optimizations, running only local stages of Generators, gathering
 * and enqueueing rebind information for runtime usage and outputting JavaScript source with names
 * that are stable across libraries.
 */
public class LibraryJavaToJavaScriptCompiler extends JavaToJavaScriptCompiler {

  private static Set<String> badRebindCombinations = Sets.newHashSet();

  private static SetMultimap<String, String> generatorNamesByPreviouslyReboundTypeName =
      HashMultimap.create();

  private static Set<String> previouslyReboundTypeNames = Sets.newHashSet();

  private static Set<JDeclaredType> gatherReboundTypes(
      Collection<CompilationUnit> compilationUnits) {
    Set<JDeclaredType> reboundTypes = Sets.newLinkedHashSet();
    for (CompilationUnit compilationUnit : compilationUnits) {
      for (JDeclaredType type : compilationUnit.getTypes()) {
        ReboundTypeRecorder.exec(type, reboundTypes);
      }
    }
    return reboundTypes;
  }

  private static Set<String> getTypeNames(Set<JDeclaredType> types) {
    Set<String> typeNames = Sets.newHashSet();
    for (JDeclaredType type : types) {
      typeNames.add(type.getName());
    }
    return typeNames;
  }

  /**
   * Generator output can create opportunities for further generator execution, so runGenerators()
   * is repeated to a fixed point. But previously handled generator/reboundType pairs should be
   * ignored.
   */
  private static void removePreviouslyReboundCombinations(
      final String generatorName, Set<String> newReboundTypeNames) {
    newReboundTypeNames.removeAll(
        Sets.newHashSet(Sets.filter(newReboundTypeNames, new Predicate<String>() {
            @Override
          public boolean apply(@Nullable String newReboundTypeName) {
            return generatorNamesByPreviouslyReboundTypeName.containsEntry(
                newReboundTypeName, generatorName);
          }
        })));
  }

  /**
   * Constructs a JavaToJavaScriptCompiler with customizations for compiling independent libraries.
   */
  public LibraryJavaToJavaScriptCompiler(TreeLogger logger, CompilerContext compilerContext) {
    super(logger, compilerContext);
  }

  @Override
  protected void beforeUnifyAst(RebindPermutationOracle rpo, Set<String> allRootTypes)
      throws UnableToCompleteException {
    StandardGeneratorContext generatorContext = rpo.getGeneratorContext();
    runGeneratorsToFixedPoint(rpo, generatorContext);

    Set<JDeclaredType> reboundTypes =
        gatherReboundTypes(rpo.getCompilationState().getCompilationUnits());
    buildFallbackRuntimeRebindRules(reboundTypes, generatorContext);
    buildLocalRuntimeRebindRules(generatorContext);

    buildRuntimeRebindRegistrator(generatorContext, allRootTypes);
    buildPropertyProviderRegistrator(generatorContext, allRootTypes);
  }

  @Override
  protected void checkEntryPoints(String[] declEntryPts, String[] additionalRootTypes) {
    // Library construction does not need to care whether their are or are not any entry points.
  }

  @Override
  protected void createJProgram() {
    jprogram = new JProgram(getOptions().shouldLink());
  }

  @Override
  protected void optimize() {
    Event draftOptimizeEvent = SpeedTracerLogger.start(CompilerEventType.DRAFT_OPTIMIZE);
    Finalizer.exec(jprogram);
    jprogram.typeOracle.recomputeAfterOptimizations();
    // Certain libraries depend on dead stripping.
    DeadCodeElimination.exec(jprogram);
    jprogram.typeOracle.recomputeAfterOptimizations();
    draftOptimizeEvent.end();
  }

  @Override
  protected void populateEntryPointRootTypes(
      RebindPermutationOracle rpo, String[] declEntryPts, Set<String> allRootTypes) {
    Collections.addAll(allRootTypes, declEntryPts);
  }

  @Override
  protected void prune() {
    // Nothing should be pruned when constructing a library since final runtime usage can not be
    // predicted.
  }

  @Override
  protected void rebindModuleEntryPoint(SourceInfo info, RebindPermutationOracle rpo,
      JMethod bootStrapMethod, JBlock block, String mainClassName, JDeclaredType mainType)
      throws UnableToCompleteException {
    JMethodCall onModuleLoadCall =
        createReboundModuleLoad(info, mainType, mainClassName, bootStrapMethod.getEnclosingType());
    block.addStmt(onModuleLoadCall.makeStatement());
  }

  @Override
  protected void removeSuperCalls() {
    // Collapsing the super call chain can not be done accurately when constructing libraries since
    // method bodies of parent classes might or might not be loaded.
  }

  @Override
  protected Map<JsName, String> runDetailedNamer(
      PropertyOracle[] propertyOracles, JsProgram jsProgram, Map<JsName, String> obfuscateMap) {
    JsVerboseNamer.exec(jsProgram, propertyOracles);
    return obfuscateMap;
  }

  @Override
  protected Pair<SyntheticArtifact, MultipleDependencyGraphRecorder> splitProgramIntoFragments(
      PropertyOracle[] propertyOracles, int permutationId, JsProgram jsProgram,
      JavaToJavaScriptMap jjsmap) {
    // Local control flow knowledge and the local list of RunAsyncs is not enough information to be
    // able to accurately split program fragments.
    return Pair.<SyntheticArtifact, MultipleDependencyGraphRecorder> create(null, null);
  }

  private void buildFallbackRuntimeRebindRules(
      Set<JDeclaredType> reboundTypes, StandardGeneratorContext context)
      throws UnableToCompleteException {
    // Create fallback rebinds.
    for (JDeclaredType reboundType : reboundTypes) {
      // HACK, really need to have all types resolved before now :(. Needs redesign.
      String resourcePath = LibraryGroupUnitCache.typeNameToResourcePath(reboundType.getName());
      CompilationUnit compilationUnit = compilerContext.getUnitCache().find(resourcePath);
      reboundType = compilationUnit.getTypeByName(reboundType.getName());

      if (!reboundType.isInstantiable()) {
        continue;
      }
      RuleBaseline rule = new RuleBaseline(reboundType.getName().replace("$", "."));
      rule.generateRuntimeRebindClasses(logger, getModule(), context);
    }
  }

  private void buildLocalRuntimeRebindRules(StandardGeneratorContext context)
      throws UnableToCompleteException {
    // Create rebinders for rules specified in the module.
    Iterator<Rule> iterator = getModule().getRules().iterator();
    while (iterator.hasNext()) {
      Rule rule = iterator.next();
      if (rule instanceof RuleGenerateWith) {
        continue;
      }
      rule.generateRuntimeRebindClasses(logger, getModule(), context);
    }
  }

  private void buildPropertyProviderRegistrator(
      StandardGeneratorContext context, Set<String> allRootTypes) throws UnableToCompleteException {
    SortedSet<BindingProperty> bindingProperties =
        getModule().getProperties().getBindingProperties();
    SortedSet<ConfigurationProperty> configurationProperties =
        getModule().getProperties().getConfigurationProperties();

    PropertyProviderRegistratorGenerator propertyProviderRegistratorGenerator =
        new PropertyProviderRegistratorGenerator(bindingProperties, configurationProperties);
    String propertyProviderRegistratorTypeName =
        propertyProviderRegistratorGenerator.generate(logger, context, getModule().getName());
    // Ensures that unification traverses and keeps the class.
    allRootTypes.add(propertyProviderRegistratorTypeName);
    // Ensures that JProgram knows to index this class's methods so that later bootstrap
    // construction code is able to locate the FooPropertyProviderRegistrator.register() function.
    jprogram.addIndexedTypeName(propertyProviderRegistratorTypeName);
    jprogram.setPropertyProviderRegistratorTypeName(propertyProviderRegistratorTypeName);
    context.finish(logger);
  }

  private void buildRuntimeRebindRegistrator(
      StandardGeneratorContext context, Set<String> allRootTypes) throws UnableToCompleteException {
    RuntimeRebindRegistratorGenerator runtimeRebindRegistratorGenerator =
        new RuntimeRebindRegistratorGenerator();
    String runtimeRebindRegistratorTypeName =
        runtimeRebindRegistratorGenerator.generate(logger, context, getModule().getName());
    // Ensures that unification traverses and keeps the class.
    allRootTypes.add(runtimeRebindRegistratorTypeName);
    // Ensures that JProgram knows to index this class's methods so that later bootstrap
    // construction code is able to locate the FooRuntimeRebindRegistrator.register() function.
    jprogram.addIndexedTypeName(runtimeRebindRegistratorTypeName);
    jprogram.setRuntimeRebindRegistratorTypeName(runtimeRebindRegistratorTypeName);
    context.finish(logger);
  }

  private boolean relevantPropertiesHaveChanged(RuleGenerateWith generatorRule) {
    // Gather binding and configuration property values that have been changed in the part of
    // the library dependency tree on which this generator has not yet run.
    Multimap<String, String> newConfigurationPropertyValues =
        compilerContext.gatherNewConfigurationPropertyValuesForGenerator(generatorRule.getName());
    Multimap<String, String> newBindingPropertyValues =
        compilerContext.gatherNewBindingPropertyValuesForGenerator(generatorRule.getName());

    return generatorRule.caresAboutProperties(newConfigurationPropertyValues.keySet())
        || generatorRule.caresAboutProperties(newBindingPropertyValues.keySet());
  }

  /**
   * Runs a particular generator on the provided set of rebound types. Takes care to guard against
   * duplicate work during reruns as generation approaches a fixed point.
   * 
   * @return whether a fixed point was reached.
   */
  private boolean runGenerator(RuleGenerateWith generatorRule, Set<String> reboundTypeNames,
      StandardGeneratorContext context) throws UnableToCompleteException {
    boolean fixedPoint = true;
    removePreviouslyReboundCombinations(generatorRule.getName(), reboundTypeNames);
    reboundTypeNames.removeAll(previouslyReboundTypeNames);

    for (String reboundTypeName : reboundTypeNames) {
      if (badRebindCombinations.contains(generatorRule.getName() + "-" + reboundTypeName)) {
        continue;
      }
      generatorNamesByPreviouslyReboundTypeName.put(reboundTypeName, generatorRule.getName());
      reboundTypeName = reboundTypeName.replace("$", ".");
      generatorRule.generate(logger, getModule(), context, reboundTypeName);

      if (context.isDirty()) {
        fixedPoint = false;
        previouslyReboundTypeNames.add(reboundTypeName);
        // Ensure that cascading generations rerun properly.
        for (String generatedTypeName : context.getGeneratedUnitMap().keySet()) {
          generatorNamesByPreviouslyReboundTypeName.removeAll(generatedTypeName);
        }
        context.finish(logger);
      } else {
        badRebindCombinations.add(generatorRule.getName() + "-" + reboundTypeName);
      }
    }

    return fixedPoint;
  }

  /**
   * Figures out which generators should run in the current context and runs them. Generator
   * execution can create new opportunities for further generator execution so this function should
   * be invoked repeatedly till a fixed point is reached.<br />
   * 
   * Returns whether a fixed point was reached.
   */
  private boolean runGenerators(StandardGeneratorContext generatorContext)
      throws UnableToCompleteException {
    boolean fixedPoint = true;
    boolean globalCompile = compilerContext.getOptions().shouldLink();
    Set<Rule> generatorRules = Sets.newHashSet(getModule().getGeneratorRules());

    for (Rule rule : generatorRules) {
      RuleGenerateWith generatorRule = (RuleGenerateWith) rule;
      String generatorName = generatorRule.getName();

      if (generatorRule.contentDependsOnTypes() && !globalCompile) {
        // Type unstable generators can only be safely run in the global phase.
        // TODO(stalcup): modify type unstable generators such that their output is no longer
        // unstable.
        continue;
      }

      // Run generator for new rebound types.
      Set<String> newReboundTypeNames =
          compilerContext.gatherNewReboundTypeNamesForGenerator(generatorName);
      fixedPoint &= runGenerator(generatorRule, newReboundTypeNames, generatorContext);

      // If the content of generator output varies when some relevant properties change and some
      // relevant properties have changed.
      if (generatorRule.contentDependsOnProperties()
          && relevantPropertiesHaveChanged(generatorRule)) {
        // Rerun the generator on old rebound types to replace old stale output.
        Set<String> oldReboundTypeNames =
            compilerContext.gatherOldReboundTypeNamesForGenerator(generatorName);
        fixedPoint &= runGenerator(generatorRule, oldReboundTypeNames, generatorContext);
      }

      compilerContext.getLibraryWriter().addRanGeneratorName(generatorName);
    }

    return fixedPoint;
  }

  private void runGeneratorsToFixedPoint(
      RebindPermutationOracle rpo, StandardGeneratorContext generatorContext)
      throws UnableToCompleteException {
    boolean fixedPoint;
    do {
      compilerContext.getLibraryWriter().setReboundTypeNames(
          getTypeNames(gatherReboundTypes(rpo.getCompilationState().getCompilationUnits())));

      fixedPoint = runGenerators(generatorContext);
    } while (!fixedPoint);

    // This is a horribly dirty hack to work around the fact that CssResourceGenerator uses a
    // completely nonstandard resource creation and caching mechanism that ignores the
    // GeneratorContext infrastructure. It and GenerateCssAst need to be fixed.
    for (Entry<String, File> entry :
        ResourceGeneratorUtilImpl.getGeneratedFilesByName().entrySet()) {
      String resourcePath = entry.getKey();
      File resourceFile = entry.getValue();
      compilerContext.getLibraryWriter().addBuildResource(
          new FileResource(null, resourcePath, resourceFile));
    }
  }
}
