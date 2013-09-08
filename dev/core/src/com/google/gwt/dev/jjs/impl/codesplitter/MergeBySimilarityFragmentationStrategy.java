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

import com.google.gwt.dev.jjs.InternalCompilerException;
import com.google.gwt.dev.jjs.ast.JDeclaredType;
import com.google.gwt.dev.jjs.ast.JField;
import com.google.gwt.dev.jjs.ast.JMethod;
import com.google.gwt.dev.jjs.ast.JNode;
import com.google.gwt.dev.jjs.ast.JReferenceType;
import com.google.gwt.dev.jjs.ast.JRunAsync;
import com.google.gwt.dev.jjs.impl.ControlFlowAnalyzer;
import com.google.gwt.dev.js.JsToStringGenerationVisitor;
import com.google.gwt.dev.js.ast.JsStatement;
import com.google.gwt.dev.util.TextOutput;
import com.google.gwt.dev.util.collect.HashMap;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * This strategy implements the fragment merge by similarity strategy.
 *
 * <p>The fragment merge by similarity computes the set of live atoms starting for the splitpoints
 * in the fragment. This is a rough under-approximation as it does not consider atoms that would be
 * reachable by the splitpoints in this fragment if a different fragment is loaded first.
 *
 * <p>A similarity graph is constructed (represented by a similarity matrix) from the results
 * of the control flow analysis, and fragments linked by the highest weights are merged.
 *
 * <p>Finally, fragments that might result very small are also merged into some related fragment.
 *
 * <p>The control flow analysis that is computed by this strategy is not used to determine
 * exclusivity.
*/
class MergeBySimilarityFragmentationStrategy implements FragmentationStrategy {
  private int targetNumberOfFragments;

  public MergeBySimilarityFragmentationStrategy(int targetNumberOfFragments) {
    this.targetNumberOfFragments = targetNumberOfFragments;
  }

  @Override
  public List<Fragment> partitionIntoFragments(ControlFlowAnalyzer initiallyLive,
      Collection<JRunAsync> nonInitialRunAsyncs) {
    List<List<JRunAsync>> asyncGroups = groupBySimilarity(initiallyLive, nonInitialRunAsyncs);
    /**
     * Create the exclusive fragments according to the merge by similarity.
     */
    List<Fragment> fragments = new ArrayList<Fragment>();
    for (List<JRunAsync> group : asyncGroups) {
      Fragment fragment = new Fragment(Fragment.Type.EXCLUSIVE);
      fragment.addSplitPoints(group);
      fragments.add(fragment);
    }
    return fragments;
  }

  private List<List<JRunAsync>> groupBySimilarity(ControlFlowAnalyzer initiallyLive,
      Collection<JRunAsync> runAsyncs) {
    // Compute the under-approximate liveset map.
    LiveSplitPointMap liveSetForRunAsync = new LiveSplitPointMap();
    computeLiveSetForEachRunAsync(initiallyLive, liveSetForRunAsync, runAsyncs);
    List<List<JRunAsync>> groups = partitionFragmentUsingEdgeGreedy(liveSetForRunAsync);
    return groups;
  }


  /**
   * Partition aggressively base on the edge information. If two split points share
   * lots of
   */
  private List<List<JRunAsync>> partitionFragmentUsingEdgeGreedy(
      LiveSplitPointMap liveSplitPointMap) {
    // This matrix serves as an adjacency matrix of split points.
    // An edge from a to b with weight of x implies split point a and b shares x atoms exclusively.
    int runAsyncCount = liveSplitPointMap.getNumberOfRunAsyncs();
    int[][] matrix = new int[runAsyncCount][runAsyncCount];
    countSharedAtomsOfType(liveSplitPointMap.fields, matrix);
    countSharedAtomsOfType(liveSplitPointMap.methods, matrix);
    countSharedAtomsOfType(liveSplitPointMap.strings, matrix);
    countSharedAtomsOfType(liveSplitPointMap.types, matrix);

    Set<Integer> mergedRunAsyncs = new HashSet<Integer>();
    List<List<JRunAsync>> asyncGroups = new ArrayList<List<JRunAsync>>();
    for (int c = 0; c < targetNumberOfFragments; c++) {
      int bestI = 0, bestJ = 0, max = 0;
      for (int i = 1; i < runAsyncCount; i++) {
        if (mergedRunAsyncs.contains(i)) {
          continue;
        }
        for (int j = 1; j < runAsyncCount; j++) {
          if (matrix[i][j] <= max || mergedRunAsyncs.contains(j)) {
            continue;
          }
          bestI = i;
          bestJ = j;
          max = matrix[i][j];
        }
      }

      if (max == 0) {
        break;
      }
      List<JRunAsync> group = new ArrayList<JRunAsync>(2);
      group.add(liveSplitPointMap.runAsyncForId(bestI));
      group.add(liveSplitPointMap.runAsyncForId(bestJ));
      asyncGroups.add(group);
      mergedRunAsyncs.add(bestI);
      mergedRunAsyncs.add(bestJ);
      matrix[bestI][bestJ] = 0;
      System.out.println("merging: " + bestI + " " + bestJ);
    }
    for (int i = 0; i < runAsyncCount; i++) {
      if (mergedRunAsyncs.contains(i)) {
        continue;
      }
      List<JRunAsync> singletonGroup = new ArrayList<JRunAsync>();
      singletonGroup.add(liveSplitPointMap.runAsyncForId(i));
      asyncGroups.add(singletonGroup);
    }
    return asyncGroups;
  }


