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

import static com.google.gwt.dev.js.JsUtils.createAssignment;

import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.linker.impl.StandardSymbolData;
import com.google.gwt.dev.CompilerContext;
import com.google.gwt.dev.MinimalRebuildCache;
import com.google.gwt.dev.PrecompileTaskOptions;
import com.google.gwt.dev.cfg.PermutationProperties;
import com.google.gwt.dev.common.InliningMode;
import com.google.gwt.dev.jjs.HasSourceInfo;
import com.google.gwt.dev.jjs.InternalCompilerException;
import com.google.gwt.dev.jjs.SourceInfo;
import com.google.gwt.dev.jjs.SourceOrigin;
import com.google.gwt.dev.jjs.ast.Context;
import com.google.gwt.dev.jjs.ast.HasEnclosingType;
import com.google.gwt.dev.jjs.ast.HasName;
import com.google.gwt.dev.jjs.ast.JAbstractMethodBody;
import com.google.gwt.dev.jjs.ast.JArrayLength;
import com.google.gwt.dev.jjs.ast.JArrayRef;
import com.google.gwt.dev.jjs.ast.JArrayType;
import com.google.gwt.dev.jjs.ast.JBinaryOperation;
import com.google.gwt.dev.jjs.ast.JBinaryOperator;
import com.google.gwt.dev.jjs.ast.JBlock;
import com.google.gwt.dev.jjs.ast.JBreakStatement;
import com.google.gwt.dev.jjs.ast.JCaseStatement;
import com.google.gwt.dev.jjs.ast.JCastMap;
import com.google.gwt.dev.jjs.ast.JCastOperation;
import com.google.gwt.dev.jjs.ast.JClassLiteral;
import com.google.gwt.dev.jjs.ast.JClassType;
import com.google.gwt.dev.jjs.ast.JConditional;
import com.google.gwt.dev.jjs.ast.JConstructor;
import com.google.gwt.dev.jjs.ast.JContinueStatement;
import com.google.gwt.dev.jjs.ast.JDeclarationStatement;
import com.google.gwt.dev.jjs.ast.JDeclaredType;
import com.google.gwt.dev.jjs.ast.JDoStatement;
import com.google.gwt.dev.jjs.ast.JExpression;
import com.google.gwt.dev.jjs.ast.JExpressionStatement;
import com.google.gwt.dev.jjs.ast.JField;
import com.google.gwt.dev.jjs.ast.JFieldRef;
import com.google.gwt.dev.jjs.ast.JForStatement;
import com.google.gwt.dev.jjs.ast.JIfStatement;
import com.google.gwt.dev.jjs.ast.JInterfaceType;
import com.google.gwt.dev.jjs.ast.JLabel;
import com.google.gwt.dev.jjs.ast.JLabeledStatement;
import com.google.gwt.dev.jjs.ast.JLiteral;
import com.google.gwt.dev.jjs.ast.JLocal;
import com.google.gwt.dev.jjs.ast.JLocalRef;
import com.google.gwt.dev.jjs.ast.JMember;
import com.google.gwt.dev.jjs.ast.JMethod;
import com.google.gwt.dev.jjs.ast.JMethodBody;
import com.google.gwt.dev.jjs.ast.JMethodCall;
import com.google.gwt.dev.jjs.ast.JNameOf;
import com.google.gwt.dev.jjs.ast.JNewInstance;
import com.google.gwt.dev.jjs.ast.JNode;
import com.google.gwt.dev.jjs.ast.JNullLiteral;
import com.google.gwt.dev.jjs.ast.JNumericEntry;
import com.google.gwt.dev.jjs.ast.JParameter;
import com.google.gwt.dev.jjs.ast.JParameterRef;
import com.google.gwt.dev.jjs.ast.JPermutationDependentValue;
import com.google.gwt.dev.jjs.ast.JPostfixOperation;
import com.google.gwt.dev.jjs.ast.JPrefixOperation;
import com.google.gwt.dev.jjs.ast.JPrimitiveType;
import com.google.gwt.dev.jjs.ast.JProgram;
import com.google.gwt.dev.jjs.ast.JReferenceType;
import com.google.gwt.dev.jjs.ast.JReturnStatement;
import com.google.gwt.dev.jjs.ast.JRunAsync;
import com.google.gwt.dev.jjs.ast.JStatement;
import com.google.gwt.dev.jjs.ast.JSwitchStatement;
import com.google.gwt.dev.jjs.ast.JThisRef;
import com.google.gwt.dev.jjs.ast.JThrowStatement;
import com.google.gwt.dev.jjs.ast.JTransformer;
import com.google.gwt.dev.jjs.ast.JTryStatement;
import com.google.gwt.dev.jjs.ast.JType;
import com.google.gwt.dev.jjs.ast.JUnaryOperator;
import com.google.gwt.dev.jjs.ast.JVariable;
import com.google.gwt.dev.jjs.ast.JVisitor;
import com.google.gwt.dev.jjs.ast.JWhileStatement;
import com.google.gwt.dev.jjs.ast.js.JDebuggerStatement;
import com.google.gwt.dev.jjs.ast.js.JMultiExpression;
import com.google.gwt.dev.jjs.ast.js.JsniClassLiteral;
import com.google.gwt.dev.jjs.ast.js.JsniFieldRef;
import com.google.gwt.dev.jjs.ast.js.JsniMethodBody;
import com.google.gwt.dev.jjs.ast.js.JsniMethodRef;
import com.google.gwt.dev.jjs.ast.js.JsonArray;
import com.google.gwt.dev.jjs.impl.ResolveRuntimeTypeReferences.TypeMapper;
import com.google.gwt.dev.js.JsStackEmulator;
import com.google.gwt.dev.js.JsUtils;
import com.google.gwt.dev.js.ast.JsArrayAccess;
import com.google.gwt.dev.js.ast.JsArrayLiteral;
import com.google.gwt.dev.js.ast.JsBinaryOperation;
import com.google.gwt.dev.js.ast.JsBinaryOperator;
import com.google.gwt.dev.js.ast.JsBlock;
import com.google.gwt.dev.js.ast.JsBreak;
import com.google.gwt.dev.js.ast.JsCase;
import com.google.gwt.dev.js.ast.JsCatch;
import com.google.gwt.dev.js.ast.JsConditional;
import com.google.gwt.dev.js.ast.JsContext;
import com.google.gwt.dev.js.ast.JsContinue;
import com.google.gwt.dev.js.ast.JsDebugger;
import com.google.gwt.dev.js.ast.JsDefault;
import com.google.gwt.dev.js.ast.JsDoWhile;
import com.google.gwt.dev.js.ast.JsEmpty;
import com.google.gwt.dev.js.ast.JsExprStmt;
import com.google.gwt.dev.js.ast.JsExpression;
import com.google.gwt.dev.js.ast.JsFor;
import com.google.gwt.dev.js.ast.JsFunction;
import com.google.gwt.dev.js.ast.JsIf;
import com.google.gwt.dev.js.ast.JsInvocation;
import com.google.gwt.dev.js.ast.JsLabel;
import com.google.gwt.dev.js.ast.JsLiteral;
import com.google.gwt.dev.js.ast.JsModVisitor;
import com.google.gwt.dev.js.ast.JsName;
import com.google.gwt.dev.js.ast.JsNameOf;
import com.google.gwt.dev.js.ast.JsNameRef;
import com.google.gwt.dev.js.ast.JsNew;
import com.google.gwt.dev.js.ast.JsNode;
import com.google.gwt.dev.js.ast.JsNormalScope;
import com.google.gwt.dev.js.ast.JsNullLiteral;
import com.google.gwt.dev.js.ast.JsNumberLiteral;
import com.google.gwt.dev.js.ast.JsNumericEntry;
import com.google.gwt.dev.js.ast.JsObjectLiteral;
import com.google.gwt.dev.js.ast.JsParameter;
import com.google.gwt.dev.js.ast.JsPositionMarker;
import com.google.gwt.dev.js.ast.JsPositionMarker.Type;
import com.google.gwt.dev.js.ast.JsPostfixOperation;
import com.google.gwt.dev.js.ast.JsPrefixOperation;
import com.google.gwt.dev.js.ast.JsProgram;
import com.google.gwt.dev.js.ast.JsPropertyInitializer;
import com.google.gwt.dev.js.ast.JsReturn;
import com.google.gwt.dev.js.ast.JsRootScope;
import com.google.gwt.dev.js.ast.JsScope;
import com.google.gwt.dev.js.ast.JsStatement;
import com.google.gwt.dev.js.ast.JsStringLiteral;
import com.google.gwt.dev.js.ast.JsSwitch;
import com.google.gwt.dev.js.ast.JsSwitchMember;
import com.google.gwt.dev.js.ast.JsThisRef;
import com.google.gwt.dev.js.ast.JsThrow;
import com.google.gwt.dev.js.ast.JsTry;
import com.google.gwt.dev.js.ast.JsUnaryOperator;
import com.google.gwt.dev.js.ast.JsVars;
import com.google.gwt.dev.js.ast.JsVars.JsVar;
import com.google.gwt.dev.js.ast.JsWhile;
import com.google.gwt.dev.util.Pair;
import com.google.gwt.dev.util.StringInterner;
import com.google.gwt.dev.util.arg.OptionMethodNameDisplayMode;
import com.google.gwt.dev.util.arg.OptionOptimize;
import com.google.gwt.dev.util.collect.Stack;
import com.google.gwt.dev.util.log.speedtracer.CompilerEventType;
import com.google.gwt.dev.util.log.speedtracer.SpeedTracerLogger;
import com.google.gwt.dev.util.log.speedtracer.SpeedTracerLogger.Event;
import com.google.gwt.thirdparty.guava.common.base.Function;
import com.google.gwt.thirdparty.guava.common.base.Joiner;
import com.google.gwt.thirdparty.guava.common.base.Predicate;
import com.google.gwt.thirdparty.guava.common.base.Predicates;
import com.google.gwt.thirdparty.guava.common.collect.FluentIterable;
import com.google.gwt.thirdparty.guava.common.collect.ImmutableList;
import com.google.gwt.thirdparty.guava.common.collect.ImmutableSortedSet;
import com.google.gwt.thirdparty.guava.common.collect.Iterables;
import com.google.gwt.thirdparty.guava.common.collect.Lists;
import com.google.gwt.thirdparty.guava.common.collect.Maps;
import com.google.gwt.thirdparty.guava.common.collect.Sets;

import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeMap;

/**
 * Creates a JavaScript AST from a <code>JProgram</code> node.
 */
public class GenerateJavaScriptAST {

  /**
   * Finds the nodes that are targets of JNameOf so that a name is assigned to them.
   */
  private class FindNameOfTargets extends JVisitor {
    @Override
    public void endVisit(JNameOf x, Context ctx) {
      nameOfTargets.add(x.getNode());
    }
  }

  private class CreateNamesAndScopesVisitor extends JVisitor {

    /**
     * Cache of computed Java source file names to URI strings for symbol
     * export. By using a cache we also ensure the miminum number of String
     * instances are serialized.
     */
    private final Map<String, String> fileNameToUriString = Maps.newHashMap();

    private final Stack<JsScope> scopeStack = new Stack<JsScope>();

    @Override
    public boolean visit(JProgram x, Context ctx) {
      // Scopes and name objects need to be calculated within all types, even reference-only ones.
      // This information is used to be able to detect and avoid name collisions during pretty or
      // obfuscated JS variable name generation.
      x.visitAllTypes(this);
      return false;
    }

    @Override
    public void endVisit(JArrayType x, Context ctx) {
      JsName name = topScope.declareName(x.getName());
      names.put(x, name);
      recordSymbol(x, name);
    }

    @Override
    public void endVisit(JClassType x, Context ctx) {
      scopeStack.pop();
    }

    @Override
    public void endVisit(JField x, Context ctx) {
      JsName jsName;
      if (x.isStatic()) {
        jsName = topScope.declareName(mangleName(x), x.getName());
      } else {
        jsName =
            x.isJsProperty()
                ? scopeStack.peek().declareUnobfuscatableName(x.getJsName())
                : scopeStack.peek().declareName(mangleName(x), x.getName());
      }
      names.put(x, jsName);
      if (program.getIndexedFields().contains(x)) {
        indexedFields.put(x.getEnclosingType().getShortName() + "." + x.getName(), jsName);
      }
      recordSymbol(x, jsName);
    }

    @Override
    public void endVisit(JInterfaceType x, Context ctx) {
      scopeStack.pop();
    }

    @Override
    public void endVisit(JLabel x, Context ctx) {
      if (names.get(x) != null) {
        return;
      }
      names.put(x, scopeStack.peek().declareName(x.getName()));
    }

    @Override
    public void endVisit(JLocal x, Context ctx) {
      // locals can conflict, that's okay just reuse the same variable
      JsScope scope = scopeStack.peek();
      JsName jsName = scope.declareName(x.getName());
      names.put(x, jsName);
    }

    @Override
    public void endVisit(JMethod x, Context ctx) {
      if (shouldNotEmitMethodImplementation(x)) {
        return;
      }
      scopeStack.pop();
    }

    @Override
    public void endVisit(JParameter x, Context ctx) {
      names.put(x, scopeStack.peek().declareName(x.getName()));
    }

    @Override
    public void endVisit(JProgram x, Context ctx) {
      /*
       * put the null method and field into objectScope since they can be
       * referenced as instance on null-types (as determined by type flow)
       */
      JMethod nullMethod = x.getNullMethod();
      polymorphicNames.put(nullMethod, objectScope.declareName("$_nullMethod"));
      JField nullField = x.getNullField();
      JsName nullFieldName = objectScope.declareName("$_nullField");
      names.put(nullField, nullFieldName);

      /*
       * Create names for instantiable array types since JProgram.traverse()
       * doesn't iterate over them.
       */
      for (JArrayType arrayType : program.getAllArrayTypes()) {
        if (program.typeOracle.isInstantiatedType(arrayType)) {
          accept(arrayType);
        }
      }
    }

    @Override
    public boolean visit(JClassType x, Context ctx) {
      // have I already been visited as a super type?
      JsScope myScope = classScopes.get(x);
      if (myScope != null) {
        scopeStack.push(myScope);
        return false;
      }

      // My seed function name
      JsName jsName = topScope.declareName(JjsUtils.mangledNameString(x), x.getShortName());
      names.put(x, jsName);
      recordSymbol(x, jsName);

      // My class scope
      if (x.getSuperClass() == null) {
        myScope = objectScope;
      } else {
        JsScope parentScope = classScopes.get(x.getSuperClass());
        // Run my superclass first!
        if (parentScope == null) {
          accept(x.getSuperClass());
        }
        parentScope = classScopes.get(x.getSuperClass());
        assert (parentScope != null);
        /*
         * WEIRD: we wedge the global interface scope in between object and all
         * of its subclasses; this ensures that interface method names trump all
         * (except Object method names)
         */
        if (parentScope == objectScope) {
          parentScope = interfaceScope;
        }
        myScope = new JsNormalScope(parentScope, "class " + x.getShortName());
      }
      classScopes.put(x, myScope);

      scopeStack.push(myScope);
      return true;
    }

