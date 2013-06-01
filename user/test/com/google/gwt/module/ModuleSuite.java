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
package com.google.gwt.module;

import com.google.gwt.junit.tools.GWTTestSuite;
import com.google.gwt.module.client.ConfigurationPropertiesTest;
import com.google.gwt.module.client.DoubleScriptInjectionTest;
import com.google.gwt.module.client.NoDeployTest;
import com.google.gwt.module.client.ScriptInjectionEncodingTest;
import com.google.gwt.module.client.SingleScriptInjectionTest;

import junit.framework.Test;

/**
 * Tests script and resource injection.
 */
public class ModuleSuite {
  public static Test suite() {
    GWTTestSuite suite = new GWTTestSuite();

    suite.addTestSuite(ConfigurationPropertiesTest.class);
    suite.addTestSuite(SingleScriptInjectionTest.class);
    suite.addTestSuite(DoubleScriptInjectionTest.class);
    suite.addTestSuite(ScriptInjectionEncodingTest.class);
    suite.addTestSuite(NoDeployTest.class);

    return suite;
  }
}
