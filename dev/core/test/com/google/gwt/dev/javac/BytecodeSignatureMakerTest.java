/*
 * Copyright 2011 Google Inc.
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

import com.google.gwt.dev.javac.testing.impl.MockJavaResource;

import com.google.gwt.thirdparty.guava.common.base.Joiner;

/**
 * Tests for {@link BytecodeSignatureMaker}
 */
public class BytecodeSignatureMakerTest extends CompilationStateTestBase {
  static final String TEST_CLASS_DEPENDENCY = "test.ClassDependency";

  public void testClassDependencySignature() {
    final MockJavaResource CLASS_DEP_ORIG =
        new MockJavaResource(TEST_CLASS_DEPENDENCY) {
          @Override
          public CharSequence getContent() {
            return Joiner.on('\n').join(
                "package test;",
                "public class ClassDependency {",
                "  static public final int fieldPublicStatic = 100;",
                "  public int fieldPublic;",
                "  protected int fieldProtected;",
                "  int fieldDefault;",
                "  private int fieldPrivate;",
                "  public int methodPublic() {return 1;};",
                "  protected int methodProtected(String arg) {return 1;};",
                "  int methodDefault() {return 1;};",
                "  private int methodPrivate(){return 1;};",
                "}");
          }
        };
    // A verbatim copy of CLASS_DEP_ORIG
    final MockJavaResource CLASS_DEP_NO_CHANGE =
        new MockJavaResource(TEST_CLASS_DEPENDENCY) {
          @Override
          public CharSequence getContent() {
            return CLASS_DEP_ORIG.getContent();
          }
        };
    final MockJavaResource CLASS_DEP_NO_PRIVATE =
        new MockJavaResource(TEST_CLASS_DEPENDENCY) {
          @Override
          public CharSequence getContent() {
            return Joiner.on('\n').join(
                "package test;",
                "public class ClassDependency {",
                "  static public final int fieldPublicStatic = 100;",
                "  public int fieldPublic;",
                "  protected int fieldProtected;",
                "  int fieldDefault;",
                // Missing fieldPrivate
                "  public int methodPublic() {return 1;};",
                "  protected int methodProtected(String arg) {return 1;};",
                "  int methodDefault() {return 1;};",
                // Missing methodPrivate
                "}");
          }
        };
    final MockJavaResource CLASS_DEP_NO_PROTECTED_FIELD =
        new MockJavaResource(TEST_CLASS_DEPENDENCY) {
          @Override
          public CharSequence getContent() {
            return Joiner.on('\n').join(
            "package test;",
            "public class ClassDependency {",
            "  static public final int fieldPublicStatic = 100;",
            "  public int fieldPublic;",
            // missing fieldProtected
            "  int fieldDefault;",
            "  private int fieldPrivate;",
            "  public int methodPublic() {return 1;};",
            "  protected int methodProtected(String arg) {return 1;};",
            "  int methodDefault() {return 1;};",
            "  private int methodPrivate(){return 1;};",
            "}");
            }
        };
    final MockJavaResource CLASS_DEP_NO_DEFAULT_FIELD =
        new MockJavaResource(TEST_CLASS_DEPENDENCY) {
          @Override
          public CharSequence getContent() {
            return Joiner.on('\n').join(
              "package test;",
              "public class ClassDependency {",
              "  static public final int fieldPublicStatic = 100;",
              "  public int fieldPublic;",
              "  protected int fieldProtected;",
              // missing fieldDefault
              "  private int fieldPrivate;",
              "  public int methodPublic() {return 1;};",
              "  protected int methodProtected(String arg) {return 1;};",
              "  int methodDefault() {return 1;};",
              "  private int methodPrivate(){return 1;};",
              "}");
          }
        };
    final MockJavaResource CLASS_DEP_NO_PUBLIC_FIELD =
        new MockJavaResource(TEST_CLASS_DEPENDENCY) {
          @Override
          public CharSequence getContent() {
            return Joiner.on('\n').join(
              "package test;",
              "public class ClassDependency {",
              "  static public final int fieldPublicStatic = 100;",
              // missing public field
              "  protected int fieldProtected;",
              "  int fieldDefault;",
              "  private int fieldPrivate;",
              "  public int methodPublic() {return 1;};",
              "  protected int methodProtected(String arg) {return 1;};",
              "  int methodDefault() {return 1;};",
              "  private int methodPrivate(){return 1;};",
              "}");
          }
        };
    final MockJavaResource CLASS_DEP_FIELD_VALUE_CHANGE =
        new MockJavaResource(TEST_CLASS_DEPENDENCY) {
          @Override
          public CharSequence getContent() {
            return Joiner.on('\n').join(
                "package test;",
                "public class ClassDependency {",
                // Value was 100
                "  static public final int fieldPublicStatic = 99;",
                "  public int fieldPublic;",
                "  protected int fieldProtected;",
                "  int fieldDefault;",
                "  private int fieldPrivate;",
                "  public int methodPublic() {return 1;};",
                "  protected int methodProtected(String arg) {return 1;};",
                "  int methodDefault() {return 1;};",
                "  private int methodPrivate(){return 1;};",
                "}");
          }
        };
    final MockJavaResource CLASS_DEP_ORDER =
        new MockJavaResource(TEST_CLASS_DEPENDENCY) {
          @Override
          public CharSequence getContent() {
            return Joiner.on('\n').join(
                "package test;",
                "public class ClassDependency {",
                // re-ordered this field
                "  public int fieldPublic;",
                "  static public final int fieldPublicStatic = 100;",
                "  protected int fieldProtected;",
                "  int fieldDefault;",
                "  private int fieldPrivate;",
                "  public int methodPublic() {return 1;};",
                // re-ordered this method
                "  int methodDefault() {return 1;};",
                "  protected int methodProtected(String arg) {return 1;};",
                "  private int methodPrivate(){return 1;};",
                "}");
          }
        };
    final MockJavaResource CLASS_DEP_INNER =
        new MockJavaResource(TEST_CLASS_DEPENDENCY) {
          @Override
          public CharSequence getContent() {
            return Joiner.on('\n').join(
                "package test;",
                "public class ClassDependency {",
                "  static public final int fieldPublicStatic = 100;",
                "  public int fieldPublic;",
                "  protected int fieldProtected;",
                "  int fieldDefault;",
                "  private int fieldPrivate;",
                "  public int methodPublic() {return 1;};",
                "  protected int methodProtected(String arg) {return 1;};",
                "  int methodDefault() {return 1;};",
                "  private int methodPrivate(){return 1;};",
                // Added an inner class definition
                "  public static class IgnoreMe {",
                "    private int ignoreThisMember;",
                "  }",
                "}");
          }
        };
    final MockJavaResource CLASS_DEP_DEPRECATED_FIELD =
        new MockJavaResource(TEST_CLASS_DEPENDENCY) {
          @Override
          public CharSequence getContent() {
            return Joiner.on('\n').join(
                "package test;",
                "public class ClassDependency {",
                "  static public final int fieldPublicStatic = 100;",
                "  @Deprecated",
                "  public int fieldPublic;",
                "  protected int fieldProtected;",
                "  int fieldDefault;",
                "  private int fieldPrivate;",
                "  public int methodPublic() {return 1;};",
                "  protected int methodProtected(String arg) {return 1;};",
                "  int methodDefault() {return 1;};",
                "  private int methodPrivate(){return 1;};",
                "}");
          }
        };
    final MockJavaResource CLASS_DEP_DEPRECATED_METHOD =
        new MockJavaResource(TEST_CLASS_DEPENDENCY) {
          @Override
          public CharSequence getContent() {
            return Joiner.on('\n').join(
                "package test;",
                "public class ClassDependency {",
                "  static public final int fieldPublicStatic = 100;",
                "  public int fieldPublic;",
                "  protected int fieldProtected;",
                "  int fieldDefault;",
                "  private int fieldPrivate;",
                "  @Deprecated",
                "  public int methodPublic() {return 1;};",
                "  protected int methodProtected(String arg) {return 1;};",
                "  int methodDefault() {return 1;};",
                "  private int methodPrivate(){return 1;};",
                "}");
          }
        };

    final MockJavaResource CLASS_DEP_ANNOTATED_FIELD =
        new MockJavaResource(TEST_CLASS_DEPENDENCY) {
          @Override
          public CharSequence getContent() {
            return Joiner.on('\n').join(
                "package test;",
                "public class ClassDependency {",
                "  static public final int fieldPublicStatic = 100;",
                "  @TestAnnotation(\"Foo\")",
                "  public int fieldPublic;",
                "  protected int fieldProtected;",
                "  int fieldDefault;",
                "  private int fieldPrivate;",
                "  public int methodPublic() {return 1;};",
                "  protected int methodProtected(String arg) {return 1;};",
                "  int methodDefault() {return 1;};",
                "  private int methodPrivate(){return 1;};",
                "}");
          }
        };
    final MockJavaResource CLASS_DEP_ANNOTATED_METHOD =
        new MockJavaResource(TEST_CLASS_DEPENDENCY) {
          @Override
          public CharSequence getContent() {
            return Joiner.on('\n').join(
                "package test;",
                "public class ClassDependency {",
                "  static public final int fieldPublicStatic = 100;",
                "  public int fieldPublic;",
                "  protected int fieldProtected;",
                "  int fieldDefault;",
                "  private int fieldPrivate;",
                "  @TestAnnotation(\"Foo\")",
                "  public int methodPublic() {return 1;};",
                "  protected int methodProtected(String arg) {return 1;};",
                "  int methodDefault() {return 1;};",
                "  private int methodPrivate(){return 1;};",
                "}");
          }
        };
    final MockJavaResource CLASS_DEP_JAVADOC =
        new MockJavaResource(TEST_CLASS_DEPENDENCY) {
          @Override
          public CharSequence getContent() {
            return Joiner.on('\n').join(
                "package test;",
                "public class ClassDependency {",
                "  /** a static field */",
                "  static public final int fieldPublicStatic = 100;",
                "  /** a public field */",
                "  public int fieldPublic;",
                "  protected int fieldProtected;",
                "  int fieldDefault;",
                "  private int fieldPrivate;",
                "  /** a public method */",
                "  public int methodPublic() {return 1;};",
                "  protected int methodProtected(String arg) {return 1;};",
                "  int methodDefault() {return 1;};",
                "  private int methodPrivate(){return 1;};",
                "}");
          }
        };

    final MockJavaResource TEST_ANNOTATION =
        new MockJavaResource("test.TestAnnotation") {
          @Override
          public CharSequence getContent() {
            return Joiner.on('\n').join(
                "package test;",
                "public @interface TestAnnotation {",
                "  String value();",
                "}");
          }
        };
    CompiledClass originalClass = buildClass(CLASS_DEP_ORIG);
    assertNotNull(originalClass);

    assertSignaturesEqual(originalClass, buildClass(CLASS_DEP_NO_CHANGE));
    assertSignaturesNotEqual(originalClass, buildClass(CLASS_DEP_NO_PRIVATE));
    assertSignaturesNotEqual(originalClass,
        buildClass(CLASS_DEP_NO_PUBLIC_FIELD));
    assertSignaturesNotEqual(originalClass,
        buildClass(CLASS_DEP_NO_PROTECTED_FIELD));
    assertSignaturesNotEqual(originalClass,
        buildClass(CLASS_DEP_NO_DEFAULT_FIELD));
    assertSignaturesNotEqual(originalClass,
        buildClass(CLASS_DEP_FIELD_VALUE_CHANGE));
    assertSignaturesEqual(originalClass, buildClass(CLASS_DEP_ORDER));
    assertSignaturesEqual(originalClass, buildClass(CLASS_DEP_INNER));
    assertSignaturesEqual(originalClass, buildClass(CLASS_DEP_DEPRECATED_FIELD));
    assertSignaturesEqual(originalClass,
        buildClass(CLASS_DEP_DEPRECATED_METHOD));

    oracle.add(TEST_ANNOTATION);
    assertSignaturesEqual(originalClass, buildClass(CLASS_DEP_ANNOTATED_FIELD));
    assertSignaturesEqual(originalClass, buildClass(CLASS_DEP_ANNOTATED_METHOD));
    assertSignaturesEqual(originalClass, buildClass(CLASS_DEP_JAVADOC));
  }