    @Override
    public boolean visit(JInterfaceType x, Context ctx) {
      // interfaces have no name at run time
      scopeStack.push(interfaceScope);
      return true;
    }

    @Override
    public boolean visit(JMethod x, Context ctx) {
      // my polymorphic name
      String name = x.getName();
      if (x.needsVtable()) {
        if (polymorphicNames.get(x) == null) {
          JsName polyName;
          if (x.isPrivate()) {
            polyName = interfaceScope.declareName(mangleNameForPrivatePoly(x), name);
          } else if (x.isPackagePrivate()) {
            polyName = interfaceScope.declareName(mangleNameForPackagePrivatePoly(x), name);
          } else {
            boolean isJsMethod = x.isOrOverridesJsMethod() && !x.isJsPropertyAccessor();
            polyName =
                isJsMethod
                    ? interfaceScope.declareUnobfuscatableName(x.getJsName())
                    : interfaceScope.declareName(mangleNameForPoly(x), name);
          }
          polymorphicNames.put(x, polyName);
        }
      }

      if (shouldNotEmitMethodImplementation(x)) {
        return false;
      }

      // my global name
      JsName globalName = null;
      assert x.getEnclosingType() != null;
      String mangleName = mangleNameForGlobal(x);

      if (JProgram.isClinit(x)) {
        name = name + "_" + x.getEnclosingType().getShortName();
      }

      /*
       * Only allocate a name for a function if it is native, not polymorphic,
       * is a JNameOf target or stack-stripping is disabled.
       */
      if (!stripStack || !polymorphicNames.containsKey(x) || x.isNative()
          || nameOfTargets.contains(x)) {
        globalName = topScope.declareName(mangleName, name);
        names.put(x, globalName);
        recordSymbol(x, globalName);
      }
      JsFunction function;
      if (x.isNative()) {
        // set the global name of the JSNI peer
        JsniMethodBody body = (JsniMethodBody) x.getBody();
        function = body.getFunc();
        function.setName(globalName);
      } else {
        /*
         * It would be more correct here to check for an inline assignment, such
         * as var foo = function blah() {} and introduce a separate scope for
         * the function's name according to EcmaScript-262, but this would mess
         * up stack traces by allowing two inner scope function names to
         * obfuscate to the same identifier, making function names no longer a
         * 1:1 mapping to obfuscated symbols. Leaving them in global scope
         * causes no harm.
         */
        function = new JsFunction(x.getSourceInfo(), topScope, globalName, !x.isJsNative());
      }

      jsFunctionsByJavaMethodBody.put(x.getBody(), function);
      scopeStack.push(function.getScope());

      if (program.getIndexedMethods().contains(x)) {
        indexedFunctions.put(x.getEnclosingType().getShortName() + "." + x.getName(), function);
      }

      // Don't traverse the method body of methods in referenceOnly types since those method bodies
      // only exist in JS output of other modules it is their responsibility to handle their naming.
      return !program.isReferenceOnly(x.getEnclosingType());
    }

    @Override
    public boolean visit(JTryStatement x, Context ctx) {
      accept(x.getTryBlock());
      for (JTryStatement.CatchClause clause : x.getCatchClauses()) {
        JLocalRef arg = clause.getArg();
        JBlock catchBlock = clause.getBlock();
        JsCatch jsCatch = new JsCatch(x.getSourceInfo(), scopeStack.peek(), arg.getTarget().getName());
        JsParameter jsParam = jsCatch.getParameter();
        names.put(arg.getTarget(), jsParam.getName());
        catchMap.put(catchBlock, jsCatch);
        catchParamIdentifiers.add(jsParam.getName());

        scopeStack.push(jsCatch.getScope());
        accept(catchBlock);
        scopeStack.pop();
      }

      // TODO: normalize this so it's never null?
      if (x.getFinallyBlock() != null) {
        accept(x.getFinallyBlock());
      }
      return false;
    }

    /**
     * Generate a file name URI string for a source info, for symbol data
     * export.
     */
    private String makeUriString(HasSourceInfo x) {
      String fileName = x.getSourceInfo().getFileName();
      if (fileName == null) {
        return null;
      }
      String uriString = fileNameToUriString.get(fileName);
      if (uriString == null) {
        uriString = StandardSymbolData.toUriString(fileName);
        fileNameToUriString.put(fileName, uriString);
      }
      return uriString;
    }

    private void recordSymbol(JReferenceType x, JsName jsName) {
      if (getRuntimeTypeReference(x) == null || !program.typeOracle.isInstantiatedType(x)) {
        return;
      }

      String typeId = getRuntimeTypeReference(x).toSource();
      StandardSymbolData symbolData =
          StandardSymbolData.forClass(x.getName(), x.getSourceInfo().getFileName(),
              x.getSourceInfo().getStartLine(), typeId);
      assert !symbolTable.containsKey(symbolData);
      symbolTable.put(symbolData, jsName);
    }

    private <T extends HasEnclosingType & HasName & HasSourceInfo> void recordSymbol(T x,
        JsName jsName) {
      /*
       * NB: The use of x.getName() can produce confusion in cases where a type
       * has both polymorphic and static dispatch for a method, because you
       * might see HashSet::$add() and HashSet::add(). Logically, these methods
       * should be treated equally, however they will be implemented with
       * separate global functions and must be recorded independently.
       *
       * Automated systems that process the symbol information can easily map
       * the statically-dispatched function by looking for method names that
       * begin with a dollar-sign and whose first parameter is the enclosing
       * type.
       */

      String methodSig = null;
      if (x instanceof JMethod) {
        JMethod method = ((JMethod) x);
        methodSig = StringInterner.get().intern(
            method.getSignature().substring(method.getName().length()));
      }

      StandardSymbolData symbolData =
          StandardSymbolData.forMember(x.getEnclosingType().getName(), x.getName(), methodSig,
              makeUriString(x), x.getSourceInfo().getStartLine());
      assert !symbolTable.containsKey(symbolData) : "Duplicate symbol " + "recorded "
          + jsName.getIdent() + " for " + x.getName() + " and key " + symbolData.getJsniIdent();
      symbolTable.put(symbolData, jsName);
    }
  }

  private class GenerateJavaScriptTransformer extends JTransformer<JsNode> {

    public static final String GOOG_INHERITS = "goog.inherits";
    public static final String GOOG_ABSTRACT_METHOD = "goog.abstractMethod";
    public static final String GOOG_OBJECT_CREATE_SET = "goog.object.createSet";
    private final Set<JDeclaredType> alreadyRan = Sets.newLinkedHashSet();

    private final Map<String, Object> exportedMembersByExportName = new TreeMap<String, Object>();

    private final Map<JDeclaredType, JsFunction> clinitFunctionForType = Maps.newHashMap();

    private JMethod currentMethod = null;

    private final JsName arrayLength = objectScope.declareUnobfuscatableName("length");

    private final JsName globalTemp = topScope.declareUnobfuscatableName("_");

    private final JsName prototype = objectScope.declareUnobfuscatableName("prototype");

    private final JsName call = objectScope.declareUnobfuscatableName("call");

    /**
     * Holds any local variable declarations which must be inserted into the current JS function
     * body under construction.
     */
    private JsVars pendingLocals;

    @Override
    public JsExpression transformArrayLength(JArrayLength x) {
      assert x.getInstance() != null : "Can't access the length of a null array";
      return arrayLength.makeQualifiedRef(x.getSourceInfo(), transform(x.getInstance()));
    }

    @Override
    public JsExpression transformArrayRef(JArrayRef x) {
      JsArrayAccess jsArrayAccess = new JsArrayAccess(x.getSourceInfo());
      jsArrayAccess.setIndexExpr(transform(x.getIndexExpr()));
      jsArrayAccess.setArrayExpr(transform(x.getInstance()));
      return jsArrayAccess;
    }

    @Override
    public JsExpression transformBinaryOperation(JBinaryOperation x) {
      JsExpression lhs = transform(x.getLhs());
      JsExpression rhs = transform(x.getRhs());
      JsBinaryOperator myOp = JavaToJsOperatorMap.get(x.getOp());

      /*
       * Use === and !== on reference types, or else you can get wrong answers
       * when Object.toString() == 'some string'.
       */
      if (myOp == JsBinaryOperator.EQ && x.getLhs().getType() instanceof JReferenceType
          && x.getRhs().getType() instanceof JReferenceType) {
        myOp = JsBinaryOperator.REF_EQ;
      } else if (myOp == JsBinaryOperator.NEQ && x.getLhs().getType() instanceof JReferenceType
          && x.getRhs().getType() instanceof JReferenceType) {
        myOp = JsBinaryOperator.REF_NEQ;
      }

      return new JsBinaryOperation(x.getSourceInfo(), myOp, lhs, rhs);
    }

    @Override
    public JsStatement transformBlock(JBlock x) {
      JsBlock jsBlock = new JsBlock(x.getSourceInfo());
      List<JsStatement> stmts = jsBlock.getStatements();

      transformIntoExcludingNulls(x.getStatements(), stmts);
      Iterator<JsStatement> iterator = stmts.iterator();
      while (iterator.hasNext()) {
        JsStatement stmt = iterator.next();
        if (stmt instanceof JsEmpty) {
          iterator.remove();
        }
      }
      return jsBlock;
    }

    @Override
    public JsNode transformBreakStatement(JBreakStatement x) {
      JsNameRef labelRef = null;
      if (x.getLabel() != null) {
        JsLabel label = transform(x.getLabel());
        labelRef = label.getName().makeRef(x.getSourceInfo());
      }
      return new JsBreak(x.getSourceInfo(), labelRef);
    }

    @Override
    public JsNode transformCaseStatement(JCaseStatement x) {
      if (x.getExpr() == null) {
        return new JsDefault(x.getSourceInfo());
      } else {
        JsCase jsCase = new JsCase(x.getSourceInfo());
        jsCase.setCaseExpr(transform(x.getExpr()));
        return jsCase;
      }
    }

    @Override
    public JsNode transformCastOperation(JCastOperation x) {
      // These are left in when cast checking is disabled.
      return transform(x.getExpr());
    }

    @Override
    public JsNode transformClassLiteral(JClassLiteral x) {
      JsName classLit = names.get(x.getField());
      return classLit.makeRef(x.getSourceInfo());
    }

    @Override
    public JsNode transformDeclaredType(JDeclaredType x) {
      // Don't generate JS for types not in current module if separate compilation is on.
      if (program.isReferenceOnly(x)) {
        return null;
      }

      if (alreadyRan.contains(x)) {
        return null;
      }

      alreadyRan.add(x);

      if (x.isJsNative()) {
        // Don't generate JS for native JsType.
        return null;
      }

      checkForDuplicateMethods(x);

      assert program.getTypeClassLiteralHolder() != x;
      assert !program.immortalCodeGenTypes.contains(x);
      // Super classes should be emitted before the actual class.
      assert x.getSuperClass() == null || program.isReferenceOnly(x.getSuperClass()) ||
          alreadyRan.contains(x.getSuperClass());

      emitStaticMethods(x);

      generateTypeSetup(x);

      emitFields(x);

      collectExports(x);
      return null;
    }

    @Override
    public JsNode transformConditional(JConditional x) {
      JsExpression ifTest = transform(x.getIfTest());
      JsExpression thenExpr = transform(x.getThenExpr());
      JsExpression elseExpr = transform(x.getElseExpr());
      return new JsConditional(x.getSourceInfo(), ifTest, thenExpr, elseExpr);
    }

    @Override
    public JsNode transformContinueStatement(JContinueStatement x) {
      JsNameRef labelRef = null;
      if (x.getLabel() != null) {
        JsLabel label = transform(x.getLabel());
        labelRef = label.getName().makeRef(x.getSourceInfo());
      }
      return new JsContinue(x.getSourceInfo(), labelRef);
    }

    @Override
    public JsNode transformDebuggerStatement(JDebuggerStatement x) {
      return new JsDebugger(x.getSourceInfo());
    }

    @Override
    public JsNode transformDeclarationStatement(JDeclarationStatement x) {
      if (x.getInitializer() == null) {
        /*
         * Declaration statements can only appear in blocks, so it's okay to
         * push null instead of an empty statement
         */
        return null;
      }

      JVariable target = x.getVariableRef().getTarget();
      if (target instanceof JField && initializeAtTopScope((JField) target)) {
        // Will initialize at top scope; no need to double-initialize.
        return null;
      }

      JsExpression initializer = transform(x.getInitializer());
      JsNameRef localRef = transform(x.getVariableRef());

      return new JsBinaryOperation(x.getSourceInfo(), JsBinaryOperator.ASG, localRef, initializer)
          .makeStmt();
    }

    @Override
    public JsNode transformDoStatement(JDoStatement x) {
      JsDoWhile stmt = new JsDoWhile(x.getSourceInfo());
      stmt.setCondition(transform(x.getTestExpr()));
      stmt.setBody(jsEmptyIfNull(x.getSourceInfo(), transform(x.getBody())));
      return stmt;
    }

    @Override
    public JsNode transformExpressionStatement(JExpressionStatement x) {
      return ((JsExpression) transform(x.getExpr())).makeStmt();
    }

    @Override
    public JsNode transformFieldRef(JFieldRef x) {
      JsExpression qualifier = transform(x.getInstance());
      boolean isStatic = x.getField().isStatic();
      return isStatic ? dispatchToStaticField(x, qualifier) : dispatchToInstanceField(x, qualifier);
    }

    private JsExpression dispatchToStaticField(JFieldRef x, JsExpression unnecessaryQualifier) {
      /*
       * Note: the comma expressions here would cause an illegal tree state if
       * the result expression ended up on the lhs of an assignment. A hack in
       * in endVisit(JBinaryOperation) rectifies the situation.
       */

      JsExpression result = createStaticReference(x.getField(), x.getSourceInfo());

      // Add clinit (if needed).
      result = createCommaExpression(maybeCreateClinitCall(x.getField(), false), result);

      return createCommaExpression(unnecessaryQualifier, result);
    }

    private JsExpression dispatchToInstanceField(JFieldRef x, JsExpression instance) {
      return names.get(x.getField()).makeQualifiedRef(x.getSourceInfo(), instance);
    }

    @Override
    public JsNode transformForStatement(JForStatement x) {
      JsFor jsFor = new JsFor(x.getSourceInfo());

      JsExpression initExpr = null;
      List<JsExprStmt> initStmts = transform(x.getInitializers());
      for (int i = 0; i < initStmts.size(); ++i) {
        JsExprStmt initStmt = initStmts.get(i);
        if (initStmt != null) {
          initExpr = createCommaExpression(initExpr, initStmt.getExpression());
        }
      }
      jsFor.setInitExpr(initExpr);
      jsFor.setCondition(transform(x.getCondition()));
      jsFor.setIncrExpr(transform(x.getIncrements()));
      jsFor.setBody(jsEmptyIfNull(x.getSourceInfo(), transform(x.getBody())));

      return jsFor;
    }

    @Override
    public JsNode transformIfStatement(JIfStatement x) {
      JsIf stmt = new JsIf(x.getSourceInfo());

      stmt.setIfExpr(transform(x.getIfExpr()));
      stmt.setThenStmt(jsEmptyIfNull(x.getSourceInfo(), transform(x.getThenStmt())));
      stmt.setElseStmt(transform(x.getElseStmt()));

      return stmt;
    }

