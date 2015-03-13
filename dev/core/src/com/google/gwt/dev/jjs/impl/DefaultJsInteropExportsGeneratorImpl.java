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
import com.google.gwt.dev.jjs.ast.JDeclaredType;
import com.google.gwt.dev.jjs.ast.JMember;
import com.google.gwt.dev.js.ast.JsExprStmt;
import com.google.gwt.dev.js.ast.JsExpression;
import com.google.gwt.dev.js.ast.JsFunction;
import com.google.gwt.dev.js.ast.JsInvocation;
import com.google.gwt.dev.js.ast.JsName;
import com.google.gwt.dev.js.ast.JsNameRef;
import com.google.gwt.dev.js.ast.JsProgram;
import com.google.gwt.dev.js.ast.JsStatement;
import com.google.gwt.dev.js.ast.JsStringLiteral;

import java.util.List;
import java.util.Map;

/**
 * Responsible for handling @JsExport code generation for non-Closure formatted code.
 *
 * Generally, export of global namespaced members looks like this
 * _ = provide('dotted.namespace')
 * _.memberName = original
 *
 * Essentially members are aliased into a global namespace. Keep in mind that there are no
 * restrictions on the order in which this can occur because of the way the provide() method
 * works.
 */
public class DefaultJsInteropExportsGeneratorImpl extends JsInteropExportsGenerator {

  private String lastExportedNamespace;
  private final List<JsStatement> exportStmts;
  private final JsName globalTemp;
  private final Map<String, JsFunction> indexedFunctions;
  private final Map<HasName, JsName> names;

  DefaultJsInteropExportsGeneratorImpl(JsProgram jsProgram, JsName globalTemp,
      List<JsStatement> exportStmts, Map<String, JsFunction> indexedFunctions,
      Map<HasName, JsName> names) {
    this.globalTemp = globalTemp;
    this.exportStmts = exportStmts;
    this.indexedFunctions = indexedFunctions;
    this.names = names;
  }

  /*
   * Exports a member as
   * _ = provide('foo.bar.ExportNamespace')
   * _.memberName = RHS
   */
  public void exportMember(JDeclaredType x, JMember member, TreeLogger logger) {
    SourceInfo sourceInfo = x.getSourceInfo();
    JsExpression exportRhs = names.get(member).makeRef(member.getSourceInfo());

    String namespace = member.getExportNamespace();
    // _ = JCHSU.provide('foo.bar.Namespace')
    ensureProvideCall(x, namespace, logger);

    // exportedMemberRef is '.memberName' usually
    JsNameRef exportedMemberRef = new JsNameRef(sourceInfo, member.getExportName());

    exportedMemberRef.setQualifier(globalTemp.makeRef(sourceInfo));
    JsExprStmt astStat = new JsExprStmt(sourceInfo, createAssignment(exportedMemberRef, exportRhs));
    exportStmts.add(astStat);
  }

  private void ensureProvideCall(JDeclaredType x, String namespace, TreeLogger logger) {
    SourceInfo info = x.getSourceInfo();
    if (namespace.equals(lastExportedNamespace)) {
      // changes lastExportedNamespace, returns true if it didn't change
      return;
    }
    lastExportedNamespace = namespace;

    JsName provideFunc = indexedFunctions.get("JavaClassHierarchySetupUtil.provide").getName();
    JsNameRef provideFuncRef = provideFunc.makeRef(info);
    JsInvocation provideCall = new JsInvocation(info);
    provideCall.setQualifier(provideFuncRef);
    exportStmts.add(createAssignment(globalTemp.makeRef(info),
        provideCall).makeStmt());
    provideCall.getArguments().add(new JsStringLiteral(info, namespace));
  }

  // non-Closure mode doesn't do anything special to export types
  public boolean exportType(JDeclaredType x, TreeLogger branch) {
    return x.hasAnyExports();
  }
}
