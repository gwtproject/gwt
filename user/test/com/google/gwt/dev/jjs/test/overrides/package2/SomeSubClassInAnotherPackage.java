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
package com.google.gwt.dev.jjs.test.overrides.package2;

import com.google.gwt.dev.jjs.test.overrides.package1.SomeParent;

/**
 * Subclass in another package, with a protected method with the same name as a package
 * private one in the super class.
 */
public class SomeSubClassInAnotherPackage extends SomeParent {
  /**
   * For this method, eclipse is giving me the warning "The method
   * SomeSubClassInAnotherPackage.m() does not override the inherited method
   * from SomeParentClass since it is private to a different package".
   */
  protected String m() {
    return "SomeSubClassInAnotherPackage";
  }

  /**
   * A public way to manually call the method above.
   */
  public static String pleaseCallm(SomeSubClassInAnotherPackage obj) {
    return obj.m();
  }
}