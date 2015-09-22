/*
 * Copyright 2008 Google Inc.
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

import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.dev.javac.testing.impl.StaticJavaResource;
import com.google.gwt.dev.resource.Resource;
import com.google.gwt.dev.util.UnitTestTreeLogger;
import com.google.gwt.dev.util.arg.SourceLevel;
import com.google.gwt.thirdparty.guava.common.base.Joiner;

import junit.framework.TestCase;

import java.util.Collections;

/**
 * Tests the JSORestrictionsChecker.
 */
public class JSORestrictionsTest extends TestCase {

  public void testBaseClassFullyImplements() {
    String goodCode = Joiner.on('\n').join(
        "import com.google.gwt.core.client.JavaScriptObject;",
        "public class Buggy {",
        "  static interface IntfA {",
        "    void a();",
        "    void b();",
        "  }",
        "  static interface IntfB {",
        "    void c();",
        "  }",
        "  static abstract class BaseA extends JavaScriptObject {",
        "    public final void a() { }",
        "    protected BaseA() { }",
        "  }",
        "  static class BaseB extends BaseA implements IntfA {",
        "    public final void b() { }",
        "    protected BaseB() { }",
        "  }",
        "  static class LeafA extends BaseB {",
        "    protected LeafA() { }",
        "  }",
        "  static class LeafB extends BaseB implements IntfB {",
        "    public final void c() { }",
        "    protected LeafB() { }",
        "  }",
        "}");

    shouldGenerateNoError(goodCode);
  }

  /**
    * Java's version of the 'diamond' type definition pattern. Both a subclass
    * and superclass implement the same interface via two different chains of
    * resolution (extended class and inherited interface) Not good style, but
    * should be allowed.
    */
   public void testDiamondInheritance() {
     String goodCode = Joiner.on('\n').join(
         "import com.google.gwt.core.client.JavaScriptObject;",
         "public class Buggy {",
         "  public interface Interface {",
         "    void method();",
         "  }",
         "  public static abstract class CommonBase extends JavaScriptObject ",
         "      implements Interface {",
         "    protected CommonBase() {}",
         "  }",
         "  public static class Impl extends CommonBase implements Interface {",
         "    protected Impl() {}",
         "    public final void method() {}",
         "  }",
         "}");

     shouldGenerateNoError(goodCode);
   }

  public void testFinalClass() {
    String code = Joiner.on('\n').join(
        "import com.google.gwt.core.client.JavaScriptObject;",
        "final public class Buggy extends JavaScriptObject {",
        "  int nonfinal() { return 10; }",
        "  protected Buggy() { }",
        "}");

    shouldGenerateNoError(code);
  }

  public void testImplementsInterfaces() {
    String goodCode = Joiner.on('\n').join(
        "import com.google.gwt.core.client.JavaScriptObject;",
        "public class Buggy {",
        "  static interface Squeaks {",
        "    public void squeak();",
        "  }",
        "  static interface Squeaks2 extends Squeaks {",
        "    public void squeak();",
        "    public void squeak2();",
        "  }",
        "  static class Squeaker extends JavaScriptObject implements Squeaks {",
        "    public final void squeak() { }",
        "    protected Squeaker() { }",
        "  }",
        "  static class Squeaker2 extends Squeaker implements Squeaks, Squeaks2 {",
        "    public final void squeak2() { }",
        "    protected Squeaker2() { }",
        "  }",
        "}");

    shouldGenerateNoError(goodCode);
  }

  public void testInstanceField() {
    String buggyCode = Joiner.on('\n').join(
        "import com.google.gwt.core.client.JavaScriptObject;",
        "public class Buggy extends JavaScriptObject {",
        "  protected Buggy() { }",
        "  int myStsate = 3;",
        "}");

    shouldGenerateError(buggyCode, "Line 4: "
        + JSORestrictionsChecker.ERR_INSTANCE_FIELD);
  }

  public void testMultiArgConstructor() {
    String buggyCode = Joiner.on('\n').join(
        "import com.google.gwt.core.client.JavaScriptObject;",
        "public final class Buggy extends JavaScriptObject {",
        "  protected Buggy(int howBuggy) { }",
        "}");

    shouldGenerateError(buggyCode, "Line 3: "
        + JSORestrictionsChecker.ERR_CONSTRUCTOR_WITH_PARAMETERS);
  }

