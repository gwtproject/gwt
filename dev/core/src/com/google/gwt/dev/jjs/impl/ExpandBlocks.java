package com.google.gwt.dev.jjs.impl;

import com.google.gwt.dev.jjs.ast.Context;
import com.google.gwt.dev.jjs.ast.JBlock;
import com.google.gwt.dev.jjs.ast.JDoStatement;
import com.google.gwt.dev.jjs.ast.JForStatement;
import com.google.gwt.dev.jjs.ast.JIfStatement;
import com.google.gwt.dev.jjs.ast.JMethod;
import com.google.gwt.dev.jjs.ast.JMethodBody;
import com.google.gwt.dev.jjs.ast.JModVisitor;
import com.google.gwt.dev.jjs.ast.JProgram;
import com.google.gwt.dev.jjs.ast.JStatement;
import com.google.gwt.dev.jjs.ast.JSwitchStatement;
import com.google.gwt.dev.jjs.ast.JTryStatement;
import com.google.gwt.dev.jjs.ast.JWhileStatement;
import com.google.gwt.dev.js.ast.JsBlock;

import java.util.Set;
import java.util.Stack;

/**
 * Examines blocks where child blocks (if/else, try/catch) use control flow statements
 * (return/throw/break/continue) which make it impossible for that block to flow back to the parent.
 * In these cases, the other block can be expanded to include the rest of the parent block.
 *
 * Examples:
 * if (condition) { statements; return; } rest of parent block;
 * or
 * if (condition) { statements; return; } else { more; } rest of parent block;
 *   -> if (condition) { statements; } else { [more;] rest of parent block; }
 *
 * try { statements; return; } catch (e) { handler(e); } rest of parent block;
 *   -> try { statements; } catch (e) { handler(e); rest of parent block; }
 *
 * These transformations have two simple benefits today:
 * <ul>
 *   <li>They make it easier for the DeadCodeElimination/Simplifier to identify if/else blocks that return simple expressions and can be rewritten to a single conditional</li>
 *   <li>They reduce complexity slightly for passes like DuplicateClinitRemover, so that it doesn't need to merge the scopes it considers to continue</li>
 * </ul>
 *
 * For cases where an else block is added, we will also add a JS pass to remove it, transforming
 * if (condition) { ... return; } else { ... }  -> if (condition) { ... return; } ...
 * so that there is no net increase in size. This will could also save a few bytes when the else
 * already existed, and can now be removed.
 *
 * Only needs to be run once as part of normalization, since MethodInliner will never move more than
 * an expression (either in a JExpressionStatement or JReturnStatement).
 *
 *
 * if (a) { return; } if (b) {return;} rest;
 *  --> if (a) { return; } else { if (b) { return; } else { rest; } }
 *
 * if (a) { for (..) { if (b) { break; } rest1; } } rest2;
 *  --> if (a) { for (..) { if (b) { break; } else { rest1; } } } else { rest2; }
 *
 */
public class ExpandBlocks {
  /**
   * Normalize the program's nested blocks.
   */
  public static void exec(JProgram program) {
    new JModVisitor() {

      private JBlock acceptor;
      @Override
      public void endVisit(JIfStatement x, Context ctx) {
        if (x.getThenStmt().unconditionalControlBreak()) {
          if (x.getElseStmt().unconditionalControlBreak()) {
            // Both branches break, nothing to do - parent block is unreachable code after if/else
            acceptor = null;
          } else {
            // else can handle rest of parent block
            acceptor = x.getElseStmt();
          }
        } else {
          if (x.getThenStmt().unconditionalControlBreak()) {
            // else can handle rest of parent block
            acceptor = x.getElseStmt();
          } else {
            // neither branch breaks, cannot optimize
            acceptor = null;
          }
        }
      }

      @Override
      public void endVisit(JTryStatement x, Context ctx) {
        if (x.getFinallyBlock() != null) {
          // Cannot optimize if there is a finally block, as finally executes after catch and before remainder
          acceptor = null;
          return;
        }

        if (!x.getTryBlock().unconditionalControlBreak()) {
          // Try must unconditionally break to optimize
          acceptor = null;
          return;
        }

        if (x.getCatchClauses().size() != 1) {
          // Only can optimize exactly one catch, and unconditional return try
          acceptor = null;
          return;
        }
        acceptor = x.getCatchClauses().get(0).getBlock();
      }

      @Override
      public void endVisit(JSwitchStatement x, Context ctx) {
        //TODO handle switch case (though probably rarely worth it)
        acceptor = null;
      }

      @Override
      public void endVisit(JForStatement x, Context ctx) {
        // Interrupts moving statements to the last acceptor block
        acceptor = null;
      }

      @Override
      public void endVisit(JWhileStatement x, Context ctx) {
        // Interrupts moving statements to the last acceptor block
        acceptor = null;
      }

      @Override
      public void endVisit(JDoStatement x, Context ctx) {
        // Interrupts moving statements to the last acceptor block
        acceptor = null;
      }


      @Override
      public boolean visit(JStatement x, Context ctx) {
        if (acceptor != null) {
          // If there is a block ready to accept this, any statement should be moved.
          // Any visit(<statement>) override must call super before it does its own work,
          // to ensure that it is relocated
          ctx.removeMe();
          acceptor.addStmt(x);
          // Moved the item itself, continue to see if it needs to adopt later statements
        }
        return super.visit(x, ctx);
      }

      @Override
      public void endVisit(JMethodBody x, Context ctx) {
        // Clear any acceptor at end of method body
        acceptor = null;
      }
    }.accept(program);
  }
}
