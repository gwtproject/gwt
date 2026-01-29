/*
 * Copyright 2024 GWT Project Authors
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
import com.google.gwt.dev.jjs.ast.JBinaryOperation;
import com.google.gwt.dev.jjs.ast.JBinaryOperator;
import com.google.gwt.dev.jjs.ast.JBooleanLiteral;
import com.google.gwt.dev.jjs.ast.JClassLiteral;
import com.google.gwt.dev.jjs.ast.JClassType;
import com.google.gwt.dev.jjs.ast.JDeclaredType;
import com.google.gwt.dev.jjs.ast.JExpression;
import com.google.gwt.dev.jjs.ast.JField;
import com.google.gwt.dev.jjs.ast.JFieldRef;
import com.google.gwt.dev.jjs.ast.JIfStatement;
import com.google.gwt.dev.jjs.ast.JIntLiteral;
import com.google.gwt.dev.jjs.ast.JLocal;
import com.google.gwt.dev.jjs.ast.JMethod;
import com.google.gwt.dev.jjs.ast.JMethodBody;
import com.google.gwt.dev.jjs.ast.JMethodCall;
import com.google.gwt.dev.jjs.ast.JNewArray;
import com.google.gwt.dev.jjs.ast.JNullLiteral;
import com.google.gwt.dev.jjs.ast.JParameter;
import com.google.gwt.dev.jjs.ast.JPrimitiveType;
import com.google.gwt.dev.jjs.ast.JProgram;
import com.google.gwt.dev.jjs.ast.JRecordType;
import com.google.gwt.dev.jjs.ast.JStringLiteral;
import com.google.gwt.dev.jjs.ast.JThisRef;
import com.google.gwt.dev.jjs.ast.JUnsafeTypeCoercion;
import com.google.gwt.dev.jjs.ast.RuntimeConstants;
import com.google.gwt.thirdparty.guava.common.collect.Lists;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Implements the methods required for a Java Record type, based on its components/fields.
 */
public class ImplementRecordComponents {

  public static void exec(JProgram program) {
    new ImplementRecordComponents(program).execImpl();
  }

  private final JProgram program;
  private final AutoboxUtils autoboxUtils;
  private final JMethod getClassMethod;
  private final JMethod getSimpleNameMethod;
  private final JClassType javaLangString;

  private ImplementRecordComponents(JProgram program) {
    this.program = program;
    this.autoboxUtils = new AutoboxUtils(program);

    getClassMethod = program.getIndexedMethod(RuntimeConstants.OBJECT_GET_CLASS);
    getSimpleNameMethod = program.getIndexedMethod(RuntimeConstants.CLASS_GET_SIMPLE_NAME);
    javaLangString = program.getTypeJavaLangString();
  }

  private void execImpl() {
    for (JDeclaredType type : program.getDeclaredTypes()) {
      if (type instanceof JRecordType) {
        implementRecordComponents((JRecordType) type);
      }
    }
  }

  private void implementRecordComponents(JRecordType type) {
    // This is a record type, and any methods that were declared but not referenced now need
    // to be defined. These include the field-named method accessors, equals/hashCode and
    // toString. If not defined, we'll synthesize them based on the record components.
    SourceInfo info = type.getSourceInfo();
    for (JMethod method : type.getMethods()) {
      if (method.getBody() != null) {
        // If there is a body, that means the record has its own declaration of this method, and
        // we should not re-declare it.
        continue;
      }

      if (method.getName().equals(GwtAstBuilder.TO_STRING_METHOD_NAME)
              && method.getParams().isEmpty()) {
        implementToString(type, method, info);
      } else if (method.getName().equals(GwtAstBuilder.EQUALS_METHOD_NAME)
              && method.getParams().size() == 1
              && method.getParams().get(0).getType().equals(program.getTypeJavaLangObject())) {
        implementEquals(type, method, info);
      } else if (method.getName().equals(GwtAstBuilder.HASHCODE_METHOD_NAME)
              && method.getParams().isEmpty()) {
        implementHashCode(type, method, info);
      } else if (method.getParams().isEmpty()) {
        // Check if it has the same name+type as a component/field
        Optional<JField> matchingField = type.getFields().stream()
                .filter(f -> f.getName().equals(method.getName()))
                .filter(f -> f.getType().equals(method.getType()))
                .findFirst();
        matchingField.ifPresent(f -> implementComponentAccessor(type, method, f));
      }
    }
  }

  private static void implementComponentAccessor(JRecordType type, JMethod method, JField field) {
    // We can pick a more specific source for this than the others, use the "field" itself.
    SourceInfo info = field.getSourceInfo();

    // Create a simple accessor method and bind it, so it can be used anywhere outside this type.
    JFieldRef fieldReference = new JFieldRef(info, new JThisRef(info, type),
            field, type);
    JMethodBody body = new JMethodBody(info);
    body.getBlock().addStmt(fieldReference.makeReturnStatement());
    method.setBody(body);
  }

  private void implementHashCode(JRecordType type, JMethod method, SourceInfo info) {
    final JExpression hashcodeStatement;
    if (type.getFields().isEmpty()) {
      // No fields, just emit hashcode=0
      hashcodeStatement = new JIntLiteral(info, 0);
    } else {
      List<JExpression> exprs = Lists.newArrayListWithCapacity(type.getFields().size());
      for (JField field : type.getFields()) {
        JFieldRef jFieldRef = new JFieldRef(info, new JThisRef(info, type), field, type);
        if (jFieldRef.getType().isPrimitiveType()) {
          exprs.add(autoboxUtils.box(jFieldRef, (JPrimitiveType) jFieldRef.getType()));
        } else {
          exprs.add(jFieldRef);
        }
      }
      JNewArray varargsWrapper = JNewArray.createArrayWithInitializers(info,
              program.getTypeJavaLangObjectArray(), exprs);
      JMethod hash = program.getIndexedMethod(RuntimeConstants.OBJECTS_HASH);
      hashcodeStatement = new JMethodCall(info, null, hash, varargsWrapper);
    }
    JMethodBody body = new JMethodBody(info);
    body.getBlock().addStmt(hashcodeStatement.makeReturnStatement());

    method.setBody(body);
  }

