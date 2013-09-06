/*
 * Copyright 2011 Google Inc.
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

import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.dev.jjs.InternalCompilerException;
import com.google.gwt.dev.jjs.ast.Context;
import com.google.gwt.dev.jjs.ast.JClassLiteral;
import com.google.gwt.dev.jjs.ast.JClassType;
import com.google.gwt.dev.jjs.ast.JDeclaredType;
import com.google.gwt.dev.jjs.ast.JExpression;
import com.google.gwt.dev.jjs.ast.JField;
import com.google.gwt.dev.jjs.ast.JMethod;
import com.google.gwt.dev.jjs.ast.JNode;
import com.google.gwt.dev.jjs.ast.JProgram;
import com.google.gwt.dev.jjs.ast.JReferenceType;
import com.google.gwt.dev.jjs.ast.JRunAsync;
import com.google.gwt.dev.jjs.ast.JStringLiteral;
import com.google.gwt.dev.jjs.ast.JVisitor;
import com.google.gwt.dev.jjs.impl.ControlFlowAnalyzer;
import com.google.gwt.dev.jjs.impl.JavaToJavaScriptMap;
import com.google.gwt.dev.jjs.impl.codesplitter.CodeSplitter.MultipleDependencyGraphRecorder;
import com.google.gwt.dev.jjs.impl.codesplitter.FragmentExtractor.CfaLivenessPredicate;
import com.google.gwt.dev.jjs.impl.codesplitter.FragmentExtractor.LivenessPredicate;
import com.google.gwt.dev.jjs.impl.codesplitter.FragmentExtractor.NothingAlivePredicate;
import com.google.gwt.dev.js.JsToStringGenerationVisitor;
import com.google.gwt.dev.js.ast.JsBlock;
import com.google.gwt.dev.js.ast.JsContext;
import com.google.gwt.dev.js.ast.JsModVisitor;
import com.google.gwt.dev.js.ast.JsNumericEntry;
import com.google.gwt.dev.js.ast.JsProgram;
import com.google.gwt.dev.js.ast.JsStatement;
import com.google.gwt.dev.util.TextOutput;
import com.google.gwt.dev.util.collect.HashMap;
import com.google.gwt.dev.util.log.speedtracer.CompilerEventType;
import com.google.gwt.dev.util.log.speedtracer.SpeedTracerLogger;
import com.google.gwt.dev.util.log.speedtracer.SpeedTracerLogger.Event;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Queue;
import java.util.Set;

/**
 * Splits the GWT module into multiple downloads. <p>
 * 
 * The code split will divide the code base into multiple <code>spitpoints</code> based
 * on dependency informations computed using {@link com.google.gwt.dev.jjs.impl.ControlFlowAnalyzer}. Each undividable
 * elements of a GWT program: {@link JField}, {@link JMethod}, {@link JDeclaredType} or
 * String literal called <code>atom</code> will be assigned to a set of spitpoints based on
 * dependency information.
 * 
 * A Fragment partitioning will then use the split point assignments and divided the atom
 * into a set of fragments. A fragment will be a single unit of download for a client's
 * code.
 * 
 * TODO(acleung): Rename to CodeSplitter upon completion.
 * TODO(acleung): Some of the data structures and methods are EXACT copy of the
 *                original CoderSplitter.java. This is intentional as we are going to remove
 *                the old one upon completion of this one.
 * TODO(acleung): Figure out how to integrate with SOYC and dependency tracker.
 * TODO(acleung): Insert SpeedTracer calls at performance sensitive places.
 * TODO(acleung): Insert logger calls to generate meaningful logs.
 */
public class CodeSplitter2 {

  /**
   * A read-only class that holds some information about the result of the
   * partition process.
   * 
   * Unlike the original code split where information about the fragments and
   * be deduced from the JProgram, certain compiler passes needs to know what
   * happened here in order to do their job correctly.
   */
  public static final class FragmentPartitioningResult {
    private final int[] fragmentToSplitPoint;
    private final int[] splitPointToFragmentMap;
    
    private FragmentPartitioningResult(int[] splitPointToFragmentMap, int numFragments) {
      this.splitPointToFragmentMap = splitPointToFragmentMap;
      fragmentToSplitPoint = new int[numFragments];
      for (int i = 1, len = splitPointToFragmentMap.length - 1; i < len; i++) {
        if (fragmentToSplitPoint[splitPointToFragmentMap[i]] == 0) {
          fragmentToSplitPoint[splitPointToFragmentMap[i]] = i;
        } else {
          fragmentToSplitPoint[splitPointToFragmentMap[i]] = -1;
        }
      }
    }

