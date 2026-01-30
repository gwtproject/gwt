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
import com.google.gwt.dev.jjs.SourceInfo;
import com.google.gwt.dev.jjs.SourceOrigin;
import com.google.gwt.dev.jjs.impl.FullCompileTestBase;
import com.google.gwt.dev.js.ast.JsExprStmt;
import com.google.gwt.dev.js.ast.JsFunction;
import com.google.gwt.dev.js.ast.JsIf;
import com.google.gwt.dev.js.ast.JsStatement;
import com.google.gwt.dev.util.DefaultTextOutput;
import com.google.gwt.dev.util.TextOutput;
import com.google.gwt.thirdparty.guava.common.collect.Maps;

import java.io.IOException;
import java.io.StringReader;
import java.util.List;
import java.util.Map;

/**
 * Tests for JsToStringGenerationVisitor.
 */
public class JsToStringGenerationVisitorTest extends FullCompileTestBase {

  // Compilation Configuration Properties.
  @Override
  public void setUp() throws Exception {
    // Compilation Configuration Properties.
    BindingProperty stackMode = new BindingProperty("compiler.stackMode");
    stackMode.addDefinedValue(new ConditionNone(), "STRIP");
    setProperties(new BindingProperty[] {stackMode}, new String[] {"STRIP"},
        new ConfigurationProperty[] {});
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
  public class EntryPoint {
    private void go(int i) {
    }
    public void onModuleLoad() {
      boolean a = true, b = true, c = true;
      if (a)
        if (b)
          go(1);
        else
          go(2);
    }

  }

  public void testDanglingElse() throws Exception {
    // No braces, ELSE will be attached to the inner IF
    String statements = """
          if (a)
            if (b)
              go(1);
            else
              go(2);
        """;
    JsStatement result = compileAndParseStatement(statements);
    assertNotNull(result);
    assertTrue(result instanceof JsIf);
    JsIf outerIf = (JsIf) result;
    assertTrue(outerIf.getThenStmt() instanceof JsIf);
    JsIf innerIf = (JsIf) outerIf.getThenStmt();
    assertNotNull(innerIf.getElseStmt());
    assertNull(outerIf.getElseStmt());
  }

  private JsStatement compileAndParseStatement(String statements) throws UnableToCompleteException, IOException, JsParserException {
    String code = """
      package test;
      public class EntryPoint {
        private static boolean a = true, b = true, c = true;
        private static void go(int i) {
        }
        public static void onModuleLoad() {
""" + statements + """
        }
      }
      """;
    compileSnippetToJS(code);
    TextOutput text = new DefaultTextOutput(true);
    JsSourceGenerationVisitor jsSourceGenerationVisitor = new JsSourceGenerationVisitor(text);
    jsSourceGenerationVisitor.accept(jsProgram);

    List<NamedRange> classRanges = jsSourceGenerationVisitor.getClassRanges();
    String entrypoint = classRanges.stream().filter(r -> r.getName().equals("test.EntryPoint")).findFirst().map(r -> {
      return text.toString().substring(r.getStartPosition(), r.getEndPosition());
    }).get();

    // Parse the source to be sure that the printed output has the expected characteristics
    List<JsStatement> parsed = JsParser.parse(SourceOrigin.UNKNOWN, jsProgram.getScope(), new StringReader(entrypoint));

    JsStatement result = null;
    for (JsStatement jsStatement : parsed) {
      if (jsStatement instanceof JsExprStmt && ((JsExprStmt) jsStatement).getExpression() instanceof JsFunction) {
        JsFunction jsFunction = (JsFunction) ((JsExprStmt) jsStatement).getExpression();
        if (jsFunction.getName().getShortIdent().equals("onModuleLoad")) {
          // In case of clinit, return the last statement only
          List<JsStatement> s = jsFunction.getBody().getStatements();
          result = s.get(s.size() - 1);
          break;
        }
      }
    }
    return result;
  }

  @Override
  protected void optimizeJava() {
  }
}
