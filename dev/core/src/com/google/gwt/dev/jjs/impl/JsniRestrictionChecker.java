/*
 * Copyright 2014 Google Inc.
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

import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.dev.jjs.ast.Context;
import com.google.gwt.dev.jjs.ast.JDeclaredType;
import com.google.gwt.dev.jjs.ast.JInterfaceType;
import com.google.gwt.dev.jjs.ast.JMethod;
import com.google.gwt.dev.jjs.ast.JMethodBody;
import com.google.gwt.dev.jjs.ast.JProgram;
import com.google.gwt.dev.jjs.ast.JVisitor;
import com.google.gwt.dev.jjs.ast.js.JsniMethodBody;
import com.google.gwt.dev.jjs.ast.js.JsniMethodRef;
import com.google.gwt.dev.js.ast.JsContext;
import com.google.gwt.dev.js.ast.JsExpression;
import com.google.gwt.dev.js.ast.JsInvocation;
import com.google.gwt.dev.js.ast.JsModVisitor;
import com.google.gwt.dev.js.ast.JsNameRef;
import com.google.gwt.thirdparty.guava.common.collect.Maps;
import com.google.gwt.thirdparty.guava.common.collect.Sets;

import java.util.Map;
import java.util.Set;

/**
 * Checks and throws errors for invalid JSNI constructs.
 */
public class JsniRestrictionChecker {

  public static void exec(final TreeLogger logger, final JProgram jprogram)
      throws UnableToCompleteException {
    final Set<JDeclaredType> typesRequiringTrampolineDispatch = Sets.newHashSet();
    for (JDeclaredType type : jprogram.getRepresentedAsNativeTypes()) {
      collectAllSuperTypes(type, typesRequiringTrampolineDispatch);
    }

    new AbstractRestrictionChecker() {

      public void checkProgram() throws UnableToCompleteException {
        new JsniRestrictionCheckerVisitor().accept(jprogram);
        boolean hasErrors = reportErrorsAndWarnings(logger);
        if (hasErrors) {
          throw new UnableToCompleteException();
        }
      }

      class JsniRestrictionCheckerVisitor extends JVisitor {

        @Override
        public boolean visit(JMethodBody x, Context ctx) {
          // Skip non jsni methods.
          return false;
        }

        @Override
        public boolean visit(final JsniMethodBody x, Context ctx) {
          final JMethod currentJsniMethod = x.getMethod();
          final Map<String, JsniMethodRef> methodsByJsniReference = Maps.newHashMap();
          for (JsniMethodRef ref : x.getJsniMethodRefs()) {
            methodsByJsniReference.put(ref.getIdent(), ref);
          }
          if (methodsByJsniReference.isEmpty()) {
            return false;
          }

          // Devirtualize method calls;
          new JsModVisitor() {
            @Override
            public boolean visit(JsInvocation x, JsContext ctx) {
              if (!(x.getQualifier() instanceof JsNameRef)) {
                // If the invocation does not have a name as a qualifier (it might be an expression).
                return true;
              }
              JsNameRef ref = (JsNameRef) x.getQualifier();
              if (!ref.isJsniReference()) {
                // The invocation is not to a JSNI method.
                return true;
              }

              // Skip the method JsNameRef but check the qualifier.
              JsExpression methodQualifier = ref.getQualifier();
              if (methodQualifier != null) {
                accept(methodQualifier);
              }

              return false;
            }

            @Override
            public void endVisit(JsNameRef x, JsContext ctx) {
              JsniMethodRef jsniMethodReference = methodsByJsniReference.get(x.getIdent());
              if (jsniMethodReference != null) {
                checkJsniMethodRef(jsniMethodReference);
              }
            }

            public void checkJsniMethodRef(JsniMethodRef x) {
              JMethod method = x.getTarget();
              JDeclaredType enclosingType = method.getEnclosingType();

              if (isNonStaticJsoClassDispatch(method, enclosingType)
                  || isJsoInterface(enclosingType)) {
                logError(x,
                    "Method %s is implemented by a JSO and can only be used in calls "
                        + "within a JSNI method body.",
                    getDescription(method));
              } else if (jprogram.isRepresentedAsNativeJsPrimitive(enclosingType)
                  && !method.isStatic()
                  && !method.isConstructor()) {
                logError(x,
                    "Method %s is implemented by devirtualized type %s JSO and can only be used in "
                        + "calls within a JSNI method body.",
                    getDescription(method),
                    getDescription(enclosingType));
              } else if (typesRequiringTrampolineDispatch.contains(enclosingType)
                  && !method.isStatic()
                  && !method.isConstructor()) {
                logWarning(x, "Unsafe reference to method %s. Instance methods from %s should "
                        + "not be called on Boolean, Double, String, Array or JSO instances "
                    + "from  within a JSNI method body.",
                    getDescription(method),
                    getDescription(enclosingType));
              }
            }
          }.accept(x.getFunc());
          return false;
        }
      }

      private boolean isJsoInterface(JDeclaredType type) {
        return jprogram.typeOracle.isSingleJsoImpl(type)
            || jprogram.typeOracle.isDualJsoInterface(type);
      }
    }.checkProgram();
  }

  private static void collectAllSuperTypes(JDeclaredType type, Set<JDeclaredType> allSuperTypes) {
    if (type.getSuperClass() != null) {
      allSuperTypes.add(type.getSuperClass());
      collectAllSuperTypes(type.getSuperClass(), allSuperTypes);
    }
    for (JInterfaceType interfaceType : type.getImplements()) {
      allSuperTypes.add(interfaceType);
      collectAllSuperTypes(interfaceType, allSuperTypes);
    }
  }

  private static boolean isNonStaticJsoClassDispatch(JMethod method, JDeclaredType enclosingType) {
    return !method.isStatic() && enclosingType.isJsoType();
  }
}
