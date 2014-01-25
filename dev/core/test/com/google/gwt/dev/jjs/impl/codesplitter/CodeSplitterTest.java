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
package com.google.gwt.dev.jjs.impl.codesplitter;

import com.google.gwt.core.ext.PropertyOracle;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.linker.SymbolData;
import com.google.gwt.core.ext.linker.impl.StandardSymbolData;
import com.google.gwt.dev.CompilerContext;
import com.google.gwt.dev.cfg.BindingProperty;
import com.google.gwt.dev.cfg.ConditionNone;
import com.google.gwt.dev.cfg.ConfigurationProperty;
import com.google.gwt.dev.cfg.Properties;
import com.google.gwt.dev.cfg.StaticPropertyOracle;
import com.google.gwt.dev.javac.CompilationState;
import com.google.gwt.dev.javac.CompilationStateBuilder;
import com.google.gwt.dev.javac.testing.impl.MockJavaResource;
import com.google.gwt.dev.jjs.JavaAstConstructor;
import com.google.gwt.dev.jjs.JsOutputOption;
import com.google.gwt.dev.jjs.ast.JMethod;
import com.google.gwt.dev.jjs.ast.JProgram;
import com.google.gwt.dev.jjs.ast.JRunAsync;
import com.google.gwt.dev.jjs.impl.ArrayNormalizer;
import com.google.gwt.dev.jjs.impl.CastNormalizer;
import com.google.gwt.dev.jjs.impl.ControlFlowAnalyzer;
import com.google.gwt.dev.jjs.impl.GenerateJavaScriptAST;
import com.google.gwt.dev.jjs.impl.JJSTestBase;
import com.google.gwt.dev.jjs.impl.JavaToJavaScriptMap;
import com.google.gwt.dev.jjs.impl.MethodCallTightener;
import com.google.gwt.dev.jjs.impl.TypeTightener;
import com.google.gwt.dev.js.ast.JsBlock;
import com.google.gwt.dev.js.ast.JsContext;
import com.google.gwt.dev.js.ast.JsFunction;
import com.google.gwt.dev.js.ast.JsName;
import com.google.gwt.dev.js.ast.JsProgram;
import com.google.gwt.dev.js.ast.JsVisitor;
import com.google.gwt.thirdparty.guava.common.collect.Lists;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Unit test for {@link com.google.gwt.dev.jjs.impl.codesplitter.CodeSplitter}.
 */
public class CodeSplitterTest extends JJSTestBase {

  /**
   * A {@link MultipleDependencyGraphRecorder} that does nothing.
   */
  private static final MultipleDependencyGraphRecorder NULL_RECORDER =
      new MultipleDependencyGraphRecorder() {
        @Override
        public void close() {
        }

        @Override
        public void endDependencyGraph() {
        }

        @Override
        public void methodIsLiveBecause(JMethod liveMethod, ArrayList<JMethod> dependencyChain) {
        }

        @Override
        public void open() {
        }

        @Override
        public void startDependencyGraph(String name, String extnds) {
        }
      };

  // These will be the functions that are shared between fragments. This unit test will
  // be based for finding these function in the proper fragments.
  private final String functionA = "public static void functionA() {}";
  private final String functionB = "public static void functionB() {}";
  private final String functionC = "public static void functionC() {}";
  private final String functionD = "public static void functionD() {}";
  private final String initialA = "public static void initialA() {}";
  private final String initialB = "public static void initialB() {}";

  // Compilation Configuration Properties.
  private BindingProperty stackMode = new BindingProperty("compiler.stackMode");
  private BindingProperty[] orderedProps = {stackMode};
  private String[] orderedPropValues = {"STRIP" };

  private ConfigurationProperty initialSequenceProp =
      new ConfigurationProperty(CodeSplitters.PROP_INITIAL_SEQUENCE, true);
  private ConfigurationProperty[] configProps = { initialSequenceProp };


  private JProgram jProgram = null;
  private JsProgram jsProgram = null;

  public int leftOverMergeSize = 0;
  public int expectedFragmentCount = 0;
  public List<JRunAsync> initialAsyncSequence = Lists.newArrayList();

