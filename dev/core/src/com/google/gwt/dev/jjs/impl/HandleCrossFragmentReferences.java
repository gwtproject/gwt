/*
 * Copyright 2010 Google Inc.
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

import com.google.gwt.dev.cfg.PermutationProperties;
import com.google.gwt.dev.js.ast.JsContext;
import com.google.gwt.dev.js.ast.JsFunction;
import com.google.gwt.dev.js.ast.JsName;
import com.google.gwt.dev.js.ast.JsNameRef;
import com.google.gwt.dev.js.ast.JsProgram;
import com.google.gwt.dev.js.ast.JsVars;
import com.google.gwt.dev.js.ast.JsVars.JsVar;
import com.google.gwt.dev.js.ast.JsVisitor;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * Rewrite JavaScript to better handle references from one code fragment to
 * another. For any function defined off the initial download and accessed from
 * a different island than the one it's defined on, predefine a variable in the
 * initial download to hold its definition.
 */
public class HandleCrossFragmentReferences {

  /**
   * Find out which islands define and use each named function or variable. This
   * visitor is not smart about which definitions and uses matter. It blindly
   * records all of them.
   */
  private class FindNameReferences extends JsVisitor {
    Map<JsName, Set<Integer>> islandsDefining = new LinkedHashMap<JsName, Set<Integer>>();
    Map<JsName, Set<Integer>> islandsUsing = new LinkedHashMap<JsName, Set<Integer>>();
    private int currentIsland;

    @Override
    public void endVisit(JsFunction x, JsContext ctx) {
      JsName name = x.getName();
      if (name != null) {
        definitionSeen(name);
      }
    }

    @Override
    public void endVisit(JsNameRef x, JsContext ctx) {
      if (x.getQualifier() == null) {
        JsName name = x.getName();
        if (name != null) {
          referenceSeen(name);
        }
      }
    }

    @Override
    public void endVisit(JsVars x, JsContext ctx) {
      for (JsVar var : x) {
        JsName name = var.getName();
        if (name != null) {
          definitionSeen(name);
        }
      }
    }

    @Override
    public boolean visit(JsProgram x, JsContext ctx) {
      for (int i = 0; i < x.getFragmentCount(); i++) {
        currentIsland = i;
        accept(x.getFragmentBlock(i));
      }

      return false;
    }

    private void definitionSeen(JsName name) {
      /*
       * Support multiple definitions, because local variables can reuse the
       * same name.
       */
      Set<Integer> defs = islandsDefining.get(name);
      if (defs == null) {
        defs = new LinkedHashSet<Integer>();
        islandsDefining.put(name, defs);
      }
      defs.add(currentIsland);
    }

    private void referenceSeen(JsName name) {
      Set<Integer> refs = islandsUsing.get(name);
      if (refs == null) {
        refs = new HashSet<Integer>();
        islandsUsing.put(name, refs);
      }
      refs.add(currentIsland);
    }
  }

  public static JsName exec(JsProgram jsProgram, PermutationProperties properties) {
    return new HandleCrossFragmentReferences(jsProgram, properties).execImpl();
  }

  private static boolean containsOtherThan(Set<Integer> set, int allowed) {
    for (int elem : set) {
      if (elem != allowed) {
        return true;
      }
    }
    return false;
  }

  private JsName jslink;
  private final JsProgram jsProgram;
  private final Set<JsName> namesToPredefine = new LinkedHashSet<JsName>();
  private final boolean shouldPredeclareReferences;

  private HandleCrossFragmentReferences(JsProgram jsProgram, PermutationProperties properties) {
    this.jsProgram = jsProgram;
    // TODO: should it be a compiler error if soft permutations differ?
    this.shouldPredeclareReferences = properties.isTrueInAnyPermutation(
        "compiler.predeclare.cross.fragment.references");
  }

  private void chooseNamesToPredefine(Map<JsName, Set<Integer>> map,
      Map<JsName, Set<Integer>> islandsUsing) {
    for (Entry<JsName, Set<Integer>> entry : map.entrySet()) {
      JsName name = entry.getKey();
      Set<Integer> defIslands = entry.getValue();
      if (defIslands.size() != 1) {
        // Only rewrite global variables, which should have exactly one
        // definition
        continue;
      }
      int defIsland = defIslands.iterator().next();
      if (defIsland == 0) {
        // Variables defined on the base island can be accessed directly from
        // other islands
        continue;
      }
      Set<Integer> useIslands = islandsUsing.get(name);
      if (useIslands == null) {
        // The variable is never used. Leave it alone.
        continue;
      }

      if (containsOtherThan(islandsUsing.get(name), defIsland)) {
        namesToPredefine.add(name);
        name.setNamespace(jslink);
      }
    }
  }

  /**
   * Define the jslink object that will be used to fix up cross-island
   * references.
   */
  private void defineJsLink() {
    jslink = jsProgram.getScope().declareName("jslink");
  }

  private JsName execImpl() {
    if (jsProgram.getFragmentCount() == 1) {
      return null;
    }
    if (!shouldPredeclareReferences) {
      return null;
    }
    defineJsLink();
    FindNameReferences findNameReferences = new FindNameReferences();
    findNameReferences.accept(jsProgram);
    chooseNamesToPredefine(findNameReferences.islandsDefining, findNameReferences.islandsUsing);
    return jslink;
  }
}
