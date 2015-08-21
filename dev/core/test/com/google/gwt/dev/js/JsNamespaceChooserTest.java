/*
 * Copyright 2014 Google Inc.
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
package com.google.gwt.dev.js;


import com.google.gwt.dev.jjs.SourceOrigin;
import com.google.gwt.dev.jjs.ast.AccessModifier;
import com.google.gwt.dev.jjs.ast.HasName;
import com.google.gwt.dev.jjs.ast.JClassType;
import com.google.gwt.dev.jjs.ast.JDeclaredType;
import com.google.gwt.dev.jjs.ast.JField;
import com.google.gwt.dev.jjs.ast.JField.Disposition;
import com.google.gwt.dev.jjs.ast.JMethod;
import com.google.gwt.dev.jjs.ast.JPrimitiveType;
import com.google.gwt.dev.jjs.impl.JavaToJavaScriptMapImpl;
import com.google.gwt.dev.js.ast.JsName;
import com.google.gwt.dev.js.ast.JsNode;
import com.google.gwt.dev.js.ast.JsProgram;
import com.google.gwt.dev.js.ast.JsStatement;
import com.google.gwt.dev.js.ast.JsVisitor;
import com.google.gwt.dev.util.DefaultTextOutput;
import com.google.gwt.dev.util.TextOutput;
import com.google.gwt.thirdparty.guava.common.collect.ImmutableList;
import com.google.gwt.thirdparty.guava.common.collect.ImmutableMap;
import com.google.gwt.thirdparty.guava.common.collect.Maps;

import junit.framework.TestCase;

import java.io.IOException;
import java.io.StringReader;
import java.util.List;
import java.util.Map;

/**
 * Verifies that {@link JsNamespaceChooser} can put globals into namespaces.
 */
public class JsNamespaceChooserTest extends TestCase {
  private JsProgram program;

  // components of the jjsmap
  private JClassType comExampleFooClass =
      new JClassType(SourceOrigin.UNKNOWN, "com.example.Foo", false, false);
  // These two will get the same namespace c.s
  private JClassType comSharedPackageClass =
      new JClassType(SourceOrigin.UNKNOWN, "com.shared.Shared1", false, false);
  private JClassType comSharedPackageClass2 =
      new JClassType(SourceOrigin.UNKNOWN, "com.shared.Shared2", false, false);

  // this will be a declaration in the leftovers fragment
  private JClassType comLeftoversPackageClass =
      new JClassType(SourceOrigin.UNKNOWN, "com.leftovers.Foo", false, false);

  // these two will be in c.w and c.x in exclusive fragments
  private JClassType comExclusivePackageClass =
      new JClassType(SourceOrigin.UNKNOWN, "com.wexclusive.Foo", false, false);

  private JClassType comExclusivePackageClass2 =
      new JClassType(SourceOrigin.UNKNOWN, "com.xxclusive1.Foo", false, false);

  private JClassType defaultPackageBarClass =
      new JClassType(SourceOrigin.UNKNOWN, "Bar", false, false);

  private Map<HasName, JsName> javaToName = Maps.newHashMap();

  protected boolean closureMode = false;

  /*
   * This test proves that namespaces declared as late as possible. Namespaces shared by two or
   * more exclusive fragments have declarations hoisted to leftovers. Namespaces already declared
   * in earlier fragments (initial, leftovers) are not redeclared.
   */
  public void testCodeSplitting() throws Exception {
    program = parseJsFragments(
        "var x = 1; x = 2;", // fragment zero
        "x=4;function s1() {} function e1() {} function l1() {}", // fragment 1
        "x=5;function s2() {} function e2() {} function l2() {}", // fragment 2
        "x=6;function l() {}" // leftovers
        );
    mapJavaField("x");
    // this should produce a declaration for c.s in the leftovers fragment
    mapJavaMethod("s1", comSharedPackageClass);
    mapJavaMethod("s2", comSharedPackageClass2);
    // whereas this should produce c.w in fragment 1, and c.x in fragment 2
    mapJavaMethod("e1", comExclusivePackageClass);
    mapJavaMethod("e2", comExclusivePackageClass2);

    // and this should produce a c.l in leftovers (fragment 3)
    mapJavaMethod("l", comLeftoversPackageClass);
    mapJavaMethod("l1", comLeftoversPackageClass);
    mapJavaMethod("l2", comLeftoversPackageClass);

    checkResultForFragment(namespace("ce") + ";ce.x=1;ce.x=2;", 0);
    checkNamespaceEquals("ce", "x");

    checkResultForFragment(namespace("cw") +
        ";ce.x=4;cs.s1=function s1(){};cw.e1=function e1(){};cl.l1=function l1(){};", 1);
    checkNamespaceEquals("cs", "s1");
    checkNamespaceEquals("cw", "e1");

    checkResultForFragment(namespace("cx") +
        ";ce.x=5;cs.s2=function s2(){};cx.e2=function e2(){};cl.l2=function l2(){};", 2);
    checkNamespaceEquals("cs", "s2");
    checkNamespaceEquals("cx", "e2");

    // 'cs' is non-exclusive so hoisted to appear in leftovers
    checkResultForFragment(namespace("cl") + ";" + namespace("cs") +";ce.x=6;cl.l=function l(){};",
        3);
    checkNamespaceEquals("cl", "l");
    checkNamespaceEquals("cl", "l1");
    checkNamespaceEquals("cl", "l2");
  }

  public void testMoveJavaField() throws Exception {
    program = parseJs("var x = 1; x = 2;");
    mapJavaField("x");
    checkResult(namespace("ce") + ";ce.x=1;ce.x=2;");
    checkNamespaceEquals("ce", "x");
  }

