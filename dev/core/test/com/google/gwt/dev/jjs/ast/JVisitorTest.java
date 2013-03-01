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
package com.google.gwt.dev.jjs.ast;

import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.dev.jjs.impl.JJSTestBase;

/**
 * Tests {@link JVisitor}.
 */
public class JVisitorTest extends JJSTestBase {



  /** Tests {@link JVisitor#getCurrentMethod }
   */
  public void testGetCurrentMethodSimple() throws UnableToCompleteException {
    JVisitor v = new JVisitor() {
      @Override
      public void endVisit(JStatement x, Context ctx) {
        // Statements can only appear inside method declarations.
        assertNotNull("getCurrentMethod() must return not null while visiting a method",
            getCurrentMethod());
        assertNull("getCurrentField() must return null while visiting a method",
            getCurrentField());
      }

      public void endVisit(JMethod x, Context ctx) {
        // Statements can only appear inside method declarations.
        assertEquals(x, getCurrentMethod());
        assertNull("getCurrentField() must return null while visiting a method",
            getCurrentField());
      }

      public void endVisit(JField x, Context ctx) {
        // Statements can only appear inside method declarations.
        assertEquals(x, getCurrentField());
        assertNull("getCurrentMethod() must return null while visiting a method",
            getCurrentMethod());
      }

      public void endVisit(JDeclaredType x, Context ctx) {
        // Statements can only appear inside method declarations.
        assertNull("getCurrentField() must return null while visiting a class",
            getCurrentField());
        assertNull("getCurrentMethod() must return null while visiting a class ",
            getCurrentMethod());
      }

    };

    JProgram program = compileSnippet("void",  "new ClassA();");
    v.accept(program);
  }

  public void setUp() throws Exception {
    addSnippetClassDecl(
        "static class  ClassA { " +
        "  static public int field1;" +
        "  static private ClassA field2;" +
        "  int field3;" +
        "  protected ClassA field4;" +
        "  int field5 = 5;" +
        "  int field6 = ClassA.method1();" +
        "  static { field1 = 3; }" +
        "  static public int method1() { return 2; }" +
        "  public int method2() { return 3; }" +
        "  public ClassA() { field4 = null; }" +
        "}"
    );

  }
}
