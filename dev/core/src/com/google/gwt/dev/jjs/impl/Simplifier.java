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

import com.google.gwt.dev.jjs.SourceInfo;
import com.google.gwt.dev.jjs.ast.JBinaryOperation;
import com.google.gwt.dev.jjs.ast.JBinaryOperator;
import com.google.gwt.dev.jjs.ast.JBlock;
import com.google.gwt.dev.jjs.ast.JBooleanLiteral;
import com.google.gwt.dev.jjs.ast.JCastOperation;
import com.google.gwt.dev.jjs.ast.JConditional;
import com.google.gwt.dev.jjs.ast.JExpression;
import com.google.gwt.dev.jjs.ast.JExpressionStatement;
import com.google.gwt.dev.jjs.ast.JIfStatement;
import com.google.gwt.dev.jjs.ast.JMethod;
import com.google.gwt.dev.jjs.ast.JNode;
import com.google.gwt.dev.jjs.ast.JPrefixOperation;
import com.google.gwt.dev.jjs.ast.JPrimitiveType;
import com.google.gwt.dev.jjs.ast.JReturnStatement;
import com.google.gwt.dev.jjs.ast.JStatement;
import com.google.gwt.dev.jjs.ast.JType;
import com.google.gwt.dev.jjs.ast.JUnaryOperation;
import com.google.gwt.dev.jjs.ast.JUnaryOperator;
import com.google.gwt.dev.jjs.ast.JValueLiteral;
import com.google.gwt.dev.jjs.ast.js.JMultiExpression;

import java.util.List;

/**
 * Methods that both construct and try to simplify AST nodes. If simplification
 * fails, then the methods will return an original, unmodified version of the
 * node if one is supplied. The routines do not recurse into their arguments;
 * the arguments are assumed to already be simplified as much as possible.
 */
public class Simplifier {
  /**
   * TODO: if the AST were normalized, we wouldn't need this.
   */
  public static boolean isEmpty(JStatement stmt) {
    if (stmt == null) {
      return true;
    }
    return (stmt instanceof JBlock && ((JBlock) stmt).getStatements().isEmpty());
  }

  /**
   * Negate the supplied expression if negating it makes the expression shorter.
   * Otherwise, return null.
   */
  private static JExpression maybeUnflipBoolean(JExpression expr) {
    if (expr instanceof JUnaryOperation) {
      JUnaryOperation unop = (JUnaryOperation) expr;
      if (unop.getOp() == JUnaryOperator.NOT) {
        return unop.getArg();
      }
    }
    return null;
  }

  private static <T> List<T> allButLast(List<T> list) {
    return list.subList(0, list.size() - 1);
  }

  private static <T> T last(List<T> list) {
    return list.get(list.size() - 1);
  }

  /**
   * This class provides only static methods. No instances will ever be created.
   */
  private Simplifier() {
  }

  /**
   * Simplify cast operations. Used when creating a cast in DeadCodeElimination. For simplifying
   * casts that are actually in the AST, cast(JCastOperation) is used instead.
   *
   * <pre>
   * (int) 1 -> 1
   * (A) (a,b) -> (a, (A) b)
   * </pre>
   *
   * @param type the Type to cast the expression <code>exp</code> to.
   * @param exp the current JExpression under the cast as it is being simplified.
   * @return the simplified expression.
   */
  public static JExpression cast(JType type, JExpression exp) {
    return castImpl(null, exp.getSourceInfo(), type, exp);
  }

  /**
   * Simplify cast operations.
   *
   * <pre>
   * (int) 1 -> 1
   * (A) (a,b) -> (a, (A) b)
   * </pre>
   *
   * @param exp a JCastOperation to be simplified.
   * @return the simplified expression if a simplification was possible; <code>exp</code> otherwise.
   */
  public static JExpression cast(JCastOperation exp) {
    return castImpl(exp, exp.getSourceInfo(), exp.getCastType(), exp.getExpr());
  }

