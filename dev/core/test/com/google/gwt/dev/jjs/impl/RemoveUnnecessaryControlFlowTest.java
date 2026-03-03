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

    // We could also remove these, resulting in returning undefined.
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

  public void testReturnInLoop() throws UnableToCompleteException {
    optimize("void", "while (condition()) { return; }")
        .into("while (condition()) { break; }");
    optimize("void", "while (condition()) { return; } return;")
        .into("while (condition()) { break; }");

    optimize("void", "while (condition()) { return; } foo();").noChange();
  }

  @Override
  protected boolean doOptimizeMethod(TreeLogger logger, JProgram program, JMethod method)
      throws UnableToCompleteException {
    // Not presently guaranteed to converge in a single pass, so loop until it does.
    int mods;
    do {
      mods = RemoveUnnecessaryControlFlow.exec(program, OptimizerContext.NULL_OPTIMIZATION_CONTEXT);
    } while (mods > 0);
    return false;
  }
}