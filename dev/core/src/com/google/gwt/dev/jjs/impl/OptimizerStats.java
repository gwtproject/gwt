/*
 * Copyright 2010 Google Inc.
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

package com.google.gwt.dev.jjs.impl;

import com.google.gwt.dev.util.log.perf.AbstractOptimizationEvent;
import com.google.gwt.dev.util.log.perf.CompilerPassEvent;
import com.google.gwt.dev.util.log.perf.OptimizationLoopEvent;
import com.google.gwt.thirdparty.guava.common.base.Joiner;
import com.google.gwt.thirdparty.guava.common.collect.Lists;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Stack;

/**
 * Stores statistics on the results of running an optimizer pass, and emits as a JFR event.
 * Starts recording when it is created, and stops recording when closed. Can still be used
 * after close to pass statistics to a parent instance, for the purpose of tracking changes.
 * <p>
 * OptimizerStats are intended to be created and closed within a single method using
 * try-with-resources blocks. They can be updated to indicate modifications during that time, and
 * will emit a JFR event when closed. Any currently open event on the stack will be registered as a
 * threadlocal, and so doesn't need to be passed as an argument for its nodecount (aka numVisits)
 * to be shared.
 * <p>
 * On the other hand, if an "end node count" is recorded, that must be called before the stats
 * instance is closed.
 */
public class OptimizerStats implements AutoCloseable {
  private static final ThreadLocal<Stack<OptimizerStats>> stack = ThreadLocal.withInitial(Stack::new);
  private final List<OptimizerStats> children = new ArrayList<>();
  private final String name;
  private final AbstractOptimizationEvent jfrEvent;
  private int numMods = 0;
  private int numVisits = 0;

  public static OptimizerStats javaPass(int passCount) {
    return new OptimizerStats("JavaPass#" + passCount,
        new OptimizationLoopEvent(passCount, "Java"));
  }

  public static OptimizerStats jsPass(int passCount) {
    return new OptimizerStats("JsPass#" + passCount,
        new OptimizationLoopEvent(passCount, "JavaScript"));
  }

  public static OptimizerStats normalizer(String name) {
    return new OptimizerStats(name, new CompilerPassEvent(name));
  }

  public static OptimizerStats optimization(String name) {
    return new OptimizerStats(name, new CompilerPassEvent(name));
  }

  private OptimizerStats(String name, AbstractOptimizationEvent jfrEvent) {
    this.name = name;
    this.jfrEvent = jfrEvent;

    stack.get().push(this);
  }

  @Override
  public void close() {
    OptimizerStats prev = stack.get().pop();
    assert prev == this;

    // If numVisits wasn't explicitly set, inherit from nearest parent
    if (this.numVisits == 0) {
      for (OptimizerStats parent : Lists.reverse(stack.get())) {
        if (parent.numVisits > 0) {
          this.numVisits = parent.numVisits;
          break;
        }
      }
    }

    jfrEvent.nodeCount = getNumVisits();
    jfrEvent.numMods = getNumMods();
    jfrEvent.commit();
  }

  /**
   * Add a child stats object.
   */
  public void add(OptimizerStats childStats) {
    children.add(childStats);
  }

  /**
   * @return <code>true</code> if the AST changed during this pass.
   */
  public boolean didChange() {
    if (numMods > 0) {
      return true;
    }
    for (OptimizerStats child : children) {
      if (child.didChange()) {
        return true;
      }
    }
    return false;
  }

  /**
   * Retrieves an immutable list of child stats objects.
   */
  public List<OptimizerStats> getChildren() {
    return Collections.unmodifiableList(children);
  }

  public String getName() {
    return name;
  }

  /**
   * @return the number of times the AST was modified by the optimizer
   */
  public int getNumMods() {
    int childMods = 0;
    for (OptimizerStats child : children) {
      childMods += child.getNumMods();
    }
    return numMods + childMods;
  }

  /**
   * @return the number of nodes visited by the optimizer
   */
  public int getNumVisits() {
    int childVisits = 0;
    for (OptimizerStats child : children) {
      childVisits += child.getNumVisits();
    }
    return numVisits + childVisits;
  }

  /**
   * Return a human-readable string representing the values of all statistics.
   */
  public String prettyPrint() {
    StringBuilder builder = new StringBuilder();
    prettyPrint(builder, 0);
    return builder.toString();
  }

  /**
   * Increment the number of times the tree was modified.
   */
  public OptimizerStats recordModified() {
    this.numMods++;
    return this;
  }

  /**
   * Increment the number of times the tree was modified.
   *
   * @param numMods the number of changes made to the AST.
   */
  public OptimizerStats recordModified(int numMods) {
    this.numMods += numMods;
    return this;
  }

  /**
   * Increment the number of times tree nodes were visited.
   */
  public OptimizerStats recordVisit() {
    this.numVisits++;
    return this;
  }

  /**
   * Increment the number of times tree nodes were visited.
   */
  public OptimizerStats recordVisits(int numVisits) {
    this.numVisits += numVisits;
    return this;
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append(String.format("%s (%d/%d)", name, getNumMods(), getNumVisits()));
    if (children.isEmpty()) {
      return sb.toString();
    }
    sb.append(" [");
    sb.append(Joiner.on(",").join(children));
    sb.append("]");
    return sb.toString();
  }

  private void prettyPrint(StringBuilder builder, int level) {
    int visits = getNumVisits();
    int mods = getNumMods();
    String ratioString = " ----";
    if (visits > 0) {
      ratioString = String.format("%5.2f", ((double) mods / (double) visits) * 100.0);
    }
    String entry = String.format("%-6s%% (%6d/%6d)", ratioString, mods, visits);
    builder.append(String.format("%12s: %-22s  ", name, entry));

    if (children.size() > 0) {
      builder.append("\n      ");
      for (int i = 0; i <= level; i++) {
        builder.append("  ");
      }
      for (OptimizerStats child : children) {
        child.prettyPrint(builder, ++level);
      }
    }
  }
}
