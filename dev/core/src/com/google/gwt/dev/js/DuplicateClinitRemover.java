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

import com.google.gwt.dev.jjs.InternalCompilerException;
import com.google.gwt.dev.jjs.impl.OptimizerStats;
import com.google.gwt.dev.js.ast.JsBinaryOperation;
import com.google.gwt.dev.js.ast.JsBinaryOperator;
import com.google.gwt.dev.js.ast.JsCase;
import com.google.gwt.dev.js.ast.JsCatch;
import com.google.gwt.dev.js.ast.JsConditional;
import com.google.gwt.dev.js.ast.JsContext;
import com.google.gwt.dev.js.ast.JsDefault;
import com.google.gwt.dev.js.ast.JsDoWhile;
import com.google.gwt.dev.js.ast.JsExpression;
import com.google.gwt.dev.js.ast.JsFor;
import com.google.gwt.dev.js.ast.JsForIn;
import com.google.gwt.dev.js.ast.JsFunction;
import com.google.gwt.dev.js.ast.JsIf;
import com.google.gwt.dev.js.ast.JsInvocation;
import com.google.gwt.dev.js.ast.JsModVisitor;
import com.google.gwt.dev.js.ast.JsNode;
import com.google.gwt.dev.js.ast.JsNullLiteral;
import com.google.gwt.dev.js.ast.JsProgram;
import com.google.gwt.dev.js.ast.JsTry;
import com.google.gwt.dev.js.ast.JsWhile;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * This is used to clean up duplication invocations of clinit function. Whenever there is a
 * possible branch in program flow, the remover will create a new instance of
 * itself to handle the possible branches.
 * <p>
 * We don't look at combining branch choices. This will not produce the most
 * efficient elimination of duplicated calls, but it handles the general case
 * and is simple to verify.
 */
public class DuplicateClinitRemover extends JsModVisitor {
  private static final String NAME = DuplicateClinitRemover.class.getSimpleName();

  /*
   * TODO: Most of the special casing below can be removed if complex
   * statements always use blocks, rather than plain statements.
   */

  /**
   * Retains the functions that we know have been called.
   */
  private final Set<JsFunction> called;
  private final JsProgram program;

  public DuplicateClinitRemover(JsProgram program) {
    this.program = program;
    this.called = new HashSet<>();
  }

  public DuplicateClinitRemover(JsProgram program, Set<JsFunction> alreadyCalled) {
    this.program = program;
    this.called = new HashSet<>(alreadyCalled);
  }

  /**
   * Given a JsInvocation, determine if it is invoking a JsFunction that is
   * specified to be executed only once during the program's lifetime.
   */
  public static JsFunction isClinit(JsInvocation invocation) {
    JsFunction f = JsUtils.isFunction(invocation.getQualifier());
    if (f != null && f.isClinit()) {
      return f;
    }
    return null;
  }

