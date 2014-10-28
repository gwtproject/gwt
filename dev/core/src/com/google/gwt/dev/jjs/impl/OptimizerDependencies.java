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
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 *
 * Maintain dependence information and modified paths, they are updated incrementally.
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

  private CallGraph callGraph;

  // add other dependencies here

  private Map<JMethod, Integer> modifiedMethods; // record the last step each method is modified

  private Map<JField, Integer> modifiedFields; // record the last step each field is modified

  public OptimizerDependencies() {
    callGraph = new CallGraph();
    /**
     * TODO: add initialization of other dependencies here.
     */

    modifiedMethods = new HashMap<JMethod, Integer>();
    modifiedFields = new HashMap<JField, Integer>();
  }

  public void addModifiedMethod(JMethod m, int step) {
    modifiedMethods.put(m, step);
  }

  public void addModifiedMethods(Collection<JMethod> methods, int step) {
    if (methods != null) {
      for (JMethod m : methods) {
        modifiedMethods.put(m, step);
      }
    }
  }

  public Map<JMethod, Integer> getModifiedMethods() {
    return modifiedMethods;
  }

  public void addModifiedField(JField f, int step) {
    modifiedFields.put(f, step);
  }

  public void addModifiedFields(Collection<JField> fields, int step) {
    if (fields != null) {
      for (JField f : fields) {
        modifiedFields.put(f, step);
      }
    }
  }

  public Map<JField, Integer> getModifiedFields() {
    return modifiedFields;
  }

  private void removeByStep(Map<?, Integer> map, int step) {
    Iterator<?> it = map.entrySet().iterator();
    while (it.hasNext()) {
      @SuppressWarnings("unchecked")
      Entry<?, Integer> entry = (Map.Entry<?, Integer>) it.next();
      if (entry.getValue() == step) {
        it.remove();
      }
    }
  }

  public void removeModifiedMethods(int step) {
    removeByStep(modifiedMethods, step);
  }

  public void removeModifiedFields(int step) {
    removeByStep(modifiedFields, step);
  }

  public CallGraph getCallGraph() {
    return callGraph;
  }

  /**
   * only the previous modified methods and the callers of simplified methods are affected.
   *
   * @param preModifications : Methods modified by previous optimizations
   * @param simplifiedMethods : Methods simplified by previous optimizations. If no record for this,
   *        take the same as preModifications
   * @return affectedMethd = {preModifications} + {callers of simplifiedMethods}
   */
  public Set<JMethod> methodInlinerComputeAffectedPaths(Set<JMethod> preModifications,
      Set<JMethod> simplifiedMethods) {
    Set<JMethod> result = new LinkedHashSet<JMethod>();
    if (preModifications == null || preModifications.size() == 0) {
      return result;
    }
    for (JMethod m : preModifications) {
      result.add(m);
    }
    result.addAll(callGraph.getCallers(simplifiedMethods));
    return result;
  }
}
