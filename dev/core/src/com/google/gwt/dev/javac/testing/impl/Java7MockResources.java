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
package com.google.gwt.dev.javac.testing.impl;

/**
 * Contains Java 7 source files used for testing.
 */
public class Java7MockResources {
  public static final MockJavaResource INTEGERLITERALS = new MockJavaResource(
      "com.google.gwt.IntegerLiterals") {
    @Override
    public CharSequence getContent() {
      StringBuilder code = new StringBuilder();
      code.append("package com.google.gwt;\n");
      code.append("public class IntegerLiterals {\n");
      code.append("  int million = 1_000_000;\n");
      code.append("}\n");
      return code;
    }
  };

  public static final MockJavaResource STRINGSWITCHTEST =
      new MockJavaResource("com.google.gwt.StringSwitchTest") {
        @Override
        public CharSequence getContent() {
          StringBuilder code = new StringBuilder();
          code.append("package com.google.gwt;\n");
          code.append("public class StringSwitchTest {\n");
          code.append("  int test() {\n");
          code.append("    int result = 0;\n");
          code.append("    String f = \"AA\";\n");
          code.append("    switch(f) {\n");
          code.append("      case \"CC\": result = - 1; break;\n");
          code.append("      case \"BB\": result = 1;\n");
          code.append("      case \"AA\": result = result + 1; break;\n");
          code.append("      default: result = -2; break;\n");
          code.append("    }\n");
          code.append("  return result;\n");
          code.append("  }\n");
          code.append("}\n");
          return code;
        }
      };

  public static final MockJavaResource DIAMOND_OPERATOR = new MockJavaResource(
      "com.google.gwt.DiamondTest") {
    @Override
    public CharSequence getContent() {
      StringBuilder code = new StringBuilder();
      code.append("package com.google.gwt;\n");
      code.append("import com.google.gwt.List;\n");
      code.append("import com.google.gwt.ArrayList;\n");
      code.append("public class DiamondTest {\n");
      code.append("  void test() {\n");
      code.append("    List<String> list = new ArrayList<>();\n");
      code.append("  }  \n");
      code.append("}\n");
      return code;
    }
  };

  public static final MockJavaResource TRY_WITH_RESOURCES =
      new MockJavaResource("com.google.gwt.TryWithResourcesTest") {
        @Override
        public CharSequence getContent() {
          StringBuilder code = new StringBuilder();
          code.append("package com.google.gwt;\n");
          code.append("import com.google.gwt.TestResource;\n");
          code.append("public class TryWithResourcesTest {\n");
          code.append("  void test() { \n");
          code.append("    try (TestResource tr1 = new TestResource(); \n");
          code.append("         TestResource tr2 = new TestResource()) {\n");
          code.append("    }  \n");
          code.append("  }  \n");
          code.append("}\n");
          return code;
        }
      };

  public static final MockJavaResource MULTI_EXCEPTIONS =
      new MockJavaResource("com.google.gwt.MultiExceptionTest") {
        @Override
        public CharSequence getContent() {
          StringBuilder code = new StringBuilder();
          code.append("package com.google.gwt;\n");
          code.append("import com.google.gwt.Exception1;\n");
          code.append("import com.google.gwt.Exception2;\n");
          code.append("public class MultiExceptionTest {\n");
          code.append("  void test() { \n");
          code.append("    int i = 1;\n");
          code.append("    try {\n");
          code.append("      if (i > 0) {\n");
          code.append("        throw new Exception1();\n");
          code.append("      } else {\n");
          code.append("        throw new Exception2();\n");
          code.append("      }");
          code.append("    } catch (Exception1 | Exception2 e) { \n");
          code.append("    }\n");
          code.append("  }  \n");
          code.append("}\n");
          return code;
        }
      };

  public static final MockJavaResource LIST_T = new MockJavaResource(
      "com.google.gwt.List") {
    @Override
    public CharSequence getContent() {
      StringBuilder code = new StringBuilder();
      code.append("package com.google.gwt;\n");
      code.append("public interface List<T> {\n");
      code.append("  T method1(); \n");
      code.append("}\n");
      return code;
    }
  };

  public static final MockJavaResource ARRAYLIST_T = new MockJavaResource(
      "com.google.gwt.ArrayList") {
    @Override
    public CharSequence getContent() {
      StringBuilder code = new StringBuilder();
      code.append("package com.google.gwt;\n");
      code.append("import com.google.gwt.List;\n");
      code.append("public class ArrayList<T> implements List<T> {\n");
      code.append("  public T method1() { return null; } \n");
      code.append("}\n");
      return code;
    }
  };

  public static final MockJavaResource TEST_RESOURCE = new MockJavaResource(
      "com.google.gwt.TestResource") {
    @Override
    public CharSequence getContent() {
      StringBuilder code = new StringBuilder();
      code.append("package com.google.gwt;\n");
      code.append("public class TestResource implements AutoCloseable {\n");
      code.append("  public void close() { } \n");
      code.append("}\n");
      return code;
    }
  };

  public static final MockJavaResource EXCEPTION1 = new MockJavaResource(
      "com.google.gwt.Exception1") {
    @Override
    public CharSequence getContent() {
      StringBuilder code = new StringBuilder();
      code.append("package com.google.gwt;\n");
      code.append("import java.lang.Exception;\n");
      code.append("public class Exception1 extends Exception {\n");
      code.append("}\n");
      return code;
    }
  };

  public static final MockJavaResource EXCEPTION2 = new MockJavaResource(
      "com.google.gwt.Exception2") {
    @Override
    public CharSequence getContent() {
      StringBuilder code = new StringBuilder();
      code.append("package com.google.gwt;\n");
      code.append("import java.lang.Exception;\n");
      code.append("public class Exception2 extends Exception {\n");
      code.append("}\n");
      return code;
    }
  };
}
