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

import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.resources.rg.GssResourceGenerator;
import com.google.gwt.thirdparty.common.css.compiler.ast.CssClassSelectorNode;
import com.google.gwt.thirdparty.common.css.compiler.ast.CssSelectorNode;
import com.google.gwt.thirdparty.common.css.compiler.ast.CssTree;
import com.google.gwt.thirdparty.common.css.compiler.ast.DefaultTreeVisitor;
import com.google.gwt.thirdparty.guava.common.base.Preconditions;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * Collect all CSS class names in a stylesheet.
 */
public class ClassNamesCollector2 extends DefaultTreeVisitor {
  private Set<String>  classNames;
  private CssSelectorNode selector;

  /**
   * Extract all CSS class names in the provided stylesheet, modulo those
   * imported from another context.
   */
  public Set<String> getClassNames(CssTree tree, CssSelectorNode selector) {
    this.selector = selector;
    Preconditions.checkNotNull(tree, "tree cannot be null");

    System.out.println("sel: " + selector);

    System.out.println("refiner: " + selector.getRefiners());
    System.out.println("combitanor: " + selector.getCombinator());

    classNames = new LinkedHashSet<>();
    tree.getVisitController().startVisit(this);

    return classNames;
  }

  boolean collect = false;

  @Override
  public boolean enterSelector(CssSelectorNode node) {
    if (selector == node) {
      collect = true;

    }
    return true;
  }

  @Override
  public void leaveSelector(CssSelectorNode node) {
    collect = false;
  }


  @Override
  public boolean enterClassSelector(CssClassSelectorNode classSelector) {
    if (!collect)
    {
      return true;
    }
    String className = classSelector.getRefinerName();


      classNames.add(className);


    return true;
  }


}