    @Override
    public JsLabel transformLabel(JLabel x) {
      return new JsLabel(x.getSourceInfo(), names.get(x));
    }

    @Override
    public JsStatement transformLabeledStatement(JLabeledStatement x) {
      JsLabel label = transform(x.getLabel());
      JsStatement body = transform(x.getBody());
      label.setStmt(body);
      return label;
    }

    @Override
    public JsLiteral transformLiteral(JLiteral x) {
      return JjsUtils.translateLiteral(x);
    }

    @Override
    public JsNode transformLocal(JLocal x) {
      return names.get(x).makeRef(x.getSourceInfo());
    }

    @Override
    public JsNode transformLocalRef(JLocalRef x) {
      return names.get(x.getTarget()).makeRef(x.getSourceInfo());
    }

    @Override
    public JsNode transformMethod(JMethod x) {
      if (shouldNotEmitMethodImplementation(x)) {
        return null;
      }
      currentMethod = x;
      pendingLocals = new JsVars(x.getSourceInfo());

      JsFunction function = transform(x.getBody());
      function.setInliningMode(x.getInliningMode());

      List<JsParameter> params = transform(x.getParams()); // params

      if (!x.isNative()) {
        // Setup params on the generated function. A native method already got
        // its jsParams set in BuildTypeMap.
        // TODO: Do we really need to do that in BuildTypeMap?
        List<JsParameter> jsParams = function.getParameters();
        for (int i = 0; i < params.size(); ++i) {
          JsParameter param = params.get(i);
          jsParams.add(param);
        }
      }

      JsInvocation jsInvocation = maybeCreateClinitCall(x);
      if (jsInvocation != null) {
        function.getBody().getStatements().add(0, jsInvocation.makeStmt());
      }

      if (!pendingLocals.isEmpty()) {
        function.getBody().getStatements().add(0, pendingLocals);
      }

      if (JProgram.isClinit(x)) {
        function.markAsClinit();
      }

      currentMethod = null;
      pendingLocals = null;
      return function;
    }

    @Override
    public JsNode transformMethodBody(JMethodBody x) {

      List<JsNameRef> locals = transform(x.getLocals());
      JsBlock body = transform(x.getBlock());

      JsFunction function = jsFunctionsByJavaMethodBody.get(x);
      function.setBody(body);

      /*
       * Emit a statement to declare the method's complete set of local
       * variables. JavaScript doesn't have the same concept of lexical scoping
       * as Java, so it's okay to just predeclare all local vars at the top of
       * the function, which saves us having to use the "var" keyword over and
       * over.
       *
       * Note: it's fine to use the same JS ident to represent two different
       * Java locals of the same name since they could never conflict with each
       * other in Java. We use the alreadySeen set to make sure we don't declare
       * the same-named local var twice.
       */
      JsVars vars = new JsVars(x.getSourceInfo());
      Set<String> alreadySeen = Sets.newHashSet();
      for (int i = 0; i < locals.size(); ++i) {
        JsName name = names.get(x.getLocals().get(i));
        String ident = name.getIdent();
        if (!alreadySeen.contains(ident)
            // Catch block params don't need var declarations
            && !catchParamIdentifiers.contains(name)) {
          alreadySeen.add(ident);
          vars.add(new JsVar(x.getSourceInfo(), name));
        }
      }

      if (!vars.isEmpty()) {
        function.getBody().getStatements().add(0, vars);
      }

      return function;
    }

    @Override
    public JsNode transformMethodCall(JMethodCall x) {
      JMethod method = x.getTarget();
      if (JProgram.isClinit(method)) {
        /*
         * It is possible for clinits to be referenced here that have actually
         * been retargeted (see {@link
         * JTypeOracle.recomputeAfterOptimizations}). Most of the time, these
         * will get cleaned up by other optimization passes prior to this point,
         * but it's not guaranteed. In this case we need to replace the method
         * call with the replaced clinit, unless the replacement is null, in
         * which case we generate a JsNullLiteral as a place-holder expression.
         */
        JDeclaredType type = method.getEnclosingType();
        JDeclaredType clinitTarget = type.getClinitTarget();
        if (clinitTarget == null) {
          // generate a null expression, which will get optimized out
          return JsNullLiteral.INSTANCE;
        }
        method = clinitTarget.getClinitMethod();
      }

      JsExpression qualifier = transform(x.getInstance());
      List<JsExpression> args = transform(x.getArgs());
      if (method.isStatic()) {
        return dispatchToStatic(qualifier, method, args, x.getSourceInfo());
      } else if (x.isStaticDispatchOnly()) {
        return  dispatchToSuper(qualifier, method, args, x.getSourceInfo());
      } else if (method.isOrOverridesJsFunctionMethod()) {
        return  dispatchToJsFunction(qualifier, args, x.getSourceInfo());
      } else {
        return  dispatchToInstanceMethod(qualifier, method, args, x.getSourceInfo());
      }
    }

    private JsExpression dispatchToStatic(JsExpression unnecessaryQualifier, JMethod method,
        List<JsExpression> args, SourceInfo sourceInfo) {
      JsNameRef methodName = createStaticReference(method, sourceInfo);
      JsExpression result = new JsInvocation(sourceInfo, methodName, args);

      return createCommaExpression(unnecessaryQualifier, result);
    }

    private JsExpression dispatchToSuper(
        JsExpression instance, JMethod method, List<JsExpression> args, SourceInfo sourceInfo) {
      JsNameRef methodNameRef;
      if (method.isConstructor()) {
        // We don't generate calls to super native constructors (yet).
        if (method.isJsNative()) {
          return JsNullLiteral.INSTANCE;
        }
        /*
         * Constructor calls through {@code this} and {@code super} are always dispatched statically
         * using the constructor function name (constructors are always defined as top level
         * functions).
         *
         * Because constructors are modeled like instance methods they have an implicit {@code this}
         * parameter, hence they are invoked like: "constructor.call(this, ...)".
         */
        methodNameRef = names.get(method).makeRef(sourceInfo);
      } else {
        // These are regular super method call. These calls are always dispatched statically and
        // optimizations will statify them (except in a few cases, like being target of
        // {@link Impl.getNameOf} or calls to the native classes.

        JDeclaredType superClass = method.getEnclosingType();
        if (method.isJsNative()) {
          // Construct jsPrototype.prototype.jsname
          methodNameRef = createJsQualifier(method.getQualifiedJsName(), sourceInfo);
        } else {
          JsExpression protoRef = getPrototypeQualifierViaLookup(superClass, sourceInfo);
          methodNameRef = polymorphicNames.get(method).makeQualifiedRef(sourceInfo, protoRef);
          methodNameRef.setQualifier(protoRef);
        }
      }

      // <method_qualifier>.call(instance, args);
      JsNameRef qualifiedMethodName = call.makeQualifiedRef(sourceInfo, methodNameRef);
      JsInvocation jsInvocation = new JsInvocation(sourceInfo, qualifiedMethodName);
      jsInvocation.getArguments().add(instance);
      jsInvocation.getArguments().addAll(args);
      return jsInvocation;
    }

    private JsExpression getPrototypeQualifierViaLookup(JDeclaredType type, SourceInfo sourceInfo) {
      if (closureCompilerFormatEnabled) {
        return getPrototypeQualifierOf(type, type.getSourceInfo());
      } else {
        // Construct JCHSU.getPrototypeFor(type).polyname
        // TODO(rluble): Ideally we would want to construct the inheritance chain the JS way and
        // then we could do Type.prototype.polyname.call(this, ...). Currently prototypes do not
        // have global names instead they are stuck into the prototypesByTypeId array.
        return constructInvocation(sourceInfo, "JavaClassHierarchySetupUtil.getClassPrototype",
            convertJavaLiteral(typeMapper.get(type)));
      }
    }

    private JsExpression dispatchToJsFunction(
        JsExpression instance, List<JsExpression> args, SourceInfo sourceInfo) {
      return new JsInvocation(sourceInfo, instance, args);
    }

    private JsExpression dispatchToInstanceMethod(
        JsExpression instance, JMethod method, List<JsExpression> args, SourceInfo sourceInfo) {
      JsNameRef reference =
          method.isJsPropertyAccessor()
              ? new JsNameRef(sourceInfo, method.getJsName())
              : polymorphicNames.get(method).makeRef(sourceInfo);
      reference.setQualifier(instance);

      switch (method.getJsPropertyAccessorType()) {
        case SETTER:
          return createAssignment(reference, args.get(0));
        case GETTER:
          return reference;
        default:
          return new JsInvocation(sourceInfo, reference, args);
      }
    }

    @Override
    public JsNode transformMultiExpression(JMultiExpression x) {
      List<JsExpression> exprs = transform(x.getExpressions());
      JsExpression cur = null;
      for (int i = 0; i < exprs.size(); ++i) {
        JsExpression next = exprs.get(i);
        cur = createCommaExpression(cur, next);
      }
      if (cur == null) {
        // the multi-expression was empty; use undefined
        cur = new JsNameRef(x.getSourceInfo(), JsRootScope.INSTANCE.getUndefined());
      }
      return cur;
    }

    @Override
    public JsNode transformNameOf(JNameOf x) {
      JsName name = names.get(x.getNode());
      if (name == null) {
        return new JsNameRef(x.getSourceInfo(), JsRootScope.INSTANCE.getUndefined());
      }
      return new JsNameOf(x.getSourceInfo(), name);
    }

    @Override
    public JsNode transformNewInstance(JNewInstance x) {
      SourceInfo sourceInfo = x.getSourceInfo();
      JConstructor ctor = x.getTarget();
      JsName ctorName = names.get(ctor);
      JsNew newExpr = new JsNew(sourceInfo, ctorName.makeRef(sourceInfo));
      if (ctor.isJsNative()) {
        String nativeName = ctor.getQualifiedJsName();
        newExpr = new JsNew(sourceInfo, createJsQualifier(nativeName, sourceInfo));
      }
      transformInto(x.getArgs(), newExpr.getArguments());

      if (x.getClassType().isJsFunctionImplementation()) {
        // Foo.prototype.samMethod
        JMethod jsFunctionMethod = getJsFunctionMethod(x.getClassType());
        JsNameRef funcNameRef = polymorphicNames.get(jsFunctionMethod).makeRef(sourceInfo);
        JsNameRef protoRef = prototype.makeRef(sourceInfo);
        funcNameRef.setQualifier(protoRef);
        protoRef.setQualifier(ctorName.makeRef(sourceInfo));

        // makeLambdaFunction(Foo.prototype.samMethod, new Foo(...))
        return constructInvocation(
            sourceInfo, "JavaClassHierarchySetupUtil.makeLambdaFunction", funcNameRef, newExpr);
      }
      return newExpr;
    }

    private JMethod getJsFunctionMethod(JClassType type) {
      for (JMethod method : type.getMethods()) {
        if (method.isOrOverridesJsFunctionMethod()) {
          return method;
        }
      }
      throw new AssertionError("Should never reach here.");
    }

    @Override
    public JsNode transformNumericEntry(JNumericEntry x) {
      return new JsNumericEntry(x.getSourceInfo(), x.getKey(), x.getValue());
    }

    @Override
    public JsNode transformParameter(JParameter x) {
      return new JsParameter(x.getSourceInfo(), names.get(x));
    }

    @Override
    public JsNode transformParameterRef(JParameterRef x) {
      return names.get(x.getTarget()).makeRef(x.getSourceInfo());
    }

    @Override
    public JsNode transformPermutationDependentValue(JPermutationDependentValue x) {
      throw new IllegalStateException("AST should not contain permutation dependent values at " +
          "this point but contains " + x);
    }

    @Override
    public JsNode transformPostfixOperation(JPostfixOperation x) {
      return new JsPostfixOperation(x.getSourceInfo(), JavaToJsOperatorMap.get(x.getOp()),
          transform(x.getArg()));
    }

    @Override
    public JsNode transformPrefixOperation(JPrefixOperation x) {
      return new JsPrefixOperation(x.getSourceInfo(), JavaToJsOperatorMap.get(x.getOp()),
         transform(x.getArg()));
    }

    /**
     * Embeds properties into permProps for easy access from JavaScript.
     */
    private void embedBindingProperties() {
      SourceInfo sourceInfo = SourceOrigin.UNKNOWN;

      // Generates a list of lists of pairs: [[["key", "value"], ...], ...]
      // The outermost list is indexed by soft permutation id. Each item represents
      // a map from binding properties to their values, but is stored as a list of pairs
      // for easy iteration.
      JsArrayLiteral permutationProperties = new JsArrayLiteral(sourceInfo);
      for (Map<String, String> propertyValueByPropertyName :
          properties.findEmbeddedProperties(TreeLogger.NULL)) {
        JsArrayLiteral entryList = new JsArrayLiteral(sourceInfo);
        for (Entry<String, String> entry : propertyValueByPropertyName.entrySet()) {
          JsArrayLiteral pair = new JsArrayLiteral(sourceInfo,
              new JsStringLiteral(sourceInfo, entry.getKey()),
              new JsStringLiteral(sourceInfo, entry.getValue()));
          entryList.getExpressions().add(pair);
        }
        permutationProperties.getExpressions().add(entryList);
      }

      getGlobalStatements().add(
          constructInvocation(sourceInfo, "ModuleUtils.setGwtProperty",
              new JsStringLiteral(sourceInfo, "permProps"), permutationProperties).makeStmt());
    }

    @Override
    public JsNode transformReturnStatement(JReturnStatement x) {
      return new JsReturn(x.getSourceInfo(), transform(x.getExpr()));
    }

    @Override
    public JsNode transformRunAsync(JRunAsync x) {
      return transform(x.getRunAsyncCall());
    }

    @Override
    public JsNode transformCastMap(JCastMap x) {
      SourceInfo sourceInfo = x.getSourceInfo();

      List<JsExpression> castableToTypeIdLiterals = transform(x.getCanCastToTypes());
      return buildJsCastMapLiteral(castableToTypeIdLiterals, sourceInfo);
    }

    @Override
    public JsNameRef transformJsniMethodRef(JsniMethodRef x) {
      JMethod method = x.getTarget();
      JsNameRef nameRef = names.get(method).makeRef(x.getSourceInfo());
      return nameRef;
    }

    @Override
    public JsArrayLiteral transformJsonArray(JsonArray x) {
      JsArrayLiteral jsArrayLiteral = new JsArrayLiteral(x.getSourceInfo());
      transformInto(x.getExprs(), jsArrayLiteral.getExpressions());
      return jsArrayLiteral;
    }

    @Override
    public JsNode transformThisRef(JThisRef x) {
      return new JsThisRef(x.getSourceInfo());
    }

    @Override
    public JsNode transformThrowStatement(JThrowStatement x) {
      return new JsThrow(x.getSourceInfo(), transform(x.getExpr()));
    }

