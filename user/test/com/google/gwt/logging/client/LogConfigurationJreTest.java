package com.google.gwt.logging.client;

import junit.framework.TestCase;

import java.util.logging.Level;

/**
 * Tests that LogConfiguration can be used outside a GWT context (e.g. JRE tests)
 */
public class LogConfigurationJreTest extends TestCase {

  public void testLogConfiguration() {
    assertTrue(LogConfiguration.loggingIsEnabled());
    assertTrue(LogConfiguration.loggingIsEnabled(Level.FINEST));
  }
}
