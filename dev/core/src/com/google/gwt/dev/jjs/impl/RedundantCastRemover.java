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
import com.google.gwt.dev.jjs.ast.JPostfixOperation;
import com.google.gwt.dev.jjs.ast.JPrefixOperation;
import com.google.gwt.dev.jjs.ast.JProgram;
import com.google.gwt.dev.jjs.ast.JStatement;
import com.google.gwt.dev.jjs.ast.JType;
import com.google.gwt.dev.jjs.ast.JVariable;
import com.google.gwt.dev.jjs.ast.JVariableRef;
import com.google.gwt.dev.jjs.ast.JWhileStatement;
import com.google.gwt.thirdparty.guava.common.collect.HashMultimap;
import com.google.gwt.thirdparty.guava.common.collect.Multimap;
import com.google.gwt.thirdparty.guava.common.collect.Sets;

import java.util.Map.Entry;
import java.util.Set;
import java.util.Stack;

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
  public class RemoveRedundantCastVisitor extends JModVisitor {
    /**
     * Stack that is used to keep track of the types of variable indicated by instanceof.
     * Effective indicated types are pushed in at branch point (e.g. ifExpr in IfStatement)),
     * and are popped out at join point (e.g. end of IfStatement/WhileStatement etc.)
     */
    private final Stack<Multimap<JVariable, JType>> indicatedTypesOfVariableAtBranchPoints =
        new Stack<Multimap<JVariable, JType>>();

    /**
     * Stack that is used to keep track of the definitions in different statements.
     */
    private final Stack<Set<JVariable>> definitions = new Stack<Set<JVariable>>();

    /**
     * Effective indicated types of variable at current statement.
     */
    private Multimap<JVariable, JType> indicatedTypesOfVariableAtCurrentStmt =
        HashMultimap.create();

    @Override
    public void endVisit(JCastOperation x, Context ctx) {
      if (x.getExpr() instanceof JVariableRef) {
        JVariable target = ((JVariableRef) x.getExpr()).getTarget();
        if (indicatedTypesOfVariableAtCurrentStmt.get(target).contains(x.getCastType())) {
          ctx.replaceMe(x.getExpr());
        }
      }
    }

    /**
     * definitions.peek() gives the definition in current statement. definitions.pop().peek() gives
     * the definitions in the statement that the current statement resides in. So when we do a pop,
     * we add the definitions that is popped out to the new peek.
     */
    @Override
    public void endVisit(JStatement x, Context ctx) {
      Set<JVariable> definitionsInCurrentStmt = definitions.pop();
      computeIndicatedTypes(definitionsInCurrentStmt, indicatedTypesOfVariableAtCurrentStmt);
      definitions.peek().addAll(definitionsInCurrentStmt);
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
      x.setIfTest(accept(x.getIfTest()));

      indicatedTypesOfVariableAtBranchPoints.push(
          HashMultimap.create(indicatedTypesOfVariableAtCurrentStmt));

      // add the new indicated types by the IfExpr.
      indicatedTypesOfVariableAtCurrentStmt.putAll(getTestTypesByVariable(x.getIfTest()));
      if (x.getThenExpr() != null) {
        x.setThenExpr(accept(x.getThenExpr()));
      }
      // recover the indicated types for the else branch.
      indicatedTypesOfVariableAtCurrentStmt = indicatedTypesOfVariableAtBranchPoints.peek();
      if (x.getElseExpr() != null) {
        x.setElseExpr(accept(x.getElseExpr()));
      }
      // recover the indicated types
      indicatedTypesOfVariableAtCurrentStmt = indicatedTypesOfVariableAtBranchPoints.pop();

      return false;
    }

    @Override
    public boolean visit(JForStatement x, Context ctx) {
      visit((JStatement) x, ctx);
      x.setInitializers(acceptWithInsertRemoveImmutable(x.getInitializers()));

      // push the indicatedTypes.
      indicatedTypesOfVariableAtBranchPoints.push(
          HashMultimap.create(indicatedTypesOfVariableAtCurrentStmt));

      // clear the indicatedTypes at current statement, add new indicated types by testExpr.
      // Since the while statement is a loop, definitions in each statement in the loop may
      // be reachable to other statements in the loop, so only keep the indicated types by
      // the testExpr.
      indicatedTypesOfVariableAtCurrentStmt.clear();
      if (x.getCondition() != null) {
        x.setCondition(accept(x.getCondition()));
      }
      indicatedTypesOfVariableAtCurrentStmt.putAll(getTestTypesByVariable(x.getCondition()));

      if (x.getIncrements() != null) {
        x.setIncrements(accept(x.getIncrements()));
      }
      if (x.getBody() != null) {
        x.setBody(accept(x.getBody()));
      }

      // recover indicated types.
      indicatedTypesOfVariableAtCurrentStmt = indicatedTypesOfVariableAtBranchPoints.pop();
      return false;
    }

    @Override
    public boolean visit(JIfStatement x, Context ctx) {
      visit((JStatement) x, ctx);
      x.setIfExpr(accept(x.getIfExpr()));

      // before go to the then branch, push the indicatedTypes.
      indicatedTypesOfVariableAtBranchPoints.push(
          HashMultimap.create(indicatedTypesOfVariableAtCurrentStmt));

      // add the new indicated types by the IfExpr.
      indicatedTypesOfVariableAtCurrentStmt.putAll(getTestTypesByVariable(x.getIfExpr()));
      if (x.getThenStmt() != null) {
        x.setThenStmt(accept(x.getThenStmt()));
      }
      // recover the indicated types before the IfExpr for the else branch.
      indicatedTypesOfVariableAtCurrentStmt = indicatedTypesOfVariableAtBranchPoints.peek();
      if (x.getElseStmt() != null) {
        x.setElseStmt(accept(x.getElseStmt()));
      }
      // recover the indicated types before the IfStatement, preparing for computing the
      // indicatedTypes after the IfStatement.
      indicatedTypesOfVariableAtCurrentStmt = indicatedTypesOfVariableAtBranchPoints.pop();

      return false;
    }

    @Override
    public boolean visit(JMethod x, Context ctx) {
      clear();
      // Initialize a sentinel value to avoid having to check for empty stack.
      indicatedTypesOfVariableAtBranchPoints.push(HashMultimap.<JVariable, JType> create());
      definitions.push(Sets.<JVariable> newLinkedHashSet());
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
      definitions.push(Sets.<JVariable> newLinkedHashSet());
      return true;
    }

    @Override
    public boolean visit(JWhileStatement x, Context ctx) {
      visit((JStatement) x, ctx);

      // push the indicatedTypes.
      indicatedTypesOfVariableAtBranchPoints.push(
          HashMultimap.create(indicatedTypesOfVariableAtCurrentStmt));

      // clear the indicatedTypes at current statement, add new indicated types by testExpr.
      // Since the while statement is a loop, definitions in each statement in the loop may
      // be reachable to other statements in the loop, so only keep the indicated types by
      // the testExpr.
      indicatedTypesOfVariableAtCurrentStmt.clear();
      x.setTestExpr(accept(x.getTestExpr()));
      indicatedTypesOfVariableAtCurrentStmt.putAll(getTestTypesByVariable(x.getTestExpr()));
      if (x.getBody() != null) {
        x.setBody(accept(x.getBody()));
      }

      // recover indicated types.
      indicatedTypesOfVariableAtCurrentStmt = indicatedTypesOfVariableAtBranchPoints.pop();
      return false;
    }

    private void clear() {
      indicatedTypesOfVariableAtBranchPoints.clear();
      indicatedTypesOfVariableAtCurrentStmt.clear();
      definitions.clear();
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
              ((JInstanceOf) expr).getTestType());
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
          for (Entry<JVariable, JType> typeOfVariable : resultLeft.entries()) {
            if (resultRight.containsEntry(typeOfVariable.getKey(), typeOfVariable.getValue())) {
              testTypesByVariable.put(typeOfVariable.getKey(), typeOfVariable.getValue());
            }
          }
        } else if (((JBinaryOperation) expr).getOp().equals(JBinaryOperator.ASG)) {
          testTypesByVariable.putAll(getTestTypesByVariable(((JBinaryOperation) expr).getRhs()));
        }
      }
      return testTypesByVariable;
    }

    private void recordDef(JExpression expr) {
      if (expr instanceof JVariableRef) {
        definitions.peek().add(((JVariableRef) expr).getTarget());
      }
    }

    /**
     * indicatedTypes (out) = indicatedTypes (in) - definitions.
     */
    private void computeIndicatedTypes(Set<JVariable> definitions,
        Multimap<JVariable, JType> indicatedTypes) {
      for (JVariable def : definitions) {
        if (indicatedTypes.containsKey(def)) {
          indicatedTypes.removeAll(def);
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
    RemoveRedundantCastVisitor removeRedundantCastVisitor = new RemoveRedundantCastVisitor();
    removeRedundantCastVisitor.accept(program);
    stats.recordModified(removeRedundantCastVisitor.getNumMods());
    return stats;
  }
}
