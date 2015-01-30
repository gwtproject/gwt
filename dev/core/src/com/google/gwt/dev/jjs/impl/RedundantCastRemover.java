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

import com.google.gwt.dev.jjs.ast.Context;
import com.google.gwt.dev.jjs.ast.JBinaryOperation;
import com.google.gwt.dev.jjs.ast.JBinaryOperator;
import com.google.gwt.dev.jjs.ast.JCastOperation;
import com.google.gwt.dev.jjs.ast.JConditional;
import com.google.gwt.dev.jjs.ast.JDeclarationStatement;
import com.google.gwt.dev.jjs.ast.JExpression;
import com.google.gwt.dev.jjs.ast.JForStatement;
import com.google.gwt.dev.jjs.ast.JIfStatement;
import com.google.gwt.dev.jjs.ast.JInstanceOf;
import com.google.gwt.dev.jjs.ast.JMethod;
import com.google.gwt.dev.jjs.ast.JModVisitor;
import com.google.gwt.dev.jjs.ast.JNode;
import com.google.gwt.dev.jjs.ast.JPostfixOperation;
import com.google.gwt.dev.jjs.ast.JPrefixOperation;
import com.google.gwt.dev.jjs.ast.JProgram;
import com.google.gwt.dev.jjs.ast.JStatement;
import com.google.gwt.dev.jjs.ast.JType;
import com.google.gwt.dev.jjs.ast.JVariable;
import com.google.gwt.dev.jjs.ast.JVariableRef;
import com.google.gwt.dev.jjs.ast.JWhileStatement;
import com.google.gwt.dev.util.collect.Stack;
import com.google.gwt.thirdparty.guava.common.collect.HashMultimap;
import com.google.gwt.thirdparty.guava.common.collect.Multimap;
import com.google.gwt.thirdparty.guava.common.collect.Sets;

import java.util.Arrays;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

/**
 * Remove redundant cast check after Instanceof. Currently only check the cast of JVariableRef.
 */
public class RedundantCastRemover {

  /**
   * Visitor for removing redundant cast checks after Instanceof.
   *
   * Remove the cast checks in the following cases:
   * 1. then branch in a IfStatement: if (a instanceof A) {(A)a;}
   * 2. then branch in Conditional: (a instanceof A) ? (A)a : ...;
   * 3. body in a WhileStatement: while (a instanceof A) { (A)a; }
   * 4. body in a ForStatement: for (; a instanceof A; ) {(A)a;}
   *
   * The types that are indicated by the test/condition expressions in the above cases are killed after
   * any definitions of the variable. So we have the following flow function:
   * indicatedType(out) = indicatedType(in) - def.
   * Since the analysis is based on AST but not CFG, the computation of definitions is conservative but
   * not so precise. For example, we encapsulate control flow statement ({if, while, for, switch} with
   * {break, continue, etc.}) in one node, then the AST seems like a acyclic graph in which
   * each node has only one entry and one exit, like this: N1->N2->N3. Then we go through the AST once
   * and compute the indicated types at each statement.
   *
   * Since the 'break' and 'continue' are encapsulated, the analysis may not be precise in some cases.
   * For example, while(s0) {s1; if(s2){s3; break;} s4;}, definitions in s3 are reachable to s4, which
   * should not be.
   */
  public class FindAndRemoveRedundantCastVisitor extends ReplaceRedundantCastVisitor {
    /**
     * Stack that is used to keep track of the types of variable indicated by instanceof.
     * Effective indicated types are pushed in at branch point (e.g. ifExpr in IfStatement)),
     * and are popped out at join point (e.g. end of IfStatement/WhileStatement etc.)
     */
    private Stack<Multimap<JVariable, JType>> inferredTypesByVariableStack = Stack.create();

    /**
     * Stack that is used to keep track of the definitions in different statements.
     */
    private Stack<Set<JVariable>> definitionStack = Stack.create();

    /**
     * At the first time we traverse a JForStatement, we ignore all the inferred types by its parent
     * block due to its loop. After traverse the statement, we
     * re-traverse it to remove the leftover redundant cast checks.
     */
    @Override
    public void endVisit(JForStatement x, Context ctx) {
      endVisit((JStatement) x, ctx);
      ReplaceRedundantCastVisitor redundantCastReplacer = new ReplaceRedundantCastVisitor();
      redundantCastReplacer.inferredTypesByVariable = inferredTypesByVariable;
      redundantCastReplacer.accept(x);
    }

    /**
     * Similar to JForStatement.
     */
    @Override
    public void endVisit(JWhileStatement x, Context ctx) {
      endVisit((JStatement) x, ctx);
      ReplaceRedundantCastVisitor redundantCastReplacer = new ReplaceRedundantCastVisitor();
      redundantCastReplacer.inferredTypesByVariable = inferredTypesByVariable;
      redundantCastReplacer.accept(x);
    }