  public void testMultipleImplementations() {
    String buggyCode = Joiner.on('\n').join(
        "import com.google.gwt.core.client.JavaScriptObject;",
        "public class Buggy {",
        "  static interface Squeaks {",
        "    public void squeak();",
        "  }",
        "  static class Squeaker extends JavaScriptObject implements Squeaks {",
        "    public final void squeak() { }",
        "    protected Squeaker() { }",
        "  }",
        "  static class Squeaker2 extends JavaScriptObject implements Squeaks {",
        "    public final void squeak() { }",
        "    protected Squeaker2() { }",
        "  }",
        "}");

    shouldGenerateError(buggyCode, "Line 10: "
        + JSORestrictionsChecker.errAlreadyImplemented("Buggy$Squeaks",
            "Buggy$Squeaker", "Buggy$Squeaker2"));
  }

  /**
   * Normally, only a single JSO can implement an interface, but if all the
   * implementations are in a common base class, that should be allowed.
   */
  public void testMultipleImplementationsOk() {
    String goodCode = Joiner.on('\n').join(
        "import com.google.gwt.core.client.JavaScriptObject;",
        "public class Buggy {",
        "  public interface CommonInterface {",
        "    void method();",
        "  }",
        "  public interface CommonInterfaceExtended extends CommonInterface {}",
        "  public static class CommonBase extends JavaScriptObject",
        "      implements CommonInterface {",
        "    protected CommonBase() {}",
        "    public final void method() {}",
        "  }",
        "  public static class Impl1 extends CommonBase",
        "      implements CommonInterfaceExtended {",
        "    protected Impl1() {}",
        "  }",
        "  public static class Impl2 extends CommonBase",
        "      implements CommonInterfaceExtended {",
        "    protected Impl2() {}",
        "  }",
        "}");

    shouldGenerateNoError(goodCode);
  }

  public void testNew() {
    String buggyCode = Joiner.on('\n').join(
        "import com.google.gwt.core.client.JavaScriptObject;",
        "public class Buggy {",
        "  public static class MyJSO extends JavaScriptObject { ",
        "    protected MyJSO() { }",
        "  }",
        "  MyJSO makeOne() { return new MyJSO(); }",
        "}");

    shouldGenerateError(buggyCode, "Line 6: "
        + JSORestrictionsChecker.ERR_NEW_JSO);
  }

  public void testNoAnnotationOnInterfaceSubtype() {
    String goodCode = Joiner.on('\n').join(
        "import com.google.gwt.core.client.JavaScriptObject;",
        "public class Buggy {",
        "  static interface Squeaks {",
        "    public void squeak();",
        "  }",
        "  static interface Sub extends Squeaks {",
        "  }",
        "}");

    shouldGenerateNoError(goodCode);
  }

  public void testNoConstructor() {
    String buggyCode = Joiner.on('\n').join(
        "import com.google.gwt.core.client.JavaScriptObject;",
        "public class Buggy extends JavaScriptObject {",
        "}");

    // The public constructor is implicit.
    shouldGenerateError(buggyCode, "Line 2: "
        + JSORestrictionsChecker.ERR_NONPROTECTED_CONSTRUCTOR);
  }

  public void testNonEmptyConstructor() {
    String buggyCode = Joiner.on('\n').join(
        "import com.google.gwt.core.client.JavaScriptObject;",
        "public class Buggy extends JavaScriptObject {",
        "  protected Buggy() { while(true) { } }",
        "}");

    shouldGenerateError(buggyCode, "Line 3: "
        + JSORestrictionsChecker.ERR_NONEMPTY_CONSTRUCTOR);
  }

  public void testNonFinalMethod() {
    String buggyCode = Joiner.on('\n').join(
        "import com.google.gwt.core.client.JavaScriptObject;",
        "public class Buggy extends JavaScriptObject {",
        "  int nonfinal() { return 10; }",
        "  protected Buggy() { }",
        "}");

    shouldGenerateError(buggyCode, "Line 3: "
        + JSORestrictionsChecker.ERR_INSTANCE_METHOD_NONFINAL);
  }

  public void testNonJsoInterfaceExtension() {
    String goodCode = Joiner.on('\n').join(
        "import com.google.gwt.core.client.JavaScriptObject;",
        "public class Buggy {",
        "  static interface Squeaks {",
        "    public void squeak();",
        "  }",
        "  static interface Squeaks2 extends Squeaks {",
        "    public void squeak2();",
        "  }",
        "  static class JsoSqueaker extends JavaScriptObject implements Squeaks {",
        "    protected JsoSqueaker() {}",
        "    public final void squeak() {}",
        "  }",
        "  static class JavaSqueaker2 implements Squeaks2 {",
        "    protected JavaSqueaker2() {}",
        "    public void squeak() {}",
        "    public void squeak2() {}",
        "  }",
        "}");

    shouldGenerateNoError(goodCode);
  }

