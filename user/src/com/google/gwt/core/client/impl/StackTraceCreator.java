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
package com.google.gwt.core.client.impl;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.client.JsArrayString;

/**
 * Encapsulates logic to create a stack trace. This class should only be used in
 * Production Mode.
 */
public class StackTraceCreator {

  /**
   * Maximum # of frames to look for {@link Throwable#fillInStackTrace()} in the generated stack
   * trace. This is just a safe guard just in case if {@code fillInStackTrace} doesn't show up in
   * the stack trace for some reason.
   */
  private static final int DROP_FRAME_LIMIT = 5;

  /**
   * Line number used in a stack trace when it is unknown.
   */
  private static final int LINE_NUMBER_UNKNOWN = -1;

  /**
   * Replacement for function names that cannot be extracted from a stack.
   */
  private static final String ANONYMOUS = "anonymous";

  /**
   * Replacement for class or file names that cannot be extracted from a stack.
   */
  private static final String UNKNOWN = "Unknown";

  /**
   * This class acts as a deferred-binding hook point to allow more optimal versions to be
   * substituted.
   */
  abstract static class Collector {

    public abstract void collect(Object t, Object jsThrown);

    public abstract StackTraceElement[] getStackTrace(Object t);
  }

  /**
   * This legacy {@link Collector} simply crawls <code>arguments.callee.caller</code> for browsers
   * that doesn't support {@code Error.stack} property.
   */
  static class CollectorLegacy extends Collector {

    @Override
    public native void collect(Object t, Object thrownIgnored) /*-{
      var seen = {};
      t.fnStack = [];

      // Ignore the collect() call
      var callee = arguments.callee.caller;
      while (callee) {
        var name = @StackTraceCreator::getFunctionName(*)(callee);
        t.fnStack.push(name);

        // Avoid infinite loop by associating names to function objects.  We
        // record each caller in the withThisName variable to handle functions
        // with identical names but separate identity (such as 'anonymous')
        var keyName = ':' + name;
        var withThisName = seen[keyName];
        if (withThisName) {
          var i, j;
          for (i = 0, j = withThisName.length; i < j; i++) {
            if (withThisName[i] === callee) {
              return;
            }
          }
        }

        (withThisName || (seen[keyName] = [])).push(callee);
        callee = callee.caller;
      }
    }-*/;

    @Override
    public StackTraceElement[] getStackTrace(Object t) {
      JsArrayString stack = getFnStack(t);

      int length = stack.length();
      StackTraceElement[] stackTrace = new StackTraceElement[length];
      for (int i = 0; i < length; i++) {
        stackTrace[i] = new StackTraceElement(UNKNOWN, stack.get(i), null, LINE_NUMBER_UNKNOWN);
      }
      return stackTrace;
    }
  }

  /**
   * Collaborates with JsStackEmulator.
   */
  static final class CollectorEmulated extends Collector {

    @Override
    public native void collect(Object t, Object jsThrownIgnored) /*-{
      t.fnStack = [];
      for (var i = 0; i < $stackDepth; i++) {
        var location = $location[i];
        var fn = $stack[i];
        var name = fn ? @StackTraceCreator::getFunctionName(*)(fn) : @StackTraceCreator::ANONYMOUS;

        // Reverse the order
        t.fnStack[$stackDepth - i - 1] = [name, location];
      }
    }-*/;

    @Override
    public StackTraceElement[] getStackTrace(Object t) {
      JsArray<JsArrayString> stack = getFnStack(t).cast();

      StackTraceElement[] stackTrace = new StackTraceElement[stack.length()];
      for (int i = 0; i < stackTrace.length; i++) {
        JsArrayString frame = stack.get(i);
        String name = frame.get(0);
        String location = frame.get(1);

        String fileName = null;
        int lineNumber = LINE_NUMBER_UNKNOWN;
        if (location != null) {
          int idx = location.indexOf(':');
          if (idx != -1) {
            fileName = location.substring(0, idx);
            lineNumber = parseInt(location.substring(idx + 1));
          } else {
            lineNumber = parseInt(location);
          }
        }
        stackTrace[i] = new StackTraceElement(UNKNOWN, name, fileName, lineNumber);
      }
      return stackTrace;
    }
  }

