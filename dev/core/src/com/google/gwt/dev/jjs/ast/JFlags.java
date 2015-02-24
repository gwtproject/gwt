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
package com.google.gwt.dev.jjs.ast;

/**
 * Flags to encode boolean properties
 */
public class JFlags {
  static final int ABSTRACT = 0x0001;
  static final int STATIC = 0x0002;
  static final int FINAL = 0x0004;
  static final int SYNTHETIC = 0x0008;
  static final int NO_EXPORT = 0x0010;

  // JMethod specific flags
  static final int INLINING_ALLOWED = 0x0020;
  static final int SIDE_EFFECTS = 0x0040;
  static final int DEFAULT_METHOD = 0x0080;
  static final int JS_PROPERTY = 0x0100;

  // JField specific flags.
  static final int COMPILE_TIME_CONSTANT = 0x0200;
  static final int THIS_REF = 0x0400;
  static final int VOLATILE = 0x0800;

  // JParameter specific flags.
  static final int THIS_PARAMETER = 0x1000;

  // JDeclaredType specific flags
  static final int EXTERNAL = 0x2000;
  static final int JS_PROTOTYPE = 0x4000;
  static final int ORDINALIZED = 0x8000;
}