    @Override
    public JsNode transformTryStatement(JTryStatement x) {
      JsTry jsTry = new JsTry(x.getSourceInfo());

      jsTry.setTryBlock(transform(x.getTryBlock()));

      int size = x.getCatchClauses().size();
      assert (size < 2);
      if (size == 1) {
        JBlock block = x.getCatchClauses().get(0).getBlock();
        JsCatch jsCatch = catchMap.get(block);
        jsCatch.setBody(transform(block));
        jsTry.getCatches().add(jsCatch);
      }

      JsBlock finallyBlock = transform(x.getFinallyBlock());
      if (finallyBlock != null && finallyBlock.getStatements().size() > 0) {
        jsTry.setFinallyBlock(finallyBlock);
      }

      return jsTry;
    }

    @Override
    public JsNode transformWhileStatement(JWhileStatement x) {
      JsWhile stmt = new JsWhile(x.getSourceInfo());
      stmt.setCondition(transform(x.getTestExpr()));
      stmt.setBody(jsEmptyIfNull(x.getSourceInfo(), transform(x.getBody())));
      return stmt;
    }

    public JsStatement jsEmptyIfNull(SourceInfo info, JsStatement statement) {
      return statement != null ? statement : new JsEmpty(info);
    }

    private void insertInTopologicalOrder(JDeclaredType type,
        Set<JDeclaredType> topologicallySortedSet) {
      if (type == null || topologicallySortedSet.contains(type) || program.isReferenceOnly(type)) {
        return;
      }
      insertInTopologicalOrder(type.getSuperClass(), topologicallySortedSet);
      topologicallySortedSet.add(type);
    }

    @Override
    public JsNode transformProgram(JProgram x) {
      // Handle the visiting here as we need to slightly change the order.
      // 1.1 (preamble) Immortal code gentypes.
      // 1.2 (preamble) Classes in the preamble, i.e. all the classes that are needed
      //                to support creation of class literals (reachable through Class.createFor* ).
      // 1.3 (preamble) Class literals for classes in the preamble.
      // 2.  (body)     Normal classes, each with its corresponding class literal (if live).
      // 3.  (epilogue) Code to start the execution of the program (gwtOnLoad, etc).

      Set<JDeclaredType> preambleTypes = generatePreamble(x);

      if (incremental) {
        // Record the names of preamble types so that it's possible to invalidate caches when the
        // preamble types are known to have become stale.
        if (!minimalRebuildCache.hasPreambleTypeNames()) {
          Set<String> preambleTypeNames =  Sets.newHashSet();
          for (JDeclaredType preambleType : preambleTypes) {
            preambleTypeNames.add(preambleType.getName());
          }
          minimalRebuildCache.setPreambleTypeNames(logger, preambleTypeNames);
        }
      }

      // Sort normal types according to superclass relationship.
      Set<JDeclaredType> topologicallySortedBodyTypes = Sets.newLinkedHashSet();
      for (JDeclaredType type : x.getModuleDeclaredTypes()) {
        insertInTopologicalOrder(type, topologicallySortedBodyTypes);
      }
      // Remove all preamble types that might have been inserted here.
      topologicallySortedBodyTypes.removeAll(preambleTypes);

      // Iterate over each type in the right order.
      markPosition("Program", Type.PROGRAM_START);
      for (JDeclaredType type : topologicallySortedBodyTypes) {
        markPosition(type.getName(), Type.CLASS_START);
        transform(type);
        maybeGenerateClassLiteral(type);
        installClassLiterals(Arrays.asList(type));
        markPosition(type.getName(), Type.CLASS_END);
      }
      markPosition("Program", Type.PROGRAM_END);

      generateEpilogue();

      // All done, do not visit children.
      return null;
    }

    private Set<JDeclaredType> generatePreamble(JProgram program) {
      // Reserve the "_" identifier.
      JsVars vars = new JsVars(jsProgram.getSourceInfo());
      vars.add(new JsVar(jsProgram.getSourceInfo(), globalTemp));
      addVarsIfNotEmpty(vars);

      // Generate immortal types in the preamble.
      generateImmortalTypes(vars);

      Set<JDeclaredType> alreadyProcessed =
          Sets.<JDeclaredType>newLinkedHashSet(program.immortalCodeGenTypes);
      alreadyProcessed.add(program.getTypeClassLiteralHolder());
      alreadyRan.addAll(alreadyProcessed);

      List<JDeclaredType> classLiteralSupportClasses =
          computeClassLiteralsSupportClasses(program, alreadyProcessed);

      // Make sure immortal classes are not doubly processed.
      classLiteralSupportClasses.removeAll(alreadyProcessed);
      for (JDeclaredType type : classLiteralSupportClasses) {
        transform(type);
      }
      generateClassLiterals(classLiteralSupportClasses);
      installClassLiterals(classLiteralSupportClasses);

      Set<JDeclaredType> preambleTypes = Sets.newLinkedHashSet(alreadyProcessed);
      preambleTypes.addAll(classLiteralSupportClasses);
      return preambleTypes;
    }

    private void installClassLiterals(List<JDeclaredType> classLiteralTypesToInstall) {
      if (!closureCompilerFormatEnabled) {
        // let createForClass() install them until a follow on CL
        // TODO(cromwellian) remove after approval from rluble in follow up CL
        return;
      }

      for (JDeclaredType type : classLiteralTypesToInstall) {
        if (hasNoTypeDefinion(type)) {
          continue;
        }

        JsNameRef classLiteralRef = createClassLiteralReference(type);
        if (classLiteralRef == null) {
          continue;
        }

        JsExpression protoRef = getPrototypeQualifierOf(type, type.getSourceInfo());
        JsNameRef clazzField = indexedFields.get("Object.___clazz").makeRef(type.getSourceInfo());
        clazzField.setQualifier(protoRef);
        JsExprStmt stmt = createAssignment(clazzField, classLiteralRef).makeStmt();
        addTypeDefinitionStatement(type, stmt);
      }
    }

    private boolean hasNoTypeDefinion(JDeclaredType type) {
      // Interfaces, Unboxed Types, JSOs, Native Types, JsFunction, and uninstantiated types
      // Do not have vtables/prototype setup
      return (type instanceof JInterfaceType && !closureCompilerFormatEnabled)
          || program.isRepresentedAsNativeJsPrimitive(type) ||
          type.isJsoType() || !program.typeOracle.isInstantiatedType(type) || type.isJsNative()
          || type.isJsFunction();
    }

    private List<JDeclaredType> computeClassLiteralsSupportClasses(JProgram program,
        Set<JDeclaredType> alreadyProcessedTypes) {
      if (program.isReferenceOnly(program.getIndexedType("Class"))) {
        return Collections.emptyList();
      }
      // Include in the preamble all classes that are reachable for Class.createForClass,
      // Class.createForInterface
      SortedSet<JDeclaredType> reachableClasses =
          computeReachableTypes(METHODS_PROVIDED_BY_PREAMBLE);

      assert !incremental || checkCoreModulePreambleComplete(program,
          program.getTypeClassLiteralHolder().getClinitMethod());

      Set<JDeclaredType> orderedPreambleClasses = Sets.newLinkedHashSet();
      for (JDeclaredType type : reachableClasses) {
        if (alreadyProcessedTypes.contains(type)) {
          continue;
        }
        insertInTopologicalOrder(type, orderedPreambleClasses);
      }

      // TODO(rluble): The set of preamble types might be overly large, in particular will include
      // JSOs that need clinit. This is due to {@link ControlFlowAnalyzer} making all JSOs live if
      // there is a cast to that type anywhere in the program. See the use of
      // {@link JTypeOracle.getInstantiatedJsoTypesViaCast} in the constructor.
      return Lists.newArrayList(orderedPreambleClasses);
    }

    /**
     * Check that in modular compiles the preamble is complete.
     * <p>
     * In modular compiles the preamble has to include code for creating all 4 types of class
     * literals.
     */
    private boolean checkCoreModulePreambleComplete(JProgram program,
        JMethod classLiteralInitMethod) {
      final Set<JMethod> calledMethods = Sets.newHashSet();
      new JVisitor() {
        @Override
        public void endVisit(JMethodCall x, Context ctx) {
          calledMethods.add(x.getTarget());
        }
      }.accept(classLiteralInitMethod);

      for (String createForMethodName : METHODS_PROVIDED_BY_PREAMBLE) {
        if (!calledMethods.contains(program.getIndexedMethod(createForMethodName))) {
          return false;
        }
      }
      return true;
    }

    /**
     * Computes the set of types whose methods or fields are reachable from {@code methods}.
     */
    private SortedSet<JDeclaredType> computeReachableTypes(Iterable<String> methodNames) {
      ControlFlowAnalyzer cfa = new ControlFlowAnalyzer(program);
      for (String methodName : methodNames) {
        JMethod method = program.getIndexedMethodOrNull(methodName);
        // Only traverse it if it has not been pruned.
        if (method != null) {
          cfa.traverseFrom(method);
        }
      }

      // Get the list of enclosing classes that were not excluded.
      SortedSet<JDeclaredType> reachableTypes =
          ImmutableSortedSet.copyOf(HasName.BY_NAME_COMPARATOR,
              Iterables.filter(
                  Iterables.transform(cfa.getLiveFieldsAndMethods(),
                      new Function<JNode, JDeclaredType>() {
                        @Override
                        public JDeclaredType apply(JNode member) {
                          if (member instanceof JMethod) {
                            return ((JMethod) member).getEnclosingType();
                          } else if (member instanceof JField) {
                            return ((JField) member).getEnclosingType();
                          } else {
                            assert member instanceof JParameter || member instanceof JLocal;
                            // Discard locals and parameters, only need the enclosing instances of reachable
                            // fields and methods.
                            return null;
                          }
                        }
                      }), Predicates.notNull()));
      return reachableTypes;
    }

    private void generateEpilogue() {
      generateRemainingClassLiterals();

      // add all @JsExport assignments
      generateExports();

      // Generate entry methods. Needs to be after class literal insertion since class literal will
      // be referenced by runtime rebind and property provider bootstrapping.
      setupGwtOnLoad();

      embedBindingProperties();

      if (program.getRunAsyncs().size() > 0) {
        // Prevent onLoad from being pruned.
        JMethod onLoadMethod = program.getIndexedMethod("AsyncFragmentLoader.onLoad");
        JsName name = names.get(onLoadMethod);
        assert name != null;
        JsFunction function = (JsFunction) name.getStaticRef();
        function.setArtificiallyRescued(true);
      }
    }

    private void generateRemainingClassLiterals() {
      if (!incremental) {
        // Emit classliterals that are references but whose classes are not live.
        generateClassLiterals(Iterables.filter(classLiteralDeclarationsByType.keySet(),
            Predicates.not(Predicates.<JType>in(alreadyRan))));
        return;
      }

      // In incremental, class literal references to class literals that were not generated
      // as part of the current compile have to be from reference only classes.
      assert FluentIterable.from(classLiteralDeclarationsByType.keySet())
          .filter(Predicates.instanceOf(JDeclaredType.class))
          .filter(Predicates.not(Predicates.<JType>in(alreadyRan)))
          .filter(
              new Predicate<JType>() {
                @Override
                public boolean apply(JType type) {
                  return !program.isReferenceOnly((JDeclaredType) type);
                }
              })
          .isEmpty();

      // In incremental only the class literals for the primitive types should be part of the
      // epilogue.
      generateClassLiterals(JPrimitiveType.types);
    }

    private void generateClassLiterals(Iterable<? extends JType> orderedTypes) {
      for (JType type : orderedTypes) {
        maybeGenerateClassLiteral(type);
      }
    }

    private void generateExports() {
      if (exportedMembersByExportName.isEmpty()) {
        return;
      }

      JsInteropExportsGenerator exportGenerator;
      if (closureCompilerFormatEnabled) {
        exportGenerator = new ClosureJsInteropExportsGenerator(getGlobalStatements(), names);
      } else {
        exportGenerator = new DefaultJsInteropExportsGenerator(getGlobalStatements(), globalTemp,
            indexedFunctions);
      }

      Set<JDeclaredType> generatedClinits = Sets.newHashSet();

      for (Object exportedEntity : exportedMembersByExportName.values()) {
        if (exportedEntity instanceof JDeclaredType) {
          exportGenerator.exportType((JDeclaredType) exportedEntity);
        } else {
          JMember member = (JMember) exportedEntity;
          maybeHoistClinit(generatedClinits, member);
          exportGenerator.exportMember(member, names.get(member).makeRef(member.getSourceInfo()));
        }
      }
    }

    private void maybeHoistClinit(Set<JDeclaredType> generatedClinits, JMember member) {
      JDeclaredType enclosingType = member.getEnclosingType();
      if (generatedClinits.contains(enclosingType)) {
        return;
      }

      JsInvocation clinitCall = member instanceof JMethod ? maybeCreateClinitCall((JMethod) member)
          : maybeCreateClinitCall((JField) member, true);
      if (clinitCall != null) {
        generatedClinits.add(enclosingType);
        getGlobalStatements().add(clinitCall.makeStmt());
      }
    }

