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
package com.google.gwt.dev.jjs;

import com.google.gwt.dev.jjs.test.JsniConstructorTest;
import com.google.gwt.junit.tools.GWTTestSuite;

import junit.framework.Test;

/**
 * The complete compiler suite.
 */
public class CompilerSuite {

  public static Test suite() {
    GWTTestSuite suite = new GWTTestSuite("Test for com.google.gwt.dev.jjs");

    suite.addTestSuite(JsniConstructorTest.class);
    // $JUnit-BEGIN$
/*    suite.addTestSuite(AnnotationsTest.class);
    suite.addTestSuite(AutoboxTest.class);
    suite.addTestSuite(BlankInterfaceTest.class);
    suite.addTestSuite(ClassCastTest.class);
    suite.addTestSuite(ClassObjectTest.class);
    suite.addTestSuite(CodeSplitterCollapsedPropertiesTest.class);
    suite.addTestSuite(CompilerTest.class);
    suite.addTestSuite(CompilerMiscRegressionTest.class);
    suite.addTestSuite(CoverageTest.class);
    suite.addTestSuite(EnhancedForLoopTest.class);
    suite.addTestSuite(EnumsTest.class);
    suite.addTestSuite(EnumsWithNameObfuscationTest.class);
    suite.addTestSuite(GenericCastTest.class);
    suite.addTestSuite(GwtIncompatibleTest.class);
    suite.addTestSuite(HostedTest.class);
    suite.addTestSuite(InitialLoadSequenceTest.class);
    suite.addTestSuite(InnerClassTest.class);
    suite.addTestSuite(InnerOuterSuperTest.class);
    suite.addTestSuite(Java7Test.class);
    suite.addTestSuite(JavaAccessFromJavaScriptTest.class);
    suite.addTestSuite(JsoTest.class);
    suite.addTestSuite(JsStaticEvalTest.class);
    suite.addTestSuite(JStaticEvalTest.class);
    suite.addTestSuite(MemberShadowingTest.class);
    suite.addTestSuite(MethodBindTest.class);
    suite.addTestSuite(MethodCallTest.class);
    suite.addTestSuite(MethodInterfaceTest.class);
    suite.addTestSuite(MiscellaneousTest.class);
    suite.addTestSuite(NativeLongTest.class);
    suite.addTestSuite(ObjectIdentityTest.class);
    suite.addTestSuite(RunAsyncContentTest.class);
    suite.addTestSuite(RunAsyncFailureTest.class);
    suite.addTestSuite(RunAsyncMetricsIntegrationTest.class);
    suite.addTestSuite(RunAsyncTest.class);
    suite.addTestSuite(ScriptOnlyTest.class);
    suite.addTestSuite(SingleJsoImplTest.class);
    suite.addTestSuite(StackTraceLineNumbersTest.class);
    suite.addTestSuite(TypeHierarchyTest.class);
    suite.addTestSuite(UnusedImportsTest.class);
    suite.addTestSuite(UnstableGeneratorTest.class);
    suite.addTestSuite(VarargsTest.class);
*/    // $JUnit-END$

    return suite;
  }
}
