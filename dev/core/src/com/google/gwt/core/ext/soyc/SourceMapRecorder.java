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

import com.google.gwt.core.ext.linker.EmittedArtifact.Visibility;
import com.google.gwt.core.ext.linker.SyntheticArtifact;
import com.google.gwt.core.ext.soyc.coderef.ClassDescriptor;
import com.google.gwt.core.ext.soyc.coderef.DependencyGraphRecorder;
import com.google.gwt.core.ext.soyc.coderef.EntityDescriptor;
import com.google.gwt.core.ext.soyc.coderef.EntityDescriptorJsonTranslator;
import com.google.gwt.core.ext.soyc.coderef.MethodDescriptor;
import com.google.gwt.core.ext.soyc.coderef.PackageDescriptor;
import com.google.gwt.core.linker.SoycReportLinker;
import com.google.gwt.core.linker.SymbolMapsLinker;
import com.google.gwt.dev.jjs.Correlation;
import com.google.gwt.dev.jjs.Correlation.Axis;
import com.google.gwt.dev.jjs.CorrelationFactory.RealCorrelationFactory;
import com.google.gwt.dev.jjs.InternalCompilerException;
import com.google.gwt.dev.jjs.SourceInfo;
import com.google.gwt.dev.jjs.SourceInfoCorrelation;
import com.google.gwt.dev.jjs.ast.JClassType;
import com.google.gwt.dev.jjs.ast.JDeclaredType;
import com.google.gwt.dev.jjs.ast.JField;
import com.google.gwt.dev.jjs.ast.JMethod;
import com.google.gwt.dev.jjs.ast.JProgram;
import com.google.gwt.dev.jjs.ast.JRunAsync;
import com.google.gwt.dev.jjs.impl.JavaToJavaScriptMap;
import com.google.gwt.dev.jjs.impl.codesplitter.FragmentPartitioningResult;
import com.google.gwt.dev.js.SizeBreakdown;
import com.google.gwt.dev.js.ast.JsName;
import com.google.gwt.thirdparty.debugging.sourcemap.FilePosition;
import com.google.gwt.thirdparty.debugging.sourcemap.SourceMapGeneratorV3;
import com.google.gwt.thirdparty.debugging.sourcemap.SourceMapParseException;
import com.google.gwt.util.tools.Utility;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * Creates Closure Compatible SourceMaps.
 */
public class SourceMapRecorder {

  public final static String SPLIT_POINTS_NAME = "splitPoints";
  public final static String PERMUTATION_EXT = "x_gwt_permutation";

  public static List<SyntheticArtifact> makeSourceMapArtifacts(int permutationId,
      List<Map<Range, SourceInfo>> sourceInfoMaps, JavaToJavaScriptMap jjsmap,
      SizeBreakdown[] sizeBreakdowns, DependencyGraphRecorder codeGraph, JProgram jprogram) {

    SourceMapRecorder recorder = new SourceMapRecorder();
    recorder.toReturn = new ArrayList<SyntheticArtifact>();
    recorder.permutationId = permutationId;

    try {
      recorder.recordSplitPoints(jprogram);
      recorder.initFragSizes(sizeBreakdowns);
      recorder.recordCodeReference(codeGraph, sizeBreakdowns, jjsmap);
      recorder.recordSourceMap(sourceInfoMaps, jjsmap);
    } catch (Exception e) {
      throw new InternalCompilerException(e.toString(), e);
    }

    return recorder.toReturn;
  }

  private static String nameFromJsName(Correlation corr, JavaToJavaScriptMap jjsmap) {
    if (corr != null) {
      JsName jsName = corr.getJsName();
      if (jsName != null) {
        //type, method or field only
        if (jjsmap.nameToType(jsName) != null) { //move to a local var if slow
          return jjsmap.nameToType(jsName).getName();
        } else if (jjsmap.nameToMethod(jsName) != null) {
          return RealCorrelationFactory.getMethodIdent(jjsmap.nameToMethod(jsName));
        } else if (jjsmap.nameToField(jsName) != null) {
          JField field = jjsmap.nameToField(jsName);
          return field.getEnclosingType().getName() + "::" + field.getName();
        } //reduce name, call getShortName
      }
    }
    return null;
  }

