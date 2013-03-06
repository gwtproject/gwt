package com.google.gwt.logging.client;

import com.google.gwt.junit.GWTMockUtilities;

import junit.framework.TestCase;

import java.util.logging.Level;

/**
 * Tests that LogConfiguration can be used outside a GWT context (e.g. JRE tests)
 */
public class LogConfigurationJreTest extends TestCase {

  @Override
  public void setUp() {
    GWTMockUtilities.disarm();
  }

  @Override
  public void tearDown() {
    GWTMockUtilities.restore();
  }

  public void testLogConfiguration() {
    assertTrue(LogConfiguration.loggingIsEnabled());
    assertTrue(LogConfiguration.loggingIsEnabled(Level.FINEST));
  }
}
