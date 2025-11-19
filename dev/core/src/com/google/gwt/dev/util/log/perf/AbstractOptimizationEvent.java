package com.google.gwt.dev.util.log.perf;

import jdk.jfr.Description;
import jdk.jfr.Label;

public class AbstractOptimizationEvent extends AbstractJfrEvent {
  @Label("Number of Modifications")
  public int numMods = -1;

  @Label("Node Count")
  @Description("Number of AST nodes in the program at the start of the optimization loop")
  public int nodeCount;
}
