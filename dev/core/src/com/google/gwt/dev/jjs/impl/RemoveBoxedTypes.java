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

import com.google.gwt.dev.jjs.InternalCompilerException;
import com.google.gwt.dev.jjs.ast.Context;
import com.google.gwt.dev.jjs.ast.JConstructor;
import com.google.gwt.dev.jjs.ast.JExpression;
import com.google.gwt.dev.jjs.ast.JFieldRef;
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
 * Remove all boxing/unboxing code and usage of boxed types.
 *
 * Convert to [BoxedType].valueOf(x) to 'x'
 * Convert x.[primType]Value() calls to 'x'
 * Convert x.[BoxedType.value] field refs to 'x'
 *
 * Flag "new [BoxedType](String | number)" as compile time error
 * TODO(cromwellian): replace JNewInstance calls with compile time handling
 */
public class RemoveBoxedTypes extends JModVisitor {

  private final AutoboxUtils autoboxUtils;
  private JProgram program;

  public RemoveBoxedTypes(JProgram program) {
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
  public void endVisit(JNewInstance x, Context ctx) {
    if (program.canBeUnboxedType(x.getTarget().getEnclosingType())) {
      JConstructor ctor = x.getTarget();
      JParameter firstArg = ctor.getParams().get(0);
      // new [BoxedType](number) -> number
      if (firstArg.getType().getUnderlyingType() instanceof JPrimitiveType) {
        ctx.replaceMe(x.getArgs().get(0));
        return;
      } else if (firstArg.getType().getUnderlyingType() == program.getTypeJavaLangString()) {
        // new [BoxedType](String) -> [BoxedType].parse[primType](string)
        ctx.replaceMe(autoboxUtils.convertStringCtorCallToParseCall(x));
        return;
      }
      throw new InternalCompilerException("Unknown or unhandled constructor call " + x);
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

  private static final String NAME = RemoveBoxedTypes.class.getSimpleName();

  private OptimizerStats execImpl() {
    OptimizerStats stats = new OptimizerStats(NAME);
    if (program.isAutoboxingDisabled()) {
      accept(program);
    }
    return stats;
  }

  public static OptimizerStats exec(JProgram program) {
    Event optimizeEvent = SpeedTracerLogger
        .start(CompilerEventType.OPTIMIZE, "optimizer", NAME);
    OptimizerStats stats = new RemoveBoxedTypes(program).execImpl();
    optimizeEvent.end("didChange", "" + stats.didChange());
    return stats;
  }
}
