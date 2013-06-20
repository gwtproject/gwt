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
package com.google.gwt.dev.jjs.impl;

import com.google.gwt.dev.jjs.ast.Context;
import com.google.gwt.dev.jjs.ast.JProgram;
import com.google.gwt.dev.jjs.ast.JRunAsync;
import com.google.gwt.dev.jjs.ast.JVisitor;
import com.google.gwt.thirdparty.guava.common.collect.LinkedHashMultimap;
import com.google.gwt.thirdparty.guava.common.collect.Lists;
import com.google.gwt.thirdparty.guava.common.collect.Multimap;

import java.util.Collection;
import java.util.List;

/**
 * Replaces some JProgram references to JRunAsync nodes after optimization to take advantage of
 * tighter types.
 */
public class RunAsyncsTightener {

  private static class RunAsyncsGathererVisitor extends JVisitor {
    // Is a Multimap not a Map because after optimization there can be duplicate versions
    // of the same splitPoint in different contexts.
    private Multimap<Integer, JRunAsync> optimizedRunAsyncListsBySplitPoint =
        LinkedHashMultimap.create();

    @Override
    public boolean visit(JRunAsync runAsync, Context ctx) {
      optimizedRunAsyncListsBySplitPoint.put(runAsync.getSplitPoint(), runAsync);
      return super.visit(runAsync, ctx);
    }
  }

  public static void exec(JProgram jProgram) {
    RunAsyncsGathererVisitor runAsyncsGathererVisitor = new RunAsyncsGathererVisitor();
    runAsyncsGathererVisitor.accept(jProgram);

    List<JRunAsync> updatedRunAsyncs = Lists.newArrayList();
    for (JRunAsync originalRunAsync : jProgram.getRunAsyncs()) {
      Collection<JRunAsync> optimizedRunAsyncs =
          runAsyncsGathererVisitor.optimizedRunAsyncListsBySplitPoint.get(
              originalRunAsync.getSplitPoint());

      int optimizedRunAsyncCount = optimizedRunAsyncs.size();
      // If after optimization there is still only one copy of a particular runAsync.
      if (optimizedRunAsyncCount == 1) {
        // Then use the optimized version of it since the optimized version may now have a tighter
        // RunAsyncCallback type that will allow CodeSplitting to place code more locally.
        JRunAsync optimizedRunAsync = optimizedRunAsyncs.iterator().next();
        updatedRunAsyncs.add(optimizedRunAsync);
      } else {
        // Expanding an original runAsync into multiple runAsyncs could actually expand the leftOver
        // fragment by causing some code to appear to be reachable from multiple runAsyncs.
        // CodeSplitter2 fragmentMerging could clean this back up automatically but there's no need
        // to add this computation overhead nor end-user tweaking. Just keep the original
        // non-duplicated runAsync.
        updatedRunAsyncs.add(originalRunAsync);
        continue;
      }
    }

    jProgram.setRunAsyncs(updatedRunAsyncs);
  }
}
