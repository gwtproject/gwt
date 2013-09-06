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
import com.google.gwt.dev.jjs.ast.JNode;
import com.google.gwt.dev.jjs.ast.JReferenceType;
import com.google.gwt.dev.jjs.ast.JRunAsync;
import com.google.gwt.dev.jjs.impl.ControlFlowAnalyzer;
import com.google.gwt.thirdparty.guava.common.base.Function;
import com.google.gwt.thirdparty.guava.common.collect.Collections2;
import com.google.gwt.thirdparty.guava.common.collect.Maps;
import com.google.gwt.thirdparty.guava.common.collect.Lists;

import java.util.BitSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

import javax.annotation.Nullable;

/**
 * Maps an atom to a set of split point that can be live (NOT necessary exclusively)
 * when that split point is activated. The split points are represented by a bit
 * set where S[i] is set if the atom needs to be live when split point i is live.
 */
class LiveAtomsByRunAsyncSets {
  private final Map<BitSet, Integer> diferentialSizeBySubset = Maps.newHashMap();
  private final Map<JRunAsync, Integer> idForRunAsync = Maps.newHashMap();
  private final Map<Integer, JRunAsync> runAsyncForId = Maps.newHashMap();
  private int nextIdForRunAsync = 0;

  private List<JRunAsync> getRunAsyncsFromSet(BitSet set) {
    int runAsyncId = -1;
    List<JRunAsync> results = Lists.newArrayList();
    while ((runAsyncId = set.nextSetBit(runAsyncId+1)) != -1 ) {
      results.add(runAsyncForId(runAsyncId));
    }
    return results;
  }

  private <T> void accumulateSizeInformation(Map<T, BitSet> livenessMap) {
    // Count the number of atoms shared only by exactly 2.
    // TODO(rluble): implement a better similarity measure taking into account all subsets that
    // appear.
    for (Map.Entry<T, BitSet> fieldLiveness : livenessMap.entrySet()) {
      BitSet liveSplitPoints = fieldLiveness.getValue();

      Integer count = diferentialSizeBySubset.get(liveSplitPoints);
      count = count == null ? 0 : count;
      count += estimateSize(fieldLiveness.getKey());
      diferentialSizeBySubset.put(liveSplitPoints, count);
    }
  }

  public List<List<JRunAsync>> pickBestNPairs(int n) {
    // Build a priority queue for sets of size 2.
    PriorityQueue<Grouping> pq = new PriorityQueue<Grouping>();
    for (BitSet bitSet : diferentialSizeBySubset.keySet()) {
      int cardinality = bitSet.cardinality();
      if (cardinality != 2) {
        continue;
      }
      pq.add(new Grouping(bitSet, diferentialSizeBySubset.get(bitSet)));
    }

    List<List<JRunAsync>> result = Lists.newArrayList();
    BitSet merged = new BitSet();
    // Remove non overlapping pairs from queue.
    while (n-- > 0 && !pq.isEmpty()) {
      BitSet currentSet = pq.poll().runAsyncSet;
      if (currentSet.intersects(merged)) {
        // Already merged
        continue;
      }
      System.out.println("merging: " + currentSet);

      result.add(getRunAsyncsFromSet(currentSet));
      merged.or(currentSet);
    }
    // Get the ones that are not merged
    BitSet notMerged = merged;
    notMerged.flip(0, getNumberOfRunAsyncs());
    result.addAll(Collections2.transform(getRunAsyncsFromSet(notMerged),
        new Function<JRunAsync, List<JRunAsync>>() {
          @Override
          public List<JRunAsync> apply(@Nullable JRunAsync runAsync) {
            System.out.println("singleton: " + getIdForRunAsync(runAsync));
            return Lists.newArrayList(runAsync);
          }
        }));
    return result;
  }

  private boolean isFragmentSizeBelowMinimum(List<JRunAsync> fragment, int minimum) {
    int size = 0;
    BitSet fragmentAsBitSet = new BitSet();
    for (JRunAsync runAsync : fragment) {
      fragmentAsBitSet.set(getIdForRunAsync(runAsync));
    }

    // Very inefficient, n^2 behaviour.
    for (BitSet asyncSet : diferentialSizeBySubset.keySet()) {
      BitSet includes = (BitSet) asyncSet.clone();
      includes.and(fragmentAsBitSet);
      if (includes.equals(fragmentAsBitSet)) {
        size += diferentialSizeBySubset.get(asyncSet);
        if (size > minimum) {
          return false;
        }
      }
    }
    return true;
  }

  public List<List<JRunAsync>> mergeSmallFragmentsTogether(List<List<JRunAsync>> fragments,
      int minFragmentSize) {

    List<JRunAsync> smallFragment = Lists.newArrayList();

    Iterator<List<JRunAsync>> fragmentIterator = fragments.iterator();
    while (fragmentIterator.hasNext()) {
      List<JRunAsync> fragment = fragmentIterator.next();
      if (isFragmentSizeBelowMinimum(fragment, minFragmentSize)) {
        smallFragment.addAll(fragment);
        fragmentIterator.remove();
      }
    }
    System.out.println("Merged small " + smallFragment);

    if (!smallFragment.isEmpty() && !isFragmentSizeBelowMinimum(smallFragment, minFragmentSize)) {
      fragments.add(smallFragment);
    }
    return fragments;
  }