  public void testNonProtectedConstructor() {
    String buggyCode = Joiner.on('\n').join(
        "import com.google.gwt.core.client.JavaScriptObject;",
        "public class Buggy extends JavaScriptObject {",
        "  Buggy() { }",
        "}");

    shouldGenerateError(buggyCode, "Line 3: "
        + JSORestrictionsChecker.ERR_NONPROTECTED_CONSTRUCTOR);
  }

  public void testNonStaticInner() {
    String buggyCode = Joiner.on('\n').join(
        "import com.google.gwt.core.client.JavaScriptObject;",
        "public class Buggy {",
        "  public class MyJSO extends JavaScriptObject {",
        "    protected MyJSO() { }",
        "  }",
        "}");

    shouldGenerateError(buggyCode, "Line 3: "
        + JSORestrictionsChecker.ERR_IS_NONSTATIC_NESTED);
  }

  public void testNoOverride() {
    String buggyCode = Joiner.on('\n').join(
        "import com.google.gwt.core.client.JavaScriptObject;",
        "public class Buggy extends JavaScriptObject {",
        "  protected Buggy() { }",
        "  public final int hashCode() { return 0; }",
        "}");

    shouldGenerateError(buggyCode, "Line 4: "
        + JSORestrictionsChecker.ERR_OVERRIDDEN_METHOD);
  }

  public void testPrivateMethod() {
    String code = Joiner.on('\n').join(
        "import com.google.gwt.core.client.JavaScriptObject;",
        "public class Buggy extends JavaScriptObject {",
        "  private int nonfinal() { return 10; }",
        "  protected Buggy() { }",
        "}");

    shouldGenerateNoError(code);
  }

  public void testTagInterfaces() {
    String goodCode = Joiner.on('\n').join(
        "import com.google.gwt.core.client.JavaScriptObject;",
        "public class Buggy {",
        "  static interface Tag {}",
        "  static interface Tag2 extends Tag {}",
        "  static interface IntrExtendsTag extends Tag2 {",
        "    public void intrExtendsTag();",
        "  }",
        "  static class Squeaker3 extends JavaScriptObject implements Tag {",
        "    public final void squeak() { }",
        "    protected Squeaker3() { }",
        "  }",
        "  static class Squeaker4 extends JavaScriptObject implements Tag2 {",
        "    public final void squeak() { }",
        "    protected Squeaker4() { }",
        "  }",
        "  static class Squeaker5 extends JavaScriptObject implements IntrExtendsTag {",
        "    public final void intrExtendsTag() { }",
        "    protected Squeaker5() { }",
        "  }",
        "}");

    shouldGenerateNoError(goodCode);
  }

  public void testJsExport() {
    String goodCode = Joiner.on('\n').join(
        "import com.google.gwt.core.client.js.JsExport;",
        "public class Buggy {",
        "  @JsExport public static final String field = null;",
        "  @JsExport public static void method() {}",
        "  public interface Foo {",
        "    @JsExport String field1 = null;",
        "    interface ImplicitlyPublicInner {",
        "      @JsExport String field2 = null;",
        "    }",
    // TODO: enable after java 8 becomes default
    //     "@JsExport static void method1() {}",
        "  }",
        "}");

    shouldGenerateNoError(goodCode);
  }

  public void testJsExportOnClass() {
    String goodCode = Joiner.on('\n').join(
        "import com.google.gwt.core.client.js.JsExport;",
        "@JsExport public class Buggy {}");

    shouldGenerateNoError(goodCode);
  }

  public void testJsExportOnInterface() {
    String goodCode = Joiner.on('\n').join(
        "import com.google.gwt.core.client.js.JsExport;",
        "@JsExport public interface Buggy {}");

    shouldGenerateNoError(goodCode);
  }

  public void testJsExportOnEnum() {
    String goodCode = Joiner.on('\n').join(
        "import com.google.gwt.core.client.js.JsExport;",
        "@JsExport enum Buggy { TEST1, TEST2;}");

    shouldGenerateNoError(goodCode);
  }

