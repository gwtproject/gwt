/*
 * Copyright 2025 GWT Project Authors
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

import com.google.gwt.dev.jjs.ast.Context;
import com.google.gwt.dev.jjs.ast.JBlock;
import com.google.gwt.dev.jjs.ast.JDoStatement;
import com.google.gwt.dev.jjs.ast.JForStatement;
import com.google.gwt.dev.jjs.ast.JIfStatement;
import com.google.gwt.dev.jjs.ast.JMethodBody;
import com.google.gwt.dev.jjs.ast.JModVisitor;
import com.google.gwt.dev.jjs.ast.JNode;
import com.google.gwt.dev.jjs.ast.JProgram;
import com.google.gwt.dev.jjs.ast.JStatement;
import com.google.gwt.dev.jjs.ast.JSwitchStatement;
import com.google.gwt.dev.jjs.ast.JTryStatement;
import com.google.gwt.dev.jjs.ast.JWhileStatement;

import java.util.Stack;

/**
 * Examines blocks where child blocks (if/else, try/catch) use control flow statements
 * (return/throw/break/continue) which make it impossible for that block to flow back to the parent.
 * In these cases, the other block can be expanded to include the rest of the parent block.
 * <p>
 * Examples:
 * if (condition) { statements; return; } rest of parent block;
 * or
 * if (condition) { statements; return; } else { more; } rest of parent block;
 *   -> if (condition) { statements; } else { [more;] rest of parent block; }
 * <p>
 * try { statements; return; } catch (e) { handler(e); } rest of parent block;
 *   -> try { statements; } catch (e) { handler(e); rest of parent block; }
 * <p>
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
 * <p>
 * Only needs to be run once as part of normalization, since MethodInliner will never move more than
 * an expression (either in a JExpressionStatement or JReturnStatement).
 * <p>
 *
 * if (a) { return; } if (b) {return;} rest;
 *  --> if (a) { return; } else { if (b) { return; } else { rest; } }
 * <p>
 * if (a) { for (..) { if (b) { break; } rest1; } } rest2;
 *  --> if (a) { for (..) { if (b) { break; } else { rest1; } } } else { rest2; }
 *
 */
public class ExpandBlocks {
  private static class Acceptor {
    JBlock acceptingBlock;
    final JNode currentNode;

