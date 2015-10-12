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
package com.google.gwt.dev.jjs.impl.codesplitter;

import com.google.gwt.dev.jjs.ast.JNode;

/**
 * A set of Java Ast notes, used to represent a set of JTypes, JField and JMethods.
 */
public interface NodeSet {
  /**
   * A {@link NodeSet} where nothing is alive.
   */
  NodeSet EMPTY = new NodeSet() {
    @Override
    public boolean contains(JNode node) {
      return false;
    }

    @Override
    public boolean containsUnclassifiedItems() {
      return false;
    }
  };

  /**
   * Returns true if NodeSet contains {@code node}.
   */
  boolean contains(JNode node);

  /**
   * Returns true if the NodeSet contains unclassified items.
   * Unclassified items result from miscellaneous statements in a JsProgram that have no
   * corresponding JNode in the JProgram. This method should almost always return <code>true</code>,
   * but does return <code>false</code> for {@link EMPTY}.
   */
  boolean containsUnclassifiedItems();
}