  public void testJsExportNotOnEnumeration() {
    String buggyCode = Joiner.on('\n').join(
        "import com.google.gwt.core.client.js.JsExport;",
        "public enum Buggy {",
        "  @JsExport TEST1, TEST2;",
        "}");

    shouldGenerateError(buggyCode, "Line 3: " + JSORestrictionsChecker.ERR_JSEXPORT_ON_ENUMERATION);
  }

  public void testJsExportOnConstructors() {
    String goodCode = Joiner.on('\n').join(
        "import com.google.gwt.core.client.js.JsExport;",
        "public class Buggy {",
    // A constructor JsExported without explicit symbol is fine here.
    // Leave it to NameConflictionChecker.
        "  @JsExport public Buggy() { }",
        "  @JsExport(\"buggy1\") public Buggy(int a) { }",
        "  public Buggy(int a, int b) { }",
        "}");

    shouldGenerateNoError(goodCode);
  }

  public void testJsExportOnClassWithDefaultConstructor() {
    String goodCode = Joiner.on('\n').join(
        "import com.google.gwt.core.client.js.JsExport;",
        "@JsExport public class Buggy {}");

    shouldGenerateNoError(goodCode);
  }

  public void testJsExportOnClassWithExplicitConstructor() {
    String goodCode = Joiner.on('\n').join(
        "import com.google.gwt.core.client.js.JsExport;",
        "@JsExport public class Buggy {",
        "  public Buggy() { }",
        "}");

    shouldGenerateNoError(goodCode);
  }

  public void testJsExportOnClassWithOnePublicConstructor() {
    String goodCode = Joiner.on('\n').join(
        "import com.google.gwt.core.client.js.JsExport;",
        "@JsExport public class Buggy {",
        "  public Buggy() { }",
        "  private Buggy(int a) { }",
        "  protected Buggy(int a, int b) { }",
        "  Buggy(int a, int b, int c) { }",
        "}");

    shouldGenerateNoError(goodCode);
  }

  public void testJsExportOnClassWithMultipleConstructors() {
    String goodCode = Joiner.on('\n').join(
        "import com.google.gwt.core.client.js.JsExport;",
        "import com.google.gwt.core.client.js.JsNoExport;",
        "@JsExport public class Buggy {",
        "  @JsExport(\"Buggy1\") public Buggy() { }",
        "  @JsExport(\"Buggy2\") public Buggy(int a) { }",
        "  @JsExport public Buggy(int a, int b) { }",
        "  @JsNoExport public Buggy(int a, int b, int c) { }",
        "}");

    shouldGenerateNoError(goodCode);
  }

  public void testJsExportNotOnNonPublicClass() {
    String buggyCode = Joiner.on('\n').join(
        "import com.google.gwt.core.client.js.JsExport;",
        "public class Buggy {",
        "  private static class PrivateNested {",
        "    public static class PublicNested {",
        "      @JsExport public static Object foo() {return null;}",
        "    }",
        "  }",
        "}");

    shouldGenerateError(buggyCode, "Line 5: "
        + JSORestrictionsChecker.ERR_JSEXPORT_ONLY_CTORS_STATIC_METHODS_AND_STATIC_FINAL_FIELDS);
  }

  public void testJsExportNotOnNonPublicField() {
    String buggyCode = Joiner.on('\n').join(
        "import com.google.gwt.core.client.js.JsExport;",
        "public class Buggy {",
        "  @JsExport final static String foo = null;",
        "}");

    shouldGenerateError(buggyCode, "Line 3: "
        + JSORestrictionsChecker.ERR_JSEXPORT_ONLY_CTORS_STATIC_METHODS_AND_STATIC_FINAL_FIELDS);
  }

  public void testJsExportNotOnNonPublicMethod() {
    String buggyCode = Joiner.on('\n').join(
        "import com.google.gwt.core.client.js.JsExport;",
        "public class Buggy {",
        "  @JsExport static Object foo() {return null;}",
        "}");

    shouldGenerateError(buggyCode, "Line 3: "
        + JSORestrictionsChecker.ERR_JSEXPORT_ONLY_CTORS_STATIC_METHODS_AND_STATIC_FINAL_FIELDS);
  }

