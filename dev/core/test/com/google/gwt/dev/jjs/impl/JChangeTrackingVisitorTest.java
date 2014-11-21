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
package com.google.gwt.dev.jjs.impl;

import com.google.gwt.dev.jjs.SourceOrigin;
import com.google.gwt.dev.jjs.ast.Context;
import com.google.gwt.dev.jjs.ast.JConstructor;
import com.google.gwt.dev.jjs.ast.JMethod;
import com.google.gwt.dev.jjs.ast.JParameter;
import com.google.gwt.dev.jjs.ast.JPrimitiveType;
import com.google.gwt.dev.jjs.ast.JProgram;

/**
 * Test for {@link JChangeTrackingVisitor}.
 */
public class JChangeTrackingVisitorTest extends JJSTestBase {

  private static final class AddParamWhenEnterMethodVisitor extends JChangeTrackingVisitor {

    public AddParamWhenEnterMethodVisitor(OptimizerContext optimizerCtx) {
      super(optimizerCtx);
    }

    @Override
    public boolean enterMethod(JMethod x, Context ctx) {
      x.addParam(new JParameter(SourceOrigin.UNKNOWN,
          "_newParam_enter",
          JPrimitiveType.INT,
          false,
          false,
          x));
      return true;
    }
  }

  private static final class AddParamWhenExitMethodVisitor extends JChangeTrackingVisitor {
    public AddParamWhenExitMethodVisitor(OptimizerContext optimizerCtx) {
      super(optimizerCtx);
    }

    @Override
    public void exitMethod(JMethod x, Context ctx) {
      x.addParam(new JParameter(SourceOrigin.UNKNOWN,
          "_newParam_exit",
          JPrimitiveType.INT,
          false,
          false,
          x));
    }
  }

  private static final class AddParamsWhenEnterAndExitMethodVisitor extends JChangeTrackingVisitor {
    public AddParamsWhenEnterAndExitMethodVisitor(OptimizerContext optimizerCtx) {
      super(optimizerCtx);
    }

    @Override
    public boolean enterMethod(JMethod x, Context ctx) {
      x.addParam(new JParameter(SourceOrigin.UNKNOWN,
          "_newParam_enter",
          JPrimitiveType.INT,
          false,
          false,
          x));
      return true;
    }

    @Override
    public void exitMethod(JMethod x, Context ctx) {
      x.addParam(new JParameter(SourceOrigin.UNKNOWN,
          "_newParam_exit",
          JPrimitiveType.INT,
          false,
          false,
          x));
    }
  }

  private static final class AddParamsWhenEnterNonConstructorMethodVisitor extends
      JChangeTrackingVisitor {
    public AddParamsWhenEnterNonConstructorMethodVisitor(OptimizerContext optimizerCtx) {
      super(optimizerCtx);
    }

    @Override
    public boolean enterMethod(JMethod x, Context ctx) {
      x.addParam(new JParameter(SourceOrigin.UNKNOWN,
          "_newParam_enter",
          JPrimitiveType.INT,
          false,
          false,
          x));
      return true;
    }

    @Override
    public boolean enterConstructor(JConstructor x, Context ctx) {
      return true;
    }
  }

  private static final class AddParamsWhenExitNonConstructorMethodVisitor extends
      JChangeTrackingVisitor {
    public AddParamsWhenExitNonConstructorMethodVisitor(OptimizerContext optimizerCtx) {
      super(optimizerCtx);
    }

    @Override
    public void exitMethod(JMethod x, Context ctx) {
      x.addParam(new JParameter(SourceOrigin.UNKNOWN,
          "_newParam_exit",
          JPrimitiveType.INT,
          false,
          false,
          x));
    }

    @Override
    public void exitConstructor(JConstructor x, Context ctx) {
      return;
    }
  }

  public void testAddParamWhenEnterMethodVisitor() throws Exception {
    addSnippetClassDecl("static class A {", "  public int field;",
        "  public A(int f) { field = f; }",
        "  public void fun () { for(int i = 3; i < 4; i++) i = 8; }", "}");
    JProgram program = compileSnippet("void", "");
    AddParamWhenEnterMethodVisitor addParamWhenEnterMethodVisitor =
        new AddParamWhenEnterMethodVisitor(new OptimizerContext(program));
    addParamWhenEnterMethodVisitor.accept(program);
    assertEquals("public void fun(int _newParam_enter);\n",
        findMethod(program.getFromTypeMap("test.EntryPoint$A"), "fun").toString());
    assertEquals("public EntryPoint$A(int f, int _newParam_enter);\n",
        findMethod(program.getFromTypeMap("test.EntryPoint$A"), "EntryPoint$A").toString());
  }

