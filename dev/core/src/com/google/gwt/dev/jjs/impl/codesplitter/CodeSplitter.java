/*
 * Copyright 2008 Google Inc.
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
import com.google.gwt.dev.jjs.ast.JClassType;
import com.google.gwt.dev.jjs.ast.JDeclaredType;
import com.google.gwt.dev.jjs.ast.JField;
import com.google.gwt.dev.jjs.ast.JMethod;
import com.google.gwt.dev.jjs.ast.JNode;
import com.google.gwt.dev.jjs.ast.JProgram;
import com.google.gwt.dev.jjs.ast.JReferenceType;
import com.google.gwt.dev.jjs.ast.JRunAsync;
import com.google.gwt.dev.jjs.impl.ControlFlowAnalyzer;
import com.google.gwt.dev.jjs.impl.ControlFlowAnalyzer.DependencyRecorder;
import com.google.gwt.dev.jjs.impl.codesplitter.FragmentExtractor.CfaLivenessPredicate;
import com.google.gwt.dev.jjs.impl.codesplitter.FragmentExtractor.LivenessPredicate;
import com.google.gwt.dev.jjs.impl.codesplitter.FragmentExtractor.NothingAlivePredicate;
import com.google.gwt.dev.jjs.impl.codesplitter.FragmentExtractor.StatementLogger;
import com.google.gwt.dev.jjs.impl.JavaToJavaScriptMap;
import com.google.gwt.dev.js.JsToStringGenerationVisitor;
import com.google.gwt.dev.js.ast.JsBlock;
import com.google.gwt.dev.js.ast.JsContext;
import com.google.gwt.dev.js.ast.JsExprStmt;
import com.google.gwt.dev.js.ast.JsExpression;
import com.google.gwt.dev.js.ast.JsFunction;
import com.google.gwt.dev.js.ast.JsModVisitor;
import com.google.gwt.dev.js.ast.JsNumericEntry;
import com.google.gwt.dev.js.ast.JsProgram;
import com.google.gwt.dev.js.ast.JsStatement;
import com.google.gwt.dev.js.ast.JsVars;
import com.google.gwt.dev.js.ast.JsVars.JsVar;
import com.google.gwt.dev.util.TextOutput;
import com.google.gwt.dev.util.collect.HashMap;
import com.google.gwt.dev.util.collect.HashSet;
import com.google.gwt.dev.util.log.speedtracer.CompilerEventType;
import com.google.gwt.dev.util.log.speedtracer.SpeedTracerLogger;
import com.google.gwt.dev.util.log.speedtracer.SpeedTracerLogger.Event;
import com.google.gwt.thirdparty.guava.common.base.Predicate;
import com.google.gwt.thirdparty.guava.common.collect.Collections2;
import com.google.gwt.thirdparty.guava.common.collect.Iterables;


import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nullable;

/**
 * <p>
 * Divides the code in a {@link JsProgram} into multiple fragments. The initial
 * fragment is sufficient to run all of the program's functionality except for
 * anything called in a callback supplied to
 * {@link com.google.gwt.core.client.GWT#runAsync(com.google.gwt.core.client.RunAsyncCallback)
 * GWT.runAsync()}. The remaining code should be downloadable via
 * {@link com.google.gwt.core.client.impl.AsyncFragmentLoader#inject(int)}.
 * </p>
 * 
 * <p>
 * The precise way the program is fragmented is an implementation detail that is
 * subject to change. Whenever the fragment strategy changes,
 * <code>AsyncFragmentLoader</code> must be updated in tandem. That said, the
 * basic fragmentation strategy is to create an initial fragment, a leftovers
 * fragment, and one fragment per split point. Additionally, the splitter
 * computes an initial load sequence. All runAsync calls in the initial load
 * sequence are reached before any call not in the sequence. Further, any call
 * in the sequence is reached before any call later in the sequence.
 * </p>
 * 
 * <p>
 * The fragment for a split point contains different things depending on whether
 * it is in the initial load sequence or not. If it's in the initial load
 * sequence, then the fragment includes the code newly live once that split
 * point is crossed, that wasn't already live for the set of split points
 * earlier in the sequence. For a split point not in the initial load sequence,
 * the fragment contains only code exclusive to that split point, that is, code
 * that cannot be reached except via that split point. All other code goes into
 * the leftovers fragment.
 * </p>
 */
