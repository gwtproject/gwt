/*
 * Copyright 2018 Google Inc.
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
package com.google.gwt.dev.jjs.test;

import com.google.gwt.core.client.GwtScriptOnly;
import com.google.gwt.junit.client.GWTTestCase;

import java.util.ArrayList;
import java.util.function.Supplier;

import java.io.Serializable;

/**
 * Tests Java 10 features. It is super sourced so that gwt can be compiled under Java 7.
 *
 * IMPORTANT: For each test here there must exist the corresponding method in the non super sourced
 * version.
 *
 * Eventually this test will graduate and not be super sourced.
 */
@GwtScriptOnly
public class Java10Test extends GWTTestCase {

  interface VarArgsFunction<T, R> {
    R apply(T... args);
  }

  @Override
  public String getModuleName() {
    return "com.google.gwt.dev.jjs.Java10Test";
  }

  public void testLocalVarType_DenotableTypes() {
    var i = 42;
    assertEquals(42, i);
    var s = "42";
    assertEquals("42", s);

    Supplier<String> initializer = () -> "37";
    var s2 = initializer.get();
    //to be sure that s2 was inferred as a string and not an Object
    String s3 = s2;
    assertEquals("37", s3);
  }

  public void testLocalVarType_Anonymous() {
    var o = new Object() {
      int i;
      String s;
    };
    o.i = 42;
    o.s = "42";
    assertEquals(42, o.i);
    assertEquals("42", o.s);
  }

  public void testLocalVarType_Ternary() {
    var value = true ? "a" : 'c';
    checkSerializableDispatch(value);
    checkComparableDispatch(value);
    assertEquals("a", value.toString());
  }

  private void checkSerializableDispatch(Object fail) {
    fail("should not be treated as object");
  }

  private void checkSerializableDispatch(Serializable pass) {
    // pass
  }

  private void checkComparableDispatch(Object fail) {
    fail("should not be treated as object");
  }

  private void checkComparableDispatch(Comparable<?> pass) {
    // pass
  }

  public void testLocalVarType_LambdaCapture() {
    var s = "42";
    Supplier<String> supplier = () -> s;
    assertEquals("42", supplier.get());
  }

  public void testLocalVarType_VarArg() {
    var args = new String[] {"4", "2"};
    VarArgsFunction<String, String> f = arr -> arr[0] + arr[1];
    assertEquals("42", f.apply(args));
  }

  public void testLocalVarType_LocalClass() {
    var i = 37;
    class Local {
      int m() {
        var i = 40;
        return i + 2;
      }

      int fromOuterScope() {
        return i;
      }
    }

    var l = new Local();
    assertEquals(37, l.fromOuterScope());
    assertEquals(42, l.m());
  }

  public void testLocalVarType_ForLoop() {
    var a = new String[] {"4", "2"};
    var s = "";
    for (var i = 0; i < a.length; i++) {
      s += a[i];
    }
    assertEquals("42", s);
  }

  public void testLocalVarType_EnhancedForLoopArray() {
    var a = new String[] {"4", "2"};
    var str = "";
    for (var s : a) {
      str += s;
    }
    assertEquals("42", str);
  }

  public void testLocalVarType_EnhancedNestedForLoopArray() {
    var m = new int[][] {{1, 2}, {3, 4}};
    var summ = 0;
    for (var row : m) {
      for (var cell : row) {
        summ += cell;
      }
    }
    assertEquals(10, summ);
  }

  public void testLocalVarType_EnhancedForLoopIterable() {
    var list = new ArrayList<String>();
    list.add("4");
    list.add("2");
    var str = "";
    for (var s : list) {
      str += s;
    }
    assertEquals("42", str);
  }
}
