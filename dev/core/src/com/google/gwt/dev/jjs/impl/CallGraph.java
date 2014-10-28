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
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

/**
 * non-precise call graph.
 */
public class CallGraph {

  private Map<JMethod, Set<JMethod>> callSite;

  private boolean isUsable;

  public CallGraph() {
    callSite = new HashMap<JMethod, Set<JMethod>>();
    isUsable = false;
  }

  public void setUsable(boolean usable) {
    isUsable = usable;
  }

  public boolean isUsable() {
    return isUsable;
  }

  public void reset() {
    callSite.clear();
    isUsable = false;
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

  public Set<JMethod> getCallers(Set<JMethod> callees) {
    Set<JMethod> result = new LinkedHashSet<JMethod>();
    if (callees != null) {
      for (JMethod m : callees) {
        if (callSite.get(m) == null || callSite.get(m).size() == 0) {
          continue;
        }
        for (JMethod caller : callSite.get(m)) {
          result.add(caller);
        }
      }
    }
    return result;
  }

}