@SuppressWarnings("JavadocReference")
public class CodeSplitter {
  // TODO(rluble): This class needs a serious refactor to be able to add significant unit tests.

  /**
   * A dependency recorder that can record multiple dependency graphs. It has
   * methods for starting and finishing new dependency graphs.
   */
  public interface MultipleDependencyGraphRecorder extends DependencyRecorder {
    /**
     * Stop recording dependencies.
     */
    void close();

    /**
     * Stop recording the current dependency graph.
     */
    void endDependencyGraph();

    void open();

    /**
     * Start a new dependency graph. It can be an extension of a previously
     * recorded dependency graph, in which case the dependencies in the previous
     * graph will not be repeated.
     */
    void startDependencyGraph(String name, String extnds);
  }

  /**
   * A statement logger that immediately prints out everything live that it
   * sees.
   */
  private class EchoStatementLogger implements StatementLogger {
    public void logStatement(JsStatement stat, boolean isIncluded) {
      if (!isIncluded) {
        return;
      }
      if (stat instanceof JsExprStmt) {
        JsExpression expr = ((JsExprStmt) stat).getExpression();
        if (!(expr instanceof JsFunction)) {
          return;
        }
        JsFunction func = (JsFunction) expr;
        if (func.getName() == null) {
          return;
        }
        JMethod method = map.nameToMethod(func.getName());
        if (method == null) {
          return;
        }
        System.out.println(fullNameString(method));

      } else if (stat instanceof JsVars) {
        JsVars vars = (JsVars) stat;
        for (JsVar var : vars) {
          JField field = map.nameToField(var.getName());
          if (field != null) {
            System.out.println(fullNameString(field));
          }
        }
      }
    }
  }

  /**
   * A {@link MultipleDependencyGraphRecorder} that does nothing.
   */
  public static final MultipleDependencyGraphRecorder NULL_RECORDER =
      new MultipleDependencyGraphRecorder() {
        public void close() {
        }

        public void endDependencyGraph() {
        }

        public void methodIsLiveBecause(JMethod liveMethod, ArrayList<JMethod> dependencyChain) {
        }

        public void open() {
        }

        public void startDependencyGraph(String name, String extnds) {
        }
      };


  /**
   * A Java property that causes the fragment map to be logged.
   */
  private static String PROP_LOG_FRAGMENT_MAP = "gwt.jjs.logFragmentMap";

  public static ControlFlowAnalyzer computeInitiallyLive(JProgram jprogram) {
    return computeInitiallyLive(jprogram, NULL_RECORDER);
  }

  public static void exec(TreeLogger logger, JProgram jprogram, JsProgram jsprogram,
      JavaToJavaScriptMap map, int expectedFragmentCount, int leftOverSizeMerge,
      MultipleDependencyGraphRecorder dependencyRecorder) {
    if (jprogram.getRunAsyncs().size() == 0) {
      // Don't do anything if there is no call to runAsync
      return;
    }
    Event codeSplitterEvent = SpeedTracerLogger.start(CompilerEventType.CODE_SPLITTER);
    dependencyRecorder.open();
    new CodeSplitter(logger, jprogram, jsprogram, map, expectedFragmentCount, leftOverSizeMerge,
        dependencyRecorder).execImpl();
    dependencyRecorder.close();
    codeSplitterEvent.end();
  }


  // TODO(rluble): these four functions would probably best moved to the FragmentPartitionResult.
  public static int getExclusiveFragmentNumber(int splitPointId) {
    return splitPointId;
  }

  public static int getLeftoversFragmentNumber(int numSplitPoints) {
    return numSplitPoints + 1;
  }

  /**
   * Infer the number of split points for a given number of code fragments.
   */
  public static int numSplitPointsForFragments(int codeFragments) {
    assert (codeFragments != 2);

    if (codeFragments == 1) {
      return 0;
    }

    return codeFragments - 2;
  }


  /**
   * <p>
   * Computes the "maximum total script size" for one permutation. The total
   * script size for one sequence of split points reached is the sum of the
   * scripts that are downloaded for that sequence. The maximum total script
   * size is the maximum such size for all possible sequences of split points.
   * </p>
   * 
   * @param jsLengths The lengths of the fragments for the compilation of one
   *          permutation
   */