  private static JExpression castImpl(JExpression original, SourceInfo info, JType type,
      JExpression exp) {
    info = getBestSourceInfo(original, info, exp);
    if (exp instanceof JMultiExpression) {
      // (T)(a,b,c) -> a,b,(T) c
      JMultiExpression expMulti = (JMultiExpression) exp;
      JMultiExpression newMulti = new JMultiExpression(info);
      newMulti.addExpressions(allButLast(expMulti.getExpressions()));
      newMulti.addExpressions(castImpl(null, info, type, last(expMulti.getExpressions())));
      // TODO(rluble): immediately simplify the resulting multi.
      // TODO(rluble): refactor common outward JMultiExpression movement.
      return newMulti;
    }
    if (type == exp.getType()) {
      return exp;
    }

    if ((type instanceof JPrimitiveType) && (exp instanceof JValueLiteral)) {
      // Statically evaluate casting literals.
      JPrimitiveType primitiveType = (JPrimitiveType) type;
      JValueLiteral expLit = (JValueLiteral) exp;
      JValueLiteral casted = primitiveType.coerce(expLit);
      if (casted != null) {
        return casted;
      }
    }

    /*
     * Discard casts from byte or short to int, because such casts are always
     * implicit anyway. Cannot coerce char since that would change the semantics
     * of concat.
     */
    if (type == JPrimitiveType.INT) {
      JType expType = exp.getType();
      if ((expType == JPrimitiveType.SHORT)
          || (expType == JPrimitiveType.BYTE)) {
        return exp;
      }
    }

    // no simplification made
    if (original != null) {
      return original;
    }
    return new JCastOperation(info, type, exp);
  }

  /**
   * Simplify conditional expressions.
   *
   * <pre>
   * (a,b,c)?d:e -> a,b,(c?d:e)
   * true ? then : else -> then
   * false ? then : else -> else
   * cond ? true : else) -> cond || else
   * cond ? false : else -> !cond && else
   * cond ? then : true -> !cond || then
   * cond ? then : false -> cond && then
   * !cond ? then : else -> cond ? else : then
   * </pre>
   *
   * @param exp a JCondintional to be simplified.
   * @return the simplified expression if a simplification was possible; <code>exp</code> otherwise.
   */
  public static JExpression conditional(JConditional exp) {
    return conditionalImpl(exp, exp.getSourceInfo(), exp.getType(), exp.getIfTest(),
        exp.getThenExpr(), exp.getElseExpr());
  }

  private static JExpression conditionalImpl(JConditional original, SourceInfo info, JType type,
      JExpression condExpr, JExpression thenExpr, JExpression elseExpr) {
    info = getBestSourceInfo(original, info, condExpr);
    if (condExpr instanceof JMultiExpression) {
      // (a,b,c)?d:e -> a,b,(c?d:e)
      // TODO(spoon): do this outward multi movement for all AST nodes
      JMultiExpression condMulti = (JMultiExpression) condExpr;
      JMultiExpression newMulti = new JMultiExpression(info);
      newMulti.addExpressions(allButLast(condMulti.getExpressions()));
      newMulti.addExpressions(conditionalImpl(null, info, type, last(condMulti.getExpressions()),
          thenExpr, elseExpr));
      // TODO(spoon): immediately simplify the resulting multi
      return newMulti;
    }
    if (condExpr instanceof JBooleanLiteral) {
      if (((JBooleanLiteral) condExpr).getValue()) {
        // e.g. (true ? then : else) -> then
        return thenExpr;
      } else {
        // e.g. (false ? then : else) -> else
        return elseExpr;
      }
    } else if (thenExpr instanceof JBooleanLiteral) {
      if (((JBooleanLiteral) thenExpr).getValue()) {
        // e.g. (cond ? true : else) -> cond || else
        return orImpl(null, info, condExpr, elseExpr);
      } else {
        // e.g. (cond ? false : else) -> !cond && else
        JExpression notCondExpr = notImpl(null, condExpr.getSourceInfo(), condExpr);
        return andImpl(null, info, notCondExpr, elseExpr);
      }
    } else if (elseExpr instanceof JBooleanLiteral) {
      if (((JBooleanLiteral) elseExpr).getValue()) {
        // e.g. (cond ? then : true) -> !cond || then
        JExpression notCondExpr = notImpl(null, condExpr.getSourceInfo(), condExpr);
        return orImpl(null, info, notCondExpr, thenExpr);
      } else {
        // e.g. (cond ? then : false) -> cond && then
        return andImpl(null, info, condExpr, thenExpr);
      }
    } else {
      // e.g. (!cond ? then : else) -> (cond ? else : then)
      JExpression unflipped = maybeUnflipBoolean(condExpr);
      if (unflipped != null) {
        return new JConditional(info, type, unflipped, elseExpr, thenExpr);
      }
    }

    // no simplification made
    if (original != null) {
      return original;
    }
    return new JConditional(info, type, condExpr, thenExpr, elseExpr);
  }