  public void testClassDependencySignatureWithExceptions() {
    MockJavaResource ILLEGAL_STATE_EXCEPTION =
        new MockJavaResource("java.lang.IllegalStateException") {
          @Override
          public CharSequence getContent() {
            return Joiner.on('\n').join(
              "package java.lang;",
              "public class IllegalStateException extends Throwable {}");
          }
        };
    MockJavaResource NUMBER_FORMAT_EXCEPTION =
        new MockJavaResource("java.lang.NumberFormatException") {
          @Override
          public CharSequence getContent() {
            return Joiner.on('\n').join(
                "package java.lang;",
                "public class NumberFormatException extends Throwable {}");
          }
        };
    MockJavaResource CLASS_DEP_EXCEPTION_ORIG =
        new MockJavaResource(TEST_CLASS_DEPENDENCY) {
          @Override
          public CharSequence getContent() {
            return Joiner.on('\n').join(
                "package test;",
                "public class ClassDependency {",
                "  public int methodPublic(String arg)",
                "      throws IllegalStateException, NumberFormatException {",
                "    return 1;",
                "  }",
                "}");
          }
        };
    MockJavaResource CLASS_DEP_EXCEPTION_MOD1 =
        new MockJavaResource(TEST_CLASS_DEPENDENCY) {
          @Override
          public CharSequence getContent() {
            return Joiner.on('\n').join(
                "package test;",
                "public class ClassDependency {",
                // no exceptions declared
                "  public int methodPublic(String arg) {return 1;};",
                "}");
          }
        };
    MockJavaResource CLASS_DEP_EXCEPTION_MOD2 =
        new MockJavaResource(TEST_CLASS_DEPENDENCY) {
          @Override
          public CharSequence getContent() {
            return Joiner.on('\n').join(
                "package test;",
                "public class ClassDependency {",
                // one exception declared
                "  public int methodPublic(String arg)",
                "     throws IllegalStateException {",
                "    return 1;",
                "  }",
                "}");
          }
        };
    MockJavaResource CLASS_DEP_EXCEPTION_MOD3 =
        new MockJavaResource(TEST_CLASS_DEPENDENCY) {
          @Override
          public CharSequence getContent() {
            return Joiner.on('\n').join(
                "package test;",
                "public class ClassDependency {",
                "  public int methodPublic(String arg)",
                // order of declared exceptions is flipped
                "     throws NumberFormatException, IllegalStateException {",
                "    return 1;",
                "  }",
                "}");
          }
        };

    oracle.add(ILLEGAL_STATE_EXCEPTION);
    oracle.add(NUMBER_FORMAT_EXCEPTION);
    CompiledClass originalClass = buildClass(CLASS_DEP_EXCEPTION_ORIG);
    assertSignaturesNotEqual(originalClass,
        buildClass(CLASS_DEP_EXCEPTION_MOD1));
    assertSignaturesNotEqual(originalClass,
        buildClass(CLASS_DEP_EXCEPTION_MOD2));
    assertSignaturesEqual(originalClass, buildClass(CLASS_DEP_EXCEPTION_MOD3));
  }

