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
package com.google.gwt.dev.javac;

import com.google.gwt.dev.jjs.ast.JDeclaredType;
import com.google.gwt.dev.jjs.ast.JProgram;
import com.google.gwt.dev.util.Util;
import com.google.gwt.dev.util.collect.Lists;

import org.eclipse.jdt.core.compiler.CategorizedProblem;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.util.Collection;
import java.util.List;

abstract class CompilationUnitImpl extends CompilationUnit {

  /**
   * Handle to serialized GWT AST.
   */
  protected transient long astToken;

  private Dependencies dependencies;
  private List<CompiledClass> exposedCompiledClasses;
  private boolean hasErrors;
  private List<JsniMethod> jsniMethods;
  private MethodArgNamesLookup methodArgs;
  private CategorizedProblem[] problems;

  public CompilationUnitImpl() {
  }

  public CompilationUnitImpl(List<CompiledClass> compiledClasses,
      List<JDeclaredType> types, Dependencies dependencies,
      Collection<? extends JsniMethod> jsniMethods,
      MethodArgNamesLookup methodArgs, CategorizedProblem[] problems) {
    this.exposedCompiledClasses = Lists.normalizeUnmodifiable(compiledClasses);
    this.dependencies = dependencies;
    this.jsniMethods = Lists.create(jsniMethods.toArray(new JsniMethod[jsniMethods.size()]));
    this.methodArgs = methodArgs;
    this.problems = problems;
    boolean hasAnyErrors = false;
    if (problems != null) {
      for (CategorizedProblem problem : problems) {
        if (problem.isError()) {
          hasAnyErrors = true;
        }
      }
    }
    this.hasErrors = hasAnyErrors;
    for (CompiledClass cc : compiledClasses) {
      cc.initUnit(this);
    }
    try {
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      ObjectOutputStream out = new ObjectOutputStream(baos);
      JProgram.serializeTypes(types, out);
      out.close();
      astToken = diskCache.writeByteArray(baos.toByteArray());
    } catch (IOException e) {
      throw new RuntimeException("Unexpected IOException on in-memory stream",
          e);
    }
  }

  @Override
  public Collection<CompiledClass> getCompiledClasses() {
    return exposedCompiledClasses;
  }

  @Override
  public List<JsniMethod> getJsniMethods() {
    return jsniMethods;
  }

  @Override
  public MethodArgNamesLookup getMethodArgs() {
    return methodArgs;
  }

  @Override
  public byte[] getTypesSerialized() {
    return diskCache.readByteArray(astToken);
  }

  @Override
  public boolean isError() {
    return hasErrors;
  }

  @Override
  Dependencies getDependencies() {
    return dependencies;
  }

  @Override
  CategorizedProblem[] getProblems() {
    return problems;
  }

  @Override
  public void writeExternal(ObjectOutput out) throws IOException {
    super.writeExternal(out);
    out.writeObject(dependencies);
    Util.serializeCollection(exposedCompiledClasses, out);
    out.writeBoolean(hasErrors);
    Util.serializeCollection(jsniMethods, out);
    out.writeObject(methodArgs);
    Util.serializeArray(problems,out);
  }

  @Override
  public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
    super.readExternal(in);
    dependencies = (Dependencies) in.readObject();
    exposedCompiledClasses = Util.deserializeObjectList(in);
    hasErrors = in.readBoolean();
    jsniMethods = Util.deserializeObjectList(in);
    methodArgs = (MethodArgNamesLookup) in.readObject();
    problems = (CategorizedProblem[]) Util.deserializeObjectArray(in, CategorizedProblem.class);
  }

}