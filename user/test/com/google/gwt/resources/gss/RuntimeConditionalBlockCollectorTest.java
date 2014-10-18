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

package com.google.gwt.resources.gss;

import com.google.gwt.thirdparty.common.css.compiler.ast.CssConditionalBlockNode;
import com.google.gwt.thirdparty.common.css.compiler.ast.CssTree;
import com.google.gwt.thirdparty.common.css.compiler.ast.ErrorManager;
import com.google.gwt.thirdparty.common.css.compiler.passes.CreateConditionalNodes;

import java.util.Set;

/**
 * Test class for {@link RuntimeConditionalBlockCollector}.
 */
public class RuntimeConditionalBlockCollectorTest extends BaseGssTest {

  public void testCollectRuntimeConditionalBlock() {
    // given
    CssTree cssTree = parseAndBuildTree(lines(
        "@if (eval('com.foo.BAR')) {",
        "  .foo {",
        "    padding: 5px;",
        "  }",
        "  @if (is('ie9')) {",
        "    .foo {",
        "      padding: 55px;",
        "    }",
        "  }",
        "}",
        "@elseif (eval('com.foo.bar()')) {",
        "  @if (eval('com.foo.FOO')) {",
        "    .foo {",
        "      padding: 15px;",
        "    }",
        "  }",
        "  @else{",
        "    .foo {",
        "      padding: 15px;",
        "    }",
        "  }",
        "}",
        "@if (is('ie6')) {",
        "  .foo {",
        "    padding: 25px;",
        "  }",
        "}",
        "@elseif (eval('com.foo.BAR')) {",
        "  .foo {",
        "    padding: 35px;",
        "  }",
        "}"));

    RuntimeConditionalBlockCollector visitor =
        new RuntimeConditionalBlockCollector(cssTree.getMutatingVisitController());

    // when
    visitor.runPass();

    // then
    Set<CssConditionalBlockNode> runtimeConditionalNodes = visitor.getRuntimeConditionalBlock();

    // We have 4 conditional blocks (@if, @elsif @ else block), 3 are with runtime condition
    assertEquals(3, runtimeConditionalNodes.size());
  }

  @Override
  protected void runPassesOnNewTree(CssTree cssTree, ErrorManager errorManager) {
    new CreateConditionalNodes(cssTree.getMutatingVisitController(), errorManager).runPass();
    new CreateRuntimeConditionalNodes(cssTree.getMutatingVisitController()).runPass();
  }
}
