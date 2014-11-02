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

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

/**
 *
 * Maintain dependence information and modified items.
 * Get updated incrementally.
 */
public class OptimizerDependencies {
  public static final int PRUNER_IDX = 0;
  public static final int FINALIZER_IDX = 1;
  public static final int MAKECALLSSTATIC_IDX = 2;
  public static final int TYPETIGHTENER_IDX = 3;
  public static final int METHODCALLTIGHTENER_IDX = 4;
  public static final int METHODCALLSPECIALIZER_IDX = 5;
  public static final int DEADCODEELIMINATION_IDX = 6;
  public static final int METHODINLINER_IDX = 7;

  private int optimizationPass;

  private CallerMethodsOfCalledMethod callGraph;

  // add other dependencies here

  // record modified methods and the last step each method is modified
  private Map<JMethod, Integer> modifiedMethods;

  // record modified fields and the last step each field is modified
  private Map<JField, Integer> modifiedFields;

  public OptimizerDependencies() {
    optimizationPass = 0;
    callGraph = new CallerMethodsOfCalledMethod();
    /**
     * TODO: add initialization of other dependencies here.
     */

    modifiedMethods = new HashMap<JMethod, Integer>();
    modifiedFields = new HashMap<JField, Integer>();
  }

  /**
   * return the sequence number of optimization pass.
   */
  public int getOptimizationPass() {
    return optimizationPass;
  }

  /**
   * increase the sequence number of optimization pass by 1.
   */
  public void incOptimizationPass() {
    optimizationPass++;
  }

  /**
   * record modified method and its last modification step.
   */
  public void addModifiedMethod(JMethod m, int step) {
    modifiedMethods.put(m, step);
  }

  /**
   * record modified methods and their last modification step.
   */
  public void addModifiedMethods(Collection<JMethod> methods, int step) {
    if (methods != null) {
      for (JMethod m : methods) {
        modifiedMethods.put(m, step);
      }
    }
  }

  /**
   * return all the effective modified methods and their last modification step.
   */
  public Map<JMethod, Integer> getModifiedMethods() {
    return modifiedMethods;
  }

  /**
   * record modified field and its last modification step.
   */
  public void addModifiedField(JField f, int step) {
    modifiedFields.put(f, step);
  }

  /**
   * record modified fileds and their last modification step.
   */
  public void addModifiedFields(Collection<JField> fields, int step) {
    if (fields != null) {
      for (JField f : fields) {
        modifiedFields.put(f, step);
      }
    }
  }

  /**
   * return all the effective modified fields and their last modification step.
   */
  public Map<JField, Integer> getModifiedFields() {
    return modifiedFields;
  }

  private void removeByStep(Map<?, Integer> map, int step) {
    map.values().removeAll(Collections.singleton(step));
  }

  /**
   * remove all the modifications by a specific step.
   */
  public void removeModificationsByLastPass(int step) {
    removeByStep(modifiedMethods, step);
    removeByStep(modifiedFields, step);
  }

  public void removeModifiedMethod(JMethod m) {
    modifiedMethods.remove(m);
  }

  public void removeModifiedField(JField f) {
    modifiedFields.remove(f);
  }

  public CallerMethodsOfCalledMethod getCallGraph() {
    return callGraph;
  }

  /**
   * return the affected methods for MethodInliner.
   * @return affectedMethods = {preModifications} + {callers of preModifications}
   */
  public Set<JMethod> methodInlinerComputeAffectedPaths(Set<JMethod> preModifications) {
    Set<JMethod> result = new LinkedHashSet<JMethod>();
    if (preModifications == null || preModifications.size() == 0) {
      return result;
    }
    result.addAll(preModifications);
    result.addAll(callGraph.getCallers(preModifications));
    return result;
  }
}
