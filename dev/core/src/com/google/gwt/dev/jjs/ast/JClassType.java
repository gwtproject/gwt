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
public class JClassType extends JDeclaredType implements CanBeSetFinal {

  public JClassType(SourceInfo info, String name, boolean isAbstract, boolean isFinal,
      JsInteropType interopType) {
    super(info, name, interopType);
    setFlag(JFlags.ABSTRACT, isAbstract);
    setFlag(JFlags.FINAL, isFinal);
  }

  private static class ExternalSerializedForm implements Serializable {
    private final String name;

    public ExternalSerializedForm(JClassType classType) {
      name = classType.getName();
    }

    private Object readResolve() {
      return new JClassType(name);
    }
  }

  private JClassType superClass;

  public JClassType(SourceInfo info, String name, boolean isAbstract, boolean isFinal) {
    this(info, name, isAbstract, isFinal, JsInteropType.NONE);
  }

  /**
   * Construct a bare-bones deserialized external class.
   */
  JClassType(String name) {
    super(SourceOrigin.UNKNOWN, name, JsInteropType.NONE);
    setExternal(true);
  }

  @Override
  public final JClassType getSuperClass() {
    return superClass;
  }

  @Override
  public boolean isAbstract() {
    return isFlagSet(JFlags.ABSTRACT);
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
    return isFlagSet(JFlags.FINAL);
  }

  @Override
  public void setFinal() {
    setFlag(JFlags.FINAL);
  }

  /**
   * Sets this type's super class.
   */
  public final void setSuperClass(JClassType superClass) {
    this.superClass = superClass;
  }

  public boolean isJsPrototypeStub() {
    return isFlagSet(JFlags.JS_PROTOTYPE);
  }

  public void setJsPrototypeStub(boolean isJsPrototype) {
    setFlag(JFlags.JS_PROTOTYPE, isJsPrototype);
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
