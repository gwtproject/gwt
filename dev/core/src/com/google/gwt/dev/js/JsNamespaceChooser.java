/*
 * Copyright 2014 Google Inc.
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
package com.google.gwt.dev.js;

import com.google.gwt.dev.jjs.SourceOrigin;
import com.google.gwt.dev.jjs.ast.JDeclaredType;
import com.google.gwt.dev.jjs.ast.JField;
import com.google.gwt.dev.jjs.ast.JMethod;
import com.google.gwt.dev.jjs.impl.JavaToJavaScriptMap;
import com.google.gwt.dev.js.ast.JsBinaryOperation;
import com.google.gwt.dev.js.ast.JsBinaryOperator;
import com.google.gwt.dev.js.ast.JsContext;
import com.google.gwt.dev.js.ast.JsExprStmt;
import com.google.gwt.dev.js.ast.JsExpression;
import com.google.gwt.dev.js.ast.JsFunction;
import com.google.gwt.dev.js.ast.JsModVisitor;
import com.google.gwt.dev.js.ast.JsName;
import com.google.gwt.dev.js.ast.JsNameRef;
import com.google.gwt.dev.js.ast.JsNullLiteral;
import com.google.gwt.dev.js.ast.JsObjectLiteral;
import com.google.gwt.dev.js.ast.JsProgram;
import com.google.gwt.dev.js.ast.JsStatement;
import com.google.gwt.dev.js.ast.JsVars;
import com.google.gwt.dev.js.ast.JsVars.JsVar;
import com.google.gwt.dev.util.Util;
import com.google.gwt.thirdparty.guava.common.collect.Lists;
import com.google.gwt.thirdparty.guava.common.collect.Maps;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * A compiler pass that creates a namespace for each Java package
 * with at least one global variable or function.
 *
 * <p>Prerequisite: JsVarRefs must be resolved.</p>
 */
public class JsNamespaceChooser {

  public static void exec(JsProgram program, JavaToJavaScriptMap jjsmap) {
    new JsNamespaceChooser(program, jjsmap).execImpl();
  }

  private final JsProgram program;
  private final JavaToJavaScriptMap jjsmap;

  /**
   * The namespaces to be added to the program.
   */
  private final Map<String, JsName> packageToNamespace = Maps.newLinkedHashMap();

  /**
   * The statements that will be in the top-level block after this compiler pass is complete.
   */
  private final List<JsStatement> after = Lists.newArrayList();

  private JsNamespaceChooser(JsProgram program, JavaToJavaScriptMap jjsmap) {
    this.program = program;
    this.jjsmap = jjsmap;
  }

  private void execImpl() {

    // First pass: visit each top-level statement in the program and move it if possible.
    // (This isn't a standard visitor because we don't want to recurse.)

    for (JsStatement before : program.getGlobalBlock().getStatements()) {
      if (before instanceof JsExprStmt) {
        JsExpression exp = ((JsExprStmt)before).getExpression();
        if (exp instanceof JsFunction) {
          visitGlobalFunction((JsFunction) exp);
        } else {
          after.add(before);
        }
      } else if (before instanceof JsVars) {
        for (JsVar var : ((JsVars) before)) {
          visitGlobalVar(var);
        }
      } else {
        after.add(before);
      }
    }

    after.addAll(0, createNamespaceInitializers(packageToNamespace.values()));
    program.getGlobalBlock().replaceAll(after);

    // Second pass: fix all references for moved names.
    new NameFixer().accept(program);
  }

  /**
   * Moves a global variable to a namespace if possible.
   * (The initializer will be updated but references must still be fixed up.)
   */
  private void visitGlobalVar(JsVar x) {
    JsName name = x.getName();

    if (!moveName(name)) {
      // We can't move it, but let's put the initializer on a separate line for readability.
      JsVars vars = new JsVars(x.getSourceInfo());
      vars.add(x);
      after.add(vars);
      return;
    }

    // Convert the initializer from a var to an assignment.
    JsNameRef newName = name.makeRef(x.getSourceInfo());
    JsExpression init = x.getInitExpr();
    if (init == null) {
      init = JsNullLiteral.INSTANCE;
    }
    JsBinaryOperation assign = new JsBinaryOperation(x.getSourceInfo(),
        JsBinaryOperator.ASG, newName, init);
    after.add(assign.makeStmt());
  }

