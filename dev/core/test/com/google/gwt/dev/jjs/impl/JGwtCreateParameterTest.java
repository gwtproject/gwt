package com.google.gwt.dev.jjs.impl;

import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.dev.javac.testing.impl.MockJavaResource;
import com.google.gwt.dev.jjs.ast.JCastOperation;
import com.google.gwt.dev.jjs.ast.JGwtCreateParameter;
import com.google.gwt.dev.jjs.ast.JMethod;
import com.google.gwt.dev.jjs.ast.JMethodBody;
import com.google.gwt.dev.jjs.ast.JMethodCall;
import com.google.gwt.dev.jjs.ast.JParameter;
import com.google.gwt.dev.jjs.ast.JProgram;
import com.google.gwt.dev.jjs.ast.JReturnStatement;

public class JGwtCreateParameterTest extends JJSTestBase {

  private JProgram program;

  public void setUp() {

    sourceOracle.addOrReplace(new MockJavaResource("test.ToCreate") {
      @Override
      public CharSequence getContent() {
        StringBuffer code = new StringBuffer();
        code.append("package test;\n");
        code.append("public class ToCreate {\n");
        code.append("}\n");
        return code;
      }
    });
    sourceOracle.addOrReplace(new MockJavaResource("test.Creator") {
      @Override
      public CharSequence getContent() {
        StringBuffer code = new StringBuffer();
        code.append("package test;\n");
        code.append("import com.google.gwt.core.shared.GwtCreate;\n");
        code.append("public interface Creator {\n");
        code.append("  <T> T create(@GwtCreate Class<T> type);\n");
        code.append("}\n");
        return code;
      }
    });
    sourceOracle.addOrReplace(new MockJavaResource("test.CreatorImpl") {
      @Override
      public CharSequence getContent() {
        StringBuffer code = new StringBuffer();
        code.append("package test;\n");
        code.append("import com.google.gwt.core.shared.GWT;\n");
        code.append("import com.google.gwt.core.shared.GwtCreate;\n");
        code.append("public class CreatorImpl implements Creator {\n");
        code.append("  @Override public <T> T create(@GwtCreate final Class<T> type) {\n");
        code.append("    return GWT.create(type);\n");
        code.append("  }\n");
        code.append("}\n");
        return code;
      }
    });
    sourceOracle.addOrReplace(new MockJavaResource("test.CreatorWrapper") {
      @Override
      public CharSequence getContent() {
        StringBuffer code = new StringBuffer();
        code.append("package test;\n");
        code.append("import com.google.gwt.core.shared.GWT;\n");
        code.append("import com.google.gwt.core.shared.GwtCreate;\n");
        code.append("public class CreatorWrapper {\n");
        code.append("  private final Creator creator = new CreatorImpl();\n");
        code.append("  public <T> T create(@GwtCreate final Class<T> type) {\n");
        code.append("    return creator.create(type);\n");
        code.append("  }\n");
        code.append("}\n");
        return code;
      }
    });
    sourceOracle.addOrReplace(new MockJavaResource("test.ByConstructorCreator") {
      @Override
      public CharSequence getContent() {
        StringBuffer code = new StringBuffer();
        code.append("package test;\n");
        code.append("import com.google.gwt.core.shared.GWT;\n");
        code.append("import com.google.gwt.core.shared.GwtCreate;\n");
        code.append("public class ByConstructorCreator<T> {\n");
        code.append("  public final T instance;\n");
        code.append("  public ByConstructorCreator(@GwtCreate final Class<T> type) {\n");
        code.append("    instance = GWT.create(type);\n");
        code.append("  }\n");
        code.append("}\n");
        return code;
      }
    });
    sourceOracle.addOrReplace(new MockJavaResource("test.MixedCreator") {
      @Override
      public CharSequence getContent() {
        StringBuffer code = new StringBuffer();
        code.append("package test;\n");
        code.append("import com.google.gwt.core.shared.GWT;\n");
        code.append("import com.google.gwt.core.shared.GwtCreate;\n");
        code.append("public class MixedCreator {\n");
        code.append("  public <T, U> void create(@GwtCreate final Class<T> typeT, @GwtCreate final Class<U> typeU) {\n");
        code.append("    GWT.create(typeT);\n");
        code.append("    GWT.create(typeU);\n");
        code.append("  }\n");
        code.append("}\n");
        return code;
      }
    });

    try {
      program =
          compileSnippet("test.ToCreate", "return new CreatorImpl().create(test.ToCreate.class);");
    } catch (UnableToCompleteException e) {
      throw new RuntimeException(e);
    }
  }

  public void testBadNestedMethods() {
    try {
      StringBuffer code = new StringBuffer();
      code.append("class BadCreatorWrapper {\n");
      code.append("  private final Creator creator = new CreatorImpl();\n");
      code.append("  public <T> T create(final Class<T> type) {\n");
      code.append("    return creator.create(type);\n");
      code.append("  }\n");
      code.append("}\n");
      code.append("return new BadCreatorWrapper().create(ToCreate.class);");

      compileSnippet("ToCreate", code.toString());
      fail();
    } catch (UnableToCompleteException e) {
      // OK
    }
  }

  public void testBadOverriding() {
    try {
      StringBuffer code = new StringBuffer();

      code.append("class BadCreatorImpl implements Creator {\n");
      code.append("  @Override public <T> T create(final Class<T> type) {\n");
      code.append("    return com.google.gwt.core.shared.GWT.create(type);\n");
      code.append("  }\n");
      code.append("};\n");
      code.append("return new BadCreatorImpl().create(ToCreate.class);");

      compileSnippet("ToCreate", code.toString());
      fail();
    } catch (UnableToCompleteException e) {
      // OK
    }
  }

  public void testMethodCall() {
    findMainMethod(program);
    JMethod mainMethod = findMainMethod(program);
    JMethodBody body = (JMethodBody) mainMethod.getBody();
    JReturnStatement returnStmt = (JReturnStatement) body.getStatements().get(0);
    JCastOperation casting = (JCastOperation) returnStmt.getExpr();
    JMethodCall methodCall = (JMethodCall) casting.getExpr();

    assertEquals(methodCall.getArgs().size(), 2);
    assertEquals(methodCall.getArgs().get(1).getType().getName(), "test.ToCreate$GwtCreateFactory");
  }

  public void testNonFinalParameter() {
    try {
      StringBuffer code = new StringBuffer();

      code.append("class NonFinalCreator {\n");
      code.append("  public <T> void create(@com.google.gwt.core.shared.GwtCreate Class<T> type) {\n");
      code.append("    com.google.gwt.core.shared.GWT.create(type);\n");
      code.append("  }\n");
      code.append("};\n");
      code.append("return new NonFinalCreator().create(ToCreate.class);");

      compileSnippet("ToCreate", code.toString());
      fail();
    } catch (UnableToCompleteException e) {
      // OK
    }
  }

  public void testParameters() {
    JMethod createMethod = findQualifiedMethod(program, "test.Creator.create");
    JParameter param = createMethod.getParams().get(0);

    assertEquals(createMethod.getParams().size(), 2);
    assertTrue(param instanceof JGwtCreateParameter);

    JParameter factoryParam = ((JGwtCreateParameter) param).getFactoryParam();
    assertEquals(factoryParam.getType().getName(), "com.google.gwt.lang.GwtCreateFactory");
  }
}
