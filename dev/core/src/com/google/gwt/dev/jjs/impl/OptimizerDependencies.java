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

import com.google.gwt.dev.jjs.ast.JMethod;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

/**
 *
 * Maintain dependence information and modified paths for each optimization pass.
 */
public class OptimizerDependencies {

  public static final int NUM_OF_OPTIMIZERS = 8;

  public static final int PRUNER_IDX = 0;
  public static final int FINALIZER_IDX = 1;
  public static final int MAKECALLSTATIC_IDX = 2;
  public static final int TYPETIGHTENER_IDX = 3;
  public static final int METHODCALLTIGHTENER_IDX = 4;
  public static final int METHODCALLSPECIALIZER_IDX = 5;
  public static final int DEADCODEELIMINATION_IDX = 6;
  public static final int METHODINLINER_IDX = 7;

  private abstract class Dependencies {

    /**
     * if the dependence information is maintained by each optimizer.
     */
    protected boolean[] isMaintained;

    public Dependencies() {
      isMaintained = new boolean[NUM_OF_OPTIMIZERS];
      for (int i = 0; i < NUM_OF_OPTIMIZERS; i++) {
        isMaintained[i] = false;
      }
    }

    public boolean isMaintained() {
      for (int i = 0; i < NUM_OF_OPTIMIZERS; i++) {
        if (!isMaintained[i]) {
          return false;
        }
      }
      return true;
    }

    public void reset() {
      for (int i = 0; i < NUM_OF_OPTIMIZERS; i++) {
        isMaintained[i] = false;
      }
    }

    public void set() {
      for (int i = 0; i < NUM_OF_OPTIMIZERS; i++) {
        isMaintained[i] = true;
      }
    }

    public void set(int optimizer) {
      isMaintained[optimizer] = true;
    }

    public void reset(int optimizer) {
      isMaintained[optimizer] = false;
    }
  }

  /**
   *
   * Dependence info for MethodInliner maintain a {callee -> callers} map (callSite)
   * and a set of modified methods (modifiedMethods).
   */
  private class MethodInlinerDependencies extends Dependencies {

    private Map<JMethod, Set<JMethod>> callSite;

    private Set<JMethod> modifiedMethods;

    public MethodInlinerDependencies() {
      super();
      callSite = new HashMap<JMethod, Set<JMethod>>();
      modifiedMethods = new LinkedHashSet<JMethod>();
    }

    @Override
    public void reset() {
      callSite.clear();
      modifiedMethods.clear();
      super.reset();
    }

    public void resetModifiedMethods() {
      this.modifiedMethods.clear();
    }

    public void resetCallSite() {
      this.callSite.clear();
    }

    public void addModifiedMethod(JMethod m) {
      modifiedMethods.add(m);
    }

    public void addModifiedMethods(Collection<JMethod> m) {
      modifiedMethods.addAll(m);
    }

    public Set<JMethod> getModifiedMethods() {
      return modifiedMethods;
    }

    public void removeCallers(Map<JMethod, Set<JMethod>> removedCallers) {
      if (removedCallers == null || removedCallers.size() == 0 || callSite.size() == 0) {
        return;
      }
      for (JMethod m : removedCallers.keySet()) {
        if (callSite.containsKey(m)) {
          callSite.get(m).removeAll(removedCallers.get(m));
        }
      }
    }

    public void addCallers(Map<JMethod, Set<JMethod>> addedCallers) {
      if (addedCallers == null || addedCallers.size() == 0) {
        return;
      }
      for (JMethod m : addedCallers.keySet()) {
        if (callSite.containsKey(m)) {
          callSite.get(m).addAll(addedCallers.get(m));
        } else {
          callSite.put(m, new LinkedHashSet<JMethod>(addedCallers.get(m)));
        }
      }
    }

    public void addCallers(JMethod callee, JMethod caller) {
      if (callSite.containsKey(callee)) {
        callSite.get(callee).add(caller);
      } else {
        callSite.put(callee, new LinkedHashSet<JMethod>(Arrays.asList(caller)));
      }
    }

    /**
     * only the previous modified methods and the callers of simplified methods are affected.
     *
     * @param preModifications : Methods modified by previous optimizations
     * @param simplifiedMethods : Methods simplified by previous optimizations. If no record for
     *        this, take the same as preModifications
     * @return affectedMethd = {preModifications} + {callers of simplifiedMethods}
     */
    private Set<JMethod> computeAffectedPaths(Set<JMethod> preModifications,
        Set<JMethod> simplifiedMethods) {
      Set<JMethod> result = new LinkedHashSet<JMethod>();
      if (preModifications == null || preModifications.size() == 0) {
        return result;
      }
      for (JMethod m : preModifications) {
        result.add(m);
      }
      if (simplifiedMethods != null) {
        for (JMethod m : simplifiedMethods) {
          if (callSite.get(m) == null || callSite.get(m).size() == 0) {
            continue;
          }
          for (JMethod caller : callSite.get(m))
            result.add(caller);
        }
      }
      return result;
    }
  }

  // end definition of MethodInlinerDependencies

  /**
   * TODO: define other optimizer dependencies here.
   */

  private MethodInlinerDependencies depForMethodInliner;

  /*
   * TODO: add other dependencies here
   */

  public OptimizerDependencies() {
    depForMethodInliner = new MethodInlinerDependencies();
    /**
     * TODO: add initialization of other dependencies here.
     */
  }

  // handlers for MethodInlinerDependencies
  public Set<JMethod> methodInlinerComputeAffectedPaths(Set<JMethod> preModifications,
      Set<JMethod> simplifiedMethods) {
    return depForMethodInliner.computeAffectedPaths(preModifications, simplifiedMethods);
  }

  public void methodInlinerRemoveCallers(Map<JMethod, Set<JMethod>> removedCallers) {
    depForMethodInliner.removeCallers(removedCallers);
  }

  public void methodInlinerAddCallers(Map<JMethod, Set<JMethod>> addedCallers) {
    depForMethodInliner.addCallers(addedCallers);
  }

  public void methodInlinerAddCallers(JMethod callee, JMethod caller) {
    depForMethodInliner.addCallers(callee, caller);
  }

  public void methodInlinerResetModifiedMethods() {
    depForMethodInliner.resetModifiedMethods();
  }

  public void methodInlinerResetCallSite() {
    depForMethodInliner.resetCallSite();
  }

  public void methodInlinerAddModifiedMethod(JMethod m) {
    depForMethodInliner.addModifiedMethod(m);
  }

  public void methodInlinerAddModifiedMethods(Collection<JMethod> m) {
    depForMethodInliner.addModifiedMethods(m);
  }

  public Set<JMethod> methodInlinerGetModifiedMethods() {
    return depForMethodInliner.getModifiedMethods();
  }

  public boolean methodInlinerIsMaintained() {
    return depForMethodInliner.isMaintained();
  }

  public void methodInlinerSet() {
    depForMethodInliner.set();
  }

  public void methodInlinerReset() {
    depForMethodInliner.reset();
  }

  public void methodInlinerSet(int optimizer) {
    depForMethodInliner.set(optimizer);
  }

  public void methodInlinerReset(int optimizer) {
    depForMethodInliner.reset(optimizer);
  }

  // end handlers for MethodInlinerDependencies

  /**
   * TODO: add handlers for other optimizers dependencies here.
   */

  public void reset() {
    methodInlinerReset();
    /**
     * TODO : reset all the optimizers.
     */
  }

}