  /**
   * Look for comma expressions that contain duplicate calls and handle the
   * conditional-evaluation case of logical and/or operations.
   * <p>
   * The comma case seems like it would be handled better by just visiting and removing/rewriting
   * invocations, under the assumption that later passes would tidy up better, but the
   * (clinit(), null) output case will leave behind the null as if it was going to be returned and
   * thus can't be removed. Since to address that, we must handle both (xyz, clinit()) and
   * (clinit(), clinit()) inputs, we might as well handle them all here.
   */
  @Override
  public boolean visit(JsBinaryOperation x, JsContext ctx) {
    if (x.getOperator() == JsBinaryOperator.COMMA) {

      // This effectively visits any JsInvocation direct child on both sides, so take care to not
      // encounter any clinit twice when descending further.
      ClinitStatus left = isDuplicateCall(x.getArg1());
      ClinitStatus right = isDuplicateCall(x.getArg2());

      if (left == ClinitStatus.DUPLICATE_CLINIT && right == ClinitStatus.DUPLICATE_CLINIT) {
        /*
         * (clinit(), clinit()) --> delete or null.
         * Repeated inlining can cause this, if there is an earlier clinit statement/expr in the
         * branch.
         */
        if (ctx.canRemove()) {
          ctx.removeMe();
        } else {
          // The return value from a clinit is never used
          ctx.replaceMe(JsNullLiteral.INSTANCE);
        }
        return false;
      } else if (left == ClinitStatus.DUPLICATE_CLINIT) {
        // (clinit(), xyz) --> xyz
        // This is the common case for simply-inlined methods/fields.
        if (right == ClinitStatus.NEW_CLINIT) {
          // Don't re-visit, it was just a clinit and we already observed it
          ctx.replaceMe(x.getArg2());
        } else {
          assert right == ClinitStatus.NOT_A_CLINIT;
          // Save to re-visit, nested clinits could be removed
          ctx.replaceMe(accept(x.getArg2()));
        }
        return false;
      } else if (right == ClinitStatus.DUPLICATE_CLINIT) {
        // (xyz, clinit()) --> xyz
        // This can happen with multiple inlined methods, each adding a new clinit for
        // the same class, where xyz might be the first clinit, or for a different class.
        if (left == ClinitStatus.NEW_CLINIT) {
          // Don't re-visit, it was just a clinit and we already observed it
          ctx.replaceMe(x.getArg1());
        } else {
          assert left == ClinitStatus.NOT_A_CLINIT;
          // Even though this is the left, it is safe to visit despite already looking at the right,
          // since we know the right isn't a direct duplicate or supertype (we would have hit a
          // different branch).
          ctx.replaceMe(accept(x.getArg1()));
        }
        return false;
      }
      // Descend to both sides only if neither is a clinit at all
      return right == ClinitStatus.NOT_A_CLINIT && left == ClinitStatus.NOT_A_CLINIT;
    } else if (x.getOperator().equals(JsBinaryOperator.AND)
        || x.getOperator().equals(JsBinaryOperator.OR)) {
      x.setArg1(accept(x.getArg1()));
      // Possibility of conditional evaluation of second parameter
      x.setArg2(branch(x.getArg2()));
      return false;
    } else {
      return true;
    }
  }

  @Override
  public boolean visit(JsCase x, JsContext ctx) {
    x.setCaseExpr(accept(x.getCaseExpr()));
    branch(x.getStmts());
    return false;
  }

  @Override
  public boolean visit(JsConditional x, JsContext ctx) {
    x.setTestExpression(accept(x.getTestExpression()));
    x.setThenExpression(branch(x.getThenExpression()));
    x.setElseExpression(branch(x.getElseExpression()));
    return false;
  }

  @Override
  public boolean visit(JsDefault x, JsContext ctx) {
    branch(x.getStmts());
    return false;
  }

  @Override
  public boolean visit(JsDoWhile x, JsContext ctx) {
    // We have to visit manually, the visitor looks at the condition before the body. At this time,
    // both must be branch()es, since we can't reliably ensure that either will be hit - an
    // if statement could "continue" and skip the rest of the method.
    x.setBody(branch(x.getBody()));
    x.setCondition(branch(x.getCondition()));
    return false;
  }

  @Override
  public boolean visit(JsFor x, JsContext ctx) {
    // The JsFor may have an expression xor a variable declaration.
    if (x.getInitExpr() != null) {
      x.setInitExpr(accept(x.getInitExpr()));
    } else if (x.getInitVars() != null) {
      x.setInitVars(accept(x.getInitVars()));
    }

    // The condition is optional
    if (x.getCondition() != null) {
      x.setCondition(accept(x.getCondition()));
    }

    // The increment expression is optional. When present, it always runs after the body, so it
    // could be a sub-branch of that, when we reliably can determine what clinits are called
    // executing a block that could continue
    if (x.getIncrExpr() != null) {
      x.setIncrExpr(branch(x.getIncrExpr()));
    }

    x.setBody(branch(x.getBody()));
    return false;
  }

  @Override
  public boolean visit(JsForIn x, JsContext ctx) {
    if (x.getIterExpr() != null) {
      x.setIterExpr(accept(x.getIterExpr()));
    }

    x.setObjExpr(accept(x.getObjExpr()));

    x.setBody(branch(x.getBody()));
    return false;
  }