  /**
   * Simplifies an ifthenelse statement.
   *
   * <pre>
   * if(a,b,c) d [else e] -> {a; b; if(c) d [else e]; }
   * if(true) a [else b] -> a
   * if(false) a else b -> b
   * if(notImpl(c)) a else b -> if(c) b else a
   * if(true) ; else b -> true
   * if(false) a [else ;] -> false
   * if(c) ; [else ;] -> c
   *</pre>
   *
   * @param stmt the statement to simplify.
   * @param currentMethod the method where the statement resides
   * @return the simplified statement if a simplification could be done and <code>stmt</code>
   *         otherwise.
   */
  public static JStatement ifStatement(JIfStatement stmt,  JMethod currentMethod) {
    return ifStatementImpl(stmt, stmt.getSourceInfo(), stmt.getIfExpr(),
        stmt.getThenStmt(), stmt.getElseStmt(), currentMethod);
  }

  private static JStatement ifStatementImpl(JIfStatement original, SourceInfo info,
      JExpression condExpr, JStatement thenStmt,JStatement elseStmt, JMethod currentMethod) {
    info = getBestSourceInfo(original, info, condExpr);
    if (condExpr instanceof JMultiExpression) {
      // if(a,b,c) d else e -> {a; b; if(c) d else e; }
      JMultiExpression condMulti = (JMultiExpression) condExpr;
      JBlock newBlock = new JBlock(info);
      for (JExpression expr : allButLast(condMulti.getExpressions())) {
        newBlock.addStmt(expr.makeStatement());
      }
      newBlock.addStmt(ifStatementImpl(null, info, last(condMulti.getExpressions()), thenStmt,
          elseStmt, currentMethod));
      // TODO(spoon): immediately simplify the resulting block
      return newBlock;
    }

    if (condExpr instanceof JBooleanLiteral) {
      JBooleanLiteral booleanLiteral = (JBooleanLiteral) condExpr;
      boolean boolVal = booleanLiteral.getValue();
      if (boolVal && !isEmpty(thenStmt)) {
        // If true, replace myself with then statement
        return thenStmt;
      } else if (!boolVal && !isEmpty(elseStmt)) {
        // If false, replace myself with else statement
        return elseStmt;
      } else {
        // just prune me
        return condExpr.makeStatement();
      }
    }

    if (isEmpty(thenStmt) && isEmpty(elseStmt)) {
      return condExpr.makeStatement();
    }

    if (!isEmpty(elseStmt)) {
      // if (!cond) foo else bar -> if (cond) bar else foo
      JExpression unflipped = Simplifier.maybeUnflipBoolean(condExpr);
      if (unflipped != null) {
        // Force sub-parts to blocks, otherwise we break else-if chains.
        // TODO: this goes away when we normalize the Java AST properly.
        thenStmt = ensureBlock(thenStmt);
        elseStmt = ensureBlock(elseStmt);
        return ifStatementImpl(null, info, unflipped, elseStmt, thenStmt, currentMethod);
      }
    }

    JStatement rewritenStatement =
        rewriteIfIntoBoolean(info, condExpr, thenStmt, elseStmt, currentMethod);
    if (rewritenStatement != null) {
      return rewritenStatement;
    }

    // no simplification made
    if (original != null) {
      return original;
    }
    return new JIfStatement(info, condExpr, thenStmt, elseStmt);
  }

