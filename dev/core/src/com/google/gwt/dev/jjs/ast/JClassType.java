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
 * Java class type reference expression.
 */
public class JClassType extends JDeclaredType implements CanBeSetFinal {

  private static class ExternalSerializedForm implements Externalizable {
    private String name;

    public ExternalSerializedForm(JClassType classType) {
      name = classType.getName();
    }

    private Object readResolve() {
      return new JClassType(name);
    }

    public ExternalSerializedForm() {
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
      com.google.gwt.dev.util.Util.serializeString(name, out);
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
      name = com.google.gwt.dev.util.Util.deserializeString(in);
    }
  }

  private boolean isAbstract;
  private boolean isFinal;
  private JClassType superClass;

  public JClassType(SourceInfo info, String name, boolean isAbstract, boolean isFinal) {
    super(info, name);
    this.isAbstract = isAbstract;
    this.isFinal = isFinal;
  }

  /**
   * Construct a bare-bones deserialized external class.
   */
  private JClassType(String name) {
    super(SourceOrigin.UNKNOWN, name);
    isAbstract = false;
    setExternal(true);
  }

  @Override
  public String getClassLiteralFactoryMethod() {
    return "Class.createForClass";
  }

  @Override
  public final JClassType getSuperClass() {
    return superClass;
  }

  public boolean isAbstract() {
    return isAbstract;
  }

  public JEnumType isEnumOrSubclass() {
    if (getSuperClass() != null) {
      return getSuperClass().isEnumOrSubclass();
    }
    return null;
  }

  public boolean isFinal() {
    return isFinal;
  }

  public void setFinal() {
    isFinal = true;
  }

  /**
   * Sets this type's super class.
   */
  public final void setSuperClass(JClassType superClass) {
    this.superClass = superClass;
  }

  public void traverse(JVisitor visitor, Context ctx) {
    if (visitor.visit(this, ctx)) {
      fields = visitor.acceptWithInsertRemoveImmutable(fields);
      methods = visitor.acceptWithInsertRemoveImmutable(methods);
    }
    visitor.endVisit(this, ctx);
  }

  @Override
  protected Object writeReplace() {
    if (isExternal()) {
      return new ExternalSerializedForm(this);
    } else {
      return this;
    }
  }

  public JClassType() {
  }

  @Override
  public void writeExternal(ObjectOutput out) throws IOException {
    super.writeExternal(out);
    out.writeBoolean(isAbstract);
    out.writeBoolean(isFinal);
    out.writeObject(superClass);
  }

  @Override
  public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
    super.readExternal(in);
    isAbstract = in.readBoolean();
    isFinal = in.readBoolean();
    superClass = (JClassType) in.readObject();
  }
}
