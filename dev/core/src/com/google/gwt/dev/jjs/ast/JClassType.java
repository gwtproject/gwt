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

import java.io.Serializable;

/**
 * Java class type reference expression.
 */
public class JClassType extends JDeclaredType {

  private static class ExternalSerializedForm implements Serializable {
    private final String name;

    public ExternalSerializedForm(JClassType classType) {
      name = classType.getName();
    }

    private Object readResolve() {
      return new JClassType(name);
    }
  }

  private final boolean isAbstract;
  private final boolean isFinal;
  private boolean isJso;
  private JClassType superClass;
  private boolean isJsPrototypeStub;

  public JClassType(SourceInfo info, String name, boolean isAbstract, boolean isFinal) {
    super(info, name);
    this.isAbstract = isAbstract;
    this.isFinal = isFinal;
    this.isJso = name.equals(JProgram.JAVASCRIPTOBJECT);
  }

  /**
   * Construct a bare-bones deserialized external class.
   */
  JClassType(String name) {
    super(SourceOrigin.UNKNOWN, name);
    isAbstract = false;
    isFinal = false;
    isJso = name.equals(JProgram.JAVASCRIPTOBJECT);
    setExternal(true);
  }

  @Override
  public final JClassType getSuperClass() {
    return superClass;
  }

  @Override
  public boolean isAbstract() {
    return isAbstract;
  }

  @Override
  public JEnumType isEnumOrSubclass() {
    if (getSuperClass() != null) {
      return getSuperClass().isEnumOrSubclass();
    }
    return null;
  }

  @Override
  public boolean isFinal() {
    return isFinal;
  }

  @Override
  public boolean isJsoType() {
    return isJso;
  }

  /**
   * Sets this type's super class.
   */
  public final void setSuperClass(JClassType superClass) {
    assert this.superClass == null || this.superClass == superClass || this.superClass.isExternal();
    this.superClass = superClass;

    if (!name.equals(JProgram.JAVASCRIPTOBJECT) && superClass != null) {
      this.isJso = superClass.isJso;
    }
  }

  public boolean isJsPrototypeStub() {
    return isJsPrototypeStub;
  }

  public void setJsPrototypeStub(boolean isJsPrototype) {
    this.isJsPrototypeStub = isJsPrototype;
  }

  @Override
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
}
