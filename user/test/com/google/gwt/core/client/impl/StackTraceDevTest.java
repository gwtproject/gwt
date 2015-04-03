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
package com.google.gwt.core.client.impl;

import com.google.gwt.junit.DoNotRunWith;
import com.google.gwt.junit.Platform;

/**
 * Tests {@link StackTraceCreator} in the native mode.
 */
@DoNotRunWith(Platform.Prod)
public class StackTraceDevTest extends StackTraceTestBase {

  @Override
  public String getModuleName() {
    return "com.google.gwt.core.StackTraceNative";
  }

  @Override
  protected String[] getTraceJava() {
    return new String[] {
        "throwException2",
        "throwException1",
        "getLiveException",
        "testTraceJava",
    };
  }

  @Override
  protected String[] getTraceRecursion() {
    return new String[] {
        "throwException2",
        "throwException1",
        "throwRecursive",
        "throwRecursive",
        "throwRecursive",
        "throwException2",
        "throwException1",
        "getLiveException",
        "testTraceRecursion",
    };
  }

  @Override
  protected String[] getTraceJse(Object whatToThrow) {
    return new String[] {
        "invokeJavascript",
        "doInvoke",
        "invokeNative",
        "invokeNativeObject",
        "invokeNativeObject",
        "throwJse",
        "throwException2",
        "throwException1",
        "getLiveException",
        "assertJse",
    };
  }
}
