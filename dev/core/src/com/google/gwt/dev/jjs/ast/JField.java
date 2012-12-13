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
package com.google.gwt.dev.jjs.ast;

import com.google.gwt.dev.jjs.SourceInfo;
import com.google.gwt.dev.jjs.SourceOrigin;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

/**
 * Java field definition.
 */
public class JField extends JVariable implements CanBeStatic, HasEnclosingType {
  /**
   * Determines whether the variable is final, volatile, or neither.
   */
  public static enum Disposition {
    COMPILE_TIME_CONSTANT, FINAL, NONE, THIS_REF, VOLATILE;

    public boolean isFinal() {
      return this == COMPILE_TIME_CONSTANT || this == FINAL || this == THIS_REF;
    }

    public boolean isThisRef() {
      return this == THIS_REF;
    }

    private boolean isCompileTimeConstant() {
      return this == COMPILE_TIME_CONSTANT;
    }

    private boolean isVolatile() {
      return this == VOLATILE;
    }
  }

  private static class ExternalSerializedForm implements Externalizable {

    private JDeclaredType enclosingType;
    private String signature;

    public ExternalSerializedForm(JField field) {
      enclosingType = field.getEnclosingType();
      signature = field.getSignature();
    }

    private Object readResolve() {
      String name = signature.substring(0, signature.indexOf(':'));
      JField result =
          new JField(SourceOrigin.UNKNOWN, name, enclosingType, JNullType.INSTANCE, false,
              Disposition.NONE);
      result.signature = signature;
      return result;
    }

    public ExternalSerializedForm() {
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
      out.writeObject(enclosingType);
      com.google.gwt.dev.util.Util.serializeString(signature, out);
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
      enclosingType = (JDeclaredType) in.readObject();
      signature = com.google.gwt.dev.util.Util.deserializeString(in);
    }
  }

  private static class ExternalSerializedNullField implements Externalizable {
    public static final ExternalSerializedNullField INSTANCE = new ExternalSerializedNullField();

    private Object readResolve() {
      return NULL_FIELD;
    }

    public ExternalSerializedNullField() {
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
      // No need to save anything. Will be replaced by singleton on deserialization.
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
      // Replaced by singleton on readResolve().
    }
  }

  public static final JField NULL_FIELD = new JField(SourceOrigin.UNKNOWN, "nullField", null,
      JNullType.INSTANCE, false, Disposition.FINAL);

  private JDeclaredType enclosingType;
  private boolean isCompileTimeConstant;
  private boolean isStatic;
  private boolean isThisRef;
  private boolean isVolatile;
  private transient String signature;

  public JField(SourceInfo info, String name, JDeclaredType enclosingType, JType type,
      boolean isStatic, Disposition disposition) {
    super(info, name, type, disposition.isFinal());
    this.enclosingType = enclosingType;
    this.isStatic = isStatic;
    this.isCompileTimeConstant = disposition.isCompileTimeConstant();
    this.isVolatile = disposition.isVolatile();
    this.isThisRef = disposition.isThisRef();
    // Disposition is not cached because we can be set final later.
  }

  public JDeclaredType getEnclosingType() {
    return enclosingType;
  }

  public JValueLiteral getLiteralInitializer() {
    JExpression initializer = getInitializer();
    if (initializer instanceof JValueLiteral) {
      return (JValueLiteral) initializer;
    }
    return null;
  }

  public String getSignature() {
    if (signature == null) {
      StringBuilder sb = new StringBuilder();
      sb.append(getName());
      sb.append(':');
      sb.append(getType().getJsniSignatureName());
      signature = sb.toString();
    }
    return signature;
  }

  public boolean isCompileTimeConstant() {
    return isCompileTimeConstant;
  }

  public boolean isExternal() {
    return getEnclosingType() != null && getEnclosingType().isExternal();
  }

  public boolean isStatic() {
    return isStatic;
  }

  public boolean isThisRef() {
    return isThisRef;
  }

  public boolean isVolatile() {
    return isVolatile;
  }

  @Override
  public void setFinal() {
    if (isVolatile()) {
      throw new IllegalStateException("Volatile fields cannot be set final");
    }
    super.setFinal();
  }

  public void setInitializer(JDeclarationStatement declStmt) {
    this.declStmt = declStmt;
  }

  public void setVolatile() {
    if (isFinal()) {
      throw new IllegalStateException("Final fields cannot be set volatile");
    }
    isVolatile = true;
  }

  public void traverse(JVisitor visitor, Context ctx) {
    if (visitor.visit(this, ctx)) {
      // Do not visit declStmt, it gets visited within its own code block.
    }
    visitor.endVisit(this, ctx);
  }

  protected Object writeReplace() {
    if (isExternal()) {
      return new ExternalSerializedForm(this);
    } else if (this == NULL_FIELD) {
      return ExternalSerializedNullField.INSTANCE;
    } else {
      return this;
    }
  }

  boolean replaces(JField originalField) {
    if (this == originalField) {
      return true;
    }
    return originalField.isExternal() && originalField.getSignature().equals(this.getSignature())
        && this.getEnclosingType().replaces(originalField.getEnclosingType());
  }

  public JField() {
  }

  @Override
  public void writeExternal(ObjectOutput out) throws IOException {
    super.writeExternal(out);
    out.writeBoolean(isCompileTimeConstant);
    out.writeBoolean(isStatic);
    out.writeBoolean(isThisRef);
    out.writeBoolean(isVolatile);
    out.writeObject(enclosingType);
  }

  @Override
  public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
    super.readExternal(in);
    isCompileTimeConstant = in.readBoolean();
    isStatic = in.readBoolean();
    isThisRef = in.readBoolean();
    isVolatile = in.readBoolean();
    enclosingType = (JDeclaredType) in.readObject();
  }
}
