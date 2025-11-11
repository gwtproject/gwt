package com.google.gwt.dev.util.log.perf;

import jdk.jfr.Description;
import jdk.jfr.Label;

public class OptimizationLoopEvent extends GwtJfrEvent {
  @Label("Node count")
  @Description("Number of AST nodes in the program at the start of the optimization loop")
  int nodeCount;

  @Label("Optimization iteration")
  @Description("The iteration number of the optimization loop")
  final int optimizationIteration;

  @Label("Size change rate")
  @Description("Rate of change of the AST size (number of nodes) per iteration of the optimization loop. Only measured for the whole loop, too expensive to measure at each step.")
  float sizeChangeRate;

  @Label("Language")
  @Description("Language being optimized (Java or JavaScript)")
  final String language;

  public OptimizationLoopEvent(int loopCount, String language) {
    this.optimizationIteration = loopCount;
    this.language = language;
  }

  public void loopComplete(int afterNodeCount) {
    this.sizeChangeRate = (nodeCount - afterNodeCount) / (float) nodeCount;
  }
}
