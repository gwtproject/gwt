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
package com.google.gwt.dev.jjs.impl;

import com.google.gwt.core.ext.PropertyOracle;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.linker.SymbolData;
import com.google.gwt.core.ext.linker.impl.StandardSymbolData;
import com.google.gwt.dev.cfg.BindingProperty;
import com.google.gwt.dev.cfg.ConditionNone;
import com.google.gwt.dev.cfg.ConfigurationProperty;
import com.google.gwt.dev.cfg.StaticPropertyOracle;
import com.google.gwt.dev.javac.CompilationState;
import com.google.gwt.dev.javac.CompilationStateBuilder;
import com.google.gwt.dev.javac.testing.impl.MockJavaResource;
import com.google.gwt.dev.jjs.JJSOptions;
import com.google.gwt.dev.jjs.JavaAstConstructor;
import com.google.gwt.dev.jjs.JsOutputOption;
import com.google.gwt.dev.jjs.ast.JMethod;
import com.google.gwt.dev.jjs.ast.JProgram;
import com.google.gwt.dev.jjs.impl.CodeSplitter.MultipleDependencyGraphRecorder;
import com.google.gwt.dev.js.ast.JsBlock;
import com.google.gwt.dev.js.ast.JsContext;
import com.google.gwt.dev.js.ast.JsFunction;
import com.google.gwt.dev.js.ast.JsName;
import com.google.gwt.dev.js.ast.JsProgram;
import com.google.gwt.dev.js.ast.JsVisitor;

import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

/**
 * Unit test for {@link CodeSplitter2}.
 */
public class CodeSplitter2Test extends JJSTestBase {

  // These will be the functions that are shared between fragments. This unit test will
  // be based for finding these function in the proper fragments.
  private final String functionA = "public static void functionA() {}";
  private final String functionB = "public static void functionB() {}";
  private final String functionC = "public static void functionC() {}";
  private final String functionD = "public static void functionD() {}";

  // Compilation Configuration Properties.
  private BindingProperty stackMode = new BindingProperty("compiler.stackMode");
  private BindingProperty[] orderedProps = {stackMode};
  private String[] orderedPropValues = {"STRIP" };
  private ConfigurationProperty[] configProps = {};
  
  private JProgram jProgram = null;
  private JsProgram jsProgram = null;

  @Mock
  private JJSOptions optionsMock;

  @Mock
  private MultipleDependencyGraphRecorder recorderMock;


  @Override
  public void setUp() throws Exception {
    super.setUp();
    stackMode.addDefinedValue(new ConditionNone(), "STRIP");
    jsProgram = new JsProgram();
    MockitoAnnotations.initMocks(this);
    when(optionsMock.isCastCheckingDisabled()).thenReturn(false);
    when(optionsMock.getOutput()).thenReturn(JsOutputOption.PRETTY);

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
    compileSnippetWithLeftoverMerge(code.toString(),
        10 * 1024 /* 10k minumum */);

    // init + leftover.
    assertFragmentCount(2);
    assertInFragment("functionA", 1);
    assertInFragment("functionB", 1);
    assertInFragment("functionC", 1);
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
    compileSnippetWithLeftoverMerge(code.toString(), 10);

    // init + leftover.
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
    compileSnippet(code.toString());

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
    addMockIntrinsic();
    sourceOracle.addOrReplace(new MockJavaResource("test.EntryPoint") {
      @Override
      public CharSequence getContent() {
        return code;
      }
    });
    addBuiltinClasses(sourceOracle);
    CompilationState state =
        CompilationStateBuilder.buildFrom(logger, sourceOracle.getResources(),
            getAdditionalTypeProviderDelegate(), sourceLevel);
    jProgram =
        JavaAstConstructor.construct(logger, state, "test.EntryPoint",
            "com.google.gwt.lang.Exceptions");
    jProgram.addEntryMethod(findMethod(jProgram, "onModuleLoad"));
    CompilerContext mockedContext =mockCompilerContext(jProgram, jsProgram);

    CompilerPass.exec(mockedContext,
        CastNormalizer.class,
        ArrayNormalizer.class);
    TypeTightener.exec(jProgram);
    MethodCallTightener.exec(jProgram);
    GenerateJavaScriptAST.exec(mockedContext);
    CodeSplitter2.exec(logger, mockedContext, 4, recorderMock, 0);
  }

  /**
   * Compiles a Java class <code>test.EntryPoint</code> and use the code splitter on it
   * with leftover merge enabled.
   */
  protected void compileSnippetWithLeftoverMerge(final String code,
      int mergeLimit) throws UnableToCompleteException {
    addMockIntrinsic();
    sourceOracle.addOrReplace(new MockJavaResource("test.EntryPoint") {
      @Override
      public CharSequence getContent() {
        return code;
      }
    });
    addBuiltinClasses(sourceOracle);
    CompilationState state =
        CompilationStateBuilder.buildFrom(logger, sourceOracle.getResources(),
            getAdditionalTypeProviderDelegate(), sourceLevel);
    jProgram =
        JavaAstConstructor.construct(logger, state, "test.EntryPoint",
            "com.google.gwt.lang.Exceptions");
    jProgram.addEntryMethod(findMethod(jProgram, "onModuleLoad"));

    CompilerContext mockedContext = mockCompilerContext(jProgram, jsProgram);
    CompilerPass.exec(mockedContext,
        CastNormalizer.class,
        ArrayNormalizer.class);

    GenerateJavaScriptAST.exec(mockedContext);
    CodeSplitter2.exec(logger, mockedContext, 4, recorderMock, mergeLimit);
  }

  private static String createRunAsync(String cast, String body) {
    StringBuffer code = new StringBuffer();
    code.append("GWT.runAsync(" + cast + "new RunAsyncCallback() {\n");
    code.append("  public void onFailure(Throwable reason) {}\n");
    code.append("  public void onSuccess() {\n");
    code.append("    " + body);
    code.append("  }\n");
    code.append("});\n");
    return code.toString();
  }

  private static String createRunAsync(String body) {
    return createRunAsync("", body);
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
        return "package com.google.gwt.core.client; public class GWT {"+
               "public static void runAsync(RunAsyncCallback cb){}}";
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

  private CompilerContext mockCompilerContext(JProgram jProgram, JsProgram jsProgram) {
    CompilerContext mockedContext = mock(CompilerContext.class);
    when(mockedContext.getJProgram()).thenReturn(jProgram);
    when(mockedContext.getJsProgram()).thenReturn(jsProgram);
    when(mockedContext.getOptions()).thenReturn(optionsMock);
    when(mockedContext.getPropertyOracles()).thenReturn(new PropertyOracle[]{
        new StaticPropertyOracle(orderedProps, orderedPropValues, configProps)});
    when(mockedContext.getSymbolTable())
        .thenReturn(new TreeMap<StandardSymbolData, JsName>(new SymbolData.ClassIdentComparator()));
    when(mockedContext.getJavaToJavaScriptMap()).thenCallRealMethod();
    doCallRealMethod().when(mockedContext).setJavaToJavaScriptMap(any(JavaToJavaScriptMap.class));
    return mockedContext;
  }
}