  private static <T> void countSharedAtomsOfType(Map<T, BitSet> livenessMap, int[][] matrix) {
    // Count the number of atoms shared only by exactly 2.
    // TODO(rluble): implement a better similarity measure taking into account all subsets that
    // appear.
    for (Map.Entry<T, BitSet> fieldLiveness : livenessMap.entrySet()) {
      BitSet liveSplitPoints = fieldLiveness.getValue();

      if (liveSplitPoints.cardinality() != 2) {
        continue;
      }

      int start = liveSplitPoints.nextSetBit(0);
      int end = liveSplitPoints.nextSetBit(start + 1);
      matrix[start][end]++;
    }
  }


  private void computeLiveSetForEachRunAsync(ControlFlowAnalyzer initiallyLive,
      LiveSplitPointMap liveness, Iterable<JRunAsync> runAsyncs) {
    // Control Flow Analysis from a split point.
    for (JRunAsync runAsync : runAsyncs) {
      ControlFlowAnalyzer cfa = new ControlFlowAnalyzer(initiallyLive);
      cfa.traverseFromRunAsync(runAsync);
      recordLiveSet(cfa, liveness, runAsync);
    }
  }

  private static ControlFlowAnalyzer recordLiveSet(ControlFlowAnalyzer cfa,
      LiveSplitPointMap liveness, JRunAsync runAsync) {
    liveness.addRunAsync(runAsync);
    for (JNode node : cfa.getLiveFieldsAndMethods()) {
      if (node instanceof JField) {
        liveness.setLive((JField) node, runAsync);
      }
      if (node instanceof JMethod) {
        liveness.setLive((JMethod) node, runAsync);
      }
    }

    for (JField node : cfa.getFieldsWritten()) {
      liveness.setLive(node, runAsync);
    }

    for (String s : cfa.getLiveStrings()) {
      liveness.setLive(s, runAsync);
    }

    for (JReferenceType t : cfa.getInstantiatedTypes()) {
      if (t instanceof JDeclaredType) {
        liveness.setLive((JDeclaredType) t, runAsync);
      }
    }
    return cfa;
  }

  private boolean fragmentSizeBelowMergeLimit(List<JsStatement> stats,
      final int leftOverMergeLimit) {
    int sizeInBytes = 0;
    TextOutput out = new TextOutput() {
      int count = 0;

      @Override
      public int getColumn() {
        return 0;
      }

      @Override
      public int getLine() {
        return 0;
      }

      @Override
      public int getPosition() {
        return count;
      }

      @Override
      public void indentIn() {
      }

      @Override
      public void indentOut() {
      }

      @Override
      public void newline() {
        inc(1);
      }

      @Override
      public void newlineOpt() {
      }

      @Override
      public void print(char c) {
        inc(1);
      }

      @Override
      public void print(char[] s) {
        inc(s.length);
      }

      @Override
      public void print(String s) {
        inc(s.length());
      }

      private void inc(int length) {
        count += length;
        if (count >= leftOverMergeLimit) {
          // yucky, but necessary, early exit
          throw new MergeLimitExceededException();
        }
      }

      @Override
      public void printOpt(char c) {
      }

      @Override
      public void printOpt(char[] s) {
      }

      @Override
      public void printOpt(String s) {
      }
    };

    try {
      JsToStringGenerationVisitor v = new JsToStringGenerationVisitor(out);
      for (JsStatement stat : stats) {
        v.accept(stat);
      }
      sizeInBytes += out.getPosition();
    } catch (InternalCompilerException me) {
      if (me.getCause().getClass() == MergeLimitExceededException.class) {
        return false;
      } else {
        throw me;
      }
    }
    return sizeInBytes < leftOverMergeLimit;
  }

  private static class MergeLimitExceededException extends RuntimeException {
  }

  /**
   * Maps an atom to a set of split point that can be live (NOT necessary exclusively)
   * when that split point is activated. The split points are represented by a bit
   * set where S[i] is set if the atom needs to be live when split point i is live.
   */
  private static class LiveSplitPointMap {
    private final Map<JRunAsync, Integer> idForRunAsync = new HashMap<JRunAsync, Integer>();
    private final Map<Integer, JRunAsync> runAsyncForId = new HashMap<Integer, JRunAsync>();
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
    public Map<JField, BitSet> fields = new HashMap<JField, BitSet>();
    public Map<JMethod, BitSet> methods = new HashMap<JMethod, BitSet>();
    public Map<String, BitSet> strings = new HashMap<String, BitSet>();
    public Map<JDeclaredType, BitSet> types = new HashMap<JDeclaredType, BitSet>();

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
}
