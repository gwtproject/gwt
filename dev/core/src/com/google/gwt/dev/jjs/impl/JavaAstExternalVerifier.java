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

import com.google.gwt.dev.javac.CompilationUnit;
import com.google.gwt.dev.jjs.ast.Context;
import com.google.gwt.dev.jjs.ast.JArrayType;
import com.google.gwt.dev.jjs.ast.JDeclaredType;
import com.google.gwt.dev.jjs.ast.JExpression;
import com.google.gwt.dev.jjs.ast.JMethod;
import com.google.gwt.dev.jjs.ast.JNullType;
import com.google.gwt.dev.jjs.ast.JReferenceType;
import com.google.gwt.dev.jjs.ast.JType;
import com.google.gwt.dev.jjs.ast.JVariable;
import com.google.gwt.dev.jjs.ast.JVisitor;
import com.google.gwt.thirdparty.guava.common.collect.Lists;

import java.util.List;

/**
 * Verifies that in Gwt AST, all JReferenceType instances that are not in current compilation unit
 * are external.
 */
public class JavaAstExternalVerifier extends JVisitor {
  final List<JDeclaredType> typesInCurrentCud;
  final List<String> typeNames;

  public JavaAstExternalVerifier(List<JDeclaredType> typesInCurrentCud) {
    super();
    this.typesInCurrentCud = typesInCurrentCud;
    this.typeNames = Lists.newArrayList();
    for (JDeclaredType type : typesInCurrentCud) {
      typeNames.add(type.getName());
    }
  }

  @Override
  public void endVisit(JVariable x, Context ctx) {
    assertExternal(x.getType());
  }

  @Override
  public void endVisit(JMethod x, Context ctx) {
    assertExternal(x.getType());
  }

  @Override
  public void endVisit(JExpression x, Context ctx) {
    if (x.getType() == null) {
      return;
    }
    assertExternal(x.getType().getUnderlyingType());
  }

  private void assertExternal(JType type) {
    JType typeToCheck = type;
    if (type instanceof JArrayType) {
      typeToCheck = (((JArrayType) type).getLeafType());
    }
    if (typeToCheck == null || !(typeToCheck instanceof JReferenceType)
        || typeToCheck.equals(JNullType.INSTANCE)) {
      return;
    }
    if (!typeNames.contains(typeToCheck.getName())) {
      assert (typeToCheck.isExternal());
    }
  }

  /**
   * Throws an assertion error if a ReferenceType that is not in current compilation unit is not
   * external.
   */
  public static void assertNonExternalOnlyInCurrentCU(CompilationUnit compilationUnit) {
    JavaAstExternalVerifier verifier = new JavaAstExternalVerifier(compilationUnit.getTypes());
    for (JDeclaredType type : compilationUnit.getTypes()) {
      verifier.accept(type);
    }
  }
}
