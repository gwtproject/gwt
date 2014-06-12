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
package com.google.gwt.core;

import com.google.gwt.core.client.*;
import com.google.gwt.core.client.impl.*;
import com.google.gwt.core.client.interop.JsTypeTest;
import com.google.gwt.core.client.prefetch.RunAsyncCodeTest;
import com.google.gwt.core.shared.SerializableThrowableTest;
import com.google.gwt.junit.tools.GWTTestSuite;
import junit.framework.Test;

/**
 * All core tests that use GWTTestCase.
 */
public class CoreSuite {
  public static Test suite() {
    GWTTestSuite suite = new GWTTestSuite("All core tests");

    suite.addTestSuite(GwtServletBaseTest.class);
    suite.addTestSuite(GWTTest.class);
    suite.addTestSuite(HttpThrowableReporterTest.class);
    suite.addTestSuite(ImplTest.class);
    suite.addTestSuite(JavaScriptExceptionTest.class);
    suite.addTestSuite(JavaScriptObjectTest.class);
    suite.addTestSuite(JsTypeTest.class);
    suite.addTestSuite(JsIdentityTest.class);
    suite.addTestSuite(JsArrayTest.class);
    suite.addTestSuite(JsArrayMixedTest.class);
    suite.addTestSuite(RunAsyncCodeTest.class);
    suite.addTestSuite(SchedulerImplTest.class);
    suite.addTestSuite(SchedulerTest.class);
    suite.addTestSuite(ScriptInjectorTest.class);
    suite.addTestSuite(SerializableThrowableTest.class);
    suite.addTestSuite(StackTraceCreatorCollectorTest.class);
    suite.addTestSuite(StackTraceDevTest.class);
    suite.addTestSuite(StackTraceEmulTest.class);
    suite.addTestSuite(StackTraceNativeTest.class);
    suite.addTestSuite(StackTraceStripTest.class);

    // Uncomment to print native stack traces for different platforms
    // suite.addTestSuite(com.google.gwt.core.client.impl.StackTraceGenerator.class);

    return suite;
  }
}
