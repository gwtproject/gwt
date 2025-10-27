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
package com.google.gwt.dev.js;

import com.google.gwt.dev.jjs.SourceOrigin;
import com.google.gwt.dev.js.ast.JsContext;
import com.google.gwt.dev.js.ast.JsFunction;
import com.google.gwt.dev.js.ast.JsModVisitor;
import com.google.gwt.dev.js.ast.JsName;
import com.google.gwt.dev.js.ast.JsProgram;
import com.google.gwt.dev.js.ast.JsStatement;
import com.google.gwt.dev.js.ast.JsVisitor;
import com.google.gwt.dev.util.DefaultTextOutput;
import com.google.gwt.dev.util.TextOutput;

import java.io.StringReader;
import java.lang.reflect.Method;
import java.util.List;

/**
 * Need tests for
 * super clinits
 * if/for/while/do/conditional/short-circuit/switch
 */
public class JsDuplicateClinitRemoverTest extends OptimizerTestBase {
  public void testRemoveDupClinitsInBlock() throws Exception {
    // Explicitly mark a clinit as such, so that the optimizer can identify it.
    JsProgram program = new JsProgram();
    String js = "function emptyFunc(){}" +
        "function clinit_A(){clinit_A = emptyFunc}" +
        "clinit_A();" +
        "clinit_A();";
    List<JsStatement> input = JsParser.parse(SourceOrigin.UNKNOWN,
        program.getScope(), new StringReader(js));
    program.getGlobalBlock().getStatements().addAll(input);

    setupProgram(program);

    String optimized = optimize(program, JsSymbolResolver.class, DuplicateClinitRemover.class, JsStaticEval.class);
    assertEquals("function emptyFunc(){}\n" +
        "function clinit_A(){clinit_A=emptyFunc}\n" +
        "clinit_A();", optimized);
  }

  public void testRemoveDupClinitsInExpr() throws Exception {
    JsProgram program = new JsProgram();
    String js = "function emptyFunc(){}" +
        "function clinit_A(){clinit_A = emptyFunc}" +
        "value = (clinit_A(),clinit_A(), 1);";
    List<JsStatement> input = JsParser.parse(SourceOrigin.UNKNOWN,
        program.getScope(), new StringReader(js));
    program.getGlobalBlock().getStatements().addAll(input);

    setupProgram(program);

    String optimized = optimize(program, JsSymbolResolver.class, DuplicateClinitRemover.class);
    assertEquals("function emptyFunc(){}\n" +
        "function clinit_A(){clinit_A=emptyFunc}\n" +
        "value=(clinit_A(),1);", optimized);
  }

  private void setupProgram(JsProgram program) {
    new JsModVisitor() {
      @Override
      public void endVisit(JsFunction x, JsContext ctx) {
        // Ensure the optimizer knows which methods are clinits
        if (x.getName().toString().startsWith("clinit_")) {
          x.markAsClinit();
        }

        // Indicate that all methods were compiled from Java source
        x.setFromJava(true);

        // Provide a static ref for each function, as if it was from Java source
        JsName name = x.getName();
        if (name != null) {
          name.setStaticRef(x);
        }
      }
    }.accept(program);
  }

  /**
   * Optimize a JS program.
   *
   * @param program the source program
   * @param toExec a list of classes that implement
   *          <code>static void exec(JsProgram)</code>
   * @return optimized JS
   */
  protected String optimize(JsProgram program, Class<?>... toExec) throws Exception {

    for (Class<?> clazz : toExec) {
      Method m = clazz.getMethod("exec", JsProgram.class);
      m.invoke(null, program);
    }

    TextOutput text = new DefaultTextOutput(true);
    JsVisitor generator = new JsSourceGenerationVisitor(text);

    generator.accept(program);
    return text.toString();
  }
}