  public void testJsExportNotOnObjectMethod() {
    String buggyCode = Joiner.on('\n').join(
        "import com.google.gwt.core.client.js.JsExport;",
        "public class Buggy {",
        "  @JsExport public void foo() {}",
        "}");

    shouldGenerateError(buggyCode, "Line 3: "
        + JSORestrictionsChecker.ERR_JSEXPORT_ONLY_CTORS_STATIC_METHODS_AND_STATIC_FINAL_FIELDS);
  }

  public void testJsExportNotOnObjectField() {
    String buggyCode = Joiner.on('\n').join(
        "import com.google.gwt.core.client.js.JsExport;",
        "public class Buggy {",
        "  @JsExport public final String foo = null;",
        "}");

    shouldGenerateError(buggyCode, "Line 3: "
        + JSORestrictionsChecker.ERR_JSEXPORT_ONLY_CTORS_STATIC_METHODS_AND_STATIC_FINAL_FIELDS);
  }

  public void testJsExportNotOnNonFinalField() {
    String buggyCode = Joiner.on('\n').join(
        "import com.google.gwt.core.client.js.JsExport;",
        "public class Buggy {",
        "  @JsExport public static String foo = null;",
        "}");

    shouldGenerateError(buggyCode, "Line 3: "
        + JSORestrictionsChecker.ERR_JSEXPORT_ONLY_CTORS_STATIC_METHODS_AND_STATIC_FINAL_FIELDS);
  }

  public void testJsExportAndJsNotExportNotOnField() {
    String buggyCode = Joiner.on('\n').join(
        "import com.google.gwt.core.client.js.JsExport;",
        "import com.google.gwt.core.client.js.JsNoExport;",
        "public class Buggy {",
        "  @JsExport @JsNoExport public final static String foo = null;",
        "}");

    shouldGenerateError(buggyCode, "Line 4: "
        + JSORestrictionsChecker.ERR_EITHER_JSEXPORT_JSNOEXPORT);
  }

  public void testJsExportAndJsNotExportNotOnMethod() {
    String buggyCode = Joiner.on('\n').join(
        "import com.google.gwt.core.client.js.JsExport;",
        "import com.google.gwt.core.client.js.JsNoExport;",
        "public class Buggy {",
        "  @JsExport @JsNoExport public static void method() {}",
        "}");

    shouldGenerateError(buggyCode, "Line 4: "
        + JSORestrictionsChecker.ERR_EITHER_JSEXPORT_JSNOEXPORT);
  }

  public void testJsFunctionOnFunctionalInterface() {
    String goodCode = Joiner.on('\n').join(
        "import com.google.gwt.core.client.js.JsFunction;",
        "@JsFunction public interface Buggy {",
        "  int foo(int x);",
        "}");

    shouldGenerateNoError(goodCode);
  }

  // it is OK on JSORestrictionChecker but will be disallowed by JsInteropRestrictionChecker.
  public void testJsFunctionAndJsTypeOnInterface() {
    String goodCode = Joiner.on('\n').join(
        "import com.google.gwt.core.client.js.JsFunction;",
        "import com.google.gwt.core.client.js.JsType;",
        "@JsFunction @JsType public interface Buggy {",
        "  int foo(int x);",
        "}");

    shouldGenerateNoError(goodCode);
  }

  public void testJsFunctionNotOnClass() {
    String buggyCode = Joiner.on('\n').join(
        "import com.google.gwt.core.client.js.JsFunction;",
        "@JsFunction public class Buggy {",
        "  int foo(int x) {return 0;} ",
        "}");

    shouldGenerateError(buggyCode,
        "Line 2: " + JSORestrictionsChecker.ERR_JS_FUNCTION_ONLY_ALLOWED_ON_FUNCTIONAL_INTERFACE);
  }

  public void testJsFunctionNotOnNonFunctionalInterface1() {
    String buggyCode = Joiner.on('\n').join(
        "import com.google.gwt.core.client.js.JsFunction;",
        "@JsFunction public interface Buggy {",
        "  int foo(int x);",
        "  int bar(int x);",
        "}");

    shouldGenerateError(buggyCode,
        "Line 2: " + JSORestrictionsChecker.ERR_JS_FUNCTION_ONLY_ALLOWED_ON_FUNCTIONAL_INTERFACE);
  }

  public void testJsFunctionNotOnNonFunctionalInterface2() {
    String buggyCode = Joiner.on('\n').join(
        "import com.google.gwt.core.client.js.JsFunction;",
        "@JsFunction public interface Buggy {",
        "}");

    shouldGenerateError(buggyCode,
        "Line 2: " + JSORestrictionsChecker.ERR_JS_FUNCTION_ONLY_ALLOWED_ON_FUNCTIONAL_INTERFACE);
  }