    private Acceptor(JNode currentNode) {
      this.currentNode = currentNode;
    }
  }
  /**
   * Normalize the program's nested blocks.
   */
  public static void exec(JProgram program) {
    new JModVisitor() {

      /**
       * The location in which statements can be moved to is represented by a stack - it isn't quite the usual use of a stack in a visitor, as it doesn't represent
       * the path from the root, but instead the most recent block that can accept statements. Nulls are added to the stack as well to indicate that there is currently
       * no position to move statements to. Testing to see if a statement can be moved then is just checking if the stack is non-empty and the top is non-null.
       */
      private final Stack<Acceptor> acceptorStack = new Stack<>();

      private JBlock acceptBlockWithoutPush(JBlock block) {
        acceptWithInsertRemove(block.getStatements());
        return block;
      }

      @Override
      public boolean visit(JStatement x, Context ctx) {
        attemptRelocate(x, ctx);
        return true;
      }

      @Override
      public boolean visit(JIfStatement x, Context ctx) {
        // We do our own visiting here, rather than delegate to super, so
        // we control then vs else.

        // Attempt to move the entire if
        attemptRelocate(x, ctx);
        // While inside the if blocks, don't move statements out of it
        Acceptor self = new Acceptor(x);
        acceptorStack.push(self);

        // Ignore condition, visit then and else
        acceptBlockWithoutPush(x.getThenStmt());
        JBlock thenAcceptor = self.acceptingBlock;
        acceptBlockWithoutPush(x.getElseStmt());
        JBlock elseAcceptor = self.acceptingBlock;

        // Pop the local node, and look to see if we should edit the parent
        acceptorStack.pop();
        Acceptor parent = acceptorStack.peek();

        // Mark where (if any) we accept later statements
        if (x.getThenStmt().unconditionalControlBreak()) {
          if (!x.getElseStmt().unconditionalControlBreak()) {
            // else can handle rest of parent block
            if (elseAcceptor != null) {
              // Use the acceptor that we found while visiting else
              parent.acceptingBlock = elseAcceptor;
            } else {
              parent.acceptingBlock = x.getElseStmt();
            }
          } else {
            // both break, can use the same block (if any) that we relocated the current node to
          }
        } else {
          if (x.getThenStmt().unconditionalControlBreak()) {
            // else can handle rest of parent block
            if (thenAcceptor != null) {
              // Use the acceptor that we found while visiting then
              parent.acceptingBlock = thenAcceptor;
            } else {
              parent.acceptingBlock = x.getThenStmt();
            }
          } else {
            // neither breaks, use the same block (if any) that we relocated the current node to
          }
        }

        // Already visited children
        return false;
      }

      @Override
      public void endVisit(JIfStatement x, Context ctx) {
        // Do nothing, handled in visit(if)
      }

      @Override
      public boolean visit(JTryStatement x, Context ctx) {
        // Attempt to move the entire try
        attemptRelocate(x, ctx);
        // While inside the try block, don't move statements out of it
        Acceptor self = new Acceptor(x);
        acceptorStack.push(self);

        // Visit each child statement (ignore exprs), though we only need to track changes to "self"
        // for the catch block if there is exactly one
        JBlock catchExceptor = null;
        accept(x.getTryBlock());
        for (int i = 0; i < x.getCatchClauses().size(); i++) {
          JTryStatement.CatchClause clause = x.getCatchClauses().get(i);
          acceptBlockWithoutPush(clause.getBlock());
          // If there is exactly one catch, we may be able to move later statements into it
          if (x.getCatchClauses().size() == 1) {
            catchExceptor = self.acceptingBlock;
          }
        }
        if (x.getFinallyBlock() != null) {
          accept(x.getFinallyBlock());
        }

        acceptorStack.pop();
        Acceptor parent = acceptorStack.peek();

        if (x.getFinallyBlock() != null) {
          // Cannot optimize if there is a finally block, as finally executes after catch and before remainder
          return false;
        }

        if (!x.getTryBlock().unconditionalControlBreak()) {
          // Try must unconditionally break to optimize, else we may catch the wrong exceptions
          return false;
        }

        if (x.getCatchClauses().size() != 1 || x.getCatchClauses().get(0).getBlock().unconditionalControlBreak()) {
          // Only can optimize exactly one catch, and unconditional return try
          return false;
        }
        // This is a valid case to rewrite, move later statements into the catch block, or nested
        // if available
        if (catchExceptor != null) {
          // Use the acceptor that we found while visiting the catch
          parent.acceptingBlock = catchExceptor;
        } else {
          parent.acceptingBlock = x.getCatchClauses().get(0).getBlock();
        }
        return false;
      }

      @Override
      public void endVisit(JTryStatement x, Context ctx) {
        // Do nothing, handled in visit(try)
      }

      @Override
      public boolean visit(JWhileStatement x, Context ctx) {
        // Attempt to move the entire while
        attemptRelocate(x, ctx);
        // While inside the while block, don't move statements out of it
        acceptorStack.push(new Acceptor(x));
        return true;
      }

      @Override
      public void endVisit(JWhileStatement x, Context ctx) {
        acceptorStack.pop();
      }

      @Override
      public boolean visit(JSwitchStatement x, Context ctx) {
        // Attempt to move the entire switch
        attemptRelocate(x, ctx);

        // Don't descend (at this time) - in the future we could either try to work around cases,
        // or could wait until we hit some child block and instantiate a new visitor for local
        // changes.
        return false;
      }


      @Override
      public void endVisit(JSwitchStatement x, Context ctx) {
        // No endVisit for switch, since we didn't push anything or descend
        //         acceptorStack.pop();
      }

      @Override
      public boolean visit(JForStatement x, Context ctx) {
        // Attempt to move the entire for
        attemptRelocate(x, ctx);
        // While inside the for block, don't move statements out of it
        acceptorStack.push(new Acceptor(x));
        return true;
      }

      @Override
      public void endVisit(JForStatement x, Context ctx) {
        acceptorStack.pop();
      }

      @Override
      public boolean visit(JDoStatement x, Context ctx) {
        // Attempt to move the entire do
        attemptRelocate(x, ctx);
        // While inside the do block, don't move statements out of it
        acceptorStack.push(new Acceptor(x));
        return true;
      }

      @Override
      public void endVisit(JDoStatement x, Context ctx) {
        acceptorStack.pop();
      }

      private void attemptRelocate(JStatement x, Context ctx) {
        if (!acceptorStack.isEmpty() && acceptorStack.peek().acceptingBlock != null) {
          // If there is a block ready to accept this, any statement should be moved.
          // Any visit(<statement>) override must call super before it does its own work
          // to ensure that it is relocated
          ctx.removeMe();
          acceptorStack.peek().acceptingBlock.addStmt(x);
          // Moved the item itself, continue to see if it needs to adopt later statements
        }
      }

      @Override
      public boolean visit(JBlock x, Context ctx) {
        // Attempt to move the entire block
//        attemptRelocate(x, ctx);
        // While inside the block, don't move statements out of it
        acceptorStack.push(new Acceptor(x));
        return true;
      }

      @Override
      public void endVisit(JBlock x, Context ctx) {
        acceptorStack.pop();
      }

      @Override
      public void endVisit(JMethodBody x, Context ctx) {
        // Clear any acceptor at end of method body
        assert acceptorStack.isEmpty();
        acceptorStack.clear();
      }
    }.accept(program);
  }
}