  /**
   * Simplifies an negation expression.
   *
   * if(a,b,c) d else e -> {a; b; if(c) d else e; }
   *
   * @param expr the expression to simplify.
   * @return the simplified expression if a simplification could be done and <code>expr</code>
   *         otherwise.
   */
  public static JExpression not(JPrefixOperation expr) {
    return notImpl(expr, expr.getSourceInfo(), expr.getArg());
  }

  private static JExpression notImpl(JPrefixOperation original, SourceInfo info, JExpression arg) {
    info = getBestSourceInfo(original, info, arg);
    if (arg instanceof JMultiExpression) {
      // !(a,b,c) -> (a,b,!c)
      JMultiExpression argMulti = (JMultiExpression) arg;
      JMultiExpression newMulti = new JMultiExpression(info);
      newMulti.addExpressions(allButLast(argMulti.getExpressions()));
      newMulti.addExpressions(notImpl(null, info, last(argMulti.getExpressions())));
      // TODO(spoon): immediately simplify the newMulti
      return newMulti;
    }
    if (arg instanceof JBinaryOperation) {
      // try to invert the binary operator
      JBinaryOperation argOp = (JBinaryOperation) arg;
      JBinaryOperator op = argOp.getOp();
      JBinaryOperator newOp = null;
      if (op == JBinaryOperator.EQ) {
        // e.g. !(x == y) -> x != y
        newOp = JBinaryOperator.NEQ;
      } else if (op == JBinaryOperator.NEQ) {
        // e.g. !(x != y) -> x == y
        newOp = JBinaryOperator.EQ;
      } else if (op == JBinaryOperator.GT) {
        // e.g. !(x > y) -> x <= y
        newOp = JBinaryOperator.LTE;
      } else if (op == JBinaryOperator.LTE) {
        // e.g. !(x <= y) -> x > y
        newOp = JBinaryOperator.GT;
      } else if (op == JBinaryOperator.GTE) {
        // e.g. !(x >= y) -> x < y
        newOp = JBinaryOperator.LT;
      } else if (op == JBinaryOperator.LT) {
        // e.g. !(x < y) -> x >= y
        newOp = JBinaryOperator.GTE;
      }
      if (newOp != null) {
        JBinaryOperation newBinOp =
            new JBinaryOperation(info, argOp.getType(), newOp, argOp.getLhs(), argOp.getRhs());
        return newBinOp;
      }
    } else if (arg instanceof JPrefixOperation) {
      // try to invert the unary operator
      JPrefixOperation argOp = (JPrefixOperation) arg;
      JUnaryOperator op = argOp.getOp();
      // e.g. !!x -> x
      if (op == JUnaryOperator.NOT) {
        return argOp.getArg();
      }
    } else if (arg instanceof JBooleanLiteral) {
      JBooleanLiteral booleanLit = (JBooleanLiteral) arg;
      return JBooleanLiteral.get(!booleanLit.getValue());
    }

    // no simplification made
    if (original != null) {
      return original;
    }
    return new JPrefixOperation(info, JUnaryOperator.NOT, arg);
  }

