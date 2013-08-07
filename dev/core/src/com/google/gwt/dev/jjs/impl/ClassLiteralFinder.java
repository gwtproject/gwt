/*
 * Copyright 2013 Google Inc.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package com.google.gwt.dev.jjs.impl;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.TreeLogger.Type;
import com.google.gwt.core.shared.GwtCreatable;
import com.google.gwt.dev.jjs.ast.JAbstractMethodBody;
import com.google.gwt.dev.jjs.ast.JClassLiteral;
import com.google.gwt.dev.jjs.ast.JExpression;
import com.google.gwt.dev.jjs.ast.JFieldRef;
import com.google.gwt.dev.jjs.ast.JLocal;
import com.google.gwt.dev.jjs.ast.JLocalRef;
import com.google.gwt.dev.jjs.ast.JMethod;
import com.google.gwt.dev.jjs.ast.JMethodBody;
import com.google.gwt.dev.jjs.ast.JMethodCall;
import com.google.gwt.dev.jjs.ast.JReturnStatement;
import com.google.gwt.dev.jjs.ast.JStatement;
import com.google.gwt.dev.jjs.ast.js.JsniClassLiteral;
import com.google.gwt.dev.jjs.ast.js.JsniMethodBody;

public class ClassLiteralFinder {

  public static JClassLiteral extractClassLiteral(TreeLogger logger, JExpression inst) {
    return extractImmutableNode(logger, JClassLiteral.class, inst);
  }

  @SuppressWarnings("unchecked")
  private static <X extends JExpression> X extractImmutableNode(TreeLogger logger, Class<X> type,
      JExpression inst) {
    boolean doLog = logger.isLoggable(Type.TRACE);
    if (inst == null) {
      return null;
    } else if (type.isAssignableFrom(inst.getClass())) {
      // We have a winner!
      return (X) inst;
    } else if (inst instanceof JLocalRef) {
      JLocal local = ((JLocalRef) inst).getLocal();
      if (local.isFinal()) {
        JExpression localInit = local.getInitializer();
        if (localInit == null) {
          inst = localInit;
        } else {
          return extractImmutableNode(logger, type, localInit);
        }
      } else {
        if (doLog)
          logNonFinalError(logger, inst);
      }
    } else if (inst instanceof JFieldRef) {
      com.google.gwt.dev.jjs.ast.JField field = ((JFieldRef) inst).getField();
      if (field.isFinal()) {
        return extractImmutableNode(logger, type, field.getInitializer());
      } else {
        if (doLog)
          logNonFinalError(logger, inst);
      }
    } else if (inst instanceof JMethodCall) {
      JMethodCall call = (JMethodCall) inst;
      JMethod target = (call).getTarget();
      if (target.isExternal()) {
        if (doLog)
          logger.log(Type.TRACE, "Unable to navigate through external method " + target.toSource()
              + " while searching for a " + type.getName());
        return null;
      }
      JAbstractMethodBody method = target.getBody();
      if (method.isNative()) {
        // The only native method we will consider is a generated GwtCreatable.
        if (target.getEnclosingType().getSuperClass().getName()
            .equals(GwtCreatable.class.getName())) {
          JsniMethodBody jsni = (JsniMethodBody) method;
          if (JClassLiteral.class.isAssignableFrom(type)) {
            List<JsniClassLiteral> literals = jsni.getClassRefs();
            if (literals.size() == 1) {
              return (X) literals.get(0);
            }
          }
        }
      } else {
        JMethodBody java = (JMethodBody) method;
        ArrayList<JReturnStatement> returns = new ArrayList<JReturnStatement>();
        for (JStatement statement : java.getStatements()) {
          if (statement instanceof JReturnStatement)
            returns.add((JReturnStatement) statement);
        }
        if (returns.size() == 1) {
          return extractImmutableNode(logger, type, returns.get(0).getExpr());
        } else {
          if (logger.isLoggable(Type.TRACE)) {
            logger
                .log(Type.TRACE, "Java " + type.getName() + " provider method must have one "
                    + "and only one return statement, which returns a " + type.getName() + " "
                    + method);
          }
        }
      }
    } else {
      logger.log(Type.WARN, "Encountered unhandled type while searching for " + type.getName()
          + ": " + debug(inst));
    }
    logger.log(Type.TRACE, "Unable to acquire a " + type.getCanonicalName() + " from "
        + debug(inst));
    return null;
  }

  private static String debug(JExpression inst) {
    return inst.getClass() + " [" + inst.toSource() + "] @" + inst.getSourceInfo();
  }

  private static void logNonFinalError(TreeLogger logger, JExpression inst) {
    logger.log(Type.TRACE, "Traced class literal down to a " + debug(inst) + ","
        + " but this member was not marked final."
        + " Aborting class literal search due to lack of determinism.");
  }

}
