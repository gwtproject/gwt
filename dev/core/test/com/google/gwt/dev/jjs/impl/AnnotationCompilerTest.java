/*
 * Copyright 2024 GWT Project Authors
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

import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.thirdparty.guava.common.base.Joiner;

public class AnnotationCompilerTest extends FullCompileTestBase {
  @Override
  protected void optimizeJava() {
  }

  public void testUseAnnotationWithoutSource() throws UnableToCompleteException {
    // The JDT represents annotations as three kinds: markers, which have no members,
    // single-member, which have only one member, and normal, which have more than one.
    // This test validates that all three work in two cases each: block scope, and class
    // scope.

    String annotations = Joiner.on(" ").join(
        "@SampleBytecodeOnlyMarkerAnnotation",
        "@SampleBytecodeOnlySingleMemberAnnotation(\"abc\")",
        "@SampleBytecodeOnlyNormalAnnotation(a=1, b=2)"
    );

    String code = Joiner.on('\n').join(
        "package test;",
        "import com.google.gwt.dev.jjs.impl.*;",
        "public class EntryPoint<T extends " + annotations + " Object> {",
        "  public static void onModuleLoad() {",
        "  }",
        "  " + annotations + " public String foo() {",
        "  return \"\";",
        "  }",
        "}");

    compileSnippetToJS(code);
  }
}