    @Override
    public JsFunction transformJsniMethodBody(JsniMethodBody x) {
      final Map<String, JNode> jsniMap = Maps.newHashMap();
      for (JsniClassLiteral ref : x.getClassRefs()) {
        jsniMap.put(ref.getIdent(), ref.getField());
      }
      for (JsniFieldRef ref : x.getJsniFieldRefs()) {
        jsniMap.put(ref.getIdent(), ref.getField());
      }
      for (JsniMethodRef ref : x.getJsniMethodRefs()) {
        jsniMap.put(ref.getIdent(), ref.getTarget());
      }

      final JsFunction function = x.getFunc();

      // replace all JSNI idents with a real JsName now that we know it
      new JsModVisitor() {

        /**
         * Marks a ctor that is a direct child of an invocation. Instead of
         * replacing the ctor with a tear-off, we replace the invocation with a
         * new operation.
         */
        private JsNameRef dontReplaceCtor;

        @Override
        public void endVisit(JsInvocation x, JsContext ctx) {
          // TODO(rluble): this fixup should be done during the initial JSNI processing in
          // GwtAstBuilder.JsniReferenceCollector.
          if (!(x.getQualifier() instanceof JsNameRef)) {
            // If the invocation does not have a name as a qualifier (it might be an expression).
            return;
          }
          JsNameRef ref = (JsNameRef) x.getQualifier();
          if (!ref.isJsniReference()) {
            // The invocation is not to a JSNI method.
            return;
          }
          // Only constructors reach this point, all other JSNI references in the method body
          // would have already been replaced at endVisit(JsNameRef).

          // Replace invocation to ctor with a new op.
          String ident = ref.getIdent();
          JNode node = jsniMap.get(ident);
          assert node instanceof JConstructor;
          assert ref.getQualifier() == null;
          JsName jsName = names.get(node);
          assert (jsName != null);
          ref.resolve(jsName);
          JsNew jsNew = new JsNew(x.getSourceInfo(), ref);
          jsNew.getArguments().addAll(x.getArguments());
          ctx.replaceMe(jsNew);
        }

        @Override
        public void endVisit(JsNameRef x, JsContext ctx) {
          if (!x.isJsniReference()) {
            return;
          }

          String ident = x.getIdent();
          JNode node = jsniMap.get(ident);
          assert (node != null);
          if (node instanceof JField) {
            JField field = (JField) node;
            JsName jsName = names.get(field);
            assert (jsName != null);
            x.resolve(jsName);

            // See if we need to add a clinit call to a static field ref
            JsInvocation clinitCall = maybeCreateClinitCall(field, false);
            if (clinitCall != null) {
              JsExpression commaExpr = createCommaExpression(clinitCall, x);
              ctx.replaceMe(commaExpr);
            }
          } else if (node instanceof JConstructor) {
            if (x == dontReplaceCtor) {
              // Do nothing, parent will handle.
            } else {
              // Replace with a local closure function.
              // function(a,b,c){return new Obj(a,b,c);}
              JConstructor ctor = (JConstructor) node;
              JsName jsName = names.get(ctor);
              assert (jsName != null);
              x.resolve(jsName);
              SourceInfo info = x.getSourceInfo();
              JsFunction closureFunc = new JsFunction(info, function.getScope());
              for (JParameter p : ctor.getParams()) {
                JsName name = closureFunc.getScope().declareName(p.getName());
                closureFunc.getParameters().add(new JsParameter(info, name));
              }
              JsNew jsNew = new JsNew(info, x);
              for (JsParameter p : closureFunc.getParameters()) {
                jsNew.getArguments().add(p.getName().makeRef(info));
              }
              JsBlock block = new JsBlock(info);
              block.getStatements().add(new JsReturn(info, jsNew));
              closureFunc.setBody(block);
              ctx.replaceMe(closureFunc);
            }
          } else {
            JMethod method = (JMethod) node;
            if (x.getQualifier() == null) {
              JsName jsName = names.get(method);
              assert (jsName != null);
              x.resolve(jsName);
            } else {
              JsName jsName = polymorphicNames.get(method);
              if (jsName == null) {
                // this can occur when JSNI references an instance method on a
                // type that was never actually instantiated.
                jsName =
                    indexedFunctions.get("JavaClassHierarchySetupUtil.emptyMethod").getName();
              }
              x.resolve(jsName);
            }
          }
        }

        @Override
        public boolean visit(JsInvocation x, JsContext ctx) {
          if (x.getQualifier() instanceof JsNameRef) {
            dontReplaceCtor = (JsNameRef) x.getQualifier();
          }
          return true;
        }
      }.accept(function);

      return function;
    }

    @Override
    public JsStatement transformSwitchStatement(JSwitchStatement x) {
      /*
       * What a pain.. JSwitchStatement and JsSwitch are modeled completely
       * differently. Here we try to resolve those differences.
       */
      JsSwitch jsSwitch = new JsSwitch(x.getSourceInfo());
      jsSwitch.setExpr(transform(x.getExpr()));

      List<JStatement> bodyStmts = x.getBody().getStatements();
      List<JsStatement> curStatements = null;
      for (JStatement stmt : bodyStmts) {
        if (stmt instanceof JCaseStatement) {
          // create a new switch member
          JsSwitchMember switchMember = transform((JNode) stmt);
          jsSwitch.getCases().add(switchMember);
          curStatements = switchMember.getStmts();
        } else {
          // add to statements for current case
          assert (curStatements != null);
          JsStatement newStmt = transform(stmt);
          if (newStmt != null) {
            // Empty JDeclarationStatement produces a null
            curStatements.add(newStmt);
          }
        }
      }

      return jsSwitch;
    }

    private JsExpression buildJsCastMapLiteral(
        List<JsExpression> runtimeTypeIdLiterals,
        SourceInfo sourceInfo) {
      if (JjsUtils.closureStyleLiteralsNeeded(incremental, closureCompilerFormatEnabled)) {
        return buildClosureStyleCastMapFromArrayLiteral(runtimeTypeIdLiterals, sourceInfo);
      } else {
        return buildCastMapAsObjectLiteral(runtimeTypeIdLiterals, sourceInfo);
      }
    }

    private JsExpression buildCastMapAsObjectLiteral(
        List<JsExpression> runtimeTypeIdLiterals, SourceInfo sourceInfo) {
      JsObjectLiteral objectLiteral = new JsObjectLiteral(sourceInfo);
      objectLiteral.setInternable();
      List<JsPropertyInitializer> initializers =
          objectLiteral.getPropertyInitializers();
      JsNumberLiteral one = new JsNumberLiteral(sourceInfo, 1);
      for (JsExpression runtimeTypeIdLiteral : runtimeTypeIdLiterals) {
        initializers.add(new JsPropertyInitializer(sourceInfo, runtimeTypeIdLiteral, one));
      }
      return objectLiteral;
    }

    private JsExpression buildClosureStyleCastMapFromArrayLiteral(
            List<JsExpression> runtimeTypeIdLiterals, SourceInfo sourceInfo) {
      /*
       * goog.object.createSet('foo', 'bar', 'baz') is optimized by closure compiler into
       * {'foo': !0, 'bar': !0, baz: !0}
       */
      JsNameRef createSet = new JsNameRef(sourceInfo, GOOG_OBJECT_CREATE_SET);
      JsInvocation jsInvocation = new JsInvocation(sourceInfo, createSet);

      for (JsExpression expr : runtimeTypeIdLiterals) {
        jsInvocation.getArguments().add(expr);
      }

      return jsInvocation;
    }

    private void checkForDuplicateMethods(JDeclaredType x) {
      // Sanity check to see that all methods are uniquely named.
      List<JMethod> methods = x.getMethods();
      Set<String> methodSignatures = Sets.newHashSet();
      for (JMethod method : methods) {
        String sig = method.getSignature();
        if (methodSignatures.contains(sig)) {
          throw new InternalCompilerException("Signature collision in Type " + x.getName()
              + " for method " + sig);
        }
        methodSignatures.add(sig);
      }
    }

    private JsExpression createCommaExpression(JsExpression lhs, JsExpression rhs) {
      if (lhs == null) {
        return rhs;
      } else if (rhs == null) {
        return lhs;
      }
      return new JsBinaryOperation(lhs.getSourceInfo(), JsBinaryOperator.COMMA, lhs, rhs);
    }

    private JsNameRef createStaticReference(JMember member, SourceInfo sourceInfo) {
      assert member.isStatic();
      return member.isJsNative()
          ? createJsQualifier(member.getQualifiedJsName(), sourceInfo)
          : names.get(member).makeRef(sourceInfo);
    }

    private void emitFields(JDeclaredType x) {
      JsVars vars = new JsVars(x.getSourceInfo());
      for (JField field : x.getFields()) {
        JsExpression initiliazer = null;
        // if we need an initial value, create an assignment
        if (initializeAtTopScope(field)) {
          // setup the constant value
          initiliazer = transform(field.getLiteralInitializer());
        } else if (field.getEnclosingType() == program.getTypeJavaLangObject()) {
          // Special fields whose initialization is done somewhere else.
        } else if (field.getType().getDefaultValue() == JNullLiteral.INSTANCE) {
          // Fields whose default value is null are left uninitialized and will
          // have a JS value of undefined.
        } else {
          // setup the default value, see Issue 380
          initiliazer = transform(field.getType().getDefaultValue());
        }

        JsName name = names.get(field);

        if (field.isStatic()) {
          // setup a var for the static
          JsVar var = new JsVar(x.getSourceInfo(), name);
          var.setInitExpr(initiliazer);
          vars.add(var);
        } else {
          if (initiliazer == null) {
            continue;
          }
          JsNameRef fieldRef =
              name.makeQualifiedRef(field.getSourceInfo(), getPrototypeQualifierOf(field));
          addTypeDefinitionStatement(x, createAssignment(fieldRef, initiliazer).makeStmt());
        }
      }
      addVarsIfNotEmpty(vars);
    }

    private void emitStaticMethods(JDeclaredType type) {
      // declare all methods into the global scope
      for (JMethod method : type.getMethods()) {
        if (method.needsVtable()) {
          continue;
        }

        JsFunction function = transform(method);
        if (function == null) {
          continue;
        }

        if (JProgram.isClinit(method)) {
          if (type.getClinitTarget() == type) {
            JDeclaredType superClass = type.getSuperClass();
            handleClinit(function, clinitFunctionForType.get(superClass));
            clinitFunctionForType.put(type, function);
          } else {
            continue;
          }
        }
        // don't add polymorphic JsFuncs, inline decl into vtable assignment
        JsExprStmt functionDefinitionStatement = function.makeStmt();
        emitMethodImplementation(method,
            function.getName().makeRef(function.getSourceInfo()), functionDefinitionStatement);
      }
    }

    private JsExpression generateCastableTypeMap(JDeclaredType x) {
      JCastMap castMap = program.getCastMap(x);
      JField castableTypeMapField = program.getIndexedField("Object.castableTypeMap");
      JsName castableTypeMapName = names.get(castableTypeMapField);

      if (castMap != null && castableTypeMapName != null) {
        return transform(castMap);
      }
      return new JsObjectLiteral(SourceOrigin.UNKNOWN);
    }

    private JField getClassLiteralField(JType type) {
      JDeclarationStatement decl = classLiteralDeclarationsByType.get(type);
      if (decl == null) {
        return null;
      }

      return (JField) decl.getVariableRef().getTarget();
    }

    private void maybeGenerateClassLiteral(JType type) {

      JField field = getClassLiteralField(type);
      if (field == null) {
        return;
      }

      // TODO(rluble): refactor so that all output related to a class is decided together.
      if (type != null && type instanceof JDeclaredType
          && program.isReferenceOnly((JDeclaredType) type)) {
        // Only generate class literals for classes in the current module.
        // TODO(rluble): In separate compilation some class literals will be duplicated, which if
        // not done with care might violate java semantics of getClass(). There are class literals
        // for primitives and arrays. Currently, because they will be assigned to the same field
        // the one defined later will be the one used and Java semantics are preserved.
        return;
      }

      JsVars vars = new JsVars(jsProgram.getSourceInfo());
      JsName jsName = names.get(field);
      JsExpression classLiteralObject = transform(field.getInitializer());
      JsVar var = new JsVar(field.getSourceInfo(), jsName);
      var.setInitExpr(classLiteralObject);
      vars.add(var);
      addVarsIfNotEmpty(vars);
    }

    private JsNameRef createClassLiteralReference(JType type) {
      JField field = getClassLiteralField(type);
      if (field == null) {
        return null;
      }
      JsName jsName = names.get(field);
      return jsName.makeRef(type.getSourceInfo());
    }

    private void generateTypeSetup(JDeclaredType x) {
      if (program.isRepresentedAsNativeJsPrimitive(x) && program.typeOracle.isInstantiatedType(x)) {
        setupCastMapForUnboxedType(x,
            program.getRepresentedAsNativeTypesDispatchMap().get(x).getCastMapField());
        return;
      }

      if (hasNoTypeDefinion(x)) {
        return;
      }

      generateClassDefinition(x);
      generateVTables(x);

      maybeGenerateToStringAlias(x);
    }

    private void markPosition(String name, Type type) {
      getGlobalStatements().add(new JsPositionMarker(SourceOrigin.UNKNOWN, name, type));
    }

    /**
     * Sets up gwtOnLoad bootstrapping code. Unusually, the created code is executed as part of
     * source loading and runs in the global scope (not inside of any function scope).
     */
    private void setupGwtOnLoad() {
      /**
       * <pre>
       * var $entry = Impl.registerEntry();
       * var gwtOnLoad = ModuleUtils.gwtOnLoad();
       * ModuleUtils.addInitFunctions(init1, init2,...)
       * </pre>
       */

      final SourceInfo sourceInfo = SourceOrigin.UNKNOWN;

      // var $entry = ModuleUtils.registerEntry();
      JsStatement entryVars = constructFunctionCallStatement(
          topScope.declareName("$entry"), "ModuleUtils.registerEntry");
      getGlobalStatements().add(entryVars);

      // var gwtOnLoad = ModuleUtils.gwtOnLoad;
      JsName gwtOnLoad = topScope.findExistingUnobfuscatableName("gwtOnLoad");
      JsVar varGwtOnLoad = new JsVar(sourceInfo, gwtOnLoad);
      varGwtOnLoad.setInitExpr(createAssignment(gwtOnLoad.makeRef(sourceInfo),
          indexedFunctions.get("ModuleUtils.gwtOnLoad").getName().makeRef(sourceInfo)));
      getGlobalStatements().add(new JsVars(sourceInfo, varGwtOnLoad));

      // ModuleUtils.addInitFunctions(init1, init2,...)
      List<JsExpression> arguments = Lists.newArrayList();
      for (JMethod entryPointMethod : program.getEntryMethods()) {
        JsFunction entryFunction = getJsFunctionFor(entryPointMethod);
        arguments.add(entryFunction.getName().makeRef(sourceInfo));
      }

      JsStatement createGwtOnLoadFunctionCall =
          constructInvocation("ModuleUtils.addInitFunctions", arguments).makeStmt();

      getGlobalStatements().add(createGwtOnLoadFunctionCall);
    }

    /**
     * Creates a (var) assignment a statement for a function call to an indexed function.
     */
    private JsStatement constructFunctionCallStatement(JsName assignToVariableName,
        String indexedFunctionName, JsExpression... args) {
      return constructFunctionCallStatement(assignToVariableName, indexedFunctionName,
          Arrays.asList(args));
    }

    /**
     * Creates a (var) assignment a statement for a function call to an indexed function.
     */
    private JsStatement constructFunctionCallStatement(JsName assignToVariableName,
        String indexedFunctionName, List<JsExpression> args) {

      SourceInfo sourceInfo = SourceOrigin.UNKNOWN;
      JsInvocation invocation = constructInvocation(indexedFunctionName, args);
      JsVar var = new JsVar(sourceInfo, assignToVariableName);
      var.setInitExpr(invocation);
      JsVars entryVars = new JsVars(sourceInfo);
      entryVars.add(var);
      return entryVars;
    }

    /**
     * Constructs an invocation for an indexed function.
     */
    private JsInvocation constructInvocation(SourceInfo sourceInfo,
        String indexedFunctionName, JsExpression... args) {
      return constructInvocation(sourceInfo, indexedFunctionName, Arrays.asList(args));
    }

    /**
     * Constructs an invocation for an indexed function.
     */
    private JsInvocation constructInvocation(String indexedFunctionName,
        List<JsExpression> args) {
      SourceInfo sourceInfo = SourceOrigin.UNKNOWN;
      return constructInvocation(sourceInfo, indexedFunctionName, args);
    }

    /**
     * Constructs an invocation for an indexed function.
     */
    private JsInvocation constructInvocation(SourceInfo sourceInfo,
        String indexedFunctionName, List<JsExpression> args) {
      JsFunction functionToInvoke = indexedFunctions.get(indexedFunctionName);
      return new JsInvocation(sourceInfo, functionToInvoke, args);
    }

