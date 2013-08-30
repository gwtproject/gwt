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

import com.google.gwt.core.ext.BadPropertyValueException;
import com.google.gwt.core.ext.ConfigurationProperty;
import com.google.gwt.core.ext.PropertyOracle;
import com.google.gwt.core.ext.SelectionProperty;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.linker.SymbolData;
import com.google.gwt.core.ext.linker.impl.StandardSymbolData;
import com.google.gwt.dev.Permutation;
import com.google.gwt.dev.jjs.JJSOptions;
import com.google.gwt.dev.jjs.UnifiedAst;
import com.google.gwt.dev.jjs.ast.JProgram;
import com.google.gwt.dev.js.ast.JsName;
import com.google.gwt.dev.js.ast.JsNode;
import com.google.gwt.dev.js.ast.JsProgram;
import com.google.gwt.thirdparty.guava.common.collect.Multimap;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 * Serializable compiler state.
 */
public class CompilerContext implements Externalizable {


  private JProgram jProgram;
  private JsProgram jsProgram;
  private JavaToJavaScriptMap javaToJavaScriptMap;
  private Set<JsNode> functionsForJsInlining;
  private Map<StandardSymbolData, JsName> symbolTable =
      new TreeMap<StandardSymbolData, JsName>(new SymbolData.ClassIdentComparator());
  private Permutation permutation;
  private JJSOptions options;
  private Multimap<String, Integer> instrumentableLines;
  private TreeLogger logger;

  /**
   * Empty constructor for externalization.
   */
  public CompilerContext() {
  }

  /**
   * Constructs a compiler context for passes that only care about JsProgram. Used by
   * {@link com.google.gwt.core.ext.linker.impl.StandardLinkerContext}.
   */
  public CompilerContext(JsProgram jsProgram) {
    this.jsProgram = jsProgram;
  }

  public CompilerContext(JProgram jProgram, JsProgram jsProgram,  JJSOptions options) {
    this.jProgram = jProgram;
    this.jsProgram = jsProgram;
    this.options = options;
  }

  public CompilerContext(TreeLogger logger, UnifiedAst.AST ast, Permutation permutation, JJSOptions options) {
    this.jProgram = ast.getJProgram();
    this.jsProgram = ast.getJsProgram();
    this.permutation = permutation;
    this.options = options;
    this.logger = logger;
  }

  /**
   * Returns the Java AST.
   */
  public JProgram getJProgram() {
    assert jProgram != null;
    return jProgram;
  }

  /**
   * Returns the JavaScript AST.
   */
  public JsProgram getJsProgram() {
    assert jsProgram != null;
    return jsProgram;
  }

  /**
   * Return the mapping between Java AST program elements and JavaScript names as a
   * JavaToJavaScriptMap object.
   *
   * <p>Such a map is produced by the {@link GenerateJavaScriptAST} pass.
   */
  public JavaToJavaScriptMap getJavaToJavaScriptMap() {
    assert javaToJavaScriptMap != null;
    return javaToJavaScriptMap;
  }

  /**
   * Set the JavaToJavaScriptMap that maps Java AST program elements to JavaScript names.
   *
   * <p>Called by the {@link GenerateJavaScriptAST} pass.
   */
  public void setJavaToJavaScriptMap(JavaToJavaScriptMap javaToJavaScriptMap) {
    this.javaToJavaScriptMap = javaToJavaScriptMap;
  }

  /**
   * Returns the JavaScript functions that have to be considered by the JsInliner.
   *
   * <p>Produced by {@link GenerateJavaScriptAST} and consumed by
   * {@link com.google.gwt.dev.js.JsInliner}
   */
  public Set<JsNode> getFunctionsForJsInlining() {
    assert functionsForJsInlining != null;
    return functionsForJsInlining;
  }

  /**
   * Sets the set of functions that need to be condisered for inliner by JsInliner.
   *
   * <p>Set by {@link GenerateJavaScriptAST}
   */
  public void setFunctionsForJsInlining(Set<JsNode> functionsForJsInlining) {
    this.functionsForJsInlining = functionsForJsInlining;
  }


  public Map<StandardSymbolData, JsName> getSymbolTable() {
    assert symbolTable != null;
    return symbolTable;
  }

  public Permutation getPermutation() {
    assert permutation != null;
    return permutation;
  }

  public PropertyOracle[] getPropertyOracles() {
    if (permutation == null) {
      return null;
    }
    return permutation.getPropertyOracles();
  }

  public JJSOptions getOptions() {
    assert options != null;
    return options;
  }

  public Multimap<String, Integer> getInstrumentableLines() {
    return instrumentableLines;
  }

  public void setInstrumentableLines(Multimap<String, Integer> instrumentableLines) {
    this.instrumentableLines = instrumentableLines;
  }

  public boolean isSourceMapsEnabled() {
    return findBooleanProperty(getPropertyOracles(),  logger,
        "compiler.useSourceMaps", "true", true, false, false);
  }

  public int getLeftOverSizeMerge() {
    return findIntegerConfigurationProperty(getPropertyOracles(), logger,
        CodeSplitter2.LEFTOVERMERGE_SIZE, 0);
  }

  public boolean isIE6OrUnknown() {
    return findBooleanProperty(getPropertyOracles(), logger, "user.agent", "ie6",
        true, false, true);
  }
  @Override
  public void writeExternal(ObjectOutput out) throws IOException {
    out.writeObject(javaToJavaScriptMap);
    out.writeObject(jProgram);
    out.writeObject(jsProgram);
  }

  @Override
  public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
    javaToJavaScriptMap = (JavaToJavaScriptMap) in.readObject();
    jProgram = (JProgram) in.readObject();
    jsProgram = (JsProgram) in.readObject();
  }


  public TreeLogger getLogger() {
    return logger;
  }

  public void setLogger(TreeLogger logger) {
    this.logger = logger;
  }

  /**
   * Look for a selection property in all property oracles.
   */
  private static boolean findBooleanProperty(PropertyOracle[] propertyOracles, TreeLogger logger,
     String name, String valueToFind, boolean valueIfFound, boolean valueIfNotFound,
     boolean valueIfError) {
    boolean toReturn = valueIfNotFound;
    for (PropertyOracle oracle : propertyOracles) {
      try {
        SelectionProperty property = oracle.getSelectionProperty(logger, name);
        if (valueToFind.equals(property.getCurrentValue())) {
          toReturn = valueIfFound;
          break;
        }
      } catch (BadPropertyValueException e) {
        // unknown value play it safe
        toReturn = valueIfError;
        break;
      }
    }
    return toReturn;
  }

  /**
   * Look for a configuration property in all property oracles.
   */
  private static int findIntegerConfigurationProperty(
      PropertyOracle[] propertyOracles, TreeLogger logger,
      String name, int def) {
    int toReturn = def;
    for (PropertyOracle oracle : propertyOracles) {
      try {
        ConfigurationProperty property = oracle.getConfigurationProperty(name);
        toReturn = Integer.parseInt(property.getValues().get(0));
      } catch (Exception e) {
        break;
      }
    }
    return toReturn;
  }

}
