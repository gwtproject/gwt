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
import com.google.gwt.dev.jjs.ast.JField;
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

    static class JsDocBuilder {
      private StringBuilder sb = new StringBuilder();
      private JProgram jprogram;
      private JavaToJavaScriptMap jjsMap;

      public JsDocBuilder(JProgram jprogram, JavaToJavaScriptMap jjsMap) {
        this.jprogram = jprogram;
        this.jjsMap = jjsMap;
      }

      public void build(JsExprStmt stmt) {
        if (sb.length() > 0) {
          stmt.setJsDoc("/**\n" + sb.toString() + "  */\n");
        }
      }

      public JsDocBuilder typedef(String typeName) {
        sb.append("  * @typedef {" + typeName + "}\n");
        return this;
      }

      public JsDocBuilder typedef(JDeclaredType type) {
        return typedef(getNameAsString(jjsMap.nameForType(type)));
      }

      public JsDocBuilder override() {
        sb.append("  * @override\n");
        return this;
      }

      public JsDocBuilder declareReturn(JType returnType) {
        sb.append("  * @return {" + getTypeAsString(returnType) + "}\n");
        return this;
      }

      public JsDocBuilder declareParameter(JType paramType, JsName paramName) {
        String pName = getNameAsString(paramName);
        sb.append("  * @param {" + getTypeAsString(paramType) + "} " + pName + "\n");
        return this;
      }

      public JsDocBuilder declareImplements(JInterfaceType intf) {
        JsName typeName = jjsMap.nameForType(intf);
        sb.append("  * @implements {" + getNameAsString(typeName) + "}\n");
        return this;
      }

      public JsDocBuilder declareExtends(JDeclaredType dType) {
        if (dType == null) {
          return this;
        }
        JsName typeName = jjsMap.nameForType(dType);
        sb.append("  * @extends {" + getNameAsString(typeName) + "}\n");
        return this;
      }

      public JsDocBuilder declareInterface() {
        sb.append("  * @interface\n");
        return this;
      }

      public JsDocBuilder declareConstructor() {
        sb.append("  * @constructor\n");
        return this;
      }

      public JsDocBuilder recordType(JType type) {
        sb.append("  * @type {" + getTypeAsString(type) + "}\n");
        return this;
      }

      private String getNameAsString(JsName jsName) {
        if (jsName == null) {
          return "?";
        }

        String namespace = jsName.getNamespace() != null ?
            getNameAsString(jsName.getNamespace()) + "." : "";
        return namespace + jsName.getShortIdent();
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
    }

    public ClosureJsDocVisitor(JProgram jprogram, JsProgram jsProgram, JavaToJavaScriptMap jjsMap) {
      this.jprogram = jprogram;
      this.jsProgram = jsProgram;
      this.jjsMap = jjsMap;
    }

    @Override
    public void endVisit(JsExprStmt x, JsContext ctx) {
      JsDocBuilder builder = new JsDocBuilder(jprogram, jjsMap);

      if (jjsMap.entityForExportStatement(x) != null) {
        handleExport(jjsMap.entityForExportStatement(x), builder);
      } else if (x.getExpression() instanceof JsFunction) {
        // look for function declarations
        JsFunction func = (JsFunction) x.getExpression();
        handleFunction(func,  builder);
      } else if (x.getExpression() instanceof JsBinaryOperation) {
        // look for a.b = function globalname() expressions
        JsBinaryOperation op = (JsBinaryOperation) x.getExpression();
        if (op.getOperator() == JsBinaryOperator.ASG) {
          if (op.getArg1() instanceof JsNameRef) {
            JsNameRef lhsRef = (JsNameRef) op.getArg1();
            if (op.getArg2() instanceof JsFunction) {
              handleFunction((JsFunction) op.getArg2(), builder);
            } else if (jjsMap.nameToField(lhsRef.getName()) != null && isGlobalStatement(x)) {
              // global field declaration (prototype or static)
              handleField(jjsMap.nameToField(lhsRef.getName()), builder);
            }
          }
        }
      }
      builder.build(x);
    }

    private void handleField(JField field, JsDocBuilder builder) {
      builder.recordType(field.getType());
    }

    private boolean isGlobalStatement(JsExprStmt x) {
      for (int i = 0; i < jsProgram.getFragmentCount(); i++) {
        if (jsProgram.getFragmentBlock(i).getStatements().contains(x)) {
          return true;
        }
      }
      return false;
    }

    private void handleExport(Object entity, JsDocBuilder builder) {
      if (entity instanceof JDeclaredType) {
        handleTypedef((JDeclaredType) entity, builder);
      }
    }

    private void handleTypedef(JDeclaredType type, JsDocBuilder builder) {
      builder.typedef(type);
    }

    /**
     * Set a JsDoc declaration on the given func statement
     * @param func the function
     * @param builder
     */
    private void handleFunction(JsFunction func, JsDocBuilder builder) {
      if (func.getName() == null) {
        return;
      }

      JsName funcName = func.getName();

      JMethod method = jjsMap.nameToMethod(funcName);
      JDeclaredType cType = jjsMap.nameToClassType(funcName);
      JDeclaredType iType = jjsMap.nameToInterfaceType(funcName);

      if (method instanceof JConstructor) {
        declareReferenceType(method.getEnclosingType(), builder);
      } else if (cType != null) {
        declareReferenceType(cType, builder);
      } else if (iType != null) {
        declareReferenceType(iType, builder);
      }

      if (method != null) {
        if (!method.getOverriddenMethods().isEmpty()) {
          builder.override();
        }
        for (int i = 0; i < func.getParameters().size(); i++) {
          JType type = method.getParams().get(i).getType();
          JsName paramName = func.getParameters().get(i).getName();
          builder.declareParameter(type, paramName);
        }
        if (method.getType() != JPrimitiveType.VOID) {
          builder.declareReturn(method.getType());
        }
      }

    }

    private void declareReferenceType(JDeclaredType enclosingType, JsDocBuilder builder) {
      if (enclosingType instanceof JInterfaceType) {
        builder.declareInterface();
        for (JInterfaceType intf : enclosingType.getImplements()) {
          builder.declareExtends(intf);
        }
      } else {
        builder.declareConstructor();
        builder.declareExtends(enclosingType.getSuperClass());
        for (JInterfaceType intf : enclosingType.getImplements()) {
          builder.declareImplements(intf);
        }
      }
    }
  }

  public static boolean exec(JProgram jprogram, JsProgram jsProgram, JavaToJavaScriptMap jjsMap) {
    ClosureJsDocVisitor v = new ClosureJsDocVisitor(jprogram, jsProgram, jjsMap);
    v.accept(jsProgram);
    return v.didChange();
  }
}
