/*
 * Copyright 2026 GWT Project Authors
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
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.dev.jjs.ast.JMethod;
import com.google.gwt.dev.jjs.ast.JProgram;

public class RemoveUnnecessaryControlFlowTest extends OptimizerTestBase {

  @Override
  protected void setUp() throws Exception {
    super.setUp();

    addSnippetClassDecl("static boolean condition() { return true; }");
    addSnippetClassDecl("static void foo() { }");
  }

  public void testReturnInSimpleBlock() throws UnableToCompleteException {
    optimize("void", "return;").into("");
    optimize("void", "{return;}").into("{}");

    optimize("void", "foo();return;").into("foo();");
    optimize("void", "{foo();return;}").into("{foo();}");
    optimize("void", "foo();{return;}").into("foo();{}");

    optimize("String", "return null;").noChange();
    optimize("String", "{return null;}").noChange();
  }

  public void testReturnInIf() throws UnableToCompleteException {
    optimize("void", "if (condition()) { return; }")
        .into("if (condition()) { }");
    optimize("void", "if (condition()) { return; } return;")
        .into("if (condition()) { }");

    optimize("void", "if (condition()) { return; } foo();").noChange();

    optimize("void", "if (condition()) { return; } else { return; }")
        .into("if (condition()) { } else { }");
    optimize("void", "if (condition()) { foo(); } else { return; }")
        .into("if (condition()) { foo(); } else { }");

    optimize("void", "if (condition()) { if (condition()) { return; } foo(); return; }")
        .into("if (condition()) { if (condition()) { return; } foo(); }");
  }

  public void testReturnInTry() throws UnableToCompleteException {
    optimize("void", "try { return; } catch (Exception e) { return; } finally { return; }")
        .into("try { } catch (Exception e) { } finally { }");
  }

  public void testReturnInLoop() throws UnableToCompleteException {
    // while, with various returns
    optimize("void", "while (condition()) { return; }")
        .into("while (condition()) { break; }");
    optimize("void", "while (condition()) { return; } return;")
        .into("while (condition()) { break; }");

    optimize("void", "while (condition()) { return; } foo();").noChange();

    // for, do/while - add some nesting
    optimize("void", "for (int i = 0; i < 10; i++) { return; }")
        .into("for (int i = 0; i < 10; i++) { break; }");
    optimize("void", "do { if (condition()) { return; } else { foo(); } } while(true);")
        .into("do { if (condition()) { break; } else { foo(); } } while(true);");
  }

  public void testContinueInLoop() throws UnableToCompleteException {
    // Same sorts of tests on each loop construct, with various other siblings/wrappings
    optimize("void", "for (int i = 0; i < 10; i++) { continue; }")
        .into("for (int i = 0; i < 10; i++) { }");
    optimize("void", "do { if (condition()) { continue; } else { foo(); } } while(true);")
        .into("do { if (condition()) { } else { foo(); } } while(true);");
    optimize("void", "while(true) { foo(); continue; } ")
        .into("while(true) { foo(); }");
  }

  public void testSwitchStmt() throws UnableToCompleteException {
    // Final return/break removed
    optimize("void", "switch (3) { case 1: return; case 2: default: return; }")
        .into("switch (3) { case 1: return; case 2: default: }");
    optimize("void", "switch (3) { case 1: break; case 2: default: break; }")
        .into("switch (3) { case 1: break; case 2: default: }");
  }

  public void testLoopInLoop() throws UnableToCompleteException {
    // Continue can be omitted even if the loop isn't the last statement
    optimize("void", "while (condition()) { while (condition()) { foo(); continue; } continue; } foo();")
        .into("while (condition()) { while (condition()) { foo(); } } foo();");
    optimize("void", "while (condition()) { while (condition()) { foo(); continue; } }")
        .into("while (condition()) { while (condition()) { foo(); } }");

    // return cannot be omitted in a loop-in-loop
    optimize("void", "while (condition()) { while (true) { foo(); return; } }")
        .noChange();
    // loop-in-loop structure doesn't impact return in the outer loop
    optimize("void", "while (condition()) { while (condition()) { foo(); } return; }")
        .into("while (condition()) { while (condition()) { foo(); } break; }");
  }

  public void testSwitchInLoop() throws UnableToCompleteException {
    // validate that the break is treated as part of the switch and still removed
    optimize("void", "for (int i = 0; i < 10; i++) { switch (i) { case 1: return; case 2: break; } }")
        .into("for (int i = 0; i < 10; i++) { switch (i) { case 1: return; case 2: } }");
    // Likewise, the continue inside the switch is part of the loop and removed as the final statement
    optimize("void", "for (int i = 0; i < 10; i++) { switch (i) { case 1: return; case 2: continue; } }")
        .into("for (int i = 0; i < 10; i++) { switch (i) { case 1: return; case 2: } }");
  }

  public void testLoopInSwitchStatement() throws UnableToCompleteException {
    optimize("void", "switch(4) {case 1: for (int i = 0; i < 10; i++) { break; }}").noChange();

    optimize("void", "switch(4) {case 0: foo(); case 1: for (int i = 0; i < 10; i++) { continue; } break; }")
         .into("switch(4) {case 0: foo(); case 1: for (int i = 0; i < 10; i++) { }}");
  }

  public void testBlocksInSwitchStatement() throws UnableToCompleteException {
    optimize("void", "switch(4) {case 0: {foo();} case 1: if (condition()) {return;} else {foo(); return;} }")
        .into("switch(4) {case 0: {foo();} case 1: if (condition()) {} else {foo(); } }");
  }

  public void testContinuesInNonVoidMethod() throws UnableToCompleteException {
    optimize("int", "while (condition()) { foo(); continue; } return 1;")
        .into("while (condition()) { foo(); }; return 1;");
  }

  @Override
  protected boolean doOptimizeMethod(TreeLogger logger, JProgram program, JMethod method)
      throws UnableToCompleteException {
    int mods = RemoveUnnecessaryControlFlow.exec(program, OptimizerContext.NULL_OPTIMIZATION_CONTEXT);
    // verify we converged in a single pass
    assert RemoveUnnecessaryControlFlow.exec(program, OptimizerContext.NULL_OPTIMIZATION_CONTEXT) == 0;
    return mods > 0;
  }
}