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

import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.dev.cfg.ConfigurationProperty;
import com.google.gwt.dev.cfg.Properties;
import com.google.gwt.dev.cfg.Property;
import com.google.gwt.dev.jjs.InternalCompilerException;
import com.google.gwt.dev.jjs.ast.JArrayType;
import com.google.gwt.dev.jjs.ast.JExpression;
import com.google.gwt.dev.jjs.ast.JMethod;
import com.google.gwt.dev.jjs.ast.JMethodCall;
import com.google.gwt.dev.jjs.ast.JNewArray;
import com.google.gwt.dev.jjs.ast.JNode;
import com.google.gwt.dev.jjs.ast.JNumericEntry;
import com.google.gwt.dev.jjs.ast.JPrimitiveType;
import com.google.gwt.dev.jjs.ast.JProgram;
import com.google.gwt.dev.jjs.ast.JRunAsync;
import com.google.gwt.dev.jjs.impl.JsniRefLookup;
import com.google.gwt.dev.util.JsniRef;
import com.google.gwt.dev.util.collect.HashMap;
import com.google.gwt.dev.util.collect.Lists;
import com.google.gwt.dev.util.log.speedtracer.CompilerEventType;
import com.google.gwt.dev.util.log.speedtracer.SpeedTracerLogger;


import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

/**
 * Utility function related to code splitting.
 */
public class Util {
  /**
   * Choose an initial load sequence of split points for the specified program.
   * Do so by identifying split points whose code always load first, before any
   * other split points. As a side effect, modifies
   * {@link com.google.gwt.core.client.impl.AsyncFragmentLoader#initialLoadSequence}
   * in the program being compiled.
   *
   * @throws com.google.gwt.core.ext.UnableToCompleteException If the module specifies a bad load order
   */
  public static LinkedHashSet<JRunAsync> pickInitialLoadSequence(TreeLogger logger,
      JProgram program, Properties properties) throws UnableToCompleteException {
    SpeedTracerLogger.Event codeSplitterEvent =
        SpeedTracerLogger
            .start(CompilerEventType.CODE_SPLITTER, "phase", "pickInitialLoadSequence");
    TreeLogger branch =
        logger.branch(TreeLogger.TRACE, "Looking up initial load sequence for split points");
    LinkedHashSet<JRunAsync> asyncsInInitialLoadSequence = new LinkedHashSet<JRunAsync>();

    ConfigurationProperty prop;
    {
      Property p = properties.find(PROP_INITIAL_SEQUENCE);
      if (p == null) {
        throw new InternalCompilerException("Could not find configuration property "
            + PROP_INITIAL_SEQUENCE);
      }
      if (!(p instanceof ConfigurationProperty)) {
        throw new InternalCompilerException(PROP_INITIAL_SEQUENCE
            + " is not a configuration property");
      }
      prop = (ConfigurationProperty) p;
    }

    for (String refString : prop.getValues()) {
      JRunAsync runAsync = findSplitPoint(refString, program, branch);
      if (asyncsInInitialLoadSequence.contains(runAsync)) {
        branch.log(TreeLogger.ERROR, "Split point specified more than once: " + refString);
      }
      asyncsInInitialLoadSequence.add(runAsync);
    }

    logInitialLoadSequence(logger, asyncsInInitialLoadSequence);
    installInitialLoadSequenceField(program, asyncsInInitialLoadSequence);
    codeSplitterEvent.end();
    return asyncsInInitialLoadSequence;
  }

