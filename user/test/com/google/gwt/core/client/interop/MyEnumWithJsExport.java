/*
 * Copyright 2015 Google Inc.
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
package com.google.gwt.core.client.interop;

import com.google.gwt.core.client.js.JsExport;

@JsExport
public enum MyEnumWithJsExport {
  @JsExport("myEnumWithJsExportTest1")TEST1, TEST2;

  public static int publicStaticMethod() {
    return 0;
  }

  public static final int publicStaticFinalField = 1;

  public static int publicStaticField = 2;

  public final int publicFinalField = 3;

  private static final int privateStaticFinalField = 4;

  static final int protectedStaticFinalField = 5;

  public int publicMethod() {
    return 0;
  }

  static int protectedStaticMethod() {
    return 0;
  }

  private static int privateStaticMethod() {
    return 0;
  }
}