  public void testClassDependencySignatureWithGenerics() {
    MockJavaResource CLASS_DEP_GENERIC_ORIG =
        new MockJavaResource(TEST_CLASS_DEPENDENCY) {
          @Override
          public CharSequence getContent() {
            return Joiner.on('\n').join(
                "package test;",
                "public class ClassDependency<T> {",
                "  public int methodPublic(T arg) {return 1;};",
                "}");
          }
        };
    MockJavaResource CLASS_DEP_GENERIC_PARAMETERIZED =
        new MockJavaResource(TEST_CLASS_DEPENDENCY) {
          @Override
          public CharSequence getContent() {
            return Joiner.on('\n').join(
                "package test;",
                "import java.util.Map;",
                "public class ClassDependency<T extends Map> {",
                "  public int methodPublic(T arg) {return 1;};",
                "}");
          }
        };
    CompiledClass originalClass = buildClass(CLASS_DEP_GENERIC_ORIG);
    assertSignaturesNotEqual(originalClass,
        buildClass(CLASS_DEP_GENERIC_PARAMETERIZED));
  }

  public void testClassDependencySignatureWithInterfaces() {
    MockJavaResource CLASS_DEP_INTERFACE_ORIG =
        new MockJavaResource(TEST_CLASS_DEPENDENCY) {
          @Override
          public CharSequence getContent() {
            return Joiner.on('\n').join(
                "package test;",
                "import java.util.Map;",
                "import java.util.Collection;",
                "public class ClassDependency implements Map, Collection {",
                "  public int methodPublic(String arg) { return 1;}",
                "}");
          }
        };
    MockJavaResource CLASS_DEP_INTERFACE_MOD1 =
        new MockJavaResource(TEST_CLASS_DEPENDENCY) {
          @Override
          public CharSequence getContent() {
            return Joiner.on('\n').join(
                "package test;",
                "import java.util.Map;",
                "import java.util.Collection;",
                // no interfaces
                "public class ClassDependency {",
                "  public int methodPublic(String arg) { return 1;}",
                "}");
          }
        };
    MockJavaResource CLASS_DEP_INTERFACE_MOD2 =
        new MockJavaResource(TEST_CLASS_DEPENDENCY) {
          @Override
          public CharSequence getContent() {
            return Joiner.on('\n').join(
                "package test;",
                "import java.util.Map;",
                "import java.util.Collection;",
                // only one interface
                "public class ClassDependency implements Map {",
                "  public int methodPublic(String arg) { return 1;}",
                "}");
          }
        };
    MockJavaResource CLASS_DEP_INTERFACE_MOD3 =
        new MockJavaResource(TEST_CLASS_DEPENDENCY) {
          @Override
          public CharSequence getContent() {
            return Joiner.on('\n').join(
                "package test;",
                "import java.util.Map;",
                "import java.util.Collection;",
                // flipped order of interface decls
                "public class ClassDependency implements Collection, Map {",
                "  public int methodPublic(String arg) { return 1;}",
                "}");
          }
        };
    CompiledClass originalClass = buildClass(CLASS_DEP_INTERFACE_ORIG);
    assertSignaturesNotEqual(originalClass,
        buildClass(CLASS_DEP_INTERFACE_MOD1));
    assertSignaturesNotEqual(originalClass,
        buildClass(CLASS_DEP_INTERFACE_MOD2));
    assertSignaturesEqual(originalClass, buildClass(CLASS_DEP_INTERFACE_MOD3));
  }

