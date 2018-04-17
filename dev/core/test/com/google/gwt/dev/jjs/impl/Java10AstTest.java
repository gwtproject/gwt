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
          "String[]list=new String[]{\"4\",\"2\"};"
        + "String str=\"\";"
        + "for(int i=0;i<list.length;i++){"
          + "str+=list[i];"
        + "}"
        ,
          "var list=new String[]{\"4\", \"2\"};"
        + "var str = \"\";"
        + "for(var i=0; i<list.length; i++){"
        + "  str += list[i];"
        + "}");
  }

  public void testLocalVarType_EnhancedForLoopArray() throws Exception {
    assertEqualBlock(
          "String[]list=new String[]{\"4\",\"2\"};"
        + "String str=\"\";"
        + "for(final String[]s$array=list,s$index=0,s$max=s$array.length;s$index<s$max;++s$index){"
        + "String s=s$array[s$index];"
        + "str+=s;"
        + "}"
        ,
          "var list=new String[]{\"4\", \"2\"};"
        + "var str = \"\";"
        + "for(var s : list){"
        + "  str += s;"
        + "}");
  }

  public void testLocalVarType_EnhancedNestedForLoopArray() throws Exception {
    assertEqualBlock(
          "int[][]m=new int[][]{new int[]{1,2},new int[]{3,4}};"
        + "int summ=0;"
        + "for(final int[][]row$array=m,row$index=0,row$max=row$array.length;row$index<row$max;"
                  + "++row$index){"
          + "int[]row=row$array[row$index];"
          + "for(final int[]cell$array=row,cell$index=0,cell$max=cell$array.length;"
                    + "cell$index<cell$max;++cell$index){"
            + "int cell=cell$array[cell$index];"
            + "summ+=cell;"
          + "}"
        + "}"
          ,
          "var m = new int[][]{{1, 2},{3, 4}};"
        + "var summ = 0;"
        + "for(var row : m){"
        + "  for(var cell : row){"
        + "    summ += cell;"
        + "  }"
        + "}");
  }

  public void testLocalVarType_EnhancedForLoopIterable() throws Exception {
    assertEqualBlock(
          "EntryPoint$1 it=new EntryPoint$1(this);"
        + "String str=\"\";"
        + "for(Iterator s$iterator=it.iterator();s$iterator.hasNext();){"
          + "String s=(String)s$iterator.next();"
          + "str+=s;"
        + "}"
          ,
          "var it = new java.lang.Iterable<String>() {"
        + "  public java.util.Iterator<String> iterator(){"
        + "    return null;"
        + "  }"
        + "};"
        + "String str = \"\";"
        + "for (var s : it) {" + 
        "    str += s;\n" + 
        "}"
        );
  }

  @Override
  protected void optimizeJava() {
  }
}