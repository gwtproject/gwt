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
/**
 *
 */
package com.google.gwt.core.ext.soyc;

import com.google.gwt.dev.jjs.SourceInfo;
import com.google.gwt.dev.jjs.SourceOrigin;

import java.util.Comparator;

/**
 * Represents a contiguous region of characters in the compiler output.
 */
public final class Range {

  /**
   * Sorts Ranges so that a Range will be preceded by any Ranges that enclose
   * it.
   */
  public static final Comparator<Range> DEPENDENCY_ORDER_COMPARATOR = new Comparator<Range>() {
    @Override
    public int compare(Range o1, Range o2) {
      int a = o1.start - o2.start;
      if (a != 0) {
        return a;
      }

      return o2.end - o1.end;
    }
  };

  /**
   * Sorts Ranges into the order in which they would appear in the source code
   * based on start position and end position.
   */
  public static final Comparator<Range> SOURCE_ORDER_COMPARATOR = new Comparator<Range>() {
    @Override
    public int compare(Range o1, Range o2) {
      int a = o1.start - o2.start;
      if (a != 0) {
        return a;
      }

      return o1.end - o2.end;
    }
  };

  final int end;
  final int endColumn;
  final int endLine;
  final int start;
  final int startColumn;
  final int startLine;
  final SourceInfo sourceInfo;

  /**
   * Constructor.
   *
   * @param start must be non-negative
   * @param end must be greater than or equal to <code>start</code>
   */
  public Range(int start, int end) {
    this(start, end, -1, -1, -1, -1, SourceOrigin.UNKNOWN);
  }

  /**
   * Constructor.
   *
   * @param start must be non-negative
   * @param end must be greater than or equal to <code>start</code>
   */
  public Range(int start, int end, SourceInfo sourceInfo) {
    this(start, end, -1, -1, -1, -1, sourceInfo);
  }

  /**
   * A range whose start and end are specified both as character positions and as
   * line numbers and columns. Everything is zero-based (similar to Java arrays).
   * The ending position must be greater or equal to the starting position.
   *
   * @param start must be non-negative
   * @param end must be greater than or equal to <code>start</code>
   */
  public Range(int start, int end, int startLine, int startColumn, int endLine, int endColumn,
      SourceInfo sourceInfo) {
    assert start >= 0;
    assert start <= end;

    this.start = start;
    this.end = end;
    this.startLine = startLine;
    this.startColumn = startColumn;
    this.endLine = endLine;
    this.endColumn = endColumn;
    this.sourceInfo = sourceInfo;
  }

  /**
   * Return <code>true</code> if the given Range lies wholly within the Range.
   */
  public boolean contains(Range o) {
    return start <= o.start && o.end <= end;
  }

  /**
   * Creates a Range copy whose start position and line number have been rebased relative to some
   * base position.
   * <p>
   * For example a range that starts at byte 5342 when normalized against a base start of 5000 will
   * now start at byte 342.
   */
  public Range createNormalizedCopy(int baseStart, int baseStartLine) {
    if (start < 0) {
      return new Range(baseStartLine, baseStartLine, baseStartLine, baseStartLine, baseStart,
          baseStartLine, sourceInfo);
    }

    return createOffsetCopy(-baseStart, -baseStartLine);
  }

  /**
   * Creates a Range copy whose start position and line number have been moved by some known offset
   * size.
   * <p>
   * For example a range that starts at byte 342 when moved by an offset of 5000 will now start at
   * byte 5342.
   */
  public Range createOffsetCopy(int positionOffset, int lineOffset) {
    return new Range(start + positionOffset, end + positionOffset, startLine + lineOffset,
        startColumn, endLine + lineOffset, endColumn, sourceInfo);
  }

  @Override
  public boolean equals(Object obj) {
    if (!(obj instanceof Range)) {
      return false;
    }
    Range o = (Range) obj;
    return start == o.start && end == o.end;
  }

  public int getEnd() {
    return end;
  }

  public int getEndColumn() {
    return endColumn;
  }

  public int getEndLine() {
    return endLine;
  }

  public SourceInfo getSourceInfo() {
    return sourceInfo;
  }

  public int getStart() {
    return start;
  }

  public int getStartColumn() {
    return startColumn;
  }

  public int getStartLine() {
    return startLine;
  }

  @Override
  public int hashCode() {
    return 37 * start + end;
  }

  public int length() {
    return end - start;
  }

  /**
   * For debugging use only.
   */
  @Override
  public String toString() {
    return "[" + start + " - " + end + ")";
  }

  /**
   * Returns a copy with the end moved.
   */
  public Range withNewEnd(int newEnd, int newEndLine, int newEndColumn) {
    return new Range(start, newEnd, startLine, startColumn, newEndLine, newEndColumn, sourceInfo);
  }
}