  public static int totalScriptSize(int[] jsLengths) {
    /*
     * The total script size is currently simple: it's the sum of all the
     * individual script files.
     */

    int maxTotalSize;
    int numSplitPoints = numSplitPointsForFragments(jsLengths.length);
    if (numSplitPoints == 0) {
      maxTotalSize = jsLengths[0];
    } else {
      // Add up the initial and exclusive fragments
      maxTotalSize = jsLengths[0];
      for (int sp = 1; sp <= numSplitPoints; sp++) {
        int excl = getExclusiveFragmentNumber(sp);
        maxTotalSize += jsLengths[excl];
      }

      // Add the leftovers
      maxTotalSize += jsLengths[getLeftoversFragmentNumber(numSplitPoints)];
    }
    return maxTotalSize;
  }
  // TODO(rluble): up to here.


  /**
   * Compute the set of initially live code for this program. Such code must be
   * included in the initial download of the program.
   */
  private static ControlFlowAnalyzer computeInitiallyLive(JProgram jprogram,
      MultipleDependencyGraphRecorder dependencyRecorder) {
    dependencyRecorder.startDependencyGraph("initial", null);

    ControlFlowAnalyzer cfa = new ControlFlowAnalyzer(jprogram);
    cfa.setDependencyRecorder(dependencyRecorder);
    cfa.traverseEntryMethods();
    traverseClassArray(jprogram, cfa);
    traverseImmortalTypes(jprogram, cfa);
    dependencyRecorder.endDependencyGraph();
    return cfa;
  }

  /**
   * Extract the types from a set that happen to be declared types.
   */
  private static Set<JDeclaredType> declaredTypesIn(Set<JReferenceType> types) {
    Set<JDeclaredType> result = new HashSet<JDeclaredType>();
    for (JReferenceType type : types) {
      if (type instanceof JDeclaredType) {
        result.add((JDeclaredType) type);
      }
    }
    return result;
  }

  private static String fullNameString(JField field) {
    return field.getEnclosingType().getName() + "." + field.getName();
  }

  private static String fullNameString(JMethod method) {
    return method.getEnclosingType().getName() + "." + JProgram.getJsniSig(method);
  }

  /**
   * Any instance method in the magic Array class must be in the initial
   * download. The methods of that class are copied to a separate object the
   * first time class Array is touched, and any methods added later won't be
   * part of the copy.
   */
  private static void traverseClassArray(JProgram jprogram, ControlFlowAnalyzer cfa) {
    JDeclaredType typeArray = jprogram.getFromTypeMap("com.google.gwt.lang.Array");
    if (typeArray == null) {
      // It was pruned; nothing to do
      return;
    }

    cfa.traverseFromInstantiationOf(typeArray);
    for (JMethod method : typeArray.getMethods()) {
      if (method.needsVtable()) {
        cfa.traverseFrom(method);
      }
    }
  }

  /**
   * Any immortal codegen types must be part of the initial download.
   */
  private static void traverseImmortalTypes(JProgram jprogram,
      ControlFlowAnalyzer cfa) {
    for (JClassType type : jprogram.immortalCodeGenTypes) {
      cfa.traverseFromInstantiationOf(type);
      for (JMethod method : type.getMethods()) {
        if (!method.needsVtable()) {
          cfa.traverseFrom(method);
        }
      }
    }
  }

  private static <T> Set<T> union(Set<? extends T> set1, Set<? extends T> set2) {
    Set<T> union = new HashSet<T>();
    union.addAll(set1);
    union.addAll(set2);
    return union;
  }

  private final MultipleDependencyGraphRecorder dependencyRecorder;
  private final FragmentExtractor fragmentExtractor;
  private final LinkedHashSet<JRunAsync> initialLoadSequence;

  /**
   * Code that is initially live when the program first downloads.
   */
  private final ControlFlowAnalyzer initiallyLive;
  private final JProgram jprogram;
  private final JsProgram jsprogram;