  public void testJsFunctionNotOnInterfaceWithDefaultMethod() {
    String buggyCode = Joiner.on('\n').join(
        "import com.google.gwt.core.client.js.JsFunction;",
        "@JsFunction public interface Buggy {",
        "  int foo(int x);",
        "  default void bar() { }",
        "}");

    shouldGenerateError(SourceLevel.JAVA8, buggyCode,
        "Line 2: " + JSORestrictionsChecker.ERR_JS_FUNCTION_CANNOT_HAVE_DEFAULT_METHODS);
  }

  public void testJsTypeNativeStaticInitializer() {
    String buggyCode = Joiner.on('\n').join(
        "import com.google.gwt.core.client.js.JsType;",
        "@JsType(prototype = \"x\") public class Buggy {",
        "  static String s = \"hello\";",
        "  static {  s += \"hello\"; }",
        "}");

    shouldGenerateError(buggyCode,
        "Line 2: " + JSORestrictionsChecker.ERR_JS_TYPE_NATIVE_CANNOT_CLINIT);
  }

  public void testJsTypeNativeInlineInitializer() {
    String buggyCode = Joiner.on('\n').join(
        "import com.google.gwt.core.client.js.JsType;",
        "@JsType(prototype = \"x\") public class Buggy {",
        "  static final String s = new String(\"hello\");",
        "}");

    shouldGenerateError(buggyCode,
        "Line 2: " + JSORestrictionsChecker.ERR_JS_TYPE_NATIVE_CANNOT_CLINIT);
  }

  public void testJsTypeNativeInterfaceInlineInitializer() {
    String buggyCode = Joiner.on('\n').join(
        "import com.google.gwt.core.client.js.JsType;",
        "@JsType(prototype = \"x\") public interface Buggy {",
        "  static final String s = new String(\"hello\");",
        "}");

    shouldGenerateError(buggyCode,
        "Line 2: " + JSORestrictionsChecker.ERR_JS_TYPE_NATIVE_CANNOT_CLINIT);
  }


  public void testJsTypeNativeCompileTimeConstant() {
    String buggyCode = Joiner.on('\n').join(
        "import com.google.gwt.core.client.js.JsType;",
        "@JsType(prototype = \"x\") public class Buggy {",
        "  static final String s = \"hello\";",
        "}");

    shouldGenerateNoError(buggyCode);
  }

  public void testJsTypeInterfaceNativeCompileTimeConstant() {
    String buggyCode = Joiner.on('\n').join(
        "import com.google.gwt.core.client.js.JsType;",
        "@JsType(prototype = \"x\") public interface Buggy {",
        "  static final String s = \"hello\";",
        "}");

    shouldGenerateNoError(buggyCode);
  }
  /**
   * Test that when compiling buggyCode, the TypeOracleUpdater emits
   * expectedError somewhere in its output. The code should define a class named
   * Buggy.
   */
  private void shouldGenerateError(SourceLevel sourceLevel, CharSequence buggyCode,
      String... expectedErrors) {
    UnitTestTreeLogger.Builder builder = new UnitTestTreeLogger.Builder();
    builder.setLowestLogLevel(TreeLogger.ERROR);
    if (expectedErrors != null) {
      builder.expectError("Errors in \'/mock/Buggy.java\'", null);
      for (String e : expectedErrors) {
        builder.expectError(e, null);
      }
    }
    UnitTestTreeLogger logger = builder.createLogger();
    StaticJavaResource buggyResource = new StaticJavaResource("Buggy",
        buggyCode);
    TypeOracleTestingUtils.buildStandardTypeOracleWith(logger,
        Collections.<Resource> emptySet(),
        CompilationStateTestBase.getGeneratedUnits(buggyResource),
        sourceLevel);
    logger.assertCorrectLogEntries();
  }

  private void shouldGenerateError(CharSequence buggyCode, String... expectedErrors) {
    shouldGenerateError(SourceLevel.DEFAULT_SOURCE_LEVEL, buggyCode, expectedErrors);
  }

  private void shouldGenerateNoError(SourceLevel sourceLevel, String buggyCode) {
    shouldGenerateError(sourceLevel, buggyCode, (String[]) null);
  }

  private void shouldGenerateNoError(String buggyCode) {
    shouldGenerateError(buggyCode, (String[]) null);
  }
}