    /**
     * definitions.peek() gives the definition in current statement. definitions.pop().peek() gives
     * the definitions in the statement that the current statement resides in. So when we do a pop,
     * we add the definitions that is popped out to the new peek.
     */
    @Override
    public void endVisit(JStatement x, Context ctx) {
      Set<JVariable> definitionsInCurrentStmt = definitionStack.pop();
      updateIndicatedTypes(definitionsInCurrentStmt, inferredTypesByVariable);
      definitionStack.peek().addAll(definitionsInCurrentStmt);
    }

    @Override
    public boolean visit(JBinaryOperation x, Context ctx) {
      if (x.isAssignment()) {
        recordDef(x.getLhs());
      }
      return true;
    }

    @Override
    public boolean visit(JDeclarationStatement x, Context ctx) {
      visit((JStatement) x, ctx);
      recordDef(x.getVariableRef());
      return true;
    }

    @Override
    public boolean visit(JConditional x, Context ctx) {
      List<JNode> conditionalBranches =
          Arrays.<JNode> asList(x.getIfTest(), x.getThenExpr(), x.getElseExpr());
      traverseConditionalBranches(conditionalBranches);
      ctx.replaceMe(new JConditional(x.getSourceInfo(), x.getType(),
          (JExpression) conditionalBranches.get(0), (JExpression) conditionalBranches.get(1),
          (JExpression) conditionalBranches.get(2)));
      return false;
    }

    @Override
    public boolean visit(JForStatement x, Context ctx) {
      visit((JStatement) x, ctx);
      List<JStatement> initializers = acceptWithInsertRemoveImmutable(x.getInitializers());

      // push the indicatedTypes.
      inferredTypesByVariableStack.push(
          HashMultimap.create(inferredTypesByVariable));

      // clear the indicatedTypes at current statement, add new indicated types by testExpr.
      // Since the fo statement is a loop, definitions in each statement in the loop may
      // be reachable to other statements in the loop, so only keep the indicated types by
      // the testExpr.
      inferredTypesByVariable.clear();
      JExpression condition = null;
      if (x.getCondition() != null) {
        condition = accept(x.getCondition());
      }
      inferredTypesByVariable.putAll(getTestTypesByVariable(x.getCondition()));

      JExpression increments = null;
      if (x.getIncrements() != null) {
        increments = accept(x.getIncrements());
      }
      JStatement body = null;
      if (x.getBody() != null) {
        body = accept(x.getBody());
      }

      // recover indicated types.
      inferredTypesByVariable = inferredTypesByVariableStack.pop();
      ctx.replaceMe(new JForStatement(x.getSourceInfo(), initializers, condition, increments, body));
      return false;
    }

    @Override
    public boolean visit(JIfStatement x, Context ctx) {
      visit((JStatement) x, ctx);
      List<JNode> conditionalBranches =
          Arrays.asList(x.getIfExpr(), x.getThenStmt(), x.getElseStmt());
      traverseConditionalBranches(conditionalBranches);
      ctx.replaceMe(new JIfStatement(x.getSourceInfo(), (JExpression) conditionalBranches.get(0),
          (JStatement) conditionalBranches.get(1), (JStatement) conditionalBranches.get(2)));
      return false;
    }

    @Override
    public boolean visit(JMethod x, Context ctx) {
      clear();
      // Initialize a sentinel value to avoid having to check for empty stack.
      inferredTypesByVariableStack.push(HashMultimap.<JVariable, JType> create());
      definitionStack.push(Sets.<JVariable> newLinkedHashSet());
      return true;
    }

    @Override
    public boolean visit(JPostfixOperation x, Context ctx) {
      if (x.getOp().isModifying()) {
        recordDef(x.getArg());
      }
      return true;
    }

    @Override
    public boolean visit(JPrefixOperation x, Context ctx) {
      if (x.getOp().isModifying()) {
        recordDef(x.getArg());
      }
      return true;
    }

    @Override
    public boolean visit(JStatement x, Context ctx) {
      // push an empty set to record the definitions in the statement.
      definitionStack.push(Sets.<JVariable> newLinkedHashSet());
      return true;
    }

    @Override
    public boolean visit(JWhileStatement x, Context ctx) {
      visit((JStatement) x, ctx);

      // push the indicatedTypes.
      inferredTypesByVariableStack.push(
          HashMultimap.create(inferredTypesByVariable));

      // clear the indicatedTypes at current statement, add new indicated types by testExpr.
      // Since the while statement is a loop, definitions in each statement in the loop may
      // be reachable to other statements in the loop, so only keep the indicated types by
      // the testExpr.
      inferredTypesByVariable.clear();
      JExpression testExpr = accept(x.getTestExpr());
      inferredTypesByVariable.putAll(getTestTypesByVariable(x.getTestExpr()));
      JStatement body = null;
      if (x.getBody() != null) {
        body = accept(x.getBody());
      }

      // recover indicated types.
      inferredTypesByVariable = inferredTypesByVariableStack.pop();
      ctx.replaceMe(new JWhileStatement(x.getSourceInfo(), testExpr, body));
      return false;
    }

    private void clear() {
      inferredTypesByVariableStack = Stack.create();
      inferredTypesByVariable.clear();
      definitionStack = Stack.create();
    }

