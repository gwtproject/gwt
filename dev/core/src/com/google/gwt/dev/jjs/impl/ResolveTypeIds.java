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
import com.google.gwt.dev.jjs.ast.JCastMap;
import com.google.gwt.dev.jjs.ast.JIntLiteral;
import com.google.gwt.dev.jjs.ast.JLiteral;
import com.google.gwt.dev.jjs.ast.JModVisitor;
import com.google.gwt.dev.jjs.ast.JProgram;
import com.google.gwt.dev.jjs.ast.JReferenceType;
import com.google.gwt.dev.jjs.ast.JType;
import com.google.gwt.dev.jjs.ast.JTypeIdOf;
import com.google.gwt.dev.jjs.ast.JVisitor;
import com.google.gwt.thirdparty.guava.common.collect.Maps;
import com.google.gwt.thirdparty.guava.common.collect.Sets;

import java.util.Map;
import java.util.Set;

/**
 * Replaces JTypeIdOf nodes with the appropriate literal.
 */
public class ResolveTypeIds {
  // TODO(rluble): this pass should insert the defineSeed in Java.


  /**
   * Collects all types that need an Id at runtime.
   */
  private class JTypeIdOfCollectorVisitor extends JVisitor {

    private final Set<JReferenceType> typesReferencedByID = Sets.newHashSet();

    @Override
    public void endVisit(JTypeIdOf x, Context ctx) {
      typesReferencedByID.add(x.getType());
    }

    public void endVisit(JReferenceType x, Context ctx) {
      // All reference types retained will need an id.
      typesReferencedByID.add(x);
    }
  }

  /**
   * Replaces JTypeIdOf nodes with the corresponding JLiteral
   */
  private class ReplaceJTypeIdOfVisitor extends JModVisitor {
    @Override
    public void endVisit(JTypeIdOf x, Context ctx) {
      ctx.replaceMe(typeIdsByType.get(x.getType()));
    }
  }

  public static Map<JType, JLiteral>  exec(JProgram program) {
    return new ResolveTypeIds(program).execImpl();
  }

  private final JProgram program;

  private final Map<JType, JLiteral> typeIdsByType = Maps.newIdentityHashMap();

  private int nextFreeId = -1;

  private ResolveTypeIds(JProgram program) {
    this.program = program;
  }

  private void assignNextId(JType type) {
    if (typeIdsByType.containsKey(type)) {
      return;
    }
    int id = nextFreeId++;
    assert (id != -1 || type == program.getJavaScriptObject());
    assert (id != 0 || type == program.getTypeJavaLangObject());
    assert (id != 1 || type == program.getTypeJavaLangString());

    typeIdsByType.put(type, JIntLiteral.get(id));
  }

  private Map<JType, JLiteral> execImpl() {
    JTypeIdOfCollectorVisitor visitor = new JTypeIdOfCollectorVisitor();
    visitor.accept(program);

    // TODO(rluble): remove the need for special ids
    // JavaScriptObject should get -1
    assignNextId(program.getJavaScriptObject());
    // java.lang.Object should get 0
    assignNextId(program.getTypeJavaLangObject());
    // java.lang.String should get 1
    assignNextId(program.getTypeJavaLangString());

    // Should we sort them?
    for (JType type : visitor.typesReferencedByID) {
      assignNextId(type);
    }

    ReplaceJTypeIdOfVisitor replaceTypeIdsVisitor = new ReplaceJTypeIdOfVisitor();
    replaceTypeIdsVisitor.accept(program);
    // Fix stored cast maps.
    // TODO(rluble): Improve the code so that things are not scattered all over.
    for (JCastMap castMap : program.getCastMaps()) {
      replaceTypeIdsVisitor.accept(castMap);
    }

    return typeIdsByType;
  }
}
