/*
 * Copyright 2009 Google Inc.
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
package com.google.gwt.resources.css.ast;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * An AST node that allows the developer to indicate that certain class
 * selectors appearing in the stylesheet should be considered external and not
 * subject to obfuscation requirements.
 */
public class CssExternalSelectors extends CssNode {

  private final Set<String> classes = new LinkedHashSet<String>();

  public Set<String> getClasses() {
    return classes;
  }

  @Override
  public boolean isStatic() {
    return true;
  }

  @Override
  public void traverse(CssVisitor visitor, Context context) {
    visitor.visit(this, context);
    visitor.endVisit(this, context);
  }
}
