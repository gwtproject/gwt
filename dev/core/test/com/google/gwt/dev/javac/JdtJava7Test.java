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

import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.dev.javac.testing.impl.JavaResourceBase;
import com.google.gwt.dev.javac.testing.impl.MockJavaResource;
import com.google.gwt.dev.resource.Resource;
import com.google.gwt.dev.util.Strings;
import com.google.gwt.dev.util.arg.OptionSource;

import junit.framework.TestCase;

import org.eclipse.jdt.core.compiler.CategorizedProblem;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Test class for language features introduced in Java 7.
 *
 * Only tests that the JDT accepts and compiles the new syntax..
 */
public class JdtJava7Test extends TestCase {

  static void assertUnitHasErrors(CompilationUnit unit, int numErrors) {
    assertTrue(unit.isError());
    assertEquals(numErrors, unit.getProblems().length);
  }

  static void assertUnitsCompiled(Collection<CompilationUnit> units) {
    for (CompilationUnit unit : units) {
      if (unit.isError()) {
        String[] messages = new String[unit.getProblems().length];
        int i = 0;
        for (CategorizedProblem pb : unit.getProblems()) {
          messages[i] = pb.getMessage();
         }
        fail(Strings.join(messages, "\n"));
      }
      assertTrue(unit.getCompiledClasses().size() > 0);
    }
  }

  public void testCompileNewStyleLiterals() throws Exception {
    List<CompilationUnitBuilder> builders = new ArrayList<CompilationUnitBuilder>();
    addAll(builders, JavaResourceBase.getStandardResources());
    addAll(builders, LIST_T, ARRAYLIST_T, INTEGERLITERALS);
    Collection<CompilationUnit> units = compile(TreeLogger.NULL, builders);
    assertUnitsCompiled(units);
  }

  public void testCompileSwitchWithStrings() throws Exception {
    List<CompilationUnitBuilder> builders = new ArrayList<CompilationUnitBuilder>();
    addAll(builders, JavaResourceBase.getStandardResources());
    addAll(builders, LIST_T, ARRAYLIST_T, STRINGSWITCHTEST);
    Collection<CompilationUnit> units = compile(TreeLogger.NULL, builders);
    assertUnitsCompiled(units);
  }

  public void testCompileDiamondOperator() throws Exception {
    List<CompilationUnitBuilder> builders = new ArrayList<CompilationUnitBuilder>();
    addAll(builders, JavaResourceBase.getStandardResources());
    addAll(builders, LIST_T, ARRAYLIST_T, DIAMOND_OPERATOR);
    Collection<CompilationUnit> units = compile(TreeLogger.NULL, builders);
    assertUnitsCompiled(units);
  }

  public void testCompileTryWithResources() throws Exception {
    List<CompilationUnitBuilder> builders = new ArrayList<CompilationUnitBuilder>();
    addAll(builders, JavaResourceBase.getStandardResources());
    addAll(builders, JAVA_LANG_AUTOCLOSEABLE,
        TEST_RESOURCE, TRY_WITH_RESOURCES);
    Collection<CompilationUnit> units = compile(TreeLogger.NULL, builders);
    assertUnitsCompiled(units);
  }

  public void testCompileMultiExceptions() throws Exception {
    List<CompilationUnitBuilder> builders = new ArrayList<CompilationUnitBuilder>();
    addAll(builders, JavaResourceBase.getStandardResources());
    addAll(builders, EXCEPTION1, EXCEPTION2, MULTI_EXCEPTIONS);
    Collection<CompilationUnit> units = compile(TreeLogger.NULL, builders);
    assertUnitsCompiled(units);
  }


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
          code.append("  int test() { \n");
          code.append("               int result = 0;");
          code.append("               String f = \"AA\";");
          code.append("               switch(f) {");
          code.append("               case \"CC\": result = - 1; break;");
          code.append("               case \"BB\": result = 1;");
          code.append("               case \"AA\": result = result + 1; break;");
          code.append("               default: result = -2; break;");
          code.append("               }  \n");
          code.append("  return result; \n");
          code.append("  }  \n");
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
          code.append("    try( TestResource tr1 = new TestResource(); \n");
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


  public static final MockJavaResource JAVA_LANG_AUTOCLOSEABLE = new MockJavaResource(
      "java.lang.AutoCloseable") {
        @Override
        public CharSequence getContent() {
          StringBuilder code = new StringBuilder();
          code.append("package java.lang;\n");
          code.append("import java.lang.Exception;\n");
          code.append("public interface AutoCloseable {\n");
          code.append("  void close() throws Exception; \n");
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

  public static final MockJavaResource EXCEPTION1= new MockJavaResource(
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

  public static final MockJavaResource EXCEPTION2= new MockJavaResource(
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

  private void addAll(Collection<CompilationUnitBuilder> units,
                      Resource... sourceFiles) {
    for (Resource sourceFile : sourceFiles) {
      units.add(CompilationUnitBuilder.create(sourceFile));
    }
  }

  private  List<CompilationUnit> compile(TreeLogger logger,
      Collection<CompilationUnitBuilder> builders) throws UnableToCompleteException {
    return JdtCompiler.compile(logger, builders, OptionSource.SourceLevel._7);
  }

}
