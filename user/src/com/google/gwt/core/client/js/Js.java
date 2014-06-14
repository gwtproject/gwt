/*
 * Copyright 2014 Google Inc.
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
package com.google.gwt.core.client.js;

/**
 * Utility methods for invoking and manipulating Javascript.
 */
public class Js {
  /**
   * Execute inline Javascript efficiently. This method substitutes for the functionality
   * of JavaScript Native Methods by providing a method for synthesizing Javascript functions
   * without declaring native Java methods.
   * <p>
   * Example:
   * <code>
   * Js.js("$wnd.alert($0 + ' ' + $1)", "Hello", 42);
   * </code>
   * <p>
   * Each position in the varargs supplied to the function is available as a numerically indexed
   * Javascript local variable <b>$<i>n</i></b>.
   * <p>
   * Primitive types are passed to Javascript unboxed, and return values are returned unboxed if
   * the context is unboxed, such as when assigning to an integer, or passing to a another method.
   * As such, js() is a <i>magic method</i> not equivalent to an eval() statement. The first
   * argument to js() containing the Javascript string MUST be a literal. This expression is
   * parsed and optimized at <b>compile time</b>.
   * <p>
   * Note on format of javascript expression:
   * <p>
   * There are two forms permitted: Expression, and Statement.
   * <p>
   * An expression contains no semicolons, no control flow statements (if/do/for/while), such
   * as short mathematical expressions, variable or property evaluations, or method calls.
   * <p>
   * Expressions are synthesized into something resembling a JSNI method by the compiler, for
   * example, Js.js("$0 + $1 + Date.now()", 2, 3) would be equivalent to:
   * <p><code>
   * public static native int func(int $0, int $1) /*- { return $0 + $1 + Date.now(); } -* /;
   * </code>
   * <p>Note that a 'return' statement and trailing semicolon were added by the compiler. In
   * statement mode, the javascript string literal is placed in the body of a function with
   * no modification.
   *
   * @param javascript a <b>literal</b> Javascript string, cannot be an expression or variable
   * @param args arguments supplied as positional arguments to the function
   * @param <T> the return type of value of the function in Javascript
   */
  public static <T> T js(String javascript, Object... args) {
    throw new UnsupportedOperationException("Only available in compiled JS");
  }

  public static int jsInt(String javascript, Object... args) {
    throw new UnsupportedOperationException("Only available in compiled JS");
  }
  public static double jsNum(String javascript, Object... args) {
    throw new UnsupportedOperationException("Only available in compiled JS");
  }

  public static <T> T jsBool(String javascript, Object... args) {
    throw new UnsupportedOperationException("Only available in compiled JS");
  }

  /**
   * Creates literal Javascript arrays at compile time, equivalent to [arg1, arg2, arg3].
   * @param args the members of the array
   * @param <T> the contained type
   * @return
   */
  public static <T> JsArray<T> array(T... args) {
    throw new UnsupportedOperationException("Only available in compiled JS");
  }

  public static JsArray ints(int... args) {
    throw new UnsupportedOperationException("Only available in compiled JS");
  }

  public static JsArray<String> strings(String... args) {
    throw new UnsupportedOperationException("Only available in compiled JS");
  }

  /**
   * Creates literal Javascript object at compile time, equivalent to {arg1: arg2, arg3: arg4}.
   * @param args the key,value members of the array, must be even number of arguments
   * @param <T> the contained value type
   * @return
   */
  public static <T> JsMapLike<T> map(Object... args) {
    throw new UnsupportedOperationException("Only available in compiled JS");
  }
}