  /**
   * Computed during {@link #execImpl()}, so that intermediate steps of it can
   * be used as they are created.
   */
  private ControlFlowAnalyzer liveAfterInitialSequence;
  private final TreeLogger logger;
  private final boolean logging;
  private final JavaToJavaScriptMap map;
  private final Set<JMethod> methodsInJavaScript;

  private int nextFragmentNumberToAssign = 0;
  private final List<Fragment> fragments = new ArrayList<Fragment>();

  private final FragmentationStrategy strategy;

  private final int leftOverMergeSize;

  private CodeSplitter(TreeLogger logger, JProgram jprogram, JsProgram jsprogram,
      JavaToJavaScriptMap map, int expectedFragmentCount, int leftOverMergeSize,
      MultipleDependencyGraphRecorder dependencyRecorder) {
    this.logger = logger.branch(TreeLogger.TRACE, "Splitting JavaScript for incremental download");
    this.jprogram = jprogram;
    this.jsprogram = jsprogram;
    this.map = map;
    this.dependencyRecorder = dependencyRecorder;
    this.initialLoadSequence = jprogram.getInitialAsyncSequence();
    this.leftOverMergeSize = leftOverMergeSize;
    assert initialLoadSequence != null;

    logging = Boolean.getBoolean(PROP_LOG_FRAGMENT_MAP);
    fragmentExtractor = new FragmentExtractor(jprogram, jsprogram, map);

    initiallyLive = computeInitiallyLive(jprogram, dependencyRecorder);

    methodsInJavaScript = fragmentExtractor.findAllMethodsInJavaScript();

    // TODO(rluble): expected fragment count is not enforced. the actual number
    // of fragments may be more or less....
    strategy = expectedFragmentCount > 0 ?
        new MergeBySimilarityFragmentationStrategy(expectedFragmentCount - 2) :
        new OneToOneFragmentationStrategy();
  }

  /**
   * Compute the statements that go into a fragment.
   * 
   * @param alreadyLoaded The code that should be assumed to have already been
   *          loaded
   * @param liveNow The code that is assumed live once this fragment loads;
   *          anything in here but not in <code>alreadyLoaded</code> will be
   *          included in the created fragment
   * @param statementsToAppend Additional statements to append to the end of the new
   *          fragment
   */
  private List<JsStatement>  statementsForFragment(int fragmentNumber,
      LivenessPredicate alreadyLoaded, LivenessPredicate liveNow) {
    if (logging) {
      System.out.println();
      System.out.println("==== Fragment " + fragmentNumber + " ====");
      fragmentExtractor.setStatementLogger(new EchoStatementLogger());
    }
    return fragmentExtractor.extractStatements(liveNow, alreadyLoaded);
  }

  /**
   * For each exclusive fragment (those that are not part of the initial load sequence) compute
   * a CFA that traces every split point not in the fragment.
   */
  private Map<Fragment, ControlFlowAnalyzer> computeComplementCfaForFragment(
      Collection<Fragment> exclusiveFragments) {
    String dependencyGraphNameAfterInitialSequence = dependencyGraphNameAfterInitialSequence();

    Map<Fragment, ControlFlowAnalyzer> complementCfaForFragment =
        new HashMap<Fragment, ControlFlowAnalyzer>();
    for (Fragment fragment : exclusiveFragments) {
      assert fragment.isExclusive();

      dependencyRecorder.startDependencyGraph("sp" + fragment.getFragmentNumber(),
          dependencyGraphNameAfterInitialSequence);
      ControlFlowAnalyzer cfa = new ControlFlowAnalyzer(liveAfterInitialSequence);
      cfa.setDependencyRecorder(dependencyRecorder);
      for (Fragment otherFragment : exclusiveFragments) {
        // don't trace the initial fragments as they have already been traced and their atoms are
        // already in {@code liveAfterInitialSequence}
        if (otherFragment.isInitial()) {
          continue;
        }
        if (otherFragment == fragment) {
          continue;
        }
        for (JRunAsync otherRunAsync : otherFragment.getRunAsyncs()) {
          cfa.traverseFromRunAsync(otherRunAsync);
        }
      }
      dependencyRecorder.endDependencyGraph();
      complementCfaForFragment.put(fragment, cfa);
    }

    return complementCfaForFragment;
  }

