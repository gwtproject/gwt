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
import com.google.gwt.dev.jjs.ast.JMethod;
import com.google.gwt.dev.jjs.ast.JMethodCall;
import com.google.gwt.dev.jjs.ast.JModVisitor;
import com.google.gwt.dev.jjs.ast.JProgram;

/**
 * Java8 default methods are implemented by creating a forwarding method on each class that
 * inherits the implementation. For a concrete type C inheriting I.m(), there will be an
 * implementation <code>C.m() { return I.super.m(); }</code>.
 *
 * References to I.super.m() are replaced by creating a static version of this method on the
 * interface, and then delegating to it instead.
 */
public class ReplaceDefaultMethodForwardingByStaticImplementation {

  public static void exec(final JProgram program) {
    final MakeCallsStatic.CreateStaticImplsVisitor staticImplCreator =
        new MakeCallsStatic.CreateStaticImplsVisitor(program);
    // 1. create the static implementations.
    // NOTE: the process needs to be done in two steps because the creation of static implementation
    // might introduce call sites in types that were already processed and hence would not be
    // rewritten (bug #9453).
    new JModVisitor() {
      @Override
      public boolean visit(JMethod x, Context ctx) {
        if (x.isDefaultMethod()) {
          staticImplCreator.getOrCreateStaticImpl(program, x);
        }
        return false;
      }
    }.accept(program);

    // 2. Rewrite call sites.
    new JModVisitor() {
      @Override
      public void endVisit(JMethodCall x, Context ctx) {
        JMethod targetMethod = x.getTarget();
        if (targetMethod.isDefaultMethod() && x.isStaticDispatchOnly()) {
          assert x.getInstance() != null;

          JMethod staticMethod = program.getStaticImpl(targetMethod);
          // Cannot use setStaticDispatchOnly() here because interfaces don't have prototypes
          JMethodCall callStaticMethod = new JMethodCall(x.getSourceInfo(), null, staticMethod);
          // add 'this' as first parameter
          callStaticMethod.addArg(x.getInstance());
          callStaticMethod.addArgs(x.getArgs());
          ctx.replaceMe(callStaticMethod);
        }
      }
    }.accept(program);
  }
}
