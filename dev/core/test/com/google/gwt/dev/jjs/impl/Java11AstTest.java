/*
 * Copyright 2019 Google Inc.
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
import com.google.gwt.dev.jjs.ast.JMethod;
import com.google.gwt.dev.jjs.ast.JProgram;
import com.google.gwt.thirdparty.guava.common.base.Joiner;

/**
 * Tests that {@link com.google.gwt.dev.jjs.impl.GwtAstBuilder} correctly builds the AST for
 * features introduced in Java 11.
 */
public class Java11AstTest extends FullCompileTestBase {

  @Override
  public void setUp() throws Exception {
    super.setUp();
    addAll(JavaResourceBase.createMockJavaResource("test.NotNull",
        "package test;",
        "public @interface NotNull {",
        "}"
    ));
    addAll(JavaResourceBase.createMockJavaResource("test.Lambda",
        "package test;",
        "public interface Lambda<T> {",
        "  T run(String a, int b);",
        "}"
    ));
    addAll(JavaResourceBase.createMockJavaResource("test.Function",
        "package test;",
        "public interface Function<T, R> {",
        "  R apply(T value);",
        "}"
    ));
    addAll(JavaResourceBase.createMockJavaResource("test.Mappable",
        "package test;",
        "public interface Mappable<T> {",
        "  <R> Mappable<R> map(Function<? super T, ? extends R> mapper);",
        "}"
    ));
  }

  public void testLambdaParametersVarType() throws Exception {
    JProgram program = compileSnippet("void",
        "Lambda<String> l = (@NotNull var a, var b) -> a + b;");
    JMethod lambdaMethod = findMethod(program, "lambda$0");
    assertEquals("lambda$0(Ljava/lang/String;I)Ljava/lang/String;", lambdaMethod.getSignature());
  }

  public void testLambdaParametersVarType_function() throws Exception {
    JProgram program = compileSnippet("void", Joiner.on('\n').join(
        "Mappable<String> m = null;",
        "Mappable<String> mapped = m.map((var s) -> s);"
    ));
    JMethod lambdaMethod = findMethod(program, "lambda$0");
    assertEquals("lambda$0(Ljava/lang/String;)Ljava/lang/String;", lambdaMethod.getSignature());
  }

  @Override
  protected void optimizeJava() {
  }
}
