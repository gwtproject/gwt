package com.google.gwt.dev.util.log.perf;

import jdk.jfr.Label;

public class OptimizationStepEvent extends GwtJfrEvent {
  @Label("Optimization Name")
  String name;

  @Label("Number of Modifications")
  int numMods;

  @Label("Number of Visits")
  int numVisits;
}
