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
import com.google.gwt.junit.client.GWTTestCase;

/**
 * Tests for {@link Impl}.
 */
public class ImplTest extends GWTTestCase {

  @Override
  public String getModuleName() {
    return "com.google.gwt.core.Core";
  }

  // A function that is not referenced and can be prunned very early in the compilation pipeline.
  public static String prunnableFunction() {
    return "Prunnable";
  }

  // A very simple function that will certainly be inlined away in optimized compiles.
  public static String inlinableFunction() {
    return "Inlinable";
  }

  private static final class Foo {
    // A very simple function that will certainly be made static but not inlined.
    // because it refers to its parameter twice.
    public final String statifiableFunction(String o) {
      return "Statifiable " + o + o;
    }
  }

  @DoNotRunWith(Platform.Devel)
  public void testPinnedByImpl_getNameOf() {
    String s = "s";
    if (Math.random() > 0.5) {
      s = "s'";
    }

    Foo foo = new Foo();
    assertNotNull(foo.statifiableFunction(s));
    assertNotNull(inlinableFunction());
    assertNotNull(
        Impl.getNameOf("@com.google.gwt.core.client.impl.ImplTest::prunnableFunction()"));
    assertNotNull(
        Impl.getNameOf("@com.google.gwt.core.client.impl.ImplTest::inlinableFunction()"));
    assertNotNull(
        Impl.getNameOf("@com.google.gwt.core.client.impl.ImplTest.Foo::statifiableFunction(*)"));
  }
}