  /**
   * Compute a CFA that covers the entire live code of the program.
   */
  private ControlFlowAnalyzer computeCompleteCfa() {
    dependencyRecorder.startDependencyGraph("total", null);
    ControlFlowAnalyzer everything = new ControlFlowAnalyzer(jprogram);
    everything.setDependencyRecorder(dependencyRecorder);
    everything.traverseEverything();
    dependencyRecorder.endDependencyGraph();
    return everything;
  }

  /**
   * The name of the dependency graph that corresponds to
   * {@link #liveAfterInitialSequence}.
   */
  private String dependencyGraphNameAfterInitialSequence() {
    if (initialLoadSequence.isEmpty()) {
      return "initial";
    } else {
      return "sp" + Iterables.getLast(initialLoadSequence).getRunAsyncId();
    }
  }

  /**
   * Map each program atom as exclusive to some split point, whenever possible.
   * Also fixes up load order problems that could result from splitting code
   * based on this assumption.
   */
  private ExclusivityMap determineExclusivity(Collection<Fragment> exclusiveFragments) {
    ExclusivityMap exclusivityMap =  computeExclusivityMap(exclusiveFragments);
    exclusivityMap.fixUpLoadOrderDependencies(logger, jprogram, methodsInJavaScript);

    return exclusivityMap;
  }

