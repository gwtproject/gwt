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

package com.google.gwt.dev.jjs.impl;

import com.google.gwt.dev.jjs.ast.Context;
import com.google.gwt.dev.jjs.ast.JDeclaredType;
import com.google.gwt.dev.jjs.ast.JField;
import com.google.gwt.dev.jjs.ast.JFieldRef;
import com.google.gwt.dev.jjs.ast.JMethod;
import com.google.gwt.dev.jjs.ast.JMethodCall;
import com.google.gwt.dev.jjs.ast.JNode;
import com.google.gwt.dev.jjs.ast.JProgram;
import com.google.gwt.dev.jjs.ast.JVisitor;
import com.google.gwt.thirdparty.guava.common.collect.HashMultimap;
import com.google.gwt.thirdparty.guava.common.collect.Multimap;

/**
 * Verifies that all the references from AST nodes to AST nodes are reachable from the
 * to of the AST.
 * <p>
 * The purpose fo this pass is to verify the consistency of the AST after a specific pass has
 * run.
 */
public class Verifier extends JVisitor {

  private JProgram program;
  private Multimap<JDeclaredType, JNode> classMembers = HashMultimap.create();

  Verifier(JProgram program) {
    for (JDeclaredType type :program.getModuleDeclaredTypes()) {
      classMembers.putAll(type, type.getMethods());
      classMembers.putAll(type, type.getFields());
    }
    this.program = program;
  }

  /**
   * Verifies the consistency of the AST for a program, throwing an failing
   */
  public static void assertProgramIsConsistent(JProgram program) {
    if (Verifier.class.desiredAssertionStatus()) {
      new Verifier(program).accept(program);
    }
  }

  @Override
  public void endVisit(JMethodCall x, Context ctx) {
    if (x.getTarget() == JMethod.NULL_METHOD)
      return;
    assert classMembers.containsEntry(x.getTarget().getEnclosingType(), x.getTarget());
  }

  @Override
  public void endVisit(JFieldRef x, Context ctx) {
    if (x.getField() == JField.NULL_FIELD)
      return;
    assert classMembers.containsEntry(x.getField().getEnclosingType(), x.getField());
  }
}