  private final static int FRAG_SIZE_IDX = 0;
  private final static int OTHER_SIZE_IDX = 1;

  private List<SyntheticArtifact> toReturn;
  private int permutationId;
  private int[][] fragSizes;

  private SourceMapRecorder() {}

  private void recordSourceMap(List<Map<Range, SourceInfo>> sourceInfoMaps,
      JavaToJavaScriptMap jjsmap) throws IOException, JSONException, SourceMapParseException {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();

    SourceMapGeneratorV3 generator = new SourceMapGeneratorV3();
    OutputStreamWriter out = new OutputStreamWriter(baos);
    int fragment = 0;
    if (!sourceInfoMaps.isEmpty()) {
      for (Map<Range, SourceInfo> sourceMap : sourceInfoMaps) {
        generator.reset();
        Set<Range> rangeSet = sourceMap.keySet();
        Range[] ranges = rangeSet.toArray(new Range[rangeSet.size()]);
        Arrays.sort(ranges, Range.DEPENDENCY_ORDER_COMPARATOR);
        for (Range r : ranges) {
          SourceInfo si = sourceMap.get(r);
          if (si.getFileName() == null || si.getStartLine() < 0) {
            // skip over synthetics with no Java source
            continue;
          }
          if (r.getStartLine() == 0 || r.getEndLine() == 0) {
            // or other bogus entries that appear
            continue;
          }

          //discard Unknown or not-so-valid (eg. com.google.gwt.dev.js.ast.JsProgram) sourceInfo ?
          String rangeName = null;
          if (si instanceof SourceInfoCorrelation) {
            Correlation correlation = ((SourceInfoCorrelation) si).getPrimaryCorrelation();
            if (correlation != null && correlation.getAxis() != Axis.LITERAL
                                    && correlation.getAxis() != Axis.JSNAME) {
              // Literals only store "class" or "string" as ident
              // ocallau: We can reduce name sizes by removing the left part corresponding to the
              // package name, eg. com.google.gwt.client. Because this is already in the file name.
              // This name includes static/synth method names
              rangeName = correlation.getIdent();
            } else {
              rangeName = nameFromJsName(si.getCorrelation(Axis.JSNAME), jjsmap);
            }
          }
          // Starting with V3, SourceMap line numbers are zero-based.
          // GWT's line numbers for Java files originally came from the JDT, which is 1-based,
          // so adjust them here to avoid an off-by-one error in debuggers.
          generator.addMapping(si.getFileName(), rangeName,
              new FilePosition(si.getStartLine() - 1, 0),
              new FilePosition(r.getStartLine(), r.getStartColumn()),
              new FilePosition(r.getEndLine(), r.getEndColumn()));
        }

        generator.addExtension(PERMUTATION_EXT, new Integer(permutationId));
        generator.addExtension("x_gwt_fragment", new Integer(fragment));
        generator.addExtension("x_gwt_fragmentMetrics", sizeMetrics(fragment));

        baos.reset();
        generator.appendTo(out, "sourceMap" + fragment);
        out.flush();
        toReturn.add(new SymbolMapsLinker.SourceMapArtifact(permutationId, fragment,
            baos.toByteArray()));
        fragment++;
      }
    }
  }

  private JSONObject sizeMetrics(int fragment) throws JSONException {
    JSONObject obj = new JSONObject();
    obj.put("totalSize", this.fragSizes[fragment][FRAG_SIZE_IDX]);
    obj.put("strAndvarSize", this.fragSizes[fragment][OTHER_SIZE_IDX]);
    return obj;
  }

  private void initFragSizes(SizeBreakdown[] sizeBreakdowns){
    this.fragSizes = new int[sizeBreakdowns.length][2];
    for (int i = 0; i < fragSizes.length ; i++) {
      this.fragSizes[i][FRAG_SIZE_IDX] = sizeBreakdowns[i].getSize();
      this.fragSizes[i][OTHER_SIZE_IDX] = 0;
    }
  }

  private void updateEntity(EntityDescriptor entity, int fragment, int size, String shortIdent) {
    EntityDescriptor.Fragment frg = entity.new Fragment();
    frg.id = fragment;
    frg.size = size;
    entity.addFragment(frg);
    //TODO (ocallau) could be avoided
    entity.setObfuscatedName(shortIdent);
  }

