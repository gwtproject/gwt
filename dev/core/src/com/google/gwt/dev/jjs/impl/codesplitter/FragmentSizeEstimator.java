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
import com.google.gwt.dev.js.JsToStringGenerationVisitor;
import com.google.gwt.dev.util.TextOutput;

import java.util.BitSet;

/**
 * Estimate the size of the fragment based on the atoms that are exclusively live in the fragment.
 */
public class FragmentSizeEstimator {
  static boolean fragmentSizeBelowMergeLimit(BitSet runAsyncSet, LiveSplitPointMap map,
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
      // for (JsStatement stat : stats) {
      //  v.accept(stat);
      // }
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