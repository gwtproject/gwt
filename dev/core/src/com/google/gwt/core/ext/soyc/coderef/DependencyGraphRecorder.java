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

import com.google.gwt.core.ext.soyc.impl.DependencyRecorder;
import com.google.gwt.dev.jjs.ast.JDeclaredType;
import com.google.gwt.dev.jjs.ast.JMethod;
import com.google.gwt.dev.jjs.ast.JProgram;
import com.google.gwt.thirdparty.guava.common.collect.Maps;

import java.io.OutputStream;
import java.util.Map;

/**
 * Records the dependencies for the (new) soyc.
 *
 */
public class DependencyGraphRecorder extends DependencyRecorder {

   //  cls -> (mth -> mth .. mth) .. (mth -> ..))
  private Map<String, ClassDescriptor> codeGraph = Maps.newTreeMap();
  private String currentGraph;
  private int nextUniqueId = 0;
  private JProgram jProgram;

  public DependencyGraphRecorder(OutputStream out, JProgram jProgram) {
    super(out);
    this.jProgram = jProgram;
  }

  protected int nextPointerId() {
    return ++nextUniqueId;
  }

  public Map<String, ClassDescriptor> getCodeGraph() {
    return this.codeGraph;
  }

  public void startDependencyGraph(String name, String extendz) {
    super.startDependencyGraph(name, extendz);

    currentGraph = name;
  }

  protected void printMethodDependencyBetween(JMethod curMethod, JMethod depMethod) {
    super.printMethodDependencyBetween(curMethod, depMethod);

    methodFrom(curMethod).addDependant(methodFrom(depMethod));
  }

  protected String signatureFor(JMethod method) {
    JMethod original = jProgram.staticImplFor(method);
    if (original == null) { //method is the original
      return method.getSignature();
    }
    return original.getSignature();
  }

  public MethodDescriptor methodFrom(JMethod method) {
    MethodDescriptor mth = classFrom(method.getEnclosingType())
                             .methodFrom(method, signatureFor(method));
    if (!isValid(mth.getUniqueId())) {
      mth.setUniqueId(nextPointerId());
    }
    return mth;
  }

  protected boolean isValid(int n) {
    return n > 0;
  }

  public ClassDescriptor classFrom(JDeclaredType classType) {
    // JDeclaredType.getName return the fully qualified name
    ClassDescriptor cls = codeGraph.get(classType.getName());
    if (cls == null) {
      cls = ClassDescriptor.from(classType);
      codeGraph.put(classType.getName(), cls);
    }
    return cls;
  }
}
