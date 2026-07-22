/*
 * Copyright 2009 Google Inc.
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
import com.google.gwt.dev.js.ast.JsProgram;
import com.google.gwt.dev.js.ast.JsStatement;
import com.google.gwt.dev.js.ast.JsVisitor;
import com.google.gwt.dev.util.DefaultTextOutput;
import com.google.gwt.dev.util.TextOutput;

import com.google.gwt.thirdparty.guava.common.base.Joiner;
import junit.framework.TestCase;

import java.io.StringReader;
import java.util.List;

/**
 * A utility base type for writing tests for JS optimizers.
 */
public abstract class OptimizerTestBase extends TestCase {

  /**
   * Optimize a JS program, so that the test can also parse the expected value and normalize away
   * simple differences. Applies the subclass's setup and optimization steps.
   */
  protected Result optimize(String... snippets) throws Exception {
    JsProgram program = parseToProgram(snippets);

    return optimize(program);
  }

  /**
   * Given a built program, applies setup and optimization steps.
   */
  protected Result optimize(JsProgram program) throws Exception {
    setupJsProgram(program);

    boolean madeChanges = doOptimize(program);

    TextOutput out = new DefaultTextOutput(true);
    return new Result(out.toString(), program, madeChanges);
  }

  protected static class Result {
    private final String originalCode;
    private final JsProgram program;
    private final boolean madeChanges;

    private Result(String originalCode, JsProgram program, boolean madeChanges) {
      this.originalCode = originalCode;
      this.program = program;
      this.madeChanges = madeChanges;
    }

    /**
     * Asserts that the optimized program is the same as the expected program by parsing and
     * comparing the printed source of both.
     * @param expected the expected js program
     */
    public void into(String... expected) throws Exception {
      JsProgram expectedProgram = new JsProgram();
      List<JsStatement> input = JsParser.parse(SourceOrigin.UNKNOWN,
          program.getScope(), new StringReader(Joiner.on("").join(expected)));
      expectedProgram.getGlobalBlock().getStatements().addAll(input);

      assertEquals(originalCode, expectedProgram.toSource(), program.toSource());
    }

    public void noChange() {
      assertFalse(madeChanges);
    }
  }

  /**
   * Optimize a JS program.
   *
   * @param js the source program
   * @return optimized JS source
   */
  protected String optimizeToSource(String js) throws Exception {
    JsProgram program = parseToProgram(js);
    doOptimize(program);

    TextOutput text = new DefaultTextOutput(true);
    JsVisitor generator = new JsSourceGenerationVisitor(text);
    generator.accept(program);
    return text.toString();
  }

  /**
   * Helper that only joins strings and parses to a program.
   */
  protected JsProgram parseToProgram(String... snippets) throws Exception {
    JsProgram program = new JsProgram();
    List<JsStatement> expected = JsParser.parse(SourceOrigin.UNKNOWN,
        program.getScope(), new StringReader(Joiner.on("").join(snippets)));

    program.getGlobalBlock().getStatements().addAll(expected);

    return program;
  }

  protected abstract boolean doOptimize(JsProgram program) throws Exception;

  /**
   * Override this method to provide additional pre-optimization setup of the js program.
   * @param program the program to update
   */
  protected void setupJsProgram(JsProgram program) {
  }
}
