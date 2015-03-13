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
import com.google.gwt.dev.js.JsUtils;
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
 * Responsible for handling @JsExport code generation for Closure formatted code.
 * <p>
 * In closure formatted mode, there are additional restrictions due to the way goog.provide()
 * works:
 * <p>
 * 1) you can't goog.provide something more than once
 * 2) enclosing namespaces must be setup and handled before enclosed namespaces
 * 3) if the exported namespace is a @JsType with methods, than the namespace will need to have
 * a function assigned to it, instead of an object literal returned by goog.provide.
 * <p>
 * In general, this implies code like the following:
 * <p>
 * goog.provide('dotted.parent.namespace')
 * dotted.parent.namespace = function() {} or constructor (if one is exported)
 * dotted.parent.namespace.member = blah;
 * goog.provide('dotted.parent.namespace.enclosed')
 * dotted.parent.namespace.enclosed = function() {} or constructor
 * dotted.parent.namespace.enclosed.member = blah;
 * <p>
 * Any exported constructors will show up twice, because the code is simplified, but it is
 * optimized away, e.g.
 * dotted.parent.namespace.enclosed = ctor1
 * dotted.parent.namespace.enclosed.JsExportNameOfCtor = ctor1
 * <p>
 * Renaming exported constructors just shows up as these unused names which are pruned, because
 * in closure mode, there's only one constructor, and it has the same name as the namespace
 * (the Class name)
 * <p>
 * The first exported ctor is always picked per class, for Closure code there should be only
 * one, but we emit a warning, not an error for this.
 */
public class ClosureJsInteropExportsGeneratorImpl extends JsInteropExportsGenerator {

  private final List<JsStatement> exportStmts;
  private final JsProgram jsProgram;
  private final Map<HasName, JsName> names;
  private final Set<String> providedNamespaces = new HashSet<String>();

  ClosureJsInteropExportsGeneratorImpl(JsProgram jsProgram, JsName globalTemp,
      List<JsStatement> exportStmts, Map<String, JsFunction> indexedFunctions,
      Map<HasName, JsName> names) {
    this.jsProgram = jsProgram;
    this.exportStmts = exportStmts;
    this.names = names;
  }

  /*
   * Exports a member as goog.provide('foo.bar.ClassSimpleName')
   * foo.bar.ClassSimpleName.memberName = RHS
   */
  public void exportMember(JDeclaredType x, JMember member, TreeLogger logger) {
    SourceInfo sourceInfo = x.getSourceInfo();
    JsExpression exportRhs = names.get(member).makeRef(member.getSourceInfo());

    if (member instanceof JConstructor
        && x.getQualifiedExportName().equals(member.getQualifiedExportName())) {
      // primary constructor is handled by exportType, don't export it here
      return;
    }

    String namespace = member.getExportNamespace();
    // goog.provide('foo.bar.Namespace'), in most cases, in Closure code, will be qualified typename
    ensureProvideCall(x, namespace, logger);

    // exportedMemberRef is '.memberName' usually
    JsNameRef exportedMemberRef = new JsNameRef(sourceInfo, member.getExportName());

    exportedMemberRef.setQualifier(createExportQualifier(namespace, sourceInfo));
    JsExprStmt astStat = new JsExprStmt(sourceInfo, JsInteropExportsGenerator.createAssignment(
        exportedMemberRef, exportRhs));
    exportStmts.add(astStat);
  }

  private void ensureProvideCall(JDeclaredType x, String namespace, TreeLogger logger) {
    SourceInfo info = x.getSourceInfo();

    if (!providedNamespaces.add(namespace) || namespace.isEmpty()) {
      // already provided, in closure mode, it's an error to provide twice
      /// don't goog.provide '' namespace
      return;
    }

    /*
     * Closure formatted mode depends on JsDoc declarations being appended later
     * (by linker or build process), and these declarations depend on js statements like:
     *
     * /* @returns {number} * / Foo.prototype.method;
     *
     *  If a @JsType is exported, but no constructors are, @JsDoc type declarations
     * added by the linker will fail in uncompiled mode because a declaration like
     * will say that 'Foo.prototype' is undefined, even though the goog.provide('Foo') statement
     * exists. In compiled JS mode, JsDoc forward declarations are stripped so there's no error.
     *
     * Here we ensure that for each goog.provide() there's a function assigned to the namespace
     * instead of just an empty object literal. TODO (cromwellian): remove this and replace with
     * @JsDoc generation within GWT.
     *
     * This code will go away once the ClosureSingleScriptLinker is eliminated and we
     * start emitting JsDoc annotations directly in the GenJsAst phase.
     *
     */
    if (x.getEnclosingType() != null) {
      // enclosing namespaces must be goog.provided before enclosed, otherwise it can be
      // a hard JSCompiler error like 'a.b.c defined before its owner a.b'
      ensureProvideCall(x.getEnclosingType(), x.getEnclosingType().getQualifiedExportName(),
          logger);
    }

    JsNameRef provideFuncRef = JsUtils.createQualifier("goog.provide", info, false);
    JsInvocation provideCall = new JsInvocation(info);
    provideCall.setQualifier(provideFuncRef);
    provideCall.getArguments().add(new JsStringLiteral(info, namespace));
    exportStmts.add(provideCall.makeStmt());

    if (x instanceof JClassType) {
      JsName ctor = getSingleExportedCtorOrPrototypeCtor(x);
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
  }

  /**
   * Ensures that each exported type has a namespace provided with a either the primary default
   * constructor assigned to it (@JsExport ctor without a name argument), the anonymous factory
   * ctor returned by defineClass(), or in the case of interfaces, an empty function literal.
   * The reason why functions are needed to be assigned to the namespace is so that
   * the <code>namespace.prototype</code> will need to exist for @JsDoc declarations that refer to
   * the virtual methods of the class.
   */
  @Override
  public boolean exportType(JDeclaredType x, TreeLogger branch) {
    boolean hasAnyExports = x.hasAnyExports();
    if (x.isJsType() || hasAnyExports) {
      // in Closure code, even if a class doesn't have exports,
      // it still needs a goog.provide for type declarations later
      ensureProvideCall(x, x.getQualifiedExportName(), branch);
    }
    return hasAnyExports;
  }

  private JsFunction createEmptyFunctionLiteral(SourceInfo info) {
    JsFunction func = new JsFunction(info, jsProgram.getScope());
    func.setBody(new JsBlock(info));
    return func;
  }

  private JsName getSingleExportedCtorOrPrototypeCtor(final JDeclaredType x) {
    if (x.getMethods() == null) {
      return null;
    }

    FluentIterable<JMethod> ctors = FluentIterable.from(x.getMethods()).filter(
        new Predicate<JMethod>() {
          @Override
          public boolean apply(JMethod jMethod) {
            return jMethod instanceof JConstructor && jMethod.isExported()
                // find the @JsExport with no argument
                && jMethod.getExportName().equals(x.getSimpleName());
          }
        });
    return names.get(Iterables.getFirst(ctors, x));
  }

  /*
   * Return either fully qualified namespace in closure format.
   */
  private JsExpression createExportQualifier(String namespace, SourceInfo sourceInfo) {
    return JsUtils.createQualifier(namespace.isEmpty() ? "window" : namespace, sourceInfo, false);
  }
}
