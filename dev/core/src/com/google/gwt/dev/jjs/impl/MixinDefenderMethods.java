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
import com.google.gwt.dev.jjs.ast.JAbstractMethodBody;
import com.google.gwt.dev.jjs.ast.JClassType;
import com.google.gwt.dev.jjs.ast.JDeclaredType;
import com.google.gwt.dev.jjs.ast.JInterfaceType;
import com.google.gwt.dev.jjs.ast.JMethod;
import com.google.gwt.dev.jjs.ast.JMethodBody;
import com.google.gwt.dev.jjs.ast.JMethodCall;
import com.google.gwt.dev.jjs.ast.JModVisitor;
import com.google.gwt.dev.jjs.ast.JParameter;
import com.google.gwt.dev.jjs.ast.JParameterRef;
import com.google.gwt.dev.jjs.ast.JPrimitiveType;
import com.google.gwt.dev.jjs.ast.JProgram;
import com.google.gwt.dev.jjs.ast.JReferenceType;
import com.google.gwt.dev.jjs.ast.JReturnStatement;
import com.google.gwt.dev.jjs.ast.JThisRef;
import com.google.gwt.dev.jjs.ast.JTypeOracle;
import com.google.gwt.dev.jjs.ast.js.JsniMethodBody;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Copy methods from interface types which have method bodies onto classes which inherit them.
 */
public class MixinDefenderMethods {
  private JProgram program;

  public MixinDefenderMethods(JProgram program) {

    this.program = program;
  }

  public static void exec(JProgram program) {
    new MixinDefenderMethods(program).execImpl();
  }

  private static boolean isDefender(JMethod x) {
    JAbstractMethodBody body = x.getBody();
    if (JProgram.isClinit(x)) {
      return false;
    }
    return (body instanceof JMethodBody && !((JMethodBody) body).getStatements().isEmpty()
        || body != null && body.isNative() && ((JsniMethodBody) body).getFunc() != null);
  }

  private void execImpl() {

    List<JMethod> defenderMethods = new ArrayList<JMethod>();
    Map<JMethod, JMethod> virtual2static = new HashMap<JMethod, JMethod>();
    MakeCallsStatic.CreateStaticImplsVisitor staticImplsVisitor =
        new MakeCallsStatic.CreateStaticImplsVisitor(program);
    nextIntf: for (JDeclaredType type : program.getDeclaredTypes()) {
      if (type instanceof JInterfaceType) {
        defenderMethods.clear();

        for (JMethod meth : type.getMethods()) {
          if (isDefender(meth)) {
            defenderMethods.add(meth);
          }
        }

        if (!defenderMethods.isEmpty()) {
          Set<JReferenceType> implementors =
              program.typeOracle.getImplementors((JInterfaceType) type);
          if (implementors == null || implementors.isEmpty()) {
            continue nextIntf;
          }
          for (JMethod dMeth : defenderMethods) {
            staticImplsVisitor.accept(dMeth);
            virtual2static.put(dMeth, program.getStaticImpl(dMeth));
            for (JReferenceType rType : implementors) {
              assert rType instanceof JClassType;
              JClassType cType = (JClassType) rType;
              boolean implemented = false;
              for (JMethod cMeth : cType.getMethods()) {
                if (JTypeOracle.methodsDoMatch(dMeth, cMeth)) {
                  implemented = true;
                }
              }
              if (!implemented) {
                JMethod clone = new JMethod(dMeth.getSourceInfo(), dMeth.getName(),
                    cType, dMeth.getType(), false, false, false,
                    dMeth.getAccess());
                clone.addThrownExceptions(dMeth.getThrownExceptions());
                for (JParameter p : dMeth.getParams()) {
                  clone.addParam(
                      new JParameter(p.getSourceInfo(), p.getName(), p.getType(), p.isFinal(),
                          p.isThis(), clone));
                }
                JMethodBody body = new JMethodBody(dMeth.getSourceInfo());
                JMethodCall delegate = new JMethodCall(dMeth.getSourceInfo(),
                    null, virtual2static.get(dMeth));
                delegate.addArg(new JThisRef(dMeth.getSourceInfo(), cType));
                for (JParameter p : clone.getParams()) {
                  delegate.addArg(new JParameterRef(p.getSourceInfo(), p));
                }
                body.getBlock().addStmt(clone.getType() == JPrimitiveType.VOID ?
                    delegate.makeStatement() :
                    new JReturnStatement(dMeth.getSourceInfo(), delegate));
                clone.setBody(body);
                clone.freezeParamTypes();
                cType.addMethod(clone);
                clone.addOverriddenMethod(dMeth);
              }
            }
          }
        }
      }
    }
    List<JInterfaceType> defendersToRemove = new ArrayList<JInterfaceType>();
    for (JInterfaceType intf : defendersToRemove) {
      new DefenderRemoverVisitor().accept(intf);
    }
  }

  private class DefenderRemoverVisitor extends JModVisitor {

    @Override
    public void endVisit(JMethod x, Context ctx) {
      if (isDefender(x)) {
        x.setBody(new JMethodBody(x.getSourceInfo()));
        x.setAbstract(true);
      }
    }
  }
}
