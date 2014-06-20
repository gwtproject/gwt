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
import com.google.gwt.dev.jjs.ast.JConstructor;
import com.google.gwt.dev.jjs.ast.JMethod;
import com.google.gwt.dev.jjs.ast.JMethodCall;
import com.google.gwt.dev.jjs.ast.JModVisitor;
import com.google.gwt.dev.jjs.ast.JNewInstance;
import com.google.gwt.dev.jjs.ast.JParameter;
import com.google.gwt.dev.jjs.ast.JPrimitiveType;
import com.google.gwt.dev.jjs.ast.JProgram;
import com.google.gwt.dev.util.log.speedtracer.CompilerEventType;
import com.google.gwt.dev.util.log.speedtracer.SpeedTracerLogger;
import com.google.gwt.dev.util.log.speedtracer.SpeedTracerLogger.Event;

/**
 * Rewrite instantiations of Boolean and Double to use static helper methods which return unboxed
 * versions.
 *
 */
public class RewriteBooleanAndDoubleBoxedInstantiations extends JModVisitor {

  private JProgram program;
  private JMethod createBooleanFromStringMethod;
  private JMethod createBooleanMethod;
  private JMethod createDoubleFromStringMethod;
  private JMethod createDoubleMethod;

  public RewriteBooleanAndDoubleBoxedInstantiations(JProgram program) {
    this.program = program;
    createBooleanFromStringMethod = program.getIndexedMethod("Boolean.$createBooleanFromString");
    createBooleanMethod = program.getIndexedMethod("Boolean.$createBoolean");
    createDoubleFromStringMethod = program.getIndexedMethod("Double.$createDoubleFromString");
    createDoubleMethod = program.getIndexedMethod("Double.$createDouble");
  }

  @Override
  public void endVisit(JNewInstance x, Context ctx) {
    JMethod createMethod = null;
    JConstructor ctor = x.getTarget();

    if (ctor.getEnclosingType() == program.getTypeJavaLangBoolean()) {
      JParameter firstArg = ctor.getParams().get(0);

      if (firstArg.getType().getUnderlyingType() == program.getTypeJavaLangString()) {
        createMethod = createBooleanFromStringMethod;
      } else {
        assert firstArg.getType().getUnderlyingType() == JPrimitiveType.BOOLEAN;
        createMethod = createBooleanMethod;
      }
    } else if (ctor.getEnclosingType() == program.getTypeJavaLangDouble()) {
      JParameter firstArg = ctor.getParams().get(0);
      if (firstArg.getType().getUnderlyingType() == program.getTypeJavaLangString()) {
        createMethod = createDoubleFromStringMethod;
      } else {
        assert firstArg.getType().getUnderlyingType() == JPrimitiveType.DOUBLE;
        createMethod = createDoubleMethod;
      }
    }

    if (createMethod != null) {
      ctx.replaceMe(new JMethodCall(x.getSourceInfo(), null, createMethod, x.getArgs().get(0)));
    }
  }

  private static final String NAME = RewriteBooleanAndDoubleBoxedInstantiations.class
      .getSimpleName();

  private OptimizerStats execImpl() {
    OptimizerStats stats = new OptimizerStats(NAME);
    accept(program);
    return stats;
  }

  public static OptimizerStats exec(JProgram program) {
    Event optimizeEvent = SpeedTracerLogger
        .start(CompilerEventType.OPTIMIZE, "optimizer", NAME);
    OptimizerStats stats = new RewriteBooleanAndDoubleBoxedInstantiations(program).execImpl();
    optimizeEvent.end("didChange", "" + stats.didChange());
    return stats;
  }
}
