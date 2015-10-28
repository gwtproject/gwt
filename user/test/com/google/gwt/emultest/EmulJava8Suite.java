/*
 * Copyright 2015 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.google.gwt.emultest;

import com.google.gwt.dev.util.arg.SourceLevel;
import com.google.gwt.emultest.java8.util.OptionalTest;
import com.google.gwt.junit.JUnitShell;
import com.google.gwt.junit.tools.GWTTestSuite;

import junit.framework.Test;

/**
 * Test Java8 JRE emulations.
 */
public class EmulJava8Suite {

  public static Test suite() {
    GWTTestSuite suite = new GWTTestSuite("Tests for com.google.gwt.emul.java8");

    // Only run these tests if -sourceLevel 8 (or greater) is enabled.
    if (JUnitShell.getCompilerOptions().getSourceLevel().compareTo(SourceLevel.JAVA8) < 0) {
      return suite;
    }

    //-- java.util
    suite.addTestSuite(OptionalTest.class);

    return suite;
  }
}