    /**
     * @return Fragment index from a splitpoint number.
     */
    public int getFragmentFromSplitPoint(int splitpoint) {
      return splitPointToFragmentMap[splitpoint];
    }

    /**
     * @return Fragment number of the left over fragment.
     */
    public int getLeftoverFragmentIndex() {
      return getNumFragments() - 1;
    }
    
    /**
     * @return Number of code fragments in the compilation. Leftover fragment and initial fragment.
     */
    public int getNumFragments() {
      return fragmentToSplitPoint.length;
    }

    /**
     * @return One of the split point number in a given fragment. If there
     *     are more than one splitpoints in the a fragment, -1 is returned.
     */
    public int getSplitPointFromFragment(int fragment) {
      return fragmentToSplitPoint[fragment];
    }
  }

  /**
   * Marks the type of partition heuristics 
   */
  public enum PartitionHeuristics {
    /**
     * A one-to-one split point to fragment partition with no fragment merging.
     * Basically the 'old' algorithm.
     */
    BIJECTIVE,
    
    /**
     * Greedily merge two piece of fragment if they share the most code
     * together.
     */
    EDGE_GREEDY,
  }


  public static void exec(TreeLogger logger, JProgram jprogram,
      JsProgram jsprogram,
      JavaToJavaScriptMap map, int fragmentsToMerge,
      MultipleDependencyGraphRecorder dependencyRecorder,
      int leftOverMergeLimit) {
    if (jprogram.getRunAsyncs().size() == 0) {
      // Don't do anything if there is no call to runAsync
      return;
    }
    Event codeSplitterEvent = SpeedTracerLogger.start(CompilerEventType.CODE_SPLITTER);
    dependencyRecorder.open();
    new CodeSplitter2(
        logger, jprogram, jsprogram, map, fragmentsToMerge,
        dependencyRecorder, leftOverMergeLimit).execImpl();
    dependencyRecorder.close();
    codeSplitterEvent.end();
  }

  
  private static <T> int getOrZero(Map<T, BitSet> map, T key) {
    BitSet value = map.get(key);
    if (value != null && value.cardinality() == 1) {
      return value.nextSetBit(0);
    }
    return 0;
  }


  private static Map<String, List<Integer>> reverseByName(List<JRunAsync> runAsyncs) {
    Map<String, List<Integer>> revmap = new HashMap<String, List<Integer>>();
    for (JRunAsync replacement : runAsyncs) {
      String name = replacement.getName();
      if (name != null) {
        List<Integer> list = revmap.get(name);
        if (list == null) {
          list = new ArrayList<Integer>();
          revmap.put(name, list);
        }
        list.add(replacement.getSplitPoint());
      }
    }
    return revmap;
  }


  private MultipleDependencyGraphRecorder dependencyRecorder;

  private final Map<JField, JClassLiteral> fieldToLiteralOfClass;
  
  private FragmentExtractor fragmentExtractor;
  
  /**
   * List of runAsyncs that needs to be in the initial load, in that order.
   */
   private final LinkedHashSet<JRunAsync> initialRunAsyncLoadSequence;
  
  /**
   * CFA result of all the initially live atoms.
   */
  private ControlFlowAnalyzer initiallyLive = null;
  
  private final JProgram jprogram;

  private final JsProgram jsprogram;

  private final Set<JMethod> methodsInJavaScript;
  
  /**
   * Number of split points to merge.
   */
  private final int splitPointsMerge;
  private int leftOverMergeLimit;

  /**
   * Maps the split point index X to Y where where that split point X would
   * appear in the Y.cache.js
   */
  private final int[] splitPointToCodeIndexMap;

  /**
   * Maps a split-point number to a fragment number.
   *
   * splitPointToFragmmentMap[x] = y implies split point #x is in fragment #y.
   * 
   * TODO(acleung): We could use some better abstraction for this. I feel this
   * piece of information will be shared with many parts of the codegen process.
   */
  private final int[] splitPointToFragmentMap;

