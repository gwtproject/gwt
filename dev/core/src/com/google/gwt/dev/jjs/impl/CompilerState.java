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
package com.google.gwt.dev.jjs.impl;

import com.google.gwt.core.ext.linker.SymbolData;
import com.google.gwt.core.ext.linker.impl.StandardSymbolData;
import com.google.gwt.dev.jjs.UnifiedAst;
import com.google.gwt.dev.jjs.ast.JProgram;
import com.google.gwt.dev.js.ast.JsName;
import com.google.gwt.dev.js.ast.JsNode;
import com.google.gwt.dev.js.ast.JsProgram;

import java.io.Serializable;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 * Serializable compiler state.
 */
public class CompilerState implements Serializable {

  private final JProgram jProgram;
  private final JsProgram jsProgram;
  private JavaToJavaScriptMap javaToJavaScriptMap;
  private Set<JsNode> functionsForJsInlining;
  private Map<StandardSymbolData, JsName> symbolTable;

  {
    symbolTable = new TreeMap<StandardSymbolData, JsName>(new SymbolData.ClassIdentComparator());
  }

  public CompilerState(UnifiedAst.AST ast) {
    this.jProgram = ast.getJProgram();
    this.jsProgram = ast.getJsProgram();
  }

  public CompilerState(JProgram jProgram, JsProgram jsProgram,
      JavaToJavaScriptMap javaToJavaScriptMap) {
    this.jProgram = jProgram;
    this.jsProgram = jsProgram;
    this.javaToJavaScriptMap = javaToJavaScriptMap;
  }

  public JProgram getjProgram() {
    return jProgram;
  }

  public JsProgram getJsProgram() {
    return jsProgram;
  }

  public JavaToJavaScriptMap getJavaToJavaScriptMap() {
    return javaToJavaScriptMap;
  }

  public void setJavaToJavaScriptMap(JavaToJavaScriptMap javaToJavaScriptMap) {
    this.javaToJavaScriptMap = javaToJavaScriptMap;
  }

  public Set<JsNode> getFunctionsForJsInlining() {
    return functionsForJsInlining;
  }

  public void setFunctionsForJsInlining(Set<JsNode> functionsForJsInlining) {
    this.functionsForJsInlining = functionsForJsInlining;
  }

  public Map<StandardSymbolData, JsName> getSymbolTable() {
    return symbolTable;
  }

  public void setSymbolTable(Map<StandardSymbolData, JsName> symbolTable) {
    this.symbolTable = symbolTable;
  }
}