  /**
   * Find a split point as designated in the {@link #PROP_INITIAL_SEQUENCE}
   * configuration property.
   */
  public static JRunAsync findSplitPoint(String refString, JProgram program, TreeLogger branch)
      throws UnableToCompleteException {
    SpeedTracerLogger.Event codeSplitterEvent =
        SpeedTracerLogger.start(CompilerEventType.CODE_SPLITTER, "phase", "findSplitPoint");
    Map<String, List<JRunAsync>> splitPointsByRunAsyncName =
        splitPointsByRunAsyncName(program.getRunAsyncs());

    if (refString.startsWith("@")) {
      JsniRef jsniRef = JsniRef.parse(refString);
      if (jsniRef == null) {
        branch.log(TreeLogger.ERROR, "Badly formatted JSNI reference in " + PROP_INITIAL_SEQUENCE
            + ": " + refString);
        throw new UnableToCompleteException();
      }
      final String lookupErrorHolder[] = new String[1];
      JNode referent =
          JsniRefLookup.findJsniRefTarget(jsniRef, program, new JsniRefLookup.ErrorReporter() {
            public void reportError(String error) {
              lookupErrorHolder[0] = error;
            }
          });
      if (referent == null) {
        TreeLogger resolveLogger =
            branch.branch(TreeLogger.ERROR, "Could not resolve JSNI reference: " + jsniRef);
        resolveLogger.log(TreeLogger.ERROR, lookupErrorHolder[0]);
        throw new UnableToCompleteException();
      }

      if (!(referent instanceof JMethod)) {
        branch.log(TreeLogger.ERROR, "Not a method: " + referent);
        throw new UnableToCompleteException();
      }

      JMethod method = (JMethod) referent;
      String canonicalName = ReplaceRunAsyncs.getImplicitName(method);
      List<JRunAsync> splitPoints = splitPointsByRunAsyncName.get(canonicalName);
      if (splitPoints == null) {
        branch.log(TreeLogger.ERROR, "Method does not enclose a runAsync call: " + jsniRef);
        throw new UnableToCompleteException();
      }
      if (splitPoints.size() > 1) {
        branch.log(TreeLogger.ERROR, "Method includes multiple runAsync calls, "
            + "so it's ambiguous which one is meant: " + jsniRef);
        throw new UnableToCompleteException();
      }

      return splitPoints.get(0);
    }

    // Assume it's a raw class name
    List<JRunAsync> splitPoints = splitPointsByRunAsyncName.get(refString);
    if (splitPoints == null || splitPoints.size() == 0) {
      branch.log(TreeLogger.ERROR, "No runAsync call is labelled with class " + refString);
      throw new UnableToCompleteException();
    }
    if (splitPoints.size() > 1) {
      branch.log(TreeLogger.ERROR, "More than one runAsync call is labelled with class "
          + refString);
      throw new UnableToCompleteException();
    }
    JRunAsync result = splitPoints.get(0);
    codeSplitterEvent.end();
    return result;
  }

  private static final String PROP_INITIAL_SEQUENCE = "compiler.splitpoint.initial.sequence";
  static final String LEFTOVERMERGE_SIZE =
      "compiler.splitpoint.leftovermerge.size";




  private static void logInitialLoadSequence(TreeLogger logger,
       LinkedHashSet<JRunAsync> initialLoadSequence) {
    if (!logger.isLoggable(TreeLogger.TRACE)) {
      return;
    }

    StringBuffer message = new StringBuffer();
    message.append("Initial load sequence of split points: ");
    if (initialLoadSequence.isEmpty()) {
      message.append("(none)");
    } else {
      boolean first = true;
      for (JRunAsync sp : initialLoadSequence) {
        if (first) {
          first = false;
        } else {
          message.append(", ");
        }
        // TODO(rluble): splitpoint ids have to go.
        message.append(sp.getSplitPoint());
      }
    }

    logger.log(TreeLogger.TRACE, message.toString());
  }

  /**
   * Installs the initial load sequence into AsyncFragmentLoader.BROWSER_LOADER.
   * The initializer looks like this:
   *
   * <pre>
   * AsyncFragmentLoader BROWSER_LOADER = makeBrowserLoader(1, new int[]{});
   * </pre>
   *
   * The second argument (<code>new int[]</code>) gets replaced by an array
   * corresponding to <code>initialLoadSequence</code>.
   */
  private static void installInitialLoadSequenceField(JProgram program,
      LinkedHashSet<JRunAsync> initialLoadSequence) {
    // Arg 1 is initialized in the source as "new int[]{}".
    JMethodCall call = ReplaceRunAsyncs.getBrowserLoaderConstructor(program);
    JExpression arg1 = call.getArgs().get(1);
    assert arg1 instanceof JNewArray;
    JArrayType arrayType = program.getTypeArray(JPrimitiveType.INT);
    assert ((JNewArray) arg1).getArrayType() == arrayType;
    List<JExpression> initializers = new ArrayList<JExpression>(initialLoadSequence.size());
    // TODO(rluble): this should be done after codesplitting but for now it relies on the
    // fact that runAsync.splitPoint() == fragmentNumber for the initial sequence.
    for (JRunAsync runAsync : initialLoadSequence) {
      initializers.add(new JNumericEntry(call.getSourceInfo(), "RunAsyncFragmentIndex",
          runAsync.getSplitPoint()));
    }
    JNewArray newArray =
        JNewArray.createInitializers(arg1.getSourceInfo(), arrayType, Lists
            .normalizeUnmodifiable(initializers));
    call.setArg(1, newArray);
  }

  private static Map<String, List<JRunAsync>> splitPointsByRunAsyncName(List<JRunAsync> runAsyncs) {
    Map<String, List<JRunAsync>> splitPointsByRunAsyncName = new HashMap<String, List<JRunAsync>>();
    for (JRunAsync runAsync : runAsyncs) {
      String name = runAsync.getName();
      if (name != null) {
        List<JRunAsync> list = splitPointsByRunAsyncName.get(name);
        if (list == null) {
          list = new ArrayList<JRunAsync>();
          splitPointsByRunAsyncName.put(name, list);
        }
        list.add(runAsync);
      }
    }
    return splitPointsByRunAsyncName;
  }
}
