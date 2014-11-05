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
package com.google.gwt.dev.jjs.impl;

import com.google.gwt.dev.jjs.ast.JField;
import com.google.gwt.dev.jjs.ast.JMethod;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 *
 * Maintain dependence information and modified items. Get updated incrementally.
 */
public class OptimizerDependencies {
  private int optimizationStep = -1;

  private CallGraph callGraph = new CallGraph();

  // add other dependencies here

  // record modified methods with its last modification step.
  // Map<JMethod, Integer> is better than Multiset<JMethod> here.
  private Map<JMethod, Integer> modifiedMethods = new HashMap<JMethod, Integer>();

  // record modified methods in each step, used for getting modifications in certain steps quickly
  private List<Set<JMethod>> modifiedMethodsInEachStep = new LinkedList<Set<JMethod>>();

  // record modified fields with its last modification step
  private Map<JField, Integer> modifiedFields = new HashMap<JField, Integer>();

  // record modified fields in each step
  private List<Set<JField>> modifiedFieldsInEachStep = new LinkedList<Set<JField>>();

  public OptimizerDependencies() {
    incOptimizationStep();
  }

  /**
   * Return the sequence number of optimization step.
   */
  public int getOptimizationStep() {
    return optimizationStep;
  }

  /**
   * Increase the optimization step by 1, create a new set to record modifications in this step.
   */
  public void incOptimizationStep() {
    modifiedMethodsInEachStep.add(new LinkedHashSet<JMethod>());
    modifiedFieldsInEachStep.add(new LinkedHashSet<JField>());
    optimizationStep++;
  }

  /**
   * Add modified method to current optimization step.
   */
  public void addModifiedMethod(JMethod m) {
    if (modifiedMethods.containsKey(m)) {
      modifiedMethodsInEachStep.get(modifiedMethods.get(m)).remove(m);
    }
    modifiedMethodsInEachStep.get(optimizationStep).add(m);
    modifiedMethods.put(m, optimizationStep);
  }

  /**
   * Return all the effective modified methods since a specific step.
   */
  public Set<JMethod> getModifiedMethodsSince(int stepSince) {
    Set<JMethod> result = new LinkedHashSet<JMethod>();
    for (int i = stepSince; i <= optimizationStep; i++) {
      result.addAll(modifiedMethodsInEachStep.get(i));
    }
    return result;
  }

  /**
   * Return all the modified methods at a specific step.
   */
  public Set<JMethod> getModifiedMethodsAt(int step) {
    assert (step >= 0 && step <= optimizationStep);
    return new LinkedHashSet<JMethod>(modifiedMethodsInEachStep.get(step));
  }

  /**
   * Add modified field to current optimization step.
   */
  public void addModifiedField(JField f) {
    if (modifiedFields.containsKey(f)) {
      modifiedFieldsInEachStep.get(modifiedFields.get(f)).remove(f);
    }
    modifiedFieldsInEachStep.get(optimizationStep).add(f);
    modifiedFields.put(f, optimizationStep);
  }

  /**
   * Return all the effective modified fields since a specific step.
   */
  public Set<JField> getModifiedFieldsSince(int stepSince) {
    Set<JField> result = new LinkedHashSet<JField>();
    for (int i = stepSince; i <= optimizationStep; i++) {
      result.addAll(modifiedFieldsInEachStep.get(i));
    }
    return result;
  }

  public void removeModifiedMethod(JMethod m) {
    if (modifiedMethods.containsKey(m)) {
      modifiedMethodsInEachStep.get(modifiedMethods.get(m)).remove(m);
      modifiedMethods.remove(m);
    }
  }

  public void removeModifiedField(JField f) {
    if (modifiedFields.containsKey(f)) {
      modifiedFieldsInEachStep.get(modifiedFields.get(f)).remove(f);
      modifiedFields.remove(f);
    }
  }

  public CallGraph getCallGraph() {
    return callGraph;
  }
}
