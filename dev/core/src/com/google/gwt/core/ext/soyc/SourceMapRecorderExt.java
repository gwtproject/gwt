package com.google.gwt.core.ext.soyc;

import com.google.gwt.core.ext.linker.SyntheticArtifact;
import com.google.gwt.dev.jjs.Correlation;
import com.google.gwt.dev.jjs.InternalCompilerException;
import com.google.gwt.dev.jjs.SourceInfo;
import com.google.gwt.dev.jjs.SourceInfoCorrelation;
import com.google.gwt.thirdparty.debugging.sourcemap.SourceMapGeneratorV3;
import com.google.gwt.thirdparty.debugging.sourcemap.SourceMapParseException;

import org.json.JSONObject;

import java.util.List;
import java.util.Map;

/**
 * Creates Closure Compatible SourceMaps with named ranges.
 */
public class SourceMapRecorderExt extends SourceMapRecorder {

  public static final String PERMUTATION_EXT = "x_gwt_permutation";

  public static List<SyntheticArtifact> makeSourceMapArtifacts(int permutationId,
      List<Map<Range, SourceInfo>> sourceInfoMaps, JSONObject[] metrics) {
    try {
      return (new SourceMapRecorderExt(permutationId, metrics)).recordSourceMap(sourceInfoMaps);
    } catch (Exception e) {
      throw new InternalCompilerException(e.toString(), e);
    }
  }

  private JSONObject[] sizeMetrics;

  protected SourceMapRecorderExt(int permutationId, JSONObject[] metrics) {
    super(permutationId);
    sizeMetrics = metrics;
  }

  protected void updateSourceMap(SourceMapGeneratorV3 generator, int fragment)
      throws SourceMapParseException {
    generator.addExtension(PERMUTATION_EXT, new Integer(permutationId));
    generator.addExtension("x_gwt_fragment", new Integer(fragment));
    generator.addExtension("x_gwt_fragmentMetrics", sizeMetrics[fragment]);
  }

  protected String getName(SourceInfo sourceInfo) {
    // We can discarding Unknown or not-so-valid (eg. com.google.gwt.dev.js.ast.JsProgram)
    // sourceInfo
    String rangeName = null;
    if (sourceInfo instanceof SourceInfoCorrelation) {
      Correlation correlation = ((SourceInfoCorrelation) sourceInfo).getPrimaryCorrelation();
      if (correlation != null) {
        // We can reduce name sizes by removing the left part corresponding to the
        // package name, eg. com.google.gwt.client. Because this is already in the file name.
        // This name includes static/synth method names
        rangeName = correlation.getIdent();
      }
    }
    return rangeName;
  }
}