  @Override
  public void setUp() throws Exception {
    super.setUp();
    stackMode.addDefinedValue(new ConditionNone(), "STRIP");
    jsProgram = new JsProgram();
  }

  public void testSimple() throws UnableToCompleteException {
    StringBuffer code = new StringBuffer();
    code.append("package test;\n");
    code.append("import com.google.gwt.core.client.GWT;\n");
    code.append("import com.google.gwt.core.client.RunAsyncCallback;\n");
    code.append("public class EntryPoint {\n");
    code.append("static {");
    //code.append("  functionC();");
    code.append("}");
    code.append(functionA);
    code.append(functionB);
    code.append(functionC);
    code.append("  public static void onModuleLoad() {\n");
    code.append("functionC();");
    // Fragment #1
    code.append(createRunAsync("functionA();"));
    // Fragment #1 (merged)
    code.append(createRunAsync("functionA(); functionB();"));
    // Fragment #2
    code.append(createRunAsync("functionC();"));
    code.append("  }\n");
    code.append("}\n");

    expectedFragmentCount = 4;
    compileSnippet(code.toString());

    // init + 2 fragments + leftover.
    assertFragmentCount(4);
    assertInFragment("functionA", 1);

    // Verify that functionA isn't duplicated else where.
    assertNotInFragment("functionA", 0);
    assertNotInFragment("functionA", 2);
    assertNotInFragment("functionA", 3);

    assertInFragment("functionB", 1);

    // functionC must be in the initial fragment.
    assertInFragment("functionC", 0);
  }


  public void testPredefinedAsyncGrouping() throws UnableToCompleteException {
    StringBuffer code = new StringBuffer();
    code.append("package test;\n");
    code.append("import com.google.gwt.core.client.GWT;\n");
    code.append("import com.google.gwt.core.client.RunAsyncCallback;\n");
    code.append("public class EntryPoint {\n");
    code.append(functionA);
    code.append(functionB);
    code.append(functionC);
    code.append(functionD);
    code.append(createNamedRunAsyncCallback("RunAsyncCallBack1", "functionA(); functionD();"));
    code.append(createNamedRunAsyncCallback("RunAsyncCallBack2", "functionB(); functionD();"));
    code.append(createNamedRunAsyncCallback("RunAsyncCallBack3", "functionC();"));

    code.append("  public static void onModuleLoad() {\n");
    // Fragment #1
    code.append("createCallBack1();");
    // Fragment #2
    code.append("createCallBack2();");
    code.append("createCallBack3();");

    code.append("  }\n");
    code.append("  private static void createCallBack1() {\n");
    code.append("    GWT.runAsync( new RunAsyncCallBack1() );");
    code.append("  }\n");
    code.append("  private static void createCallBack2() {\n");
    code.append("    GWT.runAsync(RunAsyncCallBack2.class, new RunAsyncCallBack2() );");
    code.append("  }\n");
    code.append("  private static void createCallBack3() {\n");
    code.append("    GWT.runAsync(RunAsyncCallBack2.class, new RunAsyncCallBack3() );");
    code.append("  }\n");
    code.append("}\n");

    // Use 1 to 1 merging.
    expectedFragmentCount = -1;
    compileSnippet(code.toString());

    // 1 initial + 2 fragments + leftover.
    assertFragmentCount(4);
    // TODO(rluble): using fragment numbers here is kind of tricky as the ordering is not
    // guaranteed.

    // functionB must be in the fragment #2. As first are all the user specified merges.
    assertInFragment("functionA", 2);

    // Verify that functionA isn't duplicated else where.
    assertNotInFragment("functionA", 0);
    assertNotInFragment("functionA", 1);
    assertNotInFragment("functionA", 3);

    // functionB must be in the fragment #1.
    assertInFragment("functionB", 1);

    // functionC must be in the fragment #1.
    assertInFragment("functionC", 1);

    // functionC must be in the leftover.
    assertInFragment("functionD", 3);
  }


