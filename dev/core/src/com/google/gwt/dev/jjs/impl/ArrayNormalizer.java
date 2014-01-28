/*
 * Copyright 2008 Google Inc.
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

import com.google.gwt.dev.jjs.SourceInfo;
import com.google.gwt.dev.jjs.ast.Context;
import com.google.gwt.dev.jjs.ast.JAbsentArrayDimension;
import com.google.gwt.dev.jjs.ast.JArrayRef;
import com.google.gwt.dev.jjs.ast.JArrayType;
import com.google.gwt.dev.jjs.ast.JBinaryOperation;
import com.google.gwt.dev.jjs.ast.JBinaryOperator;
import com.google.gwt.dev.jjs.ast.JBooleanLiteral;
import com.google.gwt.dev.jjs.ast.JCastMap;
import com.google.gwt.dev.jjs.ast.JExpression;
import com.google.gwt.dev.jjs.ast.JIntLiteral;
import com.google.gwt.dev.jjs.ast.JLiteral;
import com.google.gwt.dev.jjs.ast.JMethod;
import com.google.gwt.dev.jjs.ast.JMethodCall;
import com.google.gwt.dev.jjs.ast.JModVisitor;
import com.google.gwt.dev.jjs.ast.JNewArray;
import com.google.gwt.dev.jjs.ast.JNullType;
import com.google.gwt.dev.jjs.ast.JPrimitiveType;
import com.google.gwt.dev.jjs.ast.JProgram;
import com.google.gwt.dev.jjs.ast.JReferenceType;
import com.google.gwt.dev.jjs.ast.JRuntimeTypeReference;
import com.google.gwt.dev.jjs.ast.JType;
import com.google.gwt.dev.jjs.ast.js.JsonArray;

import java.util.Collections;

/**
 * Replace array accesses and instantiations with calls to the Array class.
 * Depends on {@link CompoundAssignmentNormalizer} and {@link CastNormalizer}
 * having already run.
 */
public class ArrayNormalizer {

  private class ArrayVisitor extends JModVisitor {

    @Override
    public void endVisit(JBinaryOperation x, Context ctx) {
      if (disableCastChecking || x.getOp() != JBinaryOperator.ASG ||
          !( x.getLhs() instanceof JArrayRef)) {
        return;
      }
      JArrayRef arrayRef = (JArrayRef) x.getLhs();
      JType elementType = arrayRef.getType();
      if (elementType instanceof JNullType || !(elementType instanceof JReferenceType)) {
        // JNullType will generate a null pointer exception instead,
        // Primitive array types are statically correct, no need to set check.
        return;
      }

      /*
       * See if we need to do a checked store. Effectively final are statically correct.
       */
      if (!(elementType).isFinal() || !program.typeOracle.canTriviallyCast(
          (JReferenceType) x.getRhs().getType(), (JReferenceType) elementType)) {
        // replace this assignment with a call to setCheck()
        JMethodCall call = new JMethodCall(x.getSourceInfo(), null, setCheckMethod);
        call.addArgs(arrayRef.getInstance(), arrayRef.getIndexExpr(), x.getRhs());
        ctx.replaceMe(call);
      }
    }

    @Override
    public void endVisit(JNewArray x, Context ctx) {
      JArrayType type = x.getArrayType();

      if (x.initializers != null) {
        processInitializers(x, ctx, type);
      } else {
        int realDims = 0;
        for (JExpression dim : x.dims) {
          if (dim instanceof JAbsentArrayDimension) {
            break;
          }
          ++realDims;
        }
        assert (realDims >= 1);
        if (realDims == 1) {
          processDim(x, ctx, type);
        } else {
          processDims(x, ctx, type, realDims);
        }
      }
    }

    private boolean canLeafElementTypeBeJSO(JArrayType arrayType) {
      JType elementType = arrayType.getElementType();
      if (elementType instanceof JArrayType) {
        return canLeafElementTypeBeJSO((JArrayType) elementType);
      }
      if (!(elementType instanceof JReferenceType)) {
        return false;
      }

      JReferenceType elementRefType = (JReferenceType) elementType;
      if (program.typeOracle.isEffectivelyJavaScriptObject(elementRefType) ||
          program.typeOracle.isDualJsoInterface(elementRefType)) {
        return true;
      }
      return false;
    }

    private JRuntimeTypeReference getRuntimeTypeReference(SourceInfo sourceInfo,
        JArrayType arrayType) {
      JType elementType = arrayType.getElementType();
      if (!(elementType instanceof JReferenceType)) {
        // elementType is a primitive type, store check will be performed statically.
        elementType = JNullType.INSTANCE;
      }

      if (program.typeOracle.isEffectivelyJavaScriptObject(elementType)) {
        /*
         * treat types that are effectively JSO's as JSO's, for the purpose of
         * castability checking
         */
        elementType = program.getJavaScriptObject();
      } else {
        elementType = ((JReferenceType) elementType).getUnderlyingType();
      }
      return new JRuntimeTypeReference(sourceInfo, program.getTypeJavaLangObject(),
          (JReferenceType)  elementType);
    }

    private JExpression getOrCreateCastMap(SourceInfo sourceInfo, JArrayType arrayType) {
      JCastMap castableTypeMap = program.getCastMap(arrayType);
      if (castableTypeMap == null) {
        return new JCastMap(sourceInfo, program.getTypeJavaLangObject(),
            Collections.<JReferenceType>emptyList());
      }
      return castableTypeMap;
    }