    /**
     * Return the indicated types of variable by {@code expr}.
     */
    private Multimap<JVariable, JType> getTestTypesByVariable(JExpression expr) {
      Multimap<JVariable, JType> testTypesByVariable = HashMultimap.create();
      if (expr == null) {
        return testTypesByVariable;
      }
      if (expr instanceof JInstanceOf) {
        if (((JInstanceOf) expr).getExpr() instanceof JVariableRef) {
          testTypesByVariable.put(((JVariableRef) ((JInstanceOf) expr).getExpr()).getTarget(),
              ((JInstanceOf) expr).getTestType().getUnderlyingType());
        }
      } else if (expr instanceof JBinaryOperation) {
        if (((JBinaryOperation) expr).getOp().equals(JBinaryOperator.AND)) {
          testTypesByVariable.putAll(getTestTypesByVariable(((JBinaryOperation) expr).getLhs()));
          testTypesByVariable.putAll(getTestTypesByVariable(((JBinaryOperation) expr).getRhs()));
        } else if (((JBinaryOperation) expr).getOp().equals(JBinaryOperator.OR)) {
          Multimap<JVariable, JType> resultLeft =
              getTestTypesByVariable(((JBinaryOperation) expr).getLhs());
          Multimap<JVariable, JType> resultRight =
              getTestTypesByVariable(((JBinaryOperation) expr).getRhs());
          multimapIntersection(testTypesByVariable, resultLeft, resultRight);
        } else if (((JBinaryOperation) expr).getOp().equals(JBinaryOperator.ASG)) {
          testTypesByVariable.putAll(getTestTypesByVariable(((JBinaryOperation) expr).getRhs()));
        }
      }
      return testTypesByVariable;
    }

    private void multimapIntersection(Multimap<JVariable, JType> intersectionResult,
        Multimap<JVariable, JType> resultLeft, Multimap<JVariable, JType> resultRight) {
      for (Entry<JVariable, JType> typeOfVariable : resultLeft.entries()) {
        if (resultRight.containsEntry(typeOfVariable.getKey(), typeOfVariable.getValue())) {
          intersectionResult.put(typeOfVariable.getKey(), typeOfVariable.getValue());
        }
      }
    }

    private void recordDef(JExpression expr) {
      if (expr instanceof JVariableRef) {
        definitionStack.peek().add(((JVariableRef) expr).getTarget());
      }
    }

    private void traverseConditionalBranches(List<JNode> conditionalBranches) {
      JExpression testExpr = (JExpression) conditionalBranches.get(0);
      JNode thenBranch = conditionalBranches.get(1);
      JNode elseBranch = conditionalBranches.get(2);

      conditionalBranches.set(0, accept(testExpr));

      // before go to the then branch, push the indicatedTypes.
      inferredTypesByVariableStack.push(HashMultimap.create(inferredTypesByVariable));

      // add the new indicated types by the testExpr.
      inferredTypesByVariable.putAll(getTestTypesByVariable(testExpr));
      if (thenBranch != null) {
        conditionalBranches.set(1, accept(thenBranch));
      }
      // recover the indicated types before the testExpr for the else branch.
      inferredTypesByVariable = inferredTypesByVariableStack.peek();
      if (elseBranch != null) {
        conditionalBranches.set(2, accept(elseBranch));
      }
      // recover the indicated types, preparing for computing the
      // indicatedTypes after the conditional branches.
      inferredTypesByVariable = inferredTypesByVariableStack.pop();
    }

    /**
     * indicatedTypes (out) = indicatedTypes (in) - definitions.
     */
    private void updateIndicatedTypes(Set<JVariable> definitions,
        Multimap<JVariable, JType> indicatedTypes) {
      for (JVariable def : definitions) {
        if (indicatedTypes.containsKey(def)) {
          indicatedTypes.removeAll(def);
        }
      }
    }
  }

  class ReplaceRedundantCastVisitor extends JModVisitor {
    /**
     * Effective indicated types of variable.
     */
    protected Multimap<JVariable, JType> inferredTypesByVariable =
        HashMultimap.create();

    @Override
    public void endVisit(JCastOperation x, Context ctx) {
      if (x.getExpr() instanceof JVariableRef) {
        JVariable target = ((JVariableRef) x.getExpr()).getTarget();
        if (inferredTypesByVariable.get(target).contains(x.getCastType().getUnderlyingType())) {
          if (!x.getExpr().getType().canBeNull()) {
            ctx.replaceMe(x.getExpr());
          }
        }
      }
    }
  }

  private static final String NAME = Pruner.class.getSimpleName();

  public static OptimizerStats exec(JProgram program) {
    return new RedundantCastRemover(program).execImpl();
  }

  private final JProgram program;

  public RedundantCastRemover(JProgram program) {
    this.program = program;
  }

  public OptimizerStats execImpl() {
    OptimizerStats stats = new OptimizerStats(NAME);
    FindAndRemoveRedundantCastVisitor redundantCastRemover = new FindAndRemoveRedundantCastVisitor();
    redundantCastRemover.accept(program);
    stats.recordModified(redundantCastRemover.getNumMods());
    return stats;
  }
}