  public void testVarargChangesSignature() {
    MockJavaResource CLASS_WITH_VARARG =
        new MockJavaResource(TEST_CLASS_DEPENDENCY) {
          @Override
          public CharSequence getContent() {
            return Joiner.on('\n').join(
                "package test;",
                "import java.util.Map;",
                "import java.util.Collection;",
                "public class ClassDependency {",
                "  public int methodPublic(String... arg) { return 1;}",
                "}");
          }
        };
    MockJavaResource CLASS_WITH_NOVARARG =
        new MockJavaResource(TEST_CLASS_DEPENDENCY) {
          @Override
          public CharSequence getContent() {
            return Joiner.on('\n').join(
                "package test;",
                "import java.util.Map;",
                "import java.util.Collection;",
                "public class ClassDependency {",
                "  public int methodPublic(String[] arg) { return 1;}",
                "}");
          }
        };
    assertSignaturesNotEqual(buildClass(CLASS_WITH_NOVARARG), buildClass(CLASS_WITH_VARARG));
  }

  private void assertSignaturesEqual(CompiledClass original,
      CompiledClass updated) {
    String originalSignature =
        BytecodeSignatureMaker.getCompileDependencySignature(original.getBytes());
    String updatedSignature =
        BytecodeSignatureMaker.getCompileDependencySignature(updated.getBytes());
    if (!originalSignature.equals(updatedSignature)) {
      String originalRaw =
          BytecodeSignatureMaker.getCompileDependencyRawSignature(original.getBytes());
      String updatedRaw =
          BytecodeSignatureMaker.getCompileDependencyRawSignature(updated.getBytes());
      fail("Signatures don't match.  raw data expected=<" + originalRaw
          + "> actual=<" + updatedRaw + ">");
    }
  }

