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
import com.google.gwt.dev.js.ast.JsInvocation;
import com.google.gwt.dev.js.ast.JsModVisitor;
import com.google.gwt.dev.js.ast.JsName;
import com.google.gwt.dev.js.ast.JsNameRef;
import com.google.gwt.dev.js.ast.JsObjectLiteral;
import com.google.gwt.dev.js.ast.JsProgram;
import com.google.gwt.dev.js.ast.JsStatement;
import com.google.gwt.dev.js.ast.JsStringLiteral;
import com.google.gwt.dev.js.ast.JsVars;
import com.google.gwt.dev.js.ast.JsVars.JsVar;
import com.google.gwt.dev.util.Util;
import com.google.gwt.thirdparty.guava.common.collect.Lists;
import com.google.gwt.thirdparty.guava.common.collect.Maps;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * A compiler pass that creates a namespace for each Java package
 * with at least one global variable or function.
 *
 * <p>Prerequisite: JsVarRefs must be resolved.</p>
 */
public class JsNamespaceChooser {

  public static void exec(JsProgram program, JavaToJavaScriptMap jjsmap,
      boolean closureCompilerFormatEnabled) {
    new JsNamespaceChooser(program, jjsmap, closureCompilerFormatEnabled).execImpl();
  }

  private final JsProgram program;
  private final JavaToJavaScriptMap jjsmap;
  private boolean closureCompilerFormatEnabled;

  /**
   * The namespaces to be added to the program.
   */
  private final Map<String, JsName> packageToNamespace = Maps.newLinkedHashMap();

  private JsNamespaceChooser(JsProgram program, JavaToJavaScriptMap jjsmap,
      boolean closureCompilerFormatEnabled) {
    this.program = program;
    this.jjsmap = jjsmap;
    this.closureCompilerFormatEnabled = closureCompilerFormatEnabled;
  }

  private void execImpl() {

    // Namespaces that have already been initialized by previously loaded fragments
    Set<String> liveNamespaces = new HashSet<>();

    // process fragment 0 first
    processFragment(0, liveNamespaces);

    // process leftovers fragment if it exists
    if (program.getFragmentCount() > 1) {
      processFragment(program.getFragmentCount() - 1, liveNamespaces);
    }

    // process all exclusive fragments
    // we do it in this order so the liveNamespaces are handled in the correct order
    for (int i = 1; i < program.getFragmentCount() - 1; i++) {
      processFragment(i, liveNamespaces);
    }

    // Second pass: fix all references for moved names.
    new NameFixer().accept(program);
  }

  private void processFragment(int i, Set<String> liveNamespaces) {
    // visit each top-level statement in the program and move it if possible.
    // (This isn't a standard visitor because we don't want to recurse.)
    List<JsStatement> globalStatements = program.getFragment(i).getGlobalBlock().getStatements();
    List<JsStatement> after = Lists.newArrayList();
    for (JsStatement before : globalStatements) {
      if (before instanceof JsExprStmt) {
        final JsExpression exp = ((JsExprStmt) before).getExpression();
        if (exp instanceof JsFunction) {
          JsFunction beforeFunc = (JsFunction) exp;
          final JsExpression expr = visitGlobalFunction(beforeFunc);
          // We don't want to change the JsExprStmt reference, because that will invalidate
          // the JJS Maps which map statements to types. Instead we modify the expression,
          // but leave the previous statement intact.
          if (beforeFunc != expr) {
            new JsModVisitor() {
              @Override
              public void endVisit(JsFunction x, JsContext ctx) {
                if (x == exp) {
                  ctx.replaceMe(expr);
                }
              }
            }.accept(before);
          }
        }
        after.add(before);
      } else if (before instanceof JsVars) {
        for (JsVar var : ((JsVars) before)) {
          JsStatement replacement = visitGlobalVar(var);
          if (replacement != null) {
            after.add(replacement);
          }
        }
      } else {
        after.add(before);
      }
    }

    List<JsStatement> namespaceInitializers = createNamespaceInitializers(
        packageToNamespace.values(), liveNamespaces);

    after.addAll(0, namespaceInitializers);

    globalStatements.clear();
    globalStatements.addAll(after);
  }