    private void generateImmortalTypes(JsVars globals) {
      List<JClassType> immortalTypesReversed = Lists.reverse(program.immortalCodeGenTypes);
      // visit in reverse order since insertions start at head
      JMethod createEmptyObjectMethod = program.getIndexedMethod("JavaScriptObject.createObject");
      JMethod createEmptyArrayMethod = program.getIndexedMethod("JavaScriptObject.createArray");

      for (JClassType x : immortalTypesReversed) {
        // Don't generate JS for referenceOnly types.
        if (program.isReferenceOnly(x)) {
          continue;
        }
        // should not be pruned
        assert x.getMethods().size() > 0;
        // insert all static methods
        for (JMethod method : x.getMethods()) {
          /*
           * Skip virtual methods and constructors. Even in cases where there is no constructor
           * defined, the compiler will synthesize a default constructor which invokes
           * a synthensized $init() method. We must skip both of these inserted methods.
           */
          if (method.needsVtable() || method instanceof JConstructor
              || shouldNotEmitMethodImplementation(method)) {
            continue;
          }
          JsFunction function = null;
          if (JProgram.isClinit(method)) {
            /**
             * Emit empty clinits that will be pruned. If a type B extends A, then even if
             * B and A have no fields to initialize, there will be a call inserted in B's clinit
             * to invoke A's clinit. Likewise, if you have a static field initialized to
             * JavaScriptObject.createObject(), the clinit() will include this initializer code,
             * which we don't want.
             */
            function = new JsFunction(x.getSourceInfo(), topScope,
                topScope.declareName(mangleNameForGlobal(method)), true);
            function.setBody(new JsBlock(method.getBody().getSourceInfo()));
          } else {
            function = transform(method);
          }
          // add after var declaration, but before everything else
          assert function.getName() != null;
          addMethodDefinitionStatement(1, method, function.makeStmt());
        }

        // TODO(rluble): simplify this so that emitFields can be reused here.
        // insert fields into global var declaration
        for (JField field : x.getFields()) {
          assert field.isStatic() : "All fields on immortal types must be static.";
          JExpression fieldInitializer = field.getInitializer();
          JsExpression initializer = null;

          // Patch up fields that are initialized to empty object/array literal by a call to
          // JavaScriptObject.createObject() and JavaScriptObject.createArray()
          if (fieldInitializer != null
              && field.getLiteralInitializer() == null
              && fieldInitializer.getType() == program.getJavaScriptObject()) {
            assert fieldInitializer instanceof JMethodCall;
            JMethod method = ((JMethodCall) fieldInitializer).getTarget();
            if (method == createEmptyObjectMethod) {
              initializer = new JsObjectLiteral(fieldInitializer.getSourceInfo());
            } else if (method == createEmptyArrayMethod) {
              initializer = new JsArrayLiteral(fieldInitializer.getSourceInfo());
            } else {
              assert false : "Illegal initializer expression for immortal field " + field;
            }
          } else if (fieldInitializer != null) {
            initializer = transform(fieldInitializer);
          }

          JsVar var = new JsVar(x.getSourceInfo(), names.get(field));
          var.setInitExpr(initializer);
          globals.add(var);
        }
      }
    }

    private JsExpression convertJavaLiteral(Object javaLiteral) {
      if (javaLiteral instanceof JLiteral) {
        return JjsUtils.translateLiteral((JLiteral) javaLiteral);
      } else if (javaLiteral instanceof JExpression) {
        return transform((JExpression) javaLiteral);
      } else {
        return JjsUtils.translateLiteral(program.getLiteral(javaLiteral));
      }
    }

    private void generateCallToDefineClass(JClassType x,
        List<JsNameRef> constructorArgs) {
      JExpression typeId = getRuntimeTypeReference(x);
      JClassType superClass = x.getSuperClass();
      JExpression superTypeId = (superClass == null) ? JNullLiteral.INSTANCE :
          getRuntimeTypeReference(superClass);
      String jsPrototype = getSuperPrototype(x);

      List<JsExpression> defineClassArguments = Lists.newArrayList();

      defineClassArguments.add(convertJavaLiteral(typeId));
      defineClassArguments.add(jsPrototype == null ? convertJavaLiteral(superTypeId) :
          createJsQualifier(jsPrototype, x.getSourceInfo()));
      defineClassArguments.add(generateCastableTypeMap(x));
      defineClassArguments.addAll(constructorArgs);

      // JavaClassHierarchySetupUtil.defineClass(typeId, superTypeId, castableMap, constructors)
      JsStatement defineClassStatement = constructInvocation(x.getSourceInfo(),
          "JavaClassHierarchySetupUtil.defineClass", defineClassArguments).makeStmt();
      addTypeDefinitionStatement(x, defineClassStatement);

      if (jsPrototype != null) {
        JsStatement statement =
        constructInvocation(x.getSourceInfo(),
            "JavaClassHierarchySetupUtil.copyObjectProperties",
            getPrototypeQualifierViaLookup(program.getTypeJavaLangObject(), x.getSourceInfo()),
            globalTemp.makeRef(x.getSourceInfo()))
            .makeStmt();
        addTypeDefinitionStatement(x, statement);
      }
    }

    private String getSuperPrototype(JDeclaredType x) {
      if (x.isJsFunctionImplementation()) {
        return "Function";
      }
      JClassType superClass = x.getSuperClass();
      if (superClass != null && superClass.isJsNative()) {
        return superClass.getQualifiedJsName();
      }
      return null;
    }

    private void generateClassDefinition(JDeclaredType x) {
        assert !program.isRepresentedAsNativeJsPrimitive(x);

      if (closureCompilerFormatEnabled) {
        generateClosureClassDefinition(x);
      } else {
        generateJsClassDefinition((JClassType) x);
      }
    }

    /*
     * Class definition for regular output looks like:
     *
     * defineClass(id, superId, castableTypeMap, ctor1, ctor2, ctor3);
     * _.method1 = function() { ... }
     * _.method2 = function() { ... }
     */
    private void generateJsClassDefinition(JClassType x) {
      // Add constructors as varargs to define class.
      List<JsNameRef> constructorArgs = Lists.newArrayList();
      for (JMethod method : getPotentiallyAliveConstructors(x)) {
        constructorArgs.add(names.get(method).makeRef(x.getSourceInfo()));
      }

      // defineClass(..., Ctor1, Ctor2, ...)
      generateCallToDefineClass(x, constructorArgs);
    }

    /*
     * Class definition for closure output looks like:
     *
     * function ClassName() {}
     * ClassName.prototype.method1 = function() { ... };
     * ClassName.prototype.method2 = function() { ... };
     * ClassName.prototype.castableTypeMap = {...}
     * ClassName.prototype.___clazz = classLit;
     * function Ctor1() {}
     * function Ctor2() {}
     *
     * goog$inherits(Ctor1, ClassName);
     * goog$inherits(Ctor2, ClassName);
     *
     * The primary change is to make the prototype assignment look like regular closure code to help
     * the compiler disambiguate which methods belong to which type. Elimination of defineClass()
     * makes the setup more transparent and eliminates a global table holding a reference to
     * every prototype.
     */
    private void generateClosureClassDefinition(JDeclaredType x) {
      // function ClassName(){}
      JsName classVar = declareSynthesizedClosureConstructor(x);
      generateInlinedDefineClass(x, classVar);

       /*
       * Closure style prefers 1 single ctor per type. To model this without radical changes,
       * we simply model each concrete ctor as a subtype. This works because GWT doesn't use the
       * native instanceof operator. So for example, class A() { A(int x){}, A(String s){} }
       * becomes (pseudo code):
       *
       * function A() {}
       * A.prototype.method = ...
       *
       * function A_int(x) {}
       * function A_String(s) {}
       * goog$inherits(A_int, A);
       * goog$inherits(A_string, A);
       *
       */
      for (JMethod method : getPotentiallyAliveConstructors(x)) {
        JsNameRef googInherits = JsUtils.createQualifiedNameRef(GOOG_INHERITS, x.getSourceInfo());

        JsExprStmt callGoogInherits = new JsInvocation(x.getSourceInfo(), googInherits,
            names.get(method).makeRef(method.getSourceInfo()),
            names.get(method.getEnclosingType()).makeRef(method.getSourceInfo())).makeStmt();
        addMethodDefinitionStatement(method, callGoogInherits);
      }
    }

    /**
     * Does everything JCHSU.defineClass does, but inlined into global statements. Roughly
     * parallels argument order of generateCallToDefineClass.
     */
    private void generateInlinedDefineClass(JDeclaredType x, JsName classVar) {
      if (x instanceof JInterfaceType) {
        return;
      }
      JClassType superClass = x.getSuperClass();
      // check if there's an overriding prototype
      String jsPrototype = getSuperPrototype(x);
      JsNameRef parentCtor = jsPrototype != null ?
          createJsQualifier(jsPrototype, x.getSourceInfo()) :
            superClass != null ?
              names.get(superClass).makeRef(x.getSourceInfo()) :
              null;

      if (parentCtor != null) {
        JsNameRef googInherits = JsUtils.createQualifiedNameRef(GOOG_INHERITS, x.getSourceInfo());
        // Use goog$inherits(ChildCtor, ParentCtor) to setup inheritance
        JsExprStmt callGoogInherits = new JsInvocation(x.getSourceInfo(), googInherits,
            classVar.makeRef(x.getSourceInfo()), parentCtor).makeStmt();
        addTypeDefinitionStatement(x, callGoogInherits);
      }

      if (x == program.getTypeJavaLangObject()) {
        setupTypeMarkerOnJavaLangObjectPrototype(x);
      }

      // inline assignment of castableTypeMap field instead of using defineClass()
      setupCastMapOnPrototype(x);
      if (jsPrototype != null) {
        JsStatement statement =
            constructInvocation(x.getSourceInfo(),
                "JavaClassHierarchySetupUtil.copyObjectProperties",
                getPrototypeQualifierOf(program.getTypeJavaLangObject(), x.getSourceInfo()),
                getPrototypeQualifierOf(x, x.getSourceInfo()))
                .makeStmt();
        addTypeDefinitionStatement(x, statement);
      }
    }

    private void setupCastMapOnPrototype(JDeclaredType x) {
      JsExpression castMap = generateCastableTypeMap(x);
      generateVTableAssignmentToJavaField(x, "Object.castableTypeMap", castMap);
    }

    private void setupTypeMarkerOnJavaLangObjectPrototype(JDeclaredType x) {
      JsFunction typeMarkerMethod = indexedFunctions.get(
          "JavaClassHierarchySetupUtil.typeMarkerFn");
      generateVTableAssignmentToJavaField(x, "Object.typeMarker",
          typeMarkerMethod.getName().makeRef(x.getSourceInfo()));
    }

    private void generateVTableAssignmentToJavaField(JDeclaredType x, String javaField,
        JsExpression rhs) {
      SourceInfo sourceInfo = x.getSourceInfo();
      JsNameRef protoRef = getPrototypeQualifierOf(x, sourceInfo);
      JsNameRef fieldRef = indexedFields.get(javaField).makeQualifiedRef(sourceInfo, protoRef);
      addTypeDefinitionStatement(x, createAssignment(fieldRef, rhs).makeStmt());
    }

    private void addMethodDefinitionStatement(JMethod method,
        JsExprStmt methodDefinitionStatement) {
      getGlobalStatements().add(methodDefinitionStatement);
      methodByGlobalStatement.put(methodDefinitionStatement, method);
    }

    private void addMethodDefinitionStatement(int position, JMethod method,
        JsExprStmt methodDefinitionStatement) {
      getGlobalStatements().add(position, methodDefinitionStatement);
      methodByGlobalStatement.put(methodDefinitionStatement, method);
    }

    private void addTypeDefinitionStatement(JDeclaredType x, JsStatement statement) {
      getGlobalStatements().add(statement);
      javaTypeByGlobalStatement.put(statement, x);
    }

    /*
     * Declare an empty synthesized constructor that looks like:
     * function ClassName(){}
     *
     * Closure Compiler's RewriteFunctionExpressions pass can be enabled to turn these back
     * into a factory method after optimizations.
     *
     * TODO(goktug): throw Error in the body to prevent instantiation via this constructor.
     */
    private JsName declareSynthesizedClosureConstructor(JDeclaredType x) {
      SourceInfo sourceInfo = x.getSourceInfo();
      JsName classVar = topScope.declareName(JjsUtils.mangledNameString(x));
      JsFunction closureCtor = JsUtils.createEmptyFunctionLiteral(sourceInfo, topScope, classVar);
      JsExprStmt statement = closureCtor.makeStmt();
      addTypeDefinitionStatement(x, statement);
      names.put(x, classVar);
      return classVar;
    }

    /*
     * Sets up the castmap for type X
     */
    private void setupCastMapForUnboxedType(JDeclaredType type, String castMapField) {
      //  Cast.[castMapName] = /* cast map */ { ..:1, ..:1}
      JField castableTypeMapField = program.getIndexedField(castMapField);
      JsName castableTypeMapName = names.get(castableTypeMapField);
      JsNameRef castMapVarRef = castableTypeMapName.makeRef(type.getSourceInfo());

      JsExpression castMapLiteral = generateCastableTypeMap(type);
      addTypeDefinitionStatement(type, createAssignment(castMapVarRef, castMapLiteral).makeStmt());
    }

    private void maybeGenerateToStringAlias(JDeclaredType x) {
      if (x == program.getTypeJavaLangObject()) {
        // special: setup a "toString" alias for java.lang.Object.toString()
        JMethod toStringMethod = program.getIndexedMethod("Object.toString");
        if (x.getMethods().contains(toStringMethod)) {
          JsName toStringName = objectScope.declareUnobfuscatableName("toString");
          generateVTableAlias(toStringMethod, toStringName);
        }

        //  Perform necessary polyfills.
        getGlobalStatements().add(constructInvocation(x.getSourceInfo(),
            "JavaClassHierarchySetupUtil.modernizeBrowser").makeStmt());
      }
    }

    /**
     * Create a vtable assignment of the form _.polyname = rhs; and register the line as
     * created for {@code method}.
     */
    private void generateVTableAssignment(JMethod method, JsName lhsName, JsExpression rhs) {
      SourceInfo sourceInfo = method.getSourceInfo();
      JsNameRef lhs = lhsName.makeQualifiedRef(sourceInfo, getPrototypeQualifierOf(method));
      emitMethodImplementation(method, lhs, createAssignment(lhs, rhs).makeStmt());
    }

    private void emitMethodImplementation(JMethod method, JsNameRef functionNameRef,
        JsExprStmt methodDefinitionStatement) {
      addMethodDefinitionStatement(method, methodDefinitionStatement);

      if (shouldEmitDisplayNames()) {
        JsExprStmt displayNameAssignment = outputDisplayName(functionNameRef, method);
        addMethodDefinitionStatement(method, displayNameAssignment);
      }
    }

    private void generateVTableAlias(JMethod method, JsName alias) {
      JsName polyName = polymorphicNames.get(method);
      JsExpression bridge = JsUtils.createBridge(method, polyName, topScope);
      generateVTableAssignment(method, alias, bridge);
    }

    private JsExprStmt outputDisplayName(JsNameRef function, JMethod method) {
      JsNameRef displayName = new JsNameRef(function.getSourceInfo(), "displayName");
      displayName.setQualifier(function);
      String displayStringName = getDisplayName(method);
      JsStringLiteral displayMethodName = new JsStringLiteral(function.getSourceInfo(), displayStringName);
      return createAssignment(displayName, displayMethodName).makeStmt();
    }

