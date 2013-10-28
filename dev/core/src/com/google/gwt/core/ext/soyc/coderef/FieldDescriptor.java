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

import com.google.gwt.dev.jjs.ast.JField;

/**
 * Represents a field.
 *
 * @author ocallau@google.com (Oscar Callau)
 */
public class FieldDescriptor extends MemberDescriptor {

  public static FieldDescriptor from(ClassDescriptor cls, JField jField) {
    FieldDescriptor fieldDescriptor = new FieldDescriptor(cls, jField.getName(),
        jField.getType().getJsniSignatureName());
    fieldDescriptor.reference = jField;
    return fieldDescriptor;
  }

  protected JField reference;

  public FieldDescriptor(ClassDescriptor owner, String name, String type) {
    super(owner);
    this.name = name;
    this.type = type;
  }

  public JField getReference() {
    return reference;
  }

  @Override
  public String getJsniSignature() {
    return this.name + ":" + this.type;
  }
}
