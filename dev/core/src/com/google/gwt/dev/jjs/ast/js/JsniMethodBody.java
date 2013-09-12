/*
 * Copyright 2007 Google Inc.
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
package com.google.gwt.dev.jjs.ast.js;

import com.google.gwt.dev.jjs.SourceInfo;
import com.google.gwt.dev.jjs.ast.Context;
import com.google.gwt.dev.jjs.ast.JAbstractMethodBody;
import com.google.gwt.dev.jjs.ast.JVisitor;
import com.google.gwt.dev.js.ast.JsContext;
import com.google.gwt.dev.js.ast.JsFunction;
import com.google.gwt.dev.js.ast.JsStringLiteral;
import com.google.gwt.dev.js.ast.JsVisitor;
import com.google.gwt.thirdparty.guava.common.collect.Lists;
import com.google.gwt.thirdparty.guava.common.collect.Sets;

import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * Represents a the body of a method. Can be Java or JSNI.
 */
public class JsniMethodBody extends JAbstractMethodBody {

  private List<JsniClassLiteral> classRefs = Lists.newArrayList();
  private JsFunction jsFunction = null;
  private List<JsniFieldRef> jsniFieldRefs = Lists.newArrayList();
  private List<JsniMethodRef> jsniMethodRefs = Lists.newArrayList();

  private Set<String> stringLiterals = Collections.emptySet();

  public JsniMethodBody(SourceInfo info) {
    super(info);
  }

  /**
   * Adds a reference from this method to a Java class literal.
   */
  public void addClassRef(JsniClassLiteral ref) {
    classRefs.add(ref);
  }

  /**
   * Adds a reference from this method to a Java field.
   */
  public void addJsniRef(JsniFieldRef ref) {
    jsniFieldRefs.add(ref);
  }

  /**
   * Adds a reference from this method to a Java method.
   */
  public void addJsniRef(JsniMethodRef ref) {
    jsniMethodRefs.add(ref);
  }

  /**
   * Return this method's references to Java class literals.
   */
  public List<JsniClassLiteral> getClassRefs() {
    return classRefs;
  }

  public JsFunction getFunc() {
    assert (this.jsFunction != null);
    return jsFunction;
  }

  /**
   * Return this method's references to Java fields.
   */
  public List<JsniFieldRef> getJsniFieldRefs() {
    return jsniFieldRefs;
  }

  /**
   * Return this method's references to Java methods.
   */
  public List<JsniMethodRef> getJsniMethodRefs() {
    return jsniMethodRefs;
  }

  public Set<String> getUsedStrings() {
    return stringLiterals;
  }

  @Override
  public boolean isNative() {
    return true;
  }

  public void setFunc(JsFunction jsFunction) {
    assert (this.jsFunction == null);
    this.jsFunction = jsFunction;
    stringLiterals = Sets.newHashSet();
    class RecordStrings extends JsVisitor {
      @Override
      public void endVisit(JsStringLiteral lit, JsContext ctx) {
        stringLiterals.add(lit.getValue());
      }
    }
    (new RecordStrings()).accept(jsFunction);
  }

  public void traverse(JVisitor visitor, Context ctx) {
    if (visitor.visit(this, ctx)) {
      classRefs = visitor.acceptImmutable(classRefs);
      jsniFieldRefs = visitor.acceptImmutable(jsniFieldRefs);
      jsniMethodRefs = visitor.acceptImmutable(jsniMethodRefs);
    }
    visitor.endVisit(this, ctx);
  }
}
