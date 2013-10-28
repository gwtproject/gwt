/*
 * Copyright 2013 Google Inc.
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
package com.google.gwt.core.ext.soyc.coderef;

import com.google.gwt.dev.jjs.ast.JDeclaredType;
import com.google.gwt.dev.jjs.ast.JField;
import com.google.gwt.dev.jjs.ast.JMethod;
import com.google.gwt.thirdparty.guava.common.collect.Maps;

import java.util.Collection;
import java.util.Map;

/**
 * A succinct code representation for classes.
 *
 */
public class ClassDescriptor extends EntityDescriptor {

  public static ClassDescriptor from(JDeclaredType classType) {
    ClassDescriptor cls = new ClassDescriptor(classType.getName());
    /* fields and methods are added only when reached
    for (JField jField : classType.getFields()) {
      cls.fieldDic.put(jField.getName(), FieldDescriptor.from(cls, jField));
    }*/

    cls.reference = classType;
    return cls;
  }

  private String packageName = "";
  private Map<String, MethodDescriptor> methodDic = Maps.newTreeMap();
  private Map<String, FieldDescriptor> fieldDic = Maps.newTreeMap();
  private JDeclaredType reference;

  /* TODO(ocallau) for storing hierarchy information, but supporting this has been delayed
  protected int id;
  protected ClassDescriptor superClass;
  protected Collection<ClassDescriptor> interfaces;*/

  public ClassDescriptor(String qualifiedName) {
    super();

    int idx = qualifiedName.lastIndexOf('.');
    name = qualifiedName.substring(idx + 1);
    if (idx > 0) {
      packageName = qualifiedName.substring(0, idx);
    }
  }

  public ClassDescriptor(String clsName, String pkgName) {
    super();
    this.name = clsName;
    this.packageName = pkgName;
  }

  public void addField(FieldDescriptor fld) {
    this.fieldDic.put(fld.getName(), fld);
  }

  public void addMethod(MethodDescriptor methodDescriptor) {
    this.methodDic.put(methodDescriptor.getJsniSignature(), methodDescriptor);
  }

  public FieldDescriptor fieldFrom(JField field) {
    FieldDescriptor fld = this.fieldDic.get(field.getName());
    if (fld == null) {
      fld = FieldDescriptor.from(this, field);
      fieldDic.put(field.getName(), fld);
    }
    return fld;
  }

  public FieldDescriptor getField(String fieldName) {
    return fieldDic.get(fieldName);
  }

  public Collection<FieldDescriptor> getFields() {
    return fieldDic.values();
  }

  public String getFullName() {
    if (packageName.isEmpty()) {
      return name;
    }
    return packageName + "." + name;
  }

  public MethodDescriptor getMethod(String methodSignature) {
    return methodDic.get(methodSignature);
  }

  public Collection<MethodDescriptor> getMethods() {
    return methodDic.values();
  }

  public String getName() {
    return name;
  }

  public String getPackageName() {
    return packageName;
  }

  public JDeclaredType getReference() {
    return reference;
  }

  // signature is necessary because some methods real name is hidden in static-synth methods.
  public MethodDescriptor methodFrom(JMethod method, String signature) {
    MethodDescriptor mth = this.methodDic.get(signature);
    if (mth == null) {
      mth = MethodDescriptor.from(this, method, signature);
      methodDic.put(signature, mth);
    } else {
      mth.addReference(method);
    }
    return mth;
  }
}
