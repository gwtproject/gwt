package com.google.gwt.uibinder.elementparsers;

import com.google.gwt.junit.TestSuiteWithOrder;
import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * Standalone suite to run just UiSafeHtmlInterpreterTest with methods in order.
 */
public class UiSafeHtmlInterpreterJreSuite {
  public static Test suite() {
    TestSuite suite = new TestSuite("UiSafeHtmlInterpreter JRE test");

    suite.addTest(new TestSuiteWithOrder(UiSafeHtmlInterpreterTest.class));

    return suite;
  }
}
