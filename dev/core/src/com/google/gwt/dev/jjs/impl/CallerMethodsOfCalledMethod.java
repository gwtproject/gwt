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

import com.google.gwt.dev.jjs.ast.Context;
import com.google.gwt.dev.jjs.ast.JMethod;
import com.google.gwt.dev.jjs.ast.JMethodCall;
import com.google.gwt.dev.jjs.ast.JNode;
import com.google.gwt.dev.jjs.ast.JProgram;
import com.google.gwt.dev.jjs.ast.JVisitor;
import com.google.gwt.thirdparty.guava.common.collect.HashMultimap;
import com.google.gwt.thirdparty.guava.common.collect.Multimap;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * non-precise call graph.
 * record {callee -> callers} pair.
 */
public class CallerMethodsOfCalledMethod {

  /**
   * Visitor used to build call graph.
   */
  static class BuildCallersVisitor extends JVisitor {

    private JMethod currentMethod;

    private Multimap<JMethod, JMethod> callers = HashMultimap.create();

    @Override
    public void endVisit(JMethod x, Context ctx) {
      currentMethod = null;
    }

    @Override
    public void endVisit(JMethodCall x, Context ctx) {
      JMethod method = x.getTarget();
      callers.put(method, currentMethod);
    }

    @Override
    public boolean visit(JMethod x, Context ctx) {
      currentMethod = x;
      return true;
    }

    public Multimap<JMethod, JMethod> getCallers() {
      return callers;
    }
  }

  private Multimap<JMethod, JMethod> callSite = HashMultimap.create();

  private boolean isUsable = false;

  /**
   * Set the call graph to be usable/unusable.
   */
  public void setUsable(boolean usable) {
    isUsable = usable;
  }

  /**
   * Return if the call graph is usable (has been built and well maintained).
   */
  public boolean isUsable() {
    return isUsable;
  }

  /**
   * Build the call graph of a JProgram and set the call graph to be usable.
   */
  public void buildCallSite(JProgram program) {
    BuildCallersVisitor buildCallersVisitor = new BuildCallersVisitor();
    buildCallersVisitor.accept(program);
    callSite = buildCallersVisitor.getCallers();
    isUsable = true;
  }

  /**
   * Return {callee -> callers} pairs of a snippet inside a JMethod.
   */
  public static Multimap<JMethod, JMethod> getCallees(JMethod method, JNode body) {
    BuildCallersVisitor callSiteVisitor = new BuildCallersVisitor();
    callSiteVisitor.currentMethod = method;
    callSiteVisitor.accept(body);
    return callSiteVisitor.getCallers();
  }

  /**
   * remove {callee -> callers} pairs.
   */
  public void removeCallers(Multimap<JMethod, JMethod> removedCallers) {
    if (removedCallers == null || removedCallers.size() == 0 || callSite.size() == 0) {
      return;
    }
    callSite.removeAll(removedCallers);
  }

  /**
   * add {callee -> callers} pairs.
   */
  public void addCallers(Multimap<JMethod, JMethod> addedCallers) {
    if (addedCallers == null || addedCallers.size() == 0) {
      return;
    }
    callSite.putAll(addedCallers);
  }

  /**
   * add one {callee -> caller} pair.
   */
  public void addCaller(JMethod callee, JMethod caller) {
    callSite.put(callee, caller);
  }

  /**
   * Return all the callers of a set of callee methods.
   */
  public Set<JMethod> getCallers(Set<JMethod> callees) {
    Set<JMethod> result = new LinkedHashSet<JMethod>();
    if (callees != null) {
      for (JMethod m : callees) {
        if (callSite.get(m) == null || callSite.get(m).size() == 0) {
          continue;
        }
        result.addAll(callSite.get(m));
      }
    }
    return result;
  }

}