  /**
   * The current implementation of code splitting divides the program into fragments. There are
   * four different types of fragment.
   *   - initial download: the part of the program that will execute from the entry point and is
   *     not part of any runAsync. This fragment is implicit and there is not representation of
   *     it in the code splitter.
   *   - initial fragments: some runAsyncs are forced to be in the initial download by listing them
   *     in the {@link Util.PROP_INITIAL_SEQUENCE} property. A separate fragment (Type.INITIAL)
   *     is created for each of there splitpoints and each contains only one splitpoit.
   *   - exclusive fragments: the remaining runAsyncs are assigned to some exclusive fragment. Many
   *     splitpoints may be in the same fragment but each of these splitpoints is in one and only
   *     one fragment. The fragmentation strategy assigns splitpoints to fragments.
   *   - leftover fragments: this is an artificial fragment that will contain all the atoms that
   *     are not in the initial and are not exclusive to a fragment.
   *
   *<p>Code splitting is a three stage process:
   *   - first the initial fragment are determined.
   *   - then a fragmentation strategy is run to partition runAsyncs into exclusive fragments.
   *   - lastly atoms that are not exclusive are assigned to the LEFT_OVERS fragment.
   */
  private void execImpl() {

    // Step #1: Decide how to map splitpoints to fragments.
    {
      /*
       * Compute the base fragment. It includes everything that is live when the
       * program starts.
       */
      LivenessPredicate alreadyLoaded = new NothingAlivePredicate();
      LivenessPredicate liveNow = new CfaLivenessPredicate(initiallyLive);
      Fragment fragment =
          new Fragment(Fragment.Type.INITIAL);
      fragment.setFragmentNumber(nextFragmentNumberToAssign++);
      List<JsStatement> statementsForFragment = statementsForFragment(fragment.getFragmentNumber(),
          alreadyLoaded, liveNow);
      fragment.setStatementsInFragment(statementsForFragment);
      fragments.add(fragment);
    }

    /*
     * Compute the base fragments, for split points in the initial load
     * sequence.
     */
    liveAfterInitialSequence = new ControlFlowAnalyzer(initiallyLive);
    String extendsCfa = "initial";
    List<Integer> initialFragmentNumberSequence = new ArrayList<Integer>();
    for (JRunAsync runAsync : initialLoadSequence) {
      LivenessPredicate alreadyLoaded = new CfaLivenessPredicate(liveAfterInitialSequence);

      String depGraphName = "sp" + runAsync.getRunAsyncId();
      dependencyRecorder.startDependencyGraph(depGraphName, extendsCfa);
      extendsCfa = depGraphName;

      ControlFlowAnalyzer liveAfterSp = new ControlFlowAnalyzer(liveAfterInitialSequence);
      liveAfterSp.traverseFromRunAsync(runAsync);
      dependencyRecorder.endDependencyGraph();

      LivenessPredicate liveNow = new CfaLivenessPredicate(liveAfterSp);

      Fragment fragment = new Fragment(Fragment.Type.INITIAL);
      fragment.setFragmentNumber(nextFragmentNumberToAssign++);
      // TODO(rluble): RunAsyncIds are assumed to be the fragment number in some places.
      // refactor and cleanup that later.
      assert runAsync.getRunAsyncId() == fragment.getFragmentNumber();
      fragment.addRunAsync(runAsync);
      List<JsStatement> statements = statementsForFragment(fragment.getFragmentNumber(),
          alreadyLoaded, liveNow);
      statements.addAll(fragmentExtractor.createOnLoadedCall(runAsync.getRunAsyncId()));
      fragment.setStatementsInFragment(statements);
      fragments.add(fragment);

      initialFragmentNumberSequence.add(fragment.getFragmentNumber());
      liveAfterInitialSequence = liveAfterSp;
    }

    // Set the initial fragment sequence.
    jprogram.setInitialFragmentNumberSequence(initialFragmentNumberSequence);

    // Decide exclusive fragments according to the preselected strategy.
    List<Fragment>  exclusiveFragments =
        strategy.partitionIntoFragments(liveAfterInitialSequence,
            Collections2.filter(jprogram.getRunAsyncs(), new Predicate<JRunAsync>() {
              @Override
              public boolean apply(@Nullable JRunAsync jRunAsync) {
                return !isInitial(jRunAsync);
              }
            }));

    int firstExclusiveFragmentNumber = nextFragmentNumberToAssign;
    // Assign fragment numbers to exclusive fragments.
    for (Fragment fragment : exclusiveFragments) {
      fragment.setFragmentNumber(nextFragmentNumberToAssign++);
    }


    // Determine which atoms actually land in each exclusive fragment.
    ExclusivityMap exclusivityMap = determineExclusivity(exclusiveFragments);

    /*
     * Populate the exclusively live fragments. Each includes everything
     * exclusively live after entry point i.
     */
    Iterator<Fragment> fragmentIterator = exclusiveFragments.iterator();
    Fragment fragmentCollapsingVerySmallFragments = null;
    while (fragmentIterator.hasNext()) {
      Fragment fragment = fragmentIterator.next();
      assert fragment.isExclusive();

      LivenessPredicate alreadyLoaded = exclusivityMap.getLivenessPredicate(
          ExclusivityMap.NOT_EXCLUSIVE);
      LivenessPredicate liveNow = exclusivityMap.getLivenessPredicate(fragment);
      List<JsStatement> statements = statementsForFragment(fragment.getFragmentNumber(),
          alreadyLoaded, liveNow);
      fragment.setStatementsInFragment(statements);

      if (fragmentSizeBelowMergeLimit(statements,leftOverMergeSize)) {
        // Merge small fragments together
        if (fragmentCollapsingVerySmallFragments == null) {
          fragmentCollapsingVerySmallFragments = fragment;
        } else {
          fragmentCollapsingVerySmallFragments.addRunAsyncs(fragment.getRunAsyncs());
          fragmentCollapsingVerySmallFragments.addStatementsToFragment(
              statements);
          fragment.setDeleted();
          fragmentIterator.remove();
        }
      }
    }
    // If a fragments were collapsed, check again whether the resulting fragment is too small
    // and merge with the last fragment.
    // TODO(rluble): there is some uncleanliness about this approach.
    // The partitioning algorithm should be the one deciding to merge small fragments , however
    // with the current design we can only know the java script source size after determining
    // exclusivity.
    // The fixup pass will still refer to old fragments but the resulting fixup should be ok
    // (actually conservative, atoms might become exclusive after merging but the fixup will
    // not know).
    if (fragmentCollapsingVerySmallFragments != null &&
        fragmentSizeBelowMergeLimit(
            fragmentCollapsingVerySmallFragments.getStatementsInFragment(),leftOverMergeSize)) {
      // fragment still too small, merge into last exclusive fragment
      fragmentCollapsingVerySmallFragments.setDeleted();
      exclusiveFragments.remove(fragmentCollapsingVerySmallFragments);
    }

    // Finally we now have the exclusive fragments but they might need renumbering if
    // there was a collapes.
    // TODO(rluble): Renumbering after computing the exclusivity map make it stale but
    // is still safe to use it. recomputing it is costly, but we might be able to
    // update it and just recompute the changed fragment.
    nextFragmentNumberToAssign = firstExclusiveFragmentNumber;
    for (Fragment fragment : exclusiveFragments) {
      // renumber fragments and create onLoaded call.
      fragment.setFragmentNumber(nextFragmentNumberToAssign++);
      fragment.addStatementsToFragment(
          fragmentExtractor.createOnLoadedCall(fragment.getFragmentNumber()));
    }

    fragments.addAll(exclusiveFragments);
    /*
     * Populate the leftovers fragment.
     */
    {
      LivenessPredicate alreadyLoaded = new CfaLivenessPredicate(liveAfterInitialSequence);
      LivenessPredicate liveNow = exclusivityMap.getLivenessPredicate(ExclusivityMap.NOT_EXCLUSIVE);
      Fragment fragment =
          new Fragment(Fragment.Type.NOT_EXCLUSIVE);
      fragment.setFragmentNumber(nextFragmentNumberToAssign++);
      List<JsStatement> statements = statementsForFragment(fragment.getFragmentNumber(),
          alreadyLoaded, liveNow);
      statements.addAll(fragmentExtractor.createOnLoadedCall(fragment.getFragmentNumber()));
      fragment.setStatementsInFragment(statements);
      fragments.add(fragment);
    }

    // now install the new statements in the program fragments
    jsprogram.setFragmentCount(fragments.size());
    for (int i = 0; i < fragments.size(); i++) {
      JsBlock fragBlock = jsprogram.getFragmentBlock(i);
      fragBlock.getStatements().clear();
      fragBlock.getStatements().addAll(fragments.get(i).getStatementsInFragment());
    }

    // Lastly pass the fragmenting information to JProgram.
    // TODO(rluble): Make the information uniform.
    jprogram.setFragmentPartitioningResult(
        new FragmentPartitioningResult(fragments, jprogram));

    // Lastly patch up the JavaScript AST
    replaceFragmentId();
  }