    private boolean shouldEmitDisplayNames() {
      return methodNameMappingMode != OptionMethodNameDisplayMode.Mode.NONE;
    }

    private String getDisplayName(JMethod method) {
      switch (methodNameMappingMode) {
        case ONLY_METHOD_NAME:
          return method.getName();
        case ABBREVIATED:
          return method.getEnclosingType().getShortName() + "." + method.getName();
        case FULL:
          return method.getEnclosingType().getName() + "." + method.getName();
        default:
          assert false : "Invalid display mode option " + methodNameMappingMode;
      }
      return null;
    }

    /**
     * Creates the assignment for all polynames for a certain class, assumes that the global
     * variable _ points the JavaScript prototype for {@code x}.
     */
    private void generateVTables(JDeclaredType x) {
        assert !program.isRepresentedAsNativeJsPrimitive(x);
      for (JMethod method : x.getMethods()) {
        if (!method.needsVtable()) {
          continue;
        }

        JsExpression functionDefinition =
            !shouldNotEmitMethodImplementation(method)
                ?  (JsExpression) transform(method)
                : (closureCompilerFormatEnabled && method.isAbstract()) ?
                    JsUtils.createQualifiedNameRef(GOOG_ABSTRACT_METHOD, x.getSourceInfo()) : null;

        generateVTable(x, method, functionDefinition);
      }
    }

    private void generateVTable(JDeclaredType x, JMethod method, JsExpression functionDefinition) {
      if (functionDefinition != null) {
        generateVTableAssignment(method, polymorphicNames.get(method), functionDefinition);
      }

      if (method.exposesNonJsMethod()) {
        JsName internalMangledName = interfaceScope.declareName(mangleNameForPoly(method),
            method.getName());
        generateVTableAlias(method, internalMangledName);
    }

      if (method.exposesPackagePrivateMethod()) {
        // Here is the situation where this is needed:
        //
        // class a.A { m() {} }
        // class b.B extends a.A { m() {} }
        // interface I { m(); }
        // class a.C {
        //  { A a = new b.B();  a.m() // calls A::m()} }
        //  { I i = new b.B();  a.m() // calls B::m()} }
        // }
        //
        // Up to this point it is clear that package private names need to be different than
        // public names.
        //
        // Add class a.D extends a.A implements I { public m() }
        //
        // a.D collapses A::m and I::m into the same function and it was clear that two
        // two different names were already needed, hence when creating the vtable for a.D
        // both names have to point to the same function.
        generateVTableAlias(method, getPackagePrivateName(method));
      }
    }

    public JsNameRef createJsQualifier(String qualifier, SourceInfo sourceInfo) {
      assert !qualifier.isEmpty();
      return JsUtils.createQualifiedNameRef("$wnd." + qualifier, sourceInfo);
    }

    /**
     * Returns either _ or ClassCtor.prototype depending on output mode.
     */
    private JsNameRef getPrototypeQualifierOf(JMember f) {
      return getPrototypeQualifierOf(f.getEnclosingType(), f.getSourceInfo());
    }

    /**
     * Returns either _ or ClassCtor.prototype depending on output mode.
     */
    private JsNameRef getPrototypeQualifierOf(JDeclaredType type, SourceInfo info) {
      if (closureCompilerFormatEnabled) {
        JsNameRef protoRef = prototype.makeRef(info);
        protoRef.setQualifier(names.get(type).makeRef(info));
        return protoRef;
      } else {
        return globalTemp.makeRef(info);
      }
    }

    private void collectExports(JDeclaredType x) {
      if (x.isJsType() && !x.getClassDisposition().isLocalType()) {
        // only types with explicit source names in Java may have an exported prototype
        exportedMembersByExportName.put(x.getQualifiedJsName(), x);
      }

      for (JMethod m : x.getMethods()) {
        if (m.isJsInteropEntryPoint()) {
          exportedMembersByExportName.put(m.getQualifiedJsName(), m);
        }
      }

      for (JField f : x.getFields()) {
        if (f.isJsInteropEntryPoint()) {
          if (!f.isFinal()) {
            logger.log(TreeLogger.Type.WARN, "Exporting effectively non-final field "
                + f.getQualifiedName() + ". Due to the way exporting works, the value of the"
                + " exported field will not be reflected across Java/JavaScript border.");
          }
          exportedMembersByExportName.put(f.getQualifiedJsName(), f);
        }
      }
    }

    /**
     * Returns the package private JsName for {@code method}.
     */
    private JsName getPackagePrivateName(JMethod method) {
      for (JMethod overridenMethod : method.getOverriddenMethods()) {
        if (overridenMethod.isPackagePrivate()) {
          JsName name = polymorphicNames.get(overridenMethod);
          assert name != null;
          return name;
        }
      }
      throw new AssertionError(
          method.toString() + " overrides a package private method but was not found.");
    }

    private void handleClinit(JsFunction clinitFunc, JsFunction superClinit) {
      clinitFunc.setSuperClinit(superClinit);
      List<JsStatement> statements = clinitFunc.getBody().getStatements();
      SourceInfo sourceInfo = clinitFunc.getSourceInfo();
      // Self-assign to the global noop method immediately (to prevent reentrancy). In incremental
      // mode the more costly Object constructor function is used as the noop method since doing so
      // provides a better debug experience that does not step into already used clinits.

      JsFunction emptyFunctionFn = incremental ? objectConstructorFunction
          : indexedFunctions.get("JavaClassHierarchySetupUtil.emptyMethod");
      JsExpression asg = createAssignment(clinitFunc.getName().makeRef(sourceInfo),
          emptyFunctionFn.getName().makeRef(sourceInfo));
      statements.add(0, asg.makeStmt());
    }

    private boolean isMethodPotentiallyCalledAcrossClasses(JMethod method) {
      assert incremental || crossClassTargets != null;
      return crossClassTargets == null || crossClassTargets.contains(method)
          || method.isJsInteropEntryPoint();
    }

    private Iterable<JMethod> getPotentiallyAliveConstructors(JDeclaredType x) {
      return Iterables.filter(x.getMethods(), new Predicate<JMethod>() {
        @Override
        public boolean apply(JMethod m) {
          return isMethodPotentiallyALiveConstructor(m);
        }
      });
    }

    /**
     * Whether a method is a constructor that is actually newed. Note that in absence of whole
     * world knowledge evey constructor is potentially live.
     */
    private boolean isMethodPotentiallyALiveConstructor(JMethod method) {
      if (!(method instanceof JConstructor)) {
        return false;
      }
      assert incremental || liveCtors != null;
      return liveCtors == null || liveCtors.contains(method);
    }

    private JsInvocation maybeCreateClinitCall(JField x, boolean isExported) {
      if (!x.isStatic() || x.isCompileTimeConstant()) {
        // Access to compile time constants do not trigger class initialization (JLS 12.4.1).
        return null;
      }

      JDeclaredType targetType = x.getEnclosingType().getClinitTarget();
      if (targetType == null
          || targetType.equals(program.getTypeClassLiteralHolder())
          || !isExported && (currentMethod == null
               || !currentMethod.getEnclosingType().checkClinitTo(targetType))) {
        return null;
      }

      JMethod clinitMethod = targetType.getClinitMethod();
      SourceInfo sourceInfo = x.getSourceInfo();
      return new JsInvocation(sourceInfo, names.get(clinitMethod).makeRef(sourceInfo));
    }

    private JsInvocation maybeCreateClinitCall(JMethod x) {
      if (!isMethodPotentiallyCalledAcrossClasses(x)) {
        // Global optimized compile can prune some clinit calls.
        return null;
      }
      JDeclaredType enclosingType = x.getEnclosingType();
      if (x.canBePolymorphic() || (program.isStaticImpl(x) &&
          !enclosingType.isJsoType())) {
        return null;
      }
      if (enclosingType == null || !enclosingType.hasClinit()) {
        return null;
      }
      // Avoid recursion sickness.
      if (JProgram.isClinit(x)) {
        return null;
      }

      JMethod clinitMethod = enclosingType.getClinitTarget().getClinitMethod();
      SourceInfo sourceInfo = x.getSourceInfo();
      return new JsInvocation(sourceInfo, names.get(clinitMethod).makeRef(sourceInfo));
    }

    /**
     * If a field is a literal, we can potentially treat it as immutable and assign it once on the
     * prototype, to be reused by all instances of the class, instead of re-assigning the same
     * literal in each constructor.
     *
     * Technically, to match JVM semantics, we should only do this for final or static fields. For
     * non-final/non-static fields, a super class's cstr, when it calls a polymorphic method that is
     * overridden in the subclass, should actually see default values (not the literal initializer)
     * before the subclass's cstr runs.
     *
     * However, cstr's calling polymorphic methods is admittedly an uncommon case, so we apply some
     * heuristics to see if we can initialize the field on the prototype anyway.
     */
    private boolean initializeAtTopScope(JField x) {
      if (x.getLiteralInitializer() == null) {
        return false;
      }
      if (x.isFinal() || x.isStatic() || x.isCompileTimeConstant()) {
        // we can definitely initialize at top-scope, as JVM does so as well
        return true;
      }

      return !uninitializedValuePotentiallyObservable.apply(x);
    }

    /**
     * Helpers to avoid casting (can be removed when compiling in Java 8).
     */
    private <T extends JsExpression> T transform(JExpression expression) {
      return transform((JNode) expression);
    }

    private <T extends JsStatement> T transform(JStatement statement) {
      return transform((JNode) statement);
    }

    private JsBlock transform(JBlock statement) {
      return transform((JNode) statement);
    }
  }

  private void addVarsIfNotEmpty(JsVars vars) {
    if (!vars.isEmpty()) {
      getGlobalStatements().add(vars);
    }
  }

  private List<JsStatement> getGlobalStatements() {
    return jsProgram.getGlobalBlock().getStatements();
  }

  /**
   * Return false if the methods need to be generated. Some methods do not need any output,
   * in particular abstract methods and static intializers that are never called.
   */
  private static boolean shouldNotEmitMethodImplementation(JMethod x) {
    return x.isAbstract();
  }

  private static class JavaToJsOperatorMap {
    private static final Map<JBinaryOperator, JsBinaryOperator> bOpMap =
        Maps.newEnumMap(JBinaryOperator.class);
    private static final Map<JUnaryOperator, JsUnaryOperator> uOpMap =
        Maps.newEnumMap(JUnaryOperator.class);

    static {
      bOpMap.put(JBinaryOperator.MUL, JsBinaryOperator.MUL);
      bOpMap.put(JBinaryOperator.DIV, JsBinaryOperator.DIV);
      bOpMap.put(JBinaryOperator.MOD, JsBinaryOperator.MOD);
      bOpMap.put(JBinaryOperator.ADD, JsBinaryOperator.ADD);
      bOpMap.put(JBinaryOperator.CONCAT, JsBinaryOperator.ADD);
      bOpMap.put(JBinaryOperator.SUB, JsBinaryOperator.SUB);
      bOpMap.put(JBinaryOperator.SHL, JsBinaryOperator.SHL);
      bOpMap.put(JBinaryOperator.SHR, JsBinaryOperator.SHR);
      bOpMap.put(JBinaryOperator.SHRU, JsBinaryOperator.SHRU);
      bOpMap.put(JBinaryOperator.LT, JsBinaryOperator.LT);
      bOpMap.put(JBinaryOperator.LTE, JsBinaryOperator.LTE);
      bOpMap.put(JBinaryOperator.GT, JsBinaryOperator.GT);
      bOpMap.put(JBinaryOperator.GTE, JsBinaryOperator.GTE);
      bOpMap.put(JBinaryOperator.EQ, JsBinaryOperator.EQ);
      bOpMap.put(JBinaryOperator.NEQ, JsBinaryOperator.NEQ);
      bOpMap.put(JBinaryOperator.BIT_AND, JsBinaryOperator.BIT_AND);
      bOpMap.put(JBinaryOperator.BIT_XOR, JsBinaryOperator.BIT_XOR);
      bOpMap.put(JBinaryOperator.BIT_OR, JsBinaryOperator.BIT_OR);
      bOpMap.put(JBinaryOperator.AND, JsBinaryOperator.AND);
      bOpMap.put(JBinaryOperator.OR, JsBinaryOperator.OR);
      bOpMap.put(JBinaryOperator.ASG, JsBinaryOperator.ASG);
      bOpMap.put(JBinaryOperator.ASG_ADD, JsBinaryOperator.ASG_ADD);
      bOpMap.put(JBinaryOperator.ASG_CONCAT, JsBinaryOperator.ASG_ADD);
      bOpMap.put(JBinaryOperator.ASG_SUB, JsBinaryOperator.ASG_SUB);
      bOpMap.put(JBinaryOperator.ASG_MUL, JsBinaryOperator.ASG_MUL);
      bOpMap.put(JBinaryOperator.ASG_DIV, JsBinaryOperator.ASG_DIV);
      bOpMap.put(JBinaryOperator.ASG_MOD, JsBinaryOperator.ASG_MOD);
      bOpMap.put(JBinaryOperator.ASG_SHL, JsBinaryOperator.ASG_SHL);
      bOpMap.put(JBinaryOperator.ASG_SHR, JsBinaryOperator.ASG_SHR);
      bOpMap.put(JBinaryOperator.ASG_SHRU, JsBinaryOperator.ASG_SHRU);
      bOpMap.put(JBinaryOperator.ASG_BIT_AND, JsBinaryOperator.ASG_BIT_AND);
      bOpMap.put(JBinaryOperator.ASG_BIT_OR, JsBinaryOperator.ASG_BIT_OR);
      bOpMap.put(JBinaryOperator.ASG_BIT_XOR, JsBinaryOperator.ASG_BIT_XOR);

      uOpMap.put(JUnaryOperator.INC, JsUnaryOperator.INC);
      uOpMap.put(JUnaryOperator.DEC, JsUnaryOperator.DEC);
      uOpMap.put(JUnaryOperator.NEG, JsUnaryOperator.NEG);
      uOpMap.put(JUnaryOperator.NOT, JsUnaryOperator.NOT);
      uOpMap.put(JUnaryOperator.BIT_NOT, JsUnaryOperator.BIT_NOT);
    }

    public static JsBinaryOperator get(JBinaryOperator op) {
      return bOpMap.get(op);
    }

    public static JsUnaryOperator get(JUnaryOperator op) {
      return uOpMap.get(op);
    }
  }

  private class CollectJsFunctionsForInlining extends JVisitor {

    // JavaScript functions that arise from methods that were not inlined in the Java AST
    // NOTE: We use a LinkedHashSet to preserve the order of insertion. So that the following passes
    // that use this result are deterministic.
    private Set<JsNode> functionsForJsInlining = Sets.newLinkedHashSet();
    private JMethod currentMethod;

    @Override
    public void endVisit(JMethod x, Context ctx) {
      if (x.isNative()) {
        // These are methods whose bodies where not traversed by the Java method inliner.
        JsFunction function = jsFunctionsByJavaMethodBody.get(x.getBody());
        if (function != null && function.getBody() != null) {
          functionsForJsInlining.add(function);
        }
        // Add all functions declared inside JSNI blocks as well.
        assert function != null;
        new JsModVisitor() {
          @Override
          public void endVisit(JsFunction x, JsContext ctx) {
            functionsForJsInlining.add(x);
          }
        }.accept(function);
      }

      currentMethod = null;
    }

