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
 *
 * <p>A fragment contains one or more runAsyncs (split points). Each runAsync in the program is
 * assigned to exactly one fragment.</p>
 */
class Fragment {

  /**
   * Types of fragments:
   * <ul>
   *   <li>- INITIAL fragments are the ones that from part of the initial download.</li>
   *   <li>- EXCLUSIVE fragments are the ones that contain atoms that are only live when runAsyncs
   *         contained in the fragment are activated</li>
   *   <li>- NOT_EXCLUSIVE fragments (only one at this stage) contains all the atoms that are not
   *         part of INITIAL or EXCLUSIVE fragments</li>
   *   <li>- DELETED fragments are fragments whose contents have been merged to a different
   *         fragment</li>
   * </ul>
   */
  enum Type { INITIAL, EXCLUSIVE, NOT_EXCLUSIVE, DELETED};

  public Fragment(Type type) {
    this.type = type;
  }

  /**
   * Assign a runAsync to this fragment.
   */
  public void addRunAsync(JRunAsync runAsync) {
    assert !runAsyncs.contains(runAsync);
    runAsyncs.add(runAsync);
  }

  public void addRunAsyncs(Collection<JRunAsync> runAsyncs) {
    assert !this.runAsyncs.contains(runAsyncs);
    this.runAsyncs.addAll(runAsyncs);
  }

  public void addStatementsToFragment(List<JsStatement> statements) {
    this.statementsInFragment.addAll(statements);
  }

  public int getFragmentNumber() {
    assert fragmentNumber != -1;
    return fragmentNumber;
  }

  /**
   * Splitpoints contained in this fragment.
   */
  public Set<JRunAsync> getRunAsyncs() {
    return runAsyncs;
  }

  public List<JsStatement> getStatementsInFragment() {
    return statementsInFragment;
  }

  public Type getType() {
    return type;
  }

  public boolean isDeleted() {
    return type == Type.DELETED;
  }

  public boolean isExclusive() {
    return type == Type.EXCLUSIVE;
  }

  public boolean isInitial() {
    return type == Type.INITIAL;
  }

  public void setDeleted() {
    this.type = Type.DELETED;
  }
  public void setFragmentNumber(int fragmentNumber) {
    this.fragmentNumber = fragmentNumber;
  }

  public void setStatementsInFragment(List<JsStatement> statementsInFragment) {
    this.statementsInFragment = statementsInFragment;
  }

  private Type type;
  private Set<JRunAsync> runAsyncs = new HashSet<JRunAsync>();
  private List<JsStatement> statementsInFragment;
  private int fragmentNumber = -1;
}
