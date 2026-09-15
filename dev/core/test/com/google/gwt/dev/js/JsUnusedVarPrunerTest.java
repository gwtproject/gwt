/*
 * Copyright 2026 GWT Project Authors
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

/**
 * Tests the JsUnusedVarPruner optimizer.
 */
public class JsUnusedVarPrunerTest extends OptimizerTestBase {

  public void testRemoveUnreadVar() throws Exception {
    assertEquals("function f(){}\n", optimize("function f() { var a; }"));
  }

  public void testRemoveUnreadVarWithSideEffectFreeInit() throws Exception {
    assertEquals("function f(){}\n", optimize("function f() { var a = 5; }"));
  }

  public void testPreserveReadVar() throws Exception {
    assertEquals("function f(){var a=5;alert(a)}\n",
        optimize("function f() { var a = 5; alert(a); }"));
  }

  public void testRemoveWriteOnlyVar() throws Exception {
    // A write-only var: the var decl is removed and assignment replaced with just the RHS.
    assertEquals("function f(){foo()}\n",
        optimize("function f() { var a; a = foo(); }"));
  }

  public void testRemoveWriteOnlyVarInSubexpression() throws Exception {
    // Write-only var used as a subexpression: assignment replaced with just RHS.
    assertEquals("function f(){bar(baz())}\n",
        optimize("function f() { var a; bar(a = baz()); }"));
  }

  public void testRemoveUnreferencedPreserveOtherVars() throws Exception {
    // Remove the unused variable - in the future we could inline away the other var too
    assertEquals("function f(){var b=1;alert(b)}\n",
        optimize("function f() { var a, b = 1; alert(b); }"));
  }

  public void testPreserveVarWithSideEffectInit() throws Exception {
    // Can't remove because the initializer has side effects, could be improved in the future
    assertEquals("function f(){var a=foo()}\n",
        optimize("function f() { var a = foo(); }"));
  }

  public void testPreserveCompoundAssignment() throws Exception {
    // Compound assignment reads the var, so it must be preserved.
    assertEquals("function f(){var a=0;a+=1}\n",
        optimize("function f() { var a = 0; a += 1; }"));
  }

  public void testPreserveIncrementedVar() throws Exception {
    // i++ reads the value, so i must be preserved.
    assertEquals("function f(){var i=0;i++}\n",
        optimize("function f() { var i = 0; i++; }"));
  }

  public void testPreserveNameUsedAsQualifier() throws Exception {
    // style is read as a qualifier in style.opacity, so it must be preserved.
    assertEquals("function f(){var style=input.style;style.opacity=0}\n",
        optimize("function f() { var style = input.style; style.opacity = 0; }"));
  }

  public void testPreserveVarReadInNestedFunctionAndPruneSibling() throws Exception {
    // Given multiple local vars, one only read in a nested function, only the other should
    // be pruned.
    assertEquals("function f(){var a=5;return function(){return a}}\n",
        optimize("function f() { var a = 5, b = 6; return function() { return a; }; }"));
  }

  public void testNestedFunctionLocalPreservedAndOuterUnreadPruned() throws Exception {
    // Ensure local decls stay local, and reads are checked in nested functions. "8" is left
    // behind, other optimizations will remove it.
    assertEquals("function f(){var a=5;return function(){var b=6;8;return a+b}}\n",
        optimize("function f() { var a = 5, c = 7;"
            + " return function() { var b = 6; c = 8; return a + b; }; }"));
  }

  public void testPreserveQualifiedAssignmentWhilePruningWriteOnlyLocal() throws Exception {
    // Don't mistake local "a" for the qualified "obj.a".
    assertEquals("function f(){obj.a=foo()}\n",
        optimize("function f() { var a; obj.a = a = foo(); }"));
  }

  private String optimize(String js) throws Exception {
    return optimizeToSource(js, JsSymbolResolver.class, JsUnusedVarPruner.class);
  }
}
