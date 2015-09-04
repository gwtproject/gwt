/*
 * Copyright 2010 Google Inc.
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
package com.google.gwt.dev.js;

import com.google.gwt.dev.jjs.ast.JConstructor;
import com.google.gwt.dev.jjs.ast.JDeclaredType;
import com.google.gwt.dev.jjs.ast.JInterfaceType;
import com.google.gwt.dev.jjs.ast.JMethod;
import com.google.gwt.dev.jjs.ast.JPrimitiveType;
import com.google.gwt.dev.jjs.ast.JProgram;
import com.google.gwt.dev.jjs.ast.JType;
import com.google.gwt.dev.jjs.impl.JavaToJavaScriptMap;
import com.google.gwt.dev.js.ast.JsBinaryOperation;
import com.google.gwt.dev.js.ast.JsBinaryOperator;
import com.google.gwt.dev.js.ast.JsContext;
import com.google.gwt.dev.js.ast.JsExprStmt;
import com.google.gwt.dev.js.ast.JsFunction;
import com.google.gwt.dev.js.ast.JsModVisitor;
import com.google.gwt.dev.js.ast.JsName;
import com.google.gwt.dev.js.ast.JsNameRef;
import com.google.gwt.dev.js.ast.JsProgram;

/**
 * Adds JsDoc comment node to global JsExprStmt representing method and field
 * declarations.
 */
public class JsClosureJsDocDecorator {
  private static class ClosureJsDocVisitor extends JsModVisitor {

    private JsProgram jsProgram;
    private JavaToJavaScriptMap jjsMap;
    private JProgram jprogram;

    public ClosureJsDocVisitor(JProgram jprogram, JsProgram jsProgram, JavaToJavaScriptMap jjsMap) {
      this.jprogram = jprogram;
      this.jsProgram = jsProgram;
      this.jjsMap = jjsMap;
    }

    @Override
    public void endVisit(JsExprStmt x, JsContext ctx) {
      // look for function declarations
      if (x.getExpression() instanceof JsFunction) {
        JsFunction func = (JsFunction) x.getExpression();
        handleFunction(func.getName(), func, x);
      } else if (x.getExpression() instanceof JsBinaryOperation) {
        // look for a.b = function() expressions
        JsBinaryOperation op = (JsBinaryOperation) x.getExpression();
        if (op.getOperator() == JsBinaryOperator.ASG
            && op.getArg1() instanceof JsNameRef
            && op.getArg2() instanceof JsFunction) {
          handleFunction(((JsNameRef) op.getArg1()).getName(),
              (JsFunction) op.getArg2(), x);
        }
      }
    }

    /**
     * Set a JsDoc declaration on the given func statement
     * @param funcName the name of the function
     * @param func the function assigned to funcName
     * @param stmt the func statement holding the function
     */
    private void handleFunction(JsName funcName, JsFunction func, JsExprStmt stmt) {
      if (funcName == null || funcName.getEnclosing() != jsProgram.getScope()) {
        // skip non-global names or unresolved namerefs
        return;
      }

      StringBuilder typeDecl = new StringBuilder();
      String jsName = getNameAsString(funcName);

      JMethod method = jjsMap.nameToMethod(funcName);
      JDeclaredType cType = jjsMap.nameToClassType(funcName);
      JDeclaredType iType = jjsMap.nameToInterfaceType(funcName);

      if (method instanceof JConstructor) {
        declareReferenceType(method.getEnclosingType(), jsName, typeDecl);
      } else if (cType != null) {
        declareReferenceType(cType, jsName, typeDecl);
      } else if (iType != null) {
        declareReferenceType(iType, jsName, typeDecl);
      }

      if (method != null) {
        for (int i = 0; i < func.getParameters().size(); i++) {
          JType type = method.getParams().get(i).getType();
          JsName paramName = func.getParameters().get(i).getName();
          declareParameter(type, paramName, typeDecl);
        }
        if (method.getType() != JPrimitiveType.VOID) {
          declareReturnType(method.getType(), typeDecl);
        }
      }
      if (typeDecl.length() > 0) {
        stmt.setJsDoc("/**\n" + typeDecl.toString() + "*/\n");
      }
    }

    private void declareReturnType(JType type, StringBuilder typeDecl) {
      typeDecl.append("  * @return {" + getTypeAsString(type) + "}\n");
    }

    private void declareParameter(JType type, JsName paramName, StringBuilder typeDecl) {
      String pName = getNameAsString(paramName);
      typeDecl.append("  * @param {" + getTypeAsString(type) + "} " + pName + "\n");
    }

    private String getTypeAsString(JType type) {
      type = type.getUnderlyingType();

      String typeName = "?";
      if (type instanceof JPrimitiveType) {
        if (type == JPrimitiveType.BOOLEAN) {
          typeName = "boolean";
        } else if (type == JPrimitiveType.DOUBLE || type == JPrimitiveType.FLOAT
            || type == JPrimitiveType.SHORT || type == JPrimitiveType.INT ||
            type == JPrimitiveType.BYTE || type == JPrimitiveType.CHAR) {
          typeName = "number";
        } else if (type == JPrimitiveType.VOID) {
          typeName = "void";
        }
      } else if (type instanceof JDeclaredType) {
        if (type == jprogram.getJavaScriptObject()) {
          typeName = "Object";
        } else if (type == jprogram.getTypeJavaLangString()) {
          typeName = "string";
        } else if (type == jprogram.getTypeJavaLangBoolean()) {
          typeName = "boolean";
        } else if (type == jprogram.getTypeJavaLangDouble()) {
          typeName = "number";
        } else {
          typeName = getNameAsString(jjsMap.nameForType((JDeclaredType) type));
        }
      }
      return typeName;
    }

    private void declareReferenceType(JDeclaredType enclosingType, String jsName,
        StringBuilder typeDecl) {
      if (enclosingType instanceof JInterfaceType) {
        typeDecl.append("  * @interface\n");
        for (JInterfaceType intf : enclosingType.getImplements()) {
          declareExtends(intf, typeDecl);
        }
      } else {
        typeDecl.append("  * @constructor\n");
        declareExtends(enclosingType.getSuperClass(), typeDecl);
        for (JInterfaceType intf : enclosingType.getImplements()) {
          declareImplements(intf, typeDecl);
        }
      }
    }

    private void declareImplements(JInterfaceType intf, StringBuilder typeDecl) {
      JsName typeName = jjsMap.nameForType(intf);
      typeDecl.append("  * @implements {" + getNameAsString(typeName) + "}\n");
    }

    private void declareExtends(JDeclaredType dType, StringBuilder typeDecl) {
      if (dType == null) {
        return;
      }
      JsName typeName = jjsMap.nameForType(dType);
      typeDecl.append("  * @extends {" + getNameAsString(typeName) + "}\n");
    }

    private String getNameAsString(JsName jsName) {
      if (jsName == null) {
        return "?";
      }

      String namespace = jsName.getNamespace() != null ?
          getNameAsString(jsName.getNamespace()) + "." : "";
      return namespace + jsName.getShortIdent();
    }
  }

  public static boolean exec(JProgram jprogram, JsProgram jsProgram, JavaToJavaScriptMap jjsMap) {
    ClosureJsDocVisitor v = new ClosureJsDocVisitor(jprogram, jsProgram, jjsMap);
    v.accept(jsProgram);
    return v.didChange();
  }
}