  public void testSimpleWithInitialSequence() throws UnableToCompleteException {
    // Set up configuration property.
    initialSequenceProp.addValue("@test.EntryPoint::createInitialCallBack1()");
    initialSequenceProp.addValue("@test.EntryPoint::createInitialCallBack2()");

    StringBuffer code = new StringBuffer();
    code.append("package test;\n");
    code.append("import com.google.gwt.core.client.GWT;\n");
    code.append("import com.google.gwt.core.client.RunAsyncCallback;\n");
    code.append("public class EntryPoint {\n");
    code.append(functionA);
    code.append(functionB);
    code.append(functionC);
    code.append(initialA);
    code.append(initialB);
    code.append(createNamedRunAsyncCallback("InitialRunAsyncCallBack1", "initialA();"));
    code.append(createNamedRunAsyncCallback("InitialRunAsyncCallBack2", "initialB();"));

    code.append("  public static void onModuleLoad() {\n");
    code.append("functionC();");
    // Fragment #3
    code.append(createRunAsync("functionA();"));
    // Fragment #3 (merged)
    code.append(createRunAsync("functionA(); functionB();"));
    // Fragment #4
    code.append(createRunAsync("functionC();"));
    // initial fragments #1, #2
    code.append("createInitialCallBack1();");
    code.append("createInitialCallBack2();");
    code.append("  }\n");
    code.append("  private static void createInitialCallBack1() {\n");
    code.append("    GWT.runAsync( new InitialRunAsyncCallBack1() );");
    code.append("  }\n");
    code.append("  private static void createInitialCallBack2() {\n");
    code.append("    GWT.runAsync( new InitialRunAsyncCallBack2() );");
    code.append("  }\n");
    code.append("}\n");

    expectedFragmentCount = 6;
    compileSnippet(code.toString());

    // 3 initial + 2 fragments + leftover.
    assertFragmentCount(6);
    assertInFragment("functionA", 3);

    // Verify that functionA isn't duplicated else where.
    assertNotInFragment("functionA", 0);
    assertNotInFragment("functionA", 1);
    assertNotInFragment("functionA", 2);
    assertNotInFragment("functionA", 4);
    assertNotInFragment("functionA", 5);

    assertInFragment("functionB", 3);

    // functionC must be in the initial fragment.
    assertInFragment("functionC", 0);

    // initialA must be in the initial fragment #1.
    assertInFragment("initialA", 1);

    // functionC must be in the initial fragment #2.
    assertInFragment("initialB", 2);
  }

  public void testOnSuccessCallCast() throws UnableToCompleteException {
    StringBuffer code = new StringBuffer();
    code.append("package test;\n");
    code.append("import com.google.gwt.core.client.GWT;\n");
    code.append("import com.google.gwt.core.client.RunAsyncCallback;\n");
    code.append("public class EntryPoint {\n");
    code.append("  " + functionA);
    code.append("  " + functionB);
    code.append("  " + functionC);
    code.append("  public static void onModuleLoad() {\n");
    code.append("    functionC();");
    code.append("    " + createRunAsync("(RunAsyncCallback)", "functionA();"));
    code.append("    " + createRunAsync("(RunAsyncCallback)", "functionB();"));
    code.append("  }\n");
    code.append("}\n");

    expectedFragmentCount = 4;
    compileSnippet(code.toString());

    // init + 2 fragments + leftover.
    assertFragmentCount(4);

    assertInFragment("functionA", 1);
    assertInFragment("functionB", 2);

    // Verify that functionA and B aren't in the leftover.
    assertNotInFragment("functionA", 3);
    assertNotInFragment("functionB", 3);
  }

  public void testMergeLeftOvers() throws UnableToCompleteException {
    StringBuffer code = new StringBuffer();
    code.append("package test;\n");
    code.append("import com.google.gwt.core.client.GWT;\n");
    code.append("import com.google.gwt.core.client.RunAsyncCallback;\n");
    code.append("public class EntryPoint {\n");
    code.append(functionA);
    code.append(functionB);
    code.append(functionC);
    code.append("  public static void onModuleLoad() {\n");
    // Fragment #1
    code.append(createRunAsync("functionA();"));
    // Fragment #2
    code.append(createRunAsync("functionB();"));
    // Fragment #3
    code.append(createRunAsync("functionC();"));
    code.append("  }\n");
    code.append("}\n");

    expectedFragmentCount = 2;
    leftOverMergeSize = 100 * 1024 /* 100k minumum */;
    this.compileSnippet(code.toString());

    // init + leftover.
    assertFragmentCount(2);
    assertInFragment("functionA", 1);
    assertInFragment("functionB", 1);
    assertInFragment("functionC", 1);
  }