  public void testAddParamWhenExitMethodVisitor() throws Exception {
    addSnippetClassDecl("static class A {", "  public int field;",
        "  public A(int f) { field = f; }",
        "  public void fun () { for(int i = 3; i < 4; i++) i = 8; }", "}");
    JProgram program = compileSnippet("void", "");
    AddParamWhenExitMethodVisitor addParamWhenExitMethodVisitor =
        new AddParamWhenExitMethodVisitor(new OptimizerContext(program));
    addParamWhenExitMethodVisitor.accept(program);
    assertEquals("public void fun(int _newParam_exit);\n",
        findMethod(program.getFromTypeMap("test.EntryPoint$A"), "fun").toString());
    assertEquals("public EntryPoint$A(int f, int _newParam_exit);\n",
        findMethod(program.getFromTypeMap("test.EntryPoint$A"), "EntryPoint$A").toString());
  }

  public void testAddParamsWhenEnterAndExitMethodVisitor() throws Exception {
    addSnippetClassDecl("static class A {", "  public int field;",
        "  public A(int f) { field = f; }",
        "  public void fun () { for(int i = 3; i < 4; i++) i = 8; }", "}");
    JProgram program = compileSnippet("void", "");
    AddParamsWhenEnterAndExitMethodVisitor addParamsWhenEnterAndExitMethodVisitor =
        new AddParamsWhenEnterAndExitMethodVisitor(new OptimizerContext(program));
    addParamsWhenEnterAndExitMethodVisitor.accept(program);
    assertEquals("public void fun(int _newParam_enter, int _newParam_exit);\n",
        findMethod(program.getFromTypeMap("test.EntryPoint$A"), "fun").toString());
    assertEquals("public EntryPoint$A(int f, int _newParam_enter, int _newParam_exit);\n",
        findMethod(program.getFromTypeMap("test.EntryPoint$A"), "EntryPoint$A").toString());
  }

  public void testAddParamsWhenEnterNonConstructorMethodVisitor() throws Exception {
    addSnippetClassDecl("static class A {", "  public int field;",
        "  public A(int f) { field = f; }",
        "  public void fun () { for(int i = 3; i < 4; i++) i = 8; }", "}");
    JProgram program = compileSnippet("void", "");
    AddParamsWhenEnterNonConstructorMethodVisitor addParamsWhenEnterNonConstructorMethodVisitor =
        new AddParamsWhenEnterNonConstructorMethodVisitor(new OptimizerContext(program));
    addParamsWhenEnterNonConstructorMethodVisitor.accept(program);
    assertEquals("public void fun(int _newParam_enter);\n",
        findMethod(program.getFromTypeMap("test.EntryPoint$A"), "fun").toString());
    assertEquals("public EntryPoint$A(int f);\n",
        findMethod(program.getFromTypeMap("test.EntryPoint$A"), "EntryPoint$A").toString());
  }

  public void testAddParamsWhenExitNonConstructorMethodVisitor() throws Exception {
    addSnippetClassDecl("static class A {", "  public int field;",
        "  public A(int f) { field = f; }",
        "  public void fun () { for(int i = 3; i < 4; i++) i = 8; }", "}");
    JProgram program = compileSnippet("void", "");
    AddParamsWhenExitNonConstructorMethodVisitor addParamsWhenExitNonConstructorMethodVisitor =
        new AddParamsWhenExitNonConstructorMethodVisitor(new OptimizerContext(program));
    addParamsWhenExitNonConstructorMethodVisitor.accept(program);
    assertEquals("public void fun(int _newParam_exit);\n",
        findMethod(program.getFromTypeMap("test.EntryPoint$A"), "fun").toString());
    assertEquals("public EntryPoint$A(int f);\n",
        findMethod(program.getFromTypeMap("test.EntryPoint$A"), "EntryPoint$A").toString());
  }
}