  /**
   * Moves a global variable to a namespace if possible.
   * (The references must still be fixed up.)
   * @return the new initializer or null to delete it
   */
  private JsStatement visitGlobalVar(JsVar x) {
    JsName name = x.getName();

    if (!moveName(name)) {
      // We can't move it, but let's put the initializer on a separate line for readability.
      JsVars vars = new JsVars(x.getSourceInfo());
      vars.add(x);
      return vars;
    }

    // Convert the initializer from a var to an assignment.
    JsNameRef newName = name.makeRef(x.getSourceInfo());
    JsExpression init = x.getInitExpr();
    if (init == null) {
      // It's undefined so we don't need to initialize it at all.
      // (The namespace is sufficient.)
      return null;
    }

    JsBinaryOperation assign = new JsBinaryOperation(x.getSourceInfo(),
        JsBinaryOperator.ASG, newName, init);
    return assign.makeStmt();
  }

  /**
   * Moves a global function to a namespace if possible.
   * (References must still be fixed up.)
   * @return the new function definition.
   */
  private JsExpression visitGlobalFunction(JsFunction func) {
    JsName name = func.getName();
    if (name == null || !moveName(name)) {
      return func; // no change
    }

    // Convert the function statement into an assignment taking a named function expression:
    // a.b = function b() { ... }
    // The function also keeps its unqualified name for better stack traces in some browsers.
    // Note: for reserving names, currently we pretend that 'b' is in global scope to avoid
    // any name conflicts. It is actually two different names in two scopes; the 'b' in 'a.b'
    // is in the 'a' namespace scope and the function name is in a separate scope containing
    // just the function. We don't model either scope in the GWT compiler yet.
    JsNameRef newName = name.makeRef(func.getSourceInfo());
    JsBinaryOperation assign =
        new JsBinaryOperation(func.getSourceInfo(), JsBinaryOperator.ASG, newName, func);
    return assign;
  }

  /**
   * Creates a "var = {}" statement for each namespace.
   */
  private List<JsStatement> createNamespaceInitializers(Collection<JsName> namespaces,
      Set<String> liveNamespaces) {
    // Let's list them vertically for readability.
    List<JsStatement> inits = Lists.newArrayList();
    for (JsName name : namespaces) {
      if (liveNamespaces.add(name.getShortIdent())) {
        if (closureCompilerFormatEnabled) {
          // Closure mode, we use goog.provide
          JsNameRef googProvide = JsUtils.createQualifier("goog.provide", SourceOrigin.UNKNOWN);
          JsStringLiteral namespaceLiteral = new JsStringLiteral(SourceOrigin.UNKNOWN,
              name.getShortIdent());
          inits.add(
              new JsInvocation(SourceOrigin.UNKNOWN, googProvide, namespaceLiteral).makeStmt());
        } else {
          JsVar var = new JsVar(SourceOrigin.UNKNOWN, name);
          var.setInitExpr(new JsObjectLiteral(SourceOrigin.UNKNOWN));
          JsVars vars = new JsVars(SourceOrigin.UNKNOWN);
          vars.add(var);
          inits.add(vars);
        }
      }
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
    JDeclaredType type = jjsmap.nameToType(name);
    if (type != null) {
      return findPackage(type);
    }

    return null; // not found
  }

  private String findPackage(JDeclaredType type) {
    String packageName = Util.getPackageName(type.getName());
    // Return null for the default package.
    return packageName.isEmpty() ? "$.g" : packageName;
  }

  /**
   * Find the initials of a package. For example, "java.lang" -> "jl".
   */
  private String initialsForPackage(String packageName) {
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