  /**
   * Tests that everything in the magic Array class is considered initially
   * live.
   */
  public void testArrayIsInitial() throws UnableToCompleteException {
    JProgram program = compileSnippet("void", "");
    ControlFlowAnalyzer cfa = CodeSplitter.computeInitiallyLive(program);

    assertTrue(cfa.getInstantiatedTypes().contains(findType(program, "com.google.gwt.lang.Array")));
  }


  /**
   * Test that the conversion from -XfragmentCount expectCount into number of exclusive fragments
   * is correct.
   */
  public void testExpectedFragmentCountFromFragmentMerge() {

    // Exclusive fragments are totalFragments - (1 + initials) - leftovers.
    assertEquals(40, CodeSplitters.getNumberOfExclusiveFragmentFromExpectedFragmentCount(2, 44));

    // This is a non negative number always
    assertEquals(0, CodeSplitters.getNumberOfExclusiveFragmentFromExpectedFragmentCount(2, 1));

  }
  public void testDontMergeLeftOvers() throws UnableToCompleteException {
    StringBuffer code = new StringBuffer();
    code.append("package test;\n");
    code.append("import com.google.gwt.core.client.GWT;\n");
    code.append("import com.google.gwt.core.client.RunAsyncCallback;\n");
    code.append("public class EntryPoint {\n");
    code.append(functionA);
    code.append(functionB);
    code.append(functionC);
    code.append("  public static void onModuleLoad() {\n");
    // Fragment #1
    code.append(createRunAsync("functionA();"));
    // Fragment #2
    code.append(createRunAsync("functionB();"));
    // Fragment #3
    code.append(createRunAsync("functionC();"));
    code.append("  }\n");
    code.append("}\n");

    // we want don't want them to be merged
    leftOverMergeSize = 10;
    expectedFragmentCount = 5;
    this.compileSnippet(code.toString());

    // init + 3 exlclusive fragments + leftover.
    assertFragmentCount(5);
    assertNotInFragment("functionA", 4);
    assertNotInFragment("functionB", 4);
    assertNotInFragment("functionC", 4);
  }

  public void testNoMergeMoreThanTwo() throws UnableToCompleteException {
    StringBuffer code = new StringBuffer();
    code.append("package test;\n");
    code.append("import com.google.gwt.core.client.GWT;\n");
    code.append("import com.google.gwt.core.client.RunAsyncCallback;\n");
    code.append("public class EntryPoint {\n");
    code.append(functionA);
    code.append(functionB);
    code.append(functionC);
    code.append("  public static void onModuleLoad() {\n");
    // Fragment #1
    code.append(createRunAsync("functionA();"));
    // Fragment #2
    code.append(createRunAsync("functionA();"));
    // Fragment #3
    code.append(createRunAsync("functionA();"));
    code.append("  }\n");
    code.append("}\n");

    expectedFragmentCount = 2;
    compileSnippet(code.toString());

    // There is no common code shared between any pair.

    // init + 3 fragments + leftover.
    assertFragmentCount(5);
  }

  public void testDoubleMerge() throws UnableToCompleteException {
    StringBuffer code = new StringBuffer();
    code.append("package test;\n");
    code.append("import com.google.gwt.core.client.GWT;\n");
    code.append("import com.google.gwt.core.client.RunAsyncCallback;\n");
    code.append("public class EntryPoint {\n");
    code.append(functionA);
    code.append(functionB);
    code.append(functionC);
    code.append("  public static void onModuleLoad() {\n");
    // Fragment #1
    code.append(createRunAsync("functionA();"));
    // Fragment #1
    code.append(createRunAsync("functionA(); functionC();"));
    // Fragment #2
    code.append(createRunAsync("functionB(); functionC();"));
    // Fragment #2
    code.append(createRunAsync("functionB(); functionC();"));
    code.append("  }\n");
    code.append("}\n");

    expectedFragmentCount = 4;
    compileSnippet(code.toString());

    // init + 2 fragments + leftover.
    assertFragmentCount(4);
    assertInFragment("functionA", 1);
    assertInFragment("functionB", 2);
    assertInFragment("functionC", 3);
  }

