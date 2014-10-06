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
package com.google.gwt.resources;

import com.google.gwt.resources.converter.AlternateAnnotationCreatorVisitorTest;
import com.google.gwt.resources.converter.Css2GssTest;
import com.google.gwt.resources.converter.DefCollectorVisitorTest;
import com.google.gwt.resources.converter.ElseNodeCreatorTest;
import com.google.gwt.resources.converter.FontFamilyVisitorTest;
import com.google.gwt.resources.converter.UndefinedConstantVisitorTest;
import com.google.gwt.resources.css.CssExternalTest;
import com.google.gwt.resources.css.CssNodeClonerTest;
import com.google.gwt.resources.css.CssReorderTest;
import com.google.gwt.resources.css.CssRtlTest;
import com.google.gwt.resources.css.ExtractClassNamesVisitorTest;
import com.google.gwt.resources.css.UnknownAtRuleTest;
import com.google.gwt.resources.ext.ResourceGeneratorUtilTest;
import com.google.gwt.resources.gss.ExternalClassesCollectorTest;
import com.google.gwt.resources.gss.RenamingSubstitutionMapTest;
import com.google.gwt.resources.rg.CssClassNamesTestCase;
import com.google.gwt.resources.rg.CssOutputTestCase;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * JRE tests of the ClientBundle framework.
 */
public class ResourcesJreSuite {
  public static Test suite() {

    TestSuite suite = new TestSuite("JRE test for com.google.gwt.resources");
    suite.addTestSuite(CssClassNamesTestCase.class);
    suite.addTestSuite(CssExternalTest.class);
    suite.addTestSuite(CssNodeClonerTest.class);
    suite.addTestSuite(CssReorderTest.class);
    suite.addTestSuite(CssRtlTest.class);
    suite.addTestSuite(ExtractClassNamesVisitorTest.class);
    suite.addTestSuite(ResourceGeneratorUtilTest.class);
    suite.addTestSuite(UnknownAtRuleTest.class);

    // GSS tests
    suite.addTestSuite(ExternalClassesCollectorTest.class);
    suite.addTestSuite(RenamingSubstitutionMapTest.class);

    // CSS to GSS converter tests
    suite.addTestSuite(Css2GssTest.class);
    suite.addTestSuite(CssOutputTestCase.class);
    suite.addTestSuite(DefCollectorVisitorTest.class);
    suite.addTestSuite(ElseNodeCreatorTest.class);
    suite.addTestSuite(AlternateAnnotationCreatorVisitorTest.class);
    suite.addTestSuite(FontFamilyVisitorTest.class);
    suite.addTestSuite(UndefinedConstantVisitorTest.class);

    return suite;
  }
}
