/*
 * Copyright 2015 Google Inc.
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
package com.google.gwt.dev.jjs.impl;

import com.google.gwt.dev.jjs.SourceInfo;
import com.google.gwt.dev.jjs.ast.AccessModifier;
import com.google.gwt.dev.jjs.ast.Context;
import com.google.gwt.dev.jjs.ast.JConstructor;
import com.google.gwt.dev.jjs.ast.JExpression;
import com.google.gwt.dev.jjs.ast.JMethod;
import com.google.gwt.dev.jjs.ast.JMethodBody;
import com.google.gwt.dev.jjs.ast.JMethodCall;
import com.google.gwt.dev.jjs.ast.JModVisitor;
import com.google.gwt.dev.jjs.ast.JNewInstance;
import com.google.gwt.dev.jjs.ast.JParameter;
import com.google.gwt.dev.jjs.ast.JParameterRef;
import com.google.gwt.dev.jjs.ast.JProgram;
import com.google.gwt.dev.jjs.ast.JReturnStatement;
import com.google.gwt.thirdparty.guava.common.collect.Lists;

import java.util.List;

/**
 * Performs normalizations related to JsInterop.
 *
 * TODO(goktug): Move other normalizations hidden in GenerateJavascriptAST to here.
 */
public class JsInteropNormalizer {

  public static void exec(JProgram program) {
    new JsInteropNormalizer(program).execImpl();
  }

  private final JProgram program;

  private JsInteropNormalizer(JProgram program) {
    this.program = program;
  }

  private void execImpl() {
    JsInteropVisitor visitor = new JsInteropVisitor();
    visitor.accept(program);
  }

  private class JsInteropVisitor extends JModVisitor {
    @Override
    public void endVisit(JConstructor x, Context ctx) {
      constructorToFactory(x);
    }

    private void constructorToFactory(JConstructor x) {
      if (!x.isExported() || x.getExportName().isEmpty()) {
        return;
      }

      JMethod factory = createDevirtualMethodFor(x);
      factory.setExportInfo(x.getExportNamespace(), x.getExportName());

      // Clear the export from the constructor to not special case it in elsewhere.
      x.setExportInfo(null, null);
    }
  }

  private JMethod createDevirtualMethodFor(JConstructor ctor) {
    SourceInfo sourceInfo = ctor.getSourceInfo().makeChild();

    String prefix = computeEscapedSignature(ctor.getSignature());
    JMethod factoryMethod = new JMethod(sourceInfo, prefix + "__jsfactory$",
        ctor.getEnclosingType(), ctor.getType(), false, true, true, AccessModifier.PUBLIC);
    factoryMethod.setSynthetic();
    for (JParameter oldParam : ctor.getParams()) {
      JProgram.createParameter(sourceInfo, oldParam.getName(), oldParam.getType(), true, false,
          factoryMethod);
    }
    factoryMethod.freezeParamTypes();
    factoryMethod.addThrownExceptions(ctor.getThrownExceptions());

    JMethodBody body = new JMethodBody(sourceInfo);
    JExpression dispatchExpression = createDispatch(factoryMethod, ctor);
    body.getBlock().addStmt(new JReturnStatement(sourceInfo, dispatchExpression));
    factoryMethod.setBody(body);

    ctor.getEnclosingType().addMethod(factoryMethod);
    sourceInfo.addCorrelation(sourceInfo.getCorrelator().by(factoryMethod));
    return factoryMethod;
  }

  private JExpression createDispatch(JMethod dispatchFrom, JConstructor dispatchTo) {
    List<JParameter> parameters = Lists.newArrayList(dispatchFrom.getParams());
    SourceInfo sourceInfo = dispatchFrom.getSourceInfo();
    JMethodCall dispatchCall = new JNewInstance(sourceInfo, dispatchTo);
    for (JParameter param : parameters) {
      dispatchCall.addArg(new JParameterRef(sourceInfo, param));
    }
    return dispatchCall;
  }

  private static String computeEscapedSignature(String methodSignature) {
    return methodSignature.replaceAll("[\\<\\>\\(\\)\\;\\/\\[]", "_");
  }
}
