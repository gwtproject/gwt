/*
 * Copyright 2015 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package com.google.gwt.dev.jjs.impl;

import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.dev.jjs.SourceInfo;
import com.google.gwt.dev.jjs.ast.HasName;
import com.google.gwt.dev.jjs.ast.JClassType;
import com.google.gwt.dev.jjs.ast.JConstructor;
import com.google.gwt.dev.jjs.ast.JDeclaredType;
import com.google.gwt.dev.jjs.ast.JMember;
import com.google.gwt.dev.jjs.ast.JMethod;
import com.google.gwt.dev.js.ast.JsBinaryOperation;
import com.google.gwt.dev.js.ast.JsBinaryOperator;
import com.google.gwt.dev.js.ast.JsBlock;
import com.google.gwt.dev.js.ast.JsExprStmt;
import com.google.gwt.dev.js.ast.JsExpression;
import com.google.gwt.dev.js.ast.JsFunction;
import com.google.gwt.dev.js.ast.JsInvocation;
import com.google.gwt.dev.js.ast.JsName;
import com.google.gwt.dev.js.ast.JsNameRef;
import com.google.gwt.dev.js.ast.JsProgram;
import com.google.gwt.dev.js.ast.JsStatement;
import com.google.gwt.dev.js.ast.JsStringLiteral;
import com.google.gwt.thirdparty.guava.common.base.Predicate;
import com.google.gwt.thirdparty.guava.common.collect.FluentIterable;
import com.google.gwt.thirdparty.guava.common.collect.Iterables;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Responsible for handling @JsExport code generation for non-Closure formatted code.
 */
public class JsInteropExportsGenerator {

  protected String lastExportedNamespace;
  protected final List<JsStatement> exportStmts;
  protected final JsName globalTemp;
  protected final JsProgram jsProgram;
  protected final Map<String, JsFunction> indexedFunctions;
  protected final Map<HasName, JsName> names;

  public static JsInteropExportsGenerator create(boolean closureCompilerFormatEnabled, JsProgram
      jsProgram, JsName globalTemp, List<JsStatement> exportStmts,
      Map<String, JsFunction> indexedFunctions,
      Map<HasName, JsName> names) {
    if (closureCompilerFormatEnabled) {
      return new ClosureJsInteropExportsGenerator(jsProgram, globalTemp, exportStmts,
          indexedFunctions,
          names);
    } else {
      return new JsInteropExportsGenerator(jsProgram, globalTemp, exportStmts, indexedFunctions,
          names);
    }
  }

  protected JsInteropExportsGenerator(JsProgram jsProgram, JsName globalTemp,
      List<JsStatement> exportStmts, Map<String, JsFunction> indexedFunctions,
      Map<HasName, JsName> names) {
    this.jsProgram = jsProgram;
    this.globalTemp = globalTemp;
    this.exportStmts = exportStmts;
    this.indexedFunctions = indexedFunctions;
    this.names = names;
  }

  /*
   * Exports a member as
   * goog.provide('foo.bar.ClassCtorNameOrClassSimpleName')
   * foo.bar.ClassCtorName.prototype.memberName = RHS
   */
  void exportMember(JDeclaredType x, JMember member, TreeLogger logger) {
    SourceInfo sourceInfo = x.getSourceInfo();
    JsExpression exportRhs = names.get(member).makeRef(member.getSourceInfo());

    String namespace = getProvideNamespace(x, member);
    // {goog.}provide('foo.bar.Namespace')
    lastExportedNamespace = ensureProvideCall(x, namespace, logger);

    // exportedMemberRef is '.memberName' usually
    JsNameRef exportedMemberRef = new JsNameRef(sourceInfo, member.getExportName());

    exportedMemberRef.setQualifier(createExportQualifier(namespace, sourceInfo));
    JsExprStmt astStat = new JsExprStmt(sourceInfo, createAssignment(exportedMemberRef, exportRhs));
    exportStmts.add(astStat);
  }

  String ensureProvideCall(JDeclaredType x, String namespace, TreeLogger logger) {
    JsInvocation provideCall;
    SourceInfo info = x.getSourceInfo();
    if (tryChangeNamespace(namespace)) {
      // changes lastExportedNamespace, returns true if it didn't change
      return namespace;
    }

    JsName provideFunc = indexedFunctions.get("JavaClassHierarchySetupUtil.provide").getName();
    JsNameRef provideFuncRef = provideFunc.makeRef(info);
    provideCall = new JsInvocation(info);
    provideCall.setQualifier(provideFuncRef);
    exportStmts.add(createAssignment(globalTemp.makeRef(info), provideCall).makeStmt());
    provideCall.getArguments().add(new JsStringLiteral(info, namespace));

    return lastExportedNamespace;
  }

  boolean tryChangeNamespace(String namespace) {
    if (namespace.equals(lastExportedNamespace)) {
      return true;
    }

    lastExportedNamespace = namespace;
    return false;
  }

  protected String getProvideNamespace(JDeclaredType x, JMember member) {
    return member.getExportNamespace();
  }

  /*
   * Return globalTemp (_).
   */
  protected JsExpression createExportQualifier(String namespace, SourceInfo sourceInfo) {
    return globalTemp.makeRef(sourceInfo);
  }

  /**
   * Given a string namespace qualifier such as 'foo.bar.Baz', creates a chain of JsNameRef's
   * representing this namespace, optionally prefixed by '$wnd' qualifier.
   */
  JsNameRef createQualifier(String namespace, SourceInfo sourceInfo, boolean qualifyWithWnd) {
    JsNameRef ref = null;
    if (namespace.isEmpty() || qualifyWithWnd) {
      ref = new JsNameRef(sourceInfo, "$wnd");
    }

    for (String part : namespace.split("\\.")) {
      JsNameRef newRef = new JsNameRef(sourceInfo, part);
      if (ref != null) {
        newRef.setQualifier(ref);
      }
      ref = newRef;
    }
    return ref;
  }