  private CodeSplitter2(TreeLogger logger, JProgram jprogram,
      JsProgram jsprogram,
      JavaToJavaScriptMap map, int splitPointsMerge,
      MultipleDependencyGraphRecorder dependencyRecorder,
      int leftOverMergeLimit) {
    this.jprogram = jprogram;
    this.jsprogram = jsprogram;
    this.splitPointsMerge = splitPointsMerge;
    this.leftOverMergeLimit = leftOverMergeLimit;
    this.dependencyRecorder = dependencyRecorder;
    this.fragmentExtractor = new FragmentExtractor(jprogram, jsprogram, map);
    this.initialRunAsyncLoadSequence = jprogram.getInitialAsyncSequence();
    
    // Start out to assume split gets it's own fragment. We'll merge them later.
    this.splitPointToFragmentMap = new int[jprogram.getRunAsyncs().size() + 1];
    for (int i = 0; i < splitPointToFragmentMap.length; i++) {
      splitPointToFragmentMap[i] = i;
    }
    
    this.splitPointToCodeIndexMap = new int[jprogram.getRunAsyncs().size() + 1];
    for (int i = 0; i < splitPointToCodeIndexMap.length; i++) {
      splitPointToCodeIndexMap[i] = 0;
    }
    
    // TODO(acleung): I don't full understand this. This is mostly from the old
    // algorithm which patches up certain dependency after the control flow analysis.
    fieldToLiteralOfClass = buildFieldToClassLiteralMap(jprogram);
    fragmentExtractor = new FragmentExtractor(jprogram, jsprogram, map);
 
    methodsInJavaScript = fragmentExtractor.findAllMethodsInJavaScript();
  }
  
  /**
   * Create a new fragment and add it to the table of fragments.
   * 
   * @param splitPoint The split point to associate this code with
   * @param alreadyLoaded The code that should be assumed to have already been
   *          loaded
   * @param liveNow The code that is assumed live once this fragment loads;
   *          anything in here but not in <code>alreadyLoaded</code> will be
   *          included in the created fragment
   * @param stmtsToAppend Additional statements to append to the end of the new
   *          fragment
   * @param fragmentStats The list of fragments to append to
   */
  private void addFragment(int splitPoint, LivenessPredicate alreadyLoaded,
      LivenessPredicate liveNow, List<JsStatement> stmtsToAppend,
      Map<Integer, List<JsStatement>> fragmentStats) {
    List<JsStatement> stats = fragmentExtractor.extractStatements(liveNow, alreadyLoaded);
    stats.addAll(stmtsToAppend);
    fragmentStats.put(splitPoint, stats);
  }



  /**
   * This is the high level algorithm of the pass.
   */
  private void execImpl() {

    {
      // This map is only used for deciding how to group splitpoints.
      LiveSplitPointMap liveSplitPointMap = new LiveSplitPointMap();


      // Step #1: Compute all the initially live atoms that are part of entry points
      // class inits..etc.
      initiallyLive = computeInitiallyLive(jprogram, CodeSplitter.NULL_RECORDER);
      recordLiveSet(initiallyLive, liveSplitPointMap, 0);

      // Step #2: Incrementally add each split point that are classified as initial load sequence.
      // Also, any atoms added here will be added to the initially live set as well. The liveness
      for (JRunAsync runAsync : jprogram.getRunAsyncs()) {
        if (initialRunAsyncLoadSequence.contains(runAsync.getSplitPoint())) {
          initiallyLive = computeLiveSet(initiallyLive, liveSplitPointMap, runAsync);
       }
      }

      // Step #3: Similar to #2 but this time, we independently compute the live set of each
      // split point that is not part of the initial load.
      for (JRunAsync runAsync : jprogram.getRunAsyncs()) {
        if (!initialRunAsyncLoadSequence.contains(runAsync.getSplitPoint())) {
          computeLiveSet(initiallyLive, liveSplitPointMap, runAsync);
        }
      }

      // Step #4: Fix up load order dependencies.
      // TODO(rluble): This fixup should be done on the exclusivityMap after computing it; not on
      // the liveSplitPointMap.
      fixUpLoadOrderDependencies(liveSplitPointMap);

      // Step #5: Now the LiveSplitPointMap will contain all the liveness information we need,
      // partition the fragments by focusing on making the initial download and
      // leftover fragment download as small as possible.
      partitionFragments(liveSplitPointMap);

      // Once the partition has been decided, the liveness map should not be used anymore.
    }


  */  // Step #6: Extract fragments using the partition algorithm.
    extractStatements(computeInitiallyLive(jprogram, dependencyRecorder));
    
    // Step #7: Replaces the splitpoint number with the new fragment number.
    replaceFragmentId();
  }

