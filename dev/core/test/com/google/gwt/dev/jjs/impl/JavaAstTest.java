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
package com.google.gwt.dev.jjs.impl;

import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.dev.CompilerContext;
import com.google.gwt.dev.javac.CompilationState;
import com.google.gwt.dev.javac.CompilationStateBuilder;
import com.google.gwt.dev.javac.testing.impl.JavaResourceBase;
import com.google.gwt.dev.javac.testing.impl.MockJavaResource;
import com.google.gwt.dev.jjs.JavaAstConstructor;
import com.google.gwt.dev.jjs.ast.JDeclaredType;
import com.google.gwt.dev.jjs.ast.JExpression;
import com.google.gwt.dev.jjs.ast.JField;
import com.google.gwt.dev.jjs.ast.JMethod;
import com.google.gwt.dev.jjs.ast.JMethodBody;
import com.google.gwt.dev.jjs.ast.JProgram;
import com.google.gwt.dev.jjs.ast.JReturnStatement;
import com.google.gwt.dev.resource.Resource;
import com.google.gwt.thirdparty.guava.common.base.Function;
import com.google.gwt.thirdparty.guava.common.collect.ImmutableSet;
import com.google.gwt.thirdparty.guava.common.collect.Iterables;
import com.google.gwt.thirdparty.guava.common.collect.Sets;

import java.util.Set;

/**
 * Tests that {@link GwtAstBuilder} correctly builds the AST.
 */
public class JavaAstTest extends JJSTestBase {

  // TODO(rluble): add similar tests to ensure that the AST construction is correct for all types
  // of nodes.

  @Override
  public void setUp() {
  }

  public void testSyntheticFields1() throws Exception {
    addAll(INNER_CLASSES_TEST);
    JProgram program = compile();
    assertTypeSanity(program, "com.google.gwt.InnerClassesTest$A",
        ImmutableSet.<String>of(), ImmutableSet.of("InnerClassesTest$A() <init>"));
    assertTypeSanity(program, "com.google.gwt.InnerClassesTest$A$AA",
        ImmutableSet.of("this$1:Lcom/google/gwt/InnerClassesTest$A;"),
        ImmutableSet.of("InnerClassesTest$A$AA(Lcom/google/gwt/InnerClassesTest$A;) <init>"));
    assertTypeSanity(program, "com.google.gwt.InnerClassesTest$B",
        ImmutableSet.<String>of(), ImmutableSet.of("InnerClassesTest$B() <init>"));
    assertTypeSanity(program, "com.google.gwt.InnerClassesTest$B$1",
        ImmutableSet.of("this$1:Lcom/google/gwt/InnerClassesTest$B;"),
        ImmutableSet.of("InnerClassesTest$B$1(Lcom/google/gwt/InnerClassesTest$B;"
            + "Lcom/google/gwt/InnerClassesTest$A;) <init>"));
  }

  public void testSyntheticFields2() throws Exception {
    addAll(INNER_CLASSES2_TEST);
    JProgram program = compile();
    assertTypeSanity(program, "com.google.gwt.InnerClasses2Test$A",
        ImmutableSet.<String>of(), ImmutableSet.of("InnerClasses2Test$A() <init>"));
    assertTypeSanity(program, "com.google.gwt.InnerClasses2Test$A$AA",
        ImmutableSet.of("this$1:Lcom/google/gwt/InnerClasses2Test$A;"),
        ImmutableSet.of("InnerClasses2Test$A$AA(Lcom/google/gwt/InnerClasses2Test$A;) <init>"));
    assertTypeSanity(program, "com.google.gwt.InnerClasses2Test$B",
        ImmutableSet.<String>of(), ImmutableSet.of("InnerClasses2Test$B() <init>"));
    assertTypeSanity(program, "com.google.gwt.InnerClasses2Test$B$1",
        ImmutableSet.of("this$1:Lcom/google/gwt/InnerClasses2Test$B;"),
        ImmutableSet.of("InnerClasses2Test$B$1(Lcom/google/gwt/InnerClasses2Test$B;"
            + "Lcom/google/gwt/InnerClasses2Test$A;) <init>"));
  }

  private void addAll(Resource... sourceFiles) {
    for (Resource sourceFile : sourceFiles) {
      sourceOracle.addOrReplace(sourceFile);
    }
  }