  private String namespace(String ce) {
    return closureMode ? String.format("goog.provide('%s')", ce) : String.format("var %s={}", ce);
  }

  public void testMoveUninitializedJavaField() throws Exception {
    program = parseJs("var x; x = 1;");
    mapJavaField("x");
    checkResult(namespace("ce") + ";ce.x=1;");
    checkNamespaceEquals("ce", "x");
  }

  public void testSkipDefaultPackageMember() throws Exception {
    program = parseJs("var x = 1; x = 2;");
    mapJavaField("x", defaultPackageBarClass);
    checkResult(namespace("$g") + ";$g.x=1;$g.x=2;");
    checkNamespaceEquals("$g", "x");
  }

  public void testSkipNonJavaGlobal() throws Exception {
    program = parseJs("var x = 1; x = 2;");
    checkResult("var x=1;x=2;");
    checkNamespaceEquals(null, "x");
  }

  public void testMoveJavaFunction() throws Exception {
    program = parseJs("function f() {} f();");
    mapJavaMethod("f");
    checkResult(namespace("ce") + ";ce.f=function f(){};ce.f();");
    checkNamespaceEquals("ce", "f");
  }

  public void testSkipNonJavaFunction() throws Exception {
    program = parseJs("function f() {} f();");
    checkResult("function f(){}\nf();");
    checkNamespaceEquals(null, "f");
  }

  public void testSkipPolymorphicJavaMethod() throws Exception {
    program = parseJs("var _ = {}; _.f = function f() {};");
    mapJavaMethod("f");
    checkResult("var _={};_.f=function f(){};");
    checkNamespaceEquals(null, "_");
    checkNamespaceEquals(null, "f");
  }

  public void testSkipNamedFunctionExpression() throws Exception {
    // The name in a named function expression is not a global.
    // (The scope of the name is just the JavaScript function itself.)
    program = parseJs("var a; a = function f() {}; a();");
    mapJavaMethod("f");

    checkResult("var a;a=function f(){};a();");
  }

  /** Adds a mapping from a JavaScript global to a Java field. */
  private void mapJavaField(String name) {
    mapJavaField(name, comExampleFooClass);
  }

  /** Adds a mapping from a JavaScript global to a Java field. */
  private void mapJavaField(String name, JClassType clazz) {
    JField field = new JField(SourceOrigin.UNKNOWN, name, clazz, JPrimitiveType.INT, true,
        Disposition.NONE);
    clazz.addField(field);
    javaToName.put(field, program.getScope().findExistingName(name));
  }

  /** Adds a mapping from a JavaScript global to a Java method. */
  private void mapJavaMethod(String name) {
    mapJavaMethod(name, comExampleFooClass);
  }

  private void mapJavaMethod(String name, JClassType clazz) {
    JMethod method = new JMethod(SourceOrigin.UNKNOWN, name, clazz,
        JPrimitiveType.VOID, false, true, false, AccessModifier.DEFAULT);
    comExampleFooClass.addMethod(method);
    javaToName.put(method, program.getScope().findExistingName(name));
  }

  private void checkResult(String expectedJs) {
    exec();
    String actual = serializeJs(program);
    assertEquals(expectedJs, actual);
  }

  private void checkResultForFragment(String expectedJs, int fragment) {
    exec();
    String actual = serializeJs(program.getFragment(fragment));
    assertEquals(expectedJs, actual);
  }

  private void checkNamespaceEquals(String expectedNamespace, String global) {
    JsName globalName = program.getScope().findExistingName(global);
    assertNotNull("name doesn't exist: " + global, globalName);
    JsName actual = globalName.getNamespace();
    assertEquals("namespace is different",
        expectedNamespace, actual == null ? null : actual.getIdent());
  }

  private void exec() {
    // Prerequisite: resolve name references.
    JsSymbolResolver.exec(program);

    // Build the jjsmap.
    List<JDeclaredType> types = ImmutableList.<JDeclaredType>of(comExampleFooClass,
        defaultPackageBarClass);
    Map<JsStatement, JClassType> typeForStatement = ImmutableMap.of();
    Map<JsStatement, JMethod> vtableInitForMethod = ImmutableMap.of();
    JavaToJavaScriptMapImpl jjsmap = new JavaToJavaScriptMapImpl(types, javaToName,
        typeForStatement, vtableInitForMethod);

    // Run it.
    JsNamespaceChooser.exec(program, jjsmap, null, closureMode, null, JsNamespaceOption.PACKAGE);
  }

  private static JsProgram parseJs(String js) throws IOException, JsParserException {
    JsProgram program = new JsProgram();
    List<JsStatement> statements = JsParser.parse(SourceOrigin.UNKNOWN, program.getScope(),
        new StringReader(js));
    program.getGlobalBlock().getStatements().addAll(statements);
    return program;
  }

  private static JsProgram parseJsFragments(String... srcs) throws IOException, JsParserException {
    JsProgram program = new JsProgram();
    int fragment = 0;
    program.setFragmentCount(srcs.length);
    for (String js : srcs) {
      List<JsStatement> statements = JsParser.parse(SourceOrigin.UNKNOWN, program.getScope(),
          new StringReader(js));
      program.getFragmentBlock(fragment++).getStatements().addAll(statements);
    }
    return program;
  }

  private static String serializeJs(JsNode program1) {
    TextOutput text = new DefaultTextOutput(true);
    JsVisitor generator = new JsSourceGenerationVisitor(text);
    generator.accept(program1);
    return text.toString();
  }
}