  /**
   * Modern browsers provide a <code>stack</code> property in thrown objects.
   */
  static class CollectorModern extends Collector {

    static {
      increaseStackTraceLimit();
    }

    // As of today, only available in IE10+ and Chrome.
    private static native void increaseStackTraceLimit() /*-{
      // TODO(cromwellian) make this a configurable?
      Error.stackTraceLimit = 64;
    }-*/;

    @Override
    public native void collect(Object t, Object jsThrown) /*-{
      // Carefully crafted to delay the 'stack' property until stack trace construction as it is
      // costly in some browsers (e.g. Chrome).

     function fixIE(e) {
        // In IE -unlike every other browser-, the stack property is not defined until you throw
        // the Error object. Sometimes I hope they would just stop developing browsers...
        if (!("stack" in e)) {
          try { throw e; } catch(ignored) {}
        }
        return e;
      }

      var backingJsError;
      if (typeof jsThrown == 'string') {
        backingJsError = fixIE(new Error(jsThrown));
      } else if (jsThrown instanceof Object && "stack" in jsThrown){
        backingJsError = jsThrown;
      } else {
        backingJsError = fixIE(new Error());
      }

      t.__gwt$backingJsError = backingJsError;
    }-*/;

    @Override
    public StackTraceElement[] getStackTrace(Object t) {
      JsArrayString stack = split(t);

      // We are in script-mode - let the array auto grow.
      StackTraceElement[] stackTrace = new StackTraceElement[0];
      int addIndex = 0, length = stack.length();

      if (length == 0) {
        // Nothing to parse...
        return stackTrace;
      }

      // Chrome & IE10+ contains the error msg as the first line of stack (iOS, Firefox doesn't).
      StackTraceElement ste = parse(stack.get(0));
      if (!ste.getMethodName().equals(ANONYMOUS)) {
        stackTrace[addIndex++] = ste;
      }

      // Parse and put the rest of the elements in to the stack trace.
      for (int i = 1; i < length; i++) {
        stackTrace[addIndex++] = parse(stack.get(i));
      }

      return stackTrace;
    }

    /**
     * Parses a stack trace line from the browser and returns a new {@link StackTraceElement}
     * constructed with the extracted data.
     */
    private StackTraceElement parse(String stString) {
      String location = "";

      if (stString.isEmpty()) {
        return createSte(UNKNOWN, ANONYMOUS, LINE_NUMBER_UNKNOWN, -1);
      }

      String toReturn = stString.trim();

      // Strip the "at " prefix:
      if (toReturn.startsWith("at ")) {
        toReturn = toReturn.substring(3);
      }

      toReturn = stripSquareBrackets(toReturn);

      int index = toReturn.indexOf("(");
      if (index == -1) {
        // No bracketed items found, try '@' (used by iOS & Firefox).
        index = toReturn.indexOf("@");
        if (index == -1) {
          // No bracketed items nor '@' found, hence no function name available
          location = toReturn;
          toReturn = "";
        } else {
          location = toReturn.substring(index + 1).trim();
          toReturn = toReturn.substring(0, index).trim();
        }
      } else {
        // Bracketed items found: strip them off, parse location info
        int closeParen = toReturn.indexOf(")", index);
        location = toReturn.substring(index + 1, closeParen);
        toReturn = toReturn.substring(0, index).trim();
      }

      // Strip the Type off t
      index = toReturn.indexOf('.');
      if (index != -1) {
        toReturn = toReturn.substring(index + 1);
      }

      final String ieAnonymousFunctionName = "Anonymous function";
      if (toReturn.isEmpty() || toReturn.equals(ieAnonymousFunctionName)) {
        toReturn = ANONYMOUS;
      }

      // colon between line and column
      int lastColonIndex = location.lastIndexOf(':');
      // colon between file url and line number
      int endFileUrlIndex = location.lastIndexOf(':', lastColonIndex - 1);

      int line = LINE_NUMBER_UNKNOWN;
      int col = -1;
      String fileName = UNKNOWN;

      if (lastColonIndex != -1 && endFileUrlIndex != -1) {
        fileName = location.substring(0, endFileUrlIndex);
        line = parseInt(location.substring(endFileUrlIndex + 1, lastColonIndex));
        col = parseInt(location.substring(lastColonIndex + 1));
      }

      return createSte(fileName, toReturn, line, col);
    }