  /**
   * Simplify short circuit AND expressions.
   *
   * <pre>
   * true && isWhatever() -> isWhatever()
   * false && isWhatever() -> false
   *
   * isWhatever() && true -> isWhatever()
   * isWhatever() && false -> false, unless side effects
   *
   * (a, b) && c -> (a, b && c)
   * </pre>
   *
   * @param exp an AND JBinaryExpression to be simplified.
   * @return the simplified expression if a simplification was possible; <code>exp</code> otherwise.
   *
   */
  public static JExpression and(JBinaryOperation exp) {
    assert exp.getOp() == JBinaryOperator.AND : "Simplifier.and was called with " + exp;
    return andImpl(exp, null, exp.getLhs(), exp.getRhs());
  }

  private static JExpression andImpl(JBinaryOperation original, SourceInfo info, JExpression lhs,
      JExpression rhs) {
    info = getBestSourceInfo(original, info, lhs);
    if (lhs instanceof JMultiExpression) {
      // (a,b,c)&&d -> a,b,(c&&d)
      JMultiExpression lhsMulti = (JMultiExpression) lhs;
      JMultiExpression newMulti = new JMultiExpression(info);
      newMulti.addExpressions(allButLast(lhsMulti.getExpressions()));
      newMulti.addExpressions(andImpl(null, info, last(lhsMulti.getExpressions()), rhs));
      // TODO(rluble): immediately simplify the resulting multi.
      // TODO(rluble): refactor common outward JMultiExpression movement.
      return newMulti;
    }
    if (lhs instanceof JBooleanLiteral) {
      JBooleanLiteral booleanLiteral = (JBooleanLiteral) lhs;
      if (booleanLiteral.getValue()) {
        return rhs;
      } else {
        return lhs;
      }

    } else if (rhs instanceof JBooleanLiteral) {
      JBooleanLiteral booleanLiteral = (JBooleanLiteral) rhs;
      if (booleanLiteral.getValue()) {
        return lhs;
      } else if (!lhs.hasSideEffects()) {
        return rhs;
      }
    }
    // no simplification made
    if (original != null) {
      return original;
    }
    return new JBinaryOperation(info, rhs.getType(), JBinaryOperator.AND, lhs, rhs);
  }

  /**
   * Simplify short circuit OR expressions.
   *
   * <pre>
   * true || isWhatever() -> true
   * false || isWhatever() -> isWhatever()
   *
   * isWhatever() || false isWhatever()
   * isWhatever() || true -> true, unless side effects
   *
   * (a, b) || c -> (a, b || c)
   * </pre>
   *
   * @param exp an OR JBinaryExpression to be simplified.
   * @return the simplified expression if a simplification was possible; <code>exp</code> otherwise.
   *
   */
  public static JExpression or(JBinaryOperation exp) {
    assert exp.getOp() == JBinaryOperator.OR : "Simplifier.and was called with " + exp;
    return orImpl(exp, null, exp.getLhs(), exp.getRhs());
  }

  private static JExpression orImpl(JBinaryOperation original, SourceInfo info, JExpression lhs,
      JExpression rhs) {
    info = getBestSourceInfo(original, info, lhs);
    if (lhs instanceof JMultiExpression) {
      // (a,b,c)|| d -> a,b,(c||d)
      JMultiExpression lhsMulti = (JMultiExpression) lhs;
      JMultiExpression newMulti = new JMultiExpression(info);
      newMulti.addExpressions(allButLast(lhsMulti.getExpressions()));
      newMulti.addExpressions(orImpl(null, info, last(lhsMulti.getExpressions()), rhs));
      // TODO(rluble): immediately simplify the resulting multi.
      // TODO(rluble): refactor common outward JMultiExpression movement.
      return newMulti;
    }
    if (lhs instanceof JBooleanLiteral) {
      JBooleanLiteral booleanLiteral = (JBooleanLiteral) lhs;
      if (booleanLiteral.getValue()) {
        return lhs;
      } else {
        return rhs;
      }
    } else if (rhs instanceof JBooleanLiteral) {
      JBooleanLiteral booleanLiteral = (JBooleanLiteral) rhs;
      if (!booleanLiteral.getValue()) {
        return lhs;
      } else if (!lhs.hasSideEffects()) {
        return rhs;
      }
    }
    // no simplification made
    if (original != null) {
      return original;
    }
    return new JBinaryOperation(info, rhs.getType(), JBinaryOperator.OR, lhs, rhs);
  }

