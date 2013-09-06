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

import com.google.gwt.dev.jjs.ast.JDeclaredType;
import com.google.gwt.dev.jjs.ast.JField;
import com.google.gwt.dev.jjs.ast.JMethod;
import com.google.gwt.dev.jjs.ast.JRunAsync;
import com.google.gwt.thirdparty.guava.common.collect.Maps;

import java.util.BitSet;
import java.util.Map;

/**
 * Maps an atom to a set of split point that can be live (NOT necessary exclusively)
 * when that split point is activated. The split points are represented by a bit
 * set where S[i] is set if the atom needs to be live when split point i is live.
 */
class LiveSplitPointMap {
  private final Map<JRunAsync, Integer> idForRunAsync = Maps.newHashMap();
  private final Map<Integer, JRunAsync> runAsyncForId = Maps.newHashMap();
  private int nextIdForRunAsync = 0;

  private <T> boolean setLive(Map<T, BitSet> map, T atom, JRunAsync runAsync) {
    int splitPoint = idForRunAsync.get(runAsync);
    BitSet liveSet = map.get(atom);
    if (liveSet == null) {
      liveSet = new BitSet();
      liveSet.set(splitPoint);
      map.put(atom, liveSet);
      return true;
    } else {
      if (liveSet.get(splitPoint)) {
        return false;
      } else {
        liveSet.set(splitPoint);
        return true;
      }
    }
  }
  public Map<JField, BitSet> fields = Maps.newHashMap();
  public Map<JMethod, BitSet> methods = Maps.newHashMap();
  public Map<String, BitSet> strings = Maps.newHashMap();
  public Map<JDeclaredType, BitSet> types = Maps.newHashMap();

  boolean setLive(JDeclaredType type, JRunAsync runAsync) {
    return setLive(types, type, runAsync);
  }

  boolean setLive(JField field, JRunAsync runAsync) {
    return setLive(fields, field, runAsync);
  }

  boolean setLive(JMethod method, JRunAsync runAsync) {
    return setLive(methods, method, runAsync);
  }

  boolean setLive(String string, JRunAsync runAsync) {
    return setLive(strings, string, runAsync);
  }

  public void addRunAsync(JRunAsync runAsync) {
    int runAsyncId = nextIdForRunAsync++;
    idForRunAsync.put(runAsync, runAsyncId);
    runAsyncForId.put(runAsyncId, runAsync);
  }

  public int getNumberOfRunAsyncs() {
    return idForRunAsync.size();
  }

  public int idForRunAsync(JRunAsync runAsync) {
    return idForRunAsync.get(runAsync);
  }

  public JRunAsync runAsyncForId(int id) {
    return runAsyncForId.get(id);
  }
}