  /**
   * Moves a global function to a namespace if possible.
   * (The definition will be updated but references must still be fixed up.)
   */
  private void visitGlobalFunction(JsFunction func) {
    JsName name = func.getName();
    if (name == null || !moveName(name)) {
      after.add(func.makeStmt()); // no change
      return;
    }

    // Convert the function statement into an assignment taking a named function expression:
    // var a.b = function b() { ... }
    // The function is named for better stack traces in some browsers.
    // Note: for reserving names, currently we pretend that 'b' is in global scope to avoid
    // any name conflicts. It is actually two different names in two scopes; the 'b' in 'a.b'
    // is in the 'a' namespace scope and the function name is in a separate scope containing
    // just the function. We don't model either scope in the GWT compiler yet.
    JsNameRef newName = name.makeRef(func.getSourceInfo());
    JsBinaryOperation assign =
        new JsBinaryOperation(func.getSourceInfo(), JsBinaryOperator.ASG, newName, func);
    after.add(assign.makeStmt());
  }

  /**
   * Creates a "var = {}" statement for each namespace.
   */
  private List<JsStatement> createNamespaceInitializers(Collection<JsName> namespaces) {
    // Let's list them vertically for readability.
    List<JsStatement> inits = Lists.newArrayList();
    for (JsName name : namespaces) {
      JsVar var = new JsVar(SourceOrigin.UNKNOWN, name);
      var.setInitExpr(new JsObjectLiteral(SourceOrigin.UNKNOWN));
      JsVars vars = new JsVars(SourceOrigin.UNKNOWN);
      vars.add(var);
      inits.add(vars);
    }
    return inits;
  }

  /**
   * Attempts to move the given name to a namespace. Returns true if it was changed.
   * Side effects: may set the name's namespace and/or add a new mapping to
   * {@link #packageToNamespace}.
   */
  private boolean moveName(JsName name) {
    if (name.getNamespace() != null) {
      return false; // already in a namespace. (Shouldn't happen.)
    }

    if (!name.isObfuscatable()) {
      return false; // probably a JavaScript name
    }

    String packageName = findPackage(name);
    if (packageName == null) {
      return false; // not compiled from Java
    }

    if (name.getStaticRef() instanceof JsFunction) {
      JsFunction func = (JsFunction) name.getStaticRef();
      if (program.isIndexedFunction(func)) {
        return false; // may be called directly in another pass (for example JsStackEmulator).
      }
    }

    JsName namespace = packageToNamespace.get(packageName);
    if (namespace == null) {
      namespace = program.getScope().declareName(chooseUnusedName(packageName));
      packageToNamespace.put(packageName, namespace);
    }

    name.setNamespace(namespace);
    return true;
  }

  private String chooseUnusedName(String packageName) {
    String initials = initialsForPackage(packageName);
    String candidate = initials;
    int counter = 1;
    while (program.getScope().findExistingName(candidate) != null) {
      counter++;
      candidate = initials + counter;
    }
    return candidate;
  }

  /**
   * Find the Java package name for the given JsName, or null
   * if it couldn't be determined.
   */
  private String findPackage(JsName name) {
    JMethod method = jjsmap.nameToMethod(name);
    if (method != null) {
      return findPackage(method.getEnclosingType());
    }
    JField field = jjsmap.nameToField(name);
    if (field != null) {
      return findPackage(field.getEnclosingType());
    }
    return null; // not found
  }

  private static String findPackage(JDeclaredType type) {
    return Util.getPackageName(type.getName());
  }

  /**
   * Find the initials of a package. For example, "java.lang" -> "jl".
   */
  private static String initialsForPackage(String packageName) {
    StringBuilder result = new StringBuilder();

    int end = packageName.length();
    boolean wasDot = true;
    for (int i = 0; i < end; i++) {
      char c = packageName.charAt(i);
      if (c == '.') {
        wasDot = true;
        continue;
      }
      if (wasDot) {
        result.append(c);
      }
      wasDot = false;
    }

    return result.toString();
  }

  /**
   * A compiler pass that qualifies all moved names with the namespace.
   * name => namespace.name
   */
  private static class NameFixer extends JsModVisitor {

    @Override
    public void endVisit(JsNameRef x, JsContext ctx) {
      if (!x.isLeaf() || x.getQualifier() != null || x.getName() == null) {
        return;
      }

      JsName namespace = x.getName().getNamespace();
      if (namespace == null) {
        return;
      }

      x.setQualifier(new JsNameRef(x.getSourceInfo(), namespace));
      didChange = true;
    }
  }
}
