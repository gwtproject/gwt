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
import com.google.gwt.dev.jjs.ast.JExpression;
import com.google.gwt.dev.jjs.ast.JFieldRef;
import com.google.gwt.dev.jjs.ast.JMethod;
import com.google.gwt.dev.jjs.ast.JMethodCall;
import com.google.gwt.dev.jjs.ast.JModVisitor;
import com.google.gwt.dev.jjs.ast.JPrimitiveType;
import com.google.gwt.dev.jjs.ast.JProgram;
import com.google.gwt.dev.jjs.ast.JReferenceType;
import com.google.gwt.dev.util.log.speedtracer.CompilerEventType;
import com.google.gwt.dev.util.log.speedtracer.SpeedTracerLogger;
import com.google.gwt.dev.util.log.speedtracer.SpeedTracerLogger.Event;

/**
 * Remove all method specializations before final pruning pass.
 */
public class RemoveAutoboxing extends JModVisitor {

  private final AutoboxUtils autoboxUtils;
  private JProgram program;

  public RemoveAutoboxing(JProgram program) {
    this.program = program;
    this.autoboxUtils = new AutoboxUtils(program);
  }

  public void endVisit(JMethodCall x, Context ctx) {
    JExpression replacement = autoboxUtils.undoUnbox(x);
    if (replacement == null) {
      replacement = autoboxUtils.undoBox(x);
    }

    if (replacement !=null) {
      if (replacement.getType() != JPrimitiveType.LONG ||
          replacement.getType() != JPrimitiveType.BOOLEAN) {
        ctx.replaceMe(replacement);
      }
    }
  }

  @Override
  public void endVisit(JFieldRef x, Context ctx) {
    if (x.getInstance() != null  &&
        program.typeOracle.isSuperClass((JReferenceType) x.getInstance().getType(),
        program.getTypeJavaLangNumber()) && x.getField().getName().equals("value")) {
       ctx.replaceMe(x.getInstance());
    }
  }

  private static final String NAME = RemoveAutoboxing.class.getSimpleName();

  private OptimizerStats execImpl() {
    OptimizerStats stats = new OptimizerStats(NAME);
    accept(program);
    return stats;
  }

  public static OptimizerStats exec(JProgram program) {
    Event optimizeEvent = SpeedTracerLogger
        .start(CompilerEventType.OPTIMIZE, "optimizer", NAME);
    OptimizerStats stats = new RemoveAutoboxing(program).execImpl();
    optimizeEvent.end("didChange", "" + stats.didChange());
    return stats;
  }
}
