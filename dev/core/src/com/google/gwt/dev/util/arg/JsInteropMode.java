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
package com.google.gwt.dev.util.arg;

/**
 * Specifies which level of JsInterop support is enabled in the compiler,
 * NONE, JS, and CLOSURE.
 */
public enum JsInteropMode {
  /**
   * Disabled, interop annotations are no-ops.
   */
  NONE,
  /**
   * For hand coded, external JS, not run through an external compiler.
   */
  JS,
  /**
   * For cases where GWT code is post-optimized and checked with the Closure Compiler.
   */
  CLOSURE
}
