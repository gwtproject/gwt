package com.google.gwt.dev.shell.jetty;

import com.google.gwt.dev.shell.jetty.client.JspTest;
import com.google.gwt.junit.tools.GWTTestSuite;

import junit.framework.Test;

/**
 * All JettyLauncher tests that use GWTTestCase.
 */
public class JettyLauncherSuite {
  public static Test suite() {
    GWTTestSuite suite = new GWTTestSuite("All JettyLauncher tests");

    suite.addTestSuite(JspTest.class);

    return suite;
  }
}