    protected StackTraceElement createSte(String fileName, String method, int line, int col) {
      return new StackTraceElement(UNKNOWN, method, fileName + "@" + col,
          line < 0 ? LINE_NUMBER_UNKNOWN : line);
    }

    private native String stripSquareBrackets(String toReturn) /*-{
      return toReturn.replace(/\[.*?\]/g,"")
    }-*/;
  }

  /**
   * Subclass that forces reported line numbers to -1 (fetch from symbolMap) if source maps are
   * disabled.
   */
  static class CollectorModernNoSourceMap extends CollectorModern {
    @Override
    protected StackTraceElement createSte(String fileName, String method, int line, int col) {
      return new StackTraceElement(UNKNOWN, method, fileName, LINE_NUMBER_UNKNOWN);
    }
  }

  private static native int parseInt(String number) /*-{
    return parseInt(number) || @StackTraceCreator::LINE_NUMBER_UNKNOWN;
  }-*/;

  /**
   * When compiler.stackMode = strip, we stub out the collector.
   */
  static class CollectorNull extends Collector {
    @Override
    public void collect(Object ignored, Object jsThrownIgnored) {
      // Nothing to do
    }

    @Override
    public StackTraceElement[] getStackTrace(Object ignored) {
      return new StackTraceElement[0];
    }
  }

  /**
   * Collect necessary information to construct stack trace trace later in time.
   */
  public static void captureStackTrace(Throwable throwable, Object reference) {
    collector.collect(throwable, reference);
  }

  public static StackTraceElement[] constructJavaStackTrace(Throwable thrown) {
    StackTraceElement[] stackTrace = collector.getStackTrace(thrown);
    return dropInternalFrames(stackTrace);
  }

  private static StackTraceElement[] dropInternalFrames(StackTraceElement[] stackTrace) {
    final String dropFrameUntilFnName =
        Impl.getNameOf("@com.google.gwt.core.client.impl.StackTraceCreator::captureStackTrace(*)");

    int numberOfFrameToSearch = Math.min(stackTrace.length, DROP_FRAME_LIMIT);
    for (int i = 0; i < numberOfFrameToSearch; i++) {
      if (stackTrace[i].getMethodName().equals(dropFrameUntilFnName)) {
        return splice(stackTrace, i + 1);
      }
    }

    return stackTrace;
  }

  // Visible for testing
  static final Collector collector;

  static {
    // Ensure old Safari falls back to legacy Collector implementation.
    boolean enforceLegacy = !supportsErrorStack();
    Collector c = GWT.create(Collector.class);
    collector = (c instanceof CollectorModern && enforceLegacy) ? new CollectorLegacy() : c;
  }

  private static native boolean supportsErrorStack() /*-{
    // Error.stackTraceLimit is cheaper to check and available in both IE and Chrome
    return !!Error.stackTraceLimit || "stack" in new Error();
  }-*/;

  private static native JsArrayString getFnStack(Object e) /*-{
    return (e && e.fnStack && e.fnStack instanceof Array) ? e.fnStack : [];
  }-*/;

  private static native String getFunctionName(JavaScriptObject fn) /*-{
    return fn.name || (fn.name = @StackTraceCreator::extractFunctionName(*)(fn.toString()));
  }-*/;

  // Visible for testing
  static native String extractFunctionName(String fnName) /*-{
    var fnRE = /function(?:\s+([\w$]+))?\s*\(/;
    var match = fnRE.exec(fnName);
    return (match && match[1]) || @StackTraceCreator::ANONYMOUS;
  }-*/;

  private static native JsArrayString split(Object t) /*-{
    var e = t.__gwt$backingJsError;
    return (e && e.stack) ? e.stack.split('\n') : [];
  }-*/;

  private static native <T> T splice(T arr, int length) /*-{
    (arr.length >= length) && arr.splice(0, length);
    return arr;
  }-*/;
}
