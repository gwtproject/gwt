/*
 * Copyright 2018 Google Inc.
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
package com.google.gwt.dev.jjs.impl;

import com.google.gwt.dev.javac.testing.impl.JavaResourceBase;
import com.google.gwt.dev.jjs.ast.JDeclarationStatement;
import com.google.gwt.dev.jjs.ast.JMethod;
import com.google.gwt.dev.jjs.ast.JMethodBody;
import com.google.gwt.dev.jjs.ast.JProgram;
import com.google.gwt.dev.jjs.ast.JStatement;
import com.google.gwt.dev.jjs.ast.JVariableRef;

/**
 * Tests that {@link com.google.gwt.dev.jjs.impl.GwtAstBuilder} correctly builds the AST for
 * features introduced in Java 10.
 */
public class Java10AstTest extends FullCompileTestBase {

  @Override
  public void setUp() throws Exception {
    super.setUp();
    addAll(JavaResourceBase.createMockJavaResource("java.util.Iterator",
        "package java.util;",
        "public interface Iterator<E> {",
        "  boolean hasNext();",
        "  boolean next();",
        "}"
    ));
    addAll(JavaResourceBase.createMockJavaResource("java.lang.Iterable",
        "package java.lang;",
        "public interface Iterable<E> {",
        "  java.util.Iterator<E> iterator();",
        "}"
    ));
  }

  public void testLocalVarType_Simple() throws Exception {
    assertEqualBlock(
        "int i=42;",
        "var i=42;");
    assertEqualBlock(
        "String i=\"42\";",
        "var i=\"42\";");
  }

  public void testLocalVarType_AnonymousClass() throws Exception {
    JProgram program = compileSnippet("void", "var o=new Object(){};");
    JMethod method = findMainMethod(program);
    JStatement varDeclarationStmt = ((JMethodBody) method.getBody()).getStatements().get(0);
    JVariableRef variableRef = ((JDeclarationStatement) varDeclarationStmt).getVariableRef();
    assertEquals("Should be anonymous class name",
        "test.EntryPoint$1", variableRef.getType().getName());
  }

  public void testLocalVarType_ForLoop() throws Exception {
    assertEqualBlock(
        "for(int i=0;;);",
        "for(var i=0;;);");
  }

  public void testLocalVarType_EnhancedForLoopArray() throws Exception {
    assertEqualBlock(
          "for(final String[] s$array=new String[]{},s$index=0,s$max=s$array.length;"
        + "          s$index<s$max;++s$index){"
        + "  String s=s$array[s$index];"
        + "}"
        ,
          "for(var s : new String[]{});");
  }

  public void testLocalVarType_EnhancedForLoopIterable() throws Exception {
    assertEqualBlock(
          "for(Iterator s$iterator=((Iterable)null).iterator();s$iterator.hasNext();){"
        + "  String s=(String)s$iterator.next();"
        + "}"
          ,
          "for (var s : (Iterable<String>)null);"
        );
  }

  @Override
  protected void optimizeJava() {
  }
}