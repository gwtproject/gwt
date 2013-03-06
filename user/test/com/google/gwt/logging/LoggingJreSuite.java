package com.google.gwt.logging;

import com.google.gwt.logging.client.LogConfigurationJreTest;

import junit.framework.Test;
import junit.framework.TestSuite;

public class LoggingJreSuite {

  public static Test suite() {
    TestSuite suite = new TestSuite("Non-browser tests for com.google.gwt.logging");
    suite.addTestSuite(LogConfigurationJreTest.class);
    return suite;
  }
}