  public ControlFlowAnalyzer recordLiveSet(ControlFlowAnalyzer cfa, JRunAsync runAsync) {
    addRunAsync(runAsync);
    for (JNode node : cfa.getLiveFieldsAndMethods()) {
      if (node instanceof JField) {
        setLive((JField) node, runAsync);
      }
      if (node instanceof JMethod) {
        setLive((JMethod) node, runAsync);
      }
    }

    for (JField node : cfa.getFieldsWritten()) {
      setLive(node, runAsync);
    }

    for (String s : cfa.getLiveStrings()) {
      setLive(s, runAsync);
    }

    for (JReferenceType t : cfa.getInstantiatedTypes()) {
      if (t instanceof JDeclaredType) {
        setLive((JDeclaredType) t, runAsync);
      }
    }
    return cfa;
  }

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

  public JRunAsync runAsyncForId(int id) {
    return runAsyncForId.get(id);
  }

  public boolean isFragmentSizeGreaterThanLimit(BitSet runAsyncSet, int limit) {
    return false;
  }

  final private static int AVERAGE_NAME_SIZE = 2;
  final private static int FUNCTION_DEFINITION_CONSTANT_SIZE = "function".length() + "()".length();
  final private static int AVERAGE_METHOD_SIZE = 40;
  // Provides a very rough method size estimation.
  // TODO(rluble): Either do some counting visitor on the Java method body AST or on the JsFunc
  // assotiated with it (JsFunc is not available here yet.

  private static int estimateSize(Object obj) {
    if (obj instanceof JField) {
      return estimateSize((JField) obj);
    } else if (obj instanceof JMethod) {
      return estimateSize((JMethod) obj);
    } else if (obj instanceof String) {
      return estimateSize((String) obj);
    } else if (obj instanceof JDeclaredType) {
      return estimateSize((JDeclaredType) obj);
    }
    throw new UnsupportedOperationException("estimateSize unsopported for type " +
        obj.getClass().getName());
  }


  private static int estimateSize(JMethod method) {
    int methodSize = FUNCTION_DEFINITION_CONSTANT_SIZE +
        (AVERAGE_NAME_SIZE + /*,*/ 1  + AVERAGE_METHOD_SIZE) * method.getParams().size();
    return methodSize;
  }

  private static int estimateSize(JField field) {
    return AVERAGE_NAME_SIZE;
  }

  private static int estimateSize(JDeclaredType type) {
    return AVERAGE_NAME_SIZE + 50 /*define seed */ + (3 /* _. = */ +
        AVERAGE_NAME_SIZE) * type.getMethods().size() /* TODO(rluble): only virtual methods
        should be counted here */;
  }

  private static int estimateSize(String string) {
    return string.length();
  }

  public void countAtoms() {
    accumulateSizeInformation(fields);
    accumulateSizeInformation(methods);
    accumulateSizeInformation(strings);
    accumulateSizeInformation(types);
//    printToNByCardinality(5);
  }

  void computeLiveSetForEachRunAsync(ControlFlowAnalyzer initiallyLive,
      Iterable<JRunAsync> runAsyncs) {
    // Control Flow Analysis from a split point.
    for (JRunAsync runAsync : runAsyncs) {
      ControlFlowAnalyzer cfa = new ControlFlowAnalyzer(initiallyLive);
      cfa.traverseFromRunAsync(runAsync);
      recordLiveSet(cfa, runAsync);
    }
  }

  public int getIdForRunAsync(JRunAsync runAsync) {
    return idForRunAsync.get(runAsync);
  }

  private static class Grouping implements Comparable<Grouping> {
    final BitSet runAsyncSet;
    int size;

    public Grouping(BitSet runAsyncSet, int size) {
      this.runAsyncSet = runAsyncSet;
      this.size = size;
    }

    public void addSize(int sizeToAdd) {
      size += sizeToAdd;
    }

    @Override
    public int compareTo(Grouping o) {
      return Integer.compare(o.size, size);
    }
  }

  private void printToNByCardinality(int n) {
    Map<Integer, PriorityQueue<Grouping>> sortedSubSets = Maps.newHashMap();
    for (BitSet bitSet : diferentialSizeBySubset.keySet()) {
      int cardinality = bitSet.cardinality();
      PriorityQueue<Grouping> pq = sortedSubSets.get(cardinality);
      if (pq == null) {
        pq = new PriorityQueue<Grouping>();
        sortedSubSets.put(cardinality, pq);
      }
      pq.add(new Grouping(bitSet, diferentialSizeBySubset.get(bitSet)));
    }
    for (int i = getNumberOfRunAsyncs() + 1; i > 0; i--) {
      PriorityQueue<Grouping> pq = sortedSubSets.get(i);
      if (pq == null) {
        System.out.println("No subsets of cardinality " + i);
        continue;
      }
      System.out.println(" === Cardinality " + i + " ==== ");
      int first = n;
      while (first-- > 0 && !pq.isEmpty()) {
        Grouping g = pq.poll();
        System.out.println("Group " + g.runAsyncSet + " size " + g.size + " weighted size " +
            g.size / i);
      }
    }
  }
}