  private void assertFragmentCount(int num) {
    assertEquals(num, jsProgram.getFragmentCount());
  }

  private void assertInFragment(String functionName, int fragmentNum) {
    JsBlock fragment = jsProgram.getFragmentBlock(fragmentNum);
    assertTrue(findFunctionIn(functionName, fragment));
  }

  private void assertNotInFragment(String functionName, int fragmentNum) {
    JsBlock fragment = jsProgram.getFragmentBlock(fragmentNum);
    assertFalse(findFunctionIn(functionName, fragment));
  }

  /**
   * @return true if the function exists in that fragment.
   */
  private static boolean findFunctionIn(final String functionName, JsBlock fragment) {
    final boolean[] found = {false};
    JsVisitor visitor = new JsVisitor() {
      @Override
      public boolean visit(JsFunction x, JsContext ctx) {
        JsName jsName = x.getName();
        if (jsName != null && jsName.getShortIdent().equals(functionName)) {
          found[0] = true;
        }
        return false;
      }
    };
    visitor.accept(fragment);
    return found[0];
  }

  /**
   * Compiles a Java class <code>test.EntryPoint</code> and use the code splitter on it.
   */
  protected void compileSnippet(final String code) throws UnableToCompleteException {
    // By default expects 4 fragments and don't merge leftovers.
    addMockIntrinsic();
    sourceOracle.addOrReplace(new MockJavaResource("test.EntryPoint") {
      @Override
      public CharSequence getContent() {
        return code;
      }
    });
    addBuiltinClasses(sourceOracle);
    CompilerContext compilerContext = new CompilerContext();
    CompilationState state =
        CompilationStateBuilder.buildFrom(logger, compilerContext,
            sourceOracle.getResources(), getAdditionalTypeProviderDelegate());

    Properties properties = createPropertiesObject(configProps);
    jProgram =
        JavaAstConstructor.construct(logger, state, properties, "test.EntryPoint",
            "com.google.gwt.lang.Exceptions");
    jProgram.addEntryMethod(findMethod(jProgram, "onModuleLoad"));

    CastNormalizer.exec(jProgram, false);
    ArrayNormalizer.exec(jProgram, false);
    TypeTightener.exec(jProgram);
    MethodCallTightener.exec(jProgram);

    Map<StandardSymbolData, JsName> symbolTable =
        new TreeMap<StandardSymbolData, JsName>(new SymbolData.ClassIdentComparator());
    JavaToJavaScriptMap map = GenerateJavaScriptAST.exec(
        jProgram, jsProgram, JsOutputOption.PRETTY, symbolTable, new PropertyOracle[]{
        new StaticPropertyOracle(orderedProps, orderedPropValues, configProps)}).getLeft();
    CodeSplitter.exec(logger, jProgram, jsProgram, map, expectedFragmentCount, leftOverMergeSize,
        NULL_RECORDER);
  }

  private static String createRunAsync(String cast, String body) {
    StringBuffer code = new StringBuffer();
    code.append("GWT.runAsync(" + cast + "new "+ "RunAsyncCallback() {\n");
    code.append("  public void onFailure(Throwable reason) {}\n");
    code.append("  public void onSuccess() {\n");
    code.append("    " + body);
    code.append("  }\n");
    code.append("});\n");
    return code.toString();
  }

  private static String createNamedRunAsyncCallback(String className, String body) {
    StringBuffer code = new StringBuffer();
    code.append("private static class " + className + " extends RunAsyncCallback {\n");
    code.append("  public void onFailure(Throwable reason) {}\n");
    code.append("  public void onSuccess() {\n");
    code.append("    " + body);
    code.append("  }\n");
    code.append("}\n");
    return code.toString();
  }

