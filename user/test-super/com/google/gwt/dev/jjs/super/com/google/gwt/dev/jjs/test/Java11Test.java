/*
 * Copyright 2019 Google Inc.
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

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Tests Java 11 features. It is super sourced so that gwt can be compiles under Java 8.
 *
 * IMPORTANT: For each test here there must exist the corresponding method in the non super sourced
 * version.
 *
 * Eventually this test will graduate and not be super sourced.
 */
@GwtScriptOnly
public class Java11Test extends GWTTestCase {

  @interface NotNull {
  }

  interface Lambda<T> {
    T run(T a, T b);
  }

  @Override
  public String getModuleName() {
    return "com.google.gwt.dev.jjs.Java11Test";
  }

  public void testLambdaParametersVarType() {
    Lambda<String> l = (@NotNull var x, var y) -> x + y;
    assertEquals("12", l.run("1", "2"));
  }

  public void testLambdaParametersVarType_function() {
    List<String> l = Arrays.asList("a", "b");
    l = l.stream().map((var s) -> s.toUpperCase()).collect(Collectors.toList());
    assertEquals("A", l.get(0));
    assertEquals("B", l.get(1));
  }
}