  protected JsExpression createAssignment(JsExpression lhs, JsExpression rhs) {
    return new JsBinaryOperation(lhs.getSourceInfo(), JsBinaryOperator.ASG, lhs, rhs);
  }

  /**
   * An export generator that uses goog.provide() for namespaces, uses fully qualified
   * assignment statements everywhere instead of '_', and assigns functions as the namespace
   * object instead of object literals. Implements other restrictions such as a namespace
   * cannot be goog.provide'd more than once, and that an enclosing namespace must always be
   * setup before the enclosed namespace.
   */
  static class ClosureJsInteropExportsGenerator extends JsInteropExportsGenerator {

    protected final Set<String> providedNamespaces = new HashSet<String>();

    protected ClosureJsInteropExportsGenerator(JsProgram jsProgram, JsName globalTemp,
        List<JsStatement> exportStmts,
        Map<String, JsFunction> indexedFunctions,
        Map<HasName, JsName> names) {
      super(jsProgram, globalTemp, exportStmts, indexedFunctions, names);
    }

    @Override
    String ensureProvideCall(JDeclaredType x, String namespace, TreeLogger logger) {
      JsInvocation provideCall;
      SourceInfo info = x.getSourceInfo();
      if (tryChangeNamespace(namespace)) {
        // changes lastExportedNamespace, returns true if it didn't change
        return namespace;
      }

      if (!providedNamespaces.add(namespace)) {
        // already provided, in closure mode, it's an error to provide twice
        return lastExportedNamespace;
      }

      /*
       * Closure formatted mode depends on JsDoc declarations being appended later
       * (by linker or build process), and these declarionaions depend on js statements like:
       *
       * /* @returns {number} * / Foo.prototype.method;
       *
       *  If a @JsType is exported, but no constructors are, @JsDoc type declarations
       * added by the linker will fail in uncompiled mode because a declaration like
       * will say that 'Foo.prototype' is undefined, even though the goog.provide('Foo') statement
       * exists. In compiled JS mode, JsDoc forward declarations are stripped so there's no error.
       *
       * Here we ensure that for each goog.provide() there's a function assigned to the namespace
       * instead of just an empty object literal.
       *
       * This code will go away once the ClosureSingleScriptLinker is eliminated and we
       * start emitting JsDoc annotations directly in the GenJsAst phase.
       *
       * TODO (cromwellian): remove this and replace with @JsDoc generation within GWT
       */
      if (x.getEnclosingType() != null) {
        // enclosing namespaces must be goog.provided before enclosed, otherwise it can be
        // a hard JSCompiler error like 'a.b.c defined before its owner a.b'
        ensureProvideCall(x.getEnclosingType(), x.getEnclosingType().getQualifiedExportName(),
            logger);
      }

      JsNameRef provideFuncRef = new JsNameRef(info, "provide");
      provideFuncRef.setQualifier(new JsNameRef(info, "goog"));
      provideCall = new JsInvocation(info);
      provideCall.setQualifier(provideFuncRef);
      exportStmts.add(provideCall.makeStmt());

      if (x instanceof JClassType) {
        JsName ctor = getSingleExportedCtorOrPrototypeCtor(x, logger);
        // Generate foo.Bar = FooCtor  (the name of the variable assigned to defineClass's return)
        // or an object literal if the class happens to be a JSO (enclosing say, an Enum)
        JsExpression ctorExpr = ctor == null ? createEmptyFunctionLiteral(info) :
            ctor.makeRef(info);
        exportStmts.add(createAssignment(createExportQualifier(namespace,
            info), ctorExpr).makeStmt());
      } else {
        // if JInterface with exports, output foo.Bar = function() {} for interface as placeholder
        JsFunction func = createEmptyFunctionLiteral(info);
        exportStmts.add(createAssignment(createExportQualifier(namespace, info), func)
            .makeStmt());
      }

      provideCall.getArguments().add(new JsStringLiteral(info, namespace));

      return lastExportedNamespace;
    }

    JsFunction createEmptyFunctionLiteral(SourceInfo info) {
      JsFunction func = new JsFunction(info, jsProgram.getScope());
      func.setBody(new JsBlock(info));
      return func;
    }

    JsName getSingleExportedCtorOrPrototypeCtor(JDeclaredType x, TreeLogger branch) {
      if (x.getMethods() == null) {
        return null;
      }

      FluentIterable<JMethod> ctors = FluentIterable.from(x.getMethods()).filter(
          new Predicate<JMethod>() {
            @Override
            public boolean apply(JMethod jMethod) {
              return jMethod instanceof JConstructor && jMethod.isExported();
            }
          });
      return names.get(Iterables.getFirst(ctors, x));
    }

    /*
     * For closure format, the namespace is equivalent to the class or class ctor. Exports
     * hang off the ctor, and namespaces cannot be provide()'d more than once. We do not allow
     * member fields to alter their namespaces, they are bound to the enclosing type's ctor
     * namespace.
     */
    protected String getProvideNamespace(JDeclaredType x, JMember member) {
      return x.getQualifiedExportName();
    }

    /*
     * Return either fully qualified namespace in closure format.
     */
    protected JsExpression createExportQualifier(String namespace, SourceInfo sourceInfo) {
      return createQualifier(namespace, sourceInfo, false);
    }
  }
}
