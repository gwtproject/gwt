package com.google.gwt.dev.util.log.perf;

import jdk.jfr.Category;
import jdk.jfr.Event;
import jdk.jfr.Experimental;
import jdk.jfr.StackTrace;

/**
 * Simple abstract class to make it easier to measure time consumed by the compiler.
 */
@Category("GWT")
@StackTrace(false)
@Experimental
public class AbstractJfrEvent extends Event implements AutoCloseable {
  protected AbstractJfrEvent() {
    begin();
  }

  @Override
  public void close() {
    commit();
  }
}
