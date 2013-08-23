/*
 * Copyright 2013 Google Inc.
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
package com.google.gwt.dev.javac;

import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.dev.javac.testing.impl.Java7MockResources;
import com.google.gwt.dev.javac.testing.impl.JavaResourceBase;
import com.google.gwt.dev.javac.testing.impl.MockJavaResource;
import com.google.gwt.dev.util.arg.SourceLevel;

import java.util.Collection;
import java.util.List;

/**
 * Test class for language features introduced in Java 7.
 *
 * Only tests that the JDT accepts and compiles the new syntax..
 */
public class JdtJava7Test extends JdtCompilerTestBase {

  public void testCompileNewStyleLiterals() throws Exception {
    assertResourcesCompileSuccessfully(Java7MockResources.LIST_T,
        Java7MockResources.ARRAYLIST_T,
        Java7MockResources.NEW_INTEGER_LITERALS_TEST);
  }

  public void testCompileSwitchWithStrings() throws Exception {
    assertResourcesCompileSuccessfully(Java7MockResources.LIST_T,
        Java7MockResources.ARRAYLIST_T,
        Java7MockResources.SWITCH_ON_STRINGS_TEST);
  }

  public void testCompileDiamondOperator() throws Exception {
    assertResourcesCompileSuccessfully(Java7MockResources.LIST_T,
        Java7MockResources.ARRAYLIST_T,
        Java7MockResources.DIAMOND_OPERATOR_TEST);
  }

  public void testCompileTryWithResources() throws Exception {
    assertResourcesCompileSuccessfully(Java7MockResources.TEST_RESOURCE,
        Java7MockResources.TRY_WITH_RESOURCES_TEST);
  }

  public void testCompileMultiExceptions() throws Exception {
    assertResourcesCompileSuccessfully(Java7MockResources.EXCEPTION1,
        Java7MockResources.EXCEPTION2,
        Java7MockResources.MULTI_EXCEPTION_TEST);
  }

  /**
   * Test cases for JDT bug 397462.
   */
  public void testJdtBugNameClash_1() throws Exception {
    assertResourcesCompileSuccessfully(ECLIPSE_397462_BUG_1);
  }

  public void testJdtBugNameClash_2() throws Exception {
    assertResourcesCompileSuccessfully(ECLIPSE_397462_BUG_2);
  }

  public void testJdtBugNameClash_3() throws Exception {
    List<CompilationUnit> units = compile(Java7MockResources.LIST_T, ACTUAL_NAME_CLASH);
    assertOnlyLastUnitHasErrors(units);
  }

  public void testJdtBugNameClash_4() throws Exception {
    assertResourcesCompileSuccessfully(Java7MockResources.LIST_T, ECLIPSE_397462_BUG_4);
  }

  public void testJdtBugNameClash_5() throws Exception {
    assertResourcesCompileSuccessfully(Java7MockResources.LIST_T, ECLIPSE_397462_BUG_5);
  }

  public void testJdtBugNameClash_6() throws Exception {
    assertResourcesCompileSuccessfully(Java7MockResources.LIST_T, ECLIPSE_397462_BUG_6);
  }

  public static final MockJavaResource ECLIPSE_397462_BUG_1 =
      JavaResourceBase.createMockJavaResource("eclipse.X",
          "package eclipse;",
          "public class X {",
          "  public static class Base {",
          "    static Base factoryMethod() {",
          "      return null;",
          "    }",
          "  }",
          "  public static class Child<S> extends Base {",
          "    static <T> Child<T> factoryMethod() {",
          "      return null;",
          "    }",
          "  }",
          "}");

  public static final MockJavaResource ECLIPSE_397462_BUG_2 =
      JavaResourceBase.createMockJavaResource("eclipse.X",
          "package eclipse;",
          "public class X {",
          "  public static class Base<S> {",
          "    static <T> Base<T> factoryMethod() {",
          "      return null;",
          "    }",
          "  }",
            "public static class Child<P,Q> extends Base<P> {",
            "  static <R,S> Child<R,S> factoryMethod() {",
          "      return null;",
          "    }",
          "  }",
          "}");

  public static final MockJavaResource ACTUAL_NAME_CLASH =
      JavaResourceBase.createMockJavaResource("eclipse.X",
          "package eclipse;",
          "import com.google.gwt.List;",
          "public class X {",
          "  public static class Base<R> {",
          "    static <R> Base<R> factoryMethod(List<R> x) {",
          "      return null;",
          "    }",
          "  }",
          "  public static class Child<R> extends Base<R> {",
          "    static <P,Q> Child<P> factoryMethod(List<P> x) {",
          "      return null;",
          "    }",
          "  }",
          "}");

  public static final MockJavaResource ECLIPSE_397462_BUG_4 =
      JavaResourceBase.createMockJavaResource("eclipse.X",
          "package eclipse;",
          "import com.google.gwt.List;",
          "public class X {",
          "  public static class Base<R> {",
          "    static  <R> Base<R> factoryMethod(List<R> x) {",
          "      return null;",
          "    }",
          "  }",
          "  public static class Child<R> extends Base<R> {",
          "    static <Q> Child<Q> factoryMethod(List<Q> x) {",
          "      return null;",
          "    }",
          "  }",
          "}");

  public static final MockJavaResource ECLIPSE_397462_BUG_5 =
      JavaResourceBase.createMockJavaResource("eclipse.X",
          "package eclipse;",
          "public class X {",
          "  public static class Base<S> {",
          "    static <T> Object factoryMethod() {",
          "      return null;",
          "    }",
          "  }",
          "  public static class Child<P,Q> extends Base<P> {",
          "    static <R,S> Child<R,S> factoryMethod() {",
          "      return null;",
          "    }",
          "  }",
          "}");

  public static final MockJavaResource ECLIPSE_397462_BUG_6 =
      JavaResourceBase.createMockJavaResource("eclipse.X",
          "package eclipse;",
          "import com.google.gwt.List;",
          "public class X {",
          "  public static class Base {",
          "    <T extends Base> List<T> factoryMethod() {",
          "      return null;",
          "    }",
          "  }",
          "  public static class Child extends Base {",
          "    @Override",
          "    List<Child> factoryMethod() {",
          "      return null;",
          "    }",
          "  }",
          "}");


  protected List<CompilationUnit> compileImpl(Collection<CompilationUnitBuilder> builders)
      throws UnableToCompleteException {
    return doCompile(builders, SourceLevel.JAVA7);
  }
}