  @Override
  public boolean visit(JsFunction x, JsContext ctx) {
    x.setBody(branch(x.getBody()));
    return false;
  }

  @Override
  public boolean visit(JsIf x, JsContext ctx) {
    x.setIfExpr(accept(x.getIfExpr()));

    x.setThenStmt(branch(x.getThenStmt()));
    if (x.getElseStmt() != null) {
      x.setElseStmt(branch(x.getElseStmt()));
    }

    return false;
  }

  /**
   * Possibly record that we've seen a call in the current context.
   */
  @Override
  public boolean visit(JsInvocation x, JsContext ctx) {
    if (isDuplicateCall(x) == ClinitStatus.DUPLICATE_CLINIT) {
      if (ctx.canRemove()) {
        ctx.removeMe();
      } else {
        ctx.replaceMe(JsNullLiteral.INSTANCE);
      }
      return false;
    }
    return true;
  }

  @Override
  public boolean visit(JsTry x, JsContext ctx) {
    if (!x.getCatches().isEmpty()) {
      // Catch could return control to parent block without completing the try block, so branch
      // the try block if there is any catch.
      x.setTryBlock(branch(x.getTryBlock()));
      List<JsCatch> catches = x.getCatches();
      for (int i = 0; i < catches.size(); i++) {
        JsCatch aCatch = catches.get(i);
        JsCatch c = accept(aCatch);
        catches.set(i, c);
      }
    } else {
      if (x.getFinallyBlock() != null) {
        // On the other hand, if there is a finally block, the try block isn't guaranteed to complete
        // before finally runs, so finally needs to start from the same initial state as try did. We
        // can do that by branch()ing finally first, then accept()ing try
        x.setFinallyBlock(branch(x.getFinallyBlock()));
      }
      x.setTryBlock(accept(x.getTryBlock()));
    }

    return false;
  }

  @Override
  public boolean visit(JsWhile x, JsContext ctx) {
    x.setCondition(accept(x.getCondition()));

    x.setBody(branch(x.getBody()));
    return false;
  }

  /**
   * Static entry point used by JavaToJavaScriptCompiler.
   */
  public static int exec(JsProgram program) {
    return execImpl(program);
  }

  private static int execImpl(JsProgram program) {
    try (OptimizerStats stats = OptimizerStats.optimization(NAME)) {
      DuplicateClinitRemover r = new DuplicateClinitRemover(program);
      r.accept(program);
      stats.recordModified(r.getNumMods());

      return stats.getNumMods();
    }
  }

  private <T extends JsNode> void branch(List<T> x) {
    DuplicateClinitRemover dup = new DuplicateClinitRemover(program, called);
    dup.acceptWithInsertRemove(x);
    numMods += dup.getNumMods();
  }

  private <T extends JsNode> T branch(T x) {
    DuplicateClinitRemover dup = new DuplicateClinitRemover(program, called);
    T toReturn = dup.accept(x);

    if ((toReturn != x) && dup.getNumMods() == 0) {
      throw new InternalCompilerException(
          "node replacement should imply getNumMods() > 0");
    }

    numMods += dup.getNumMods();
    return toReturn;
  }

  private enum ClinitStatus {
    NOT_A_CLINIT,
    NEW_CLINIT,
    DUPLICATE_CLINIT
  }

  /**
   * If the expression is a clinit, mark it as seen, and return true if it should be removed.
   */
  private ClinitStatus isDuplicateCall(JsExpression x) {
    if (!(x instanceof JsInvocation)) {
      return ClinitStatus.NOT_A_CLINIT;
    }

    JsFunction func = isClinit((JsInvocation) x);
    if (func != null) {
      if (called.contains(func)) {
        return ClinitStatus.DUPLICATE_CLINIT;
      }
      while (func != null) {
        called.add(func);
        func = func.getSuperClinit();
      }
      return ClinitStatus.NEW_CLINIT;
    }
    return ClinitStatus.NOT_A_CLINIT;
  }
}