  /**
   * Given the set of code initially live, and a set of splitpoints grouped into fragments:
   * The core algorithm to compute exclusive merged fragments is as follows:
   * For each fragment (grouping of merged splitpoint numbers)
   * 1) compute the set of live statements of every splitpoint EXCEPT those in the fragment
   * 2) compute the set of live statements reachable from those in the fragment
   * 3) calculate a set difference of everything live minus the results of step 1
   * 4) filter results by checking for membership in the results of step 2
   * 5) assign resulting live code to this fragment (recorded in a map)
   *
   * The results of these steps are then used to extract individual JavaScript chunks
   * into blocks corresponding to fragments which are ultimately written to disk.
   * @param initiallyLive the CFA of code live from the entry point (initial fragments)
   */
  private void extractStatements(ControlFlowAnalyzer initiallyLive) {
    Map<Integer, List<JsStatement>> fragmentStats = new LinkedHashMap<Integer, List<JsStatement>>();
    
    // Initial download
    {
      LivenessPredicate alreadyLoaded = new NothingAlivePredicate();
      LivenessPredicate liveNow = new CfaLivenessPredicate(initiallyLive);
      List<JsStatement> noStats = new ArrayList<JsStatement>();
      addFragment(0, alreadyLoaded, liveNow, noStats, fragmentStats);
    }
    
    ControlFlowAnalyzer liveAfterInitialSequence = new ControlFlowAnalyzer(initiallyLive);
    String extendsCfa = "initial";
   
    int cacheIndex = 1;
 /*   // Initial Split Point.
    {      
      for (final int sp : initialRunAsyncLoadSequence) {
        splitPointToCodeIndexMap[sp] = cacheIndex;
        String depGraphName = "sp" + cacheIndex;

        // Records dependency Graph.
        dependencyRecorder.startDependencyGraph(depGraphName, extendsCfa);
        extendsCfa = depGraphName;

        LivenessPredicate alreadyLoaded = new CfaLivenessPredicate(liveAfterInitialSequence);
        ControlFlowAnalyzer liveAfterSp = new ControlFlowAnalyzer(liveAfterInitialSequence);
        JRunAsync runAsync = jprogram.getRunAsyncs().get(sp - 1);

        liveAfterSp.setDependencyRecorder(dependencyRecorder);
        liveAfterSp.traverseFromRunAsync(runAsync);
        dependencyRecorder.endDependencyGraph();

        LivenessPredicate liveNow = new CfaLivenessPredicate(liveAfterSp);
        List<JsStatement> statsToAppend = fragmentExtractor.createOnLoadedCall(cacheIndex);
        addFragment(sp, alreadyLoaded, liveNow, statsToAppend, fragmentStats);
        liveAfterInitialSequence = liveAfterSp;       
        cacheIndex++;
      }
    }
*/
    ControlFlowAnalyzer everything = computeCompleteCfa();
    Set<JField> allFields = new HashSet<JField>();
    Set<JMethod> allMethods = new HashSet<JMethod>();

    for (JNode node : everything.getLiveFieldsAndMethods()) {
      if (node instanceof JField) {
        allFields.add((JField) node);
      }
      if (node instanceof JMethod) {
        allMethods.add((JMethod) node);
      }
    }
    allFields.addAll(everything.getFieldsWritten());
    ArrayList<JsStatement> leftOverMergeStats = new ArrayList<JsStatement>();

    ExclusivityMap exclusivityMap = new ExclusivityMap();


    // Search for all the atoms that are exclusively needed in each split point.
    for (int i = 1; i < splitPointToFragmentMap.length; i++) {
      
      ArrayList<Integer> splitPoints = new ArrayList<Integer>();
      
      // This mean split point [i] has been merged with another split point, ignore it.
      if (splitPointToFragmentMap[i] != i) {
        continue;
      }
      
      // This was needed in the initial load sequence, ignore it.
/*      if (initialRunAsyncLoadSequence.contains(i)) {
        continue;
      }
      
      splitPoints.add(i);
      splitPointToCodeIndexMap[i] = cacheIndex;
      
      
      for (int j = i + 1; j < splitPointToFragmentMap.length; j++) {
        if (initialRunAsyncLoadSequence.contains(j)) {
          continue;
        }
        if (splitPointToFragmentMap[j] == i) {
          splitPointToCodeIndexMap[j] = cacheIndex;
          splitPoints.add(j);
        }
      }

  */    dependencyRecorder.startDependencyGraph("sp" + cacheIndex, extendsCfa);
      ControlFlowAnalyzer allExceptCurrentSetOfSplitpoints =
          computeAllButNCfas(liveAfterInitialSequence, splitPoints, dependencyRecorder);
      dependencyRecorder.endDependencyGraph();

      // TODO(rluble): the dependency recorder wont capture atoms that are not in the current
      // fragment due to the fact that they are not live here.
/*
      // Update the exclusivity map.
      Set<JNode> allLiveNodes =
          union(allExceptCurrentSetOfSplitpoints.getLiveFieldsAndMethods(),
              allExceptCurrentSetOfSplitpoints.getFieldsWritten());
      exclusivityMap.updateFields(i, allLiveNodes, allFields);
      exclusivityMap.updateMethods(i, allExceptCurrentSetOfSplitpoints.getLiveFieldsAndMethods(),
          allMethods);
      exclusivityMap.updateStrings(i, allExceptCurrentSetOfSplitpoints.getLiveStrings(), everything
          .getLiveStrings());
      exclusivityMap.updateTypes(i,
          declaredTypesIn(allExceptCurrentSetOfSplitpoints.getInstantiatedTypes()),
          declaredTypesIn(everything.getInstantiatedTypes()));


      // This mean split point [i] has been merged with another split point, ignore it.
      if (splitPointToFragmentMap[i] != i) {
        continue;
      }
      
      // This was needed in the initial load sequence, ignore it.
      if (initialRunAsyncLoadSequence.contains(i)) {
        continue;
      }

      LivenessPredicate alreadyLoaded = exclusivityMap.getLivenessPredicate(0);
      LivenessPredicate liveNow = exclusivityMap.getLivenessPredicate(i);
      List<JsStatement> exclusiveStats = fragmentExtractor.extractStatements(liveNow, alreadyLoaded);
      if (fragmentSizeBelowMergeLimit(exclusiveStats, leftOverMergeLimit)) {
        leftOverMergeStats.addAll(exclusiveStats);
        // merged to leftovers
        splitPointToFragmentMap[i] = -1;
        continue;
      } else {
        List<JsStatement> statsToAppend = fragmentExtractor.createOnLoadedCall(cacheIndex);
        addFragment(i, alreadyLoaded, liveNow, statsToAppend, fragmentStats);
      }
      cacheIndex++;
  */  }



      for (int i = 0; i < splitPointToFragmentMap.length; i++) {
      if (splitPointToFragmentMap[i] == -1) {
        // set fragment number -1 to be leftovers fragment number
        splitPointToFragmentMap[i] = splitPointToFragmentMap.length;
      }
    }

    /*
     * Compute the leftovers fragment.
     */
    {
      LivenessPredicate alreadyLoaded = new CfaLivenessPredicate(liveAfterInitialSequence);
//      LivenessPredicate liveNow = exclusivityMap.getLivenessPredicate(0);
      List<JsStatement> statsToAppend = fragmentExtractor.createOnLoadedCall(cacheIndex);
      leftOverMergeStats.addAll(statsToAppend);
//      addFragment(splitPointToFragmentMap.length, alreadyLoaded, liveNow, leftOverMergeStats,
//          fragmentStats);
    }
    
    // now install the new statements in the program fragments
    jsprogram.setFragmentCount(fragmentStats.size());
    int count = 0;
    for (int i : fragmentStats.keySet()) {
      JsBlock fragBlock = jsprogram.getFragmentBlock(count++);
      fragBlock.getStatements().clear();
      fragBlock.getStatements().addAll(fragmentStats.get(i));
    }

    jprogram.setFragmentPartitioningResult(
        new FragmentPartitioningResult(splitPointToCodeIndexMap, fragmentStats.size()));
  }


  /**
   * We haves pinned down that fragment partition is an NP-Complete problem that maps right to
   * weight graph partitioning.
   */
  private void replaceFragmentId() {
    (new JsModVisitor() {
      @Override
      public void endVisit(JsNumericEntry x, JsContext ctx) {
        if (x.getKey().equals("RunAsyncFragmentIndex")) {
          x.setValue(splitPointToCodeIndexMap[x.getValue()]);
        }
        if (x.getKey().equals("RunAsyncFragmentCount")) {
          x.setValue(jsprogram.getFragmentCount() - 1);
        }
      }
    }).accept(jsprogram);
  }

}
