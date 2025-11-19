package com.google.gwt.dev.util.log.perf;

import jdk.jfr.Description;

/**
 * Simple JFR event impl that logs a name and timestamp/duration.
 */
@Description("General event for measuring time taken by a named task")
public class SimpleEvent extends AbstractJfrEvent {
  @Description("Name of the task being measured")
  public final String name;

  public SimpleEvent(String name) {
    this.name = name;
  }
}
