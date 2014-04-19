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
package com.google.gwt.core.ext.soyc;

import com.google.gwt.core.ext.linker.SyntheticArtifact;
import com.google.gwt.core.linker.SymbolMapsLinker;
import com.google.gwt.dev.jjs.InternalCompilerException;
import com.google.gwt.dev.jjs.SourceInfo;
import com.google.gwt.dev.jjs.SourceOrigin;
import com.google.gwt.thirdparty.debugging.sourcemap.FilePosition;
import com.google.gwt.thirdparty.debugging.sourcemap.SourceMapGeneratorV3;
import com.google.gwt.thirdparty.debugging.sourcemap.SourceMapParseException;
import com.google.gwt.thirdparty.guava.common.collect.Lists;

import org.json.JSONException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Creates Closure Compatible SourceMaps.
 */
public class SourceMapRecorder {

  public static List<SyntheticArtifact> makeSourceMapArtifacts(int permutationId,
      List<Map<Range, SourceInfo>> sourceInfoMaps) {
    try {
      return (new SourceMapRecorder(permutationId)).recordSourceMap(sourceInfoMaps);
    } catch (Exception e) {
      throw new InternalCompilerException(e.toString(), e);
    }
  }

  protected final int permutationId;

  protected SourceMapRecorder(int permutationId) {
    this.permutationId = permutationId;
  }

  protected List<SyntheticArtifact> recordSourceMap(List<Map<Range, SourceInfo>> sourceInfoMaps)
      throws IOException, JSONException, SourceMapParseException {
    List<SyntheticArtifact> toReturn = Lists.newArrayList();
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    SourceMapGeneratorV3 generator = new SourceMapGeneratorV3();
    int fragment = 0;
    if (!sourceInfoMaps.isEmpty()) {
      for (Map<Range, SourceInfo> sourceMap : sourceInfoMaps) {
        generator.reset();
        addMappings(generator, sourceMap);
        updateSourceMap(generator, fragment);

        baos.reset();
        OutputStreamWriter out = new OutputStreamWriter(baos);
        generator.appendTo(out, "sourceMap" + fragment);
        out.flush();
        toReturn.add(new SymbolMapsLinker.SourceMapArtifact(permutationId, fragment,
            baos.toByteArray()));
        fragment++;
      }
    }
    return toReturn;
  }

  /**
   * A hook allowing a subclass to add more info to the sourcemap for a given fragment.
   */
  protected void updateSourceMap(SourceMapGeneratorV3 generator, int fragment)
      throws SourceMapParseException { }

  /**
   * Returns the string to put into the "names" entry in the sourcemap, or null for none.
   * This field isn't used by debuggers and isn't well-defined by the spec, but in theory
   * it should hold the Java expression that corresponds to an obfuscated JavaScript identifier.
   * (That is, it should be set if the range covers one JavaScript identifier.)
   */
  protected String getJavaExpression(SourceInfo sourceInfo) {
    return null;
  }

  /**
   * Adds the source mappings for one JavaScript file to its sourcemap.
   * Consolidates adjacent ranges to reduce the amount of data that the JavaScript
   * debugger has to load.
   */
  private void addMappings(SourceMapGeneratorV3 out, Map<Range, SourceInfo> mappings) {
    Set<Range> rangeSet = mappings.keySet();

    Range[] ranges = rangeSet.toArray(new Range[rangeSet.size()]);
    Arrays.sort(ranges, Range.DEPENDENCY_ORDER_COMPARATOR);

    UnionBuffer buf = new UnionBuffer();

    for (Range r : ranges) {
      SourceInfo info = mappings.get(r);

      if (info == SourceOrigin.UNKNOWN || info.getFileName() == null || info.getStartLine() < 0) {
        // skip a synthetic with no Java source
        continue;
      }
      if (r.getStartLine() == 0 || r.getEndLine() == 0) {
        // skip a bogus entry
        // JavaClassHierarchySetupUtil:prototypesByTypeId is pruned here. Maybe others too?
        continue;
      }

      String expression = getJavaExpression(info);
      if (expression != null) {
        // Mappings that have Java expressions can't be consolidated.
        buf.flush(out, null);
        buf.append(r, info);
        buf.flush(out, expression);
        continue;
      }

      if (buf.append(r, info)) {
        continue;
      }

      // Cannot merge ranges, so start a new range.
      buf.flush(out, null);
      buf.append(r, info);
    }
    buf.flush(out, null);
  }

  /**
   * A buffer containing the union of source mappings that are all on the same Java line
   * and overlap in JavaScript.
   * (This is used in an inner loop, so avoid memory allocation.)
   */
  private static class UnionBuffer {
    private boolean empty = true;

    private String javaFile;
    private int javaLine; // one-based

    // the JavaScript range so far (zero-based)
    private int startLine;
    private int startColumn;
    private int endLine;
    private int endColumn;

    /**
     * Attempts to append another mapping to the buffer.
     * The mappings must be adjacent or overlapping in JavaScript and map to the same line in Java,
     * Returns true if successful; otherwise the buffer is unchanged.
     */
    boolean append(Range nextRange, SourceInfo nextInfo) {
      if (empty) {
        // Start a new range.
        javaFile = nextInfo.getFileName();
        javaLine = nextInfo.getStartLine();
        startLine = nextRange.getStartLine();
        startColumn = nextRange.getStartColumn();
        endLine = nextRange.getEndLine();
        endColumn = nextRange.getEndColumn();
        empty = false;
        return true;
      }

      // The ranges were sorted by starting position. Therefore we only need to to check
      // that our ending position >= their starting position.
      boolean overlapsJavascriptRange = endLine > nextRange.getStartLine() ||
          (endLine == nextRange.getStartLine() && endColumn >= nextRange.getStartColumn());

      if (!overlapsJavascriptRange ||
          javaLine != nextInfo.getStartLine() || !javaFile.equals(nextInfo.getFileName())) {
        // They cannot be merged.
        return false;
      }

      // Merge them by adjusting the range end if needed.
      // (This code isn't normally used because appended range is usually a subset.)
      if (nextRange.getEndLine() > endLine) {
        endLine = nextRange.getEndLine();
        endColumn = nextRange.getEndColumn();
      } else if (nextRange.getEndLine() == endLine && nextRange.getEndColumn() > endColumn) {
        endColumn = nextRange.getEndColumn();
      }

      return true;
    }

    /**
     * Writes the buffer to the SourceMap (if necessary) and clears the buffer.
     */
    void flush(SourceMapGeneratorV3 out, String javaName) {
      if (empty) {
        return;
      }
      // Starting with V3, SourceMap line numbers are zero-based.
      // GWT's line numbers for Java files originally came from the JDT, which is 1-based,
      // so adjust them here to avoid an off-by-one error in debuggers.
      out.addMapping(javaFile, javaName,
          new FilePosition(javaLine - 1, 0),
          new FilePosition(startLine, startColumn),
          new FilePosition(endLine, endColumn));
      empty = true; // to make sure we don't write it twice.
    }
  }
}
