/*
 * Copyright 2007 Google Inc.
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
package com.google.gwt.dev.generator.ast;

import java.util.List;

/**
 * A Node that represents a Java <code>while</code> loop.
 */
public class WhileLoop implements Statements {

  private final StatementsList body = new StatementsList();

  private final String test;

  /**
   * Creates a new <code>while</code> loop with <code>test</code> as the test
   * {@link Expression}. The <code>WhileLoop</code> has an empty body.
   *
   * @param test A textual <code>boolean</code> {@link Expression}. Must not be
   * <code>null</code>.
   */
  public WhileLoop(String test) {
    this.test = test;
  }

  @Override
  public List<Statements> getStatements() {
    return body.getStatements();
  }

  @Override
  public String toCode() {
    return "while ( " + test + " ) {\n" +
        body.toCode() + "\n" +
        "}\n";
  }
}
