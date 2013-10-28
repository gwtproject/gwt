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

import com.google.gwt.dev.jjs.ast.JMethod;
import com.google.gwt.thirdparty.guava.common.base.Strings;
import com.google.gwt.thirdparty.guava.common.collect.Lists;
import com.google.gwt.thirdparty.guava.common.collect.Sets;

import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * Represents a method. Its goal is to keep as minimal information as possible and track
 * dependencies between them. The signature is in jsni format, including the return type and the
 * parameter types.
 *
 * @author ocallau@google.com (Oscar Callau)
 */
public class MethodDescriptor extends MemberDescriptor {

  public static MethodDescriptor from(ClassDescriptor owner, JMethod method, String signature) {
    MethodDescriptor methodDescriptor = new MethodDescriptor(owner, signature);
    methodDescriptor.reference = method;
    return methodDescriptor;
  }

  protected Set<MethodDescriptor> dependants = Sets.newIdentityHashSet();
  protected int uniqueId;
  protected String paramTypes;
  protected JMethod reference;
  protected Set<String> otherJsNames = Sets.newHashSet();

  public MethodDescriptor(ClassDescriptor owner, String jsniSignature) {
    super(owner);
    String[] methodName = jsniSignature.split("\\(|\\)");
    this.name = methodName[0];
    this.paramTypes = methodName[1];
    // fix for wrong jsni signature for constructors in JMethod.getSignature()
    this.type = methodName[2].equals(" <init>") ? "V" : methodName[2];
  }

  public void addDependant(MethodDescriptor methodDescriptor) {
    dependants.add(methodDescriptor);
  }

  public List<String> getAllObfuscatedNames() {
    List<String> all = Lists.newArrayList();
    all.add(obfuscatedName);
    all.addAll(otherJsNames);
    return all;
  }

  public int[] getDependantPointers() {
    int[] ps = new int[dependants.size()];
    int c = 0;
    for (MethodDescriptor dependant : dependants) {
      ps[c++] = dependant.getUniqueId();
    }
    return ps;
  }

  public Set<MethodDescriptor> getDependants() {
    return dependants;
  }

  @Override
  public String getJsniSignature() {
    return name + "(" + paramTypes + ")" + type;
  }

  public String getParamTypes() {
    return paramTypes;
  }

  public JMethod getReference() {
    return reference;
  }

  public int getUniqueId() {
    return uniqueId;
  }

  public void setObfuscatedName(String newJsName) {
    if (Strings.isNullOrEmpty(obfuscatedName)) {
      obfuscatedName = newJsName;
    } else {
      if (!newJsName.equals(obfuscatedName)) {
        otherJsNames.add(newJsName);
      }
    }
  }

  public void setUniqueId(int uniqueId) {
    this.uniqueId = uniqueId;
  }

  public Collection<String> getMoreObfuscatedNames() {
    return this.otherJsNames;
  }
}