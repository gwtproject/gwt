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
 * Remove all boxing/unboxing code.
 *
 * Convert to [BoxedType].valueOf(x) to 'x'
 * Convert x.[primType]Value() calls to 'x'
 * Convert x.[BoxedType.value] field refs to 'x'
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
      // undoUnbox is used by other passes, and can convert x.[BoxedType]Value() calls
      // but we don't want longs to participate
      if (replacement.getType() != JPrimitiveType.LONG) {
        ctx.replaceMe(replacement);
      }
    }
  }

  @Override
  public void endVisit(JFieldRef x, Context ctx) {
    if (x.getInstance() != null  &&
        // can x.[BoxedType.value] field access to 'x'
        program.canBeUnboxedType(x.getField().getEnclosingType()) &&
        x.getField().getName().equals("value")) {
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
