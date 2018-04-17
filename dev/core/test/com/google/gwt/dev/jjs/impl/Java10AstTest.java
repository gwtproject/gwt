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

import com.google.gwt.dev.jjs.ast.JDeclarationStatement;
import com.google.gwt.dev.jjs.ast.JMethod;
import com.google.gwt.dev.jjs.ast.JMethodBody;
import com.google.gwt.dev.jjs.ast.JProgram;
import com.google.gwt.dev.jjs.ast.JStatement;
import com.google.gwt.dev.jjs.ast.JVariableRef;
import com.google.gwt.dev.util.arg.SourceLevel;

/**
 * Tests that {@link com.google.gwt.dev.jjs.impl.GwtAstBuilder} correctly builds the AST for
 * features introduced in Java 10.
 */
public class Java10AstTest extends FullCompileTestBase {

  @Override
  public void setUp() throws Exception {
    sourceLevel = SourceLevel.JAVA10;
    super.setUp();
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
  
  @Override
  protected void optimizeJava() {
  }
}