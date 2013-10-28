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

/**
 * Represents an abstract member, such as a field or a method. It is keep simple and will be
 * serialized to json. The type of a member is its type in jsni format when evaluated.
 *
 * @author ocallau@google.com (Oscar Callau)
 */
public abstract class MemberDescriptor extends EntityDescriptor {

  protected String type;
  protected ClassDescriptor enclosingType;

  protected MemberDescriptor(ClassDescriptor owner) {
    enclosingType = owner;
  }

  public ClassDescriptor getEnclosingType() {
    return enclosingType;
  }

  /**
   * The signature of the member
   *
   * @return The member name plus its signature
   */
  public abstract String getJsniSignature();

  public String getName() {
    return name;
  }

  public String getType() {
    return type;
  }

  public String getFullName() {
    return enclosingType.getFullName() + "::" + this.getJsniSignature();
  }
}