  private void recordCodeReference(DependencyGraphRecorder codeGraph,
      SizeBreakdown[] sizeBreakdowns, JavaToJavaScriptMap jjsmap)
      throws IOException, JSONException {
    //add sizes and other info
    for (int i = 0; i < sizeBreakdowns.length; i++) {
      for (Entry<JsName, Integer> kv : sizeBreakdowns[i].getSizeMap().entrySet()) {
        JsName name = kv.getKey();
        int size = kv.getValue();
        //find method
        JMethod method = jjsmap.nameToMethod(name);
        if (method != null) {
          updateEntity(codeGraph.methodFrom(method), i, size, name.getShortIdent());
          continue;
        }
        //find field
        JField field = jjsmap.nameToField(name);
        if ((field != null) && (field.getEnclosingType() != null)) {
          updateEntity(codeGraph.classFrom(field.getEnclosingType()).fieldFrom(field),
              i, size, name.getShortIdent());
          continue;
        }
        //find class, which means constructor
        JClassType type = jjsmap.nameToType(name);
        if (type != null) {
          updateEntity(codeGraph.classFrom(type), i, size, name.getShortIdent());
          continue;
        }
        //otherwise is a string or variable
        this.fragSizes[i][OTHER_SIZE_IDX] += size;
      }
    }
    //adding symbol names
    for (ClassDescriptor cls : codeGraph.getCodeGraph().values()) {
      JDeclaredType type = cls.getReference();
      if (type instanceof JClassType) {
        JsName jsName = jjsmap.nameForType((JClassType) type);
        if (jsName != null){
          cls.setObfuscatedName(jsName.getShortIdent());
        }
      }
      for (MethodDescriptor mth : cls.getMethods()) {
        JsName jsName = jjsmap.nameForMethod(mth.getReference());
        if (jsName != null){
          mth.setObfuscatedName(jsName.getShortIdent());
        }
      }
      for (JField field : type.getFields()) {
        JsName jsName = jjsmap.nameForField(field);
        if (jsName != null){
          // create field, if it doesn't exist
          cls.fieldFrom(field).setObfuscatedName(jsName.getShortIdent());
        }
      }
    }
    //build json
    addArtifactFromJson(
        EntityDescriptorJsonTranslator.writeJson(
            PackageDescriptor.newFrom(codeGraph.getCodeGraph())),
        codeRefArtifactName());
  }

  private String addArtifactFromJson(Object value, String named) throws IOException {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    PrintWriter writer = new PrintWriter(baos);
    writer.write(value.toString());
    Utility.close(writer);

    //TODO(ocallau) Must be updated with the correct/final linker
    SyntheticArtifact artifact = new SyntheticArtifact(
        SoycReportLinker.class, named, baos.toByteArray());
    artifact.setVisibility(Visibility.LegacyDeploy);

    toReturn.add(artifact);
    return named;
  }

  private void recordSplitPoints(JProgram jprogram) throws IOException, JSONException {
    JSONObject jsonPoints = new JSONObject();
    //split points
    FragmentPartitioningResult partitionResult = jprogram.getFragmentPartitioningResult();
    JSONArray splitPoints = new JSONArray();
    for (JRunAsync runAsync : jprogram.getRunAsyncs()) {
      JSONObject splitPoint = new JSONObject();
      int fragId = runAsync.getRunAsyncId();
      if (partitionResult != null) {
        fragId = partitionResult.getFragmentForRunAsync(fragId);
      }
      splitPoint.put("id", new Integer(fragId));
      splitPoint.put("location", runAsync.getName());

      splitPoints.put(splitPoint);
    }
    jsonPoints.put("splitPoints", splitPoints);
    //initial sequence points
    JSONArray initialSequence = new JSONArray();
    for (int fragId : jprogram.getInitialFragmentIdSequence()){
      initialSequence.put(partitionResult.getFragmentForRunAsync(fragId));
    }
    jsonPoints.put("initialSequence", initialSequence);

    addArtifactFromJson(jsonPoints, splitPointArtifactName());
  }

  private String splitPointArtifactName(){
    return SPLIT_POINTS_NAME + permutationId + ".json";
  }

  private String codeRefArtifactName(){
    return EntityDescriptorJsonTranslator.ARTIFACT_NAME + permutationId + ".json";
  }
}
