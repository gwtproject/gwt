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

import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.dev.javac.testing.impl.JavaResourceBase;
import com.google.gwt.dev.javac.testing.impl.MockJavaResource;

import java.util.Collection;
import java.util.List;

/**
 * Test class for {@link JdtCompiler}.
 */
public class JdtCompilerTest extends JdtCompilerTestBase {

  public void testCompile() throws Exception {
    assertResourcesCompileSuccessfully(JavaResourceBase.FOO, JavaResourceBase.BAR);
  }

  public void testCompileError() throws Exception {
    List<CompilationUnit> units = compile(JavaResourceBase.BAR);
    assertOnlyLastUnitHasErrors(units, "Foo cannot be resolved to a type");
  }

  public void testCompileIncremental() throws Exception {
    List<CompilationUnitBuilder> builders = buildersFor();
    Collection<CompilationUnit> units = compile(builders);
    assertUnitsCompiled(units);
    addAll(builders, JavaResourceBase.FOO, JavaResourceBase.BAR);
    units = compile(builders);
    assertUnitsCompiled(units);
  }

  public void testCompileEmptyFile() throws Exception {
    assertUnitCompilesWithNoErrors("com.example.Empty",
        "package com.example;");
  }

  public void testCompileEmptyFileWithImports() throws Exception {
    JdtCompiler.setRemoveUnusedImports(false);
    assertUnitCompilesWithNoErrors("com.example.EmptyWithImports",
        "package com.example;",
        "import java.util.Collections;");
    JdtCompiler.setRemoveUnusedImports(true);
    assertUnitCompilesWithNoErrors("com.example.EmptyWithImports",
        "package com.example;",
        "import java.util.Collections;");
  }

  public void testCompileEmptyFileWithUnresolvableImports() throws Exception {
    JdtCompiler.setRemoveUnusedImports(false);
    assertUnitHasErrors("com.example.EmptyWithImports",
        "package com.example;",
        "import java.util.BlahBlahBlah;");
    JdtCompiler.setRemoveUnusedImports(true);
    assertUnitCompilesWithNoErrors("com.example.EmptyWithImports",
        "package com.example;",
        "import java.util.BlahBlahBlah;");
  }

  private void assertUnitCompilesWithNoErrors(String sourceName, String... sourceLines)
      throws UnableToCompleteException {
    CompilationUnit unit = compileUnit(sourceName, sourceLines);
    assertNotNull(unit);
    assertFalse(unit.isError());
  }

  private void assertUnitHasErrors(String sourceName, String... sourceLines)
      throws UnableToCompleteException {
    CompilationUnit unit = compileUnit(sourceName, sourceLines);
    assertNotNull(unit);
    assertTrue(unit.isError());
  }

  private CompilationUnit compileUnit(String sourceName, String[] sourceLines)
      throws UnableToCompleteException {
    MockJavaResource emptySourceFile =
        JavaResourceBase.createMockJavaResource(sourceName, sourceLines);
    List<CompilationUnit> units = compile(emptySourceFile);
    return findUnitBySourceName(sourceName, units);
  }

  private CompilationUnit findUnitBySourceName(String sourceName, List<CompilationUnit> units) {
    for (CompilationUnit unit : units) {
      if (unit.getTypeName().equals(sourceName)) {
        return unit;
      }
    }
    return null;
  }
}
