/*
 * Copyright 2008 Google Inc.
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

import com.google.gwt.emultest.java.math.MathContextTest;
import com.google.gwt.emultest.java.math.RoundingModeTest;
import com.google.gwt.emultest.java.security.MessageDigestTest;
import com.google.gwt.junit.tools.GWTTestSuite;

import junit.framework.Test;

/**
 * Test JRE emulations.
 */
public class EmulMiscSuite {

  /**
   * Note: due to compiler error, only can use one Test Case at a time.
   */
  public static Test suite() {
    GWTTestSuite suite = new GWTTestSuite("Tests for com.google.gwt.emul.java");

    // $JUnit-BEGIN$

    //-- java.math
    // BigDecimal is tested in {@link BigDecimalSuite}
    // BigInteger is tested in {@link BigIntegerSuite}
    suite.addTestSuite(RoundingModeTest.class);
    suite.addTestSuite(MathContextTest.class);


    //-- java.security
    suite.addTestSuite(MessageDigestTest.class);
    // $JUnit-END$

    return suite;
  }
}
