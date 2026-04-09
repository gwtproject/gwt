package com.google.gwt.dev.util.log.perf;

import com.google.gwt.dev.About;
import jdk.jfr.Category;
import jdk.jfr.Description;
import jdk.jfr.Event;
import jdk.jfr.StackTrace;

import java.util.Arrays;

@Description("Basic startup info about the current GWT process")
@Category("GWT")
@StackTrace(false)
public class GwtStartupEvent extends Event {
  @Description("GWT compiler version")
  final String gwtVersion = About.getGwtVersion();

  @Description("Git revision of the GWT compiler build")
  final String gwtCommit = About.getGwtGitRev();

  @Description("Class being invoked to start the compiler")
  final String main;

  @Description("String created from options built from command line arguments and defaults")
  final String options;

  public GwtStartupEvent(Class<?> main, String... options) {
    this.main = main.getCanonicalName();
    this.options = Arrays.toString(options);
    commit();
  }
}