  protected void assertTypeSanity(JProgram program, String typeSignature,
      Set<String> expectedFieldSignatures, Set<String> expectedMethodSignatures) {
    JDeclaredType type = program.getFromTypeMap(typeSignature);
    assertNotNull("Type " + typeSignature + " not found ", type);

    Set<String> fieldSignatures = Sets.newHashSet(
        Iterables.transform(type.getFields(), new Function<JField, String>() {
          @Override
          public String apply(JField field) {
            return field.getSignature();
          }
        }));
    Set<String> missingFieldSignatures = Sets.newHashSet(expectedFieldSignatures);
    missingFieldSignatures.removeAll(fieldSignatures);
    assertTrue("Missing fields in type " + typeSignature + ":" + missingFieldSignatures + " found:"
        + fieldSignatures, missingFieldSignatures.isEmpty());
    Set<String> methodSignatures = Sets.newHashSet(
        Iterables.transform(type.getMethods(), new Function<JMethod, String>() {
          @Override
          public String apply(JMethod method) {
            return method.getSignature();
          }
        }));
    Set<String> missingMethodSignatures = Sets.newHashSet(expectedMethodSignatures);
    missingMethodSignatures.removeAll(methodSignatures);
    assertTrue("Missing methods in type " + typeSignature + ":" + missingMethodSignatures +
        " found:" + methodSignatures, missingMethodSignatures.isEmpty());
  }

  private void assertEqualExpression(String type, String expected, String expression)
      throws UnableToCompleteException {
    JExpression testExpresssion = getExpression(type, expression);
    assertEquals(expected, testExpresssion.toSource());
  }

  private JExpression getExpression(String type, String expression)
      throws UnableToCompleteException {
    JProgram program = compileSnippet(type, "return " + expression + ";");
    JMethod mainMethod = findMainMethod(program);
    JMethodBody body = (JMethodBody) mainMethod.getBody();
    JReturnStatement returnStmt = (JReturnStatement) body.getStatements().get(0);
    return returnStmt.getExpr();
  }

  private void assertEqualBlock(String expected, String input)
      throws UnableToCompleteException {
//    JBlock testExpression = getStatement(input);
//    assertEquals(formatSource("{ " + expected + "}"),
//        formatSource(testExpression.toSource()));
  }

  public static final MockJavaResource INNER_CLASSES_TEST =
      JavaResourceBase.createMockJavaResource("com.google.gwt.InnerClassesTest",
          "package com.google.gwt;",
          "public class InnerClassesTest {",
          "  public static class A {",
          "    public class AA {",
          "    }",
          "  }",
          "  public static class B extends A {",
          "    {",
          "      new AA() {",
          "      };",
          "    }",
          "  }",
          "}");


  public static final MockJavaResource INNER_CLASSES2_TEST =
      JavaResourceBase.createMockJavaResource("com.google.gwt.InnerClasses2Test",
          "package com.google.gwt;",
          "public class InnerClasses2Test {",
          "  public static class A {",
          "    public class AA {",
          "    }",
          "  }",
          "  public static class B {",
          "    {",
          "      new A().new AA() {",
          "      };",
          "    }",
          "  }",
          "}");
  /**
   * Removes most whitespace while still leaving one space separating words.
   *
   * Used to make the assertEquals ignore whitespace (mostly) while still retaining meaningful
   * output when the test fails.
   */
  private String formatSource(String source) {
    return source.replaceAll("\\s+", " ") // substitutes multiple whitespaces into one.
      .replaceAll("\\s([\\p{Punct}&&[^$]])", "$1")  // removes whitespace preceding symbols
                                                    // (except $ which can be part of an identifier)
      .replaceAll("([\\p{Punct}&&[^$]])\\s", "$1"); // removes whitespace succeeding symbols.
  }

  private JProgram compile() throws UnableToCompleteException {
    CompilerContext compilerContext =
        new CompilerContext.Builder().compileMonolithic(true).build();
    CompilationState state =
        CompilationStateBuilder.buildFrom(logger, compilerContext,
            sourceOracle.getResources(), getAdditionalTypeProviderDelegate());
    return JavaAstConstructor.construct(logger, state, compilerContext.getOptions(),
            null, "com.google.gwt.lang.Exceptions");
  }
}