  private void assertSignaturesNotEqual(CompiledClass original,
      CompiledClass updated) {
    String originalSignature =
        BytecodeSignatureMaker.getCompileDependencySignature(original.getBytes());
    String updatedSignature =
        BytecodeSignatureMaker.getCompileDependencySignature(updated.getBytes());
    if (originalSignature.equals(updatedSignature)) {
      String originalRaw =
          BytecodeSignatureMaker.getCompileDependencyRawSignature(original.getBytes());
      String updatedRaw =
          BytecodeSignatureMaker.getCompileDependencyRawSignature(updated.getBytes());
      fail("Signatures should not match.  raw data expected=<" + originalRaw
          + "> actual=<" + updatedRaw + ">");
    }
  }

  private CompiledClass buildClass(MockJavaResource resource) {
    oracle.addOrReplace(resource);
    this.rebuildCompilationState();
    CompilationUnit unit =
        state.getCompilationUnitMap().get(resource.getTypeName());
    assertNotNull(unit);
    String internalName = resource.getTypeName().replace(".", "/");
    CategorizedProblem[] problems = unit.getProblems();
    if (problems != null && problems.length != 0) {
      fail(problems[0].toString());
    }
    for (CompiledClass cc : unit.getCompiledClasses()) {
      if (cc.getInternalName().equals(internalName)) {
        return cc;
      }
    }
    fail("Couldn't find class " + internalName + " after compiling.");
    return null;
  }
}
