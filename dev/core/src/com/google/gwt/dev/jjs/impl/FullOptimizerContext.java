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
package com.google.gwt.dev.jjs.impl;

import com.google.gwt.dev.jjs.ast.JDeclaredType;
import com.google.gwt.dev.jjs.ast.JField;
import com.google.gwt.dev.jjs.ast.JMethod;
import com.google.gwt.dev.jjs.ast.JProgram;
import com.google.gwt.thirdparty.guava.common.collect.HashMultiset;
import com.google.gwt.thirdparty.guava.common.collect.Lists;
import com.google.gwt.thirdparty.guava.common.collect.Multiset;
import com.google.gwt.thirdparty.guava.common.collect.Sets;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Maintains dependence and modification information for AST optimizers.
 * <p>
 * Is updated incrementally.
 */
public class FullOptimizerContext implements OptimizerContext {
  private int optimizationStep = -1;

  private CallGraph callGraph = new CallGraph();
  private FieldReferencesGraph fieldReferencesGraph = new FieldReferencesGraph();

  /**
   * Removed callee methods.
   * <p>
   * When a method call is removed from the caller method (the caller method body is changed and the
   * method call is eliminated or replaced, or the caller method itself is pruned), record the callee
   * method in {@code removedCalleeMethods}.
   * <P>
   * It is used in TypeTightener. When a method is called, we record the {arg<->param} pairs in
   * {@code assignments}, and the type of the parameter is analyzed according to {@code assignments}
   * . If a method call is removed, there is an opportunity that the type of the callee method's
   * parameter can be tightened since it may have less assignments.
   */
  private Set<JMethod> removedCalleeMethods = Sets.newLinkedHashSet();

  // TODO(leafwang): add other dependencies here

  /**
   * A mapping from methods to the numbers of the most recent step in which they were modified.
   */
  private Multiset<JMethod> modificationStepByMethod = HashMultiset.create();

  /**
   * A list of modified methods in each step.
   */
  private List<Set<JMethod>> methodsByModificationStep = Lists.newArrayList();

  /**
   * A mapping from methods to the numbers of the most recent step in which they were modified.
   */
  private Multiset<JField> modificationStepByField = HashMultiset.create();

  /**
   * A list of modified fields in each step.
   */
  private List<Set<JField>> fieldsByModificationStep = Lists.newArrayList();

  /**
   * A mapping from optimizers to their last modification step.
   */
  private Multiset<String> lastStepForOptimizer = HashMultiset.create();

  public FullOptimizerContext(JProgram program) {
    incOptimizationStep();
    initializeModifications(program);
    buildCallGraph(program);
    buildFieldReferencesGraph(program);
    incOptimizationStep();
  }

  @Override
  public void markModified(JField modifiedField) {
    fieldsByModificationStep.get(modificationStepByField.count(modifiedField)).remove(
        modifiedField);
    fieldsByModificationStep.get(optimizationStep).add(modifiedField);
    modificationStepByField.setCount(modifiedField, optimizationStep);
    // TODO(leafwang): update related dependence information here.
  }

  @Override
  public void markModified(JMethod modifiedMethod) {
    methodsByModificationStep.get(modificationStepByMethod.count(modifiedMethod)).remove(
        modifiedMethod);
    methodsByModificationStep.get(optimizationStep).add(modifiedMethod);
    modificationStepByMethod.setCount(modifiedMethod, optimizationStep);

    Set<JMethod> originalCalleeMethods = callGraph.getCallees(Collections.singleton(modifiedMethod));
    callGraph.updateCallGraphOfMethod(modifiedMethod);
    removedCalleeMethods.addAll(
        Sets.difference(originalCalleeMethods, callGraph.getCallees(Collections.singleton(modifiedMethod))));

    fieldReferencesGraph.updateFieldRefencesOfMethod(modifiedMethod);
  }

  @Override
  public Set<JMethod> getCallees(Collection<JMethod> callerMethods) {
    return callGraph.getCallees(callerMethods);
  }

  @Override
  public Set<JMethod> getCallers(Collection<JMethod> calleeMethods) {
    return callGraph.getCallers(calleeMethods);
  }

  @Override
  public int getLastStepFor(String optimizerName) {
    return lastStepForOptimizer.count(optimizerName);
  }

  @Override
  public Set<JMethod> getMethodsByReferencedFields(Collection<JField> fields) {
    return fieldReferencesGraph.getMethodsByReferencedFields(fields);
  }

  @Override
  public Set<JField> getModifiedFieldsSince(int stepSince) {
    Set<JField> result = Sets.newLinkedHashSet();
    for (int i = stepSince; i < optimizationStep; i++) {
      result.addAll(fieldsByModificationStep.get(i));
    }
    return result;
  }

  @Override
  public Set<JMethod> getModifiedMethodsSince(int stepSince) {
    Set<JMethod> result = Sets.newLinkedHashSet();
    for (int i = stepSince; i < optimizationStep; i++) {
      result.addAll(methodsByModificationStep.get(i));
    }
    return result;
  }

  @Override
  public int getOptimizationStep() {
    return optimizationStep;
  }

  @Override
  public Set<JField> getReferencedFieldsByMethods(Collection<JMethod> methods) {
    return fieldReferencesGraph.getReferencedFieldsByMethods(methods);
  }

  @Override
  public Set<JMethod> getRemovedCalleeMethods() {
    return removedCalleeMethods;
  }

  @Override
  public void incOptimizationStep() {
    methodsByModificationStep.add(new LinkedHashSet<JMethod>());
    fieldsByModificationStep.add(new LinkedHashSet<JField>());
    optimizationStep++;
  }

  @Override
  public void remove(JField field) {
    fieldsByModificationStep.get(modificationStepByField.count(field)).remove(field);
    modificationStepByField.remove(field);
    fieldReferencesGraph.removeField(field);
  }

  @Override
  public void removeFields(Collection<JField> fields) {
    for (JField field : fields) {
      remove(field);
    }
  }

  @Override
  public void remove(JMethod method) {
    methodsByModificationStep.get(modificationStepByMethod.count(method)).remove(method);
    modificationStepByMethod.remove(method);
    callGraph.removeMethod(method);
    fieldReferencesGraph.removeMethod(method);
  }

  @Override
  public void removeMethods(Collection<JMethod> methods) {
    for (JMethod method : methods) {
      remove(method);
    }
  }

  @Override
  public void setLastStepFor(String optimizerName, int step) {
    lastStepForOptimizer.setCount(optimizerName, step);
  }

  @Override
  public void addRemovedCallees(Collection<JMethod> calleeMethods) {
    removedCalleeMethods.addAll(calleeMethods);
  }

  @Override
  public void clearRemovedCallees() {
    removedCalleeMethods.clear();
  }

  @Override
  public void syncRemovedCallees(Collection<JMethod> prunedMethods) {
    removedCalleeMethods.removeAll(prunedMethods);
  }

  private void buildCallGraph(JProgram program) {
    callGraph.buildCallGraph(program);
  }

  private void buildFieldReferencesGraph(JProgram program) {
    fieldReferencesGraph.buildFieldReferencesGraph(program);
  }

  private void initializeModifications(JProgram program) {
    assert optimizationStep == 0;
    for (JDeclaredType type : program.getModuleDeclaredTypes()) {
      fieldsByModificationStep.get(0).addAll(type.getFields());
      methodsByModificationStep.get(0).addAll(type.getMethods());
    }
  }
}