  private static String createRunAsync(String body) {
    return createRunAsync("", body);
  }

  private static Properties createPropertiesObject(ConfigurationProperty[] propertyArray) {
    Properties properties = new Properties();
    for (ConfigurationProperty configurationPropertyFromArray : propertyArray) {
      ConfigurationProperty configurationProperty =
          properties.createConfiguration(configurationPropertyFromArray.getName(),
          configurationPropertyFromArray.allowsMultipleValues());
      for (String value : configurationPropertyFromArray.getValues()) {
        configurationProperty.addValue(value);
      }
    }
    return properties;
  }

  /**
   * Add some of the compiler intrinsic
   */
  private void addMockIntrinsic() {
    sourceOracle.addOrReplace(new MockJavaResource("java.lang.Comparable") {
      @Override
      public CharSequence getContent() {
        return "package java.lang; public interface Comparable {}";
      }
    });

    sourceOracle.addOrReplace(new MockJavaResource("java.lang.Object") {
      @Override
      public CharSequence getContent() {
        return "package java.lang; public class Object {" +
               "public Object castableTypeMap = null;" +
               "public Object typeMarker = null;" +
               "public Object ___clazz = null;" +
               "public Object getClass() {return null;}" +
               "public String toString() {return null;} }";
      }
    });

    sourceOracle.addOrReplace(new MockJavaResource("com.google.gwt.lang.Array") {
      @Override
      public CharSequence getContent() {
        return "package com.google.gwt.lang; public class Array {" +
               " public static int length = 0;" +
               " public static void setCheck(Array array, int index, Object value) { }" +
               " static void initDim() { }" +
               " static void initDims() { }" +
               " static void initValues() { }" +
               "}";
      }
    });

    sourceOracle.addOrReplace(new MockJavaResource("java.lang.CharSequence") {
      @Override
      public CharSequence getContent() {
        return "package java.lang; public interface CharSequence {}";
      }
    });

    sourceOracle.addOrReplace(new MockJavaResource("com.google.gwt.lang.SeedUtil") {
      @Override
      public CharSequence getContent() {
        return "package com.google.gwt.lang; public class SeedUtil {" +
               "public static Object defineSeed(int id, int seed, Object map){return null;}}";
      }
    });

    sourceOracle.addOrReplace(new MockJavaResource("com.google.gwt.core.client.impl.Impl") {
      @Override
      public CharSequence getContent() {
        return "package com.google.gwt.core.client.impl; public class Impl {"+
               "public static Object registerEntry(){return null;}}";
      }
    });

    sourceOracle.addOrReplace(new MockJavaResource("com.google.gwt.lang.CollapsedPropertyHolder") {
      @Override
      public CharSequence getContent() {
        return "package com.google.gwt.lang; public class CollapsedPropertyHolder {" +
               "public static int permutationId = -1;}";
      }
    });

    sourceOracle.addOrReplace(new MockJavaResource("com.google.gwt.core.client.JavaScriptObject") {
      @Override
      public CharSequence getContent() {
        return "package com.google.gwt.core.client; public class JavaScriptObject {" +
               "public static Object createArray() {return null;}" +
               "public static Object createObject() {return null;}}";
      }
    });

    sourceOracle.addOrReplace(new MockJavaResource("com.google.gwt.core.client.GWT") {
      @Override
      public CharSequence getContent() {
        return "package com.google.gwt.core.client; public class GWT {" +
            "public static void runAsync(RunAsyncCallback cb){}"+
            "public static void runAsync(Class<?> clazz, RunAsyncCallback cb){}}";
      }
    });

    sourceOracle.addOrReplace(new MockJavaResource("com.google.gwt.core.client.RunAsyncCallback") {
      @Override
      public CharSequence getContent() {
        return "package com.google.gwt.core.client; public class RunAsyncCallback {" +
        		"public void onFailure(Throwable reason) {}" +
        		"public void onSuccess() {}}";
      }
    });
  }
}