    @Override
    public void endVisit(JMethodCall x, Context ctx) {
      JMethod target = x.getTarget();
      if (target.isInliningAllowed() && (target.isNative()
          || program.getIndexedTypes().contains(target.getEnclosingType())
          || target.getInliningMode() == InliningMode.FORCE_INLINE)) {
        // These are either: 1) callsites to JSNI functions, in which case MethodInliner did not
        // attempt to inline; 2) inserted by normalizations passes AFTER all inlining or 3)
        // calls to methods annotated with @ForceInline that were not inlined by the simple
        // MethodInliner.
        JsFunction function = jsFunctionsByJavaMethodBody.get(currentMethod.getBody());
        if (function != null && function.getBody() != null) {
          functionsForJsInlining.add(function);
        }
      }
    }

    @Override
    public boolean visit(JMethod x, Context ctx) {
      currentMethod = x;
      return true;
    }

    public Set<JsNode> getFunctionsForJsInlining() {
      accept(program);
      return functionsForJsInlining;
    }
  }

  /**
   * Computes:<p>
   * <ul>
   * <li> 1. whether a constructors are live directly (through being in a new operation) or
   * indirectly (only called by other constructors). Only directly live constructors become
   * JS constructor, otherwise they will behave like regular static functions.
   * </li> 2. whether there exists cross class (static) calls or accesses that would need clinits to
   * be triggered. If not clinits need only be called in constructors.
   * <li>
   * </li>
   * </ul>
   */
  private class RecordCrossClassCallsAndConstructorLiveness extends JVisitor {
    // TODO(rluble): This analysis should be extracted from GenerateJavaScriptAST into its own
    // JAVA optimization pass. Constructors that are not newed can be transformed into statified
    // regular methods; and methods that are not called from outside the class boundary can be
    // privatized. Currently we do not use the private modifier to avoid emitting clinits, instead
    // we use the result of this analysis (private methods CAN be called from JSNI in an unrelated
    // class, touche!).
    {
      crossClassTargets =  Sets.newHashSet();
      liveCtors = Sets.newIdentityHashSet();
    }

    private JMethod currentMethod;

    @Override
    public void endVisit(JMethod x, Context ctx) {
      // methods which are exported or static indexed methods may be called externally
      if (x.isJsInteropEntryPoint()
          || (x.isStatic() && program.getIndexedMethods().contains(x))) {
        if (x instanceof JConstructor) {
          // exported ctors always considered live
          liveCtors.add((JConstructor) x);
        }
        // could be called from JS, so clinit must be called from body
        crossClassTargets.add(x);
      }
      currentMethod = null;
    }

    @Override
    public void endVisit(JMethodCall x, Context ctx) {
      JDeclaredType sourceType = currentMethod.getEnclosingType();
      JDeclaredType targetType = x.getTarget().getEnclosingType();
      if (sourceType.checkClinitTo(targetType)) {
        crossClassTargets.add(x.getTarget());
      }
    }

    @Override
    public void endVisit(JNewInstance x, Context ctx) {
      super.endVisit(x, ctx);
      liveCtors.add(x.getTarget());
    }

    @Override
    public void endVisit(JProgram x, Context ctx) {
      // Entry methods can be called externally, so they must run clinit.
      crossClassTargets.addAll(x.getEntryMethods());
    }

    @Override
    public void endVisit(JsniMethodRef x, Context ctx) {
      if (x.getTarget() instanceof JConstructor) {
        liveCtors.add((JConstructor) x.getTarget());
      }

      endVisit((JMethodCall) x, ctx);
    }

    @Override
    public boolean visit(JMethod x, Context ctx) {
      currentMethod = x;
      return true;
    }
  }

  private static class SortVisitor extends JVisitor {

    @Override
    public void endVisit(JClassType x, Context ctx) {
      x.sortFields(HasName.BY_NAME_COMPARATOR);
      x.sortMethods(JMethod.BY_SIGNATURE_COMPARATOR);
    }

    @Override
    public void endVisit(JInterfaceType x, Context ctx) {
      x.sortFields(HasName.BY_NAME_COMPARATOR);
      x.sortMethods(JMethod.BY_SIGNATURE_COMPARATOR);
    }

    @Override
    public void endVisit(JMethodBody x, Context ctx) {
      x.sortLocals(HasName.BY_NAME_COMPARATOR);
    }

    @Override
    public void endVisit(JProgram x, Context ctx) {
      Collections.sort(x.getEntryMethods(), JMethod.BY_SIGNATURE_COMPARATOR);
      Collections.sort(x.getDeclaredTypes(), HasName.BY_NAME_COMPARATOR);
    }

    @Override
    public boolean visit(JMethodBody x, Context ctx) {
      // No need to visit method bodies.
      return false;
    }
  }

  /**
   * This is the main entry point for the translation from Java to JavaScript. Starts from a
   * Java AST and constructs a JavaScript AST while collecting other useful information that
   * is used in subsequent passes.
   *
   * @param logger            a TreeLogger
   * @param program           a Java AST
   * @param jsProgram         an (empty) JavaScript AST
   * @param symbolTable       an (empty) symbol table that will be populated here
   *
   * @return A pair containing a JavaToJavaScriptMap and a Set of JsFunctions that need to be
   *         considered for inlining.
   */
  public static Pair<JavaToJavaScriptMap, Set<JsNode>> exec(TreeLogger logger, JProgram program,
      JsProgram jsProgram, CompilerContext compilerContext, TypeMapper<?> typeMapper,
      Map<StandardSymbolData, JsName> symbolTable, PermutationProperties props) {

    Event event = SpeedTracerLogger.start(CompilerEventType.GENERATE_JS_AST);
    try {
      GenerateJavaScriptAST generateJavaScriptAST = new GenerateJavaScriptAST(logger, program,
          jsProgram, compilerContext, typeMapper, symbolTable, props);
      return generateJavaScriptAST.execImpl();
    } finally {
      event.end();
    }
  }

  private static final ImmutableList<String> METHODS_PROVIDED_BY_PREAMBLE = ImmutableList.of(
      "Class.createForClass", "Class.createForPrimitive", "Class.createForInterface",
      "Class.createForEnum");

  private final Map<JBlock, JsCatch> catchMap = Maps.newIdentityHashMap();

  private final Set<JsName> catchParamIdentifiers = Sets.newHashSet();

  private final Map<JClassType, JsScope> classScopes = Maps.newIdentityHashMap();

  /**
   * A list of methods that are called from another class (ie might need to
   * clinit).
   */
  private Set<JMethod> crossClassTargets = null;

  private Map<String, JsFunction> indexedFunctions = Maps.newHashMap();

  private Map<String, JsName> indexedFields = Maps.newHashMap();

  /**
   * Contains JsNames for all interface methods. A special scope is needed so
   * that independent classes will obfuscate their interface implementation
   * methods the same way.
   */
  private final JsScope interfaceScope;

  private final JsProgram jsProgram;

  private Set<JConstructor> liveCtors = null;

  /**
   * Classes that could potentially see uninitialized values for fields that are initialized in the
   * declaration.
   */
  private Predicate<JField> uninitializedValuePotentiallyObservable;

  private final Map<JAbstractMethodBody, JsFunction> jsFunctionsByJavaMethodBody =
      Maps.newIdentityHashMap();
  private final Map<HasName, JsName> names = Maps.newIdentityHashMap();

  /**
   * Contains JsNames for the Object instance methods, such as equals, hashCode,
   * and toString. All other class scopes have this scope as an ultimate parent.
   */
  private final JsScope objectScope;
  private final Map<JMethod, JsName> polymorphicNames = Maps.newIdentityHashMap();
  private final JProgram program;

  /**
   * SEt of all targets of JNameOf.
   */
  private Set<HasName> nameOfTargets = Sets.newHashSet();

  private final TreeLogger logger;

  /**
   * Maps JsNames to machine-usable identifiers.
   */
  private final Map<StandardSymbolData, JsName> symbolTable;

  /**
   * Contains JsNames for all globals, such as static fields and methods.
   */
  private final JsScope topScope;

  private final Map<JsStatement, JDeclaredType> javaTypeByGlobalStatement = Maps.newHashMap();

  private final Map<JsStatement, JMethod> methodByGlobalStatement = Maps.newHashMap();

  private final TypeMapper<?> typeMapper;

  private final MinimalRebuildCache minimalRebuildCache;

  private final PermutationProperties properties;

  private JsFunction objectConstructorFunction;

  private OptionMethodNameDisplayMode.Mode methodNameMappingMode;

  private final boolean closureCompilerFormatEnabled;

  private final boolean optimize;

  // This is also used to do some final optimizations.
  // TODO(rluble) move optimizations to a Java AST optimization pass.
  private final boolean incremental;

  /**
   * If true, polymorphic functions are made anonymous vtable declarations and
   * not assigned topScope identifiers.
   */
  private final boolean stripStack;

  private GenerateJavaScriptAST(TreeLogger logger, JProgram program, JsProgram jsProgram,
      CompilerContext compilerContext, TypeMapper<?> typeMapper,
      Map<StandardSymbolData, JsName> symbolTable, PermutationProperties properties) {
    this.logger = logger;
    this.program = program;
    this.jsProgram = jsProgram;
    this.topScope = jsProgram.getScope();
    this.objectScope = jsProgram.getObjectScope();
    this.interfaceScope = new JsNormalScope(objectScope, "Interfaces");
    this.minimalRebuildCache = compilerContext.getMinimalRebuildCache();
    this.symbolTable = symbolTable;
    this.typeMapper = typeMapper;
    this.properties = properties;

    PrecompileTaskOptions options = compilerContext.getOptions();
    this.optimize = options.getOptimizationLevel() > OptionOptimize.OPTIMIZE_LEVEL_DRAFT;
    this.methodNameMappingMode = options.getMethodNameDisplayMode();
    assert methodNameMappingMode != null;
    this.incremental = options.isIncrementalCompileEnabled();

    this.stripStack = JsStackEmulator.getStackMode(properties) == JsStackEmulator.StackMode.STRIP;
    this.closureCompilerFormatEnabled = options.isClosureCompilerFormatEnabled();
    this.objectConstructorFunction =
        new JsFunction(SourceOrigin.UNKNOWN, topScope, topScope.findExistingName("Object"));
  }

  /**
   * Retrieves the runtime typeId for {@code type}.
   */
  JExpression getRuntimeTypeReference(JReferenceType type) {
    Object typeId = typeMapper.get(type);
    if (typeId == null) {
      return null;
    }
    if (typeId instanceof JMethodCall) {
      return (JMethodCall) typeId;
    }
    return program.getLiteral(typeId);
  }

  private String mangleName(JField x) {
    return JjsUtils.mangleMemberName(x.getEnclosingType().getName(), x.getName());
  }

  private String mangleNameForGlobal(JMethod x) {
    String s = JjsUtils.mangleMemberName(x.getEnclosingType().getName(), x.getName()) + "__";
    for (JType type : x.getOriginalParamTypes()) {
      s += type.getJavahSignatureName();
    }
    s += x.getOriginalReturnType().getJavahSignatureName();
    return StringInterner.get().intern(s);
  }

  private String mangleNameForPackagePrivatePoly(JMethod x) {
    assert x.isPackagePrivate() && !x.isStatic();
    /*
     * Package private instance methods in different package should not override each
     * other, so they must have distinct polymorphic names. Therefore, add the
     * package to the mangled name.
     */
    String mangledName = Joiner.on("$").join(
        "package_private",
        JjsUtils.mangledNameString(x.getEnclosingType().getPackageName()),
        JjsUtils.mangledNameString(x));
    return StringInterner.get().intern(JjsUtils.constructManglingSignature(x, mangledName));
  }

  private String mangleNameForPoly(JMethod x) {
    assert !x.isPrivate() && !x.isStatic();

    return StringInterner.get().intern(
        JjsUtils.constructManglingSignature(x, JjsUtils.mangledNameString(x)));
  }

  private String mangleNameForPrivatePoly(JMethod x) {
    assert x.isPrivate() && !x.isStatic();
    /*
     * Private instance methods in different classes should not override each
     * other, so they must have distinct polymorphic names. Therefore, add the
     * class name to the mangled name.
     */
    String mangledName = Joiner.on("$").join(
        "private",
        JjsUtils.mangledNameString(x.getEnclosingType()),
        JjsUtils.mangledNameString(x));

    return StringInterner.get().intern(JjsUtils.constructManglingSignature(x, mangledName));
  }

  private final Map<JType, JDeclarationStatement> classLiteralDeclarationsByType =
      Maps.newLinkedHashMap();

  private void contructTypeToClassLiteralDeclarationMap() {
      /*
       * Must execute in clinit statement order, NOT field order, so that back
       * refs to super classes are preserved.
       */
    JMethodBody clinitBody =
        (JMethodBody) program.getTypeClassLiteralHolder().getClinitMethod().getBody();
    for (JStatement stmt : clinitBody.getStatements()) {
      if (!(stmt instanceof JDeclarationStatement)) {
        continue;
      }
      JDeclarationStatement classLiteralDeclaration = (JDeclarationStatement) stmt;

      JType type = program.getTypeByClassLiteralField(
          (JField) ((JDeclarationStatement) stmt).getVariableRef().getTarget());

      assert !classLiteralDeclarationsByType.containsKey(type);
      classLiteralDeclarationsByType.put(type, classLiteralDeclaration);
    }
  }

  private Pair<JavaToJavaScriptMap, Set<JsNode>> execImpl() {
    uninitializedValuePotentiallyObservable = optimize
        ? ComputePotentiallyObservableUninitializedValues.analyze(program)
        : Predicates.<JField>alwaysTrue();
    new FindNameOfTargets().accept(program);
    new SortVisitor().accept(program);
    if (!incremental) {
      // TODO(rluble): pull out this analysis and make it a Java AST optimization pass.
      new RecordCrossClassCallsAndConstructorLiveness().accept(program);
    }

    // Map class literals to their respective types.
    contructTypeToClassLiteralDeclarationMap();

    new CreateNamesAndScopesVisitor().accept(program);
    new GenerateJavaScriptTransformer().transform(program);

    jsProgram.setIndexedFields(indexedFields);
    jsProgram.setIndexedFunctions(indexedFunctions);

    // TODO(spoon): Instead of gathering the information here, get it via
    // SourceInfo
    JavaToJavaScriptMap jjsMap = new JavaToJavaScriptMapImpl(program.getDeclaredTypes(),
        names, javaTypeByGlobalStatement, methodByGlobalStatement);

    Set<JsNode> functionsForJsInlining = incremental ? Collections.<JsNode>emptySet() :
        new CollectJsFunctionsForInlining().getFunctionsForJsInlining();

    return Pair.create(jjsMap, functionsForJsInlining);
  }

  private JsFunction getJsFunctionFor(JMethod jMethod) {
    return jsFunctionsByJavaMethodBody.get(jMethod.getBody());
  }
}