    /**
     * @see com.google.gwt.lang.Array regarding initial value types types
     */
    private JIntLiteral getInitValueTypeFor(JType type) {
      if (type instanceof JPrimitiveType) {
        if (type == program.getTypePrimitiveLong()) {
          // The long type, thus 0L (index 3)
          return program.getLiteralInt(3);
        } else if (type == program.getTypePrimitiveBoolean()) {
          // The boolean type, thus false (index 2)
          return program.getLiteralInt(2);
        } else {
          // A numeric type, thus zero (index 1).
          return program.getLiteralInt(1);
        }
      }
      // An Object type, thus null (index 0).
      return program.getLiteralInt(0);
    }

    private void processDim(JNewArray x, Context ctx, JArrayType arrayType) {
      // override the type of the called method with the array's type
      SourceInfo sourceInfo = x.getSourceInfo();
      JMethodCall call = new JMethodCall(sourceInfo, null, initDim, arrayType);
      JLiteral classLit = x.getClassLiteral();
      JExpression castableTypeMap = getOrCreateCastMap(sourceInfo, arrayType);
      JRuntimeTypeReference elementTypeId = getRuntimeTypeReference(sourceInfo, arrayType);
      JBooleanLiteral canLeafElementBeJSOLiteral =
          JBooleanLiteral.get(canLeafElementTypeBeJSO(arrayType));
      JExpression dim = x.dims.get(0);
      JType elementType = arrayType.getElementType();
      call.addArgs(classLit, castableTypeMap, elementTypeId, dim, canLeafElementBeJSOLiteral,
          getInitValueTypeFor(elementType));
      ctx.replaceMe(call);
    }

    private void processDims(JNewArray x, Context ctx, JArrayType arrayType, int dims) {
      // override the type of the called method with the array's type
      SourceInfo sourceInfo = x.getSourceInfo();
      JMethodCall call = new JMethodCall(sourceInfo, null, initDims, arrayType);
      JsonArray classLitList = new JsonArray(sourceInfo, program.getJavaScriptObject());
      JsonArray castableTypeMapList = new JsonArray(sourceInfo, program.getJavaScriptObject());
      JsonArray elementTypeIdList = new JsonArray(sourceInfo, program.getJavaScriptObject());
      JsonArray dimList = new JsonArray(sourceInfo, program.getJavaScriptObject());
      JBooleanLiteral canLeafElementBeJSOLiteral =
          JBooleanLiteral.get(canLeafElementTypeBeJSO(arrayType));
      JType cur = arrayType;
      for (int i = 0; i < dims; ++i) {
        // Walk down each type from most dims to least.
        JArrayType curArrayType = (JArrayType) cur;

        JLiteral classLit = x.getClassLiterals().get(i);
        classLitList.getExprs().add(classLit);

        JExpression castableTypeMap = getOrCreateCastMap(sourceInfo, curArrayType);
        castableTypeMapList.getExprs().add(castableTypeMap);

        JRuntimeTypeReference elementTypeIdLit = getRuntimeTypeReference(sourceInfo, curArrayType);
        elementTypeIdList.getExprs().add(elementTypeIdLit);

        dimList.getExprs().add(x.dims.get(i));
        cur = curArrayType.getElementType();
      }
      call.addArgs(classLitList, castableTypeMapList, elementTypeIdList,canLeafElementBeJSOLiteral,
          dimList, program.getLiteralInt(dims), getInitValueTypeFor(cur));
      ctx.replaceMe(call);
    }

    private void processInitializers(JNewArray x, Context ctx, JArrayType arrayType) {
      // override the type of the called method with the array's type
      SourceInfo sourceInfo = x.getSourceInfo();
      JMethodCall call = new JMethodCall(sourceInfo, null, initValues, arrayType);
      JLiteral classLit = x.getClassLiteral();
      JExpression castableTypeMap = getOrCreateCastMap(sourceInfo, arrayType);
      JRuntimeTypeReference elementTypeIds = getRuntimeTypeReference(sourceInfo, arrayType);
      JsonArray initList = new JsonArray(sourceInfo, program.getJavaScriptObject());
      JBooleanLiteral canLeafElementBeJSOLiteral =
          JBooleanLiteral.get(canLeafElementTypeBeJSO(arrayType));
      for (int i = 0; i < x.initializers.size(); ++i) {
        initList.getExprs().add(x.initializers.get(i));
      }
      call.addArgs(classLit, castableTypeMap, elementTypeIds, canLeafElementBeJSOLiteral, initList);
      ctx.replaceMe(call);
    }
  }

  public static void exec(JProgram program, boolean disableCastChecking) {
    new ArrayNormalizer(program, disableCastChecking).execImpl();
  }

  private final boolean disableCastChecking;
  private final JMethod initDim;
  private final JMethod initDims;
  private final JMethod initValues;
  private final JProgram program;
  private final JMethod setCheckMethod;

  private ArrayNormalizer(JProgram program, boolean disableCastChecking) {
    this.program = program;
    this.disableCastChecking = disableCastChecking;
    setCheckMethod = program.getIndexedMethod("Array.setCheck");

    initDim = program.getIndexedMethod("Array.initDim");
    initDims = program.getIndexedMethod("Array.initDims");
    initValues = program.getIndexedMethod("Array.initValues");
  }

  private void execImpl() {
    ArrayVisitor visitor = new ArrayVisitor();
    visitor.accept(program);
  }

}
