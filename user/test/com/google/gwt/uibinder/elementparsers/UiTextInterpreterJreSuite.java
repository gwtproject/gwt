package com.google.gwt.uibinder.elementparsers;

import com.google.gwt.junit.TestSuiteWithOrder;
import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * Standalone suite to run just UiTextInterpreterTest with methods in order.
 */
public class UiTextInterpreterJreSuite {
  public static Test suite() {
    TestSuite suite = new TestSuite("UiTextInterpreter JRE test");

    suite.addTest(new TestSuiteWithOrder(UiTextInterpreterTest.class));

    return suite;
  }
}