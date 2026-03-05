/*
 * Copyright 2014 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package com.google.gwt.dev.js;

import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.linker.impl.NamedRange;
import com.google.gwt.dev.cfg.BindingProperty;
import com.google.gwt.dev.cfg.ConditionNone;
import com.google.gwt.dev.cfg.ConfigurationProperty;
import com.google.gwt.dev.jjs.impl.DeadCodeElimination;
import com.google.gwt.dev.jjs.impl.FullCompileTestBase;
import com.google.gwt.dev.jjs.impl.FullOptimizerContext;
import com.google.gwt.dev.util.DefaultTextOutput;
import com.google.gwt.dev.util.TextOutput;
import com.google.gwt.thirdparty.guava.common.collect.Maps;

import java.util.List;
import java.util.Map;

/**
 * Tests for JsToStringGenerationVisitor.
 */
public class JsToStringGenerationVisitorTest extends FullCompileTestBase {

  private boolean runDeadCodeElimination = false;

  // Compilation Configuration Properties.
  @Override
  public void setUp() throws Exception {
    // Compilation Configuration Properties.
    BindingProperty stackMode = new BindingProperty("compiler.stackMode");
    stackMode.addDefinedValue(new ConditionNone(), "STRIP");
    setProperties(new BindingProperty[] {stackMode}, new String[] {"STRIP"},
        new ConfigurationProperty[] {});
    runDeadCodeElimination = false;
    super.setUp();
  }

  public void testClassRangeMarking() throws UnableToCompleteException {
    // Prepares the EntryPoint class to compile.
    StringBuilder code = new StringBuilder();
    code.append("package test;\n");
    code.append("public class EntryPoint {\n");
    code.append("  public interface SomeInterface {}\n");
    code.append("  public static void onModuleLoad() {}\n");
    code.append("}\n");

    // Compiles EntryPoint to JS.
    compileSnippetToJS(code.toString());
    TextOutput text = new DefaultTextOutput(true);
    JsSourceGenerationVisitor jsSourceGenerationVisitor = new JsSourceGenerationVisitor(text);
    jsSourceGenerationVisitor.accept(jsProgram);

    // Verifies that the EntryPoint class, SomeInterface interface and some other classes were
    // delimited in the output by getClassRanges().
    List<NamedRange> classRanges = jsSourceGenerationVisitor.getClassRanges();
    Map<String, NamedRange> classRangesByName = Maps.newHashMap();
    for (NamedRange classRange : classRanges) {
      classRangesByName.put(classRange.getName(), classRange);
    }
    assertTrue(classRangesByName.containsKey("test.EntryPoint"));
    assertTrue(classRangesByName.containsKey("test.EntryPoint$SomeInterface"));
    assertTrue(classRangesByName.size() > 2);

    NamedRange programClassRange = jsSourceGenerationVisitor.getProgramClassRange();
    // Verifies there is a preamble before the program class range.
    assertTrue(programClassRange.getStartPosition() > 0);
    // Verifies there is an epilogue after the program class range.
    assertTrue(programClassRange.getEndPosition() < text.getPosition());
  }

  public void testLiteralPrint() throws UnableToCompleteException {
    TextOutput text = buildTextOutput(new JsToStringGenerationVisitor.PrintOptions(false, false));

    assertContains("_.truth=function(){return true}", text.toString());
    assertContains("_.falsehood=function(){return false}", text.toString());
    assertContains("_.zero=function(){return 0}", text.toString());
    assertContains("_.negZero=function(){return-0}", text.toString());
    assertContains("_.decimal=function(){return 0.1}", text.toString());
    assertContains("_.negDecimal=function(){return-0.1}", text.toString());
    assertContains("_.hundred=function(){return 100}", text.toString());
    assertContains("_.thousand=function(){return 1000}", text.toString());
    assertContains("_.maxDec=function(){return 999999999999}", text.toString());
    assertContains("_.minHex=function(){return 1000000000001}", text.toString());
    assertContains("_.maxAbsNegDec=function(){return-999999999999}", text.toString());
    assertContains("_.minAbsNegHex=function(){return-1000000000001}", text.toString());
  }

  public void testLiteralPrintWithDCE() throws UnableToCompleteException {
    runDeadCodeElimination = true;
    TextOutput text = buildTextOutput(new JsToStringGenerationVisitor.PrintOptions(false, false));
    assertContains("_.negZero=function(){return-0}", text.toString());
    assertContains("_.negDecimal=function(){return-0.1}", text.toString());
    assertContains("_.maxAbsNegDec=function(){return-999999999999}", text.toString());
    assertContains("_.minAbsNegHex=function(){return-1000000000001}", text.toString());
  }

  public void testLiteralMinification() throws UnableToCompleteException {
    runDeadCodeElimination = true;
    TextOutput text = buildTextOutput(new JsToStringGenerationVisitor.PrintOptions(false, true));

    assertContains("_.truth=function(){return!0}", text.toString());
    assertContains("_.falsehood=function(){return!1}", text.toString());
    assertContains("_.zero=function(){return 0}", text.toString());
    assertContains("_.negZero=function(){return-0}", text.toString());
    assertContains("_.decimal=function(){return.1}", text.toString());
    assertContains("_.negDecimal=function(){return-.1}", text.toString());
    assertContains("_.hundred=function(){return 100}", text.toString());
    assertContains("_.thousand=function(){return 1e3}", text.toString());
    assertContains("_.maxDec=function(){return 999999999999}", text.toString());
    assertContains("_.minHex=function(){return 0xe8d4a51001}", text.toString());
    assertContains("_.maxAbsNegDec=function(){return-999999999999}", text.toString());
    assertContains("_.minAbsNegHex=function(){return-0xe8d4a51001}", text.toString());
  }

  private TextOutput buildTextOutput(JsToStringGenerationVisitor.PrintOptions options)
      throws UnableToCompleteException {
    String code = "package test;\n" +
        "public class EntryPoint {\n" +
        "  public double decimal() { return 0.1;}\n" +
        "  public double negDecimal() { return -0.1;}\n" +
        "  public double zero() { return 0.0;}\n" +
        "  public double negZero() { return -0.0;}\n" +
        "  public double hundred() { return 100;}\n" +
        "  public double thousand() { return 1000;}\n" +
        "  public double maxDec() { return 999999999999.0;}\n" +
        "  public double minHex() { return 1000000000001.0;}\n" +
        "  public double maxAbsNegDec() { return -999999999999.0;}\n" +
        "  public double minAbsNegHex() { return -1000000000001.0;}\n" +
        "  public boolean truth() { return true;}\n" +
        "  public boolean falsehood() { return false;}\n" +
        "  public static void onModuleLoad() {}\n" +
        "}\n";

    // Compiles EntryPoint to JS.
    compileSnippetToJS(code);
    TextOutput text = new DefaultTextOutput(true);
    JsSourceGenerationVisitor visitor = new JsSourceGenerationVisitor(text, options);
    visitor.accept(jsProgram);
    return text;
  }

  private void assertContains(String needle, String haystack) {
    assertTrue("Should contain " + needle + " but was " + haystack, haystack.contains(needle));
  }

  @Override
  protected void optimizeJava() {
    if (runDeadCodeElimination) {
      DeadCodeElimination.exec(jProgram, new FullOptimizerContext(jProgram));
    }
  }
}