  private boolean isInitial(JRunAsync runAsync) {
    return initialLoadSequence.contains(runAsync);
  }

  /**
   * Patch up the fragment loading code in the JavaScript AST.
   */
  private void replaceFragmentId() {
    final FragmentPartitioningResult result = jprogram.getFragmentPartitioningResult();
    (new JsModVisitor() {
      @Override
      public void endVisit(JsNumericEntry x, JsContext ctx) {
        if (x.getKey().equals("RunAsyncFragmentIndex")) {
          x.setValue(result.getFragmentForRunAsync(x.getValue()));
        }
        if (x.getKey().equals("RunAsyncFragmentCount")) {
          x.setValue(jsprogram.getFragmentCount() - 1);
        }
      }
    }).accept(jsprogram);
  }


  /**
   * Map atoms to exclusive fragments. Do this by trying to find code atoms that
   * are only needed by a single split point. Such code can be moved to the
   * exclusively live fragment associated with that split point.
   */
  private ExclusivityMap computeExclusivityMap(Collection<Fragment> exclusiveFragments) {

    ExclusivityMap exclusivityMap = new ExclusivityMap();
    Map<Fragment, ControlFlowAnalyzer> allButOnes =
        computeComplementCfaForFragment(exclusiveFragments);

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

    for (Fragment fragment : exclusiveFragments) {
      assert fragment.isExclusive();
      ControlFlowAnalyzer allButOne = allButOnes.get(fragment);
      Set<JNode> allLiveNodes =
          union(allButOne.getLiveFieldsAndMethods(), allButOne.getFieldsWritten());
      exclusivityMap.updateFields(fragment, allLiveNodes, allFields);
      exclusivityMap.updateMethods(fragment, allButOne.getLiveFieldsAndMethods(),
          allMethods);
      exclusivityMap.updateStrings(fragment, allButOne.getLiveStrings(), everything
          .getLiveStrings());
      exclusivityMap.updateTypes(fragment,
          declaredTypesIn(allButOne.getInstantiatedTypes()),
          declaredTypesIn(everything.getInstantiatedTypes()));
    }
    return exclusivityMap;
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
        if (count > leftOverMergeLimit) {
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


}