  private static JStatement ensureBlock(JStatement stmt) {
    if (stmt == null) {
      return null;
    }
    if (!(stmt instanceof JBlock)) {
      JBlock block = new JBlock(stmt.getSourceInfo());
      block.addStmt(stmt);
      stmt = block;
    }
    return stmt;
  }

  private static JExpression extractExpression(JStatement stmt) {
    if (stmt instanceof JExpressionStatement) {
      JExpressionStatement statement = (JExpressionStatement) stmt;
      return statement.getExpr();
    }

    return null;
  }

  private static JStatement extractSingleStatement(JStatement stmt) {
    if (stmt instanceof JBlock) {
      JBlock block = (JBlock) stmt;
      if (block.getStatements().size() == 1) {
        return extractSingleStatement(block.getStatements().get(0));
      }
    }

    return stmt;
  }

  /**
   * Determine the best SourceInfo to use in a particular transformation.
   *
   * @param original the original node that is being transformed. Can be <code>null</code>.
   * @param info an explicit SourceInfo that might be used, Can be <code>null</code>.
   * @param defaultNode a node from where to obtain the SourceInfo.
   * @return a SourceInfo chosen according to the following priority info>original>default.
   */
  private static SourceInfo getBestSourceInfo(JNode original, SourceInfo info, JNode defaultNode) {
    if (info == null) {
      if (original == null) {
        info = defaultNode.getSourceInfo();
      } else {
        info = original.getSourceInfo();
      }
    }
    return info;
  }

  private static JStatement rewriteIfIntoBoolean(SourceInfo sourceInfo, JExpression condExpr,
      JStatement thenStmt, JStatement elseStmt, JMethod currentMethod) {
    thenStmt = extractSingleStatement(thenStmt);
    elseStmt = extractSingleStatement(elseStmt);

    if (thenStmt instanceof JReturnStatement && elseStmt instanceof JReturnStatement
        && currentMethod != null) {
      // Special case
      // if () { return ..; } else { return ..; } =>
      // return ... ? ... : ...;
      JExpression thenExpression = ((JReturnStatement) thenStmt).getExpr();
      JExpression elseExpression = ((JReturnStatement) elseStmt).getExpr();
      if (thenExpression == null || elseExpression == null) {
        // empty returns are not supported.
        return null;
      }

      JConditional conditional =
          new JConditional(sourceInfo, currentMethod.getType(), condExpr, thenExpression,
              elseExpression);

      JReturnStatement returnStatement = new JReturnStatement(sourceInfo, conditional);
      return returnStatement;
    }

    if (elseStmt != null) {
      // if () { } else { } -> ... ? ... : ... ;
      JExpression thenExpression = extractExpression(thenStmt);
      JExpression elseExpression = extractExpression(elseStmt);

      if (thenExpression != null && elseExpression != null) {
        JConditional conditional =
            new JConditional(sourceInfo, JPrimitiveType.VOID, condExpr, thenExpression,
                elseExpression);

        return conditional.makeStatement();
      }
    } else {
      // if () { } -> ... && ...;
      JExpression thenExpression = extractExpression(thenStmt);

      if (thenExpression != null) {
        JBinaryOperator binaryOperator = JBinaryOperator.AND;

        JExpression unflipExpression = maybeUnflipBoolean(condExpr);
        if (unflipExpression != null) {
          condExpr = unflipExpression;
          binaryOperator = JBinaryOperator.OR;
        }

        JBinaryOperation binaryOperation =
            new JBinaryOperation(sourceInfo, JPrimitiveType.VOID, binaryOperator, condExpr,
                thenExpression);

        return binaryOperation.makeStatement();
      }
    }

    return null;
  }
}
