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
import com.google.gwt.dev.jjs.ast.JMethod;
import com.google.gwt.dev.jjs.ast.JMethodBody;
import com.google.gwt.dev.jjs.ast.JMethodCall;
import com.google.gwt.dev.jjs.ast.JModVisitor;
import com.google.gwt.dev.jjs.ast.JNewInstance;
import com.google.gwt.dev.jjs.ast.JParameter;
import com.google.gwt.dev.jjs.ast.JParameterRef;
import com.google.gwt.dev.jjs.ast.JPrimitiveType;
import com.google.gwt.dev.jjs.ast.JProgram;
import com.google.gwt.dev.jjs.ast.JReturnStatement;
import com.google.gwt.dev.jjs.ast.JThisRef;
import com.google.gwt.dev.util.log.speedtracer.CompilerEventType;
import com.google.gwt.dev.util.log.speedtracer.SpeedTracerLogger;
import com.google.gwt.dev.util.log.speedtracer.SpeedTracerLogger.Event;

/**
 * Remove boxed type construction and update unbox methods.
 *
 * Convert new [BoxedType]() calls to BoxedNumericTypeHelper.create[BoxedType]()
 * Convert x.[primType]Value() method bodies to call BoxedNumericTypeHelper.$[primType]Value()
 *
 */
public class RemoveBoxedTypes extends JModVisitor {

  private final AutoboxUtils autoboxUtils;
  private JProgram program;

  public RemoveBoxedTypes(JProgram program) {
    this.program = program;
    this.autoboxUtils = new AutoboxUtils(program);
  }

  @Override
  public void endVisit(JNewInstance x, Context ctx) {
    if (program.canBeUnboxedType(x.getTarget().getEnclosingType())) {
      JConstructor ctor = x.getTarget();
      JParameter firstArg = ctor.getParams().get(0);
      // new [BoxedType](number) -> [BoxedNumericTypeHelper].$create[BoxedType](number)
      if (firstArg.getType().getUnderlyingType() instanceof JPrimitiveType) {
        ctx.replaceMe(autoboxUtils.convertPrimitiveCtorCallToCreateCall(x));
        return;
      } else if (firstArg.getType().getUnderlyingType() == program.getTypeJavaLangString()) {
        // new [BoxedType](String) -> [BoxedNumericTypeHelper].parse[primType](string)
        ctx.replaceMe(autoboxUtils.convertStringCtorCallToCreateCall(x));
        return;
      }
      throw new InternalCompilerException("Unknown or unhandled constructor call " + x);
    }
  }

  // rewrite Number.[primType]Value() methods to delegate to hand-written static
  // version on BoxedNumericTypeHelper.$[primType]Value() if it exists
  public void endVisit(JMethod x, Context ctx) {
    if (program.canBeUnboxedType(x.getEnclosingType())) {
      for (JMethod overriddenMethod  : x.getOverriddenMethods()) {
        if (overriddenMethod.getEnclosingType() == program.getTypeJavaLangNumber() ||
            overriddenMethod.getEnclosingType() == program.getTypeJavaLangObject() ||
            program.getTypeJavaLangString().getImplements().contains(
                overriddenMethod.getEnclosingType())) {
          JMethod staticMethod = program.getIndexedMethod("BoxedNumericTypeHelper.$"
              + x.getName());
          if (staticMethod != null) {
            // replace body with return BoxedNumericTypeHelper.$method(this[,args])
            JMethodCall call = new JMethodCall(x.getSourceInfo(), null, staticMethod);
            call.addArg(new JThisRef(x.getSourceInfo(), x.getEnclosingType()));
            for (JParameter p : x.getParams()) {
              call.addArg(new JParameterRef(p.getSourceInfo(), p));
            }
            JMethodBody body = new JMethodBody(x.getSourceInfo());
            body.getBlock().addStmt(new JReturnStatement(x.getSourceInfo(), call));
            x.setBody(body);
          }
          break;
        }
      }
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
