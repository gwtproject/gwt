package com.google.gwt.dev.util.log.perf;

import jdk.jfr.Description;
import jdk.jfr.Label;

public class OptimizationLoopEvent extends GwtJfrEvent {
  @Label("Node count")
  @Description("Number of AST nodes in the J/JsProgram at the start of the optimization loop")
  int nodeCount;

  @Label("Optimization iteration")
  @Description("The iteration number of the optimization loop")
  int optimizationIteration;

  @Label("Size change rate")
  @Description("Rate of change of the AST size (number of nodes) per iteration of the optimization loop")
  float sizeChangeRate;

  @Label("Language")
  @Description("Language being optimized (Java or JavaScript)")
  String language;
}
