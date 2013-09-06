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

import com.google.gwt.dev.jjs.ast.JRunAsync;
import com.google.gwt.dev.js.ast.JsStatement;
import com.google.gwt.dev.util.collect.HashSet;

import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * Describes a fragment and its contents.
 */
class Fragment {

  public List<JsStatement> getStatementsInFragment() {
    return statementsInFragment;
  }

  public void setStatementsInFragment(List<JsStatement> statementsInFragment) {
    this.statementsInFragment = statementsInFragment;
  }

  public void addStatementsToFragment(List<JsStatement> statements) {
    this.statementsInFragment.addAll(statements);
  }

  public Type getType() {
    return type;
  }

  enum Type {INITIAL, EXCLUSIVE, NOT_EXCLUSIVE};

  public Fragment(Type type, int fragmentNumber) {
    this.type = type;
    this.fragmentNumber = fragmentNumber;
  }

  public int getFragmentNumber() {
    return fragmentNumber;
  }

  public void setFragmentNumber(int fragmentNumber) {
    this.fragmentNumber = fragmentNumber;
  }

  public boolean isExclusive() {
    return type == Type.EXCLUSIVE;
  }

  public boolean isInitial() {
    return type == Type.INITIAL;
  }

  /**
   * Splitpoints contained in this fragment.
   */
  public Set<JRunAsync> getSplitPoints() {
    return splitPoints;
  }

  public void addSplitPoint(JRunAsync runAsync) {
    assert !splitPoints.contains(runAsync);
    splitPoints.add(runAsync);
  }

  public void addSplitPoints(Collection<JRunAsync> runAsyncs) {
    assert !splitPoints.contains(runAsyncs);
    splitPoints.addAll(runAsyncs);
  }

  private Type type;
  private Set<JRunAsync> splitPoints = new HashSet<JRunAsync>();
  private int fragmentNumber;
  private List<JsStatement> statementsInFragment;

}
