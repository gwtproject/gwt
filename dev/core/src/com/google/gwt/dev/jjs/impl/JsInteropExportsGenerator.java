/*
 * Copyright 2015 Google Inc.
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

import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.dev.jjs.SourceInfo;
import com.google.gwt.dev.jjs.ast.HasName;
import com.google.gwt.dev.jjs.ast.JDeclaredType;
import com.google.gwt.dev.jjs.ast.JMember;
import com.google.gwt.dev.js.ast.JsBinaryOperation;
import com.google.gwt.dev.js.ast.JsBinaryOperator;
import com.google.gwt.dev.js.ast.JsExpression;
import com.google.gwt.dev.js.ast.JsFunction;
import com.google.gwt.dev.js.ast.JsName;
import com.google.gwt.dev.js.ast.JsNameRef;
import com.google.gwt.dev.js.ast.JsProgram;
import com.google.gwt.dev.js.ast.JsStatement;

import java.util.List;
import java.util.Map;

/**
 * Responsible for handling @JsExport code generation for non-Closure formatted code.
 */
public abstract class JsInteropExportsGenerator {

  public static JsInteropExportsGenerator create(boolean closureCompilerFormatEnabled, JsProgram
      jsProgram, JsName globalTemp, List<JsStatement> exportStmts,
      Map<String, JsFunction> indexedFunctions,
      Map<HasName, JsName> names) {
    if (closureCompilerFormatEnabled) {
      return new ClosureJsInteropExportsGeneratorImpl(jsProgram, globalTemp, exportStmts,
          indexedFunctions, names);
    } else {
      return new DefaultJsInteropExportsGeneratorImpl(jsProgram, globalTemp, exportStmts,
          indexedFunctions, names);
    }
  }

  /*
   * Exports a member as
   * {goog.|JCHSU.}provide('foo.bar.ClassCtorNameOrClassSimpleName')
   */
  public abstract void exportMember(JDeclaredType x, JMember member, TreeLogger logger);

  /**
   * If the type itself needs special setup (e.g. namespace provisioned), do so. Returns false
   * if the members of this type should not be exported.
   */
  public abstract boolean exportType(JDeclaredType x, TreeLogger branch);

  static JsExpression createAssignment(JsExpression lhs, JsExpression rhs) {
    return new JsBinaryOperation(lhs.getSourceInfo(), JsBinaryOperator.ASG, lhs, rhs);
  }
}