  private void implementEquals(JRecordType type, JMethod method, SourceInfo info) {
    JMethodBody body = new JMethodBody(info);
    JParameter otherParam = method.getParams().get(0);

    // if (this == other) return true;
    JBinaryOperation eq =
            new JBinaryOperation(info, JPrimitiveType.BOOLEAN, JBinaryOperator.EQ,
                    new JThisRef(info, type),
                    otherParam.createRef(info));
    body.getBlock().addStmt(new JIfStatement(info, eq,
            JBooleanLiteral.TRUE.makeReturnStatement(), null));

    // other == null
    JBinaryOperation nonNullCheck =
            new JBinaryOperation(info, JPrimitiveType.BOOLEAN, JBinaryOperator.EQ,
                    otherParam.createRef(info), JNullLiteral.INSTANCE);
    // MyRecordType.class != other.getClass()
    JBinaryOperation sameTypeCheck =
            new JBinaryOperation(info, JPrimitiveType.BOOLEAN, JBinaryOperator.NEQ,
                    new JClassLiteral(info, type),
                    new JMethodCall(info, otherParam.createRef(info), getClassMethod));
    // other == null || MyRecordType.class != other.getClass()
    JBinaryOperation nullAndTypeCheck =
            new JBinaryOperation(info, JPrimitiveType.BOOLEAN, JBinaryOperator.OR,
                    nonNullCheck, sameTypeCheck);

    // if (other == null || MyRecordType.class != other.getClass()) return false;
    body.getBlock().addStmt(new JIfStatement(info, nullAndTypeCheck,
            JBooleanLiteral.FALSE.makeReturnStatement(), null));

    // Create a local to assign to and compare each component
    JLocal typedOther = JProgram.createLocal(info, "other", type, true, body);
    // We can use an unsafe cast since we know the check will succeed
    JUnsafeTypeCoercion uncheckedCast =
            new JUnsafeTypeCoercion(info, type, otherParam.createRef(info));
    JBinaryOperation uncheckedAssign = new JBinaryOperation(info, type, JBinaryOperator.ASG,
            typedOther.createRef(info), uncheckedCast);
    body.getBlock().addStmt(uncheckedAssign.makeStatement());

    JExpression componentCheck = JBooleanLiteral.TRUE;
    JMethod objectEquals = program.getIndexedMethod(RuntimeConstants.OBJECT_EQUALS);
    for (JField field : type.getFields()) {
      if (!field.isStatic()) {
        JFieldRef myField = new JFieldRef(info, new JThisRef(info, type), field, type);
        JFieldRef otherField = new JFieldRef(info, typedOther.createRef(info), field, type);
        final JBinaryOperation equals;
        if (field.getType().isPrimitiveType()) {
          equals = new JBinaryOperation(info, JPrimitiveType.BOOLEAN,
                  JBinaryOperator.EQ,
                  myField,
                  otherField);
        } else {
          // We would like to use Objects.equals here to be more concise, but we would need
          // to look up the right impl based on the field - just as simple to insert a null check
          // and get it a little closer to all being inlined away

          // Make another field ref to call equals() on
          JFieldRef myField2 = new JFieldRef(info, new JThisRef(info, type), field, type);
          equals = new JBinaryOperation(info, JPrimitiveType.BOOLEAN, JBinaryOperator.AND,
                  new JBinaryOperation(info, JPrimitiveType.BOOLEAN, JBinaryOperator.NEQ,
                          myField, JNullLiteral.INSTANCE),
                  new JMethodCall(info, myField2, objectEquals, otherField));
        }
        if (componentCheck != JBooleanLiteral.TRUE) {
          componentCheck = new JBinaryOperation(info, JPrimitiveType.BOOLEAN,
                  JBinaryOperator.AND,
                  componentCheck,
                  equals);
        } else {
          componentCheck = equals;
        }
      }
    }

    body.getBlock().addStmt(componentCheck.makeReturnStatement());
    method.setBody(body);
  }

  private void implementToString(JRecordType type, JMethod method, SourceInfo info) {
    List<JExpression> args = new ArrayList<>();

    // Concatenate type with []s and component values, assigning to toStrExpr as we append
    // more concat operations. Using getClass().getSimpleName() rather than a string literal
    // allows the toString implementation to emit the obfuscated class name.
    JMethodCall getClass = new JMethodCall(info, new JThisRef(info, type), getClassMethod);
    JExpression toStrExpr = new JMethodCall(info, getClass, getSimpleNameMethod);

    args.add(new JStringLiteral(info, "[", javaLangString));
    List<JField> fields = type.getFields();
    for (int i = 0; i < fields.size(); i++) {
      if (i != 0) {
        args.add(new JStringLiteral(info, ", ", javaLangString));
      }
      JField field = fields.get(i);
      args.add(new JStringLiteral(info, field.getName() + "=", javaLangString));
      args.add(new JFieldRef(info, new JThisRef(info, type), field, type));
    }
    args.add(new JStringLiteral(info, "]", javaLangString));
    for (JExpression arg : args) {
      toStrExpr = new JBinaryOperation(info, javaLangString, JBinaryOperator.CONCAT,
              toStrExpr,
              arg);
    }

    JMethodBody body = new JMethodBody(info);
    body.getBlock().addStmt(toStrExpr.makeReturnStatement());
    method.setBody(body);
  }
}
